package com.demshape.dempred.classifier;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demshape.dempred.bfgs.LBFGS;
import com.demshape.dempred.bfgs.OWLQNMinimizer;
import com.demshape.dempred.bfgs.LBFGS.ExceptionWithIflag;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.loggingtools.LoggerTools;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorInterface;
import com.demshape.dempred.rprop.IRpropMinus;
import com.demshape.dempred.rprop.RpropInterface;


import edu.stanford.nlp.optimization.DiffFunction;

public class WrapperPrimal<T extends Datapoint> extends AbstractLinearClassifier<T> implements ClassifierInterface<T>, Cloneable, Serializable {

	private static final long serialVersionUID = 6890065816825318778L;
	private static final Logger logger = Logger.getLogger(WrapperPrimal.class.getName());
	private transient double gradLength;
	private transient int solver;
	private transient int rPropFlag;

	public void learn(Dataset<T> dataset) throws Exception {
		int numFeatures = dataset.numFeatures() + 1;
		if (weight == null || weight.size() != numFeatures)
			weight = new DenseVector(numFeatures, 0.0);
		if (solver == 0)
			learnBFGS(dataset);
		else if (solver == 1)
			learnRprop(dataset);
		else if (solver == 2)
			learnOWLQN(dataset);
		else if (solver == 3)
			learnRpropOld(dataset);
		else
			throw new IllegalArgumentException("The selected solver does not exist.");
	}

	public void learnOWLQN(Dataset<T> dataset) throws IllegalArgumentException {
		dataset.extend(1.0);
		double regweight = lambda1;
		OWLQNMinimizer owlqn = new OWLQNMinimizer(regweight);
		if (LoggerTools.getLevel(logger).intValue() <= Level.FINEST.intValue())
			owlqn.quite = false;
		else
			owlqn.quite = true;
		FunctionAdapter obj = new FunctionAdapter();
		obj.numFeatures = dataset.numFeatures();
		obj.dataset = dataset;
		double tol = 0.0000001;
		double[] opt = owlqn.minimize(obj, tol, weight.getElements());
		weight = new DenseVector(opt);
		dataset.reduce();
	}

	class FunctionAdapter implements DiffFunction {
		public int numFeatures;
		public Dataset<T> dataset;

		public double valueAt(double[] x) {
			return L_mod(new DenseVector(x), dataset);
		}

		public double[] derivativeAt(double[] x) {
			return L_mod_deriv(new DenseVector(x), dataset).getElements();
		}

		public int domainDimension() {
			return numFeatures;
		}
	}
	
	public void learnRpropOld(Dataset<T> dataset) throws IllegalArgumentException {
		rPropFlag = 0;
		dataset.extend(1.0);
		int featureSize = dataset.numFeatures();
		RpropInterface rprop = new IRpropMinus(featureSize, 0.001);
		int LoggerLevel = LoggerTools.getLevel(logger).intValue();
		int i = 0;
		VectorInterface g = L_mod_deriv(weight, dataset);
		double preError = Double.MAX_VALUE;
		while (rPropFlag < 5) {
			double error = L_mod(weight, dataset);
			if (Math.abs(error - preError) < gradLength)
				++rPropFlag;
			else
				rPropFlag = 0;
			if (error < preError)
				preError = error;
			rprop.adjust(weight.getElements(), g.getElements(), 0.0);
			g = L_mod_deriv(weight, dataset);
			if (LoggerLevel <= Level.FINEST.intValue()){
				logger.finest(String.format("Learning RpropOld step:%d | Error:%.8f | GradientNorm:%.8f | WeightNorm:%.8f", ++i, error, g.normRadical(2), weight.normRadical(2)));
			}
		}		
		dataset.reduce();
	}

	public void learnRprop(Dataset<T> dataset) throws IllegalArgumentException {
		dataset.extend(1.0);
		int featureSize = dataset.numFeatures();
		RpropInterface rprop = new IRpropMinus(featureSize, 0.001);
		int LoggerLevel = LoggerTools.getLevel(logger).intValue();
		int i = 0;
		VectorInterface g = L_mod_deriv(weight, dataset);
		while (g.normRadical(2) > (gradLength * Math.max(1.0, weight.normRadical(2)))) {
			rprop.adjust(weight.getElements(), g.getElements(), 0.0);
			g = L_mod_deriv(weight, dataset);
			if (LoggerLevel <= Level.FINEST.intValue())
				logger.finest(String.format("Learning Rprop step:%d | Error:%.8f | GradientNorm:%.8f | WeightNorm:%.8f", ++i, L_mod(weight, dataset), g.normRadical(2), weight.normRadical(2)));
		}
		dataset.reduce();
	}

	public void learnBFGS(Dataset<T> dataset) throws ExceptionWithIflag {
		dataset.extend(1.0);
		int LoggerLevel = LoggerTools.getLevel(logger).intValue();
		int i = 0;
		int[] iflag = { 0 };
		int[] iprint = { -1, 1 };
		int n = dataset.numFeatures();
		int m = 3;
		double xtol = 1.0e-16;
		boolean diagco = false;
		double[] diag = new double[n];
		LBFGS lbfgs = new LBFGS();
		double lmod;
		VectorInterface g;
		do {
			lmod = L_mod(weight, dataset);
			g = L_mod_deriv(weight, dataset);
			lbfgs.lbfgs(n, m, weight.getElements(), lmod, g.getElements(), diagco, diag, iprint, gradLength, xtol, iflag);
			if (LoggerLevel <= Level.FINEST.intValue())
				logger.finest(String.format("Learning BFGS step:%d | Error:%.8f | GradientNorm:%.8f | WeightNorm:%.8f", ++i, lmod, g.normRadical(2), weight.normRadical(2)));
		} while (iflag[0] != 0);
		dataset.reduce();
	}

	@Override
	public ClassifierInterface<T> clone() {
		WrapperPrimal<T> cl = (WrapperPrimal<T>) super.clone();
		cl.weight = this.weight.clone();
		return cl;
	}

	// ********************************************
	// getters and setters
	// ********************************************

	public final double getGradLength() {
		return gradLength;
	}

	public final void setGradLength(double gradLength) {
		this.gradLength = gradLength;
	}

	public int getSolver() {
		return solver;
	}

	public void setSolver(int solver) {
		this.solver = solver;
	}

}
