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
package org.dyn4j.collision.shapes;

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Circle} - {@link Rectangle} collision detection.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.0.0
 */
public class CircleRectangleTest extends AbstractTest {
	/** The test {@link Circle} */
	private Circle circ;
	
	/** The test {@link Rectangle} */
	private Rectangle rect;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.circ = new Circle(1.0);
		this.rect = new Rectangle(1.0, 1.0);
	}
	
	/**
	 * Tests {@link Shape} AABB.
	 */
	@Test
	public void detectShapeAABB() {
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test containment
		TestCase.assertTrue(this.sap.detect(circ, t1, rect, t2));
		TestCase.assertTrue(this.sap.detect(rect, t2, circ, t1));
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sap.detect(circ, t1, rect, t2));
		TestCase.assertTrue(this.sap.detect(rect, t2, circ, t1));
		
		// test only AABB overlap
		t2.translate(0.0, 1.4);
		TestCase.assertTrue(this.sap.detect(circ, t1, rect, t2));
		TestCase.assertTrue(this.sap.detect(rect, t2, circ, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sap.detect(circ, t1, rect, t2));
		TestCase.assertFalse(this.sap.detect(rect, t2, circ, t1));
	}
	
	/**
	 * Tests {@link CollisionBody} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		TestCollisionBody ct1 = new TestCollisionBody(circ);
		TestCollisionBody ct2 = new TestCollisionBody(rect);
		
		// test containment
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sap.detect(ct1, ct2));
		TestCase.assertTrue(this.sap.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(0.0, 1.4);
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
		List<CollisionPair<TestCollisionBody, Fixture>> pairs;
		
		// create some collidables
		TestCollisionBody ct1 = new TestCollisionBody(circ);
		TestCollisionBody ct2 = new TestCollisionBody(rect);
		
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
		ct2.translate(0.0, 1.4);
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
		TestCase.assertTrue(this.sat.detect(circ, t1, rect, t2, p));
		TestCase.assertTrue(this.sat.detect(circ, t1, rect, t2));
		n = p.getNormal();
		TestCase.assertEquals(1.0, n.x, 1.0e-10);
		TestCase.assertEquals(0.0, n.y, 1.0e-10);
		TestCase.assertEquals(1.5, p.getDepth(), 1.0e-10);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(rect, t2, circ, t1, p));
		TestCase.assertTrue(this.sat.detect(rect, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(1.0, n.x, 1.0e-3);
		TestCase.assertEquals(0.0, n.y, 1.0e-10);
		TestCase.assertEquals(1.5, p.getDepth(), 1.0e-3);
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sat.detect(circ, t1, rect, t2, p));
		TestCase.assertTrue(this.sat.detect(circ, t1, rect, t2));
		n = p.getNormal();
		TestCase.assertEquals(1.0, n.x);
		TestCase.assertEquals(0.0, n.y);
		TestCase.assertEquals(0.5, p.getDepth());
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(rect, t2, circ, t1, p));
		TestCase.assertTrue(this.sat.detect(rect, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(-1.0, n.x);
		TestCase.assertEquals(0.0, Math.abs(n.y));
		TestCase.assertEquals(0.5, p.getDepth());
		
		// test AABB overlap
		t2.translate(0.0, 1.4);
		TestCase.assertFalse(this.sat.detect(circ, t1, rect, t2, p));
		TestCase.assertFalse(this.sat.detect(circ, t1, rect, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(rect, t2, circ, t1, p));
		TestCase.assertFalse(this.sat.detect(rect, t2, circ, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sat.detect(circ, t1, rect, t2, p));
		TestCase.assertFalse(this.sat.detect(circ, t1, rect, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(rect, t2, circ, t1, p));
		TestCase.assertFalse(this.sat.detect(rect, t2, circ, t1));
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
		TestCase.assertTrue(this.gjk.detect(circ, t1, rect, t2, p));
		TestCase.assertTrue(this.gjk.detect(circ, t1, rect, t2));
		n = p.getNormal();
		TestCase.assertEquals( 1.500, p.getDepth(), 1.0e-4);
		TestCase.assertEquals( 1.000, n.x, 1.0e-4);
		TestCase.assertEquals( 0.000, n.y, 1.0e-4);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(rect, t2, circ, t1, p));
		TestCase.assertTrue(this.gjk.detect(rect, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals( 1.500, p.getDepth(), 1.0e-4);
		TestCase.assertEquals( 0.000, n.x, 1.0e-4);
		TestCase.assertEquals( 1.000, n.y, 1.0e-4);
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.detect(circ, t1, rect, t2, p));
		TestCase.assertTrue(this.gjk.detect(circ, t1, rect, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.5, p.getDepth(), 1.0e-4);
		TestCase.assertEquals(1.0, n.x, 1.0e-4);
		TestCase.assertEquals(0.0, n.y, 1.0e-4);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(rect, t2, circ, t1, p));
		TestCase.assertTrue(this.gjk.detect(rect, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.5, p.getDepth(), 1.0e-4);
		TestCase.assertEquals(-1.0, n.x, 1.0e-4);
		TestCase.assertEquals(0.0, n.y, 1.0e-4);
		
		// test AABB overlap
		t2.translate(0.0, 1.4);
		TestCase.assertFalse(this.gjk.detect(circ, t1, rect, t2, p));
		TestCase.assertFalse(this.gjk.detect(circ, t1, rect, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(rect, t2, circ, t1, p));
		TestCase.assertFalse(this.gjk.detect(rect, t2, circ, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(circ, t1, rect, t2, p));
		TestCase.assertFalse(this.gjk.detect(circ, t1, rect, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(rect, t2, circ, t1, p));
		TestCase.assertFalse(this.gjk.detect(rect, t2, circ, t1));
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
		TestCase.assertFalse(this.gjk.distance(circ, t1, rect, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(rect, t2, circ, t1, s));
		
		// test overlap
		t1.translate(-0.9, 0.0);
		TestCase.assertFalse(this.gjk.distance(circ, t1, rect, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(rect, t2, circ, t1, s));
		
		t1.translate(-0.1, 0.0);
		TestCase.assertTrue(this.gjk.distance(circ, t1, rect, t2, s));
		TestCase.assertEquals(0.000, s.getDistance(), 1e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(rect, t2, circ, t1, s));
		TestCase.assertEquals(0.000, s.getDistance(), 1e-3);
		
		// test AABB overlap
		t2.translate(0.0, 1.4);
		TestCase.assertTrue(this.gjk.distance(circ, t1, rect, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.029, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.485, n.x, 1.0e-3);
		TestCase.assertEquals(0.874, n.y, 1.0e-3);
		TestCase.assertEquals(-0.514, p1.x, 1.0e-3);
		TestCase.assertEquals(0.874, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p2.x, 1.0e-3);
		TestCase.assertEquals(0.900, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(rect, t2, circ, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.029, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.485, n.x, 1.0e-3);
		TestCase.assertEquals(-0.874, n.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(0.900, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.514, p2.x, 1.0e-3);
		TestCase.assertEquals(0.874, p2.y, 1.0e-3);
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(circ, t1, rect, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.749, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.857, n.x, 1.0e-3);
		TestCase.assertEquals(0.514, n.y, 1.0e-3);
		TestCase.assertEquals(-1.142, p1.x, 1.0e-3);
		TestCase.assertEquals(0.514, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p2.x, 1.0e-3);
		TestCase.assertEquals(0.900, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(rect, t2, circ, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.749, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.857, n.x, 1.0e-3);
		TestCase.assertEquals(-0.514, n.y, 1.0e-3);
		TestCase.assertEquals(-0.500, p1.x, 1.0e-3);
		TestCase.assertEquals(0.900, p1.y, 1.0e-3);
		TestCase.assertEquals(-1.142, p2.x, 1.0e-3);
		TestCase.assertEquals(0.514, p2.y, 1.0e-3);
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
		this.gjk.detect(circ, t1, rect, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, rect, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		// try reversing the shapes
		this.gjk.detect(rect, t2, circ, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		// test containment sat
		this.sat.detect(circ, t1, rect, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, rect, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		// try reversing the shapes
		this.sat.detect(rect, t2, circ, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		t1.translate(-1.0, 0.0);
		
		// test overlap gjk
		this.gjk.detect(circ, t1, rect, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, rect, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.0, p1.x, 1.0e-4);
		TestCase.assertEquals(0.0, p1.y, 1.0e-4);
		TestCase.assertEquals(0.5, mp.getDepth(), 1.0e-4);
		// try reversing the shapes
		this.gjk.detect(rect, t2, circ, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.0, p1.x, 1.0e-4);
		TestCase.assertEquals(0.0, p1.y, 1.0e-4);
		TestCase.assertEquals(0.5, mp.getDepth(), 1.0e-4);
		
		// test overlap sat
		this.sat.detect(circ, t1, rect, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, rect, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.0, p1.x);
		TestCase.assertEquals(0.0, p1.y);
		TestCase.assertEquals(0.5, mp.getDepth());
		// try reversing the shapes
		this.sat.detect(rect, t2, circ, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.0, p1.x);
		TestCase.assertEquals(0.0, p1.y);
		TestCase.assertEquals(0.5, mp.getDepth());
	}
}
