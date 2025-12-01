package dankcompiler.dankode.optimization.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dankcompiler.dankode.optimization.rules.precfg.CFG;
import dankcompiler.dankode.optimization.rules.precfg.CFGNode;
import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.ASTPrunner;
import dankcompiler.parsing.ast.ASTSpecVisitor;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.ast.nodes.Assignment;
import dankcompiler.parsing.ast.nodes.BinaryOp;
import dankcompiler.parsing.ast.nodes.Expression;
import dankcompiler.parsing.ast.nodes.UnaryOp;
import dankcompiler.parsing.ast.nodes.Variable;

public class PreDSE extends ASTSpecVisitor {
	//LOCAL
	private AST ast_ref;
	private DeadStatementPrunner prunner;
	private Set<String> live = null;
	private List<Node> dead_statements;
	private void makeDataStoreElimination(CFG cfg) {
		if (cfg == null) return;
		for(CFGNode block : cfg.getNodes()) {
			this.live = new HashSet<String>(block.getOut());
			for(int i = block.getChildren().size()-1 ; i>=0; i--) {
				Node updated_node = block.getChildren().get(i).accept(this);
				if(updated_node == null) block.getChildren().remove(i);
			}
		}
	}
	private void applyChangesOnAST(AST ast) {
		prunner.setDeadList(dead_statements);
		this.ast_ref = ast;
		this.ast_ref.setRoot(this.ast_ref.getRoot().accept(prunner));
	}
	public PreDSE() {
		prunner = new DeadStatementPrunner();
		dead_statements = new ArrayList<Node>();
	}
	public void optimize(AST ast, CFG cfg) {
		makeDataStoreElimination(cfg);
		applyChangesOnAST(ast);
	}
	//VISIT METHODS
	@Override
	public Node visit(Assignment assignment) {
		Variable var = assignment.getVariable();
		Expression expr = assignment.getExpression();
		String varname = var.getValue().getSymbol();
		if(!live.contains(varname)) {
			dead_statements.add(assignment);
			return null;
		}else {
			live.remove(varname);
			expr.accept(this);
		}
		return assignment;
	}
	@Override
	public Node visit(BinaryOp binary_op) {
		binary_op.getLeftTerm().accept(this);
		binary_op.getRightTerm().accept(this);
		return binary_op;
	}
	@Override
	public Node visit(UnaryOp unary_op) {
		unary_op.getTerm().accept(this);
		return unary_op;
	}
	@Override
	public Node visit(Variable var) {
		live.add(var.getValue().getSymbol());
		return var;
	}
}

class DeadStatementPrunner extends ASTPrunner {
	private List<Node> dead_statements;
	public DeadStatementPrunner() {
	}
	public void setDeadList(List<Node> dead_nodes) {
		this.dead_statements = dead_nodes;
	}
	@Override
	public boolean mustBePruned(Node node) {
		if(dead_statements.contains(node)) return true;
		return false;
	}
}
