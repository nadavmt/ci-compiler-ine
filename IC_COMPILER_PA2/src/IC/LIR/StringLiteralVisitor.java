package IC.LIR;

import IC.LiteralTypes;
import IC.AST.*;
import java.io.*;

public class StringLiteralVisitor extends BaseVisitor {

	private PrintWriter out;
	private int counter;
	
	public StringLiteralVisitor(PrintWriter output)
	{
		out = output;
		counter = 0;
	}
	
	@Override
	public Object visit(Literal literal)
	{
		if (literal.getType() == LiteralTypes.STRING)
		{
			counter++;
			String newLabel = "str" + counter ; 
			out.println(newLabel+ ": " + literal.getValue());
			literal.setValue(newLabel);	
		}
		
		return true;
	}
	
	public int getCount()
	{
		return counter;
	}
}
