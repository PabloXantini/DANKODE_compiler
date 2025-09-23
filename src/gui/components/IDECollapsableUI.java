package gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class IDECollapsableUI extends JPanel {
    private JButton Header;
    private JPanel Container;
    public IDECollapsableUI(){
        Header = new JButton();
        Container = new JPanel();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Header);
        add(Container);
        //Event
        Header.addActionListener(new ToggleEvent(Container));
    }
    public JButton getHeader() {
        return Header;
    }
    public JPanel getContent() {
        return Container;
    }
    public void add(JComponent component){
        Container.add(component);
    }
}

class ToggleEvent implements ActionListener {
    private boolean state = false;
    private JComponent target;
    public ToggleEvent(JComponent target){
        this.target=target;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(state){
            state=false;
            target.setVisible(state);
        }else{
            state=true;
            target.setVisible(state);
        }
    }

}