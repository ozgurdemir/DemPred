package dempred.featureselection;

import java.util.logging.Logger;

import dempred.classifier.AbstractLinearClassifier;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetNormalizer;
import dempred.math.DenseVector;
import dempred.math.VectorInterface;
import dempred.resampling.Bootstrap;
import dempred.resampling.CrossValidation;

public class GeneralizedRFE<T extends Datapoint> {
	private static final Logger logger = Logger.getLogger(GeneralizedRFE.class.getName());
	private AbstractLinearClassifier<T> classifier;
	private Dataset<T> dataset;
	private Dataset<T> testset;
	private int numFolds;
	private int numRounds;
	private int numSamples;

	public GeneralizedRFE(AbstractLinearClassifier<T> classifier, Dataset<T> dataset) {
		this.classifier = classifier;
		this.dataset = dataset;
	}

	public VectorInterface computeRank() throws Exception {
		CrossValidation<T> crossVal = new CrossValidation<T>(dataset);
		double[] rank = new double[dataset.numFeatures()];
		for (int round = 0; round < numRounds; ++round) {
			crossVal.generateFolds(numFolds);
			for (int foldNumber = 0; foldNumber < numFolds; ++foldNumber) {
				logger.fine("--------------------------------------------");
				logger.fine(String.format("feature selection: round: %d of %d, fold:%d of %d", round + 1, numRounds, foldNumber + 1, numFolds));
				Dataset<T> crossMain = crossVal.getFoldsExcept(foldNumber).clone();
				Dataset<T> crossFold = crossVal.getFold(foldNumber).clone();
				DatasetNormalizer normalizer = new DatasetNormalizer(crossMain);
				normalizer.normalize(crossMain);
				normalizer.normalize(crossFold);
				int[] zeroIndices = normalizer.getZeroIndices();
				int[] nonZeroIndices = normalizer.getNonZeroIndices();
				logger.fine(String.format("%d features with standart deviation of zero deleted.", zeroIndices.length));
				classifier.learn(crossMain);
				VectorInterface effectObjMain = classifier.effectObjFunc(crossMain);
				VectorInterface effectObjFold = classifier.effectObjFunc(crossFold);
				for (int i = 0; i < effectObjFold.size(); ++i)
					 rank[nonZeroIndices[i]] += effectObjFold.get(i) + effectObjMain.get(i);
			}
		}
		return new DenseVector(rank);
	}

	public int[] select(int numDeletions) throws Exception {
		VectorInterface rank = computeRank();
		return rank.minIndex(numDeletions);
	}

	// dataset must not be normalized before using this method
	public VectorInterface computeRank632() throws Exception {
		logger.fine("Selecting features with Generalized-RFE.632");
		Bootstrap<T> bootstrap = new Bootstrap<T>(dataset);
		double[] rank = new double[dataset.numFeatures()];
		for (int sample = 0; sample < numSamples; ++sample) {
			logger.fine("--------------------------------------------");
			logger.fine(String.format("feature selection: sample: %d of %d", sample + 1, numSamples));
			bootstrap.generateSample();
			Dataset<T> crossMain = bootstrap.getSample().clone();
			Dataset<T> crossFold = bootstrap.getUnsampled().clone();
			DatasetNormalizer normalizer = new DatasetNormalizer(crossMain);
			normalizer.normalize(crossMain);
			normalizer.normalize(crossFold);
			int[] zeroIndices = normalizer.getZeroIndices();
			int[] nonZeroIndices = normalizer.getNonZeroIndices();
			logger.fine(String.format("%d features with standart deviation of zero deleted.", zeroIndices.length));
			classifier.learn(crossMain);
			VectorInterface effectObjMain = classifier.effectObjFunc(crossMain);
			VectorInterface effectObjFold = classifier.effectObjFunc(crossFold);
			for (int i = 0; i < effectObjFold.size(); ++i)
				rank[nonZeroIndices[i]] += 0.632 * effectObjFold.get(i) + 0.368 * effectObjMain.get(i);
		}
		return new DenseVector(rank);
	}

	public int[] select632(int numDeletions) throws Exception {
		VectorInterface rank = computeRank632();
		return rank.minIndex(numDeletions);
	}

	public VectorInterface computeRank632Retrain() throws Exception {
		logger.fine("Selecting features with Generalized-RFE.632 retrain");
		Bootstrap<T> bootstrap = new Bootstrap<T>(dataset);
		double[] rank = new double[dataset.numFeatures()];
		for (int sample = 0; sample < numSamples; ++sample) {
			logger.fine("--------------------------------------------");
			logger.fine(String.format("feature selection: sample: %d of %d", sample + 1, numSamples));
			bootstrap.generateSample();
			Dataset<T> crossMain = bootstrap.getSample().clone();
			Dataset<T> crossFold = bootstrap.getUnsampled().clone();
			DatasetNormalizer normalizer = new DatasetNormalizer(crossMain);
			normalizer.normalize(crossMain);
			normalizer.normalize(crossFold);
			int[] zeroIndices = normalizer.getZeroIndices();
			logger.fine(String.format("%d features with standart deviation of zero deleted.", zeroIndices.length));
			classifier.learn(crossMain);
			VectorInterface[] ranks = classifier.effectObjRetrain(crossMain, crossFold);
			VectorInterface effectObjMain = ranks[0];
			VectorInterface effectObjFold = ranks[1];
			int[] nonZeroIndices = normalizer.getNonZeroIndices();
			for (int i = 0; i < effectObjFold.size(); ++i)
				rank[nonZeroIndices[i]] += 0.632 * effectObjFold.get(i) + 0.368 * effectObjMain.get(i);
		}
		return new DenseVector(rank);
	}

	public int[] select632Retrain(int numDeletions) throws Exception {
		VectorInterface rank = computeRank632Retrain();
		return rank.minIndex(numDeletions);
	}

	public VectorInterface computeRankFake() throws Exception {
		double[] rank = new double[dataset.numFeatures()];
		Dataset<T> crossMain = dataset;
		Dataset<T> crossFold = testset;
		DatasetNormalizer normalizer = new DatasetNormalizer(crossMain);
		normalizer.normalize(crossMain);
		normalizer.normalize(crossFold);
		int[] zeroIndices = normalizer.getZeroIndices();
		logger.fine(String.format("%d features with standart deviation of zero deleted.", zeroIndices.length));
		classifier.learn(crossMain);
		VectorInterface effectObjMain = classifier.effectObjFunc(crossMain);
		VectorInterface effectObjFold = classifier.effectObjFunc(crossFold);
		int[] nonZeroIndices = normalizer.getNonZeroIndices();
		for (int i = 0; i < effectObjFold.size(); ++i)
			rank[nonZeroIndices[i]] += effectObjFold.get(i) + effectObjMain.get(i);
		return new DenseVector(rank);
	}

	public int[] selectFake(int numDeletions) throws Exception {
		VectorInterface rank = computeRankFake();
		return rank.minIndex(numDeletions);
	}

	// *******************************************
	// getter and setter
	// *******************************************
	public final int getNumFolds() {
		return numFolds;
	}

	public final void setNumFolds(int numFolds) {
		this.numFolds = numFolds;
	}

	public final int getNumRounds() {
		return numRounds;
	}

	public final void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public final int getNumSamples() {
		return numSamples;
	}

	public final void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	public final Dataset<T> getTestset() {
		return testset;
	}

	public final void setTestset(Dataset<T> testset) {
		this.testset = testset;
	}

}
