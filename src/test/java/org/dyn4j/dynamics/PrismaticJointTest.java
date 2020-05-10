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
package org.dyn4j.dynamics;

import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link PrismaticJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class PrismaticJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
	}
	
	/**
	 * Tests the create method passing a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new PrismaticJoint(null, b2, new Vector2(), new Vector2(0.0, 1.0));
	}
	
	/**
	 * Tests the create method passing a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new PrismaticJoint(b1, null, new Vector2(), new Vector2(0.0, 1.0));
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new PrismaticJoint(b1, b2, null, new Vector2(0.0, 1.0));
	}

	/**
	 * Tests the create method passing a null axis.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAxis() {
		new PrismaticJoint(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new PrismaticJoint(b1, b1, new Vector2(), new Vector2(0.0, 1.0));
	}

	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaximumMotorForce() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setMaximumMotorForce(0.0);
		TestCase.assertEquals(0.0, pj.getMaximumMotorForce());
		
		pj.setMaximumMotorForce(10.0);
		TestCase.assertEquals(10.0, pj.getMaximumMotorForce());
		
		pj.setMaximumMotorForce(2548.0);
		TestCase.assertEquals(2548.0, pj.getMaximumMotorForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumMotorForce() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setMaximumMotorForce(-2.0);
	}

	/**
	 * Tests the setting the maximum motor force wrt. sleeping.
	 */
	@Test
	public void setMaximumMotorForceSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertEquals(0.0, pj.getMaximumMotorForce());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		pj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// don't change the max force
		pj.setMaximumMotorForce(0.0);
		TestCase.assertEquals(0.0, pj.getMaximumMotorForce());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		
		// change the max force
		pj.setMaximumMotorForce(2.0);
		TestCase.assertEquals(2.0, pj.getMaximumMotorForce());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		pj.setMotorEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setMaximumMotorForce(1.0);
		TestCase.assertEquals(1.0, pj.getMaximumMotorForce());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
	}

	/**
	 * Tests the enabling of the motor.
	 */
	@Test
	public void setMotorEnabled() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		
		pj.setMotorEnabled(true);
		TestCase.assertTrue(pj.isMotorEnabled());
		
		pj.setMotorEnabled(false);
		TestCase.assertFalse(pj.isMotorEnabled());
	}
	
	/**
	 * Tests the enabling of the motor wrt sleeping.
	 */
	@Test
	public void setMotorEnabledSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// disable the motor
		pj.setMotorEnabled(false);
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		
		// enable the motor
		pj.setMotorEnabled(true);
		TestCase.assertTrue(pj.isMotorEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		// set the motor to enabled again
		b1.setAsleep(true);
		b2.setAsleep(true);
		pj.setMotorEnabled(true);
		TestCase.assertTrue(pj.isMotorEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());		
		
		pj.setMotorEnabled(false);
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
	}

	/**
	 * Tests the setting the motor speed.
	 */
	@Test
	public void setMotorSpeed() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
		
		pj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, pj.getMotorSpeed());
		
		pj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, pj.getMotorSpeed());
		
		pj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
	}
	
	/**
	 * Tests the setting the motor speed wrt. sleeping.
	 */
	@Test
	public void setMotorSpeedSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		pj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// don't change the speed
		pj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		
		// change the speed
		pj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, pj.getMotorSpeed());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		pj.setMotorEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, pj.getMotorSpeed());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
	}
	
	/**
	 * Tests the successful setting of the upper limit.
	 */
	@Test
	public void setUpperLimitSuccess() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setUpperLimit(1.0);
		
		TestCase.assertEquals(1.0, pj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the upper limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setUpperLimit(-1.0);
	}
	
	/**
	 * Tests the successful setting of the lower limit.
	 */
	@Test
	public void setLowerLimit() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		
		pj.setLowerLimit(-1.0);
		
		TestCase.assertEquals(-1.0, pj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the lower limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		
		pj.setLowerLimit(1.0);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setLimits(0.0, 1.0);		
		TestCase.assertEquals(0.0, pj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(1.0, pj.getUpperLimit(), 1e-6);
		
		pj.setLimits(-2.0, -1.0);		
		TestCase.assertEquals(-2.0, pj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(-1.0, pj.getUpperLimit(), 1e-6);
		
		pj.setLimits(1.0, 1.0);
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setLimits(1.0, 0.0);
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitEnabledSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// lets disable it first and ensure that the bodies are awake
		pj.setLimitEnabled(false);
		TestCase.assertFalse(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// if we disable it again, the bodies should not wake
		pj.setLimitEnabled(false);
		TestCase.assertFalse(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		
		// when we enable it, we should awake the bodies
		pj.setLimitEnabled(true);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAsleep(true);
		b2.setAsleep(true);
		pj.setLimitEnabled(true);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		
		// if we disable the limit, then the bodies should be reawakened
		pj.setLimitEnabled(false);
		TestCase.assertFalse(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		pj.setLimits(1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		pj.setLowerLimit(-1.0);
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimits(1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		pj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimits(1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		pj.setLimitEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimits(-1.0, -1.0);
		TestCase.assertFalse(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(-1.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		pj.setLimits(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		pj.setLowerLimit(-2.0);
		TestCase.assertEquals(-2.0, pj.getLowerLimit());
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimits(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		pj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimits(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		pj.setLimitEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimits(1.0, 2.0);
		TestCase.assertFalse(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		pj.setLowerLimit(-1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		pj.setLimitEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLowerLimit(-2.0);
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(-2.0, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		pj.setUpperLimit(1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		pj.setLimitEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setUpperLimit(2.0);
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		pj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		pj.setLimitsEnabled(1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		b1.setAsleep(true);
		b2.setAsleep(true);
		pj.setLimitEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		pj.setLimitsEnabled(1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		PrismaticJoint pj = new PrismaticJoint(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertFalse(pj.isLimitEnabled());
		
		pj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());

		// then put the bodies to sleep
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		pj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		pj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		pj.setLowerLimit(-2.0);
		TestCase.assertEquals(-2.0, pj.getLowerLimit());
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		pj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		pj.setLimitEnabled(false);
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		pj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(pj.isLimitEnabled());
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
	}
}
