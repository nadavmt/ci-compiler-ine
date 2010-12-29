package IC.SemanticAnalysis;

public class IntType extends Type
{
	public static final String NAME = "int";

	public IntType()
	{
		this(0);
	}
	
	public IntType(int dimension)
	{
		super(NAME, dimension);
	}

	@Override
	public Type clone() {		
		return new IntType(dimension);
	}
}
