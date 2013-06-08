package dempred.resampling;

import java.util.Random;

import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;

public class Bootstrap <T extends Datapoint> {
	private Dataset<T> dataset;
	private boolean[] selectedDatapoint;
	private Dataset<T> sample;
	private Random random;

	public Bootstrap(Dataset<T> dataset) {
		random = new Random();
		this.dataset = dataset;
	}

	public Bootstrap(Dataset<T> dataset, int n) {
		random = new Random();
		this.dataset = dataset;
		generateSample(n);
	}

	public void generateSample() {
		generateSample(dataset.size());
	}
	
	public void generateSample(int n) {
		sample = new Dataset<T>();
		sample.setName(this.dataset.getName() + " (Bootstrap sample!)");
		sample.setComment("Bootstrap sample!");
		selectedDatapoint = new boolean[dataset.size()];
		int numDatapoints = dataset.size();
		for (int i = 0; i < n; ++i) {
			int randomIndex = random.nextInt(numDatapoints);
			sample.addDatapoint(dataset.getDatapoint(randomIndex));
			selectedDatapoint[randomIndex] = true;
		}
	}

	public Dataset<T> getSample() {
		return sample;
	}

	public Dataset<T> getUnsampled() {
		Dataset<T> unSampled = new Dataset<T>();
		unSampled.setName(this.dataset.getName() + " (Bootstrap unsampled!)");
		unSampled.setComment("Bootstrap unsampled!");
		for (int i = 0; i < selectedDatapoint.length; ++i) {
			if (!selectedDatapoint[i])
				unSampled.addDatapoint(dataset.getDatapoint(i));
		}
		return unSampled;
	}

	public void printFoldIndex() {
		int numSampled = 0;
		for (int i = 0; i < selectedDatapoint.length; ++i){
			if (selectedDatapoint[i]){
				++numSampled;
				System.out.format("Datapoint: %d sampled %n",i+1);
			}else
				System.out.format("Datapoint: %d not sampled %n",i+1);
		}
		System.out.println("num sampled total: " + numSampled);
		System.out.println(sample);
		System.out.println(getUnsampled());
	}

}
