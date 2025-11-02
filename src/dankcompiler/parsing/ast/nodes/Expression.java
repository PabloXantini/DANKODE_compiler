package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;

//EXPRESSIONS
public class Expression extends Node {
	private Token start;
    public Expression(){}
    public Token getStart() {
    	return this.start;
    }
    public void setStart(Token start) {
    	this.start = start;
    }
}