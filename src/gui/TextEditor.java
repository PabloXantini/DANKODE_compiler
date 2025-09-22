package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gui.components.IDEScrollbarUI;

public class TextEditor extends JPanel{
    //Components
    private JTextArea codeText;
    private JScrollPane scrollPane;
    //Component properties
    //CodeText
    private Color background;
    private Color bgcolor_scroll;
    private Color color_scroll;
    private Color fontColor;
    private int fontsize = 16*5;
    //Method Stuff
    private void styleCodeEditor(){
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
        codeText.setBackground(background);
        codeText.setFont(new Font("Consolas", Font.PLAIN, fontsize));
        // vertical.setBackground(bgcolor_scroll);
        // vertical.setForeground(bgcolor_scroll);
        // horizontal.setBackground(bgcolor_scroll);
        // horizontal.setForeground(bgcolor_scroll);
        //Thumb
        IDEScrollbarUI vscrollUI = new IDEScrollbarUI();
        IDEScrollbarUI hscrollUI = new IDEScrollbarUI();
        vscrollUI.setTrackColor(bgcolor_scroll);
        vscrollUI.setThumbColor(color_scroll);
        hscrollUI.setTrackColor(bgcolor_scroll);
        hscrollUI.setThumbColor(color_scroll);
        vertical.setUI(vscrollUI);
        horizontal.setUI(hscrollUI);
    } 
    TextEditor(){
        background = new Color(204, 255, 255);
        bgcolor_scroll = new Color(128,128,128);
        color_scroll = new Color(200,200,200);
        codeText = new JTextArea();
        scrollPane = new JScrollPane(codeText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //Structuring
        setFocusable(true);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        //Styles
        setBackground(background);
        styleCodeEditor();
    }
    public JTextArea getCodeText(){
        return this.codeText;
    }
}
