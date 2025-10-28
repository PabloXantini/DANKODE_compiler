package dankcompiler.parsing.tokens;

import java.util.EnumMap;

public class TokenTable {
    private final EnumMap<TokenType,Regex> tokenTable;
    private final EnumMap<TokenType,TokenCat> tokenCategories;
    public TokenTable(){
        //Build the token Categories
        tokenCategories = new EnumMap<TokenType,TokenCat>(TokenType.class);
        tokenCategories.put(TokenType.NUMMY, TokenCat.RESERVED);
        tokenCategories.put(TokenType.NUMPT, TokenCat.RESERVED);
        tokenCategories.put(TokenType.CHARA, TokenCat.RESERVED);
        tokenCategories.put(TokenType.WHILE, TokenCat.RESERVED);
        tokenCategories.put(TokenType.IF, TokenCat.RESERVED);
        tokenCategories.put(TokenType.ELSE, TokenCat.RESERVED);
        tokenCategories.put(TokenType.AND, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.OR, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.EQUAL, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.NONEQUAL, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.GTE, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.LTE, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.GT, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.LT, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.PLUS, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.MINUS, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.MUL, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.DIV, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.MOD, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.ASSIGN, TokenCat.OPERATOR);
        tokenCategories.put(TokenType.LP, TokenCat.DELIMITER);
        tokenCategories.put(TokenType.RP, TokenCat.DELIMITER);
        tokenCategories.put(TokenType.LB, TokenCat.DELIMITER);
        tokenCategories.put(TokenType.RB, TokenCat.DELIMITER);
        tokenCategories.put(TokenType.COMMA, TokenCat.DELIMITER);
        tokenCategories.put(TokenType.SEMICOLON, TokenCat.DELIMITER);
        tokenCategories.put(TokenType.CFLOAT, TokenCat.LITERAL);
        tokenCategories.put(TokenType.CINT, TokenCat.LITERAL);
        tokenCategories.put(TokenType.CSTRING, TokenCat.LITERAL);
        tokenCategories.put(TokenType.ID, TokenCat.ID);
        tokenCategories.put(TokenType.EOF, TokenCat.EOF);
        //Build the tokenTable
        tokenTable = new EnumMap<TokenType,Regex>(TokenType.class);
        tokenTable.put(TokenType.NUMMY, new Regex("nummy"));
        tokenTable.put(TokenType.NUMPT, new Regex("numpt"));
        tokenTable.put(TokenType.CHARA, new Regex("chara"));
        //tokenTable.put(TokenType.P_TYPE, new Regex("(?:nummy|numpt|chara)"));
        tokenTable.put(TokenType.WHILE, new Regex("while"));
        tokenTable.put(TokenType.IF, new Regex("if"));
        tokenTable.put(TokenType.ELSE, new Regex("else"));
        tokenTable.put(TokenType.AND, new Regex("&&"));
        tokenTable.put(TokenType.OR, new Regex("\\|\\|"));
        tokenTable.put(TokenType.EQUAL, new Regex("=="));
        tokenTable.put(TokenType.NONEQUAL, new Regex("!="));
        tokenTable.put(TokenType.GTE, new Regex(">="));
        tokenTable.put(TokenType.LTE, new Regex("<="));
        tokenTable.put(TokenType.GT, new Regex(">"));
        tokenTable.put(TokenType.LT, new Regex("<"));
        tokenTable.put(TokenType.PLUS, new Regex("\\+"));
        tokenTable.put(TokenType.MINUS, new Regex("-"));
        tokenTable.put(TokenType.MUL, new Regex("\\*"));
        tokenTable.put(TokenType.DIV, new Regex("/"));
        tokenTable.put(TokenType.MOD, new Regex("%"));
        tokenTable.put(TokenType.ASSIGN, new Regex("="));
        tokenTable.put(TokenType.LP, new Regex("\\("));
        tokenTable.put(TokenType.RP, new Regex("\\)"));
        tokenTable.put(TokenType.LB, new Regex("\\{"));
        tokenTable.put(TokenType.RB, new Regex("\\}"));
        tokenTable.put(TokenType.SEMICOLON, new Regex(";"));
        tokenTable.put(TokenType.COMMA ,new Regex(","));
        tokenTable.put(TokenType.CFLOAT, new Regex("\\d+\\.\\d+"));
        tokenTable.put(TokenType.CINT, new Regex("\\d+"));
        tokenTable.put(TokenType.CSTRING, new Regex("\"[^\"]*\""));
        tokenTable.put(TokenType.ID, new Regex("^papuvar[_#]([A-Za-z]+[_#])+[0-9]*"));
    }
    public EnumMap<TokenType,Regex> get(){
        return this.tokenTable;
    }
    public Regex getRegex(TokenType type){
        return tokenTable.get(type);
    }
    public TokenCat getCategorie(TokenType type){
        return tokenCategories.get(type);
    }
}
