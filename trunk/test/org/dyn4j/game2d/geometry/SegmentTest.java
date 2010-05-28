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
package org.dyn4j.game2d.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Segment} class.
 * @author William Bittle
 */
public class SegmentTest {
	/**
	 * Tests coincident points.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCoincident() {
		new Segment(new Vector(), new Vector());
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void creatSuccess() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.0, 2.0)
		);
		
		TestCase.assertEquals(0.500, s.center.x, 1.0e-3);
		TestCase.assertEquals(1.500, s.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the length method.
	 */
	@Test
	public void getLength() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.5, 3.0)
		);
		
		TestCase.assertEquals(2.500, s.getLength(), 1.0e-3);
	}
	
	/**
	 * Tests the getLocation method.
	 */
	@Test
	public void getLocation() {
		// test invalid line
		double loc = Segment.getLocation(new Vector(1.0, 1.0), new Vector(), new Vector());
		TestCase.assertEquals(0.000, loc, 1.0e-3);
		
		// test valid line/on line
		loc = Segment.getLocation(new Vector(1.0, 1.0), new Vector(), new Vector(2.0, 2.0));
		TestCase.assertEquals(0.000, loc, 1.0e-3);
		
		// test valid line/left-above line
		loc = Segment.getLocation(new Vector(1.0, 1.0), new Vector(), new Vector(1.0, 0.5));
		TestCase.assertTrue(loc > 0);
		
		// test valid line/right-below line
		loc = Segment.getLocation(new Vector(1.0, 1.0), new Vector(), new Vector(1.0, 2.0));
		TestCase.assertTrue(loc < 0);
	}
	
	/**
	 * Tests the get closest point methods.
	 */
	@Test
	public void getPointClosest() {
		Vector pt = new Vector(1.0, -1.0);
		
		// test invalid line/segment
		Vector p = Segment.getPointOnLineClosestToPoint(pt, new Vector(1.0, 1.0), new Vector(1.0, 1.0));
		TestCase.assertEquals(1.000, p.x, 1.0e-3);
		TestCase.assertEquals(1.000, p.y, 1.0e-3);
		
		p = Segment.getPointOnSegmentClosestToPoint(pt, new Vector(1.0, 1.0), new Vector(1.0, 1.0));
		TestCase.assertEquals(1.000, p.x, 1.0e-3);
		TestCase.assertEquals(1.000, p.y, 1.0e-3);
		
		// test valid line
		p = Segment.getPointOnLineClosestToPoint(pt, new Vector(), new Vector(5.0, 5.0));
		// since 0,0 is perp to pt
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
		
		p = Segment.getPointOnLineClosestToPoint(pt, new Vector(), new Vector(2.5, 5.0));
		TestCase.assertEquals(-0.200, p.x, 1.0e-3);
		TestCase.assertEquals(-0.400, p.y, 1.0e-3);
		
		// test valid segment
		p = Segment.getPointOnSegmentClosestToPoint(pt, new Vector(-1.0, -1.0), new Vector(1.0, 1.0));
		// since 0,0 is perp to pt
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
		
		// test closest is one of the segment points
		p = Segment.getPointOnSegmentClosestToPoint(pt, new Vector(), new Vector(2.5, 5.0));
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.5, 3.0)
		);
		Transform t = new Transform();
		
		Vector[] axes = s.getAxes(null, t);
		
		TestCase.assertEquals(2, axes.length);
		
		Vector seg = s.vertices[0].to(s.vertices[1]);
		// one should be the line itself and the other should be the perp
		TestCase.assertEquals(0.000, seg.cross(axes[1]), 1.0e-3);
		TestCase.assertEquals(0.000, seg.dot(axes[0]), 1.0e-3);
		
		// perform some transformations
		t.translate(1.0, 0.0);
		t.rotate(Math.toRadians(25));
		
		axes = s.getAxes(null, t);
		
		seg = t.getTransformed(s.vertices[0]).to(t.getTransformed(s.vertices[1]));
		// one should the line itself and the other should be the perp
		TestCase.assertEquals(0.000, seg.cross(axes[1]), 1.0e-3);
		TestCase.assertEquals(0.000, seg.dot(axes[0]), 1.0e-3);
		
		// test for some foci
		Vector f = new Vector(2.0, -2.0);
		t.identity();
		
		axes = s.getAxes(new Vector[] {f}, t);
		
		TestCase.assertEquals(3, axes.length);
		
		Vector v1 = s.vertices[0].to(f);
		
		TestCase.assertEquals(v1.x, axes[2].x, 1.0e-3);
		TestCase.assertEquals(v1.y, axes[2].y, 1.0e-3);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test
	public void getFoci() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.5, 3.0)
		);
		Transform t = new Transform();
		
		Vector[] foci = s.getFoci(t);
		TestCase.assertNull(foci);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.5, 3.0)
		);
		Transform t = new Transform();
		
		TestCase.assertFalse(s.contains(new Vector(2.0, 2.0), t));
		TestCase.assertTrue(s.contains(new Vector(0.75, 2.0), t));
	}
	
	/**
	 * Tests the contains with radius method.
	 */
	@Test
	public void containsRadius() {
		Segment s = new Segment(
				new Vector(1.0, 1.0),
				new Vector(-1.0, -1.0)
			);
			Transform t = new Transform();
			
			TestCase.assertFalse(s.contains(new Vector(2.0, 2.0), t, 0.1));
			TestCase.assertTrue(s.contains(new Vector(1.05, 1.05), t, 0.1));
			TestCase.assertTrue(s.contains(new Vector(0.505, 0.5), t, 0.1));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.5, 3.0)
		);
		Transform t = new Transform();
		Vector n = new Vector(1.0, 0.0);
		
		Interval i = s.project(n, t);
		
		TestCase.assertEquals(0.000, i.min, 1.0e-3);
		TestCase.assertEquals(1.500, i.max, 1.0e-3);
		
		n.set(1.0, 1.0);
		i = s.project(n, t);
		
		TestCase.assertEquals(1.000, i.min, 1.0e-3);
		TestCase.assertEquals(4.500, i.max, 1.0e-3);
		
		n.set(0.0, 1.0);
		i = s.project(n, t);
		
		TestCase.assertEquals(1.000, i.min, 1.0e-3);
		TestCase.assertEquals(3.000, i.max, 1.0e-3);
		
		// transform the segment a bit
		t.translate(1.0, 2.0);
		t.rotate(Math.toRadians(90), t.getTransformed(s.center));
		
		i = s.project(n, t);
		
		TestCase.assertEquals(3.250, i.min, 1.0e-3);
		TestCase.assertEquals(4.750, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the getFarthest methods.
	 */
	@Test
	public void getFarthest() {
		Segment s = new Segment(
			new Vector(0.0, 1.0),
			new Vector(1.5, 3.0)
		);
		Transform t = new Transform();
		Vector n = new Vector(1.0, 0.0);
		
		Edge f = s.getFarthestFeature(n, t);
		TestCase.assertTrue(f.isEdge());
		TestCase.assertEquals(1.500, f.max.point.x, 1.0e-3);
		TestCase.assertEquals(3.000, f.max.point.y, 1.0e-3);
		
		TestCase.assertEquals(0.000, f.vertices[0].point.x, 1.0e-3);
		TestCase.assertEquals(1.000, f.vertices[0].point.y, 1.0e-3);
		TestCase.assertEquals(1.500, f.vertices[1].point.x, 1.0e-3);
		TestCase.assertEquals(3.000, f.vertices[1].point.y, 1.0e-3);
		
		Vector p = s.getFarthestPoint(n, t);
		TestCase.assertEquals(1.500, p.x, 1.0e-3);
		TestCase.assertEquals(3.000, p.y, 1.0e-3);
		
		// move the segment a bit
		t.translate(0.0, -1.0);
		t.rotate(Math.toRadians(45));
		
		p = s.getFarthestPoint(n, t);
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the rotate method.
	 */
	@Test
	public void rotate() {
		Segment s = new Segment(
			new Vector(0.0, 0.0),
			new Vector(1.0, 1.0)
		);
		s.rotate(Math.toRadians(45), 0, 0);
		
		TestCase.assertEquals(0.000, s.vertices[0].x, 1.0e-3);
		TestCase.assertEquals(0.000, s.vertices[0].y, 1.0e-3);
		
		TestCase.assertEquals(0.000, s.vertices[1].x, 1.0e-3);
		TestCase.assertEquals(1.414, s.vertices[1].y, 1.0e-3);
	}
	
	/**
	 * Tests the translate method.
	 */
	@Test
	public void translate() {
		Segment s = new Segment(
			new Vector(0.0, 0.0),
			new Vector(1.0, 1.0)
		);
		s.translate(2.0, -1.0);
		
		TestCase.assertEquals( 2.000, s.vertices[0].x, 1.0e-3);
		TestCase.assertEquals(-1.000, s.vertices[0].y, 1.0e-3);
		TestCase.assertEquals( 3.000, s.vertices[1].x, 1.0e-3);
		TestCase.assertEquals( 0.000, s.vertices[1].y, 1.0e-3);
	}
}
