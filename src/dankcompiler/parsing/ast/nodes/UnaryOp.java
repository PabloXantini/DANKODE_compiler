package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.tokens.Token;

public class UnaryOp extends Expression {
    private final Token op;
    private Expression term;
    public UnaryOp(Token op){
    	super();
        this.op = op;
    }
    public Token getOp(){
        return this.op;
    }
    public Expression getTerm(){
        return this.term;
    }
    public void setTerm(Expression term){
        this.term = term;
    }
}