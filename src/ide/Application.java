package ide;

import dankcompiler.CompilerTask;

public class Application {
	private String path;	//BY MOMENT THIS APP ONLY HANDLE ONE FILE
	private CompilerTask compiler;
	public Application() {
		this.compiler = new CompilerTask();
	}
}
