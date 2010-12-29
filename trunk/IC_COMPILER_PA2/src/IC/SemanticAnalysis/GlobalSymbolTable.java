package IC.SemanticAnalysis;

import java.util.List;
import java.util.Map;

import IC.AST.ICClass;

public class GlobalSymbolTable extends SymbolTable {

	public GlobalSymbolTable(String id) {
		super(id, null);
	}

	@Override
	public SymbolTableKind getTableKind() {
		return SymbolTableKind.GLOBAL;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("Global Symbol Table: "
				+ this.getId());

		Map<String, ClassSymbolTable> classTables = ClassTable.getClassTables();
		
		for(ClassSymbolTable c : classTables.values()){
			str.append("\n\tClass: " + c.getId());
		}
		
		List<ClassSymbolTable> children = ClassTable.getChildren(this);
		if (!children.isEmpty())
		{
			str.append("\nChildren tables: ");
			for (int i = 0; i < children.size(); i++)
			{
				if (i > 0)
					str.append(", ");
				str.append(children.get(i).getId());
			}
		}

		str.append("\n");

		Map<String, ICClass> classASTs = ClassTable.getClassASTs();
		for (ICClass c : classASTs.values()) {
			str.append(c.toString());
		}

		return str.toString();
	}

}
