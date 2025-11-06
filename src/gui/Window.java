package gui;

import javax.swing.JFrame;

import ide.IDE;

public class Window extends JFrame {
    private static final long serialVersionUID = 1L;
    private IDE context;
    private GUI gui;
    public Window(IDE context){
    	this.context = context;
        setTitle("DANKIDE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(50, 50, 800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        gui = new GUI(this.context);
        setContentPane(gui);
    }
    public GUI getGUI() {
    	return this.gui;
    }
}
