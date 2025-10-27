package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.tokens.TokenType;

public class BinaryOp extends Expression {
    private final TokenType op;
    private Expression left_term;
    private Expression right_term;
    public BinaryOp(TokenType op){
        this.op = op;
    }
    public TokenType getOp(){
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