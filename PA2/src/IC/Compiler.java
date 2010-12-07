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
    	
    	if (args.length == 0 || args.length > 2)
    	{
    		System.err.println("java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ]");
    		return;
    	}
    	//TODO: 2nd parameter
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
    	
		// The lexical analyzer which breaks down the source file into tokens.
		Lexer lexer = new Lexer(fis);
		Parser parser = new Parser(lexer);
    }
}
