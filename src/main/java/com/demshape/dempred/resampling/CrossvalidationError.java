package com.demshape.dempred.resampling;

import java.io.Serializable;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.lossfunction.LossFunctionInterface;



public class CrossvalidationError<T extends Datapoint> implements ResamplingErrorInterface<T>, Serializable {
	
	private static final long serialVersionUID = 7387976240805852103L;
	private int numRounds;
	private int numFolds;
	private LossFunctionInterface<T> lossFunction;
	private boolean weighting;
	private boolean groupAveraging;
	
	public void CrossValidation(){
		
	}

	public CrossvalidationError(int numFolds, int numRounds, LossFunctionInterface<T> lossFunction, boolean groupAveraging, boolean weighting) {
		this.numFolds = numFolds;
		this.numRounds = numRounds;
		this.lossFunction = lossFunction;
		this.groupAveraging = groupAveraging;
		this.weighting = weighting;
	}

	@Override
	public double error(ClassifierInterface<T> classifier, Dataset<T> dataset) throws Exception {
		double sum_pos = 0.0;
		double sum_neg = 0.0;
		int num_neg = 0;
		int num_pos = 0;
		CrossValidation<T> crossValidation = new CrossValidation<T>(dataset);
		for (int round = 0; round < numRounds; ++round) {
			crossValidation.generateFolds(numFolds);
			for (int fold = 0; fold < numFolds; ++fold) {
				
				Dataset<T> crossMain = crossValidation.getFoldsExcept(fold);
				Dataset<T> crossFold = crossValidation.getFold(fold);
				
				/*
				Dataset<T> crossMain = crossValidation.getFoldsExcept(fold).clone();
				Dataset<T> crossFold = crossValidation.getFold(fold).clone();
				DatasetNormalizer normalizer = new DatasetNormalizer(crossMain);
				normalizer.normalize(crossMain);
				normalizer.normalize(crossFold);
				*/
				
				classifier.learn(crossMain);
				classifier.predict(crossFold);
				double weight = 1.0;
				for (T datapoint : crossFold.getDatapoints()) {
					if (weighting)
						weight = datapoint.getWeight();
					if (groupAveraging && datapoint.getGroup() == 1) {
						++num_pos;
						sum_pos += weight * lossFunction.g(datapoint.getPredictedValue(), datapoint.getValue(), datapoint);
					} else {
						++num_neg;
						sum_neg += weight * lossFunction.g(datapoint.getPredictedValue(), datapoint.getValue(), datapoint);
					}
				}
			}
		}
		double predictionError = 0.0;
		if (groupAveraging)
			predictionError = ((sum_pos / num_pos) + (sum_neg / num_neg)) / 2.0;
		else
			predictionError = sum_neg / num_neg;
		return predictionError;
	}

	public final int getNumRounds() {
		return numRounds;
	}

	public final void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public final int getNumFolds() {
		return numFolds;
	}

	public final void setNumFolds(int numFolds) {
		this.numFolds = numFolds;
	}

	public final LossFunctionInterface<T> getLossFunction() {
		return lossFunction;
	}

	public final void setLossFunction(LossFunctionInterface<T> lossFunction) {
		this.lossFunction = lossFunction;
	}

	public final boolean isWeighting() {
		return weighting;
	}

	public final void setWeighting(boolean weighting) {
		this.weighting = weighting;
	}

}
