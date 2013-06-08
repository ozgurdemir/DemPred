package dempred.featuregeneration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dempred.datastructure.Datapoint;

/**
 * This class is a wrapper used to concatenate features of various feature generators...
 *
 * @param <T> the generic type
 */
public class FeatureConcatenator<T extends Datapoint> implements FeatureGeneratorInterface<T>, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1715808212392977824L;
	
	/** The feature generators used to generate the features. */
	private ArrayList<FeatureGeneratorInterface<T>> featureGenerators;

	/**
	 * Instantiates a new feature concatenator.
	 */
	public FeatureConcatenator() {
		featureGenerators = new ArrayList<FeatureGeneratorInterface<T>>();
	}

	/**
	 * Instantiates a new feature concatenator with an initial size of feature generators...
	 *
	 * @param initialSize the initial size
	 */
	public FeatureConcatenator(int initialSize) {
		featureGenerators = new ArrayList<FeatureGeneratorInterface<T>>(initialSize);
	}

	/* (non-Javadoc)
	 * @see dempred.featuregeneration.FeatureGeneratorInterface#generateFeature(dempred.datastructure.Datapoint)
	 */
	public double[] generateFeature(T datapoint) throws Exception {
		double[][] features = new double[featureGenerators.size()][];
		int numFeatures = 0;
		for (int i = 0; i < featureGenerators.size(); ++i) {
			features[i] = featureGenerators.get(i).generateFeature(datapoint);
			numFeatures += features[i].length;
		}
		double[] featureVector = new double[numFeatures];
		int index = 0;
		for (int i = 0; i < features.length; ++i)
			for (int j = 0; j < features[i].length; ++j)
				featureVector[index++] = features[i][j];
		return featureVector;
	}

	/**
	 * Adds a feature generator to this concatenator.
	 *
	 * @param featureGenerator the feature generator to be added
	 */
	public void addFeatureGenerator(FeatureGeneratorInterface<T> featureGenerator) {
		featureGenerators.add(featureGenerator);
	}

	/**
	 * Gets the feature generators used.
	 *
	 * @return the feature generators
	 */
	public final ArrayList<FeatureGeneratorInterface<T>> getFeatureGenerators() {
		return featureGenerators;
	}

	/* (non-Javadoc)
	 * @see dempred.featuregeneration.FeatureGeneratorInterface#getNames(dempred.datastructure.Datapoint)
	 */
	public List<String> getNames(T datapoint) {
		List<String> featureNames = new ArrayList<String>();
		for (FeatureGeneratorInterface<T> generator : featureGenerators)
			featureNames.addAll(generator.getNames(datapoint));
		return featureNames;
	}

}
