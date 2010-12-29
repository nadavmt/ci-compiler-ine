package IC.SemanticAnalysis;

public class NullType extends Type
{
	public static final String NAME = "null";

	public NullType()
	{
		super(NAME, 0);
	}

	@Override
	public Type clone() 
	{	
		return new NullType();
	}
}
