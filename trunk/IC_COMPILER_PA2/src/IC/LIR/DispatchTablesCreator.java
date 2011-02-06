package IC.LIR;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import IC.AST.*;
import IC.SemanticAnalysis.*;

public class DispatchTablesCreator
{
	private Map<String, Integer> offsets = null;
	
	private PrintWriter out = null;

	public DispatchTablesCreator(PrintWriter output)
	{
		out = output;
		
		offsets = new HashMap<String, Integer>();
	}

	public Map<String, Integer> getOffsets()
	{
		return offsets;
	}
	
	public void create(Program program) {
		for (ICClass icClass : program.getClasses()) {
			if (!icClass.getName().equals("Library"))
			{
				DispatchTable t = createClassDispatchTable(icClass);
				ClassTable.getClassTable(icClass.getName()).setDispatchTable(t);
				
				out.println(t.toString());
				
				for (Pair p : t.methodOffset.values())
				{
					offsets.put(p.uniqueName, p.counter);
				}
				
				for (Pair p : t.fieldOffset.values())
				{
					offsets.put(p.uniqueName, p.counter);
				}
			}
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
				String name = f.getName();
				dTable.addField(st.getSymbol(name).getCoreId(),name);
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
