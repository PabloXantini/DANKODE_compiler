package dankcompiler.dankode.analysis;

import dankcompiler.parsing.ast.GroupNode;
import dankcompiler.parsing.ast.Node;

public abstract class AnalyzeVisitor {
	public void check(Node node) {
		visit(node);
		if(node instanceof GroupNode) {
			for(Node n : ((GroupNode)node).getChildren()) {
				check(n);
			}
		}
	}
	public abstract void visit(Node node);
}
