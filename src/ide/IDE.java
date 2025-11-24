package ide;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import dankcompiler.CompilerTask;
import dankcompiler.dankode.analysis.symbol.Symbol;
import dankcompiler.dankode.analysis.symbol.SymbolTable;
import dankcompiler.dankode.analysis.triplets.Triplet;
import dankcompiler.dankode.errors.CompileError;
import dankcompiler.dankode.messages.CompileMsgHandler;
import gui.TextEditor;
import gui.sections.OutputViewer;
import gui.sections.ProblemsViewer;
import gui.sections.SymbolTableViewer;

public class IDE {
	private CompileMsgHandler messageHandler;
	private CompilerTask compiler;
	//COMMANDED COMPONENTS
	private TextEditor refTextEditor;
	private ProblemsViewer refErrors;
	private SymbolTableViewer refSymTable;
	private OutputViewer refOut;
	//FILES
	private volatile String default_path = System.getProperty("user.home");
	private volatile String open_path = "";	//BY MOMENT THIS APP ONLY HANDLE ONE FILE
	public IDE() {
		this.messageHandler = new CompileMsgHandler();
		this.compiler = new CompilerTask();
	}
	public String getDefaultPath() {
		return this.default_path;
	}
	public String getPath() {
		return this.open_path;
	}
	public TextEditor getEditor() {
		return this.refTextEditor;
	}
	public CompilerTask getCompiler() {
		return this.compiler;
	}
	public void setEditor(TextEditor editor) {
		this.refTextEditor = editor;
	}
	public void setSymbolTableViewer(SymbolTableViewer view) {
		this.refSymTable = view;
	}
	public void setProblemViewer(ProblemsViewer view) {
		this.refErrors = view;
	}
	public void setOutputViewer(OutputViewer view) {
		this.refOut = view;
	}
	public void focusPath(String file_path) {
		this.open_path = file_path;
	}
	public void saveFileInEditor() {
		File file = new File(open_path);
		BufferedWriter buffer = null;
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			buffer = new BufferedWriter(writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			refTextEditor.getCodeText().write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void showErrors() {
		refErrors.clear();
		for(CompileError error : compiler.getImplementation().getAllErrors()) {
			String message = messageHandler.generateErrorMessage(error);
			refErrors.addRow(error.code, error.lexem, message, error.line, error.column);
		}
	}
	public void showSymbolTable() {
		refSymTable.clear();
		SymbolTable ref = compiler.getImplementation().getSymbolTable();
		Set<String> keys = ref.getModel().keySet();
		for(String key : keys) {
			refSymTable.addRow(key, ref.getModel().get(key).getType());
		}
	}
	public void showOutput() {
		refOut.clear();
		for(Triplet triplet : compiler.getImplementation().getOutput()) {
			refOut.addRow(triplet.getIndex(), triplet.getInstruction().name() ,triplet.getIdObject(), triplet.getIdSource());
		}
	}
}
