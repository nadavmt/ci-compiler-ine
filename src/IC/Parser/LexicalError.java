package IC.Parser;

public class LexicalError extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private int lineNumber = -1;
	
	public LexicalError(String message)
	{
		super(message);
	}
	
    public LexicalError(int line, String message)
    {
    	super(message);
    	this.lineNumber = line;
    }
    
    public int getLineNumber()
    {
    	return lineNumber;
    }
    
    
}

