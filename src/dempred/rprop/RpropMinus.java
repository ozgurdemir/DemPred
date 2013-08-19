package dempred.rprop;

public class RpropMinus implements RpropInterface {
	private double[] stepSize;
	private int[] preSign;
	private double rhoplus;
	private double rhominus;
	private double wMax;
	private double wMin;

	public RpropMinus(int dim, double startvalue) {
		stepSize = new double[dim];
		preSign = new int[dim];
		for (int i = 0; i < dim; ++i) {
			stepSize[i] = startvalue;
			preSign[i] = 0;
		}
		rhoplus = 1.2;
		rhominus = 0.5;
		wMax = 50.0;
		wMin = 0.0;
	}

	public final void adjust(double[] weightVector, double[] gradientVector, double error) {
		for (int i = 0; i < gradientVector.length; ++i) {
			int gradSign = (int) Math.signum(gradientVector[i]);
			if (gradSign * preSign[i] > 0)
				stepSize[i] = Math.min(stepSize[i] * rhoplus, wMax);
			else if (gradSign * preSign[i] < 0)
				stepSize[i] = Math.max(stepSize[i] * rhominus, wMin);
			preSign[i] = gradSign;
			weightVector[i] -= gradSign * stepSize[i];
		}
	}

	public final double getRhoplus() {
		return rhoplus;
	}

	public final void setRhoplus(double rhoplus) {
		this.rhoplus = rhoplus;
	}

	public final double getRhominus() {
		return rhominus;
	}

	public final void setRhominus(double rhominus) {
		this.rhominus = rhominus;
	}

	public final double getWMax() {
		return wMax;
	}

	public final void setWMax(double max) {
		wMax = max;
	}

	public final double getWMin() {
		return wMin;
	}

	public final void setWMin(double min) {
		wMin = min;
	}

	public double[] getStepSize() {
		return stepSize;
	}

}
