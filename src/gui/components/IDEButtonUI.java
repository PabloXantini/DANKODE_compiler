package gui.components;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class IDEButtonUI extends JButton{
    private Image image;
    private ImageObserver img_observer;
    public IDEButtonUI(ImageIcon icon){
        super();
        image = icon.getImage();
        img_observer = icon.getImageObserver();
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), img_observer);
    }
}
