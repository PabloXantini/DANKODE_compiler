package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;

public class Assignment extends Node {
    private Variable var;
    private Expression expr;
    public Assignment(){}
    public Assignment(Variable var){
        this.var = var;
    }
    public Variable getVariable(){
        return this.var;
    }
    public Expression getExpression(){
        return this.expr;
    }
    public void setVariable(Variable var){
        this.var = var;
    }
    public void setExpression(Expression expr){
        this.expr = expr;
    }
	@Override
	public Node accept(ASTVisitor visitor) {
		return visitor.visit(this);
	}
}