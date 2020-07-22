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
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link RevoluteJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class RevoluteJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new RevoluteJoint<Body>(b1, b2, new Vector2());
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
		TestCase.assertEquals(0.0, rj.getMaximumMotorTorque());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		rj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the max force
		rj.setMaximumMotorTorque(0.0);
		TestCase.assertEquals(0.0, rj.getMaximumMotorTorque());
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
		rj.setLimits(Math.toRadians(30), Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), rj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(30), rj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitEnabledSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is enabled
		TestCase.assertFalse(rj.isLimitEnabled());
		
		rj.setLimitEnabled(true);
		
		// lets disable it first and ensure that the bodies are awake
		rj.setLimitEnabled(false);
		TestCase.assertFalse(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		rj.setLimitEnabled(false);
		TestCase.assertFalse(rj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		rj.setLimitEnabled(true);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setLimitEnabled(true);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		rj.setLimitEnabled(false);
		TestCase.assertFalse(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is enabled
		TestCase.assertFalse(rj.isLimitEnabled());
		
		rj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimits(Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(-Math.PI);
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		rj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(-Math.PI, -Math.PI);
		TestCase.assertFalse(rj.isLimitEnabled());
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
		
		// by default the limit is enabled
		TestCase.assertFalse(rj.isLimitEnabled());
		
		rj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(rj.isLimitEnabled());
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
		TestCase.assertTrue(rj.isLimitEnabled());
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
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		rj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(Math.PI, 2*Math.PI);
		TestCase.assertFalse(rj.isLimitEnabled());
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
		
		// by default the limit is enabled
		TestCase.assertFalse(rj.isLimitEnabled());
		
		rj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		rj.setLowerLimit(-Math.PI);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		rj.setLimitEnabled(false);
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
		
		// by default the limit is enabled
		TestCase.assertFalse(rj.isLimitEnabled());
		
		rj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = rj.getLowerLimit();
		double defaultUpperLimit = rj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		rj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		rj.setUpperLimit(Math.PI);
		TestCase.assertTrue(rj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(Math.PI, rj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		rj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setUpperLimit(2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, rj.getUpperLimit());
	}
}
