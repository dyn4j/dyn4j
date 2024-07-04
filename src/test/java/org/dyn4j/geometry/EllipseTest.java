/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Ellipse} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 3.1.5
 */
public class EllipseTest {
	/** Identity Transform instance */
	private static final Transform IDENTITY = new Transform();
	
	/**
	 * Tests a zero width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroWidth() {
		new Ellipse(0.0, 1.0);
	}
	
	/**
	 * Tests a negative width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeWidth() {
		new Ellipse(-1.0, 1.0);
	}
	
	/**
	 * Tests a zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeight() {
		new Ellipse(1.0, 0.0);
	}
	
	/**
	 * Tests a negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeight() {
		new Ellipse(1.0, -1.0);
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccess() {
		Ellipse e = new Ellipse(1.0, 2.0);
		TestCase.assertEquals(1.0, e.getHalfHeight());
		TestCase.assertEquals(0.5, e.getHalfWidth());
		TestCase.assertEquals(1.0, e.getWidth());
		TestCase.assertEquals(2.0, e.getHeight());
		TestCase.assertNotNull(e.toString());
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Ellipse e = new Ellipse(2.0, 1.0);
		Transform t = new Transform();
		Vector2 p = new Vector2(0.75, 0.35);
		
		// shouldn't be in the circle
		TestCase.assertTrue(!e.contains(p, t));
		TestCase.assertTrue(!e.contains(p, t, false));
		
		// move the circle a bit
		t.translate(0.5, 0.0);
		
		// should be in the circle
		TestCase.assertTrue(e.contains(p, t));
		TestCase.assertTrue(e.contains(p, t, false));
		
		p.set(1.5, 0.0);
		
		// should be on the edge
		TestCase.assertTrue(e.contains(p, t));
		TestCase.assertFalse(e.contains(p, t, false));
		
		// test with local translation
		e.rotate(Math.toRadians(90));
		e.translate(0.5, 1.0);
		
		TestCase.assertFalse(e.contains(p, t));
		TestCase.assertFalse(e.contains(p, t, false));
		p.set(1.0, 2.1);
		TestCase.assertFalse(e.contains(p, t));
		TestCase.assertFalse(e.contains(p, t, false));
		p.set(1.0, 2.0);
		TestCase.assertTrue(e.contains(p, t));
		// its on the edge
		TestCase.assertFalse(e.contains(p, t, false));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Ellipse e = new Ellipse(2.0, 1.0);
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, -1.0);
		
		// try some translation
		t.translate(1.0, 0.5);
		
		Interval i = e.project(x, t);
		TestCase.assertEquals( 0.000, i.min, 1.0e-3);
		TestCase.assertEquals( 2.000, i.max, 1.0e-3);
		
		// try some rotation
		t.rotate(Math.toRadians(30), 1.0, 0.5);
		
		i = e.project(y, t);
		TestCase.assertEquals(-1.161, i.min, 1.0e-3);
		TestCase.assertEquals( 0.161, i.max, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotate(Math.toRadians(30), 1.0, 0.5);
		
		i = e.project(y, IDENTITY);
		TestCase.assertEquals(-1.161, i.min, 1.0e-3);
		TestCase.assertEquals( 0.161, i.max, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		i = e.project(y, t);
		TestCase.assertEquals(-2.161, i.min, 1.0e-3);
		TestCase.assertEquals(-0.839, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the farthest methods.
	 */
	@Test
	public void getFarthest() {
		Ellipse e = new Ellipse(2.0, 1.0);
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, -1.0);
		
		// try some translation
		t.translate(1.0, 0.5);
		
		Vector2 p = e.getFarthestPoint(x, t);
		TestCase.assertEquals( 2.000, p.x, 1.0e-3);
		TestCase.assertEquals( 0.500, p.y, 1.0e-3);
		
		// try some rotation
		t.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 0.509, p.x, 1.0e-3);
		TestCase.assertEquals(-0.161, p.y, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, IDENTITY);
		TestCase.assertEquals( 0.509, p.x, 1.0e-3);
		TestCase.assertEquals(-0.161, p.y, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 0.509, p.x, 1.0e-3);
		TestCase.assertEquals( 0.838, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getFarthestPointOnEllipse method.
	 */
	@Test
	public void getFarthestPointOnEllipse() {
		// the assumptions are that a is the semi-major axis
		// and b is the semi-minor axis and that the ellipse
		// is centered at the origin and the semi-major axis
		// aligns with the x-axis
		
		// quadrant 1
		Vector2 p = Ellipse.getFarthestPointOnEllipse(1.0, 0.5, new Vector2(2.0, 0.1));		
		TestCase.assertEquals(-1.000, p.x, 1e-3);
		TestCase.assertEquals(-0.009, p.y, 1e-3);
		
		// quadrant 2
		p = Ellipse.getFarthestPointOnEllipse(1.0, 0.5, new Vector2(0.1, 2.0));
		TestCase.assertEquals(-0.325, p.x, 1e-3);
		TestCase.assertEquals(-0.472, p.y, 1e-3);

		// quadrant 3
		p = Ellipse.getFarthestPointOnEllipse(1.0, 0.5, new Vector2(-2.0, -0.1));
		TestCase.assertEquals( 1.000, p.x, 1e-3);
		TestCase.assertEquals( 0.009, p.y, 1e-3);
		
		// quadrant 4
		p = Ellipse.getFarthestPointOnEllipse(1.0, 0.5, new Vector2(0.1, -2.0));
		TestCase.assertEquals(-0.325, p.x, 1e-3);
		TestCase.assertEquals( 0.472, p.y, 1e-3);
	
		// on axis
		p = Ellipse.getFarthestPointOnEllipse(1.0, 0.5, new Vector2(2.0, 0.0));		
		TestCase.assertEquals(-1.000, p.x, 1e-3);
		TestCase.assertEquals( 0.000, p.y, 1e-3);
		
		// test y-axis aligned ellipse
		p = Ellipse.getFarthestPointOnEllipse(0.5, 1.0, new Vector2(0.0, 2.0));		
		TestCase.assertEquals( 0.000, p.x, 1e-3);
		TestCase.assertEquals(-1.000, p.y, 1e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getAxes() {
		Ellipse e = new Ellipse(1.0, 0.5);
		e.getAxes(new Vector2[] { new Vector2() }, IDENTITY);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getFoci() {
		Ellipse e = new Ellipse(1.0, 0.5);
		e.getFoci(IDENTITY);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		Ellipse e = new Ellipse(1.0, 0.5);
		
		// rotate about center
		e.translate(1.0, 1.0);
		e.rotateAboutCenter(Math.toRadians(30));
		TestCase.assertEquals(1.000, e.center.x, 1.0e-3);
		TestCase.assertEquals(1.000, e.center.y, 1.0e-3);
		
		// rotate about the origin
		e.rotate(Math.toRadians(90));
		TestCase.assertEquals(-1.000, e.center.x, 1.0e-3);
		TestCase.assertEquals( 1.000, e.center.y, 1.0e-3);
		e.translate(e.getCenter().getNegative());
		
		// should move the center
		e.rotate(Math.toRadians(90), 1.0, -1.0);
		TestCase.assertEquals( 0.000, e.center.x, 1.0e-3);
		TestCase.assertEquals(-2.000, e.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the translate methods.
	 */
	@Test
	public void translate() {
		Ellipse e = new Ellipse(1.0, 0.5);
		
		e.translate(1.0, -0.5);
		
		TestCase.assertEquals( 1.000, e.center.x, 1.0e-3);
		TestCase.assertEquals(-0.500, e.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the generated AABB.
	 */
	@Test
	public void createAABB() {
		Ellipse e = new Ellipse(1.0, 0.5);
		
		// using an identity transform
		AABB aabb = e.createAABB(IDENTITY);
		TestCase.assertEquals(-0.500, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(-0.250, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals( 0.500, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals( 0.250, aabb.getMaxY(), 1.0e-3);
		
		// try using the default method
		AABB aabb2 = e.createAABB();
		TestCase.assertEquals(aabb.getMinX(), aabb2.getMinX());
		TestCase.assertEquals(aabb.getMinY(), aabb2.getMinY());
		TestCase.assertEquals(aabb.getMaxX(), aabb2.getMaxX());
		TestCase.assertEquals(aabb.getMaxY(), aabb2.getMaxY());
		
		// test using a rotation and translation matrix
		Transform tx = new Transform();
		tx.rotate(Math.toRadians(30.0));
		tx.translate(1.0, 2.0);
		
		aabb = e.createAABB(tx);
		TestCase.assertEquals(0.549, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(1.669, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals(1.450, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals(2.330, aabb.getMaxY(), 1.0e-3);
	}
	
	/**
	 * Tests the radius method
	 */
	@Test
	public void getRadius() {
		Ellipse e = new Ellipse(1.0, 0.5);
		
		// using an identity transform
		double r = e.getRadius();
		TestCase.assertEquals(0.5, r, 1.0e-3);
		
		r = e.getRadius(new Vector2(1.0, 0.0));
		TestCase.assertEquals(1.5, r, 1.0e-3);
		
		e.rotate(Math.toRadians(30));
		r = e.getRadius(new Vector2(1.0, 0.0));
		TestCase.assertEquals(1.463, r, 1.0e-3);
	}

	/**
	 * Tests the createMass method
	 */
	@Test
	public void createMass() {
		Ellipse e = new Ellipse(1.0, 0.5);
		Mass mass = e.createMass(1.0);
		
		TestCase.assertEquals(0.392, mass.getMass(), 1e-3);
		TestCase.assertEquals(0.030, mass.getInertia(), 1e-3);
		TestCase.assertEquals(2.546, mass.getInverseMass(), 1e-3);
		TestCase.assertEquals(32.594, mass.getInverseInertia(), 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().x, 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().y, 1e-3);
		TestCase.assertEquals(MassType.NORMAL, mass.getType());
		
		e = new Ellipse(0.5, 1.0);
		mass = e.createMass(2.0);
		
		TestCase.assertEquals(0.785, mass.getMass(), 1e-3);
		TestCase.assertEquals(0.061, mass.getInertia(), 1e-3);
		TestCase.assertEquals(1.273, mass.getInverseMass(), 1e-3);
		TestCase.assertEquals(16.297, mass.getInverseInertia(), 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().x, 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().y, 1e-3);
		TestCase.assertEquals(MassType.NORMAL, mass.getType());
		
		mass = e.createMass(0);
		TestCase.assertEquals(0.000, mass.getMass(), 1e-3);
		TestCase.assertEquals(0.000, mass.getInertia(), 1e-3);
		TestCase.assertEquals(0.000, mass.getInverseMass(), 1e-3);
		TestCase.assertEquals(0.000, mass.getInverseInertia(), 1e-3);
		TestCase.assertEquals(0.000, mass.getCenter().x, 1e-3);
		TestCase.assertEquals(0.000, mass.getCenter().y, 1e-3);
		TestCase.assertEquals(MassType.INFINITE, mass.getType());
	}

	/**
	 * Tests the getArea method
	 */
	@Test
	public void getArea() {
		Ellipse e = new Ellipse(1.0, 0.5);
		TestCase.assertEquals(0.392, e.getArea(), 1e-3);
		
		e = new Ellipse(0.5, 1.0);
		TestCase.assertEquals(0.392, e.getArea(), 1e-3);
	}

	/**
	 * Tests the get rotation.
	 */
	@Test
	public void getRotation() {
		// the rotation intially is zero
		Ellipse e = new Ellipse(1.0, 0.5);
		TestCase.assertEquals(0.0, e.getRotationAngle());
		TestCase.assertEquals(1.0, e.getRotation().getCost());
		TestCase.assertEquals(0.0, e.getRotation().getSint());
		
		e = new Ellipse(1.0, 1.1);
		TestCase.assertEquals(0.0, e.getRotationAngle());
		TestCase.assertEquals(1.0, e.getRotation().getCost());
		TestCase.assertEquals(0.0, e.getRotation().getSint());
		
		e.rotate(Math.toRadians(30));
		TestCase.assertEquals(Math.toRadians(30), e.getRotationAngle(), 1e-8);
		TestCase.assertEquals(Math.cos(Math.toRadians(30)), e.getRotation().getCost(), 1e-8);
		TestCase.assertEquals(Math.sin(Math.toRadians(30)), e.getRotation().getSint(), 1e-8);
		
		e.rotate(Math.toRadians(-60));
		TestCase.assertEquals(Math.toRadians(-30), e.getRotationAngle(), 1e-8);
		TestCase.assertEquals(Math.cos(Math.toRadians(-30)), e.getRotation().getCost(), 1e-8);
		TestCase.assertEquals(Math.sin(Math.toRadians(-30)), e.getRotation().getSint(), 1e-8);
	}

	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Ellipse ellipse = new Ellipse(1.0, 0.5);
		ellipse.setUserData(new Object());
		Ellipse copy = ellipse.copy();
		
		TestCase.assertNotSame(ellipse, copy);
		TestCase.assertNotSame(ellipse.center, copy.center);
		TestCase.assertNotSame(ellipse.rotation, copy.rotation);
		TestCase.assertEquals(ellipse.radius, copy.radius);
		TestCase.assertEquals(ellipse.halfHeight, copy.halfHeight);
		TestCase.assertEquals(ellipse.halfWidth, copy.halfWidth);
		TestCase.assertNull(copy.userData);
		
		TestCase.assertEquals(ellipse.center.x, copy.center.x);
		TestCase.assertEquals(ellipse.center.y, copy.center.y);
		TestCase.assertEquals(ellipse.rotation.cost, copy.rotation.cost);
		TestCase.assertEquals(ellipse.rotation.sint, copy.rotation.sint);
	}
}
