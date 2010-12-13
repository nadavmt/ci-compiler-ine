package IC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java_cup.runtime.Symbol;

import IC.AST.*;
import IC.Parser.*;

/**
 * Parses a given IC source file and creates its Abstract Syntax Tree:
 * - breaks down the source file to tokens using a JFlex lexical analyzer.
 * - feeds the above tokens to Java CUP, which outputs the AST.
 */
public class Compiler
{
	/**
	 * The entry point for the compiler.
	 * Arguments:
	 * - Input IC filename
	 * - Optional library declarations IC file (-L...)
	 * - Optional AST print flag (-print-ast) 
	 */
	public static void main(String[] args) throws Exception
    {
		ArrayList<String> myArgs = new ArrayList<String>();
		
		for (int i = 0; i < args.length; i++)
		{
			myArgs.add(args[i]);
		}
		
		String libPath = null;
		String srcPath = null;
		
		boolean prettyPrint = false;
		
		try
		{
			if ((args.length == 0) || (args.length > 3))
				throw new Exception("Illegal number of arguments");
			
			prettyPrint = findOptions(myArgs, "-print-ast", true) != null;
			libPath = findOptions(myArgs, "-L", false);
			
			if (libPath == null)
				libPath = "libic.sig";
			
			if (myArgs.size() != 1)
				throw new Exception("Illegal number of arguments");
			
			srcPath = myArgs.get(0);
		}
		catch (Exception e)
		{
			System.err.println("java IC.Compiler <file.ic> [ -L</path/to/libic.sig> ]");
    		return;	
		}
    	
    	File srcFile = new File(srcPath);
    	File libFile = new File(libPath);
    	
    	FileInputStream fis = null;
    	FileInputStream lis = null;
    	
    	Symbol libParseSymbol = new Symbol(0);
    	Symbol srcParseSymbol = new Symbol(1);
    	
    	String currentFile = null;
    	PrettyPrinter printer = null;
    	
    	try
    	{
    		currentFile = libPath;
    		lis = new FileInputStream(libFile);
    		Lexer libLexer = new Lexer(lis);
    		LibParser libParser = new LibParser(libLexer);
    		libParseSymbol = libParser.parse();
			ICClass libRoot = (ICClass)libParseSymbol.value;
    		lis.close();
    		lis = null;
			
			System.out.println("Parsed " + currentFile + " successfully!");
    		
    		if (prettyPrint)
        	{
        		printer = new PrettyPrinter(libPath);
        		System.out.println(libRoot.accept(printer) + "\n");
        	}
    		
    		currentFile = srcPath;
    		fis = new FileInputStream(srcFile);
    		Lexer srcLexer = new Lexer(fis);
    		Parser fileParser = new Parser(srcLexer);
    		srcParseSymbol = fileParser.parse();
			Program srcRoot = (Program)srcParseSymbol.value;
    		fis.close();
    		fis = null;
			
			System.out.println("Parsed " + currentFile + " successfully!");
    		
        	if (prettyPrint)
        	{
        		printer = new PrettyPrinter(srcPath);
        		System.out.println(srcRoot.accept(printer));
        	}
			
        	srcRoot.getClasses().add(libRoot);
    	}
    	catch (FileNotFoundException e)
    	{
    		System.err.println("Unable to locate file " + currentFile);
    		return;
    	}
    	catch (SyntaxError se)
    	{
    		System.err.println(currentFile + ": Syntax error (line " + se.getLineNumber() + "): " + se.getMessage());
    		return;
    	}
    	catch (SemanticError se)
    	{
    		System.err.println(currentFile + ": Semantic error (line " + se.getLineNumber() + "): " + se.getMessage());
    		return;
    	}
    	catch (LexicalError le)
    	{
    		System.err.println(currentFile + ": Lexical error (line " + le.getLineNumber() + "): " + le.getMessage());
    		return;
    	}
    	catch (Exception e)
    	{
    		System.err.println(currentFile + ": " + e);
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
	
	/**
	 * Checks whether a given command line parameter exists among the existing arguments, and
	 * returns the parameter's value.
	 * 
	 * @param list The list of given command line arguments
	 * @param option The command line option to search for
	 * @param exact Whether to search for the exact name or a prefix
	 * @return If the parameter was found: the whole param if exact, its tail if not. If not found, returns null
	 */
	private static String findOptions(ArrayList<String> list, String option, boolean exact)
	{
		for (int i = 0; i < list.size(); i++)
		{
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
}
