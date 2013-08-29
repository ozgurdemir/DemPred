package com.demshape.dempred.classifier;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Logger;

import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.resampling.ResamplingErrorInterface;
import com.demshape.dempred.util.Lib;



/**
 * A utility class used to optimize parameters of a classifier such as the lambda value.
 *
 * @param <T> the generic type
 */
public class ParameterOptimizer<T extends Datapoint> implements Iterator<ClassifierInterface<T>>, Iterable<ClassifierInterface<T>> {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ParameterOptimizer.class.getName());
	
	/** The best index. */
	private int bestIndex = 0;
	
	/** The actual index. */
	private int actualIndex = 0;
	
	/** The smallest error. */
	private double smallestError = Double.POSITIVE_INFINITY;
	
	/** The classifier. */
	private ClassifierInterface<T> classifier;
	
	/** The trainset. */
	private Dataset<T> trainset;
	
	/** The resampler. */
	private ResamplingErrorInterface<T> resampler;
	
	/** The field. */
	private Field field;
	
	/** The parameter list. */
	private Object[] parameterList;
	
	/** The actual error. */
	private double actualError;

	/**
	 * Instantiates a new parameter optimizer.
	 *
	 * @param classifier the classifier to be optimized
	 * @param trainset the trainset used to optimize th classifier
	 * @param resampler the resampler used to evaluate the error of a parameter
	 * @param field the field to be optimized
	 * @param parameterList the parameter list
	 */
	public ParameterOptimizer(ClassifierInterface<T> classifier, Dataset<T> trainset, ResamplingErrorInterface<T> resampler, Field field, Object[] parameterList) {
		super();
		this.classifier = classifier;
		this.trainset = trainset;
		this.resampler = resampler;
		this.field = field;
		this.parameterList = parameterList;
		this.field.setAccessible(true);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return actualIndex < parameterList.length - 1;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ClassifierInterface<T> next() {
		logger.fine(String.format("Setting %s to %s", field.getName(), parameterList[actualIndex].toString()));
		try {
			field.set(classifier, parameterList[actualIndex]);
			actualError = resampler.error(classifier, trainset);
			logger.fine(String.format("Estimated error: %.4f", actualError));
			if (actualError < smallestError) {
				smallestError = actualError;
				bestIndex = actualIndex;
			}
		} catch (Exception e) {
			logger.fine(Lib.getStackTrace(e));
			return null;
		}
		actualIndex++;
		return classifier;
	}

	/**
	 * Sets the best so far found parameter.
	 *
	 * @throws Exception the exception
	 */
	public void setBest() throws Exception {
		field.set(classifier, parameterList[bestIndex]);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new AssertionError("Not allowed");
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<ClassifierInterface<T>> iterator() {
		return this;
	}

	/**
	 * Returns the parameter with lowest error
	 *
	 * @return the best parameter with lowest error
	 */
	public Object bestValue() {
		return parameterList[bestIndex];
	}

	/**
	 * Returns the actual error.
	 *
	 * @return the actual error
	 */
	public final double getActualError() {
		return actualError;
	}

}
