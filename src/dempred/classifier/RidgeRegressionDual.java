package dempred.classifier;

import java.io.Serializable;
import java.util.logging.Logger;

import Jama.CholeskyDecomposition;
import Jama.Matrix;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.math.DenseVector;

public class RidgeRegressionDual<T extends Datapoint> extends AbstractKernelClassifier<T> implements ClassifierInterface<T>, Serializable {

	private static final long serialVersionUID = 1872365710384415880L;
	private static final Logger logger = Logger.getLogger(RidgeRegressionDual.class.getName());

	public void learn(Dataset<T> dataset) throws IllegalArgumentException {
		logger.fine("learning RidgeRegressionDual");
		dataset.extend(1.0);
		int featureSize = dataset.numFeatures();
		alpha = new DenseVector(dataset.size(), 0.0);

		// K Matrix
		double obj = 1 - lambda2;
		double weight_plus = w_plus / dataset.groupQuantity(1);
		double weight_minus = (1 - w_plus) / dataset.groupQuantity(-1);
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
		// Matrix X = new Matrix(Xtemp);

		// M Matrix
		double[][] Btemp = new double[dataset.size()][1];
		for (int i = 0; i < dataset.size(); ++i)
			Btemp[i][0] = weighting[i] * dataset.getDatapoint(i).getValue();
		Matrix M = new Matrix(Btemp);

		// store training points
		trainVectors = new DenseVector[dataset.size()];
		for (int i = 0; i < dataset.size(); ++i)
			trainVectors[i] = new DenseVector(Xtemp[i]);

		// CheloskyDecomposition
		double[][] atemp = new double[dataset.size()][dataset.size()];
		for (int i = 0; i < dataset.size(); ++i) {
			for (int j = 0; j < dataset.size(); ++j)
				atemp[i][j] = kernel.evaluate(trainVectors[i], trainVectors[j]);
		}
		Matrix A = new Matrix(atemp);
		Matrix B = M;
		if (epsilon != null) {
			for (int i = 0; i < A.getColumnDimension() - 1; ++i)
				A.set(i, i, (A.get(i, i) + lambda2 * epsilon.get(i)));
		} else {
			for (int i = 0; i < A.getColumnDimension(); ++i)
				A.set(i, i, (A.get(i, i) + lambda2));
		}
		CholeskyDecomposition cholDec = new CholeskyDecomposition(A);
		Matrix a = cholDec.solve(B);
		double[][] wtemp = a.transpose().getArray();
		alpha = new DenseVector(wtemp[0]);

		dataset.reduce();
	}

	public ClassifierInterface<T> clone() {
		RidgeRegressionDual<T> cl = (RidgeRegressionDual<T>) super.clone();
		cl.alpha = this.alpha.clone();
		return cl;
	}

}
