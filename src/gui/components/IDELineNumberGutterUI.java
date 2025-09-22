package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class IDELineNumberGutterUI extends JPanel {
    private Color line_color;
    private JTextArea textArea;
    public IDELineNumberGutterUI(JTextArea textArea){
        this.textArea = textArea;
        this.line_color = Color.BLACK;
        setPreferredSize(new Dimension(60, Integer.MAX_VALUE));
    }
    public void setLineColor(Color line_color) {
        this.line_color = line_color;
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
        //Gutter
        int gutterWidth = getWidth();
        //Draw numbers
        g.setColor(line_color);
        for(int i=0; i<visibleLines&&i<lines; i++){
            int lineNumber = i+1;
            int numStr = fM.stringWidth(String.valueOf(lineNumber));
            int x=gutterWidth-numStr-10;
            int y=(i+1)*lineHeight-descent;
            g.drawString(String.valueOf(lineNumber), x, y);
        }
    }
}
