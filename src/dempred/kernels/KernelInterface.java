package dempred.kernels;

import dempred.math.DenseVector;
import dempred.math.VectorInterface;

/**
 * The Interface for all Kernels.
 */
public interface KernelInterface {
	
	/**
	 * Evaluates the Kernel for two given Vectors.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public double evaluate(VectorInterface a, VectorInterface b);
}
