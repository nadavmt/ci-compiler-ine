package IC.SemanticAnalysis;

import java.util.HashMap;

import IC.AST.ICClass;
import IC.AST.UserType;

public class ClassTable
{
	private static HashMap<String, ClassSymbolTable> classTables;
	private static HashMap<String, ICClass> classASTs;
	
	static
	{
		classTables = new HashMap<String, ClassSymbolTable>();
		classASTs = new HashMap<String, ICClass>();
	}
	
	public static ClassSymbolTable getClassTable(String name)
	{
		return classTables.get(name);
	}
	
	public static ICClass getClassAST(String name)
	{
		return classASTs.get(name);
	}
	
	public static void addClass(String name, ClassSymbolTable table, ICClass classAST)
	{
		classTables.put(name, table);
		classASTs.put(name, classAST);
	}
	
	public static ClassType fromUserType(UserType type)
	{
		return new ClassType(getClassAST(type.getName()), type.getDimension());
	}
}
