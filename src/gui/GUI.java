package gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class GUI extends JPanel {
    private JPanel viewport;
    private MenuBar menuBar;
    private FileMenuBar fileMenuBar;
    private SideBar sideBar;
    private TextEditor textEditor;
    public GUI(){
        viewport = new JPanel();
        menuBar = new MenuBar();
        fileMenuBar = new FileMenuBar();
        sideBar = new SideBar();
        textEditor = new TextEditor();
        viewport.setLayout(new BoxLayout(viewport, BoxLayout.Y_AXIS));
        viewport.add(fileMenuBar);
        viewport.add(textEditor);
        setFocusable(true);
        setLayout(new BorderLayout(0, 0));
        add(menuBar, BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(viewport, BorderLayout.CENTER);
    }
    public void focusEditor(){
        textEditor.getCodeText().requestFocusInWindow();
    }
}
