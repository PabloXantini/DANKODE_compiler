package dankcompiler.errors;

import java.util.Objects;

public class CompileErrorHandler {
    //Error Sequence
    private static int errornum = 0;
    //Method stuff
    private static long generateCode(CompileErrorType type, CompileErrorCode errorcode){
        int hashed = Objects.hash(type, errorcode, errornum);
        long new_code = Integer.toUnsignedLong(hashed);
        errornum++;
        return new_code;
    }
    public static CompileError generateError(String lexem, CompileErrorType type, int line, int column, CompileErrorCode errorcode, String... args){
        long codetoken = generateCode(type, errorcode);
        return new CompileError(codetoken, type, lexem, line, column, errorcode, args);
    }
}
