package dempred.featuregeneration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dempred.datastructure.Datapoint;

/**
 * The feature generator is used to square all features of another feature generator. If a feature generator featGenX generates features like: f1, f2, f3 
 * then this squarer will generate features like: f1*f2, f1*f3,f2*f3,f1,f2,f3 
 *
 * @param <T> the generic type
 */
public class FeatureSquarer<T extends Datapoint> implements FeatureGeneratorInterface<T>, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3259096710217874367L;
	
	/** The feature generator. */
	private FeatureGeneratorInterface<T> featureGenerator;

	/**
	 * Instantiates a new feature squarer using a feature generator.
	 *
	 * @param featureGenerator the feature generator
	 */
	public FeatureSquarer(FeatureGeneratorInterface<T> featureGenerator) {
		this.featureGenerator = featureGenerator;
	}

	/* (non-Javadoc)
	 * @see dempred.featuregeneration.FeatureGeneratorInterface#generateFeature(dempred.datastructure.Datapoint)
	 */
	public double[] generateFeature(T datapoint) throws Exception {
		double[] linearFeatures = featureGenerator.generateFeature(datapoint);
		int d = linearFeatures.length;
		int numSquareFeatures = (int) (d + d * (d + 1.0) / 2.0);
		double[] squareFeatures = new double[numSquareFeatures];
		int index = 0;
		for (int i = 0; i < d; ++i) {
			squareFeatures[index++] = linearFeatures[i];
			for (int j = i; j < d; ++j)
				squareFeatures[index++] = linearFeatures[i] * linearFeatures[j];
		}
		return squareFeatures;
	}

	/**
	 * Gets the feature generator used.
	 *
	 * @return the feature generator
	 */
	public final FeatureGeneratorInterface<T> getFeatureGenerator() {
		return featureGenerator;
	}

	/**
	 * Sets the feature generator to be used.
	 *
	 * @param featureGenerator the new feature generator
	 */
	public final void setFeatureGenerator(FeatureGeneratorInterface<T> featureGenerator) {
		this.featureGenerator = featureGenerator;
	}

	/* (non-Javadoc)
	 * @see dempred.featuregeneration.FeatureGeneratorInterface#getNames(dempred.datastructure.Datapoint)
	 */
	public List<String> getNames(T datapoint) {
		List<String> originalFeatureNames = featureGenerator.getNames(datapoint);
		int d = originalFeatureNames.size();
		int numSquareFeatures = (int) (d + d * (d + 1.0) / 2.0);
		List<String> featureNames = new ArrayList<String>(numSquareFeatures);
		for (int i = 0; i < d; ++i) {
			featureNames.add(originalFeatureNames.get(i));
			for (int j = i; j < d; ++j)
				featureNames.add(originalFeatureNames.get(i) + "*" + originalFeatureNames.get(j));
		}
		return featureNames;
	}

}
