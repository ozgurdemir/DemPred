package dempred.math;

public interface VectorInterface {

	// ********************modifier
	public abstract double get(int i);

	public abstract void set(int i, double element);

	public abstract int size();

	public abstract VectorInterface extendByOne(double value);

	public abstract VectorInterface reduceByOne();

	public abstract double sum();

	public abstract double norm(double p);

	public abstract double normRadical(double p);

	public abstract VectorInterface sign();

	public abstract VectorInterface abs();

	public abstract int count(String mode, double x);

	public abstract int[] findIndices(String mode, double x);

	public abstract int[] minIndex(int numEntries);

	public abstract int[] maxIndex(int numEntries);

	public abstract double min();

	public abstract double max();

	public abstract double mean();

	public abstract double std();

	public abstract double std(double mean);

	public abstract double median();

	// ********************matrix operationen
	public abstract VectorInterface mulMatrix(SimpleMatrix mat) throws IllegalArgumentException;

	// ********************vector operationen
	public abstract double scalarProduct(VectorInterface b);

	public abstract VectorInterface addVector(VectorInterface b) throws IllegalArgumentException;

	public abstract VectorInterface subVector(VectorInterface b);

	public abstract VectorInterface mulVector(VectorInterface b);

	public abstract VectorInterface divVector(VectorInterface b) throws IllegalArgumentException;

	public abstract VectorInterface powVector(VectorInterface b);

	// ********************scalar operationen
	public abstract VectorInterface powScalar(double scalar);

	public abstract VectorInterface divScalarZero(double scalar);

	public abstract VectorInterface divScalar(double scalar) throws IllegalArgumentException;

	public abstract VectorInterface addScalar(double scalar);

	public abstract VectorInterface subScalar(double scalar);

	public abstract VectorInterface mulScalar(double scalar);

	// ********************sonstige
	public abstract VectorInterface delete(int[] indices);

	public abstract void keep(int[] indices);

	public abstract VectorInterface clone();

	public abstract String toString();

	public abstract String toString(String delimiter, String pattern);

	// getters and setters
	public abstract double[] getElements();

	public abstract void setElements(double[] elements);

}