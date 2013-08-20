package com.demshape.dempred.featureselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.classifier.RidgeRegressionPrimal;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.DatasetGenerator;
import com.demshape.dempred.datastructure.DatasetResult;
import com.demshape.dempred.math.VectorInterface;
import com.demshape.dempred.resampling.ResamplingErrorInterface;



public class Combinatorial<T extends Datapoint> {

	private static final Logger logger = Logger.getLogger(Combinatorial.class.getName());
	private List<FeatureSubset> featureSubsets;
	private double gamma;
	private int maxSubsets;
	private int numMergedSubsets;
	private int numRegardedSubsets;
	private ResamplingErrorInterface<T> resampler;
	private double similarityThreshold;
	private int numPrintedSubsets;
	private double alpha;

	public Combinatorial() {
		numPrintedSubsets = 20;
	}

	// *******************************************************
	// Kombination der features wobei nur aus einer 1ner Liste
	// genommen wird.
	// *******************************************************
	public void selectFeaturesSimple(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset) throws Exception {
		System.out.println("Selecting features simple.");
		int numRounds = 15;
		List<FeatureSubset> singleSubsets = new ArrayList<FeatureSubset>(trainset.numFeatures());
		// FVector ranking = FeatureRanker.pearsonCorrelation(trainset).powScalar(2);
		// FVector ranking = FeatureRanker.pearsonCorrelationWeights(trainset).powScalar(2);
		for (int i = 0; i < trainset.numFeatures(); ++i) {
			if (i % 50 == 0)
				System.out.println("initialising subset: " + (i + 1));
			FeatureSubset subset = new FeatureSubset(i);
			subset.setScore(SubsetRanker.getScore(classifier, trainset, subset, resampler) + gamma * subset.size());
			// subset.setScore(ranking.get(i));
			singleSubsets.add(subset);
		}
		Collections.sort(singleSubsets, new FeatureSubset.ScoreComparatorDesc());
		int numFilteredSubsets = (int) Math.round(Math.max(1.0, alpha * singleSubsets.size()));
		System.out.println("Features after prefiltering: " + numFilteredSubsets);
		numMergedSubsets = numRegardedSubsets * numFilteredSubsets;
		maxSubsets = 10 * numRegardedSubsets;
		HashSet<FeatureSubset> generatedSubsets = new HashSet<FeatureSubset>(numMergedSubsets * numRounds);
		featureSubsets = new ArrayList<FeatureSubset>(maxSubsets + numMergedSubsets);

		for (int i = 0; i < singleSubsets.size() && i < maxSubsets; ++i)
			featureSubsets.add(singleSubsets.get(i));
		for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
			System.out.format("%d of %d: %s %n", i, featureSubsets.size(), featureSubsets.get(i));

		double meanOldScore = Double.POSITIVE_INFINITY;
		for (int round = 0; round < numRounds; ++round) {
			System.out.format("%n%n subsetsize: %d hashsetsize: %d %n%n", featureSubsets.size(), generatedSubsets.size());

			int numMerged = 0;
			List<FeatureSubset> mergedSubsets = new ArrayList<FeatureSubset>(numMergedSubsets);
			for (int i = 0; i < featureSubsets.size() && i < numRegardedSubsets; ++i) {
				List<FeatureSubset> filteredMergedSubsets = new ArrayList<FeatureSubset>(numFilteredSubsets);
				Dataset<T> tempTrain = DatasetGenerator.generateSubset(trainset, featureSubsets.get(i));
				RidgeRegressionPrimal<T> newClassifier = new RidgeRegressionPrimal<T>();
				newClassifier.learn(tempTrain);
				VectorInterface weight = newClassifier.getWeight();
				weight.extendByOne(weight.get(weight.size() - 1));
				weight.set(weight.size() - 2, 0.0);

				for (int j = 0; j < singleSubsets.size(); ++j) {
					if (!singleSubsets.get(j).overlap(featureSubsets.get(i))) {
						FeatureSubset mergedSubset = new FeatureSubset();
						mergedSubset.merge(featureSubsets.get(i));
						mergedSubset.merge(singleSubsets.get(j));
						if (!generatedSubsets.contains(mergedSubset)) {
							generatedSubsets.add(mergedSubset);
							if (alpha < 1.0) {
								Dataset<T> reducedTrainset = DatasetGenerator.generateSubset(trainset, mergedSubset);
								reducedTrainset.extend(1.0);
								//FVector gradient = newClassifier.L_deriv(weight, reducedTrainset);
								//mergedSubset.setScore(Math.abs(gradient.get(gradient.size() - 2)));
							}
							filteredMergedSubsets.add(mergedSubset);
						}
					}
				}
				if (alpha < 1.0) {
					Collections.sort(filteredMergedSubsets, new FeatureSubset.ScoreComparatorAsc());
					if (filteredMergedSubsets.size() > numFilteredSubsets)
						filteredMergedSubsets.subList(numFilteredSubsets, filteredMergedSubsets.size()).clear();
				}

				for (int j = 0; j < filteredMergedSubsets.size(); ++j) {
					FeatureSubset mergedSubset = filteredMergedSubsets.get(j);
					mergedSubset.setScore(SubsetRanker.getScore(classifier, trainset, mergedSubset, resampler) + gamma * mergedSubset.size());
					if (mergedSubset.getScore() < mergedSubset.getMergeHistory().get(0).getScore() && mergedSubset.getScore() < mergedSubset.getMergeHistory().get(1).getScore()) {
						++numMerged;
						mergedSubsets.add(mergedSubset);
						System.out.format("%d. merged: (%s) and (%s) to (%s) %n", numMerged, mergedSubset.getMergeHistory().get(0).toString(), mergedSubset.getMergeHistory().get(1).toString(), mergedSubset.toString());
					}
				}
				featureSubsets.get(i).setScore(Double.POSITIVE_INFINITY);
			}
			// reduceFeatureSets(classifier, trainset, mergedSubsets, generatedSubsets);
			Collections.sort(mergedSubsets, new FeatureSubset.ScoreComparatorDesc());
			// mergedSubsets = clearClones(trainset, mergedSubsets, 3, numRegardedSubsets);
			mergedSubsets = clearList(trainset, mergedSubsets, similarityThreshold, numRegardedSubsets);
			featureSubsets.addAll(mergedSubsets);
			Collections.sort(featureSubsets, new FeatureSubset.ScoreComparatorDesc());
			if (featureSubsets.size() > maxSubsets)
				featureSubsets.subList(maxSubsets, featureSubsets.size()).clear();

			// printing criteria for stopping
			int numTop = 1;
			double meanSubsetLength = 0.0;
			double meanScore = 0.0;
			for (int i = 0; i < numTop; ++i) {
				meanSubsetLength += featureSubsets.get(i).size();
				meanScore += featureSubsets.get(i).getScore();
			}
			meanSubsetLength /= numTop;
			meanScore /= numTop;
			System.out.format("round: %d meanlength: %.4f meanScore: %.4f (diff: %.4f) (for top %d) %n", round, meanSubsetLength, meanScore, meanScore - meanOldScore, numTop);
			meanOldScore = meanScore;
			testResults(classifier, trainset, testset, numPrintedSubsets);

			for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
				System.out.format("%d of %d: %s %n", i, featureSubsets.size(), featureSubsets.get(i));
		}
	}

	// *******************************************************
	// fast algorithm
	// *******************************************************
	public void selectFeaturesFast(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset) throws Exception {
		System.out.println("Selecting features fast.");
		double correlationThreshold = 0.1;
		int numRounds = 12;
		HashSet<FeatureSubset> generatedSubsets = new HashSet<FeatureSubset>(numMergedSubsets * numRounds);
		featureSubsets = new ArrayList<FeatureSubset>(maxSubsets + numMergedSubsets);
		List<FeatureSubset> singleSubsets = new ArrayList<FeatureSubset>(trainset.numFeatures());
		// FVector ranking = FeatureRanker.pearsonCorrelationWeights(trainset).powScalar(2);
		// FVector ranking = FeatureRanker.pearsonCorrelation(trainset).powScalar(2);
		for (int i = 0; i < trainset.numFeatures(); ++i) {
			System.out.println("initialising subset: " + i);
			FeatureSubset subset = new FeatureSubset(i);
			// subset.setScore(ranking.get(i));
			subset.setScore(SubsetRanker.getScore(classifier, trainset, subset, resampler) + gamma * subset.size());
			singleSubsets.add(subset);
		}
		Collections.sort(singleSubsets, new FeatureSubset.ScoreComparatorAsc());
		// singleSubsets = clearList(trainset, singleSubsets, 0.9, singleSubsets.size());
		for (int i = 0; i < singleSubsets.size() && i < maxSubsets; ++i)
			featureSubsets.add(singleSubsets.get(i));
		for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
			System.out.format("%d of %d: %s %n", i, featureSubsets.size(), featureSubsets.get(i));

		for (int round = 0; round < numRounds; ++round) {
			int numMerged = 0;
			List<FeatureSubset> mergedSubsets = new ArrayList<FeatureSubset>(numMergedSubsets);
			for (int i = 0; i < featureSubsets.size() && numMerged < numMergedSubsets; ++i) {
				for (int j = 0; j < singleSubsets.size() && numMerged < numMergedSubsets; ++j) {
					double correlation = SubsetRanker.pairwiseMaxCosine(trainset, featureSubsets.get(i), singleSubsets.get(j));
					if (correlation < correlationThreshold) {
						FeatureSubset mergedSubset = new FeatureSubset();
						mergedSubset.merge(featureSubsets.get(i));
						mergedSubset.merge(singleSubsets.get(j));
						if (!generatedSubsets.contains(mergedSubset)) {
							++numMerged;
							generatedSubsets.add(mergedSubset);
							mergedSubsets.add(mergedSubset);
							System.out.format("round:%d %d. Merged %d and %d %n", round, numMerged, i, j);
						}
					}
				}
			}
			for (FeatureSubset subset : mergedSubsets)
				subset.setScore(SubsetRanker.getScore(classifier, trainset, subset, resampler) + gamma * subset.size());
			Collections.sort(mergedSubsets, new FeatureSubset.ScoreComparatorAsc());
			mergedSubsets = clearList(trainset, mergedSubsets, similarityThreshold, maxSubsets);
			featureSubsets.addAll(mergedSubsets);
			Collections.sort(featureSubsets, new FeatureSubset.ScoreComparatorAsc());
			if (featureSubsets.size() > maxSubsets)
				featureSubsets.subList(maxSubsets, featureSubsets.size()).clear();
			// featureSubsets = clearList(trainset, featureSubsets, similarityThreshold, maxSubsets);
			testResults(classifier, trainset, testset, numPrintedSubsets);
			for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
				System.out.format("%d of %d: %s %n", i, featureSubsets.size(), featureSubsets.get(i));
		}
	}

	// *******************************************************
	// fast algorithm version old
	// *******************************************************
	public void selectFeaturesFastOld(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset) throws Exception {
		System.out.println("Selecting features fast old.");
		double correlationThreshold = 0.1;
		int numRounds = 12;
		HashSet<FeatureSubset> generatedSubsets = new HashSet<FeatureSubset>(numMergedSubsets * numRounds);
		featureSubsets = new ArrayList<FeatureSubset>(maxSubsets + numMergedSubsets);
		List<FeatureSubset> singleSubsets = new ArrayList<FeatureSubset>(trainset.numFeatures());
		VectorInterface ranking = FeatureRanker.pearsonCorrelation(trainset).powScalar(2);
		for (int i = 0; i < trainset.numFeatures(); ++i) {
			System.out.println("initialisieng subset: " + (i + 1));
			FeatureSubset subset = new FeatureSubset(i);
			subset.setScore(SubsetRanker.getScore(classifier, trainset, subset, resampler) + gamma * subset.size());
			// subset.setScore(ranking.get(i));
			singleSubsets.add(subset);
		}
		Collections.sort(singleSubsets, new FeatureSubset.ScoreComparatorAsc());
		// singleSubsets = clearList(trainset, singleSubsets, 0.9, singleSubsets.size());
		for (int i = 0; i < singleSubsets.size() && i < maxSubsets; ++i)
			featureSubsets.add(singleSubsets.get(i));
		for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
			System.out.format("%d of %d: %s %n", i, featureSubsets.size(), featureSubsets.get(i));

		for (int round = 0; round < numRounds; ++round) {
			int numMerged = 0;
			ArrayList<FeatureSubset> mergedSubsets = new ArrayList<FeatureSubset>(numMergedSubsets);
			for (int i = 0; i < featureSubsets.size() && numMerged < numMergedSubsets; ++i) {
				for (int j = 0; j < singleSubsets.size() && numMerged < numMergedSubsets; ++j) {
					double correlation = SubsetRanker.pairwiseMaxCosine(trainset, featureSubsets.get(i), singleSubsets.get(j));
					if (correlation < correlationThreshold) {
						FeatureSubset mergedSubset = new FeatureSubset();
						mergedSubset.merge(featureSubsets.get(i));
						mergedSubset.merge(singleSubsets.get(j));
						if (!generatedSubsets.contains(mergedSubset)) {
							generatedSubsets.add(mergedSubset);
							double maxSimilarity = Double.NEGATIVE_INFINITY;
							for (FeatureSubset subset : mergedSubsets) {
								double similarity = SubsetRanker.similarity(trainset, mergedSubset, subset);
								if (similarity > maxSimilarity)
									maxSimilarity = similarity;
							}
							if (maxSimilarity < similarityThreshold) {
								++numMerged;
								mergedSubsets.add(mergedSubset);
								System.out.format("round:%d %d. Merged %d and %d %n", round, numMerged, i, j);
							}
						}
					}
				}
			}
			for (FeatureSubset subset : mergedSubsets)
				subset.setScore(SubsetRanker.getScore(classifier, trainset, subset, resampler) + gamma * subset.size());
			featureSubsets.addAll(mergedSubsets);
			Collections.sort(featureSubsets, new FeatureSubset.ScoreComparatorAsc());
			if (featureSubsets.size() > maxSubsets)
				featureSubsets.subList(maxSubsets, featureSubsets.size()).clear();
			testResults(classifier, trainset, testset, numPrintedSubsets);
			for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
				System.out.format("%d of %d: %s %n", i, featureSubsets.size(), featureSubsets.get(i));
		}
	}

	// *******************************************************
	// algorithm zum testen der hypothese 1
	// *******************************************************
	public void selectFeaturesHypo1(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset) throws Exception {
		double similarity = 0.8;
		featureSubsets = new ArrayList<FeatureSubset>(maxSubsets + numMergedSubsets);
		List<FeatureSubset> singleSubsets = new ArrayList<FeatureSubset>(trainset.numFeatures());
		VectorInterface ranking = FeatureRanker.pearsonCorrelationValues(trainset).powScalar(2);
		for (int i = 0; i < trainset.numFeatures(); ++i) {
			System.out.println("initialisieng subset: " + (i + 1));
			FeatureSubset subset = new FeatureSubset();
			subset.addFeatureIndex(i);
			// subset.setScore(SubsetRanker.MRMR(classifier, trainset, subset, crossFolds, crossRounds, gamma));
			subset.setScore(ranking.get(i));
			singleSubsets.add(subset);
		}
		Collections.sort(singleSubsets, new FeatureSubset.ScoreComparatorAsc());
		for (int i = 0; i < singleSubsets.size() && i < maxSubsets; ++i)
			featureSubsets.add(singleSubsets.get(i));
		for (int i = 0; i < featureSubsets.size() && i < numPrintedSubsets; ++i)
			System.out.println(i + ": " + featureSubsets.get(i));

		int numMerged = 0;
		for (int i = 0; i < featureSubsets.size() && numMerged < numMergedSubsets; ++i) {
			for (int j = 0; j < singleSubsets.size() && numMerged < numMergedSubsets; ++j) {
				FeatureSubset mergedSubset = new FeatureSubset();
				mergedSubset.merge(featureSubsets.get(i));
				mergedSubset.merge(singleSubsets.get(j));
				mergedSubset.setScore(SubsetRanker.getScore(classifier, trainset, mergedSubset, resampler) + gamma * mergedSubset.size());
				if (mergedSubset.getScore() > featureSubsets.get(i).getScore() && mergedSubset.getScore() > singleSubsets.get(j).getScore()) {
					++numMerged;
					System.out.format("round:%d %d. Merged %d and %d Score: %.4f %n", 1, numMerged, i, j, mergedSubset.getScore());
					System.out.println("showing similar: " + similarity);
					for (int k = j + 1; k < singleSubsets.size(); ++k) {
						if (!singleSubsets.get(k).equals(singleSubsets.get(j)) && similarity <= SubsetRanker.similarity(trainset, singleSubsets.get(j), singleSubsets.get(k))) {
							FeatureSubset mergedSubsetSim = new FeatureSubset();
							mergedSubsetSim.merge(featureSubsets.get(i));
							mergedSubsetSim.merge(singleSubsets.get(k));
							System.out.println(mergedSubset + " AND " + mergedSubsetSim);
							System.out.format("FeaturePosition %d(%.4f) and %d(%.4f) are similar. %n", j, singleSubsets.get(j).getScore(), k, singleSubsets.get(k).getScore());
							mergedSubsetSim.setScore(SubsetRanker.getScore(classifier, trainset, mergedSubsetSim, resampler) + gamma * mergedSubsetSim.size());
							if (mergedSubsetSim.getScore() < mergedSubset.getScore() + 0.01)
								System.out.println("OK score is smaller!: " + mergedSubset.getScore() + " " + mergedSubsetSim.getScore());
							else
								System.out.println("noooooooo score is larger!" + mergedSubset.getScore() + " " + mergedSubsetSim.getScore());
						}
					}
					System.out.println();
				}
			}
		}
	}

	private List<FeatureSubset> clearList(Dataset<?> dataset, List<FeatureSubset> featureSets, double cutoff, int size) {
		List<FeatureSubset> diverseSubsets = new ArrayList<FeatureSubset>(featureSets.size());
		int numDiverse = 0;
		for (int i = 0; i < featureSets.size() && numDiverse < size; ++i) {
			int maxIndex = 0;
			double maxSimilarity = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < diverseSubsets.size(); ++j) {
				double similarity = SubsetRanker.similarity(dataset, featureSets.get(i), diverseSubsets.get(j));
				if (similarity > maxSimilarity) {
					maxSimilarity = similarity;
					maxIndex = j;
				}
			}
			if (maxSimilarity < cutoff) {
				++numDiverse;
				diverseSubsets.add(featureSets.get(i));
			} else
				System.out.format("Similarity is too high: %.4f between %d and %d | %s | %s %n", maxSimilarity, i, maxIndex, featureSets.get(i), diverseSubsets.get(maxIndex));
		}
		return diverseSubsets;
	}

	private List<FeatureSubset> clearClones(Dataset<T> dataset, List<FeatureSubset> featureSets, int cutoff, int size) {
		List<FeatureSubset> diverseSubsets = new ArrayList<FeatureSubset>(featureSets.size());
		HashMap<FeatureSubset, Integer> subsetsVariants = new HashMap<FeatureSubset, Integer>();
		int numDiverse = 0;
		for (int i = 0; i < featureSets.size() && numDiverse < size; ++i) {
			FeatureSubset tempSet = featureSets.get(i).getRoot();
			Integer count = subsetsVariants.get(tempSet);
			if (count == null)
				count = 0;
			if (count < cutoff) {
				diverseSubsets.add(featureSets.get(i));
				subsetsVariants.put(tempSet, ++count);
			}
		}
		return diverseSubsets;
	}

	public void testResults(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, int number) throws Exception {
		for (int i = 0; i < number; ++i) {
			System.out.format("Testing subset %d of %d: %n", i, featureSubsets.size());
			Dataset<T> tempTrain = DatasetGenerator.generateSubset(trainset, featureSubsets.get(i));
			Dataset<T> tempTest = DatasetGenerator.generateSubset(testset, featureSubsets.get(i));

			double[] lambdaValues = { 0.0000000001, 0.000000001, 0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8 };

			// analytical
			// ClassifierTools.optimizeParameter(classifier, tempTrain, tempTest, classifier.getClass().getSuperclass().getDeclaredField("lambda_2"), lambdaValues, crossFolds, crossRounds, false);
			classifier.learn(tempTrain);
			classifier.predict(tempTrain);
			classifier.predict(tempTest);
			System.out.println(featureSubsets.get(i));
			System.out.println("AnalyticalClassifier: ");
			logger.fine(DatasetResult.toStringClassification(tempTrain));
			logger.fine(DatasetResult.toStringClassification(tempTest));

			// linear
			// LinearWrapperClassifier linClassifier = new LinearWrapperClassifier();
			// linClassifier.setGradLength(0.000001);
			// linClassifier.setLambda_1(0.0);
			// linClassifier.setLambda_2(0.0001);
			// linClassifier.setW_plus(0.5);
			// linClassifier.setLossFunctionType(0);
			// linClassifier.setUseDWeights(false);
			// linClassifier.setVerbose(false);
			// ClassifierTools.optimizeParameter(linClassifier, tempTrain, tempTest, linClassifier.getClass().getDeclaredField("lambda_2"), lambdaValues, crossFolds, crossRounds);
			// linClassifier.learn(tempTrain);
			// linClassifier.predict(tempTrain);
			// linClassifier.predict(tempTest);
			// System.out.println("linearClassifier: ");
			// DatasetWriter.showResults(tempTrain);
			// DatasetWriter.showResults(tempTest);
			//			
			// //quadratic
			// QuadraticWrapperClassifier quadClassifier = new QuadraticWrapperClassifier();
			// quadClassifier.setGradLength(0.000001);
			// quadClassifier.setLambda_1(0.0);
			// quadClassifier.setLambda_2(0.0001);
			// quadClassifier.setW_plus(0.5);
			// quadClassifier.setLossFunctionType(0);
			// quadClassifier.setVerbose(false);
			// quadClassifier.learn(tempTrain);
			// quadClassifier.predict(tempTrain);
			// quadClassifier.predict(tempTest);
			// System.out.println("QuadraticClassifier: ");
			// DatasetWriter.showResults(tempTrain);
			// DatasetWriter.showResults(tempTest);
			// System.out.println("");
		}
	}

	// *******************************************************
	// getters and setters
	// *******************************************************
	public final double getGamma() {
		return gamma;
	}

	public final void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public final int getMaxSubsets() {
		return maxSubsets;
	}

	public final void setMaxSubsets(int maxSubsets) {
		this.maxSubsets = maxSubsets;
	}

	public final int getNumMergedSubsets() {
		return numMergedSubsets;
	}

	public final void setNumMergedSubsets(int numMergedSubsets) {
		this.numMergedSubsets = numMergedSubsets;
	}

	public final void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public final int getNumRegardedSubsets() {
		return numRegardedSubsets;
	}

	public final void setNumRegardedSubsets(int numRegardedSubsets) {
		this.numRegardedSubsets = numRegardedSubsets;
	}

	public final double getAlpha() {
		return alpha;
	}

	public final void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public final ResamplingErrorInterface<T> getResampler() {
		return resampler;
	}

	public final void setResampler(ResamplingErrorInterface<T> resampler) {
		this.resampler = resampler;
	}

	public final List<FeatureSubset> getFeatureSubsets() {
		return featureSubsets;
	}

}
