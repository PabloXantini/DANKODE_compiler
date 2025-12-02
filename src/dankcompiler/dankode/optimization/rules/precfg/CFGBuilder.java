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

public class CFGBuilder extends ASTGeneralVisitor{
	enum CFGBuilderState{
		CFG_GENERATION,
		DEF_USE_GENERATION
	}
	private CFG cfg;
	private CFGNode currentNode;
	private CFGBuilderState state;
	private void revealDefUse() {
		if(cfg == null) return;
		for(CFGNode node : cfg.getNodes()) {
			currentNode = node;
			currentNode.getDef().clear();
			currentNode.getUse().clear();
			node.accept(this);
		}
	}
	public CFGBuilder(){
		cfg = new CFG();
	}
	public CFG getCFG() {
		return cfg;
	}
	public CFG generateCFG(AST ast) {
		cfg.reset();
		//Create the entryNode
		currentNode = cfg.createNode();
		state = CFGBuilderState.CFG_GENERATION;
		ast.getRoot().accept(this);
		cfg.setExit(currentNode);
		state = CFGBuilderState.DEF_USE_GENERATION;
		revealDefUse();
		return this.cfg;
	}
	public void updateRefs() {
		CFGBuilderState previous = state;
		state = CFGBuilderState.DEF_USE_GENERATION;
		revealDefUse();
		state = previous;
	}
	@Override
	public Node visit(GroupNode groupnode) {
		for (Node child : groupnode.getChildren()) {
			child.accept(this);
		}
		return groupnode;
	}
	@Override
	public Node visit(Declaration declaration) {
		return declaration;
	}
	@Override
	public Node visit(Assignment assignment) {
		switch(state) {
			case CFG_GENERATION:				
				currentNode.appendNode(assignment);
				break;
			case DEF_USE_GENERATION:
				Variable var = assignment.getVariable();
				String varname = var.getValue().getSymbol();
				Expression expr = assignment.getExpression();
				expr.accept(this);				
				currentNode.getDef().add(varname);
				break;
			default: break;
		}
		return assignment;
	}
	@Override
	public Node visit(While whileNode) {
		switch(state) {
			case CFG_GENERATION:
				//BASIC BLOCK OF CONDITION
				CFGNode conditional = cfg.createNode();
				cfg.createEdge(currentNode, conditional);
				currentNode = conditional;
				currentNode.appendNode(whileNode.getAtCondition());
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
				break;
			case DEF_USE_GENERATION:
				break;
			default: break;
		}
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
		switch(state) {
			case DEF_USE_GENERATION:
				binary_op.getLeftTerm().accept(this);
				binary_op.getRightTerm().accept(this);
				break;
			default: break;
		}
		return binary_op;
	}
	@Override
	public Node visit(UnaryOp unary_op) {
		switch(state) {
			case DEF_USE_GENERATION:
				unary_op.getTerm().accept(this);
				break;
			default: break;
		}
		return unary_op;
	}
	@Override
	public Node visit(Variable var) {
		switch(state) {
			case DEF_USE_GENERATION:
				String varname = var.getValue().getSymbol();
				if (!currentNode.getDef().contains(varname)) 
					currentNode.getUse().add(varname);
				break;
			default: break;
		}
		return var;
	}
	@Override
	public Node visit(Constant constant) {
		return constant;
	}
}
