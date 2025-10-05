package dankcompiler.messages;

import dankcompiler.errors.TokenError;

public class CompileMsgHandler {
    private Language default_language = Language.ES;
    private MessageHandler messages;
    public CompileMsgHandler(){
        messages = new MessageHandler(default_language);
    }
    public void setLanguage(Language lang){
        this.default_language = lang;
        messages = new MessageHandler(default_language);
    }
    public String generateErrorMessage(TokenError error){
        String output = messages.getMessagePlaceHolder(error.errorcode);
        output = String.format(output, error.lexem);       
        return output;
    }
    //This is for interactive purpose
    public String verboseTypeError(TokenError error){
        return messages.getErrorTypeInfo(error.type);
    }
}
