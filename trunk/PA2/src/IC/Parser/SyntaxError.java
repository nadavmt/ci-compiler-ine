package IC.Parser;

import java_cup.runtime.Symbol;

public class SyntaxError extends ICException
{
	
	// ctor with no line number
	public SyntaxError(String message)
	{
		super(message);
	}
	
	// ctor
	
	public SyntaxError(int line, String message)
	{
		super(line, message);
	}
    public SyntaxError(int line, Symbol sym)
    {
    	// call exception's ctor
    	super(line, Integer.toString(sym.sym));
    }
}
