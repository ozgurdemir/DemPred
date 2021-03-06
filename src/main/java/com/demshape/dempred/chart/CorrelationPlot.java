package com.demshape.dempred.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.DatasetManipulator;
import com.demshape.dempred.math.VectorInterface;


public class CorrelationPlot {
	private String title;
	private Dataset<?> dataset;
	private boolean printFoldError;

	public CorrelationPlot(String title, Dataset<?> dataset) {
		super();
		this.dataset = dataset;
		this.title = title;
	}

	public JFreeChart generateChart() {
		VectorInterface values = DatasetManipulator.getValueVector(dataset);
		double min = values.min();
		double max = values.max();
		// create axis
		ValueAxis domain = new NumberAxis("measured");
		ValueAxis range = new NumberAxis("predicted");

		// scatter data
		XYLineAndShapeRenderer dotRenderer = new XYLineAndShapeRenderer();
		dotRenderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 4.0, 4.0));
		dotRenderer.setSeriesPaint(0, Color.green);
		dotRenderer.setSeriesLinesVisible(0, false);
		XYSeries correlationSeries = new XYSeries("correlation");
		for (Datapoint datapoint : dataset.getDatapoints())
			correlationSeries.add(datapoint.getValue(), datapoint.getPredictedValue());
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(correlationSeries);
		XYPlot plot = new XYPlot(data, domain, range, dotRenderer);

		// diagonal line data
		XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
		lineRenderer.setSeriesShapesVisible(0, false);
		lineRenderer.setSeriesPaint(0, new Color(255, 0, 0));
		XYSeries diagonalSeries = new XYSeries("diagonal");
		diagonalSeries.add(min, min);
		diagonalSeries.add(max, max);
		XYSeriesCollection linedata = new XYSeriesCollection();
		linedata.addSeries(diagonalSeries);

		// fold error data
		if (printFoldError) {
			//2 fold error
			lineRenderer.setSeriesShapesVisible(1, false);
			lineRenderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 10.0f, 6.0f }, 0.0f));
			lineRenderer.setSeriesPaint(1, new Color(0, 0, 255));
			XYSeries diagonalSeriesLowerFold = new XYSeries("LowerFold2");
			double minPow = Math.pow(10, min);
			double maxPow = Math.pow(10, max);
			double diffMin = Math.log10(2 * minPow) - min;
			double diffMax = Math.log10(2 * maxPow) - max;
			diagonalSeriesLowerFold.add(min, min + diffMin);
			diagonalSeriesLowerFold.add(max, max + diffMax);
			linedata.addSeries(diagonalSeriesLowerFold);

			lineRenderer.setSeriesShapesVisible(2, false);
			lineRenderer.setSeriesStroke(2, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 10.0f, 6.0f }, 0.0f));
			lineRenderer.setSeriesPaint(2, new Color(0, 0, 255));
			XYSeries diagonalSeriesUpperFold = new XYSeries("UpperFold2");
			diagonalSeriesUpperFold.add(min, min - diffMin);
			diagonalSeriesUpperFold.add(max, max - diffMax);
			linedata.addSeries(diagonalSeriesUpperFold);
			
			// 3 fold error
			lineRenderer.setSeriesShapesVisible(3, false);
			lineRenderer.setSeriesStroke(3, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 10.0f, 6.0f }, 0.0f));
			lineRenderer.setSeriesPaint(3, new Color(0, 255, 100));
			XYSeries diagonalSeriesLowerFold3 = new XYSeries("LowerFold3");
			minPow = Math.pow(10, min);
			maxPow = Math.pow(10, max);
			diffMin = Math.log10(3 * minPow) - min;
			diffMax = Math.log10(3 * maxPow) - max;
			diagonalSeriesLowerFold3.add(min, min + diffMin);
			diagonalSeriesLowerFold3.add(max, max + diffMax);
			linedata.addSeries(diagonalSeriesLowerFold3);

			lineRenderer.setSeriesShapesVisible(4, false);
			lineRenderer.setSeriesStroke(4, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 10.0f, 6.0f }, 0.0f));
			lineRenderer.setSeriesPaint(4, new Color(0, 255, 100));
			XYSeries diagonalSeriesUpperFold3 = new XYSeries("UpperFold3");
			diagonalSeriesUpperFold3.add(min, min - diffMin);
			diagonalSeriesUpperFold3.add(max, max - diffMax);
			linedata.addSeries(diagonalSeriesUpperFold3);
		}

		plot.setDataset(1, linedata);
		plot.setRenderer(1, lineRenderer);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));

		plot.getDomainAxis().setRange(min, max);
		plot.getRangeAxis().setRange(min, max);

		// ValueAxis domain = plot.getDomainAxis();
		// domain.setRange(min, max);
		// ValueAxis range = plot.getRangeAxis();
		// range.setRange(min, max);

		return new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);

	}

	// *********************************************
	// getters and setters
	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final Dataset<?> getDataset() {
		return dataset;
	}

	public final void setDataset(Dataset<?> dataset) {
		this.dataset = dataset;
	}

	public final boolean isFoldError() {
		return printFoldError;
	}

	public final void setFoldError(boolean foldError) {
		this.printFoldError = foldError;
	}

}
