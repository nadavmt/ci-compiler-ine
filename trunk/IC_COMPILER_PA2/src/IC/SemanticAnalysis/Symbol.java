package IC.SemanticAnalysis;

public class Symbol {
	private String id;
	private Kind kind;
	private Type type;
	
	private int opCount;
	
	private boolean isFormal = false;
	
	public Symbol(String id, Kind kind, Type type) {
		this.id = id;
		this.kind = kind;
		this.type = type;
		this.opCount = -1;
	}
	
	
	public void setOpCount(int count)
	{
		opCount = count;
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
	
	public int GetOpCount()
	{
		return opCount;
	}
}
