package com.assembly.ui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.assembly.ui.themes.ThemeStyle;

public class TabPanel extends JPanel implements ActionListener
{

    private static final long serialVersionUID = -4925723751800337729L;

    private ThemeStyle stylize = new ThemeStyle();

    private WrapLayout layout;
    private Container container;

    private Map<Integer, JButton> tabs = new HashMap<Integer, JButton>();
    private Map<Integer, ArrayList<Object>> contents = new HashMap<Integer, ArrayList<Object>>();

    private int counter = 0;
    private int selected = 0;

    public TabPanel()
    {
    }

    @Override
    public void updateUI()
    {
        super.updateUI();

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setFont(UIManager.getFont("TextField.font").deriveFont(14f));
        setForeground(Color.decode("#000000"));
        setBackground(Color.decode("#f7f7f7"));

        layout = new WrapLayout();
        layout.setAlignment(FlowLayout.LEFT);
        container = this;
        container.setLayout(layout);
    }

    public void stylize(ThemeStyle style)
    {
        stylize = style;
    }

    public int addTab(String name)
    {
        int handle = counter++;
        JButton instance = new JButton(name);
        {
            instance.setFont(UIManager.getFont("Label.font").deriveFont(13.0f));
            instance.setForeground(Color.decode("#000000"));
            instance.setBackground(Color.decode("#d9d9d9"));
            instance.setAlignmentX(Component.LEFT_ALIGNMENT);

            instance.setOpaque(true);
            instance.setContentAreaFilled(true);
            instance.setBorderPainted(false);
            instance.setFocusPainted(false);

            Border lines = BorderFactory.createLineBorder(Color.decode("#ffffff"), 0);
            Border empty = BorderFactory.createEmptyBorder(5, 5, 5, 5);
            instance.setBorder(new CompoundBorder(lines, empty));

            instance.setMnemonic(handle);
            instance.addActionListener(this);

            container.add(instance);
        }
        tabs.put(handle, instance);

        return handle;
    }

    public void addSpace(int width)
    {
        container.add(Box.createHorizontalStrut(width));
        this.tabChange(selected);
    }

    public void addPage(int handle, JComponent panel)
    {
        ArrayList<Object> pages = null;
        if (!tabs.containsKey(new Integer(handle)))
        {
            pages = new ArrayList<Object>();
            contents.put(handle, pages);
        }
        else
        {
            pages = contents.get(new Integer(handle));
            if (pages == null || !(pages instanceof ArrayList))
            {
                pages = new ArrayList<Object>();
                contents.put(handle, pages);
            }
        }
        pages.add(panel);
        this.tabChange(selected);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        JButton instance = (JButton) e.getSource();
        selected = instance.getMnemonic();

        this.tabChange(selected);
    }

    private void tabChange(int handle)
    {
        ArrayList<Object> pages = null;
        if (tabs.containsKey(new Integer(handle)))
        {
            JButton instance = (JButton) tabs.get(handle);
            instance.setForeground(Color.decode("#000000"));
            instance.setBackground(Color.decode("#ffdf84"));

            pages = contents.get(new Integer(handle));
            if (pages != null && pages instanceof ArrayList && pages.size() > 0)
            {
                for (Object page : pages)
                {
                    JComponent panel = (JComponent) page;
                    if (panel != null && panel instanceof JComponent)
                    {
                        panel.setVisible(true);
                    }
                }
            }
        }

        for (Entry<Integer, JButton> entry : tabs.entrySet())
        {
            Integer key = entry.getKey();
            if (key != handle)
            {
                JButton instance = (JButton) entry.getValue();
                instance.setForeground(Color.decode("#000000"));
                instance.setBackground(Color.decode("#d9d9d9"));

                if (tabs.containsKey(new Integer(key)))
                {
                    pages = contents.get(new Integer(key));
                    if (pages != null && pages instanceof ArrayList && pages.size() > 0)
                    {
                        for (Object page : pages)
                        {
                            JComponent panel = (JComponent) page;
                            if (panel != null && panel instanceof JComponent)
                            {
                                panel.setVisible(false);
                            }
                        }
                    }
                }
            }
        }
    }
}
