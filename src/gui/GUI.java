package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class GUI extends JPanel {
    private MenuBar menuBar;
    private SideBar sideBar;
    private TextEditor textEditor;
    public GUI(){
        menuBar = new MenuBar();
        sideBar = new SideBar();
        textEditor = new TextEditor();
        setFocusable(true);
        setLayout(new BorderLayout(0, 0));
        add(menuBar, BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(textEditor, BorderLayout.CENTER);
    }
    public void focusEditor(){
        textEditor.getCodeText().requestFocusInWindow();
    }
}
