/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the contains methods of the {@link Sat} class.
 * @author William Bittle
 * @version 4.2.1
 * @since 4.2.1
 */
public class SatTest {
	/**
	 * Tests the containment of two polygons.
	 */
	@Test
	public void containsPolygonCompletely() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createUnitCirclePolygon(5, 0.2);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertTrue(result);
	}
	
	/**
	 * Tests the containment of two polygons that are only intersecting.
	 */
	@Test
	public void doesNotContainPolygon() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		tx2.translate(0.3, 0);
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createUnitCirclePolygon(5, 0.2);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
	}
	
	/**
	 * Tests the containment of two polygons, where an edge is overlapping
	 */
	@Test
	public void containPolygonButNotInclusive() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		tx2.translate(0.2, 0);
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createUnitCirclePolygon(5, 0.2);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
	}

	/**
	 * Tests the containment of two polygons that are exactly the same.
	 */
	@Test
	public void samePolygon() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createUnitCirclePolygon(5, 0.4);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
	}
	
	/**
	 * Tests the containment of two different polygon shapes.
	 */
	@Test
	public void differentPolygons() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createSquare(1.0);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertTrue(result);
	}
	
	/**
	 * Tests the containment of two different polygons that are intersecting.
	 */
	@Test
	public void differentPolygonsNoContainment() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		tx1.translate(0.2, 0);
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createSquare(1.0);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
	}
	
	/**
	 * Tests the containment of a polygon and a slice.
	 */
	@Test
	public void slice() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createSlice(0.2, Math.toRadians(30));
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertTrue(result);
		
		tx2.translate(-0.7, 0);
		p2 = Geometry.createSlice(2.0, Math.toRadians(120));
		
		result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertTrue(result);
	}
	
	/**
	 * Tests the containment of a polygon and a slice.
	 */
	@Test
	public void sliceNoContainment() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex p1 = Geometry.createUnitCirclePolygon(5, 0.4);
		Convex p2 = Geometry.createSlice(0.5, Math.toRadians(30));
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
	}
	
	/**
	 * Tests the containment of segment/segment - should always be false
	 */
	@Test
	public void segmentSegment() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		// exact overlap
		Convex p1 = Geometry.createSegment(new Vector2(1.0, 1.0));
		Convex p2 = Geometry.createSegment(new Vector2(1.0, 1.0));
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
		
		p1 = Geometry.createSegment(new Vector2(1.0, 1.0));
		p2 = Geometry.createSegment(new Vector2(-1.0, 1.0));
		
		result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
	}

	/**
	 * Tests the containment of circle/circle.
	 */
	@Test
	public void circleCircle() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		// exact overlap
		Convex p1 = Geometry.createCircle(1.0);
		Convex p2 = Geometry.createCircle(1.0);
		
		boolean result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
		
		// moved to intersection (therefore false)
		tx1.translate(0.5, 0);
		result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
		
		// change p2 to smaller so it's inside, but touching
		p2 = Geometry.createCircle(0.5);
		result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertFalse(result);
		
		// now do full containment
		tx1.identity();
		result = sat.contains(p1, tx1, p2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(p2, tx2, p1, tx1, c);
		TestCase.assertTrue(result);
	}

	/**
	 * Tests a segment.
	 */
	@Test
	public void segmentCircle() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex c1 = Geometry.createSegment(new Vector2(0.0, 0.4));
		Convex c2 = Geometry.createCircle(0.6);
		
		boolean result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertTrue(result);
		
		tx1.translate(0.6, 0);
		result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertFalse(result);
		
		tx1.translate(0.25, 0);
		result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertFalse(result);
	}
	
	/**
	 * Tests a capsule.
	 */
	@Test
	public void capsuleCircle() {
		Sat sat = new Sat();
		Containment c = new Containment();
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		Convex c1 = Geometry.createCapsule(1.0, 0.5);
		Convex c2 = Geometry.createCircle(0.6);
		
		boolean result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertTrue(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertTrue(result);
		
		tx1.translate(0.6, 0);
		result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertFalse(result);
		
		tx1.translate(0.25, 0);
		result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertFalse(result);
		
		tx1.translate(5.0, 0);
		result = sat.contains(c1, tx1, c2, tx2, c);
		TestCase.assertFalse(result);
		
		result = sat.contains(c1, tx2, c2, tx1, c);
		TestCase.assertFalse(result);
	}
}
