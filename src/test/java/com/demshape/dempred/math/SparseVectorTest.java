package com.demshape.dempred.math;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;

import com.demshape.dempred.math.SparseVector;


public class SparseVectorTest extends TestCase {

	private SparseVector vector;
	private int[] keys = { 1, 3, 5 };
	private double[] values = { 1.0, -3.0, 1.0 };

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.vector = new SparseVector(keys, values);
	}

	public void testConstructor1() {
		SparseVector vectorB = new SparseVector(6, 3);
		for (int i = 0; i < vector.size(); ++i) {
			if (vector.isUsed(i))
				vectorB.set(i, vector.get(i));
		}
		assertTrue(vector.equals(vectorB));
	}

	
//	public void testConstructorDense(){
//		double[] elements = { 0.0, 1.0, -3.0, 0.0, 1.0 };
//		SparseVector vectorB = new SparseVector(elements);
//		System.out.println(vectorB);
//		assertTrue(vector.equals(vectorB));
//	}
	
	public void testSum() {
		assertEquals(-1.0, vector.sum());
	}

	public void testAddVector() {
		int[] keys = { 2, 3, 5 };
		double[] values = { 1.0, -2.0, 1.0 };
		SparseVector vectorB = new SparseVector(keys, values);
		SparseVector vectorA = vector.clone();
		vectorA.addVector(vectorB);
		assertEquals(-1.0, vectorA.sum());
	}

	@Test
	public void testMulScalar() {
		assertEquals(-2.0, vector.mulScalar(2.0).sum());
	}
	
	public void testScalarProduct() {
		int[] keys = { 2, 3, 5 };
		double[] values = { 1.0, -2.0, 1.0 };
		SparseVector vectorB = new SparseVector(keys, values);
		assertEquals(7.0, vector.scalarProduct(vectorB));
	}
	
	public void testElements() {
		double[] elements = {0.0, 1.0, 0.0, -3.0, 0.0, 1.0};
		assertTrue(Arrays.equals(elements, vector.getElements()));
	}
	
	public void testExtendReduce() {
		double[] elements = {0.0, 1.0, 0.0, -3.0, 0.0, 1.0, 1.0};
		assertTrue(Arrays.equals(elements, vector.extendByOne(1.0).getElements()));
		double[] reducedElements = {0.0, 1.0, 0.0,  -3.0, 0.0, 1.0};
		vector.reduceByOne();
		assertTrue(Arrays.equals(reducedElements, vector.getElements()));
		
		
	}
	
	public void testNorm(){
		assertEquals(5.0,  vector.norm(1));
		assertEquals(11.0,  vector.norm(2));
		assertEquals(Math.sqrt(11.0),  vector.normRadical(2));
	}

	public void testToString() {
		assertEquals("0.0 1.0 0.0 -3.0 0.0 1.0 ", vector.toString());
	}

	public void testClone() {
		SparseVector vectorB = vector.clone();
		vectorB.set(1, 2.0);
		assertEquals(1.0, vector.get(1));
		assertEquals(2.0, vectorB.get(1));
	}

	public void testEquals() {
		SparseVector vector2 = new SparseVector(keys, values);
		assertTrue(vector.equals(vector2));
	}

}
