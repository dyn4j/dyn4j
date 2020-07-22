/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.joint;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link PulleyJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 2.1.0
 */
public class PulleyJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}

	/**
	 * Tests the create method passing a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new PulleyJoint<Body>(null, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}

	/**
	 * Tests the create method passing a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new PulleyJoint<Body>(b1, null, new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor1() {
		new PulleyJoint<Body>(b1, b2, null, new Vector2(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor2() {
		new PulleyJoint<Body>(b1, b2, new Vector2(), null, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor3() {
		new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor4() {
		new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new PulleyJoint<Body>(b1, b1, new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the setRatio method.
	 */
	@Test
	public void setRatio() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		pj.setRatio(2.0);
		TestCase.assertEquals(2.0, pj.getRatio());
	}
	
	/**
	 * Tests the setRatio method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setRatioNegative() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		pj.setRatio(-1.0);
	}
	
	/**
	 * Tests the setRatio method passing a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setRatioZero() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		pj.setRatio(0.0);
	}
	
	/**
	 * Tests the setRatio method wrt. sleeping.
	 */
	@Test
	public void setRatioSleep() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		double ratio = pj.getRatio();
		TestCase.assertEquals(1.0, ratio);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setRatio(ratio);
		TestCase.assertEquals(ratio, pj.getRatio());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		pj.setRatio(2.0);
		TestCase.assertEquals(2.0, pj.getRatio());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the setSlackEnabled method.
	 */
	@Test
	public void setSlackEnabled() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		TestCase.assertFalse(pj.isSlackEnabled());
		
		pj.setSlackEnabled(true);
		TestCase.assertTrue(pj.isSlackEnabled());
		
		pj.setSlackEnabled(false);
		TestCase.assertFalse(pj.isSlackEnabled());
	}
	
	/**
	 * Tests the setLength method.
	 */
	@Test
	public void setLength() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		pj.setLength(2.0);
		TestCase.assertEquals(2.0, pj.getLength());
	}
	
	/**
	 * Tests the setLength method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLengthNegative() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		pj.setLength(-1.0);
	}
	
	/**
	 * Tests the setRatio method wrt. sleeping.
	 */
	@Test
	public void setLengthSleep() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		
		double length = pj.getLength();
		TestCase.assertEquals(0.0, length);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLength(length);
		TestCase.assertEquals(length, pj.getLength());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		pj.setLength(2.0);
		TestCase.assertEquals(2.0, pj.getLength());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the shiftCoordinates method.
	 * @since 3.1.0
	 */
	@Test
	public void shiftCoordinates() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(1.0, 0.0), new Vector2(-1.0, 1.0), new Vector2(), new Vector2());
		
		pj.shift(new Vector2(-1.0, 2.0));
		
		TestCase.assertEquals( 0.0, pj.getPulleyAnchor1().x, 1.0e-3);
		TestCase.assertEquals( 2.0, pj.getPulleyAnchor1().y, 1.0e-3);
		TestCase.assertEquals(-2.0, pj.getPulleyAnchor2().x, 1.0e-3);
		TestCase.assertEquals( 3.0, pj.getPulleyAnchor2().y, 1.0e-3);
	}
}
