package com.demshape.dempred.resampling;

import java.util.logging.Logger;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.losslunction.LossFunctionInterface;



public class Bootstrap632PlusError<T extends Datapoint> implements ResamplingErrorInterface<T> {
	private static final Logger logger = Logger.getLogger(Bootstrap632PlusError.class.getName());
	private int numRounds;
	private LossFunctionInterface<T> lossFunction;
	private boolean weighting;
	private boolean groupAveraging;

	public Bootstrap632PlusError() {

	}

	public Bootstrap632PlusError(int numRounds, LossFunctionInterface<T> lossFunction, boolean groupAveraging, boolean weighting) {
		this.numRounds = numRounds;
		this.lossFunction = lossFunction;
		this.groupAveraging = groupAveraging;
		this.weighting = weighting;
	}

	// dies gibt, dasselbe resultat wie wenn man es wie im paper beschrieben macht.
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
		double prior = 0.0;
		double posterior = 0.0;
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
			// prio and posterior
			prior += bootMain.groupQuantity(1);
			for (Datapoint datapoint : bootMain.getDatapoints()) {
				if (datapoint.getPredictedGroup() == 1)
					++posterior;
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
		logger.fine(String.format("recallError:%.4f predictionError:%.4f", recallError, predictionError));

		prior /= num_pos_recall + num_neg_recall;
		posterior /= num_pos_recall + num_neg_recall;
		double gamma = prior * (1 - posterior) + (1 - prior) * posterior;
		double r = (predictionError - recallError) / (gamma - recallError);
		double weight = 0.632 / (1.0 - 0.368 * r);
		if (weight > 1.0)
			weight = 1.0;
		else if (weight < 0.0)
			weight = 0.0;
		double result = (1 - weight) * (recallError) + weight * (predictionError);
		logger.fine(String.format("prior: %f posterior: %f gamma: %.4f OverfittingRate: %.4f weight: %.4f %n", prior, posterior, gamma, r, weight));
		return result;
	}

	public final int getNumRounds() {
		return numRounds;
	}

	public final void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}
}
