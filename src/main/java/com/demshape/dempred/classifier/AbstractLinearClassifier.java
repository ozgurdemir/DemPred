package com.demshape.dempred.classifier;

import java.io.Serializable;
import java.util.ArrayList;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.grouper.GrouperInterface;
import com.demshape.dempred.losslunction.LossFunctionInterface;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorInterface;



// TODO: Auto-generated Javadoc
/**
 * This is an abstract class of a linear learner.
 *
 * @param <T> the generic type
 */
public abstract class AbstractLinearClassifier<T extends Datapoint> implements ClassifierInterface<T>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1584937045062049454L;
	
	/** The loss function. */
	protected transient LossFunctionInterface<T> lossFunction;
	
	/** The w_plus. */
	protected transient double w_plus;
	
	/** The lambda2. */
	protected transient double lambda2;
	
	/** The lambda1. */
	protected transient double lambda1;
	
	/** The use d weights. */
	protected transient boolean useDWeights;
	
	/** The group averaging. */
	protected transient boolean groupAveraging;
	
	/** The epsilon. */
	protected transient VectorInterface epsilon;
	
	/** The weight. */
	protected VectorInterface weight;
	
	/** The grouper. */
	protected GrouperInterface grouper;

	/**
	 * Scoring function f...
	 *
	 * @param x the x
	 * @param w the w
	 * @return the double
	 */
	public final double f(VectorInterface x, VectorInterface w) {
		return x.scalarProduct(w);
	}

	/**
	 * derivative of scoring function f
	 *
	 * @param x the x
	 * @param w the w
	 * @return the f vector
	 */
	public final VectorInterface f_deriv(VectorInterface x, VectorInterface w) {
		return x.clone();
	}

	/**
	 * Objective function without regularization
	 *
	 * @param w the w
	 * @param dataset the dataset
	 * @return score of objective function...
	 */
	public final double L(VectorInterface w, Dataset<T> dataset) {
		double sum_neg = 0.0;
		double sum_pos = 0.0;
		double dweight = 1.0;
		for (T datapoint : dataset.getDatapoints()) {
			if (useDWeights)
				dweight = datapoint.getWeight();
			if (groupAveraging && datapoint.getGroup() == 1)
				sum_pos += dweight * lossFunction.g(f(datapoint.getFeatureVector(), w), datapoint.getValue(), datapoint);
			else
				sum_neg += dweight * lossFunction.g(f(datapoint.getFeatureVector(), w), datapoint.getValue(), datapoint);
		}
		if (groupAveraging) {
			double weight_plus = w_plus / dataset.groupQuantity(1);
			double weight_minus = (1.0 - w_plus) / dataset.groupQuantity(-1);
			return ((weight_minus * sum_neg) + (weight_plus * sum_pos));
		} else
			return (1.0 / dataset.size()) * sum_neg;
	}

	/**
	 * objective function with regularization term..
	 *
	 * @param w the w
	 * @param dataset the dataset
	 * @return the double
	 */
	public final double L_mod(VectorInterface w, Dataset<T> dataset) {
		double obj = 1 - (lambda2 + lambda1);
		VectorInterface wCopy = w.clone();
		wCopy.set(wCopy.size() - 1, 0.0);
		if (epsilon != null) {
			double[] a1 = wCopy.getElements();
			for (int i = 0; i < epsilon.size(); ++i)
				a1[i] *= epsilon.get(i);
		}
		return (obj * L(w, dataset) + lambda2 * wCopy.norm(2));

//		 double obj = 1 - (lambda2 + lambda1);
//		 FVector wCopy = w.clone();
//		 wCopy.set(wCopy.size() - 1, 0.0);
//		 if (epsilon != null) {
//		 double[] a1 = wCopy.getElements();
//		 for (int i = 0; i < epsilon.size(); ++i)
//		 a1[i] *= epsilon.get(i);
//		 }
//		 return (obj * L(w, dataset) + lambda2 * wCopy.norm(2) + lambda1 * wCopy.norm(1));
	}

	/**
	 * derivative of objective function without regulariztion term.
	 *
	 * @param w the w
	 * @param dataset the dataset
	 * @return the gradient of the objective function without regularization term.
	 */
	public final VectorInterface L_deriv(VectorInterface w, Dataset<T> dataset) {
		VectorInterface sum_neg = new DenseVector(w.size(), 0.0);
		VectorInterface sum_pos = new DenseVector(w.size(), 0.0);
		double dweight = 1.0;
		int index=0;
		for (T datapoint : dataset.getDatapoints()) {
			if (useDWeights)
				dweight = datapoint.getWeight();
			VectorInterface feature = datapoint.getFeatureVector();
			if (groupAveraging && datapoint.getGroup() == 1)
				sum_pos.addVector(f_deriv(feature, w).mulScalar(dweight * lossFunction.g_deriv(f(feature, w), datapoint.getValue(), datapoint)));
			else
				sum_neg.addVector(f_deriv(feature, w).mulScalar(dweight * lossFunction.g_deriv(f(feature, w), datapoint.getValue(), datapoint)));
		}
		if (groupAveraging) {
			double weight_plus = w_plus / dataset.groupQuantity(1);
			double weight_minus = (1.0 - w_plus) / dataset.groupQuantity(-1);
			return sum_neg.mulScalar(weight_minus).addVector(sum_pos.mulScalar(weight_plus));
		} else
			return sum_neg.mulScalar(1.0 / dataset.size());
	}

	/**
	 * derivative of objective function wit regulariztion term.
	 *
	 * @param w the w
	 * @param dataset the dataset
	 * @return the gradient of the objective function with regularization term.
	 */
	public final VectorInterface L_mod_deriv(VectorInterface w, Dataset<T> dataset) {
		double obj = 1 - (lambda2 + lambda1);
		VectorInterface wCopy = w.clone();
		wCopy.set(wCopy.size() - 1, 0.0);
		if (epsilon != null) {
			double[] a1 = wCopy.getElements();
			for (int i = 0; i < epsilon.size(); ++i) {
				a1[i] *= epsilon.get(i);
			}
		}
		return (L_deriv(w, dataset).mulScalar(obj).addVector(wCopy.mulScalar(lambda2 * 2)));

//		double obj = 1 - (lambda2 + lambda1);
//		FVector w_copy = w.clone();
//		w_copy.set(w_copy.size() - 1, 0.0);
//		FVector w_copy2 = w.clone();
//		w_copy2.set(w_copy2.size() - 1, 0.0);
//		if (epsilon != null) {
//			double[] a1 = w_copy.getElements();
//			double[] a2 = w_copy2.getElements();
//			for (int i = 0; i < epsilon.size(); ++i) {
//				a1[i] *= epsilon.get(i);
//				a2[i] *= epsilon.get(i);
//			}
//		}
//		return (L_deriv(w, dataset).mulScalar(obj).addVector(w_copy.sign().mulScalar(lambda1)).addVector(w_copy2.mulScalar(lambda2 * 2)));
	}

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#predict(dempred.datastructure.Dataset)
	 */
	public final void predict(Dataset<T> dataset) throws IllegalArgumentException {
		dataset.extend(1.0);
		for (Datapoint datapoint : dataset.getDatapoints()) {
			datapoint.setPredictedValue(f(datapoint.getFeatureVector(), weight));
			if (grouper != null)
				datapoint.setPredictedGroup(grouper.getGroup(datapoint.getPredictedValue()));
		}
		dataset.reduce();
	}

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#predict(dempred.datastructure.Datapoint)
	 */
	public final double predict(Datapoint datapoint) throws IllegalArgumentException {
		datapoint.getFeatureVector().extendByOne(1.0);
		double value = f(datapoint.getFeatureVector(), weight);
		datapoint.setPredictedValue(value);
		if (grouper != null)
			datapoint.setPredictedGroup(grouper.getGroup(value));
		datapoint.getFeatureVector().reduceByOne();
		return value;
	}

	// achtung funktioniert nicht mit kernels!
	// wenn effectVector
	// positiv = feature verkleinert den Fehler, also wichtiges Feature
	// null = feature hat keine Auswirkung, also unwichtig
	// negativ = feature vergroessert den Fehler, also besser ohne
	/**
	 * Calculates the effect of every single feature on the objective function. the larger the value the more importand the feature.
	 *
	 * @param dataset the dataset
	 * @return a vector containing a value representing the importance of each value...
	 */
	public final VectorInterface effectObjFunc(Dataset<T> dataset) {
		dataset.extend(1.0);
		double sum_neg = 0.0;
		double sum_pos = 0.0;
		double weight_plus = 1.0 / dataset.size();
		double weight_minus = 1.0 / dataset.size();
		if (groupAveraging) {
			weight_plus = w_plus / dataset.groupQuantity(1);
			weight_minus = (1.0 - w_plus) / dataset.groupQuantity(-1);
		}
		ArrayList<Double> tempVec = new ArrayList<Double>(dataset.size() - 1);
		VectorInterface effectVec = new DenseVector(dataset.numFeatures() - 1);
		for (T datapoint : dataset.getDatapoints()) {
			if (groupAveraging && datapoint.getGroup() == 1)
				sum_pos += lossFunction.g(f(datapoint.getFeatureVector(), weight), datapoint.getValue(), datapoint);
			else
				sum_neg += lossFunction.g(f(datapoint.getFeatureVector(), weight), datapoint.getValue(), datapoint);
			tempVec.add(f(datapoint.getFeatureVector(), weight));
		}
		double with_weight = (weight_minus * sum_neg) + (weight_plus * sum_pos);
		for (int j = 0; j < effectVec.size(); ++j) {
			sum_pos = 0.0;
			sum_neg = 0.0;
			int i = 0;
			for (T datapoint : dataset.getDatapoints()) {
				if (groupAveraging && datapoint.getGroup() == 1)
					sum_pos += lossFunction.g(tempVec.get(i) - (datapoint.getFeatureAt(j) * weight.get(j)), datapoint.getValue(), datapoint);
				else
					sum_neg += lossFunction.g(tempVec.get(i) - (datapoint.getFeatureAt(j) * weight.get(j)), datapoint.getValue(), datapoint);
				++i;
			}
			double without_weight = (weight_minus * sum_neg) + (weight_plus * sum_pos);
			effectVec.set(j, (without_weight - with_weight));
		}
		dataset.reduce();
		return effectVec;
	}

	/**
	 * Calculates the effect of every single feature on the objective function. the larger the value the more importand the feature. 
	 * This function retrains the classifier for every single feature and hence is computational quite expensive... 
	 *
	 * @param dataset the dataset
	 * @return the f vector
	 * @throws Exception the exception
	 */
	public final VectorInterface effectObjRetrain(Dataset<T> dataset) throws Exception {
		VectorInterface rank = new DenseVector(dataset.numFeatures());
		dataset.extend(1.0);
		double with_weight = L(weight, dataset);
		dataset.reduce();
		for (int i = 0; i < rank.size(); ++i) {
			Dataset<T> reducedDataset = dataset.clone();
			int[] deletedFeatures = { i };
			reducedDataset.deleteFeatures(deletedFeatures);
			this.learn(reducedDataset);
			reducedDataset.extend(1.0);
			double without_weight = L(weight, reducedDataset);
			reducedDataset.reduce();
			rank.set(i, without_weight - with_weight);
		}
		return rank;
	}

	/**
	 * Calculates the effect of every single feature on the objective function. the larger the value the more importand the feature. 
	 * This function retrains the classifier for every single feature and hence is computational quite expensive... 
	 *
	 *
	 * @param trainset the trainset
	 * @param testset the testset
	 * @return the f vector[]
	 * @throws Exception the exception
	 */
	public final VectorInterface[] effectObjRetrain(Dataset<T> trainset, Dataset<T> testset) throws Exception {
		VectorInterface rankTrain = new DenseVector(trainset.numFeatures());
		VectorInterface rankTest = new DenseVector(trainset.numFeatures());
		trainset.extend(1.0);
		testset.extend(1.0);
		double with_weight_train = L(weight, trainset);
		double with_weight_test = L(weight, testset);
		trainset.reduce();
		testset.reduce();
		for (int i = 0; i < rankTrain.size(); ++i) {
			Dataset<T> reducedTrainset = trainset.clone();
			Dataset<T> reducedTestset = testset.clone();
			int[] deletedFeatures = { i };
			reducedTrainset.deleteFeatures(deletedFeatures);
			reducedTestset.deleteFeatures(deletedFeatures);
			this.learn(reducedTrainset);
			reducedTrainset.extend(1.0);
			reducedTestset.extend(1.0);
			double without_weight_train = L(weight, reducedTrainset);
			double without_weight_test = L(weight, reducedTestset);
			reducedTrainset.reduce();
			reducedTestset.reduce();
			rankTrain.set(i, without_weight_train - with_weight_train);
			rankTest.set(i, without_weight_test - with_weight_test);
		}
		VectorInterface[] resultRank = { rankTrain, rankTest };
		return resultRank;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ClassifierInterface<T> clone() {
		try {
			return (AbstractLinearClassifier<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ********************************************
	// getters and setters
	// ********************************************

	/**
	 * Gets the w_plus.
	 *
	 * @return the w_plus
	 */
	public final double getW_plus() {
		return w_plus;
	}

	/**
	 * Sets the w_plus.
	 *
	 * @param w_plus the new w_plus
	 */
	public final void setW_plus(double w_plus) {
		this.w_plus = w_plus;
	}

	/**
	 * Gets the lambda2.
	 *
	 * @return the lambda2
	 */
	public final double getLambda2() {
		return lambda2;
	}

	/**
	 * Sets the lambda2.
	 *
	 * @param lambda the new lambda2
	 */
	public final void setLambda2(double lambda) {
		this.lambda2 = lambda;
	}

	/**
	 * Gets the lambda1.
	 *
	 * @return the lambda1
	 */
	public final double getLambda1() {
		return lambda1;
	}

	/**
	 * Sets the lambda1.
	 *
	 * @param lambda1 the new lambda1
	 */
	public final void setLambda1(double lambda1) {
		this.lambda1 = lambda1;
	}

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public final VectorInterface getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 *
	 * @param weight the new weight
	 */
	public final void setWeight(VectorInterface weight) {
		this.weight = weight;
	}

	/**
	 * Gets the loss function name.
	 *
	 * @return the loss function name
	 */
	public final String getLossFunctionName() {
		return lossFunction.getName();
	}

	/**
	 * Checks if is use d weights.
	 *
	 * @return true, if is use d weights
	 */
	public final boolean isUseDWeights() {
		return useDWeights;
	}

	/**
	 * Sets the use d weights.
	 *
	 * @param useDWeights the new use d weights
	 */
	public final void setUseDWeights(boolean useDWeights) {
		this.useDWeights = useDWeights;
	}

	/**
	 * Gets the loss function.
	 *
	 * @return the loss function
	 */
	public final LossFunctionInterface<T> getLossFunction() {
		return lossFunction;
	}

	/**
	 * Sets the loss function.
	 *
	 * @param lossFunction the new loss function
	 */
	public final void setLossFunction(LossFunctionInterface<T> lossFunction) {
		this.lossFunction = lossFunction;
	}

	/**
	 * Gets the epsilon.
	 *
	 * @return the epsilon
	 */
	public final VectorInterface getEpsilon() {
		return epsilon;
	}

	/**
	 * Sets the epsilon.
	 *
	 * @param epsilon the new epsilon
	 */
	public final void setEpsilon(VectorInterface epsilon) {
		this.epsilon = epsilon;
	}

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#getGrouper()
	 */
	public final GrouperInterface getGrouper() {
		return grouper;
	}

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#setGrouper(dempred.grouper.GrouperInterface)
	 */
	public final void setGrouper(GrouperInterface grouper) {
		this.grouper = grouper;
	}

	/**
	 * Checks if is group averaging.
	 *
	 * @return true, if is group averaging
	 */
	public final boolean isGroupAveraging() {
		return groupAveraging;
	}

	/**
	 * Sets the group averaging.
	 *
	 * @param groupAveraging the new group averaging
	 */
	public final void setGroupAveraging(boolean groupAveraging) {
		this.groupAveraging = groupAveraging;
	}
}
