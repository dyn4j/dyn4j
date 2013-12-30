/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.geometry.Edge;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

/**
 * Test case for the {@link Segment} class.
 * @author William Bittle
 * @version 3.1.6
 * @since 1.0.0
 */
public class SegmentTest {
	/**
	 * Tests a failed create using one null point.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint1() {
		new Segment(null, new Vector2());
	}
	
	/**
	 * Tests a failed create using one null point.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint2() {
		new Segment(new Vector2(), null);
	}
	
	/**
	 * Tests coincident points.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCoincident() {
		new Segment(new Vector2(), new Vector2());
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void creatSuccess() {
		Segment s = new Segment(
			new Vector2(0.0, 1.0),
			new Vector2(1.0, 2.0)
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
			new Vector2(0.0, 1.0),
			new Vector2(1.5, 3.0)
		);
		
		TestCase.assertEquals(2.500, s.getLength(), 1.0e-3);
	}
	
	/**
	 * Tests the getLocation method.
	 */
	@Test
	public void getLocation() {
		// test invalid line
		double loc = Segment.getLocation(new Vector2(1.0, 1.0), new Vector2(), new Vector2());
		TestCase.assertEquals(0.000, loc, 1.0e-3);
		
		// test valid line/on line
		loc = Segment.getLocation(new Vector2(1.0, 1.0), new Vector2(), new Vector2(2.0, 2.0));
		TestCase.assertEquals(0.000, loc, 1.0e-3);
		
		// test valid line/left-above line
		loc = Segment.getLocation(new Vector2(1.0, 1.0), new Vector2(), new Vector2(1.0, 0.5));
		TestCase.assertTrue(loc > 0);
		
		// test valid line/right-below line
		loc = Segment.getLocation(new Vector2(1.0, 1.0), new Vector2(), new Vector2(1.0, 2.0));
		TestCase.assertTrue(loc < 0);
		
		// test vertical line
		loc = Segment.getLocation(new Vector2(1.0, 1.0), new Vector2(), new Vector2(0.0, 3.0));
		TestCase.assertTrue(loc < 0);
		loc = Segment.getLocation(new Vector2(-1.0, 1.0), new Vector2(), new Vector2(0.0, 3.0));
		TestCase.assertTrue(loc > 0);
		
		// test horizontal line
		loc = Segment.getLocation(new Vector2(1.0, 1.0), new Vector2(0.0, 0.0), new Vector2(1.0, 0.0));
		TestCase.assertTrue(loc > 0);
		loc = Segment.getLocation(new Vector2(1.0, -1.0), new Vector2(0.0, 0.0), new Vector2(1.0, 0.0));
		TestCase.assertTrue(loc < 0);
	}
	
	/**
	 * Tests the get closest point methods.
	 */
	@Test
	public void getPointClosest() {
		Vector2 pt = new Vector2(1.0, -1.0);
		
		// test invalid line/segment
		Vector2 p = Segment.getPointOnLineClosestToPoint(pt, new Vector2(1.0, 1.0), new Vector2(1.0, 1.0));
		TestCase.assertEquals(1.000, p.x, 1.0e-3);
		TestCase.assertEquals(1.000, p.y, 1.0e-3);
		
		p = Segment.getPointOnSegmentClosestToPoint(pt, new Vector2(1.0, 1.0), new Vector2(1.0, 1.0));
		TestCase.assertEquals(1.000, p.x, 1.0e-3);
		TestCase.assertEquals(1.000, p.y, 1.0e-3);
		
		// test valid line
		p = Segment.getPointOnLineClosestToPoint(pt, new Vector2(), new Vector2(5.0, 5.0));
		// since 0,0 is perp to pt
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
		
		p = Segment.getPointOnLineClosestToPoint(pt, new Vector2(), new Vector2(2.5, 5.0));
		TestCase.assertEquals(-0.200, p.x, 1.0e-3);
		TestCase.assertEquals(-0.400, p.y, 1.0e-3);
		
		// test valid segment
		p = Segment.getPointOnSegmentClosestToPoint(pt, new Vector2(-1.0, -1.0), new Vector2(1.0, 1.0));
		// since 0,0 is perp to pt
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
		
		// test closest is one of the segment points
		p = Segment.getPointOnSegmentClosestToPoint(pt, new Vector2(), new Vector2(2.5, 5.0));
		TestCase.assertEquals(0.000, p.x, 1.0e-3);
		TestCase.assertEquals(0.000, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Segment s = new Segment(
			new Vector2(0.0, 1.0),
			new Vector2(1.5, 3.0)
		);
		Transform t = new Transform();
		
		Vector2[] axes = s.getAxes(null, t);
		
		TestCase.assertEquals(2, axes.length);
		
		Vector2 seg = s.vertices[0].to(s.vertices[1]);
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
		Vector2 f = new Vector2(2.0, -2.0);
		t.identity();
		
		axes = s.getAxes(new Vector2[] {f}, t);
		
		TestCase.assertEquals(3, axes.length);
		
		Vector2 v1 = s.vertices[0].to(f);
		v1.normalize();
		
		TestCase.assertEquals(v1.x, axes[2].x, 1.0e-3);
		TestCase.assertEquals(v1.y, axes[2].y, 1.0e-3);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test
	public void getFoci() {
		Segment s = new Segment(
			new Vector2(0.0, 1.0),
			new Vector2(1.5, 3.0)
		);
		Transform t = new Transform();
		
		Vector2[] foci = s.getFoci(t);
		TestCase.assertNull(foci);
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Segment s = new Segment(
			new Vector2(0.0, 1.0),
			new Vector2(1.5, 3.0)
		);
		Transform t = new Transform();
		
		TestCase.assertFalse(s.contains(new Vector2(2.0, 2.0), t));
		TestCase.assertTrue(s.contains(new Vector2(0.75, 2.0), t));
	}
	
	/**
	 * Tests the contains with radius method.
	 */
	@Test
	public void containsRadius() {
		Segment s = new Segment(
				new Vector2(1.0, 1.0),
				new Vector2(-1.0, -1.0)
			);
			Transform t = new Transform();
			
			TestCase.assertFalse(s.contains(new Vector2(2.0, 2.0), t, 0.1));
			TestCase.assertTrue(s.contains(new Vector2(1.05, 1.05), t, 0.1));
			TestCase.assertTrue(s.contains(new Vector2(0.505, 0.5), t, 0.1));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Segment s = new Segment(
			new Vector2(0.0, 1.0),
			new Vector2(1.5, 3.0)
		);
		Transform t = new Transform();
		Vector2 n = new Vector2(1.0, 0.0);
		
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
			new Vector2(0.0, 1.0),
			new Vector2(1.5, 3.0)
		);
		Transform t = new Transform();
		Vector2 n = new Vector2(1.0, 0.0);
		
		Edge f = s.getFarthestFeature(n, t);
		TestCase.assertTrue(f.isEdge());
		TestCase.assertEquals(1.500, f.max.point.x, 1.0e-3);
		TestCase.assertEquals(3.000, f.max.point.y, 1.0e-3);
		
		TestCase.assertEquals(0.000, f.vertex1.point.x, 1.0e-3);
		TestCase.assertEquals(1.000, f.vertex1.point.y, 1.0e-3);
		TestCase.assertEquals(1.500, f.vertex2.point.x, 1.0e-3);
		TestCase.assertEquals(3.000, f.vertex2.point.y, 1.0e-3);
		
		Vector2 p = s.getFarthestPoint(n, t);
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
			new Vector2(0.0, 0.0),
			new Vector2(1.0, 1.0)
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
			new Vector2(0.0, 0.0),
			new Vector2(1.0, 1.0)
		);
		s.translate(2.0, -1.0);
		
		TestCase.assertEquals( 2.000, s.vertices[0].x, 1.0e-3);
		TestCase.assertEquals(-1.000, s.vertices[0].y, 1.0e-3);
		TestCase.assertEquals( 3.000, s.vertices[1].x, 1.0e-3);
		TestCase.assertEquals( 0.000, s.vertices[1].y, 1.0e-3);
	}
	
	/**
	 * Tests the createAABB method.
	 * @since 3.1.0
	 */
	@Test
	public void createAABB() {
		Segment s = new Segment(
			new Vector2(0.0, 0.0),
			new Vector2(1.0, 1.0)
		);
		
		AABB aabb = s.createAABB(Transform.IDENTITY);
		TestCase.assertEquals(0.0, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(0.0, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals(1.0, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals(1.0, aabb.getMaxY(), 1.0e-3);
		
		// try using the default method
		AABB aabb2 = s.createAABB();
		TestCase.assertEquals(aabb.getMinX(), aabb2.getMinX());
		TestCase.assertEquals(aabb.getMinY(), aabb2.getMinY());
		TestCase.assertEquals(aabb.getMaxX(), aabb2.getMaxX());
		TestCase.assertEquals(aabb.getMaxY(), aabb2.getMaxY());
		
		Transform tx = new Transform();
		tx.rotate(Math.toRadians(30.0));
		tx.translate(1.0, 2.0);
		aabb = s.createAABB(tx);
		TestCase.assertEquals(1.0, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(2.0, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals(1.366, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals(3.366, aabb.getMaxY(), 1.0e-3);
	}
	
	/**
	 * Tests the getLineIntersection method.
	 * @since 3.1.1
	 */
	@Test
	public void getLineIntersection() {
		// normal case
		Vector2 p = Segment.getLineIntersection(
				new Vector2(-1.0, -1.0), new Vector2(2.0, 0.0), 
				new Vector2(-1.0,  0.0), new Vector2(1.0, 0.5));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(11.0, p.x);
		TestCase.assertEquals(3.0, p.y);
		
		// try horizontal line
		p = Segment.getLineIntersection(
				new Vector2(-1.0, 1.0), new Vector2(2.0, 1.0), 
				new Vector2(-1.0, 0.0), new Vector2(1.0, 0.5));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(3.0, p.x);
		TestCase.assertEquals(1.0, p.y);
		
		// try a vertical line
		p = Segment.getLineIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, 1.0), 
				new Vector2(-1.0, 0.0), new Vector2(1.0, 0.5));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(3.0, p.x);
		TestCase.assertEquals(1.0, p.y);
		
		// try a vertical and horizontal line
		p = Segment.getLineIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, -2.0), 
				new Vector2(0.0, 1.0), new Vector2(4.0, 1.0));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(3.0, p.x);
		TestCase.assertEquals(1.0, p.y);
		
		// try two parallel lines
		p = Segment.getLineIntersection(
				new Vector2(-2.0, -1.0), new Vector2(-1.0, 0.0), 
				new Vector2(-1.0, -1.0), new Vector2(0.0, 0.0));
		
		TestCase.assertNull(p);
		
		// try two vertical lines (parallel)
		p = Segment.getLineIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, 1.0), 
				new Vector2(2.0, 0.0), new Vector2(2.0, 1.0));
		
		TestCase.assertNull(p);
		
		// try two horizontal lines (parallel)
		p = Segment.getLineIntersection(
				new Vector2(3.0, 1.0), new Vector2(4.0, 1.0), 
				new Vector2(2.0, 2.0), new Vector2(4.0, 2.0));
		
		TestCase.assertNull(p);
		
		// try colinear lines
		p = Segment.getLineIntersection(
				new Vector2(-1.0, -1.0), new Vector2(1.0, 1.0), 
				new Vector2(-2.0, -2.0), new Vector2(-1.5, -1.5));
		
		TestCase.assertNull(p);
		
		// try colinear vertical lines
		p = Segment.getLineIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, 1.0), 
				new Vector2(3.0, 2.0), new Vector2(3.0, 7.0));
		
		TestCase.assertNull(p);
		
		// try colinear horizontal lines
		p = Segment.getLineIntersection(
				new Vector2(4.0, 1.0), new Vector2(5.0, 1.0), 
				new Vector2(-1.0, 1.0), new Vector2(1.0, 1.0));
		
		TestCase.assertNull(p);
	}
	
	/**
	 * Tests the getLineIntersection method.
	 * @since 3.1.1
	 */
	@Test
	public void getSegmentIntersection() {
		Vector2 p = Segment.getSegmentIntersection(
				new Vector2(-3.0, -1.0), new Vector2(3.0, 1.0), 
				new Vector2(-1.0, -2.0), new Vector2(1.0, 2.0));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(0.0, p.x);
		TestCase.assertEquals(0.0, p.y);
		
		// normal case, no intersection
		p = Segment.getSegmentIntersection(
				new Vector2(-1.0, -1.0), new Vector2(2.0, 0.0), 
				new Vector2(-1.0,  0.0), new Vector2(1.0, 0.5));
		
		TestCase.assertNull(p);
		
		// try horizontal segment
		p = Segment.getSegmentIntersection(
				new Vector2(-1.0, 1.0), new Vector2(2.0, 1.0), 
				new Vector2(-1.0, 0.0), new Vector2(1.0, 2.0));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(0.0, p.x);
		TestCase.assertEquals(1.0, p.y);
		
		// try a vertical segment
		p = Segment.getSegmentIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, 3.0), 
				new Vector2(4.0, 0.0), new Vector2(1.0, 3.0));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(3.0, p.x);
		TestCase.assertEquals(1.0, p.y);
		
		// try a vertical and horizontal segment
		p = Segment.getSegmentIntersection(
				new Vector2(3.0, 2.0), new Vector2(3.0, -2.0), 
				new Vector2(0.0, 1.0), new Vector2(4.0, 1.0));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(3.0, p.x);
		TestCase.assertEquals(1.0, p.y);
		
		// try two parallel segments
		p = Segment.getSegmentIntersection(
				new Vector2(-2.0, -1.0), new Vector2(-1.0, 0.0), 
				new Vector2(-1.0, -1.0), new Vector2(0.0, 0.0));
		
		TestCase.assertNull(p);
		
		// try two vertical segments (parallel)
		p = Segment.getSegmentIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, 1.0), 
				new Vector2(2.0, 0.0), new Vector2(2.0, 1.0));
		
		TestCase.assertNull(p);
		
		// try two horizontal segments (parallel)
		p = Segment.getSegmentIntersection(
				new Vector2(3.0, 1.0), new Vector2(4.0, 1.0), 
				new Vector2(3.0, 2.0), new Vector2(4.0, 2.0));
		
		TestCase.assertNull(p);
		
		// try colinear segments
		p = Segment.getSegmentIntersection(
				new Vector2(-1.0, -1.0), new Vector2(1.0, 1.0), 
				new Vector2(-2.0, -2.0), new Vector2(-1.5, -1.5));
		
		TestCase.assertNull(p);
		
		// try colinear vertical segments
		p = Segment.getSegmentIntersection(
				new Vector2(3.0, 0.0), new Vector2(3.0, 1.0), 
				new Vector2(3.0, -1.0), new Vector2(3.0, 7.0));
		
		TestCase.assertNull(p);
		
		// try colinear horizontal segments
		p = Segment.getSegmentIntersection(
				new Vector2(-1.0, 1.0), new Vector2(5.0, 1.0), 
				new Vector2(-1.0, 1.0), new Vector2(1.0, 1.0));
		
		TestCase.assertNull(p);
		
		// try intersection at end point
		p = Segment.getSegmentIntersection(
				new Vector2(1.0, 0.0), new Vector2(3.0, -2.0), 
				new Vector2(-1.0, -1.0), new Vector2(1.0, 0.0));
		
		TestCase.assertNotNull(p);
		TestCase.assertEquals(1.0, p.x);
		TestCase.assertEquals(0.0, p.y);
		
		// test segment intersection perpendicular
		Segment s1 = new Segment(new Vector2(-10, 10), new Vector2(10, 10));
		Segment s2 = new Segment(new Vector2(0, 0), new Vector2(0, 5));
		p = s2.getSegmentIntersection(s1);
		TestCase.assertNull(p);
	}
}
