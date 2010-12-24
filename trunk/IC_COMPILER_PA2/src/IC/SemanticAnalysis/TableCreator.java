package IC.SemanticAnalysis;

import java.text.FieldPosition;
import java.util.Collection;

import sun.nio.cs.ext.ISCII91;
import IC.DataTypes;
import IC.AST.*;
import IC.Parser.SemanticError;

public class TableCreator implements Visitor {
	
	private String file;
	
	public TableCreator(String file)
	{
		this.file = file;
	}
	
	private Object visitMethod(Method method)
	{
		try//itai:24.12
		{
			SymbolTable t = new MethodSymbolTable(method.getName(),
				method.getEnclosingScope());
		
			if (!addSymbols(t, method.getFormals()))
				return null;
		
			for (Statement statement : method.getStatements())
			{
				statement.setEnclosingScope(t);
				if (statement.accept(this) == null)
					return null;
			}
			
			Type returnType = (Type)method.getType().accept(this);
			if (returnType == null)
			return null;
		
			Type[] paramTypes = new Type[method.getFormals().size()];
			for (int i = 0; i < paramTypes.length; i++)
			{
				Formal f = method.getFormals().get(i);
				paramTypes[i] = t.getSymbol(f.getName()).getType(); 
			}
			
			return new Symbol(method.getName(), Kind.Method, new MethodType(paramTypes, returnType));
		}
		catch (SemanticError e)
		{
			e.setLineNumber(method.getLine());
			System.err.println(e.getMessage());
			return null;
		}
	
	}

	private boolean addSymbols(SymbolTable t, Collection<? extends ASTNode> c)
	{
		try
		{
			for (ASTNode node : c)
				addSymbol(t, node);
		}
		catch(SemanticError se)
		{
			System.err.println(se.getMessage());
			return false;
		}
		catch(InternalSemanticError e)
		{
			return false;
		}
		
		return true;
	}
	
	private void addSymbol(SymbolTable t, ASTNode node) throws SemanticError,InternalSemanticError {
		node.setEnclosingScope(t);
		Symbol s = (Symbol)node.accept(this);
		if (s == null)
			throw new InternalSemanticError();
		t.addEntry(s);
	}
	
	public Object visit(Program program){
		SymbolTable t = new GlobalSymbolTable(file);
		
		if (!addSymbols(t, program.getClasses()))
			return null;
		
		//TODO: add second pass
		return t;
	}

	public Object visit(ICClass icClass) {
		SymbolTable t = new ClassSymbolTable(icClass.getName(),
				icClass.getEnclosingScope());
		
		if (!addSymbols(t, icClass.getFields()))
			return null;
		if (!addSymbols(t, icClass.getMethods()))
			return null;
		
		return new Symbol(icClass.getName(), Kind.Class, new ClassType(icClass));
	}
	
	public Object visit(VirtualMethod method) {
		return visitMethod(method);
	}
	
	public Object visit(StaticMethod method) {
		return visitMethod(method);
	}
	
	public Object visit(LibraryMethod method) {
		return visitMethod(method);
	}
	
	public Object visit(Formal formal) {
		Type t = (Type)formal.getType().accept(this);
		if (t == null)
			return null;
		return new Symbol(formal.getName(), Kind.Field, t);
	}
	
	public Object visit(StatementsBlock statementsBlock) {
		BlockSymbolTable t = new BlockSymbolTable(statementsBlock.getEnclosingScope());
		if (!addSymbols(t, statementsBlock.getStatements()))
			return null;
		return t;
	}
	
	public Object visit(Field field) {
		Type t = (Type)field.getType().accept(this);
		if (t == null)
			return null;
		return new Symbol(field.getName(), Kind.Field, t);
	}
	
	public Object visit(PrimitiveType type) {
		//TODO: check how to add arrays in the correct way
		if (type.getDimension() > 0)
			return TypeTable.arrayType(type);
		
		switch(type.getType())
		{
			case BOOLEAN:
				return TypeTable.boolType;
			case INT:
				return TypeTable.intType;
			case STRING:
				return TypeTable.stringType;
			case VOID:
				return TypeTable.voidType;
			default:
				return null;
		}
	}

	public Object visit(UserType type) {
		return Boolean.TRUE;
	}
	
	public Object visit(Assignment assignment) {
		assignment.getVariable().setEnclosingScope(assignment.getEnclosingScope());
		assignment.getAssignment().setEnclosingScope(assignment.getEnclosingScope());
		
		if (assignment.getVariable().accept(this) == null)
			return null;
		if (assignment.getAssignment().accept(this) == null)
			return null;
		return Boolean.TRUE;
	}

	public Object visit(CallStatement callStatement) {
		callStatement.getCall().setEnclosingScope(callStatement.getEnclosingScope());
		if (callStatement.getCall().accept(this) == null)
			return null;
		return Boolean.TRUE;
	}

	public Object visit(Return returnStatement) {
		if (returnStatement.hasValue()) {
			returnStatement.getValue().setEnclosingScope(returnStatement.getEnclosingScope());
			if (returnStatement.getValue().accept(this) == null)
				return null;
		}
		
		return Boolean.TRUE;
	}

	public Object visit(If ifStatement) {
		ifStatement.getCondition().setEnclosingScope(ifStatement.getEnclosingScope());
		ifStatement.getOperation().setEnclosingScope(ifStatement.getEnclosingScope());
		if (ifStatement.hasElse())
			ifStatement.getElseOperation().setEnclosingScope(ifStatement.getEnclosingScope());
		
		if (ifStatement.getCondition().accept(this) == null)
			return null;
		if (ifStatement.getOperation().accept(this) == null)
			return null;
		if (ifStatement.hasElse())
		{
			if (ifStatement.getElseOperation().accept(this) == null)
				return null;
		}

		return Boolean.TRUE;
	}

	public Object visit(While whileStatement) {
		whileStatement.getCondition().setEnclosingScope(whileStatement.getEnclosingScope());
		whileStatement.getOperation().setEnclosingScope(whileStatement.getEnclosingScope());
		if (whileStatement.getCondition().accept(this) == null)
			return null;
		if (whileStatement.getOperation().accept(this) == null)
			return null;
		
		return Boolean.TRUE;
	}

	public Object visit(Break breakStatement) {
		return Boolean.TRUE;
	}

	public Object visit(Continue continueStatement) {
		return Boolean.TRUE;
	}

	public Object visit(LocalVariable localVariable) {
		if (localVariable.hasInitValue()) {
			localVariable.getInitValue().setEnclosingScope(localVariable.getEnclosingScope());
			if (localVariable.getInitValue().accept(this) == null)
				return null;
		}

		localVariable.getType().setEnclosingScope(localVariable.getEnclosingScope());
		Type t = (Type)localVariable.getType().accept(this);
		if (t == null)
			return null;
		Symbol s = new Symbol(localVariable.getName(), Kind.Field, t);
		try {
			localVariable.getEnclosingScope().addEntry(s);
		} catch (SemanticError e) {
			//i added these lines
			e.setLineNumber(localVariable.getLine());
			System.err.println(e.getMessage());
			return null;//why didn't we print the error message
		}
		return s;
	}
/*itai 24.12*/
	public Object visit(VariableLocation location) {
		if (location.isExternal()) //checks that the location isn't null
		{
			location.getLocation().setEnclosingScope(location.getEnclosingScope());
			if (location.getLocation().accept(this)==null)
				return null;
		}
		else
			try{
			location.getEnclosingScope().getSymbol(location.getName());
				}
			catch (SemanticError e)
			{
				e.setLineNumber(location.getLine());
				System.err.println(e.getMessage());
				return null;
			}
		return Boolean.TRUE;
	}
	

	public Object visit(ArrayLocation location) 
	{
		location.getArray().setEnclosingScope(location.getEnclosingScope());
		location.getIndex().setEnclosingScope(location.getEnclosingScope());
		if ((location.getIndex().accept(this)==null)|| (location.getArray().accept(this)==null))
			return null;
		return Boolean.TRUE;
	}
		
	private Object iterateOverParams(Call call)//will be used for both static and virtual call
	{
		for (Expression e : call.getArguments())
		{
			e.setEnclosingScope(call.getEnclosingScope());
			if (e.accept(this)==null)
				return null;
		}
		return Boolean.TRUE;
	}
	
	public Object visit(StaticCall call) 
	{
		return iterateOverParams(call);
	}
	
	public Object visit(VirtualCall call) 
	{
		if (call.isExternal())//location !=null
		{
			call.getLocation().setEnclosingScope(call.getEnclosingScope());
			if (call.getLocation().accept(this)==null)
				return null;
		}
		
		return iterateOverParams(call);
	}
	
	public Object visit(This thisExpression) {
		return Boolean.TRUE;
	}

	public Object visit(NewClass newClass) {
		return Boolean.TRUE;
	}

	public Object visit(NewArray newArray) 
	{
		newArray.getType().setEnclosingScope(newArray.getEnclosingScope());
		newArray.getSize().setEnclosingScope(newArray.getEnclosingScope());
		if ((newArray.getType().accept(this)==null)|| (newArray.getSize().accept(this)==null))
			return null;
		return Boolean.TRUE;
	}


	public Object visit(Length length) 
	{
		length.getArray().setEnclosingScope(length.getEnclosingScope());
		if (length.getArray().accept(this)==null)
			return null;
		return Boolean.TRUE;
	}

	private Object handleSingleExpression(Expression target, SymbolTable scope)
	{
		target.setEnclosingScope(scope);
		return target.accept(this);
	}
	
	private Object binaryOperationHandler(BinaryOp binaryOp)
	{
		if (handleSingleExpression(binaryOp.getFirstOperand(),binaryOp.getEnclosingScope())==null)
				return null;
		return (handleSingleExpression(binaryOp.getFirstOperand(),binaryOp.getEnclosingScope()));
	}
	
	public Object visit(MathBinaryOp binaryOp) 
	{
		return binaryOperationHandler(binaryOp);
	}
		

	public Object visit(LogicalBinaryOp binaryOp) 
	{
		return binaryOperationHandler(binaryOp);	
	}

	public Object visit(MathUnaryOp unaryOp) 
	{
		return handleSingleExpression(unaryOp.getOperand(), unaryOp.getEnclosingScope());
	}

	public Object visit(LogicalUnaryOp unaryOp) 
	{
		return handleSingleExpression(unaryOp.getOperand(), unaryOp.getEnclosingScope());
	}

	public Object visit(Literal literal) 
	{
		return Boolean.TRUE;
	}

	/**where do we ever use expression block????**/
	
	public Object visit(ExpressionBlock expressionBlock) {
		StringBuffer output = new StringBuffer();

		indent(output, expressionBlock);
		output.append("Parenthesized expression");
		++depth;
		output.append(expressionBlock.getExpression().accept(this));
		--depth;
		return output.toString();
	}

	@Override
	public Object visit(FieldOrMethod fieldOrMethod) 
	{
		for (Field field : fieldOrMethod.getFields())
		{
			field.setEnclosingScope(fieldOrMethod.getEnclosingScope());
			if (field.accept(this)==null)
				return null;
		}
		for (Method method : fieldOrMethod.getMethods())
		{
			method.setEnclosingScope(fieldOrMethod.getEnclosingScope());
			if (visitMethod(method)==null)
				return null;
		}
		return Boolean.TRUE;
	}
}
t(this));
		--depth;
		re