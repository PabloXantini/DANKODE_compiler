package dankcompiler.dankode.messages;

import java.util.EnumMap;

import dankcompiler.dankode.errors.CompileErrorCode;
import dankcompiler.dankode.errors.CompileErrorType;

public class MessageHandler {
    private final Language lang;
    private final EnumMap<MessageType, String> messages;
    private final EnumMap<CompileErrorCode, String> errorMessages;
    private final EnumMap<CompileErrorType, String> typeInfo;
    public MessageHandler(Language lang){
        this.lang = lang;
        this.messages = new EnumMap<MessageType, String>(MessageType.class);
        this.typeInfo = new EnumMap<CompileErrorType, String>(CompileErrorType.class);
        this.errorMessages = new EnumMap<CompileErrorCode, String>(CompileErrorCode.class);
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
    public String getMessagePlaceHolder(CompileErrorCode errorcode){
        return errorMessages.get(errorcode);
    }
    public String getErrorTypeInfo(CompileErrorType type){
        return typeInfo.get(type);
    }
    private void createESMessages(){
        //Messages
        messages.put(MessageType.ERRORS_FOUND_MESSAGE, "Se encontraron errores:");
        messages.put(MessageType.ERRORS_NOT_FOUND_MESSAGE, "No se encontraron errores.");
        //Phases
        typeInfo.put(CompileErrorType.LEXICAL, "Análisis Léxico");
        typeInfo.put(CompileErrorType.SYNTAX, "Análisis Sintáctico");
        typeInfo.put(CompileErrorType.SEMANT, "Análisis Semántico");
        //Messages
        errorMessages.put(CompileErrorCode.LEXEM_UNKNOWN, "Lexema desconocido: %s");
        errorMessages.put(CompileErrorCode.MISMATCH, "Se esperaba: %s");
        errorMessages.put(CompileErrorCode.TOKEN_MISMATCH, "(%s) Se esperaba: %s");
        errorMessages.put(CompileErrorCode.TOKEN_UNEXPECTED, "Token inesperado: %s");
        errorMessages.put(CompileErrorCode.ID_UNEXPECTED, "Identificador inesperado: %s");
        errorMessages.put(CompileErrorCode.VAR_UNDEFINED, "Variable indefinida: %s");
        errorMessages.put(CompileErrorCode.VAR_REDEFINITION, "Redefinición de variable: %s, está declarada antes como (%s)");
        errorMessages.put(CompileErrorCode.OPERATOR_INVALID, "Operador invalido: %s entre un (%s) y (%s)");
        errorMessages.put(CompileErrorCode.TYPE_INCOMPATIBILITY, "El término \"%s\" (%s) es incompatible con el tipo de la variable \"%s\" (%s)");
        errorMessages.put(CompileErrorCode.TYPE_EXPR_INCOMPATIBILITY, "La expresión no es compatible con el tipo (%s)");
    }
}
