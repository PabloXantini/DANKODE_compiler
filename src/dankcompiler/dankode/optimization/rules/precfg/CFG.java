package dankcompiler.dankode.optimization.rules.precfg;

import java.util.HashSet;

public class CFG {
	private HashSet<CFGNode> nodes = null;
	private HashSet<CFGEdge> edges = null;
	public CFG(HashSet<CFGNode> nodes, HashSet<CFGEdge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	public HashSet<CFGNode> getNodes() {
		return nodes;
	}
	public HashSet<CFGEdge> getEdges() {
		return edges;
	}
	public void setNodes(HashSet<CFGNode> nodes) {
		this.nodes = nodes;
	}
	public void setEdges(HashSet<CFGEdge> edges) {
		this.edges = edges;
	}
	
}
