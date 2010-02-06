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
package org.dyn4j.game2d.dynamics;

import junit.framework.TestCase;

import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector;
import org.junit.Test;

/**
 * Tests the methods of the {@link Mass} class.
 * @author William Bittle
 */
public class MassTest {
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the mass must be > 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroMass() {
		Mass.create(new Vector(), 0, 1.0);
	}
	
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the inertia tensor must be > 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroInertia() {
		Mass.create(new Vector(), 1.0, 0);
	}
	
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the mass must be > 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeMass() {
		Mass.create(new Vector(), -1.0, 1.0);
	}
	
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the inertia tensor must be > 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeInertia() {
		Mass.create(new Vector(), 1.0, -1.0);
	}
	
	/**
	 * Tests the isInfinite mehtod.
	 */
	@Test
	public void infinite() {
		Mass m = Mass.create(new Vector());
		TestCase.assertTrue(m.isInfinite());
	}
	
	/**
	 * Test the create method.
	 */
	@Test
	public void createSuccess() {
		Mass m = Mass.create(new Vector(), 1.0, 1.0);
		TestCase.assertTrue(m.c.equals(new Vector()));
		TestCase.assertEquals(m.m, 1.0);
		TestCase.assertEquals(m.I, 1.0);
	}
	
	/**
	 * Test the create method for infinite mass.
	 */
	@Test
	public void createSuccessInfinite() {
		Mass m = Mass.create(new Vector());
		TestCase.assertTrue(m.c.equals(new Vector()));
		TestCase.assertTrue(m.isInfinite());
	}
	
	/**
	 * Test case for the circle create method.
	 */
	@Test
	public void createCircle() {
		Circle c = new Circle(3.0);
		Mass m = Mass.create(c, 2.0);
		// the mass should be pi * r * r * d
		TestCase.assertEquals(56.548, m.m, 1.0e-3);
		// I should be m * r * r / 2
		TestCase.assertEquals(254.469, m.I, 1.0e-3);
		
		// test an infinte mass
		m = Mass.create(c, 0.0);
		TestCase.assertTrue(m.isInfinite());
	}
	
	/**
	 * Test case for the circle create method passing
	 * a negative density value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCircleNegativeDensity() {
		Circle c = new Circle(3.0);
		Mass.create(c, -1.0);
	}
	
	/**
	 * Test case for the polygon create method.
	 */
	@Test
	public void createPolygon() {
		Polygon p = Geometry.getUnitCirclePolygon(5, 0.5);
		Mass m = Mass.create(p, 1.0);
		// the polygon mass should be the area * d
		TestCase.assertEquals(0.594, m.m, 1.0e-3);
		TestCase.assertEquals(0.057, m.I, 1.0e-3);
		
		// test an infinte mass
		m = Mass.create(p, 0.0);
		TestCase.assertTrue(m.isInfinite());
	}
	
	/**
	 * Test case for the polygon create method passing
	 * a negative density value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createPolygonNegativeDensity() {
		Polygon p = Geometry.getUnitCirclePolygon(5, 0.5);
		Mass.create(p, -1.0);
	}
	
	/**
	 * Test case for the rectangle create method.
	 */
	@Test
	public void createRectangle() {
		Rectangle r = new Rectangle(1.0, 1.0);
		Mass m = Mass.create(r, 1.5);
		// the mass of a rectangle should be h * w * d
		TestCase.assertEquals(1.500, m.m, 1.0e-3);
		TestCase.assertEquals(0.250, m.I, 1.0e-3);
		
		// test an infinte mass
		m = Mass.create(r, 0.0);
		TestCase.assertTrue(m.isInfinite());
	}
	
	/**
	 * Test case for the rectangle create method passing
	 * a negative density value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createRectangleNegativeDensity() {
		Rectangle r = new Rectangle(1.0, 1.0);
		Mass.create(r, -1.0);
	}
	
	/**
	 * Test case for the segment create method.
	 */
	@Test
	public void createSegment() {
		Segment s = new Segment(new Vector(-1.0, 0.0), new Vector(1.0, 0.5));
		Mass m = Mass.create(s, 1.0);
		// the mass of a segment should be l * d
		TestCase.assertEquals(2.061, m.m, 1.0e-3);
		// the I of a segment should be 1 / 12 * l ^ 2 * m
		TestCase.assertEquals(0.730, m.I, 1.0e-3);
		
		// test an infinte mass
		m = Mass.create(s, 0.0);
		TestCase.assertTrue(m.isInfinite());
	}
	
	/**
	 * Test case for the segment create method passing
	 * a negative density value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSegmentNegativeDensity() {
		Segment s = new Segment(new Vector(-1.0, 0.0), new Vector(1.0, 0.5));
		Mass.create(s, -1.0);
	}
	
	/**
	 * Test the create method accepting an array of {@link Mass} objects
	 * where one of the {@link Mass} objects is infinite.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createArrayInfinite() {
		Vector c = new Vector();
		Mass infin = Mass.create(c);
		Mass norm1 = Mass.create(c, 1.0, 0.322);
		Mass norm2 = Mass.create(c, 2.6, 1.2);
		Mass.create(norm1, infin, norm2);
	}
	
	/**
	 * Test the create method accepting an array of {@link Mass} objects.
	 */
	@Test
	public void createArray() {
		Mass m1 = Mass.create(new Vector( 1.0,  1.0), 3.00, 1.00);
		Mass m2 = Mass.create(new Vector(-1.0,  0.0), 0.50, 0.02);
		Mass m3 = Mass.create(new Vector( 1.0, -2.0), 2.00, 3.00);
		Mass m = Mass.create(m1, m2, m3);
		
		TestCase.assertEquals( 0.818, m.c.x, 1.0e-3);
		TestCase.assertEquals(-0.181, m.c.y, 1.0e-3);
		TestCase.assertEquals( 5.500, m.m, 1.0e-3);
		TestCase.assertEquals(16.656, m.I, 1.0e-3);
	}
}
