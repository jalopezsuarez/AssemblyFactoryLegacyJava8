package com.assembly.ui.components.plot;

import com.assembly.ui.themes.ThemeStyle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;

public class BarPlot extends ChartPanel implements PlotInterface
{

    private static final long serialVersionUID = 1522660322352446293L;

    public BarPlot()
    {
        super(null);
    }

    @Override
    public void add(String serie)
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(String serie, Object x, Object y)
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stylize(ThemeStyle style)
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render()
    {
        Dataset dataset = null;

        if (dataset instanceof CategoryDataset)
        {
            CategoryDataset categoryDataset = (CategoryDataset) dataset;

            JFreeChart createBarChart = ChartFactory.createBarChart("title", "categoryAxisLabel", "valueAxisLabel", categoryDataset, PlotOrientation.VERTICAL, false, false, false);
            setChart(createBarChart);

            setPreferredSize(new Dimension(600, 300));
            setMinimumSize(new Dimension(600, 300));
            setMaximumSize(new Dimension(600, 300));

            CategoryPlot categoryPlot = createBarChart.getCategoryPlot();

            BarRenderer barRenderer = new BarRenderer();
            barRenderer.setSeriesPaint(0, Color.decode("#1f487d"));
            barRenderer.setSeriesPaint(1, Color.decode("#c15251"));
            barRenderer.setSeriesPaint(2, Color.decode("#f79647"));
            barRenderer.setSeriesPaint(3, Color.decode("#5e4b79"));
            barRenderer.setSeriesPaint(4, Color.decode("#4f81bc"));
            barRenderer.setSeriesPaint(5, Color.decode("#4bacc4"));
            barRenderer.setSeriesPaint(6, Color.decode("#9bbb58"));
            barRenderer.setSeriesPaint(7, Color.decode("#9bbb58"));
            barRenderer.setSeriesPaint(8, Color.decode("#9bbb58"));
            barRenderer.setSeriesPaint(9, Color.decode("#9bbb58"));

            barRenderer.setBarPainter(new StandardBarPainter());
            barRenderer.setShadowVisible(false);
            barRenderer.setDrawBarOutline(false);

            categoryPlot.setRenderer(barRenderer);

            categoryPlot.setBackgroundPaint(Color.decode("#ffffff"));

            categoryPlot.setRangeGridlineStroke(new BasicStroke(1.0f));
            categoryPlot.setRangeGridlinesVisible(true);
            categoryPlot.setRangeGridlinePaint(Color.decode("#e9e9e9"));

            categoryPlot.setDomainGridlineStroke(new BasicStroke(1.0f));
            categoryPlot.setDomainGridlinesVisible(false);
            categoryPlot.setDomainGridlinePaint(Color.decode("#e9e9e9"));

            // categoryPlot.s
        }
    }
}
