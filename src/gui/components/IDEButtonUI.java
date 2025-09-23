package gui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
    public void setIconColor(Color color){
        //Post-process the image
        BufferedImage buffered_img = new BufferedImage(image.getWidth(img_observer), image.getHeight(img_observer), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered_img.createGraphics();
        g2d.drawImage(image, 0, 0, img_observer);
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(color);
        g2d.fillRect(0, 0, buffered_img.getWidth(), buffered_img.getHeight());
        g2d.dispose();
        image = buffered_img;
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), img_observer);
    }
}
