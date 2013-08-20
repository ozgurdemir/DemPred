package com.demshape.dempred.featureselection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class FeatureSubset {
	private LinkedHashSet<Integer> featureIndices;
	private double score;
	private ArrayList<FeatureSubset> mergeHistory; 
	

	public FeatureSubset() {
		featureIndices = new LinkedHashSet<Integer>();
		mergeHistory = new ArrayList<FeatureSubset>(2);
	}

	public FeatureSubset(int index) {
		featureIndices = new LinkedHashSet<Integer>();
		featureIndices.add(index);
		mergeHistory = new ArrayList<FeatureSubset>(2);
	}

	public FeatureSubset(int[] indices) {
		featureIndices = new LinkedHashSet<Integer>();
		for (int index : indices)
			featureIndices.add(index);
		mergeHistory = new ArrayList<FeatureSubset>(2);
	}
	
	public FeatureSubset(FeatureSubset b) {
		featureIndices = new LinkedHashSet<Integer>();
		for (int index : b.featureIndices)
			featureIndices.add(index);
		mergeHistory = new ArrayList<FeatureSubset>(2);
	}
	
	public void addFeatureIndex(int index) {
		featureIndices.add(index);
	}

	public void addFeatureIndex(int[] indices) {
		for (int featureIndex : indices)
			featureIndices.add(featureIndex);
	}

	public void merge(FeatureSubset b) {
		for (int featureIndex : b.getFeatureIndices())
			this.addFeatureIndex(featureIndex);
		mergeHistory.add(b);
	}

	public int size() {
		return featureIndices.size();
	}

	public boolean overlap(FeatureSubset b) {
		for (int featureIndex : this.featureIndices) {
			if (b.featureIndices.contains(featureIndex))
				return true;
		}
		return false;
	}
	
	public void removeFeature(int index){
		this.featureIndices.remove(index);
	}

	public FeatureSubset getRoot() {
		FeatureSubset rootSet = new FeatureSubset();
		int i = 0;
		for (int featureIndex : featureIndices) {
			rootSet.addFeatureIndex(featureIndex);
			++i;
			if (i >= featureIndices.size() - 1)
				break;
		}
		return rootSet;
	}

	public boolean contains(int featureIndex) {
		return featureIndices.contains(featureIndex);
	}

	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(String.format("sc: %.4f #f: %d --> ", score, featureIndices.size()));
		String delimiter = "";
		for (int featureIndex : featureIndices) {
			strBuffer.append(delimiter + featureIndex);
			delimiter = ",";
		}
		return strBuffer.toString();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!o.getClass().equals(getClass()))
			return false;
		return this.featureIndices.equals(((FeatureSubset) o).featureIndices);
	}

	public int hashCode() {
		return featureIndices.hashCode();
	}

	// *****************************
	// Klasse zum sortieren
	public static class ScoreComparatorAsc implements Comparator<FeatureSubset> {
		public int compare(FeatureSubset a, FeatureSubset b) {
			if (a.score < b.score)
				return 1;
			else if (a.score > b.score)
				return -1;
			else
				return 0;
		}
	}

	public static class ScoreComparatorDesc implements Comparator<FeatureSubset> {
		public int compare(FeatureSubset a, FeatureSubset b) {
			if (a.score < b.score)
				return -1;
			else if (a.score > b.score)
				return 1;
			else
				return 0;
		}
	}

	// *****************************
	// getters and setters
	public final Set<Integer> getFeatureIndices() {
		return featureIndices;
	}

	public final double getScore() {
		return score;
	}

	public final void setScore(double score) {
		this.score = score;
	}

	public final ArrayList<FeatureSubset> getMergeHistory() {
		return mergeHistory;
	}
	
}
