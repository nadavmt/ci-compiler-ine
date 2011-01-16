package IC.LIR;

import IC.AST.*;
import IC.SemanticAnalysis.ClassTable;
import IC.SemanticAnalysis.SymbolTable;

public class RenamingVisitor extends BaseVisitor{

	
	@Override 
	public Object visit(ICClass icClass) {
		for (SymbolTable sb : ClassTable.getClassTable(icClass.getName()).getChildrenTable())
		{
			sb.updateTableName();
		}
		return super.visit(icClass);
	}

	
	public Object visit(Field field) {
		field.updateUniqueName();	
		return true;
	}

	public Object visit(LibraryMethod method) {
		method.updateUniqueName();
		return true;
	}

	public Object visit(Formal formal) {
		formal.updateUniqueName();
		return true;
	}

	public Object visitMethod(Method method) {
		method.updateUniqueName();
		return super.visitMethod(method);
		
	}
		
	public Object visit(LocalVariable localVariable) {
		localVariable.updateUniqueName();
		return super.visit(localVariable);
	}

	public Object visit(VariableLocation location) {
		location.updateUniqueName(operationCounter);
		return super.visit(location);
	}

	
	public Object visit(StaticCall call) {
		call.updateUniqueName(operationCounter);
		return super.visit(call);
	}

	public Object visit(VirtualCall call) {
		call.updateUniqueName(operationCounter);
		return super.visit(call);
	}

}
