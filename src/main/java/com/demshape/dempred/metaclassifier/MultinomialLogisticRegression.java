package com.demshape.dempred.metaclassifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.MultigroupDatapoint;
import com.demshape.dempred.datastructure.MultigroupPrediction;
import com.demshape.dempred.grouper.GrouperInterface;
import com.demshape.dempred.math.DenseVector;



public class MultinomialLogisticRegression<T extends MultigroupDatapoint> implements ClassifierInterface<T>, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MultinomialLogisticRegression.class.getName());
	ClassifierInterface<T> baseClassifier;
	private ArrayList<ClassifierInterface<T>> classifiers;
	private Map<Integer, Integer> groupCounter;
	private int[] groups;
	private int numGroups;
	private int pivotGroup;

	public MultinomialLogisticRegression(ClassifierInterface<T> classifier, Dataset<T> dataset) {
		this.baseClassifier = classifier;
		this.groupCounter = dataset.getGroupCounter();
		this.numGroups = groupCounter.size();
		this.classifiers = new ArrayList<ClassifierInterface<T>>(numGroups - 1);
		this.groups = new int[numGroups];
	}

	public int initPivot() {
		int max = 0;
		for (Entry<Integer, Integer> group : groupCounter.entrySet()) {
			if (group.getValue() > max) {
				max = group.getValue();
				this.pivotGroup = group.getKey();
			}
		}
		return pivotGroup;
	}

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#learn(dempred.datastructure.Dataset)
	 * learns classifiers for all groups except the pivot group. By convention the pivot group is set as the last entry in the groups array
	 */
	public void learn(Dataset<T> dataset) throws Exception {
		int index = 0;
		for (Entry<Integer, Integer> group : groupCounter.entrySet()) {
			int currentGroup = group.getKey();
			if (currentGroup != pivotGroup) {
				groups[index++] = currentGroup;
				logger.fine(String.format("learning group %d of %d", index, numGroups - 1));
				Dataset<T> learnDataset = new Dataset<T>();
				for (T datapoint : dataset) {
					if (datapoint.getMultiGroup() == currentGroup) {
						datapoint.setValue(1.0);
						datapoint.setGroup(1);
						learnDataset.addDatapoint(datapoint);
					} // else if (datapoint.getMultiGroup() == pivotGroup) {
					else {	
						datapoint.setValue(-1.0);
						datapoint.setGroup(-1);
						learnDataset.addDatapoint(datapoint);
					}
				}
				baseClassifier.learn(learnDataset);
				classifiers.add(baseClassifier.clone());
			}
		}
		groups[numGroups - 1] = pivotGroup;
		for (T datapoint : dataset)
			datapoint.setGroup(datapoint.getMultiGroup());
	}

	public void predict(Dataset<T> dataset) throws Exception {
		for (T datapoint : dataset) {
			MultigroupPrediction[] multigroupPredictions = new MultigroupPrediction[numGroups];
			MultigroupPrediction maxPrediction = new MultigroupPrediction();
			double sumPropabilities = 0.0;
			double prediction;
			for (int i = 0; i < numGroups - 1; ++i){
				prediction = Math.exp(classifiers.get(i).predict(datapoint));
				multigroupPredictions[i] = new MultigroupPrediction(groups[i], prediction);
				sumPropabilities += prediction;
			}
			// compute propability of pivot group
			multigroupPredictions[numGroups - 1] = new MultigroupPrediction(pivotGroup, 1.0 / (1.0 + sumPropabilities));
			
			// compute propability of other groups group
			for (MultigroupPrediction multigroupPrediction: multigroupPredictions)
				multigroupPrediction.prediction = multigroupPrediction.prediction / (1.0 + sumPropabilities);

			for(MultigroupPrediction multigroupPrediction: multigroupPredictions){
				if(multigroupPrediction.prediction > maxPrediction.prediction)
					maxPrediction = multigroupPrediction;
			}
			datapoint.setPredictedValue(maxPrediction.prediction);
			datapoint.setPredictedGroup(maxPrediction.group);
			datapoint.setMultigroupPredictions(multigroupPredictions);
		}
	}

	public double predict(T datapoint) throws Exception {
		DenseVector predictions;
		predictions = new DenseVector(numGroups);
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

	public ArrayList<ClassifierInterface<T>> getClassifiers() {
		return classifiers;
	}

	public int getPivot() {
		return pivotGroup;
	}

	public void setPivot(int pivot) {
		this.pivotGroup = pivot;
	}

}
