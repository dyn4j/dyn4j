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
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Circle} - {@link Triangle} collision detection.
 * @author William Bittle
 */
public class CircleTriangleTest extends AbstractTest {
	/** The test {@link Circle} */
	private Circle circ;
	
	/** The test {@link Triangle} */
	private Triangle tri;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.circ = new Circle(1.0);
		this.tri = new Triangle(
				new Vector(0.5, 0.5),
				new Vector(-0.3, -0.5),
				new Vector(1.0, -0.3));
	}
	
	/**
	 * Tests {@link Shape} AABB.
	 */
	@Test
	public void detectShapeAABB() {
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(circ, t1, tri, t2));
		TestCase.assertTrue(this.aabb.detect(tri, t2, circ, t1));
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.aabb.detect(circ, t1, tri, t2));
		TestCase.assertTrue(this.aabb.detect(tri, t2, circ, t1));
		
		// test only AABB overlap
		t2.translate(0.0, 1.3);
		TestCase.assertTrue(this.aabb.detect(circ, t1, tri, t2));
		TestCase.assertTrue(this.aabb.detect(tri, t2, circ, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.aabb.detect(circ, t1, tri, t2));
		TestCase.assertFalse(this.aabb.detect(tri, t2, circ, t1));
	}
	
	/**
	 * Tests {@link Collidable} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		List<Convex> shapes = null;
		
		// create some collidables
		shapes = new ArrayList<Convex>(1); shapes.add(circ);
		CollidableTest ct1 = new CollidableTest(shapes, Filter.DEFAULT_FILTER);
		
		shapes = new ArrayList<Convex>(1); shapes.add(tri);
		CollidableTest ct2 = new CollidableTest(shapes, Filter.DEFAULT_FILTER);
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test overlap
		ct1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(0.0, 1.3);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test no overlap
		ct1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.aabb.detect(ct1, ct2));
		TestCase.assertFalse(this.aabb.detect(ct2, ct1));
	}
	
	/**
	 * Tests {@link Sap}.
	 */
	@Test
	public void detectSap() {
		List<Convex> shapes;
		List<BroadphasePair<CollidableTest>> pairs;
		
		// create some collidables
		shapes = new ArrayList<Convex>(1); shapes.add(circ);
		CollidableTest ct1 = new CollidableTest(shapes, Filter.DEFAULT_FILTER);
		
		shapes = new ArrayList<Convex>(1); shapes.add(tri);
		CollidableTest ct2 = new CollidableTest(shapes, Filter.DEFAULT_FILTER);
		
		List<CollidableTest> objs = new ArrayList<CollidableTest>();
		objs.add(ct1);
		objs.add(ct2);
		
		// test containment
		pairs = this.sap.detect(objs);
		TestCase.assertEquals(1, pairs.size());
		
		// test overlap
		ct1.translate(-1.0, 0.0);
		pairs = this.sap.detect(objs);
		TestCase.assertEquals(1, pairs.size());
		
		// test only AABB overlap
		ct2.translate(0.0, 1.3);
		pairs = this.sap.detect(objs);
		TestCase.assertEquals(1, pairs.size());
		
		// test no overlap
		ct1.translate(-1.0, 0.0);
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
		
		Vector n = null;
		
		// test containment
		TestCase.assertTrue(this.sat.detect(circ, t1, tri, t2, p));
		TestCase.assertTrue(this.sat.detect(circ, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.780, n.x, 1.0e-3);
		TestCase.assertEquals(-0.624, n.y, 1.0e-3);
		TestCase.assertEquals(0.921, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(tri, t2, circ, t1, p));
		TestCase.assertTrue(this.sat.detect(tri, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.780, n.x, 1.0e-3);
		TestCase.assertEquals(0.624, n.y, 1.0e-3);
		TestCase.assertEquals(0.921, p.getDepth(), 1.0e-3);
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.sat.detect(circ, t1, tri, t2, p));
		TestCase.assertTrue(this.sat.detect(circ, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.813, n.x, 1.0e-3);
		TestCase.assertEquals(-0.581, n.y, 1.0e-3);
		TestCase.assertEquals(0.139, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(tri, t2, circ, t1, p));
		TestCase.assertTrue(this.sat.detect(tri, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.813, n.x, 1.0e-3);
		TestCase.assertEquals(0.581, n.y, 1.0e-3);
		TestCase.assertEquals(0.139, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, 1.3);
		TestCase.assertFalse(this.sat.detect(circ, t1, tri, t2, p));
		TestCase.assertFalse(this.sat.detect(circ, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(tri, t2, circ, t1, p));
		TestCase.assertFalse(this.sat.detect(tri, t2, circ, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.sat.detect(circ, t1, tri, t2, p));
		TestCase.assertFalse(this.sat.detect(circ, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(tri, t2, circ, t1, p));
		TestCase.assertFalse(this.sat.detect(tri, t2, circ, t1));
	}
	
	/**
	 * Tests {@link Gjk}.
	 */
	@Test
	public void detectGjk() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector n = null;
		
		// test containment
		TestCase.assertTrue(this.gjk.detect(circ, t1, tri, t2, p));
		TestCase.assertTrue(this.gjk.detect(circ, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.780, n.x, 1.0e-3);
		TestCase.assertEquals(-0.624, n.y, 1.0e-3);
		TestCase.assertEquals(0.921, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(tri, t2, circ, t1, p));
		TestCase.assertTrue(this.gjk.detect(tri, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.780, n.x, 1.0e-3);
		TestCase.assertEquals(0.624, n.y, 1.0e-3);
		TestCase.assertEquals(0.921, p.getDepth(), 1.0e-3);
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.detect(circ, t1, tri, t2, p));
		TestCase.assertTrue(this.gjk.detect(circ, t1, tri, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.813, n.x, 1.0e-3);
		TestCase.assertEquals(-0.581, n.y, 1.0e-3);
		TestCase.assertEquals(0.139, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(tri, t2, circ, t1, p));
		TestCase.assertTrue(this.gjk.detect(tri, t2, circ, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.813, n.x, 1.0e-3);
		TestCase.assertEquals(0.581, n.y, 1.0e-3);
		TestCase.assertEquals(0.139, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, 1.3);
		TestCase.assertFalse(this.gjk.detect(circ, t1, tri, t2, p));
		TestCase.assertFalse(this.gjk.detect(circ, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(tri, t2, circ, t1, p));
		TestCase.assertFalse(this.gjk.detect(tri, t2, circ, t1));
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.detect(circ, t1, tri, t2, p));
		TestCase.assertFalse(this.gjk.detect(circ, t1, tri, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(tri, t2, circ, t1, p));
		TestCase.assertFalse(this.gjk.detect(tri, t2, circ, t1));
	}
	
	/**
	 * Tests the {@link Gjk} distance method.
	 */
	@Test
	public void gjkDistance() {
		Separation s = new Separation();
		
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector n, p1, p2;
		
		// test containment
		TestCase.assertFalse(this.gjk.distance(circ, t1, tri, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(tri, t2, circ, t1, s));
		
		// test overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.gjk.distance(circ, t1, tri, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(tri, t2, circ, t1, s));
		
		// test AABB overlap
		t2.translate(0.0, 1.3);
		TestCase.assertTrue(this.gjk.distance(circ, t1, tri, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.063, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.658, n.x, 1.0e-3);
		TestCase.assertEquals(0.752, n.y, 1.0e-3);
		TestCase.assertEquals(-0.341, p1.x, 1.0e-3);
		TestCase.assertEquals(0.752, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.3, p2.x);
		TestCase.assertEquals(0.8, p2.y);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(tri, t2, circ, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.063, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.658, n.x, 1.0e-3);
		TestCase.assertEquals(-0.752, n.y, 1.0e-3);
		TestCase.assertEquals(-0.3, p1.x);
		TestCase.assertEquals(0.8, p1.y);
		TestCase.assertEquals(-0.341, p2.x, 1.0e-3);
		TestCase.assertEquals(0.752, p2.y, 1.0e-3);
		
		// test no overlap
		t1.translate(-1.0, 0.0);
		TestCase.assertTrue(this.gjk.distance(circ, t1, tri, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.878, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.904, n.x, 1.0e-3);
		TestCase.assertEquals(0.425, n.y, 1.0e-3);
		TestCase.assertEquals(-1.095, p1.x, 1.0e-3);
		TestCase.assertEquals(0.425, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.3, p2.x);
		TestCase.assertEquals(0.8, p2.y);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(tri, t2, circ, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.878, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.904, n.x, 1.0e-3);
		TestCase.assertEquals(-0.425, n.y, 1.0e-3);
		TestCase.assertEquals(-0.3, p1.x);
		TestCase.assertEquals(0.8, p1.y);
		TestCase.assertEquals(-1.095, p2.x, 1.0e-3);
		TestCase.assertEquals(0.425, p2.y, 1.0e-3);
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
		Vector p1 = null;
		
		// test containment gjk
		this.gjk.detect(circ, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, tri, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		// test containment sat
		this.sat.detect(circ, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, tri, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		t1.translate(-1.0, 0.0);
		
		// test overlap gjk
		this.gjk.detect(circ, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, tri, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.139, mp.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.186, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.581, p1.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.139, mp.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.186, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.581, p1.y, 1.0e-3);
		
		// test overlap sat
		this.sat.detect(circ, t1, tri, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, circ, t1, tri, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.139, mp.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.186, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.581, p1.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri, t2, circ, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp = m.getPoints().get(0);
		p1 = mp.getPoint();
		TestCase.assertEquals(0.139, mp.getDepth(), 1.0e-3);
		TestCase.assertEquals(-0.186, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.581, p1.y, 1.0e-3);
	}
}
