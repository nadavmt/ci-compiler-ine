package IC.SemanticAnalysis;

public class StringType extends Type
{
	public static final String NAME = "string";

	public StringType()
	{
		this(0);
	}
	
	public StringType(int dimension)
	{
		super(NAME, dimension);
	}

	@Override
	public Type clone() 
	{		
		return new StringType(dimension);
	}
}
