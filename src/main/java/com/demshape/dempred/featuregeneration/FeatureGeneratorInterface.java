package com.demshape.dempred.featuregeneration;

import java.util.List;

import com.demshape.dempred.datastructure.Datapoint;



/**
 * The Interface all feature generators have to implement.
 *
 * @param <T> the generic type of the feature generator
 */
public interface FeatureGeneratorInterface <T extends Datapoint>{
	
	/**
	 * Given a dataset this function has to return an array of double values containing the features for this datapoint.
	 *
	 * @param datapoint the datapoint
	 * @return the double[]
	 * @throws Exception the exception
	 */
	public double[] generateFeature(T datapoint) throws Exception;
	
	/**
	 * This function has to return the names of the computed features...
	 *
	 * @param datapoint the datapoint
	 * @return the names
	 */
	public List<String> getNames(T datapoint);
}
