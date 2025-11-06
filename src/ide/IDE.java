package ide;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dankcompiler.CompilerTask;
import gui.TextEditor;

public class IDE {
	private CompilerTask compiler;
	//COMMANDED COMPONENTS
	private TextEditor refTextEditor;
	//FILES
	private volatile String open_path = System.getProperty("user.home");	//BY MOMENT THIS APP ONLY HANDLE ONE FILE
	public IDE() {
		this.compiler = new CompilerTask();
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
}
