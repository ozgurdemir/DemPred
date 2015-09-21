package com.demshape.dempred.lossfunction;

import com.demshape.dempred.datastructure.Datapoint;

public class Bnll<T extends Datapoint> implements LossFunctionInterface<T> {


	// !!! Achtung fehler bei grossen zahlen, da exp(x) von zahlen groesser 600 nicht berechnet werden kann !!!
	public double g(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		return Math.log(1.0 + Math.exp(group * (-x + m - group)));
		
//		return Math.log(1.0 + Math.exp(-group * f));
	}

	public double g_deriv(double x, double m, T datapoint) {
		int group = datapoint.getGroup();
		return (-group * Math.exp(group * (-x + m - group)) / (1.0 + Math.exp(group * (-x + m - group))));
	}

	public String getName() {
		return "BNLL";
	}
}
