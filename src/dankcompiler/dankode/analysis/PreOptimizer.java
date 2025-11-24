package dankcompiler.dankode.analysis;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.ASTVisitor;
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

public class PreOptimizer implements ASTVisitor {
	//THIS CLASS IS GOING TO IMPLEMENT THE DEAD STORE ELIMINATION
	private boolean inside_loop = false;
	private AST ast;
	private final Set<Variable> live_vars;
	private final List<AssignmentInfo> assignments;
	private final List<AssignmentInfo> dead_assignments;
	public PreOptimizer() {
		live_vars = new LinkedHashSet<>();
		assignments = new ArrayList<AssignmentInfo>();
		dead_assignments = new ArrayList<AssignmentInfo>();
	}
	/*
	private void revealDeadNodes() {
		int index = assignments.size() - 1;
		boolean thereAssignmentsUnused = true;
		boolean removedAllUnused = false;
		AssignmentInfo lastAssignment = assignments.get(index);
		Variable res = lastAssignment.getNode().getVariable();
		live_vars.add(res);
		
		while(thereAssignmentsUnused) {
			while(index>=0) {				
				if(live_vars.contains(res)) {
					live_vars.remove(res);
				}else {
					lastAssignment.markAsDead(true);
					dead_assignments.add(lastAssignment);
				}
				visitUsedVariables(lastAssignment.getNode());
				index--;
				lastAssignment = assignments.get(index);
				res = lastAssignment.getNode().getVariable();
			}
			removedAllUnused = assignments.removeIf(AssignmentInfo::isDead);
			if(removedAllUnused) thereAssignmentsUnused = false;
			index = assignments.size() - 1;
		}
	}
	private void visitUsedVariables(Node node) {
		if(node == null) return;
		if(node instanceof Assignment) {			
			visitUsedVariables(((Assignment)node).getExpression());
		} else if(node instanceof BinaryOp) {
			visitUsedVariables(((BinaryOp)node).getLeftTerm());
			visitUsedVariables(((BinaryOp)node).getRightTerm());
		} else if(node instanceof UnaryOp) {
			visitUsedVariables(((UnaryOp)node).getTerm());
		} else if(node instanceof Variable) {
			//HERE MUST SAVE USED VARS
			live_vars.add((Variable)node);
		}
	}
	private void discardDeadNodes(Node node) {
		if (node == null) return;
		if(node instanceof Assignment) {
			for(AssignmentInfo assign : dead_assignments) {
				if(assign.getNode().equals(node) && !assign.isLoopScoped()) {
					node = null;
				}
			}
		}
		else if(node instanceof GroupNode) {
			for(Node child : ((GroupNode)node).getChildren()) {
				discardDeadNodes(child);
			}
		}
	}*/
	public void optimize(AST ast) {
		this.ast = ast;
		this.ast.getRoot().accept(this);
		//visit(this.ast.getRoot());
		//revealDeadNodes();
		//discardDeadNodes(this.ast.getRoot());
	}
	/*
	@Override
	public void visit(Node node) {
		if(node == null) return;
		if(node instanceof Assignment) {
			AssignmentInfo new_entry = new AssignmentInfo((Assignment)node, inside_loop);
			assignments.add(new_entry);
		}
		else if(node instanceof While) {
			inside_loop = true;
			Node body = ((While)node).getLoopBody();
			check(body);
			inside_loop = false; 
		}
	}*/
	@Override
	public Node visit(GroupNode groupnode) {
		System.out.println("Hola");
		return groupnode;
	}
	@Override
	public Node visit(Declaration declaration) {
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