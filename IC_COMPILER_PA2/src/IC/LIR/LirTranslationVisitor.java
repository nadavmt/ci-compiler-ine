package IC.LIR;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.java2d.pipe.LoopPipe;

import IC.BinaryOps;
import IC.DataTypes;
import IC.LiteralTypes;
import IC.AST.*;
import IC.Parser.SemanticError;
import IC.SemanticAnalysis.*;
import IC.SemanticAnalysis.Type;

public class LirTranslationVisitor extends BaseVisitor
{
	Map<String, String> uniqueToReg = null;
	
	Map<String, Type> regToType = null;
	Map<String, Type> uniqueToType = null;
	Map<String, Integer> memberOffsets = null;
	
	PrintWriter out = null;
	
	int regCounter = 1;
	int labelCounter = 1;
	
	String exitLabel = "";
	String contLabel = "";
	
	boolean isVariableAssigment;
	
	public LirTranslationVisitor(PrintWriter output, Map<String, Type> utt, Map<String, Integer> offsets, int stringCount)
	{
		out = output;
		uniqueToType = utt;
		memberOffsets = offsets;
		
		uniqueToReg = new HashMap<String, String>();
		regToType = new HashMap<String, Type>();
		
		for (int i = 0; i < stringCount; i++)
		{
			uniqueToType.put("str" + Integer.toString(i + 1), new StringType());
		}
	}
		
	public Object visit(Program program)
	{
		super.visit(program);
		out.println("_end_of_program:" );
		return true;
		
	}
	public Object visit(Field field)
	{
		//out.println("Move 0, " + field.getName());
		
		return true;
	}
	
	public Object visitMethod(Method method)
	{
		out.println(method.getName()+ ":");
		
		regCounter = 1;
		regToType.clear();
		boolean isMain = method.getName().equals("_ic_main");
		if (!isMain)
		{
			for (Formal f : method.getFormals())
			{
				String formalName = extractOriginalFormalName(f.getName());
				
				String regName = "R" + regCounter++;
				Type regType = null;
				
				try
				{
					regType = f.getEnclosingScope().getSymbol(f.getName()).getType();
				}
				catch (SemanticError e) {}
				
				out.println("Move " + formalName + ", " + regName);
				
				uniqueToReg.put(f.getName(), regName);
				regToType.put(regName, regType);
			}
		}
		
		super.visitMethod(method);
		
		if (!isMain) {
			try {
				MethodType methodType = (MethodType) method.getEnclosingScope()
						.getSymbol(method.getName()).getType();
				if (methodType.getReturnType().getName().equals(VoidType.NAME)) {
					out.println("Return Rdummy");
				}
			} catch (SemanticError e) {
			}
		}
		else
		{
			out.println("Jump _end_of_program");
		}
		return true;
	}
	
	public Object visit(Assignment assignment)
	{
		isVariableAssigment = false;
		String assResult = (String) assignment.getAssignment().accept(this);
		isVariableAssigment = true;
		String varResult = (String) assignment.getVariable().accept(this);
		isVariableAssigment = false;
		
		String command = "Move";
		
		if (varResult.contains(".") || assResult.contains("."))
		{
			int periodIndex = assResult.lastIndexOf(".");
			
			String fieldName = assResult.substring(periodIndex + 1);
			Type fieldType = uniqueToType.get(fieldName);
			
			
			if (!varResult.contains(".") && varResult.startsWith("R"))
			{
				regToType.put(varResult, fieldType);
			}
			
			command = "MoveField";
		}
		else if (varResult.contains("["))
		{
			Type fieldType = null;
			
			if (assResult.startsWith("R"))
			{
				fieldType = regToType.get(assResult);
			}
			else
			{
				fieldType = uniqueToType.get(assResult);
			}
			
			if (!varResult.contains("[") && varResult.startsWith("R"))
			{
				Type newType = fieldType.clone();
				newType.setDimension(newType.getDimension() - 1);
				
				regToType.put(varResult, newType);
			}
			
			command = "MoveArray";
		}
		else
		{
			Type fieldType = null;
			
			if (assResult.startsWith("R"))
			{
				fieldType = regToType.get(assResult);
			}
			else
			{
				fieldType = uniqueToType.get(assResult);
			}
			
			if (varResult.startsWith("R"))
			{
				regToType.put(varResult, fieldType);
			}
		}
		
		out.println(command + " " + assResult + ", " + varResult);
		
		return true;
	}

	public Object visit(Return returnStatement)
	{	
		String retValue = "";
		
		if (returnStatement.hasValue())
		{
			retValue = (String) returnStatement.getValue().accept(this);
		}
		
		out.println("Return " + retValue);
		
		return true;
	}

	public Object visit(If ifStatement)
	{
		String condResult = (String) ifStatement.getCondition().accept(this);
		condResult = newReg(condResult);
		String falseLabel = "_false_label" + labelCounter;
		labelCounter++;
		
		String endLabel = "_end_label" + labelCounter;
		labelCounter++;
		
		out.println("Compare 0, " + condResult);
		out.println("JumpTrue " + falseLabel);
		
		ifStatement.getOperation().accept(this);
		
		out.println("Jump " + endLabel);
		out.println(falseLabel + ":");
		
		if (ifStatement.hasElse())
		{
			ifStatement.getElseOperation().accept(this);
		}
		
		out.println(endLabel + ":");
		
		return true;
	}

	public Object visit(While whileStatement)
	{
		String testLabel = "_test_label" + labelCounter++;
		String endLabel = "_end_label" + labelCounter++;
		
		String prevExit = exitLabel;
		String prevCont = contLabel;
		
		exitLabel = endLabel;
		contLabel = testLabel;
		
		out.println(testLabel + ":");
		
		String condResult = (String) whileStatement.getCondition().accept(this);
		condResult = newReg(condResult);
		out.println("Compare 0, " + condResult);
		out.println("JumpTrue " + endLabel);
		
		whileStatement.getOperation().accept(this);
		
		out.println("Jump " + testLabel);
		out.println(endLabel + ":");
		
		exitLabel = prevExit;
		contLabel = prevCont;
		
		return true;
	}

	public Object visit(Break breakStatement)
	{
		out.println("Jump " + exitLabel);
		
		return true;
	}

	public Object visit(Continue continueStatement)
	{
		out.println("Jump " + contLabel);
		
		return true;
	}
	
	public Object visit(StatementsBlock statementsBlock)
	{
		for (Statement statement : statementsBlock.getStatements())
			statement.accept(this);
		
		return true;
	}
	
	public Object visit(LocalVariable localVariable)
	{
		String initValue = "0";
		
		if (localVariable.hasInitValue())
		{
			initValue = (String) localVariable.getInitValue().accept(this);
		}
		
		String varName = localVariable.getName();
		String varReg = uniqueToReg.get(varName);
		
		if (varReg == null)
		{
			varReg = "R" + regCounter;
			regCounter++;
			
			uniqueToReg.put(varName, varReg);
			
			Type regType = null;
			
			try {
				regType = localVariable.getEnclosingScope().getSymbol(localVariable.getName()).getType();
			} catch (SemanticError e) {
				
			}
			regToType.put(varReg, regType);
		}
		
		out.println("Move " + initValue + ", " + varReg);
		
		return true;
	}
	
	public Object visit(VariableLocation location)
	{
		String name = location.getName();
		if (location.isExternal()&& (!(location.getLocation() instanceof This)))
		{
			Type type = uniqueToType.get(name);
			
			name = (String) location.getLocation().accept(this) + "." + memberOffsets.get(name).toString();
			
			if (isVariableAssigment)
				return name;
			
			String regName = "R" + regCounter++;
			
			regToType.put(regName, type);
			out.println("MoveField " + name + ", " + regName);
			return regName;
		}
		else
		{
			String regName = "R" + regCounter++;
			
			Type type = null;
			//String command = null;
			
			if (uniqueToReg.containsKey(name))
			{
				name = uniqueToReg.get(name);
				return name;
			}
			else
			{
				if (uniqueToType.containsKey(name))
				{
					type = uniqueToType.get(name);
				}
				else
				{
					try {
						type = location.getEnclosingScope().getSymbol(location.getName()).getType();
					} catch (SemanticError e) {
						
					}
				}
				out.println("Move this, " + regName);
				regToType.put(regName, type);
				regName += "." +  memberOffsets.get(name).toString();
				if (isVariableAssigment)
					{
					return regName;
					}
				else
				{
					String newRegName = "R" + regCounter++;
					out.println("MoveField " + regName + ", " + newRegName);
					regToType.put(newRegName, type);
					return newRegName;
				}
			
			}
			
		}
	}
	
	public Object visit(ArrayLocation location)
	{
		String arrName = (String) location.getArray().accept(this);
		String indexName = (String) location.getIndex().accept(this);
		
		
		String name = "R" + regCounter++;
		
		Type newType = null;
		
		if (arrName.startsWith("R"))
		{
			newType = regToType.get(arrName);
			if (newType == null)
				newType = regToType.get(arrName.substring(0, arrName.indexOf('[')));
		}
		else
		{
			newType = uniqueToType.get(arrName);
		}
		
		newType = newType.clone();
		newType.setDimension(newType.getDimension() - 1);
		
		regToType.put(name, newType);
		
		if (isVariableAssigment)
		{
			
			String command = arrName.contains("[") ? "MoveArray" : "Move";
			String res = "R" + regCounter++;
			regToType.put(res, newType);
			out.println(command + " " + arrName + ", " + res);
			return res + "[" + indexName + "]";
		}
		else
		{
			out.println("MoveArray " + arrName+ "[" + indexName + "], " + name);
			return name;
		}
	}

	public Object visit(StaticCall call)
	{
		if (call.getClassName().equals("Library"))
			return visitLibraryCall(call);
		
		List<String> variables = new ArrayList<String>();
		
		for (Expression argument : call.getArguments())
			variables.add((String)argument.accept(this));
		
		Method m = findMethodAST(call.getClassName(), call.getName());
		
		out.print("StaticCall " + call.getName());
		
		printMethodCall(variables, m.getFormals());
		
		SymbolTable st = ClassTable.getClassTable(call.getClassName());
		Symbol s = null;
		
		try
		{
			s = st.getSymbol(call.getName());
		}
		catch (SemanticError e) {}
		
		String result = "Rdummy";
		
		if (!(((MethodType)s.getType()).getReturnType() instanceof VoidType))
		{
			result = "R" + regCounter++;
			
			regToType.put(result, ((MethodType)s.getType()).getReturnType());
		}
		
		out.println(", " + result);
		
		return result; 
	}
	
	public Object visitLibraryCall(StaticCall call)
	{
		List<String> variables = new ArrayList<String>();
		
		for (Expression argument : call.getArguments())
			variables.add((String)argument.accept(this));	
		
		out.print("Library "+ call.getName());
		out.print("(");
		
		for (int i = 0; i < variables.size(); i++)
		{
			String v = variables.get(i);
			
			if (i > 0)
			{
				out.print(", ");
			}
			
			out.print(v);
		}
		
		out.print(")");
		
		SymbolTable st = ClassTable.getClassTable(call.getClassName());
		Symbol s = null;
		
		try
		{
			s = st.getSymbol(call.getName());
		}
		catch (SemanticError e) {}
		
		String result = "Rdummy";
		
		if (!(((MethodType)s.getType()).getReturnType() instanceof VoidType))
		{
			result = "R" + regCounter++;
			regToType.put(result, ((MethodType)s.getType()).getReturnType());
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
		
		for (int i = 0; i < formals.size(); i++)
		{
			String f = extractOriginalFormalName(formals.get(i).getName());
			String v = variables.get(i);
			
			if (i > 0)
			{
				out.print(", ");
			}
			
			out.print(f + "=" + v);
		}
		
		out.print(")");
	}
	
	public Object visit(VirtualCall call)
	{
		List<String> arguments = new ArrayList<String>();
		String funcName = "";
		SymbolTable st;
		
		if (call.isExternal())
		{
			String locationReg = (String)call.getLocation().accept(this);
			funcName += locationReg + ".";
			Type t = regToType.get(locationReg);
			st = ClassTable.getClassTable(t.getName());
			//st = call.getLocation().getEnclosingScope();
		}
		else
		{
			out.println("Move this, R" + regCounter);
			funcName += "R" + regCounter++ + ".";
			st = call.getEnclosingScope();
		}
		
		funcName += memberOffsets.get(call.getName()).toString();
		
		for (Expression argument : call.getArguments())
			arguments.add((String)argument.accept(this));
		
		st = st.findSymbolTable(call.getName(), Integer.MAX_VALUE);
		
		Method m = findMethodAST(st.getId(), call.getName());
		
		out.print("VirtualCall " + funcName);
		printMethodCall(arguments, m.getFormals());
		
		Symbol s = null;
		
		try 
		{
			s = st.getSymbol(call.getName());
		}
		catch (SemanticError e) {}
		
		String result = "Rdummy";
		
		if (!(((MethodType)s.getType()).getReturnType() instanceof VoidType))
		{
			result = "R" + regCounter++;
			
			regToType.put(result, ((MethodType)s.getType()).getReturnType());
		}
		
		out.println(", " + result);
		
		return result;
	}

	public Object visit(This thisExpression)
	{
		return true;
	}

	public Object visit(NewClass newClass)
	{
		String name = "R" + regCounter++;
		ICClass icClass = ClassTable.getClassAST(newClass.getName()); 
		ClassSymbolTable cs = ClassTable.getClassTable(newClass.getName());
		int size = 1 + cs.getFieldCount();
		size *= 4;
		
		out.println("Library __allocateObject(" + size + "), " + name);
		out.println("MoveField _DV_" + newClass.getName() + ", "+ name + ".0");
		
		for (IC.AST.Field f : icClass.getFields()) {
			out.println("MoveField 0, " + name + "." + memberOffsets.get(f.getName()));
		}
		
		return name; 
	}

	public Object visit(NewArray newArray)
	{
		String sizeReg = (String) newArray.getSize().accept(this);
		String newReg = "R" + regCounter++;
		String sizeReg2 = "R" + regCounter++;
		String resReg = "R" + regCounter++;
		out.println("Move " + sizeReg + ", " + newReg);
		out.println("Move " + sizeReg + ", " + sizeReg2);
		out.println("Mul 4, " + newReg);
		out.println("Library __allocateArray(" + newReg + "), " + resReg);
		
		String counterReg = "R" + regCounter++;
		out.println("Move 0, " + counterReg);
		String loopLabel = "_loop_" + labelCounter++;
		out.println(loopLabel + ":");
		out.println("MoveArray 0, " + resReg + "[" + counterReg + "]");
		out.println("Add 1, " + counterReg);
		out.println("Compare " + counterReg + ", " + sizeReg2);
		out.println("JumpL " + loopLabel);
				
		int dim = newArray.getType().getDimension();
		
		if (newArray.getType() instanceof PrimitiveType)
		{
			PrimitiveType pt = (PrimitiveType)newArray.getType();
			
			if (pt.getType() == DataTypes.BOOLEAN)
				regToType.put(resReg, new BoolType(dim));
			else if (pt.getType() == DataTypes.INT)
				regToType.put(resReg, new IntType(dim));
			else
				regToType.put(resReg, new StringType(dim));
		}
		else
		{
			UserType ut = (UserType)newArray.getType();
			try {
				Type regType = ut.getEnclosingScope().getSymbol(ut.getName()).getType();
				regToType.put(resReg, regType);
			} catch (SemanticError e) {
			}
			
		}
		
		return resReg;
	}

	public Object visit(Length length)
	{
		String name = (String) length.getArray().accept(this);
		String result = "R" + regCounter++;
		
		out.println("ArrayLength " + name + ", "+ result);
		
		return result;
	}

	public Object visit(MathBinaryOp binaryOp)
	{
		String first = (String) binaryOp.getFirstOperand().accept(this);
		String second = (String) binaryOp.getSecondOperand().accept(this);
		
		String resRegister = "R" + regCounter++;
		
		if (((regToType.get(first) instanceof StringType) && (regToType.get(first).getDimension() == 0)) &&
			((regToType.get(second) instanceof StringType) && (regToType.get(second).getDimension() == 0)))
		{
			
			regToType.put(resRegister, new StringType());
			out.println("Library __stringCat(" + first + ", " + second + "), " + resRegister);
			return resRegister;
		}
		else
		{
			String operation = "";
			
			switch (binaryOp.getOperator())
			{
				case DIVIDE: operation = "Div"; break;
				case PLUS: operation = "Add"; break;
				case MULTIPLY: operation = "Mul"; break;
				case MINUS: operation = "Sub"; break;
				case MOD: operation = "Mod"; break;
			}
			
			
			out.println("Move " + first + ", " + resRegister);
			regToType.put(resRegister, new IntType());
			out.println(operation + " " + second + ", " + resRegister);
			return resRegister;
		}
		
	}

	private String newReg(String old)
	{
		String newReg = "R" + regCounter++;
		out.println("Move " + old + ", " + newReg);
		return newReg;
	}
	
	public Object visit(LogicalBinaryOp binaryOp)
	{
		String first = (String) binaryOp.getFirstOperand().accept(this);
		
		String endLabel = "_end_label" + labelCounter++;
		String trueLabel = "_true_label" + labelCounter++;
		first = newReg(first);
		if (binaryOp.getOperator()==BinaryOps.LAND)
		{
			out.println("Compare 0, "+ first);
			out.println("JumpTrue "+ endLabel);
			String second = (String) binaryOp.getSecondOperand().accept(this);
			second = newReg(second); 
			out.println("And " + second + ", " + first);
			out.println(endLabel + ":");
		}	
		else if (binaryOp.getOperator()==BinaryOps.LOR)
		{
			out.println("Compare 1, "+ first);
			out.println("JumpTrue "+ endLabel);
			String second = (String) binaryOp.getSecondOperand().accept(this);
			second = newReg(second);
			out.println("Or " + second + ", " + first);
			out.println(endLabel + ":");
		}
		else if (binaryOp.getOperator()==BinaryOps.EQUAL)
		{
			String second = (String) binaryOp.getSecondOperand().accept(this);
			second = newReg(second);
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
			second = newReg(second);
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
			second = newReg(second);
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
			second = newReg(second);
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
			second = newReg(second);
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
			second = newReg(second);
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

	public Object visit(MathUnaryOp unaryOp)
	{
		String result = (String) unaryOp.getOperand().accept(this);
		result = newReg(result);
		out.println("Neg "+ result);
		
		return result;
	}
	
	public Object visit(LogicalUnaryOp unaryOp)
	{
		String result = (String) unaryOp.getOperand().accept(this);
		result = newReg(result);
		String falseLabel = "_false_label_" + labelCounter++;
		String endLabel = "_end_Label_" + labelCounter++;
		out.println("Compare 0, " + result);
		out.println("JumpTrue " + falseLabel);
		out.println("Move 0, " + result);
		out.println("Jump " + endLabel);
		out.println(falseLabel+ ":");
		out.println("Move 1, " + result);
		out.println(endLabel+ ":");
		
		//	out.println("Not " + result);
		
		return result;
	}
	
	public Object visit(Literal literal)
	{
		if (literal.getType() == LiteralTypes.INTEGER)
		{
			return literal.getValue().toString();
		}
		else if (literal.getType() == LiteralTypes.STRING)
		{
			regToType.put(literal.getValue().toString(), new StringType());
			return literal.getValue().toString();
		}
		else if (literal.getType() == LiteralTypes.FALSE)
		{
			return "0";
		}
		else if (literal.getType() == LiteralTypes.TRUE)
		{
			return "1";
		}
		else if (literal.getType() == LiteralTypes.NULL)
		{
			return "0";
		}
		
		return true;
	}
	
	private String extractOriginalFormalName(String name)
	{
		if (name.startsWith("__"))
		{
			name = name.replaceFirst("__+", "_");
		}
		
		int us1 = name.indexOf("_", 1);
		int us2 = name.indexOf("_", us1 + 1);
		
		name = name.substring(us2 + 1);
		
		return name;
	}
}
