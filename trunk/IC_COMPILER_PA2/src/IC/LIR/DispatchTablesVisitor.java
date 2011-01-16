package IC.LIR;

import java.io.PrintWriter;

import IC.AST.*;
import IC.SemanticAnalysis.ClassSymbolTable;
import IC.SemanticAnalysis.ClassTable;
import IC.SemanticAnalysis.DispatchTable;

public class DispatchTablesVisitor {

	private PrintWriter out;

	public DispatchTablesVisitor(PrintWriter output) {
		out = output;
	}

	public void create(Program program)
	{
		for (ICClass icClass : program.getClasses())
		{
			DispatchTable t = createClassDispatchTable(icClass);
		}
	}
	
	public DispatchTable createClassDispatchTable(ICClass icClass){
		ClassSymbolTable table = ClassTable.getClassTable(icClass.getName());
		
		DispatchTable dTable;
		
		if (icClass.hasSuperClass())
			dTable = createClassDispatchTable(ClassTable.getClassAST(icClass.getSuperClassName()));
		else
			dTable = new DispatchTable();
		for (Field f : icClass.getFields())
		{
			
		}
		
		return dTable;
	}
	
	public Object visit(ICClass icClass) {
		out.print("_DV_" + icClass.getName() + ": [");
		boolean firstFlag = true;
		for (Method method : icClass.getMethods()) {
			if (!firstFlag)
				out.print(",");
			else
				firstFlag = false;
			out.print(method.getName());
		}
		out.println("]");
		return true;
	}
}
