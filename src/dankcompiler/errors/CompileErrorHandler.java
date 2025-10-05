package dankcompiler.errors;

import java.util.Objects;

public class CompileErrorHandler {
    //Error Sequence
    private static int errornum = 0;
    //Method stuff
    private static long generateCode(TokenErrorType type, TokenErrorCode errorcode){
        int hashed = Objects.hash(type, errorcode, errornum);
        long new_code = Integer.toUnsignedLong(hashed);
        errornum++;
        return new_code;
    }
    public static TokenError generateError(String lexem, TokenErrorType type, int line, int column, TokenErrorCode errorcode){
        long codetoken = generateCode(type, errorcode);
        return new TokenError(codetoken, type, lexem, line, column, errorcode);
    }
}
