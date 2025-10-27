package dankcompiler.messages;

import java.util.EnumMap;

import dankcompiler.parsing.errors.TokenErrorCode;
import dankcompiler.parsing.errors.TokenErrorType;

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
            case ES:
                createESMessages();
                break;
            default:
                break;
        }
    }
    public Language getLanguage() {
    	return this.lang;
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
        typeInfo.put(TokenErrorType.SYNTAX, "Análisis Sintáctico");
        typeInfo.put(TokenErrorType.SEMANT, "Análisis Semántico");
        //Messages
        errorMessages.put(TokenErrorCode.LEXEM_UNKNOWN, "Lexema desconocido: %s");
        errorMessages.put(TokenErrorCode.MISMATCH, "Se esperaba: %s");
        errorMessages.put(TokenErrorCode.TOKEN_MISMATCH, "(%s) Se esperaba: %s");
        errorMessages.put(TokenErrorCode.TOKEN_UNEXPECTED, "Token inesperado: %s");
        errorMessages.put(TokenErrorCode.ID_UNEXPECTED, "Identificador inesperado: %s");
    }
}
