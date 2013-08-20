package com.demshape.dempred.losslunction;

import com.demshape.dempred.datastructure.Datapoint;

public class SidedLorentzian<T extends Datapoint> implements LossFunctionInterface<T> {



	public double g(double x, double y, T datapoint) {
		int group = datapoint.getGroup();
		if (group * x < group * y)
			return Math.log(Math.pow((x - y), 2) + 1);
		else
			return 0;
	}

	public double g_deriv(double x, double y, T datapoint) {
		int group = datapoint.getGroup();
		if (group * x < group * y)
			return (2.0 * x - 2.0 * y) / (Math.pow(x - y, 2) + 1);
		else
			return 0;
	}

	public String getName() {
		return "SidedLorentzian";
	}

}
