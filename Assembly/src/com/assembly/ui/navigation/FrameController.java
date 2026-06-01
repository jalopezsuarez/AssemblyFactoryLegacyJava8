/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.ui.navigation;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Component;

import com.assembly.ui.themes.DimensionStyle;
import java.util.EnumSet;
import javax.swing.JFrame;

/**
 *
 * @author Administrator
 */
public class FrameController extends JFrame implements WindowController
{
    private Component windowPane = null;

    public FrameController(JPanel panel, Windowed... windowed)
    {
        this.initialize(panel, EnumSet.of(Windowed.Defaults, windowed));
    }

    private void initialize(JPanel panel, EnumSet<Windowed> windowed)
    {
        windowPane = panel;

        this.setUndecorated(false);
        this.setAlwaysOnTop(false);

        for (Windowed attribute : windowed)
        {
            if (attribute.equals(Windowed.Undecorated))
            {
                this.setUndecorated(true);
            }
            else if (attribute.equals(Windowed.AlwaysOnTop))
            {
                this.setAlwaysOnTop(true);
            }
        }

        this.setTitle(panel.getName());
        this.add(panel);

        DimensionStyle dimension = new DimensionStyle();
        dimension.setDimension(panel.getPreferredSize().width, panel.getPreferredSize().height);

        this.setSize(dimension.getWidth(), dimension.getHeight());
        this.setLocation(dimension.getX(), dimension.getY());
        this.pack();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    @Override
    public void showWindow()
    {
        this.setVisible(true);
        this.toFront();
        this.requestFocus();
    }

    @Override
    public Component getWindowPane()
    {
        return windowPane;
    }
}
