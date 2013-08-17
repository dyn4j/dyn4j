/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import junit.framework.TestCase;

import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Matrix33;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;
import org.junit.Test;

/**
 * Test cases for the {@link Matrix22} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class Matrix33Test {
	/**
	 * Test the creation method passing four doubles.
	 */
	@Test
	public void createFull() {
		Matrix33 m = new Matrix33(1.0, 2.0, 1.0,
				                 -3.0, 8.0, 2.0,
				                  1.0, 5.0, -1.0);
		TestCase.assertEquals(1.0, m.m00);
		TestCase.assertEquals(2.0, m.m01);
		TestCase.assertEquals(1.0, m.m02);
		TestCase.assertEquals(-3.0, m.m10);
		TestCase.assertEquals(8.0, m.m11);
		TestCase.assertEquals(2.0, m.m12);
		TestCase.assertEquals(1.0, m.m20);
		TestCase.assertEquals(5.0, m.m21);
		TestCase.assertEquals(-1.0, m.m22);
	}
	
	/**
	 * Test the creation method passing a double array.
	 */
	@Test
	public void createFullArray() {
		Matrix33 m = new Matrix33(new double[] {1.0, 2.0, 1.0,
                                               -3.0, 8.0, 2.0,
                                                1.0, 5.0, -1.0});
		TestCase.assertEquals(1.0, m.m00);
		TestCase.assertEquals(2.0, m.m01);
		TestCase.assertEquals(1.0, m.m02);
		TestCase.assertEquals(-3.0, m.m10);
		TestCase.assertEquals(8.0, m.m11);
		TestCase.assertEquals(2.0, m.m12);
		TestCase.assertEquals(1.0, m.m20);
		TestCase.assertEquals(5.0, m.m21);
		TestCase.assertEquals(-1.0, m.m22);
	}
	
	/**
	 * Tests the copy constructor.
	 */
	@Test
	public void copy() {
		Matrix33 m1 = new Matrix33(0.0, 1.0, -1.0,
				                   2.0, -1.0, 3.0,
				                   3.0, 2.5, 0.5);
		
		// make a copy
		Matrix33 m2 = new Matrix33(m1);
		// test the values
		TestCase.assertEquals(m2.m00, m1.m00);
		TestCase.assertEquals(m2.m01, m1.m01);
		TestCase.assertEquals(m2.m02, m1.m02);
		TestCase.assertEquals(m2.m10, m1.m10);
		TestCase.assertEquals(m2.m11, m1.m11);
		TestCase.assertEquals(m2.m12, m1.m12);
		TestCase.assertEquals(m2.m20, m1.m20);
		TestCase.assertEquals(m2.m21, m1.m21);
		TestCase.assertEquals(m2.m22, m1.m22);
	}
	
	/**
	 * Tests the add method.
	 */
	@Test
	public void add() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
				                   3.0, 1.0, 1.0,
				                   2.0, 0.0, -1.0);
		Matrix33 m2 = new Matrix33(1.0, 1.0, 3.0,
				                   0.0, 4.0, 1.0,
				                   2.0, 2.0, 1.0);
		m1.add(m2);
		// test the values
		TestCase.assertEquals(1.0, m1.m00);
		TestCase.assertEquals(3.0, m1.m01);
		TestCase.assertEquals(3.0, m1.m02);
		TestCase.assertEquals(3.0, m1.m10);
		TestCase.assertEquals(5.0, m1.m11);
		TestCase.assertEquals(2.0, m1.m12);
		TestCase.assertEquals(4.0, m1.m20);
		TestCase.assertEquals(2.0, m1.m21);
		TestCase.assertEquals(0.0, m1.m22);
	}

	/**
	 * Tests the sum method.
	 */
	@Test
	public void sum() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = new Matrix33(1.0, 1.0, 3.0,
		                           0.0, 4.0, 1.0,
		                           2.0, 2.0, 1.0);
		Matrix33 m3 = m1.sum(m2);
		// test the values
		TestCase.assertEquals(1.0, m3.m00);
		TestCase.assertEquals(3.0, m3.m01);
		TestCase.assertEquals(3.0, m3.m02);
		TestCase.assertEquals(3.0, m3.m10);
		TestCase.assertEquals(5.0, m3.m11);
		TestCase.assertEquals(2.0, m3.m12);
		TestCase.assertEquals(4.0, m3.m20);
		TestCase.assertEquals(2.0, m3.m21);
		TestCase.assertEquals(0.0, m3.m22);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m3));
	}

	/**
	 * Tests the subtract method.
	 */
	@Test
	public void subtract() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = new Matrix33(1.0, 1.0, 3.0,
		                           0.0, 4.0, 1.0,
		                           2.0, 2.0, 1.0);
		m1.subtract(m2);
		// test the values
		TestCase.assertEquals(-1.0, m1.m00);
		TestCase.assertEquals( 1.0, m1.m01);
		TestCase.assertEquals(-3.0, m1.m02);
		TestCase.assertEquals( 3.0, m1.m10);
		TestCase.assertEquals(-3.0, m1.m11);
		TestCase.assertEquals( 0.0, m1.m12);
		TestCase.assertEquals( 0.0, m1.m20);
		TestCase.assertEquals(-2.0, m1.m21);
		TestCase.assertEquals(-2.0, m1.m22);
	}

	/**
	 * Tests the difference method.
	 */
	@Test
	public void difference() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = new Matrix33(1.0, 1.0, 3.0,
		                           0.0, 4.0, 1.0,
		                           2.0, 2.0, 1.0);
		Matrix33 m3 = m1.difference(m2);
		// test the values
		TestCase.assertEquals(-1.0, m3.m00);
		TestCase.assertEquals( 1.0, m3.m01);
		TestCase.assertEquals(-3.0, m3.m02);
		TestCase.assertEquals( 3.0, m3.m10);
		TestCase.assertEquals(-3.0, m3.m11);
		TestCase.assertEquals( 0.0, m3.m12);
		TestCase.assertEquals( 0.0, m3.m20);
		TestCase.assertEquals(-2.0, m3.m21);
		TestCase.assertEquals(-2.0, m3.m22);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m3));
	}
	
	/**
	 * Tests the multiply matrix method.
	 */
	@Test
	public void multiplyMatrix() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = new Matrix33(1.0, 1.0, 3.0,
		                           0.0, 4.0, 1.0,
		                           2.0, 2.0, 1.0);
		m1.multiply(m2);
		TestCase.assertEquals(0.0, m1.m00);
		TestCase.assertEquals(8.0, m1.m01);
		TestCase.assertEquals(2.0, m1.m02);
		TestCase.assertEquals(5.0, m1.m10);
		TestCase.assertEquals(9.0, m1.m11);
		TestCase.assertEquals(11.0, m1.m12);
		TestCase.assertEquals(0.0, m1.m20);
		TestCase.assertEquals(0.0, m1.m21);
		TestCase.assertEquals(5.0, m1.m22);
	}
	
	/**
	 * Tests the product matrix method.
	 */
	@Test
	public void productMatrix() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = new Matrix33(1.0, 1.0, 3.0,
		                           0.0, 4.0, 1.0,
		                           2.0, 2.0, 1.0);
		Matrix33 m3 = m1.product(m2);
		TestCase.assertEquals(0.0, m3.m00);
		TestCase.assertEquals(8.0, m3.m01);
		TestCase.assertEquals(2.0, m3.m02);
		TestCase.assertEquals(5.0, m3.m10);
		TestCase.assertEquals(9.0, m3.m11);
		TestCase.assertEquals(11.0, m3.m12);
		TestCase.assertEquals(0.0, m3.m20);
		TestCase.assertEquals(0.0, m3.m21);
		TestCase.assertEquals(5.0, m3.m22);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m3));
	}
	
	/** 
	 * Tests the multiply vector method.
	 */
	@Test
	public void multiplyVector() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
				                   3.0, 1.0, 1.0,
				                   2.0, 0.0, -1.0);
		Vector3 v1 = new Vector3(1.0, -1.0, 2.0);
		
		m1.multiply(v1);
		TestCase.assertEquals(-2.0, v1.x);
		TestCase.assertEquals( 4.0, v1.y);
		TestCase.assertEquals( 0.0, v1.z);
	}

	/** 
	 * Tests the product vector method.
	 */
	@Test
	public void productVector() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Vector3 v1 = new Vector3(1.0, -1.0, 2.0);
		
		Vector3 v2 = m1.product(v1);
		TestCase.assertEquals(-2.0, v2.x);
		TestCase.assertEquals( 4.0, v2.y);
		TestCase.assertEquals( 0.0, v2.z);
		// make sure we didnt modify the first vector
		TestCase.assertFalse(v1.equals(v2));
	}

	/** 
	 * Tests the multiply vector transpose method.
	 */
	@Test
	public void multiplyVectorT() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Vector3 v1 = new Vector3(1.0, -1.0, 2.0);
		
		m1.multiplyT(v1);
		TestCase.assertEquals( 1.0, v1.x);
		TestCase.assertEquals( 1.0, v1.y);
		TestCase.assertEquals(-3.0, v1.z);
	}

	/** 
	 * Tests the product vector transpose method.
	 */
	@Test
	public void productVectorT() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
                                   3.0, 1.0, 1.0,
                                   2.0, 0.0, -1.0);
		Vector3 v1 = new Vector3(1.0, -1.0, 2.0);
		
		Vector3 v2 = m1.productT(v1);
		TestCase.assertEquals( 1.0, v2.x);
		TestCase.assertEquals( 1.0, v2.y);
		TestCase.assertEquals(-3.0, v2.z);
		// make sure we didnt modify the first vector
		TestCase.assertFalse(v1.equals(v2));
	}
	
	/** 
	 * Tests the multiply by a scalar method.
	 */
	@Test
	public void multiplyScalar() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
                                   3.0, 1.0, 1.0,
                                   2.0, 0.0, -1.0);
		m1.multiply(2.0);
		TestCase.assertEquals(0.0, m1.m00);
		TestCase.assertEquals(4.0, m1.m01);
		TestCase.assertEquals(0.0, m1.m02);
		TestCase.assertEquals(6.0, m1.m10);
		TestCase.assertEquals(2.0, m1.m11);
		TestCase.assertEquals(2.0, m1.m12);
		TestCase.assertEquals(4.0, m1.m20);
		TestCase.assertEquals(0.0, m1.m21);
		TestCase.assertEquals(-2.0, m1.m22);
	}

	/** 
	 * Tests the product by a scalar method.
	 */
	@Test
	public void productScalar() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = m1.product(2.0);
		TestCase.assertEquals(0.0, m2.m00);
		TestCase.assertEquals(4.0, m2.m01);
		TestCase.assertEquals(0.0, m2.m02);
		TestCase.assertEquals(6.0, m2.m10);
		TestCase.assertEquals(2.0, m2.m11);
		TestCase.assertEquals(2.0, m2.m12);
		TestCase.assertEquals(4.0, m2.m20);
		TestCase.assertEquals(0.0, m2.m21);
		TestCase.assertEquals(-2.0, m2.m22);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Tests the identity method.
	 */
	@Test
	public void identity() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
                                   3.0, 1.0, 1.0,
                                   2.0, 0.0, -1.0);
		m1.identity();
		TestCase.assertEquals(1.0, m1.m00);
		TestCase.assertEquals(0.0, m1.m01);
		TestCase.assertEquals(0.0, m1.m02);
		TestCase.assertEquals(0.0, m1.m10);
		TestCase.assertEquals(1.0, m1.m11);
		TestCase.assertEquals(0.0, m1.m12);
		TestCase.assertEquals(0.0, m1.m20);
		TestCase.assertEquals(0.0, m1.m21);
		TestCase.assertEquals(1.0, m1.m22);
	}
	
	/**
	 * Tests the transpose method.
	 */
	@Test
	public void transpose() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
                                   3.0, 1.0, 1.0,
                                   2.0, 0.0, -1.0);
		m1.transpose();
		TestCase.assertEquals(0.0, m1.m00);
		TestCase.assertEquals(3.0, m1.m01);
		TestCase.assertEquals(2.0, m1.m02);
		TestCase.assertEquals(2.0, m1.m10);
		TestCase.assertEquals(1.0, m1.m11);
		TestCase.assertEquals(0.0, m1.m12);
		TestCase.assertEquals(0.0, m1.m20);
		TestCase.assertEquals(1.0, m1.m21);
		TestCase.assertEquals(-1.0, m1.m22);
	}
	
	/**
	 * Tests the get transpose method.
	 */
	@Test
	public void getTranspose() {
		Matrix33 m1 = new Matrix33(0.0, 2.0, 0.0,
		                           3.0, 1.0, 1.0,
		                           2.0, 0.0, -1.0);
		Matrix33 m2 = m1.getTranspose();
		TestCase.assertEquals(0.0, m2.m00);
		TestCase.assertEquals(3.0, m2.m01);
		TestCase.assertEquals(2.0, m2.m02);
		TestCase.assertEquals(2.0, m2.m10);
		TestCase.assertEquals(1.0, m2.m11);
		TestCase.assertEquals(0.0, m2.m12);
		TestCase.assertEquals(0.0, m2.m20);
		TestCase.assertEquals(1.0, m2.m21);
		TestCase.assertEquals(-1.0, m2.m22);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Tests the determinant method.
	 */
	@Test
	public void determinant() {
		Matrix33 m1 = new Matrix33(1.0, 0.0, 5.0,
                                   2.0, 1.0, 6.0,
                                   3.0, 4.0, 0.0);
		double det = m1.determinant();
		TestCase.assertEquals(1.0, det);
	}
	
	/**
	 * Tests the invert method.
	 */
	@Test
	public void invert() {
		Matrix33 m1 = new Matrix33(1.0, 0.0, 5.0,
                                   2.0, 1.0, 6.0,
                                   3.0, 4.0, 0.0);
		m1.invert();
		//-24.0 20.0 -5.0 18.0 -15.0 4.0 5.0 -4.0 1.0
		TestCase.assertEquals(-24.0, m1.m00);
		TestCase.assertEquals( 20.0, m1.m01);
		TestCase.assertEquals( -5.0, m1.m02);
		TestCase.assertEquals( 18.0, m1.m10);
		TestCase.assertEquals(-15.0, m1.m11);
		TestCase.assertEquals(  4.0, m1.m12);
		TestCase.assertEquals(  5.0, m1.m20);
		TestCase.assertEquals( -4.0, m1.m21);
		TestCase.assertEquals(  1.0, m1.m22);
	}
	
	/**
	 * Tests the get inverse method.
	 */
	@Test
	public void getInverse() {
		Matrix33 m1 = new Matrix33(1.0, 0.0, 5.0,
		                           2.0, 1.0, 6.0,
		                           3.0, 4.0, 0.0);
		Matrix33 m2 = m1.getInverse();
		//-24.0 20.0 -5.0 18.0 -15.0 4.0 5.0 -4.0 1.0
		TestCase.assertEquals(-24.0, m2.m00);
		TestCase.assertEquals( 20.0, m2.m01);
		TestCase.assertEquals( -5.0, m2.m02);
		TestCase.assertEquals( 18.0, m2.m10);
		TestCase.assertEquals(-15.0, m2.m11);
		TestCase.assertEquals(  4.0, m2.m12);
		TestCase.assertEquals(  5.0, m2.m20);
		TestCase.assertEquals( -4.0, m2.m21);
		TestCase.assertEquals(  1.0, m2.m22);
		// make sure we didnt modify the first matrix
		TestCase.assertFalse(m1.equals(m2));
	}
	
	/**
	 * Tests the solve method.
	 */
	@Test
	public void solve22() {
		Matrix33 A = new Matrix33(3.0, -1.0, 0.0,
                			     -1.0, -1.0, 0.0,
                			      0.0,  0.0, 0.0);
		Vector2 b = new Vector2(2.0, 6.0);
		Vector2 x = A.solve22(b);
		TestCase.assertEquals(-1.0, x.x);
		TestCase.assertEquals(-5.0, x.y);
	}
	
	/**
	 * Tests the solve method.
	 */
	@Test
	public void solve33() {
		Matrix33 A = new Matrix33(1.0, -3.0, 3.0,
                                  2.0, 3.0, -1.0,
                                  4.0, -3.0, -1.0);
		Vector3 b = new Vector3(-4.0, 15.0, 19.0);
		Vector3 x = A.solve33(b);
		//(5.0, 1.0, -2.0)
		TestCase.assertEquals( 5.0, x.x);
		TestCase.assertEquals( 1.0, x.y);
		TestCase.assertEquals(-2.0, x.z);
	}
}
