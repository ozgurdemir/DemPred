package com.demshape.dempred.lossfunction;

import com.demshape.dempred.datastructure.Datapoint;

public interface LossFunctionInterface<T extends Datapoint> {
	
	public double g(double a, double m, T datapoint);

	public double g_deriv(double a, double m, T datapoint);

	public String getName();
}
