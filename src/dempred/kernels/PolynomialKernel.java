package dempred.kernels;

import java.io.Serializable;

import dempred.math.DenseVector;
import dempred.math.VectorInterface;

/**
 * An PolynomialKernel.
 */
public class PolynomialKernel implements KernelInterface, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7127559150918541972L;
	
	/** The factor. */
	private double factor;
	
	/** The degree. */
	private double degree;
	
	/** The offset. */
	private double offset;

	/**
	 * Instantiates a new polynomial kernel of degree.
	 *
	 * @param degree the degree
	 */
	public PolynomialKernel(double degree) {
		setDegree(degree);
		setFactor(1.0);
		setOffset(1.0);
	}

	/**
	 * Instantiates a new polynomial kernel.
	 *
	 * @param degree the degree
	 * @param factor the factor
	 */
	public PolynomialKernel(double degree, double factor) {
		setDegree(degree);
		setFactor(factor);
		setOffset(1.0);
	}

	/**
	 * Instantiates a new polynomial kernel.
	 *
	 * @param degree the degree
	 * @param factor the factor
	 * @param offset the offset
	 */
	public PolynomialKernel(double degree, double factor, double offset) {
		setDegree(degree);
		setFactor(factor);
		setOffset(offset);
	}

	/* (non-Javadoc)
	 * @see dempred.kernels.KernelInterface#evaluate(dempred.fmath.FVector, dempred.fmath.FVector)
	 */
	@Override
	public double evaluate(VectorInterface a, VectorInterface b) {
		return Math.pow(factor * a.scalarProduct(b) + offset, degree);
	}

	/**
	 * Gets the degree.
	 *
	 * @return the degree
	 */
	public final double getDegree() {
		return degree;
	}

	/**
	 * Sets the degree.
	 *
	 * @param degree the new degree
	 */
	public final void setDegree(double degree) {
		this.degree = degree;
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
