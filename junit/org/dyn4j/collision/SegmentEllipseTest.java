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
package org.dyn4j.collision;

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Segment} - {@link Ellipse} collision detection.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public class SegmentEllipseTest extends AbstractTest {
	/** The test {@link Segment} */
	private Segment s;
	
	/** The test {@link Ellipse} */
	private Ellipse e;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.s = new Segment(new Vector2(-1.5, 1.0), new Vector2(1.5, -1.0));
		this.e = new Ellipse(1.0, 0.5);
	}
	
	/**
	 * Tests {@link Shape} AABB.
	 */
	@Test
	public void detectShapeAABB() {
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(s, t1, e, t2));
		TestCase.assertTrue(this.aabb.detect(e, t2, s, t1));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.aabb.detect(s, t1, e, t2));
		TestCase.assertTrue(this.aabb.detect(e, t2, s, t1));
		
		// test only AABB overlap
		t2.translate(1.0, 0.0);
		TestCase.assertTrue(this.aabb.detect(s, t1, e, t2));
		TestCase.assertTrue(this.aabb.detect(e, t2, s, t1));
		
		// test no overlap
		t2.translate(3.0, 2.0);
		TestCase.assertFalse(this.aabb.detect(s, t1, e, t2));
		TestCase.assertFalse(this.aabb.detect(e, t2, s, t1));
	}
	
	/**
	 * Tests {@link Collidable} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		CollidableTest ct1 = new CollidableTest(s);
		CollidableTest ct2 = new CollidableTest(e);
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(1.0, 0.0);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test no overlap
		ct2.translate(3.0, 2.0);
		TestCase.assertFalse(this.aabb.detect(ct1, ct2));
		TestCase.assertFalse(this.aabb.detect(ct2, ct1));
	}
	
	/**
	 * Tests the broadphase detectors.
	 */
	@Test
	public void detectBroadphase() {
		List<BroadphasePair<CollidableTest>> pairs;
		
		// create some collidables
		CollidableTest ct1 = new CollidableTest(s);
		CollidableTest ct2 = new CollidableTest(e);
		
		this.sapI.add(ct1);
		this.sapI.add(ct2);
		this.sapBF.add(ct1);
		this.sapBF.add(ct2);
		this.sapT.add(ct1);
		this.sapT.add(ct2);
		this.dynT.add(ct1);
		this.dynT.add(ct2);
		
		// test containment
		pairs = this.sapI.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.sapBF.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.sapT.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dynT.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test overlap
		ct1.translate(-0.5, 0.0);
		this.sapI.update(ct1);
		this.sapBF.update(ct1);
		this.sapT.update(ct1);
		this.dynT.update(ct1);
		pairs = this.sapI.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.sapBF.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.sapT.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dynT.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test only AABB overlap
		ct2.translate(1.0, 0.0);
		this.sapI.update(ct2);
		this.sapBF.update(ct2);
		this.sapT.update(ct2);
		this.dynT.update(ct2);
		pairs = this.sapI.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.sapBF.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.sapT.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dynT.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test no overlap
		ct2.translate(3.0, 2.0);
		this.sapI.update(ct2);
		this.sapBF.update(ct2);
		this.sapT.update(ct2);
		this.dynT.update(ct2);
		pairs = this.sapI.detect();
		TestCase.assertEquals(0, pairs.size());
		pairs = this.sapBF.detect();
		TestCase.assertEquals(0, pairs.size());
		pairs = this.sapT.detect();
		TestCase.assertEquals(0, pairs.size());
		pairs = this.dynT.detect();
		TestCase.assertEquals(0, pairs.size());
	}
	
	/**
	 * Tests that SAT is not supported.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void detectSat() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		this.sat.detect(s, t1, e, t2, p);
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
		TestCase.assertTrue(this.gjk.detect(s, t1, e, t2, p));
		TestCase.assertTrue(this.gjk.detect(s, t1, e, t2));
		n = p.getNormal();
		TestCase.assertEquals(-0.554, n.x, 1.0e-3);
		TestCase.assertEquals(-0.832, n.y, 1.0e-3);
		TestCase.assertEquals( 0.346, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(e, t2, s, t1, p));
		TestCase.assertTrue(this.gjk.detect(e, t2, s, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.554, n.x, 1.0e-3);
		TestCase.assertEquals(-0.832, n.y, 1.0e-3);
		TestCase.assertEquals( 0.346, p.getDepth(), 1.0e-3);
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.gjk.detect(s, t1, e, t2, p));
		TestCase.assertTrue(this.gjk.detect(s, t1, e, t2));
		n = p.getNormal();
		TestCase.assertEquals( 0.554, n.x, 1.0e-3);
		TestCase.assertEquals( 0.832, n.y, 1.0e-3);
		TestCase.assertEquals( 0.069, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(e, t2, s, t1, p));
		TestCase.assertTrue(this.gjk.detect(e, t2, s, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.554, n.x, 1.0e-3);
		TestCase.assertEquals(-0.832, n.y, 1.0e-3);
		TestCase.assertEquals( 0.069, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(s, t1, e, t2, p));
		TestCase.assertFalse(this.gjk.detect(s, t1, e, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(e, t2, s, t1, p));
		TestCase.assertFalse(this.gjk.detect(e, t2, s, t1));
		
		// test no overlap
		t2.translate(3.0, 2.0);
		TestCase.assertFalse(this.gjk.detect(s, t1, e, t2, p));
		TestCase.assertFalse(this.gjk.detect(s, t1, e, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(e, t2, s, t1, p));
		TestCase.assertFalse(this.gjk.detect(e, t2, s, t1));
	}
	
	/**
	 * Tests the {@link Gjk} distance method.
	 */
	@Test
	public void gjkDistance() {
		Separation se = new Separation();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 n, p1, p2;
		
		// test containment
		TestCase.assertFalse(this.gjk.distance(s, t1, e, t2, se));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(e, t2, s, t1, se));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertFalse(this.gjk.distance(s, t1, e, t2, se));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(e, t2, s, t1, se));
		
		// test AABB overlap
		t2.translate(1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(s, t1, e, t2, se));
		n = se.getNormal();
		p1 = se.getPoint1();
		p2 = se.getPoint2();
		TestCase.assertEquals( 0.485, se.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.554, n.x, 1.0e-3);
		TestCase.assertEquals( 0.832, n.y, 1.0e-3);
		TestCase.assertEquals( 0.330, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.553, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.599, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.149, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(e, t2, s, t1, se));
		n = se.getNormal();
		p1 = se.getPoint1();
		p2 = se.getPoint2();
		TestCase.assertEquals( 0.485, se.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.554, n.x, 1.0e-3);
		TestCase.assertEquals(-0.832, n.y, 1.0e-3);
		TestCase.assertEquals( 0.599, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.149, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.330, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.553, p2.y, 1.0e-3);
		
		// test no overlap
		t2.translate(3.0, 2.0);
		TestCase.assertTrue(this.gjk.distance(s, t1, e, t2, se));
		n = se.getNormal();
		p1 = se.getPoint1();
		p2 = se.getPoint2();
		TestCase.assertEquals( 3.854, se.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.665, n.x, 1.0e-3);
		TestCase.assertEquals( 0.746, n.y, 1.0e-3);
		TestCase.assertEquals( 1.000, p1.x, 1.0e-3);
		TestCase.assertEquals(-1.000, p1.y, 1.0e-3);
		TestCase.assertEquals( 3.563, p2.x, 1.0e-3);
		TestCase.assertEquals( 1.877, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(e, t2, s, t1, se));
		n = se.getNormal();
		p1 = se.getPoint1();
		p2 = se.getPoint2();
		TestCase.assertEquals( 3.854, se.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.665, n.x, 1.0e-3);
		TestCase.assertEquals(-0.746, n.y, 1.0e-3);
		TestCase.assertEquals( 3.563, p1.x, 1.0e-3);
		TestCase.assertEquals( 1.877, p1.y, 1.0e-3);
		TestCase.assertEquals( 1.000, p2.x, 1.0e-3);
		TestCase.assertEquals(-1.000, p2.y, 1.0e-3);
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
		
		ManifoldPoint mp;
		Vector2 p1;
		
		// test containment gjk
		this.gjk.detect(s, t1, e, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, s, t1, e, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		// try reversing the shapes
		this.gjk.detect(e, t2, s, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, e, t2, s, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		t1.translate(-0.5, 0.0);
		
		// test overlap gjk
		this.gjk.detect(s, t1, e, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, s, t1, e, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.400, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.149, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.069, mp.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.gjk.detect(e, t2, s, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, e, t2, s, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.400, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.149, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.069, mp.getDepth(), 1.0e-3);
	}
}
