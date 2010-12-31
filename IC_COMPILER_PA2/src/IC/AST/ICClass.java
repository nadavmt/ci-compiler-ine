package IC.AST;

import java.util.List;
import IC.SemanticAnalysis.ClassSymbolTable;
import IC.SemanticAnalysis.ClassTable;
import IC.SemanticAnalysis.SymbolTable;

/**
 * Class declaration AST node.
 * 
 * @author Tovi Almozlino
 */
public class ICClass extends ASTNode {

	private String name;

	private String superClassName = null;

	private List<Field> fields;

	private List<Method> methods;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new class node.
	 * 
	 * @param line
	 *            Line number of class declaration.
	 * @param name
	 *            Class identifier name.
	 * @param fields
	 *            List of all fields in the class.
	 * @param methods
	 *            List of all methods in the class.
	 */
	public ICClass(int line, String name, List<Field> fields,
			List<Method> methods) {
		super(line);
		this.name = name;
		this.fields = fields;
		this.methods = methods;
	}

	/**
	 * Constructs a new class node, with a superclass.
	 * 
	 * @param line
	 *            Line number of class declaration.
	 * @param name
	 *            Class identifier name.
	 * @param superClassName
	 *            Superclass identifier name.
	 * @param fields
	 *            List of all fields in the class.
	 * @param methods
	 *            List of all methods in the class.
	 */
	public ICClass(int line, String name, String superClassName,
			List<Field> fields, List<Method> methods) {
		this(line, name, fields, methods);
		this.superClassName = superClassName;
	}

	public String getName() {
		return name;
	}

	public boolean hasSuperClass() {
		return (superClassName != null);
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public List<Field> getFields() {
		return fields;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
		str.append("Class Symbol Table: ");
		str.append(name + "\n");
		
		for (Field f : fields)
		{
			str.append("\tField: " + f + "\n");
		}
		
		for (Method m : methods)
		{
			if (m.isStatic())
				str.append("\tStatic method: " + m +"\n");
		}
		
		for (Method m : methods)
		{
			if (!m.isStatic())
				str.append("\tmethod: " + m +"\n");
		}
		
		ClassSymbolTable t = ClassTable.getClassTable(getName());
		List<ClassSymbolTable> children = ClassTable.getChildren(t);
		
		if (methods.size() + children.size() > 0)
			str.append("Children tables: ");
		
		boolean firstAdded = false;
		if (!methods.isEmpty())
		{
			for (Method m : methods)
			{
				if (m.isStatic())
				{
					if (firstAdded)
						str.append(", ");
					else
						firstAdded = true;
					str.append(m.getName());
				}
			}
			
			for (Method m : methods)
			{
				if (!m.isStatic())
				{
					if (firstAdded)
						str.append(", ");
					else
						firstAdded = true;
					str.append(m.getName());
				}
			}
		}
		
		for (ClassSymbolTable c : children)
		{
			if (firstAdded)
				str.append(", ");
			else
				firstAdded = true;
			str.append(c.getId());
		}
		
		if (methods.size() + children.size() > 0)
			str.append("\n\n");
		
		for (SymbolTable methodTable : t.getChildrenTable())
		{
			str.append(methodTable + "\n");
		}
		
		//str.append("\n");
		return str.toString();
	}
}
