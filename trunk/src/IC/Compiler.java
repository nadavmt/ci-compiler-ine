package IC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import IC.Parser.Lexer;
import IC.Parser.LexicalError;
import IC.Parser.Token;
import IC.Parser.sym;

public class Compiler
{
    public static void main(String[] args)
    {
    	if (args.length != 1)
    	{
    		System.err.println("Usage: java IC.Compiler <file.ic>");
    		return;
    	}
    	
    	File file = new File(args[0]);
    	FileInputStream fis = null;
    	
    	try
    	{
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
	    		Lexer lexer = new Lexer(fis);
	    		Token token = lexer.next_token();
	    		
	    		do
	    		{
	    			int line = token.lineNumber+1;
	    			
	    			String tokenName = sym.tokenName(token.sym);
	    			String tokenVal = (token.value != null) ? "(" + token.value.toString() + ")" : "";
	    			
	    			System.out.println(line + ": " + tokenName + tokenVal);
	    			
	    			token = lexer.next_token();
	    		}
	    		while (token.sym != sym.EOF);
    		}
    		catch (LexicalError e)
    		{
    			int lineNumber = e.getLineNumber()+1;
    			
    			String line = (lineNumber != -1) ? lineNumber + ": " : "";
    			String message = e.getMessage();
    			
    			System.out.println(line + "Lexical error: " + message);
    			return;
    		}
    		catch (IOException e)
    		{
    			System.err.println("Unknown IO exception.");
    			return;
    		}
    		finally
    		{
    			try
    			{
    				fis.close();
    			}
    			catch (IOException e) {}
    		}
    	}
    }
}
