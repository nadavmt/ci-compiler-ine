package IC.SemanticAnalysis;

import java.util.Collection;
import java.util.List;

import IC.AST.ASTNode;
import IC.AST.ArrayLocation;
import IC.AST.Assignment;
import IC.AST.BinaryOp;
import IC.AST.Break;
import IC.AST.Call;
import IC.AST.CallStatement;
import IC.AST.Continue;
import IC.AST.Expression;
import IC.AST.ExpressionBlock;
import IC.AST.Field;
import IC.AST.FieldOrMethod;
import IC.AST.Formal;
import IC.AST.ICClass;
import IC.AST.If;
import IC.AST.Length;
import IC.AST.LibraryMethod;
import IC.AST.Literal;
import IC.AST.LocalVariable;
import IC.AST.LogicalBinaryOp;
import IC.AST.LogicalUnaryOp;
import IC.AST.MathBinaryOp;
import IC.AST.MathUnaryOp;
import IC.AST.Method;
import IC.AST.NewArray;
import IC.AST.NewClass;
import IC.AST.PrimitiveType;
import IC.AST.Program;
import IC.AST.Return;
import IC.AST.Statement;
import IC.AST.StatementsBlock;
import IC.AST.StaticCall;
import IC.AST.StaticMethod;
import IC.AST.This;
import IC.AST.UserType;
import IC.AST.VariableLocation;
import IC.AST.VirtualCall;
import IC.AST.VirtualMethod;
import IC.AST.Visitor;
import IC.AST.While;
import IC.Parser.SemanticError;

public class TableCreator implements Visitor {

	private String file;
	private boolean mainMethodEncountered = false;

	public TableCreator(String file) {
		this.file = file;
	}

	private Object visitMethod(Method method) {
		try
		{
			SymbolTable t = new MethodSymbolTable(method.getName(), method.getEnclosingScope());

			if (!addSymbols(t, method.getFormals()))
				return null;

			for (Statement statement : method.getStatements()) {
				statement.setEnclosingScope(t);
				if (statement.accept(this) == null)
					return null;
			}

			Type returnType = (Type) method.getType().accept(this);
			if (returnType == null)
				return null;

			Type[] paramTypes = new Type[method.getFormals().size()];
			for (int i = 0; i < paramTypes.length; i++) {
				Formal f = method.getFormals().get(i);
				paramTypes[i] = t.getSymbol(f.getName()).getType();
			}

			return new Symbol(method.getName(), Kind.Method, new MethodType(paramTypes, returnType));
			
		} catch (SemanticError e) {
			e.setLineNumber(method.getLine());
			System.err.println(e.getLineNumber()+": "+ e.getMessage());
			return null;
		}
	}

	private boolean addSymbols(SymbolTable t, Collection<? extends ASTNode> c) {
		try {
			for (ASTNode node : c)
				addSymbol(t, node);
		} catch (SemanticError se) {
			System.err.println(se.getMessage());
			return false;
		} catch (InternalSemanticError e) {
			return false;
		}

		return true;
	}

	private void addSymbol(SymbolTable t, ASTNode node) throws SemanticError,
			InternalSemanticError {
		node.setEnclosingScope(t);
		Symbol s = (Symbol) node.accept(this);
		if (s == null)
			throw new InternalSemanticError();
		t.addEntry(s);
	}

	private Object prepareAllTables(List<ICClass> classCollection, GlobalSymbolTable t) throws SemanticError {
		for (ICClass c : classCollection) {
			ClassSymbolTable ct = new ClassSymbolTable(c.getName(), t);
			ClassTable.addClass(c.getName(), ct, c);

			try {
				t.addEntry(new Symbol(c.getName(), Kind.Class, null));
			} catch (SemanticError e) {
				e.setLineNumber(c.getLine());
				throw e;
			}

			for (Method m : c.getMethods()) {
				try {
					ct.addEntry(new Symbol(m.getName(), Kind.Method, null));
				} catch (SemanticError e) {
					e.setLineNumber(c.getLine());
					throw e;
				}
			}

			for (Field f : c.getFields()) {
				try {
					ct.addEntry(new Symbol(f.getName(), Kind.Field, null));
				} catch (SemanticError e) {
					e.setLineNumber(c.getLine());
					throw e;
				}
			}
		}

		// correct the class hierarchy pointers. i.e. make derived classes point
		// to their supers.
		for (ICClass c : classCollection) {
			if (c.hasSuperClass()) {
				ClassSymbolTable enclosingTable = ClassTable.getClassTable(c.getSuperClassName());

				if (enclosingTable == null)
					throw new SemanticError(c.getLine(),
							"Cannot find superclass " + c.getSuperClassName());

				c.setEnclosingScope(enclosingTable);
			}
		}

		return Boolean.TRUE;
	}

	public Object visit(Program program) {
		GlobalSymbolTable t = new GlobalSymbolTable(file);

		try {
			prepareAllTables(program.getClasses(), t);
		} catch (SemanticError e) {
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}

		if (!addSymbols(t, program.getClasses()))
			return null;

		return t;
	}

	public Object visit(ICClass icClass) {
		ClassSymbolTable t = ClassTable.getClassTable(icClass.getName());

		if (!addSymbols(t, icClass.getFields()))
			return null;
		if (!addSymbols(t, icClass.getMethods()))
			return null;
		for (Method m : icClass.getMethods())
			t.setStaticEntry(m.getName(), m.isStatic());
		
		return new Symbol(icClass.getName(), Kind.Class, new ClassType(icClass));
	}

	public Object visit(VirtualMethod method) {
		if (method.getName().equals("main")) {
			try {
				throw new SemanticError("main method must be static");
			} catch (SemanticError e) {
				e.setLineNumber(method.getLine());
				System.err.println(e.getLineNumber()+": "+ e.getMessage());;
				return null;
			}
		}
		return visitMethod(method);
	}

	public Object visit(StaticMethod method) {
		if (method.getName().equals("main")) {
			// checks if main function is of the right format
			try {

				if ((method.getType().getName().equals("void"))
						&& (method.getFormals().size() == 1)
						&& (method.getFormals().get(0).getType().getDimension() == 1)
						&& method.getFormals().get(0).getType().getName()
								.equals("string")) {
					if (mainMethodEncountered)// and if it was already declared
						throw new SemanticError(
								"there can be only one main method");
					else
						mainMethodEncountered = true;
				} else {
					throw new SemanticError(
							"main function declaration should be in the form \"static void main(string[] args)\"");
				}
			} catch (SemanticError e) {
				e.setLineNumber(method.getLine());
				System.err.println(e.getLineNumber()+": "+ e.getMessage());;
				return null;
			}

		}
		return visitMethod(method);
	}

	public Object visit(LibraryMethod method) {
		if (method.getName().equals("main")) {
			try {
				throw new SemanticError("Libarary cannot contain main method");
			} catch (SemanticError e) {
				e.setLineNumber(method.getLine());
				System.err.println(e.getLineNumber()+": "+ e.getMessage());;
				return null;
			}
		}
		return visitMethod(method);
	}

	public Object visit(Formal formal) {
		Type t = (Type) formal.getType().accept(this);
		if (t == null)
			return null;
		return new Symbol(formal.getName(), Kind.Field, t);
	}

	public Object visit(StatementsBlock statementsBlock) {
		BlockSymbolTable t = new BlockSymbolTable(statementsBlock.getEnclosingScope());
		
        for (Statement s: statementsBlock.getStatements()){
                s.setEnclosingScope(t);
                if (s.accept(this) == null) return null;
        }
        
		return t;
	}

	public Object visit(Field field) {
		Type t = (Type) field.getType().accept(this);
		if (t == null)
			return null;
		return new Symbol(field.getName(), Kind.Field, t);
	}

	public Object visit(PrimitiveType type)
	{
		switch (type.getType())
		{
			case BOOLEAN:	return new BoolType(type.getDimension());
			case INT:		return new IntType(type.getDimension()); 
			case STRING:	return new StringType(type.getDimension());
			case VOID:		return new StringType(type.getDimension());
		}
		
		return null;
	}

	public Object visit(UserType type)
	{
		return ClassTable.fromUserType(type);
	}

	public Object visit(Assignment assignment) {
		assignment.getVariable().setEnclosingScope(assignment.getEnclosingScope());
		assignment.getAssignment().setEnclosingScope(assignment.getEnclosingScope());
		if (assignment.getVariable().accept(this) == null)
			return null;
		if (assignment.getAssignment().accept(this) == null)
			return null;
		return Boolean.TRUE;
	}

	public Object visit(CallStatement callStatement) {
		callStatement.getCall().setEnclosingScope(
				callStatement.getEnclosingScope());
		if (callStatement.getCall().accept(this) == null)
			return null;
		return Boolean.TRUE;
	}

	public Object visit(Return returnStatement) {
		if (returnStatement.hasValue()) {
			returnStatement.getValue().setEnclosingScope(
					returnStatement.getEnclosingScope());
			if (returnStatement.getValue().accept(this) == null)
				return null;
		}

		return Boolean.TRUE;
	}

	public Object visit(If ifStatement) {
		ifStatement.getCondition().setEnclosingScope(
				ifStatement.getEnclosingScope());
		ifStatement.getOperation().setEnclosingScope(
				ifStatement.getEnclosingScope());
		if (ifStatement.hasElse())
			ifStatement.getElseOperation().setEnclosingScope(
					ifStatement.getEnclosingScope());

		if (ifStatement.getCondition().accept(this) == null)
			return null;
		if (ifStatement.getOperation().accept(this) == null)
			return null;
		if (ifStatement.hasElse()) {
			if (ifStatement.getElseOperation().accept(this) == null)
				return null;
		}

		return Boolean.TRUE;
	}

	public Object visit(While whileStatement) {
		
		whileStatement.getCondition().setEnclosingScope(
				whileStatement.getEnclosingScope());
		whileStatement.getOperation().setEnclosingScope(
				whileStatement.getEnclosingScope());
		if (whileStatement.getCondition().accept(this) == null)
			return null;
        
		BlockSymbolTable blockTable = (BlockSymbolTable)whileStatement.getOperation().accept(this);
		if (blockTable == null)
			return null;
		blockTable.setBreakable(true); // make sure we know we're inside a loop

		return Boolean.TRUE;
	}

	public Object visit(Break breakStatement) {
		return Boolean.TRUE;
	}

	public Object visit(Continue continueStatement) {
		return Boolean.TRUE;
	}

	public Object visit(LocalVariable localVariable) {
		if (localVariable.hasInitValue()) {
			localVariable.getInitValue().setEnclosingScope(localVariable.getEnclosingScope());
			if (localVariable.getInitValue().accept(this) == null)
				return null;
		}

		localVariable.getType().setEnclosingScope(localVariable.getEnclosingScope());
		Type t = (Type) localVariable.getType().accept(this);
		if (t == null)
			return null;
		Symbol s = new Symbol(localVariable.getName(), Kind.Field, t);
		try {
			localVariable.getEnclosingScope().addEntry(s);
		} catch (SemanticError e) {
			e.setLineNumber(localVariable.getLine());
			System.err.println(e.getLineNumber()+": "+ e.getMessage());;
			return null;
		}
		return s;
	}

	/* itai 24.12 */
	public Object visit(VariableLocation location)
	{
		if (location.isExternal()) // checks that the location isn't null
		{
			location.getLocation().setEnclosingScope(location.getEnclosingScope());
			if (location.getLocation().accept(this) == null)
				return null;
		}
		else
		{
			try
			{
				SymbolTable st = location.getEnclosingScope();
				while (st != null)
				{
					if (st.symbolExists(location.getName()))
						return Boolean.TRUE;
					st = st.getParent();
				}
				throw new SemanticError(location.getLine(), "Unknow identifier: " + location.getName());
			}
			catch (SemanticError e)
			{
				e.setLineNumber(location.getLine());
				System.err.println(e.getLineNumber()+": "+ e.getMessage());;
				return null;
			}
		}
		return Boolean.TRUE;
	}

	public Object visit(ArrayLocation location) {
		location.getArray().setEnclosingScope(location.getEnclosingScope());
		location.getIndex().setEnclosingScope(location.getEnclosingScope());
		if ((location.getIndex().accept(this) == null)
				|| (location.getArray().accept(this) == null))
			return null;
		return Boolean.TRUE;
	}

	private Object iterateOverParams(Call call)// will be used for both static
	// and virtual call
	{
		for (Expression e : call.getArguments()) {
			e.setEnclosingScope(call.getEnclosingScope());
			if (e.accept(this) == null)
				return null;
		}
		return Boolean.TRUE;
	}

	public Object visit(StaticCall call) {
		return iterateOverParams(call);
	}
	
	public Object visit(VirtualCall call) {
		if (call.isExternal())// location !=null
		{
			call.getLocation().setEnclosingScope(call.getEnclosingScope());
			if (call.getLocation().accept(this) == null)
				return null;
		}

		return iterateOverParams(call);
	}

	public Object visit(This thisExpression) {
		return Boolean.TRUE;
	}

	public Object visit(NewClass newClass) {
		return Boolean.TRUE;
	}

	public Object visit(NewArray newArray) {
		newArray.getType().setEnclosingScope(newArray.getEnclosingScope());
		newArray.getSize().setEnclosingScope(newArray.getEnclosingScope());
		if ((newArray.getType().accept(this) == null)
				|| (newArray.getSize().accept(this) == null))
			return null;
		return Boolean.TRUE;
	}

	public Object visit(Length length) {
		length.getArray().setEnclosingScope(length.getEnclosingScope());
		if (length.getArray().accept(this) == null)
			return null;
		return Boolean.TRUE;
	}

	private Object handleSingleExpression(Expression target, SymbolTable scope) {
		target.setEnclosingScope(scope);
		return target.accept(this);
	}

	private Object binaryOperationHandler(BinaryOp binaryOp) {
		if (handleSingleExpression(binaryOp.getFirstOperand(), binaryOp
				.getEnclosingScope()) == null)
			return null;
		return (handleSingleExpression(binaryOp.getFirstOperand(), binaryOp
				.getEnclosingScope()));
	}

	public Object visit(MathBinaryOp binaryOp) {
		return binaryOperationHandler(binaryOp);
	}

	public Object visit(LogicalBinaryOp binaryOp) {
		return binaryOperationHandler(binaryOp);
	}

	public Object visit(MathUnaryOp unaryOp) {
		return handleSingleExpression(unaryOp.getOperand(), unaryOp
				.getEnclosingScope());
	}

	public Object visit(LogicalUnaryOp unaryOp) {
		return handleSingleExpression(unaryOp.getOperand(), unaryOp
				.getEnclosingScope());
	}

	public Object visit(Literal literal) {
		return Boolean.TRUE;
	}

	/** where do we ever use expression block???? **/

	public Object visit(ExpressionBlock expressionBlock) {
		/*
		 * StringBuffer output = new StringBuffer();
		 * 
		 * indent(output, expressionBlock);
		 * output.append("Parenthesized expression"); ++depth;
		 * output.append(expressionBlock.getExpression().accept(this)); --depth;
		 * return output.toString();
		 */
		return Boolean.TRUE;
	}

	@Override
	public Object visit(FieldOrMethod fieldOrMethod) {
		for (Field field : fieldOrMethod.getFields()) {
			field.setEnclosingScope(fieldOrMethod.getEnclosingScope());
			if (field.accept(this) == null)
				return null;
		}
		for (Method method : fieldOrMethod.getMethods()) {
			method.setEnclosingScope(fieldOrMethod.getEnclosingScope());
			if (visitMethod(method) == null)
				return null;
		}
		return Boolean.TRUE;
	}
}
