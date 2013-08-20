package com.demshape.dempred.datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.demshape.dempred.featuregeneration.FeatureGeneratorInterface;
import com.demshape.dempred.featureselection.FeatureSubset;
import com.demshape.dempred.grouper.GrouperInterface;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorInterface;



/**
 * The Class DatasetManipulator contains various methods to manipulate existing datasets. 
 */
public class DatasetManipulator {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(DatasetManipulator.class.getName());

	/**
	 * Given a dataset this function generates features using a featureGenerator interface. Previously existing features in the dataset will be overwritten.
	 *
	 * @param <T> Dataset Type
	 * @param dataset Dataset to generate the features for
	 * @param featureGenerator the feature generator used to generate the features
	 * @throws Exception the exception
	 */
	public static <T extends Datapoint> void generateFeatures(Dataset<T> dataset, FeatureGeneratorInterface<T> featureGenerator) throws Exception {
		int numFeatures = -1;
		int index = 0;
		for (T datapoint : dataset.getDatapoints()) {
			logger.fine(String.format("generating features for datapoint %d of %d", ++index, dataset.size()));
			datapoint.setFeatureVector(new DenseVector(featureGenerator.generateFeature(datapoint)));
			numFeatures = datapoint.getNumFeatures();
		}
		dataset.initFeatureIndex(numFeatures);
		dataset.setFeatureNames(featureGenerator.getNames(dataset.getDatapoint(0)));
	}

	/**
	 * Given a datapoint this function generates features using a featureGenerator interface. Previously existing features in the datapoint will be overwritten.
	 *
	 * @param <T> Datapoint Type
	 * @param datapoint the datapoint to generate the features for
	 * @param featureGenerator the feature generator
	 * @throws Exception the exception
	 */
	public static <T extends Datapoint> void generateFeatures(T datapoint, FeatureGeneratorInterface<T> featureGenerator) throws Exception {
		datapoint.setFeatureVector(new DenseVector(featureGenerator.generateFeature(datapoint)));
	}

	/**
	 * Given a dataset this function squares all existing features in this dataset: f1,f2,f3 will be squared to f1*f2,f1*f3,f3*f3,f1,f2,f3
	 *
	 * @param <T> Dataset Type
	 * @param dataset the dataset
	 */
	public static <T extends Datapoint> void squareFeatures(Dataset<T> dataset) {
		int numSquareFeatures = 0;
		for (T datapoint : dataset.getDatapoints()) {
			double[] linearFeatures = datapoint.getFeatureVector().getElements();
			int d = linearFeatures.length;
			numSquareFeatures = (int) (d + d * (d + 1.0) / 2.0);
			double[] squareFeatures = new double[numSquareFeatures];
			int index = 0;
			for (int i = 0; i < d; ++i) {
				squareFeatures[index++] = linearFeatures[i];
				for (int j = i; j < d; ++j)
					squareFeatures[index++] = linearFeatures[i] * linearFeatures[j];
			}
			datapoint.setFeatureVector(new DenseVector(squareFeatures));
		}
		dataset.initFeatureIndex(numSquareFeatures);
		List<String> originalFeatureNames = dataset.getFeatureNames();
		int d = originalFeatureNames.size();
		List<String> featureNames = new ArrayList<String>(numSquareFeatures);
		for (int i = 0; i < d; ++i) {
			featureNames.add(originalFeatureNames.get(i));
			for (int j = i; j < d; ++j)
				featureNames.add(originalFeatureNames.get(i) + " * " + originalFeatureNames.get(j));
		}
		dataset.setFeatureNames(featureNames);
	}

	/**
	 * Given a dataset this function squares a single feature at the specified index position.
	 *
	 * @param <T> Dataset Type
	 * @param dataset the dataset to be processed
	 * @param featureIndex index of the featre to be squared
	 */
	public static <T extends Datapoint> void squareSingleFeatures(Dataset<T> dataset, int featureIndex) {
		int numSquareFeatures = 0;
		List<String> originalFeatureNames = dataset.getFeatureNames();
		List<String> featureNames = new ArrayList<String>(numSquareFeatures);

		for (T datapoint : dataset.getDatapoints()) {
			double[] linearFeatures = datapoint.getFeatureVector().getElements();
			int d = linearFeatures.length;
			numSquareFeatures = d + d - 1;
			double[] squareFeatures = new double[numSquareFeatures];
			for (int i = 0; i < d; ++i) {
				squareFeatures[i] = linearFeatures[i];
				featureNames.add(originalFeatureNames.get(i));
			}
			int index = d;
			for (int i = 0; i < d; ++i) {
				if (i != featureIndex) {
					squareFeatures[index++] = linearFeatures[featureIndex] * linearFeatures[i];
					featureNames.add(originalFeatureNames.get(featureIndex) + " * " + originalFeatureNames.get(i));
				}
			}
			datapoint.setFeatureVector(new DenseVector(squareFeatures));
		}
		dataset.initFeatureIndex(numSquareFeatures);
		dataset.setFeatureNames(featureNames);
	}

	/**
	 * This function adds all features defined by a featureGenerator interface to a dataset. Existing features will not be overwritten.
	 *
	 * @param <T> Dataset Type
	 * @param dataset the dataset
	 * @param featureGenerator the feature generator used to generate the features
	 * @throws Exception the exception
	 */
	public static <T extends Datapoint> void addFeatures(Dataset<T> dataset, FeatureGeneratorInterface<T> featureGenerator) throws Exception {
		int numFeatures = -1;
		for (T datapoint : dataset.getDatapoints()) {
			double[] oldFeatures = datapoint.getFeatureVector().getElements();
			double[] newFeatures = featureGenerator.generateFeature(datapoint);
			double[] features = new double[oldFeatures.length + newFeatures.length];
			System.arraycopy(oldFeatures, 0, features, 0, oldFeatures.length);
			System.arraycopy(newFeatures, 0, features, oldFeatures.length, newFeatures.length);
			datapoint.setFeatureVector(new DenseVector(features));
			numFeatures = datapoint.getNumFeatures();
		}
		dataset.initFeatureIndex(numFeatures);
		dataset.getFeatureNames().addAll(featureGenerator.getNames(dataset.getDatapoint(0)));
	}

	/**
	 * This function adds all features defined by a featureGenerator interface to a datapoint. Existing features will not be overwritten.
	 *
	 * @param <T> Datapoint Type
	 * @param datapoint the datapoint
	 * @param featureGenerator the feature generator used to generate the features
	 * @throws Exception the exception
	 */
	public static <T extends Datapoint> void addFeatures(T datapoint, FeatureGeneratorInterface<T> featureGenerator) throws Exception {
		double[] oldFeatures = datapoint.getFeatureVector().getElements();
		double[] newFeatures = featureGenerator.generateFeature(datapoint);
		double[] features = new double[oldFeatures.length + newFeatures.length];
		System.arraycopy(oldFeatures, 0, features, 0, oldFeatures.length);
		System.arraycopy(newFeatures, 0, features, oldFeatures.length, newFeatures.length);
		datapoint.setFeatureVector(new DenseVector(features));
	}

	/**
	 * Given a dataset this function returns an array of FVectors. Each containing the features of a single datapoint.
	 * The returned array is a fresh copy of the features not a simple reference to the original ones.
	 *
	 * @param dataset the dataset used to extract the feature vectors
	 * @return the feature vectors
	 */
	public static DenseVector[] getFeatureVectors(Dataset<?> dataset) {
		DenseVector[] featureVectors = new DenseVector[dataset.numFeatures()];
		for (int i = 0; i < featureVectors.length; ++i) {
			double[] features = new double[dataset.size()];
			for (int j = 0; j < dataset.size(); ++j)
				features[j] = dataset.getDatapoint(j).getFeatureAt(i);
			DenseVector featureVector = new DenseVector(features);
			featureVectors[i] = featureVector;
		}
		return featureVectors;
	}

	/**
	 * Returns a HashMap containing all features of all datapoints for a featureName.
	 *
	 * @param dataset the input dataset
	 * @return a feature HashMap 
	 */
	public static Map<String, DenseVector> getFeatureMap(Dataset<?> dataset) {
		HashMap<String, DenseVector> featureMap = new HashMap<String, DenseVector>(dataset.numFeatures());
		List<String> featureNames = dataset.getFeatureNames();
		for (int i = 0; i < featureNames.size(); ++i) {
			double[] features = new double[dataset.size()];
			for (int j = 0; j < dataset.size(); ++j)
				features[j] = dataset.getDatapoint(j).getFeatureAt(i);
			DenseVector featureVector = new DenseVector(features);
			featureMap.put(featureNames.get(i), featureVector);
		}
		return featureMap;
	}

	/**
	 * Returns an array of FeatureVectors (FVecors) containing all features defined by a subset.
	 *
	 * @param dataset the dataset
	 * @param subset the subset of features which should be extracted
	 * @return the feature vectors
	 */
	public static DenseVector[] getFeatureVectors(Dataset<?> dataset, FeatureSubset subset) {
		DenseVector[] featureVectors = new DenseVector[subset.size()];
		int index = 0;
		for (int featureIndex : subset.getFeatureIndices()) {
			double[] features = new double[dataset.size()];
			for (int j = 0; j < dataset.size(); ++j)
				features[j] = dataset.getDatapoint(j).getFeatureAt(featureIndex);
			DenseVector featureVector = new DenseVector(features);
			featureVectors[index++] = featureVector;
		}
		return featureVectors;
	}

	/**
	 * Returns an FVector containing the group information of  of all datapoints in a dataset.
	 *
	 * @param dataset the dataset
	 * @return the class vector
	 */
	public static VectorInterface getClassVector(Dataset<?> dataset) {
		VectorInterface vecY = new DenseVector(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(i++, datapoint.getGroup());
		return vecY;
	}

	/**
	 * Returns a FVector containing the value information of all datapoints in a dataset.
	 *
	 * @param dataset the dataset
	 * @return the value vector
	 */
	public static VectorInterface getValueVector(Dataset<?> dataset) {
		VectorInterface vecY = new DenseVector(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(i++, datapoint.getValue());
		return vecY;
	}

	/**
	 * Returns a FVector containing the predicted value information of all datapoints in a dataset.
	 *
	 * @param dataset the dataset
	 * @return the predicted value vector
	 */
	public static VectorInterface getPredictedValueVector(Dataset<?> dataset) {
		VectorInterface vecY = new DenseVector(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(i++, datapoint.getPredictedValue());
		return vecY;
	}

	/**
	 * Returns a FVector containing the weight information of all datapoints in a dataset.
	 *
	 * @param dataset the dataset
	 * @return the weight vector
	 */
	public static VectorInterface getWeightVector(Dataset<?> dataset) {
		VectorInterface vecY = new DenseVector(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(i++, datapoint.getWeight());
		return vecY;
	}

	/**
	 * Updates the group (class) information of a dataset given a GrouperInterface.
	 *
	 * @param dataset that should be updated
	 * @param grouper the grouper
	 */
	public static void updateGrouping(Dataset<?> dataset, GrouperInterface grouper) {
		HashMap<Integer, Integer> groupCounter = new HashMap<Integer, Integer>();
		int numPos = 0;
		int numNeg = 0;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			int group = grouper.getGroup(datapoint.getValue());
			if (group == 1) {
				datapoint.setGroup(+1);
				++numPos;
			} else {
				datapoint.setGroup(-1);
				++numNeg;
			}
		}
		groupCounter.put(-1, numNeg);
		groupCounter.put(+1, numPos);
		dataset.setGroupCounter(groupCounter);
	}

	/**
	 * Deletes all feature Vectors of a dataset. 
	 *
	 * @param dataset the dataset
	 */
	public static void deleteFeatureVectors(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			datapoint.setFeatureVector(new DenseVector(0));
		}
		dataset.initFeatureIndex(0);
		dataset.setFeatureNames(null);
	}

}
