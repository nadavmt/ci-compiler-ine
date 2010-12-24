package IC.Parser;

public class ICException extends Exception
{
	// The warning told me to put this here...
	private static final long serialVersionUID = 1L;
	
	// will save the line number of the error
	private int lineNumber = -1;
	
	// ctor with no line number
	public ICException(String message)
	{
		super(message);
	}
	
	// ctor
    public ICException(int line, String message)
    {
    	// call exception's ctor
    	super(message);
    	
    	// save line number
    	this.lineNumber = line;
    }

    // returns the line number which the error was in
    public int getLineNumber()
    {
    	return lineNumber;
    }
    public void setLineNumber(int line)
    {
    	this.lineNumber=line;
    }
}
 = -1;
	
	// ctor with no line number
	public ICException(String message)
	{
		super(message);
	}
	
	// ctor
    public ICException(int line, String message)
    {
    	// call exception's ctor
    	super(message);
    	
    	// save line number
    	this.lineNu