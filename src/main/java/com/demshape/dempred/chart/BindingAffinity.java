package com.demshape.dempred.chart;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;


public class BindingAffinity {

	public static JFreeChart plotBindingAffinitiesWithRegression(String title, Dataset<?> dataset, double threshold) {
		List<Datapoint> datapoints = new ArrayList<Datapoint>(dataset.getDatapoints());
		//Collections.sort(datapoints, new Datapoint.ValueComparatorDesc());
		XYSeries exptrueSeries = new XYSeries("exp. (corr.)");
		XYSeries expfalseSeries = new XYSeries("exp. (incorr.)");
		XYSeries predTrueSeries = new XYSeries("pred. (corr.)");
		XYSeries predFalseSeries = new XYSeries("pred. (incorr.)");
		XYSeries thresholdSeries = new XYSeries("threshold");
		for (int i = 0; i < datapoints.size(); ++i) {
			if (datapoints.get(i).getGroup() == datapoints.get(i).getPredictedGroup()){
				exptrueSeries.add(i, datapoints.get(i).getValue());
				predTrueSeries.add(i, datapoints.get(i).getPredictedValue());	
			}
			else{
				expfalseSeries.add(i, datapoints.get(i).getValue());
				predFalseSeries.add(i, datapoints.get(i).getPredictedValue());
			}
			
			thresholdSeries.add(i, threshold);
		}
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(thresholdSeries);
		data.addSeries(predTrueSeries);
		data.addSeries(predFalseSeries);
		data.addSeries(exptrueSeries);
		data.addSeries(expfalseSeries);

		JFreeChart chart = ChartFactory.createScatterPlot(title, "datapoint i", "measured / predicted", data, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		XYItemRenderer renderer = plot.getRenderer();
		// threshold
		renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 1.0, 1.0));
		renderer.setSeriesPaint(0, Color.black);
		// predCorr.
		renderer.setSeriesShape(1, new Ellipse2D.Double(-3.0, -3.0, 2.0, 2.0));
		renderer.setSeriesPaint(1, Color.green);
		// predInCorr.
		renderer.setSeriesShape(2, new Ellipse2D.Double(-3.0, -3.0, 2.0, 2.0));
		renderer.setSeriesPaint(2, Color.red);
		// exp Corr.
		renderer.setSeriesShape(3, new Ellipse2D.Double(-3.0, -3.0, 2.0, 2.0));
		renderer.setSeriesPaint(3, Color.blue);
		// exp Incor.
		renderer.setSeriesShape(4, new Ellipse2D.Double(-3.0, -3.0, 2.0, 2.0));
		renderer.setSeriesPaint(4, Color.yellow);

		return (chart);
	}

	public static JFreeChart plotBindingAffinitiesOnly(String title, Dataset<?> dataset, double threshold) {
		List<Datapoint> datapoints = new ArrayList<Datapoint>(dataset.getDatapoints());
		Collections.sort(datapoints, new Datapoint.ValueComparatorDesc());
		XYSeries expSeries = new XYSeries("exp.");
		XYSeries thresholdSeries = new XYSeries("threshold");
		for (int i = 0; i < datapoints.size(); ++i) {
			expSeries.add(i, datapoints.get(i).getValue());
			thresholdSeries.add(i, threshold);
		}
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(thresholdSeries);
		data.addSeries(expSeries);

		JFreeChart chart = ChartFactory.createScatterPlot(title, "datapoint i", "binding affinity (pIC50)", data, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		XYItemRenderer renderer = plot.getRenderer();
		// threshold
		renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 1.0, 1.0));
		renderer.setSeriesPaint(0, Color.black);
		// exp.
		renderer.setSeriesShape(1, new Ellipse2D.Double(-3.0, -3.0, 4.0, 4.0));
		renderer.setSeriesPaint(1, Color.green);

		return (chart);

	}
}
