/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
 * Test case for the {@link Rectangle} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class RectangleTest {
	/**
	 * Tests the constructor with an invalid width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createInvalidWidth() {
		new Rectangle(-1.0, 3.0);
	}
	
	/**
	 * Tests the constructor with an invalid height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createInvalidHeight() {
		new Rectangle(2.0, 0.0);
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void createSuccess() {
		Rectangle r = new Rectangle(2.0, 2.0);
		// make sure the center is 0,0
		TestCase.assertEquals(0.000, r.center.x, 1.0e-3);
		TestCase.assertEquals(0.000, r.center.y, 1.0e-3);
		// make sure the points are correct
		TestCase.assertEquals(-1.000, r.vertices[0].x, 1.0e-3);
		TestCase.assertEquals(-1.000, r.vertices[0].y, 1.0e-3);
		
		TestCase.assertEquals( 1.000, r.vertices[1].x, 1.0e-3);
		TestCase.assertEquals(-1.000, r.vertices[1].y, 1.0e-3);
		
		TestCase.assertEquals( 1.000, r.vertices[2].x, 1.0e-3);
		TestCase.assertEquals( 1.000, r.vertices[2].y, 1.0e-3);
		
		TestCase.assertEquals(-1.000, r.vertices[3].x, 1.0e-3);
		TestCase.assertEquals( 1.000, r.vertices[3].y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Rectangle r = new Rectangle(1.0, 1.0);
		Transform t = new Transform();
		Vector2[] axes = r.getAxes(null, t);
		
		// make sure there is only two
		TestCase.assertEquals(2, axes.length);
		
		// make sure the axes are perpendicular to the edges
		Vector2 ab = r.vertices[0].to(r.vertices[1]);
		Vector2 ad = r.vertices[0].to(r.vertices[3]);
		
		TestCase.assertEquals(0.000, ab.dot(axes[1]), 1.0e-3);
		TestCase.assertEquals(0.000, ad.dot(axes[0]), 1.0e-3);
		
		// test a focal point
		Vector2 pt = new Vector2(2.0, -1.0);
		axes = r.getAxes(new Vector2[] {pt}, t);
		
		// make sure there are 4 more axes
		TestCase.assertEquals(3, axes.length);
		// make sure they are parallel to the vector from a vertex to the focal point
		TestCase.assertEquals(0.000, r.vertices[1].to(pt).cross(axes[2]), 1.0e-3);
	}
	
	/**
	 * Test the contains method.
	 */
	@Test
	public void contains() {
		Rectangle r = new Rectangle(1.0, 2.0);
		Transform t = new Transform();
		
		Vector2 pt = new Vector2(2.0, 0.5);
		
		TestCase.assertTrue(!r.contains(pt, t));
		
		// move the rectangle a bit
		t.translate(2.0, 0.0);
		t.rotate(Math.toRadians(30), r.center);
		
		TestCase.assertTrue(r.contains(pt, t));
		
		// check for on the edge
		t.identity();
		pt.set(0.5, 0.5);
		
		TestCase.assertTrue(r.contains(pt, t));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Rectangle r = new Rectangle(2.0, 1.0);
		Transform t = new Transform();
		
		Vector2 axis = new Vector2(1.0, 0.0);
		
		Interval i = r.project(axis, t);
		
		TestCase.assertEquals(-1.000, i.min, 1.0e-3);
		TestCase.assertEquals( 1.000, i.max, 1.0e-3);
		
		// move the rectangle a bit
		t.translate(1.0, 1.0);
		t.rotate(Math.toRadians(30), t.getTransformed(r.center));
		
		i = r.project(axis, t);
		
		TestCase.assertEquals(-0.116, i.min, 1.0e-3);
		TestCase.assertEquals( 2.116, i.max, 1.0e-3);
		
		axis.set(0.0, 1.0);
		
		i = r.project(axis, t);
		
		TestCase.assertEquals(0.066, i.min, 1.0e-3);
		TestCase.assertEquals(1.933, i.max, 1.0e-3);
	}
}
