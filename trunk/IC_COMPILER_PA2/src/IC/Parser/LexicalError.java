package IC.Parser;

public class LexicalError extends ICException
{
	private static final long serialVersionUID = 1L;

	// ctor with no line number
	public LexicalError(String message)
	{
		super(message);
	}
	
	// ctor
    public LexicalError(int line, String message)
    {
    	super(line, message);
    }
}
