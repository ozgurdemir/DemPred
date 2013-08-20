package com.demshape.dempred.transformer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

import com.demshape.dempred.chart.ChartTools;
import com.demshape.dempred.chart.Histogramm;
import com.demshape.dempred.chart.SimpleLineChart;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorInterface;



public class GaussTransformation implements TransformationFunctionInterface, Serializable {
	private static final Logger logger = Logger.getLogger(GaussTransformation.class.getName());
	private double[] g_rev_x;
	private double[] g_rev_y;
	private LinearInterpolation linInter_g_rev;
	private LinearInterpolation linInter_g;
	private LinearInterpolation linInter_g_g_rev;

	private GaussianSmoothing gaussInter_g_rev;
	private GaussianSmoothing gaussInter_g;

	private double ymean;
	private double ysecondMoment;
	private double divider;
	private boolean showCharts = false;

	public GaussTransformation(Dataset<?> dataset) {
		int numDatapoints = dataset.size();
		VectorInterface regressionValues = new DenseVector(numDatapoints);
		for (int i = 0; i < numDatapoints; ++i)
			regressionValues.set(i, dataset.getDatapoint(i).getValue());
		Arrays.sort(regressionValues.getElements());
		// *********************************************************************
		// create fake data
		int numPoints = 10000;
		double[] startx = new double[numPoints];
		double[] starty = new double[numPoints];
		double start = -10;
		double end = 10;
		double stepsize = (end - start) / (numPoints - 1);
		for (int i = 0; i < numPoints; ++i) {
			startx[i] = start + i * stepsize;
			starty[i] = g_g(startx[i]);
		}
		linInter_g_g_rev = new LinearInterpolation(starty, startx);
		logger.fine(String.format("fake function largest:%.8f smallest:%.8f%n", linInter_g_g_rev.getLargest(), linInter_g_g_rev.getSmallest()));
		double[] error_rex = new double[numDatapoints];
		double[] error_rexy = new double[numDatapoints];
		start = 0.0;
		end = 1;
		stepsize = (end - start) / (numDatapoints - 1);
		for (int i = 0; i < numDatapoints; ++i) {
			error_rex[i] = start + i * stepsize;
			error_rexy[i] = linInter_g_g_rev.calcValue(error_rex[i]);
		}

		// regressionValues.setElements(error_rexy);

		// *********************************************************************
		g_rev_x = new double[numDatapoints];
		g_rev_y = new double[numDatapoints];
		ymean = regressionValues.mean();
		ysecondMoment = regressionValues.clone().powScalar(2).mean();
		divider = Math.pow((ysecondMoment - Math.pow(ymean, 2)), 0.5);
		start = 0.01;
		end = 0.99;
		stepsize = (end - start) / (numDatapoints - 1);
		for (int i = 0; i < numDatapoints; ++i) {
			g_rev_x[i] = start + i * stepsize;
			g_rev_y[i] = (regressionValues.get(i) - ymean) / Math.pow((ysecondMoment - Math.pow(ymean, 2)), 0.5);
		}
		Histogramm hist = new Histogramm("hist before transformation", "ic value", "occurence", g_rev_y, false);
		if (showCharts)
			ChartTools.showChartAsFrame(hist.generateChart());

		// *********************************************************************
		// generiere g_rev und g
		linInter_g_rev = new LinearInterpolation(g_rev_x, g_rev_y);
		logger.fine(String.format("g_rev largest:%.8f smallest:%.8f%n", linInter_g_rev.getLargest(), linInter_g_rev.getSmallest()));
		linInter_g = new LinearInterpolation(g_rev_y, g_rev_x);
		logger.fine(String.format("g largest:%.8f smallest:%.8f%n", linInter_g.getLargest(), linInter_g.getSmallest()));

		// g_rev function
		SimpleLineChart lineChart = new SimpleLineChart("g_rev", "x", "y", g_rev_x, g_rev_y, false);
		if (showCharts)
			ChartTools.showChartAsFrame(lineChart.generateChart());

		// g function
		SimpleLineChart lineChart2 = new SimpleLineChart("g", "y", "x", g_rev_y, g_rev_x, false);
		if (showCharts)
			ChartTools.showChartAsFrame(lineChart2.generateChart());

		// *********************************************************************
		// equidistant g function
		double[] g_equi_x = new double[numDatapoints];
		double[] g_equi_y = new double[numDatapoints];
		double deltaY = (g_rev_y[numDatapoints - 1] - g_rev_y[0]) / numDatapoints;
		for (int i = 0; i < numDatapoints; ++i) {
			g_equi_y[i] = g_rev_y[0] + i * deltaY;
			g_equi_x[i] = linInter_g.calcValue(g_equi_y[i]);
		}
		SimpleLineChart lineChart3 = new SimpleLineChart("g_equidistant", "y", "x", g_equi_y, g_equi_x, false);
		if (showCharts)
			ChartTools.showChartAsFrame(lineChart3.generateChart());

		// *********************************************************************
		// smoothed g function
//		gaussInter_g = new GaussianSmoothing(g_equi_y, g_equi_x);
//		double[] g_gauss_x = new double[numDatapoints];
//		double[] g_gauss_y = new double[numDatapoints];
//		deltaY = (g_rev_y[numDatapoints - 1] - g_rev_y[0]) / numDatapoints;
//		for (int i = 0; i < numDatapoints; ++i) {
//			g_gauss_y[i] = g_equi_y[0] + i * deltaY;
//			g_gauss_x[i] = gaussInter_g.calcValue(g_gauss_y[i]);
//		}
//		lineChart3 = new SimpleLineChart("g_smoothed", "y", "x", g_gauss_y, g_gauss_x, false);
//		if (showCharts)
//			ChartTools.showChartAsFrame(lineChart3.generateChart());

		// equidistant g_rev function
		// double[] g_rev_equi_x = new double[numDatapoints];
		// double[] g_rev_equi_y = new double[numDatapoints];
		// double deltax = (g_rev_x[numDatapoints - 1] - g_rev_x[0]) / numDatapoints;
		// for (int i = 0; i < numDatapoints; ++i) {
		// g_rev_equi_x[i] = g_rev_x[0] + i * deltax;
		// g_rev_equi_y[i] = linInter_g_rev.calcValue(g_rev_equi_x[i]);
		// }
		// SimpleLineChart lineChart7 = new SimpleLineChart("g_rev_equidistant", "x", "y", g_rev_equi_x, g_rev_equi_y, false);
		// ChartTools.showChartAsFrame(lineChart7.generateChart());

		// transformation function
		double[] testx = new double[numDatapoints];
		double[] testy = new double[numDatapoints];
		for (int i = 0; i < numDatapoints; ++i) {
			testx[i] = g_rev_y[i];
			testy[i] = linInter_g_g_rev.calcValue(linInter_g.calcValue(g_rev_y[i]));
			//testy[i] = linInter_g_g_rev.calcValue(gaussInter_g.calcValue(g_rev_y[i]));
		}
		SimpleLineChart lineChart4 = new SimpleLineChart("transformation function f(y)", "y", "y", testx, testy, false);
		if (showCharts)
			ChartTools.showChartAsFrame(lineChart4.generateChart());

		// transformed values
		double[] transx = new double[numDatapoints];
		double[] transy = new double[numDatapoints];
		for (int i = 0; i < numDatapoints; ++i) {
			transx[i] = g_rev_x[i];
			transy[i] = linInter_g_g_rev.calcValue(linInter_g.calcValue(g_rev_y[i]));
			//transy[i] = linInter_g_g_rev.calcValue(gaussInter_g.calcValue(g_rev_y[i]));
		}
		SimpleLineChart lineChart5 = new SimpleLineChart("transformed g", "y", "x", transy, transx, false);
		if (showCharts)
			ChartTools.showChartAsFrame(lineChart5.generateChart());

		// // g_g function
		int steps = 200;
		testx = new double[steps];
		testy = new double[steps];
		for (int i = 0; i < steps; ++i) {
			testx[i] = -10 + i * 0.1;
			testy[i] = g_g(testx[i]);
		}
		SimpleLineChart lineChart6 = new SimpleLineChart("g_g", "x", "y", testx, testy, false);
		if (showCharts)
			ChartTools.showChartAsFrame(lineChart6.generateChart());

		// // g_g_rev function
		// double[] x = new double[steps];
		// double[] y = new double[steps];
		// for (int i = 0; i < testy.length; ++i) {
		// y[i] = testy[i];
		// x[i] = erf_ref(testy[i]);
		// }
		// System.out.println(regressionValues);
		// SimpleLineChart testChart = new SimpleLineChart("g_g_rev", "y", "x", y, x, false);
		// ChartTools.showChartAsFrame(testChart.generateChart());

		Histogramm hist2 = new Histogramm("transformed hist", "ic value", "occurence", transy, false);
		hist2.setBins(20);
		if (showCharts)
			ChartTools.showChartAsFrame(hist2.generateChart());

		double[] derivativex = new double[steps];
		double[] derivativey = new double[steps];
		for (int i = 0; i < steps; ++i) {
			derivativex[i] = -10 + i * 0.1;
			derivativey[i] = g_g_der(derivativex[i]);
		}
		SimpleLineChart lineChart8 = new SimpleLineChart("derivative", "x", "y", derivativex, derivativey, false);
		// ChartTools.showChartAsFrame(lineChart8.generateChart());
	}

	public double transform(double x) {
		double minInter = linInter_g.getSmallest();
		double maxInter = linInter_g.getLargest();
		double temp = (x - ymean) / divider;
		if (temp < minInter) {
			temp = minInter;
			System.out.println("transform smaller than interpolation:" + x);
		} else if (temp > maxInter) {
			temp = maxInter;
			System.out.println("transform larger than interpolation:" + x);
		}
		return (linInter_g_g_rev.calcValue(linInter_g.calcValue(temp)));
	}

	public double retransform(double x) {
		double minInter = linInter_g_rev.getSmallest();
		double maxInter = linInter_g_rev.getLargest();
		double temp = g_g(x);
		if (temp < minInter) {
			temp = minInter;
			System.out.format("retransform smaller than interpolation: %.8f < %.8f%n", temp, minInter);
		} else if (temp > maxInter) {
			temp = maxInter;
			System.out.format("retransform larger than interpolation: %.8f > %.8f%n", temp, maxInter);
		}
		return (linInter_g_rev.calcValue(temp) * divider + ymean);
	}
	
	public void transform(Datapoint datapoint) {
		datapoint.setValue(transform(datapoint.getValue()));
		datapoint.setPredictedValue(transform(datapoint.getPredictedValue()));
	}

	public void retransform(Datapoint datapoint) {
		datapoint.setValue(retransform(datapoint.getValue()));
		datapoint.setPredictedValue(retransform(datapoint.getPredictedValue()));
	}

	private static final double g_g(double x) {
		final double fac = Math.sqrt(2);
		return 0.5 * (1 + erf(x / fac));
	}

	private static final double g_g_rev(double x) {
		final double fac = Math.sqrt(2);
		return fac * erf_rev(2 * x - 1);
	}

	private static final double g_g_der(double x) {
		final double fac = 1.0 / Math.sqrt(2.0 * Math.PI);
		return fac * Math.exp(-1 * (Math.pow(x, 2) / 2.0));
	}

	// *********************************************************
	// error functions
	public static final double erf(double x) {
		if (x < 0)
			return -erf(-x);
		final double p = 0.47047;
		final double a1 = 0.34802;
		final double a2 = -0.09587;
		final double a3 = 0.74785;
		double t = 1.0 / (1.0 + p * x);
		double result = 1.0 - (a1 * t + a2 * Math.pow(t, 2) + a3 * Math.pow(t, 3)) * Math.exp(-1.0 * Math.pow(x, 2));
		return result;
	}

	public static final double erf_rev(double x) {
		final double pi = Math.PI;
		final double fac_0 = 0.5 * Math.sqrt(pi);
		final double fac_1 = pi / 12.0;
		final double fac_2 = (7.0 * Math.pow(pi, 2)) / 480.0;
		final double fac_3 = (127.0 * Math.pow(pi, 3)) / 40320.0;
		final double fac_4 = (4369.0 * Math.pow(pi, 4)) / 5806080;
		final double fac_5 = (34807.0 * Math.pow(pi, 5)) / 182476800;
		return fac_0 * (x + fac_1 * Math.pow(x, 3) + fac_2 * Math.pow(x, 5) + fac_3 * Math.pow(x, 7) + fac_4 * Math.pow(x, 9) + fac_5 * Math.pow(x, 11));
	}
}
