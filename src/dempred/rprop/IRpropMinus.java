package dempred.rprop;

public class IRpropMinus implements RpropInterface {

	private double[] stepSize;
	private int[] preSign;
	private double rhoplus;
	private double rhominus;
	private double wMax;
	private double wMin;

	public IRpropMinus(int dim, double startvalue) {
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

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#adjust(double[], double[])
	 */
	public final void adjust(double[] weightVector, double[] gradientVector, double error) {
		for (int i = 0; i < gradientVector.length; ++i) {
			int gradSign = (int) Math.signum(gradientVector[i]);
			if (gradSign * preSign[i] > 0)
				stepSize[i] = Math.min(stepSize[i] * rhoplus, wMax);
			else if (gradSign * preSign[i] < 0){
				stepSize[i] = Math.max(stepSize[i] * rhominus, wMin);
				gradSign = 0;
			}
			preSign[i] = gradSign;
			weightVector[i] -= gradSign * stepSize[i];
		}
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#getRhoplus()
	 */
	public final double getRhoplus() {
		return rhoplus;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#setRhoplus(double)
	 */
	public final void setRhoplus(double rhoplus) {
		this.rhoplus = rhoplus;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#getRhominus()
	 */
	public final double getRhominus() {
		return rhominus;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#setRhominus(double)
	 */
	public final void setRhominus(double rhominus) {
		this.rhominus = rhominus;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#getWMax()
	 */
	public final double getWMax() {
		return wMax;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#setWMax(double)
	 */
	public final void setWMax(double max) {
		wMax = max;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#getWMin()
	 */
	public final double getWMin() {
		return wMin;
	}

	/* (non-Javadoc)
	 * @see optimizer.RpropInterface2#setWMin(double)
	 */
	public final void setWMin(double min) {
		wMin = min;
	}

}
