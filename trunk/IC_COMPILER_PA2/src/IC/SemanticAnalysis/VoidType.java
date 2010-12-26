package IC.SemanticAnalysis;

public class VoidType extends Type
{
	public static final String NAME = "void";

	public VoidType()
	{
		this(0);
	}
	
	public VoidType(int dimension)
	{
		super(NAME, dimension);
	}
}
