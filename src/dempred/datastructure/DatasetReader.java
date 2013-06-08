package dempred.datastructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import dempred.math.SimpleVector;


/**
 * The Class DatasetReader is a simple class containing functions to read a dataset from various sources such as text files.
 */
public class DatasetReader {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(DatasetReader.class.getName());
	
	/** The Constant splitRegex. */
	private static final Pattern splitRegex = Pattern.compile(",");

	/**
	 * Read from .cvs. A simple function used to read a basic dataset from a comma separated file. The format of the dataset has to be as follows:
	 * comment, Group (class), Value, Weight, f1, f2 f3, ...fn
	 * Lines can be commented using a '#' sign. These lines will be ignored. 
	 * @param file the file
	 * @return the dataset
	 * @throws Exception the exception
	 */
	public static Dataset<Datapoint> readFromCVS(File file) throws Exception {
		Dataset<Datapoint> dataset = new Dataset<Datapoint>();
		BufferedReader bufReader = new BufferedReader(new FileReader(file));
		String line;
		int linenumber = 0;
		while ((line = bufReader.readLine()) != null) {
			logger.fine("Reading line:" + (++linenumber));
			if (!line.startsWith("#") && !line.trim().isEmpty()) {
				int featureIndex = 0;
				String value;
				String[] splittet = splitRegex.split(line);
				Datapoint datapoint = new Datapoint();
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setComment(value);
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setGroup(Integer.parseInt(value));
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setValue(Double.parseDouble(value));
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setWeight(Double.parseDouble(value));
				double[] featureVector = new double[splittet.length - featureIndex];
				for (int i = featureIndex; i < splittet.length; ++i)
					featureVector[i - featureIndex] = Double.parseDouble(splittet[i]);
				datapoint.setFeatureVector(new SimpleVector(featureVector));
				dataset.addDatapoint(datapoint);
			}
		}
		bufReader.close();
		return dataset;
	}
}
