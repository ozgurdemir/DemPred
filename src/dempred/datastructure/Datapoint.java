
package dempred.datastructure;
import java.io.Serializable;
import java.util.Comparator;

import dempred.math.DenseVector;
import dempred.math.VectorInterface;

/**
 * Datapoint is the central structure for machine learning tasks. A single
 * Datapoint contains all descriptors (features) together with group (for
 * classification) and value (for regression) information.
 */
public class Datapoint implements Cloneable, Serializable {

	/** Used for additional information on a datapoint. */
	protected String comment = "";

	/**
	 * The group of a datapoint for classification tasks (e.g. -1 for negative,
	 * +1 for positive datapoints)
	 */
	protected Integer group;

	/**
	 * Can be used to weigh more reliable data points higher than others during
	 * learning. OUtliers for example should be weighted lower than other
	 * datapoints. E.g. all datapoints weight=1.0 and outliers weight=0.5
	 */
	protected double weight = Double.NaN;

	/**
	 * The value of the datapoint. For regression tasks this is the value which
	 * the classifier tries to predict. For classification tasks 'value' should
	 * be set to -1 or +1 for negative and positive datapoints respectively.
	 */
	protected double value = Double.NaN;

	/** Contains the predicted group after prediction. */
	protected Integer predictedGroup;

	/** Contains predicted value after prediction. */
	protected double predictedValue = Double.NaN;

	/** The feature vector which contains all descriptors of this datapoint. */
	protected VectorInterface featureVector;

	/**
	 * A boolean indicating if this datapoint's featureVector is extendet by an
	 * additional value set to 1.0. This is used to encode the bias parameter
	 * 'b'
	 */
	protected boolean extended;

	/**
	 * Instantiates a new datapoint with no features.
	 */
	public Datapoint() {
		featureVector = new DenseVector(0);
		extended = false;
	}

	/**
	 * Instantiates a new datapoint with 'dim' descriptors
	 * 
	 * @param dim
	 *            the number of features (descriptors) the datapoint should have
	 */
	public Datapoint(int dim) {
		this.featureVector = new DenseVector(dim);
	}

	/**
	 * Gets the number of features (descriptors).
	 * 
	 * @return the number features (descriptors)
	 */
	public final int getNumFeatures() {
		return featureVector.size();
	}

	/**
	 * Checks if the datapoint has group info set.
	 * 
	 * @return true, if datapoint has group info
	 */
	public boolean hasGroup() {
		return (this.group != null);
	}

	/**
	 * Returns if group info is set and if the predicted group equals the given
	 * group => true prediction.
	 * 
	 * @return true, if predicted group is correct
	 */
	public boolean groupCorrect() {
		return (this.group != null && this.group == this.predictedGroup);
	}

	/**
	 * Sets the feature at a given index.
	 * 
	 * @param index
	 *            the index of the feature
	 * @param feature
	 *            the feature value
	 */
	public final void setFeatureAt(int index, Double feature) {
		featureVector.set(index, feature);
	}

	/**
	 * Gets the feature at a given index
	 * 
	 * @param index
	 *            the index of the feature
	 * @return the value of the feature at 'index'
	 */
	public final double getFeatureAt(int index) {
		return featureVector.get(index);
	}

	/**
	 * Gets the group (e.g. -1, +1).
	 * 
	 * @return group information
	 */
	public final Integer getGroup() {
		return group;
	}

	/**
	 * Sets the group (e.g. -1, +1)
	 * 
	 * @param group
	 *            the new group of this datapoint
	 */
	public final void setGroup(Integer group) {
		this.group = group;
	}

	/**
	 * Gets the feature vector.
	 * 
	 * @return the feature vector
	 */
	public final VectorInterface getFeatureVector() {
		return featureVector;
	}

	/**
	 * Sets the feature vector.
	 * 
	 * @param features
	 *            the new feature vector
	 */
	public final void setFeatureVector(VectorInterface features) {
		this.featureVector = features;
	}

	/**
	 * Gets the predicted group.
	 * 
	 * @return the predicted group
	 */
	public final Integer getPredictedGroup() {
		return predictedGroup;
	}

	/**
	 * Sets the predicted group.
	 * 
	 * @param classification
	 *            the new predicted group
	 */
	public final void setPredictedGroup(Integer classification) {
		this.predictedGroup = classification;
	}

	/**
	 * Gets the comment.
	 * 
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 * 
	 * @param comment
	 *            the new comment
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String output = String.format("%s,%d,%.4f,%.4f,%d,%.4f --> %s", comment, group, weight, value, predictedGroup, predictedValue, featureVector.toString());
		return output;
	}

	/**
	 * A short String representation of this datapoint. The returned values are:
	 * 'comment, group, weight, value, predictedGroup, predictedValue, number of
	 * features'
	 * 
	 * @return string representation of this datapoint
	 */
	public String toStringShort() {
		String output = String.format("%s,%d,%.4f,%.4f,%d,%.4f --> %d features", comment, group, weight, value, predictedGroup, predictedValue, featureVector.size());
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Datapoint clone() {
		try {
			Datapoint newDatapoint = (Datapoint) super.clone();
			newDatapoint.featureVector = this.featureVector.clone();
			return newDatapoint;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	/**
	 * Checks if is extended.
	 * 
	 * @return true, if is extended
	 */
	public final boolean isExtended() {
		return extended;
	}

	/**
	 * Sets if extended.
	 * 
	 * @param extended
	 *            the extended status
	 */
	public final void setExtended(boolean extended) {
		this.extended = extended;
	}

	/**
	 * Gets the weight.
	 * 
	 * @return the weight
	 */
	public final double getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 * 
	 * @param weight
	 *            the new weight
	 */
	public final void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public final double getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the new value
	 */
	public final void setValue(double value) {
		this.value = value;
	}

	/**
	 * Gets the predicted value.
	 * 
	 * @return the predicted value
	 */
	public final double getPredictedValue() {
		return predictedValue;
	}

	/**
	 * Sets the predicted value.
	 * 
	 * @param predictedValue
	 *            the new predicted value
	 */
	public final void setPredictedValue(double predictedValue) {
		this.predictedValue = predictedValue;
	}

	/**
	 * An internal comparator class used to sort datapoint by their value in
	 * ascending order.
	 */
	public static class ValueComparatorAsc implements Comparator<Datapoint> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Datapoint a, Datapoint b) {
			if (a.value < b.value)
				return -1;
			else if (a.value > b.value)
				return 1;
			else
				return 0;
		}
	}

	/**
	 * An internal comparator class used to sort datapoint by their value in
	 * descending order.
	 */
	public static class ValueComparatorDesc implements Comparator<Datapoint> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Datapoint a, Datapoint b) {
			if (a.value < b.value)
				return 1;
			else if (a.value > b.value)
				return -1;
			else
				return 0;
		}
	}

	/**
	 * An internal comparator class used to sort datapoint by their predicted
	 * value in ascending order.
	 */
	public static class PredictedValueComparatorAsc implements Comparator<Datapoint> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Datapoint a, Datapoint b) {
			if (a.predictedValue < b.predictedValue)
				return -1;
			else if (a.predictedValue > b.predictedValue)
				return 1;
			else
				return 0;
		}
	}

	/**
	 * An internal comparator class used to sort datapoint by their predicted
	 * value in descending order.
	 */
	public static class PredictedValueComparatorDesc implements Comparator<Datapoint> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Datapoint a, Datapoint b) {
			if (a.predictedValue < b.predictedValue)
				return 1;
			else if (a.predictedValue > b.predictedValue)
				return -1;
			else
				return 0;
		}
	}

}
