package dankcompiler.utils;

import java.io.File;

import dankcompiler.parsing.ast.ASTGeneralVisitor;
import dankcompiler.parsing.ast.Node;
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

public class SyntaxExporter extends ASTGeneralVisitor{
	private File TempOutput = null;
	public SyntaxExporter() {
		
	}
	public SyntaxExporter(File output) {
		this.TempOutput = output;
	}
	@Override
	public Node visit(Declaration declaration) {
		// TODO Auto-generated method stub
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		// TODO Auto-generated method stub
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		// TODO Auto-generated method stub
		return whileNode;
	}
	@Override
	public Node visit(DoWhile dowhileNode) {
		// TODO Auto-generated method stub
		return dowhileNode;
	}

	@Override
	public Node visit(If ifNode) {
		// TODO Auto-generated method stub
		return ifNode;
	}
	@Override
	public Node visit(For forNode) {
		// TODO Auto-generated method stub
		return forNode;
	}

	@Override
	public Node visitExpression(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}

	@Override
	public Node visit(BinaryOp binary_op) {
		// TODO Auto-generated method stub
		return binary_op;
	}

	@Override
	public Node visit(UnaryOp unary_op) {
		// TODO Auto-generated method stub
		return unary_op;
	}

	@Override
	public Node visit(Variable var) {
		// TODO Auto-generated method stub
		return var;
	}

	@Override
	public Node visit(Constant constant) {
		// TODO Auto-generated method stub
		return constant;
	}
}
