package dempred.chart;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SimpleLineChart {
	private String title;
	private String xAxisTitle;
	private String yAxisTitle;
	private double[] xdata;
	private double[] ydata;
	private boolean legend;

	public SimpleLineChart(String title, String axisTitle, String axisTitle2, double[] xdata, double[] ydata, boolean legend) {
		super();
		this.xdata = xdata;
		this.ydata = ydata;
		this.title = title;
		this.legend = false;
		this.xAxisTitle = axisTitle;
		this.yAxisTitle = axisTitle2;
	}

	public JFreeChart generateChart() {
		XYSeries series = new XYSeries(1);
		for (int i = 0; i < xdata.length; ++i)
			series.add(xdata[i], ydata[i]);
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisTitle, yAxisTitle, dataset, PlotOrientation.VERTICAL, legend, false, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3.0, 3.0));
        plot.setRenderer(renderer);
		return chart;
	}
}
