package dankcompiler.parsing.ast;

import java.util.ArrayList;

public abstract class ASTGeneralVisitor implements ASTVisitor {
	@Override
	public Node visit(GroupNode groupnode) {
		ArrayList<Node> children = new ArrayList<Node>();
		for(Node child : groupnode.getChildren()) {
			Node node = child.accept(this);
			if(node != null) children.add(node); 
		}
		if(children.isEmpty()) return null;
		groupnode.setChildren(children);
		return groupnode;
	}
}
