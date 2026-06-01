package com.assembly.ui.themes;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;

public class BorderStyle implements Cloneable
{

    public int width = 0;
    public Color color = Color.decode("#cccccc");

    @Override
    public BorderStyle clone()
    {
        final BorderStyle clone;
        try
        {
            clone = (BorderStyle) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e);
        }

        clone.width = this.width;
        clone.color = new ColorUIResource(this.color);

        return clone;
    }
}
