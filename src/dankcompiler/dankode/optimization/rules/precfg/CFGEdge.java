package dankcompiler.dankode.optimization.rules.precfg;

public class CFGEdge {
	private CFGNode previous = null;
	private CFGNode next = null;
	public CFGEdge(CFGNode previous, CFGNode next){
		this.previous = previous;
		this.next = next;
		this.previous.getSucc().add(next);
		this.next.getPred().add(previous);
	}
	public CFGNode getPrevious() {
		return previous;
	}
	public CFGNode getNext() {
		return next;
	}
	public void setPrevious(CFGNode previous) {
		this.previous = previous;
	}
	public void setNext(CFGNode next) {
		this.next = next;
	}	
}
