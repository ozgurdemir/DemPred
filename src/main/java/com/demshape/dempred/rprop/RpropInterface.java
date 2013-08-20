package com.demshape.dempred.rprop;

public interface RpropInterface {

	public abstract void adjust(double[] weightVector, double[] gradientVector, double error);

	public abstract double getRhoplus();

	public abstract void setRhoplus(double rhoplus);

	public abstract double getRhominus();

	public abstract void setRhominus(double rhominus);

	public abstract double getWMax();

	public abstract void setWMax(double max);

	public abstract double getWMin();

	public abstract void setWMin(double min);

}