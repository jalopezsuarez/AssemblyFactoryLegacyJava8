package com.assembly.ui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ScrollBarUI extends BasicScrollBarUI
{

    private static final Color TRACK_BACKGROUND_COLOR = Color.decode("#f0f0f0");
    private static final Color THUMB_DEFAULT_COLOR = Color.decode("#c1c1c1");
    private static final Color THUMB_ROLLOVER_COLOR = Color.decode("#c1c1c1");
    private static final Color THUMB_DRAGGING_COLOR = Color.decode("#808080");

    private static final int TRACK_WIDTH = 18;
    private static final int THUMB_WIDTH = 18;

    @Override
    public Dimension getPreferredSize(JComponent c)
    {
        Dimension d = super.getPreferredSize(c); //To change body of generated methods, choose Tools | Templates.
        d.width = TRACK_WIDTH;
        d.height = TRACK_WIDTH;
        return d;
    }

    @Override
    protected JButton createIncreaseButton(int orientation)
    {
        return new ZeroSizeButton();
    }

    @Override
    protected JButton createDecreaseButton(int orientation)
    {
        return new ZeroSizeButton();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r)
    {
        g.setColor(TRACK_BACKGROUND_COLOR);
        g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r)
    {
        Color color;
        if (isDragging)
        {
            color = THUMB_DRAGGING_COLOR;
        }
        else if (isThumbRollover())
        {
            color = THUMB_ROLLOVER_COLOR;
        }
        else
        {
            color = THUMB_DEFAULT_COLOR;
        }
        int position = Math.max(0, Math.min(r.width, TRACK_WIDTH) / 2 - Math.min(r.width, THUMB_WIDTH) / 2);

        Graphics2D graphics2D = (Graphics2D) g.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(color);
        graphics2D.fillRect(r.x + position, r.y, Math.min(r.width, THUMB_WIDTH), r.height);
        graphics2D.dispose();
    }

    private static class ZeroSizeButton extends JButton
    {

        private static final Dimension ZERO_SIZE = new Dimension();

        @Override
        public Dimension getPreferredSize()
        {
            return ZERO_SIZE;
        }
    }

}
