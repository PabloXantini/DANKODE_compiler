package gui.sections;

import gui.components.IDETableUI;

public class ProblemsViewer extends IDETableUI{
	public ProblemsViewer() {
		super();
		setColumns("ID", "Lexema", "Mensaje", "Linea", "Columna");
		setVisible(true);
	}
}
