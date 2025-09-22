package gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class IDEScrollbarUI extends BasicScrollBarUI {
    private Color thumb_color;
    private Color track_color;
    public IDEScrollbarUI(){
    }
    public void setThumbColor(Color thumb_color) {
        this.thumb_color = thumb_color;
    }
    public void setTrackColor(Color track_color) {
        this.track_color = track_color;
    }
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor=thumb_color;
        this.thumbHighlightColor=thumb_color;
        this.trackColor=track_color;
    }
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        super.paintThumb(g, c, thumbBounds);
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
        g.setColor(thumbColor);
    }
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        super.paintTrack(g, c, trackBounds);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g.setColor(trackColor);
    }
}
