package com.demshape.dempred.transformer;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class LinearInterpolation {

	private NavigableMap<Double, Double> tree;
	private double smallest;
	private double largest;

	public LinearInterpolation(double[] x, double[] y) {
		tree = new TreeMap<Double, Double>();
		for (int i = 0; i < x.length; ++i)
			tree.put(x[i], y[i]);
		smallest = tree.firstKey();
		largest = tree.lastKey();
	}

	public double calcValue(double x) {
		if (x < smallest || x > largest)
			throw new IllegalArgumentException(String.format("%.4f is out of range (%.4f...%.4f) and cannot be interpolated", x, smallest, largest));
		if (tree.containsKey(x))
			return tree.get(x);
		Entry<Double, Double> lower = tree.lowerEntry(x);
		Entry<Double, Double> higher = tree.higherEntry(x);
		double result = lower.getValue() + ((higher.getValue() - lower.getValue()) / (higher.getKey() - lower.getKey())) * (x - lower.getKey());
		return result;
	}

	public final double getSmallest() {
		return smallest;
	}

	public final double getLargest() {
		return largest;
	}

}
