package com.assembly.ui.controls.table;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import com.assembly.ui.themes.AlignmentStyle;
import com.assembly.ui.themes.BorderStyle;
import com.assembly.ui.themes.PositionStyle;
import com.assembly.ui.themes.ThemeStyle;

class DefaultCellFactory extends JTextArea implements TableCellRenderer
{

    private static final long serialVersionUID = 8125105261493200231L;
    private final ArrayList<ArrayList<Integer>> rowAndCellHeightList = new ArrayList<>();

    private ArrayList<ThemeStyle> stylize;

    public void stylize(ArrayList<ThemeStyle> stylize)
    {
        this.stylize = stylize;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        ThemeStyle style = stylize.get(column);

        // =======================================================
        if (isSelected)
        {
            setForeground(style.getForeColorSelected());
            setBackground(style.getBackColorSelected());
            setBackground(style.getBackColorSelected());
        }
        else
        {
            setForeground(style.getForeColor());
            setBackground((row % 2 == 0) ? style.getBackColorAlternate() : style.getBackColor());
            setBackground((row % 2 == 0) ? style.getBackColorAlternate() : style.getBackColor());
        }

        // -------------------------------------------------------
        if (style.getTextAlign() == AlignmentStyle.RightTop || style.getTextAlign() == AlignmentStyle.RightCenter || style.getTextAlign() == AlignmentStyle.RightBottom)
        {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        else
        {
            setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        // -------------------------------------------------------
        BorderStyle styleBorderTop = style.getBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = style.getBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = style.getBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = style.getBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border emptyBorder = new EmptyBorder(style.getPadding().top, style.getPadding().left, style.getPadding().bottom, style.getPadding().right);

        Border cellBorder = new CompoundBorder(colorBorder, emptyBorder);
        setBorder(cellBorder);

        // -------------------------------------------------------
        setLineWrap(style.isWordWrap());
        setWrapStyleWord(style.isWordWrap());

        // -------------------------------------------------------
        setFont(style.getFont());

        // =======================================================
        setText(Objects.toString(value, ""));
        adjustRowHeight(table, row, column);
        return this;
    }

    private void adjustRowHeight(JTable table, int row, int column)
    {
        setBounds(table.getCellRect(row, column, false));

        int preferredHeight = getPreferredSize().height;
        while (rowAndCellHeightList.size() <= row)
        {
            rowAndCellHeightList.add(new ArrayList<>(column));
        }
        ArrayList<Integer> cellHeightList = rowAndCellHeightList.get(row);
        while (cellHeightList.size() <= column)
        {
            cellHeightList.add(0);
        }
        cellHeightList.set(column, preferredHeight);
        int max = cellHeightList.stream().max(Integer::compare).get();
        if (table.getRowHeight(row) != max)
        {
            table.setRowHeight(row, max);
        }
    }

}
