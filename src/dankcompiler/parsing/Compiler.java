package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import dankcompiler.messages.CompileMsgHandler;
import dankcompiler.messages.MessageType;
import dankcompiler.analysis.Analyzer;
import dankcompiler.analysis.symbol.SymbolTable;
import dankcompiler.errors.CompileError;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.rdutils.FileHandler;

public class Compiler extends FileHandler {
	private CompileMsgHandler MsgHandler;
	private Lexer lexer;
	private Parser parser;
	private Analyzer analyzer;
	//DATA OUTPUTS
	private SymbolTable SymTable = null;
    private final ArrayList<CompileError> ErrorTable;
	public Compiler() {
		super();
		MsgHandler = new CompileMsgHandler();
		ErrorTable = new ArrayList<CompileError>();
		lexer = new Lexer(this.getCursor());
		parser = new Parser(this.lexer);
		analyzer = new Analyzer();
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
    public void showSymbolTable() {
    	Set<String> keys = SymTable.getModel().keySet();
    	System.out.println("Simbolo\t\t\tTipo");
    	for(String key : keys) {
    		System.out.println(key+"\t\t\t"+SymTable.get(key).getType().name());
    	}
    }
    private void attachErrors(ArrayList<CompileError> errors) {
    	ErrorTable.addAll(errors);
    }
    public void analyze() {
    	analyzer.setupVerbosity(true);
    	analyzer.analyze(parser.getAST());
    	attachErrors(analyzer.getCurrentErrors());
    	//analyzer.clean();
    	//parser.getSymbolTable().clear();
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
		SymTable = parser.getSymbolTable();
		analyzer.setSymbolTable(SymTable);
		
		parser.getCurrentErrors().clear();
		
		getWriter().close();
	}
}
