package IC.SemanticAnalysis;

import IC.AST.ICClass;

public class ClassType extends Type
{
	private ICClass classAST;

	public ClassType(ICClass classAST)
	{
		this(classAST, 0);
	}
	
	public ClassType(ICClass classAST, int dimension)
	{
		super(classAST.getName(), dimension);	
		this.classAST = classAST;
	}

	public ICClass getClassAST()
	{
		return classAST;
	}
	
	@Override
	public boolean isUserType()
	{
		return true;
	}

	@Override
	public Type clone() 
	{
		return new ClassType(classAST);
	}
}
