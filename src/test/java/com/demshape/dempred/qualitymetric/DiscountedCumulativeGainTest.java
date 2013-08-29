package com.demshape.dempred.qualitymetric;

import junit.framework.TestCase;

import org.junit.Test;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;

public class DiscountedCumulativeGainTest extends TestCase {

	private Dataset<Datapoint> dataset;
	double[] measured = { 2, 1, 0, 3, 2, 3 };
	double[] predicted = { 1, 2, 3, 4, 5, 6 };

	public void setUp() {
		dataset = new Dataset<Datapoint>();
		for (int i = 0; i < measured.length; ++i) {
			Datapoint datapoint = new Datapoint();
			datapoint.setValue(measured[i]);
			datapoint.setPredictedValue(predicted[i]);
			dataset.addDatapoint(datapoint);
		}
	}

	@Test
	public void testCumulativeGain() {
		DiscountedCumulativeGain discountedCumulativeGain = new DiscountedCumulativeGain(new DiscountedCumulativeGain.CumulativeGainScorer());
		assertEquals(discountedCumulativeGain.gain(dataset), 11.0);
		assertEquals(discountedCumulativeGain.gain(dataset, 4), 8.0);
	}
	
	
	@Test
	public void testDiscontinuedCumulativeGain() {
		DiscountedCumulativeGain discountedCumulativeGain = new DiscountedCumulativeGain(new DiscountedCumulativeGain.DiscountedCumulativeGainScorer());
		assertEquals(discountedCumulativeGain.gain(dataset), 8.1, 0.01);
		assertEquals(discountedCumulativeGain.gain(dataset, 4), 6.9, 0.01);
	}

}
