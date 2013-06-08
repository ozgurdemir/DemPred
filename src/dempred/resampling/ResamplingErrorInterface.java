package dempred.resampling;

import dempred.classifier.ClassifierInterface;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;

public interface ResamplingErrorInterface<T extends Datapoint>{
	public double error(ClassifierInterface<T> classifier, Dataset<T> dataset) throws Exception;
}
