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
package org.dyn4j.game2d.collision;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.game2d.collision.broadphase.BroadphasePair;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldPoint;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.collision.narrowphase.Separation;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Rectangle} - {@link Triangle} collision detection.
 * @author William Bittle
 * @version 1.1.0
 * @since 1.0.0
 */
public class RectangleTriangleTest extends AbstractTest {
	/** The test {@link Rectangle} */
	private Rectangle rect;
	
	/** The test {@link Triangle} */
	private Triangle tri;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.rect = new Rectangle(1.0, 1.0);
		this.tri = new Triangle(
				new Vector2(0.45, -0.12),
				new Vector2(-0.45, 0.38),
				new Vector2(-0.15, -0.22));
	}
	
	/**
	 * Tests {@link Shape} AABB.
	 */
	@Test
	public void detectShapeAABB() {
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(rect, t1, tri, t2));
		TestCase.assertTrue(this.aabb.detect(tri, t2, rect, t1));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.aabb.detect(rect, t1, tri, t2));
		TestCase.assertTrue(this.aabb.detect(tri, t2, rect, t1));
		
		// test AABB overlap
		t2.translate(0.34, 0.42);
		TestCase.assertTrue(this.aabb.detect(rect, t1, tri, t2));
		TestCase.assertTrue(this.aabb.detect(tri, t2, rect, t1));
		
		// test no overlap
		t2.translate(0.0, 0.58);
		TestCase.assertFalse(this.aabb.detect(rect, t1, tri, t2));
		TestCase.assertFalse(this.aabb.detect(tri, t2, rect, t1));
	}
	
	/**
	 * Tests {@link Collidable} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		CollidableTest ct1 = new CollidableTest(rect);
		CollidableTest ct2 = new CollidableTest(tri);
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test AABB overlap
		ct2.translate(0.34, 0.42);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		
		// test no overlap
		ct2.translate(0.0, 0.58);
		TestCase.assertFalse(this.aabb.detect(ct1, ct2));
		TestCase.assertFalse(this.aabb.detect(ct2, ct1));
	}
	
	/**
	 * Tests {@link Sap}.
	 */
	@Test
	public void detectSap() {
		List<BroadphasePair<CollidableTest>> pairs;
		
		// create some collidables
		CollidableTest ct1 = new CollidableTest(rect);
		CollidableTest ct2 = new CollidableTest(tri);
		
		List<CollidableTest> objs = new ArrayList<CollidableTest>();
		objs.add(ct1);
		objs.add(ct2);
		
		// test containment
		pairs = this.sap.detect(objs);
		TestCase.assertEquals(1, pairs.size());
		
		// test overlap
		ct1.translate(-0.5, 0.0);
		pairs = this.sap.detect(objs);
		TestCase.assertEquals(1, pairs.size());
		
		// test AABB overlap
		ct2.translate(0.34, 0.42);
		pairs = this.sap.detect(objs);
		TestCase.assertEquals(1, pairs.size());
		
		// test no overlap
		ct2.translate(0.0, 0.58);
		pairs = this.sap.detect(objs);
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
		TestCase.assertTrue(this.sat.detect(rect, t1, tri, t2, p));
		TestCase.assertTrue(this.sat.detect(rect, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.720, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.000, n.x, 1.0e-3);
		TestCase.assertEquals(1.000, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(tri, t2, rect, t1, p));
		TestCase.assertTrue(this.sat.detect(tri, t2, rect, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.720, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.000, n.x, 1.0e-3);
		TestCase.assertEquals(-1.000, n.y, 1.0e-3);
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.sat.detect(rect, t1, tri, t2, p));
		TestCase.assertTrue(this.sat.detect(rect, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.450, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(1.000, n.x, 1.0e-3);
		TestCase.assertEquals(0.000, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(tri, t2, rect, t1, p));
		TestCase.assertTrue(this.sat.detect(tri, t2, rect, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.450, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(-1.000, n.x, 1.0e-3);
		TestCase.assertEquals(0.000, n.y, 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.34, 0.42);
		TestCase.assertFalse(this.sat.detect(rect, t1, tri, t2, p));
		TestCase.assertFalse(this.sat.detect(rect, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(tri, t2, rect, t1, p));
		TestCase.assertFalse(this.sat.detect(tri, t2, rect, t1));
		
		// test no overlap
		t2.translate(0.0, 0.58);
		TestCase.assertFalse(this.sat.detect(rect, t1, tri, t2, p));
		TestCase.assertFalse(this.sat.detect(rect, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(tri, t2, rect, t1, p));
		TestCase.assertFalse(this.sat.detect(tri, t2, rect, t1));
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
		TestCase.assertTrue(this.gjk.detect(rect, t1, tri, t2, p));
		TestCase.assertTrue(this.gjk.detect(rect, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.720, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.000, n.x, 1.0e-3);
		TestCase.assertEquals(1.000, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(tri, t2, rect, t1, p));
		TestCase.assertTrue(this.gjk.detect(tri, t2, rect, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.720, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.000, n.x, 1.0e-3);
		TestCase.assertEquals(-1.000, n.y, 1.0e-3);
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertTrue(this.gjk.detect(rect, t1, tri, t2, p));
		TestCase.assertTrue(this.gjk.detect(rect, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.450, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(1.000, n.x, 1.0e-3);
		TestCase.assertEquals(0.000, n.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(tri, t2, rect, t1, p));
		TestCase.assertTrue(this.gjk.detect(tri, t2, rect, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.450, p.getDepth(), 1.0e-3);
		TestCase.assertEquals(-1.000, n.x, 1.0e-3);
		TestCase.assertEquals(0.000, n.y, 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.34, 0.42);
		TestCase.assertFalse(this.gjk.detect(rect, t1, tri, t2, p));
		TestCase.assertFalse(this.gjk.detect(rect, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(tri, t2, rect, t1, p));
		TestCase.assertFalse(this.gjk.detect(tri, t2, rect, t1));
		
		// test no overlap
		t2.translate(0.0, 0.58);
		TestCase.assertFalse(this.gjk.detect(rect, t1, tri, t2, p));
		TestCase.assertFalse(this.gjk.detect(rect, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(tri, t2, rect, t1, p));
		TestCase.assertFalse(this.gjk.detect(tri, t2, rect, t1));
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
		TestCase.assertFalse(this.gjk.distance(rect, t1, tri, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(tri, t2, rect, t1, s));
		
		// test overlap
		t1.translate(-0.5, 0.0);
		TestCase.assertFalse(this.gjk.distance(rect, t1, tri, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(tri, t2, rect, t1, s));
		
		// test AABB overlap
		t2.translate(0.34, 0.42);
		TestCase.assertTrue(this.gjk.distance(rect, t1, tri, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.035, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.894, n.x, 1.0e-3);
		TestCase.assertEquals(0.447, n.y, 1.0e-3);
		TestCase.assertEquals(0.000, p1.x, 1.0e-3);
		TestCase.assertEquals(0.500, p1.y, 1.0e-3);
		TestCase.assertEquals(0.032, p2.x, 1.0e-3);
		TestCase.assertEquals(0.516, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(tri, t2, rect, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.035, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.894, n.x, 1.0e-3);
		TestCase.assertEquals(-0.447, n.y, 1.0e-3);
		TestCase.assertEquals(0.032, p1.x, 1.0e-3);
		TestCase.assertEquals(0.516, p1.y, 1.0e-3);
		TestCase.assertEquals(0.000, p2.x, 1.0e-3);
		TestCase.assertEquals(0.500, p2.y, 1.0e-3);
		
		// test no overlap
		t2.translate(0.0, 0.58);
		TestCase.assertTrue(this.gjk.distance(rect, t1, tri, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.338, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.561, n.x, 1.0e-3);
		TestCase.assertEquals(0.827, n.y, 1.0e-3);
		TestCase.assertEquals(0.000, p1.x, 1.0e-3);
		TestCase.assertEquals(0.500, p1.y, 1.0e-3);
		TestCase.assertEquals(0.190, p2.x, 1.0e-3);
		TestCase.assertEquals(0.780, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(tri, t2, rect, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.338, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.561, n.x, 1.0e-3);
		TestCase.assertEquals(-0.827, n.y, 1.0e-3);
		TestCase.assertEquals(0.190, p1.x, 1.0e-3);
		TestCase.assertEquals(0.780, p1.y, 1.0e-3);
		TestCase.assertEquals(0.000, p2.x, 1.0e-3);
		TestCase.assertEquals(0.500, p2.y, 1.0e-3);
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
		this.gjk.detect(rect, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t1, tri, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, rect, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		// test containment sat
		this.sat.detect(rect, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t1, tri, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, rect, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		t1.translate(-0.5, 0.0);
		
		// test overlap gjk
		this.gjk.detect(rect, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t1, tri, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.450, p1.x, 1.0e-3);
		TestCase.assertEquals(0.380, p1.y, 1.0e-3);
		TestCase.assertEquals(0.450, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.150, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.220, p2.y, 1.0e-3);
		TestCase.assertEquals(0.150, mp2.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.gjk.detect(tri, t2, rect, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, rect, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.450, p1.x, 1.0e-3);
		TestCase.assertEquals(0.380, p1.y, 1.0e-3);
		TestCase.assertEquals(0.450, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.150, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.220, p2.y, 1.0e-3);
		TestCase.assertEquals(0.150, mp2.getDepth(), 1.0e-3);
		
		// test overlap sat
		this.sat.detect(rect, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, rect, t1, tri, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.450, p1.x, 1.0e-3);
		TestCase.assertEquals(0.380, p1.y, 1.0e-3);
		TestCase.assertEquals(0.450, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.150, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.220, p2.y, 1.0e-3);
		TestCase.assertEquals(0.150, mp2.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.sat.detect(tri, t2, rect, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, rect, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.450, p1.x, 1.0e-3);
		TestCase.assertEquals(0.380, p1.y, 1.0e-3);
		TestCase.assertEquals(0.450, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.150, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.220, p2.y, 1.0e-3);
		TestCase.assertEquals(0.150, mp2.getDepth(), 1.0e-3);
	}
}
