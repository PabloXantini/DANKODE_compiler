package dankcompiler.errors;

public class CompileError {
    public final long code;
    public final CompileErrorType type;
    public final String lexem;
    public final int line;
    public final int column;
    public final String[] args;
    public final CompileErrorCode errorcode;
    public CompileError(long code, CompileErrorType type, String lexem, int line, int column, CompileErrorCode errorcode, String... args){
        this.code = code;
        this.type = type;
        this.lexem = lexem;
        this.line = line;
        this.column = column;
        this.errorcode = errorcode;
        this.args = args;
    }    
}
