package gui.sections;

import java.awt.Dimension;

import gui.components.IDETableUI;

public class SymbolTableViewer extends IDETableUI {
    public SymbolTableViewer(){
        super();
        setColumns("Nombre","Tipo");
        setPreferredSize(new Dimension(200, 300)); 
        setVisible(true);
    }
}
