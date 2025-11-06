import java.awt.EventQueue;

import gui.Window;
import ide.IDE;

public class DANKIDE {
	private static Window app;
	private static IDE ide;
	public static void exec() {
	    EventQueue.invokeLater(new Runnable(){
	    	public void run(){
	    		try{
	    			ide = new IDE();
	    			app = new Window(ide);
	    			app.setVisible(true);
	    			app.getGUI().focusEditor();
	    		}catch (Exception e){
	    			e.printStackTrace();
	    		}
	        }
	    });
	}
	public static void main(String[] args) {
		//ide = new IDE();
		exec();
	}
}
