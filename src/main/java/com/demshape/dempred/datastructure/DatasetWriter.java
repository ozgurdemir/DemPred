package com.demshape.dempred.datastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The Class DatasetWriter contains simple functions to write a dataset to various sources such as a text file.
 */
public class DatasetWriter {

	
	/**
	 * Saves a dataset to a text file. Each line containing one datapoint. The format can be specified using a String pattern. Keywords are as follows:
	 *  comment: #c <br>
	 *  group: #g <br>
	 *  value: #v <br>
	 *  predictedGroup: #G <br>
	 *  predictedValue: #V <br>
	 *  weight: #w <br>
	 *  features #f <br>
	 *
	 * @param <T> the generic type
	 * @param dataset the dataset
	 * @param file the file
	 * @param pattern the pattern
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static <T extends Datapoint> void writeToLineSeperated(Dataset<T> dataset, File file, String pattern) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
		String output;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			output = pattern.replaceAll("#c", datapoint.getComment());
			
			if (datapoint.getPredictedGroup() != null)
				output = output.replaceAll("#G", datapoint.getPredictedGroup().toString());
			else
				output = output.replaceAll("#G", "");
			
			if (datapoint.getGroup() != null)
				output = output.replaceAll("#g", datapoint.getGroup().toString());
			else
				output = output.replaceAll("#g", "");
			
			if (datapoint.getFeatureVector() != null)
				output = output.replaceAll("#f", datapoint.getFeatureVector().toString(",", "#"));
			else
				output = output.replaceAll("#f", "");
			
			output = output.replaceAll("#v", Double.toString(datapoint.getValue()));
			output = output.replaceAll("#V", Double.toString(datapoint.getPredictedValue()));
			output = output.replaceAll("#w", Double.toString(datapoint.getWeight()));
			out.write(output);
			out.newLine();
		}
		out.flush();
		out.close();
	}

}
