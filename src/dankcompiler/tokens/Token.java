package dankcompiler.tokens;

public class Token {
    private final String symbol;
    private final TokenType type;
    private final TokenCat cat;
    Token(String symbol, TokenType type, TokenCat cat){
        this.symbol = symbol;
        this.type = type;
        this.cat = cat;
    }
}