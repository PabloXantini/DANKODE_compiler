package dankcompiler.tokens;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Token {
    private final String symbol;
    private final TokenType type;
    Token(String symbol, TokenType type){
        this.symbol = symbol;
        this.type = type;
    }
}