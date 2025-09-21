package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class SideBar extends JPanel{
    //Colors
    private Color background;
    private Color colbtn1;
    //Components
    private JButton fileManagerBtn;
    //Method Stuff
    private void styleSideBarButton(JButton btn){
        Dimension btnDimension = new Dimension(50, 50);
        btn.setBackground(colbtn1);
        btn.setPreferredSize(btnDimension);
        btn.setMinimumSize(btnDimension);
        btn.setMaximumSize(btnDimension);
    }
    SideBar(){
        background = new Color(102, 178, 255);
        colbtn1 = new Color(255, 255, 255);
        fileManagerBtn = new JButton();
        //Structuring
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(fileManagerBtn);
        //Styles
        setBackground(background);
        styleSideBarButton(fileManagerBtn);
    }    
}
