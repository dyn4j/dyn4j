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
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Ellipse} - {@link Ellipse} collision detection.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public class EllipseEllipseTest extends AbstractTest {
	/** The first test {@link Ellipse} */
	private Ellipse c1;
	
	/** The second test {@link Ellipse} */
	private Ellipse c2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.c1 = new Ellipse(1.0, 0.5);
		this.c2 = new Ellipse(0.5, 1.0);
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
		TestCase.assertTrue(this.sap.detect(c1, t1, c2, t2));
		TestCase.assertTrue(this.sap.detect(c2, t2, c1, t1));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.sap.detect(c1, t1, c2, t2));
		TestCase.assertTrue(this.sap.detect(c2, t2, c1, t1));
		
		// test only AABB overlap
		t2.translate(0.0, -0.62);
		TestCase.assertTrue(this.sap.detect(c1, t1, c2, t2));
		TestCase.assertTrue(this.sap.detect(c2, t2, c1, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sap.detect(c1, t1, c2, t2));
		TestCase.assertFalse(this.sap.detect(c2, t2, c1, t1));
	}
	
	/**
	 * Tests {@link Collidable} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		CollidableTest ct1 = new CollidableTest(c1);
		CollidableTest ct2 = new CollidableTest(c2);
		
		// test containment
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(0.0, -0.62);
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
		CollidableTest ct1 = new CollidableTest(c1);
		CollidableTest ct2 = new CollidableTest(c2);
		
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
		ct2.translate(0.0, -0.62);
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
		this.sat.detect(c1, t1, c2, t2, p);
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
		TestCase.assertTrue(this.gjk.detect(c1, t1, c2, t2, p));
		TestCase.assertTrue(this.gjk.detect(c1, t1, c2, t2));
		n = p.getNormal();
		TestCase.assertEquals(-1.000, n.x, 1.0e-3);
		TestCase.assertEquals( 0.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.750, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(c2, t2, c1, t1, p));
		TestCase.assertTrue(this.gjk.detect(c2, t2, c1, t1));
		n = p.getNormal();
		TestCase.assertEquals(-1.000, n.x, 1.0e-3);
		TestCase.assertEquals( 0.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.750, p.getDepth(), 1.0e-3);
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.gjk.detect(c1, t1, c2, t2, p));
		TestCase.assertTrue(this.gjk.detect(c1, t1, c2, t2));
		n = p.getNormal();
		TestCase.assertEquals( 1.000, n.x, 1.0e-3);
		TestCase.assertEquals( 0.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.250, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(c2, t2, c1, t1, p));
		TestCase.assertTrue(this.gjk.detect(c2, t2, c1, t1));
		n = p.getNormal();
		TestCase.assertEquals(-1.000, n.x, 1.0e-3);
		TestCase.assertEquals( 0.000, n.y, 1.0e-3);
		TestCase.assertEquals( 0.250, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, -0.62);
		TestCase.assertFalse(this.gjk.detect(c1, t1, c2, t2, p));
		TestCase.assertFalse(this.gjk.detect(c1, t1, c2, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(c2, t2, c1, t1, p));
		TestCase.assertFalse(this.gjk.detect(c2, t2, c1, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(c1, t1, c2, t2, p));
		TestCase.assertFalse(this.gjk.detect(c1, t1, c2, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(c2, t2, c1, t1, p));
		TestCase.assertFalse(this.gjk.detect(c2, t2, c1, t1));
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
		TestCase.assertFalse(this.gjk.distance(c1, t1, c2, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(c2, t2, c1, t1, s));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertFalse(this.gjk.distance(c1, t1, c2, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(c2, t2, c1, t1, s));
		
		// test AABB overlap
		t2.translate(0.0, -0.62);
		TestCase.assertTrue(this.gjk.distance(c1, t1, c2, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.008, s.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.583, n.x, 1.0e-3);
		TestCase.assertEquals(-0.812, n.y, 1.0e-3);
		TestCase.assertEquals(-0.089, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.142, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.084, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.149, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(c2, t2, c1, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.008, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.583, n.x, 1.0e-3);
		TestCase.assertEquals( 0.812, n.y, 1.0e-3);
		TestCase.assertEquals(-0.084, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.149, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.089, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.142, p2.y, 1.0e-3);
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(c1, t1, c2, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.853, s.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.942, n.x, 1.0e-3);
		TestCase.assertEquals(-0.335, n.y, 1.0e-3);
		TestCase.assertEquals(-1.007, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.043, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.203, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.329, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(c2, t2, c1, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.853, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.942, n.x, 1.0e-3);
		TestCase.assertEquals( 0.335, n.y, 1.0e-3);
		TestCase.assertEquals(-0.203, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.329, p1.y, 1.0e-3);
		TestCase.assertEquals(-1.007, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.043, p2.y, 1.0e-3);
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
		this.gjk.detect(c1, t1, c2, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, c1, t1, c2, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		// try reversing the shapes
		this.gjk.detect(c2, t2, c1, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, c2, t2, c1, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		t1.translate(-0.65, 0.0);
		
		// test overlap gjk
		this.gjk.detect(c1, t1, c2, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, c1, t1, c2, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.100, mp.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.gjk.detect(c2, t2, c1, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, c2, t2, c1, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(-0.249, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.000, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.100, mp.getDepth(), 1.0e-3);
	}
	
	/**
	 * Tests a false positive case with the GJK algorithm.
	 */
	@Test
	public void falsePositiveCollision() {
		Penetration p = new Penetration();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test AABB overlap
		t2.translate(-0.5, -0.6);
		TestCase.assertTrue(this.gjk.detect(c1, t1, c2, t2, p));
	}
	
	/**
	 * Tests a false negative case with the GJK algorithm.
	 */
	@Test
	public void falseNegativeDistance() {
		Separation s = new Separation();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test AABB overlap
		t2.translate(-0.5, -0.6);
		TestCase.assertFalse(this.gjk.distance(c1, t1, c2, t2, s));
	}
}
