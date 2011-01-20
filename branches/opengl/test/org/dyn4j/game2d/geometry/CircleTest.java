/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
 * Test case for the {@link Circle} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class CircleTest {
	/**
	 * Tests a zero radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroRadius() {
		new Circle(0.0);
	}
	
	/**
	 * Tests a negative radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeRadius() {
		new Circle(-1.0);
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccess() {
		new Circle(1.0);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Circle c = new Circle(2.0);
		Transform t = new Transform();
		Vector2 p = new Vector2(2.0, 4.0);
		
		// shouldn't be in the circle
		TestCase.assertTrue(!c.contains(p, t));
		
		// move the circle a bit
		t.translate(2.0, 2.5);
		
		// should be in the circle
		TestCase.assertTrue(c.contains(p, t));
		
		t.translate(0.0, -0.5);
		
		// should be on the edge
		TestCase.assertTrue(c.contains(p, t));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Circle c = new Circle(1.5);
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, 1.0);
		
		t.translate(1.0, 0.5);
		
		Interval i = c.project(x, t);
		
		TestCase.assertEquals(-0.500, i.min, 1.0e-3);
		TestCase.assertEquals( 2.500, i.max, 1.0e-3);
		
		// rotating about the center shouldn't effect anything
		t.rotate(Math.toRadians(30), 1.0, 0.5);
		
		i = c.project(y, t);
		
		TestCase.assertEquals(-1.000, i.min, 1.0e-3);
		TestCase.assertEquals( 2.000, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the farthest methods.
	 */
	@Test
	public void getFarthest() {
		Circle c = new Circle(1.5);
		Transform t = new Transform();
		Vector2 y = new Vector2(0.0, -1.0);
		
		Vertex f = c.getFarthestFeature(y, t);
		TestCase.assertTrue(f.isVertex());
		TestCase.assertEquals( 0.000, f.point.x, 1.0e-3);
		TestCase.assertEquals(-1.500, f.point.y, 1.0e-3);
		
		Vector2 p = c.getFarthestPoint(y, t);
		TestCase.assertEquals( 0.000, p.x, 1.0e-3);
		TestCase.assertEquals(-1.500, p.y, 1.0e-3);
		
		// move the circle a bit
		t.translate(0.0, -0.5);
		
		f = c.getFarthestFeature(y.getNegative(), t);
		TestCase.assertTrue(f.isVertex());
		TestCase.assertEquals(0.000, f.point.x, 1.0e-3);
		TestCase.assertEquals(1.000, f.point.y, 1.0e-3);
		
		p = c.getFarthestPoint(y.getNegative(), t);
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(1.000, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Circle c = new Circle(1.5);
		Transform t = new Transform();
		// a cicle has infinite axes so it should be null
		Vector2[] axes = c.getAxes(null, t);
		TestCase.assertNull(axes);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test
	public void getFoci() {
		Circle c = new Circle(1.5);
		Transform t = new Transform();
		// should only return one
		Vector2[] foci = c.getFoci(t);
		TestCase.assertEquals(1, foci.length);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		// center is at 0,0
		Circle c = new Circle(1.0);
		
		// should do nothing since its about the circle's center
		c.rotate(Math.toRadians(30));
		
		TestCase.assertEquals(0.000, c.center.x, 1.0e-3);
		TestCase.assertEquals(0.000, c.center.y, 1.0e-3);
		
		// should move the center
		c.rotate(Math.toRadians(90), 1.0, -1.0);
		
		TestCase.assertEquals( 0.000, c.center.x, 1.0e-3);
		TestCase.assertEquals(-2.000, c.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the translate methods.
	 */
	@Test
	public void translate() {
		// center is at 0,0
		Circle c = new Circle(1.0);
		
		c.translate(1.0, -0.5);
		
		TestCase.assertEquals( 1.000, c.center.x, 1.0e-3);
		TestCase.assertEquals(-0.500, c.center.y, 1.0e-3);
	}
}
