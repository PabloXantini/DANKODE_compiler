package dankcompiler.parsing.ast;

import dankcompiler.parsing.ast.nodes.Assignment;
import dankcompiler.parsing.ast.nodes.BinaryOp;
import dankcompiler.parsing.ast.nodes.Constant;
import dankcompiler.parsing.ast.nodes.Declaration;
import dankcompiler.parsing.ast.nodes.DoWhile;
import dankcompiler.parsing.ast.nodes.Expression;
import dankcompiler.parsing.ast.nodes.For;
import dankcompiler.parsing.ast.nodes.If;
import dankcompiler.parsing.ast.nodes.UnaryOp;
import dankcompiler.parsing.ast.nodes.Variable;
import dankcompiler.parsing.ast.nodes.While;

public abstract class ASTPrunner extends ASTGeneralVisitor {
	public ASTPrunner() {
	}
	public abstract boolean mustBePruned(Node node);
	@Override
	public Node visit(Declaration declaration) {
		if(mustBePruned(declaration)) return null;
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		if(mustBePruned(assignment)) return null;
		Node expr = assignment.getExpression().accept(this);
		assignment.setExpression((Expression)expr);
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		if(mustBePruned(whileNode)) return null;
		Node cond = whileNode.getAtCondition().accept(this);
		whileNode.setAtCondition((Expression)cond);
		Node body = whileNode.getLoopBody().accept(this);
		whileNode.setLoopBody(body);
		return whileNode;
	}
	@Override
	public Node visit(DoWhile dowhileNode) {
		if(mustBePruned(dowhileNode)) return null;
		return dowhileNode;
	}
	@Override
	public Node visit(If ifNode) {
		if(mustBePruned(ifNode)) return null;
		return ifNode;
	}

	@Override
	public Node visit(For forNode) {
		if(mustBePruned(forNode)) return null;
		return forNode;
	}

	@Override
	public Node visitExpression(Expression expression) {
		if(mustBePruned(expression)) return null;
		return expression;
	}
	@Override
	public Node visit(BinaryOp binary_op) {
		if(mustBePruned(binary_op)) return null;
		Node left_term = binary_op.getLeftTerm().accept(this);
		binary_op.setLeftTerm((Expression)left_term);
		Node right_term = binary_op.getRightTerm().accept(this);
		binary_op.setRightTerm((Expression)right_term);
		return binary_op;
	}
	@Override
	public Node visit(UnaryOp unary_op) {
		if(mustBePruned(unary_op)) return null;
		Node term = unary_op.getTerm().accept(this);
		unary_op.setTerm((Expression)term);
		return unary_op;
	}
	@Override
	public Node visit(Variable var) {
		if(mustBePruned(var)) return null;
		return var;
	}
	@Override
	public Node visit(Constant constant) {
		if(mustBePruned(constant)) return null;
		return constant;
	}
}
