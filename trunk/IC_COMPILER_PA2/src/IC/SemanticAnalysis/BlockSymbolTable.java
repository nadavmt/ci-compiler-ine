package IC.SemanticAnalysis;

public class BlockSymbolTable extends SymbolTable {
	
	private static int count = 0;
	
	public BlockSymbolTable(SymbolTable parent) {
		super("block" + getCount(), parent);
	}
	
	private static int getCount()
	{
		return count++;
	}
}
