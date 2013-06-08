package dempred.resampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;

public class CrossValidation<T extends Datapoint> {
	private Dataset<T> dataset;
	private int[][] foldIndex;
	private ArrayList<Integer> datasetIndex;

	public CrossValidation(Dataset<T> dataset) {
		this.dataset = dataset;
		setMaxFoldNumber();
	}

	public CrossValidation(Dataset<T> dataset, int numFolds) {
		this.dataset = dataset;
		setMaxFoldNumber();
		generateFolds(numFolds);
	}

	private void setMaxFoldNumber() {
		// Collection<Integer> groupCounts = dataset.getGroupCounter().values();
	}

	public void generateFolds(int numFolds) throws IllegalArgumentException {
		if (numFolds > dataset.size() || numFolds < 2)
			throw new IllegalArgumentException("The number of crossfolds can not be larger than the number of elements in the Dataset or smaller than 2: You set:" + numFolds);
		this.foldIndex = new int[numFolds][];
		datasetIndex = new ArrayList<Integer>(dataset.size());
		for (int i = 0; i < dataset.size(); ++i)
			datasetIndex.add(i);
		Collections.shuffle(datasetIndex);
		int modulo = dataset.size() % numFolds;
		int numDataInFoldCeil = (int) Math.ceil(((double) this.dataset.size()) / numFolds);
		int numDataInFoldFloor = (int) Math.floor(((double) this.dataset.size()) / numFolds);
		Iterator<Integer> rIterator = datasetIndex.iterator();
		for (int fold = 0; fold < numFolds; ++fold) {
			int numDataInFold;
			if (fold < modulo)
				numDataInFold = numDataInFoldCeil;
			else
				numDataInFold = numDataInFoldFloor;
			foldIndex[fold] = new int[numDataInFold];
			for (int j = 0; j < numDataInFold; ++j)
				foldIndex[fold][j] = rIterator.next();
		}
	}

	// erstes fold ist 0 !!!
	public Dataset<T> getFold(int foldNumber) throws IllegalArgumentException {
		Dataset<T> reduceddataset = new Dataset<T>();
		reduceddataset.setName(this.dataset.getName() + " (Crossvalidation Subset " + foldNumber + "!)");
		reduceddataset.setComment("K fold CrossValidation. Fold: " + foldNumber);
		for (int index : foldIndex[foldNumber]) {
			reduceddataset.addDatapoint(this.dataset.getDatapoint(index));
		}
		reduceddataset.setFeatureNames(dataset.getFeatureNames());
		return reduceddataset;
	}

	public Dataset<T> getFoldsExcept(int foldNumber) throws IllegalArgumentException {
		Dataset<T> reduceddataset = new Dataset<T>();
		reduceddataset.setName(this.dataset.getName() + " (Crossvalidation Subset without fold " + foldNumber + "!)");
		reduceddataset.setComment("K fold CrossValidation. Without fold: " + foldNumber);
		for (int actualFold = 0; actualFold < this.foldIndex.length; ++actualFold) {
			if (actualFold == foldNumber)
				continue;
			for (int index : foldIndex[actualFold])
				reduceddataset.addDatapoint(this.dataset.getDatapoint(index));
		}
		reduceddataset.setFeatureNames(dataset.getFeatureNames());
		return reduceddataset;
	}

	public void averagePrediction(Dataset<?> averagedDataset, Dataset<?> predictedDataset, int crossRounds) {
		double value = 0.0;
		for (int i = 0; i < predictedDataset.size(); ++i) {
			averagedDataset.getDatapoint(datasetIndex.get(i)).setValue(predictedDataset.getDatapoint(i).getValue());
			value = predictedDataset.getDatapoint(i).getPredictedValue();
			value /= crossRounds;
			Datapoint datapoint = averagedDataset.getDatapoint(datasetIndex.get(i));
			datapoint.setPredictedValue(datapoint.getPredictedValue() + value);
		}
	}

	public String foldIndexAsString() {
		String lineSeperator = System.getProperty("line.separator");
		StringBuffer strBuf = new StringBuffer(2000);
		int numDatapointsTotal = 0;
		for (int i = 0; i < foldIndex.length; ++i) {
			strBuf.append(String.format("fold: %d size: %d --> ", i, foldIndex[i].length));
			String delimiter = "";
			for (int index : foldIndex[i]) {
				strBuf.append(String.format("%s%d(%d)", delimiter, index, dataset.getDatapoint(index).getGroup()));
				delimiter = ", ";
			}
			strBuf.append("}" + lineSeperator);
			numDatapointsTotal += foldIndex[i].length;
		}
		strBuf.append(String.format("total datapoints: %d %n", numDatapointsTotal));
		return strBuf.toString();
	}

	public final int[][] getFoldIndex() {
		return foldIndex;
	}

}
