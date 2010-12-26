package IC.SemanticAnalysis;

public class BoolType extends Type
{
	public static final String NAME = "boolean";

	public BoolType()
	{
		this(0);
	}
	
	public BoolType(int dimension)
	{
		super(NAME, dimension);
	}
}
