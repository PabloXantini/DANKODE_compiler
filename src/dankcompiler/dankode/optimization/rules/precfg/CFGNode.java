package dankcompiler.dankode.optimization.rules.precfg;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import dankcompiler.parsing.ast.AST;
import dankcompiler.parsing.ast.GroupNode;

/**
 * Each node means a basic block inherited from {@link GroupNode} (a block) from the 
 * {@link AST}, because the assignments can be stored in insertion order.
 * */
public class CFGNode extends GroupNode {
	private Set<CFGNode> predecessors;
	private Set<CFGNode> successors;
	private Set<String> defined_vars;
	private Set<String> used_vars;
	private Set<String> live_in;
	private Set<String> live_out;
	//Method Stuff
	private Set<String> computeIn(){
		Set<String> new_in = new HashSet<String>(this.used_vars);
		Set<String> temp_out = new HashSet<String>(this.live_out);
		temp_out.removeAll(this.defined_vars);
		new_in.addAll(temp_out);
		this.live_in = new_in;
		return this.live_in;
	}
	private Set<String> computeOut(){
		Set<String> new_out = new HashSet<String>();
		for(CFGNode w : this.successors) {
			new_out.addAll(w.getIn());
		}
		this.live_out = new_out;
		return this.live_out;
	}
	public CFGNode() {
		super();
		this.predecessors = new LinkedHashSet<CFGNode>();
		this.successors = new LinkedHashSet<CFGNode>();
		this.defined_vars = new HashSet<String>();
		this.used_vars = new HashSet<String>();
		this.live_in = new HashSet<String>();
		this.live_out = new HashSet<String>();
	}
	public Set<CFGNode> getPred() {
		return this.predecessors;
	}
	public Set<CFGNode> getSucc() {
		return this.successors;
	}
	public Set<String> getDef(){
		return this.defined_vars;
	}
	public Set<String> getUse(){
		return this.used_vars;
	}
	public Set<String> getIn(){
		return this.live_in;
	}
	public Set<String> getOut(){
		return this.live_out;
	}
	public boolean changeInOut() {
		HashSet<String> old_out = new HashSet<String>(this.live_out);
		HashSet<String> old_in = new HashSet<String>(this.live_in);
		computeOut();
		computeIn();
		return !old_in.equals(this.live_in) || !old_out.equals(this.live_out);
	}
}
