package dankcompiler.errors;

import java.util.EnumMap;

import dankcompiler.Language;

public class ErrorMessage {
    private final Language lang;
    private final EnumMap<TokenErrorCode, String> messages;
    private final EnumMap<TokenErrorType, String> typeInfo;
    public ErrorMessage(Language lang){
        this.lang = lang;
        this.typeInfo = new EnumMap<TokenErrorType, String>(TokenErrorType.class);
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
    public String getErrorTypeInfo(TokenErrorType type){
        return typeInfo.get(type);
    }
    private void createESMessages(){
        //Phases
        typeInfo.put(TokenErrorType.LEXICAL, "Análisis Léxico");
        //Messages
        messages.put(TokenErrorCode.LEXEM_UNKNOWN, "Lexema desconocido: %s");
    }
}
