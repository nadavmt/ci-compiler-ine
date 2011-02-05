package IC.SemanticAnalysis;

import java.util.HashMap;

public class DispatchTable
{
	private int methodCount = 0;
	private int fieldCount = 0;
	public HashMap<String, Pair> methodOffset;
	public HashMap<String, Pair> fieldOffset;
	private String name;
	
	public DispatchTable(String name)
	{
		methodOffset = new HashMap<String, Pair>();
		fieldOffset = new HashMap<String, Pair>();
		this.name  = name;
	}
	
	public void addMethod(String methodName, String uniqueName)
	{
		int count;
		
		if (methodOffset.containsKey(methodName))
			count = methodOffset.get(methodName).counter;
		else
			count = methodCount++;
		
		methodOffset.put(methodName,new Pair(uniqueName,count));
	}
	
	public void addField(String fieldName, String UniqueName)
	{
		int count;
		
		if (fieldOffset.containsKey(fieldName))
			count = fieldOffset.get(fieldName).counter;
		else
			count = fieldCount++;
		
		fieldOffset.put(fieldName,new Pair(UniqueName,count));
	}
	
	public String toString()
	{
		Object[] arr = methodOffset.values().toArray();
		bubbleSort1(arr);
		StringBuffer str = new StringBuffer();
		str.append("_DV_");
		str.append(name);
		str.append(": [");
		
		for (int i = 0; i < arr.length; i++)
		{
			if (i > 0)
			{
				str.append(',');
			}
			
			str.append(((Pair)arr[i]).uniqueName);
		}
		
		str.append("]");
		
		return str.toString();	
	}
	
	public static void bubbleSort1(Object[] xObj)
	{
	    Pair x1;
	    Pair x2;
		
		int n = xObj.length;
		
	    for (int pass = 1; pass < n; pass++)
		{
	        for (int i = 0; i < n - pass; i++)
			{
	        	x1 = (Pair)xObj[i];
	        	x2 = (Pair)xObj[i + 1];
				
	            if (x1.counter > x2.counter)
				{
					Object temp = xObj[i]; 
					xObj[i] = xObj[i + 1]; 
					xObj[i + 1] = temp;
	            }
	        }
	    }
	}

	public void setName(String name2)
	{
		name = name2;
	}
}
