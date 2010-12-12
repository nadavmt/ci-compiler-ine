package IC.Parser;

import java_cup.runtime.Symbol;

public class SyntaxError extends ICException
{
	private static final long serialVersionUID = 1L;
	
	// ctor with no line number
	public SyntaxError(String message)
	{
		super(message);
	}
	
	// ctor w/ message
	public SyntaxError(int line, String message)
	{
		super(line, message);
	}
	
	// ctor w/ symbol
    public SyntaxError(int line, Symbol sym)
    {
    	super(line, Integer.toString(sym.sym));
    }
}
