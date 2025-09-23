package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import gui.components.IDEButtonUI;

public class FileMenuBar extends JPanel{
    //Colors
    private Color background;
    private Color colbtn1;
    private Color colicon;
    //Icons
    private ImageIcon icon1;
    //Components
    private IDEButtonUI btnCompile;
    //Method stuff
    private void styleMenuButton(IDEButtonUI btn){
        btn.setBackground(colbtn1);
        btn.setPreferredSize(new Dimension(30,30));
        btn.setBorder(null);
        btn.setIconColor(colicon);
    }
    public FileMenuBar(){
        background = new Color(255, 255, 255);
        colbtn1 = new Color(255, 255, 255);
        colicon = new Color(102, 178, 255);
        icon1 = new ImageIcon("res/icons/compile.png");
        btnCompile = new IDEButtonUI(icon1);
        //Structuring
        setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        add(btnCompile);
        //Styles
        setBackground(background);
        setBorder(null);
        styleMenuButton(btnCompile);
        //Events
        btnCompile.setToolTipText("Compilar y Analizar");
    }
    public JButton getBtnCompile(){
        return this.btnCompile;
    }
}
