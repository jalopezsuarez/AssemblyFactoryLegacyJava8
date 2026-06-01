package com.assembly.ui.controls;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;

import com.assembly.ui.themes.ThemeStyle;
import com.assembly.core.trace.Trace;

public class CheckBox extends JCheckBox
{
    private static final long serialVersionUID = 5613529526074740632L;

    private static final String ReferenceResources = "/res/assets/";
    private ThemeStyle stylize = new ThemeStyle();

    public CheckBox()
    {
        super();

        try
        {
            String resourceChecked = "checkon";
            String resourceUncheck = "checkoff";

            BufferedImage imageChecked = ImageIO.read(getClass().getResourceAsStream(ReferenceResources + resourceChecked + ".png"));
            BufferedImage imageUncheck = ImageIO.read(getClass().getResourceAsStream(ReferenceResources + resourceUncheck + ".png"));

            setSelectedIcon(new ImageIcon(imageChecked));
            setIcon(new ImageIcon(imageUncheck));
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    @Override
    public void updateUI()
    {
        super.updateUI();
        setFocusPainted(false);
    }

    public void stylize(ThemeStyle style)
    {
        stylize = style;

        setFont(stylize.getFont());
        setForeground(stylize.getForeColor());
        setBackground(stylize.getBackColor());

        Border emptyBorder = new EmptyBorder(stylize.getPadding().top, stylize.getPadding().left, stylize.getPadding().bottom, stylize.getPadding().right);
        setBorder(emptyBorder);
    }

}
