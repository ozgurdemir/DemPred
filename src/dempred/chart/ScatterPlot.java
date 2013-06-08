package dempred.chart;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot {
	private String title;
	private String xAxisTitle;
	private String yAxisTitle;
	private double[] values;
	private boolean legend;

	public ScatterPlot(String title, String xAxisTitle, String yAxisTitle, double[] values, boolean legend) {
		super();
		this.values = values;
		this.title = title;
		this.legend = legend;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
	}

	public JFreeChart generateChart() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries data = new XYSeries("data");
		for (int i = 0; i < values.length; ++i)
			data.add(i, values[i]);
		dataset.addSeries(data);
		JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisTitle, yAxisTitle, dataset, PlotOrientation.VERTICAL, legend, false, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		return chart;
	}
}
