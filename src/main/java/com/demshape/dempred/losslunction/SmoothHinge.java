package com.demshape.dempred.losslunction;

import com.demshape.dempred.datastructure.Datapoint;

public class SmoothHinge<T extends Datapoint> implements LossFunctionInterface<T> {

	public double g(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		if ((group * x) <= (group * m - 1))
			return 0.5 - group * x;
		if (((group * m - 1) < (group * x)) && ((group * x) < (group * m)))
			return 0.5 * Math.pow(m - x, 2);
		return 0;
	}

	public double g_deriv(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		if ((group * x) <= ((group * m) - 1))
			return -1.0 * group;
		if (((group * m - 1) < (group * x)) && ((group * x) < (group * m)))
			return -m + x;
		return 0;
	}

	public String getName() {
		return "SmoothHinge";
	}

}
