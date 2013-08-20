package com.demshape.dempred.grouper;

public class BelowThreshold implements GrouperInterface {
	private double threshold;

	public BelowThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getGroup(double value) {
		if (value < threshold)
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
