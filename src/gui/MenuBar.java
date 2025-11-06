package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import ide.IDE;
import ide.actions.file.FileActions;

public class MenuBar extends JPanel{
	//Context
	private IDE context;
    //Colors
    private Color background;
    private Color border_color;
    private Color colbtn1;
    //Components
    private JPanel panelIDETools;
    private JPanel panelWinTools;
    private JButton btnArchive;
    private JButton btnMin;
    private JButton btnMax;
    private JButton btnClose;
    //Method stuff
    private void styleMenuButton(JButton btn){
        btn.setBackground(colbtn1);
    }
    private void styleWindowButton(JButton btn){
        btn.setPreferredSize(new Dimension(40,30));
    }
    private void setupEvents() {
    	btnArchive.addActionListener(new FileActions(context, btnArchive));
    }
    public MenuBar(IDE context){
    	this.context = context;
        background = new Color(102, 178, 255);
        border_color = new Color(120, 190, 255);
        colbtn1 = new Color(255,255,255);
        btnArchive = new JButton("Archivo");
        panelIDETools = new JPanel();
        panelWinTools = new JPanel();
        btnMin = new JButton(); //Minimizar
        btnMax = new JButton(); //Maximizar
        btnClose = new JButton(); //Cerrar
        //Structuring
        panelIDETools.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        panelWinTools.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
        setLayout(new BorderLayout());
        add(panelIDETools, BorderLayout.WEST);
        add(panelWinTools, BorderLayout.EAST);
        panelIDETools.add(btnArchive);
        panelWinTools.add(btnMin);
        panelWinTools.add(btnMax);
        panelWinTools.add(btnClose);
        //Styles
        panelIDETools.setBackground(background);
        panelWinTools.setBackground(background);
        setBackground(background);
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, border_color));
        styleMenuButton(btnArchive);
        styleWindowButton(btnMin);
        styleWindowButton(btnMax);
        styleWindowButton(btnClose);
        //Events
        setupEvents();
    }

}
