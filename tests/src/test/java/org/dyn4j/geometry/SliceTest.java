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

import org.dyn4j.Epsilon;
import org.junit.Test;

/**
 * Test case for the {@link Slice} class.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public class SliceTest {
	/**
	 * Tests a zero radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroRadius() {
		new Slice(0.0, Math.toRadians(50));
	}
	
	/**
	 * Tests a negative radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeRadius() {
		new Slice(-1.0, Math.toRadians(50));
	}
	
	/**
	 * Tests a zero theta.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroTheta() {
		new Slice(1.0, 0);
	}
	
	/**
	 * Tests a negative theta.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeTheta() {
		new Slice(1.0, -Math.toRadians(50));
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccess() {
		Slice slice = new Slice(1.0, Math.toRadians(50));
		
		// the circle center should be the origin
		TestCase.assertEquals(0.000, slice.getCircleCenter().x, 1.0e-3);
		TestCase.assertEquals(0.000, slice.getCircleCenter().y, 1.0e-3);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		Transform t = new Transform();
		Vector2 p = new Vector2(0.5, -0.3);
		
		// shouldn't be inside
		TestCase.assertTrue(!e.contains(p, t));
		
		// move it a bit
		t.translate(-0.25, 0.0);
		
		// should be inside
		TestCase.assertTrue(e.contains(p, t));
		
		p.set(0.75, 0.0);
		// should be on the edge
		TestCase.assertTrue(e.contains(p, t));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, 1.0);
		
		// try some translation
		t.translate(1.0, 0.5);
		
		Interval i = e.project(x, t);
		TestCase.assertEquals( 1.000, i.min, 1.0e-3);
		TestCase.assertEquals( 2.000, i.max, 1.0e-3);
		
		// try some rotation
		t.rotate(Math.toRadians(30), t.getTransformed(e.getCenter()));
		
		i = e.project(y, t);
		TestCase.assertEquals(0.177, i.min, 1.0e-3);
		TestCase.assertEquals(0.996, i.max, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotateAboutCenter(Math.toRadians(30));
		
		i = e.project(y, Transform.IDENTITY);
		TestCase.assertEquals(0.177, i.min, 1.0e-3);
		TestCase.assertEquals(0.996, i.max, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		i = e.project(y, t);
		TestCase.assertEquals(1.177, i.min, 1.0e-3);
		TestCase.assertEquals(1.996, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the farthest methods.
	 */
	@Test
	public void getFarthest() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, 1.0);
		
		// try some translation
		t.translate(1.0, 0.5);
		
		Vector2 p = e.getFarthestPoint(x, t);
		TestCase.assertEquals( 2.000, p.x, 1.0e-3);
		TestCase.assertEquals( 0.500, p.y, 1.0e-3);
		
		// try some rotation
		t.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 1.573, p.x, 1.0e-3);
		TestCase.assertEquals( 1.319, p.y, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, Transform.IDENTITY);
		TestCase.assertEquals( 1.573, p.x, 1.0e-3);
		TestCase.assertEquals( 1.319, p.y, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 1.573, p.x, 1.0e-3);
		TestCase.assertEquals( 2.319, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		// should be two axes + number of foci
		Vector2[] foci = new Vector2[] {
			new Vector2(2.0, -0.5),
			new Vector2(1.0, 3.0)
		};
		Vector2[] axes = e.getAxes(foci, Transform.IDENTITY);
		TestCase.assertEquals(4, axes.length);
		
		// make sure we get back the right axes
		axes = e.getAxes(null, Transform.IDENTITY);
		TestCase.assertEquals(-0.422, axes[0].x, 1.0e-3);
		TestCase.assertEquals( 0.906, axes[0].y, 1.0e-3);
		TestCase.assertEquals(-0.422, axes[1].x, 1.0e-3);
		TestCase.assertEquals(-0.906, axes[1].y, 1.0e-3);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test
	public void getFoci() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		Vector2[] foci = e.getFoci(Transform.IDENTITY);
		// should be two foci
		TestCase.assertEquals(1, foci.length);
		// make sure the foci are correct
		TestCase.assertEquals( 0.000, foci[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, foci[0].y, 1.0e-3);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		// note: the center is not at the origin
		
		// rotate about center
		e.translate(1.0, 1.0);
		e.rotateAboutCenter(Math.toRadians(30));
		TestCase.assertEquals(1.645, e.center.x, 1.0e-3);
		TestCase.assertEquals(1.000, e.center.y, 1.0e-3);
		
		// rotate about the origin
		e.rotate(Math.toRadians(90));
		TestCase.assertEquals(-1.000, e.center.x, 1.0e-3);
		TestCase.assertEquals( 1.645, e.center.y, 1.0e-3);
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
		Slice e = new Slice(1.0, Math.toRadians(50));
		
		e.translate(1.0, -0.5);
		
		TestCase.assertEquals( 1.645, e.center.x, 1.0e-3);
		TestCase.assertEquals(-0.500, e.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the generated AABB.
	 */
	@Test
	public void createAABB() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		
		// using an identity transform
		AABB aabb = e.createAABB(Transform.IDENTITY);
		TestCase.assertEquals( 0.000, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(-0.422, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals( 1.000, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals( 0.422, aabb.getMaxY(), 1.0e-3);
		
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
		TestCase.assertEquals( 1.000, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals( 2.000, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals( 1.996, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals( 2.819, aabb.getMaxY(), 1.0e-3);
	}
	
	/**
	 * Verifies the output of the getRadius and getSliceRadius methods.
	 */
	@Test
	public void sliceRadius() {
		Slice e = new Slice(1.0, Math.toRadians(50));
		TestCase.assertEquals(1.000, e.getSliceRadius(), 1.0e-3);
		TestCase.assertFalse(Math.abs(1.0 - e.getRadius()) < Epsilon.E);
		TestCase.assertFalse(Math.abs(e.getSliceRadius() - e.getRadius()) < Epsilon.E);
	}
}
