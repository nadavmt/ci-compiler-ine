package IC.SemanticAnalysis;

import java.util.HashMap;

public class DispatchTable {
	
	public DispatchTable()
	{
		methodOffset = new HashMap<String, Integer>();
		fieldOffset = new HashMap<String, Integer>();
	}
	
	public HashMap<String, Integer> methodOffset;
	public HashMap<String, Integer> fieldOffset;
}
