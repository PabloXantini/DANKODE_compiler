package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.border.EmptyBorder;

public class Window extends JFrame {
    private static final long serialVersionUID = 1L;
    private GUI gui;
    public Window(){
        setTitle("DANKIDE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(50, 50, 800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        gui = new GUI();
        setContentPane(gui);
    }
    //This a module test, should not execute as entrancy point
    public static void main(String[] arguments){
        EventQueue.invokeLater(new Runnable(){
            public void run(){
                try{
                    Window window = new Window();
                    window.setVisible(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
