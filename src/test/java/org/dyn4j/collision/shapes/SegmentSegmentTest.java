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
package org.dyn4j.collision.shapes;

import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for {@link Segment} - {@link Segment} collision detection.
 * @author William Bittle
 * @version 4.2.1
 * @since 1.0.0
 */
public class SegmentSegmentTest extends AbstractNarrowphaseShapeTest {
	/** The first test {@link Segment} */
	private Segment seg1;
	
	/** The second test {@link Segment} */
	private Segment seg2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.seg1 = new Segment(new Vector2(-0.3, -0.3), new Vector2(0.2, 0.3));
		this.seg2 = new Segment(new Vector2(-0.5, 0.0), new Vector2(0.5, 0.0));
	}
	
	/**
	 * Tests {@link Sat}.
	 */
	@Test
	public void detectSat() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test overlap
		TestCase.assertTrue(this.sat.detect(seg1, t1, seg2, t2, p));
		TestCase.assertTrue(this.sat.detect(seg1, t1, seg2, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertTrue(this.sat.detect(seg2, t2, seg1, t1, p));
		TestCase.assertTrue(this.sat.detect(seg2, t2, seg1, t1));
		
		// test AABB overlap
		p.clear();
		t2.translate(0.5, 0.0);
		TestCase.assertFalse(this.sat.detect(seg1, t1, seg2, t2, p));
		TestCase.assertFalse(this.sat.detect(seg1, t1, seg2, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertFalse(this.sat.detect(seg2, t2, seg1, t1, p));
		TestCase.assertFalse(this.sat.detect(seg2, t2, seg1, t1));
		
		// test no overlap
		p.clear();
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sat.detect(seg1, t1, seg2, t2, p));
		TestCase.assertFalse(this.sat.detect(seg1, t1, seg2, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertFalse(this.sat.detect(seg2, t2, seg1, t1, p));
		TestCase.assertFalse(this.sat.detect(seg2, t2, seg1, t1));
	}
	
	/**
	 * Tests {@link Gjk}.
	 */
	@Test
	public void detectGjk() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test overlap
		TestCase.assertTrue(this.gjk.detect(seg1, t1, seg2, t2, p));
		TestCase.assertTrue(this.gjk.detect(seg1, t1, seg2, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertTrue(this.gjk.detect(seg2, t2, seg1, t1, p));
		TestCase.assertTrue(this.gjk.detect(seg2, t2, seg1, t1));
		
		// test AABB overlap
		p.clear();
		t2.translate(0.5, 0.0);
		TestCase.assertFalse(this.gjk.detect(seg1, t1, seg2, t2, p));
		TestCase.assertFalse(this.gjk.detect(seg1, t1, seg2, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertFalse(this.gjk.detect(seg2, t2, seg1, t1, p));
		TestCase.assertFalse(this.gjk.detect(seg2, t2, seg1, t1));
		
		// test no overlap
		p.clear();
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(seg1, t1, seg2, t2, p));
		TestCase.assertFalse(this.gjk.detect(seg1, t1, seg2, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertFalse(this.gjk.detect(seg2, t2, seg1, t1, p));
		TestCase.assertFalse(this.gjk.detect(seg2, t2, seg1, t1));
	}
	
	/**
	 * Tests the {@link Gjk} distance method.
	 */
	@Test
	public void gjkDistance() {
		Separation s = new Separation();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 n, p1, p2;
		
		// test overlap
		TestCase.assertFalse(this.gjk.distance(seg1, t1, seg2, t2, s));
		
		// try reversing the shapes
		s.clear();
		TestCase.assertFalse(this.gjk.distance(seg2, t2, seg1, t1, s));
		
		// test AABB overlap
		s.clear();
		t2.translate(0.5, 0.0);
		TestCase.assertTrue(this.gjk.distance(seg1, t1, seg2, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.038, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.768, n.x, 1.0e-3);
		TestCase.assertEquals(-0.640, n.y, 1.0e-3);
		TestCase.assertEquals(-0.029, p1.x, 1.0e-3);
		TestCase.assertEquals(0.024, p1.y, 1.0e-3);
		TestCase.assertEquals(0.000, p2.x, 1.0e-3);
		TestCase.assertEquals(0.000, p2.y, 1.0e-3);
		
		// try reversing the shapes
		s.clear();
		TestCase.assertTrue(this.gjk.distance(seg2, t2, seg1, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.038, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.768, n.x, 1.0e-3);
		TestCase.assertEquals(0.640, n.y, 1.0e-3);
		TestCase.assertEquals(0.000, p1.x, 1.0e-3);
		TestCase.assertEquals(0.000, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.029, p2.x, 1.0e-3);
		TestCase.assertEquals(0.024, p2.y, 1.0e-3);
		
		// test no overlap
		s.clear();
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(seg1, t1, seg2, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.854, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.936, n.x, 1.0e-3);
		TestCase.assertEquals(-0.351, n.y, 1.0e-3);
		TestCase.assertEquals(-0.800, p1.x, 1.0e-3);
		TestCase.assertEquals(0.300, p1.y, 1.0e-3);
		TestCase.assertEquals(0.000, p2.x, 1.0e-3);
		TestCase.assertEquals(0.000, p2.y, 1.0e-3);
		
		// try reversing the shapes
		s.clear();
		TestCase.assertTrue(this.gjk.distance(seg2, t2, seg1, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.854, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.936, n.x, 1.0e-3);
		TestCase.assertEquals(0.351, n.y, 1.0e-3);
		TestCase.assertEquals(0.000, p1.x, 1.0e-3);
		TestCase.assertEquals(0.000, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.800, p2.x, 1.0e-3);
		TestCase.assertEquals(0.300, p2.y, 1.0e-3);
	}
}
