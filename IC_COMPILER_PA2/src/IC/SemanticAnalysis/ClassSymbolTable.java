package IC.SemanticAnalysis;

import java.util.HashMap;

public class ClassSymbolTable extends SymbolTable {

	private HashMap<String, Boolean> isStatic;
	private DispatchTable dispatchTable = new DispatchTable();
	
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
	
	public void addMethodOffset(String name, int index)
	{
		dispatchTable.methodOffset.put(name, index);
	}
	
	public int getMethodIndex (String name)
	{
		return dispatchTable.methodOffset.get(name);
	}
	
	public void addFieldOffset(String name, int index)
	{
		dispatchTable.fieldOffset.put(name, index);
	}
	
	public int getFieldIndex (String name)
	{
		return dispatchTable.fieldOffset.get(name);
	}
	
}
