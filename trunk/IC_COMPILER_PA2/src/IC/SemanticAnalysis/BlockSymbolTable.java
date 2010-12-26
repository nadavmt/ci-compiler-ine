package IC.SemanticAnalysis;

public class BlockSymbolTable extends SymbolTable
{
	private static int count = 0;
	private boolean breakable = false;
	
	public BlockSymbolTable(SymbolTable parent) {
		super("block" + getCount(), parent);
	}
	
	private static int getCount()
	{
		return count++;
	}
	
	public void setBreakable(boolean val)
	{
		breakable = val;
	}
	
	public boolean isBreakable()
	{
		return breakable;
	}
}
