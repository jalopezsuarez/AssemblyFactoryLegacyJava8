package com.assembly.ui.controls.table;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import com.assembly.ui.themes.AlignmentStyle;
import com.assembly.ui.themes.BorderStyle;
import com.assembly.ui.themes.PositionStyle;
import com.assembly.ui.themes.ThemeStyle;

public class HeaderCellFactory extends JLabel implements TableCellRenderer
{

    private static final long serialVersionUID = 5743937874763901931L;

    private ArrayList<ThemeStyle> stylize;

    public void stylize(ArrayList<ThemeStyle> columnStyles)
    {
        this.stylize = columnStyles;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        ThemeStyle style = stylize.get(column);

        // -------------------------------------------------------
        setText((value == null) ? "" : value.toString());

        // -------------------------------------------------------
        setOpaque(true);
        setForeground(style.getHeaderForeColor());
        setBackground(style.getHeaderBackColor());

        // -------------------------------------------------------
        if (style.getHeaderTextAlign() == AlignmentStyle.LeftTop || style.getHeaderTextAlign() == AlignmentStyle.LeftCenter || style.getHeaderTextAlign() == AlignmentStyle.LeftBottom)
        {
            setHorizontalAlignment(SwingConstants.LEFT);
            setAlignmentX(Component.LEFT_ALIGNMENT);

            if (style.getHeaderTextAlign() == AlignmentStyle.LeftTop)
            {
                setVerticalAlignment(SwingConstants.TOP);
            }
            else if (style.getHeaderTextAlign() == AlignmentStyle.LeftCenter)
            {
                setVerticalAlignment(SwingConstants.CENTER);
            }
            else if (style.getHeaderTextAlign() == AlignmentStyle.LeftBottom)
            {
                setVerticalAlignment(SwingConstants.BOTTOM);
            }
        }
        else if (style.getHeaderTextAlign() == AlignmentStyle.CenterTop || style.getHeaderTextAlign() == AlignmentStyle.CenterCenter || style.getHeaderTextAlign() == AlignmentStyle.CenterBottom)
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setAlignmentX(Component.CENTER_ALIGNMENT);

            if (style.getHeaderTextAlign() == AlignmentStyle.CenterTop)
            {
                setVerticalAlignment(SwingConstants.TOP);
            }
            else if (style.getHeaderTextAlign() == AlignmentStyle.CenterCenter)
            {
                setVerticalAlignment(SwingConstants.CENTER);
            }
            else if (style.getHeaderTextAlign() == AlignmentStyle.CenterBottom)
            {
                setVerticalAlignment(SwingConstants.BOTTOM);
            }
        }
        else if (style.getHeaderTextAlign() == AlignmentStyle.RightTop || style.getHeaderTextAlign() == AlignmentStyle.RightCenter || style.getHeaderTextAlign() == AlignmentStyle.RightBottom)
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setAlignmentX(Component.RIGHT_ALIGNMENT);

            if (style.getHeaderTextAlign() == AlignmentStyle.RightTop)
            {
                setVerticalAlignment(SwingConstants.TOP);
            }
            else if (style.getHeaderTextAlign() == AlignmentStyle.RightCenter)
            {
                setVerticalAlignment(SwingConstants.CENTER);
            }
            else if (style.getHeaderTextAlign() == AlignmentStyle.RightBottom)
            {
                setVerticalAlignment(SwingConstants.BOTTOM);
            }
        }

        // -------------------------------------------------------
        BorderStyle styleBorderTop = style.getHeaderBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = style.getHeaderBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = style.getHeaderBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = style.getHeaderBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border emptyBorder = new EmptyBorder(style.getHeaderPadding().top, style.getHeaderPadding().left, style.getHeaderPadding().bottom, style.getHeaderPadding().right);

        Border cellBorder = new CompoundBorder(colorBorder, emptyBorder);
        setBorder(cellBorder);

        // -------------------------------------------------------
        setFont(style.getHeaderFont());

        // -------------------------------------------------------
        if (style.getHeaderTextAlign() == AlignmentStyle.LeftTop && style.getHeaderTextAlign() == AlignmentStyle.LeftCenter && style.getHeaderTextAlign() == AlignmentStyle.LeftBottom)
        {
            setHorizontalAlignment(SwingConstants.LEFT);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        else if (style.getHeaderTextAlign() == AlignmentStyle.CenterTop && style.getHeaderTextAlign() == AlignmentStyle.CenterCenter && style.getHeaderTextAlign() == AlignmentStyle.CenterBottom)
        {
            setHorizontalAlignment(SwingConstants.CENTER);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        else if (style.getHeaderTextAlign() == AlignmentStyle.RightTop && style.getHeaderTextAlign() == AlignmentStyle.RightCenter && style.getHeaderTextAlign() == AlignmentStyle.RightBottom)
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        // -------------------------------------------------------
        return this;
    }
}
