/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Vector3} class.
 * @author William Bittle
 * @version 5.0.2
 * @since 1.0.0
 */
public class Vector3Test {
	/**
	 * Tests the create methods.
	 */
	@Test
	public void create() {
		Vector3 v1 = new Vector3();
		// should default to zero
		TestCase.assertEquals(0.0, v1.x);
		TestCase.assertEquals(0.0, v1.y);
		TestCase.assertEquals(0.0, v1.z);
		TestCase.assertNotNull(v1.toString());
		
		Vector3 v2 = new Vector3(1.0, 2.0, 3.0);
		TestCase.assertEquals(1.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(3.0, v2.z);
		
		Vector3 v3 = new Vector3(v2);
		TestCase.assertFalse(v3 == v2);
		TestCase.assertEquals(1.0, v3.x);
		TestCase.assertEquals(2.0, v3.y);
		TestCase.assertEquals(3.0, v3.z);
		
		Vector3 v4 = new Vector3(0.0, 1.0, 1.0, 2.0, 3.0, 1.0);
		TestCase.assertEquals(2.0, v4.x);
		TestCase.assertEquals(2.0, v4.y);
		TestCase.assertEquals(0.0, v4.z);
		
		Vector3 v5 = new Vector3(v2, v1);
		TestCase.assertEquals(-1.0, v5.x);
		TestCase.assertEquals(-2.0, v5.y);
		TestCase.assertEquals(-3.0, v5.z);
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Vector3 v = new Vector3(1.0, 3.0, 2.0);
		Vector3 vc = v.copy();
		
		TestCase.assertFalse(v == vc);
		TestCase.assertEquals(v.x, vc.x);
		TestCase.assertEquals(v.y, vc.y);
		TestCase.assertEquals(v.z, vc.z);
	}
	
	/**
	 * Tests the distance methods.
	 */
	@Test
	public void distance() {
		Vector3 v = new Vector3();
		
		TestCase.assertEquals(4.000, v.distanceSquared(2.0, 0.0, 0.0), 1.0e-3);
		TestCase.assertEquals(9.000, v.distanceSquared(2.0, -1.0, 2.0), 1.0e-3);
		TestCase.assertEquals(2.000, v.distance(2.0, 0.0, 0.0), 1.0e-3);
		TestCase.assertEquals(3.000, v.distance(2.0, -1.0, 2.0), 1.0e-3);
		
		TestCase.assertEquals(4.000, v.distanceSquared(new Vector3(2.0, 0.0, 0.0)), 1.0e-3);
		TestCase.assertEquals(9.000, v.distanceSquared(new Vector3(2.0, -1.0, 2.0)), 1.0e-3);
		TestCase.assertEquals(2.000, v.distance(new Vector3(2.0, 0.0, 0.0)), 1.0e-3);
		TestCase.assertEquals(3.000, v.distance(new Vector3(2.0, -1.0, 2.0)), 1.0e-3);
	}
	
	/**
	 * Tests the triple product method.
	 */
	@Test
	public void tripleProduct() {
		Vector3 v1 = new Vector3(1.0, 1.0, 0.0);
		Vector3 v2 = new Vector3(0.0, -1.0, 1.0);
		
		Vector3 r = Vector3.tripleProduct(v1, v2, v2);
		
		TestCase.assertEquals(-2.000, r.x, 1.0e-3);
		TestCase.assertEquals(-1.000, r.y, 1.0e-3);
		TestCase.assertEquals(-1.000, r.z, 1.0e-3);
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void equals() {
		Vector3 v = new Vector3(1.0, 2.0, -1.0);
		
		TestCase.assertTrue(v.equals(v));
		TestCase.assertEquals(v.hashCode(), v.hashCode());
		TestCase.assertTrue(v.equals(v.copy()));
		TestCase.assertEquals(v.hashCode(), v.copy().hashCode());
		TestCase.assertTrue(v.equals(new Vector3(1.0, 2.0, -1.0)));
		TestCase.assertEquals(v.hashCode(), new Vector3(1.0, 2.0, -1.0).hashCode());
		TestCase.assertTrue(v.equals(1.0, 2.0, -1.0));
		
		TestCase.assertFalse(v.equals(v.copy().set(2.0, 1.0, -1.0)));
		TestCase.assertFalse(v.hashCode() == new Vector3(2.0, 1.0, -1.0).hashCode());
		TestCase.assertFalse(v.equals(2.0, 2.0, 3.0));
		TestCase.assertFalse(v.hashCode() == new Vector3(2.0, 2.0, 3.0).hashCode());
		
		TestCase.assertFalse(v.equals(null));
		TestCase.assertFalse(v.equals((Object)null));
		TestCase.assertFalse(v.equals(new Object()));
		TestCase.assertFalse(v.equals((Object)new Vector3(1, 1, 1)));
		TestCase.assertTrue(v.equals((Object)new Vector3(1, 2, -1)));
	}
	
	/**
	 * Tests the set methods.
	 */
	@Test
	public void set() {
		Vector3 v = new Vector3();
		
		Vector3 v2 = new Vector3(1.0, -3.0, 2.0);
		v.set(v2);
		
		TestCase.assertFalse(v == v2);
		TestCase.assertEquals(1.0, v.x);
		TestCase.assertEquals(-3.0, v.y);
		TestCase.assertEquals(2.0, v.z);
		
		v.set(-1.0, 0.0, 0.0);
		TestCase.assertEquals(-1.0, v.x);
		TestCase.assertEquals( 0.0, v.y);
		TestCase.assertEquals( 0.0, v.z);
		
		v.setMagnitude(3.0);
		TestCase.assertEquals(-3.0, v.x, 1.0e-3);
		TestCase.assertEquals( 0.0, v.y);
		TestCase.assertEquals( 0.0, v.z);
		
		v.setMagnitude(0.0);
		TestCase.assertEquals( 0.0, v.x, 1.0e-3);
		TestCase.assertEquals( 0.0, v.y);
		TestCase.assertEquals( 0.0, v.z);
	}
	
	/**
	 * Tests the get methods.
	 */
	@Test
	public void get() {
		Vector3 v = new Vector3(2.0, 1.0, -2.0);
		
		Vector3 x = v.getXComponent();
		Vector3 y = v.getYComponent();
		Vector3 z = v.getZComponent();
		
		TestCase.assertEquals(2.0, x.x);
		TestCase.assertEquals(0.0, x.y);
		TestCase.assertEquals(0.0, x.z);
		
		TestCase.assertEquals(0.0, y.x);
		TestCase.assertEquals(1.0, y.y);
		TestCase.assertEquals(0.0, y.z);
		
		TestCase.assertEquals( 0.0, z.x);
		TestCase.assertEquals( 0.0, z.y);
		TestCase.assertEquals(-2.0, z.z);
		
		TestCase.assertEquals(3.000, v.getMagnitude(), 1.0e-3);
		TestCase.assertEquals(9.000, v.getMagnitudeSquared(), 1.0e-3);
		
		Vector3 v2 = v.getNegative();
		TestCase.assertEquals(-2.0, v2.x);
		TestCase.assertEquals(-1.0, v2.y);
		TestCase.assertEquals( 2.0, v2.z);
		
		v2 = v.getNormalized();
		TestCase.assertEquals( 0.666, v2.x, 1.0e-3);
		TestCase.assertEquals( 0.333, v2.y, 1.0e-3);
		TestCase.assertEquals(-0.666, v2.z, 1.0e-3);
		
		v.set(0.0,0.0,0.0);
		v2 = v.getNormalized();
		TestCase.assertEquals( 0.0, v2.x, 1.0e-3);
		TestCase.assertEquals( 0.0, v2.y, 1.0e-3);
		TestCase.assertEquals( 0.0, v2.z, 1.0e-3);
	}
	
	/**
	 * Tests the add and sum methods.
	 */
	@Test
	public void add() {
		Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
		Vector3 v2 = new Vector3(-2.0, 1.0, -1.0);
		
		Vector3 v3 = v1.sum(v2);
		TestCase.assertEquals(-1.0, v3.x);
		TestCase.assertEquals( 3.0, v3.y);
		TestCase.assertEquals( 2.0, v3.z);
		
		v3 = v1.sum(3.0, -7.5, 2.0);
		TestCase.assertEquals( 4.0, v3.x);
		TestCase.assertEquals(-5.5, v3.y);
		TestCase.assertEquals( 5.0, v3.z);
		
		v1.add(v2);
		TestCase.assertEquals(-1.0, v1.x);
		TestCase.assertEquals( 3.0, v1.y);
		TestCase.assertEquals( 2.0, v1.z);
		
		v1.add(-2.0, 1.0, 0.0);
		TestCase.assertEquals(-3.0, v1.x);
		TestCase.assertEquals( 4.0, v1.y);
		TestCase.assertEquals( 2.0, v1.z);
	}
	
	/**
	 * Tests the subtact and difference methods.
	 */
	@Test
	public void subtract() {
		Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
		Vector3 v2 = new Vector3(-2.0, 1.0, -1.0);
		
		Vector3 v3 = v1.difference(v2);
		TestCase.assertEquals(3.0, v3.x);
		TestCase.assertEquals(1.0, v3.y);
		TestCase.assertEquals(4.0, v3.z);
		
		v3 = v1.difference(3.0, -7.5, 2.0);
		TestCase.assertEquals(-2.0, v3.x);
		TestCase.assertEquals( 9.5, v3.y);
		TestCase.assertEquals( 1.0, v3.z);
		
		v1.subtract(v2);
		TestCase.assertEquals(3.0, v1.x);
		TestCase.assertEquals(1.0, v1.y);
		TestCase.assertEquals(4.0, v1.z);
		
		v1.subtract(-2.0, 1.0, 0.0);
		TestCase.assertEquals(5.0, v1.x);
		TestCase.assertEquals(0.0, v1.y);
		TestCase.assertEquals(4.0, v1.z);
	}
	
	/**
	 * Tests the to method.
	 */
	@Test
	public void to() {
		Vector3 p1 = new Vector3(1.0, 1.0, 1.0);
		Vector3 p2 = new Vector3(0.0, 1.0, 0.0);
		
		Vector3 r = p1.to(p2);
		
		TestCase.assertEquals(-1.0, r.x);
		TestCase.assertEquals( 0.0, r.y);
		TestCase.assertEquals(-1.0, r.z);
		
		r = p1.to(2.0, 0.0, -1.0);
		
		TestCase.assertEquals( 1.0, r.x);
		TestCase.assertEquals(-1.0, r.y);
		TestCase.assertEquals(-2.0, r.z);
	}
	
	/**
	 * Tests the multiply and product methods.
	 */
	@Test
	public void multiply() {
		Vector3 v1 = new Vector3(2.0, 1.0, -1.0);
		
		Vector3 r = v1.product(-1.5);
		TestCase.assertEquals(-3.0, r.x);
		TestCase.assertEquals(-1.5, r.y);
		TestCase.assertEquals( 1.5, r.z);
		
		v1.multiply(-1.5);
		TestCase.assertEquals(-3.0, v1.x);
		TestCase.assertEquals(-1.5, v1.y);
		TestCase.assertEquals( 1.5, v1.z);
	}
	
	/**
	 * Tests the dot method.
	 */
	@Test
	public void dot() {
		Vector3 v1 = new Vector3(1.0, 1.0, -1.0);
		Vector3 v2 = new Vector3(0.0, 1.0, 0.0);
		
		TestCase.assertEquals(1.0, v1.dot(v2));
		
		TestCase.assertEquals(1.0, v1.dot(0.0, 1.0, 0.0));
		
		// test a perpendicular vector
		TestCase.assertEquals(0.0, v1.dot(-1.0, 1.0, 0.0));
		
		TestCase.assertEquals(2.0, v1.dot(1.0, 1.0, 0.0));
	}
	
	/**
	 * Tests the cross product methods.
	 */
	@Test
	public void cross() {
		Vector3 v1 = new Vector3(1.0, 1.0, 0.0);
		Vector3 v2 = new Vector3(0.0, 1.0, -1.0);
		
		Vector3 r = v1.cross(v1);
		TestCase.assertEquals(0.0, r.x);
		TestCase.assertEquals(0.0, r.y);
		TestCase.assertEquals(0.0, r.z);
		
		r = v1.cross(v2);
		TestCase.assertEquals(-1.0, r.x);
		TestCase.assertEquals( 1.0, r.y);
		TestCase.assertEquals( 1.0, r.z);
		
		r = v1.cross(1.0, 1.0, 0.0);
		TestCase.assertEquals(0.0, r.x);
		TestCase.assertEquals(0.0, r.y);
		TestCase.assertEquals(0.0, r.z);
		
		r = v1.cross(0.0, 1.0, 1.0);
		TestCase.assertEquals( 1.0, r.x);
		TestCase.assertEquals(-1.0, r.y);
		TestCase.assertEquals( 1.0, r.z);
		
		r = v1.cross(-1.0, 1.0, -1.0);
		TestCase.assertEquals(-1.0, r.x);
		TestCase.assertEquals( 1.0, r.y);
		TestCase.assertEquals( 2.0, r.z);
	}
	
	/**
	 * Tests the isOrthoganal method.
	 */
	@Test
	public void isOrthogonal() {
		Vector3 v1 = new Vector3(1.0, 1.0, 0.0);
		Vector3 v2 = new Vector3(0.0, 1.0, 2.0);
		
		TestCase.assertFalse(v1.isOrthogonal(v2));
		TestCase.assertFalse(v1.isOrthogonal(v1));
		
		TestCase.assertFalse(v1.isOrthogonal(0.0, 1.0, 0.0));
		TestCase.assertTrue(v1.isOrthogonal(1.0, -1.0, 0.0));
		TestCase.assertTrue(v1.isOrthogonal(-1.0, 1.0, 0.0));
		TestCase.assertFalse(v1.isOrthogonal(1.0, 1.0, 0.0));
		TestCase.assertTrue(v1.isOrthogonal(0.0, 0.0, 0.0));
	}
	
	/**
	 * Tests the isZero method.
	 */
	@Test
	public void isZero() {
		Vector3 v = new Vector3();
		
		TestCase.assertTrue(v.isZero());
		
		v.set(1.0, 0.0, 0.0);
		TestCase.assertFalse(v.isZero());
		
		v.set(1.0, 1.0, 0.0);
		TestCase.assertFalse(v.isZero());
		
		v.set(0.0, 1.0, 1.0);
		TestCase.assertFalse(v.isZero());
		
		v.set(0.0, 0.0, 1.0);
		TestCase.assertFalse(v.isZero());
	}
	
	/**
	 * Tests the negate method.
	 */
	@Test
	public void negate() {
		Vector3 v = new Vector3(1.0, -6.0, 2.0);
		
		v.negate();
		TestCase.assertEquals(-1.0, v.x);
		TestCase.assertEquals( 6.0, v.y);
		TestCase.assertEquals(-2.0, v.z);
	}
	
	/**
	 * Tests the zero method.
	 */
	@Test
	public void zero() {
		Vector3 v = new Vector3(1.0, -2.0, 3.0);
		
		v.zero();
		TestCase.assertEquals( 0.0, v.x);
		TestCase.assertEquals( 0.0, v.y);
		TestCase.assertEquals( 0.0, v.z);
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Vector3 v1 = new Vector3(1.0, 1.0, 0.0);
		Vector3 v2 = new Vector3(0.5, 1.0, 1.0);
		
		Vector3 r = v1.project(v2);
		
		TestCase.assertEquals( 0.333, r.x, 1.0e-3);
		TestCase.assertEquals( 0.666, r.y, 1.0e-3);
		TestCase.assertEquals( 0.666, r.z, 1.0e-3);
		
		r = v1.project(new Vector3());
		TestCase.assertEquals( 0.0, r.x, 1.0e-3);
		TestCase.assertEquals( 0.0, r.y, 1.0e-3);
		TestCase.assertEquals( 0.0, r.z, 1.0e-3);
	}
	
	/**
	 * Tests the normalize method.
	 */
	@Test
	public void normalize() {
		Vector3 v = new Vector3(2.0, 1.0, 2.0);
		v.normalize();
		
		TestCase.assertEquals( 2.0 / 3.0, v.x, 1.0e-3);
		TestCase.assertEquals( 1.0 / 3.0, v.y, 1.0e-3);
		TestCase.assertEquals( 2.0 / 3.0, v.z, 1.0e-3);
	}
	
	/**
	 * Tests the interpolate methods.
	 * @since 5.0.2
	 */
	@Test
	public void interpolate() {
		Vector3 start = new Vector3(1, 1, 1);
		Vector3 end = new Vector3(2, 2, 2);
		
		// go to middle
		start.lerp(end, 0.5);
		TestCase.assertEquals(start.x, 1.5, 1e-5);
		TestCase.assertEquals(start.y, 1.5, 1e-5);
		TestCase.assertEquals(start.z, 1.5, 1e-5);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// try again
		start.lerp(end, 0.5);
		TestCase.assertEquals(start.x, 1.75, 1e-5);
		TestCase.assertEquals(start.y, 1.75, 1e-5);
		TestCase.assertEquals(start.z, 1.75, 1e-5);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// negative change
		start.set(1, 1, 1);
		start.lerp(end, -0.5);
		TestCase.assertEquals(start.x, 0.5, 1e-5);
		TestCase.assertEquals(start.y, 0.5, 1e-5);
		TestCase.assertEquals(start.z, 0.5, 1e-5);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// no change
		start.lerp(end, 0);
		TestCase.assertEquals(start.x, 0.5, 1e-5);
		TestCase.assertEquals(start.y, 0.5, 1e-5);
		TestCase.assertEquals(start.z, 0.5, 1e-5);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// try another non-middle value
		start.lerp(end, 0.1);
		TestCase.assertEquals(start.x, 0.65, 1e-5);
		TestCase.assertEquals(start.y, 0.65, 1e-5);
		TestCase.assertEquals(start.z, 0.65, 1e-5);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		/// now try the other method
		Vector3 dest = new Vector3();
		
		// go to middle
		start.set(1, 1, 1);
		dest.lerp(start, end, 0.5);
		TestCase.assertEquals(dest.x, 1.5, 1e-5);
		TestCase.assertEquals(dest.y, 1.5, 1e-5);
		TestCase.assertEquals(dest.z, 1.5, 1e-5);
		TestCase.assertEquals(start.x, 1.0);
		TestCase.assertEquals(start.y, 1.0);
		TestCase.assertEquals(start.z, 1.0);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// try again
		dest.lerp(start, end, 0.5);
		TestCase.assertEquals(dest.x, 1.5, 1e-5);
		TestCase.assertEquals(dest.y, 1.5, 1e-5);
		TestCase.assertEquals(dest.z, 1.5, 1e-5);
		TestCase.assertEquals(start.x, 1.0);
		TestCase.assertEquals(start.y, 1.0);
		TestCase.assertEquals(start.z, 1.0);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// negative change
		dest.lerp(start, end, -0.5);
		TestCase.assertEquals(dest.x, 0.5, 1e-5);
		TestCase.assertEquals(dest.y, 0.5, 1e-5);
		TestCase.assertEquals(dest.z, 0.5, 1e-5);
		TestCase.assertEquals(start.x, 1.0);
		TestCase.assertEquals(start.y, 1.0);
		TestCase.assertEquals(start.z, 1.0);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// no change
		dest.lerp(start, end, 0);
		TestCase.assertEquals(dest.x, 1.0, 1e-5);
		TestCase.assertEquals(dest.y, 1.0, 1e-5);
		TestCase.assertEquals(dest.z, 1.0, 1e-5);
		TestCase.assertEquals(start.x, 1.0);
		TestCase.assertEquals(start.y, 1.0);
		TestCase.assertEquals(start.z, 1.0);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
		
		// try another non-middle value
		dest.lerp(start, end, 0.1);
		TestCase.assertEquals(dest.x, 1.1, 1e-5);
		TestCase.assertEquals(dest.y, 1.1, 1e-5);
		TestCase.assertEquals(dest.z, 1.1, 1e-5);
		TestCase.assertEquals(start.x, 1.0);
		TestCase.assertEquals(start.y, 1.0);
		TestCase.assertEquals(start.z, 1.0);
		TestCase.assertEquals(end.x, 2.0);
		TestCase.assertEquals(end.y, 2.0);
		TestCase.assertEquals(end.z, 2.0);
	}
	
	/**
	 * Tests the equals w/ epsilon method.
	 */
	@Test
	public void equalsEpsilon() {
		Vector3 v1 = new Vector3(1, 1, 1);
		Vector3 v2 = new Vector3(2, 2, 2);
		
		TestCase.assertFalse(v1.equals(v2, 0.0));
		TestCase.assertFalse(v1.equals(v2, 0.1));
		TestCase.assertFalse(v1.equals(v2, 0.99));
		TestCase.assertTrue(v1.equals(v2, 1.0));
		TestCase.assertTrue(v1.equals(v2, 5.0));
		TestCase.assertTrue(v1.equals(v2, Double.MAX_VALUE));
		
		// extremes
		v1.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		v2.set(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
		
		TestCase.assertFalse(v1.equals(v2, 0.1));
		TestCase.assertFalse(v1.equals(v2, 1.0));
		TestCase.assertFalse(v1.equals(v2, 5.0));
		TestCase.assertFalse(v1.equals(v2, Double.MAX_VALUE));
		TestCase.assertTrue(v1.equals(v2, Double.POSITIVE_INFINITY));
		
		v1.set(1, 1, 1);
		
		TestCase.assertFalse(v1.equals(2, 2, 2, 0.0));
		TestCase.assertFalse(v1.equals(2, 2, 2, 0.1));
		TestCase.assertFalse(v1.equals(2, 2, 2, 0.99));
		TestCase.assertTrue(v1.equals(2, 2, 2, 1.0));
		TestCase.assertTrue(v1.equals(2, 2, 2, 5.0));
		TestCase.assertTrue(v1.equals(2, 2, 2, Double.MAX_VALUE));
		
		// extremes
		v1.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		
		TestCase.assertFalse(v1.equals(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, 0.1));
		TestCase.assertFalse(v1.equals(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, 1.0));
		TestCase.assertFalse(v1.equals(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, 5.0));
		TestCase.assertFalse(v1.equals(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE));
		TestCase.assertTrue(v1.equals(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, Double.POSITIVE_INFINITY));
		
		// others
		TestCase.assertFalse(v1.equals(null, 0));
		TestCase.assertTrue(v1.equals(v1, 0));
		
		// x component matches exactly, but y is off
		v1.set(1, 2, 1);
		v2.set(1, 3, 1);
		
		TestCase.assertFalse(v1.equals(v2, 0.0));
		TestCase.assertFalse(v1.equals(v2, 0.1));
		TestCase.assertFalse(v1.equals(v2, 0.99));
		TestCase.assertTrue(v1.equals(v2, 1.0));
		TestCase.assertTrue(v1.equals(v2, 5.0));
		TestCase.assertTrue(v1.equals(v2, Double.MAX_VALUE));
		
		TestCase.assertFalse(v1.equals(1, 3, 1, 0.0));
		TestCase.assertFalse(v1.equals(1, 3, 1, 0.1));
		TestCase.assertFalse(v1.equals(1, 3, 1, 0.99));
		TestCase.assertTrue(v1.equals(1, 3, 1, 1.0));
		TestCase.assertTrue(v1.equals(1, 3, 1, 5.0));
		TestCase.assertTrue(v1.equals(1, 3, 1, Double.MAX_VALUE));
		
		// x & y component matches exactly, but z is off
		v1.set(1, 3, 0);
		v2.set(1, 3, 1);
		
		TestCase.assertFalse(v1.equals(v2, 0.0));
		TestCase.assertFalse(v1.equals(v2, 0.1));
		TestCase.assertFalse(v1.equals(v2, 0.99));
		TestCase.assertTrue(v1.equals(v2, 1.0));
		TestCase.assertTrue(v1.equals(v2, 5.0));
		TestCase.assertTrue(v1.equals(v2, Double.MAX_VALUE));
		
		TestCase.assertFalse(v1.equals(1, 3, 1, 0.0));
		TestCase.assertFalse(v1.equals(1, 3, 1, 0.1));
		TestCase.assertFalse(v1.equals(1, 3, 1, 0.99));
		TestCase.assertTrue(v1.equals(1, 3, 1, 1.0));
		TestCase.assertTrue(v1.equals(1, 3, 1, 5.0));
		TestCase.assertTrue(v1.equals(1, 3, 1, Double.MAX_VALUE));
	}
}
