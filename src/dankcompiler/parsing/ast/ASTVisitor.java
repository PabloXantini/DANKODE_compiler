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

public interface ASTVisitor {
	public Node visit(GroupNode groupnode);
	public Node visit(Declaration declaration);
	public Node visit(Assignment assignment);
	public Node visit(While whileNode);
	public Node visit(DoWhile dowhileNode);
	public Node visit(If ifNode);
	public Node visit(For forNode);
	public Node visitExpression(Expression expression);
	public Node visit(BinaryOp binary_op);
	public Node visit(UnaryOp unary_op);
	public Node visit(Variable var);
	public Node visit(Constant constant);
}
