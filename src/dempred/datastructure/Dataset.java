package dempred.datastructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dempred.math.SimpleVector;


/**
 * Dataset is the central structure for machine learning tasks. It contains a set of {@link dempred.datastructure.Datapoint Datapoints} plus additional information for this dataset.
 * Usually a couple of datasets are used to build a predictor. A so called training set for training and a so called testing set to test the accuracy of the predictor.
 * A predtion set is finally used to predict new unseen datapoints.
 * 
 * Dataset is not restricted to the given implementation of {@link dempred.datastructure.Datapoint Datapoint}. You may easily extend {@link dempred.datastructure.Datapoint Datapoint} and use the new extendet class instead to generate a dataset.
 * This makes dataset very flexible for all kind of classification and regression tasks.
 *
 * @param <T> the generic type of the datapoints that this dataset contains. T must be an extension of the {@link Datapoint Datapoint} class
 */
public class Dataset<T extends Datapoint> implements Cloneable, Serializable, Iterable<T> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6560594683595420293L;
	
	/** The name of this dataset (e.g. trainset, testset...). */
	private String name;
	
	/** The comment for this dataset (e.g. source, date...). */
	private String comment;
	
	/** The list of datapoints this dataset contains. */
	private List<T> datapoints;
	
	/** A simple map which contains the number of datapoints for a specific group (e.g. how many negative and how many positive datapoints does this dataset contain). */
	private Map<Integer, Integer> groupCounter;
	
	/** The feature index. This is a simple index starting from '0' to 'numberOfFeatures-1'. This is a usefull structure if at any point of model building features are deleted. This index will help to keep track which features are still used.*/
	private List<Integer> featureIndex;
	
	/** The feature names. If the used features have names or a description this list should contain these. That way the feature names can be used to identify the features at any stage of model building.*/
	private List<String> featureNames;

	/**
	 * Instantiates a new dataset with empty datapoints, new groupCounter and empty featurenames
	 */
	public Dataset() {
		name = "";
		comment = "";
		datapoints = new ArrayList<T>();
		groupCounter = new HashMap<Integer, Integer>();
		featureNames = new ArrayList<String>();
	}

	/**
	 * Returns the size of this dataset (the number of datapoints)
	 *
	 * @return the number of datapoints
	 */
	public final int size() {
		return datapoints.size();
	}

	/**
	 * Number of features this dataset contains. Since all datapoints must have the same number of features.
	 *
	 * @return the number of features used for this dataset
	 */
	public int numFeatures() {
		return featureIndex.size();
	}

	/**
	 * Number of correct predictions for a specific group.
	 *
	 * @param groupnumber the groupnumber
	 * @return the number of correct predictions
	 */
	public int numCorrectPredictions(int groupnumber) {
		int num = 0;
		for (Datapoint datapoint : this.getDatapoints()) {
			if (datapoint.getGroup() != null && datapoint.getGroup() == groupnumber && datapoint.getGroup() == datapoint.getPredictedGroup())
				++num;
		}
		return num;
	}

	/**
	 * Adds a given datapoint to this dataset. If this is the first datapoint which is added the 'featureIndex' will be automatically initialised from '0' to 'number of features -1'. All successive added datapoints must have the same number of features. An exception is thrown otherwise.
	 * If the added datapoint has group info attached (e.g. -1 for negative, +1 for positive) the groupMapping will be updated and then contains the number of datapoints for a specific group.
	 * @param datapoint the datapoint
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void addDatapoint(T datapoint) throws IllegalArgumentException {
		if (featureIndex == null)
			initFeatureIndex(datapoint.getNumFeatures());
		else if (featureIndex.size() != datapoint.getNumFeatures())
			throw new IllegalArgumentException("The inserted datapoint has not the same number of features as the previous ones!: actual:" + featureIndex.size() + " trying to insert: " + datapoint.getNumFeatures());
		datapoints.add(datapoint);
		int numGroup = 0;
		if (datapoint.hasGroup()) {
			if (groupCounter.containsKey(datapoint.getGroup()))
				numGroup = groupCounter.get(datapoint.getGroup());
			groupCounter.put(datapoint.getGroup(), numGroup + 1);
		}
	}

	/**
	 * Helper function to initialize the feature index for a given number of features
	 *
	 * @param numFeatures the number of features
	 */
	public void initFeatureIndex(int numFeatures) {
		featureIndex = new ArrayList<Integer>(numFeatures + 1);
		for (int i = 0; i < numFeatures; ++i)
			featureIndex.add(i);
	}
	
	/**
	 * Returns the number of datapoints for a specific group.
	 *
	 * @param groupnumber the groupnumber (e.g -1, +1)
	 * @return the number of datapoints with 'groupnumber' group in this dataset 
	 */
	public int groupQuantity(int groupnumber) {
		if (groupCounter.containsKey(groupnumber))
			return groupCounter.get(groupnumber);
		else
			return 0;
	}
	
	public int numGroups(){
		return this.groupCounter.size();
	}

	// feature related
	/**
	 * Utility function used to extend all datapoints in this dataset by an additional feature with value. This is used in order to encode the bias parameter 'b'.
	 *
	 * @param value the value of the last feature
	 * @return the extendet dataset
	 */
	public Dataset<T> extend(Double value) {
		for (Datapoint datapoint : this.getDatapoints()) {
			if (!datapoint.isExtended()) {
				datapoint.getFeatureVector().extendByOne(value);
				datapoint.setExtended(true);
			}
		}
		this.featureIndex.add(this.featureIndex.size());
		return this;
	}

	/**
	 * Utility function used to reduce all datapoints of this dataset. Normally used after {@link #extend(Double) Datapoints} function has been used.
	 */
	public void reduce() {
		for (Datapoint datapoint : this.getDatapoints()) {
			if (datapoint.isExtended()) {
				datapoint.getFeatureVector().reduceByOne();
				datapoint.setExtended(false);
			}
		}
		this.featureIndex.remove(this.featureIndex.size() - 1);
	}

	/**
	 * Deletes all features except those with given index. {@link #featureIndex featureIndex} and {@link #featureNames featureNames} is updated as well.
	 *
	 * @param indices the indices of the features which should be kept
	 */
	public void keepFeatures(int[] indices) {
		for (Datapoint datapoint : this.datapoints) {
			SimpleVector reducedFeatures = new SimpleVector(indices.length);
			for (int i = 0; i < indices.length; ++i)
				reducedFeatures.set(i, datapoint.getFeatureAt(indices[i]));
			datapoint.setFeatureVector(reducedFeatures);
		}
		featureIndex = new ArrayList<Integer>(indices.length);
		for (int i = 0; i < indices.length; ++i)
			featureIndex.add(indices[i]);
		if (featureNames != null) {
			ArrayList<String> newFeatureNames = new ArrayList<String>(indices.length);
			for (int i = 0; i < indices.length; ++i)
				newFeatureNames.add(featureNames.get(indices[i]));
			featureNames = newFeatureNames;
		}
	}

	/**
	 * Deletes all features with given index. {@link #featureIndex featureIndex} and {@link #featureNames featureNames} is updated as well.
	 *
	 * @param indices the indices of the features which should be deleted
	 */
	public void deleteFeatures(int[] indices) {
		Arrays.sort(indices);
		for (Datapoint datapoint : this.datapoints) {
			SimpleVector reducedFeatures = new SimpleVector(datapoint.getNumFeatures() - indices.length);
			int indexPointer = 0;
			int vectorPointer = 0;
			for (int i = 0; i < datapoint.getNumFeatures(); ++i) {
				if (indexPointer < indices.length && i == indices[indexPointer]) {
					while (indexPointer < indices.length && i == indices[indexPointer])
						++indexPointer;
				} else
					reducedFeatures.set(vectorPointer++, datapoint.getFeatureAt(i));
			}
			datapoint.setFeatureVector(reducedFeatures);
		}
		// featureIndex hier reduzieren
		List<Integer> reducedFeatureIndex = new ArrayList<Integer>(featureIndex.size() - indices.length);
		int indexPointer = 0;
		for (int i = 0; i < featureIndex.size(); ++i) {
			if (indexPointer < indices.length && i == indices[indexPointer]) {
				while (indexPointer < indices.length && i == indices[indexPointer])
					++indexPointer;
			} else
				reducedFeatureIndex.add(featureIndex.get(i));
		}
		this.featureIndex = reducedFeatureIndex;
		// featureNames hier reduzieren
		if (featureNames != null && featureNames.size() > 0) {
			List<String> reducedFeatureNames = new ArrayList<String>(featureNames.size() - indices.length);
			indexPointer = 0;
			for (int i = 0; i < featureNames.size(); ++i) {
				if (indexPointer < indices.length && i == indices[indexPointer]) {
					while (indexPointer < indices.length && i == indices[indexPointer])
						++indexPointer;
				} else
					reducedFeatureNames.add(featureNames.get(i));
			}
			this.featureNames = reducedFeatureNames;
		}
	}

	/**
	 * Gets datapoint i of this dataset.
	 *
	 * @param index the index of the datapoint
	 * @return the datapoint at index
	 */
	public T getDatapoint(int index) {
		return this.datapoints.get(index);
	}

	/**
	 * Gets the name of this dataset.
	 *
	 * @return the name of this dataset
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the name of this dataset.
	 *
	 * @param name the new name of this dataset
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the comment of this dataset.
	 *
	 * @return the comment of this dataset
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * Sets the comment of this dataset.
	 *
	 * @param comment the new comment of this dataset
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the datapoints of this dataset.
	 *
	 * @return the datapoints of this dataset
	 */
	public final List<T> getDatapoints() {
		return datapoints;
	}

	/**
	 * Gets the feature index of this dataset.
	 *
	 * @return the feature index of this dataset
	 */
	public List<Integer> getFeatureIndex() {
		return featureIndex;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String lineseperator = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer("#####printing dataset#####" + lineseperator);
		output.append("Datasetname: " + name + lineseperator);
		output.append("Datasetcomment: " + comment + lineseperator);
		output.append("Datapoints: " + datapoints.size() + lineseperator);
		output.append("FeatureIndex:" + lineseperator);
		output.append(this.featureIndex + lineseperator);
		output.append("Datapoints:" + lineseperator);
		for (Datapoint datapoint : datapoints)
			output.append(datapoint.toString() + lineseperator);
		output.append("###########end###########");
		return output.toString();
	}

	/**
	 * A short string representation of this dataset. 
	 *
	 * @return a short string representation of this dataset.
	 */
	public String toStringShort() {
		String lineseperator = System.getProperty("line.separator");
		StringBuffer output = new StringBuffer("#####printing dataset#####" + lineseperator);
		output.append("Datasetname: " + name + lineseperator);
		output.append("Datasetcomment: " + comment + lineseperator);
		output.append("Datapoints: " + datapoints.size() + lineseperator);
		output.append("FeatureIndex:" + lineseperator);
		output.append(this.featureIndex + lineseperator);
		output.append("Datapoints:" + lineseperator);
		for (Datapoint datapoint : datapoints)
			output.append(datapoint.toStringShort() + lineseperator);
		output.append("###########end###########");
		return output.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Dataset<T> clone() {
		try {
			Dataset<T> cl = (Dataset<T>) super.clone();
			cl.datapoints = new ArrayList<T>(this.size());
			cl.groupCounter = new HashMap<Integer, Integer>();
			cl.featureNames = new ArrayList<String>(this.featureNames);
			cl.featureIndex = null;
			for (T datapoint : this.getDatapoints())
				cl.addDatapoint((T) datapoint.clone());
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	/**
	 * Gets the group counter.
	 *
	 * @return the group counter
	 */
	public final Map<Integer, Integer> getGroupCounter() {
		return groupCounter;
	}

	/**
	 * Sets the group counter.
	 *
	 * @param groupCounter the group counter
	 */
	public final void setGroupCounter(Map<Integer, Integer> groupCounter) {
		this.groupCounter = groupCounter;
	}

	/**
	 * Gets the feature names.
	 *
	 * @return the feature names
	 */
	public final List<String> getFeatureNames() {
		return featureNames;
	}

	/**
	 * Sets the feature names.
	 *
	 * @param featureNames the new feature names
	 */
	public final void setFeatureNames(List<String> featureNames) {
		this.featureNames = featureNames;
	}

	@Override
	public Iterator<T> iterator() {
		return datapoints.iterator();
	}

}
