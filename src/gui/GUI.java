package gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ide.IDE;
import ide.actions_tools.analysis.CompileAction;

public class GUI extends JPanel {
	private IDE context;
    private JPanel viewport;
    private MenuBar menuBar;
    private FileMenuBar fileMenuBar;
    private SideBar sideBar;
    private TextEditor textEditor;
    private BottomInfoView bottomInfo;
    
    //Method Build Stuff
    private void setupEvents(){
        fileMenuBar.getBtnCompile().addActionListener(new CompileAction(this.context));
    }
    public GUI(IDE context){
    	this.context = context;
        viewport = new JPanel();
        menuBar = new MenuBar(this.context);
        fileMenuBar = new FileMenuBar(this.context);
        sideBar = new SideBar(this.context);
        textEditor = new TextEditor(this.context);
        bottomInfo = new BottomInfoView(context);
        viewport.setLayout(new BoxLayout(viewport, BoxLayout.Y_AXIS));
        viewport.add(fileMenuBar);
        viewport.add(textEditor);
        viewport.add(bottomInfo);
        setFocusable(true);
        setLayout(new BorderLayout(0, 0));
        add(menuBar, BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(viewport, BorderLayout.CENTER);
        //Finally
        setupEvents();
    }
    public void focusEditor(){
        textEditor.getCodeText().requestFocusInWindow();
    }
}
