package dempred.metaclassifier;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dempred.classifier.ClassifierInterface;
import dempred.classifier.ClassifierTools;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetManipulator;
import dempred.datastructure.DatasetNormalizer;
import dempred.featuregeneration.FeatureGeneratorInterface;
import dempred.grouper.GrouperInterface;
import dempred.math.DenseVector;
import dempred.resampling.ResamplingErrorInterface;
import dempred.util.Lib;

public class ConsensusClassifier<T extends Datapoint> implements ClassifierInterface<T>, Serializable {
	private static final long serialVersionUID = 5290272433158523549L;
	private static final Logger logger = Logger.getLogger(ConsensusClassifier.class.getName());
	private ArrayList<ClassifierInterface<T>> classifiers;
	private ArrayList<FeatureGeneratorInterface<T>> featureGenerators;
	private ArrayList<DatasetNormalizer> normalizers;
	private int[][] featureSets;
	private ClassifierInterface<Datapoint> superClassifier;
	private int numFolds;
	private int numRounds;
	private String optimizationField;
	private double[] optimizationValues;
	private ResamplingErrorInterface<Datapoint> resampler;
	private boolean optimize;

	public ConsensusClassifier(int numClassifiers) {
		this.classifiers = new ArrayList<ClassifierInterface<T>>(numClassifiers);
		this.featureGenerators = new ArrayList<FeatureGeneratorInterface<T>>(numClassifiers);
		this.normalizers = new ArrayList<DatasetNormalizer>(numClassifiers);
	}

	public void addClassifier(ClassifierInterface<T> classifier, FeatureGeneratorInterface<T> featureGenerator) {
		classifiers.add(classifier);
		featureGenerators.add(featureGenerator);
	}

	public void learn(Dataset<T> dataset) throws Exception {
		Dataset<Datapoint> superDataset = new Dataset<Datapoint>();
		for (T datapoint : dataset.getDatapoints()) {
			Datapoint superDatapoint = datapoint.clone();
			superDatapoint.setFeatureVector(new DenseVector(classifiers.size()));
			superDataset.addDatapoint(superDatapoint);
		}
		List<String> featureNames = new ArrayList<String>(classifiers.size());
		for (int i = 0; i < classifiers.size(); ++i) {
			featureNames.add("Classifier_" + i);
			generateFeatures(dataset, i);
			normalizeTrainset(dataset, i);
			logger.fine("predicting consensus crossvall");
			ClassifierTools.predictCrossval(classifiers.get(i), dataset, numFolds, numRounds);
			for (int j = 0; j < dataset.size(); ++j)
				superDataset.getDatapoint(j).setFeatureAt(i, dataset.getDatapoint(j).getPredictedValue());
			logger.fine("learning consensus whole dataset");
			classifiers.get(i).learn(dataset);
		}
		superDataset.setFeatureNames(featureNames);
		// DatasetManipulator.squareFeatures(superDataset);
		normalizeTrainset(superDataset, classifiers.size());
		if (optimize) {
			Logger.getLogger(ClassifierTools.class.getName()).setParent(logger);
			ClassifierTools.optimizeParameter(superClassifier, superDataset, null, resampler, Lib.getDeclaredField(superClassifier, optimizationField), optimizationValues);
		}
		superClassifier.learn(superDataset);
	}

	public void predict(Dataset<T> dataset) throws Exception {
		Dataset<Datapoint> superDataset = new Dataset<Datapoint>();
		for (T datapoint : dataset.getDatapoints()) {
			Datapoint superDatapoint = datapoint.clone();
			superDatapoint.setFeatureVector(new DenseVector(classifiers.size()));
			superDataset.addDatapoint(superDatapoint);
		}
		List<String> featureNames = new ArrayList<String>(classifiers.size());
		for (int i = 0; i < classifiers.size(); ++i) {
			featureNames.add("Classifier_" + i);
			generateFeatures(dataset, i);
			normalizeTestset(dataset, i);
			classifiers.get(i).predict(dataset);
			for (int j = 0; j < dataset.size(); ++j)
				superDataset.getDatapoint(j).setFeatureAt(i, dataset.getDatapoint(j).getPredictedValue());
		}
		superDataset.setFeatureNames(featureNames);
		normalizeTestset(superDataset, classifiers.size());
		superClassifier.predict(superDataset);
		for (int j = 0; j < dataset.size(); ++j) {
			Datapoint datapoint = dataset.getDatapoint(j);
			Datapoint superDatapoint = superDataset.getDatapoint(j);
			datapoint.setPredictedGroup(superDatapoint.getPredictedGroup());
			datapoint.setPredictedValue(superDatapoint.getPredictedValue());
		}
	}

	public double predict(T datapoint) throws Exception {
		Datapoint superDatapoint = new Datapoint();
		superDatapoint = datapoint.clone();
		superDatapoint.setFeatureVector(new DenseVector(classifiers.size()));
		for (int i = 0; i < classifiers.size(); ++i) {
			generateFeatures(datapoint, i);
			normalizeTestpoint(datapoint, i);
			classifiers.get(i).predict(datapoint);
			superDatapoint.setFeatureAt(i, datapoint.getPredictedValue());
		}
		normalizeTestpoint(superDatapoint, classifiers.size());
		superClassifier.predict(superDatapoint);
		datapoint.setPredictedGroup(superDatapoint.getPredictedGroup());
		datapoint.setPredictedValue(superDatapoint.getPredictedValue());
		return superDatapoint.getPredictedValue(); 
	}

	private void generateFeatures(Dataset<T> dataset, int index) throws Exception {
		DatasetManipulator.generateFeatures(dataset, featureGenerators.get(index));
		if (featureSets != null && featureSets[index] != null)
			dataset.keepFeatures(featureSets[index]);
		logger.fine("Finished generating features:" + dataset.numFeatures());
	}

	private void normalizeTrainset(Dataset<?> dataset, int index) {
		DatasetNormalizer normalizer = new DatasetNormalizer(dataset);
		normalizer.normalize(dataset);
		normalizers.add(index, normalizer);
		logger.fine(String.format("%d feature(s) deleted with standart deviation of zero!", normalizer.getZeroIndices().length));
	}
	
	private void generateFeatures(T datapoint, int index) throws Exception {
		DatasetManipulator.generateFeatures(datapoint, featureGenerators.get(index));
		if (featureSets != null && featureSets[index] != null)
			datapoint.getFeatureVector().keep(featureSets[index]);
		logger.fine("Finished generating features:" + datapoint.getNumFeatures());
	}

	private void normalizeTestset(Dataset<?> dataset, int index) {
		DatasetNormalizer normalizer = normalizers.get(index);
		normalizer.normalize(dataset);
		logger.fine(String.format("%d feature(s) deleted with standart deviation of zero!", normalizer.getZeroIndices().length));
	}
	
	private void normalizeTestpoint(Datapoint datapoint, int index) {
		DatasetNormalizer normalizer = normalizers.get(index);
		normalizer.normalize(datapoint);
		logger.fine(String.format("%d feature(s) deleted with standart deviation of zero!", normalizer.getZeroIndices().length));
	}

	public double optimizeParameter(int index, Dataset<T> trainset, Dataset<T> testset, ResamplingErrorInterface<T> errorInterface, Field field, double[] parameterList) throws Exception {
		DatasetManipulator.generateFeatures(trainset, featureGenerators.get(index));
		DatasetManipulator.generateFeatures(testset, featureGenerators.get(index));
		DatasetNormalizer normalizer = new DatasetNormalizer(trainset);
		normalizer.normalize(trainset);
		normalizer.normalize(testset);
		return ClassifierTools.optimizeParameter(classifiers.get(index), trainset, testset, errorInterface, field, parameterList);
	}

	public double getError(int index, Dataset<T> trainset, Dataset<T> testset, ResamplingErrorInterface<T> errorInterface) throws Exception {
		generateFeatures(trainset, index);
		generateFeatures(testset, index);
		DatasetNormalizer normalizer = new DatasetNormalizer(trainset);
		normalizer.normalize(trainset);
		normalizer.normalize(testset);
		return errorInterface.error(classifiers.get(index), trainset);
	}

	public void setFeatureSet(int index, int[] featureSet) {
		this.featureSets[index] = featureSet;
	}

	// public void setEpsilon(int index, Dataset<T> dataset, double[] lambdas) {
	// generateFeatures(index, dataset);
	// DatasetNormalizer normalizer = new DatasetNormalizer(dataset);
	// normalizer.normalize(dataset);
	// if (classifiers.get(index) instanceof RidgeRegressionPrimal)
	// ((RidgeRegressionPrimal) classifiers[index]).setEpsilon(ApplicationTools.fillEpsilon(dataset.getFeatureIndex(), featureGenerators[index].numFeatures(), lambdas));
	// else
	// ((LinearWrapperClassifier) classifiers[index]).setEpsilon(ApplicationTools.fillEpsilon(dataset.getFeatureIndex(), featureGenerators[index].numFeatures(), lambdas));
	// }

	public ClassifierInterface<T> clone() {
		return null;
	}

	// ***************************************************************
	// getters and setters

	public final GrouperInterface getGrouper() {
		return superClassifier.getGrouper();
	}

	public final void setGrouper(GrouperInterface grouper) {
		superClassifier.setGrouper(grouper);
	}

	public final ClassifierInterface<Datapoint> getSuperClassifier() {
		return superClassifier;
	}

	public final void setSuperClassifier(ClassifierInterface<Datapoint> superClassifier) {
		this.superClassifier = superClassifier;
	}

	public final int getNumFolds() {
		return numFolds;
	}

	public final void setNumFolds(int numFolds) {
		this.numFolds = numFolds;
	}

	public final int getNumRounds() {
		return numRounds;
	}

	public final void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public final String getOptimizationField() {
		return optimizationField;
	}

	public final void setOptimizationField(String optimizationField) {
		this.optimizationField = optimizationField;
	}

	public final double[] getOptimizationValues() {
		return optimizationValues;
	}

	public final void setOptimizationValues(double[] optimizationValues) {
		this.optimizationValues = optimizationValues;
	}

	public final ResamplingErrorInterface<Datapoint> getResampler() {
		return resampler;
	}

	public final void setResampler(ResamplingErrorInterface<Datapoint> resampler) {
		this.resampler = resampler;
	}

	public final boolean isOptimize() {
		return optimize;
	}

	public final void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

}
