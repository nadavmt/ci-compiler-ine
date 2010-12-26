package IC.SemanticAnalysis;

public class MethodType extends Type
{
	private Type[] paramTypes;
	private Type returnType;
	
	public MethodType(Type[] paramTypes, Type returnType)
	{
		super("", 0);
		
		this.paramTypes = paramTypes;
		this.returnType = returnType;
		
		for (int i = 0; i < paramTypes.length; i++)
		{
			name += paramTypes[i].getName();
			if (i != paramTypes.length - 1)
				name += ", ";
		}
		name += " -> " + returnType.getName();
	}
	
	public Type[] getParamTypes()
	{
		return paramTypes;
	}

	public Type getReturnType()
	{
		return returnType;
	}
}
