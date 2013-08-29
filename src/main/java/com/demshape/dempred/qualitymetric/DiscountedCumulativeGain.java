package com.demshape.dempred.qualitymetric;

import java.util.Collections;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;

public class DiscountedCumulativeGain {

	/**
	 * this class has a pluggable gainscorer so that different scoring schemes
	 * can be used
	 */
	private GainScorerInterface gainScorerInterface;

	public DiscountedCumulativeGain() {

	}

	public DiscountedCumulativeGain(GainScorerInterface gainScorerInterface) {
		super();
		this.gainScorerInterface = gainScorerInterface;
	}

	/**
	 * computes unnormalized gain
	 * 
	 * @param dataset
	 * @return
	 */
	public double gain(Dataset<?> dataset) {
		return gain(dataset, dataset.size());
	}

	public double gain(Dataset<?> dataset, int k) {
		Collections.sort(dataset.getDatapoints(), new Datapoint.PredictedValueComparatorDesc());
		return computeSorted(dataset, k);
	}

	/**
	 * computes optimal gain
	 * 
	 * @param dataset
	 * @return
	 */
	public double optimalGain(Dataset<?> dataset) {
		return optimalGain(dataset, dataset.size());
	}

	public double optimalGain(Dataset<?> dataset, int k) {
		Collections.sort(dataset.getDatapoints(), new Datapoint.ValueComparatorDesc());
		return computeSorted(dataset, k);
	}

	public double normalizedGain(Dataset<?> dataset, int k) {
		double dcg = gain(dataset, k);
		double optimum = optimalGain(dataset, k);
		return dcg / optimum;
	}

	private double computeSorted(Dataset<?> dataset, int k) {
		double result = 0.0;
		int index = 0;
		for (int i = 0; i < k; ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			result += gainScorerInterface.score(datapoint.getValue(), index++);
		}
		return result;
	}

	public GainScorerInterface getGainScorerInterface() {
		return gainScorerInterface;
	}

	public void setGainScorerInterface(GainScorerInterface gainScorerInterface) {
		this.gainScorerInterface = gainScorerInterface;
	}

	/**
	 * 
	 * @author ozgurdemir
	 * 
	 */
	public interface GainScorerInterface {
		public double score(double score, int position);
	}

	/**
	 * computes cumulative gain. Similar to DiscountedCumulativeGain but does
	 * not take ordering into account
	 * 
	 * @author ozgurdemir
	 * 
	 */
	public static class CumulativeGainScorer implements GainScorerInterface {

		public double score(double score, int position) {
			return score;
		}

	}

	/**
	 * computes the DiscountedCumulativeGain. See
	 * http://en.wikipedia.org/wiki/Discounted_cumulative_gain Predicted values
	 * define the ordering. The measured values reflect the real relevance.
	 * 
	 * @author ozgurdemir
	 * 
	 */
	public static class DiscountedCumulativeGainScorer implements GainScorerInterface {

		public double score(double score, int position) {
			if (position == 0)
				return score;
			else
				return score / log2(position + 1);
		}
	}

	/**
	 * helper function to compute logarithm of base 2
	 * 
	 * @param x
	 * @return
	 */
	private static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}
}
