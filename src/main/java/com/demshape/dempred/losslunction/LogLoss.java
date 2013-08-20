package com.demshape.dempred.losslunction;

import com.demshape.dempred.datastructure.Datapoint;

public class LogLoss<T extends Datapoint> implements LossFunctionInterface<T> {
	private double eps;

	public LogLoss() {
		super();
		this.eps = 0.00001;
	}
	
	public LogLoss(double eps) {
		super();
		this.eps = eps;
	}

	public double g(double f, double m, T datapoint) {
		double logLoss = 0.0;
		f = Math.max(f, eps);
		f = Math.min(f, 1.0 - eps);
		logLoss += m * Math.log(f) + (1.0 - m) * Math.log(1.0 - f);
		return -1.0 * logLoss;
	}

	public double g_deriv(double f, double m, T datapoint) {
		double logLoss = 0.0;
		f = Math.max(f, eps);
		f = Math.min(f, 1.0 - eps);
		logLoss += m * 1.0/f + (1.0 - m) * 1.0/(1.0 - f);
//		System.out.format("logLoss:%.4f | predictedValue:%.4f | measuredValue:%.4f%n", logLoss, f, m);
		return -1.0 * logLoss;
	}

	public String getName() {
		return "LogLoss";
	}

	public final double getEps() {
		return eps;
	}

	public final void setEps(double eps) {
		this.eps = eps;
	}
	
	
}
