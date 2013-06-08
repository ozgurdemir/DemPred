package dempred.kernels;

import java.io.Serializable;

import dempred.math.VectorInterface;


/**
 * A simple linear kernel which is the scalar product of two vectors.
 */
public class SimpleKernel implements KernelInterface, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1949078538832518186L;

	/* (non-Javadoc)
	 * @see dempred.kernels.KernelInterface#evaluate(dempred.fmath.FVector, dempred.fmath.FVector)
	 */
	public double evaluate(VectorInterface a, VectorInterface b) {
		return a.scalarProduct(b);
	}

}
