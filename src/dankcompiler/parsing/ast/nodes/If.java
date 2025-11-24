package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;

//INSTRUCTIONS
public class If extends Node {
    private Expression Cond;
    private Node thenBody;
    private Node elseBody; 
    public If(){}
    public Expression getCond() {
        return Cond;
    }
    public void setCond(Expression cond) {
        this.Cond = cond;
    }
    public Node getThenBody() {
        return thenBody;
    }
    public void setThenBody(Node thenBody) {
        this.thenBody = thenBody;
    }
    public Node getElseBody() {
        return elseBody;
    }
    public void setElseBody(Node elseBody) {
        this.elseBody = elseBody;
    }
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);		
	}
}