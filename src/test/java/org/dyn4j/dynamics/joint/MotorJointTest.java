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
 * Used to test the {@link MotorJoint} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 */
public class MotorJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);

		TestCase.assertEquals(0.0, mj.getAngularTarget());
		TestCase.assertEquals(b2.getWorldCenter(), mj.getLinearTarget());
		
		TestCase.assertEquals(0.3, mj.getCorrectionFactor());
		
		TestCase.assertEquals(1000.0, mj.getMaximumForce());
		TestCase.assertEquals(1000.0, mj.getMaximumTorque());
		
		TestCase.assertEquals(b1, mj.getBody1());
		TestCase.assertEquals(b2, mj.getBody2());
		
		TestCase.assertEquals(null, mj.getOwner());
		TestCase.assertEquals(null, mj.getUserData());
		TestCase.assertEquals(b2, mj.getOtherBody(b1));
		
		TestCase.assertEquals(false, mj.isCollisionAllowed());
		
		TestCase.assertNotNull(mj.toString());
	}
	
	/**
	 * Tests the create method passing a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new MotorJoint<Body>(b1, null);
	}
	
	/**
	 * Tests the create method passing a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new MotorJoint<Body>(null, b2);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new MotorJoint<Body>(b1, b1);
	}
	
	/**
	 * Tests valid correction factor values.
	 */
	@Test
	public void setValidCorrectionFactor() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		
		mj.setCorrectionFactor(0.0);
		TestCase.assertEquals(0.0, mj.getCorrectionFactor());
		
		mj.setCorrectionFactor(0.5);
		TestCase.assertEquals(0.5, mj.getCorrectionFactor());
		
		mj.setCorrectionFactor(1.0);
		TestCase.assertEquals(1.0, mj.getCorrectionFactor());
	}
	
	/**
	 * Tests a negative correction factor value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeCorrectionFactor() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		mj.setCorrectionFactor(-1.0);
	}

	/**
	 * Tests a correction factor value greater than 1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setCorrectionFactorGreaterThan1() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		mj.setCorrectionFactor(1.1);
	}

	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaximumTorque() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		
		mj.setMaximumTorque(0.0);
		TestCase.assertEquals(0.0, mj.getMaximumTorque());
		
		mj.setMaximumTorque(10.0);
		TestCase.assertEquals(10.0, mj.getMaximumTorque());
		
		mj.setMaximumTorque(2548.0);
		TestCase.assertEquals(2548.0, mj.getMaximumTorque());
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumTorque() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		mj.setMaximumTorque(-2.0);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaximumForce() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		
		mj.setMaximumForce(0.0);
		TestCase.assertEquals(0.0, mj.getMaximumForce());
		
		mj.setMaximumForce(10.0);
		TestCase.assertEquals(10.0, mj.getMaximumForce());
		
		mj.setMaximumForce(2548.0);
		TestCase.assertEquals(2548.0, mj.getMaximumForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumForce() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		mj.setMaximumForce(-2.0);
	}
	
	/**
	 * Tests the body's sleep state when changing the linear target.
	 */
	@Test
	public void setLinearTargetSleep() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		
		Vector2 defaultLinearTarget = mj.getLinearTarget();
		
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertTrue(defaultLinearTarget.equals(mj.getLinearTarget()));
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the target to the same value
		mj.setLinearTarget(defaultLinearTarget);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertTrue(defaultLinearTarget.equals(mj.getLinearTarget()));
		
		// set the target to a different value and make
		// sure the bodies are awakened
		Vector2 target = new Vector2(1.0, 1.0);
		mj.setLinearTarget(target);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertTrue(target.equals(mj.getLinearTarget()));
		
		// set the target to the same value
		b1.setAtRest(true);
		b2.setAtRest(true);
		mj.setLinearTarget(target);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertTrue(target.equals(mj.getLinearTarget()));
	}

	/**
	 * Tests the body's sleep state when changing the angular target.
	 */
	@Test
	public void setAngularTargetSleep() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		
		double defaultAngularTarget = mj.getAngularTarget();
		
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultAngularTarget, mj.getAngularTarget());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the target to the same value
		mj.setAngularTarget(defaultAngularTarget);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultAngularTarget, mj.getAngularTarget());
		
		// set the target to a different value and make
		// sure the bodies are awakened
		mj.setAngularTarget(1.0);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(1.0, mj.getAngularTarget());
		
		// set the target to the same value
		b1.setAtRest(true);
		b2.setAtRest(true);
		mj.setAngularTarget(1.0);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, mj.getAngularTarget());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		mj.setCollisionAllowed(true);
		mj.setOwner(new Object());
		mj.setUserData(new Object());
		mj.setMaximumForce(5);
		mj.setMaximumTorque(9);
		mj.setAngularTarget(Math.toRadians(40));
		mj.setCorrectionFactor(0.1);
		mj.setLinearTarget(3, 4);
		mj.angularError = 1;
		mj.angularImpulse = 7;
		mj.angularMass = 4;
		mj.K.m00 = 1;
		mj.K.m01 = 2;
		mj.K.m10 = 3;
		mj.K.m11 = 4;
		mj.linearError.set(2, 1);
		mj.linearImpulse.set(2, 3);
		
		MotorJoint<Body> mjc = mj.copy();
		
		TestCase.assertNotSame(mj, mjc);
		TestCase.assertNotSame(mj.bodies, mjc.bodies);
		TestCase.assertNotSame(mj.body1, mjc.body1);
		TestCase.assertNotSame(mj.body2, mjc.body2);
		TestCase.assertNotSame(mj.K, mjc.K);
		TestCase.assertNotSame(mj.linearError, mjc.linearError);
		TestCase.assertNotSame(mj.linearImpulse, mjc.linearImpulse);
		TestCase.assertNotSame(mj.linearTarget, mjc.linearTarget);
		TestCase.assertNotSame(mj.r1, mjc.r1);
		TestCase.assertNotSame(mj.r2, mjc.r2);
		TestCase.assertSame(mjc.body1, mjc.bodies.get(0));
		TestCase.assertSame(mjc.body2, mjc.bodies.get(1));
		TestCase.assertEquals(mj.bodies.size(), mjc.bodies.size());
		TestCase.assertEquals(mj.linearError.x, mjc.linearError.x);
		TestCase.assertEquals(mj.linearError.y, mjc.linearError.y);
		TestCase.assertEquals(mj.linearImpulse.x, mjc.linearImpulse.x);
		TestCase.assertEquals(mj.linearImpulse.y, mjc.linearImpulse.y);
		TestCase.assertEquals(mj.linearTarget.y, mjc.linearTarget.y);
		TestCase.assertEquals(mj.linearTarget.y, mjc.linearTarget.y);
		TestCase.assertEquals(mj.K.m00, mjc.K.m00);
		TestCase.assertEquals(mj.K.m01, mjc.K.m01);
		TestCase.assertEquals(mj.K.m10, mjc.K.m10);
		TestCase.assertEquals(mj.K.m11, mjc.K.m11);
		
		TestCase.assertNull(mjc.owner);
		TestCase.assertNull(mjc.userData);
		
		TestCase.assertEquals(mj.angularError, mjc.angularError);
		TestCase.assertEquals(mj.angularImpulse, mjc.angularImpulse);
		TestCase.assertEquals(mj.angularMass, mjc.angularMass);
		TestCase.assertEquals(mj.angularTarget, mjc.angularTarget);
		TestCase.assertEquals(mj.collisionAllowed, mjc.collisionAllowed);
		TestCase.assertEquals(mj.correctionFactor, mjc.correctionFactor);
		TestCase.assertEquals(mj.maximumForce, mjc.maximumForce);
		TestCase.assertEquals(mj.maximumTorque, mjc.maximumTorque);
		
		// test overriding the bodies
		mjc = mj.copy(b1, b2);
		
		TestCase.assertNotSame(mj, mjc);
		TestCase.assertNotSame(mj.bodies, mjc.bodies);
		TestCase.assertSame(mj.body1, mjc.body1);
		TestCase.assertSame(mj.body2, mjc.body2);
		TestCase.assertSame(mjc.body1, mjc.bodies.get(0));
		TestCase.assertSame(mjc.body2, mjc.bodies.get(1));
		TestCase.assertEquals(mj.bodies.size(), mjc.bodies.size());
		
		// test overriding body1
		mjc = mj.copy(b1, null);
		
		TestCase.assertNotSame(mj, mjc);
		TestCase.assertNotSame(mj.bodies, mjc.bodies);
		TestCase.assertSame(mj.body1, mjc.body1);
		TestCase.assertNotSame(mj.body2, mjc.body2);
		TestCase.assertSame(mjc.body1, mjc.bodies.get(0));
		TestCase.assertSame(mjc.body2, mjc.bodies.get(1));
		TestCase.assertEquals(mj.bodies.size(), mjc.bodies.size());

		// test overriding body2
		mjc = mj.copy(null, b2);
		
		TestCase.assertNotSame(mj, mjc);
		TestCase.assertNotSame(mj.bodies, mjc.bodies);
		TestCase.assertNotSame(mj.body1, mjc.body1);
		TestCase.assertSame(mj.body2, mjc.body2);
		TestCase.assertSame(mjc.body1, mjc.bodies.get(0));
		TestCase.assertSame(mjc.body2, mjc.bodies.get(1));
		TestCase.assertEquals(mj.bodies.size(), mjc.bodies.size());
	}
	
	/**
	 * Test the copy fail fast.
	 */
	@Test(expected = ClassCastException.class)
	public void copyFailed() {
		TestBody b1 = new TestBody();
		TestBody b2 = new TestBody();
		
		MotorJoint<Body> mj = new MotorJoint<Body>(b1, b2);
		
		mj.copy();
	}
}
