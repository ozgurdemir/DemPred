package dempred.math;

public class VectorMetric {

	public static double scalarProd(VectorInterface a, VectorInterface b) {
		return a.scalarProduct(b);
	}

	public static double euclidean(VectorInterface a, VectorInterface b) {
		VectorInterface tmp = a.clone();
		tmp.subVector(b);
		tmp.powScalar(2);
		return Math.pow(tmp.sum(), 0.5);
	}

	public static double jaccard(VectorInterface a, VectorInterface b) {
		double dot = a.scalarProduct(b);
		double euclA = a.normRadical(2);
		double euclB = b.normRadical(2);
		return 1 - (dot / (euclA * euclB));
	}

	public static double cosine(VectorInterface a, VectorInterface b) {
		double dot = a.scalarProduct(b);
		double euclA = a.normRadical(2);
		double euclB = b.normRadical(2);
		return (dot / (euclA * euclB));
	}

	public static double pcc(VectorInterface a, VectorInterface b) {
		VectorInterface cloneA = a.clone();
		VectorInterface cloneB = b.clone();
		cloneA.subScalar(cloneA.mean());
		cloneB.subScalar(cloneB.mean());
		double upperValue = cloneA.scalarProduct(cloneB);
		double lowerValue = cloneA.powScalar(2.0).sum() * cloneB.powScalar(2.0).sum();
		return upperValue / Math.sqrt(lowerValue);
	}

}
