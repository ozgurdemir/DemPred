package dempred.featureselection;

import dempred.classifier.ClassifierInterface;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetGenerator;
import dempred.datastructure.DatasetManipulator;
import dempred.math.DenseVector;
import dempred.math.VectorInterface;
import dempred.math.VectorMetric;
import dempred.resampling.ResamplingErrorInterface;

public class SubsetRanker {

	// public static double[] missclassificationRate(ClassifierInterface classifier, Dataset dataset, FeatureSubset subset, int numFolds, int numRounds) throws Exception {
	// Dataset reducedDataset = DatasetGenerator.generateSubset(dataset, subset);
	// return ClassifierTools.crossValidationError(classifier, reducedDataset, numFolds, numRounds);
	// }

	public static <T extends Datapoint> double getScore(ClassifierInterface<T> classifier, Dataset<T> dataset, FeatureSubset subset, ResamplingErrorInterface<T> resampler) throws Exception {
		Dataset<T> reducedDataset = DatasetGenerator.generateSubset(dataset, subset);
		//double[] lambdaValues = { 0.0000001, 0.00001, 0.001, 0.1, 0.3, 0.5, 0.7, 0.9, 0.95 };
		//double optimizedParam = ClassifierTools.optimizeParameter(classifier, reducedDataset, null, resampler, Lib.getDeclaredField(classifier, "lambda_2"), lambdaValues);
		return resampler.error(classifier, reducedDataset);
	}

	public static <T extends Datapoint>double intraCorrelation(Dataset<T> dataset, FeatureSubset subset) {
		VectorInterface[] featureVectors = DatasetManipulator.getFeatureVectors(dataset, subset);
		double score = 0.0;
		for (int i = 0; i < featureVectors.length; ++i) {
			for (int j = i + 1; j < featureVectors.length; ++j)
				score += Math.pow(VectorMetric.pcc(featureVectors[i], featureVectors[j]), 2);
		}
		int n = subset.size();
		n = (n * (n - 1)) / 2;
		if (n == 0)
			return 0;
		else {
			return (score / n);
		}
	}

	public static <T extends Datapoint>double ClassCorrelation(Dataset<T> dataset, FeatureSubset subset) {
		VectorInterface[] featureVectors = DatasetManipulator.getFeatureVectors(dataset, subset);
		VectorInterface classVector = DatasetManipulator.getClassVector(dataset);
		double score = 0.0;
		for (VectorInterface feature : featureVectors)
			score += Math.pow(VectorMetric.pcc(feature, classVector), 2);
		score /= subset.size();
		return score;
	}

	public static <T extends Datapoint>double similarityold(Dataset<T> dataset, FeatureSubset subset1, FeatureSubset subset2) {
		VectorInterface[] largeVectors;
		DenseVector[] smallVectors;
		if (subset1.size() > subset2.size()) {
			largeVectors = DatasetManipulator.getFeatureVectors(dataset, subset1);
			smallVectors = DatasetManipulator.getFeatureVectors(dataset, subset2);
		} else {
			largeVectors = DatasetManipulator.getFeatureVectors(dataset, subset2);
			smallVectors = DatasetManipulator.getFeatureVectors(dataset, subset1);
		}
		double score = 0.0;
		for (VectorInterface feature1 : largeVectors) {
			double maxSimilarity = Double.NEGATIVE_INFINITY;
			for (DenseVector feature2 : smallVectors) {
				double temp = Math.pow(VectorMetric.cosine(feature1, feature2), 2);
				if (temp > maxSimilarity)
					maxSimilarity = temp;
			}
			score += maxSimilarity;
		}
		return (score / (largeVectors.length));
	}

	public static <T extends Datapoint>double similarity(Dataset<T> dataset, FeatureSubset subset1, FeatureSubset subset2) {
		FeatureSubset reducedSubset1 = new FeatureSubset(subset1);
		FeatureSubset reducedSubset2 = new FeatureSubset(subset2);
		reducedSubset1.getFeatureIndices().removeAll(subset2.getFeatureIndices());
		reducedSubset2.getFeatureIndices().removeAll(subset1.getFeatureIndices());
		VectorInterface[] largeVectors;
		DenseVector[] smallVectors;
		if (reducedSubset1.size() > reducedSubset2.size()) {
			largeVectors = DatasetManipulator.getFeatureVectors(dataset, reducedSubset1);
			smallVectors = DatasetManipulator.getFeatureVectors(dataset, reducedSubset2);
		} else {
			largeVectors = DatasetManipulator.getFeatureVectors(dataset, reducedSubset2);
			smallVectors = DatasetManipulator.getFeatureVectors(dataset, reducedSubset1);
		}
		double score = 0.0;
		for (VectorInterface feature1 : largeVectors) {
			double maxSimilarity = Double.NEGATIVE_INFINITY;
			for (DenseVector feature2 : smallVectors) {
				double temp = Math.pow(VectorMetric.cosine(feature1, feature2), 2);
				if (temp > maxSimilarity)
					maxSimilarity = temp;
			}
			score += maxSimilarity;
		}
		return (score / (largeVectors.length));
	}

	public static <T extends Datapoint>double pairwiseMaxCosine(Dataset<T> dataset, FeatureSubset subset1, FeatureSubset subset2) {
		VectorInterface[] featureVectors1 = DatasetManipulator.getFeatureVectors(dataset, subset1);
		DenseVector[] featureVectors2 = DatasetManipulator.getFeatureVectors(dataset, subset2);
		double maxSimilarity = Double.NEGATIVE_INFINITY;
		for (VectorInterface feature1 : featureVectors1) {
			for (DenseVector feature2 : featureVectors2) {
				double temp = Math.pow(VectorMetric.cosine(feature1, feature2), 2);
				if (temp > maxSimilarity)
					maxSimilarity = temp;
			}
		}
		return maxSimilarity;
	}

}
