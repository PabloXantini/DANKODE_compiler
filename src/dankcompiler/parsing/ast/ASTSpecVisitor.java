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

public class ASTSpecVisitor extends ASTGeneralVisitor {
	protected void visitDefault(Node node) {}
	@Override
	public Node visit(Declaration declaration) {
		visitDefault(declaration);
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		visitDefault(assignment);
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		visitDefault(whileNode);
		return whileNode;
	}
	@Override
	public Node visit(DoWhile dowhileNode) {
		visitDefault(dowhileNode);
		return dowhileNode;
	}
	@Override
	public Node visit(If ifNode) {
		visitDefault(ifNode);
		return ifNode;
	}
	@Override
	public Node visit(For forNode) {
		visitDefault(forNode);
		return forNode;
	}
	@Override
	public Node visitExpression(Expression expression) {
		visitDefault(expression);
		return expression;
	}

	@Override
	public Node visit(BinaryOp binary_op) {
		visitDefault(binary_op);
		return null;
	}
	@Override
	public Node visit(UnaryOp unary_op) {
		visitDefault(unary_op);
		return null;
	}

	@Override
	public Node visit(Variable var) {
		visitDefault(var);
		return null;
	}
	@Override
	public Node visit(Constant constant) {
		visitDefault(constant);
		return null;
	}
}
