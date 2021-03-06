package com.demshape.dempred.metaclassifier;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.demshape.dempred.chart.ChartTools;
import com.demshape.dempred.chart.CorrelationPlot;
import com.demshape.dempred.classifier.ClassifierInterface;
import com.demshape.dempred.classifier.ClassifierTools;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.DatasetManipulator;
import com.demshape.dempred.grouper.GrouperInterface;
import com.demshape.dempred.math.VectorInterface;
import com.demshape.dempred.resampling.ResamplingErrorInterface;
import com.demshape.dempred.util.Lib;



public class BoosterEqui<T extends Datapoint> implements ClassifierInterface<T>, Serializable {
	private static final long serialVersionUID = 538039097602946862L;
	private static final Logger logger = Logger.getLogger(BoosterEqui.class.getName());
	private ArrayList<ClassifierInterface<T>> classifiers;
	private ClassifierInterface<T> baseClassifier;
	private int numBasePoints;
	private double min;
	private double max;
	private double intervalSize;
	private String optimizationField;
	private double[] optimizationValues;
	private ResamplingErrorInterface<T> resampler;
	private double gaussWidth;
	private double[] lambdas;
	private Double lambdaBase;
	private boolean optimize;

	public BoosterEqui(ClassifierInterface<T> classifier, int numBasePoints, double gaussWidth) {
		this.baseClassifier = classifier;
		this.numBasePoints = numBasePoints;
		this.gaussWidth = gaussWidth;

	}

	public void learn(Dataset<T> dataset) throws Exception {
		VectorInterface valueVector = DatasetManipulator.getValueVector(dataset);
		VectorInterface weightVector = DatasetManipulator.getWeightVector(dataset);
		min = valueVector.min();
		max = valueVector.max();
		intervalSize = (max - min) / (numBasePoints - 1);
		logger.fine(String.format("min: %.4f max:%.4f intervalSize:%.4f", min, max, intervalSize));
		double std = intervalSize / gaussWidth;
		classifiers = new ArrayList<ClassifierInterface<T>>(numBasePoints);
		Field field = Lib.getDeclaredField(baseClassifier, optimizationField);
		field.setAccessible(true);
		for (int i = 0; i < numBasePoints; ++i) {
			logger.fine(String.format("Learning classifier %d of %d", (i + 1), numBasePoints));
			reweightDataset(dataset, min + i * intervalSize, std);
			logger.fine(String.format("Reweighting dataset with mean:%.4f and std:%.4f", min + i * intervalSize, std));
			if (optimize) {
				if(lambdas == null)
					lambdas = new double[numBasePoints];
				Logger.getLogger(ClassifierTools.class.getName()).setParent(logger);
				ClassifierTools.optimizeParameter(baseClassifier, dataset, null, resampler, field, optimizationValues);
				lambdas[i] = field.getDouble(baseClassifier);
			}
			if (lambdas != null) 
				field.set(baseClassifier, lambdas[i]);
			baseClassifier.learn(dataset);
			classifiers.add(baseClassifier.clone());
		}
		logger.fine("Learning base classifier");
		for (int i = 0; i < weightVector.size(); ++i)
			dataset.getDatapoint(i).setWeight(weightVector.get(i));
		if (optimize) {
			Logger.getLogger(ClassifierTools.class.getName()).setParent(logger);	
			ClassifierTools.optimizeParameter(baseClassifier, dataset, null, resampler, field, optimizationValues);
			lambdaBase = field.getDouble(baseClassifier);
		}
		if (lambdaBase != null) 
			field.set(baseClassifier, lambdaBase);
		baseClassifier.learn(dataset);
	}

	public void predict(Dataset<T> dataset) throws Exception {
		baseClassifier.predict(dataset);
		for (T datapoint : dataset.getDatapoints()) {
			int index = (int) Math.round(((datapoint.getPredictedValue() - min) / intervalSize));
			if (index >= classifiers.size())
				index = classifiers.size() - 1;
			if (index < 0)
				index = 0;
			classifiers.get(index).predict(datapoint);
		}
	}

	public void plots(Dataset<T> dataset, String filepath) throws Exception {
		for (int i = 0; i < classifiers.size(); ++i) {
			classifiers.get(i).predict(dataset);
			CorrelationPlot plot = new CorrelationPlot("Averaged", dataset);
			ChartTools.saveChartAsJPG(filepath + i + "_", plot.generateChart(), 600, 600);
		}
	}

	public double predict(T datapoint) throws Exception {
		baseClassifier.predict(datapoint);
		int index = (int) Math.round(((datapoint.getPredictedValue() - min) / intervalSize));
		return classifiers.get(index).predict(datapoint);
	}

	private void reweightDataset(Dataset<?> dataset, double mean, double std) {
		for (Datapoint datapoint : dataset.getDatapoints())
			datapoint.setWeight(gaussian(datapoint.getValue(), mean, std));
	}

	private double gaussian(double x, double mean, double std) {
		return (1.0 / (std * Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * Math.pow((x - mean) / std, 2)));
	}

	public ClassifierInterface<T> clone() {
		try {
			@SuppressWarnings("unchecked")
			BoosterEqui<T> cl = (BoosterEqui<T>) super.clone();
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	// getters and setters
	// ****************************************************

	public final GrouperInterface getGrouper() {
		return baseClassifier.getGrouper();
	}

	public final void setGrouper(GrouperInterface grouper) {
		baseClassifier.setGrouper(grouper);
	}

	public final String getOptimizationField() {
		return optimizationField;
	}

	public final void setOptimizationField(String optimizationParameter) {
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

	public final double getLambdaBase() {
		return lambdaBase;
	}

	public final void setLambdaBase(double lambdaBase) {
		this.lambdaBase = lambdaBase;
	}

	public final double[] getLambdas() {
		return lambdas;
	}

	public final void setLambdas(double[] lambdas) {
		this.lambdas = lambdas;
	}

	public final boolean isOptimize() {
		return optimize;
	}

	public final void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}
	
	

}
