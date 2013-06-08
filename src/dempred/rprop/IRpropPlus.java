package dempred.rprop;

public class IRpropPlus implements RpropInterface{
	private double[] stepSize;
	private int[] preSign;
	private double[] preWeight;
	private double preError;
	private double rhoplus;
	private double rhominus;
	private double wMax;
	private double wMin;

	public IRpropPlus(int dim, double startvalue) {
		stepSize = new double[dim];
		preSign = new int[dim];
		preWeight = new double[dim];
		preError = 0.0;
		for (int i = 0; i < dim; ++i) {
			stepSize[i] = startvalue;
			preSign[i] = 0;
			preWeight[i] = 0.0;
		}
		rhoplus = 1.2;
		rhominus = 0.5;
		wMax = 50.0;
		wMin = 0.0;
	}
	
	public final void adjust(double[] weightVector, double[] gradientVector, double error) {
		for (int i = 0; i < gradientVector.length; ++i) {
			int gradSign = (int) Math.signum(gradientVector[i]);
			if (gradSign * preSign[i] > 0) {
				stepSize[i] = Math.min(stepSize[i] * rhoplus, wMax);
				preWeight[i] = -gradSign * stepSize[i];
				weightVector[i] += preWeight[i];
			} else if (gradSign * preSign[i] < 0) {
				stepSize[i] = Math.max(stepSize[i] * rhominus, wMin);
				if(error > preError)
					weightVector[i] -= preWeight[i];
				gradSign = 0;
			} else if (gradSign * preSign[i] == 0) {
				preWeight[i] = -gradSign * stepSize[i];
				weightVector[i] += preWeight[i];
			}
			preSign[i] = gradSign;
			preError = error;
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
}
