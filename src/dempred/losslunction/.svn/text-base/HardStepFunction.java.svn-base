package dempred.losslunction;

import dempred.datastructure.Datapoint;

public class HardStepFunction<T extends Datapoint> implements LossFunctionInterface<T> {

	private double threshold;

	public HardStepFunction(double threshold) {
		this.threshold = threshold;
	}

	public double g(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		if (group * x > group * threshold)
			return 0;
		else
			return 1;
	}

	public double g_deriv(double x, double m, T datapoint) {
		return 0;
	}

	public String getName() {
		return "HardStep";
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
