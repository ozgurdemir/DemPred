package com.demshape.dempred.chart;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

public class Histogramm {
	private String title;
	private String xAxisTitle;
	private String yAxisTitle;
	private double[] data;
	private int bins=10;
	private boolean legend;

	public Histogramm(String title, String xAxisTitle, String yAxisTitle, double[] data, boolean legend) {
		super();
		this.data = data;
		this.title = title;
		this.legend = legend;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
	}

	public JFreeChart generateChart() {
		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);
		dataset.addSeries(0, data, bins);
		JFreeChart chart = ChartFactory.createHistogram(title, xAxisTitle, yAxisTitle, dataset, PlotOrientation.VERTICAL, legend, false, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		return chart;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getXAxisTitle() {
		return xAxisTitle;
	}

	public final void setXAxisTitle(String axisTitle) {
		xAxisTitle = axisTitle;
	}

	public final String getYAxisTitle() {
		return yAxisTitle;
	}

	public final void setYAxisTitle(String axisTitle) {
		yAxisTitle = axisTitle;
	}

	public final double[] getData() {
		return data;
	}

	public final void setData(double[] data) {
		this.data = data;
	}

	public final int getBins() {
		return bins;
	}

	public final void setBins(int bins) {
		this.bins = bins;
	}

	public final boolean isLegend() {
		return legend;
	}

	public final void setLegend(boolean legend) {
		this.legend = legend;
	}
	
	

}
