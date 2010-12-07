package IC.Parser;

public class SemanticError extends ICException
{
	// ctor with no line number
	public SemanticError(String message)
	{
		super(message);
	}
	
	// ctor
    public SemanticError(int line, String message)
    {
    	// call exception's ctor
    	super(line, message);
    }
}
