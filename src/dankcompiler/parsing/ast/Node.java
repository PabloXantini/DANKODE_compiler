package dankcompiler.parsing.ast;

import dankcompiler.parsing.tokens.Token;

public abstract class Node {
    protected Token value = null;
    public Node(){
    }
    public void setValue(Token value){
        this.value = value;
    }
    public Token getValue(){
    	return this.value;
    }
    public abstract Node accept(ASTVisitor visitor);
}
