package IC.SemanticAnalysis;

import java.util.HashMap;
import java.util.Map;

import IC.Parser.SemanticError;

public class SymbolTable {
	  /** map from String to Symbol **/
	  private Map<String,Symbol> entries;
	  private String id;
	  private SymbolTable parentSymbolTable;
	  
	  private Map<String,Boolean> fref = null;
	  
	  public SymbolTable(String id, SymbolTable parent) {
	    this.id = id;
	    entries = new HashMap<String,Symbol>();
	    parent = parentSymbolTable;
	  }
	  
	  public void addEntry(Symbol sym) throws SemanticError
	  {
		  if (entries.containsKey(sym.getId()))
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
	  
	  public Map<String,Boolean> getFref()
	  {
		  return fref;
	  } 
}

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
			  throw new Sem