package dempred.transformer;

import java.io.Serializable;

public class Exp10Transformation implements TransformationFunctionInterface, Serializable {

	@Override
	public double retransform(double value) {
		return Math.log10(value);
	}

	@Override
	public double transform(double value) {
		return Math.pow(10, value);
	}

}
