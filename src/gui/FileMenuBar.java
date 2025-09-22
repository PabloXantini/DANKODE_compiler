package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class FileMenuBar extends JPanel{
    //Colors
    private Color background;
    private Color colbtn1;
    //Components
    private JButton btnCompile;
    //Method stuff
    private void styleMenuButton(JButton btn){
        btn.setBackground(colbtn1);
        btn.setPreferredSize(new Dimension(40,40));
    }
    FileMenuBar(){
        background = new Color(255, 255, 255);
        colbtn1 = new Color(255, 255, 255);
        btnCompile = new JButton();
        //Structuring
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        add(btnCompile);
        //Styles
        setBackground(background);
        setBorder(null);
        styleMenuButton(btnCompile);
    }
}
