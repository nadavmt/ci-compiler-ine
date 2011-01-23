package IC.SemanticAnalysis;

public class VoidType extends Type
{
	public static final String NAME = "void";

	public VoidType()
	{
		super(NAME,0);
	}
	/*
	public VoidType(int dimension)
	{
		super(NAME, dimension);
	}
*/
	@Override
	public Type clone() {
		return new VoidType();
	}
	
}
