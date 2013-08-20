package com.demshape.dempred.kernels;

/**
 * A factory class for creating Kernel objects.
 */
public class KernelFactory {

	/**
	 * Gets the kernel of a special type: <br>
	 * 0: Simple Linear Kernel <br>
	 * 1: RBF Kernel <br>
	 * 2: Sigmoid Kernel <br>
	 * 3: Polynomial Kernel
	 *
	 * @param type the type
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the kernel
	 */
	public static KernelInterface getKernel(int type, double a, double b, double c) {
		switch (type) {
		case 0:
			return new SimpleKernel();
		case 1:
			return new RBFKernel(a);
		case 2:
			return new SigmoidKernel(a, b);
		case 3:
			return new PolynomialKernel(a, b, c);
		default:
			throw new IllegalArgumentException("The selected Kernel does not exist!: " + type);
		}
	}

}
