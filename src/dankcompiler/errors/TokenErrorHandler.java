package dankcompiler.errors;

import java.util.Objects;

import dankcompiler.Language;

public class TokenErrorHandler {
    //Language
    private Language default_language = Language.ES;
    private ErrorMessage messages;
    //Error Sequence
    private static int errornum = 0;
    //Method stuff
    private static int generateCode(TokenErrorType type, TokenErrorCode errorcode){
        int new_code = Objects.hash(type, errorcode, errornum);
        errornum++;
        return new_code;
    }
    public static TokenError generateError(String lexem, TokenErrorType type, int line, int column, TokenErrorCode errorcode){
        int codetoken = generateCode(type, errorcode);
        return new TokenError(codetoken, type, lexem, line, column, errorcode);
    }
    //THIS IS WHEN YOU CALL IT AS AN OBJECT
    public TokenErrorHandler(){
        messages = new ErrorMessage(default_language);
    }
    public void setLanguage(Language lang){
        this.default_language = lang;
        messages = new ErrorMessage(default_language);
    }
    public String generateMessage(TokenError error){
        String output = messages.getMessagePlaceHolder(error.errorcode);
        output = String.format(output, error.lexem);       
        return output;
    }
}
