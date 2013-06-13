package dempred.math;

import java.util.Arrays;

public class VectorTools {

	public static int[] sortDescIndices(VectorInterface vector) {
		ElementSorter[] elements = new ElementSorter[vector.size()];
		for (int i = 0; i < vector.size(); ++i)
			elements[i] = new ElementSorter(i, vector.get(i));
		Arrays.sort(elements);
		int[] elementOrder = new int[vector.size()];
		for (int i = 0; i < vector.size(); ++i)
			elementOrder[i] = elements[i].index;
		return elementOrder;
	}

	public static class ElementSorter implements Comparable<ElementSorter> {
		int index;
		double value;

		public ElementSorter(int index, double value) {
			this.index = index;
			this.value = value;
		}

		public int compareTo(ElementSorter b) {
			return this.value < b.value ? 1 : 0;
		}
	}

	public static VectorInterface randomVector(int size, double min, double max) {
		VectorInterface returnVector = new DenseVector(size);
		for (int i = 0; i < size; ++i)
			returnVector.set(i, min + (Math.random() * (max - min)));
		return returnVector;
	}
}
