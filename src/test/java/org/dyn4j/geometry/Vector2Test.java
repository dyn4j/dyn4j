/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Vector2} class.
 * @author William Bittle
 * @version 3.4.0
 * @since 1.0.0
 */
public class Vector2Test {
	/**
	 * Tests the create methods.
	 */
	@Test
	public void create() {
		Vector2 v1 = new Vector2();
		// should default to zero
		TestCase.assertEquals(0.0, v1.x);
		TestCase.assertEquals(0.0, v1.y);
		
		Vector2 v2 = new Vector2(1.0, 2.0);
		TestCase.assertEquals(1.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		
		Vector2 v3 = new Vector2(v2);
		TestCase.assertFalse(v3 == v2);
		TestCase.assertEquals(1.0, v3.x);
		TestCase.assertEquals(2.0, v3.y);
		
		Vector2 v4 = new Vector2(0.0, 1.0, 2.0, 3.0);
		TestCase.assertEquals(2.0, v4.x);
		TestCase.assertEquals(2.0, v4.y);
		
		Vector2 v5 = new Vector2(v2, v1);
		TestCase.assertEquals(-1.0, v5.x);
		TestCase.assertEquals(-2.0, v5.y);
		
		Vector2 v7 = new Vector2(Math.toRadians(30.0));
		TestCase.assertEquals(1.000, v7.getMagnitude(), 1.0E-4);
		TestCase.assertEquals(30.000, Math.toDegrees(v7.getDirection()), 1.0E-4);
		
		Vector2 v6 = Vector2.create(1.0, Math.toRadians(90));
		TestCase.assertEquals( 0.000, v6.x, 1.0e-3);
		TestCase.assertEquals( 1.000, v6.y, 1.0e-3);
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Vector2 v = new Vector2(1.0, 3.0);
		Vector2 vc = v.copy();
		
		TestCase.assertFalse(v == vc);
		TestCase.assertEquals(v.x, vc.x);
		TestCase.assertEquals(v.y, vc.y);
	}
	
	/**
	 * Tests the distance methods.
	 */
	@Test
	public void distance() {
		Vector2 v = new Vector2();
		
		TestCase.assertEquals(4.000, v.distanceSquared(2.0, 0.0), 1.0e-3);
		TestCase.assertEquals(5.000, v.distanceSquared(2.0, -1.0), 1.0e-3);
		TestCase.assertEquals(2.000, v.distance(2.0, 0.0), 1.0e-3);
		TestCase.assertEquals(5.000, v.distance(3.0, 4.0), 1.0e-3);
		
		TestCase.assertEquals(4.000, v.distanceSquared(new Vector2(2.0, 0.0)), 1.0e-3);
		TestCase.assertEquals(5.000, v.distanceSquared(new Vector2(2.0, -1.0)), 1.0e-3);
		TestCase.assertEquals(2.000, v.distance(new Vector2(2.0, 0.0)), 1.0e-3);
		TestCase.assertEquals(5.000, v.distance(new Vector2(3.0, 4.0)), 1.0e-3);
	}
	
	/**
	 * Tests the distance(double,double) method for bug found in versions 1.1.0 to 3.1.7.
	 * @since 3.1.8
	 */
	@Test
	public void distanceBugInVersions_1_1_0_to_3_1_7() {
		Vector2 v = new Vector2(1.0, 2.0);
		TestCase.assertEquals(2.236, v.getMagnitude(), 1.0e-3);
		TestCase.assertEquals(2.236, v.distance(2.0, 0.0), 1.0e-3);
		TestCase.assertEquals(0.000, v.distance(1.0, 2.0), 1.0e-3);
		TestCase.assertEquals(1.414, v.distance(2.0, 1.0), 1.0e-3);
		TestCase.assertEquals(4.242, v.distance(-2.0, -1.0), 1.0e-3);
	}
	
	/**
	 * Tests the triple product method.
	 */
	@Test
	public void tripleProduct() {
		Vector2 v1 = new Vector2(1.0, 1.0);
		Vector2 v2 = new Vector2(1.0, -1.0);
		
		Vector2 r = Vector2.tripleProduct(v1, v2, v2);
		
		// the below would be -1.0 if the vectors were normalized
		TestCase.assertEquals(-2.000, r.x, 1.0e-3);
		TestCase.assertEquals(-2.000, r.y, 1.0e-3);
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void equals() {
		Vector2 v = new Vector2(1.0, 2.0);
		
		TestCase.assertTrue(v.equals(v));
		TestCase.assertTrue(v.equals(v.copy()));
		TestCase.assertTrue(v.equals(new Vector2(1.0, 2.0)));
		TestCase.assertTrue(v.equals(1.0, 2.0));
		
		TestCase.assertFalse(v.equals(v.copy().set(2.0, 1.0)));
		TestCase.assertFalse(v.equals(2.0, 2.0));
	}
	
	/**
	 * Tests the set methods.
	 */
	@Test
	public void set() {
		Vector2 v = new Vector2();
		
		Vector2 v2 = new Vector2(1.0, -3.0);
		v.set(v2);
		
		TestCase.assertFalse(v == v2);
		TestCase.assertEquals(1.0, v.x);
		TestCase.assertEquals(-3.0, v.y);
		
		v.set(-1.0, 0.0);
		TestCase.assertEquals(-1.0, v.x);
		TestCase.assertEquals( 0.0, v.y);
		
		v.setDirection(Math.toRadians(90));
		TestCase.assertEquals( 0.0, v.x, 1E-10);
		TestCase.assertEquals( 1.0, v.y);
		
		v.setMagnitude(3.0);
		TestCase.assertEquals( 0.0, v.x, 1E-10);
		TestCase.assertEquals( 3.0, v.y);
	}
	
	/**
	 * Tests the get methods.
	 */
	@Test
	public void get() {
		Vector2 v = new Vector2(3.0, 4.0);
		
		Vector2 x = v.getXComponent();
		Vector2 y = v.getYComponent();
		
		TestCase.assertEquals(3.0, x.x);
		TestCase.assertEquals(0.0, x.y);
		TestCase.assertEquals(0.0, y.x);
		TestCase.assertEquals(4.0, y.y);
		
		TestCase.assertEquals(5.000, v.getMagnitude(), 1.0e-3);
		TestCase.assertEquals(25.000, v.getMagnitudeSquared(), 1.0e-3);
		TestCase.assertEquals(53.130, Math.toDegrees(v.getDirection()), 1.0e-3);
		
		Vector2 v2 = new Vector2(-4.0, 3.0);
		TestCase.assertEquals(90.000, Math.toDegrees(v.getAngleBetween(v2)), 1.0e-3);
		
		v2 = v.getLeftHandOrthogonalVector();
		TestCase.assertEquals( 4.0, v2.x);
		TestCase.assertEquals(-3.0, v2.y);
		
		v2 = v.getRightHandOrthogonalVector();
		TestCase.assertEquals(-4.0, v2.x);
		TestCase.assertEquals( 3.0, v2.y);
		
		v2 = v.getNegative();
		TestCase.assertEquals(-3.0, v2.x);
		TestCase.assertEquals(-4.0, v2.y);
		
		v2 = v.getNormalized();
		TestCase.assertEquals(0.600, v2.x, 1.0e-3);
		TestCase.assertEquals(0.800, v2.y, 1.0e-3);
	}
	
	/**
	 * Tests the add and sum methods.
	 */
	@Test
	public void add() {
		Vector2 v1 = new Vector2(1.0, 2.0);
		Vector2 v2 = new Vector2(-2.0, 1.0);
		
		Vector2 v3 = v1.sum(v2);
		TestCase.assertEquals(-1.0, v3.x);
		TestCase.assertEquals( 3.0, v3.y);
		
		v3 = v1.sum(3.0, -7.5);
		TestCase.assertEquals( 4.0, v3.x);
		TestCase.assertEquals(-5.5, v3.y);
		
		v1.add(v2);
		TestCase.assertEquals(-1.0, v1.x);
		TestCase.assertEquals( 3.0, v1.y);
		
		v1.add(-2.0, 1.0);
		TestCase.assertEquals(-3.0, v1.x);
		TestCase.assertEquals( 4.0, v1.y);
	}
	
	/**
	 * Tests the subtact and difference methods.
	 */
	@Test
	public void subtract() {
		Vector2 v1 = new Vector2(1.0, 2.0);
		Vector2 v2 = new Vector2(-2.0, 1.0);
		
		Vector2 v3 = v1.difference(v2);
		TestCase.assertEquals( 3.0, v3.x);
		TestCase.assertEquals( 1.0, v3.y);
		
		v3 = v1.difference(3.0, -7.5);
		TestCase.assertEquals(-2.0, v3.x);
		TestCase.assertEquals( 9.5, v3.y);
		
		v1.subtract(v2);
		TestCase.assertEquals( 3.0, v1.x);
		TestCase.assertEquals( 1.0, v1.y);
		
		v1.subtract(-2.0, 1.0);
		TestCase.assertEquals( 5.0, v1.x);
		TestCase.assertEquals( 0.0, v1.y);
	}
	
	/**
	 * Tests the to method.
	 */
	@Test
	public void to() {
		Vector2 p1 = new Vector2(1.0, 1.0);
		Vector2 p2 = new Vector2(0.0, 1.0);
		
		Vector2 r = p1.to(p2);
		
		TestCase.assertEquals(-1.0, r.x);
		TestCase.assertEquals( 0.0, r.y);
		
		r = p1.to(2.0, 0.0);
		
		TestCase.assertEquals( 1.0, r.x);
		TestCase.assertEquals(-1.0, r.y);
	}

	/**
	 * Tests the multiply and product methods.
	 */
	@Test
	public void multiply() {
		Vector2 v1 = new Vector2(2.0, 1.0);
		
		Vector2 r = v1.product(-1.5);
		TestCase.assertEquals(-3.0, r.x);
		TestCase.assertEquals(-1.5, r.y);
		
		v1.multiply(-1.5);
		TestCase.assertEquals(-3.0, v1.x);
		TestCase.assertEquals(-1.5, v1.y);
	}
	
	/**
	 * Tests the divide and quotient methods.
	 */
	@Test
	public void divide() {
		Vector2 v1 = new Vector2(2.0, 1.0);

		Vector2 r = v1.quotient(-2.0);
		TestCase.assertEquals(-1.0, r.x);
		TestCase.assertEquals(-0.5, r.y);
		
		v1.divide(-2.0);
		TestCase.assertEquals(-1.0, r.x);
		TestCase.assertEquals(-0.5, r.y);
	}
	
	/**
	 * Tests the dot method.
	 */
	@Test
	public void dot() {
		Vector2 v1 = new Vector2(1.0, 1.0);
		Vector2 v2 = new Vector2(0.0, 1.0);
		
		TestCase.assertEquals(1.0, v1.dot(v2));
		// test a perpendicular vector
		TestCase.assertEquals(0.0, v1.dot(v1.getLeftHandOrthogonalVector()));
		TestCase.assertEquals(v1.getMagnitudeSquared(), v1.dot(v1));
		
		TestCase.assertEquals(1.0, v1.dot(0.0, 1.0));
		// test a perpendicular vector
		TestCase.assertEquals(0.0, v1.dot(-1.0, 1.0));
		TestCase.assertEquals(2.0, v1.dot(1.0, 1.0));
	}
	
	/**
	 * Tests the cross product methods.
	 */
	@Test
	public void cross() {
		Vector2 v1 = new Vector2(1.0, 1.0);
		Vector2 v2 = new Vector2(0.0, 1.0);
		
		TestCase.assertEquals(0.0, v1.cross(v1));
		TestCase.assertEquals(1.0, v1.cross(v2));
		TestCase.assertEquals(-2.0, v1.cross(v1.getLeftHandOrthogonalVector()));
		
		TestCase.assertEquals(0.0, v1.cross(1.0, 1.0));
		TestCase.assertEquals(1.0, v1.cross(0.0, 1.0));
		TestCase.assertEquals(2.0, v1.cross(-1.0, 1.0));
		
		Vector2 r = v1.cross(3.0);
		
		TestCase.assertEquals(-3.0, r.x);
		TestCase.assertEquals( 3.0, r.y);
	}
	
	/**
	 * Tests the isOrthoganal method.
	 */
	@Test
	public void isOrthogonal() {
		Vector2 v1 = new Vector2(1.0, 1.0);
		Vector2 v2 = new Vector2(0.0, 1.0);
		
		TestCase.assertFalse(v1.isOrthogonal(v2));
		TestCase.assertTrue(v1.isOrthogonal(v1.getLeftHandOrthogonalVector()));
		TestCase.assertTrue(v1.isOrthogonal(v1.getRightHandOrthogonalVector()));
		TestCase.assertFalse(v1.isOrthogonal(v1));
		
		TestCase.assertFalse(v1.isOrthogonal(0.0, 1.0));
		TestCase.assertTrue(v1.isOrthogonal(1.0, -1.0));
		TestCase.assertTrue(v1.isOrthogonal(-1.0, 1.0));
		TestCase.assertFalse(v1.isOrthogonal(1.0, 1.0));
	}
	
	/**
	 * Tests the isZero method.
	 */
	@Test
	public void isZero() {
		Vector2 v = new Vector2();
		
		TestCase.assertTrue(v.isZero());
		
		v.set(1.0, 0.0);
		TestCase.assertFalse(v.isZero());
		
		v.set(1.0, 1.0);
		TestCase.assertFalse(v.isZero());
		
		v.set(0.0, 1.0);
		TestCase.assertFalse(v.isZero());
	}
	
	/**
	 * Tests the negate method.
	 */
	@Test
	public void negate() {
		Vector2 v = new Vector2(1.0, -6.0);
		
		v.negate();
		TestCase.assertEquals(-1.0, v.x);
		TestCase.assertEquals( 6.0, v.y);
	}
	
	/**
	 * Tests the zero method.
	 */
	@Test
	public void zero() {
		Vector2 v = new Vector2(1.0, -2.0);
		
		v.zero();
		TestCase.assertEquals( 0.0, v.x);
		TestCase.assertEquals( 0.0, v.y);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		Vector2 v = new Vector2(2.0, 1.0);
		
		v.rotate(Math.toRadians(90));
		TestCase.assertEquals(-1.000, v.x, 1.0e-3);
		TestCase.assertEquals( 2.000, v.y, 1.0e-3);
		
		v.rotate(new Rotation(Math.toRadians(60)), 0.0, 1.0);
		TestCase.assertEquals(-1.366, v.x, 1.0e-3);
		TestCase.assertEquals( 0.634, v.y, 1.0e-3);
		
		v.inverseRotate(new Rotation(Math.toRadians(60)), 0.0, 1.0);
		TestCase.assertEquals(-1.000, v.x, 1.0e-3);
		TestCase.assertEquals( 2.000, v.y, 1.0e-3);
		
		v.inverseRotate(Math.toRadians(90));
		TestCase.assertEquals(2.0, v.x, 1.0e-3);
		TestCase.assertEquals(1.0, v.y, 1.0e-3);
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Vector2 v1 = new Vector2(1.0, 1.0);
		Vector2 v2 = new Vector2(0.5, 1.0);
		
		Vector2 r = v1.project(v2);
		
		TestCase.assertEquals( 0.600, r.x, 1.0e-3);
		TestCase.assertEquals( 1.200, r.y, 1.0e-3);
	}
	
	/**
	 * Tests the left method.
	 */
	@Test
	public void left() {
		Vector2 v = new Vector2(11.0, 2.5);
		v.left();
		
		TestCase.assertEquals( 2.5, v.x);
		TestCase.assertEquals( -11.0, v.y);
	}
	
	/**
	 * Tests the right method.
	 */
	@Test
	public void right() {
		Vector2 v = new Vector2(11.0, 2.5);
		v.right();
		
		TestCase.assertEquals( -2.5, v.x);
		TestCase.assertEquals( 11.0, v.y);
	}
	
	/**
	 * Tests the normalize method.
	 */
	@Test
	public void normalize() {
		Vector2 v = new Vector2(3.0, 4.0);
		v.normalize();
		
		TestCase.assertEquals( 3.0 / 5.0, v.x, 1.0e-3);
		TestCase.assertEquals( 4.0 / 5.0, v.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAngleBetween method for the range.
	 * @since 3.1.5
	 */
	@Test
	public void getAngleBetweenRange() {
		Vector2 v1 = new Vector2(-1.0, 2.0);
		Vector2 v2 = new Vector2(-2.0, -1.0);
		
		// this should return in the range of -pi,pi
		TestCase.assertTrue(Math.PI >= Math.abs(v1.getAngleBetween(v2)));
	}
}
