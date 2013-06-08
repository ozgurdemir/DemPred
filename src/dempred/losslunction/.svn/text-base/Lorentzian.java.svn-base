package dempred.losslunction;

import dempred.datastructure.Datapoint;

public class Lorentzian<T extends Datapoint> implements LossFunctionInterface<T> {


	public double g(double x, double m, T datapoint) {
		return Math.log(Math.pow((x - m), 2) + 1);
	}

	public double g_deriv(double x, double m, T datapoint) {
		return (2.0 * x - 2.0 * m) / (Math.pow(x - m, 2) + 1);
	}

	public String getName() {
		return "Lorentzian";
	}

}
