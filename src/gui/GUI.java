package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GUI extends JPanel {
    private MenuBar menuBar;
    public GUI(){
        menuBar = new MenuBar();
        setFocusable(true);
        setLayout(new BorderLayout(0, 0));
        add(menuBar, BorderLayout.NORTH);
    }
}
