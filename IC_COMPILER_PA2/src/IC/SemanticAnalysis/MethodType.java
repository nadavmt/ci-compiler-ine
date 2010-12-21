package IC.SemanticAnalysis;

public class MethodType extends Type{
	private Type[] paramTypes;
	private Type returnType;
	
	public MethodType(Type[] paramTypes, Type returnType) {
		super();
		this.paramTypes = paramTypes;
		this.returnType = returnType;
	}
	
	public Type[] getParamTypes() {
		return paramTypes;
	}

	public Type getReturnType() {
		return returnType;
	}
}
