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
package org.dyn4j.game2d.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Transform} object.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class TransformTest {
	/**
	 * Tests the identity method.
	 */
	@Test
	public void identity() {
		Transform t = new Transform();
		t.translate(5, 2);
		
		t.identity();
		
		TestCase.assertEquals(0.0, t.x);
		TestCase.assertEquals(0.0, t.y);
	}
	
	/**
	 * Test the translate method.
	 */
	@Test
	public void translate() {
		Transform t = new Transform();
		t.translate(2, -1);
		
		t.translate(4, 4);
		
		TestCase.assertEquals(6.000, t.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(3.000, t.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the rotate method.
	 */
	@Test
	public void rotate() {
		Transform t = new Transform();
		t.rotate(Math.toRadians(30));
		
		double r = t.getRotation();
		
		TestCase.assertEquals(30.000, Math.floor(Math.toDegrees(r) + 0.5), 1.0e-3);
		
		t.identity();
		
		t.translate(5, 5);
		t.rotate(Math.toRadians(90));
		
		Vector2 v = t.getTranslation();
		TestCase.assertEquals(-5.000, v.x, 1.0e-3);
		TestCase.assertEquals( 5.000, v.y, 1.0e-3);
		
		t.rotate(Math.toRadians(90));
		v = t.getTranslation();
		TestCase.assertEquals(-5.000, v.x, 1.0e-3);
		TestCase.assertEquals(-5.000, v.y, 1.0e-3);
		
		t.rotate(Math.toRadians(35), -5.0, -5.0);
		v = t.getTranslation();
		TestCase.assertEquals(-5.000, v.x, 1.0e-3);
		TestCase.assertEquals(-5.000, v.y, 1.0e-3);
		
		t.rotate(Math.toRadians(45), -1.0, -1.0);
		v = t.getTranslation();
		TestCase.assertEquals(-1.000, v.x, 1.0e-3);
		TestCase.assertEquals(-6.656, v.y, 1.0e-3);
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Transform t = new Transform();
		t.translate(2.0, -1.0);
		t.rotate(Math.toRadians(20), -2.0, 6.0);
		
		Transform tc = t.copy();
		
		TestCase.assertEquals(t.m00, tc.m00);
		TestCase.assertEquals(t.m01, tc.m01);
		TestCase.assertEquals(t.m10, tc.m10);
		TestCase.assertEquals(t.m11, tc.m11);
		TestCase.assertEquals(t.x,   tc.x);
		TestCase.assertEquals(t.y,   tc.y);
	}
	
	/**
	 * Tests the getTransformed methods.
	 */
	@Test
	public void getTransformed() {
		Transform t = new Transform();
		t.translate(2.0, 1.0);
		t.rotate(Math.toRadians(25), 1.0, -1.0);
		
		Vector2 v = new Vector2(1.0, 0.0);
		
		// test transformation
		Vector2 vt = t.getTransformed(v);
		TestCase.assertEquals(1.967, vt.x, 1.0e-3);
		TestCase.assertEquals(1.657, vt.y, 1.0e-3);
		
		// test inverse transformation
		vt = t.getInverseTransformed(vt);
		TestCase.assertEquals(1.000, vt.x, 1.0e-3);
		TestCase.assertEquals(0.000, vt.y, 1.0e-3);
		
		// test just a rotation transformation
		vt = t.getTransformedR(v);
		TestCase.assertEquals(0.906, vt.x, 1.0e-3);
		TestCase.assertEquals(0.422, vt.y, 1.0e-3);
		
		// test inverse rotation transformation
		vt = t.getInverseTransformedR(v);
		vt = t.getTransformedR(vt);
		TestCase.assertTrue(vt.equals(v));
	}
}
