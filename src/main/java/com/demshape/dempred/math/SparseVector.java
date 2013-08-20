package com.demshape.dempred.math;

import java.util.Arrays;

public class SparseVector implements Cloneable, VectorInterface {

	private int[] keys;
	private int size, used;
	private double[] values;

	public SparseVector(int size, int capacity) {
		assert size >= 0;
		assert capacity >= 0;
		this.size = size;
		this.keys = new int[capacity];
		this.values = new double[capacity];
	}

	public SparseVector(int[] keys, double[] values) {
		if (keys.length != values.length)
			throw new IllegalArgumentException("Elements have different number of elements: " + keys.length + " and " + values.length);
		this.keys = keys;
		this.values = values;
		this.size = keys[keys.length - 1] + 1;
		this.used = keys.length;
	}

	public SparseVector(double[] weightElements) {
		// count non null elements first then create according sparse vector
		int capacity = weightElements.length;
		this.keys = new int[capacity];
		this.values = new double[capacity];
		this.size = capacity;
		this.used = capacity;
		for (int i = 0; i < weightElements.length; ++i) {
			if (weightElements[i] != 0.0)
				this.set(i, weightElements[i]);
		}
	}

	public double get(int key) {
		if (key < 0 || key >= size)
			throw new IndexOutOfBoundsException(Integer.toString(key));
		int spot = Arrays.binarySearch(keys, 0, used, key);
		return spot < 0 ? 0 : values[spot];
	}

	public boolean isUsed(int key) {
		return 0 <= Arrays.binarySearch(keys, 0, used, key);
	}
	
	public void append(int key, double value){
		update(used, key, value);
	}

	public void set(int key, double value) {
		if (key < 0)
			throw new IndexOutOfBoundsException(Integer.toString(key));
		int spot = Arrays.binarySearch(keys, 0, used, key);
		if (spot >= 0)
			values[spot] = value;
		else
			update(-1 - spot, key, value);
	}

	public void resizeTo(int newSize) {
		if (newSize < this.size)
			throw new UnsupportedOperationException();
		this.size = newSize;
	}

	public int size() {
		return size;
	}

	private double update(int spot, int key, double value) {
		// grow if reaching end of capacity
		if (used == keys.length) {
			int capacity = (keys.length * 3) / 2 + 1;
			keys = Arrays.copyOf(keys, capacity);
			values = Arrays.copyOf(values, capacity);
		}
		// shift values if not appending
		if (spot < used) {
			System.arraycopy(keys, spot, keys, spot + 1, used - spot);
			System.arraycopy(values, spot, values, spot + 1, used - spot);
		}
		if (key >= size) {
			size++;
		}
		used++;
		keys[spot] = key;
		return values[spot] = value;
	}

	public int used() {
		return used;
	}

	public SparseVector trim() {
		keys = Arrays.copyOf(keys, used);
		values = Arrays.copyOf(values, used);
		return this;
	}

	@Override
	public SparseVector extendByOne(double value) {
		set(size, value);
		return this;
	}

	@Override
	public SparseVector reduceByOne() {
		this.size--;
		this.used--;
		return this;
	}

	@Override
	public double sum() {
		double sum = 0.0;
		for (int i = 0; i < used; ++i)
			sum += values[i];
		return sum;
	}

	@Override
	public double norm(double p) {
		double p_norm = 0.0;
		for (int i = 0; i < used; ++i)
			p_norm += Math.pow(Math.abs(values[i]), p);
		return p_norm;
	}

	@Override
	public double normRadical(double p) {
		return Math.pow(norm(p), (1.0 / p));
	}

	@Override
	public SparseVector sign() {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet sign");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector abs() {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet abs");
		System.exit(0);
		return null;
	}

	@Override
	public int count(String mode, double x) {
		System.out.println("Not implemented yet reduce");
		System.exit(0);
		return 0;
	}

	@Override
	public int[] findIndices(String mode, double x) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet indices");
		System.exit(0);
		return null;
	}

	@Override
	public int[] minIndex(int numEntries) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet minindex");
		System.exit(0);
		return null;
	}

	@Override
	public int[] maxIndex(int numEntries) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet maxindex");
		System.exit(0);
		return null;
	}

	@Override
	public double min() {
		System.out.println("Not implemented yet reduce");
		System.exit(0);
		return 0;
	}

	@Override
	public double max() {
		System.out.println("Not implemented yet reduce");
		System.exit(0);
		return 0;
	}

	@Override
	public double mean() {
		System.out.println("Not implemented yet reduce");
		System.exit(0);
		return 0;
	}

	@Override
	public double std() {
		System.out.println("Not implemented yet reduce");
		System.exit(0);
		return 0;
	}

	@Override
	public double std(double mean) {
		System.out.println("Not implemented yet reduce");
		System.exit(0);
		return 0;
	}

	@Override
	public double median() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SparseVector mulMatrix(SimpleMatrix mat) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet mulmatrix");
		System.exit(0);
		return null;
	}

	@Override
	public double scalarProduct(VectorInterface b) {
		if (this.size != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.size + " and " + b.size());
		double sum = 0.0;

		if (b instanceof SparseVector) {
			int indexA = 0;
			int indexB = 0;
			while (indexA < this.used && indexB < ((SparseVector) b).used) {
				if (keys[indexA] == ((SparseVector) b).keys[indexB])
					sum += values[indexA++] * ((SparseVector) b).values[indexB++];
				else if (keys[indexA] < ((SparseVector) b).keys[indexB])
					++indexA;
				else if (keys[indexA] > ((SparseVector) b).keys[indexB])
					++indexB;
			}
			return sum;
		} else if (b instanceof DenseVector) {
			for (int i = 0; i < used; ++i)
				sum += b.get(keys[i]) * values[i];
			return sum;
		}
		throw new IllegalArgumentException("Wrong vector types");
	}

	@Override
	public SparseVector addVector(VectorInterface b) throws IllegalArgumentException {
		if (this.size != b.size())
			throw new IllegalArgumentException("Vectors have different number of elements: " + this.size + " and " + b.size());

		if (b instanceof SparseVector) {
			int indexA = 0;
			int indexB = 0;
			while (indexA < this.used && indexB < ((SparseVector) b).used) {
				if (keys[indexA] == ((SparseVector) b).keys[indexB])
					values[indexA] = values[indexA++] + ((SparseVector) b).values[indexB++];
				else if (keys[indexA] < ((SparseVector) b).keys[indexB]) {
					++indexA;
				} else if (keys[indexA] > ((SparseVector) b).keys[indexB]) {
					this.set(((SparseVector) b).keys[indexB], ((SparseVector) b).values[indexB]);
					++indexB;
				}
			}
			return this;
		}
		throw new IllegalArgumentException("Wrong types johnny");
	}

	@Override
	public SparseVector subVector(VectorInterface b) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet subvet");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector mulVector(VectorInterface b) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet mulvec");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector divVector(VectorInterface b) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet div");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector powVector(VectorInterface b) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet pow");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector powScalar(double scalar) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet pow");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector divScalarZero(double scalar) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet div");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector divScalar(double scalar) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet div");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector addScalar(double scalar) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yetadd");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector subScalar(double scalar) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yetsub");
		System.exit(0);
		return null;
	}

	@Override
	public SparseVector mulScalar(double scalar) {
		for (int i = 0; i < values.length; ++i)
			values[i] = values[i] * scalar;
		return this;
	}

	@Override
	public SparseVector delete(int[] indices) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yetdelete");
		System.exit(0);
		return null;
	}

	@Override
	public void keep(int[] indices) {
		System.out.println("Not implemented yet reduce");
		System.exit(0);

	}

	@Override
	public String toString(String delimiter, String pattern) {
		// TODO Auto-generated method stub
		System.out.println("Not implemented yet tostring");
		System.exit(0);
		return null;
	}

	@Override
	public double[] getElements() {
		double[] elements = new double[size];
		int index = 0;
		for (int i = 0; i < size; ++i) {
			if (keys[index] == i)
				elements[i] = values[index++];
			else
				elements[i] = 0.0;
		}
		return elements;
	}

	@Override
	public void setElements(double[] elements) {
		System.out.println("Not implemented yet reduce");
		System.exit(0);

	}

	@Override
	public SparseVector clone() {
		try {
			SparseVector cl = (SparseVector) super.clone();
			cl.keys = this.keys.clone();
			cl.values = this.values.clone();
			return cl;
		} catch (CloneNotSupportedException e) {
			System.out.println("Clone not supported!");
			return null;
		}
	}

	public boolean equals(SparseVector b) {
		this.trim();
		b.trim();
		return (Arrays.equals(this.keys, b.keys) && Arrays.equals(this.values, b.values));
	}

	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		int index = 0;
		for (int i = 0; i < size; ++i) {
			if (keys[index] == i)
				strBuffer.append(values[index++]);
			else
				strBuffer.append(0.0);
			strBuffer.append(" ");
		}
		return strBuffer.toString();
	}

	public String toStringSparse() {
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < used; ++i) {
			strBuffer.append(keys[i]);
			strBuffer.append(":");
			strBuffer.append(values[i]);
			strBuffer.append(" ");
		}
		return strBuffer.toString();
	}

	public String toStringDebug() {
		return String.format("size:%d used:%d keys.length:%d values.length:%d", size, used, keys.length, values.length);
	}

	public int[] getKeys() {
		return keys;
	}

	public int getSize() {
		return size;
	}

	public int getUsed() {
		return used;
	}

	public double[] getValues() {
		return values;
	}

}
