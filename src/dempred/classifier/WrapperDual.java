package dempred.classifier;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import dempred.bfgs.LBFGS;
import dempred.bfgs.LBFGS.ExceptionWithIflag;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.loggingtools.LoggerTools;
import dempred.math.DenseVector;
import dempred.math.VectorInterface;
import dempred.rprop.IRpropMinus;
import dempred.rprop.RpropInterface;

public class WrapperDual<T extends Datapoint> extends AbstractKernelClassifier<T> implements ClassifierInterface<T>, Cloneable, Serializable {
	private static final long serialVersionUID = -6158721043971445795L;
	private static final Logger logger = Logger.getLogger(WrapperDual.class.getName());
	private transient int solver;
	private transient double gradLength;

	private final double predictTrainingPoint(VectorInterface a, int i, double[][] kernelMatrix) {
		double predictedValue = 0.0;
		for (int j = 0; j < a.size(); ++j)
			predictedValue += a.get(j) * kernelMatrix[i][j];
		return predictedValue + offset;
	}

	public final double L(VectorInterface a, Dataset<T> dataset, double[][] kernelMatrix) {
		double sum_neg = 0.0;
		double sum_pos = 0.0;
		double dweight = 1.0;
		for (int i = 0; i < dataset.size(); ++i) {
			T datapoint = dataset.getDatapoint(i);
			if (useDWeights)
				dweight = datapoint.getWeight();
			if (groupAveraging && datapoint.getGroup() == 1)
				sum_pos += dweight * lossFunction.g(predictTrainingPoint(a, i, kernelMatrix), datapoint.getValue(), datapoint);
			else
				sum_neg += dweight * lossFunction.g(predictTrainingPoint(a, i, kernelMatrix), datapoint.getValue(), datapoint);
		}
		if (groupAveraging) {
			double weight_plus = w_plus / dataset.groupQuantity(1);
			double weight_minus = (1.0 - w_plus) / dataset.groupQuantity(-1);
			return ((weight_minus * sum_neg) + (weight_plus * sum_pos));
		} else
			return (1.0 / dataset.size()) * sum_neg;
	}

	public final double L_mod(VectorInterface a, Dataset<T> dataset, double[][] kernelMatrix) {
		double obj = 1 - (lambda2);
		return (obj * L(a, dataset, kernelMatrix) + lambda2 * getWeight().norm(2));
	}

	public final VectorInterface alpha_deriv(VectorInterface a, Dataset<T> dataset, double[][] kernelMatrix) {
		double obj = 1 - (lambda2);
		double weight_plus;
		double weight_minus;
		if (groupAveraging) {
			weight_plus = (w_plus * obj) / (dataset.groupQuantity(1) * 2 * lambda2);
			weight_minus = ((1.0 - w_plus) * obj) / (dataset.groupQuantity(-1) * 2 * lambda2);
		} else {
			weight_plus = obj / (dataset.size() * 2 * lambda2);
			weight_minus = obj / (dataset.size() * 2 * lambda2);
		}
		double dweight = 1.0;
		double[] gradient = new double[dataset.size()];
		for (int i = 0; i < dataset.size(); ++i) {
			T datapoint = dataset.getDatapoint(i);
			if (useDWeights)
				dweight = datapoint.getWeight();
			if (groupAveraging && datapoint.getGroup() == 1)
				gradient[i] = a.get(i) + weight_plus * dweight * lossFunction.g_deriv(predictTrainingPoint(a, i, kernelMatrix), datapoint.getValue(), datapoint);
			else
				gradient[i] = a.get(i) + weight_minus * dweight * lossFunction.g_deriv(predictTrainingPoint(a, i, kernelMatrix), datapoint.getValue(), datapoint);
		}
		return new DenseVector(gradient);
	}

	public final double b_deriv(VectorInterface a, Dataset<T> dataset, double[][] kernelMatrix) {
		double obj = 1 - (lambda2);
		double weight_plus;
		double weight_minus;
		if (groupAveraging) {
			weight_plus = (w_plus * obj) / (dataset.groupQuantity(1) * 2 * lambda2);
			weight_minus = ((1.0 - w_plus) * obj) / (dataset.groupQuantity(-1) * 2 * lambda2);
		} else {
			weight_plus = obj / (dataset.size() * 2 * lambda2);
			weight_minus = obj / (dataset.size() * 2 * lambda2);
		}
		double dweight = 1.0;
		double gradient = 0.0;
		for (int i = 0; i < dataset.size(); ++i) {
			T datapoint = dataset.getDatapoint(i);
			if (useDWeights)
				dweight = datapoint.getWeight();
			if (groupAveraging && datapoint.getGroup() == 1)
				gradient += weight_plus * dweight * lossFunction.g_deriv(predictTrainingPoint(a, i, kernelMatrix), datapoint.getValue(), datapoint);
			else
				gradient += weight_minus * dweight * lossFunction.g_deriv(predictTrainingPoint(a, i, kernelMatrix), datapoint.getValue(), datapoint);
		}
		return gradient;
	}

	public void learn(Dataset<T> dataset) throws Exception {
		trainVectors = new VectorInterface[dataset.size()];
		for (int i = 0; i < dataset.size(); ++i)
			trainVectors[i] = dataset.getDatapoint(i).getFeatureVector().clone();
		int numDatapoints = dataset.size();
		double[][] kernelMatrix = new double[numDatapoints][numDatapoints];
		for (int i = 0; i < numDatapoints; ++i){
			logger.fine(String.format("Evaluating %d of %d", i, numDatapoints));
			for (int j = 0; j < numDatapoints; ++j)
				kernelMatrix[i][j] = kernel.evaluate(trainVectors[i], trainVectors[j]);
		}
		if (solver == 0)
			learnBFGS(dataset, kernelMatrix);
		else if (solver == 1)
			learnRprop(dataset, kernelMatrix);
		else
			throw new IllegalArgumentException("The selected solver does not exist.");
	}

	public void learnRprop(Dataset<T> dataset, double[][] kernelMatrix) throws IllegalArgumentException {
		int numDatapoints = dataset.size();
		RpropInterface rprop = new IRpropMinus(numDatapoints, 0.001);
		IRpropMinus rpropOffset = new IRpropMinus(1, 0.001);
		alpha = new DenseVector(numDatapoints, 0.0);
		int LoggerLevel = LoggerTools.getLevel(logger).intValue();
		int i = 0;
		VectorInterface g = alpha_deriv(alpha, dataset, kernelMatrix);
		double b[] = { b_deriv(alpha, dataset, kernelMatrix) };
		double off[] = { offset };
		while (g.normRadical(2) > (gradLength * Math.max(1.0, alpha.normRadical(2)))) {
			rprop.adjust(alpha.getElements(), g.getElements(), 0.0);
			rpropOffset.adjust(off, b, 0.0);
			offset = off[0];
			g = alpha_deriv(alpha, dataset, kernelMatrix);
			b[0] = b_deriv(alpha, dataset, kernelMatrix);
			if (LoggerLevel <= Level.FINEST.intValue())
				logger.finest(String.format("Learning Rprop step:%d | GradientNorm:%.8f", ++i, g.normRadical(2)));
		}
	}

	public void learnBFGS(Dataset<T> dataset, double[][] kernelMatrix) throws ExceptionWithIflag {
		int n = dataset.size();
		alpha = new DenseVector(n, 0.0);
		int LoggerLevel = LoggerTools.getLevel(logger).intValue();
		int i = 0;
		int[] iflag = { 0 };
		int[] iprint = { -1, 1 };
		int m = 3;
		double xtol = 1.0e-16;
		boolean diagco = false;
		double[] diag = new double[n];
		LBFGS lbfgs = new LBFGS();
		do {
			lbfgs.lbfgs(n, m, alpha.getElements(), L_mod(alpha, dataset, kernelMatrix), alpha_deriv(alpha, dataset, kernelMatrix).getElements(), diagco, diag, iprint, gradLength, xtol, iflag);
			if (LoggerLevel <= Level.FINEST.intValue())
				logger.finest(String.format("Learning BFGS step:%d | Error:%.8f | GradientNorm:%.8f", ++i, L_mod(alpha, dataset, kernelMatrix), alpha_deriv(alpha, dataset, kernelMatrix).normRadical(2)));
		} while (iflag[0] != 0);
	}

	public ClassifierInterface<T> clone() {
		WrapperDual<T> cl = (WrapperDual<T>) super.clone();
		cl.alpha = this.alpha.clone();
		return cl;
	}

	// ********************************************
	// getters and setters
	// ********************************************

	public int getSolver() {
		return solver;
	}

	public void setSolver(int solver) {
		this.solver = solver;
	}

	public final double getGradLength() {
		return gradLength;
	}

	public final void setGradLength(double gradLength) {
		this.gradLength = gradLength;
	}

}
