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
	enum VisitState {
		ASSIGNMENT_RECOLLECTION,
		USED_VAR_RECOLLECTION,
		ASSIGNMENT_DISCARD
	};
	private VisitState state;
	private boolean inside_loop = false;
	private AST ast;
	private final Set<Variable> live_vars;
	private final List<AssignmentInfo> assignments;
	private final List<AssignmentInfo> dead_assignments;
	//PROCESS OF DSE
	private void visitAssignments() {
		this.ast.getRoot().accept(this);
	}
	private void revealDeadNodes() {
	    if (assignments.isEmpty()) return;
	    boolean changed;
	    do {
	        changed = false;
	        AssignmentInfo last = assignments.getLast();
	        Variable res = last.getNode().getVariable();
	        live_vars.add(res);
	        // backward pass
	        for (int i = assignments.size()-1; i>=0; i--) {
	            last = assignments.get(i);
	            res = last.getNode().getVariable();
	            if (!live_vars.contains(res) && !last.isLoopScoped()) {
	                if (!last.isDead()) {
	                    last.markAsDead(true);
	                    dead_assignments.add(last);
	                    changed = true;
	                }
	            } else {
	                live_vars.remove(res);
	                Expression expr = last.getNode().getExpression();
	                if (expr != null) {
	                    VisitState prev = state;
	                    state = VisitState.USED_VAR_RECOLLECTION;
	                    expr.accept(this);
	                    state = prev;
	                }
	            }
	        }
	    } while (changed);
	}
	private void discardNodes() {
		state=VisitState.ASSIGNMENT_DISCARD;
		this.ast.setRoot(this.ast.getRoot().accept(this));
	}
	public PreDSE() {
		this.state = VisitState.ASSIGNMENT_RECOLLECTION;
		this.live_vars = new LinkedHashSet<Variable>();
		this.assignments = new ArrayList<AssignmentInfo>();
		this.dead_assignments = new ArrayList<AssignmentInfo>();
	}
	public void reset() {
		inside_loop = false;
		live_vars.clear();
		assignments.clear();
		dead_assignments.clear();
		state=VisitState.ASSIGNMENT_RECOLLECTION;
	}
	public void optimize(AST ast) {
		this.ast = ast;
		visitAssignments();
		revealDeadNodes();
		discardNodes();
	}
	@Override
	public Node visit(Declaration declaration) {
		// TODO Auto-generated method stub
		return declaration;
	}

	@Override
	public Node visit(Assignment assignment) {
		switch(this.state) {
			case ASSIGNMENT_RECOLLECTION:
					AssignmentInfo new_entry = new AssignmentInfo(assignment, inside_loop);
					assignments.add(new_entry);
				break;
			case ASSIGNMENT_DISCARD:
					for(AssignmentInfo assign : dead_assignments) {
						if(assign.getNode() == assignment && !assign.isLoopScoped()) return null;
					}
				break;
			default: break;
		}
		return assignment;
	}

	@Override
	public Node visit(While whileNode) {
		switch(this.state) {
			case ASSIGNMENT_RECOLLECTION:
				boolean prev = inside_loop;
				inside_loop = true;
				whileNode.getLoopBody().accept(this);
				inside_loop = prev;
				break;
			case ASSIGNMENT_DISCARD:
				Node body = whileNode.getLoopBody().accept(this);
				whileNode.setLoopBody(body);
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
		switch(this.state) {
			case USED_VAR_RECOLLECTION:
				live_vars.add(var);
				break;
		default: break;
	}
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
