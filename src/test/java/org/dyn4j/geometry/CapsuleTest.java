/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
 * Test case for the {@link Capsule} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 3.1.5
 */
public class CapsuleTest {
	/** Identity Transform instance */
	private static final Transform IDENTITY = new Transform();
	
	/**
	 * Tests a zero width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroWidth() {
		new Capsule(0.0, 1.0);
	}
	
	/**
	 * Tests a negative width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeWidth() {
		new Capsule(-1.0, 1.0);
	}
	
	/**
	 * Tests a zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeight() {
		new Capsule(1.0, 0.0);
	}
	
	/**
	 * Tests a negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeight() {
		new Capsule(1.0, -1.0);
	}
	
	/**
	 * Tests a capsule that should be a circle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCircle() {
		new Capsule(1.0, 1.0);
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccessHorizontal() {
		Capsule cap = new Capsule(2.0, 1.0);
		Vector2 x = cap.localXAxis;
		TestCase.assertEquals(1.000, x.x, 1.0e-3);
		TestCase.assertEquals(0.000, x.y, 1.0e-3);
		
		TestCase.assertNotNull(cap.toString());
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccessVertical() {
		Capsule cap = new Capsule(1.0, 2.0);
		Vector2 x = cap.localXAxis;
		TestCase.assertEquals(0.000, x.x, 1.0e-3);
		TestCase.assertEquals(1.000, x.y, 1.0e-3);
		
		TestCase.assertNotNull(cap.toString());
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Capsule e = new Capsule(2.0, 1.0);
		Transform t = new Transform();
		Vector2 p = new Vector2(0.8, -0.45);
		
		// shouldn't be inside
		TestCase.assertTrue(!e.contains(p, t));
		
		// move it a bit
		t.translate(0.5, 0.0);
		
		// should be inside
		TestCase.assertTrue(e.contains(p, t));
		
		p.set(1.5, 0.0);
		// should be on the edge
		TestCase.assertTrue(e.contains(p, t));
		p.set(0.75, 0.5);
		// should be on the edge
		TestCase.assertTrue(e.contains(p, t));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Capsule e = new Capsule(2.0, 1.0);
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
		TestCase.assertEquals(-1.25, i.min, 1.0e-3);
		TestCase.assertEquals( 0.25, i.max, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotate(Math.toRadians(30), 1.0, 0.5);
		
		i = e.project(y, IDENTITY);
		TestCase.assertEquals(-1.25, i.min, 1.0e-3);
		TestCase.assertEquals( 0.25, i.max, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		i = e.project(y, t);
		TestCase.assertEquals(-2.25, i.min, 1.0e-3);
		TestCase.assertEquals(-0.75, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the farthest methods.
	 */
	@Test
	public void getFarthest() {
		Capsule e = new Capsule(2.0, 1.0);
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
		TestCase.assertEquals( 0.566, p.x, 1.0e-3);
		TestCase.assertEquals(-0.25, p.y, 1.0e-3);
		
		// try some local rotation
		e.translate(1.0, 0.5);
		e.rotate(Math.toRadians(30), 1.0, 0.5);
		
		p = e.getFarthestPoint(y, IDENTITY);
		TestCase.assertEquals( 0.566, p.x, 1.0e-3);
		TestCase.assertEquals(-0.25, p.y, 1.0e-3);
		
		t.identity();
		t.translate(0.0, 1.0);
		p = e.getFarthestPoint(y, t);
		TestCase.assertEquals( 0.566, p.x, 1.0e-3);
		TestCase.assertEquals( 0.75, p.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Capsule e = new Capsule(1.0, 0.5);
		// should be two axes + number of foci
		Vector2[] foci = new Vector2[] {
			new Vector2(2.0, -0.5),
			new Vector2(1.0, 3.0)
		};
		Vector2[] axes = e.getAxes(foci, IDENTITY);
		TestCase.assertEquals(4, axes.length);
		
		// make sure we get back the right axes
		axes = e.getAxes(null, IDENTITY);
		TestCase.assertEquals(1.000, axes[0].x, 1.0e-3);
		TestCase.assertEquals(0.000, axes[0].y, 1.0e-3);
		TestCase.assertEquals(0.000, axes[1].x, 1.0e-3);
		TestCase.assertEquals(1.000, axes[1].y, 1.0e-3);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test
	public void getFoci() {
		Capsule e = new Capsule(1.0, 0.5);
		Vector2[] foci = e.getFoci(IDENTITY);
		// should be two foci
		TestCase.assertEquals(2, foci.length);
		// make sure the foci are correct
		TestCase.assertEquals(-0.250, foci[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, foci[0].y, 1.0e-3);
		TestCase.assertEquals( 0.250, foci[1].x, 1.0e-3);
		TestCase.assertEquals( 0.000, foci[1].y, 1.0e-3);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		Capsule e = new Capsule(1.0, 0.5);
		
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
		Capsule e = new Capsule(1.0, 0.5);
		
		e.translate(1.0, -0.5);
		
		TestCase.assertEquals( 1.000, e.center.x, 1.0e-3);
		TestCase.assertEquals(-0.500, e.center.y, 1.0e-3);
	}
	
	/**
	 * Tests the generated AABB.
	 */
	@Test
	public void createAABB() {
		Capsule e = new Capsule(1.0, 0.5);
		
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
		TestCase.assertEquals(0.533, aabb.getMinX(), 1.0e-3);
		TestCase.assertEquals(1.625, aabb.getMinY(), 1.0e-3);
		TestCase.assertEquals(1.466, aabb.getMaxX(), 1.0e-3);
		TestCase.assertEquals(2.375, aabb.getMaxY(), 1.0e-3);
	}
	
	/**
	 * Tests the mass calculation.
	 */
	@Test
	public void createMass() {
		Capsule e = new Capsule(1.0, 0.5);
		Mass mass = e.createMass(1.0);
		
		TestCase.assertEquals(0.446, mass.getMass(), 1e-3);
		TestCase.assertEquals(0.028, mass.getInertia(), 1e-3);
		TestCase.assertEquals(2.240, mass.getInverseMass(), 1e-3);
		TestCase.assertEquals(34.692, mass.getInverseInertia(), 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().x, 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().y, 1e-3);
		TestCase.assertEquals(MassType.NORMAL, mass.getType());
		
		e = new Capsule(0.5, 1.0);
		mass = e.createMass(1.0);
		
		TestCase.assertEquals(0.446, mass.getMass(), 1e-3);
		TestCase.assertEquals(0.028, mass.getInertia(), 1e-3);
		TestCase.assertEquals(2.240, mass.getInverseMass(), 1e-3);
		TestCase.assertEquals(34.692, mass.getInverseInertia(), 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().x, 1e-3);
		TestCase.assertEquals(0.0, mass.getCenter().y, 1e-3);
		TestCase.assertEquals(MassType.NORMAL, mass.getType());
	}

	/**
	 * Tests the get radius.
	 */
	@Test
	public void getRadius() {
		// the radius for a capsule is half the largest dimension
		Capsule e = new Capsule(1.0, 0.5);
		TestCase.assertEquals(0.5, e.getRadius());
		TestCase.assertEquals(1.5, e.getRadius(new Vector2(-1.0, 0.0)));
		TestCase.assertEquals(5.403, e.getRadius(new Vector2(-3.0, 4.0)), 1e-3);
		
		e = new Capsule(1.0, 1.1);
		TestCase.assertEquals(0.55, e.getRadius());
		TestCase.assertEquals(1.55, e.getRadius(new Vector2(0.0, -1.0)), 1e-3);
		TestCase.assertEquals(1.501, e.getRadius(new Vector2(-1.0, 0.0)), 1e-3);
	}

	/**
	 * Tests the get rotation.
	 */
	@Test
	public void getRotation() {
		// the rotation intially is zero
		Capsule e = new Capsule(1.0, 0.5);
		TestCase.assertEquals(0.0, e.getRotationAngle());
		TestCase.assertEquals(1.0, e.getRotation().getCost());
		TestCase.assertEquals(0.0, e.getRotation().getSint());
		
		// for a horizontal it's 90 degrees
		e = new Capsule(1.0, 1.1);
		TestCase.assertEquals(Math.PI * 0.5, e.getRotationAngle());
		TestCase.assertEquals(0.0, e.getRotation().getCost());
		TestCase.assertEquals(1.0, e.getRotation().getSint());
		
		e.rotate(Math.toRadians(30));
		TestCase.assertEquals(Math.PI * 0.5 + Math.toRadians(30), e.getRotationAngle(), 1e-8);
		TestCase.assertEquals(Math.cos(Math.toRadians(120)), e.getRotation().getCost(), 1e-8);
		TestCase.assertEquals(Math.sin(Math.toRadians(120)), e.getRotation().getSint(), 1e-8);
		
		e.rotate(Math.toRadians(-60));
		TestCase.assertEquals(Math.PI * 0.5 - Math.toRadians(30), e.getRotationAngle(), 1e-8);
		TestCase.assertEquals(Math.cos(Math.toRadians(60)), e.getRotation().getCost(), 1e-8);
		TestCase.assertEquals(Math.sin(Math.toRadians(60)), e.getRotation().getSint(), 1e-8);
	}
}
