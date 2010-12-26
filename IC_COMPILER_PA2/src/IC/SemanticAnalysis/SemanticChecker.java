package IC.SemanticAnalysis;

import IC.AST.ArrayLocation;
import IC.AST.Assignment;
import IC.AST.Break;
import IC.AST.CallStatement;
import IC.AST.Continue;
import IC.AST.ExpressionBlock;
import IC.AST.Field;
import IC.AST.FieldOrMethod;
import IC.AST.Formal;
import IC.AST.ICClass;
import IC.AST.If;
import IC.AST.Length;
import IC.AST.LibraryMethod;
import IC.AST.Literal;
import IC.AST.LocalVariable;
import IC.AST.LogicalBinaryOp;
import IC.AST.LogicalUnaryOp;
import IC.AST.MathBinaryOp;
import IC.AST.MathUnaryOp;
import IC.AST.Method;
import IC.AST.NewArray;
import IC.AST.NewClass;
import IC.AST.PrimitiveType;
import IC.AST.Program;
import IC.AST.Return;
import IC.AST.Statement;
import IC.AST.StatementsBlock;
import IC.AST.StaticCall;
import IC.AST.StaticMethod;
import IC.AST.This;
import IC.AST.UserType;
import IC.AST.VariableLocation;
import IC.AST.VirtualCall;
import IC.AST.VirtualMethod;
import IC.AST.Visitor;
import IC.AST.While;
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
		}
		catch (SemanticError e) {
			System.err.println(e.getMessage());
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
		
		return Boolean.TRUE; 
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
			return (firstLoc.equals(secondLoc));			
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
		Type expType = (Type)assignment.getAssignment().accept(this);
		try
		{
			if (!checkHierarchy(locType, expType))
				throw new SemanticError(assignment.getLine(), "Cannot assign " + expType.getName() + " to " + locType.getName());
			return Boolean.TRUE;
		}
		catch (SemanticError e)
		{
			System.err.println(e.getMessage());
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
			System.err.println(e.getMessage());
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
			System.err.println(e.getMessage());
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
			System.err.println(e.getMessage());
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
			System.err.println(e.getMessage());
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
			System.err.println(e.getMessage());
			return null;
		}
	}

	///////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	////////////WE ARE HERE//////////
	/////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////
	
	@Override
	public Object visit(VariableLocation location)
	{
		try
		{
			SymbolTable t = location.getEnclosingScope();
			while (!t.symbolExists(location.getName()))
			return location.getEnclosingScope().getSymbol(location.getName()).getType();
		}
		catch (SemanticError e)
		{
			
		}
		
		
		/*
		 * 		if (location.isExternal()) // checks that the location isn't null
		{
			location.getLocation().setEnclosingScope(location.getEnclosingScope());
			if (location.getLocation().accept(this) == null)
				return null;
		} else
			try {
				location.getEnclosingScope().getSymbol(location.getName());
			} catch (SemanticError e) {
				e.setLineNumber(location.getLine());
				System.err.println(e.getMessage());
				return null;
			}
		return Boolean.TRUE;
		 */
		 */
	}

	@Override
	public Object visit(ArrayLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewClass newClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FieldOrMethod fieldOrMethod) {
		// TODO Auto-generated method stub
		return null;
	}

}
