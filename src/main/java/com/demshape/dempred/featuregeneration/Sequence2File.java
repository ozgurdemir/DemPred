package com.demshape.dempred.featuregeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * This feature generator class is used to generate features for string sequences like amino acid or nucleic acid sequences. 
 * The features are read from a matrix text file such as a BLOSUM matrix.
 */
public class Sequence2File {
	
	/** The aamap. */
	private LinkedHashMap<Character, double[]> aamap;
	
	/** The alphabet size. */
	private int alphabetSize;
	
	/** The feature names. */
	private String[] featureNames;
	
	/** The filename. */
	private String filename;

	/**
	 * Instantiates a new instance and reads in a feature matrix from specified file. 
	 *
	 * @param filepath the filepath
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Sequence2File(String filepath) throws IOException {
		alphabetSize = 0;
		readFeaturesFromFile(filepath);
	}

	/**
	 * Reads features from a text file.
	 *
	 * @param filepath the filepath of the input matrix
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void readFeaturesFromFile(String filepath) throws IOException {
		aamap = new LinkedHashMap<Character, double[]>(25);
		File file = new File(filepath);
		filename = file.getName();
		BufferedReader bufReader = new BufferedReader(new FileReader(file));
		String line;
		int i = 0;
		while ((line = bufReader.readLine()) != null) {
			if (!line.startsWith("#")) {
				if (line.length() > 0 && i++ > 0)
					fillMap(line);
				else
					readHeader(line);
			}
		}
		bufReader.close();
	}

	/**
	 * Fill map.
	 *
	 * @param line the line
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	private void fillMap(String line) throws IllegalArgumentException {
		String[] splittet = line.split(" ++");
		int numFeatures = splittet.length - 1;
		if (alphabetSize != numFeatures)
			throw new IllegalArgumentException(String.format("The number of features is not the same for all aminoacids in the feature file! alphabetSize:%d numFeatures:%d", alphabetSize, numFeatures));
		double[] features = new double[numFeatures];
		for (int i = 0; i < numFeatures; ++i)
			features[i] = Double.parseDouble(splittet[i + 1]);
		aamap.put(splittet[0].trim().toUpperCase().charAt(0), features);
	}

	/**
	 * Read header.
	 *
	 * @param line the line
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	private void readHeader(String line) throws IllegalArgumentException {
		String[] splittet = line.trim().split(" ++");
		int numFeatures = splittet.length;
		if (alphabetSize <= 0)
			alphabetSize = numFeatures;
		featureNames = new String[numFeatures];
		for (int i = 0; i < numFeatures; ++i)
			featureNames[i] = String.format("%s (%s)", splittet[i], filename);
	}

	/**
	 * Generate features for an amino acid sequence using the specified matrix.
	 *
	 * @param sequence the sequence
	 * @return the double[]
	 */
	public double[] generateFeature(String sequence) {
		sequence = sequence.toUpperCase();
		int seqLength = sequence.length();
		double[] featureVector = new double[alphabetSize * seqLength];
		double[] featureVectorTemp;
		try {
			for (int i = 0; i < seqLength; ++i) {
				featureVectorTemp = aamap.get(sequence.charAt(i));
				if (featureVectorTemp == null)
					throw new IllegalArgumentException("The inpufile contains aminoacids, that are not specified in the featurefile: " + sequence.charAt(i));
				for (int j = 0; j < alphabetSize; ++j)
					featureVector[i * alphabetSize + j] = featureVectorTemp[j];
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println(e);
		}
		return featureVector;
	}

	/**
	 * returns the number of generated features for a single amino acid.
	 *
	 * @return the int
	 */
	public int numFeatures() {
		return alphabetSize;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String lineSeperator = System.getProperty("line.separator");
		StringBuffer strBuffer = new StringBuffer();
		for (char key : aamap.keySet())
			strBuffer.append(key + " --> " + Arrays.toString(aamap.get(key)) + lineSeperator);
		return strBuffer.toString();
	}

	/**
	 * Returns the feature names of this matrix file.
	 *
	 * @return the feature names
	 */
	public String[] getFeatureNames() {
		return featureNames;
	}
}
