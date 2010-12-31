package IC.SemanticAnalysis;

import java.util.List;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;


public class MethodSymbolTable extends SymbolTable {
	
	
	public MethodSymbolTable(String id, SymbolTable parent) {
		super(id, parent);
	}

	@Override
	public SymbolTableKind getTableKind() {
		return SymbolTableKind.METHOD;
	}
	
	@Override
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
		str.append("Method Symbol Table: " + getId());
		str.append("\n");
		
		for (Symbol s : entries.values())
		{
			if (s.getIsFormal())
			{
				str.append("\tParameter: ");
				str.append(s.getType().getName());
				str.append(" ");
				str.append(s.getId());
				str.append("\n");
			}
		}
		
		for (Symbol s : entries.values())
		{
			if (!s.getIsFormal())
			{
				str.append("\tLocal variable: ");
				str.append(s.getType().getName());
				str.append(" ");
				str.append(s.getId());
				str.append("\n");
			}
		}
		
		String location = "statement block in " + getId();
		List<SymbolTable> subs = getChildrenTable();
		if (!subs.isEmpty())
		{
			str.append("Children tables: ");
			
			for (int i = 0; i < subs.size(); i++)
			{
				if (i > 0)
				{
					str.append(", ");
				}
				
				str.append(location);
			}
			
			if (subs.size() > 0)
				str.append("\n");
			
			for (int i = 0; i < subs.size(); i++)
			{
				BlockSymbolTable bst = (BlockSymbolTable)subs.get(i); 
				str.append(bst.toString(getId()));
			}
			
			str.append("\n");
		}
		
		return str.toString();
	}
}
