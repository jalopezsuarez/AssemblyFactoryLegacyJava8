/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.ui.controls;

import com.assembly.ui.themes.AlignmentStyle;
import com.assembly.ui.themes.BorderStyle;
import com.assembly.ui.themes.PositionStyle;
import com.assembly.ui.themes.ThemeStyle;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Administrator
 */
public class LabelField extends JLabel
{

    private static final long serialVersionUID = -7335903246837874747L;

    private ThemeStyle stylize = new ThemeStyle();

    private String styleHTMLBegin = "";
    private String styleHTMLText = "";
    private String styleHTMLEnd = "";

    public LabelField()
    {
    }

    public void stylize(ThemeStyle style)
    {
        this.stylize = style;

        // -------------------------------------------------------
        setFont(stylize.getFont());
        setForeground(stylize.getForeColor());
        setBackground(stylize.getBackColor());
        setOpaque(!stylize.isTransparentBackground());

        // -------------------------------------------------------
        if (stylize.isWordWrap())
        {
            styleHTMLBegin = "<html><body>";
            styleHTMLEnd = "</p></body></html>";
        }
        else
        {
            styleHTMLBegin = "";
            styleHTMLEnd = "";
        }

        // -------------------------------------------------------
        if (stylize.getTextAlign() == AlignmentStyle.LeftTop || stylize.getTextAlign() == AlignmentStyle.LeftCenter || stylize.getTextAlign() == AlignmentStyle.LeftBottom)
        {
            setHorizontalAlignment(SwingConstants.LEFT);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            styleHTMLText = "<p style='text-align:left'>";

            if (stylize.getTextAlign() == AlignmentStyle.LeftTop)
            {
                setVerticalAlignment(SwingConstants.TOP);
                setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.LeftCenter)
            {
                setVerticalAlignment(SwingConstants.CENTER);
                setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.LeftBottom)
            {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }
        else if (stylize.getTextAlign() == AlignmentStyle.CenterTop || stylize.getTextAlign() == AlignmentStyle.CenterCenter || stylize.getTextAlign() == AlignmentStyle.CenterBottom)
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            styleHTMLText = "<p style='text-align:center'>";

            if (stylize.getTextAlign() == AlignmentStyle.CenterTop)
            {
                setVerticalAlignment(SwingConstants.TOP);
                setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.CenterCenter)
            {
                setVerticalAlignment(SwingConstants.CENTER);
                setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.CenterBottom)
            {
                setVerticalAlignment(SwingConstants.BOTTOM);
                setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }
        else if (stylize.getTextAlign() == AlignmentStyle.RightTop || stylize.getTextAlign() == AlignmentStyle.RightCenter || stylize.getTextAlign() == AlignmentStyle.RightBottom)
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setAlignmentX(Component.RIGHT_ALIGNMENT);
            styleHTMLText = "<p style='text-align:right'>";

            if (stylize.getTextAlign() == AlignmentStyle.RightTop)
            {
                setVerticalAlignment(SwingConstants.TOP);
                setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.RightCenter)
            {
                setVerticalAlignment(SwingConstants.CENTER);
                setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (stylize.getTextAlign() == AlignmentStyle.RightBottom)
            {
                setVerticalAlignment(SwingConstants.BOTTOM);
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
    }

    @Override
    public void setText(String text)
    {
        StringBuilder string = new StringBuilder();
        if (styleHTMLBegin != null && styleHTMLText != null && styleHTMLEnd != null)
        {
            if (!styleHTMLBegin.isEmpty() && !styleHTMLText.isEmpty() && !styleHTMLEnd.isEmpty())
            {
                string.append(styleHTMLBegin);
                string.append(styleHTMLText);
            }
        }

        string.append(text);

        if (styleHTMLBegin != null && styleHTMLText != null && styleHTMLEnd != null)
        {
            if (!styleHTMLBegin.isEmpty() && !styleHTMLText.isEmpty() && !styleHTMLEnd.isEmpty())
            {
                string.append(styleHTMLEnd);
            }
        }

        super.setText(string.toString());
    }

}
