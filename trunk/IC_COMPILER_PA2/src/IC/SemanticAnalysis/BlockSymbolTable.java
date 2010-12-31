package IC.SemanticAnalysis;

import java.util.List;

public class BlockSymbolTable extends SymbolTable {
	private static int count = 0;
	private boolean breakable = false;

	public BlockSymbolTable(SymbolTable parent) {
		super("block" + getCount(), parent);
	}

	private static int getCount() {
		return count++;
	}

	public void setBreakable(boolean val) {
		breakable = val;
	}

	public boolean isBreakable() {
		return breakable;
	}

	@Override
	public SymbolTableKind getTableKind() {
		return SymbolTableKind.BLOCK;
	}

	public String toString(String location) {
		StringBuffer str = new StringBuffer();
		str.append("Statement Block Symbol Table ( located in " + location
				+ " )");

		for (Symbol s : entries.values()) {
			str.append("\n\tLocal variable: ");
			str.append(s.getType().getName() + " " + s.getId());
		}

		location = "statement block in " + location;
		List<SymbolTable> subs = getChildrenTable();

		if (!subs.isEmpty()) {
			str.append("\nChildren tables: ");

			for (int i = 0; i < subs.size(); i++) {
				if (i > 0)
					str.append(", ");
				BlockSymbolTable bst = (BlockSymbolTable) subs.get(i);
				str.append("statement block in " + location);
			}
		}

		if (subs.size() > 0)
			str.append("\n\n");

		for (int i = 0; i < subs.size(); i++) {
			if (i > 0)
				str.append(", ");
			BlockSymbolTable bst = (BlockSymbolTable) subs.get(i);
			str.append(bst.toString(location));
		}

		return str.toString();
	}

}
