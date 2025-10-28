package dankcompiler.parsing.ast.nodes;

import dankcompiler.parsing.tokens.Token;

public class Variable extends Expression {
    public Variable(){}
    public Variable(Token value){
        this.value = value;
    }
}