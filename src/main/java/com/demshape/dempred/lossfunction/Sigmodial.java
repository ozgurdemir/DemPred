package com.demshape.dempred.lossfunction;

import com.demshape.dempred.datastructure.Datapoint;

public class Sigmodial<T extends Datapoint> implements LossFunctionInterface<T> {

	private double sigma;
	
	public Sigmodial(double sigma){
		setSigma(sigma);
	}

	public double g(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		return (1 / (1 + Math.exp(group * sigma * (x - m + group))));
	}

	public double g_deriv(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		return -1.0 / Math.pow(1 + Math.exp(group * sigma * (x - m + group)), 2) * group * sigma * Math.exp(group * sigma * (x - m + group));
	}

	public String getName() {
		return "Sigmodial";
	}

	public final double getSigma() {
		return sigma;
	}

	public final void setSigma(double sigma) {
		if (sigma <= 0)
			throw new IllegalArgumentException("The slope parameter alpha must not be smaller or equal to zero!");
		this.sigma = sigma;
	}

}
