package IC.Parser;

import java_cup.runtime.Symbol;

/**
 * The class of the objects returned by the lexical analyzer.
 * The class contains information about the type of token, its
 * line-number in the source file, and its value (if it has a value).
 */
public class Token extends Symbol
{
	// The line in the source file in which the token appears
	public int lineNumber;
	
	// ctor for tokens with no value
    public Token(int line, int id)
    {
        super(id, line + 1, line + 1, null);
        
        this.lineNumber = line;
    }
    
    // ctor for tokens with a value
    public Token(int line, int id, Object val)
    {
    	super(id, line + 1, line + 1, val);
    	
    	this.lineNumber = line;
    }
}

