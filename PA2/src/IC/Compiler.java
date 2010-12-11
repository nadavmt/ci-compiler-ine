package IC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import java_cup.runtime.Symbol;
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
    
	private static String findOptions(ArrayList<String> list, String option, boolean exact)
	{
		for (int i = 0; i < list.size(); i++) {
			if (exact)
			{
				if (list.get(i).equals(option))
				{
					list.remove(i);
					return option;
				}
			}
			else
			{
				if (list.get(i).startsWith(option))
				{
					String tail = list.get(i).substring(option.length());
					list.remove(i);
					return tail;
				}
			}
			
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception
    {
		ArrayList<String> myArgs = new ArrayList<String>();
		for (int i=0; i<args.length;i++)
		{
			myArgs.add(args[i]);
		}
		
		String libPath = "libic.sig";
		String icFile;
		boolean prettyPrint;
		
		try
		{
			if (args.length == 0 || args.length > 3)
				throw new Exception("illegal arga number");
			prettyPrint = findOptions(myArgs, "-print-ast", true) != null;
			libPath = findOptions(myArgs, "-L", false);
			if (libPath == null)
				libPath = "libic.sig";
			
			if (myArgs.size() != 1)
				throw new Exception();
			
			icFile = myArgs.get(0);
		}
		catch(Exception e)
		{
			System.err.println("java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ]");
    		return;	
		}
    	
    	File wantedFile = new File(icFile);
    	File libFile = new File(libPath);
    	FileInputStream fis = null;
    	FileInputStream lis = null;
    	
    	Symbol libraryParseSymbol = new Symbol(0);
    	Symbol fileParseSymbol = new Symbol(1);
    	LibParser libraryParser = null;
    	String currentFile = null;
    	//parses the library file
    	try
    	{
    		// Try to open the file for reading	
    		currentFile = libPath;
    		lis = new FileInputStream(libFile);
    		Lexer libraryLexer = new Lexer(lis);
    		libraryParser = new LibParser(libraryLexer);
    		libraryParseSymbol = libraryParser.parse();//maybe scan()
    		lis.close();
    		lis = null;	
    		currentFile = icFile;
    		fis = new FileInputStream(wantedFile);
    		Lexer fileLexer = new Lexer(fis);
    		Parser fileParser = new Parser(fileLexer);
    		fileParseSymbol = fileParser.parse();//maybe scan();
    		fis.close();
    		fis = null;
    		
    		ICClass libraryRoot = (ICClass) libraryParseSymbol.value;//creates a class from the root
        	Program root = (Program) fileParseSymbol.value;
        	root.getClasses().add(libraryRoot);//adds it to the file classes
        	
        	if (prettyPrint)
        	{
        		PrettyPrinter printer = new PrettyPrinter(args[0]);
        		System.out.println(root.accept(printer));
        	}
    	}
    	catch (FileNotFoundException e)
    	{
    		System.err.println("Unable to locate file " + currentFile);
    		return;
    	}
    	catch(SyntaxError se)
    	{
    		System.err.println(currentFile+ ": Syntax error at line " + se.getLineNumber() + ": " + se.getMessage());
    		return ;
    	}
    	catch (SemanticError se)
    	{
    		System.err.println(currentFile+ ": Semantic error at line " + se.getLineNumber() + ": " + se.getMessage());
    		return ;
    	}
    	catch (LexicalError le)
    	{
    		System.err.println(currentFile+ ": Lexical error at line " + le.getLineNumber() + ": " + le.getMessage());
    		return ;	
    	}
    	catch (Exception e)
    	{
    		System.err.println(currentFile+ ": " + e);
    		return ;
    	}
    	finally
    	{
    		if (lis != null)
    			lis.close();
    		if (fis != null)
    			fis.close();
    	}
    }
}
