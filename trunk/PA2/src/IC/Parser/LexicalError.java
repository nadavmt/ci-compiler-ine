package IC.Parser;

public class LexicalError extends ICException
{
	// ctor with no line number
	public LexicalError(String message)
	{
		super(message);
	}
	
	// ctor
    public LexicalError(int line, String message)
    {
    	// call exception's ctor
    	super(line, message);
    }
}
