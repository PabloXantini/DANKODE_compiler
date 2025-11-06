package ide.actions_tools.analysis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

//import compilador.*;
import ide.IDE;

public class CompileAction implements ActionListener{
    private IDE context;
    //Method stuff
    private void compile(){
    	//PROCESS
    	//1. If not saved, force the save of file
    	context.saveFileInEditor();
    	//2. Request to IDE for the focus path
    	String path = context.getPath();
    	//3. Compile using the dankcompiler
    	context.getCompiler().start(path);
    }
    public CompileAction(IDE context) {
    	this.context = context;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Compilar el archivo
        compile();
    }    
}
