package dankcompiler.parsing.ast;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class ASTGeneralVisitor implements ASTVisitor {
	@Override
	public Node visit(GroupNode groupnode) {
		Iterator<Node> it = groupnode.getChildren().iterator();
		while(it.hasNext()) {
			Node node = it.next().accept(this);
			if(node == null) it.remove();
		}
		/*
		ArrayList<Node> children = new ArrayList<Node>();
		for(Node child : groupnode.getChildren()) {
			Node node = child.accept(this);
			if(node != null) children.add(node); 
		}
		if(children.isEmpty()) return null;
		groupnode.setChildren(children);
		*/
		return groupnode;
	}
}
