package dempred.featureselection;

import java.util.logging.Logger;

import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetManipulator;
import dempred.math.SimpleVector;
import dempred.math.VectorInterface;

public class FeatureRanker {

	private static final Logger logger = Logger.getLogger(FeatureRanker.class.getName());

	public static VectorInterface pearsonCorrelation(Dataset<?> dataset) {
		VectorInterface rankVector = new SimpleVector(dataset.numFeatures());
		SimpleVector vecY = new SimpleVector(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(i++, datapoint.getGroup());
		double meanY = vecY.mean();
		double stdY = vecY.std(meanY);
		for (i = 0; i < dataset.numFeatures(); ++i) {
			VectorInterface vecX = new SimpleVector(dataset.size());
			int j = 0;
			for (Datapoint datapoint : dataset.getDatapoints())
				vecX.set(j++, datapoint.getFeatureAt(i));
			double meanX = vecX.mean();
			double stdX = vecX.std(meanX);
			double meanXmulY = vecX.mulVector(vecY).mean();
			double rank = (meanXmulY - meanX * meanY) / ((stdX * stdY));
			rankVector.set(i, rank);
		}
		return rankVector;
	}

	public static VectorInterface pearsonCorrelationValues(Dataset<?> dataset) {
		VectorInterface rankVector = new SimpleVector(dataset.numFeatures());
		SimpleVector vecY = new SimpleVector(dataset.size());
		int i = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(i++, datapoint.getValue());
		double meanY = vecY.mean();
		double stdY = vecY.std(meanY);
		for (i = 0; i < dataset.numFeatures(); ++i) {
			VectorInterface vecX = new SimpleVector(dataset.size());
			int j = 0;
			for (Datapoint datapoint : dataset.getDatapoints())
				vecX.set(j++, datapoint.getFeatureAt(i));
			double meanX = vecX.mean();
			double stdX = vecX.std(meanX);
			double meanXmulY = vecX.mulVector(vecY).mean();
			double rank = (meanXmulY - meanX * meanY) / ((stdX * stdY));
			rankVector.set(i, rank);
		}
		return rankVector;
	}

	public static VectorInterface pearsonCorrelationQuad(Dataset<?> dataset, double cutoff) {
		VectorInterface rankVector = pearsonCorrelationValues(dataset).powScalar(2);
		SimpleVector vecY = new SimpleVector(dataset.size());
		int oldPercent=-1;
		int index = 0;
		for (Datapoint datapoint : dataset.getDatapoints())
			vecY.set(index++, datapoint.getValue());
		double meanY = vecY.mean();
		double stdY = vecY.std(meanY);
		SimpleVector[] featureVectors = DatasetManipulator.getFeatureVectors(dataset);
		for (int i = 0; i < featureVectors.length; ++i) {
				for (int j = i + 1; j < featureVectors.length; ++j) {
						VectorInterface vecX = featureVectors[i].clone();
						vecX.addVector(featureVectors[j]);
						double meanX = vecX.mean();
						double stdX = vecX.std(meanX);
						double meanXmulY = vecX.mulVector(vecY).mean();
						double rankpos = (meanXmulY - meanX * meanY) / ((stdX * stdY));
						rankpos = Math.pow(rankpos, 2);
						
						vecX = featureVectors[i].clone();
						vecX.subVector(featureVectors[j]);
						meanX = vecX.mean();
						stdX = vecX.std(meanX);
						meanXmulY = vecX.mulVector(vecY).mean();
						double rankneg = (meanXmulY - meanX * meanY) / ((stdX * stdY));
						rankneg = Math.pow(rankneg, 2);
						double rank = Math.max(rankpos, rankneg);
						
						if (rankVector.get(i) < rank)
							rankVector.set(i, rank);
						if (rankVector.get(j) < rank)
							rankVector.set(j, rank);
				}
			int percent = (int)Math.round((double)i/featureVectors.length*100);
			if(oldPercent != percent){
				logger.fine(String.format("%d%%", percent));
				oldPercent = percent;
			}
		}
		return rankVector;
	}

}
