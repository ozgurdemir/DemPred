package com.demshape.dempred.resampling;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.lossfunction.LossFunctionInterface;


public class BootstrapError<T extends Datapoint> implements ResamplingErrorInterface<T> {
	private int numRounds;
	private LossFunctionInterface<T> lossFunction;
	private boolean weighting;
	private boolean groupAveraging;

	public BootstrapError() {

	}

	public BootstrapError(int numRounds, LossFunctionInterface<T> lossFunction, boolean groupAveraging, boolean weighting) {
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
		Bootstrap<T> bootstrap = new Bootstrap<T>(dataset);
		for (int round = 0; round < numRounds; ++round) {
			bootstrap.generateSample();
			Dataset<T> bootMain = bootstrap.getSample();
			Dataset<T> bootFold = bootstrap.getUnsampled();
			classifier.learn(bootMain);
			classifier.predict(bootFold);
			double weight = 1.0;
			for (T datapoint : bootFold.getDatapoints()) {
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

}
