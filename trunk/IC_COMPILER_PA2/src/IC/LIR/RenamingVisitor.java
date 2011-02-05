package IC.LIR;

import java.util.HashMap;
import java.util.Map;

import IC.AST.*;
import IC.Parser.SemanticError;
import IC.SemanticAnalysis.ClassTable;
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
		for (SymbolTable sb : ClassTable.getClassTable(icClass.getName()).getChildrenTable())
		{
			sb.updateTableName();
		}
		
		return super.visit(icClass);
	}
	
	public Object visit(Field field)
	{
		field.updateUniqueName();
		
		try
		{
			uniqueToType.put(field.getName(), field.getEnclosingScope().getSymbol(field.getName()).getType());
		}
		catch (SemanticError e)
		{
			assert(false);
		}
		
		return true;
	}
	
	public Object visit(LibraryMethod method)
	{
		method.updateLibraryUniqueName();
		
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
			assert(false);
		}
		
		return true;
	}
	
	public Object visitMethod(Method method)
	{
		method.updateUniqueName();
		
		return super.visitMethod(method);
	}
	
	public Object visit(LocalVariable localVariable)
	{
		localVariable.updateUniqueName();
		
		return super.visit(localVariable);
	}
	
	public Object visit(VariableLocation location)
	{
		location.updateUniqueName(operationCounter);
		
		return super.visit(location);
	}
	
	public Object visit(StaticCall call)
	{
		call.updateUniqueStaticName(call.getClassName());
		
		return super.visit(call);
	}
	
	public Object visit(VirtualCall call)
	{
		call.updateUniqueName(operationCounter);
		
		return super.visit(call);
	}
}
