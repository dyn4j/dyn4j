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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Transform} object.
 * @author William Bittle
 * @version 3.1.0
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
		
		TestCase.assertEquals(t.cost, tc.cost);
		TestCase.assertEquals(t.sint, tc.sint);
		//TestCase.assertEquals(t.m10, tc.m10);
		//TestCase.assertEquals(t.m11, tc.m11);
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
	
	/**
	 * Tests the transform methods.
	 * @since 3.1.0
	 */
	@Test
	public void transform() {
		Transform t = new Transform();
		t.translate(2.0, 1.0);
		t.rotate(Math.toRadians(25), 1.0, -1.0);
		
		Vector2 v = new Vector2(1.0, 0.0);
		
		// test transformation
		t.transform(v);
		TestCase.assertEquals(1.967, v.x, 1.0e-3);
		TestCase.assertEquals(1.657, v.y, 1.0e-3);
		
		// test inverse transformation
		t.inverseTransform(v);
		TestCase.assertEquals(1.000, v.x, 1.0e-3);
		TestCase.assertEquals(0.000, v.y, 1.0e-3);
		
		// test just a rotation transformation
		t.transformR(v);
		TestCase.assertEquals(0.906, v.x, 1.0e-3);
		TestCase.assertEquals(0.422, v.y, 1.0e-3);
		
		// test inverse rotation transformation
		t.inverseTransformR(v);
		t.transformR(v);
		TestCase.assertTrue(v.equals(v));
	}
	
	/**
	 * Tests the setTransform method.
	 */
	@Test
	public void setTransform() {
		Transform tx = new Transform();
		tx.rotate(Math.toRadians(30));
		tx.translate(2.0, 0.5);
		Transform tx2 = new Transform();
		tx2.set(tx);
		
		// shouldnt be the same object reference
		TestCase.assertNotSame(tx2, tx);
		
		// should be the same transformation
		TestCase.assertEquals(tx.cost, tx2.cost);
		TestCase.assertEquals(tx.sint, tx2.sint);
		//TestCase.assertEquals(tx.m10, tx2.m10);
		//TestCase.assertEquals(tx.m11, tx2.m11);
		TestCase.assertEquals(tx.x, tx2.x);
		TestCase.assertEquals(tx.y, tx2.y);
	}
	
	/**
	 * Tests the setTranslation methods.
	 */
	@Test
	public void setTranslation() {
		Transform tx = new Transform();
		tx.translate(1.0, 2.0);
		tx.rotate(Math.toRadians(45));
		tx.setTranslation(0.0, 0.0);
		
		TestCase.assertEquals(0.0, tx.x);
		TestCase.assertEquals(0.0, tx.y);
		TestCase.assertEquals(Math.toRadians(45.000), tx.getRotation(), 1.0e-3);
		
		tx.setTranslationX(2.0);
		TestCase.assertEquals(2.0, tx.x);
		TestCase.assertEquals(0.0, tx.y);
		TestCase.assertEquals(Math.toRadians(45.000), tx.getRotation(), 1.0e-3);
		
		tx.setTranslationY(3.0);
		TestCase.assertEquals(2.0, tx.x);
		TestCase.assertEquals(3.0, tx.y);
		TestCase.assertEquals(Math.toRadians(45.000), tx.getRotation(), 1.0e-3);
	}
	
	/**
	 * Tests the setRotation method.
	 */
	@Test
	public void setRotation() {
		Transform tx = new Transform();
		tx.rotate(Math.toRadians(45.0));
		tx.translate(1.0, 0.0);
		
		tx.setRotation(Math.toRadians(30.0));
		TestCase.assertEquals(30.000, Math.toDegrees(tx.getRotation()), 1.0e-3);
		TestCase.assertEquals(1.0, tx.x);
		TestCase.assertEquals(0.0, tx.y);
	}
	
	/**
	 * Tests the linear interpolation methods.
	 */
	@Test
	public void lerp() {
		Vector2 p = new Vector2();
		
		Transform start = new Transform();
		start.translate(1.0, 0.0);
		start.rotate(Math.toRadians(45));
		
		Transform end = new Transform();
		end.set(start);
		end.translate(3.0, 2.0);
		end.rotate(Math.toRadians(20));
		
		Vector2 s = start.getTransformed(p);
		Vector2 e = end.getTransformed(p);
		
		final double alpha = 0.5;
		
		Transform mid = new Transform();
		start.lerp(end, alpha, mid);
		start.lerp(end, alpha);
		
		Vector2 m = mid.getTransformed(p);
		// this test only works this way for the mid point
		// otherwise we would have to replicate the lerp method
		TestCase.assertEquals((s.x + e.x) * alpha, m.x);
		TestCase.assertEquals((s.y + e.y) * alpha, m.y);
		
		m = start.getTransformed(p);
		// this test only works this way for the mid point
		// otherwise we would have to replicate the lerp method
		TestCase.assertEquals((s.x + e.x) * alpha, m.x);
		TestCase.assertEquals((s.y + e.y) * alpha, m.y);
		
		// test opposing sign angles
		start.identity();
		start.rotate(Math.toRadians(174.0));
		
		end.identity();
		end.rotate(Math.toRadians(-168));
		
		Transform l = start.lerped(end, alpha);
		TestCase.assertEquals(-3.089, l.getRotation(), 1.0e-3);
		
		// test opposing sign angles
		start.identity();
		start.rotate(Math.toRadians(354.0));
		
		end.identity();
		end.rotate(Math.toRadians(2.0));
		
		l = start.lerped(end, alpha);
		TestCase.assertEquals(-0.034, l.getRotation(), 1.0e-3);
	}
	
	/**
	 * Tests the getValues method.
	 * @since 3.0.1
	 */
	@Test
	public void values() {
		Transform t = new Transform();
		t.translate(2.0, -1.0);
		
		double[] values = t.getValues();
		TestCase.assertEquals(1.0, values[0]);
		TestCase.assertEquals(0.0, values[1]);
		TestCase.assertEquals(2.0, values[2]);
		TestCase.assertEquals(0.0, values[3]);
		TestCase.assertEquals(1.0, values[4]);
		TestCase.assertEquals(-1.0, values[5]);
	}
	
	/**
	 * Tests the identity's rotate method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identityRotate1() {
		Transform.IDENTITY.rotate(Math.toRadians(30));
	}
	
	/**
	 * Tests the identity's rotate method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identityRotate2() {
		Transform.IDENTITY.rotate(Math.toRadians(30), new Vector2());
	}

	/**
	 * Tests the identity's rotate method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identityRotate3() {
		Transform.IDENTITY.rotate(Math.toRadians(30), 2, 3);
	}
	
	/**
	 * Tests the identity's lerp method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identityLerp() {
		Transform.IDENTITY.lerp(new Transform(), 0.5);
	}
	
	/**
	 * Tests the identity's set method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identitySet() {
		Transform.IDENTITY.set(new Transform());
	}
	
	/**
	 * Tests the identity's setRotation method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identitySetRotation() {
		Transform.IDENTITY.setRotation(Math.toRadians(20));
	}

	/**
	 * Tests the identity's setTranslation method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identitySetTranslation1() {
		Transform.IDENTITY.setTranslation(new Vector2());
	}
	
	/**
	 * Tests the identity's setTranslation method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identitySetTranslation2() {
		Transform.IDENTITY.setTranslation(3, 2);
	}
	
	/**
	 * Tests the identity's setTranslationX method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identitySetTranslationX() {
		Transform.IDENTITY.setTranslationX(3);
	}
	
	/**
	 * Tests the identity's setTranslationX method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identitySetTranslationY() {
		Transform.IDENTITY.setTranslationY(3);
	}

	/**
	 * Tests the identity's translate method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identityTranslate1() {
		Transform.IDENTITY.translate(new Vector2());
	}
	
	/**
	 * Tests the identity's translate method to ensure no mutation.
	 * @since 3.0.1
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void identityTranslate2() {
		Transform.IDENTITY.translate(2, 3);
	}
}
