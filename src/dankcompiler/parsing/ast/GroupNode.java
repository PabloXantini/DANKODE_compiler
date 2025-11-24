package dankcompiler.parsing.ast;

import java.util.ArrayList;

public class GroupNode extends Node{
    private ArrayList<Node> children;
    public GroupNode(){
        this.children = new ArrayList<Node>();
    }
    public void appendNode(Node node){
        this.children.add(node);
    }
    public void setChildren(ArrayList<Node> children) {
    	this.children = children;
    }
    public ArrayList<Node> getChildren(){
    	return this.children;
    }
	@Override
	public Node accept(ASTVisitor visitor) {
		return visitor.visit(this);
	}
}
