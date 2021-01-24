/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.Epsilon;
import org.junit.Test;

/**
 * Test case for the AABB class.
 * @author William Bittle
 * @version 4.1.0
 * @since 3.0.0
 */
public class AABBTest {
	/**
	 * Tests the successful creation of an AABB.
	 */
	@Test
	public void createSuccess() {
		new AABB(0.0, 0.0, 1.0, 1.0);
		new AABB(-2.0, 2.0, -1.0, 5.0);
		new AABB(new Vector2(-3.0, 0.0), new Vector2(-2.0, 2.0));
	}
	
	/**
	 * Creates an aabb from a radius.
	 * @since 3.1.5
	 */
	@Test
	public void createRadius() {
		AABB aabb = new AABB(0.5);
		TestCase.assertEquals(-0.500, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(-0.500, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals( 0.500, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals( 0.500, aabb.getMaxY(), 1.0e-3);
		
		aabb = new AABB(new Vector2(-1.0, 1.0), 0.5);
		TestCase.assertEquals(-1.500, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals( 0.500, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals(-0.500, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals( 1.500, aabb.getMaxY(), 1.0e-3);
	}
	
	/**
	 * Creates an aabb from a negative radius.
	 * @since 4.0.0
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createRadiusNegative() {
		new AABB(new Vector2(-1.0, 1.0), -1.0);
	}
	
	/**
	 * Tests the successful copy of an AABB.
	 */
	@Test
	public void createCopy() {
		AABB aabb1 = new AABB(new Vector2(-3.0, 0.0), new Vector2(-2.0, 2.0));
		AABB aabb2 = new AABB(aabb1);
		TestCase.assertNotSame(aabb1, aabb2);
		TestCase.assertEquals(aabb1.getMinX(), aabb2.getMinX(), 1.0E-4);
		TestCase.assertEquals(aabb1.getMinY(), aabb2.getMinY(), 1.0E-4);
		TestCase.assertEquals(aabb1.getMaxX(), aabb2.getMaxX(), 1.0E-4);
		TestCase.assertEquals(aabb1.getMaxY(), aabb2.getMaxY(), 1.0E-4);
	}
	
	/**
	 * Tests the failed creation of an AABB.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createFailure1() {
		new AABB(0.0, 0.0, -1.0, 2.0);
	}
	
	/**
	 * Tests the failed creation of an AABB.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createFailure2() {
		new AABB(new Vector2(0.0, 0.0), new Vector2(-1.0, 2.0));
	}
	
	/**
	 * Tests the perimeter method.
	 */
	@Test
	public void perimeter() {
		AABB aabb = new AABB(-2.0, 0.0, 2.0, 1.0);
		// 4 + 1 = 5 * 2 = 10
		TestCase.assertEquals(10.0, aabb.getPerimeter(), 1.0E-4);
	}

	/**
	 * Tests the area method.
	 */
	@Test
	public void area() {
		AABB aabb = new AABB(-2.0, 0.0, 2.0, 1.0);
		// 4 * 1 = 4
		TestCase.assertEquals(4.0, aabb.getArea(), 1.0E-4);
	}

	/**
	 * Tests the union methods.
	 */
	@Test
	public void union() {
		// overlapping AABBs
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		AABB aabb2 = new AABB(-1.0, -2.0, 5.0, 0.5);
		
		// test the getUnion method
		AABB aabbr = aabb1.getUnion(aabb2);
		TestCase.assertEquals(-2.0, aabbr.getMinX(), 1.0E-4);
		TestCase.assertEquals(-2.0, aabbr.getMinY(), 1.0E-4);
		TestCase.assertEquals(5.0, aabbr.getMaxX(), 1.0E-4);
		TestCase.assertEquals(1.0, aabbr.getMaxY(), 1.0E-4);
		
		// test the getUnion method using separated aabbs
		AABB aabb3 = new AABB(-4.0, 2.0, -3.0, 4.0);
		aabbr = aabb1.getUnion(aabb3);
		TestCase.assertEquals(-4.0, aabbr.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMinY(), 1.0E-4);
		TestCase.assertEquals(2.0, aabbr.getMaxX(), 1.0E-4);
		TestCase.assertEquals(4.0, aabbr.getMaxY(), 1.0E-4);
		
		aabb1.union(aabb2);
		TestCase.assertEquals(-2.0, aabb1.getMinX(), 1.0E-4);
		TestCase.assertEquals(-2.0, aabb1.getMinY(), 1.0E-4);
		TestCase.assertEquals(5.0, aabb1.getMaxX(), 1.0E-4);
		TestCase.assertEquals(1.0, aabb1.getMaxY(), 1.0E-4);
		
		aabb1.union(aabb2, aabb3);
		TestCase.assertEquals(-4.0, aabb1.getMinX(), 1.0E-4);
		TestCase.assertEquals(-2.0, aabb1.getMinY(), 1.0E-4);
		TestCase.assertEquals(5.0, aabb1.getMaxX(), 1.0E-4);
		TestCase.assertEquals(4.0, aabb1.getMaxY(), 1.0E-4);
	}
	
	/**
	 * Tests the expand method.
	 */
	@Test
	public void expand() {
		AABB aabb = new AABB(-2.0, 0.0, 4.0, 4.0);
		AABB aabb2 = aabb.getExpanded(2.0);
		
		TestCase.assertNotSame(aabb, aabb2);
		
		aabb.expand(1.0);
		TestCase.assertEquals(-2.5, aabb.getMinX(), 1.0E-4);
		TestCase.assertEquals(-0.5, aabb.getMinY(), 1.0E-4);
		TestCase.assertEquals(4.5, aabb.getMaxX(), 1.0E-4);
		TestCase.assertEquals(4.5, aabb.getMaxY(), 1.0E-4);
		
		// the second aabb will have different values
		TestCase.assertEquals(-3.0, aabb2.getMinX(), 1.0E-4);
		TestCase.assertEquals(-1.0, aabb2.getMinY(), 1.0E-4);
		TestCase.assertEquals(5.0, aabb2.getMaxX(), 1.0E-4);
		TestCase.assertEquals(5.0, aabb2.getMaxY(), 1.0E-4);
		
		// test negative expansion
		aabb2 = aabb.getExpanded(-1.0);
		TestCase.assertEquals(-2.0, aabb2.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabb2.getMinY(), 1.0E-4);
		TestCase.assertEquals(4.0, aabb2.getMaxX(), 1.0E-4);
		TestCase.assertEquals(4.0, aabb2.getMaxY(), 1.0E-4);
		aabb.expand(-1.0);
		TestCase.assertEquals(-2.0, aabb.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabb.getMinY(), 1.0E-4);
		TestCase.assertEquals(4.0, aabb.getMaxX(), 1.0E-4);
		TestCase.assertEquals(4.0, aabb.getMaxY(), 1.0E-4);
		
		// test an overly negative expansion
		aabb2 = aabb.getExpanded(-8.0);
		TestCase.assertEquals(1.0, aabb2.getMinX(), 1.0E-4);
		TestCase.assertEquals(2.0, aabb2.getMinY(), 1.0E-4);
		TestCase.assertEquals(1.0, aabb2.getMaxX(), 1.0E-4);
		TestCase.assertEquals(2.0, aabb2.getMaxY(), 1.0E-4);
		aabb.expand(-8.0);
		TestCase.assertEquals(1.0, aabb.getMinX(), 1.0E-4);
		TestCase.assertEquals(2.0, aabb.getMinY(), 1.0E-4);
		TestCase.assertEquals(1.0, aabb.getMaxX(), 1.0E-4);
		TestCase.assertEquals(2.0, aabb.getMaxY(), 1.0E-4);
	}
	
	/**
	 * Tests the overlaps method.
	 */
	@Test
	public void overlaps() {
		// test overlap
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		AABB aabb2 = new AABB(-1.0, -2.0, 5.0, 2.0);
		TestCase.assertTrue(aabb1.overlaps(aabb2));
		TestCase.assertTrue(aabb2.overlaps(aabb1));
		
		// test no overlap
		AABB aabb3 = new AABB(3.0, 2.0, 4.0, 3.0);
		TestCase.assertFalse(aabb1.overlaps(aabb3));
		TestCase.assertFalse(aabb3.overlaps(aabb1));
		
		// test containment
		AABB aabb4 = new AABB(-1.0, 0.25, 1.0, 0.75);
		TestCase.assertTrue(aabb1.overlaps(aabb4));
		TestCase.assertTrue(aabb4.overlaps(aabb1));
	}
	
	/**
	 * Test the contains method.
	 */
	@Test
	public void contains() {
		// test overlap
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		AABB aabb2 = new AABB(-1.0, -2.0, 5.0, 2.0);
		TestCase.assertFalse(aabb1.contains(aabb2));
		TestCase.assertFalse(aabb2.contains(aabb1));
		
		// test no overlap
		AABB aabb3 = new AABB(3.0, 2.0, 4.0, 3.0);
		TestCase.assertFalse(aabb1.contains(aabb3));
		TestCase.assertFalse(aabb3.contains(aabb1));
		
		// test containment
		AABB aabb4 = new AABB(-1.0, 0.25, 1.0, 0.75);
		TestCase.assertTrue(aabb1.contains(aabb4));
		TestCase.assertFalse(aabb4.contains(aabb1));
	}
	
	/**
	 * Tests the getWidth method.
	 * @since 3.0.2
	 */
	@Test
	public void getWidth() {
		AABB aabb = new AABB(-2.0, 0.0, 1.0, 1.0);
		
		TestCase.assertEquals(3.0, aabb.getWidth());
	}
	
	/**
	 * Tests the getHeight method.
	 * @since 3.0.2
	 */
	@Test
	public void getHeight() {
		AABB aabb = new AABB(-2.0, 0.0, 1.0, 1.0);
		
		TestCase.assertEquals(1.0, aabb.getHeight());
	}

	/**
	 * Tests the translate method.
	 * @since 3.1.0
	 */
	@Test
	public void translate() {
		AABB aabb = new AABB(-2.0, 0.0, 1.0, 1.0);
		
		AABB aabb2 = aabb.getTranslated(new Vector2(-1.0, 2.0));
		TestCase.assertNotSame(aabb, aabb2);
		
		TestCase.assertEquals(-2.0, aabb.getMinX(), 1.0E-4);
		TestCase.assertEquals( 0.0, aabb.getMinY(), 1.0E-4);
		TestCase.assertEquals( 1.0, aabb.getMaxX(), 1.0E-4);
		TestCase.assertEquals( 1.0, aabb.getMaxY(), 1.0E-4);
		
		TestCase.assertEquals(-3.0, aabb2.getMinX(), 1.0E-4);
		TestCase.assertEquals( 2.0, aabb2.getMinY(), 1.0E-4);
		TestCase.assertEquals( 0.0, aabb2.getMaxX(), 1.0E-4);
		TestCase.assertEquals( 3.0, aabb2.getMaxY(), 1.0E-4);
		
		aabb.translate(new Vector2(-1.0, 2.0));
		
		TestCase.assertEquals(-3.0, aabb.getMinX(), 1.0E-4);
		TestCase.assertEquals( 2.0, aabb.getMinY(), 1.0E-4);
		TestCase.assertEquals( 0.0, aabb.getMaxX(), 1.0E-4);
		TestCase.assertEquals( 3.0, aabb.getMaxY(), 1.0E-4);
	}
	
	/**
	 * Tests the contains point method.
	 * @since 3.1.1
	 */
	@Test
	public void containsPoint() {
		AABB aabb = new AABB(-2.0, 0.0, 2.0, 1.0);
		
		// test containment
		TestCase.assertTrue(aabb.contains(0.0, 0.5));
		TestCase.assertTrue(aabb.contains(new Vector2(0.0, 0.5)));
		
		// test no containment
		TestCase.assertFalse(aabb.contains(0.0, 2.0));
		TestCase.assertFalse(aabb.contains(new Vector2(0.0, 2.0)));
		
		// test on edge
		TestCase.assertTrue(aabb.contains(0.0, 1.0));
		TestCase.assertTrue(aabb.contains(new Vector2(0.0, 1.0)));
	}
	
	/**
	 * Tests the intersection methods.
	 * @since 3.1.1
	 */
	@Test
	public void intersection() {
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		AABB aabb2 = new AABB(-1.0, -2.0, 5.0, 0.5);
		
		AABB aabbr = aabb1.getIntersection(aabb2);
		TestCase.assertEquals(-1.0, aabbr.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMinY(), 1.0E-4);
		TestCase.assertEquals(2.0, aabbr.getMaxX(), 1.0E-4);
		TestCase.assertEquals(0.5, aabbr.getMaxY(), 1.0E-4);
		
		// test using separated aabbs (should give a zero AABB)
		AABB aabb3 = new AABB(-4.0, 2.0, -3.0, 4.0);
		aabbr = aabb1.getIntersection(aabb3);
		TestCase.assertEquals(0.0, aabbr.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMinY(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMaxX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMaxY(), 1.0E-4);
		
		aabbr.intersection(aabb1, aabb2);
		TestCase.assertEquals(-1.0, aabbr.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMinY(), 1.0E-4);
		TestCase.assertEquals(2.0, aabbr.getMaxX(), 1.0E-4);
		TestCase.assertEquals(0.5, aabbr.getMaxY(), 1.0E-4);
		
		aabbr.intersection(aabb1, aabb3);
		TestCase.assertEquals(0.0, aabbr.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMinY(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMaxX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabbr.getMaxY(), 1.0E-4);
		
		aabb1.intersection(aabb2);
		TestCase.assertEquals(-1.0, aabb1.getMinX(), 1.0E-4);
		TestCase.assertEquals(0.0, aabb1.getMinY(), 1.0E-4);
		TestCase.assertEquals(2.0, aabb1.getMaxX(), 1.0E-4);
		TestCase.assertEquals(0.5, aabb1.getMaxY(), 1.0E-4);
	}
	
	/**
	 * Tests the isDegenerate methods.
	 */
	@Test
	public void degenerate() {
		AABB aabb = new AABB(0.0, 0.0, 0.0, 0.0);
		TestCase.assertTrue(aabb.isDegenerate());
		
		aabb = new AABB(1.0, 2.0, 1.0, 3.0);
		TestCase.assertTrue(aabb.isDegenerate());
		
		aabb = new AABB(1.0, 0.0, 2.0, 1.0);
		TestCase.assertFalse(aabb.isDegenerate());
		
		aabb = new AABB(1.0, 0.0, 1.000001, 2.0);
		TestCase.assertFalse(aabb.isDegenerate());
		TestCase.assertFalse(aabb.isDegenerate(Epsilon.E));
		TestCase.assertTrue(aabb.isDegenerate(0.000001));
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void testEquals() {
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		AABB aabb2 = new AABB(-1.0, -2.0, 5.0, 2.0);
		AABB aabb3 = new AABB(-1.0, -2.0, 5.0, 2.0);
		
		TestCase.assertFalse(aabb1.equals(null));
		TestCase.assertTrue(aabb1.equals(aabb1));
		TestCase.assertFalse(aabb1.equals(aabb2));
		TestCase.assertFalse(aabb1.equals(aabb3));
		TestCase.assertTrue(aabb2.equals(aabb3));
		TestCase.assertFalse(aabb1.equals(new Object()));
	}
	
	/**
	 * Tests the hashcode method.
	 */
	@Test
	public void testHashcode() {
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		AABB aabb2 = new AABB(-1.0, -2.0, 5.0, 2.0);
		AABB aabb3 = new AABB(-1.0, -2.0, 5.0, 2.0);
		
		TestCase.assertEquals(aabb1.hashCode(), aabb1.hashCode());
		TestCase.assertEquals(aabb2.hashCode(), aabb3.hashCode());
		TestCase.assertFalse(aabb1.hashCode() == aabb2.hashCode());
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void testToString() {
		AABB aabb1 = new AABB(-2.0, 0.0, 2.0, 1.0);
		TestCase.assertNotNull(aabb1.toString());
	}
	
	/**
	 * Tests the createFromPoints methods.
	 */
	@Test
	public void createFromPoints() {
		Vector2 p1 = new Vector2(1, 1);
		Vector2 p2 = new Vector2(3, 3);
		
		AABB aabb = AABB.createFromPoints(p1, p2);
		
		TestCase.assertEquals(1.0, aabb.getMinX());
		TestCase.assertEquals(1.0, aabb.getMinY());
		TestCase.assertEquals(3.0, aabb.getMaxX());
		TestCase.assertEquals(3.0, aabb.getMaxY());
		
		aabb = AABB.createFromPoints(1.0, 4.0, 2.0, 2.0);
		
		TestCase.assertEquals(1.0, aabb.getMinX());
		TestCase.assertEquals(2.0, aabb.getMinY());
		TestCase.assertEquals(2.0, aabb.getMaxX());
		TestCase.assertEquals(4.0, aabb.getMaxY());
	}

	/**
	 * Tests the setFromPoints methods.
	 */
	@Test
	public void setFromPoints() {
		Vector2 p1 = new Vector2(1, 1);
		Vector2 p2 = new Vector2(3, 3);
		
		AABB aabb = new AABB(0,0,0,0);
		AABB.setFromPoints(p1, p2, aabb);
		
		TestCase.assertEquals(1.0, aabb.getMinX());
		TestCase.assertEquals(1.0, aabb.getMinY());
		TestCase.assertEquals(3.0, aabb.getMaxX());
		TestCase.assertEquals(3.0, aabb.getMaxY());
		
		AABB.setFromPoints(1.0, 4.0, 2.0, 2.0, aabb);
		
		TestCase.assertEquals(1.0, aabb.getMinX());
		TestCase.assertEquals(2.0, aabb.getMinY());
		TestCase.assertEquals(2.0, aabb.getMaxX());
		TestCase.assertEquals(4.0, aabb.getMaxY());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		AABB aabb = new AABB(1,1,3,3);
		
		AABB copy = aabb.copy();
		
		TestCase.assertEquals(aabb.getMinX(), copy.getMinX());
		TestCase.assertEquals(aabb.getMinY(), copy.getMinY());
		TestCase.assertEquals(aabb.getMaxX(), copy.getMaxX());
		TestCase.assertEquals(aabb.getMaxY(), copy.getMaxY());
	}
	
	/**
	 * Tests the center point.
	 */
	@Test
	public void getCenter() {
		AABB aabb = new AABB(1,1,3,3);
		Vector2 c = aabb.getCenter();
		
		TestCase.assertEquals(2.0, c.x);
		TestCase.assertEquals(2.0, c.y);
	}
	
	/**
	 * Tests the zero method.
	 */
	@Test
	public void zero() {
		AABB aabb = new AABB(1,1,3,3);
		
		TestCase.assertEquals(1.0, aabb.getMinX());
		TestCase.assertEquals(1.0, aabb.getMinY());
		TestCase.assertEquals(3.0, aabb.getMaxX());
		TestCase.assertEquals(3.0, aabb.getMaxY());
		
		aabb.zero();
		
		TestCase.assertEquals(0.0, aabb.getMinX());
		TestCase.assertEquals(0.0, aabb.getMinY());
		TestCase.assertEquals(0.0, aabb.getMaxX());
		TestCase.assertEquals(0.0, aabb.getMaxY());
	}
}
