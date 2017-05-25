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
package org.dyn4j.collision;

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Polygon} - {@link Segment} collision detection.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.0.0
 */
public class PolygonSegmentTest extends AbstractTest {
	/** The test {@link Polygon} */
	private Polygon poly;
	
	/** The test {@link Segment} */
	private Segment seg;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.poly = Geometry.createUnitCirclePolygon(5, 1.0);
		this.seg = new Segment(new Vector2(-0.5, 0.0), new Vector2(0.0, 0.5));
	}
	
	/**
	 * Tests {@link Shape} AABB.
	 */
	@Test
	public void detectShapeAABB() {
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test containment
		TestCase.assertTrue(this.sap.detect(poly, t1, seg, t2));
		TestCase.assertTrue(this.sap.detect(seg, t2, poly, t1));
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sap.detect(poly, t1, seg, t2));
		TestCase.assertTrue(this.sap.detect(seg, t2, poly, t1));
		
		// test only AABB overlap
		t2.translate(0.0, 0.9);
		TestCase.assertTrue(this.sap.detect(poly, t1, seg, t2));
		TestCase.assertTrue(this.sap.detect(seg, t2, poly, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sap.detect(poly, t1, seg, t2));
		TestCase.assertFalse(this.sap.detect(seg, t2, poly, t1));
	}
	
	/**
	 * Tests {@link Collidable} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		CollidableTest ct1 = new CollidableTest(poly);
		CollidableTest ct2 = new CollidableTest(seg);
		
		// test containment
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(0.0, 0.9);
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test no overlap
		ct1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sap.detect(ct1, ct2));
		TestCase.assertFalse(this.sap.detect(ct2, ct1));
	}
	
	/**
	 * Tests the broadphase detectors.
	 */
	@Test
	public void detectBroadphase() {
		List<BroadphasePair<CollidableTest, Fixture>> pairs;
		
		// create some collidables
		CollidableTest ct1 = new CollidableTest(poly);
		CollidableTest ct2 = new CollidableTest(seg);
		
		this.sap.add(ct1);
		this.sap.add(ct2);
		this.dyn.add(ct1);
		this.dyn.add(ct2);
		
		// test containment
		pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test overlap
		ct1.translate(-1.0, 0.0);
		this.sap.update(ct1);
		this.dyn.update(ct1);
		pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test only AABB overlap
		ct2.translate(0.0, 0.9);
		this.sap.update(ct2);
		this.dyn.update(ct2);
		pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test no overlap
		ct1.translate(-1.0, 0.0);
		this.sap.update(ct1);
		this.dyn.update(ct1);
		pairs = this.sap.detect();
		TestCase.assertEquals(0, pairs.size());
		pairs = this.dyn.detect();
		TestCase.assertEquals(0, pairs.size());
	}
	
	/**
	 * Tests {@link Sat}.
	 */
	@Test
	public void detectSat() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 n = null;
		
		// test containment
		TestCase.assertTrue(this.sat.detect(poly, t1, seg, t2, p));
		TestCase.assertTrue(this.sat.detect(poly, t1, seg, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.634, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.707, n.x, 1.0e-3);
		TestCase.assertEquals(0.707, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(seg, t2, poly, t1, p));
		TestCase.assertTrue(this.sat.detect(seg, t2, poly, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.634, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.707, n.x, 1.0e-3);
		TestCase.assertEquals(-0.707, n.y, 1.0e-3);
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sat.detect(poly, t1, seg, t2, p));
		TestCase.assertTrue(this.sat.detect(poly, t1, seg, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.404, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.809, n.x, 1.0e-3);
		TestCase.assertEquals(0.587, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(seg, t2, poly, t1, p));
		TestCase.assertTrue(this.sat.detect(seg, t2, poly, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.404, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.809, n.x, 1.0e-3);
		TestCase.assertEquals(-0.587, n.y, 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, 0.9);
		TestCase.assertFalse(this.sat.detect(poly, t1, seg, t2, p));
		TestCase.assertFalse(this.sat.detect(poly, t1, seg, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(seg, t2, poly, t1, p));
		TestCase.assertFalse(this.sat.detect(seg, t2, poly, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sat.detect(poly, t1, seg, t2, p));
		TestCase.assertFalse(this.sat.detect(poly, t1, seg, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(seg, t2, poly, t1, p));
		TestCase.assertFalse(this.sat.detect(seg, t2, poly, t1));
	}
	
	/**
	 * Tests {@link Gjk}.
	 */
	@Test
	public void detectGjk() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 n = null;
		
		// test containment
		TestCase.assertTrue(this.gjk.detect(poly, t1, seg, t2, p));
		TestCase.assertTrue(this.gjk.detect(poly, t1, seg, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.634, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.707, n.x, 1.0e-3);
		TestCase.assertEquals(0.707, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(seg, t2, poly, t1, p));
		TestCase.assertTrue(this.gjk.detect(seg, t2, poly, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.634, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.707, n.x, 1.0e-3);
		TestCase.assertEquals(-0.707, n.y, 1.0e-3);
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.detect(poly, t1, seg, t2, p));
		TestCase.assertTrue(this.gjk.detect(poly, t1, seg, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.404, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.809, n.x, 1.0e-3);
		TestCase.assertEquals(0.587, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(seg, t2, poly, t1, p));
		TestCase.assertTrue(this.gjk.detect(seg, t2, poly, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.404, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.809, n.x, 1.0e-3);
		TestCase.assertEquals(-0.587, n.y, 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, 0.9);
		TestCase.assertFalse(this.gjk.detect(poly, t1, seg, t2, p));
		TestCase.assertFalse(this.gjk.detect(poly, t1, seg, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(seg, t2, poly, t1, p));
		TestCase.assertFalse(this.gjk.detect(seg, t2, poly, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(poly, t1, seg, t2, p));
		TestCase.assertFalse(this.gjk.detect(poly, t1, seg, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(seg, t2, poly, t1, p));
		TestCase.assertFalse(this.gjk.detect(seg, t2, poly, t1));
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
		
		// test containment
		TestCase.assertFalse(this.gjk.distance(poly, t1, seg, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(seg, t2, poly, t1, s));
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.distance(poly, t1, seg, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(seg, t2, poly, t1, s));
		
		// test AABB overlap
		t2.translate(0.0, 1.3);
		TestCase.assertTrue(this.gjk.distance(poly, t1, seg, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.397, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.480, n.x, 1.0e-3);
		TestCase.assertEquals(0.877, n.y, 1.0e-3);
		TestCase.assertEquals(-0.690, p1.x, 1.0e-3);
		TestCase.assertEquals(0.951, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p2.x, 1.0e-3);
		TestCase.assertEquals(1.300, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(seg, t2, poly, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.397, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.480, n.x, 1.0e-3);
		TestCase.assertEquals(-0.877, n.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(1.300, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.690, p2.x, 1.0e-3);
		TestCase.assertEquals(0.951, p2.y, 1.0e-3);
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(poly, t1, seg, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(1.168, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.809, n.x, 1.0e-3);
		TestCase.assertEquals(0.587, n.y, 1.0e-3);
		TestCase.assertEquals(-1.445, p1.x, 1.0e-3);
		TestCase.assertEquals(0.613, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p2.x, 1.0e-3);
		TestCase.assertEquals(1.3, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(seg, t2, poly, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(1.168, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.809, n.x, 1.0e-3);
		TestCase.assertEquals(-0.587, n.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(1.3, p1.y, 1.0e-3);
		TestCase.assertEquals(-1.445, p2.x, 1.0e-3);
		TestCase.assertEquals(0.613, p2.y, 1.0e-3);
	}
	
	/**
	 * Test the {@link ClippingManifoldSolver}.
	 */
	@Test
	public void getClipManifold() {
		Manifold m = new Manifold();
		Penetration p = new Penetration();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		ManifoldPoint mp = null;
		Vector2 p1 = null;
		
		// test containment gjk
		this.gjk.detect(poly, t1, seg, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, poly, t1, seg, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		this.gjk.detect(seg, t2, poly, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, seg, t2, poly, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		// test containment sat
		this.sat.detect(poly, t1, seg, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, poly, t1, seg, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		this.sat.detect(seg, t2, poly, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, seg, t2, poly, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		t1.translate(-1.0, 0.0);
		
		// test overlap gjk
		this.gjk.detect(poly, t1, seg, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, poly, t1, seg, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(0.000, p1.y, 1.0e-3);
		TestCase.assertEquals(0.404, mp.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.gjk.detect(seg, t2, poly, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, seg, t2, poly, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(0.000, p1.y, 1.0e-3);
		TestCase.assertEquals(0.404, mp.getDepth(), 1.0e-3);
		
		// test overlap sat
		this.sat.detect(poly, t1, seg, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, poly, t1, seg, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(0.0, p1.y, 1.0e-3);
		TestCase.assertEquals(0.404, mp.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.sat.detect(seg, t2, poly, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, seg, t2, poly, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(0.000, p1.y, 1.0e-3);
		TestCase.assertEquals(0.404, mp.getDepth(), 1.0e-3);
	}
	
	/**
	 * This method is to test a case where a {@link Segment}
	 * to {@link Polygon} manifold detection would return
	 * zero manifold points.
	 * <p>
	 * This was caused by the {@link Segment} having an
	 * arbitrary winding direction.
	 * <p>
	 * The fix was to determine what winding to give to the
	 * manifold solver given the penetration direction.
	 */
	@Test
	public void noManifoldPoints() {
		Manifold m = new Manifold();
		Penetration p = new Penetration();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		ManifoldPoint mp1, mp2;
		Vector2 p1, p2;
		
		t1.translate(-1.0, 0.0);
		t2.translate(-0.6484375, -0.9375);
		
		// try with SAT
		TestCase.assertTrue(this.sat.detect(poly, t1, seg, t2, p));
		TestCase.assertTrue(this.cmfs.getManifold(p, poly, t1, seg, t2, m));
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.690, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.951, p1.y, 1.0e-3);
		TestCase.assertEquals(0.333, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.456, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.628, p2.y, 1.0e-3);
		TestCase.assertEquals(0.270, mp2.getDepth(), 1.0e-3);
		// try to reverse the shapes
		TestCase.assertTrue(this.sat.detect(seg, t2, poly, t1, p));
		TestCase.assertTrue(this.cmfs.getManifold(p, seg, t2, poly, t1, m));
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.690, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.951, p1.y, 1.0e-3);
		TestCase.assertEquals(0.333, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.456, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.628, p2.y, 1.0e-3);
		TestCase.assertEquals(0.270, mp2.getDepth(), 1.0e-3);
		
		// try with GJK
		TestCase.assertTrue(this.gjk.detect(poly, t1, seg, t2, p));
		TestCase.assertTrue(this.cmfs.getManifold(p, poly, t1, seg, t2, m));
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.690, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.951, p1.y, 1.0e-3);
		TestCase.assertEquals(0.333, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.456, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.628, p2.y, 1.0e-3);
		TestCase.assertEquals(0.270, mp2.getDepth(), 1.0e-3);
		// try to reverse the shapes
		TestCase.assertTrue(this.gjk.detect(seg, t2, poly, t1, p));
		TestCase.assertTrue(this.cmfs.getManifold(p, seg, t2, poly, t1, m));
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.690, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.951, p1.y, 1.0e-3);
		TestCase.assertEquals(0.333, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.456, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.628, p2.y, 1.0e-3);
		TestCase.assertEquals(0.270, mp2.getDepth(), 1.0e-3);
	}
	
	/**
	 * Tests a case where the manifold solver would find the wrong
	 * contact points when a thin object intersected a polygon.
	 * @since 1.1.0
	 */
	@Test
	public void testBadPoints1() {
		Polygon p = Geometry.createUnitCirclePolygon(5, 1.0);
		Segment s = new Segment(new Vector2(-0.5, 0.0), new Vector2(0.5, 0.0));
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();

		// polygon
		// [1.0 0.0 | 1.3671875]
		// [0.0 1.0 | 0.8828125]
		
		// segment
		// [1.0 0.0 | 0.171875]
		// [0.0 1.0 | 1.0     ]
		
		t1.translate(1.3671875, 0.8828125);
		t2.translate(0.171875, 1.0);
		
		Penetration penetration = new Penetration();
		TestCase.assertTrue(this.gjk.detect(s, t2, p, t1, penetration));
		
		Manifold manifold = new Manifold();
		TestCase.assertTrue(this.cmfs.getManifold(penetration, s, t2, p, t1, manifold));
		
		ManifoldPoint mp1 = manifold.getPoints().get(0);
		Vector2 p1 = mp1.getPoint();
		TestCase.assertEquals(0.671, p1.x, 1.0e-3);
		TestCase.assertEquals(1.000, p1.y, 1.0e-3);
		TestCase.assertEquals(0.113, mp1.getDepth(), 1.0e-3);
	}
}
