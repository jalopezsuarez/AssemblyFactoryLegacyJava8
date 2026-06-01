/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.ui.controls.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.UUID;
import java.lang.reflect.Field;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.assembly.ui.controls.ScrollBarUI;
import com.assembly.ui.themes.BorderStyle;
import com.assembly.ui.themes.PositionStyle;
import com.assembly.ui.themes.ThemeStyle;
import com.assembly.core.trace.Trace;

/**
 *
 * @author Administrator
 */
public class TableFactory
{

    private JScrollPane scroll = null;
    private JTable table = null;

    private HeaderCellFactory headerCellFactory = new HeaderCellFactory();
    private DefaultCellFactory defaultCellFactory = new DefaultCellFactory();

    private HashMap<Object, ThemeStyle> styles = new HashMap<Object, ThemeStyle>();
    private ArrayList<ThemeStyle> stylesCellFactory = new ArrayList<ThemeStyle>();

    public TableFactory(JScrollPane component, TableAdapter context)
    {
        scroll = (JScrollPane) component;

        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
        verticalScrollBar.setUI(new ScrollBarUI());
        JScrollBar horizontalScrollBar = scroll.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new ScrollBarUI());

        DefaultTableModel model = new DefaultTableModel()
        {
            private static final long serialVersionUID = -6910269759312857530L;

            public boolean isCellEditable(int nRow, int nCol)
            {
                return false;
            }
        };
        model.addColumn(UUID.randomUUID().toString());

        table = (JTable) scroll.getViewport().getView();
        table.setModel(model);

        table.setBorder(null);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false);

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        MouseAdapter mouseAdapter = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() > 0)
                {
                    if (e.getSource() instanceof JTable)
                    {
                        JTable table = (JTable) e.getSource();
                        int row = table.rowAtPoint(e.getPoint());
                        int col = table.columnAtPoint(e.getPoint());
                        if (table.contains(row, col))
                        {
                            table.setRowSelectionInterval(row, row);
                            table.setColumnSelectionInterval(col, col);

                            Object value = table.getValueAt(row, col);
                            context.tableSecondEvent(table, row, value);
                        }
                    }
                }
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
                {
                    if (e.getSource() instanceof JTable)
                    {
                        JTable table = (JTable) e.getSource();
                        int row = table.rowAtPoint(e.getPoint());
                        int col = table.columnAtPoint(e.getPoint());
                        if (table.contains(row, col))
                        {
                            table.setRowSelectionInterval(row, row);
                            table.setColumnSelectionInterval(col, col);

                            Object value = table.getValueAt(row, col);
                            context.tableDoubleEvent(table, row, value);
                        }
                    }
                }
                else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1)
                {
                    if (e.getSource() instanceof JTable)
                    {
                        JTable table = (JTable) e.getSource();
                        int row = table.rowAtPoint(e.getPoint());
                        int col = table.columnAtPoint(e.getPoint());
                        if (table.contains(row, col))
                        {
                            table.setRowSelectionInterval(row, row);
                            table.setColumnSelectionInterval(col, col);

                            Object value = table.getValueAt(row, col);
                            context.tableSingleEvent(table, row, value);
                        }
                    }
                }
                e.consume();
            }
        };
        table.addMouseListener(mouseAdapter);

        table.getTableHeader().addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                resizeTableColumnWidth();
            }
        });
    }

    public void stylize(ThemeStyle style)
    {
        scroll.setBackground(style.getBackColor());
        scroll.getViewport().setBackground(style.getBackColor());

        table.setBackground(style.getBackColor());
        table.getParent().setBackground(style.getBackColor());

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
        scroll.setBorder(cellBorder);
    }

    public void column(ThemeStyle style)
    {
        styles.put(style.getIdentifier(), style);
        stylesCellFactory.add(style);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addColumn(style.getIdentifier());

        headerCellFactory.stylize(stylesCellFactory);
        defaultCellFactory.stylize(stylesCellFactory);
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setHeaderRenderer(headerCellFactory);
            column.setCellRenderer(defaultCellFactory);
            if (styles.containsKey(column.getIdentifier()))
            {
                Object identifier = column.getIdentifier();
                column.setHeaderValue(styles.get(column.getIdentifier()));
                column.setIdentifier(identifier);
            }
        }

        table.removeColumn(table.getColumnModel().getColumn(0));
    }

    public void syncronize(ArrayList<?> instances)
    {
        try
        {
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            if (instances != null && instances.size() > 0)
            {
                for (int reference = 0; reference < instances.size(); reference++)
                {
                    Object instance = instances.get(reference);
                    boolean insertRow = true;
                    boolean updateRow = false;
                    int positionRow = 0;

                    if (instance instanceof Comparable)
                    {
                        Comparable comparable = (Comparable) instance;
                        for (int i = 0; i < model.getRowCount(); i++)
                        {
                            Comparable other = (Comparable) model.getValueAt(i, 0);
                            if (instance.getClass().equals(other.getClass()))
                            {
                                int compareTo = comparable.compareTo(other);
                                if (compareTo == 0)
                                {
                                    model.setValueAt(comparable, i, 0);
                                    insertRow = false;
                                    updateRow = true;
                                    break;
                                }
                                else
                                {
                                    positionRow = reference;
                                    insertRow = true;
                                    updateRow = false;
                                }
                            }
                        }
                    }
                    if (insertRow)
                    {
                        Vector<Object> rowData = new Vector<Object>();
                        rowData.add(instance);
                        Class<?> clazz = instance.getClass();
                        for (Field field : clazz.getDeclaredFields())
                        {
                            field.setAccessible(true);
                            if (styles.containsKey(field.getName()))
                            {
                                rowData.add(field.get(instance));
                            }
                        }
                        model.insertRow(positionRow, rowData);
                    }
                    if (updateRow && positionRow < model.getRowCount())
                    {
                        HashMap<String, Object> instanceData = new HashMap<String, Object>();
                        Class<?> clazz = instance.getClass();
                        for (Field field : clazz.getDeclaredFields())
                        {
                            field.setAccessible(true);
                            instanceData.put(field.getName(), (Object) field.get(instance));
                        }
                        for (int i = 0; i < model.getColumnCount(); i++)
                        {
                            Object referenceValue = (Object) model.getValueAt(positionRow, i);
                            if (instanceData.containsKey(model.getColumnName(i)))
                            {
                                Object instanceValue = instanceData.get(model.getColumnName(i));
                                if (referenceValue != null && !referenceValue.equals(instanceValue))
                                {
                                    model.setValueAt(instanceValue, positionRow, i);
                                    model.fireTableCellUpdated(positionRow, i);
                                }
                            }
                        }
                    }
                }

                for (int reference = 0; reference < model.getRowCount(); reference++)
                {
                    Comparable comparable = (Comparable) model.getValueAt(reference, 0);
                    boolean removeRow = false;
                    int positionRow = 0;

                    for (Object instance : instances)
                    {
                        if (instance instanceof Comparable)
                        {
                            Comparable other = (Comparable) instance;
                            if (instance.getClass().equals(other.getClass()))
                            {
                                int compareTo = comparable.compareTo(other);
                                if (compareTo == 0)
                                {
                                    removeRow = false;
                                    break;
                                }
                                else
                                {
                                    removeRow = true;
                                    positionRow = reference;
                                }
                            }
                        }
                    }
                    if (removeRow)
                    {
                        model.removeRow(positionRow);
                    }
                }
            }
            else
            {
                model.setRowCount(0);
            }
        }
        catch (Exception | Error ex)
        {
            Trace.printStackTrace(ex);
        }
    }

    private void resizeTableColumnWidth()
    {
        int cumulativeSpace = 0;
        HashMap<String, Integer> fillColumns = new HashMap<String, Integer>();
        HashMap<String, Integer> percentageColumns = new HashMap<String, Integer>();

        for (int column = 0; column < table.getColumnCount(); column++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            ThemeStyle style = styles.get(tableColumn.getIdentifier());

            if (style != null)
            {
                if (style.getWidth() > 0)
                {
                    int preferredWidth = Math.min(Math.max(style.getMinWidth(), style.getWidth()), style.getMaxWidth());
                    tableColumn.setPreferredWidth(preferredWidth);
                    cumulativeSpace += preferredWidth;
                }
                else if (style.getPercentageWidth() > 0)
                {
                    percentageColumns.put(style.getIdentifier(), -1);
                }
                else
                {
                    fillColumns.put(style.getIdentifier(), -1);
                }
            }
        }

        if (percentageColumns.size() > 0)
        {
            int fillSpace = (int) table.getParent().getSize().width - cumulativeSpace;

            for (int column = 0; column < table.getColumnCount(); column++)
            {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                ThemeStyle style = styles.get(tableColumn.getIdentifier());

                if (style != null && percentageColumns.containsKey(style.getIdentifier()))
                {
                    int fillWidth = (int) Math.floor((double) (style.getPercentageWidth() * fillSpace / 100));
                    int preferredWidth = Math.min(Math.max(style.getMinWidth(), fillWidth), style.getMaxWidth());
                    tableColumn.setPreferredWidth(preferredWidth);
                    cumulativeSpace += preferredWidth;
                }
            }
        }

        if (fillColumns.size() > 0)
        {
            int fillSpace = (int) table.getParent().getSize().width - cumulativeSpace;
            int fillWidth = (int) Math.floor((double) (fillSpace / fillColumns.size()));

            for (int column = 0; column < table.getColumnCount(); column++)
            {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                ThemeStyle style = styles.get(tableColumn.getIdentifier());

                if (style != null && fillColumns.containsKey(style.getIdentifier()))
                {
                    int preferredWidth = Math.min(Math.max(style.getMinWidth(), fillWidth), style.getMaxWidth());
                    tableColumn.setPreferredWidth(preferredWidth);
                }
            }
        }
    }
}
