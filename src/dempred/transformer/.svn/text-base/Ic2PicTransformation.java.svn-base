package dempred.transformer;

import java.io.Serializable;


public class Ic2PicTransformation implements TransformationFunctionInterface, Serializable {

	public double transform(double value) {
		return -Math.log10(value * 1e-9);
	}

	public double retransform(double value) {
		return Math.pow(10, -value) / (1e-9);
	}

}
