package IC.Parser;

import java_cup.runtime.Symbol;

public class Token extends Symbol
{
	public int lineNumber;
	
    public Token(int line, int id)
    {
        super(id, null);
        
        this.lineNumber = line;
    }
    
    public Token(int line, int id, Object val)
    {
    	super(id, val);
    	
    	this.lineNumber = line;
    }
}

