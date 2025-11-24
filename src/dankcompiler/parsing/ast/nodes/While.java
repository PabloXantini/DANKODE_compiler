package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;

public class While extends Node {
    private Expression atCond;
    private Node loopBody;
    public While(){}
    public Expression getAtCondition() {
        return atCond;
    }
    public void setAtCondition(Expression atCond) {
        this.atCond = atCond;
    }
    public Node getLoopBody() {
        return loopBody;
    }
    public void setLoopBody(Node loopBody) {
        this.loopBody = loopBody;
    }
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}