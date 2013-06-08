package dempred.metaclassifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import dempred.classifier.ClassifierInterface;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetManipulator;
import dempred.grouper.GrouperInterface;
import dempred.math.SimpleVector;
import dempred.math.VectorInterface;

public class MultiClass<T extends Datapoint> implements ClassifierInterface<T>, Serializable {

	private static final long serialVersionUID = -7882351684133280142L;
	private static final Logger logger = Logger.getLogger(MultiClass.class.getName());
	ClassifierInterface<T> baseClassifier;
	private ArrayList<ClassifierInterface<T>> classifiers;
	private Map<Integer, Integer> groupCounter;
	private int[] groups;
	private int numGroups;

	public MultiClass(ClassifierInterface<T> classifier, Dataset<T> dataset) {
		this.baseClassifier = classifier;
		this.groupCounter = dataset.getGroupCounter();
		this.numGroups = groupCounter.size();
		this.classifiers = new ArrayList<ClassifierInterface<T>>(numGroups);
		this.groups = new int[numGroups];
	}

	public void learn(Dataset<T> dataset) throws Exception {
		int index = 0;
		for (Entry<Integer, Integer> group : groupCounter.entrySet()) {
			logger.fine(String.format("learning group %d of %d", index + 1, numGroups));
			int currentGroup = group.getKey();
			groups[index] = currentGroup;
			for (T datapoint : dataset) {
				if (datapoint.getMultiGroup() == currentGroup) {
					datapoint.setValue(1.0);
					datapoint.setGroup(1);
				} else {
					datapoint.setValue(-1.0);
					datapoint.setGroup(-1);
				}
			}
			baseClassifier.learn(dataset);
			classifiers.add(baseClassifier.clone());
			++index;
		}
		for (T datapoint : dataset)
			datapoint.setGroup(datapoint.getMultiGroup());
	}

	public void predict(Dataset<T> dataset) throws Exception {
		SimpleVector predictions;
		for (T datapoint : dataset) {
			predictions = new SimpleVector(numGroups);
			for (int i = 0; i < numGroups; ++i)
				predictions.set(i, classifiers.get(i).predict(datapoint));
			int maxIndex = predictions.maxIndex(1)[0];
			datapoint.setPredictedValue(predictions.get(maxIndex));
			datapoint.setPredictedGroup(groups[maxIndex]);
		}
	}

	public double predict(T datapoint) throws Exception {
		SimpleVector predictions;
		predictions = new SimpleVector(numGroups);
		for (int i = 0; i < numGroups; ++i)
			predictions.set(i, classifiers.get(i).predict(datapoint));
		int maxIndex = predictions.maxIndex(1)[0];
		datapoint.setPredictedValue(predictions.get(maxIndex));
		datapoint.setPredictedGroup(groups[maxIndex]);
		return datapoint.getValue();
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

}
