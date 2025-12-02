package dankcompiler.dankode.analysis.triplets.args;

public interface ArgVisitor {
	public void visit(Tag t);
	public void visit(ConstV c);
	public void visit(Temporal tmp);
	public void visit(StoreVar var);
}
