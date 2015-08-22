/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests the methods of the {@link Mass} class.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.0.0
 */
public class MassTest {
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the mass must be > 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeMass() {
		new Mass(new Vector2(), -1.0, 1.0);
	}
	
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the inertia tensor must be > 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeInertia() {
		new Mass(new Vector2(), 1.0, -1.0);
	}
	
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the center is null.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullCenter() {
		new Mass(null, 1.0, 1.0);
	}
	
	/**
	 * Test the create method.
	 */
	@Test
	public void createSuccess() {
		Mass m = new Mass(new Vector2(), 1.0, 1.0);
		TestCase.assertTrue(m.getCenter().equals(new Vector2()));
		TestCase.assertEquals(m.getMass(), 1.0);
		TestCase.assertEquals(m.getInertia(), 1.0);
	}
	
	/**
	 * Test the create infinite method.
	 * @since 2.0.0
	 */
	@Test
	public void createInfinite() {
		Mass m = new Mass(new Vector2(), 0, 0);
		TestCase.assertTrue(m.isInfinite());
		TestCase.assertTrue(m.getCenter().equals(new Vector2()));
		TestCase.assertEquals(m.getMass(), 0.0);
		TestCase.assertEquals(m.getInertia(), 0.0);
	}

	/**
	 * Test the create fixed linear velocity method.
	 * @since 2.0.0
	 */
	@Test
	public void createFixedLinearVelocity() {
		Mass m = new Mass(new Vector2(), 0, 1.0);
		TestCase.assertFalse(m.isInfinite());
		TestCase.assertEquals(MassType.FIXED_LINEAR_VELOCITY, m.getType());
		TestCase.assertTrue(m.getCenter().equals(new Vector2()));
		TestCase.assertEquals(m.getMass(), 0.0);
		TestCase.assertEquals(m.getInertia(), 1.0);
	}
	
	/**
	 * Test the create fixed angular velocity method.
	 * @since 2.0.0
	 */
	@Test
	public void createFixedAngularVelocity() {
		Mass m = new Mass(new Vector2(), 1.0, 0.0);
		TestCase.assertFalse(m.isInfinite());
		TestCase.assertEquals(MassType.FIXED_ANGULAR_VELOCITY, m.getType());
		TestCase.assertTrue(m.getCenter().equals(new Vector2()));
		TestCase.assertEquals(m.getMass(), 1.0);
		TestCase.assertEquals(m.getInertia(), 0.0);
	}
	
	/**
	 * Test the create method.
	 * <p>
	 * Should throw an exception because the mass to copy is null.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void createCopyNull() {
		new Mass(null);
	}
	
	/**
	 * Test the create method.
	 * @since 2.0.0
	 */
	@Test
	public void createCopy() {
		Mass m = new Mass(new Vector2(1.0, 0.0), 2.0, 1.0);
		Mass m2 = new Mass(m);
		
		TestCase.assertNotSame(m, m2);
		TestCase.assertNotSame(m.center, m2.center);
		TestCase.assertEquals(m.center.x, m2.center.x);
		TestCase.assertEquals(m.center.y, m2.center.y);
		TestCase.assertEquals(m.getMass(), m2.getMass());
		TestCase.assertEquals(m.getInertia(), m2.getInertia());
		TestCase.assertEquals(m.getType(), m2.getType());
	}
	
	/**
	 * Test case for the circle create method.
	 */
	@Test
	public void createCircle() {
		Circle c = new Circle(3.0);
		Mass m = c.createMass(2.0);
		// the mass should be pi * r * r * d
		TestCase.assertEquals(56.548, m.getMass(), 1.0e-3);
		// I should be m * r * r / 2
		TestCase.assertEquals(254.469, m.getInertia(), 1.0e-3);
	}
	
	/**
	 * Test case for the polygon create method.
	 */
	@Test
	public void createPolygon() {
		Polygon p = Geometry.createUnitCirclePolygon(5, 0.5);
		Mass m = p.createMass(1.0);
		// the polygon mass should be the area * d
		TestCase.assertEquals(0.594, m.getMass(), 1.0e-3);
		TestCase.assertEquals(0.057, m.getInertia(), 1.0e-3);
	}
	
	/**
	 * Test case for the rectangle create method.
	 */
	@Test
	public void createRectangle() {
		Rectangle r = new Rectangle(1.0, 1.0);
		Mass m = r.createMass(1.5);
		// the mass of a rectangle should be h * w * d
		TestCase.assertEquals(1.500, m.getMass(), 1.0e-3);
		TestCase.assertEquals(0.250, m.getInertia(), 1.0e-3);
	}
	
	/**
	 * Test case for the segment create method.
	 */
	@Test
	public void createSegment() {
		Segment s = new Segment(new Vector2(-1.0, 0.0), new Vector2(1.0, 0.5));
		Mass m = s.createMass(1.0);
		// the mass of a segment should be l * d
		TestCase.assertEquals(2.061, m.getMass(), 1.0e-3);
		// the I of a segment should be 1 / 12 * l ^ 2 * m
		TestCase.assertEquals(0.730, m.getInertia(), 1.0e-3);
	}
	
	/**
	 * Test the create method accepting an array of {@link Mass} objects.
	 * <p>
	 * Renamed from createArray
	 * @since 2.0.0
	 */
	@Test
	public void createList() {
		Mass m1 = new Mass(new Vector2( 1.0,  1.0), 3.00, 1.00);
		Mass m2 = new Mass(new Vector2(-1.0,  0.0), 0.50, 0.02);
		Mass m3 = new Mass(new Vector2( 1.0, -2.0), 2.00, 3.00);
		List<Mass> masses = new ArrayList<Mass>();
		masses.add(m1);
		masses.add(m2);
		masses.add(m3);
		Mass m = Mass.create(masses);
		
		Vector2 c = m.getCenter();		
		TestCase.assertEquals( 0.818, c.x, 1.0e-3);
		TestCase.assertEquals(-0.181, c.y, 1.0e-3);
		TestCase.assertEquals( 5.500, m.getMass(), 1.0e-3);
		TestCase.assertEquals(16.656, m.getInertia(), 1.0e-3);
	}
	
	/**
	 * Test the create method accepting an array of infinite {@link Mass} objects.
	 * @since 2.0.0
	 */
	@Test
	public void createListInfinite() {
		Mass m1 = new Mass();
		Mass m2 = new Mass();
		Mass m3 = new Mass();
		List<Mass> masses = new ArrayList<Mass>();
		masses.add(m1);
		masses.add(m2);
		masses.add(m3);
		Mass m = Mass.create(masses);
		
		Vector2 c = m.getCenter();	
		TestCase.assertTrue(m.isInfinite());
		TestCase.assertEquals(0.000, c.x, 1.0e-3);
		TestCase.assertEquals(0.000, c.y, 1.0e-3);
		TestCase.assertEquals(0.000, m.getMass(), 1.0e-3);
		TestCase.assertEquals(0.000, m.getInertia(), 1.0e-3);
	}
	
	/**
	 * Test the create method accepting a list of one mass.
	 * @since 2.0.0
	 */
	@Test
	public void createListOneElement() {
		Mass m1 = new Mass(new Vector2(), 1.0, 2.0);
		List<Mass> masses = new ArrayList<Mass>();
		masses.add(m1);
		Mass m = Mass.create(masses);
		
		Vector2 c = m.getCenter();	
		TestCase.assertFalse(m.isInfinite());
		TestCase.assertNotSame(m1, m);
		TestCase.assertEquals(0.000, c.x, 1.0e-3);
		TestCase.assertEquals(0.000, c.y, 1.0e-3);
		TestCase.assertEquals(1.000, m.getMass(), 1.0e-3);
		TestCase.assertEquals(2.000, m.getInertia(), 1.0e-3);
	}

	/**
	 * Test the create method accepting a null list.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createListNull() {
		Mass.create(null);
	}
	
	/**
	 * Test the create method accepting an empty list.
	 * @since 3.1.0
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createListEmpty() {
		Mass.create(new ArrayList<Mass>());
	}
	
	/**
	 * Test the create method accepting a list of one null mass.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void createListOneNullElement() {
		List<Mass> masses = new ArrayList<Mass>();
		masses.add(null);
		Mass.create(masses);
	}
	
	/**
	 * Test the create method accepting a list masses where 1 is null.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void createListNullElement() {
		Mass m1 = new Mass(new Vector2(), 1.0, 2.0);
		Mass m2 = new Mass(new Vector2(), 2.0, 7.0);
		List<Mass> masses = new ArrayList<Mass>();
		masses.add(m1);
		masses.add(null);
		masses.add(m2);
		Mass.create(masses);
	}
	
	/**
	 * Tests setting the type of mass.
	 * @since 1.0.2
	 */
	@Test
	public void setType() {
		Circle c = Geometry.createCircle(2.0);
		Mass mi = c.createMass(1.0);
		
		// setting the type should not alter the
		// mass values
		mi.setType(MassType.INFINITE);
		TestCase.assertTrue(mi.isInfinite());
		TestCase.assertFalse(0.0 == mi.mass);
		TestCase.assertFalse(0.0 == mi.invMass);
		TestCase.assertFalse(0.0 == mi.inertia);
		TestCase.assertFalse(0.0 == mi.invInertia);
		// the get methods should return 0
		TestCase.assertEquals(0.0, mi.getMass());
		TestCase.assertEquals(0.0, mi.getInverseMass());
		TestCase.assertEquals(0.0, mi.getInertia());
		TestCase.assertEquals(0.0, mi.getInverseInertia());
		
		mi.setType(MassType.FIXED_ANGULAR_VELOCITY);
		TestCase.assertFalse(0.0 == mi.mass);
		TestCase.assertFalse(0.0 == mi.invMass);
		TestCase.assertFalse(0.0 == mi.inertia);
		TestCase.assertFalse(0.0 == mi.invInertia);
		TestCase.assertEquals(0.0, mi.getInertia());
		TestCase.assertEquals(0.0, mi.getInverseInertia());
		
		mi.setType(MassType.FIXED_LINEAR_VELOCITY);
		TestCase.assertFalse(0.0 == mi.mass);
		TestCase.assertFalse(0.0 == mi.invMass);
		TestCase.assertFalse(0.0 == mi.inertia);
		TestCase.assertFalse(0.0 == mi.invInertia);
		TestCase.assertEquals(0.0, mi.getMass());
		TestCase.assertEquals(0.0, mi.getInverseMass());
	}
	
	/**
	 * Tests setting the type of mass to null.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullType() {
		Mass m = new Mass();
		m.setType(null);
	}
	
	/**
	 * Tests the inertia and COM calculations for polygon shapes.
	 * @since 3.1.4
	 */
	@Test
	public void polygonInertiaAndCOM() {
		// a polygon of a simple shape should match a simple shape's mass and inertia
		Polygon p = Geometry.createUnitCirclePolygon(4, Math.hypot(0.5, 0.5));
		Rectangle r = Geometry.createSquare(1.0);
		
		Mass pm = p.createMass(10.0);
		Mass rm = r.createMass(10.0);
		
		TestCase.assertEquals(rm.mass, pm.mass, 1.0e-3);
		TestCase.assertEquals(rm.inertia, pm.inertia, 1.0e-3);
	}

	/**
	 * Make sure the center of mass does not effect the mass or inertia.
	 * @since 3.1.5
	 */
	@Test
	public void polygonInertiaAndMass() {
		// a polygon of a simple shape should match a simple shape's mass and inertia
		Polygon p = Geometry.createUnitCirclePolygon(4, Math.hypot(0.5, 0.5));
		Mass m1 = p.createMass(10.0);
		
		p.translate(0.5, -2.0);
		Mass m2 = p.createMass(10.0);
		
		TestCase.assertEquals(m1.mass, m2.mass, 1.0e-3);
		TestCase.assertEquals(m1.inertia, m2.inertia, 1.0e-3);
	}
}
