package IC.LIR;

import java.util.HashMap;
import java.util.Map;

import IC.AST.*;
import IC.Parser.SemanticError;
import IC.SemanticAnalysis.ClassSymbolTable;
import IC.SemanticAnalysis.ClassTable;
import IC.SemanticAnalysis.MethodType;
import IC.SemanticAnalysis.Symbol;
import IC.SemanticAnalysis.SymbolTable;

public class RenamingVisitor extends BaseVisitor
{
	Map<String, IC.SemanticAnalysis.Type> uniqueToType = null;
	
	public RenamingVisitor()
	{
		super();
		
		uniqueToType = new HashMap<String, IC.SemanticAnalysis.Type>();
	}
	
	public Map<String, IC.SemanticAnalysis.Type> getUniqueToType()
	{
		return uniqueToType;
	}
	
	
	@Override 
	public Object visit(ICClass icClass)
	{
		ClassSymbolTable classTable = (ClassSymbolTable)ClassTable.getClassTable(icClass.getName());
		for (SymbolTable sb : classTable.getChildrenTable())
		{
			sb.updateTableName();
			
		}
		
		if (icClass.getName().equals("Library"))
		{
			for (Method m : icClass.getMethods()) {
				//classTable.changeUniqueName(m);
				m.updateLibraryUniqueName();
			}
		}
		else
		{
			for (Method m : icClass.getMethods()) {
				//classTable.changeUniqueName(m);
				m.updateUniqueName();
			}
			
			for (Field f : icClass.getFields()) {
				//classTable.changeUniqueName(f.getName());
				f.updateUniqueName();
			}
		}
		
		return super.visit(icClass);
	}
	
	public Object visit(Field field)
	{
		//field.updateUniqueName();
		
		try
		{
			uniqueToType.put(field.getName(), field.getEnclosingScope().getSymbol(field.getName()).getType());
		}
		catch (SemanticError e)
		{
		}
		
		return true;
	}
	
	public Object visit(LibraryMethod method)
	{
		//method.updateLibraryUniqueName();
		
		return true;
	}
	
	public Object visit(Formal formal)
	{
		formal.updateUniqueName();
		
		try
		{
			uniqueToType.put(formal.getName(), formal.getEnclosingScope().getSymbol(formal.getName()).getType());
		}
		catch (SemanticError e)
		{
			
		}
		
		return true;
	}
	
	public Object visitMethod(Method method)
	{
		//method.updateUniqueName();
		
		return super.visitMethod(method);
	}
	
	public Object visit(LocalVariable localVariable)
	{
		localVariable.updateUniqueName();
		
		return super.visit(localVariable);
	}
	
	public Object visit(VariableLocation location)
	{
		
		
		IC.SemanticAnalysis.ClassType t= null;
		if (location.isExternal()&& (!(location.getLocation() instanceof This)))
		{
			t = (IC.SemanticAnalysis.ClassType)location.getLocation().accept(this);
			ClassSymbolTable baseClass = ClassTable.getClassTable(t.getName());
			baseClass = (ClassSymbolTable)baseClass.findSymbolTable(location.getName(), Integer.MAX_VALUE);
			
			location.updateUniqueExternalName(baseClass.getId(), operationCounter);
		}
		else
		{
			location.updateUniqueName(operationCounter);
		}
		
		//super.visit(location);
		if (uniqueToType.containsKey(location.getName()))
		{
			return uniqueToType.get(location.getName());
		}
		else
		{
			try {
				return location.getEnclosingScope().getSymbol(location.getName()).getType();
			} catch (SemanticError e) {
				return null;
			}
		}
	}
	
	public Object visit(StaticCall call)
	{
		call.updateUniqueStaticName(call.getClassName());
		super.visit(call);
		try {
			
			Symbol s = ClassTable.getClassTable(call.getClassName()).findSymbolTable(call.getName(), Integer.MAX_VALUE).getSymbol(call.getName());
			MethodType mt = (MethodType)s.getType();
			return mt.getReturnType();
		} catch (SemanticError e) {
			return null;
		}
	}
	
	public Object visit(VirtualCall call)
	{
		IC.SemanticAnalysis.ClassType t= null;
		if (call.isExternal())
			t = (IC.SemanticAnalysis.ClassType)call.getLocation().accept(this);
		for (Expression argument : call.getArguments())
			argument.accept(this);
		
		
		call.updateUniqueVirtualName(t, operationCounter);
		
		return true;
	}

	/*public Object visit(This thisExpression)
	{
		thisExpression.get
	}*/
			
}
