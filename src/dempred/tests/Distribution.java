package dempred.tests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;

import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetGenerator;
import dempred.datastructure.DatasetManipulator;
import dempred.math.DenseVector;
import dempred.math.VectorMetric;

public class Distribution {
	
	public static double[] getFeatSimilarity(Dataset<?> dataset){
		DenseVector[] featureVectors = DatasetManipulator.getFeatureVectors(dataset);
		int numFeatures = dataset.numFeatures();
		int total=((numFeatures-1)*numFeatures)/2;
		int oldPercent=-1;
		int percent=0;
		double[] result = new double[total];
		int index=0;
		for(int i=0; i<numFeatures; ++i){
			percent = (int)Math.floor((double)index*100.0/total);
			if(oldPercent != percent){
				System.out.println(percent+ " %");
				oldPercent = percent;
			}
			for(int j=i+1; j<numFeatures; ++j){
				result[index++] = Math.pow(VectorMetric.cosine(featureVectors[i], featureVectors[j]),2);
			}
		}
		return result;
	}
	
	public static double[] getMolSimilarity(Dataset<?> dataset){
		int numDatapoints = dataset.size();
		int total=((numDatapoints-1)*numDatapoints)/2;
		double[] result = new double[total];
		int index=0;
		for(int i=0; i<numDatapoints; ++i){
			for(int j=i+1; j<numDatapoints; ++j){
				result[index++] = VectorMetric.cosine(dataset.getDatapoint(i).getFeatureVector(),dataset.getDatapoint(j).getFeatureVector());
			}
		}
		return result;
	}
	
	
	public static void featSimAll(Dataset<?> dataset, String filepath){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filepath, false));
			out.write(Arrays.toString(Distribution.hist(Distribution.getFeatSimilarity(dataset))).replace("[", "").replace("]", ""));
			out.newLine();
			out.write(Arrays.toString(Distribution.hist(Distribution.getFeatSimilarity(DatasetGenerator.getGroup(dataset,1)))).replace("[", "").replace("]", ""));
			out.newLine();
			out.write(Arrays.toString(Distribution.hist(Distribution.getFeatSimilarity(DatasetGenerator.getGroup(dataset,-1)))).replace("[", "").replace("]", ""));
			out.newLine();
			out.flush();
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void molSimAll(Dataset<?> dataset, String filepath){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filepath, false));
			out.write(Arrays.toString(Distribution.hist(Distribution.getMolSimilarity(dataset))).replace("[", "").replace("]", ""));
			out.newLine();
			out.write(Arrays.toString(Distribution.hist(Distribution.getMolSimilarity(DatasetGenerator.getGroup(dataset,1)))).replace("[", "").replace("]", ""));
			out.newLine();
			out.write(Arrays.toString(Distribution.hist(Distribution.getMolSimilarity(DatasetGenerator.getGroup(dataset,-1)))).replace("[", "").replace("]", ""));
			out.newLine();
			out.flush();
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static int[] hist(double[] input){
		double min = -1.0;
		double max = 1.0;
		int numBins=50;
		double stepsize = (max-min)/numBins;
		int[] result = new int[numBins];
		for(double value: input){
			int binIndex = (int)Math.floor((value-min)/stepsize);
			if(binIndex>=result.length)
				binIndex=result.length-1;
			else if(binIndex<0)
				binIndex=0;
			result[binIndex]++;
		}
		return result;
	}
	
}
