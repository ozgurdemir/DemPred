package dempred.grouper;

import java.io.Serializable;

public class AboveThreshold implements GrouperInterface, Serializable {
	
	private static final long serialVersionUID = 4132728525708666484L;
	private double threshold;

	public AboveThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getGroup(double value) {
		if (value > threshold)
			return 1;
		else
			return -1;
	}

	public final double getThreshold() {
		return threshold;
	}

	public final void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
