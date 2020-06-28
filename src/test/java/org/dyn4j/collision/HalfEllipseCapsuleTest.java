/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision;

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link HalfEllipse} - {@link Capsule} collision detection.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public class HalfEllipseCapsuleTest extends AbstractTest {
	/** The first test {@link HalfEllipse} */
	private HalfEllipse ellipse;
	
	/** The second test {@link Capsule} */
	private Capsule capsule;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.ellipse = new HalfEllipse(2.0, 0.5);
		this.capsule = new Capsule(1.0, 0.5);
		this.sap.clear();
		this.dyn.clear();
	}
	
	/**
	 * Tests {@link Shape} AABB.
	 */
	@Test
	public void detectShapeAABB() {
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test containment
		TestCase.assertTrue(this.sap.detect(ellipse, t1, capsule, t2));
		TestCase.assertTrue(this.sap.detect(capsule, t2, ellipse, t1));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.sap.detect(ellipse, t1, capsule, t2));
		TestCase.assertTrue(this.sap.detect(capsule, t2, ellipse, t1));
		
		// test only AABB overlap
		t2.translate(0.6, 0.6);
		TestCase.assertTrue(this.sap.detect(ellipse, t1, capsule, t2));
		TestCase.assertTrue(this.sap.detect(capsule, t2, ellipse, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sap.detect(ellipse, t1, capsule, t2));
		TestCase.assertFalse(this.sap.detect(capsule, t2, ellipse, t1));
	}
	
	/**
	 * Tests {@link CollisionBody} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		CollidableTest ct1 = new CollidableTest(ellipse);
		CollidableTest ct2 = new CollidableTest(capsule);
		
		// test containment
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(0.6, 0.6);
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
		List<CollisionPair<CollidableTest, Fixture>> pairs;
		
		// create some collidables
		CollidableTest ct1 = new CollidableTest(ellipse);
		CollidableTest ct2 = new CollidableTest(capsule);
		
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
		ct1.translate(-0.5, 0.0);
		this.sap.update(ct1);
		this.dyn.update(ct1);
		pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// test only AABB overlap
		ct2.translate(0.6, 0.6);
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
	@Test(expected = UnsupportedOperationException.class)
	public void detectSat() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		this.sat.detect(ellipse, t1, capsule, t2, p);
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
		TestCase.assertTrue(this.gjk.detect(ellipse, t1, capsule, t2, p));
		TestCase.assertTrue(this.gjk.detect(ellipse, t1, capsule, t2));
		n = p.getNormal();
		TestCase.assertEquals( 0.000, n.x, 1.0e-3);
		TestCase.assertEquals(-1.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.250, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(capsule, t2, ellipse, t1, p));
		TestCase.assertTrue(this.gjk.detect(capsule, t2, ellipse, t1));
		n = p.getNormal();
		TestCase.assertEquals( 0.000, n.x, 1.0e-3);
		TestCase.assertEquals( 1.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.250, p.getDepth(), 1.0e-3);
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.gjk.detect(ellipse, t1, capsule, t2, p));
		TestCase.assertTrue(this.gjk.detect(ellipse, t1, capsule, t2));
		n = p.getNormal();
		TestCase.assertEquals( 0.000, n.x, 1.0e-3);
		TestCase.assertEquals(-1.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.250, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(capsule, t2, ellipse, t1, p));
		TestCase.assertTrue(this.gjk.detect(capsule, t2, ellipse, t1));
		n = p.getNormal();
		TestCase.assertEquals( 0.000, n.x, 1.0e-3);
		TestCase.assertEquals( 1.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.250, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.6, 0.6);
		TestCase.assertFalse(this.gjk.detect(ellipse, t1, capsule, t2, p));
		TestCase.assertFalse(this.gjk.detect(ellipse, t1, capsule, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(capsule, t2, ellipse, t1, p));
		TestCase.assertFalse(this.gjk.detect(capsule, t2, ellipse, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(ellipse, t1, capsule, t2, p));
		TestCase.assertFalse(this.gjk.detect(ellipse, t1, capsule, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(capsule, t2, ellipse, t1, p));
		TestCase.assertFalse(this.gjk.detect(capsule, t2, ellipse, t1));
	}
	
	/**
	 * Tests the {@link Gjk} distance method.
	 */
	@Test
	public void gjkDistance() {
		Separation s = new Separation();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 n = null;
		Vector2 p1 = null;
		Vector2 p2 = null;
		
		// test containment
		TestCase.assertFalse(this.gjk.distance(ellipse, t1, capsule, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(capsule, t2, ellipse, t1, s));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertFalse(this.gjk.distance(ellipse, t1, capsule, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(capsule, t2, ellipse, t1, s));
		
		// test AABB overlap
		t2.translate(0.6, 0.6);
		TestCase.assertTrue(this.gjk.distance(ellipse, t1, capsule, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.034, s.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.459, n.x, 1.0e-3);
		TestCase.assertEquals( 0.888, n.y, 1.0e-3);
		TestCase.assertEquals( 0.219, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.347, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.235, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.377, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(capsule, t2, ellipse, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.034, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.459, n.x, 1.0e-3);
		TestCase.assertEquals(-0.888, n.y, 1.0e-3);
		TestCase.assertEquals( 0.235, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.377, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.219, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.347, p2.y, 1.0e-3);
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(ellipse, t1, capsule, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.751, s.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.882, n.x, 1.0e-3);
		TestCase.assertEquals( 0.470, n.y, 1.0e-3);
		TestCase.assertEquals(-0.533, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.128, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.129, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.482, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(capsule, t2, ellipse, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.751, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.882, n.x, 1.0e-3);
		TestCase.assertEquals(-0.470, n.y, 1.0e-3);
		TestCase.assertEquals( 0.129, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.482, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.533, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.128, p2.y, 1.0e-3);
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
		
		ManifoldPoint mp1, mp2;
		Vector2 p1, p2;
		
		// test containment gjk
		this.gjk.detect(ellipse, t1, capsule, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, ellipse, t1, capsule, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		this.gjk.detect(capsule, t2, ellipse, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, capsule, t2, ellipse, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		t1.translate(-0.5, 0.0);
		
		// test overlap gjk
		this.gjk.detect(ellipse, t1, capsule, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, ellipse, t1, capsule, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		p1 = mp1.getPoint();
		TestCase.assertEquals( 0.300, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.250, mp1.getDepth(), 1.0e-3);
		mp2 = m.getPoints().get(1);
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.300, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p2.y, 1.0e-3);
		TestCase.assertEquals( 0.250, mp2.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.gjk.detect(capsule, t2, ellipse, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, capsule, t2, ellipse, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		p1 = mp1.getPoint();
		TestCase.assertEquals( 0.300, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.250, mp1.getDepth(), 1.0e-3);
		mp2 = m.getPoints().get(1);
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.300, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p2.y, 1.0e-3);
		TestCase.assertEquals( 0.250, mp2.getDepth(), 1.0e-3);
	}
}
