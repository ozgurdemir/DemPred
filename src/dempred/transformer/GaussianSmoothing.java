package dempred.transformer;

import dempred.math.SimpleVector;
import dempred.math.VectorInterface;

public class GaussianSmoothing {
	private double[] x;
	private double[] y;
	private double[] weights;
	private int index;

	public GaussianSmoothing(double[] x, double[] y) {
		this.x = x;
		this.y = y;
		this.weights = new double[x.length];
		index = 0;
	}

	public final double calcValue(double value) {
		// if (x < smallest || x > largest)
		// throw new IllegalArgumentException(String.format("%.4f is out of range (%.4f...%.4f) and cannot be interpolated", x, smallest, largest));
		double weighting = 0.0;
		//System.out.println("***********************");
		++index;
		for (int i = 0; i < x.length; ++i) {
			weights[i] = gauss(Math.abs(x[i] - value), 0.5, 0.0);
			weighting += weights[i];
		}
		VectorInterface weightVector = new SimpleVector(weights);
		double result = 0.0;
		for (int i = 0; i < x.length; ++i) {
			weights[i] /= weighting;
			result += weights[i] * y[i];
		}
		// SimpleLineChart lineChart2 = new SimpleLineChart("g", "y", "x", x, weights, false);
		// ChartTools.showChartAsFrame(lineChart2.generateChart());
		// ChartTools.saveChartAsJPG("/user/odemir/coepradatasets/programoutputs/gauss/"+index, lineChart2.generateChart(), 400, 400);
		return result;
	}

	public static final double gauss(double x, double sigma, double mu) {
		return (1.0 / (sigma * Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * (Math.pow((x - mu) / sigma, 2))));
	}
}
