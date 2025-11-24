package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;

public class Variable extends Expression {
    public Variable(){}
    public Variable(Token value){
        this.value = value;
        setStart(value);
    }
    @Override
    public Node accept(ASTVisitor visitor) {
    	return visitor.visit(this);
    }
}