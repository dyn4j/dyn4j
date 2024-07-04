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
 * Test case for the {@link Rectangle} class.
 * @author William Bittle
 * @version 6.0.0
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
		
		// make sure tostring doesn't blow up
		TestCase.assertNotNull(r.toString());
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
		TestCase.assertTrue(!r.contains(pt, t, false));
		
		// move the rectangle a bit
		t.translate(2.0, 0.0);
		t.rotate(Math.toRadians(30), r.center);
		
		TestCase.assertTrue(r.contains(pt, t));
		TestCase.assertTrue(r.contains(pt, t, false));
		
		// check for on the edge
		t.identity();
		pt.set(0.5, 0.5);
		
		TestCase.assertTrue(r.contains(pt, t));
		TestCase.assertFalse(r.contains(pt, t, false));
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
	
	/**
	 * Tests the getRotation method.
	 */
	@Test
	public void getRotation() {
		Rectangle r = Geometry.createRectangle(1, 1);
		r.translate(1, 1);
		r.rotate(Math.toRadians(30));
		
		TestCase.assertEquals(30, Math.toDegrees(r.getRotationAngle()), 1.0e-3);
		
		Rotation rt = r.getRotation();
		TestCase.assertEquals(30, rt.toDegrees(), 1.0e-3);
	}

	/**
	 * Test case for the rectangle createMass method.
	 */
	@Test
	public void createMass() {
		Rectangle r = new Rectangle(1.0, 1.0);
		Mass m = r.createMass(1.5);
		// the mass of a rectangle should be h * w * d
		TestCase.assertEquals(1.500, m.getMass(), 1.0e-3);
		TestCase.assertEquals(0.250, m.getInertia(), 1.0e-3);
		
		m = r.createMass(0);
		TestCase.assertEquals(0.000, m.getMass(), 1e-3);
		TestCase.assertEquals(0.000, m.getInertia(), 1e-3);
		TestCase.assertEquals(0.000, m.getInverseMass(), 1e-3);
		TestCase.assertEquals(0.000, m.getInverseInertia(), 1e-3);
		TestCase.assertEquals(0.000, m.getCenter().x, 1e-3);
		TestCase.assertEquals(0.000, m.getCenter().y, 1e-3);
		TestCase.assertEquals(MassType.INFINITE, m.getType());
	}

	/**
	 * Test case for the rectangle getArea method.
	 */
	@Test
	public void getArea() {
		Rectangle r = new Rectangle(1.0, 1.0);
		TestCase.assertEquals(1.0, r.getArea(), 1.0e-3);
		
		r = new Rectangle(2.5, 3.0);
		TestCase.assertEquals(7.5, r.getArea(), 1.0e-3);
	}

	/**
	 * Test case for the rectangle computeAABB method.
	 */
	@Test
	public void computeAABB() {
		Rectangle r = new Rectangle(1.0, 1.0);
		AABB aabb = r.createAABB();
		TestCase.assertEquals(0.5, aabb.maxX, 1e-8);
		TestCase.assertEquals(0.5, aabb.maxY, 1e-8);
		TestCase.assertEquals(-0.5, aabb.minX, 1e-8);
		TestCase.assertEquals(-0.5, aabb.minY, 1e-8);
		
		r.rotate(Math.toRadians(45.0));
		aabb = r.createAABB();
		TestCase.assertEquals(0.707, aabb.maxX, 1e-3);
		TestCase.assertEquals(0.707, aabb.maxY, 1e-3);
		TestCase.assertEquals(-0.707, aabb.minX, 1e-3);
		TestCase.assertEquals(-0.707, aabb.minY, 1e-3);
	}

	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Rectangle rect = new Rectangle(1.0, 1.0);
		rect.setUserData(new Object());
		Rectangle copy = rect.copy();
		
		TestCase.assertNotSame(rect, copy);
		TestCase.assertNotSame(rect.center, copy.center);
		TestCase.assertNotSame(rect.normals, copy.normals);
		TestCase.assertNotSame(rect.vertices, copy.vertices);
		TestCase.assertEquals(rect.radius, copy.radius);
		TestCase.assertEquals(rect.height, copy.height);
		TestCase.assertEquals(rect.width, copy.width);
		TestCase.assertNull(copy.userData);
		
		TestCase.assertEquals(rect.center.x, copy.center.x);
		TestCase.assertEquals(rect.center.y, copy.center.y);
		
		TestCase.assertEquals(rect.normals.length, copy.normals.length);
		for (int i = 0; i < rect.normals.length; i++) {
			TestCase.assertEquals(rect.normals[i].x, copy.normals[i].x);
			TestCase.assertEquals(rect.normals[i].y, copy.normals[i].y);
		}
		
		TestCase.assertEquals(rect.vertices.length, copy.vertices.length);
		for (int i = 0; i < rect.vertices.length; i++) {
			TestCase.assertEquals(rect.vertices[i].x, copy.vertices[i].x);
			TestCase.assertEquals(rect.vertices[i].y, copy.vertices[i].y);
		}
	}
}
