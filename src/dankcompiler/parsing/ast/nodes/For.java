package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.Node;

class For extends Node {
    private Declaration decInit;
    private Assignment Init;
    private Expression Cond;
    private Expression Action;
    private Node loopBody;
    public For(){}
    public Declaration getDeclarationInit() {
        return decInit;
    }
    public Assignment getInit() {
        return Init;
    }
    public Expression getCondition() {
        return Cond;
    }
    public Expression getAction() {
        return Action;
    }
    public Node getLoopBody() {
        return loopBody;
    }
    public void setDecInit(Declaration decInit) {
        this.decInit = decInit;
    }
    public void setInit(Assignment init) {
        Init = init;
    }
    public void setCondition(Expression cond) {
        Cond = cond;
    }
    public void setAction(Expression action) {
        Action = action;
    }
    public void setLoopBody(Node loopBody) {
        this.loopBody = loopBody;
    }
}