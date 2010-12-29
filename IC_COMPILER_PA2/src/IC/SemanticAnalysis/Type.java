package IC.SemanticAnalysis;

public abstract class Type 
{
	protected String name;
	protected int dimension;
	
	public Type(String name, int dimension)
	{
		this.name = name;
		this.dimension = dimension;
	}
	
	public int getDimension()
	{
		return dimension;
	}
	
	public String getName()
	{
		String result = name;
		for (int i = 0; i < dimension; i++)
			result += "[]";
		
		return result;
	}
	
	public boolean isUserType()
	{
		return false;
	}
	
	public abstract Type clone();

	public void setDimension(int i)
	{
		this.dimension = i;
	}
}
