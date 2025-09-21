package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MenuBar extends JPanel{
    private Color colbtn1;
    private Color background;
    private JButton btnCompile;
    private void styleMenuButton(JButton btn){
        btn.setBackground(colbtn1);
        btn.setPreferredSize(new Dimension(40,40));
    }
    MenuBar(){
        background = new Color(255, 255, 255);
        colbtn1 = new Color(255, 255, 255);
        btnCompile = new JButton();
        styleMenuButton(btnCompile);
        setBackground(background);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        add(btnCompile);
    }
}
