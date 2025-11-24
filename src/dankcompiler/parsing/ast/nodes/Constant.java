package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.ast.ASTVisitor;
import dankcompiler.parsing.ast.Node;
import dankcompiler.parsing.tokens.Token;

public class Constant extends Expression {
    public Constant(Token value){
        this.value = value;
        setStart(value);
    }
    @Override
    public Node accept(ASTVisitor visitor) {
    	return visitor.visit(this);
    }
}