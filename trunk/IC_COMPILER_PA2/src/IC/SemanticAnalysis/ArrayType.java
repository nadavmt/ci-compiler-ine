package IC.SemanticAnalysis;

public class ArrayType extends Type{
	private IC.AST.Type elemType;
	
	
	public ArrayType(IC.AST.Type type)
	{
		this.elemType = type;
	}
	
	public IC.AST.Type getElementsType()
	{
		return elemType;
	}
}
