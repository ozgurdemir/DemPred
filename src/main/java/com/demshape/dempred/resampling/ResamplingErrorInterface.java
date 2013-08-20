package com.demshape.dempred.resampling;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;


public interface ResamplingErrorInterface<T extends Datapoint>{
	public double error(ClassifierInterface<T> classifier, Dataset<T> dataset) throws Exception;
}
