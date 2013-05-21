/*
 * Copyright (c) 2011-2013 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link Triangle} - {@link Triangle} collision detection.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.0.0
 */
public class TriangleTriangleTest extends AbstractTest {
	/** The first test {@link Triangle} */
	private Triangle tri1;
	
	/** The second test {@link Triangle} */
	private Triangle tri2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.tri1 = new Triangle(
						new Vector2(1.29, 0.25),
						new Vector2(-0.71, 0.65),
						new Vector2(-0.59, -0.85));
		this.tri2 = new Triangle(
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
		TestCase.assertTrue(this.aabb.detect(tri1, t1, tri2, t2));
		TestCase.assertTrue(this.aabb.detect(tri2, t2, tri1, t1));
		
		// test overlap
		t2.translate(0.0, 0.5);
		TestCase.assertTrue(this.aabb.detect(tri1, t1, tri2, t2));
		TestCase.assertTrue(this.aabb.detect(tri2, t2, tri1, t1));
		
		// test only AABB overlap
		t2.translate(0.0, 0.3);
		TestCase.assertTrue(this.aabb.detect(tri1, t1, tri2, t2));
		TestCase.assertTrue(this.aabb.detect(tri2, t2, tri1, t1));
		
		// test no overlap
		t2.translate(0.0, 0.3);
		TestCase.assertFalse(this.aabb.detect(tri1, t1, tri2, t2));
		TestCase.assertFalse(this.aabb.detect(tri2, t2, tri1, t1));
	}
	
	/**
	 * Tests {@link Collidable} AABB.
	 */
	@Test	
	public void detectCollidableAABB() {
		// create some collidables
		CollidableTest ct1 = new CollidableTest(tri1);
		CollidableTest ct2 = new CollidableTest(tri2);
		
		// test containment
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test overlap
		ct2.translate(0.0, 0.5);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test only AABB overlap
		ct2.translate(0.0, 0.3);
		TestCase.assertTrue(this.aabb.detect(ct1, ct2));
		TestCase.assertTrue(this.aabb.detect(ct2, ct1));
		
		// test no overlap
		ct2.translate(0.0, 0.3);
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
		CollidableTest ct1 = new CollidableTest(tri1);
		CollidableTest ct2 = new CollidableTest(tri2);
		
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
		ct1.translate(0.0, 0.5);
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
		ct2.translate(0.0, 0.3);
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
		ct1.translate(0.0, 1.3);
		this.sapI.update(ct1);
		this.sapBF.update(ct1);
		this.sapT.update(ct1);
		this.dynT.update(ct1);
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
	 * Tests {@link Sat}.
	 */
	@Test
	public void detectSat() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 n = null;
		
		// test containment
		TestCase.assertTrue(this.sat.detect(tri1, t1, tri2, t2, p));
		TestCase.assertTrue(this.sat.detect(tri1, t1, tri2, t2));
		n = p.getNormal();
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.743, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(tri2, t2, tri1, t1, p));
		TestCase.assertTrue(this.sat.detect(tri2, t2, tri1, t1));
		n = p.getNormal();
		TestCase.assertEquals(0.196, n.x, 1.0e-3);
		TestCase.assertEquals(0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.743, p.getDepth(), 1.0e-3);
		
		// test overlap
		t2.translate(0.0, 0.5);
		TestCase.assertTrue(this.sat.detect(tri1, t1, tri2, t2, p));
		TestCase.assertTrue(this.sat.detect(tri1, t1, tri2, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.196, n.x, 1.0e-3);
		TestCase.assertEquals(0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.252, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.sat.detect(tri2, t2, tri1, t1, p));
		TestCase.assertTrue(this.sat.detect(tri2, t2, tri1, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.252, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, 0.3);
		TestCase.assertFalse(this.sat.detect(tri1, t1, tri2, t2, p));
		TestCase.assertFalse(this.sat.detect(tri1, t1, tri2, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(tri2, t2, tri1, t1, p));
		TestCase.assertFalse(this.sat.detect(tri2, t2, tri1, t1));
		
		// test no overlap
		t2.translate(0.0, 0.3);
		TestCase.assertFalse(this.sat.detect(tri1, t1, tri2, t2, p));
		TestCase.assertFalse(this.sat.detect(tri1, t1, tri2, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.sat.detect(tri2, t2, tri1, t1, p));
		TestCase.assertFalse(this.sat.detect(tri2, t2, tri1, t1));
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
		TestCase.assertTrue(this.gjk.detect(tri1, t1, tri2, t2, p));
		TestCase.assertTrue(this.gjk.detect(tri1, t1, tri2, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.196, n.x, 1.0e-3);
		TestCase.assertEquals(0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.743, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(tri2, t2, tri1, t1, p));
		TestCase.assertTrue(this.gjk.detect(tri2, t2, tri1, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.743, p.getDepth(), 1.0e-3);
		
		// test overlap
		t2.translate(0.0, 0.5);
		TestCase.assertTrue(this.gjk.detect(tri1, t1, tri2, t2, p));
		TestCase.assertTrue(this.gjk.detect(tri1, t1, tri2, t2));
		n = p.getNormal();
		TestCase.assertEquals(0.196, n.x, 1.0e-3);
		TestCase.assertEquals(0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.252, p.getDepth(), 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.detect(tri2, t2, tri1, t1, p));
		TestCase.assertTrue(this.gjk.detect(tri2, t2, tri1, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals(0.252, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		t2.translate(0.0, 0.3);
		TestCase.assertFalse(this.gjk.detect(tri1, t1, tri2, t2, p));
		TestCase.assertFalse(this.gjk.detect(tri1, t1, tri2, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(tri2, t2, tri1, t1, p));
		TestCase.assertFalse(this.gjk.detect(tri2, t2, tri1, t1));
		
		// test no overlap
		t2.translate(0.0, 0.3);
		TestCase.assertFalse(this.gjk.detect(tri1, t1, tri2, t2, p));
		TestCase.assertFalse(this.gjk.detect(tri1, t1, tri2, t2));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.detect(tri2, t2, tri1, t1, p));
		TestCase.assertFalse(this.gjk.detect(tri2, t2, tri1, t1));
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
		TestCase.assertFalse(this.gjk.distance(tri1, t1, tri2, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(tri2, t2, tri1, t1, s));
		
		// test overlap
		t2.translate(0.0, 0.5);
		TestCase.assertFalse(this.gjk.distance(tri1, t1, tri2, t2, s));
		// try reversing the shapes
		TestCase.assertFalse(this.gjk.distance(tri2, t2, tri1, t1, s));
		
		// test AABB overlap
		t2.translate(0.0, 0.3);
		TestCase.assertTrue(this.gjk.distance(tri1, t1, tri2, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.041, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.196, n.x, 1.0e-3);
		TestCase.assertEquals(0.980, n.y, 1.0e-3);
		TestCase.assertEquals(-0.158, p1.x, 1.0e-3);
		TestCase.assertEquals(0.539, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.150, p2.x, 1.0e-3);
		TestCase.assertEquals(0.580, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(tri2, t2, tri1, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.041, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals(0.580, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.158, p2.x, 1.0e-3);
		TestCase.assertEquals(0.539, p2.y, 1.0e-3);
		
		// test no overlap
		t2.translate(0.0, 0.3);
		TestCase.assertTrue(this.gjk.distance(tri1, t1, tri2, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.335, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(0.196, n.x, 1.0e-3);
		TestCase.assertEquals(0.980, n.y, 1.0e-3);
		TestCase.assertEquals(-0.215, p1.x, 1.0e-3);
		TestCase.assertEquals(0.551, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.150, p2.x, 1.0e-3);
		TestCase.assertEquals(0.880, p2.y, 1.0e-3);
		// try reversing the shapes
		TestCase.assertTrue(this.gjk.distance(tri2, t2, tri1, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals(0.335, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals(0.880, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.215, p2.x, 1.0e-3);
		TestCase.assertEquals(0.551, p2.y, 1.0e-3);
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
		this.gjk.detect(tri1, t1, tri2, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri1, t1, tri2, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri2, t2, tri1, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		// test containment sat
		this.sat.detect(tri1, t1, tri2, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri1, t1, tri2, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		// try reversing the shapes
		TestCase.assertTrue(this.cmfs.getManifold(p, tri2, t2, tri1, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		
		t2.translate(0.0, 0.5);
		
		// test overlap gjk
		this.gjk.detect(tri1, t1, tri2, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri1, t1, tri2, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals(0.280, p1.y, 1.0e-3);
		TestCase.assertEquals(0.252, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.450, p2.x, 1.0e-3);
		TestCase.assertEquals(0.380, p2.y, 1.0e-3);
		TestCase.assertEquals(0.037, mp2.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.gjk.detect(tri2, t2, tri1, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri2, t2, tri1, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals(0.280, p1.y, 1.0e-3);
		TestCase.assertEquals(0.252, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.450, p2.x, 1.0e-3);
		TestCase.assertEquals(0.380, p2.y, 1.0e-3);
		TestCase.assertEquals(0.037, mp2.getDepth(), 1.0e-3);
		
		// test overlap sat
		this.sat.detect(tri1, t1, tri2, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri1, t1, tri2, t2, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals(0.280, p1.y, 1.0e-3);
		TestCase.assertEquals(0.252, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.450, p2.x, 1.0e-3);
		TestCase.assertEquals(0.380, p2.y, 1.0e-3);
		TestCase.assertEquals(0.037, mp2.getDepth(), 1.0e-3);
		// try reversing the shapes
		this.sat.detect(tri2, t2, tri1, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, tri2, t2, tri1, t1, m));
		TestCase.assertEquals(2, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		mp2 = m.getPoints().get(1);
		p1 = mp1.getPoint();
		p2 = mp2.getPoint();
		TestCase.assertEquals(-0.150, p1.x, 1.0e-3);
		TestCase.assertEquals(0.280, p1.y, 1.0e-3);
		TestCase.assertEquals(0.252, mp1.getDepth(), 1.0e-3);
		TestCase.assertEquals(0.450, p2.x, 1.0e-3);
		TestCase.assertEquals(0.380, p2.y, 1.0e-3);
		TestCase.assertEquals(0.037, mp2.getDepth(), 1.0e-3);
	}
	
	/**
	 * Test case specific to a bug found where two triangles are not
	 * detected as being separated by the GJK distance algorithm.
	 */
	@Test
	public void distanceNotFound() {
		Separation s = new Separation();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		Vector2 p1, p2;
		
		t2.translate(1.578125, 0.6875);
		
		TestCase.assertTrue(this.gjk.distance(tri1, t1, tri2, t2, s));
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		// this bug was caused by the point solving code returning two
		// points whose coordinates where NaN, NaN
		TestCase.assertFalse(Double.isNaN(p1.x));
		TestCase.assertFalse(Double.isNaN(p1.y));
		TestCase.assertFalse(Double.isNaN(p2.x));
		TestCase.assertFalse(Double.isNaN(p2.y));
	}
	
	/**
	 * Test case specific to a bug found where two triangles are found
	 * to be penetrating yet the depth is zero.
	 * @since 1.1.0
	 */
	@Test
	public void falsePenetration1() {
		// this config would generate a penetration object with zero depth
		Triangle t1 = new Triangle(
				new Vector2(-0.5877852522924732, -0.8090169943749473),
				new Vector2(-0.30901699437494756, -0.9510565162951535),
				new Vector2(3.592757177872429E-17, -2.470020559787295E-17));
		Triangle t2 = new Triangle(
				new Vector2(-0.9510565162951536, -0.3090169943749473),
				new Vector2(-0.8090169943749475, -0.587785252292473),
				new Vector2(3.592757177872429E-17, -2.470020559787295E-17));
		Penetration p = new Penetration();
		Transform tx = new Transform();
		tx.translate(-2.5, -2.5752222222222203);
		boolean collided = this.gjk.detect(t1, tx, t2, tx, p);
		TestCase.assertFalse(collided);
		
		Triangle t3 = new Triangle(
				new Vector2(-0.30901699437494756, -0.9510565162951535),
				new Vector2(-1.8369701987210297E-16, -1.0),
				new Vector2(3.592757177872429E-17, -2.470020559787295E-17));
		Triangle t4 = new Triangle(
				new Vector2(-1.8369701987210297E-16, -1.0),
				new Vector2(0.30901699437494723, -0.9510565162951536),
				new Vector2(3.592757177872429E-17, -2.470020559787295E-17));
		
		collided = this.gjk.detect(t3, tx, t4, tx, p);
		TestCase.assertFalse(collided);
	}
}
