package IC.SemanticAnalysis;


import java.util.List;

import IC.BinaryOps;
import IC.LiteralTypes;
import IC.AST.*;
import IC.Parser.SemanticError;

public class SemanticChecker implements Visitor {

	@Override
	public Object visit(Program program) {
		
		
		for (ICClass c : program.getClasses()) {
			if (c.accept(this) == null) {
				return null;
			}
		}

		return Boolean.TRUE;
	}

	private boolean isUniqueClassMember(ICClass c, String id) {
		SymbolTable t = c.getEnclosingScope();

		while (t != null) {
			if (t.symbolExists(id)) {
				return false;
			}
			t = t.getParent();
		}
		
		return true;
	}

	@Override
	public Object visit(ICClass icClass) {
		try {
			for (Field f : icClass.getFields()) {
				if (!isUniqueClassMember(icClass, f.getName()))
					throw new SemanticError(f.getLine(), "Class member " + f.getName() + " already exists.");
			}			
			for (Method m : icClass.getMethods()) {
				if (!isUniqueClassMember(icClass, m.getName()))
					throw new SemanticError(m.getLine(), "Class member " + m.getName() + " already exists.");
			}
			
			for (Field f : icClass.getFields()) {
				if (f.accept(this) == null)
					return null;
			}			
			for (Method m : icClass.getMethods()) {
				if (m.accept(this) == null)
					return null;
			}
		}
		catch (SemanticError e) {
			System.err.println(e.getMessage()+ " in line "+ e.getLineNumber());
			return null;
		}

		return Boolean.TRUE;
	}

	@Override
	public Object visit(Field field) {
		return Boolean.TRUE;
	}

	private Object methodVisitor(Method method) {
		for (Statement s : method.getStatements())
		{
			if (s.accept(this) == null)
				return null;
		}
		
		if (!checkReturn(method))
		{
			System.err.println("method " + method.getName() + " doesn't have a return statement in all code path");
			return null;
		}
		
		return Boolean.TRUE; 
	}
	
	private boolean checkReturn(Method method)
	{
		if (method.getType().getName().equals(VoidType.NAME))
			return true;
		if (method.getEnclosingScope().getId().equals("Library"))
			return true;
		return checkReturnStatements(method.getStatements());
	}
	
	private boolean checkReturnStatements(List<Statement> statements)
	{
		for (Statement s : statements) {
			if (checkReturnStatement(s))
				return true;
		}

		return false;
	}
	
	private boolean checkReturnStatement(Statement s)
	{
		if (s instanceof Return)
			return true;
		
		if (s instanceof If)
		{
			If real = (If)s;
			if (!real.hasElse())
				return false;
			if (checkReturnStatement(real.getOperation()) &&
				checkReturnStatement(real.getElseOperation()))
				return true;
			return false;
		}
		else if (s instanceof StatementsBlock)
		{
			StatementsBlock real = (StatementsBlock)s;
			return checkReturnStatements(real.getStatements());
		}
		
		return false;
	}
	
	
	
	@Override
	public Object visit(VirtualMethod method) {
		return methodVisitor(method);
	}

	@Override
	public Object visit(StaticMethod method) {
		return methodVisitor(method);
	}

	@Override
	public Object visit(LibraryMethod method) {
		return methodVisitor(method);
	}

	@Override
	public Object visit(Formal formal) {
		return Boolean.TRUE;
	}

	@Override
	public Object visit(PrimitiveType type)
	{
		switch (type.getType())
		{
			case BOOLEAN:	return new BoolType(type.getDimension());
			case INT:		return new IntType(type.getDimension()); 
			case STRING:	return new StringType(type.getDimension());
			case VOID:		return new StringType(type.getDimension());
		}
		
		return null;
	}

	@Override
	public Object visit(UserType type)
	{
		return ClassTable.fromUserType(type);
	}

	private boolean checkHierarchy(Type firstLoc, Type secondLoc)
	{ 
		if (((firstLoc.isUserType()) || (firstLoc.getDimension() > 0) || (firstLoc.getName().equals(StringType.NAME))) &&
				(secondLoc.getName().equals(NullType.NAME)))
			return true;
		if (firstLoc.isUserType() != secondLoc.isUserType())//one of them is primitive and one is User
			return false;
		else if ((firstLoc.getDimension()>0)|| (secondLoc.getDimension()>0))//at least one of them is array type
		{
			return (firstLoc.getName().equals(secondLoc.getName()));			
		}
		else if (firstLoc.isUserType())//checks class hierarchy 
		{
			ICClass expC = ((ClassType)secondLoc).getClassAST();
			ICClass locC = ((ClassType)firstLoc).getClassAST();
			do
			{
				if (expC.getName().equals(locC.getName()))
					return true;
				expC = ClassTable.getClassAST(expC.getSuperClassName());
			}while (expC.hasSuperClass());
			//if we didn't find it in the hierarchy
			return false;
		}
		return (firstLoc.getName().equals(secondLoc.getName()));
	}
	
	@Override
	public Object visit(Assignment assignment) {
		Type locType = (Type)assignment.getVariable().accept(this);
		if (locType == null)
			return null;
		
		Type expType = (Type)assignment.getAssignment().accept(this);
		if (expType == null)
			return null;
		
		try
		{
			if (!checkHierarchy(locType, expType))
				throw new SemanticError(assignment.getLine(), "Cannot assign " + expType.getName() + " to " + locType.getName());
			return Boolean.TRUE;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}

	}

	@Override
	public Object visit(CallStatement callStatement)
	{
		if (callStatement.getCall().accept(this)==null) 
			return null;
		return Boolean.TRUE;
	}

	private Type getMethodReturnType(SymbolTable s)
	{
		while (!(s instanceof MethodSymbolTable))
			s = s.getParent();//getting to the method table
		String methodName = s.getId();
		s = s.getParent(); // getting the class table containing the method
		try { // extract the return type of the method
			return ((MethodType)(s.getSymbol(methodName).getType())).getReturnType();
		} catch (SemanticError e) {		
			return null;
		}
	}
	
	@Override
	public Object visit(Return returnStatement) { 
		Type methodType = getMethodReturnType(returnStatement.getEnclosingScope());
		try
		{		
			if (methodType == null) // shouldn't happen
				return null;
			if (!returnStatement.hasValue()) // a "return;" statement
			{
				if (methodType.getName().equals(VoidType.NAME))
					return Boolean.TRUE;
				else
					throw new SemanticError(returnStatement.getLine(), "expected return value of type " + methodType.getName());
			}
			else if (methodType.getName().equals(VoidType.NAME)) // if the method shouldn't return a value
				throw new SemanticError(returnStatement.getLine(), "return value not expected");
			else
			{
				Type returnType = (Type)returnStatement.getValue().accept(this); // get the type of the return expression
				if (returnType == null)
					return null;
				if (!checkHierarchy(methodType, returnType)) // check that the returned value is derived from the return type of the method
					throw new SemanticError(returnStatement.getLine(), "Cannot assign " + returnType.getName() + " to " + methodType.getName());
				return Boolean.TRUE;
			}
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	@Override
	public Object visit(If ifStatement)
	{
		try
		{
			Type expType = (Type)ifStatement.getCondition().accept(this);
			if (expType == null)
				return null;
			if (!expType.getName().equals(BoolType.NAME))
				throw new SemanticError(ifStatement.getLine(), "If condition must be of type boolean");
			if (ifStatement.getOperation().accept(this) == null)
				return null;
			if (ifStatement.hasElse() && (ifStatement.getElseOperation().accept(this) == null))
				return null;
			return Boolean.TRUE;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	@Override
	public Object visit(While whileStatement)
	{
		try
		{
			Type expType = (Type)whileStatement.getCondition().accept(this);
			if (expType == null)
				return null;
			if (!expType.getName().equals(BoolType.NAME))
				throw new SemanticError(whileStatement.getLine(), "Condition must be of type boolean");
			if (!whileStatement.getOperation().isBreakContinue()) // continue/break: no reason to accept
				if (whileStatement.getOperation().accept(this) == null)
					return null;
			return Boolean.TRUE;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	private Object handleBreakContinue(Statement s)
	{
		try
		{
			SymbolTable st = s.getEnclosingScope();
			while (!(st instanceof MethodSymbolTable))
			{
				if (((BlockSymbolTable)st).isBreakable())
					return Boolean.TRUE;
				st = st.getParent();
			}
			throw new SemanticError(s.getLine(), "Can only use break and continue inside loops");
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}
	
	@Override
	public Object visit(Break breakStatement)
	{
		return handleBreakContinue(breakStatement);
	}

	@Override
	public Object visit(Continue continueStatement)
	{
		return handleBreakContinue(continueStatement);
	}

	@Override
	public Object visit(StatementsBlock statementsBlock)
	{
		for (Statement s : statementsBlock.getStatements())
		{
			if (s.accept(this) == null)
				return null;
		}
		return Boolean.TRUE;
	}

	@Override
	public Object visit(LocalVariable localVariable)
	{
		try
		{
			if (!localVariable.hasInitValue())
				return Boolean.TRUE;
			Type varType = (Type)localVariable.getType().accept(this);
			if (varType == null)
				return null;
			Type valueType = (Type)localVariable.getInitValue().accept(this);
			if (valueType == null)
				return null;
			if (!checkHierarchy(varType, valueType))
				throw new SemanticError(localVariable.getLine(), "Cannot assign " + valueType.getName() + " to " + varType.getName());
			return Boolean.TRUE;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}
	
	@Override
	public Object visit(VariableLocation location)
	{
		try
		{
			Type externalType = null;	
			SymbolTable t = null;
			
			if (location.isExternal())//gets the location's table
			{	
				externalType = (Type)location.getLocation().accept(this);
				if (externalType == null)
					return null;
				if (!externalType.isUserType())
					throw new SemanticError(location.getLine(),"can only use location of user types");
			
				t = ClassTable.getClassTable(externalType.getName());	
			}
			else //if it's internal
			{
				t = location.getEnclosingScope();
			}
			
			 //if this is the global scope	
			while (t!=null)
			{
				if (t.symbolExists(location.getName()))
				{				
					return t.getSymbol(location.getName()).getType();
				}
				t = t.getParent();		
			}
			throw new SemanticError(location.getLine(), "Undefined Symbol "+ location.getName() + (externalType==null? "": (" in " + externalType.getName())));
			
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	@Override
	public Object visit(ArrayLocation location) 
	{
		try
		{
			Type arrType = (Type) location.getArray().accept(this);
			if (arrType == null)
				return null;
			
			if (arrType.getDimension()==0)
				throw new SemanticError(location.getLine(),arrType.getName() + " is not an array type");
		
			Type indexType = (Type) location.getIndex().accept(this);
			
			if (indexType==null)
				return null;
			
			if (!indexType.getName().equals(IntType.NAME))
				throw new SemanticError(location.getLine(),"the index is not of type integer");
			Type returnType = arrType.clone();
			returnType.setDimension(returnType.getDimension()-1);
			return returnType;
			
		}
		catch(SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}	
	}
		
	private Object checkMethodParams(MethodType mt,Call call)
	{
		try
		{
			if (mt.getParamTypes().length != call.getArguments().size())//check param number
				throw new SemanticError(call.getLine(),"number of params given does not match");
			
			for (int i=0;i<call.getArguments().size();i++)
			{
				Type param = (Type)call.getArguments().get(i).accept(this);
				if (param == null)
					return null;
				if (!checkHierarchy(mt.getParamTypes()[i], param))
					throw new SemanticError(call.getLine(),"the " + (i+1) + " parameter does not match expected type");
			}
			return Boolean.TRUE;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}
	
	
	@Override
	public Object visit(StaticCall call) 
	{
		try
		{
			SymbolTable t = ClassTable.getClassTable(call.getClassName());
			if (t == null)
				throw new SemanticError(call.getLine(),"the class " + call.getClassName()+ " is undefined");	
			while (t.getParent()!=null)
			{
				if (t.symbolExists(call.getName()))
				{
					Type memberType = t.getSymbol(call.getName()).getType();
					if (memberType instanceof MethodType)
					{
						MethodType mt = (MethodType) memberType;
						ClassSymbolTable ct = (ClassSymbolTable) t;
						if (!ct.isStaticMethod(call.getName()))
							throw new SemanticError(call.getLine(),"cannot call a virtual method in current context");
						if (checkMethodParams(mt,call) ==null)
							return null;
						return (mt.getReturnType());
					}
					else
						throw new SemanticError(call.getLine(),call.getName() + " is not a method");
				}		
				t = t.getParent();
			}
			throw new SemanticError(call.getLine(),"the method "+call.getName()+ " is undefined");
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	
	@Override
	public Object visit(VirtualCall call) {
		try
		{
			SymbolTable t;
			if (call.isExternal())
			{
				Type externalType = (Type) call.getLocation().accept(this);
				if (externalType == null)
					return null;
				if (!externalType.isUserType())
					throw new SemanticError(call.getLine(),"can only use location of user types");
				t = ClassTable.getClassTable(externalType.getName());		
			}
			else
			{
				t = call.getEnclosingScope();
			}
		
			while (t.getParent()!=null)
			{
				if (t.symbolExists(call.getName()))
				{
					Type memberType = t.getSymbol(call.getName()).getType();
					if (memberType instanceof MethodType)
					{
						MethodType mt = (MethodType) memberType;
						if (checkMethodParams(mt,call) ==null)
							return null;
						return (mt.getReturnType());
					}
					else
						throw new SemanticError(call.getLine(),call.getName() + " is not a method");
				}
				t = t.getParent();
			}
			throw new SemanticError(call.getLine(),"undefined method "+ call.getName());
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}		
	}

	@Override
	public Object visit(This thisExpression) 
	{
		try
		{
			SymbolTable t = thisExpression.getEnclosingScope();
			String enclosingMethodName = null;
			while (t.getTableKind()!= SymbolTableKind.CLASS)//gets to the class enclosing the current call
			{
				if (t.getTableKind()== SymbolTableKind.METHOD)
					enclosingMethodName = t.getId();
				t = t.getParent();
			}
			if (enclosingMethodName==null)//should never happen
				throw new SemanticError (thisExpression.getLine(),"this must be used in a method");
			ClassSymbolTable ct = (ClassSymbolTable) t;
			if (ct.isStaticMethod(enclosingMethodName))
				throw new SemanticError(thisExpression.getLine(),"cannot use this in static context");
			return new ClassType(ClassTable.getClassAST(t.getId()));
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	@Override
	public Object visit(NewClass newClass) {
		try
		{
			ICClass c = ClassTable.getClassAST(newClass.getName());
			if (c==null)
				throw new SemanticError(newClass.getLine(),"class "+ newClass.getName()+ " is not defined");
			return new ClassType(c);
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}		
	}

	@Override
	public Object visit(NewArray newArray) {
		try
		{
			Type elemType = (Type)newArray.getType().accept(this);
			if (elemType == null)
				return null;
			if (elemType.getName().equals(VoidType.NAME) || elemType.getName().equals(NullType.NAME))
				throw new SemanticError(newArray.getLine(),"cannot create an array from type null/void");
			Type sizeType = (Type)newArray.getSize().accept(this);
			if (sizeType== null)
				return null;
			if (!sizeType.getName().equals(IntType.NAME))
				throw new SemanticError(newArray.getLine(),"array size must be of type int");
			Type ret = elemType.clone();
			ret.setDimension(elemType.getDimension()+1);
			return ret;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}			
	}

	@Override
	public Object visit(Length length) 
	{
		try
		{
			Type arrType = (Type) length.getArray().accept(this);
			if (arrType==null)
				return null;
			if(arrType.getDimension()==0)
				throw new SemanticError(length.getLine(),"length can only be used on array types");
			return new IntType();
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}			
	}
	
	@Override
	public Object visit(MathBinaryOp binaryOp) {
		try
		{
			Type firstType = (Type) binaryOp.getFirstOperand().accept(this);
			if (firstType == null)
				return null;
			Type secondType = (Type) binaryOp.getSecondOperand().accept(this);
			if (secondType == null)
				return null;
			
			if (!firstType.getName().equals(secondType.getName()))
				throw new SemanticError(binaryOp.getLine(),"operands must be of the same type");
			if ((!firstType.getName().equals(IntType.NAME)) && (!firstType.getName().equals(StringType.NAME)))
				throw new SemanticError(binaryOp.getLine(),"cannot apply binary opps on " + firstType.getName());
			if ((firstType.getName().equals(StringType.NAME))&& (binaryOp.getOperator()!=BinaryOps.PLUS))
				throw new SemanticError(binaryOp.getLine(),"the only binary operations defined for Strings is PLUS");
			return firstType.clone();
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}				
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp) {
		try
		{
			Type firstType = (Type) binaryOp.getFirstOperand().accept(this);
			if (firstType == null)
				return null;
			Type secondType = (Type) binaryOp.getSecondOperand().accept(this);
			if (secondType == null)
				return null;
			
			if ((binaryOp.getOperator()==BinaryOps.LOR) ||
				(binaryOp.getOperator()==BinaryOps.LAND))
			{
				if ((firstType.getName().equals(secondType.getName())) && firstType.getName().equals(BoolType.NAME))
					return new BoolType();
				else
					throw new SemanticError(binaryOp.getLine(),"|| and && are only defined between two booleans");
			}
			if ((binaryOp.getOperator()==BinaryOps.GT) ||
				(binaryOp.getOperator()==BinaryOps.GTE)||
				(binaryOp.getOperator()==BinaryOps.LT)||
				(binaryOp.getOperator()==BinaryOps.LTE))
			{
				if ((firstType.getName().equals(secondType.getName())) && firstType.getName().equals(IntType.NAME))
					return new BoolType();
				else
					throw new SemanticError(binaryOp.getLine(),"<,<=,>,>= are only defined between two integers");
			}
			else
			{
				if ((checkHierarchy(firstType, secondType)) || checkHierarchy(secondType, firstType))
					return new BoolType();
				else
					throw new SemanticError(binaryOp.getLine(),"the two types are not subTypes of each other");
			}		
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}				
		
	}

	@Override
	public Object visit(MathUnaryOp unaryOp) {
		try
		{
			Type t = (Type) unaryOp.getOperand().accept(this);
			if (t==null)
				return null;
			if (t.getName().equals(IntType.NAME))
				return new IntType();
			else
				throw new SemanticError(unaryOp.getLine(),"- can only be used before an integer");	
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}				
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp) {
		try
		{
			Type t = (Type) unaryOp.getOperand().accept(this);
			if (t==null)
				return null;
			if (t.getName().equals(BoolType.NAME))
				return new BoolType();
			else
				throw new SemanticError(unaryOp.getLine(),"! can only be used before a boolean");	
		}
		catch (SemanticError e)
		{
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
	}

	@Override
	public Object visit(Literal literal) 
	{
		LiteralTypes lt = literal.getType();
		switch (lt)
		{
		case INTEGER : return new IntType();
		case STRING : return new StringType();
		case TRUE :
		case FALSE: return new BoolType();
		case NULL : return new NullType();
		}
		return null;//shouldn't get here
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock) 
	{
		return expressionBlock.getExpression().accept(this);
	}

	@Override
	public Object visit(FieldOrMethod fieldOrMethod) 
	{
		for (Field field : fieldOrMethod.getFields()) 
		{
			if (field.accept(this) == null)
				return null;
		}
		for (Method method : fieldOrMethod.getMethods()) 
		{
			if (method.accept(this) == null)
				return null;
		}
		return Boolean.TRUE;
	}
	

}
