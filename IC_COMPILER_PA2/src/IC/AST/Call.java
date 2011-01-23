package IC.AST;

import java.util.List;

import IC.SemanticAnalysis.ClassTable;
import IC.SemanticAnalysis.SymbolTable;

/**
 * Abstract base class for method call AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Call extends Expression {

	private String name;

	private List<Expression> arguments;

	/**
	 * Constructs a new method call node. Used by subclasses.
	 * 
	 * @param line
	 *            Line number of call.
	 * @param name
	 *            Name of method.
	 * @param arguments
	 *            List of all method arguments.
	 */
	protected Call(int line, String name, List<Expression> arguments) {
		super(line);
		this.name = name;
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public List<Expression> getArguments() {
		return arguments;
	}
	
	public void updateUniqueName(int operNum)
	{
		
		SymbolTable t = this.getEnclosingScope();
		t = t.findSymbolTable(name,operNum);		
		if (name.equals("main"))
			name = "_ic_main";
		else
			name = "_" + t.getId() + "_" + name;
	}
	
	public void updateUniqueStaticName(String className)
	{
		SymbolTable t = ClassTable.getClassTable(className);
		if (name.equals("main"))
			name = "_ic_main";
		else if (className.equals("Library"))
			name = "__" + name;
		else
			name = "_" + t.getId() + "_" + name;
	}
}
