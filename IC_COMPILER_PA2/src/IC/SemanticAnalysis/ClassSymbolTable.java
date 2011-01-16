package IC.SemanticAnalysis;

import java.util.HashMap;

public class ClassSymbolTable extends SymbolTable {

	private HashMap<String, Boolean> isStatic;
	private DispatchTable dispatchTable;
	
	public ClassSymbolTable(String id, SymbolTable parent) {
		super(id, parent);
		isStatic = new HashMap<String,Boolean>();
	}

	public void setStaticEntry(String id, boolean isStatic) 
	{
		this.isStatic.put(id, isStatic);	
	}
	
	public boolean isStaticMethod(String id)
	{
		return isStatic.get(id);
	}

	@Override
	public SymbolTableKind getTableKind() {
		return SymbolTableKind.CLASS;
	}
	
	public void setParentTable(SymbolTable parent)
	{
		this.parentSymbolTable = parent;
	}
	
		
	public int getMethodIndex (String name)
	{
		return dispatchTable.methodOffset.get(name).counter;
	}
	
	
	public int getFieldIndex (String name)
	{
		return dispatchTable.fieldOffset.get(name).counter;
	}

	public void setDispatchTable(DispatchTable table) {
		this.dispatchTable = table;
		
	}
	
}
