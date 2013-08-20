package com.demshape.dempred.classifier;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.grouper.GrouperInterface;


/**
 * This is the basic interface for a supervised learner...
 *
 * @param <T> the generic type of the learner
 */
public interface ClassifierInterface<T extends Datapoint> {
	
	/**
	 * Basic function for learning a classifier given a dataset...
	 *
	 * @param dataset the dataset to learn from
	 * @throws Exception the exception
	 */
	public void learn(Dataset<T> dataset) throws Exception;

	/**
	 * Basic function to predict a dataset.
	 *
	 * @param dataset the dataset
	 * @throws Exception the exception
	 */
	public void predict(Dataset<T> dataset) throws Exception;

	/**
	 * Basic function to predict a single datapoint
	 *
	 * @param datapoint the datapoint
	 * @return the double
	 * @throws Exception the exception
	 */
	public double predict(T datapoint) throws Exception;
	
	/**
	 * Sets the grouper.
	 *
	 * @param grouper the new grouper
	 */
	public void setGrouper(GrouperInterface grouper);

	/**
	 * Gets the grouper.
	 *
	 * @return the grouper
	 */
	public GrouperInterface getGrouper();

	/**
	 * A function to clone the classifier.
	 *
	 * @return the classifier interface
	 */
	public ClassifierInterface<T> clone();
}
