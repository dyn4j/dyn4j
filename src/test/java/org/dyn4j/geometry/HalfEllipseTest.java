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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link HalfEllipse} class.
 * @author William Bittle
 * @version 3.3.1
 * @since 3.1.5
 */
public class HalfEllipseTest {
	/** Identity Transform instance */
	private static final Transform IDENTITY = new Transform();
	
	/**
	 * Tests the getRadius method for a point above the x-axis but below the evolute
	 * to ensure that we obtain the correct max distance.
	 * @since 3.3.1
	 */
	@Test
	public void getRadius() {
		HalfEllipse he = new HalfEllipse(4.0, 4.0);
		double x = 1;
		double y = 0.25;
		double d = he.getRadius(new Vector2(x, y));
		
		TestCase.assertEquals(3.927647520827677, d, 1e-3);
	}
	
	/**
	 * Tests a zero width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroWidth() {
		new HalfEllipse(0.0, 1.0);
	}
	
	/**
	 * Tests a negative width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeWidth() {
		new HalfEllipse(-1.0, 1.0);
	}
	
	/**
	 * Tests a zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeight() {
		new HalfEllipse(1.0, 0.0);
	}
	
	/**
	 * Tests a negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeight() {
		new HalfEllipse(1.0, -1.0);
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccess() {
		new HalfEllipse(1.0, 2.0);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		HalfEllipse e = new HalfEllipse(2.0, 0.5);
		Transform t = new Transform();
		Vector2 p = new Vector2(0.75, 0.35);
		
		// shouldn't be in
		TestCase.assertTrue(!e.contains(p, t));
		
		p.set(0.75, -0.2);
		TestCase.assertTrue(!e.contains(p, t));
		
		// move a bit
		p.set(0.75, 0.35);
		t.translate(0.5, 0.0);
		
		// should be in
		TestCase.assertTrue(e.contains(p, t));
		
		p.set(1.5, 0.0);
		
		// should be on the edge
		TestCase.assertTrue(e.contains(p, t));
		
		// another test for failure case
		p.set(0.75, 0.35);
		e.translate(e.getCenter().getNegative());
		TestCase.assertFalse(e.contains(p, t));
		
		// try local rotation and translation
		e.rotate(Math.toDegrees(90));
		e.translate(0.5, 1.0);
		
		p.set(0.3, 0.3);
		TestCase.assertFalse(e.contains(p, t));
		
		p.set(0.7, 0.4);
		TestCase.assertTrue(e.contains(p, t));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		HalfEllipse e = new HalfEllipse(2.0, 0.5);
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, 1.0);
		
		// try some translation
		t.translate(1.0, 0.5);
		
		Interval i = e.project(x, t);
		TestCase.assertEquals( 0.000, i.min, 1.0e-3);
		TestCase.assertEquals( 2.000, i.max, 1.0e-3);
		
		i = e.project(y, t);
		TestCase.assertEquals( 0.500, i.min, 1.0e-3);
		TestCase.assertEquals( 1.000, i.max, 1.0e-3);		
		
		// try some rotation
		t.rotate(Math.toRadians(30), t.getTransformed(e.getCenter()));
		
		i = e.project(y, t);
		TestCase.assertEquals( 0.028, i.min, 1.0e-3);
		TestCase.assertEquals( 1.189, i.max, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotateAboutCenter(Math.toRadians(30));
		
		i = e.project(y, IDENTITY);
		TestCase.assertEquals( 0.028, i.min, 1.0e-3);
		TestCase.assertEquals( 1.189, i.max, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		i = e.project(y, t);
		TestCase.assertEquals( 1.028, i.min, 1.0e-3);
		TestCase.assertEquals( 2.189, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the farthest methods.
	 */
	@Test
	public void getFarthest() {
		HalfEllipse e = new HalfEllipse(2.0, 0.5);
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, -1.0);
		
		// try some translation
		t.translate(1.0, 0.5);
		
		Vector2 p = e.getFarthestPoint(x, t);
		TestCase.assertEquals( 2.000, p.x, 1.0e-3);
		TestCase.assertEquals( 0.5, p.y, 1.0e-3);
		
		// try some rotation
		t.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 0.133, p.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p.y, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, IDENTITY);
		TestCase.assertEquals( 0.133, p.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p.y, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 0.133, p.x, 1.0e-3);
		TestCase.assertEquals( 1.000, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getAxes() {
		HalfEllipse e = new HalfEllipse(1.0, 0.5);
		e.getAxes(new Vector2[] { new Vector2() }, IDENTITY);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getFoci() {
		HalfEllipse e = new HalfEllipse(1.0, 0.5);
		e.getFoci(IDENTITY);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		HalfEllipse e = new HalfEllipse(1.0, 0.25);
		
		// rotate about center
		e.translate(1.0, 1.0);
		e.rotateAboutCenter(Math.toRadians(30));
		TestCase.assertEquals(1.000, e.center.x, 1.0e-3);
		TestCase.assertEquals(1.106, e.center.y, 1.0e-3);
		
		// rotate about the origin
		e.rotate(Math.toRadians(90));
		TestCase.assertEquals(-1.106, e.center.x, 1.0e-3);
		TestCase.assertEquals( 1.000, e.center.y, 1.0e-3);
		e.translate(e.getCenter().getNegative());
		
		// should move the center
		e.rotate(Math.toRadians(90), 1.0, -1.0);
		TestCase.assertEquals( 0.000, e.center.x, 1.0e-3);
		TestCase.assertEquals(-2.000, e.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the translate methods.
	 */
	@Test
	public void translate() {
		HalfEllipse e = new HalfEllipse(1.0, 0.25);
		
		e.translate(1.0, -0.5);
		
		TestCase.assertEquals( 1.000, e.getEllipseCenter().x, 1.0e-3);
		TestCase.assertEquals(-0.500, e.getEllipseCenter().y, 1.0e-3);
	}
	
	/**
	 * Tests the generated AABB.
	 */
	@Test
	public void createAABB() {
		HalfEllipse e = new HalfEllipse(1.0, 0.25);
		
		// using an identity transform
		AABB aabb = e.createAABB(IDENTITY);
		TestCase.assertEquals(-0.500, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals( 0.000, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals( 0.500, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals( 0.250, aabb.getMaxY(), 1.0e-3);
		
		// try using the default method
		AABB aabb2 = e.createAABB();
		TestCase.assertEquals(aabb.getMinX(), aabb2.getMinX());
		TestCase.assertEquals(aabb.getMinY(), aabb2.getMinY());
		TestCase.assertEquals(aabb.getMaxX(), aabb2.getMaxX());
		TestCase.assertEquals(aabb.getMaxY(), aabb2.getMaxY());
		
		// test using a rotation and translation matrix
		Transform tx = new Transform();
		tx.rotate(Math.toRadians(30.0));
		tx.translate(1.0, 2.0);
		
		aabb = e.createAABB(tx);
		TestCase.assertEquals(0.549, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(1.750, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals(1.433, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals(2.330, aabb.getMaxY(), 1.0e-3);
	}
}
