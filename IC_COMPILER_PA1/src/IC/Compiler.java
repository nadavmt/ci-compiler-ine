package IC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import IC.Parser.Lexer;
import IC.Parser.LexicalError;
import IC.Parser.Token;
import IC.Parser.sym;

/**
 * Breaks down a given source file to tokens using a lexical analyzer.
 * Outputs a list of the tokens along with their line-numbers and their
 * values (where applicable).
 */
public class Compiler
{
	/**
	 * The entry point for the compiler.
	 * Receives one single argument - a source file to analyze.
	 * 
	 * @param args Input filename
	 */
    public static void main(String[] args)
    {
    	// Check that a filename was provided
    	
    	if (args.length != 1)
    	{
    		System.err.println("Usage: java IC.Compiler <file.ic>");
    		return;
    	}
    	
    	File file = new File(args[0]);
    	FileInputStream fis = null;
    	
    	try
    	{
    		// Try to open the file for reading
    		
    		fis = new FileInputStream(file);
    	}
    	catch (FileNotFoundException e)
    	{
    		System.err.println("Unable to find the specified file.");
    		return;
    	}
    	
    	if (fis != null)
    	{
    		try
    		{
    			// The lexical analyzer which breaks down the source file into tokens.
    			
	    		Lexer lexer = new Lexer(fis);
	    		Token token = null;
	    		
	    		do
	    		{
	    			// Get the next token from the lexical analyzer, print it to the screen
	    			// with its line-number and value (if one exists).
	    			
	    			token = lexer.next_token();
	    			
	    			int line = token.lineNumber + 1;
	    			
	    			String tokenName = sym.tokenName(token.sym);
	    			String tokenVal = (token.value != null) ? "(" + token.value.toString() + ")" : "";
	    			
	    			System.out.println(line + ": " + tokenName + tokenVal);
	    		}
	    		while (token.sym != sym.EOF);
    		}
    		catch (LexicalError e)
    		{
    			// Thrown by the lexical analyzer when it encounters a lexical error.
    			// Print the error and the line in which it appeared.
    			
    			int lineNumber = e.getLineNumber() + 1;
    			
    			String line = (lineNumber != 0) ? lineNumber + ": " : "";
    			String message = e.getMessage();
    			
    			System.out.println(line + "Lexical error: " + message);
    			return;
    		}
    		catch (IOException e)
    		{
    			// Any IO error that might occur while reading from the file-stream.
    			
    			System.err.println("Unknown IO exception.");
    			return;
    		}
    		finally
    		{
    			try
    			{
    				// Finally, close the file-stream before exiting.
    				
    				fis.close();
    			}
    			catch (IOException e) {}
    		}
    	}
    }
}
