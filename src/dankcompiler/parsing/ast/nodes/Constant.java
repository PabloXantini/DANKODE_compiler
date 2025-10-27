package dankcompiler.parsing.ast.nodes;

public class Constant extends Expression {
    public Constant(String value){
        this.value = value;
    }
}