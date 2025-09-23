package ide.actions;//???? DONOTUSEPLS

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

enum State{
    ACTIVE,
    INACTIVE
}

public class ToggleSection implements ActionListener {
    private State state = State.INACTIVE;
    private JComponent component;
    public ToggleSection(JComponent target){
        this.component=target;
    }
    private void toggle(){
        switch(state){
            case ACTIVE:
                component.setVisible(false);
                state = State.INACTIVE;
                break;
            case INACTIVE:
                component.setVisible(true);
                state = State.ACTIVE;
                break;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        toggle();
    }
    
}
