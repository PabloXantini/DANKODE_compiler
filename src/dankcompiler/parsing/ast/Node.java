package dankcompiler.parsing.ast;

public abstract class Node {
    protected String value = null;
    public Node(){
    }
    public void setValue(String value){
        this.value = value;
    }
}
