package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import gui.components.IDEButtonUI;
import gui.sections.OutputViewer;
import gui.sections.SymbolTableViewer;
import ide.IDE;

public class SideBar extends JPanel{
	//Context
	private IDE context;
    //Colors
    private Color background;
    private Color border_color;
    //private Color colbtn1;
    private Color colicon;
    //Icons
    private ImageIcon icon1;
    //Components
    private JPanel panelIDETools;
    private JPanel panelContent;
    //There must be here a handler
    private SymbolTableViewer symbolViewer;
    private OutputViewer outViewer;
    //

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
    SideBar(IDE context){
    	this.context = context;
        background = new Color(102, 178, 255);
        border_color = new Color(153, 194, 255);
        //colbtn1 = new Color(255, 255, 255);
        colicon = new Color(173,224,255);
        icon1 = new ImageIcon("res/icons/analysis.png");
        panelIDETools = new JPanel();
        panelContent = new JPanel();
        //btnFileManager = new JButton();
        btnDebugAnalysis = new IDEButtonUI(icon1);
        symbolViewer = new SymbolTableViewer();
        outViewer = new OutputViewer();
        context.setSymbolTableViewer(symbolViewer);
        context.setOutputViewer(outViewer);
        //Structuring
        panelIDETools.setLayout(new BoxLayout(panelIDETools, BoxLayout.Y_AXIS));
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());
        add(panelIDETools, BorderLayout.WEST);
        add(panelContent, BorderLayout.EAST);
        //add(btnFileManager);
        panelIDETools.add(btnDebugAnalysis);
        panelContent.add(symbolViewer);
        panelContent.add(outViewer);
        //Styles
        panelIDETools.setBackground(background);
        panelIDETools.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, border_color));
        panelIDETools.setPreferredSize(new Dimension(50, 0));
        panelContent.setBackground(background);
        panelContent.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, border_color));
        panelContent.setPreferredSize(new Dimension(250, 0));
        panelContent.setVisible(true);
        styleSideBarButton(btnDebugAnalysis);
        //Events
        btnDebugAnalysis.setToolTipText("Analizar depuración y compilación");
    }    
}
