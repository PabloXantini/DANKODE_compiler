package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;
import dankcompiler.parsing.tokens.TokenType;

//STATEMENTS
public class Declaration extends Node{
	private Token data_type;
	private Variable var;
    public Declaration(Token type, Variable var) {
    	this.data_type = type;
    	this.var = var;
    }
    public TokenType getType() {
    	return this.data_type.getType();
    }
    public Token getDefType() {
    	return this.data_type;
    }
    public Variable getVariable(){
    	return this.var;
    }
	@Override
	public Node accept(ASTVisitor visitor) {
		return visitor.visit(this);
	}
}