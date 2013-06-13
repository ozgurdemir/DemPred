package dempred.featureselection;

import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.math.DenseVector;
import dempred.math.VectorInterface;
import dempred.math.VectorMetric;

public class DatasetComparator {

	// finde zu jedem datenpunkt in dataset1 den ähnlichsten in dataset2 und
	// berechne anhand dessen einen feature rank
	public static VectorInterface compare(Dataset<?> dataset1, Dataset<?> dataset2) throws IllegalArgumentException {
		VectorInterface rankVector = new DenseVector(dataset1.numFeatures(), 0.0);
		VectorInterface tempVector;
		double distance;
		double minDistance;
		Datapoint minDatapoint = null;
		int numentries = 0;
		for (Datapoint datapoint1 : dataset1.getDatapoints()) {
			minDistance = Double.POSITIVE_INFINITY;
			for (Datapoint datapoint2 : dataset2.getDatapoints()) {
				distance = VectorMetric.euclidean(datapoint1.getFeatureVector(), datapoint2.getFeatureVector());
				if (distance < minDistance && datapoint1 != datapoint2) {
					minDistance = distance;
					minDatapoint = datapoint2;
				}
			}
			if (datapoint1.getGroup() != minDatapoint.getGroup()) {
				tempVector = datapoint1.getFeatureVector().clone();
				tempVector.subVector(minDatapoint.getFeatureVector());
				tempVector.abs();
				/*
				 * System.out.println(datapoint1.getSequence()
				 * +" "+minDatapoint.getSequence());
				 * System.out.println("hello"+tempVector);
				 */
				tempVector.divScalar(tempVector.max());
				rankVector.addVector(tempVector);
				++numentries;
			}
		}
		System.out.println("numentries: " + numentries);
		rankVector.divScalar(rankVector.max());
		return rankVector;
	}
}
