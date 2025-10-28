package dankcompiler.parsing.ast;

import dankcompiler.parsing.tokens.Token;

public class Node {
    protected Token value = null;
    public Node(){
    }
    public void setValue(Token value){
        this.value = value;
    }
    //public abstract void appendNode(Node node);
    //public abstract void appendNode(Node node, Node branch);
}
