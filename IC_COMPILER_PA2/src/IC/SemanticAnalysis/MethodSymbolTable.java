package IC.SemanticAnalysis;

public class MethodSymbolTable extends SymbolTable {
	public MethodSymbolTable(String id, SymbolTable parent) {
		super(id, parent);
	}

	@Override
	public SymbolTableKind getTableKind() {
		return SymbolTableKind.METHOD;
	}
}
