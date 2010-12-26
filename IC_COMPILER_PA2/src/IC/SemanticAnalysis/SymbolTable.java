package IC.SemanticAnalysis;

import java.util.HashMap;
import java.util.Map;

import IC.Parser.SemanticError;

public class SymbolTable {
	  /** map from String to Symbol **/
	  protected Map<String,Symbol> entries;
	  protected String id;
	  protected SymbolTable parentSymbolTable;
	  
	  public SymbolTable(String id, SymbolTable parent) {
	    this.id = id;
	    entries = new HashMap<String,Symbol>();
	    parent = parentSymbolTable;
	  }
	  
	  public void addEntry(Symbol sym) throws SemanticError
	  {
	      if (entries.containsKey(sym.getId()) && (entries.get(sym.getId()).getType() != null))
	    	  throw new SemanticError("Identifier " + sym.getId() + " already exists");
	      
	      entries.put(sym.getId(), sym);
	  }
	  
	  public SymbolTable getParent()
	  {
		  return parentSymbolTable;
	  }
	  
	  public String getId()
	  {
		  return id;
	  }
	  
	  public Symbol getSymbol(String id) throws SemanticError
	  {
		  Symbol s = entries.get(id);
		  if (s==null)
			  throw new SemanticError("Identifier " + id + " does not exist"); 
		  return s;
		 // return entries.get(id);
	  } 
	  
	  public boolean symbolExists(String id) {
		  return (entries.get(id) != null);
	  }
}
