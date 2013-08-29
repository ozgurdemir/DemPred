package com.demshape.dempred.classifier;

import java.io.Serializable;
import java.util.logging.Logger;

import Jama.CholeskyDecomposition;
import Jama.Matrix;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.losslunction.Mse;
import com.demshape.dempred.math.DenseVector;

public class RidgeRegressionPrimal<T extends Datapoint> extends AbstractLinearClassifier<T> implements ClassifierInterface<T>, Cloneable, Serializable {

	private static final long serialVersionUID = 1312119293710866838L;
	private static final Logger logger = Logger.getLogger(RidgeRegressionPrimal.class.getName());

	public RidgeRegressionPrimal() {
		super();
		lossFunction = new Mse<T>();
	}

	public void learn(Dataset<T> dataset) throws IllegalArgumentException {
		logger.fine("learning RidgeRegressionPrimal");
		dataset.extend(1.0);
		int featureSize = dataset.numFeatures();
		weight = new DenseVector(featureSize, 0.0);

		// K Matrix
		double obj = 1 - lambda2;
		double weight_plus = w_plus / dataset.groupQuantity(1);
		double weight_minus = (1.0 - w_plus) / dataset.groupQuantity(-1);
		double dweight = 1.0;
		double[] weighting = new double[dataset.size()];
		for (int i = 0; i < dataset.size(); ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			if (useDWeights)
				dweight = datapoint.getWeight();
			if (groupAveraging) {
				if (datapoint.getGroup() == 1)
					weighting[i] = Math.sqrt(obj * weight_plus * dweight);
				else
					weighting[i] = Math.sqrt(obj * weight_minus * dweight);
			} else
				weighting[i] = Math.sqrt(obj * dweight * (1.0 / dataset.size()));
		}

		// X Matrix
		double[][] Xtemp = new double[dataset.size()][featureSize];
		for (int i = 0; i < dataset.size(); ++i) {
			for (int j = 0; j < dataset.numFeatures(); ++j)
				Xtemp[i][j] = weighting[i] * dataset.getDatapoint(i).getFeatureAt(j);
		}
		Matrix X = new Matrix(Xtemp);

		// M Matrix
		double[][] Btemp = new double[dataset.size()][1];
		for (int i = 0; i < dataset.size(); ++i)
			Btemp[i][0] = weighting[i] * dataset.getDatapoint(i).getValue();
		Matrix M = new Matrix(Btemp);

		// CholeskyDecomposition
		Matrix A = X.transpose().times(X);
		Matrix B = X.transpose().times(M);
		if (epsilon != null) {
			for (int i = 0; i < A.getColumnDimension() - 1; ++i)
				A.set(i, i, (A.get(i, i) + lambda2 * epsilon.get(i)));
		} else {
			for (int i = 0; i < A.getColumnDimension() - 1; ++i)
				A.set(i, i, (A.get(i, i) + lambda2));
		}
		CholeskyDecomposition cholDec = new CholeskyDecomposition(A);
		Matrix w = cholDec.solve(B);

		double[][] wtemp = w.transpose().getArray();
		weight = new DenseVector(wtemp[0]);
		dataset.reduce();
	}

	@Override
	public ClassifierInterface<T> clone() {
		RidgeRegressionPrimal<T> cl = (RidgeRegressionPrimal<T>) super.clone();
		cl.weight = this.weight.clone();
		return cl;
	}

}
