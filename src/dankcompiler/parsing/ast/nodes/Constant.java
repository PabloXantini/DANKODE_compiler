package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.tokens.Token;

public class Constant extends Expression {
    public Constant(Token value){
        this.value = value;
        setStart(value);
    }
}