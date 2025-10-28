package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.tokens.Token;

public class BinaryOp extends Expression {
    private final Token op;
    private Expression left_term;
    private Expression right_term;
    public BinaryOp(Token op){
        this.op = op;
    }
    public Token getOp(){
        return this.op;
    }
    public Expression getLeftTerm(){
        return this.left_term;
    }
    public Expression getRightTerm(){
        return this.right_term;
    }
    public void setLeftTerm(Expression left) {
        this.left_term = left;
    }
    public void setRightTerm(Expression right) {
        this.right_term = right;
    }  
}