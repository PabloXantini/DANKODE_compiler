package gui.sections;

import java.awt.Dimension;

public class OutputViewer extends SymbolTableViewer{
	public OutputViewer() {
		super();
		setColumns("IDX", "OP", "OBJ", "FUENTE");
		setPreferredSize(new Dimension(200, 300));
		setVisible(true);
	}
}
