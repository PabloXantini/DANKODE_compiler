package dankcompiler.dankode.optimization.rules;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dankcompiler.parsing.ast.AST;
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

/*
 * This class will implement the DSE technique for discard unused assignments in a AST
 * */
public class PreDSE extends ASTGeneralVisitor{
	private boolean inside_loop = false;
	private AST ast;
	private final Set<Variable> live_vars;
	private final List<AssignmentInfo> assignments;
	private final List<AssignmentInfo> dead_assignments;
	public PreDSE(AST ast) {
		this.ast = ast;
		this.live_vars = new LinkedHashSet<Variable>();
		this.assignments = new ArrayList<AssignmentInfo>();
		this.dead_assignments = new ArrayList<AssignmentInfo>();
	}
	public void optimize() {
		this.ast.setRoot(ast.getRoot().accept(this));
	}
	@Override
	public Node visit(Declaration declaration) {
		// TODO Auto-generated method stub
		return declaration;
	}

	@Override
	public Node visit(Assignment assignment) {
		AssignmentInfo new_entry = new AssignmentInfo(assignment, inside_loop);
		assignments.add(new_entry);
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

class AssignmentInfo {
	private final Assignment node;
	private final boolean loop_scoped;
	private boolean dead = false;
	public AssignmentInfo(Assignment node, boolean loop_scoped) {
		this.node = node;
		this.loop_scoped = loop_scoped;
	}
	public boolean isDead() {
		return dead;
	}
	public void markAsDead(boolean dead) {
		this.dead = dead;
	}
	public Assignment getNode() {
		return node;
	}
	public boolean isLoopScoped() {
		return loop_scoped;
	}
}
