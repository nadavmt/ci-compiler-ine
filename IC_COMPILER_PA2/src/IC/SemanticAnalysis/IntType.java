package IC.SemanticAnalysis;

public class IntType extends Type
{
	public IntType()
	{
		this(0);
	}
	
	public IntType(int dimension)
	{
		super("int", dimension);
	}
}
