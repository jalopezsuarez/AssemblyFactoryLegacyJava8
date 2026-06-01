/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assembly.core.config;

import com.assembly.core.source.db.DriverAdapter;

/**
 *
 * @author Administrator
 */
public class PoolAdapter
{
    DriverAdapter adapter;

    int enable;
    int maximum;
    int idle;
    int minimum;

    public PoolAdapter(DriverAdapter driver)
    {
        adapter = driver;

        enable = 0;
        maximum = 8;
        idle = 8;
        minimum = 0;
    }

    public DriverAdapter getAdapter()
    {
        return adapter;
    }

    public int getEnable()
    {
        return enable;
    }

    public void setEnable(int enable)
    {
        this.enable = enable;
    }

    public int getMaximum()
    {
        return maximum;
    }

    public void setMaximum(int maximum)
    {
        this.maximum = maximum;
    }

    public int getIdle()
    {
        return idle;
    }

    public void setIdle(int idle)
    {
        this.idle = idle;
    }

    public int getMinimum()
    {
        return minimum;
    }

    public void setMinimum(int minimum)
    {
        this.minimum = minimum;
    }

}
