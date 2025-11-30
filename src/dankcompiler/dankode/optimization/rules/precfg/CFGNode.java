package dankcompiler.dankode.optimization.rules.precfg;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import dankcompiler.parsing.ast.GroupNode;

public class CFGNode extends GroupNode {
	private Set<CFGNode> predecessors;
	private Set<CFGNode> successors;
	private Set<String> defined_vars;
	private Set<String> used_vars;
	public CFGNode() {
		super();
		this.predecessors = new LinkedHashSet<CFGNode>();
		this.successors = new LinkedHashSet<CFGNode>();
		this.defined_vars = new HashSet<String>();
		this.used_vars = new HashSet<String>();
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
}
