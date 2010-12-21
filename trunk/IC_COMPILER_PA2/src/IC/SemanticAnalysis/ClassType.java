package IC.SemanticAnalysis;

import IC.AST.ICClass;

public class ClassType extends Type{
	private ICClass classAST;

	public ClassType(ICClass classAST) {
		super();
		this.classAST = classAST;
	}

	public ICClass getClassAST() {
		return classAST;
	}
	
}
