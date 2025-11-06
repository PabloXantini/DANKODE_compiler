package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gui.components.IDEScrollbarUI;
import ide.IDE;
import gui.components.IDELineNumberGutterUI;

public class TextEditor extends JPanel{
    //Context
	private IDE context;
	//Components
    private JTextArea codeText;
    private IDELineNumberGutterUI lineCounter;
    private JScrollPane scrollPane;
    //Component properties
    //CodeText
    private Color background;
    private Color bgcolor_scroll;
    private Color color_scroll;
    private Color fontColor;
    private Color lfontColor;
    private int fontsize = 16;
    //Method Stuff
    private void styleCodeEditor(){
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
        codeText.setBorder(null);
        codeText.setBackground(background);
        codeText.setForeground(fontColor);
        codeText.setFont(new Font("Consolas", Font.PLAIN, fontsize));
        scrollPane.setBorder(null);
        lineCounter.setBackground(background);
        lineCounter.setLineColor(lfontColor);
        IDEScrollbarUI vscrollUI = new IDEScrollbarUI();
        IDEScrollbarUI hscrollUI = new IDEScrollbarUI();
        vscrollUI.setTrackColor(bgcolor_scroll);
        vscrollUI.setThumbColor(color_scroll);
        hscrollUI.setTrackColor(bgcolor_scroll);
        hscrollUI.setThumbColor(color_scroll);
        vertical.setUnitIncrement(6);
        horizontal.setUnitIncrement(6);
        vertical.setUI(vscrollUI);
        horizontal.setUI(hscrollUI);
    } 
    TextEditor(IDE context){
    	this.context = context;
    	context.setEditor(this);
        background = new Color(204, 255, 255);
        bgcolor_scroll = new Color(128,128,128);
        color_scroll = new Color(200,200,200);
        fontColor = new Color(0,0,0);
        lfontColor = new Color(64,64,64);
        codeText = new JTextArea();
        lineCounter = new IDELineNumberGutterUI(codeText);
        scrollPane = new JScrollPane(codeText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //Structuring
        setFocusable(true);
        setLayout(new BorderLayout());
        scrollPane.setRowHeaderView(lineCounter);
        add(scrollPane, BorderLayout.CENTER);
        //Styles
        setBorder(null);
        setBackground(background);
        styleCodeEditor();
        //Event Listeners
        codeText.getDocument().addDocumentListener(new EditorEvent(this));
    }
    public JTextArea getCodeText(){
        return this.codeText;
    }
    public IDELineNumberGutterUI getGutter(){
        return this.lineCounter;
    }
    
}

class EditorEvent implements DocumentListener {
    TextEditor editorContext;
    public EditorEvent(){
    }
    public EditorEvent(TextEditor editor){
        this.editorContext = editor;
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
        this.editorContext.getGutter().repaint();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
        this.editorContext.getGutter().repaint();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub
    	//this.editorContext.getGutter().repaint();
    }
    
}