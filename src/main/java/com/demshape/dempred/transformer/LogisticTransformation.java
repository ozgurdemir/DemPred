package com.demshape.dempred.transformer;

import java.io.Serializable;

public class LogisticTransformation implements TransformationFunctionInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 425795233130057229L;
	private double alpha;

	public LogisticTransformation() {
		alpha = 1.0;
	}

	public LogisticTransformation(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double retransform(double value) {
		return 0.0;
	}

	@Override
	public double transform(double value) {
		return 1.0 / (1.0 + Math.exp(-alpha * value));
	}

	public final double getAlpha() {
		return alpha;
	}

	public final void setAlpha(double alpha) {
		this.alpha = alpha;
	}

}
