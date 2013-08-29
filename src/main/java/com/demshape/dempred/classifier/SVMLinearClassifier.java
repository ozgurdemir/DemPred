package com.demshape.dempred.classifier;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.grouper.GrouperInterface;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class SVMLinearClassifier<T extends Datapoint> implements ClassifierInterface<T> {
	private Model svmModel;
	private Parameter svmParameter;
	private int mode;
	private int solverType;
	private double c;
	private double eps;
	private GrouperInterface grouper;

	public SVMLinearClassifier() {

	}

	

	@Override
	public void learn(Dataset<T> dataset) throws IllegalArgumentException {
		svmParameter = new Parameter(SolverType.L2R_LR, c, eps);
		Problem svmProblem = new Problem();
		FeatureNode[][] nodes = new FeatureNode[dataset.size()][];
		double[] y = new double[dataset.size()];
		for (int i = 0; i < dataset.size(); ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			// if (mode == 0)
			y[i] = datapoint.getGroup();
			// else
			// y[i] = datapoint.getValue();
			nodes[i] = new FeatureNode[datapoint.getNumFeatures()];
			for (int j = 0; j < datapoint.getNumFeatures(); ++j)
				nodes[i][j] = new FeatureNode(j + 1, datapoint.getFeatureAt(j));
		}
		svmProblem.n = dataset.numFeatures();
		svmProblem.bias = -1;
		svmProblem.l = dataset.size();
		svmProblem.y = y;
		svmProblem.x = nodes;
		svmModel = Linear.train(svmProblem, svmParameter);
	}

	@Override
	public void predict(Dataset<T> dataset) throws IllegalArgumentException {
		FeatureNode[][] nodes = new FeatureNode[dataset.size()][];
		for (int i = 0; i < dataset.size(); ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			nodes[i] = new FeatureNode[datapoint.getNumFeatures()];
			for (int j = 0; j < datapoint.getNumFeatures(); ++j)
				nodes[i][j] = new FeatureNode(j + 1, datapoint.getFeatureAt(j));
		}
		for (int i = 0; i < nodes.length; ++i) {
			dataset.getDatapoint(i).setPredictedValue((double) Linear.predict(svmModel, nodes[i]));
			dataset.getDatapoint(i).setPredictedGroup(grouper.getGroup(dataset.getDatapoint(i).getPredictedValue()));
		}
	}

	public double predict(Datapoint datapoint) throws IllegalArgumentException {
		FeatureNode[] nodes = new FeatureNode[datapoint.getNumFeatures() + 1];
		for (int j = 0; j < datapoint.getNumFeatures(); ++j) {
			nodes[j] = new FeatureNode(j + 1, datapoint.getFeatureAt(j));
		}
		double value = (double) Linear.predict(svmModel, nodes);
		datapoint.setPredictedValue(value);
		datapoint.setPredictedGroup(grouper.getGroup(value));
		return value;
	}

	@SuppressWarnings("unchecked")
	public ClassifierInterface<T> clone() {
		try {
			SVMLinearClassifier<T> cl = (SVMLinearClassifier) super.clone();
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	public Model getSvmModel() {
		return svmModel;
	}

	public void setSvmModel(Model svmModel) {
		this.svmModel = svmModel;
	}

	public Parameter getSvmParameter() {
		return svmParameter;
	}

	public void setSvmParameter(Parameter svmParameter) {
		this.svmParameter = svmParameter;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getSolverType() {
		return solverType;
	}

	public void setSolverType(int solverType) {
		this.solverType = solverType;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public final GrouperInterface getGrouper() {
		return grouper;
	}

	public final void setGrouper(GrouperInterface grouper) {
		this.grouper = grouper;
	}

}
