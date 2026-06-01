package com.assembly.ui.themes;

import java.util.HashMap;
import java.util.UUID;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

import com.assembly.core.resources.RelativeResource;

public class ThemeStyle implements Cloneable
{

    public static final String FontResources = "/res/fonts/";
    public static final HashMap<String, Font> fonts = new HashMap<String, Font>();

    private String identifier;
    private String title;

    private Font font;
    private Color foreColor;
    private Color backColor;
    private Color foreColorAlternate;
    private Color backColorAlternate;
    private Color foreColorSelected;
    private Color backColorSelected;

    private boolean transparentBackground;

    private AlignmentStyle textAlign;
    private boolean wordWrap;
    private boolean readOnly;
    private int maxLength;

    private int width;
    private int minWidth;
    private int maxWidth;
    private int percentageWidth;

    private HashMap<PositionStyle, BorderStyle> borders;
    private Insets padding;
    private Insets margin;

    private Font headerFont;
    private Color headerForeColor;
    private Color headerBackColor;
    private AlignmentStyle headerTextAlign;
    private HashMap<PositionStyle, BorderStyle> headerBorders;
    private Insets headerPadding;
    private Insets headerMargin;

    public ThemeStyle()
    {
        initialize(UUID.randomUUID().toString());
    }

    public ThemeStyle(String identifier)
    {
        initialize(identifier);
    }

    private void initialize(String identifier)
    {
        this.identifier = identifier;
        this.title = "";

        this.font = UIManager.getFont("TextField.font").deriveFont(13f);
        this.foreColor = Color.decode("#000000");
        this.backColor = Color.decode("#ffffff");
        this.foreColorAlternate = Color.decode("#000000");
        this.backColorAlternate = Color.decode("#ffffff");
        this.foreColorSelected = Color.decode("#000000");
        this.backColorSelected = Color.decode("#eeeeee");

        this.transparentBackground = false;

        this.textAlign = AlignmentStyle.LeftCenter;
        this.wordWrap = false;
        this.readOnly = false;
        this.maxLength = -1;

        this.width = -1;
        this.minWidth = 0;
        this.maxWidth = Integer.MAX_VALUE;
        this.percentageWidth = -1;

        this.borders = new HashMap<>();
        this.padding = new Insets(0, 0, 0, 0);
        this.margin = new Insets(0, 0, 0, 0);

        this.headerFont = UIManager.getFont("TextField.font").deriveFont(13f);
        this.headerForeColor = Color.decode("#000000");
        this.headerBackColor = Color.decode("#ffffff");
        this.headerTextAlign = AlignmentStyle.LeftCenter;
        this.headerBorders = new HashMap<>();
        this.headerPadding = new Insets(0, 0, 0, 0);
        this.headerMargin = new Insets(0, 0, 0, 0);
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Font getFont()
    {
        return font;
    }

    public void setFont(FontStyle style, int size)
    {
        int fontStyle = Font.PLAIN;
        if (style == FontStyle.Light)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.Bold)
        {
            fontStyle = Font.BOLD;
        }
        else if (style == FontStyle.CondensedLight)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.CondensedRegular)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.CondensedBold)
        {
            fontStyle = Font.BOLD;
        }

        this.font = font.deriveFont(fontStyle, size);
    }

    public void setFont(int size)
    {
        this.font = font.deriveFont(new Float(size));
    }

    public void setFont(String resource, int size)
    {
        int fontStyle = Font.PLAIN;

        Font fontResource = null;
        if (fontResource == null)
        {
            if (fonts.containsKey(resource))
            {
                fontResource = fonts.get(resource).deriveFont(fontStyle, size);
            }
        }
        if (fontResource == null)
        {
            try
            {
                InputStream resourceStream = RelativeResource.instance().read(FontResources + resource);
                fontResource = Font.createFont(Font.TRUETYPE_FONT, resourceStream).deriveFont(fontStyle, size);
                fonts.put(resource, fontResource);
            }
            catch (Exception | Error ex)
            {
                fontResource = null;
            }
        }
        if (fontResource == null)
        {
            try
            {
                InputStream resourceStream = RelativeResource.instance().read(FontResources + resource);
                fontResource = Font.createFont(Font.TRUETYPE_FONT, resourceStream).deriveFont(fontStyle, size);
                fonts.put(resource, fontResource);
            }
            catch (Exception | Error ex)
            {
                fontResource = null;
            }
        }
        if (fontResource == null)
        {
            fontResource = UIManager.getFont("TextField.font").deriveFont(fontStyle, size);
        }

        this.font = fontResource;
    }

    public Color getForeColor()
    {
        return foreColor;
    }

    public void setForeColor(Color foreColor)
    {
        this.foreColor = foreColor;
        this.foreColorAlternate = foreColor;
        this.foreColorSelected = foreColor;
    }

    public void setForeColor(String foreColor)
    {
        this.foreColor = Color.decode(foreColor);
        this.foreColorAlternate = Color.decode(foreColor);
        this.foreColorSelected = Color.decode(foreColor);
    }

    public Color getBackColor()
    {
        return backColor;
    }

    public void setBackColor(Color backColor)
    {
        this.backColor = backColor;
        this.backColorAlternate = backColor;
        this.backColorSelected = backColor;
    }

    public void setBackColor(String backColor)
    {
        this.backColor = Color.decode(backColor);
        this.backColorAlternate = Color.decode(backColor);
        this.backColorSelected = Color.decode(backColor);
    }

    public Color getForeColorAlternate()
    {
        return foreColorAlternate;
    }

    public void setForeColorAlternate(Color foreColorAlternate)
    {
        this.foreColorAlternate = foreColorAlternate;
    }

    public void setForeColorAlternate(String foreColorAlternate)
    {
        this.foreColorAlternate = Color.decode(foreColorAlternate);
    }

    public Color getBackColorAlternate()
    {
        return backColorAlternate;
    }

    public void setBackColorAlternate(Color backColorAlternate)
    {
        this.backColorAlternate = backColorAlternate;
    }

    public void setBackColorAlternate(String backColorAlternate)
    {
        this.backColorAlternate = Color.decode(backColorAlternate);
    }

    public Color getForeColorSelected()
    {
        return foreColorSelected;
    }

    public void setForeColorSelected(Color foreColorSelected)
    {
        this.foreColorSelected = foreColorSelected;
    }

    public void setForeColorSelected(String foreColorSelected)
    {
        this.foreColorSelected = Color.decode(foreColorSelected);
    }

    public Color getBackColorSelected()
    {
        return backColorSelected;
    }

    public void setBackColorSelected(Color backColorSelected)
    {
        this.backColorSelected = backColorSelected;
    }

    public void setBackColorSelected(String backColorSelected)
    {
        this.backColorSelected = Color.decode(backColorSelected);
    }

    public boolean isTransparentBackground()
    {
        return transparentBackground;
    }

    public void setTransparentBackground(boolean transparentBackground)
    {
        this.transparentBackground = transparentBackground;
    }

    public AlignmentStyle getTextAlign()
    {
        return textAlign;
    }

    public void setTextAlign(AlignmentStyle textAlign)
    {
        this.textAlign = textAlign;
    }

    public boolean isWordWrap()
    {
        return wordWrap;
    }

    public void setWordWrap(boolean wordWrap)
    {
        this.wordWrap = wordWrap;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getMinWidth()
    {
        return minWidth;
    }

    public void setMinWidth(int minWidth)
    {
        this.minWidth = minWidth;
    }

    public int getMaxWidth()
    {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    public int getPercentageWidth()
    {
        return percentageWidth;
    }

    public void setPercentageWidth(int percentageWidth)
    {
        this.percentageWidth = percentageWidth;
    }

    public BorderStyle getBorder(PositionStyle position)
    {
        BorderStyle style = new BorderStyle();
        if (borders.containsKey(position) && borders.get(position) != null)
        {
            style = borders.get(position);
        }
        return style;
    }

    public void setBorder(PositionStyle position, int width)
    {
        BorderStyle style = new BorderStyle();
        style.width = width;
        borders.put(position, style);
    }

    public void setBorder(PositionStyle position, int width, Color color)
    {
        BorderStyle style = new BorderStyle();
        style.width = width;
        style.color = color;
        borders.put(position, style);
    }

    public void setBorder(PositionStyle position, int width, String color)
    {
        BorderStyle style = new BorderStyle();
        style.width = width;
        style.color = Color.decode(color);
        borders.put(position, style);
    }

    public Insets getPadding()
    {
        return padding;
    }

    public void setPadding(int top, int right, int bottom, int left)
    {
        this.padding = new Insets(top, left, bottom, right);
    }

    public Insets getMargin()
    {
        return margin;
    }

    public void setMargin(int top, int right, int bottom, int left)
    {
        this.margin = new Insets(top, left, bottom, right);
    }

    public Font getHeaderFont()
    {
        return headerFont;
    }

    public void setHeaderFont(FontStyle style, int size)
    {
        int fontStyle = Font.PLAIN;
        if (style == FontStyle.Light)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.Bold)
        {
            fontStyle = Font.BOLD;
        }
        else if (style == FontStyle.CondensedLight)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.CondensedRegular)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.CondensedBold)
        {
            fontStyle = Font.BOLD;
        }

        this.headerFont = headerFont.deriveFont(fontStyle, size);
    }

    public void setHeaderFont(int size)
    {
        this.headerFont = headerFont.deriveFont(new Float(size));
    }

    public void setHeaderFont(String resource, FontStyle style, int size)
    {
        int fontStyle = Font.PLAIN;
        if (style == FontStyle.Light)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.Bold)
        {
            fontStyle = Font.BOLD;
        }
        else if (style == FontStyle.CondensedLight)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.CondensedRegular)
        {
            fontStyle = Font.PLAIN;
        }
        else if (style == FontStyle.CondensedBold)
        {
            fontStyle = Font.BOLD;
        }

        Font fontResource = null;
        if (fontResource == null)
        {
            if (fonts.containsKey(resource))
            {
                fontResource = fonts.get(resource).deriveFont(fontStyle, size);
            }
        }
        if (fontResource == null)
        {
            try
            {
                InputStream resourceStream = RelativeResource.instance().read(FontResources + resource);
                fontResource = Font.createFont(Font.TRUETYPE_FONT, resourceStream).deriveFont(fontStyle, size);
                fonts.put(resource, fontResource);
            }
            catch (Exception | Error ex)
            {
                fontResource = null;
            }
        }
        if (fontResource == null)
        {
            try
            {
                InputStream resourceStream = RelativeResource.instance().read(FontResources + resource);
                fontResource = Font.createFont(Font.TRUETYPE_FONT, resourceStream).deriveFont(fontStyle, size);
                fonts.put(resource, fontResource);
            }
            catch (Exception | Error ex)
            {
                fontResource = null;
            }
        }
        if (fontResource == null)
        {
            fontResource = UIManager.getFont("TextField.font").deriveFont(fontStyle, size);
        }

        this.headerFont = fontResource;
    }

    public Color getHeaderForeColor()
    {
        return headerForeColor;
    }

    public void setHeaderForeColor(Color headerForeColor)
    {
        this.headerForeColor = headerForeColor;
    }

    public void setHeaderForeColor(String headerForeColor)
    {
        this.headerForeColor = Color.decode(headerForeColor);
    }

    public Color getHeaderBackColor()
    {
        return headerBackColor;
    }

    public void setHeaderBackColor(Color headerBackColor)
    {
        this.headerBackColor = headerBackColor;
    }

    public void setHeaderBackColor(String headerBackColor)
    {
        this.headerBackColor = Color.decode(headerBackColor);
    }

    public AlignmentStyle getHeaderTextAlign()
    {
        return headerTextAlign;
    }

    public void setHeaderTextAlign(AlignmentStyle headerTextAlign)
    {
        this.headerTextAlign = headerTextAlign;
    }

    public BorderStyle getHeaderBorder(PositionStyle position)
    {
        BorderStyle style = new BorderStyle();
        if (headerBorders.containsKey(position) && headerBorders.get(position) != null)
        {
            style = headerBorders.get(position);
        }
        return style;
    }

    public void setHeaderBorder(PositionStyle position, int width)
    {
        BorderStyle style = new BorderStyle();
        style.width = width;
        headerBorders.put(position, style);
    }

    public void setHeaderBorder(PositionStyle position, int width, Color color)
    {
        BorderStyle style = new BorderStyle();
        style.width = width;
        style.color = color;
        headerBorders.put(position, style);
    }

    public Insets getHeaderPadding()
    {
        return headerPadding;
    }

    public void setHeaderPadding(int top, int right, int bottom, int left)
    {
        this.headerPadding = new Insets(top, left, bottom, right);
    }

    public Insets getHeaderMargin()
    {
        return headerMargin;
    }

    public void setHeaderMargin(int top, int right, int bottom, int left)
    {
        this.headerMargin = new Insets(top, left, bottom, right);
    }

    @Override
    public String toString()
    {
        return title != null && !title.isEmpty() ? title : super.toString();
    }

    @Override
    public ThemeStyle clone()
    {
        final ThemeStyle clone;
        try
        {
            clone = (ThemeStyle) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e);
        }

        clone.identifier = new String(this.identifier);
        clone.title = new String(this.title);

        clone.font = new FontUIResource(this.font);
        clone.foreColor = new ColorUIResource(this.foreColor);
        clone.backColor = new ColorUIResource(this.backColor);
        clone.foreColorAlternate = new ColorUIResource(this.foreColorAlternate);
        clone.backColorAlternate = new ColorUIResource(this.backColorAlternate);
        clone.foreColorSelected = new ColorUIResource(this.foreColorSelected);
        clone.backColorSelected = new ColorUIResource(this.backColorSelected);

        clone.transparentBackground = this.transparentBackground;

        clone.textAlign = this.textAlign;
        clone.wordWrap = this.wordWrap;
        clone.readOnly = this.readOnly;
        clone.maxLength = this.maxLength;

        clone.width = this.width;
        clone.minWidth = this.minWidth;
        clone.maxWidth = this.maxWidth;
        clone.percentageWidth = this.percentageWidth;

        for (HashMap.Entry<PositionStyle, BorderStyle> entry : this.borders.entrySet())
        {
            clone.borders.put(entry.getKey(), entry.getValue().clone());
        }
        clone.padding = new InsetsUIResource(this.padding.top, this.padding.left, this.padding.bottom, this.padding.right);
        clone.margin = new InsetsUIResource(this.margin.top, this.margin.left, this.margin.bottom, this.margin.right);

        clone.headerFont = new FontUIResource(this.headerFont);
        clone.headerForeColor = new ColorUIResource(this.headerForeColor);
        clone.headerBackColor = new ColorUIResource(this.headerBackColor);
        clone.headerTextAlign = this.headerTextAlign;

        for (HashMap.Entry<PositionStyle, BorderStyle> entry : this.headerBorders.entrySet())
        {
            clone.headerBorders.put(entry.getKey(), entry.getValue().clone());
        }
        clone.headerPadding = new InsetsUIResource(this.headerPadding.top, this.headerPadding.left, this.headerPadding.bottom, this.headerPadding.right);
        clone.headerMargin = new InsetsUIResource(this.headerMargin.top, this.headerMargin.left, this.headerMargin.bottom, this.headerMargin.right);

        return clone;
    }

    public static void applicationStylize()
    {
        try
        {
            System.setProperty("apple.awt.graphics.UseQuartz", "true");
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("sun.java2d.xrender", "true");
            System.setProperty("swing.aatext", "true");

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception | Error ex)
        {
        }
    }

    public void stylize(JPanel panel)
    {
        panel.setBackground(this.getBackColor());
        panel.setOpaque(!this.isTransparentBackground());

        BorderStyle styleBorderTop = this.getBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = this.getBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = this.getBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = this.getBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border paddingBorder = new EmptyBorder(this.getPadding().top, this.getPadding().left, this.getPadding().bottom, this.getPadding().right);
        Border marginBorder = new EmptyBorder(this.getMargin().top, this.getMargin().left, this.getMargin().bottom, this.getMargin().right);

        Border styleBorder = new CompoundBorder(new CompoundBorder(marginBorder, colorBorder), paddingBorder);
        panel.setBorder(styleBorder);
    }

    public void stylize(JLabel label)
    {
        Method methodToFind = null;
        try
        {
            Class clazz = label.getClass();
            methodToFind = clazz.getMethod("stylize", new Class[]
            {
                ThemeStyle.class
            });
            if (methodToFind != null)
            {
                methodToFind.invoke(label, this);
            }
        }
        catch (Exception | Error ex)
        {
        }
        if (methodToFind == null)
        {
            label.setFont(this.getFont());
            label.setForeground(this.getForeColor());
            label.setBackground(this.getBackColor());
            label.setOpaque(!this.isTransparentBackground());

            if (this.getTextAlign() == AlignmentStyle.LeftTop || this.getTextAlign() == AlignmentStyle.LeftCenter || this.getTextAlign() == AlignmentStyle.LeftBottom)
            {
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);

                if (this.getTextAlign() == AlignmentStyle.LeftTop)
                {
                    label.setVerticalAlignment(SwingConstants.TOP);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                else if (this.getTextAlign() == AlignmentStyle.LeftCenter)
                {
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setAlignmentY(Component.CENTER_ALIGNMENT);
                }
                else if (this.getTextAlign() == AlignmentStyle.LeftBottom)
                {
                    label.setVerticalAlignment(SwingConstants.BOTTOM);
                    label.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                }
            }
            else if (this.getTextAlign() == AlignmentStyle.CenterTop || this.getTextAlign() == AlignmentStyle.CenterCenter || this.getTextAlign() == AlignmentStyle.CenterBottom)
            {
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);

                if (this.getTextAlign() == AlignmentStyle.CenterTop)
                {
                    label.setVerticalAlignment(SwingConstants.TOP);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                else if (this.getTextAlign() == AlignmentStyle.CenterCenter)
                {
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setAlignmentY(Component.CENTER_ALIGNMENT);
                }
                else if (this.getTextAlign() == AlignmentStyle.CenterBottom)
                {
                    label.setVerticalAlignment(SwingConstants.BOTTOM);
                    label.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                }
            }
            else if (this.getTextAlign() == AlignmentStyle.RightTop || this.getTextAlign() == AlignmentStyle.RightCenter || this.getTextAlign() == AlignmentStyle.RightBottom)
            {
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                label.setAlignmentX(Component.RIGHT_ALIGNMENT);

                if (this.getTextAlign() == AlignmentStyle.RightTop)
                {
                    label.setVerticalAlignment(SwingConstants.TOP);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                else if (this.getTextAlign() == AlignmentStyle.RightCenter)
                {
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setAlignmentY(Component.CENTER_ALIGNMENT);
                }
                else if (this.getTextAlign() == AlignmentStyle.RightBottom)
                {
                    label.setVerticalAlignment(SwingConstants.BOTTOM);
                    label.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                }
            }

            BorderStyle styleBorderTop = this.getBorder(PositionStyle.Top);
            BorderStyle styleBorderLeft = this.getBorder(PositionStyle.Left);
            BorderStyle styleBorderRight = this.getBorder(PositionStyle.Right);
            BorderStyle styleBorderBottom = this.getBorder(PositionStyle.Bottom);

            Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
            Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
            Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
            Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

            Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
            Border emptyBorder = new EmptyBorder(this.getPadding().top, this.getPadding().left, this.getPadding().bottom, this.getPadding().right);

            Border styleBorder = new CompoundBorder(colorBorder, emptyBorder);
            label.setBorder(styleBorder);
        }
    }

    public void stylize(JButton button)
    {
        button.setFont(this.getFont());
        button.setForeground(this.getForeColor());
        button.setBackground(this.getBackColor());
        button.setFocusPainted(false);

        button.setContentAreaFilled(false);
        if (!this.isTransparentBackground())
        {
            button.setOpaque(true);
        }
        else
        {
            button.setOpaque(false);
        }

        ThemeStyle context = this;

        button.getModel().addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                ButtonModel model = (ButtonModel) e.getSource();
                {
                    button.setBackground(context.getBackColor());
                    button.setForeground(context.getForeColor());
                }
                if (model.isRollover())
                {
                    button.setBackground(context.getBackColorAlternate());
                    button.setForeground(context.getForeColorAlternate());
                }
                if (model.isPressed())
                {
                    button.setBackground(context.getBackColorSelected());
                    button.setForeground(context.getForeColorSelected());
                }
            }
        });

        if (this.getTextAlign() == AlignmentStyle.LeftTop || this.getTextAlign() == AlignmentStyle.LeftCenter || this.getTextAlign() == AlignmentStyle.LeftBottom)
        {
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (this.getTextAlign() == AlignmentStyle.LeftTop)
            {
                button.setVerticalAlignment(SwingConstants.TOP);
                button.setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (this.getTextAlign() == AlignmentStyle.LeftCenter)
            {
                button.setVerticalAlignment(SwingConstants.CENTER);
                button.setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (this.getTextAlign() == AlignmentStyle.LeftBottom)
            {
                button.setVerticalAlignment(SwingConstants.BOTTOM);
                button.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }
        else if (this.getTextAlign() == AlignmentStyle.CenterTop || this.getTextAlign() == AlignmentStyle.CenterCenter || this.getTextAlign() == AlignmentStyle.CenterBottom)
        {
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);

            if (this.getTextAlign() == AlignmentStyle.CenterTop)
            {
                button.setVerticalAlignment(SwingConstants.TOP);
                button.setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (this.getTextAlign() == AlignmentStyle.CenterCenter)
            {
                button.setVerticalAlignment(SwingConstants.CENTER);
                button.setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (this.getTextAlign() == AlignmentStyle.CenterBottom)
            {
                button.setVerticalAlignment(SwingConstants.BOTTOM);
                button.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }
        else if (this.getTextAlign() == AlignmentStyle.RightTop || this.getTextAlign() == AlignmentStyle.RightCenter || this.getTextAlign() == AlignmentStyle.RightBottom)
        {
            button.setHorizontalAlignment(SwingConstants.RIGHT);
            button.setAlignmentX(Component.RIGHT_ALIGNMENT);

            if (this.getTextAlign() == AlignmentStyle.RightTop)
            {
                button.setVerticalAlignment(SwingConstants.TOP);
                button.setAlignmentY(Component.TOP_ALIGNMENT);
            }
            else if (this.getTextAlign() == AlignmentStyle.RightCenter)
            {
                button.setVerticalAlignment(SwingConstants.CENTER);
                button.setAlignmentY(Component.CENTER_ALIGNMENT);
            }
            else if (this.getTextAlign() == AlignmentStyle.RightBottom)
            {
                button.setVerticalAlignment(SwingConstants.BOTTOM);
                button.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            }
        }

        BorderStyle styleBorderTop = this.getBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = this.getBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = this.getBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = this.getBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border emptyBorder = new EmptyBorder(this.getPadding().top, this.getPadding().left, this.getPadding().bottom, this.getPadding().right);

        Border styleBorder = new CompoundBorder(colorBorder, emptyBorder);
        button.setBorder(styleBorder);

        if (this.getWidth() > 0)
        {
            button.setSize(new Dimension(this.getWidth(), (int) button.getSize().getHeight()));
        }
        if (this.getMinWidth() > 0)
        {
            button.setMinimumSize(new Dimension(this.getMinWidth(), (int) button.getMinimumSize().getHeight()));
        }
        if (this.getMaxWidth() < Integer.MAX_VALUE)
        {
            button.setMaximumSize(new Dimension(this.getMaxWidth(), (int) button.getMaximumSize().getHeight()));
        }
    }

    public void stylize(JTextComponent textField)
    {
        textField.setFont(this.getFont());
        textField.setForeground(this.getForeColor());
        textField.setBackground(this.getBackColor());
        textField.setOpaque(!this.isTransparentBackground());

        if (this.getTextAlign() == AlignmentStyle.RightTop || this.getTextAlign() == AlignmentStyle.RightCenter || this.getTextAlign() == AlignmentStyle.RightBottom)
        {
            textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        else
        {
            textField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        BorderStyle styleBorderTop = this.getBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = this.getBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = this.getBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = this.getBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border emptyBorder = new EmptyBorder(this.getPadding().top, this.getPadding().left, this.getPadding().bottom, this.getPadding().right);

        Border styleBorder = new CompoundBorder(colorBorder, emptyBorder);
        textField.setBorder(styleBorder);

        textField.setEditable(!this.isReadOnly());
        textField.setFocusable(!this.isReadOnly());

        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        textField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

        {
            InputMap inputMap = textField.getInputMap();
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
        }
    }

    public void stylize(JTextArea textArea)
    {
        textArea.setFont(this.getFont());
        textArea.setForeground(this.getForeColor());
        textArea.setBackground(this.getBackColor());
        textArea.setOpaque(!this.isTransparentBackground());

        if (this.getTextAlign() == AlignmentStyle.RightTop || this.getTextAlign() == AlignmentStyle.RightCenter || this.getTextAlign() == AlignmentStyle.RightBottom)
        {

            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        else
        {
            textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        BorderStyle styleBorderTop = this.getBorder(PositionStyle.Top);
        BorderStyle styleBorderLeft = this.getBorder(PositionStyle.Left);
        BorderStyle styleBorderRight = this.getBorder(PositionStyle.Right);
        BorderStyle styleBorderBottom = this.getBorder(PositionStyle.Bottom);

        Border borderTop = new MatteBorder(styleBorderTop.width, 0, 0, 0, styleBorderTop.color);
        Border borderLeft = new MatteBorder(0, styleBorderLeft.width, 0, 0, styleBorderLeft.color);
        Border borderRight = new MatteBorder(0, 0, styleBorderBottom.width, 0, styleBorderBottom.color);
        Border borderBottom = new MatteBorder(0, 0, 0, styleBorderRight.width, styleBorderRight.color);

        Border colorBorder = new CompoundBorder(new CompoundBorder(new CompoundBorder(borderTop, borderLeft), borderRight), borderBottom);
        Border emptyBorder = new EmptyBorder(this.getPadding().top, this.getPadding().left, this.getPadding().bottom, this.getPadding().right);

        Border styleBorder = new CompoundBorder(colorBorder, emptyBorder);
        textArea.setBorder(styleBorder);

        textArea.setEditable(!this.isReadOnly());
        textArea.setFocusable(!this.isReadOnly());

        textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        {
            InputMap inputMap = textArea.getInputMap();
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
        }

        ThemeStyle context = this;

        textArea.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyChar() == (KeyEvent.VK_ENTER))
                {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }
        });

        textArea.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (e.getSource() instanceof JTextComponent && e.getSource() != null)
                {
                    JTextComponent instance = (JTextComponent) e.getSource();
                    instance.setBackground(context.getBackColorSelected());
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (e.getSource() instanceof JTextComponent && e.getSource() != null)
                {
                    JTextComponent instance = (JTextComponent) e.getSource();
                    instance.setBackground(context.getBackColor());
                }
            }
        });

        ((AbstractDocument) textArea.getDocument()).setDocumentFilter(new TextFilter());
    }

    private class TextFilter extends DocumentFilter
    {

        public void insertString(final DocumentFilter.FilterBypass fb, final int offset, String string, final AttributeSet attr) throws BadLocationException
        {
            if (maxLength > 0 && fb.getDocument().getLength() > maxLength)
            {
                return;
            }

            string = string.replaceAll("\n", " ");
            string = string.replaceAll("\t", " ");
            fb.insertString(offset, string, attr);
        }

        public void replace(final DocumentFilter.FilterBypass fb, final int offset, final int length, String string, final AttributeSet attrs) throws BadLocationException
        {
            if (maxLength > 0 && fb.getDocument().getLength() > maxLength)
            {
                return;
            }

            string = string.replaceAll("\n", " ");
            string = string.replaceAll("\t", " ");
            fb.replace(offset, length, string, attrs);
        }
    }

}
