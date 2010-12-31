package IC.SemanticAnalysis;

public class Symbol {
	private String id;
	private Kind kind;
	private Type type;
	  
	private boolean isFormal = false;
	
	public Symbol(String id, Kind kind, Type type) {
		this.id = id;
		this.kind = kind;
		this.type = type;
	}
	
	public void setIsFormal(boolean b)
	{
		isFormal = b;
	}
	
	public boolean getIsFormal()
	{
		return isFormal;
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

