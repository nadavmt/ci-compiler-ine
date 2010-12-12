package IC.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * Class declaration AST node.
 * 
 * @author Tovi Almozlino
 */
public class FieldOrMethod extends ASTNode {

	private List<Field> fields;

	private List<Method> methods;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new class node.
	 * 
	 * @param fields
	 *            List of all fields in the class.
	 * @param methods
	 *            List of all methods in the class.
	 */
	public FieldOrMethod(int line) {
		super(line);
		this.fields = new ArrayList<Field>();
		this.methods = new ArrayList<Method>();
	}

	public List<Field> getFields() {
		return fields;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public void addFields(List<Field> f)
	{
		fields.addAll(f);
	}
	
	public void addMethod(Method m)
	{
		methods.add(m);
	}
}
