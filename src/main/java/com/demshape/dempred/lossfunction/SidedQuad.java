package com.demshape.dempred.lossfunction;

import com.demshape.dempred.datastructure.Datapoint;

public class SidedQuad<T extends Datapoint> implements LossFunctionInterface<T> {



	public double g(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		if (group * x < group * m) {
			return Math.pow((x - m), 2);
		} else
			return 0;
	}

	public double g_deriv(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		if (group * x < group * m) {
			return 2.0 * (x - m);
		} else
			return 0;
	}

	public String getName() {
		return "SidedQuad";
	}

}
