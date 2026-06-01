package com.assembly.ui.components.plot;

import com.assembly.ui.themes.ThemeStyle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LinePlot extends ChartPanel implements PlotInterface
{

    //  https://www.programcreek.com/java-api-examples/index.php?api=org.jfree.data.time.TimeSeriesCollection
    private static final long serialVersionUID = -7863760763454369613L;

    private TimeSeriesCollection datasetTimeSeries = null;
    private XYSeriesCollection datasetXYSeries = null;

    //private final ArrayList<Series> seriesCollection = new ArrayList();
    private final HashMap<String, Series> seriesCollection = new HashMap();
    // TimeSeries series1 = new TimeSeries("Object 1");

    public LinePlot()
    {
        super(null);
    }

    @Override
    public void add(String serie)
    {
        TimeSeries series1 = new TimeSeries(serie);
        seriesCollection.put(serie, series1);
    }

    @Override
    public void add(String serie, Object x, Object y)
    {
        if (seriesCollection.containsKey(serie) && x instanceof Date)
        {
            Series dataset = seriesCollection.get(serie);
            if (dataset instanceof TimeSeries)
            {
                Date key = (Date) x;
                TimeSeries series = (TimeSeries) dataset;
                // series.add(new Day(key), y);
            }
        }
        else if (seriesCollection.containsKey(serie) && x instanceof Double)
        {
            Series dataset = seriesCollection.get(serie);
            if (dataset instanceof TimeSeries)
            {
                Double key = (Double) x;
                XYSeries series = (XYSeries) dataset;
                // series.add(key, y);
            }
        }

        // ----------------------- PRUEBAS
        HashMap<String, Double> resumen = new HashMap();
        // for (Averia averia : averias)
        {
            String stringValue = String.valueOf(5);
            stringValue = stringValue.substring(0, 8);
            if (!resumen.containsKey(stringValue))
            {
                resumen.put(stringValue, new Double(0));
            }
            Double counter = resumen.get(stringValue);
            counter++;
            resumen.put(stringValue, counter);
        }

        // chartAverias.addSerie("PRUEBA", "test");
        for (HashMap.Entry<String, Double> entry : resumen.entrySet())
        {
            try
            {
                SimpleDateFormat parser = new SimpleDateFormat("yyyymmdd");
                java.util.Date date = parser.parse(entry.getKey());
                // chartAverias.addPlot("PRUEBA", date, entry.getValue());
            }
            catch (Exception ex)
            {
            }
        }
        // chartAverias.render();
    }

    @Override
    public void stylize(ThemeStyle style)
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render()
    {
        JFreeChart createLineChart = null;

        datasetTimeSeries = new TimeSeriesCollection();
        for (HashMap.Entry<String, Series> entry : seriesCollection.entrySet())
        {
            datasetTimeSeries.addSeries((TimeSeries) entry.getValue());
        }

        if (datasetTimeSeries != null)
        {
            createLineChart = ChartFactory.createTimeSeriesChart("title", "categoryAxisLabel", "valueAxisLabel", datasetTimeSeries, false, false, false);
        }
        else
        {

            createLineChart = ChartFactory.createXYLineChart("title", "categoryAxisLabel", "valueAxisLabel", datasetXYSeries, PlotOrientation.VERTICAL, false, false, false);
        }

        setChart(createLineChart);
        setRangeZoomable(false);

        setPreferredSize(new Dimension(600, 300));
        setMinimumSize(new Dimension(600, 300));
        setMaximumSize(new Dimension(600, 300));

        XYPlot XYPlot = createLineChart.getXYPlot();

        XYLineAndShapeRenderer lineAndShapeRenderer = new XYLineAndShapeRenderer();
        lineAndShapeRenderer.setSeriesPaint(0, Color.decode("#1f487d"));
        lineAndShapeRenderer.setSeriesPaint(1, Color.decode("#c15251"));
        lineAndShapeRenderer.setSeriesPaint(2, Color.decode("#f79647"));
        lineAndShapeRenderer.setSeriesPaint(3, Color.decode("#5e4b79"));
        lineAndShapeRenderer.setSeriesPaint(4, Color.decode("#4f81bc"));
        lineAndShapeRenderer.setSeriesPaint(5, Color.decode("#4bacc4"));
        lineAndShapeRenderer.setSeriesPaint(6, Color.decode("#9bbb58"));
        lineAndShapeRenderer.setSeriesPaint(7, Color.decode("#9bbb58"));
        lineAndShapeRenderer.setSeriesPaint(8, Color.decode("#9bbb58"));
        lineAndShapeRenderer.setSeriesPaint(9, Color.decode("#9bbb58"));

        Shape square = new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0);
        lineAndShapeRenderer.setSeriesShape(0, square);
        lineAndShapeRenderer.setSeriesShape(1, square);
        lineAndShapeRenderer.setSeriesShape(2, square);
        lineAndShapeRenderer.setSeriesShape(3, square);
        lineAndShapeRenderer.setSeriesShape(4, square);
        lineAndShapeRenderer.setSeriesShape(5, square);
        lineAndShapeRenderer.setSeriesShape(6, square);
        lineAndShapeRenderer.setSeriesShape(7, square);
        lineAndShapeRenderer.setSeriesShape(8, square);
        lineAndShapeRenderer.setSeriesShape(9, square);

        lineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(1, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(2, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(3, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(4, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(5, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(6, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(7, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(8, new BasicStroke(2));
        lineAndShapeRenderer.setSeriesStroke(9, new BasicStroke(2));

        XYPlot.setRenderer(lineAndShapeRenderer);
        XYPlot.setBackgroundPaint(Color.decode("#ffffff"));

        XYPlot.setRangeGridlineStroke(new BasicStroke(1.0f));
        XYPlot.setRangeGridlinesVisible(true);
        XYPlot.setRangeGridlinePaint(Color.decode("#e9e9e9"));

        XYPlot.setDomainGridlineStroke(new BasicStroke(1.0f));
        XYPlot.setDomainGridlinesVisible(false);
        XYPlot.setDomainGridlinePaint(Color.decode("#e9e9e9"));
    }

}
