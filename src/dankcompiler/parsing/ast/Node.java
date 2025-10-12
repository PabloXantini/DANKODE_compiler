package dankcompiler.parsing.ast;

public abstract class Node {
    protected String value = null;
    public Node(){
    }
    public void setValue(String value){
        this.value = value;
    }
    public abstract void appendNode(Node node);
    public abstract void appendNode(Node node, Node branch);
}
