package dempred.featureselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dempred.classifier.ClassifierInterface;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetGenerator;
import dempred.datastructure.DatasetResult;
import dempred.loggingtools.LoggerTools;
import dempred.resampling.ResamplingErrorInterface;

public class SFFS<T extends Datapoint> {
	private static final Logger logger = Logger.getLogger(SFFS.class.getName());
	private FeatureSubset[] bestSubsets;
	private ResamplingErrorInterface<T> resampler;
	private double gamma;
	private boolean featureDeletion;
	private int maxSetLength;

	public SFFS() {
		maxSetLength = 6;
		gamma = 0.0;
		featureDeletion = true;
	}

	public void select(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset) throws Exception {
		// initialize features
		bestSubsets = new FeatureSubset[trainset.numFeatures()];
		List<FeatureSubset> singleSubsets = new ArrayList<FeatureSubset>(trainset.numFeatures());
		logger.fine("############# initialising subsets #############");
		for (int i = 0; i < trainset.numFeatures(); ++i) {
			if (i % 50 == 0)
				logger.fine(String.format("initialising subset: %d", (i + 1)));
			FeatureSubset subset = new FeatureSubset(i);
			subset.setScore(SubsetRanker.getScore(classifier, trainset, subset, resampler));
			singleSubsets.add(subset);
		}
		Collections.sort(singleSubsets, new FeatureSubset.ScoreComparatorDesc());
		FeatureSubset actualSubset = singleSubsets.get(0);
		bestSubsets[0] = actualSubset;

		int round = 0;
		while (actualSubset.size() < maxSetLength) {
			logger.fine(String.format("%n%n############# round: %d ############# ", ++round));
			logger.fine(String.format("actualSubset: %s %n", actualSubset.toString()));

			// add feature
			logger.fine("-->Adding feature:");
			for (int i = 0; i < singleSubsets.size(); ++i) {
				if (!singleSubsets.get(i).overlap(actualSubset)) {
					FeatureSubset mergedSubset = new FeatureSubset();
					mergedSubset.merge(actualSubset);
					mergedSubset.merge(singleSubsets.get(i));
					mergedSubset.setScore(SubsetRanker.getScore(classifier, trainset, mergedSubset, resampler));
					if (bestSubsets[mergedSubset.size() - 1] == null || mergedSubset.getScore() < bestSubsets[mergedSubset.size() - 1].getScore())
						bestSubsets[mergedSubset.size() - 1] = mergedSubset;
				}
			}
			actualSubset = bestSubsets[actualSubset.size()];
			logger.fine(String.format("actualSubset: %s %n", actualSubset.toString()));

			// remove features
			if (featureDeletion) {
				logger.fine("-->Removing feature(s):");
				Iterator<Integer> featureIndexIterator = actualSubset.getFeatureIndices().iterator();
				while (actualSubset.size() > 1 && featureIndexIterator.hasNext()) {
					int featureIndex = featureIndexIterator.next();
					logger.fine("removing: " + featureIndex);
					FeatureSubset reducedSet = new FeatureSubset();
					reducedSet.merge(actualSubset);
					reducedSet.removeFeature(featureIndex);
					logger.fine("reduced set:" + reducedSet);
					reducedSet.setScore(SubsetRanker.getScore(classifier, trainset, reducedSet, resampler));
					if (reducedSet.getScore() < bestSubsets[reducedSet.size() - 1].getScore() && !reducedSet.equals(bestSubsets[reducedSet.size() - 1])) {
						logger.fine(String.format("reduced score %.4f normalScore: %.4f %n", reducedSet.getScore(), bestSubsets[reducedSet.size() - 1].getScore()));
						logger.fine(String.format("feature: %d removed!!!!!!! %n", featureIndex));
						bestSubsets[actualSubset.size() - 1] = reducedSet;
						actualSubset = reducedSet;
						featureIndexIterator = actualSubset.getFeatureIndices().iterator();
					}
				}
				logger.fine(String.format("actualSubset: %s %n", actualSubset.toString()));
			}

			if (LoggerTools.getLevel(logger).intValue() <= Level.FINE.intValue()) {
				// test actual feature set
				logger.fine("-->Testing actual feature set:");
				Dataset<T> tempTrain = DatasetGenerator.generateSubset(trainset, actualSubset);
				Dataset<T> tempTest = DatasetGenerator.generateSubset(testset, actualSubset);
				classifier.learn(tempTrain);
				classifier.predict(tempTrain);
				classifier.predict(tempTest);
				logger.fine(actualSubset.toString());
				logger.fine(DatasetResult.toStringClassification(tempTrain));
				logger.fine(DatasetResult.toStringClassification(tempTest));
			}

		}
		if (LoggerTools.getLevel(logger).intValue() <= Level.FINE.intValue())
			testResults(classifier, trainset, testset, 30);
	}

	public void testResults(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, int number) throws Exception {
		double[] lambdaValues = { 0.0000000001, 0.000000001, 0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95 };
		for (int i = 0; i < number; ++i) {
			if (bestSubsets[i] != null) {
				logger.fine(String.format("Testing subset %d of %d: %n", i, bestSubsets.length));
				Dataset<T> tempTrain = DatasetGenerator.generateSubset(trainset, bestSubsets[i]);
				Dataset<T> tempTest = DatasetGenerator.generateSubset(testset, bestSubsets[i]);

				// analytical
				classifier.learn(tempTrain);
				classifier.predict(tempTrain);
				classifier.predict(tempTest);
				logger.fine("AnalyticalClassifier: " + bestSubsets[i]);
				logger.fine(DatasetResult.toStringClassification(tempTrain));
				logger.fine(DatasetResult.toStringClassification(tempTest));

				// linear
				// LinearWrapperClassifier linClassifier = new LinearWrapperClassifier();
				// linClassifier.setGradLength(0.000001);
				// linClassifier.setLambda_1(0.0);
				// linClassifier.setLambda_2(0.1);
				// linClassifier.setW_plus(0.5);
				// linClassifier.setLossFunction(LossFunctionGetter.getFunction(0));
				// linClassifier.setUseDWeights(false);
				// ClassifierTools.optimizeParameter(linClassifier, tempTrain, tempTest, resampler, linClassifier.getClass().getDeclaredField("lambda_2"), lambdaValues);
				// linClassifier.learn(tempTrain);
				// linClassifier.predict(tempTrain);
				// linClassifier.predict(tempTest);
				// logger.fine("linearClassifier: " + bestSubsets[i]);
				// logger.fine(new Result(tempTrain).toString());
				// logger.fine(new Result(tempTest).toString());

				// quadratic
				// QuadraticWrapperClassifier quadClassifier = new QuadraticWrapperClassifier();
				// quadClassifier.setGradLength(0.000001);
				// quadClassifier.setLambda_1(0.0);
				// quadClassifier.setLambda_2(0.1);
				// quadClassifier.setW_plus(0.5);
				// quadClassifier.setLossFunction(LossFunctionGetter.getFunction(0));
				// quadClassifier.learn(tempTrain);
				// quadClassifier.predict(tempTrain);
				// quadClassifier.predict(tempTest);
				// logger.fine("QuadraticClassifier: " + bestSubsets[i]);
				// logger.fine(new Result(tempTrain).toString());
				// logger.fine(new Result(tempTest).toString());
			}
		}
	}

	public final double getGamma() {
		return gamma;
	}

	public final void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public final FeatureSubset[] getBestSubsets() {
		return bestSubsets;
	}

	public final boolean isFeatureDeletion() {
		return featureDeletion;
	}

	public final void setFeatureDeletion(boolean featureDeletion) {
		this.featureDeletion = featureDeletion;
	}

	public final int getMaxSetLength() {
		return maxSetLength;
	}

	public final void setMaxSetLength(int maxSetLength) {
		this.maxSetLength = maxSetLength;
	}

	public final ResamplingErrorInterface<T> getResampler() {
		return resampler;
	}

	public final void setResampler(ResamplingErrorInterface<T> resampler) {
		this.resampler = resampler;
	}

}
