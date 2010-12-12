package IC.Parser;

public class SemanticError extends ICException
{
	private static final long serialVersionUID = 1L;

	// ctor with no line number
	public SemanticError(String message)
	{
		super(message);
	}
	
	// ctor
    public SemanticError(int line, String message)
    {
    	super(line, message);
    }
}
