package com.demshape.dempred.datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.demshape.dempred.featureselection.FeatureSubset;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorMetric;




/**
 * A utility class that helps generating new datasets by merging, splitting...existing ones.
 */
public class DatasetGenerator {

	/**
	 * Generates a diverse dataset given a dataset. 
	 *
	 * @param <T> the generic type of the datapoints
	 * @param dataset the input dataset
	 * @param cutoff the cutoff
	 * @return list which contains two datasets. The main dataset on index 0 and the fold dataset on index 1.
	 */
	public static <T extends Datapoint> List<Dataset<T>> generateDiverse(Dataset<T> dataset, double cutoff) {
		boolean[] flag = new boolean[dataset.size()];
		Arrays.fill(flag, true);
		Dataset<T> mainDataset = new Dataset<T>();
		Dataset<T> foldDataset = new Dataset<T>();
		T datapoint1 = null;
		T datapoint2 = null;
		double distance;
		double minDistance;
		int minIndex = 0;
		T minDatapoint = null;
		for (int i = 0; i < dataset.size(); ++i) {
			minDistance = Double.POSITIVE_INFINITY;
			if (!flag[i])
				continue;
			for (int j = i; j < dataset.size(); ++j) {
				datapoint1 = dataset.getDatapoint(i);
				datapoint2 = dataset.getDatapoint(j);
				distance = VectorMetric.euclidean(datapoint1.getFeatureVector(), datapoint2.getFeatureVector());
				if (distance < minDistance && datapoint1.getGroup() != datapoint2.getGroup() && flag[j]) {
					minDistance = distance;
					minDatapoint = datapoint2;
					minIndex = j;
				}
			}
			mainDataset.addDatapoint(datapoint1);
			if (minDistance < cutoff) {
				flag[minIndex] = false;
				foldDataset.addDatapoint(minDatapoint);
			}
		}
		ArrayList<Dataset<T>> resultDatasets = new ArrayList<Dataset<T>>(2);
		resultDatasets.add(mainDataset);
		resultDatasets.add(foldDataset);
		return resultDatasets;
	}
	
	/**
	 * Generates a new dataset by taking only those datapoints which are correctly predicted. 
	 *
	 * @param <T> the generic type
	 * @param dataset the input dataset which must have been predicted
	 * @return dataset which contains only those datapoints which are correctly predicted
	 */
	public static <T extends Datapoint> Dataset<T> getCorrectClassified(Dataset<T> dataset) {
		Dataset<T> resultDataset = new Dataset<T>();
		for (T datapoint : dataset.getDatapoints()) {
			if (datapoint.getGroup() == datapoint.getPredictedGroup())
				resultDataset.addDatapoint(datapoint);
		}
		return resultDataset;
	}

	/**
	 * Generates a new dataset by taking only those datapoints which are wrongly predicted. 
	 *
	 * @param <T> the generic type
	 * @param dataset the input dataset which must have been predicted
	 * @return dataset which contains only those datapoints which are wrongly predicted
	 */
	public static <T extends Datapoint> Dataset<T> getMissclassified(Dataset<T> dataset) {
		Dataset<T> resultDataset = new Dataset<T>();
		for (T datapoint : dataset.getDatapoints()) {
			if (datapoint.getGroup() != datapoint.getPredictedGroup())
				resultDataset.addDatapoint(datapoint);
		}
		return resultDataset;
	}

	/**
	 * Gets indices of all missclassified datapoints.
	 *
	 * @param dataset the input dataset
	 * @return the missclassified indices
	 */
	public static ArrayList<Integer> getMissclassifiedIndices(Dataset<?> dataset) {
		ArrayList<Integer> indices = new ArrayList<Integer>(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			++i;
			if (datapoint.getPredictedGroup() != datapoint.getGroup())
				indices.add(i);
		}
		return indices;
	}

	/**
	 * Creates a new dataset by finding the nearest datapoint in dataset2 for all datapoints in dataset1. 
	 *
	 * @param <T> the generic type
	 * @param dataset1 the input dataset1
	 * @param dataset2 the input dataset2
	 * @return the result dataset with nearest datapoints
	 */
	public static <T extends Datapoint> Dataset<T> findNearest(Dataset<T> dataset1, Dataset<T> dataset2) {
		Dataset<T> resultDataset = new Dataset<T>();
		double distance;
		double minDistance;
		T minDatapoint = null;
		for (T datapoint1 : dataset1.getDatapoints()) {
			minDistance = Double.POSITIVE_INFINITY;
			for (T datapoint2 : dataset2.getDatapoints()) {
				distance = VectorMetric.euclidean(datapoint1.getFeatureVector(), datapoint2.getFeatureVector());
				if (distance < minDistance) {
					minDistance = distance;
					minDatapoint = datapoint2;
				}
			}
			resultDataset.addDatapoint(datapoint1);
			resultDataset.addDatapoint(minDatapoint);
		}
		return resultDataset;
	}

	/**
	 * Creates a new dataset by joining two existing ones.
	 *
	 * @param <T> the generic type
	 * @param dataset1 the input dataset1
	 * @param dataset2 the input dataset2
	 * @return the joined (merged) dataset
	 */
	public static <T extends Datapoint> Dataset<T> join(Dataset<T> dataset1, Dataset<T> dataset2) {
		Dataset<T> resultDataset = new Dataset<T>();
		resultDataset.setName(dataset1.getName() + " JOINED WITH " + dataset2.getName());
		for (T datapoint1 : dataset1.getDatapoints())
			resultDataset.addDatapoint(datapoint1);
		for (T datapoint2 : dataset2.getDatapoints())
			resultDataset.addDatapoint(datapoint2);
		return resultDataset;
	}

	/**
	 * Adds all datapoints of dataset2 to dataset1.
	 *
	 * @param <T> the generic type
	 * @param dataset1 the input dataset1
	 * @param dataset2 the input dataset2
	 * @return dataset1 extendet by all datapoints of dataset2
	 */
	public static <T extends Datapoint> Dataset<T> add(Dataset<T> dataset1, Dataset<T> dataset2) {
		for (T datapoint2 : dataset2.getDatapoints())
			dataset1.addDatapoint(datapoint2);
		return dataset1;
	}

	/**
	 * Adds all datapoints of dataset2 to dataset1. In contrast to {@link #add(Dataset, Dataset) add} a new datapoint is created before adding which only contains some basic infos. These basic infos are:
	 * 'group, predictedGroup, value, predictedValue, weight, comment'.
	 * This function should be used if only these base infos are needed as it only needs few space. 
	 * Since datapoints are copied before adding the original datapoints can be manipulated without affecting the added ones.
	 *
	 * @param dataset1 the input dataset1
	 * @param dataset2 the input dataset2
	 * @return dataset1 extendet by all base infos of all datapoints of dataset2
	 */
	public static Dataset<Datapoint> addBaseInfoOnly(Dataset<Datapoint> dataset1, Dataset<?> dataset2) {
		for (Datapoint datapoint2 : dataset2.getDatapoints()) {
			Datapoint datapoint = new Datapoint();
			datapoint.setGroup(datapoint2.getGroup());
			datapoint.setPredictedGroup(datapoint2.getPredictedGroup());
			datapoint.setPredictedValue(datapoint2.getPredictedValue());
			datapoint.setValue(datapoint2.getValue());
			datapoint.setWeight(datapoint2.getWeight());
			datapoint.setComment(datapoint2.getComment());
			dataset1.addDatapoint(datapoint);
		}
		return dataset1;
	}

	/**
	 * Generates two new datasets by randomly splitting a given dataset at a predefined number.
	 *
	 * @param <T> the generic type
	 * @param dataset the input dataset
	 * @param number the number of datapoints the first dataset should have
	 * @return a list of two datasets. The first dataset contains 'number' datapoints and the second dataset contains all the rest
	 */
	public static <T extends Datapoint> ArrayList<Dataset<T>> split(Dataset<T> dataset, int number) {
		return split(dataset, ((double) number / dataset.size()));
	}

	/**
	 * Generates two new datasets by randomly splitting a given dataset at a defined ratio 
	 *
	 * @param <T> the generic type
	 * @param dataset the dataset
	 * @param ratio the ratio (0.0 - 1.0). e.g. 0.5 means that both datasets contain around 50% of the original dataset 
	 * @return a list of two datasets. The first dataset contains 'ratio' datapoints and the second dataset contains '1.0-ratio' datapoints
	 */
	public static <T extends Datapoint> ArrayList<Dataset<T>> split(Dataset<T> dataset, double ratio) {
		Dataset<T> splitted1 = new Dataset<T>();
		splitted1.setName(dataset.getName() + " splitted1");
		splitted1.setComment(dataset.getComment());
		Dataset<T> splitted2 = new Dataset<T>();
		splitted2.setName(dataset.getName() + " splitted2");
		splitted2.setComment(dataset.getComment());
		int numSplitted = (int) Math.round(ratio * dataset.size());
		ArrayList<T> completeList = new ArrayList<T>(dataset.size());
		completeList.addAll(dataset.getDatapoints());
		Collections.shuffle(completeList);
		for (int i = 0; i < dataset.size(); ++i) {
			if (i < numSplitted)
				splitted1.addDatapoint(completeList.get(i));
			else
				splitted2.addDatapoint(completeList.get(i));
		}
		ArrayList<Dataset<T>> result = new ArrayList<Dataset<T>>();
		result.add(splitted1);
		result.add(splitted2);
		return result;
	}

	/**
	 * Returns a new dataset which only contains datapoints of a single group.
	 *
	 * @param <T> the generic type
	 * @param dataset the input dataset
	 * @param group the group of returned datapoints
	 * @return dataset which only contains datapoints of a single group.
	 */
	public static <T extends Datapoint> Dataset<T> getGroup(Dataset<T> dataset, int group) {
		Dataset<T> resultDataset = new Dataset<T>();
		resultDataset.setName(dataset.getName() + " ONLY GROUP: " + group);
		for (T datapoint : dataset.getDatapoints()) {
			if (datapoint.getGroup() == group)
				resultDataset.addDatapoint(datapoint);
		}
		return resultDataset;
	}

	/**
	 * Generates a new dataset which contains all datapoints from the original input dataset but only with a subset of features.
	 *
	 * @param <T> the generic type
	 * @param dataset the dataset
	 * @param subset a feature subset
	 * @return a dataset which contains all datapoints from the original input dataset but only with a subset of features.
	 */
	public static <T extends Datapoint> Dataset<T> generateSubset(Dataset<T> dataset, FeatureSubset subset) {
		Dataset<T> returnDataset = new Dataset<T>();
		returnDataset.setName(dataset.getName() + "-Subset");
		returnDataset.setComment("Generated from a feature subset of the Dataset: " + dataset.getName());
		int numFeatures = subset.size();
		for (int i = 0; i < dataset.size(); ++i) {
			T originalDatapoint = dataset.getDatapoint(i);
			T datapoint = (T) originalDatapoint.clone();
			DenseVector featureVector = new DenseVector(numFeatures);
			int index = 0;
			for (int featureIndex : subset.getFeatureIndices())
				featureVector.set(index++, originalDatapoint.getFeatureAt(featureIndex));
			datapoint.setFeatureVector(featureVector);
			returnDataset.addDatapoint(datapoint);
		}
		return returnDataset;
	}

}
