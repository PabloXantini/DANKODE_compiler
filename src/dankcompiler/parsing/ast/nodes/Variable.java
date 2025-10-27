package dankcompiler.parsing.ast.nodes;

public class Variable extends Expression {
    public Variable(){}
    public Variable(String value){
        this.value = value;
    }
}