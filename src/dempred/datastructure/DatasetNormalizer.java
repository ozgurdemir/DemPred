package dempred.datastructure;

import java.io.Serializable;
import java.util.Arrays;

import dempred.math.DenseVector;
import dempred.math.VectorInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetNormalizer. This class is used to normalize a dataset. 
 * In general features of a dataset are transformed such that they have a mean of zero and a standart variation of one. But other normalizations are also common.
 * It is crucial to transform the prediction set with the same values used to normalize the training set.
 */
public class DatasetNormalizer implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7817284463812821189L;
	
	/** The mean vector extracted from the trainings set. */
	private DenseVector meanVector;
	
	/** The std vector extracted from the trainings set. */
	private DenseVector stdVector;
	
	/** The zero indices extracted from the trainings set. */
	private int[] zeroIndices;
	
	/** The non zero indices extracted from the trainings set. */
	private int[] nonZeroIndices;

	/**
	 * Instantiates a new dataset normalizer.
	 */
	public DatasetNormalizer() {

	}

	/**
	 * Instantiates a new dataset normalizer. The given dataset is used to train the normalizer that is the mean and standard vector are extracted.
	 *
	 * @param dataset the dataset
	 */
	public DatasetNormalizer(Dataset<?> dataset) {
		train(dataset);
	}

	/**
	 * Instantiates a new dataset normalizer given a mean and standard normalization vector and a vector containing those indices that have a standard
	 * variation of zero
	 *
	 * @param meanVector the mean vector
	 * @param stdVector the std vector
	 * @param zeroIndices the zero indices
	 */
	public DatasetNormalizer(DenseVector meanVector, DenseVector stdVector, int[] zeroIndices) {
		super();
		this.meanVector = meanVector;
		this.stdVector = stdVector;
		this.zeroIndices = zeroIndices;
	}

	/**
	 * Train is used to extract the mean and std vector of a given dataset. All indices that have a standard variation of zero are saved in 
	 * the zero indices vector 
	 *
	 * @param dataset the dataset
	 */
	public void train(Dataset<?> dataset) {
		meanVector = getMean(dataset);
		stdVector = getStandart(dataset, meanVector);
		zeroIndices = stdVector.findIndices("==", 0.0);
		nonZeroIndices = stdVector.findIndices("!=", 0.0);
		meanVector.delete(zeroIndices);
		stdVector.delete(zeroIndices);
	}

	/**
	 * Normalizes a given dataset with mean and standard values extracted from train process. Indices saved in zeroIndices are removed from the dataset.
	 * 
	 *
	 * @param dataset the dataset
	 */
	public void normalize(Dataset<?> dataset) {
		dataset.deleteFeatures(zeroIndices);
		substractMean(dataset, meanVector);
		divStandart(dataset, stdVector);
	}

	/**
	 * Normalizes a single datapoint.
	 *
	 * @param datapoint the datapoint
	 */
	public void normalize(Datapoint datapoint) {
		VectorInterface featureVector = datapoint.getFeatureVector();
		featureVector.delete(zeroIndices);
		featureVector.subVector(meanVector);
		featureVector.divVector(stdVector);
	}

	/**
	 * Returns a FVector containing the mean values of the features. 
	 *
	 * @param dataset the dataset
	 * @return the mean
	 */
	public DenseVector getMean(Dataset<?> dataset) {
		DenseVector meanVector = new DenseVector(dataset.numFeatures(), 0.0);
		for (Datapoint datapoint : dataset.getDatapoints()) {
			meanVector.addVector(datapoint.getFeatureVector());
		}
		meanVector.divScalarZero(dataset.size());
		return meanVector;
	}

	/**
	 * Given a meanVector this function supstracts these values form a dataset.
	 *
	 * @param dataset the dataset
	 * @param meanVector the mean vector
	 */
	public void substractMean(Dataset<?> dataset, DenseVector meanVector) {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			datapoint.getFeatureVector().subVector(meanVector);
		}
	}

	/**
	 * Returns the standard vector of a dataset given a meanVector.
	 *
	 * @param dataset the dataset
	 * @param meanVector the mean vector
	 * @return the standart
	 */
	public DenseVector getStandart(Dataset<?> dataset, DenseVector meanVector) {
		VectorInterface tempVector;
		DenseVector sumVector = new DenseVector(dataset.numFeatures(), 0.0);
		for (Datapoint datapoint : dataset.getDatapoints()) {
			tempVector = datapoint.getFeatureVector().clone();
			tempVector.subVector(meanVector);
			tempVector.powScalar(2);
			sumVector.addVector(tempVector);
		}
		sumVector.divScalarZero(dataset.size() - 1);
		sumVector.powScalar(0.5);
		return sumVector;
	}

	/**
	 *  Returns the standard vector of a dataset.
	 *
	 * @param dataset the dataset
	 * @return the standart
	 */
	public VectorInterface getStandart(Dataset<?> dataset) {
		return getStandart(dataset, getMean(dataset));
	}

	/**
	 * Divides all datapoints of a dataset by the standard vector.
	 *
	 * @param dataset the dataset
	 * @param stdVector the std vector
	 */
	public void divStandart(Dataset<?> dataset, DenseVector stdVector) {
		for (Datapoint datapoint : dataset.getDatapoints()) {
			datapoint.getFeatureVector().divVector(stdVector);
		}
	}

	/**
	 * Given an integer array of zero indices the mean and standard vector are pruned by these values. 
	 * That is all indices noted in indices vector are removed from these vectors.
	 *
	 * @param indices the indices
	 */
	public void prune(int[] indices) {
		meanVector.delete(indices);
		stdVector.delete(indices);
		zeroIndices = new int[0];
		nonZeroIndices = new int[meanVector.size()];
		for (int i = 0; i < nonZeroIndices.length; ++i)
			nonZeroIndices[i] = i;
	}
	
	/**
	 * Given an array of indices the mean and standard vector are...
	 *
	 * @param indices the indices
	 */
	public void keep(int[] indices) {
		meanVector.keep(indices);
		stdVector.keep(indices);
		zeroIndices = new int[0];
		nonZeroIndices = new int[meanVector.size()];
		for (int i = 0; i < nonZeroIndices.length; ++i)
			nonZeroIndices[i] = i;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String lineseperator = System.getProperty("line.separator");
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(meanVector.toString());
		strBuf.append(lineseperator);
		strBuf.append(stdVector.toString());
		strBuf.append(lineseperator);
		strBuf.append(Arrays.toString(zeroIndices));
		strBuf.append(lineseperator);
		strBuf.append(Arrays.toString(nonZeroIndices));
		strBuf.append(lineseperator);
		return strBuf.toString();
	}

	/**
	 * Gets the mean vector.
	 *
	 * @return the mean vector
	 */
	public VectorInterface getMeanVector() {
		return meanVector;
	}

	/**
	 * Sets the mean vector.
	 *
	 * @param meanVector the new mean vector
	 */
	public void setMeanVector(DenseVector meanVector) {
		this.meanVector = meanVector;
	}

	/**
	 * Gets the std vector.
	 *
	 * @return the std vector
	 */
	public VectorInterface getStdVector() {
		return stdVector;
	}

	/**
	 * Sets the std vector.
	 *
	 * @param stdVector the new std vector
	 */
	public void setStdVector(DenseVector stdVector) {
		this.stdVector = stdVector;
	}

	/**
	 * Gets the zero indices.
	 *
	 * @return the zero indices
	 */
	public int[] getZeroIndices() {
		return zeroIndices;
	}

	/**
	 * Sets the zero indices.
	 *
	 * @param zeroIndices the new zero indices
	 */
	public void setZeroIndices(int[] zeroIndices) {
		this.zeroIndices = zeroIndices;
	}

	/**
	 * Gets the non zero indices.
	 *
	 * @return the non zero indices
	 */
	public final int[] getNonZeroIndices() {
		return nonZeroIndices;
	}

}
