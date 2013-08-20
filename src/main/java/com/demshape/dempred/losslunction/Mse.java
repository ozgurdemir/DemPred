package com.demshape.dempred.losslunction;

import com.demshape.dempred.datastructure.Datapoint;

public class Mse<T extends Datapoint> implements LossFunctionInterface<T> {



	public double g(double x, double m, T datapoint) {
		return Math.pow((x - m), 2);
	}

	public double g_deriv(double x, double m, T datapoint) {
		return 2.0 * (x - m);
	}

	public String getName() {
		return "Mse";
	}
}
