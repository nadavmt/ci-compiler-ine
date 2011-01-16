package IC.LIR;

import java.io.PrintWriter;

import IC.AST.*;
import IC.SemanticAnalysis.*;



public class LirTranlationVisitor extends BaseVisitor {

	PrintWriter out;
	int regCounter = 1;
	int labelCounter = 1;
	String exitLabel="";
	String contLabel="";
	
	public LirTranlationVisitor(PrintWriter output)
	{
		out = output;
	}
	
	
	public Object visit(PrimitiveType type) {
		return true;
	}

	public Object visit(UserType type) {
		return true;
	}

	public Object visit(Field field) {
			
		return true;
	}

	public Object visit(LibraryMethod method) {
	
		return true;
	}

	public Object visit(Formal formal) {
	
		return true;
	}

	public Object visitMethod(Method method) {
		out.println(method.getName()+ ":");
		regCounter = 1;
		return super.visitMethod(method);
		
	}
	

	public Object visit(Assignment assignment) {
		String varResult;
		String assResult;
		varResult = (String) assignment.getAssignment().accept(this);
		assResult = (String) assignment.getVariable().accept(this);
		String command = "Move";
		if (varResult.contains(".")|| assResult.contains("."))
			command = "MoveField";
		
		out.println(command + " "+ assResult + "," + varResult);
		out.println(command + " "+ varResult + ",R"+ regCounter);
		regCounter++;
		
		return true;
	}


	public Object visit(Return returnStatement) {	
		String retValue = "";
		if (returnStatement.hasValue()) {
			retValue = (String) returnStatement.getValue().accept(this);
		}
		out.println("Return " + retValue);
		return true;
	}

	public Object visit(If ifStatement) {
		String condResult = (String) ifStatement.getCondition().accept(this);
		String falseLabel = "_false_label"+ labelCounter;
		labelCounter++;
		String endLabel = "_end_label"+labelCounter;
		labelCounter++;
		out.println("Compare 0,"+condResult);
		out.println("JumpTrue "+falseLabel);
		ifStatement.getOperation().accept(this);
		out.println("Jump "+endLabel);
		out.println(falseLabel+":");
		if (ifStatement.hasElse())
			{
			ifStatement.getElseOperation().accept(this);
			}
		out.println(endLabel+":");
		
		return true;
	}

	public Object visit(While whileStatement) {
		String testLabel = "_test_label"+ regCounter++;
		String endLabel = "_end_label" + regCounter++;
		String prevExit = exitLabel;
		String prevCont = contLabel;
		exitLabel = endLabel;
		contLabel = testLabel;
		out.println(testLabel+":");
		String condResult = (String) whileStatement.getCondition().accept(this);
		out.println("Compare 0,"+condResult);
		out.println("JumpTrue "+endLabel);
		whileStatement.getOperation().accept(this);
		out.println("Jump "+ testLabel);
		out.println(endLabel+":");
		exitLabel = prevExit;
		contLabel = prevCont;
		return true;
	}

	public Object visit(Break breakStatement) {
		out.println("Jump "+ exitLabel);
		return true;
	}

	public Object visit(Continue continueStatement) {
		out.println("Jump "+ contLabel);
		return true;
	}
/*****************************************************************/
	
	public Object visit(StatementsBlock statementsBlock) {
		for (Statement statement : statementsBlock.getStatements())
			statement.accept(this);
		
		return true;
	}

	public Object visit(LocalVariable localVariable) {
		operationCounter++;
		localVariable.getType().accept(this);
		if (localVariable.hasInitValue()) {
			localVariable.getInitValue().accept(this);
		}
		
		return true;
	}

	public Object visit(VariableLocation location) {
		
		if (location.isExternal()) {
			location.getLocation().accept(this);
		}
		return true;
	}

	public Object visit(ArrayLocation location) {
		operationCounter++;
		location.getArray().accept(this);
		location.getIndex().accept(this);
		
		return true;
	}

	public Object visit(StaticCall call) {

		for (Expression argument : call.getArguments())
			argument.accept(this);
		
		return true;
	}

	public Object visit(VirtualCall call) {

		if (call.isExternal())
			call.getLocation().accept(this);
		for (Expression argument : call.getArguments())
			argument.accept(this);
		
		return true;
	}

	public Object visit(This thisExpression) {
		return true;
	}

	public Object visit(NewClass newClass) {
		return true;
	}

	public Object visit(NewArray newArray) {
		newArray.getType().accept(this);
		newArray.getSize().accept(this);
		return true;
	}

	public Object visit(Length length) {
		length.getArray().accept(this);
		return true;
	}

	public Object visit(MathBinaryOp binaryOp) {
		binaryOp.getFirstOperand().accept(this);
		binaryOp.getSecondOperand().accept(this);
		return true;
	}

	public Object visit(LogicalBinaryOp binaryOp) {
		binaryOp.getFirstOperand().accept(this);
		binaryOp.getSecondOperand().accept(this);
		return true;
	}

	public Object visit(MathUnaryOp unaryOp) {
		unaryOp.getOperand().accept(this);
		return true;
	}

	public Object visit(LogicalUnaryOp unaryOp) {
		unaryOp.getOperand().accept(this);
		return true;
	}

	public Object visit(Literal literal) {
		return true;
	}

	public Object visit(ExpressionBlock expressionBlock) {
		expressionBlock.getExpression().accept(this);
		return true;
	}

	public Object visit(FieldOrMethod fieldOrMethod) {
		for (Field field : fieldOrMethod.getFields())
			field.accept(this);
		
		for (Method method : fieldOrMethod.getMethods())
			method.accept(this);
		
		return true;
	}

}
