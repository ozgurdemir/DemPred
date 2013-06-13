package dempred.kernels;

import java.io.Serializable;

import dempred.math.DenseVector;
import dempred.math.VectorInterface;


/**
 * A Sigmoidial Kernel.
 */
public class SigmoidKernel implements KernelInterface, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6082112731711339252L;
	
	/** The factor. */
	private double factor;
	
	/** The offset. */
	private double offset;

	/**
	 * Instantiates a new sigmoid kernel.
	 */
	public SigmoidKernel() {
		setFactor(1.0);
		setOffset(1.0);
	}

	/**
	 * Instantiates a new sigmoid kernel.
	 *
	 * @param factor the factor
	 */
	public SigmoidKernel(double factor) {
		setFactor(factor);
		setOffset(1.0);
	}

	/**
	 * Instantiates a new sigmoid kernel.
	 *
	 * @param factor the factor
	 * @param offset the offset
	 */
	public SigmoidKernel(double factor, double offset) {
		setFactor(factor);
		setOffset(offset);
	}

	/* (non-Javadoc)
	 * @see dempred.kernels.KernelInterface#evaluate(dempred.fmath.FVector, dempred.fmath.FVector)
	 */
	@Override
	public double evaluate(VectorInterface a, VectorInterface b) {
		Math.tanh(factor * a.scalarProduct(b) + offset);
		return 0;
	}

	/**
	 * Gets the factor.
	 *
	 * @return the factor
	 */
	public final double getFactor() {
		return factor;
	}

	/**
	 * Sets the factor.
	 *
	 * @param factor the new factor
	 */
	public final void setFactor(double factor) {
		this.factor = factor;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public final double getOffset() {
		return offset;
	}

	/**
	 * Sets the offset.
	 *
	 * @param offset the new offset
	 */
	public final void setOffset(double offset) {
		this.offset = offset;
	}

}
