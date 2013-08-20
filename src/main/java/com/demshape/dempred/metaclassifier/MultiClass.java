package com.demshape.dempred.metaclassifier;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.MultigroupDatapoint;
import com.demshape.dempred.datastructure.MultigroupPrediction;
import com.demshape.dempred.grouper.GrouperInterface;



public class MultiClass<T extends MultigroupDatapoint> implements ClassifierInterface<T>, Serializable {

	private static final long serialVersionUID = -7882351684133280142L;
	private static final Logger logger = Logger.getLogger(MultiClass.class.getName());
	ClassifierInterface<T> baseClassifier;
	private Map<Integer, ClassifierInterface<T>> classifiers;
	private Map<Integer, Integer> groupCounter;
	private int numGroups;

	public MultiClass(ClassifierInterface<T> classifier, Dataset<T> dataset) {
		this.baseClassifier = classifier;
		this.groupCounter = dataset.getGroupCounter();
		this.numGroups = groupCounter.size();
		this.classifiers = new TreeMap<Integer, ClassifierInterface<T>>();
	}

	public void learn(Dataset<T> dataset) throws Exception {
		int index = 0;
		for (Entry<Integer, Integer> group : groupCounter.entrySet()) {
			logger.fine(String.format("learning group %d of %d", ++index, numGroups));
			int currentGroup = group.getKey();
			Dataset<T> learnDataset = new Dataset<T>();
			for (T datapoint : dataset) {
				if (datapoint.getMultiGroup() == currentGroup) {
					datapoint.setValue(1.0);
					datapoint.setGroup(1);
				} else {
					datapoint.setValue(-1.0);
					datapoint.setGroup(-1);
				}
				learnDataset.addDatapoint(datapoint);
			}
			baseClassifier.learn(learnDataset);
			classifiers.put(currentGroup, baseClassifier.clone());
		}
		for (T datapoint : dataset)
			datapoint.setGroup(datapoint.getMultiGroup());
	}

	public void predict(Dataset<T> dataset) throws Exception {
		for (T datapoint : dataset) 
			predict(datapoint);
	}

	public double predict(T datapoint) throws Exception {
		int index = 0;
		MultigroupPrediction[] multigroupPredictions = new MultigroupPrediction[numGroups];
		MultigroupPrediction maxPrediction = new MultigroupPrediction();
		for (Entry<Integer, ClassifierInterface<T>> groupClassifier : classifiers.entrySet()){
			MultigroupPrediction multigroupPrediction = new MultigroupPrediction(groupClassifier.getKey(), groupClassifier.getValue().predict(datapoint));
			multigroupPredictions[index++] = multigroupPrediction;
			if (multigroupPrediction.prediction > maxPrediction.prediction)
				maxPrediction = multigroupPrediction;
		}
		datapoint.setPredictedValue(maxPrediction.prediction);
		datapoint.setPredictedGroup(maxPrediction.group);
		datapoint.setMultigroupPredictions(multigroupPredictions);
		return datapoint.getValue();
	}

	public ClassifierInterface<T> getClassifierOfGroup(int group) {
		return classifiers.get(group);
	}

	public ClassifierInterface<T> clone() {
		return null;
	}

	@Override
	public void setGrouper(GrouperInterface grouper) {
		baseClassifier.setGrouper(grouper);
	}

	@Override
	public GrouperInterface getGrouper() {
		return baseClassifier.getGrouper();
	}

	public Map<Integer, ClassifierInterface<T>> getClassifiers() {
		return classifiers;
	}

}
