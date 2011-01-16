package IC.LIR;

import java.io.PrintWriter;

import IC.AST.*;
import IC.SemanticAnalysis.*;

public class DispatchTablesCreator {

	private PrintWriter out;

	public DispatchTablesCreator(PrintWriter output) {
		out = output;
	}

	public void create(Program program) {
		for (ICClass icClass : program.getClasses()) {	
			DispatchTable t = createClassDispatchTable(icClass);
			ClassTable.getClassTable(icClass.getName()).setDispatchTable(t);
			out.println(t.toString());
		}
	}

	public DispatchTable createClassDispatchTable(ICClass icClass) {
		ClassSymbolTable t = ClassTable.getClassTable(icClass.getName());
		DispatchTable dTable= null;
		SymbolTable st;
		try {

			if (icClass.hasSuperClass())
			{
				dTable = createClassDispatchTable(ClassTable
						.getClassAST(icClass.getSuperClassName()));
				dTable.setName(icClass.getName());
			}
			else
				dTable = new DispatchTable(icClass.getName());

			for (Field f : icClass.getFields()) {
				st = t.findSymbolTable(f.getName(), Integer.MAX_VALUE);
				dTable.addField(st.getSymbol(f.getName()).getCoreId(),f.getName());
			}
			for (Method m : icClass.getMethods()) {
				st = t.findSymbolTable(m.getName(), Integer.MAX_VALUE);
				dTable.addMethod(st.getSymbol(m.getName()).getCoreId(), m.getName());
			}
		} catch (Exception e) {

		}
		return dTable;
	}

	
}
