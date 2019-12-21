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
package org.dyn4j.geometry;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Rotation} class.
 * @author Manolis Tsamis
 * @since 3.4.0
 */
public class RotationTest {
	/**
	 * Tests the create methods.
	 */
	@Test
	public void create() {
		Rotation r1 = new Rotation();
		// should default to zero angle
		TestCase.assertEquals(1.0, r1.cost, 1.0e-6);
		TestCase.assertEquals(0.0, r1.sint, 1.0e-6);
		
		Rotation r2 = new Rotation(Math.PI);
		TestCase.assertEquals(-1.0, r2.cost, 1.0e-6);
		TestCase.assertEquals(0.0, r2.sint, 1.0e-6);
		
		Rotation r3 = new Rotation(-1.0, 0.0);
		TestCase.assertEquals(-1.0, r3.cost);
		TestCase.assertEquals(0.0, r3.sint);
		
		Rotation r4 = new Rotation(1.0);
		TestCase.assertEquals(Math.cos(1.0), r4.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(1.0), r4.sint, 1.0e-6);
		
		Rotation r5 = new Rotation(r4);
		TestCase.assertEquals(Math.cos(1.0), r5.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(1.0), r5.sint, 1.0e-6);

		Rotation r6 = Rotation.of(2.5);
		TestCase.assertEquals(Math.cos(2.5), r6.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(2.5), r6.sint, 1.0e-6);
		
		Vector2 v1 = new Vector2(5, -5);
		
		Rotation r7 = Rotation.of(v1);
		TestCase.assertEquals(Math.cos(Math.toRadians(-45)), r7.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(-45)), r7.sint, 1.0e-6);
		
		Transform t1 = new Transform();
		t1.setRotation(-1.0);
		t1.translate(-10, -20); // translation should not affect the result
		
		Rotation r8 = Rotation.of(t1);
		TestCase.assertEquals(Math.cos(-1.0), r8.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(-1.0), r8.sint, 1.0e-6);
		
		// test static methods for creation of common angles
		
		Rotation r0 = Rotation.rotation0();
		TestCase.assertEquals(Math.cos(Math.toRadians(0.0)), r0.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(0.0)), r0.sint, 1.0e-6);

		Rotation r90 = Rotation.rotation90();
		TestCase.assertEquals(Math.cos(Math.toRadians(90.0)), r90.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(90.0)), r90.sint, 1.0e-6);

		Rotation r180 = Rotation.rotation180();
		TestCase.assertEquals(Math.cos(Math.toRadians(180.0)), r180.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(180.0)), r180.sint, 1.0e-6);

		Rotation r270 = Rotation.rotation270();
		TestCase.assertEquals(Math.cos(Math.toRadians(270.0)), r270.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(270.0)), r270.sint, 1.0e-6);
		
		Rotation r45 = Rotation.rotation45();
		TestCase.assertEquals(Math.cos(Math.toRadians(45.0)), r45.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(45.0)), r45.sint, 1.0e-6);

		Rotation r135 = Rotation.rotation135();
		TestCase.assertEquals(Math.cos(Math.toRadians(135.0)), r135.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(135.0)), r135.sint, 1.0e-6);

		Rotation r225 = Rotation.rotation225();
		TestCase.assertEquals(Math.cos(Math.toRadians(225.0)), r225.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(225.0)), r225.sint, 1.0e-6);

		Rotation r315 = Rotation.rotation315();
		TestCase.assertEquals(Math.cos(Math.toRadians(315.0)), r315.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(Math.toRadians(315.0)), r315.sint, 1.0e-6);
	}
	
	/**
	 * Tests the unit length property of rotations.
	 */
	@Test
	public void testIsUnit() {
		// For any angle, the corresponding {@link Rotation} is always a unit vector
		for (int i=0;i<1000;i++) {
			Rotation r = new Rotation(Math.toRadians(i));
			TestCase.assertEquals(1.0, r.dot(r), 1.0e-6);
		}
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Rotation r = new Rotation(1.0, 3.0);
		Rotation rc = r.copy();
		
		TestCase.assertFalse(r == rc);
		TestCase.assertEquals(r.cost, rc.cost);
		TestCase.assertEquals(r.sint, rc.sint);
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void equals() {
		Rotation r = new Rotation(2.0);
		
		TestCase.assertTrue(r.equals(r));
		TestCase.assertTrue(r.equals(r.copy()));
		TestCase.assertTrue(r.equals(new Rotation(2.0)));
		
		TestCase.assertFalse(r.equals(new Rotation(1.0)));
		TestCase.assertFalse(r.equals(new Object()));
		TestCase.assertFalse(r.equals(null));
		
		TestCase.assertTrue(r.equals(r, 1.0e-4));
		Rotation r2 = r.copy().rotate(5).rotate(-5);
		TestCase.assertTrue(r.equals(r2, 1.0e-4));
		
		TestCase.assertFalse(r.equals(new Rotation(1.0), 1.0e-4));

		TestCase.assertTrue(r.equals(2.0));
		TestCase.assertTrue(r.equals(2.0 + 1.0e-6, 1.0e-4));
		
		TestCase.assertFalse(r.equals(1.0));
		TestCase.assertFalse(r.equals(2.0 + 1.0e-2, 1.0e-4));
	}
	
	/**
	 * Tests the set methods.
	 */
	@Test
	public void set() {
		Rotation r = new Rotation(2.0);
		
		Rotation r2 = new Rotation(3.0);
		r.set(r2);
		
		TestCase.assertFalse(r == r2);
		TestCase.assertEquals(Math.cos(3.0), r.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(3.0), r.sint, 1.0e-6);
		TestCase.assertEquals(r2.cost, r.cost);
		TestCase.assertEquals(r2.sint, r.sint);
		
		r.set(-1.0);
		TestCase.assertEquals(Math.cos(-1.0), r.cost, 1.0e-6);
		TestCase.assertEquals(Math.sin(-1.0), r.sint, 1.0e-6);
		
		r.setIdentity();
		TestCase.assertEquals(1.0, r.cost, 1.0e-6);
		TestCase.assertEquals(0.0, r.sint, 1.0e-6);
	}

	/**
	 * Tests the get methods.
	 */
	@Test
	public void get() {
		Rotation r = new Rotation(1.5);
		
		TestCase.assertEquals(Math.cos(1.5), r.getCost());
		TestCase.assertEquals(Math.sin(1.5), r.getSint());
	}

	/**
	 * Tests radians/degrees conversion methods.
	 */
	@Test
	public void toAngle() {
		Rotation r = new Rotation(0.0, -1.0);
		
		TestCase.assertEquals(-Math.PI / 2, r.toRadians(), 1.0e-6);
		TestCase.assertEquals(-90, r.toDegrees(), 1.0e-6);
		
		r.rotate135();
		
		TestCase.assertEquals(Math.PI / 4, r.toRadians(), 1.0e-6);
		TestCase.assertEquals(45, r.toDegrees(), 1.0e-6);
	}
	
	/**
	 * Tests vector conversion methods.
	 */
	@Test
	public void toVector() {
		Rotation r = new Rotation(Math.toRadians(30));
		Vector2 v1 = r.toVector();
		Vector2 v2 = r.toVector(2.5);
		
		TestCase.assertEquals(r.toRadians(), v1.getDirection(), 1.0e-6);
		TestCase.assertEquals(r.toRadians(), v2.getDirection(), 1.0e-6);
		
		TestCase.assertEquals(1.0, v1.getMagnitude(), 1.0e-6);
		TestCase.assertEquals(2.5, v2.getMagnitude(), 1.0e-6);
	}
	
	/**
	 * Tests the dot method.
	 */
	@Test
	public void dot() {
		Rotation r1 = new Rotation(1.0, 0.0);
		Rotation r2 = new Rotation(0.0, 1.0);
		Vector2 v = new Vector2(0.0, -5.0);
		
		TestCase.assertEquals(0.0, r1.dot(r2), 1.0e-6);
		TestCase.assertEquals(-1.0, r1.dot(r2.getRotated90()), 1.0e-6);
		TestCase.assertEquals(0.0, r1.dot(v), 1.0e-6);
		
		// all Rotation objects are unit vectors
		TestCase.assertEquals(1.0, r1.dot(r1), 1.0e-6);
		TestCase.assertEquals(1.0, r2.dot(r2), 1.0e-6);
	}
	
	/**
	 * Tests the cross product methods.
	 */
	@Test
	public void cross() {
		Rotation r1 = new Rotation(1.0, 0.0);
		Rotation r2 = new Rotation(0.0, 1.0);
		Vector2 v = new Vector2(-5.0, 0.0);
		
		TestCase.assertEquals(1.0, r1.cross(r2), 1.0e-6);
		TestCase.assertEquals(0.0, r1.cross(r2.getRotated90()), 1.0e-6);
		TestCase.assertEquals(0.0, r1.cross(v), 1.0e-6);
		
		TestCase.assertEquals(0.0, r1.cross(r1), 1.0e-6);
		TestCase.assertEquals(0.0, r2.cross(r2), 1.0e-6);
	}
	
	/**
	 * Tests the isIdentity method.
	 */
	@Test
	public void isIdentity() {
		Rotation r = new Rotation();
		
		TestCase.assertTrue(r.isIdentity());
		TestCase.assertTrue(r.isIdentity(1.0e-6));
		
		r.set(1.0);
		TestCase.assertFalse(r.isIdentity());
		TestCase.assertFalse(r.isIdentity(1.0e-6));
		
		r.set(1.0e-6);
		TestCase.assertFalse(r.isIdentity());
		TestCase.assertTrue(r.isIdentity(1.0e-4));
	}
	
	/**
	 * Tests the inverse method.
	 */
	@Test
	public void inverse() {
		Rotation r1 = new Rotation(0.0);

		r1.inverse();
		TestCase.assertEquals(1.0, r1.cost, 1.0e-6);
		TestCase.assertEquals(0.0, r1.sint, 1.0e-6);
		
		Rotation r2 = new Rotation(Math.PI / 2.0);
		Rotation r3 = r2.copy();
		
		r2.inverse();
		TestCase.assertEquals(0.0, r2.cost, 1.0e-6);
		TestCase.assertEquals(-1.0, r2.sint, 1.0e-6);
		
		r2.inverse();
		TestCase.assertEquals(r3, r2);
		
		Rotation temp = r2.getInversed();
		TestCase.assertEquals(0.0, temp.cost, 1.0e-6);
		TestCase.assertEquals(-1.0, temp.sint, 1.0e-6);
		
		TestCase.assertEquals(r3, r2);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		Rotation r1 = new Rotation(Math.toRadians(0));
		
		r1.rotate(Math.toRadians(90));
		TestCase.assertEquals(0.0, r1.cost, 1.0e-6);
		TestCase.assertEquals(1.0, r1.sint, 1.0e-6);
		
		r1.rotate(new Rotation(Math.toRadians(90)));
		TestCase.assertEquals(-1.0, r1.cost, 1.0e-6);
		TestCase.assertEquals( 0.0, r1.sint, 1.0e-6);
		
		Rotation r2 = new Rotation(Math.toRadians(45));
		
		r2.rotate(Math.toRadians(63));
		TestCase.assertEquals(-0.309, r2.cost, 1.0e-3);
		TestCase.assertEquals( 0.951, r2.sint, 1.0e-3);
		
		r2.rotate(new Rotation(Math.toRadians(29)));
		TestCase.assertEquals(-0.731, r2.cost, 1.0e-3);
		TestCase.assertEquals( 0.682, r2.sint, 1.0e-3);
		
		Rotation r3 = new Rotation(Math.toRadians(60));
		
		TestCase.assertEquals(0.500, r3.cost, 1.0e-3);
		TestCase.assertEquals(0.866, r3.sint, 1.0e-3);
		
		r3.rotate90();
		TestCase.assertEquals(-0.866, r3.cost, 1.0e-3);
		TestCase.assertEquals( 0.500, r3.sint, 1.0e-3);
		
		r3.rotate180();
		TestCase.assertEquals( 0.866, r3.cost, 1.0e-3);
		TestCase.assertEquals(-0.500, r3.sint, 1.0e-3);
		
		r3.rotate270();
		TestCase.assertEquals(-0.500, r3.cost, 1.0e-3);
		TestCase.assertEquals(-0.866, r3.sint, 1.0e-3);
		
		r3.rotate90();
		TestCase.assertEquals( 0.866, r3.cost, 1.0e-3);
		TestCase.assertEquals(-0.500, r3.sint, 1.0e-3);
		
		r3.rotate45();
		TestCase.assertEquals(0.966, r3.cost, 1.0e-3);
		TestCase.assertEquals(0.259, r3.sint, 1.0e-3);
		
		r3.rotate45();
		TestCase.assertEquals(0.500, r3.cost, 1.0e-3);
		TestCase.assertEquals(0.866, r3.sint, 1.0e-3);
		
		r3.rotate135();
		TestCase.assertEquals(-0.966, r3.cost, 1.0e-3);
		TestCase.assertEquals(-0.259, r3.sint, 1.0e-3);
		
		r3.rotate225();
		TestCase.assertEquals(0.500, r3.cost, 1.0e-3);
		TestCase.assertEquals(0.866, r3.sint, 1.0e-3);
		
		r3.rotate315();
		TestCase.assertEquals(0.966, r3.cost, 1.0e-3);
		TestCase.assertEquals(0.259, r3.sint, 1.0e-3);
	}
	
	/**
	 * Tests the getRotated methods.
	 */
	@Test
	public void getRotated() {
		Rotation temp;
		Rotation r1 = new Rotation(Math.toRadians(0));
		
		temp = r1.getRotated(Math.toRadians(90));
		TestCase.assertEquals(0.0, temp.cost, 1.0e-6);
		TestCase.assertEquals(1.0, temp.sint, 1.0e-6);
		
		temp = r1.getRotated(new Rotation(Math.toRadians(180)));
		TestCase.assertEquals(-1.0, temp.cost, 1.0e-6);
		TestCase.assertEquals( 0.0, temp.sint, 1.0e-6);
		
		Rotation r2 = new Rotation(Math.toRadians(45));
		
		temp = r2.getRotated(Math.toRadians(63));
		TestCase.assertEquals(-0.309, temp.cost, 1.0e-3);
		TestCase.assertEquals( 0.951, temp.sint, 1.0e-3);
		
		temp = r2.getRotated(new Rotation(Math.toRadians(29)));
		TestCase.assertEquals(0.276, temp.cost, 1.0e-3);
		TestCase.assertEquals(0.961, temp.sint, 1.0e-3);
		
		Rotation r3 = new Rotation(Math.toRadians(60));
		
		TestCase.assertEquals(0.500, r3.cost, 1.0e-3);
		TestCase.assertEquals(0.866, r3.sint, 1.0e-3);
		
		temp = r3.getRotated90();
		TestCase.assertEquals(-0.866, temp.cost, 1.0e-3);
		TestCase.assertEquals( 0.500, temp.sint, 1.0e-3);
		
		temp = r3.getRotated180();
		TestCase.assertEquals(-0.500, temp.cost, 1.0e-3);
		TestCase.assertEquals(-0.866, temp.sint, 1.0e-3);
		
		temp = r3.getRotated270();
		TestCase.assertEquals( 0.866, temp.cost, 1.0e-3);
		TestCase.assertEquals(-0.500, temp.sint, 1.0e-3);
		
		temp = r3.getRotated45();
		TestCase.assertEquals(-0.259, temp.cost, 1.0e-3);
		TestCase.assertEquals( 0.966, temp.sint, 1.0e-3);
		
		temp = r3.getRotated135();
		TestCase.assertEquals(-0.966, temp.cost, 1.0e-3);
		TestCase.assertEquals(-0.259, temp.sint, 1.0e-3);
		
		temp = r3.getRotated225();
		TestCase.assertEquals( 0.259, temp.cost, 1.0e-3);
		TestCase.assertEquals(-0.966, temp.sint, 1.0e-3);
		
		temp = r3.getRotated315();
		TestCase.assertEquals(0.966, temp.cost, 1.0e-3);
		TestCase.assertEquals(0.259, temp.sint, 1.0e-3);
	}
	
	/**
	 * Tests the compare methods.
	 */
	@Test
	public void compare() {
		Rotation r1 = new Rotation(Math.toRadians(10));
		Rotation r2 = new Rotation(Math.toRadians(100));
		Rotation r3 = new Rotation(Math.toRadians(100));
		Rotation r4 = new Rotation(Math.toRadians(-50));
		Rotation r5 = new Rotation(Math.toRadians(110 + 10 * 360));
		
		Vector2 v1 = new Vector2(Math.toRadians(-65));
		Vector2 v2 = new Vector2(Math.toRadians(120));
		v1.multiply(4.5);
		v2.multiply(0.75);
		
		TestCase.assertEquals(0, r1.compare(r1));
		
		TestCase.assertEquals(0, r2.compare(r3));
		TestCase.assertEquals(1, r1.compare(r2));
		TestCase.assertEquals(1, r3.compare(r5));
		TestCase.assertEquals(-1, r1.compare(r4));
		
		TestCase.assertEquals(1, r3.compare(v2));
		TestCase.assertEquals(-1, r4.compare(v1));
	}
	
	/**
	 * Tests the getRotationBetween methods.
	 */
	@Test
	public void getRotationBetween() {
		Rotation r1 = new Rotation(Math.toRadians(10));
		Rotation r2 = new Rotation(Math.toRadians(100));
		Rotation r3 = new Rotation(Math.toRadians(100));
		Rotation r4 = new Rotation(Math.toRadians(-50));
		Rotation r5 = new Rotation(Math.toRadians(110 + 10 * 360));
		
		Vector2 v1 = new Vector2(Math.toRadians(-65));
		Vector2 v2 = new Vector2(Math.toRadians(120));
		v1.multiply(4.5);
		v2.multiply(0.75);
		
		TestCase.assertEquals(Math.toRadians(0), r1.getRotationBetween(r1).toRadians(), 1.0e-6);
		TestCase.assertEquals(Math.toRadians(0), r2.getRotationBetween(r3).toRadians(), 1.0e-6);
		TestCase.assertEquals(Math.toRadians(90), r1.getRotationBetween(r2).toRadians(), 1.0e-6);
		TestCase.assertEquals(Math.toRadians(-60), r1.getRotationBetween(r4).toRadians(), 1.0e-6);
		TestCase.assertEquals(Math.toRadians(10), r3.getRotationBetween(r5).toRadians(), 1.0e-6);
		
		TestCase.assertEquals(Math.toRadians(20), r3.getRotationBetween(v2).toRadians(), 1.0e-6);
		TestCase.assertEquals(Math.toRadians(-15), r4.getRotationBetween(v1).toRadians(), 1.0e-6);
	}
	
}
