/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.ui.components.plot;

import com.assembly.ui.themes.ThemeStyle;

/**
 *
 * @author Administrator
 */
public interface PlotInterface
{

    public void add(String serie);

    public void add(String serie, Object x, Object y);

    public void stylize(ThemeStyle style);

    public void render();
}
