package dempred.util;

import java.util.Random;

public class RandomNumber {
	private Random random;

	public RandomNumber() {
		this.random = new Random(System.nanoTime());
	}

	public double gauss(double mean, double std) {
		return mean + std * random.nextGaussian();
	}

	public double normal(double mean, double std) {
		return mean + std * random.nextDouble();
	}

	public double[] gaussArray(int size, double mean, double std) {
		double[] result = new double[size];
		for (int i = 0; i < size; ++i)
			result[i] = gauss(mean, std);
		return result;
	}
}
