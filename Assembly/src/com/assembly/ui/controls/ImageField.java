package com.assembly.ui.controls;

import com.assembly.core.commons.FileHelper;
import java.lang.reflect.Field;
import javax.imageio.ImageIO;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.BorderFactory;

import com.assembly.ui.themes.AlignmentStyle;
import com.assembly.ui.themes.BorderStyle;
import com.assembly.ui.themes.PositionStyle;
import com.assembly.ui.themes.ThemeStyle;
import com.assembly.core.resources.RelativeResource;
import com.assembly.core.trace.Trace;

public class ImageField extends JPanel
{
    private static final long serialVersionUID = 5613929516074740632L;

    private static final String ReferenceResources = "/res/assets/";
    private ThemeStyle stylize = new ThemeStyle();

    private String source;
    private BufferedImage image;
    private ImageIcon size;

    public ImageField()
    {
        source = "";
        image = null;
        size = null;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public void setResource(String resource)
    {
        try
        {
            if (source != null && resource != null && !source.equalsIgnoreCase(resource))
            {
                source = resource;

                if (scale() > 1)
                {
                    String relativeResource = FileHelper.buildResource(ReferenceResources, source) + "@2x.png";
                    image = ImageIO.read(RelativeResource.instance().read(relativeResource));
                }
                else
                {
                    String relativeResource = FileHelper.buildResource(ReferenceResources, source) + ".png";
                    image = ImageIO.read(RelativeResource.instance().read(relativeResource));
                }
                size = new ImageIcon(image.getScaledInstance(-1, image.getHeight() / scale(), Image.SCALE_DEFAULT));

                Dimension dimension = new Dimension(size.getIconWidth(), size.getIconHeight());
                setMinimumSize(dimension);
                setMaximumSize(dimension);
                setPreferredSize(dimension);
                setSize(dimension);
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    public void stylize(ThemeStyle style)
    {
        stylize = style;

        // -------------------------------------------------------
        setBackground(stylize.getBackColor());
        setOpaque(!stylize.isTransparentBackground());

        // -------------------------------------------------------
        if (stylize.getTextAlign() == AlignmentStyle.LeftTop || stylize.getTextAlign() == AlignmentStyle.LeftCenter || stylize.getTextAlign() == AlignmentStyle.LeftBottom)
        {
            setAlignmentX(Component.LEFT_ALIGNMENT);

            if (stylize.getTextAlign() == AlignmentStyle.LeftTop)
            {
                setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.LeftCenter)
            {
                setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.LeftBottom)
            {
                setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }
        else if (stylize.getTextAlign() == AlignmentStyle.CenterTop || stylize.getTextAlign() == AlignmentStyle.CenterCenter || stylize.getTextAlign() == AlignmentStyle.CenterBottom)
        {
            setAlignmentX(Component.CENTER_ALIGNMENT);

            if (stylize.getTextAlign() == AlignmentStyle.CenterTop)
            {
                setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.CenterCenter)
            {
                setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.CenterBottom)
            {
                setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }
        else if (stylize.getTextAlign() == AlignmentStyle.RightTop || stylize.getTextAlign() == AlignmentStyle.RightCenter || stylize.getTextAlign() == AlignmentStyle.RightBottom)
        {
            setAlignmentX(Component.RIGHT_ALIGNMENT);

            if (stylize.getTextAlign() == AlignmentStyle.RightTop)
            {
                setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.RightCenter)
            {
                setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.RightBottom)
            {
                setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }

        // -------------------------------------------------------
        BorderStyle styleBorderTop = stylize.getBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = stylize.getBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = stylize.getBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = stylize.getBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border emptyBorder = new EmptyBorder(stylize.getPadding().top, stylize.getPadding().left, stylize.getPadding().bottom, stylize.getPadding().right);

        Border styleBorder = new CompoundBorder(colorBorder, emptyBorder);
        setBorder(styleBorder);

        // -------------------------------------------------------
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        try
        {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.drawImage(image, 0, 0, size.getIconWidth(), size.getIconHeight(), null);
            super.paintComponent(g2);
        }
        catch (Exception ex)
        {
            super.paintComponent(g);
        }
    }

    private int scale()
    {
        int scaleValue = 1;
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        final java.awt.GraphicsDevice device = env.getDefaultScreenDevice();
        try
        {
            Field field = device.getClass().getDeclaredField("scale");
            if (field != null)
            {
                field.setAccessible(true);
                Object scale = field.get(device);
                if (scale instanceof Integer && (Integer) scale == 2)
                {
                    scaleValue = ((Integer) scale).intValue();
                }
            }
        }
        catch (Exception ex)
        {
        }
        return scaleValue;
    }

}
