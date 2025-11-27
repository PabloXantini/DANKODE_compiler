package dankcompiler.dankode.optimization.rules.precfg;

import java.util.HashSet;
import java.util.Set;

import dankcompiler.parsing.ast.GroupNode;

public class CFGNode extends GroupNode {
	protected Set<String> defined_vars;
	protected Set<String> used_vars;
	public CFGNode() {
		super();
		this.defined_vars = new HashSet<String>();
		this.used_vars = new HashSet<String>();
	}
}
