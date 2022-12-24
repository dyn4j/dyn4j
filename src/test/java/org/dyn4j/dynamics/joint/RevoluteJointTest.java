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
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link RevoluteJoint} class.
 * @author William Bittle
 * @version 4.0.1
 * @since 1.0.2
 */
public class RevoluteJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 p = new Vector2(1.0, 2.0);
		
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, p);
		
		TestCase.assertEquals(p, rj.getAnchor1());
		TestCase.assertEquals(p, rj.getAnchor2());
		TestCase.assertNotSame(p, rj.getAnchor1());
		TestCase.assertNotSame(p, rj.getAnchor2());
		
		TestCase.assertEquals(0.0, rj.getAngularTranslation());
		TestCase.assertEquals(1000.0, rj.getMaximumMotorTorque());
		TestCase.assertEquals(0.0, rj.getMotorSpeed());
		
		TestCase.assertEquals(0.0, rj.getLimitsReferenceAngle());
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		TestCase.assertEquals(0.0, rj.getUpperLimit());
		
		TestCase.assertEquals(b1, rj.getBody1());
		TestCase.assertEquals(b2, rj.getBody2());
		
		TestCase.assertEquals(null, rj.getOwner());
		TestCase.assertEquals(null, rj.getUserData());
		TestCase.assertEquals(b2, rj.getOtherBody(b1));
		
		TestCase.assertEquals(false, rj.isCollisionAllowed());
		TestCase.assertEquals(false, rj.isLimitsEnabled());
		TestCase.assertEquals(false, rj.isMotorEnabled());
		TestCase.assertEquals(false, rj.isMaximumMotorTorqueEnabled());
		
		TestCase.assertNotNull(rj.toString());
	}
	
	/**
	 * Tests the create with a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new RevoluteJoint<Body>(null, b2, new Vector2());
	}
	
	/**
	 * Tests the create with a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new RevoluteJoint<Body>(b1, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new RevoluteJoint<Body>(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new RevoluteJoint<Body>(b1, b1, new Vector2());
	}
	
	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaximumMotorTorque() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		rj.setMaximumMotorTorque(0.0);
		TestCase.assertEquals(0.0, rj.getMaximumMotorTorque());
		
		rj.setMaximumMotorTorque(10.0);
		TestCase.assertEquals(10.0, rj.getMaximumMotorTorque());
		
		rj.setMaximumMotorTorque(2548.0);
		TestCase.assertEquals(2548.0, rj.getMaximumMotorTorque());
	}

	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumMotorTorqueNegative() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setMaximumMotorTorque(-2.0);
	}

	/**
	 * Tests the setting the maximum motor torque wrt. sleeping.
	 */
	@Test
	public void setMaximumMotorTorqueSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertFalse(rj.isMotorEnabled());
		TestCase.assertEquals(1000.0, rj.getMaximumMotorTorque());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		rj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the max torque
		rj.setMaximumMotorTorque(1000.0);
		TestCase.assertEquals(1000.0, rj.getMaximumMotorTorque());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the max force
		rj.setMaximumMotorTorque(2.0);
		TestCase.assertEquals(2.0, rj.getMaximumMotorTorque());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		rj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setMaximumMotorTorque(1.0);
		TestCase.assertEquals(1.0, rj.getMaximumMotorTorque());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}

	/**
	 * Tests the setting the maximum motor torque wrt. sleeping.
	 */
	@Test
	public void setMaximumMotorTorqueEnabledSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertFalse(rj.isMotorEnabled());
		TestCase.assertEquals(1000.0, rj.getMaximumMotorTorque());
		TestCase.assertFalse(rj.isMaximumMotorTorqueEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		rj.setMotorEnabled(true);
		rj.setMaximumMotorTorqueEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the flag
		rj.setMaximumMotorTorqueEnabled(true);
		TestCase.assertTrue(rj.isMaximumMotorTorqueEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// disable it
		rj.setMaximumMotorTorqueEnabled(false);
		TestCase.assertFalse(rj.isMaximumMotorTorqueEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		rj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setMaximumMotorTorqueEnabled(true);
		TestCase.assertTrue(rj.isMaximumMotorTorqueEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}

	/**
	 * Tests the enabling of the motor.
	 */
	@Test
	public void setMotorEnabled() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertFalse(rj.isMotorEnabled());
		
		rj.setMotorEnabled(true);
		TestCase.assertTrue(rj.isMotorEnabled());
		
		rj.setMotorEnabled(false);
		TestCase.assertFalse(rj.isMotorEnabled());
	}
	
	/**
	 * Tests the enabling of the motor wrt sleeping.
	 */
	@Test
	public void setMotorEnabledSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertFalse(rj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// disable the motor
		rj.setMotorEnabled(false);
		TestCase.assertFalse(rj.isMotorEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// enable the motor
		rj.setMotorEnabled(true);
		TestCase.assertTrue(rj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// set the motor to enabled again
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setMotorEnabled(true);
		TestCase.assertTrue(rj.isMotorEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());		
		
		rj.setMotorEnabled(false);
		TestCase.assertFalse(rj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}

	/**
	 * Tests the setting the motor speed.
	 */
	@Test
	public void setMotorSpeed() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertEquals(0.0, rj.getMotorSpeed());
		
		rj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, rj.getMotorSpeed());
		
		rj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, rj.getMotorSpeed());
		
		rj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, rj.getMotorSpeed());
	}
	
	/**
	 * Tests the setting the motor speed wrt. sleeping.
	 */
	@Test
	public void setMotorSpeedSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertFalse(rj.isMotorEnabled());
		TestCase.assertEquals(0.0, rj.getMotorSpeed());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		rj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the speed
		rj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, rj.getMotorSpeed());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the speed
		rj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, rj.getMotorSpeed());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		rj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, rj.getMotorSpeed());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}
	
	/**
	 * Tests the successful setting of the maximum angle.
	 */
	@Test
	public void setUpperLimitSuccess() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setUpperLimit(Math.toRadians(10));
		
		TestCase.assertEquals(Math.toRadians(10), rj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setUpperLimit(Math.toRadians(-10));
	}
	
	/**
	 * Tests the successful setting of the minimum angle.
	 */
	@Test
	public void setLowerLimit() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setLowerLimit(Math.toRadians(-10));
		
		TestCase.assertEquals(Math.toRadians(-10), rj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setLowerLimit(Math.toRadians(10));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setLimits(Math.toRadians(-30), Math.toRadians(20));
		
		TestCase.assertEquals(Math.toRadians(-30), rj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(20), rj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setLimits(Math.toRadians(30), Math.toRadians(20));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimitsToSameValue() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		rj.setLimits(Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), rj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(30), rj.getUpperLimit(), 1e-6);
	}

	/**
	 * Tests the get/set for the reference angle.
	 */
	@Test
	public void setLimitsReferenceAngle() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertEquals(0.0, rj.getLimitsReferenceAngle());
	
		rj.setLimitsReferenceAngle(Math.toRadians(30.0));
		
		TestCase.assertEquals(Math.toRadians(30.0), rj.getLimitsReferenceAngle());
	}

	/**
	 * Tests setting the reference angle.
	 */
	@Test
	public void setLimitsReferenceAngleSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we set it to the same value, the bodies should not wake
		rj.setLimitsReferenceAngle(0.0);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		rj.setLimitsReferenceAngle(1.0);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitEnabledSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		rj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setLimits(defaultLowerLimit);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimits(Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(-Math.PI);
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(-Math.PI);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(-Math.PI, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		rj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(-2*Math.PI);
		TestCase.assertEquals(-2*Math.PI, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(Math.PI, 2*Math.PI);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		rj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		rj.setLowerLimit(-Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLowerLimit(-2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-2*Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		rj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		rj.setUpperLimit(Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setUpperLimit(2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		rj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		rj.setLimitsEnabled(defaultLowerLimit);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		rj.setLimitsEnabled(Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setLimitsEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		rj.setLimitsEnabled(Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is enabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		rj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		rj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(-2*Math.PI);
		TestCase.assertEquals(-2*Math.PI, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitsEnabledSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(rj.isLimitsEnabled());
		
		rj.setLimitsEnabled(true);
		
		// lets disable it first and ensure that the bodies are awake
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(-3.0, rj.getAnchor1().x);
		TestCase.assertEquals(0.5, rj.getAnchor1().y);
		TestCase.assertEquals(-3.0, rj.getAnchor2().x);
		TestCase.assertEquals(0.5, rj.getAnchor2().y);
		
		rj.shift(new Vector2(1.0, 3.0));
		
		// nothing should have changed
		TestCase.assertEquals(-3.0, rj.getAnchor1().x);
		TestCase.assertEquals(0.5, rj.getAnchor1().y);
		TestCase.assertEquals(-3.0, rj.getAnchor2().x);
		TestCase.assertEquals(0.5, rj.getAnchor2().y);
	}
	
}
