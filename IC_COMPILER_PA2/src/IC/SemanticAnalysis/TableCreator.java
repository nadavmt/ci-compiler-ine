package IC.SemanticAnalysis;

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
			return null;
		}
		return s;
	}

	public Object visit(VariableLocation location) {
		StringBuffer output = new StringBuffer();

		indent(output, location);
		output.append("Reference to variable: " + location.getName());
		if (location.isExternal())
			output.append(", in external scope");
		if (location.isExternal()) {
			++depth;
			output.append(location.getLocation().accept(this));
			--depth;
		}
		return output.toString();
	}

	public Object visit(ArrayLocation location) {
		StringBuffer output = new StringBuffer();

		indent(output, location);
		output.append("Reference to array");
		depth += 2;
		output.append(location.getArray().accept(this));
		output.append(location.getIndex().accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(StaticCall call) {
		StringBuffer output = new StringBuffer();

		indent(output, call);
		output.append("Call to static method: " + call.getName()
				+ ", in class " + call.getClassName());
		depth += 2;
		for (Expression argument : call.getArguments())
			output.append(argument.accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(VirtualCall call) {
		StringBuffer output = new StringBuffer();

		indent(output, call);
		output.append("Call to virtual method: " + call.getName());
		if (call.isExternal())
			output.append(", in external scope");
		depth += 2;
		if (call.isExternal())
			output.append(call.getLocation().accept(this));
		for (Expression argument : call.getArguments())
			output.append(argument.accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(This thisExpression) {
		StringBuffer output = new StringBuffer();

		indent(output, thisExpression);
		output.append("Reference to 'this' instance");
		return output.toString();
	}

	public Object visit(NewClass newClass) {
		StringBuffer output = new StringBuffer();

		indent(output, newClass);
		output.append("Instantiation of class: " + newClass.getName());
		return output.toString();
	}

	public Object visit(NewArray newArray) {
		StringBuffer output = new StringBuffer();

		indent(output, newArray);
		output.append("Array allocation");
		depth += 2;
		output.append(newArray.getType().accept(this));
		output.append(newArray.getSize().accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(Length length) {
		StringBuffer output = new StringBuffer();

		indent(output, length);
		output.append("Reference to array length");
		++depth;
		output.append(length.getArray().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(MathBinaryOp binaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, binaryOp);
		output.append("Mathematical binary operation: "
				+ binaryOp.getOperator().getDescription());
		depth += 2;
		output.append(binaryOp.getFirstOperand().accept(this));
		output.append(binaryOp.getSecondOperand().accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(LogicalBinaryOp binaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, binaryOp);
		output.append("Logical binary operation: "
				+ binaryOp.getOperator().getDescription());
		depth += 2;
		output.append(binaryOp.getFirstOperand().accept(this));
		output.append(binaryOp.getSecondOperand().accept(this));
		depth -= 2;
		return output.toString();
	}

	public Object visit(MathUnaryOp unaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, unaryOp);
		output.append("Mathematical unary operation: "
				+ unaryOp.getOperator().getDescription());
		++depth;
		output.append(unaryOp.getOperand().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(LogicalUnaryOp unaryOp) {
		StringBuffer output = new StringBuffer();

		indent(output, unaryOp);
		output.append("Logical unary operation: "
				+ unaryOp.getOperator().getDescription());
		++depth;
		output.append(unaryOp.getOperand().accept(this));
		--depth;
		return output.toString();
	}

	public Object visit(Literal literal) {
		StringBuffer output = new StringBuffer();

		indent(output, literal);
		output.append(literal.getType().getDescription() + ": "
				+ literal.getType().toFormattedString(literal.getValue()));
		return output.toString();
	}

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
	public Object visit(FieldOrMethod fieldOrMethod) {
		StringBuffer output = new StringBuffer();

		indent(output, fieldOrMethod);
		output.append("Class Members:");
		depth += 2;
		for (Field field : fieldOrMethod.getFields())
			output.append(field.accept(this));
		
		for (Method method : fieldOrMethod.getMethods())
			output.append(method.accept(this));
		
		depth -= 2;
		return output.toString();
	}
}
