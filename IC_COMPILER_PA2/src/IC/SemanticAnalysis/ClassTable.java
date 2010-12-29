package IC.SemanticAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static HashMap<String, ClassSymbolTable> getClassTables()
	{
		return classTables;
	}
	
	public static HashMap<String, ICClass> getClassASTs()
	{
		return classASTs;
	}
	
	public static List<ClassSymbolTable> getChildren(SymbolTable st)
	{
		List<ClassSymbolTable> children = new ArrayList<ClassSymbolTable>();
		for(ClassSymbolTable c : classTables.values()){
			if (c.getParent() == st)
				children.add(c);
		}
		return children;
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
