package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;

import dankcompiler.messages.CompileMsgHandler;
import dankcompiler.messages.MessageType;
import dankcompiler.parsing.errors.TokenError;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.rdutils.FileHandler;

public class Compiler extends FileHandler {
	private CompileMsgHandler MsgHandler;
	private Lexer lexer;
	private Parser parser;
	//DATA OUTPUTS
    private final ArrayList<TokenError> ErrorTable;
	public Compiler() {
		super();
		MsgHandler = new CompileMsgHandler();
		ErrorTable = new ArrayList<TokenError>();
		lexer = new Lexer();
	}
	public Compiler(String filepath) {
		super(filepath);
		MsgHandler = new CompileMsgHandler();
		ErrorTable = new ArrayList<TokenError>();
	}
    public ArrayList<TokenError> getAllErrors(){
        return ErrorTable;
    }
    public boolean thereErrors(){
        if(ErrorTable.size()==0) return false;
        return true;
    }
    public void showErrors(){
        if(!thereErrors()){
            System.out.println(MsgHandler.generateMessage(MessageType.ERRORS_NOT_FOUND_MESSAGE));
            return;
        }
        System.out.println(MsgHandler.generateMessage(MessageType.ERRORS_FOUND_MESSAGE));
        for(TokenError error : ErrorTable){
            String message = MsgHandler.generateErrorMessage(error);
            String errTypeInfo = MsgHandler.verboseTypeError(error);
            System.out.println(
                "Codigo: "+error.code+
                ", Fase: "+errTypeInfo+
                ", Mensaje: "+message+
                ", Linea: "+error.line+
                ", Columna: "+error.column);
        }
    }
    private void attachErrors(ArrayList<TokenError> errors) {
    	for(TokenError error : errors) {
    		ErrorTable.add(error);
    	}
    }
	@Override
	public void process(Cursor cursor) throws IOException {
		lexer.generateNextToken(cursor);
		attachErrors(lexer.getErrors());
	}
	
}
