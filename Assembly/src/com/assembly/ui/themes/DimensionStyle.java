package com.assembly.ui.themes;

import java.awt.GraphicsDevice;
import java.awt.MouseInfo;

public class DimensionStyle
{

    private int width;

    private int height;

    private int minWidth;

    private int minHeight;

    private int maxWidth;

    private int maxHeight;

    private int x;

    private int y;

    private final GraphicsDevice screenDevice;

    private boolean fullScreen;

    private boolean undecorated;

    // =======================================================
    public DimensionStyle()
    {
        this.initialize();
        screenDevice = MouseInfo.getPointerInfo().getDevice();
    }

    private void initialize()
    {
        x = -1;
        y = -1;
        width = -1;
        height = -1;
        minWidth = -1;
        minHeight = -1;
        maxWidth = -1;
        maxHeight = -1;
        fullScreen = false;
        undecorated = false;
    }

    // =======================================================
    public DimensionStyle setMinimum(int minWidth, int minHeight)
    {
        this.minWidth = minWidth;
        this.minHeight = minHeight;

        // -------------------------------------------------------
        this.width = minWidth;
        this.height = minHeight;

        // -------------------------------------------------------
        int dimensionX = (int) screenDevice.getDefaultConfiguration().getBounds().getX();
        dimensionX += (screenDevice.getDisplayMode().getWidth() / 2) - (this.width / 2);

        int dimensionY = (int) screenDevice.getDefaultConfiguration().getBounds().getY();
        dimensionY += (screenDevice.getDisplayMode().getHeight() / 2) - (this.height / 2);

        // -------------------------------------------------------
        this.x = dimensionX;
        this.y = dimensionY;

        // =======================================================
        return this;
    }

    public DimensionStyle setMaximum(int maxWidth, int maxHeight)
    {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        // =======================================================
        this.width = maxWidth;
        this.height = maxHeight;

        // -------------------------------------------------------
        int dimensionX = (int) screenDevice.getDefaultConfiguration().getBounds().getX();
        dimensionX += (screenDevice.getDisplayMode().getWidth() / 2) - (this.width / 2);

        int dimensionY = (int) screenDevice.getDefaultConfiguration().getBounds().getY();
        dimensionY += (screenDevice.getDisplayMode().getHeight() / 2) - (this.height / 2);

        // -------------------------------------------------------
        this.x = dimensionX;
        this.y = dimensionY;

        // =======================================================
        return this;
    }

    public DimensionStyle setPercentage(int percentageWidth, int percentageHeight)
    {

        int relativeWidth = (int) Math.round((percentageWidth * screenDevice.getDisplayMode().getWidth()) / 100);
        int relativeHeight = (int) Math.round((percentageHeight * screenDevice.getDisplayMode().getHeight()) / 100);

        // -------------------------------------------------------
        int dimensionWidth = relativeWidth;
        int dimensionHeight = relativeHeight;

        if (this.minWidth > 0)
        {
            dimensionWidth = Math.max(this.minWidth, dimensionWidth);
        }
        if (this.minHeight > 0)
        {
            dimensionHeight = Math.max(this.minHeight, dimensionHeight);
        }

        if (this.maxWidth > 0)
        {
            dimensionWidth = Math.min(this.maxWidth, dimensionWidth);
        }
        if (this.maxHeight > 0)
        {
            dimensionHeight = Math.min(this.maxHeight, dimensionHeight);
        }

        // -------------------------------------------------------
        this.width = dimensionWidth;
        this.height = dimensionHeight;

        // =======================================================
        int dimensionX = (int) screenDevice.getDefaultConfiguration().getBounds().getX();
        dimensionX += (screenDevice.getDisplayMode().getWidth() / 2) - (this.width / 2);

        int dimensionY = (int) screenDevice.getDefaultConfiguration().getBounds().getY();
        dimensionY += (screenDevice.getDisplayMode().getHeight() / 2) - (this.height / 2);

        // -------------------------------------------------------
        this.x = dimensionX;
        this.y = dimensionY;

        // =======================================================
        return this;
    }

    public DimensionStyle setDimension(int width, int height)
    {
        int dimensionWidth = width;
        int dimensionHeight = height;

        if (this.minWidth > 0)
        {
            dimensionWidth = Math.max(this.minWidth, dimensionWidth);
        }
        if (this.minHeight > 0)
        {
            dimensionHeight = Math.max(this.minHeight, dimensionHeight);
        }

        if (this.maxWidth > 0)
        {
            dimensionWidth = Math.min(this.maxWidth, dimensionWidth);
        }
        if (this.maxHeight > 0)
        {
            dimensionHeight = Math.min(this.maxHeight, dimensionHeight);
        }

        // =======================================================
        this.width = dimensionWidth;
        this.height = dimensionHeight;

        // -------------------------------------------------------
        int dimensionX = (int) screenDevice.getDefaultConfiguration().getBounds().getX();
        dimensionX += (screenDevice.getDisplayMode().getWidth() / 2) - (this.width / 2);

        int dimensionY = (int) screenDevice.getDefaultConfiguration().getBounds().getY();
        dimensionY += (screenDevice.getDisplayMode().getHeight() / 2) - (this.height / 2);

        // -------------------------------------------------------
        this.x = dimensionX;
        this.y = dimensionY;

        // =======================================================
        return this;
    }

    public DimensionStyle setPosition(AlignmentStyle position)
    {
        int dimensionX = (int) screenDevice.getDefaultConfiguration().getBounds().getX();
        int dimensionY = (int) screenDevice.getDefaultConfiguration().getBounds().getY();

        // -------------------------------------------------------
        if (position == AlignmentStyle.LeftTop || position == AlignmentStyle.LeftCenter || position == AlignmentStyle.LeftBottom)
        {
            dimensionX += 0;
        }
        else if (position == AlignmentStyle.CenterTop || position == AlignmentStyle.CenterCenter || position == AlignmentStyle.CenterBottom)
        {
            dimensionX += (screenDevice.getDisplayMode().getWidth() / 2) - (this.width / 2);
        }
        else if (position == AlignmentStyle.RightTop || position == AlignmentStyle.RightCenter || position == AlignmentStyle.RightBottom)
        {
            dimensionX += screenDevice.getDisplayMode().getWidth() - this.width;
        }

        // -------------------------------------------------------
        if (position == AlignmentStyle.LeftTop || position == AlignmentStyle.CenterTop || position == AlignmentStyle.RightTop)
        {
            dimensionY += 0;
        }
        else if (position == AlignmentStyle.LeftCenter || position == AlignmentStyle.CenterCenter || position == AlignmentStyle.RightCenter)
        {
            dimensionY += (screenDevice.getDisplayMode().getHeight() / 2) - (this.height / 2);
        }
        else if (position == AlignmentStyle.LeftBottom || position == AlignmentStyle.CenterBottom || position == AlignmentStyle.RightBottom)
        {
            dimensionY += screenDevice.getDisplayMode().getHeight() - this.height;
        }

        // -------------------------------------------------------
        this.x = dimensionX;
        this.y = dimensionY;

        // =======================================================
        return this;
    }

    public void setFullScreen()
    {
        this.fullScreen = true;
    }

    public void setUndecorated()
    {
        this.undecorated = true;
    }

    // =======================================================
    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean isFullScreen()
    {
        return fullScreen;
    }

    public boolean isUndecorated()
    {
        return undecorated;
    }

}
