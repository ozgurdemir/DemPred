package dempred.transformer;

import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;

public class FeatureTransformer {

	private TransformationFunctionInterface transformer;

	public FeatureTransformer() {

	};

	public FeatureTransformer(TransformationFunctionInterface transformer) {
		this.transformer = transformer;
	};

	public void transformFeatures(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			double[] features = datapoint.getFeatureVector().getElements();
			for (int i = 0, len = features.length; i < len; ++i)
				features[i] = transformer.transform(features[i]);
		}
	}

	public void transformFeature(Dataset<?> dataset, int index) {
		for (Datapoint datapoint : dataset.getDatapoints())
			datapoint.setFeatureAt(index, transformer.transform(datapoint.getFeatureAt(index)));
	}

	public TransformationFunctionInterface getTransformer() {
		return transformer;
	}

	public void setTransformer(TransformationFunctionInterface transformer) {
		this.transformer = transformer;
	}

}
