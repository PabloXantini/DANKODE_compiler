package ide.actions.file;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ide.IDE;

public class FileActions implements ActionListener{
	//Context
	private JComponent invoker;
	private IDE context;
	//File Actions
	private JMenuItem openFile;
	//Popup Menu
	private JPopupMenu fileMenu;
	
	private void setupEvents() {
		openFile.addActionListener(new OpenFileAction(context));
	}
	public FileActions(IDE context, JComponent invoker) {
		this.context = context;
		this.invoker = invoker;
		fileMenu = new JPopupMenu();
		openFile = new JMenuItem("Abrir archivo");
		fileMenu.add(openFile);
		//Styles
		openFile.setPreferredSize(new Dimension(250, 30));
		//Events
		setupEvents();
	}
	private void showOptions() {
		fileMenu.show(invoker, invoker.getX(), invoker.getHeight());
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		showOptions();
	}

}
