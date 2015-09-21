package com.demshape.dempred.resampling;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.lossfunction.LossFunctionInterface;


public class Bootstrap632Error<T extends Datapoint> implements ResamplingErrorInterface<T> {
	private int numRounds;
	private LossFunctionInterface<T> lossFunction;
	private boolean weighting;
	private boolean groupAveraging;

	public Bootstrap632Error() {

	}

	public Bootstrap632Error(int numRounds, LossFunctionInterface<T> lossFunction, boolean groupAveraging, boolean weighting) {
		this.numRounds = numRounds;
		this.lossFunction = lossFunction;
		this.groupAveraging = groupAveraging;
		this.weighting = weighting;
	}

	@Override
	public double error(ClassifierInterface<T> classifier, Dataset<T> dataset) throws Exception {
		double sum_pos_pred = 0.0;
		double sum_neg_pred = 0.0;
		int num_neg_pred = 0;
		int num_pos_pred = 0;
		double sum_pos_recall = 0.0;
		double sum_neg_recall = 0.0;
		int num_neg_recall = 0;
		int num_pos_recall = 0;
		Bootstrap<T> bootstrap = new Bootstrap<T>(dataset);
		for (int round = 0; round < numRounds; ++round) {
			bootstrap.generateSample();
			Dataset<T> bootMain = bootstrap.getSample();
			Dataset<T> bootFold = bootstrap.getUnsampled();
			classifier.learn(bootMain);
			classifier.predict(bootMain);
			classifier.predict(bootFold);
			double weight = 1.0;
			// for recall
			for (T datapoint : bootMain.getDatapoints()) {
				if (weighting)
					weight = datapoint.getWeight();
				if (groupAveraging && datapoint.getGroup() == 1) {
					++num_pos_recall;
					sum_pos_recall += weight * lossFunction.g(datapoint.getPredictedValue(), datapoint.getValue(), datapoint);
				} else {
					++num_neg_recall;
					sum_neg_recall += weight * lossFunction.g(datapoint.getPredictedValue(), datapoint.getValue(), datapoint);
				}
			}
			// for prediction
			for (T datapoint : bootFold.getDatapoints()) {
				if (weighting)
					weight = datapoint.getWeight();
				if (groupAveraging && datapoint.getGroup() == 1) {
					++num_pos_pred;
					sum_pos_pred += weight * lossFunction.g(datapoint.getPredictedValue(), datapoint.getValue(), datapoint);
				} else {
					++num_neg_pred;
					sum_neg_pred += weight * lossFunction.g(datapoint.getPredictedValue(), datapoint.getValue(), datapoint);
				}
			}
		}
		double recallError = 0.0;
		double predictionError = 0.0;
		if (groupAveraging) {
			recallError = ((sum_pos_recall / num_pos_recall) + (sum_neg_recall / num_neg_recall)) / 2.0;
			predictionError = ((sum_pos_pred / num_pos_pred) + (sum_neg_pred / num_neg_pred)) / 2.0;
		} else {
			recallError = sum_neg_recall / num_neg_recall;
			predictionError = sum_neg_pred / num_neg_pred;
		}
		return (0.368 * recallError + 0.632 * predictionError);
	}

	public final int getNumRounds() {
		return numRounds;
	}

	public final void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

}
