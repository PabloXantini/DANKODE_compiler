package dankcompiler.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import dankcompiler.messages.CompileMsgHandler;
import dankcompiler.messages.MessageType;
import dankcompiler.analysis.Analyzer;
import dankcompiler.analysis.symbol.SymbolTable;
import dankcompiler.analysis.triplets.Triplet;
import dankcompiler.errors.CompileError;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.rdutils.FileHandler;
import dankcompiler.utils.CsvExporter;

public class Compiler extends FileHandler {
	private CompileMsgHandler MsgHandler;
	private Lexer lexer;
	private Parser parser;
	private Analyzer analyzer;
	//DATA OUTPUTS
	private SymbolTable SymTable = null;
	private ArrayList<Triplet> ICode = null;
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
    public void showOutput(){
        for(Triplet entry : ICode){
            System.out.println(
            	entry.getIndex()+
                "\t"+entry.getInstruction().name()+
                "\t"+entry.getIdObject()+
                "\t\t\t"+entry.getIdSource());
        }
    }
    private void attachErrors(ArrayList<CompileError> errors) {
    	ErrorTable.addAll(errors);
    }
    public void clear() {
    	if (ErrorTable != null) ErrorTable.clear();
    	if (SymTable != null) {
    		parser.reset();
    		analyzer.clean(); 
    	}
    	if (ICode != null) ICode.clear();
    }
    public void dumpDiagnostics() {
    	String symPath = "src/dankcompiler/temp/out_symbols.csv";
		String errPath = "src/dankcompiler/temp/out_errors.csv";
		String outPath = "src/dankcompiler/temp/output.csv";
		boolean symOk = false;
		boolean errOk = false;
		boolean outOk = false;
    	if(SymTable!=null) symOk = CsvExporter.exportSymbols(SymTable, symPath);
		errOk = CsvExporter.exportErrors(ErrorTable, errPath, MsgHandler);
		if(ICode!=null) outOk = CsvExporter.exportOutput(ICode, outPath);
		if(symOk) System.out.println("CSV exportado: " + symPath);
		if(errOk) System.out.println("CSV exportado: " + errPath);
		if(outOk) System.out.println("CSV exportado: " + outPath);
    }
    public void analyze() {
    	analyzer.setupVerbosity(true);
    	analyzer.analyze(parser.getAST());
    	attachErrors(analyzer.getCurrentErrors());
    	ICode = analyzer.getCode();
    }
	@Override
	public void process() throws IOException {
		//parser.reset();
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
