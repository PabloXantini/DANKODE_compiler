package ide.actions.file;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ide.IDE;

public class OpenFileAction implements ActionListener{
	private IDE context;
	private File opened_file; 
	private void openExplorer() {
		Frame frame = new Frame();
		FileDialog dialog = new FileDialog(frame, "Abrir archivo", FileDialog.LOAD);
		dialog.setVisible(true);
		if(dialog.getFile()!=null) {
			FileReader reader = null;
			BufferedReader buffer = null;
			opened_file = new File(dialog.getDirectory(), dialog.getFile());
			context.focusPath(opened_file.getAbsolutePath());
			try {
				reader = new FileReader(opened_file);
				buffer = new BufferedReader(reader);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				context.getEditor().getCodeText().read(buffer, null);
			} catch (IOException error) {
				System.out.println("Error at read file: "+error);
			}
		}
		frame.dispose();
	}
	public OpenFileAction(IDE context) {
		this.context = context;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		openExplorer();
	}
}
