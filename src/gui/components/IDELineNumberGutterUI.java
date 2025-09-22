package gui.components;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.JScrollPane;

public class IDELineNumberGutterUI extends JPanel {
    private JTextArea textArea;
    public IDELineNumberGutterUI(JTextArea textArea){
        this.textArea = textArea;
        setPreferredSize(new Dimension(40, textArea.getHeight()));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Font Preset
        FontMetrics fM = textArea.getFontMetrics(textArea.getFont());
        int descent = fM.getDescent();
        //Line Preset
        int lines = textArea.getLineCount();
        int lineHeight = fM.getHeight();
        //Visible Lines Preset
        int visibleLines = textArea.getHeight()/lineHeight;
        //Draw numbers
        for(int i=0; i<visibleLines&&i<lines; i++){
            int lineNumber = i+1;
            int y=(i+1)*lineHeight-descent;
            g.drawString(String.valueOf(lineNumber), 10, y);
        }
    }
}
