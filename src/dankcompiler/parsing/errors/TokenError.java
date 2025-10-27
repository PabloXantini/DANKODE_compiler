package dankcompiler.parsing.errors;

public class TokenError {
    public final long code;
    public final TokenErrorType type;
    public final String lexem;
    public final int line;
    public final int column;
    public final String[] args;
    public final TokenErrorCode errorcode;
    public TokenError(long code, TokenErrorType type, String lexem, int line, int column, TokenErrorCode errorcode, String... args){
        this.code = code;
        this.type = type;
        this.lexem = lexem;
        this.line = line;
        this.column = column;
        this.errorcode = errorcode;
        this.args = args;
    }    
}
