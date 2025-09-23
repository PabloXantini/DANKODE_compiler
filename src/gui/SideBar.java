package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import gui.components.IDEButtonUI;

public class SideBar extends JPanel{
    //Colors
    private Color background;
    private Color border_color;
    //private Color colbtn1;
    private Color colicon;
    //Icons
    private ImageIcon icon1;
    //Components
    //private JButton btnFileManager;
    private IDEButtonUI btnDebugAnalysis;
    //Method Stuff
    private void styleSideBarButton(IDEButtonUI btn){
        Dimension btnDimension = new Dimension(50, 50);
        btn.setBackground(background);
        btn.setPreferredSize(btnDimension);
        btn.setMinimumSize(btnDimension);
        btn.setMaximumSize(btnDimension);
        btn.setBorder(null);
        btn.setIconColor(colicon);
        btn.setPadding(8);
    }
    SideBar(){
        background = new Color(102, 178, 255);
        border_color = new Color(120, 190, 255);
        //colbtn1 = new Color(255, 255, 255);
        colicon = new Color(173,224,255);
        icon1 = new ImageIcon("res/icons/analysis.png");
        //btnFileManager = new JButton();
        btnDebugAnalysis = new IDEButtonUI(icon1);
        //Structuring
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //add(btnFileManager);
        add(btnDebugAnalysis);
        //Styles
        setBackground(background);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, border_color));
        //styleSideBarButton(btnFileManager);
        styleSideBarButton(btnDebugAnalysis);
    }    
}
