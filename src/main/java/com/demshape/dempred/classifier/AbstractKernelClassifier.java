package com.demshape.dempred.classifier;

import java.io.Serializable;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.grouper.GrouperInterface;
import com.demshape.dempred.kernels.KernelInterface;
import com.demshape.dempred.lossfunction.LossFunctionInterface;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorInterface;



/**
 * This is an abstract class of a kernel learner...
 *
 * @param <T> the generic type
 */
public abstract class AbstractKernelClassifier<T extends Datapoint> implements ClassifierInterface<T>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -25654304351676869L;
	
	/** The w_plus. */
	protected transient double w_plus;
	
	/** The lambda2. */
	protected transient double lambda2;
	
	/** The use d weights. */
	protected transient boolean useDWeights;
	
	/** The group averaging. */
	protected transient boolean groupAveraging;
	
	/** The epsilon. */
	protected transient VectorInterface epsilon;
	
	/** The loss function. */
	protected transient LossFunctionInterface<T> lossFunction;
	
	/** The alpha. */
	protected VectorInterface alpha;
	
	/** The grouper. */
	protected GrouperInterface grouper;
	
	/** The kernel. */
	protected KernelInterface kernel;
	
	/** The train vectors. */
	protected VectorInterface[] trainVectors;
	
	/** The offset. */
	protected double offset;

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#predict(dempred.datastructure.Dataset)
	 */
	public final void predict(Dataset<T> dataset) throws IllegalArgumentException {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			double value = 0.0;
			for (int i = 0; i < alpha.size(); ++i)
				value += alpha.get(i) * kernel.evaluate(trainVectors[i], datapoint.getFeatureVector());
			datapoint.setPredictedValue(value + offset);
			if (grouper != null)
				datapoint.setPredictedGroup(grouper.getGroup(datapoint.getPredictedValue()));
		}
	}

	/* (non-Javadoc)
	 * @see dempred.classifier.ClassifierInterface#predict(dempred.datastructure.Datapoint)
	 */
	public final double predict(Datapoint datapoint) throws IllegalArgumentException {
		double value = 0.0;
		for (int i = 0; i < alpha.size(); ++i)
			value += alpha.get(i) * kernel.evaluate(trainVectors[i], datapoint.getFeatureVector());
		datapoint.setPredictedValue(value + offset);
		if (grouper != null)
			datapoint.setPredictedGroup(grouper.getGroup(datapoint.getPredictedValue()));
		return value;
	}

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	public final VectorInterface getWeight() {
		VectorInterface weight = new DenseVector(trainVectors[0].size());
		for (int i = 0; i < alpha.size(); ++i)
			weight.addVector(trainVectors[i].clone().mulScalar(alpha.get(i)));
		return weight;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ClassifierInterface<T> clone() {
		try {
			return (AbstractKernelClassifier<T>) super.clone();
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
	 * Gets the lambda.
	 *
	 * @return the lambda
	 */
	public final double getLambda() {
		return lambda2;
	}

	/**
	 * Sets the lambda.
	 *
	 * @param lambda the new lambda
	 */
	public final void setLambda(double lambda) {
		this.lambda2 = lambda;
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
	 * Gets the kernel.
	 *
	 * @return the kernel
	 */
	public final KernelInterface getKernel() {
		return kernel;
	}

	/**
	 * Sets the kernel.
	 *
	 * @param kernel the new kernel
	 */
	public final void setKernel(KernelInterface kernel) {
		this.kernel = kernel;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public final double getOffset() {
		return offset;
	}

	/**
	 * Sets the offset.
	 *
	 * @param offset the new offset
	 */
	public final void setOffset(double offset) {
		this.offset = offset;
	}

	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public final VectorInterface getAlpha() {
		return alpha;
	}

}
