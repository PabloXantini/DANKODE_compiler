package dankcompiler.dankode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import dankcompiler.dankode.messages.CompileMsgHandler;
import dankcompiler.dankode.messages.MessageType;
import dankcompiler.dankode.optimization.PreOptimizer;
import dankcompiler.dankode.analysis.Analyzer;
import dankcompiler.dankode.analysis.symbol.SymbolTable;
import dankcompiler.dankode.analysis.triplets.Triplet;
import dankcompiler.dankode.build.ASMx86Exporter;
import dankcompiler.dankode.errors.CompileError;
import dankcompiler.dankode.errors.CompileErrorHandler;
import dankcompiler.parsing.Lexer;
import dankcompiler.parsing.Parser;
import dankcompiler.parsing.rdutils.Cursor;
import dankcompiler.parsing.rdutils.FileHandler;
import dankcompiler.utils.CsvExporter;
import dankcompiler.utils.SyntaxExporter;

public class Compiler extends FileHandler {
	private CompileMsgHandler MsgHandler;
	private Lexer lexer;
	private Parser parser;
	private Analyzer analyzer;
	private PreOptimizer poptimizer;
	//EXPORTERS
	private SyntaxExporter syntax_exporter = null;
	// -> HERE MUST BE HAVE A EXPORT SYSTEM
	private ASMx86Exporter output_exporter = null;
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
		poptimizer = new PreOptimizer();
		
		output_exporter = new ASMx86Exporter();
	}
    public ArrayList<CompileError> getAllErrors(){
        return ErrorTable;
    }
    public SymbolTable getSymbolTable() {
		return SymTable;
    }
    public ArrayList<Triplet> getOutput(){
    	return ICode;
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
    	poptimizer.clear();
    	CompileErrorHandler.reset();
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
    public void optimize() {
    	poptimizer.optimize(parser.getAST());
    	syntax_exporter.export(parser.getAST());
    }
    public void build() {
    	output_exporter.setCodeInput(ICode);
    	output_exporter.export(getFilePath());
    }
    @Override
    protected void setupFileOutputBinding() {
    	//call exporters with the same output for each phase
    	syntax_exporter = new SyntaxExporter(getFileOutput());
    }
	@Override
	protected void process() throws IOException {
		//parser.reset();
		parser.parse();
	}
	@Override
	protected void doAtReadFinish(Cursor cursor) {
		attachErrors(parser.getCurrentErrors());
		SymTable = parser.getSymbolTable();
		analyzer.setSymbolTable(SymTable);
		
		parser.getCurrentErrors().clear();
		
		getWriter().close();
	}
}
