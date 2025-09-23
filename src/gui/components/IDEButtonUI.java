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
    private int padding_top = 0;
    private int padding_bottom = 0;
    private int padding_left = 0;
    private int padding_right = 0;
    private Image image;
    private ImageObserver img_observer;
    //Method stuff
    /* 
     private float getScaleFit(int org_w, int org_h, int d_w, int d_h){
        float scale = Math.min((float)d_w/org_w, (float)d_h/org_h);
        return scale;
    }
    */
    public IDEButtonUI(ImageIcon icon){
        super();
        image = icon.getImage();
        img_observer = icon.getImageObserver();
    }
    public void setPadding(int padding){
        this.padding_bottom = padding;
        this.padding_left = padding;
        this.padding_top = padding;
        this.padding_right = padding;
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
        if(image!=null){
            int img_w = getWidth();
            int img_h = getHeight();
            int w = img_w-padding_left-padding_right;
            int h = img_h-padding_top-padding_bottom; 
            g.drawImage(image, padding_left, padding_top, w, h, img_observer);
        }
    }
}
