package dankcompiler.dankode.optimization.rules.precfg;

import java.util.HashSet;

public class CFG {
	private CFGNode entry = null;
	private CFGNode exit = null;
	private HashSet<CFGNode> nodes = null;
	private HashSet<CFGEdge> edges = null;
	public CFG() {
		nodes = new HashSet<CFGNode>();
		edges = new HashSet<CFGEdge>();
	}
	public CFG(HashSet<CFGNode> nodes, HashSet<CFGEdge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	public CFGNode getEntryNode() {
		return entry;
	}
	public CFGNode getExitNode() {
		return exit;
	}
	public HashSet<CFGNode> getNodes() {
		return nodes;
	}
	public HashSet<CFGEdge> getEdges() {
		return edges;
	}
	public void setEntry(CFGNode node) {
		this.entry = node;
	}
	public void setExit(CFGNode node) {
		this.exit = node;
	}
	public void setNodes(HashSet<CFGNode> nodes) {
		this.nodes = nodes;
	}
	public void setEdges(HashSet<CFGEdge> edges) {
		this.edges = edges;
	}
	public CFGNode createNode() {
		CFGNode new_node = new CFGNode();
		this.nodes.add(new_node);
		if(entry == null) entry = new_node;
		return new_node;
	}
	public CFGEdge createEdge(CFGNode prev, CFGNode next) {
		CFGEdge new_edge = new CFGEdge(prev, next);
		this.edges.add(new_edge);
		return new_edge;
	}
	public void addNode(CFGNode node) {
		nodes.add(node);
	}
	public void addEdge(CFGEdge edge) {
		edges.add(edge);
	}
}
