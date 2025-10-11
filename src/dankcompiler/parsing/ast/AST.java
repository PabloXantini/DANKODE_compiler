package dankcompiler.parsing.ast;

public class AST {
    private Node root;
    public AST(Node root){
        this.root = root;
    }
    public Node getRoot(){
        return this.root;
    }
}
