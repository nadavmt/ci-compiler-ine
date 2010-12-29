package IC.SemanticAnalysis;

public class GlobalSymbolTable extends SymbolTable{
	
	public GlobalSymbolTable(String id) {
		super(id, null);
	}

	@Override
	public SymbolTableKind getTableKind() {
		return SymbolTableKind.GLOBAL;
	}
}
