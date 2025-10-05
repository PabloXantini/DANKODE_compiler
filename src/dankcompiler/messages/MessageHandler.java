package dankcompiler.messages;

import java.util.EnumMap;

import dankcompiler.errors.TokenErrorCode;
import dankcompiler.errors.TokenErrorType;

public class MessageHandler {
    private final Language lang;
    private final EnumMap<TokenErrorCode, String> errorMessages;
    private final EnumMap<TokenErrorType, String> typeInfo;
    public MessageHandler(Language lang){
        this.lang = lang;
        this.typeInfo = new EnumMap<TokenErrorType, String>(TokenErrorType.class);
        this.errorMessages = new EnumMap<TokenErrorCode, String>(TokenErrorCode.class);
        switch (lang) {
            case Language.ES:
                createESMessages();
                break;
            default:
                break;
        }
    }
    public String getMessagePlaceHolder(TokenErrorCode errorcode){
        return errorMessages.get(errorcode);
    }
    public String getErrorTypeInfo(TokenErrorType type){
        return typeInfo.get(type);
    }
    private void createESMessages(){
        //Phases
        typeInfo.put(TokenErrorType.LEXICAL, "Análisis Léxico");
        //Messages
        errorMessages.put(TokenErrorCode.LEXEM_UNKNOWN, "Lexema desconocido: %s");
    }
}
