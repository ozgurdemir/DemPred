package dempred.math;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DenseVector implements Cloneable, Serializable, VectorInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6270174159819507806L;
	private double[] elements;

	// ********************konstruktoren
	public DenseVector(double[] elements) {
		this.elements = elements;
	}

	public DenseVector(int[] elements) {
		this.elements = new double[elements.length];
		for (int i = 0; i < elements.length; ++i)
			this.elements[i] = elements[i];
	}

	public DenseVector(int dim) {
		this.elements = new double[dim];
	}

	public DenseVector(int dim, double init) {
		this.elements = new double[dim];
		Arrays.fill(this.elements, init);
	}

	// ********************modifier
	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#get(int)
	 */
	@Override
	public final double get(int i) {
		return elements[i];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#set(int, double)
	 */
	@Override
	public final void set(int i, double element) {
		elements[i] = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#size()
	 */
	@Override
	public final int size() {
		return elements.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#extendByOne(double)
	 */
	@Override
	public DenseVector extendByOne(double value) {
		double[] newElements = new double[this.elements.length + 1];
		System.arraycopy(this.elements, 0, newElements, 0, this.elements.length);
		newElements[newElements.length - 1] = value;
		this.elements = newElements;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#reduceByOne()
	 */
	@Override
	public DenseVector reduceByOne() {
		double[] newElements = new double[this.elements.length - 1];
		System.arraycopy(this.elements, 0, newElements, 0, this.elements.length - 1);
		this.elements = newElements;
		return this;
	}

	// ********************operationen

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#sum()
	 */
	@Override
	public final double sum() {
		double sum = 0;
		for (int i = 0; i < elements.length; ++i)
			sum += this.elements[i];
		return sum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#norm(double)
	 */
	@Override
	public final double norm(double p) {
		double p_norm = 0.0;
		for (int i = 0; i < elements.length; ++i) {
			p_norm += Math.pow(Math.abs(this.elements[i]), p);
		}
		return p_norm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#normRadical(double)
	 */
	@Override
	public final double normRadical(double p) {
		return Math.pow(norm(p), (1.0 / p));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#sign()
	 */
	@Override
	public DenseVector sign() {
		for (int i = 0; i < this.elements.length; ++i) {
			this.elements[i] = Math.signum(this.elements[i]);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#abs()
	 */
	@Override
	public DenseVector abs() {
		for (int i = 0; i < this.elements.length; ++i)
			this.elements[i] = Math.abs(this.elements[i]);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#count(java.lang.String, double)
	 */
	@Override
	public int count(String mode, double x) {
		int num = 0;
		for (int i = 0; i < this.elements.length; ++i) {
			if (mode.equals("<") && this.elements[i] < x)
				++num;
			else if (mode.equals("<=") && this.elements[i] <= x)
				++num;
			else if (mode.equals(">") && this.elements[i] > x)
				++num;
			else if (mode.equals(">=") && this.elements[i] >= x)
				++num;
			else if (mode.equals("==") && this.elements[i] == x)
				++num;
		}
		return num;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#findIndices(java.lang.String, double)
	 */
	@Override
	public int[] findIndices(String mode, double x) {
		List<Integer> indices = new ArrayList<Integer>(this.elements.length);
		for (int i = 0; i < this.elements.length; ++i) {
			if (mode.equals("<") && this.elements[i] < x)
				indices.add(i);
			else if (mode.equals("<=") && this.elements[i] <= x)
				indices.add(i);
			else if (mode.equals(">") && this.elements[i] > x)
				indices.add(i);
			else if (mode.equals(">=") && this.elements[i] >= x)
				indices.add(i);
			else if (mode.equals("==") && this.elements[i] == x)
				indices.add(i);
			else if (mode.equals("!=") && this.elements[i] != x)
				indices.add(i);
		}
		int[] indicesArray = new int[indices.size()];
		int i = 0;
		for (Integer index : indices)
			indicesArray[i++] = index;
		return indicesArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#minIndex(int)
	 */
	@Override
	public int[] minIndex(int numEntries) {
		int[] resultArray = new int[numEntries];
		IndexHelper[] helperArray = new IndexHelper[this.elements.length];
		for (int i = 0; i < this.elements.length; ++i)
			helperArray[i] = new IndexHelper(this.elements[i], i);
		Arrays.sort(helperArray);
		for (int i = 0; i < numEntries; ++i)
			resultArray[i] = helperArray[i].index;
		return resultArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#maxIndex(int)
	 */
	@Override
	public int[] maxIndex(int numEntries) {
		int[] resultArray = new int[numEntries];
		IndexHelper[] helperArray = new IndexHelper[this.elements.length];
		for (int i = 0; i < this.elements.length; ++i)
			helperArray[i] = new IndexHelper(this.elements[i], i);
		Arrays.sort(helperArray);
		int j = 0;
		for (int i = helperArray.length - numEntries; i < helperArray.length; ++i)
			resultArray[j++] = helperArray[i].index;
		return resultArray;
	}

	class IndexHelper implements Comparable<IndexHelper> {
		public double value;
		public int index;

		public IndexHelper(double value, int index) {
			this.value = value;
			this.index = index;
		}

		public int compareTo(IndexHelper b) {
			if (this.value < b.value)
				return -1;
			if (this.value > b.value)
				return 1;
			else
				return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#min()
	 */
	@Override
	public double min() {
		double min = Double.POSITIVE_INFINITY;
		for (double value : this.elements) {
			if (value < min)
				min = value;
		}
		return min;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#max()
	 */
	@Override
	public double max() {
		double max = Double.NEGATIVE_INFINITY;
		for (double value : this.elements) {
			if (value > max)
				max = value;
		}
		return max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#mean()
	 */
	@Override
	public double mean() {
		double mean = 0.0;
		for (int i = 0; i < this.elements.length; ++i) {
			mean += this.elements[i];
		}
		return (mean / this.elements.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#std()
	 */
	@Override
	public double std() {
		return std(this.mean());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#std(double)
	 */
	@Override
	public double std(double mean) {
		double sdt = 0.0;
		for (int i = 0; i < this.elements.length; ++i) {
			sdt += Math.pow(this.elements[i] - mean, 2);
		}
		return Math.sqrt(sdt / (this.elements.length - 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#median()
	 */
	@Override
	public double median() {
		double[] sortedElements = elements.clone();
		Arrays.sort(sortedElements);
		int medianIndex = (int) Math.ceil((double) sortedElements.length / 2.0) - 1;
		if (sortedElements.length % 2 == 0)
			return (sortedElements[medianIndex] + sortedElements[medianIndex + 1]) / 2.0;
		else
			return sortedElements[medianIndex];
	}

	/*
	 * public void random(byte mean, byte stand){ for(int i=0;
	 * i<dimension.length;++i) dimension[i]= (byte)mean + Math.random()stand; }
	 */

	// ********************matrix operationen
	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#mulMatrix(dempred.math.SimpleMatrix)
	 */
	@Override
	public final DenseVector mulMatrix(SimpleMatrix mat) throws IllegalArgumentException {
		if (mat.numRows() != this.size())
			throw new IllegalArgumentException("Matrix(" + mat.numRows() + ") and vector(" + this.size() + ") have different length!");
		double[] oldVector = this.elements.clone();
		double value;
		for (int colnum = 0; colnum < mat.numColumns(); ++colnum) {
			value = 0.0;
			for (int i = 0; i < this.size(); ++i) {
				value += oldVector[i] * mat.getCell(i, colnum);
			}
			this.elements[colnum] = value;
		}
		return this;
	}

	// ********************vector operationen
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dempred.math.VectorInterface#scalarProduct(dempred.math.SimpleVector)
	 */
	@Override
	public final double scalarProduct(VectorInterface b) {
		if (this.size() != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.elements.length + " and " + b.size());
		double sum = 0.0;
		for (int i = 0; i < elements.length; ++i)
			sum += this.get(i) * b.get(i);
		return sum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#addVector(dempred.math.SimpleVector)
	 */
	@Override
	public final DenseVector addVector(VectorInterface b) throws IllegalArgumentException {
		if (this.size() != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.elements.length + " and " + b.size());
		if (b instanceof DenseVector) {
			DenseVector denseVector = (DenseVector) b;
			for (int i = 0; i < elements.length; ++i)
				this.elements[i] += denseVector.elements[i];
		} else if (b instanceof SparseVector) {
			SparseVector sparseVector = (SparseVector) b;
			int[] keys = sparseVector.getKeys();
			double[] values = sparseVector.getValues();
			for (int i = 0; i < sparseVector.used(); ++i)
				this.elements[keys[i]] += values[i];
		} else
			throw new IllegalArgumentException("Wrong Vector Type");
		
		return this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#subVector(dempred.math.SimpleVector)
	 */
	@Override
	public final DenseVector subVector(VectorInterface b) {
		if (this.size() != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.elements.length + " and " + b.size());
		for (int i = 0; i < elements.length; ++i)
			this.elements[i] -= b.get(i);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#mulVector(dempred.math.SimpleVector)
	 */
	@Override
	public final DenseVector mulVector(VectorInterface b) {
		if (this.size() != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.elements.length + " and " + b.size());
		for (int i = 0; i < elements.length; ++i)
			this.elements[i] *= b.get(i);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#divVector(dempred.math.SimpleVector)
	 */
	@Override
	public final DenseVector divVector(VectorInterface b) throws IllegalArgumentException {
		if (this.size() != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.elements.length + " and " + b.size());
		for (int i = 0; i < elements.length; ++i) {
			if (b.get(i) == 0)
				throw new IllegalArgumentException("Division by zero!");
			this.elements[i] /= b.get(i);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#powVector(dempred.math.SimpleVector)
	 */
	@Override
	public final DenseVector powVector(VectorInterface b) {
		if (this.size() != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.elements.length + " and " + b.size());
		for (int i = 0; i < elements.length; ++i)
			this.elements[i] = Math.pow(this.elements[i], b.get(i));
		return this;
	}

	// ********************scalar operationen
	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#powScalar(double)
	 */
	@Override
	public final DenseVector powScalar(double scalar) {
		for (int i = 0; i < elements.length; ++i)
			this.elements[i] = Math.pow(this.elements[i], scalar);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#divScalarZero(double)
	 */
	@Override
	public final DenseVector divScalarZero(double scalar) {
		if (scalar == 0)
			return this;
		for (int i = 0; i < elements.length; ++i)
			elements[i] /= scalar;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#divScalar(double)
	 */
	@Override
	public final DenseVector divScalar(double scalar) throws IllegalArgumentException {
		if (scalar == 0)
			throw new IllegalArgumentException("Division by zero!");
		for (int i = 0; i < elements.length; ++i)
			elements[i] /= scalar;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#addScalar(double)
	 */
	@Override
	public final DenseVector addScalar(double scalar) {
		for (int i = 0; i < elements.length; ++i)
			elements[i] += scalar;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#subScalar(double)
	 */
	@Override
	public final DenseVector subScalar(double scalar) {
		for (int i = 0; i < elements.length; ++i)
			elements[i] -= scalar;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#mulScalar(double)
	 */
	@Override
	public final DenseVector mulScalar(double scalar) {
		for (int i = 0; i < elements.length; ++i)
			elements[i] *= scalar;
		return this;
	}

	// ********************sonstige
	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#delete(int[])
	 */
	@Override
	public DenseVector delete(int[] indices) {
		Arrays.sort(indices);
		double[] reducedVector = new double[this.elements.length - indices.length];
		int indexPointer = 0;
		int vectorPointer = 0;
		for (int i = 0; i < this.elements.length; ++i) {
			if (indexPointer < indices.length && i == indices[indexPointer])
				++indexPointer;
			else
				reducedVector[vectorPointer++] = this.elements[i];
		}
		this.elements = reducedVector;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#keep(int[])
	 */
	@Override
	public void keep(int[] indices) {
		double[] reduced = new double[indices.length];
		for (int i = 0; i < indices.length; ++i)
			reduced[i] = this.elements[indices[i]];
		this.elements = reduced;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#clone()
	 */
	@Override
	public DenseVector clone() {
		try {
			DenseVector cl = (DenseVector) super.clone();
			cl.setElements(this.elements.clone());
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#toString()
	 */
	@Override
	public final String toString() {
		return toString(",", "0.0000");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#toString(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public final String toString(String delimiter, String pattern) {
		String del = "";
		StringBuffer output = new StringBuffer(this.elements.length * 10);
		DecimalFormat myDF = new DecimalFormat(pattern);
		for (double element : elements) {
			output.append(del);
			output.append(myDF.format(element));
			del = delimiter;
		}
		return output.toString();
	}

	// getters and setters
	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#getElements()
	 */
	@Override
	public final double[] getElements() {
		return elements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dempred.math.VectorInterface#setElements(double[])
	 */
	@Override
	public final void setElements(double[] elements) {
		this.elements = elements;
	}

}
