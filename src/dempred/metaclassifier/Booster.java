package dempred.metaclassifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import dempred.chart.ChartTools;
import dempred.chart.CorrelationPlot;
import dempred.classifier.ClassifierInterface;
import dempred.classifier.ClassifierTools;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetManipulator;
import dempred.grouper.GrouperInterface;
import dempred.math.VectorInterface;
import dempred.resampling.ResamplingErrorInterface;
import dempred.util.Lib;

public class Booster<T extends Datapoint> implements ClassifierInterface<T> {
	private static final Logger logger = Logger.getLogger(Booster.class.getName());
	private TreeMap<Double, ClassifierInterface<T>> classifierMap;
	private ClassifierInterface<T> baseClassifier;
	private int numBasePoints;
	private String optimizationField;
	private double[] optimizationValues;
	private ResamplingErrorInterface<T> resampler;
	private double gaussWidth;
	private VectorInterface[] baseWeights;
	private double[] basePoints;

	public Booster(ClassifierInterface<T> classifier, int numBasePoints, double gaussWidth) {
		this.baseClassifier = classifier;
		this.numBasePoints = numBasePoints;
		this.gaussWidth = gaussWidth;
	}

	public void learn(Dataset<T> dataset) throws Exception {
		Collections.sort(dataset.getDatapoints(), new Datapoint.ValueComparatorAsc());
		VectorInterface valueVector = DatasetManipulator.getValueVector(dataset);
		VectorInterface weightVector = DatasetManipulator.getWeightVector(dataset);
		Arrays.sort(valueVector.getElements());
		double min = valueVector.get(0);
		int pointsPerIntervall = (int) Math.ceil((double) dataset.size() / (numBasePoints - 1));
		logger.fine(String.format("datapoints:%d pointsPerIntervall:%d in %d intervals", dataset.size(), pointsPerIntervall, numBasePoints));
		int numInFold = 0;
		double gaussMean = min;
		double gaussStd = 1.0;
		classifierMap = new TreeMap<Double, ClassifierInterface<T>>();
		baseWeights = new VectorInterface[numBasePoints];
		basePoints = new double[numBasePoints];
		int numLearnedClassifiers = 0;
		for (int i = 0; i < valueVector.size(); ++i) {
			++numInFold;
			if (numInFold > pointsPerIntervall || i == valueVector.size() - 1) {
				numInFold = 1;
				double value = valueVector.get(i);
				gaussStd = (value - gaussMean) / gaussWidth;
				reweightDataset(dataset, gaussMean, gaussStd);
				logger.fine(String.format("Learning classifier %d of %d", numLearnedClassifiers + 1, numBasePoints));
				logger.fine(String.format("Reweighting dataset with mean:%f and std:%.4f", gaussMean, gaussStd));
				if (optimizationField != null && optimizationValues != null) {
					Logger.getLogger(ClassifierTools.class.getName()).setParent(logger);
					ClassifierTools.optimizeParameter(baseClassifier, dataset, null, resampler, Lib.getDeclaredField(baseClassifier, optimizationField), optimizationValues);
				}
				baseClassifier.learn(dataset);
				classifierMap.put(gaussMean, baseClassifier.clone());

				baseWeights[numLearnedClassifiers] = DatasetManipulator.getWeightVector(dataset);
				basePoints[numLearnedClassifiers] = gaussMean;

				gaussMean = value;
				++numLearnedClassifiers;
			}
		}
		reweightDataset(dataset, gaussMean, gaussStd);
		logger.fine(String.format("Learning classifier %d of %d", numLearnedClassifiers + 1, numBasePoints));
		logger.fine(String.format("Reweighting dataset with mean:%.4f and std:%.4f", gaussMean, gaussStd));
		baseClassifier.learn(dataset);
		classifierMap.put(gaussMean, baseClassifier.clone());

		baseWeights[numLearnedClassifiers] = DatasetManipulator.getWeightVector(dataset);
		basePoints[numLearnedClassifiers] = gaussMean;

		logger.fine("Learning base classifier");
		for (int i = 0; i < weightVector.size(); ++i)
			dataset.getDatapoint(i).setWeight(weightVector.get(i));
		baseClassifier.learn(dataset);
	}

	public void predict(Dataset<T> dataset) throws Exception {
		baseClassifier.predict(dataset);

		// FVector[] fingerPrintsTrain = new FVector[trainset.size()];
		// FVector[] fingerPrintsPredict = new FVector[dataset.size()];
		// for (int i = 0; i < trainset.size(); ++i)
		// fingerPrintsTrain[i] = getFingerprint(trainset.getDatapoint(i));
		// // fingerPrintsTrain[i] = trainset.getDatapoint(i).getFeatureVector();
		// for (int i = 0; i < dataset.size(); ++i)
		// fingerPrintsPredict[i] = getFingerprint((SDFDatapoint) dataset.getDatapoint(i));
		// // fingerPrintsPredict[i] = dataset.getDatapoint(i).getFeatureVector();
		//
		// int numPredicted = 0;

		for (T datapoint : dataset.getDatapoints()) {
			// logger.fine(String.format("Predicting datapoint %d of %d", numPredicted + 1, dataset.size()));
			// double bestSimilarity = Double.NEGATIVE_INFINITY;
			// double point = 0.0;
			// for (int i = 0; i < baseWeights.length; ++i) {
			// System.out.println(baseWeights[i]);
			// double similarity = 0.0;
			// for (int j = 0; j < trainset.size(); ++j)
			// similarity += baseWeights[i].get(j) * FVectorMetric.pcc(fingerPrintsTrain[j], fingerPrintsPredict[numPredicted]);
			// if (similarity > bestSimilarity) {
			// bestSimilarity = similarity;
			// point = basePoints[i];
			// }
			// }

			closest(classifierMap, datapoint.getValue()).getValue().predict(datapoint);
			// ++numPredicted;
			// closest(classifierMap, point).predict(datapoint);
		}
	}

	public void plots(Dataset<T> dataset, String filepath) throws Exception {
		int i = 0;
		for (Entry<Double, ClassifierInterface<T>> entry : classifierMap.entrySet()) {
			++i;
			entry.getValue().predict(dataset);
			CorrelationPlot plot = new CorrelationPlot("Averaged", dataset);
			ChartTools.saveChartAsJPG(filepath + i + "_" + entry.getKey(), plot.generateChart(), 600, 600);
		}
	}

	public double predict(T datapoint) throws Exception {
		baseClassifier.predict(datapoint);
		return closest(classifierMap, datapoint.getPredictedValue()).getValue().predict(datapoint);
	}

	private void reweightDataset(Dataset<T> dataset, double mean, double std) {
		for (Datapoint datapoint : dataset.getDatapoints())
			datapoint.setWeight(gaussian(datapoint.getValue(), mean, std));
	}

	private double gaussian(double x, double mean, double std) {
		return (1.0 / (std * Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * Math.pow((x - mean) / std, 2)));
	}

	public VectorInterface getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public static <A extends Number, T> Entry<A, T> closest(NavigableMap<A, T> map, A key) {
		Entry<A, T> floor = map.floorEntry(key);
		Entry<A, T> ceil = map.ceilingEntry(key);
		if (floor == null)
			return ceil;
		if (ceil == null)
			return floor;
		if (Math.abs(floor.getKey().doubleValue() - key.doubleValue()) < Math.abs(ceil.getKey().doubleValue() - key.doubleValue()))
			return floor;
		else
			return ceil;
	}

	@SuppressWarnings("unchecked")
	public ClassifierInterface<T> clone() {
		try {
			Booster<T> cl = (Booster<T>) super.clone();
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	// ******************************************************************
	// getter and setter
	public final GrouperInterface getGrouper() {
		return baseClassifier.getGrouper();
	}

	public final void setGrouper(GrouperInterface grouper) {
		baseClassifier.setGrouper(grouper);
	}

	public final String getOptimizationParameter() {
		return optimizationField;
	}

	public final void setOptimizationParameter(String optimizationParameter) {
		this.optimizationField = optimizationParameter;
	}

	public final double[] getOptimizationValues() {
		return optimizationValues;
	}

	public final void setOptimizationValues(double[] optimizationValues) {
		this.optimizationValues = optimizationValues;
	}

	public final ResamplingErrorInterface<T> getResampler() {
		return resampler;
	}

	public final void setResampler(ResamplingErrorInterface<T> resampler) {
		this.resampler = resampler;
	}

	public final double getGaussWidth() {
		return gaussWidth;
	}

	public final void setGaussWidth(double gaussWidth) {
		this.gaussWidth = gaussWidth;
	}
}
