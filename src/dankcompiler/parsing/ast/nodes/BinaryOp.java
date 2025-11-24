package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;

public class BinaryOp extends Expression {
    private final Token op;
    private Expression left_term;
    private Expression right_term;
    public BinaryOp(Token op){
    	super();
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
    @Override
    public Node accept(ASTVisitor visitor) {
    	return visitor.visit(this);
    }
}