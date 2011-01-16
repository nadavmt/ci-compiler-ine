package IC.LIR;

import IC.AST.*;

public abstract class BaseVisitor implements Visitor {

	protected int operationCounter = 0;
	
	public Object visit(Program program) {
			
			for (ICClass icClass : program.getClasses())
				icClass.accept(this);
			return true;
		}

		public Object visit(ICClass icClass) {
			for (Field field : icClass.getFields())
				field.accept(this);
			for (Method method : icClass.getMethods())
				method.accept(this);
			return true;
		}

		public Object visit(PrimitiveType type) {
			return true;
		}

		public Object visit(UserType type) {
			return true;
		}

		public Object visit(Field field) {
				
			return true;
		}

		public Object visit(LibraryMethod method) {
		
			return true;
		}

		public Object visit(Formal formal) {
		
			return true;
		}

		public Object visitMethod(Method method) {
		
			for (Formal formal : method.getFormals())
				formal.accept(this);
			for (Statement statement : method.getStatements())
				statement.accept(this);
			
			return true;
		}
		
		public Object visit(VirtualMethod method) {
			return visitMethod(method);
		}

		public Object visit(StaticMethod method) {
			return visitMethod(method);
		}

		public Object visit(Assignment assignment) {
			operationCounter++;
			assignment.getVariable().accept(this);
			assignment.getAssignment().accept(this);
			
			return true;
		}

		public Object visit(CallStatement callStatement) {
			operationCounter++;
			callStatement.getCall().accept(this);
			return true;
		}

		public Object visit(Return returnStatement) {
			operationCounter++;
			if (returnStatement.hasValue()) {
				returnStatement.getValue().accept(this);
			}
			return true;
		}

		public Object visit(If ifStatement) {
			operationCounter++;
			ifStatement.getCondition().accept(this);
			ifStatement.getOperation().accept(this);
			if (ifStatement.hasElse())
				ifStatement.getElseOperation().accept(this);
			
			return true;
		}

		public Object visit(While whileStatement) {
			operationCounter++;
			whileStatement.getCondition().accept(this);
			whileStatement.getOperation().accept(this);
			return true;
		}

		public Object visit(Break breakStatement) {
			return true;
		}

		public Object visit(Continue continueStatement) {
			return true;
		}

		public Object visit(StatementsBlock statementsBlock) {
			for (Statement statement : statementsBlock.getStatements())
				statement.accept(this);
			
			return true;
		}

		public Object visit(LocalVariable localVariable) {
			operationCounter++;
			localVariable.getType().accept(this);
			if (localVariable.hasInitValue()) {
				localVariable.getInitValue().accept(this);
			}
			
			return true;
		}

		public Object visit(VariableLocation location) {
			
			if (location.isExternal()) {
				location.getLocation().accept(this);
			}
			return true;
		}

		public Object visit(ArrayLocation location) {
			operationCounter++;
			location.getArray().accept(this);
			location.getIndex().accept(this);
			
			return true;
		}

		public Object visit(StaticCall call) {

			for (Expression argument : call.getArguments())
				argument.accept(this);
			
			return true;
		}

		public Object visit(VirtualCall call) {

			if (call.isExternal())
				call.getLocation().accept(this);
			for (Expression argument : call.getArguments())
				argument.accept(this);
			
			return true;
		}

		public Object visit(This thisExpression) {
			return true;
		}

		public Object visit(NewClass newClass) {
			return true;
		}

		public Object visit(NewArray newArray) {
			newArray.getType().accept(this);
			newArray.getSize().accept(this);
			return true;
		}

		public Object visit(Length length) {
			length.getArray().accept(this);
			return true;
		}

		public Object visit(MathBinaryOp binaryOp) {
			binaryOp.getFirstOperand().accept(this);
			binaryOp.getSecondOperand().accept(this);
			return true;
		}

		public Object visit(LogicalBinaryOp binaryOp) {
			binaryOp.getFirstOperand().accept(this);
			binaryOp.getSecondOperand().accept(this);
			return true;
		}

		public Object visit(MathUnaryOp unaryOp) {
			unaryOp.getOperand().accept(this);
			return true;
		}

		public Object visit(LogicalUnaryOp unaryOp) {
			unaryOp.getOperand().accept(this);
			return true;
		}

		public Object visit(Literal literal) {
			return true;
		}

		public Object visit(ExpressionBlock expressionBlock) {
			expressionBlock.getExpression().accept(this);
			return true;
		}

		public Object visit(FieldOrMethod fieldOrMethod) {
			for (Field field : fieldOrMethod.getFields())
				field.accept(this);
			
			for (Method method : fieldOrMethod.getMethods())
				method.accept(this);
			
			return true;
		}
	}

