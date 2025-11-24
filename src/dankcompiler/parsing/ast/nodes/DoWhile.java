package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;

public class DoWhile extends Node {
    private Expression thenCond;
    private Node loopBody;
    public DoWhile(){}
    public Expression getThenCondition() {
        return thenCond;
    }
    public Node getLoopBody() {
        return loopBody;
    }
    public void setThenCondition(Expression thenCond) {
        this.thenCond = thenCond;
    }
    public void setLoopBody(Node loopBody) {
        this.loopBody = loopBody;
    }
	public Node accept(ASTVisitor visitor) {
		return visitor.visit(this);	
	}
}