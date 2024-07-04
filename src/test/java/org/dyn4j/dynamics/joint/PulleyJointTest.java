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
package org.dyn4j.dynamics.joint;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link PulleyJoint} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 2.1.0
 */
public class PulleyJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 pa1 = new Vector2(-1.0, 1.0);
		Vector2 pa2 = new Vector2(1.0, 1.0);
		Vector2 a1 = new Vector2(-1.0, 0.0);
		Vector2 a2 = new Vector2(1.0, 0.0);
		
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, pa1, pa2, a1, a2);
		
		TestCase.assertEquals(pa1, pj.getPulleyAnchor1());
		TestCase.assertEquals(pa2, pj.getPulleyAnchor2());
		TestCase.assertNotSame(pa1, pj.getPulleyAnchor1());
		TestCase.assertNotSame(pa2, pj.getPulleyAnchor2());

		TestCase.assertEquals(a1, pj.getAnchor1());
		TestCase.assertEquals(a2, pj.getAnchor2());
		TestCase.assertNotSame(a1, pj.getAnchor1());
		TestCase.assertNotSame(a2, pj.getAnchor2());
		
		TestCase.assertEquals(pa1.distance(a1) + pa2.distance(a2), pj.getLength());
		TestCase.assertEquals(pa1.distance(a1), pj.getCurrentLength1());
		TestCase.assertEquals(pa2.distance(a2), pj.getCurrentLength2());
		
		TestCase.assertEquals(1.0, pj.getRatio());
		
		TestCase.assertEquals(b1, pj.getBody1());
		TestCase.assertEquals(b2, pj.getBody2());
		
		TestCase.assertEquals(null, pj.getOwner());
		TestCase.assertEquals(null, pj.getUserData());
		TestCase.assertEquals(b2, pj.getOtherBody(b1));
		
		TestCase.assertEquals(false, pj.isCollisionAllowed());
		TestCase.assertEquals(false, pj.isSlackEnabled());
		
		TestCase.assertNotNull(pj.toString());
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

	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(1.0, 0.0), new Vector2(-1.0, 0.0), new Vector2(1.0, -1.0), new Vector2(-1.0, -1.0));
		pj.setCollisionAllowed(true);
		pj.setLength(5);
		pj.setRatio(0.4);
		pj.setSlackEnabled(true);
		pj.setOwner(new Object());
		pj.setUserData(new Object());
		pj.impulse = 9;
		pj.invK = 1;
		pj.n1.set(1, 2);
		pj.n2.set(4, 5);
		pj.overLength = true;
		
		PulleyJoint<Body> pjc = pj.copy();
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertNotSame(pj.body1, pjc.body1);
		TestCase.assertNotSame(pj.body2, pjc.body2);
		TestCase.assertNotSame(pj.localAnchor1, pjc.localAnchor1);
		TestCase.assertNotSame(pj.localAnchor2, pjc.localAnchor2);
		TestCase.assertNotSame(pj.n1, pjc.n1);
		TestCase.assertNotSame(pj.n2, pjc.n2);
		TestCase.assertSame(pjc.body1, pjc.bodies.get(0));
		TestCase.assertSame(pjc.body2, pjc.bodies.get(1));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());
		TestCase.assertEquals(pj.pulleyAnchor1.x, pjc.pulleyAnchor1.x);
		TestCase.assertEquals(pj.pulleyAnchor1.y, pjc.pulleyAnchor1.y);
		TestCase.assertEquals(pj.pulleyAnchor2.x, pjc.pulleyAnchor2.x);
		TestCase.assertEquals(pj.pulleyAnchor2.y, pjc.pulleyAnchor2.y);
		TestCase.assertEquals(pj.localAnchor1.x, pjc.localAnchor1.x);
		TestCase.assertEquals(pj.localAnchor1.y, pjc.localAnchor1.y);
		TestCase.assertEquals(pj.localAnchor2.x, pjc.localAnchor2.x);
		TestCase.assertEquals(pj.localAnchor2.y, pjc.localAnchor2.y);
		TestCase.assertEquals(pj.n1.x, pjc.n1.x);
		TestCase.assertEquals(pj.n1.y, pjc.n1.y);
		TestCase.assertEquals(pj.n2.x, pjc.n2.x);
		TestCase.assertEquals(pj.n2.y, pjc.n2.y);
		
		TestCase.assertNull(pjc.owner);
		TestCase.assertNull(pjc.userData);
		
		TestCase.assertEquals(pj.collisionAllowed, pjc.collisionAllowed);
		TestCase.assertEquals(pj.impulse, pjc.impulse);
		TestCase.assertEquals(pj.invK, pjc.invK);
		TestCase.assertEquals(pj.length, pjc.length);
		TestCase.assertEquals(pj.overLength, pjc.overLength);
		TestCase.assertEquals(pj.ratio, pjc.ratio);
		TestCase.assertEquals(pj.slackEnabled, pjc.slackEnabled);
		
		// test overriding the bodies
		pjc = pj.copy(b1, b2);
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertSame(pj.body1, pjc.body1);
		TestCase.assertSame(pj.body2, pjc.body2);
		TestCase.assertSame(pjc.body1, pjc.bodies.get(0));
		TestCase.assertSame(pjc.body2, pjc.bodies.get(1));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());
		
		// test overriding body1
		pjc = pj.copy(b1, null);
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertSame(pj.body1, pjc.body1);
		TestCase.assertNotSame(pj.body2, pjc.body2);
		TestCase.assertSame(pjc.body1, pjc.bodies.get(0));
		TestCase.assertSame(pjc.body2, pjc.bodies.get(1));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());

		// test overriding body2
		pjc = pj.copy(null, b2);
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertNotSame(pj.body1, pjc.body1);
		TestCase.assertSame(pj.body2, pjc.body2);
		TestCase.assertSame(pjc.body1, pjc.bodies.get(0));
		TestCase.assertSame(pjc.body2, pjc.bodies.get(1));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());
	}
	
	/**
	 * Test the copy fail fast.
	 */
	@Test(expected = ClassCastException.class)
	public void copyFailed() {
		TestBody b1 = new TestBody();
		TestBody b2 = new TestBody();
		
		PulleyJoint<Body> pj = new PulleyJoint<Body>(b1, b2, new Vector2(1.0, 0.0), new Vector2(-1.0, 1.0), new Vector2(), new Vector2());
		
		pj.copy();
	}
}
