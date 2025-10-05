package dankcompiler.messages;

import java.util.EnumMap;

import dankcompiler.errors.TokenErrorCode;
import dankcompiler.errors.TokenErrorType;

public class MessageHandler {
    private final Language lang;
    private final EnumMap<MessageType, String> messages;
    private final EnumMap<TokenErrorCode, String> errorMessages;
    private final EnumMap<TokenErrorType, String> typeInfo;
    public MessageHandler(Language lang){
        this.lang = lang;
        this.messages = new EnumMap<MessageType, String>(MessageType.class);
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
    public void setMessage(MessageType msgcode, String newMessage){
        messages.replace(msgcode, newMessage);
    }
    public String getMessage(MessageType msgcode){
        return messages.get(msgcode);
    }
    public String getMessagePlaceHolder(TokenErrorCode errorcode){
        return errorMessages.get(errorcode);
    }
    public String getErrorTypeInfo(TokenErrorType type){
        return typeInfo.get(type);
    }
    private void createESMessages(){
        //Messages
        messages.put(MessageType.ERRORS_FOUND_MESSAGE, "Se encontraron errores:");
        messages.put(MessageType.ERRORS_NOT_FOUND_MESSAGE, "No se encontraron errores.");
        //Phases
        typeInfo.put(TokenErrorType.LEXICAL, "Análisis Léxico");
        //Messages
        errorMessages.put(TokenErrorCode.LEXEM_UNKNOWN, "Lexema desconocido: %s");
    }
}
