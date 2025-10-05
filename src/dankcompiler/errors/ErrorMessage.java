package dankcompiler.errors;

import java.util.EnumMap;

import dankcompiler.Language;

public class ErrorMessage {
    private final Language lang;
    private final EnumMap<TokenErrorCode, String> messages;
    public ErrorMessage(Language lang){
        this.lang = lang;
        this.messages = new EnumMap<TokenErrorCode, String>(TokenErrorCode.class);
        switch (lang) {
            case Language.ES:
                createESMessages();
                break;
            default:
                break;
        }
    }
    public String getMessagePlaceHolder(TokenErrorCode errorcode){
        return messages.get(errorcode);
    }
    private void createESMessages(){
        messages.put(TokenErrorCode.LEXEM_UNKNOWN, "Lexema desconocido: %s");
    }
}
