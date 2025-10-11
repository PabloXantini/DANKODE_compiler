package dankcompiler.parsing.ast;

import java.util.ArrayList;

public abstract class GroupNode extends Node{
    private ArrayList<Node> children;
    public GroupNode(){
        this.children = new ArrayList<Node>();
    }
    public void appendNode(Node node){
        this.children.add(node);
    }
}
