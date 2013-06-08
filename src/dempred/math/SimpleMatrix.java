package dempred.math;

import java.io.Serializable;

public class SimpleMatrix implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2512889415544599767L;
	private double[][] elements;

	public SimpleMatrix(int i, int j) {
		elements = new double[i][j];
	}

	public SimpleMatrix(int i, int j, double value) {
		elements = new double[i][j];
		fill(value);
	}

	public final double getCell(int i, int j) {
		return elements[i][j];
	}

	public final void setCell(int i, int j, double value) {
		elements[i][j] = value;
	}

	public final void fill(double value) {
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
				elements[i][j] = value;
			}
		}
	}
	
	public final double norm(int p){
		double p_norm = 0.0;
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
				p_norm += Math.pow(Math.abs(elements[i][j]), p);
			}
		}
		return p_norm;
	}
	
	public SimpleMatrix sign(){
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
				this.elements[i][j] = Math.signum(this.elements[i][j]);
			}
		}
		return this;
	}
	
	public int count(double x){
		int num=0;
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
			if(this.elements[i][j]==x)
				++num;
			}
		}
		return num;
	}
	
	public int countSmaller(double x){
		int num=0;
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
			if(this.elements[i][j]<x)
				++num;
			}
		}
		return num;
	}

	public final int numRows() {
		return elements.length;
	}

	public final int numColumns() {
		return elements[0].length;
	}

	public final SimpleMatrix addMatrix(SimpleMatrix mat) throws IllegalArgumentException {
		if (this.elements.length != mat.elements.length || this.elements[0].length != mat.elements[0].length)
			throw new IllegalArgumentException("Addition of the two Matrices is not possible. They have different amount of elements");

		for (int i = 0; i < this.elements.length; ++i) {
			for (int j = 0; j < this.elements[i].length; ++j) {
				this.elements[i][j] += mat.elements[i][j];
			}
		}
		return this;
	}

	public final SimpleMatrix mulScalar(double scalar) {
		for (int i = 0; i < this.elements.length; ++i) {
			for (int j = 0; j < this.elements[i].length; ++j) {
				this.elements[i][j] *= scalar;
			}
		}
		return this;
	}

	public String toString() {
		String output = "";
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
				output += elements[i][j] + ",";
			}
			output += System.getProperty("line.separator");
		}
		return output;
	}

	public SimpleMatrix clone() {
		SimpleMatrix temp = new SimpleMatrix(this.elements.length, this.elements[0].length);
		for (int i = 0; i < elements.length; ++i) {
			for (int j = 0; j < elements[i].length; ++j) {
				temp.elements[i][j] = this.elements[i][j];
			}
		}
		return temp;
	}

}
