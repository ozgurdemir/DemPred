package dempred.datastructure;

import java.util.Comparator;

public class MultigroupPrediction {
	public int group;
	public double prediction;

	public MultigroupPrediction() {

	}

	public MultigroupPrediction(int group, double prediction) {
		this.group = group;
		this.prediction = prediction;
	}

	public String toString() {
		return String.format("group:%d prediction:%.4f", group, prediction);
	}
	
	public static class predictionComparatorDesc implements Comparator<MultigroupPrediction> {

		@Override
		public int compare(MultigroupPrediction a, MultigroupPrediction b) {
			return Double.compare(b.prediction, a.prediction);
		}
		
	}
}
