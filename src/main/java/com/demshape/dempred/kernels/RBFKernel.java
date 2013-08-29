package com.demshape.dempred.kernels;

import java.io.Serializable;

import com.demshape.dempred.math.VectorInterface;




/**
 * A RBF Kernel.
 */
public class RBFKernel implements KernelInterface, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4384837381056056453L;
	
	/** The sigma. */
	private double sigma;

	/**
	 * Instantiates a new rBF kernel.
	 *
	 * @param sigma the sigma
	 */
	public RBFKernel(double sigma) {
		setSigma(sigma);
	}

	/* (non-Javadoc)
	 * @see dempred.kernels.KernelInterface#evaluate(dempred.fmath.FVector, dempred.fmath.FVector)
	 */
	@Override
	public double evaluate(VectorInterface a, VectorInterface b) {
		return Math.exp( sigma * (a.clone().subVector(b)).norm(2.0) );
	}
	
	/**
	 * Gets the sigma.
	 *
	 * @return the sigma
	 */
	public final double getSigma() {
		return sigma;
	}

	/**
	 * Sets the sigma.
	 *
	 * @param sigma the new sigma
	 */
	public final void setSigma(double sigma) {
		if (sigma <= 0)
			throw new IllegalArgumentException("Sigma must not be smaller than 0");
		this.sigma = -1.0d / (2.0d * sigma * sigma);
	}
	
	/**
	 * Sets the gamma.
	 *
	 * @param gamma the new gamma
	 */
	public final void setGamma(double gamma) {
		if (gamma <= 0)
			throw new IllegalArgumentException("Gamma must not be smaller than 0");
		this.sigma = gamma;
	}

}
