package dempred.losslunction;

import dempred.datastructure.Datapoint;

public class LogisticRegression<T extends Datapoint> implements LossFunctionInterface<T> {

	public LogisticRegression() {

	}

	public double g(double f, double m, T datapoint) {
		double group = datapoint.getGroup();
		return Math.log(1.0 + Math.exp(-group * f));
	}

	public double g_deriv(double f, double m, T datapoint) {
		double group = datapoint.getGroup();
		return -(group / (Math.exp(group * f) + 1.0));
	}

	public String getName() {
		return "LogisticRegression";
	}
}
