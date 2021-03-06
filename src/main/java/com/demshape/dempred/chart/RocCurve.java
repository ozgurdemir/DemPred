package com.demshape.dempred.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;


public class RocCurve {
	public static int width = 600;
	public static int height = 400;

	public static JFreeChart showRocCurve(String datasetName, Dataset<?> trainset, Dataset<?> testset) {
		XYSeries rocTrainSeries = new XYSeries("trainset");
		for (RocPoint rocPoint : getRocPoints(trainset))
			rocTrainSeries.add(rocPoint.getFpr(), rocPoint.getTpr());

		XYSeries rocTestSeries = new XYSeries("testset");
		for (RocPoint rocPoint : getRocPoints(testset))
			rocTestSeries.add(rocPoint.getFpr(), rocPoint.getTpr());
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(rocTrainSeries);
		data.addSeries(rocTestSeries);

		JFreeChart chart = ChartFactory.createXYLineChart("ROC Curve " + datasetName, "fpr (1-specificity)", "tpr (sensitivity)", data, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		// plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.green);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setRange(0.0, 1.0);
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(0.0, 1.0);

		return chart;
	}
	
	public static JFreeChart showRocCurve(String datasetName, Dataset<?> trainset) {
		XYSeries rocTrainSeries = new XYSeries("trainset");
		for (RocPoint rocPoint : getRocPoints(trainset))
			rocTrainSeries.add(rocPoint.getFpr(), rocPoint.getTpr());

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(rocTrainSeries);

		JFreeChart chart = ChartFactory.createXYLineChart("ROC Curve " + datasetName, "fpr (1-specificity)", "tpr (sensitivity)", data, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		// plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.green);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setRange(0.0, 1.0);
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(0.0, 1.0);

		return chart;
	}

	public static ArrayList<RocPoint> getRocPoints(Dataset<?> dataset) {
		int p = dataset.groupQuantity(1);
		int n = dataset.groupQuantity(-1);
		int fp = 0;
		int tp = 0;
		Collections.sort(dataset.getDatapoints(), new Datapoint.PredictedValueComparatorDesc());
		ArrayList<RocPoint> r = new ArrayList<RocPoint>(dataset.size());
		double fprev = Double.NEGATIVE_INFINITY;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			if (datapoint.getPredictedValue() != fprev) {
				r.add(new RocCurve.RocPoint((double) fp / n, (double) tp / p, datapoint.getPredictedValue()));
				fprev = datapoint.getPredictedValue();
			}
			if (datapoint.getGroup() == 1)
				tp++;
			else
				fp++;
		}
		r.add(new RocCurve.RocPoint((double) fp / n, (double) tp / p, 1.0));
		return r;
	}

	// innere klasse fur rocPunkte
	public static class RocPoint {
		private double fpr;
		private double tpr;
		private double threshold;

		public RocPoint(double fpr, double tpr, double threshold) {
			this.fpr = fpr;
			this.tpr = tpr;
			this.threshold = threshold;
		}

		public final double getFpr() {
			return fpr;
		}

		public final void setFpr(double fpr) {
			this.fpr = fpr;
		}

		public final double getTpr() {
			return tpr;
		}

		public final void setTpr(double tpr) {
			this.tpr = tpr;
		}

		public final double getThreshold() {
			return threshold;
		}

		public final void setThreshold(double threshold) {
			this.threshold = threshold;
		}

		public String toString() {
			return String.format("fpr:%.4f tpr:%.4f threshold:%.4f", fpr, tpr, threshold);
		}

	}

}