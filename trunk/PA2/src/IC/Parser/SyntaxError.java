package IC.Parser;

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
    	// call exception's ctor
    	super(line, message);
    }
}
