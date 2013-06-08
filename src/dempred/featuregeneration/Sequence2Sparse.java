package dempred.featuregeneration;

import java.util.Arrays;

/**
 * This feature generator class is used to generate "sparse" features for amino acid sequences. 
 */
public class Sequence2Sparse {

	/** The index. */
	private int[] index;
	
	/** The alphabet size. */
	private int alphabetSize;

	/**
	 * Instantiates a new sequence2 sparse.
	 */
	public Sequence2Sparse() {
		alphabetSize = 0;
		index = new int[128];
		index[((int) 'A')] = ++alphabetSize;
		index[((int) 'R')] = ++alphabetSize;
		index[((int) 'N')] = ++alphabetSize;
		index[((int) 'D')] = ++alphabetSize;
		index[((int) 'C')] = ++alphabetSize;
		index[((int) 'Q')] = ++alphabetSize;
		index[((int) 'E')] = ++alphabetSize;
		index[((int) 'G')] = ++alphabetSize;
		index[((int) 'H')] = ++alphabetSize;
		index[((int) 'I')] = ++alphabetSize;
		index[((int) 'L')] = ++alphabetSize;
		index[((int) 'K')] = ++alphabetSize;
		index[((int) 'M')] = ++alphabetSize;
		index[((int) 'F')] = ++alphabetSize;
		index[((int) 'P')] = ++alphabetSize;
		index[((int) 'S')] = ++alphabetSize;
		index[((int) 'T')] = ++alphabetSize;
		index[((int) 'W')] = ++alphabetSize;
		index[((int) 'Y')] = ++alphabetSize;
		index[((int) 'V')] = ++alphabetSize;
		index[((int) 'B')] = ++alphabetSize;
		index[((int) 'Z')] = ++alphabetSize;
		index[((int) 'X')] = ++alphabetSize;
		index[((int) '*')] = ++alphabetSize;
	}

	/**
	 * Generates sparse features for a sequence of amino acids.
	 *
	 * @param sequence the sequence
	 * @return the double[]
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public double[] generateFeature(String sequence) throws IllegalArgumentException {
		sequence = sequence.trim().toUpperCase();
		int seqLength = sequence.length();
		int ascode;
		double[] featureVector = new double[alphabetSize * seqLength];
		for (int i = 0; i < seqLength; ++i) {
			ascode = index[(int) sequence.charAt(i)];
			if (ascode < 1 || ascode > alphabetSize)
				throw new IllegalArgumentException("The inputfile contains illegal Aminoacids:'" + sequence.charAt(i) + "'");
			for (int j = 0; j < alphabetSize; ++j) {
				if (j == ascode - 1)
					featureVector[i * alphabetSize + j] = 1;
				else
					featureVector[i * alphabetSize + j] = 0;
			}
		}
		return featureVector;
	}

	/**
	 * Gets the feature names.
	 *
	 * @return the feature names
	 */
	public String[] getFeatureNames() {
		String[] featureNames = new String[alphabetSize];
		for (int i = 0; i < index.length; ++i) {
			if (index[i] > 0) 
				featureNames[index[i]-1] = Character.toString((char) i);
		}
		return featureNames;
	}

	/**
	 * Num features.
	 *
	 * @return the int
	 */
	public int numFeatures() {
		return alphabetSize;
	}
}
