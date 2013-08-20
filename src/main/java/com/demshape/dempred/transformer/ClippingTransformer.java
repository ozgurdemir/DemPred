package com.demshape.dempred.transformer;

public class ClippingTransformer implements TransformationFunctionInterface {

	private double minValue;
	private double maxValue;
	private double minReplaceValue;
	private double maxReplaceValue;

	public ClippingTransformer(double minValue, double maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.minReplaceValue = minValue;
		this.maxReplaceValue = maxValue;
	}

	public ClippingTransformer(double minValue, double minReplaceValue, double maxValue, double maxReplaceValue) {
		super();
		this.minValue = minValue;
		this.minReplaceValue = minReplaceValue;
		this.maxValue = maxValue;
		this.maxReplaceValue = maxReplaceValue;
	}

	@Override
	public double transform(double value) {
		if (value > maxValue)
			return maxReplaceValue;
		if (value < minValue)
			return minReplaceValue;
		return value;
	}

	@Override
	public double retransform(double value) {
		throw new UnsupportedOperationException("This Transformer does not have a retransform operation");
	}

	// getters and setters
	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

}
