package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.TokenType;

//STATEMENTS
public class Declaration extends Node{
	private TokenType data_type;
	private Variable var;
    public Declaration(TokenType type, Variable var) {
    	this.data_type = type;
    	this.var = var;
    }
    public TokenType getType() {
    	return this.data_type;
    }
    public Variable getVariable(){
    	return this.var;
    }
}