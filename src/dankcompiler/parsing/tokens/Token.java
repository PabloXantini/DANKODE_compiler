package dankcompiler.parsing.tokens;

public class Token {
    private final String symbol;
    private final TokenType type;
    private final TokenCat cat;
    private final int line;
    private final int column;
    public Token(String symbol, TokenType type, TokenCat cat, int line, int column){
        this.symbol = symbol;
        this.type = type;
        this.cat = cat;
        this.line = line;
        this.column = column;
    }
    public String getSymbol(){
        return symbol;
    }
    public TokenType getType(){
        return type;
    }
    public TokenCat getCategory(){
        return cat;
    }
}