package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import gui.sections.ProblemsViewer;
import ide.IDE;

public class BottomInfoView extends JPanel {
	//Context
	private IDE context;
	//private JPanel content;
	//Components
	private ProblemsViewer problemsViewer;
	public BottomInfoView(IDE context){
		this.context = context;
		setLayout(new BorderLayout(0,0));
		setPreferredSize(new Dimension(0, 0));
		problemsViewer = new ProblemsViewer();
		context.setProblemViewer(problemsViewer);
		//Structuring
		add(problemsViewer, BorderLayout.CENTER);
	}
}
