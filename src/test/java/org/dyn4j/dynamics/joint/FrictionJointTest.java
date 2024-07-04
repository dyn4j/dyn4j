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
 * Used to test the {@link FrictionJoint} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.2
 */
public class FrictionJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		Vector2 p = new Vector2(1.0, 2.0);
		
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, p);
		
		TestCase.assertEquals(p, fj.getAnchor1());
		TestCase.assertEquals(p, fj.getAnchor2());
		TestCase.assertNotSame(p, fj.getAnchor1());
		TestCase.assertNotSame(p, fj.getAnchor2());
		
		TestCase.assertEquals(10.0, fj.getMaximumForce());
		TestCase.assertEquals(0.25, fj.getMaximumTorque());
		
		TestCase.assertEquals(b1, fj.getBody1());
		TestCase.assertEquals(b2, fj.getBody2());
		
		TestCase.assertEquals(null, fj.getOwner());
		TestCase.assertEquals(null, fj.getUserData());
		TestCase.assertEquals(b2, fj.getOtherBody(b1));
		
		TestCase.assertEquals(false, fj.isCollisionAllowed());
		
		TestCase.assertNotNull(fj.toString());
	}
	
	/**
	 * Tests the failed creation with a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new FrictionJoint<Body>(null, b2, new Vector2());
	}

	/**
	 * Tests the failed creation with a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new FrictionJoint<Body>(b1, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new FrictionJoint<Body>(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new FrictionJoint<Body>(b1, b1, new Vector2());
	}
	
	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaximumTorque() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		
		fj.setMaximumTorque(0.0);
		TestCase.assertEquals(0.0, fj.getMaximumTorque());
		
		fj.setMaximumTorque(10.0);
		TestCase.assertEquals(10.0, fj.getMaximumTorque());
		
		fj.setMaximumTorque(2548.0);
		TestCase.assertEquals(2548.0, fj.getMaximumTorque());
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumTorque() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		fj.setMaximumTorque(-2.0);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaximumForce() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		
		fj.setMaximumForce(0.0);
		TestCase.assertEquals(0.0, fj.getMaximumForce());
		
		fj.setMaximumForce(10.0);
		TestCase.assertEquals(10.0, fj.getMaximumForce());
		
		fj.setMaximumForce(2548.0);
		TestCase.assertEquals(2548.0, fj.getMaximumForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumForce() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		fj.setMaximumForce(-2.0);
	}

	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2(1.0, 2.0));
		
		TestCase.assertEquals(1.0, fj.getAnchor1().x);
		TestCase.assertEquals(2.0, fj.getAnchor1().y);
		TestCase.assertEquals(1.0, fj.getAnchor2().x);
		TestCase.assertEquals(2.0, fj.getAnchor2().y);
		
		fj.shift(new Vector2(1.0, 3.0));
		
		// nothing should have changed
		TestCase.assertEquals(1.0, fj.getAnchor1().x);
		TestCase.assertEquals(2.0, fj.getAnchor1().y);
		TestCase.assertEquals(1.0, fj.getAnchor2().x);
		TestCase.assertEquals(2.0, fj.getAnchor2().y);
	}

	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2(1, 1));
		fj.setCollisionAllowed(true);
		fj.setOwner(new Object());
		fj.setUserData(new Object());
		fj.setMaximumForce(5);
		fj.setMaximumTorque(9);
		fj.angularImpulse = 7;
		fj.angularMass = 4;
		fj.K.m00 = 1;
		fj.K.m01 = 2;
		fj.K.m10 = 3;
		fj.K.m11 = 4;
		fj.linearImpulse.set(2, 3);
		
		FrictionJoint<Body> fjc = fj.copy();
		
		TestCase.assertNotSame(fj, fjc);
		TestCase.assertNotSame(fj.bodies, fjc.bodies);
		TestCase.assertNotSame(fj.body1, fjc.body1);
		TestCase.assertNotSame(fj.body2, fjc.body2);
		TestCase.assertNotSame(fj.localAnchor1, fjc.localAnchor1);
		TestCase.assertNotSame(fj.localAnchor2, fjc.localAnchor2);
		TestCase.assertNotSame(fj.K, fjc.K);
		TestCase.assertSame(fjc.body1, fjc.bodies.get(0));
		TestCase.assertSame(fjc.body2, fjc.bodies.get(1));
		TestCase.assertEquals(fj.bodies.size(), fjc.bodies.size());
		TestCase.assertEquals(fj.localAnchor1.x, fjc.localAnchor1.x);
		TestCase.assertEquals(fj.localAnchor1.y, fjc.localAnchor1.y);
		TestCase.assertEquals(fj.localAnchor2.x, fjc.localAnchor2.x);
		TestCase.assertEquals(fj.localAnchor2.y, fjc.localAnchor2.y);
		TestCase.assertEquals(fj.K.m00, fjc.K.m00);
		TestCase.assertEquals(fj.K.m01, fjc.K.m01);
		TestCase.assertEquals(fj.K.m10, fjc.K.m10);
		TestCase.assertEquals(fj.K.m11, fjc.K.m11);
		TestCase.assertEquals(fj.linearImpulse.x, fjc.linearImpulse.x);
		TestCase.assertEquals(fj.linearImpulse.y, fjc.linearImpulse.y);
		
		TestCase.assertNull(fjc.owner);
		TestCase.assertNull(fjc.userData);
		
		TestCase.assertEquals(fj.angularImpulse, fjc.angularImpulse);
		TestCase.assertEquals(fj.collisionAllowed, fjc.collisionAllowed);
		TestCase.assertEquals(fj.angularMass, fjc.angularMass);
		TestCase.assertEquals(fj.maximumForce, fjc.maximumForce);
		TestCase.assertEquals(fj.maximumTorque, fjc.maximumTorque);
		
		// test overriding the bodies
		fjc = fj.copy(b1, b2);
		
		TestCase.assertNotSame(fj, fjc);
		TestCase.assertNotSame(fj.bodies, fjc.bodies);
		TestCase.assertSame(fj.body1, fjc.body1);
		TestCase.assertSame(fj.body2, fjc.body2);
		TestCase.assertSame(fjc.body1, fjc.bodies.get(0));
		TestCase.assertSame(fjc.body2, fjc.bodies.get(1));
		TestCase.assertEquals(fj.bodies.size(), fjc.bodies.size());
		
		// test overriding body1
		fjc = fj.copy(b1, null);
		
		TestCase.assertNotSame(fj, fjc);
		TestCase.assertNotSame(fj.bodies, fjc.bodies);
		TestCase.assertSame(fj.body1, fjc.body1);
		TestCase.assertNotSame(fj.body2, fjc.body2);
		TestCase.assertSame(fjc.body1, fjc.bodies.get(0));
		TestCase.assertSame(fjc.body2, fjc.bodies.get(1));
		TestCase.assertEquals(fj.bodies.size(), fjc.bodies.size());

		// test overriding body2
		fjc = fj.copy(null, b2);
		
		TestCase.assertNotSame(fj, fjc);
		TestCase.assertNotSame(fj.bodies, fjc.bodies);
		TestCase.assertNotSame(fj.body1, fjc.body1);
		TestCase.assertSame(fj.body2, fjc.body2);
		TestCase.assertSame(fjc.body1, fjc.bodies.get(0));
		TestCase.assertSame(fjc.body2, fjc.bodies.get(1));
		TestCase.assertEquals(fj.bodies.size(), fjc.bodies.size());
	}
	
	/**
	 * Test the copy fail fast.
	 */
	@Test(expected = ClassCastException.class)
	public void copyFailed() {
		TestBody b1 = new TestBody();
		TestBody b2 = new TestBody();
		
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2(1, 1));
		
		fj.copy();
	}
}
