/*
 * Copyright (c) 2010, William Bittle
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.game2d.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test cases for the {@link Matrix} class.
 * @author William Bittle
 */
public class MatrixTest {
	/**
	 * Test the creation method passing four doubles.
	 */
	@Test
	public void createFull() {
		Matrix m = new Matrix(1.0, 2.0, 
				             -3.0, 8.0);
		TestCase.assertEquals(1.0, m.m00);
		TestCase.assertEquals(2.0, m.m01);
		TestCase.assertEquals(-3.0, m.m10);
		TestCase.assertEquals(8.0, m.m11);
	}
	
	/**
	 * Test the creation method passing a double array.
	 */
	@Test
	public void createFullArray() {
		Matrix m = new Matrix(new double[] {1.0, 2.0, 
				                           -3.0, 8.0});
		TestCase.assertEquals(1.0, m.m00);
		TestCase.assertEquals(2.0, m.m01);
		TestCase.assertEquals(-3.0, m.m10);
		TestCase.assertEquals(8.0, m.m11);
	}
	
	/**
	 * Tests the copy constructor.
	 */
	@Test
	public void copy() {
		Matrix m1 = new Matrix();
		m1.m00 = 0; m1.m01 = 2;
		m1.m10 = 1; m1.m11 = 3;
		
		// make a copy
		Matrix m2 = new Matrix(m1);
		// test the values
		TestCase.assertEquals(m1.m00, m2.m00);
		TestCase.assertEquals(m1.m01, m2.m01);
		TestCase.assertEquals(m1.m10, m2.m10);
		TestCase.assertEquals(m1.m11, m2.m11);
	}
	
	/**
	 * Tests the add method.
	 */
	@Test
	public void add() {
		Matrix m1 = new Matrix(0.0, 2.0, 
				               3.5, 1.2);
		Matrix m2 = new Matrix(1.3, 0.3, 
				               0.0, 4.5);
		m1.add(m2);
		// test the values
		TestCase.assertEquals(1.3, m1.m00);
		TestCase.assertEquals(2.3, m1.m01);
		TestCase.assertEquals(3.5, m1.m10);
		TestCase.assertEquals(5.7, m1.m11);
	}

	/**
	 * Tests the sum method.
	 */
	@Test
	public void sum() {
		Matrix m1 = new Matrix(0.0, 2.0, 
				               3.5, 1.2);
		Matrix m2 = new Matrix(1.3, 0.3, 
				               0.0, 4.5);
		Matrix m3 = m1.sum(m2);
		// test the values
		TestCase.assertEquals(1.3, m3.m00);
		TestCase.assertEquals(2.3, m3.m01);
		TestCase.assertEquals(3.5, m3.m10);
		TestCase.assertEquals(5.7, m3.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m3));
	}

	/**
	 * Tests the subtract method.
	 */
	@Test
	public void subtract() {
		Matrix m1 = new Matrix(0.0, 2.0, 
				               3.5, 1.2);
		Matrix m2 = new Matrix(1.3, 0.3, 
				               0.0, 4.5);
		m1.subtract(m2);
		// test the values
		TestCase.assertEquals(-1.3, m1.m00);
		TestCase.assertEquals(1.7, m1.m01);
		TestCase.assertEquals(3.5, m1.m10);
		TestCase.assertEquals(-3.3, m1.m11);
	}

	/**
	 * Tests the difference method.
	 */
	@Test
	public void difference() {
		Matrix m1 = new Matrix(0.0, 2.0, 
				               3.5, 1.2);
		Matrix m2 = new Matrix(1.3, 0.3, 
				               0.0, 4.5);
		Matrix m3 = m1.difference(m2);
		// test the values
		TestCase.assertEquals(-1.3, m3.m00);
		TestCase.assertEquals(1.7, m3.m01);
		TestCase.assertEquals(3.5, m3.m10);
		TestCase.assertEquals(-3.3, m3.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m3));
	}
	
	/**
	 * Tests the multiply matrix method.
	 */
	@Test
	public void multiplyMatrix() {
		Matrix m1 = new Matrix(1.0, 2.0, 
				               3.0, 4.0);
		Matrix m2 = new Matrix(4.0, 3.0, 
				               2.0, 1.0);
		m1.multiply(m2);
		TestCase.assertEquals(8.0, m1.m00);
		TestCase.assertEquals(5.0, m1.m01);
		TestCase.assertEquals(20.0, m1.m10);
		TestCase.assertEquals(13.0, m1.m11);
	}
	
	/**
	 * Tests the product matrix method.
	 */
	@Test
	public void productMatrix() {
		Matrix m1 = new Matrix(1.0, 2.0, 
				               3.0, 4.0);
		Matrix m2 = new Matrix(4.0, 3.0, 
				               2.0, 1.0);
		Matrix m3 = m1.product(m2);
		TestCase.assertEquals(8.0, m3.m00);
		TestCase.assertEquals(5.0, m3.m01);
		TestCase.assertEquals(20.0, m3.m10);
		TestCase.assertEquals(13.0, m3.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m3));
	}
	
	/** 
	 * Tests the multiply vector method.
	 */
	@Test
	public void multiplyVector() {
		Matrix m1 = new Matrix(1.0, 2.0, 
	                           3.0, 4.0);
		Vector v1 = new Vector(1.0, -1.0);
		m1.multiply(v1);
		TestCase.assertEquals(-1.0, v1.x);
		TestCase.assertEquals(-1.0, v1.y);
	}

	/** 
	 * Tests the product vector method.
	 */
	@Test
	public void productVector() {
		Matrix m1 = new Matrix(1.0, 2.0, 
	                           3.0, 4.0);
		Vector v1 = new Vector(1.0, -1.0);
		Vector v2 = m1.product(v1);
		TestCase.assertEquals(-1.0, v2.x);
		TestCase.assertEquals(-1.0, v2.y);
		// make sure we didnt modify the first vector
		TestCase.assertFalse(v1.equals(v2));
	}

	/** 
	 * Tests the multiply vector transpose method.
	 */
	@Test
	public void multiplyVectorT() {
		Matrix m1 = new Matrix(1.0, 2.0, 
	                           3.0, 4.0);
		Vector v1 = new Vector(1.0, -1.0);
		m1.multiplyT(v1);
		TestCase.assertEquals(-2.0, v1.x);
		TestCase.assertEquals(-2.0, v1.y);
	}

	/** 
	 * Tests the product vector transpose method.
	 */
	@Test
	public void productVectorT() {
		Matrix m1 = new Matrix(1.0, 2.0, 
	                           3.0, 4.0);
		Vector v1 = new Vector(1.0, -1.0);
		Vector v2 = m1.productT(v1);
		TestCase.assertEquals(-2.0, v2.x);
		TestCase.assertEquals(-2.0, v2.y);
		// make sure we didnt modify the first vector
		TestCase.assertFalse(v1.equals(v2));
	}
	
	/** 
	 * Tests the multiply by a scalar method.
	 */
	@Test
	public void multiplyScalar() {
		Matrix m1 = new Matrix(1.0, 2.0, 
	                           3.0, 4.0);
		m1.multiply(2.0);
		TestCase.assertEquals(2.0, m1.m00);
		TestCase.assertEquals(4.0, m1.m01);
		TestCase.assertEquals(6.0, m1.m10);
		TestCase.assertEquals(8.0, m1.m11);
	}

	/** 
	 * Tests the product by a scalar method.
	 */
	@Test
	public void productScalar() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                3.0, 4.0);
		Matrix m2 = m1.product(2.0);
		TestCase.assertEquals(2.0, m2.m00);
		TestCase.assertEquals(4.0, m2.m01);
		TestCase.assertEquals(6.0, m2.m10);
		TestCase.assertEquals(8.0, m2.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}

	/** 
	 * Tests the divide by a scalar method.
	 */
	@Test
	public void divide() {
		Matrix m1 = new Matrix(1.0, 2.0, 
	                           3.0, 4.0);
		m1.divide(2.0);
		TestCase.assertEquals(0.5, m1.m00);
		TestCase.assertEquals(1.0, m1.m01);
		TestCase.assertEquals(1.5, m1.m10);
		TestCase.assertEquals(2.0, m1.m11);
	}

	/** 
	 * Tests the quotient by a scalar method.
	 */
	@Test
	public void quotient() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		Matrix m2 = m1.quotient(2.0);
		TestCase.assertEquals(0.5, m2.m00);
		TestCase.assertEquals(1.0, m2.m01);
		TestCase.assertEquals(1.5, m2.m10);
		TestCase.assertEquals(2.0, m2.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Tests the identity method.
	 */
	@Test
	public void identity() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		m1.identity();
		TestCase.assertEquals(1.0, m1.m00);
		TestCase.assertEquals(0.0, m1.m01);
		TestCase.assertEquals(0.0, m1.m10);
		TestCase.assertEquals(1.0, m1.m11);
	}
	
	/**
	 * Tests the transpose method.
	 */
	@Test
	public void transpose() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		m1.transpose();
		TestCase.assertEquals(1.0, m1.m00);
		TestCase.assertEquals(3.0, m1.m01);
		TestCase.assertEquals(2.0, m1.m10);
		TestCase.assertEquals(4.0, m1.m11);
	}
	
	/**
	 * Tests the get transpose method.
	 */
	@Test
	public void getTranspose() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		Matrix m2 = m1.getTranspose();
		TestCase.assertEquals(1.0, m2.m00);
		TestCase.assertEquals(3.0, m2.m01);
		TestCase.assertEquals(2.0, m2.m10);
		TestCase.assertEquals(4.0, m2.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Tests the determinant method.
	 */
	@Test
	public void determinant() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		double det = m1.determinant();
		TestCase.assertEquals(-2.0, det);
	}
	
	/**
	 * Tests the invert method.
	 */
	@Test
	public void invert() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		m1.invert();
		TestCase.assertEquals(-2.0, m1.m00);
		TestCase.assertEquals(1.0, m1.m01);
		TestCase.assertEquals(1.5, m1.m10);
		TestCase.assertEquals(-0.5, m1.m11);
	}
	
	/**
	 * Tests the get inverse method.
	 */
	@Test
	public void getInverse() {
		Matrix m1 = new Matrix(1.0, 2.0, 
                               3.0, 4.0);
		Matrix m2 = m1.getInverse();
		TestCase.assertEquals(-2.0, m2.m00);
		TestCase.assertEquals(1.0, m2.m01);
		TestCase.assertEquals(1.5, m2.m10);
		TestCase.assertEquals(-0.5, m2.m11);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Tests the solve method.
	 */
	@Test
	public void solve() {
		Matrix A = new Matrix(3.0, -1.0, 
                			 -1.0, -1.0);
		Vector b = new Vector(2.0, 6.0);
		Vector x = A.solve(b);
		TestCase.assertEquals(-1.0, x.x);
		TestCase.assertEquals(-5.0, x.y);
	}
}
