package com.demshape.dempred.classifier;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.grouper.GrouperInterface;

public class SVMClassifier<T extends Datapoint> implements ClassifierInterface<T> {

	private svm_model svmModel;
	private svm_parameter svmParameter;
	private int mode;
	private GrouperInterface grouper;

	public SVMClassifier() {
		svmParameter = new svm_parameter();
	}

	@Override
	public void learn(Dataset<T> dataset) throws IllegalArgumentException {
		svm_problem svmProblem = new svm_problem();
		svm_node[][] nodes = new svm_node[dataset.size()][];
		double[] y = new double[dataset.size()];
		for (int i = 0; i < dataset.size(); ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			if (mode == 0)
				y[i] = datapoint.getGroup();
			else
				y[i] = datapoint.getValue();
			nodes[i] = new svm_node[datapoint.getNumFeatures() + 1];
			for (int j = 0; j < datapoint.getNumFeatures(); ++j) {
				nodes[i][j] = new svm_node();
				nodes[i][j].index = j;
				nodes[i][j].value = datapoint.getFeatureAt(j);
			}
			nodes[i][datapoint.getNumFeatures()] = new svm_node();
			nodes[i][datapoint.getNumFeatures()].index = -1;
		}
		svmProblem.l = dataset.size();
		svmProblem.y = y;
		svmProblem.x = nodes;
		svmModel = svm.svm_train(svmProblem, svmParameter);
	}

	@Override
	public void predict(Dataset<T> dataset) throws IllegalArgumentException {
		svm_node[][] nodes = new svm_node[dataset.size()][];
		for (int i = 0; i < dataset.size(); ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			nodes[i] = new svm_node[datapoint.getNumFeatures() + 1];
			for (int j = 0; j < datapoint.getNumFeatures(); ++j) {
				nodes[i][j] = new svm_node();
				nodes[i][j].index = j;
				nodes[i][j].value = datapoint.getFeatureAt(j);
			}
			nodes[i][datapoint.getNumFeatures()] = new svm_node();
			nodes[i][datapoint.getNumFeatures()].index = -1;
		}
		for (int i = 0; i < nodes.length; ++i){
			dataset.getDatapoint(i).setPredictedValue(svm.svm_predict(svmModel, nodes[i]));
			if (grouper != null)
				dataset.getDatapoint(i).setPredictedGroup(grouper.getGroup(dataset.getDatapoint(i).getPredictedValue()));
		}
	}

	public double predict(Datapoint datapoint) throws IllegalArgumentException {
		svm_node[] nodes = new svm_node[datapoint.getNumFeatures() + 1];
		for (int j = 0; j < datapoint.getNumFeatures(); ++j) {
			nodes[j] = new svm_node();
			nodes[j].index = j;
			nodes[j].value = datapoint.getFeatureAt(j);
		}
		nodes[datapoint.getNumFeatures()] = new svm_node();
		nodes[datapoint.getNumFeatures()].index = -1;
		double value = svm.svm_predict(svmModel, nodes);
		datapoint.setPredictedValue(value);
		if (grouper != null)
			datapoint.setPredictedGroup(grouper.getGroup(value));
		return value;
	}

	public void setParameters() {
		svmParameter.svm_type = svm_parameter.NU_SVC;
		svmParameter.kernel_type = svm_parameter.LINEAR;
		svmParameter.gamma = 8;
		svmParameter.cache_size = 100;
		svmParameter.eps = 1e-3;
		svmParameter.nu = 0.1;
		svmParameter.p = 0.1;
		mode = 0;
	}

	@SuppressWarnings("unchecked")
	public ClassifierInterface<T> clone() {
		try {
			SVMClassifier<T> cl = (SVMClassifier) super.clone();
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	public svm_model getSvmModel() {
		return svmModel;
	}

	public void setSvmModel(svm_model svmModel) {
		this.svmModel = svmModel;
	}

	public svm_parameter getSvmParameter() {
		return svmParameter;
	}

	public void setSvmParameter(svm_parameter svmParameter) {
		this.svmParameter = svmParameter;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public final GrouperInterface getGrouper() {
		return grouper;
	}

	public final void setGrouper(GrouperInterface grouper) {
		this.grouper = grouper;
	}
	
	

}
