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
    public ArrayList<Node> getChildren(){
    	return this.children;
    }
	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
		for(Node child : children) {
			child.accept(visitor);
		}
	}
}
