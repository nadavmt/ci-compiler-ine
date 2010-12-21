package IC.SemanticAnalysis;

public class Symbol {
	private String id;
	private Kind kind;
	private Type type;
	  
	  
	public Symbol(String id, Kind kind, Type type) {
		this.id = id;
		this.kind = kind;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public Type getType() {
		return type;
	}  
}

