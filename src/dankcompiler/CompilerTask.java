package dankcompiler;

import dankcompiler.parsing.Compiler;
import dankcompiler.parsing.rdutils.ReadMode;

public class CompilerTask{    
    private Compiler Analyzer;
    public CompilerTask() {
    	Analyzer = new Compiler();
    }
    public static void main(String[] args) {
    	CompilerTask dank = new CompilerTask();
    	//dank.start("src/dankcompiler/HolaMundo.dank");
    	dank.start("src/dankcompiler/Test1.dank");
    }
    public void start(String file) {
    	Analyzer.setFilePath(file);
    	Analyzer.setReadMode(ReadMode.LAZY);
    	Analyzer.focusFileOutput("out.dankc", "src/dankcompiler/temp");
    	Analyzer.read();
    	Analyzer.analyze();
    	Analyzer.showErrors();
    	Analyzer.showSymbolTable();
    	Analyzer.showOutput();
    	Analyzer.dumpDiagnostics();
    }
}
