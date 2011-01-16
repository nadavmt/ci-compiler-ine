package IC.SemanticAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import IC.AST.*;
import IC.Parser.SemanticError;

public abstract class SymbolTable {
	/** map from String to Symbol **/
	protected Map<String, Symbol> entries;
	protected String id;
	protected SymbolTable parentSymbolTable;
	protected List<SymbolTable> childrenTable = new ArrayList<SymbolTable>();

	public SymbolTable(String id, SymbolTable parent) {
		this.id = id;
		entries = new HashMap<String, Symbol>();
		parentSymbolTable = parent;
	}

	public void addEntry(Symbol sym) throws SemanticError {
		if (entries.containsKey(sym.getId())
				&& (entries.get(sym.getId()).getType() != null))
			throw new SemanticError("Identifier " + sym.getId()
					+ " already exists");

		entries.put(sym.getId(), sym);
	}

	public List<SymbolTable> getChildrenTable() {
		return childrenTable;
	}

	public SymbolTable getParent() {
		return parentSymbolTable;
	}

	public String getId() {
		return id;
	}

	public Symbol getSymbol(String id) throws SemanticError {
		Symbol s = entries.get(id);
		if (s == null)
			throw new SemanticError("Identifier " + id + " does not exist");
		return s;
		// return entries.get(id);
	}

	public boolean symbolExists(String id) {
		return (entries.get(id) != null);
	}

	public abstract SymbolTableKind getTableKind();

	public void changeUniqueName(String name) {
		Symbol s = entries.remove(name);
		String newID;
		if (name.equals("main"))
			newID = "_ic_main"; 
		else
			newID = "_" + id + "_" + name;
		s.setId(newID);
		entries.put(newID, s);
	}

	public void changeUniqueName(Method m) {
		Symbol s = entries.remove(m.getName());
		String newID = "_" + id + "_" + m.getName();
		s.setId(newID);
		entries.put(newID, s);
	}

	public void updateTableName() {
		id = "_" + this.getParent().getId() + "_" + id;
	}

	// finds the right symbol table containing name
	public SymbolTable findSymbolTable(String name, int operNum) {
		Symbol s;
		SymbolTable t = this;
		if (name.equals("_ic_main"))
			return t;
		try {
			while (t != null) {
				if (t.symbolExists("_" + t.getId() + "_" + name)) {
					s = t.getSymbol("_" + t.getId() + "_" + name);
					if (s.GetOpCount() <= operNum)
						return t;
				} else if (t.symbolExists(name)) {
					s = t.getSymbol(name);
					if (s.GetOpCount() <= operNum)
						return t;
				}
				
				t = t.getParent();
			}

		} catch (Exception e) {

		}
		return t;
	}
}
