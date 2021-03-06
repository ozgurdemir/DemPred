package com.demshape.dempred.transformer;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;

public class DatasetTransformer {
	private TransformationFunctionInterface transformationFunction;

	public DatasetTransformer() {

	}

	public DatasetTransformer(TransformationFunctionInterface transformationFunction) {
		this.transformationFunction = transformationFunction;
	}

	public void transformDataset(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			datapoint.setValue(transformationFunction.transform(datapoint.getValue()));
			datapoint.setPredictedValue(transformationFunction.transform(datapoint.getPredictedValue()));
		}
	}

	public void retransformDataset(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			datapoint.setValue(transformationFunction.retransform(datapoint.getValue()));
			datapoint.setPredictedValue(transformationFunction.retransform(datapoint.getPredictedValue()));
		}
	}
	
	public void transformPredictedValues(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) 
			datapoint.setPredictedValue(transformationFunction.transform(datapoint.getPredictedValue()));
	}

	public void retransformPredictedValues(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) 
			datapoint.setPredictedValue(transformationFunction.retransform(datapoint.getPredictedValue()));
	}

	
	public void transformValues(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) 
			datapoint.setValue(transformationFunction.transform(datapoint.getValue()));
	}
	
	public void retransformValues(Dataset<?> dataset) {
		for (Datapoint datapoint : dataset.getDatapoints()) 
			datapoint.setValue(transformationFunction.retransform(datapoint.getValue()));
	}

	public final TransformationFunctionInterface getTransformationFunction() {
		return transformationFunction;
	}

	public final void setTransformationFunction(TransformationFunctionInterface transformationFunction) {
		this.transformationFunction = transformationFunction;
	}

}
