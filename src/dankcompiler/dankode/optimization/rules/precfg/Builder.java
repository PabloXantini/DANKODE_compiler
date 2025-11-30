package dankcompiler.dankode.optimization.rules.precfg;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.ASTGeneralVisitor;
import dankcompiler.parsing.ast.GroupNode;
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

public class Builder extends ASTGeneralVisitor{
	private CFG cfg;
	private CFGNode currentNode;
	public Builder(){
		cfg = new CFG();
		//Create the entryNode
		currentNode = cfg.createNode();
	}
	public CFG getCFG() {
		return cfg;
	}
	public void generateCFG(AST ast) {
		ast.getRoot().accept(this);
		System.out.println(".");
	}
	@Override
	public Node visit(GroupNode groupnode) {
		System.out.println("Bloque");
		for (Node child : groupnode.getChildren()) {
			child.accept(this);
		}
		System.out.println("Bloque de salida");
		return groupnode;
	}
	@Override
	public Node visit(Declaration declaration) {
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		currentNode.appendNode(assignment);
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		//BASIC BLOCK OF CONDITION
		CFGNode conditional = cfg.createNode();
		cfg.createEdge(currentNode, conditional);
		currentNode = conditional;
		whileNode.getAtCondition().accept(this);
		//BASIC BLOCK OF BODY
		CFGNode body = cfg.createNode();
		cfg.createEdge(conditional, body);
		currentNode = body;
		whileNode.getLoopBody().accept(this);
		cfg.createEdge(currentNode, conditional);
		//BASIC BLOCK OF EXIT
		CFGNode exit = cfg.createNode();
		cfg.createEdge(conditional, exit);
		currentNode = exit;
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
		return null;
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
