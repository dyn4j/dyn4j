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

import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for {@link Triangle} - {@link Ellipse} collision detection.
 * @author William Bittle
 * @version 4.2.1
 * @since 3.1.5
 */
public class TriangleEllipseTest extends AbstractNarrowphaseShapeTest {
	/** The {@link Triangle} */
	private Triangle t;
	
	/** The {@link Ellipse} */
	private Ellipse e;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.t = new Triangle(
						new Vector2(1.29, 0.25),
						new Vector2(-0.71, 0.65),
						new Vector2(-0.59, -0.85));
		this.e = new Ellipse(1.0, 0.5);
	}
	
	/**
	 * Tests that SAT is not supported.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void detectSat() {
		Penetration p = new Penetration();
		Transform t1 = new Transform();
		Transform t2 = new Transform();
		
		this.sat.detect(t, t1, e, t2, p);
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
		TestCase.assertTrue(this.gjk.detect(t, t1, e, t2, p));
		TestCase.assertTrue(this.gjk.detect(t, t1, e, t2));
		n = p.getNormal();
		TestCase.assertEquals( 0.196, n.x, 1.0e-3);
		TestCase.assertEquals( 0.980, n.y, 1.0e-3);
		TestCase.assertEquals( 0.762, p.getDepth(), 1.0e-3);
		
		// try reversing the shapes
		p.clear();
		TestCase.assertTrue(this.gjk.detect(e, t2, t, t1, p));
		TestCase.assertTrue(this.gjk.detect(e, t2, t, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.196, n.x, 1.0e-3);
		TestCase.assertEquals(-0.980, n.y, 1.0e-3);
		TestCase.assertEquals( 0.762, p.getDepth(), 1.0e-3);
		
		// test overlap
		p.clear();
		t1.translate(-0.8, 0.0);
		TestCase.assertTrue(this.gjk.detect(t, t1, e, t2, p));
		TestCase.assertTrue(this.gjk.detect(t, t1, e, t2));
		n = p.getNormal();
		TestCase.assertEquals( 0.505, n.x, 1.0e-3);
		TestCase.assertEquals(-0.863, n.y, 1.0e-3);
		TestCase.assertEquals( 0.363, p.getDepth(), 1.0e-3);
		
		// try reversing the shapes
		p.clear();
		TestCase.assertTrue(this.gjk.detect(e, t2, t, t1, p));
		TestCase.assertTrue(this.gjk.detect(e, t2, t, t1));
		n = p.getNormal();
		TestCase.assertEquals(-0.505, n.x, 1.0e-3);
		TestCase.assertEquals( 0.863, n.y, 1.0e-3);
		TestCase.assertEquals( 0.363, p.getDepth(), 1.0e-3);
		
		// test AABB overlap
		p.clear();
		t2.translate(0.5, -0.5);
		TestCase.assertFalse(this.gjk.detect(t, t1, e, t2, p));
		TestCase.assertFalse(this.gjk.detect(t, t1, e, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertFalse(this.gjk.detect(e, t2, t, t1, p));
		TestCase.assertFalse(this.gjk.detect(e, t2, t, t1));
		
		// test no overlap
		p.clear();
		t2.translate(1.0, 0.5);
		TestCase.assertFalse(this.gjk.detect(t, t1, e, t2, p));
		TestCase.assertFalse(this.gjk.detect(t, t1, e, t2));
		
		// try reversing the shapes
		p.clear();
		TestCase.assertFalse(this.gjk.detect(e, t2, t, t1, p));
		TestCase.assertFalse(this.gjk.detect(e, t2, t, t1));
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
		TestCase.assertFalse(this.gjk.distance(t, t1, e, t2, s));
		
		// try reversing the shapes
		s.clear();
		TestCase.assertFalse(this.gjk.distance(e, t2, t, t1, s));
		
		// test overlap
		s.clear();
		t1.translate(-0.8, 0.0);
		TestCase.assertFalse(this.gjk.distance(t, t1, e, t2, s));
		
		// try reversing the shapes
		s.clear();
		TestCase.assertFalse(this.gjk.distance(e, t2, t, t1, s));
		
		// test AABB overlap
		s.clear();
		t2.translate(0.5, -0.5);
		TestCase.assertTrue(this.gjk.distance(t, t1, e, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.320, s.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.505, n.x, 1.0e-3);
		TestCase.assertEquals(-0.863, n.y, 1.0e-3);
		TestCase.assertEquals(-0.041, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.061, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.119, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.337, p2.y, 1.0e-3);
		
		// try reversing the shapes
		s.clear();
		TestCase.assertTrue(this.gjk.distance(e, t2, t, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.320, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.505, n.x, 1.0e-3);
		TestCase.assertEquals( 0.863, n.y, 1.0e-3);
		TestCase.assertEquals( 0.119, p1.x, 1.0e-3);
		TestCase.assertEquals(-0.337, p1.y, 1.0e-3);
		TestCase.assertEquals(-0.041, p2.x, 1.0e-3);
		TestCase.assertEquals(-0.061, p2.y, 1.0e-3);
		
		// test no overlap
		s.clear();
		t2.translate(1.0, 0.5);
		TestCase.assertTrue(this.gjk.distance(t, t1, e, t2, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.557, s.getDistance(), 1.0e-3);
		TestCase.assertEquals( 0.931, n.x, 1.0e-3);
		TestCase.assertEquals(-0.362, n.y, 1.0e-3);
		TestCase.assertEquals( 0.490, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.250, p1.y, 1.0e-3);
		TestCase.assertEquals( 1.009, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.047, p2.y, 1.0e-3);
		
		// try reversing the shapes
		s.clear();
		TestCase.assertTrue(this.gjk.distance(e, t2, t, t1, s));
		n = s.getNormal();
		p1 = s.getPoint1();
		p2 = s.getPoint2();
		TestCase.assertEquals( 0.557, s.getDistance(), 1.0e-3);
		TestCase.assertEquals(-0.931, n.x, 1.0e-3);
		TestCase.assertEquals( 0.362, n.y, 1.0e-3);
		TestCase.assertEquals( 1.009, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.047, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.490, p2.x, 1.0e-3);
		TestCase.assertEquals( 0.250, p2.y, 1.0e-3);
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
		
		ManifoldPoint mp1;
		Vector2 p1;
		
		// test containment gjk
		this.gjk.detect(t, t1, e, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, t, t1, e, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		// try reversing the shapes
		p.clear();
		m.clear();
		this.gjk.detect(e, t2, t, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, e, t2, t, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		
		t1.translate(-0.8, 0.0);
		
		// test overlap gjk
		p.clear();
		m.clear();
		this.gjk.detect(t, t1, e, t2, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, t, t1, e, t2, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		p1 = mp1.getPoint();
		TestCase.assertEquals(-0.380, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.162, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.363, mp1.getDepth(), 1.0e-3);
		
		// try reversing the shapes
		p.clear();
		m.clear();
		this.gjk.detect(e, t2, t, t1, p);
		TestCase.assertTrue(this.cmfs.getManifold(p, e, t2, t, t1, m));
		TestCase.assertEquals(1, m.getPoints().size());
		mp1 = m.getPoints().get(0);
		p1 = mp1.getPoint();
		TestCase.assertEquals(-0.380, p1.x, 1.0e-3);
		TestCase.assertEquals( 0.162, p1.y, 1.0e-3);
		TestCase.assertEquals( 0.363, mp1.getDepth(), 1.0e-3);
	}
}
