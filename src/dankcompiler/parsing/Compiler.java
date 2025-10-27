package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;

import dankcompiler.messages.CompileMsgHandler;
import dankcompiler.messages.MessageType;
import dankcompiler.errors.CompileError;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.rdutils.FileHandler;

public class Compiler extends FileHandler {
	private CompileMsgHandler MsgHandler;
	private Lexer lexer;
	private Parser parser;
	//DATA OUTPUTS
    private final ArrayList<CompileError> ErrorTable;
	public Compiler() {
		super();
		MsgHandler = new CompileMsgHandler();
		ErrorTable = new ArrayList<CompileError>();
		lexer = new Lexer(this.getCursor());
		parser = new Parser(this.lexer);
	}
	public Compiler(String filepath) {
		super(filepath);
		MsgHandler = new CompileMsgHandler();
		ErrorTable = new ArrayList<CompileError>();
	}
    public ArrayList<CompileError> getAllErrors(){
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
        for(CompileError error : ErrorTable){
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
    private void attachErrors(ArrayList<CompileError> errors) {
    	ErrorTable.addAll(errors);
    }
	@Override
	public void process() throws IOException {
		//Token nextToken = lexer.generateNextToken();
		//attachErrors(lexer.getErrors());
		parser.parse();
	}
	@Override
	public void doAtReadFinish(Cursor cursor) {
		attachErrors(parser.getCurrentErrors());
		parser.clean();
		getWriter().close();
	}
}
