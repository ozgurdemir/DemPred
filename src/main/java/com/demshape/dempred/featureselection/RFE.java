package com.demshape.dempred.featureselection;

import java.util.logging.Logger;

import com.demshape.dempred.classifier.AbstractLinearClassifier;
import com.demshape.dempred.classifier.WrapperPrimal;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.math.VectorInterface;



public class RFE<T extends Datapoint> {
	private static final Logger logger = Logger.getLogger(RFE.class.getName());
	private AbstractLinearClassifier<T> classifier;
	private Dataset<T> dataset;
	private byte rankMethod;

	public RFE(AbstractLinearClassifier<T> classifier, Dataset<T> dataset, byte rankMethod) {
		this.classifier = classifier;
		this.dataset = dataset;
		this.rankMethod = rankMethod;
	}

	public VectorInterface computeRank() throws Exception {
		VectorInterface rank;
		classifier.learn(dataset);
		if (rankMethod == 0)
			rank = classifier.effectObjFunc(dataset);
		else if (rankMethod == 1) {
			rank = classifier.getWeight().clone();
			rank.reduceByOne();
			rank.abs();
		} else if (rankMethod == 2)
			rank = classifier.effectObjRetrain(dataset);
		else
			throw new IllegalArgumentException("The selected ranking method does not exist: " + rankMethod);
		return rank;
	}

	public int[] select(int numDeletions) throws Exception {
		VectorInterface rank = computeRank();
		return rank.minIndex(numDeletions);
	}

}
