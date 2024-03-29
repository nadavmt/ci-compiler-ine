package IC.AST;

import java.util.List;

/**
 * Abstract base class for method AST nodes.
 * 
 * @author Tovi Almozlino
 */
public abstract class Method extends ASTNode {

	protected Type type;

	protected String name;

	protected List<Formal> formals;

	protected List<Statement> statements;
	

	/**
	 * Constructs a new method node. Used by subclasses.
	 * 
	 * @param type
	 *            Data type returned by method.
	 * @param name
	 *            Name of method.
	 * @param formals
	 *            List of method parameters.
	 * @param statements
	 *            List of method's statements.
	 */
	protected Method(Type type, String name, List<Formal> formals,
			List<Statement> statements) {
		super(type.getLine());
		this.type = type;
		this.name = name;
		this.formals = formals;
		this.statements = statements;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public List<Formal> getFormals() {
		return formals;
	}

	public List<Statement> getStatements() {
		return statements;
	}
	
	public abstract boolean isStatic();
	
	@Override
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append(name + " {");
		for (int i = 0; i < formals.size(); i++)
		{
			if (i > 0)
				str.append(", ");
			Type t = formals.get(i).getType();
			str.append(t.toString());
		}
		str.append(" -> ");
		str.append(type + "}");
		
		return str.toString();
	}
	
	public void updateUniqueName()
	{
		this.getEnclosingScope().changeUniqueName(name);
		if (name.equals("main"))
			name = "_ic_main";
		else
			name = "_" + this.getEnclosingScope().getId() + "_" + name;
	}

	public void updateLibraryUniqueName()
	{
		this.getEnclosingScope().changeUniqueLibraryName(name);
		name = "__" + name;
	}
}