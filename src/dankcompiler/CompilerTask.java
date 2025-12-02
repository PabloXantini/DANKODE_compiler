package dankcompiler;

import dankcompiler.dankode.Compiler;
import dankcompiler.parsing.rdutils.ReadMode;

public class CompilerTask{    
    private Compiler CompilerImpl;
    public CompilerTask() {
    	CompilerImpl = new Compiler();
    	CompilerImpl.setReadMode(ReadMode.LAZY);
    	CompilerImpl.focusFileOutput("out.dankc", "src/dankcompiler/temp");
    }
    /*
    public static void main(String[] args) {
    	CompilerTask dank = new CompilerTask();
    	//dank.start("src/dankcompiler/HolaMundo.dank");
    	dank.start("src/dankcompiler/Test1.dank");
    	dank.start("src/dankcompiler/Test1.dank");
    }*/
    public void start(String file) {
    	CompilerImpl.clear();
    	CompilerImpl.setFilePath(file);
    	CompilerImpl.read();
    	CompilerImpl.optimize();
    	//RECOMPILE
    	CompilerImpl.clear();
    	CompilerImpl.setFilePath("src/dankcompiler/temp/out.dankc");
    	CompilerImpl.read();
    	//CONTINUE COMPILING
    	CompilerImpl.analyze();
    	CompilerImpl.showErrors();
    	//EXPORT
    	CompilerImpl.setFilePath(file);
    	CompilerImpl.build();
    	/*
    	//CompilerImpl.showSymbolTable();
    	//CompilerImpl.showOutput();
    	CompilerImpl.dumpDiagnostics();
    	*/
    }
    public Compiler getImplementation() {
    	return this.CompilerImpl;
    }
}
