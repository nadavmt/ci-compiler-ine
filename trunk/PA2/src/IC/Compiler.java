package IC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import IC.AST.*;
import IC.Parser.*;
import IC.*;

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
	 * @throws Exception 
	 */
    
	public static void main(String[] args) throws Exception
    {
    	args = new String[1];
    	args[0] = "D:\\study\\CompilationWorkspace\\IC_COMPILER\\PA2\\src\\IC\\Parser\\IC.cup";
    	// Check that a filename was provided
    	
    	if (args.length == 0 || args.length > 2)
    	{
    		System.err.println("java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ]");
    		return;
    	}
    	//TODO: 2nd parameter
    	File libFile = new File(args[1]);
    	File wantedFile = new File(args[0]);
    	FileInputStream fis = null;
    	FileInputStream lis = null;
    	java_cup.runtime.Symbol libraryParseSymbol = new java_cup.runtime.Symbol(0);
    	java_cup.runtime.Symbol fileParseSymbol = new java_cup.runtime.Symbol(0);

    	//parses the library file
    	try
    	{
    		// Try to open the file for reading	
    		lis = new FileInputStream(libFile);
    		Lexer libraryLexer = new Lexer(lis);
    		parser libraryParser = new parser(libraryLexer);
    		libraryParseSymbol = libraryParser.parse();//maybe scan()
    	}
    	catch (FileNotFoundException e)
    	{
    		System.err.println("Unable to find the library  file " + args[1]);
    		return;
    	}
    	catch (Exception e)
    	{
    		System.err.println(e);
    		return ;
    	}
    	//parses the "BIG" file
    	try
    	{
    		// Try to open the file for reading	
    		fis = new FileInputStream(wantedFile);
    		Lexer fileLexer = new Lexer(fis);
    		parser fileParser = new parser(fileLexer);
    		fileParseSymbol = fileParser.parse();//maybe scan();
    	}
    	catch (FileNotFoundException e)
    	{
    		System.err.println("Unable to find the specified file.");
    		return;
    	}
    	catch (Exception e)
    	{
    		System.err.println(e);
    		return ;
    	}
    	
    	ICClass libraryRoot = (ICClass) libraryParseSymbol.value;//creates a ckass frin the root
    	Program root = (Program) fileParseSymbol.value;
    	root.getClasses().add(libraryRoot);//adds it to the file classes
    	
		PrettyPrinter printer = new PrettyPrinter(args[0]);
        System.out.println(root.accept(printer));

    }
}
