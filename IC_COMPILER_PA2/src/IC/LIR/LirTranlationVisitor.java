package IC.LIR;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import IC.BinaryOps;
import IC.LiteralTypes;
import IC.AST.*;
import IC.Parser.SemanticError;
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
		
	public Object visit(Field field)
	{
		out.println("Move 0, " + field.getName());
		return true;
	}
	public Object visitMethod(Method method) {
		out.println(method.getName()+ ":");
		regCounter = 1;
		super.visitMethod(method);
		//try {
			//MethodType mt = (MethodType)method.getEnclosingScope().getSymbol(method.getName()).getType();
			//if (mt.getReturnType() instanceof VoidType)
				//out.println("Return Rdummy");
		//} catch (SemanticError e) {
		//}
		return true;
	}
	

	public Object visit(Assignment assignment) {
		String varResult;
		String assResult;
		varResult = (String) assignment.getAssignment().accept(this);
		assResult = (String) assignment.getVariable().accept(this);
		String command = "Move";
		if (varResult.contains(".")|| assResult.contains("."))
			command = "MoveField";
		else if (varResult.contains("[")|| assResult.contains("["))
			command = "MoveArray";
		
		
		out.println(command + " "+ assResult + "," + varResult);
		//out.println(command + " "+ varResult + ",R"+ regCounter);
		//regCounter++;
		
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
		String testLabel = "_test_label"+ labelCounter++;
		String endLabel = "_end_label" + labelCounter++;
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
	
	public Object visit(StatementsBlock statementsBlock) {
		for (Statement statement : statementsBlock.getStatements())
			statement.accept(this);
		
		return true;
	}

	public Object visit(LocalVariable localVariable) {
		String initValue = "0";
		if (localVariable.hasInitValue()) {
			initValue = (String) localVariable.getInitValue().accept(this);
			//out.println("Move "+ initValue+ ", R"+ regCounter);
			//out.println("Move R"+ regCounter + ", " + localVariable.getName());
			//regCounter++;
		}
		out.println("Move "+ initValue+ ", " + localVariable.getName());
		return true;
	}

	public Object visit(VariableLocation location) {
		
		String name ="";
		if (location.isExternal()) {
			name = (String) location.getLocation().accept(this) + "." + location.getName();
			out.println("MoveField " + name + ", R" + regCounter);
		}
		else
		{
			out.println("Move "+ location.getName() + ", R" + regCounter);
		}
		 name = "R" + regCounter;
		 regCounter++;
		return name;
	}

	public Object visit(ArrayLocation location) {
	
		String arrName = (String) location.getArray().accept(this);
		String indexName = (String) location.getIndex().accept(this);
		String name = "R"+ regCounter++;
		out.println("MoveArray " + arrName+ "[" + indexName + "], "+ name);
		return name;

	}

	public Object visit(StaticCall call) {
		if (call.getClassName().equals("Library"))
			return visitLibraryCall(call);
		List<String> variables = new ArrayList<String>();
		for (Expression argument : call.getArguments())
			variables.add((String)argument.accept(this));	
		Method m = findMethodAST(call.getClassName(), call.getName());
		
			
		out.print("StaticCall "+ call.getName());
		
		printMethodCall(variables, m.getFormals());
		
		SymbolTable st = ClassTable.getClassTable(call.getClassName());
		Symbol s=null;
		try {
			s = st.getSymbol(call.getName());
		} catch (SemanticError e) {}
		
		String result = "Rdummy";
		if (! (((MethodType)s.getType()).getReturnType() instanceof VoidType))
		{
			 result = "R"+ regCounter++;
			
			return result;
		}
		
		out.println(", "+ result);	
		return result; 
	}
	
	public Object visitLibraryCall(StaticCall call) {
		
		List<String> variables = new ArrayList<String>();
		for (Expression argument : call.getArguments())
			variables.add((String)argument.accept(this));	
				
		out.print("Library "+ call.getName());
		
		out.print("(");
		for (int i =0; i<variables.size(); i++)
		{
			String v = variables.get(i);
			if (i>0)
				out.print(",");
			out.print(v);
		}
		out.print(")");
		
		SymbolTable st = ClassTable.getClassTable(call.getClassName());
		Symbol s=null;
		try {
			s = st.getSymbol(call.getName());
		} catch (SemanticError e) {}
		
		String result = "Rdummy";
		if (! (((MethodType)s.getType()).getReturnType() instanceof VoidType))
		{
			 result = "R"+ regCounter++;
			
			return result;
		}
		
		out.println(", "+ result);	
		return result; 
	}
	
	private Method findMethodAST(String className, String methodName)
	{
		for (Method m : ClassTable.getClassAST(className).getMethods())
		{
			if (m.getName().equals(methodName))
					return m;
				
		}
		return null;
	}

	private void printMethodCall(List<String> variables, List<Formal> formals)
	{
		out.print("(");
		for (int i =0; i<formals.size(); i++)
		{
			String f = formals.get(i).getName();
			String v = variables.get(i);
			if (i>0)
				out.print(",");
			out.print(f + "=" + v);
		}
		out.print(")");
	}
	public Object visit(VirtualCall call) {
		List<String> arguments = new ArrayList<String>();
		String funcName = "";
		SymbolTable st;
		if (call.isExternal())
		{
			funcName += (String)call.getLocation().accept(this)+ ".";
			st = call.getLocation().getEnclosingScope();
		}
		else
		{
			out.println("Move this, R"+ regCounter);
			funcName += "R"+ regCounter++ + ".";
			st = call.getEnclosingScope();
		}
			funcName += call.getName();
			
		for (Expression argument : call.getArguments())
			arguments.add((String)argument.accept(this));
		
		st = st.findSymbolTable(call.getName(), Integer.MAX_VALUE);
		
		Method m = findMethodAST(st.getId(), call.getName());
		
		out.print("VirtualCall "+ funcName);
		printMethodCall(arguments, m.getFormals());
		
		Symbol s=null;
		try {
			s = st.getSymbol(call.getName());
		} catch (SemanticError e) {}
		
		String result = "Rdummy";
		if (! (((MethodType)s.getType()).getReturnType() instanceof VoidType))
		{
			result = "R"+ regCounter++;	
		}
		
		out.println(", "+ result);
		return result;
	}

	public Object visit(This thisExpression) {
		return true;
	}

	public Object visit(NewClass newClass) {
		String name = "R" + regCounter++;
		int size = ClassTable.getClassAST(newClass.getName()).getFields().size() *4;
		out.println("Library __allocateObject(" + size + "), " + name);
		out.println("MoveField _DV_"+ newClass.getName() + ", "+ name + ".0");
		return name; 
	}
	
	

	public Object visit(NewArray newArray) {
		
		String sizeReg = (String) newArray.getSize().accept(this);
		out.println("_allocateArray("+ sizeReg+ ")");
		return true;
	}

	public Object visit(Length length) {
		String name = (String) length.getArray().accept(this);
		String result = "R" + regCounter++;
		out.println("ArrayLength "+ name + ", "+ result);
		return result;
		
	}

	public Object visit(MathBinaryOp binaryOp) {
		String first = (String) binaryOp.getFirstOperand().accept(this);
		String second = (String) binaryOp.getSecondOperand().accept(this);
		String operation = "";
		switch (binaryOp.getOperator()){
			case DIVIDE: operation = "Div";break;
			case PLUS: operation = "Add";break;
			case MULTIPLY: operation = "Mul";break;
			case MINUS: operation = "Sub";break;
			case MOD: operation = "Mod";break;
		}
		out.println(operation + " " + first + ", "+ second);
		
		return second;
	}

	public Object visit(LogicalBinaryOp binaryOp) {
		String first = (String) binaryOp.getFirstOperand().accept(this);
		
		String endLabel = "_end_label"+ labelCounter++;
		String trueLabel = "_true_label" + labelCounter++;
		if (binaryOp.getOperator()==BinaryOps.LAND)
		{
			out.println("Comapre 0, "+ first);
			out.println("JumpTrue "+ endLabel);
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("And " + second + ", " + first);
			out.println(endLabel + ":");
		}	
		else if (binaryOp.getOperator()==BinaryOps.LOR)
		{
			out.println("Comapre 1, "+ first);
			out.println("JumpTrue "+ endLabel);
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Or " + second + ", " + first);
			out.println(endLabel + ":");
		}
		else if (binaryOp.getOperator()==BinaryOps.EQUAL)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Compare " + second +", "+ first);
			out.println("JumpTrue "+ trueLabel);
			out.println("Move 0, "+ first);
			out.println("Jump "+ endLabel);
			out.println(trueLabel+":");
			out.println("Move 1, "+ first);
			out.println(endLabel+":");
		}
		else if (binaryOp.getOperator()==BinaryOps.NEQUAL)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Compare " + second +", "+ first);
			out.println("JumpFalse "+ trueLabel);
			out.println("Move 0, "+ first);
			out.println("Jump "+ endLabel);
			out.println(trueLabel+":");
			out.println("Move 1, "+ first);
			out.println(endLabel+":");
		}
		else if (binaryOp.getOperator()==BinaryOps.LT)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Compare " + second +", "+ first);
			out.println("JumpL "+ trueLabel);
			out.println("Move 0, "+ first);
			out.println("Jump "+ endLabel);
			out.println(trueLabel+":");
			out.println("Move 1, "+ first);
			out.println(endLabel+":");
		}
		else if (binaryOp.getOperator()==BinaryOps.LTE)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Compare " + second +", "+ first);
			out.println("JumpLE "+ trueLabel);
			out.println("Move 0, "+ first);
			out.println("Jump "+ endLabel);
			out.println(trueLabel+":");
			out.println("Move 1, "+ first);
			out.println(endLabel+":");
		}
		else if (binaryOp.getOperator()==BinaryOps.GT)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Compare " + second +", "+ first);
			out.println("JumpG "+ trueLabel);
			out.println("Move 0, "+ first);
			out.println("Jump "+ endLabel);
			out.println(trueLabel+":");
			out.println("Move 1, "+ first);
			out.println(endLabel+":");
		}
		else if (binaryOp.getOperator()==BinaryOps.GTE)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			out.println("Compare " + second +", "+ first);
			out.println("JumpGE "+ trueLabel);
			out.println("Move 0, "+ first);
			out.println("Jump "+ endLabel);
			out.println(trueLabel+":");
			out.println("Move 1, "+ first);
			out.println(endLabel+":");
		}
		return first;
	}

	public Object visit(MathUnaryOp unaryOp) {
		String result = (String) unaryOp.getOperand().accept(this);
		out.println("Neg "+ result);
		return result;
	}

	public Object visit(LogicalUnaryOp unaryOp) {
		String result = (String) unaryOp.getOperand().accept(this);
		out.println("Not " + result);
		return result;
	}

	public Object visit(Literal literal) {
		if (literal.getType() == LiteralTypes.INTEGER
				|| literal.getType() == LiteralTypes.STRING)
		{
			return literal.getValue().toString();
		}
		else if (literal.getType() == LiteralTypes.FALSE)
			return "0";
		else if (literal.getType() == LiteralTypes.TRUE)
			return "1";
		else if (literal.getType() == LiteralTypes.NULL)
			return "0";
		
		return true;
	}

}
