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
 * Used to test the {@link PrismaticJoint} class.
 * @author William Bittle
 * @version 4.0.1
 * @since 1.0.2
 */
public class PrismaticJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 anchor = new Vector2();
		Vector2 axis = new Vector2(0.0, 1.0);
		
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, anchor, axis);
		
		TestCase.assertEquals(anchor, pj.getAnchor1());
		TestCase.assertEquals(anchor, pj.getAnchor2());
		TestCase.assertNotSame(anchor, pj.getAnchor1());
		TestCase.assertNotSame(anchor, pj.getAnchor2());
		TestCase.assertEquals(axis, pj.getAxis());
		TestCase.assertNotSame(axis, pj.getAxis());
		
		TestCase.assertEquals(1000.0, pj.getMaximumMotorForce());
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
		
		TestCase.assertEquals(0.0, pj.getReferenceAngle());
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		TestCase.assertEquals(b1, pj.getBody1());
		TestCase.assertEquals(b2, pj.getBody2());
		
		TestCase.assertEquals(null, pj.getOwner());
		TestCase.assertEquals(null, pj.getUserData());
		TestCase.assertEquals(b2, pj.getOtherBody(b1));
		
		TestCase.assertEquals(false, pj.isCollisionAllowed());
		TestCase.assertEquals(false, pj.isLowerLimitEnabled());
		TestCase.assertEquals(false, pj.isUpperLimitEnabled());
		TestCase.assertEquals(false, pj.isMotorEnabled());
		TestCase.assertEquals(false, pj.isMaximumMotorForceEnabled());
		
		TestCase.assertNotNull(pj.toString());
	}
	
	/**
	 * Tests the create method passing a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new PrismaticJoint<Body>(null, b2, new Vector2(), new Vector2(0.0, 1.0));
	}
	
	/**
	 * Tests the create method passing a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new PrismaticJoint<Body>(b1, null, new Vector2(), new Vector2(0.0, 1.0));
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new PrismaticJoint<Body>(b1, b2, null, new Vector2(0.0, 1.0));
	}

	/**
	 * Tests the create method passing a null axis.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAxis() {
		new PrismaticJoint<Body>(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMotorMaximumForce() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setMaximumMotorForce(10.0);
		TestCase.assertEquals(10.0, pj.getMaximumMotorForce());
		
		pj.setMaximumMotorForce(2548.0);
		TestCase.assertEquals(2548.0, pj.getMaximumMotorForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroMaximumMotorForce() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setMaximumMotorForce(0.0);
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumMotorForce() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setMaximumMotorForce(-2.0);
	}

	/**
	 * Tests the setting the maximum motor force wrt. sleeping.
	 */
	@Test
	public void setMotorMaximumForceSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertEquals(1000.0, pj.getMaximumMotorForce());
		TestCase.assertFalse(pj.isMaximumMotorForceEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		pj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the max force
		pj.setMaximumMotorForce(1000.0);
		TestCase.assertEquals(1000.0, pj.getMaximumMotorForce());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the max force
		pj.setMaximumMotorForce(2.0);
		TestCase.assertEquals(2.0, pj.getMaximumMotorForce());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		pj.setMaximumMotorForceEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// change the max force
		pj.setMaximumMotorForce(2.0);
		TestCase.assertEquals(2.0, pj.getMaximumMotorForce());
		TestCase.assertTrue(pj.isMaximumMotorForceEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the max force
		pj.setMaximumMotorForce(4.0);
		TestCase.assertEquals(4.0, pj.getMaximumMotorForce());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		pj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setMaximumMotorForce(1.0);
		TestCase.assertEquals(1.0, pj.getMaximumMotorForce());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}

	/**
	 * Tests the enabling of the motor.
	 */
	@Test
	public void setMotorEnabled() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
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
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// disable the motor
		pj.setMotorEnabled(false);
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// enable the motor
		pj.setMotorEnabled(true);
		TestCase.assertTrue(pj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// set the motor to enabled again
		b1.setAtRest(true);
		b2.setAtRest(true);
		pj.setMotorEnabled(true);
		TestCase.assertTrue(pj.isMotorEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());		
		
		pj.setMotorEnabled(false);
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}

	/**
	 * Tests the setting the motor speed.
	 */
	@Test
	public void setMotorSpeed() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
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
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertFalse(pj.isMotorEnabled());
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		pj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the speed
		pj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, pj.getMotorSpeed());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the speed
		pj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, pj.getMotorSpeed());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		pj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, pj.getMotorSpeed());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}

	/**
	 * Tests the successful setting of the reference angle.
	 */
	@Test
	public void setReferenceAngle() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getReferenceAngle());
		
		pj.setReferenceAngle(Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), pj.getReferenceAngle());
	}

	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(1.0, 1.0), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, pj.getAnchor1().x);
		TestCase.assertEquals(1.0, pj.getAnchor1().y);
		TestCase.assertEquals(1.0, pj.getAnchor2().x);
		TestCase.assertEquals(1.0, pj.getAnchor2().y);
		
		pj.shift(new Vector2(1.0, 3.0));
		
		// nothing should have changed
		TestCase.assertEquals(1.0, pj.getAnchor1().x);
		TestCase.assertEquals(1.0, pj.getAnchor1().y);
		TestCase.assertEquals(1.0, pj.getAnchor2().x);
		TestCase.assertEquals(1.0, pj.getAnchor2().y);
	}
	
	/**
	 * Tests the successful setting of the upper limit.
	 */
	@Test
	public void setUpperLimitSuccess() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setLowerLimit(-5.0);
		
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, pj.getUpperLimit(), 1e-6);
		
		pj.setUpperLimit(-2.0);
		TestCase.assertEquals(-2.0, pj.getUpperLimit(), 1e-6);
		
		pj.setUpperLimit(0.0);
		TestCase.assertEquals(0.0, pj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the upper limit because it would be less than the lower.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setUpperLimit(-0.5);
	}
	
	/**
	 * Tests the successful setting of the lower limit.
	 */
	@Test
	public void setLowerLimit() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setUpperLimit(5.0);
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		
		pj.setLowerLimit(-1.0);
		TestCase.assertEquals(-1.0, pj.getLowerLimit(), 1e-6);
		
		pj.setLowerLimit(0.0);
		TestCase.assertEquals(0.0, pj.getLowerLimit(), 1e-6);
		
		pj.setLowerLimit(2.0);
		TestCase.assertEquals(2.0, pj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the lower limit because it would be higher than the upper.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		
		pj.setLowerLimit(1.5);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setLimits(0.0, 1.0);		
		TestCase.assertEquals(0.0, pj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(1.0, pj.getUpperLimit(), 1e-6);
		
		pj.setLimits(-1.0, 2.0);		
		TestCase.assertEquals(-1.0, pj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(2.0, pj.getUpperLimit(), 1e-6);
		
		pj.setLimits(-1.0, -1.0);
		TestCase.assertEquals(-1.0, pj.getLowerLimit());
		TestCase.assertEquals(-1.0, pj.getUpperLimit());
		
		pj.setLimits(-4.0, -1.0);
		TestCase.assertEquals(-4.0, pj.getLowerLimit());
		TestCase.assertEquals(-1.0, pj.getUpperLimit());
		
		pj.setLimits(3.0, 5.0);
		TestCase.assertEquals(3.0, pj.getLowerLimit());
		TestCase.assertEquals(5.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid1() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setLimits(1.0, 0.0);
	}

	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid2() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setLimits(0.0, -1.0);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setSameLimitValid() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(0.0, pj.getUpperLimit());
		
		pj.setLimits(2.0);
		TestCase.assertEquals(2.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		pj.setLimits(-2.0);
		TestCase.assertEquals(-2.0, pj.getLowerLimit());
		TestCase.assertEquals(-2.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitsEnabledSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		pj.setLimitsEnabled(false);
		TestCase.assertFalse(pj.isUpperLimitEnabled());
		TestCase.assertFalse(pj.isLowerLimitEnabled());

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		pj.setLimitsEnabled(false);
		TestCase.assertFalse(pj.isUpperLimitEnabled());
		TestCase.assertFalse(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		pj.setLimitsEnabled(true);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		pj.setLimitsEnabled(true);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		pj.setLimitsEnabled(false);
		TestCase.assertFalse(pj.isUpperLimitEnabled());
		TestCase.assertFalse(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		pj.setLimits(2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		pj.setLowerLimit(0.0);
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimits(2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		pj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, pj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimits(2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		pj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimits(1.0);
		TestCase.assertFalse(pj.isUpperLimitEnabled());
		TestCase.assertFalse(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		pj.setLimits(2.0, 3.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, pj.getLowerLimit());
		TestCase.assertEquals(3.0, pj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		pj.setLowerLimit(1.0);
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimits(0.0, 3.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(3.0, pj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		pj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimits(0.0, 1.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		pj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimits(1.0, 2.0);
		TestCase.assertFalse(pj.isUpperLimitEnabled());
		TestCase.assertFalse(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		pj.setLowerLimit(-0.5);
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-0.5, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		pj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLowerLimit(-0.2);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-0.2, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		pj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		pj.setUpperLimit(2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		pj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setUpperLimit(3.0);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(3.0, pj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		pj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		pj.setLimitsEnabled(2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		pj.setLimitsEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		pj.setLimitsEnabled(1.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(1.0, pj.getLowerLimit());
		TestCase.assertEquals(1.0, pj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setLowerLimitEnabled(true);
		pj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = pj.getLowerLimit();
		double defaultUpperLimit = pj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		pj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, pj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, pj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		pj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		pj.setLowerLimit(0.5);
		TestCase.assertEquals(0.5, pj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		pj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, pj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, pj.getLowerLimit());
		TestCase.assertEquals(2.0, pj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		pj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		pj.setLimitsEnabled(0.5, 4.0);
		TestCase.assertTrue(pj.isUpperLimitEnabled());
		TestCase.assertTrue(pj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.5, pj.getLowerLimit());
		TestCase.assertEquals(4.0, pj.getUpperLimit());
	}
	

	/**
	 * Tests the isSpringEnabled method.
	 */
	@Test
	public void isSpringEnabled() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		TestCase.assertFalse(pj.isSpringEnabled());
		
		pj.setSpringFrequency(1.0);
		TestCase.assertFalse(pj.isSpringEnabled());
		
		pj.setSpringFrequency(100.0);
		TestCase.assertFalse(pj.isSpringEnabled());
		
		pj.setSpringEnabled(true);
		TestCase.assertTrue(pj.isSpringEnabled());

		pj.setSpringFrequency(50.0);
		TestCase.assertTrue(pj.isSpringEnabled());
		
		pj.setSpringEnabled(false);
		TestCase.assertFalse(pj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringFrequency(1.0);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringFrequency(100.0);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringDampingRatio(0.4);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringDampingRatio(1.0);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringEnabled(true);
		pj.setSpringDamperEnabled(false);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringDamperEnabled(true);
		TestCase.assertTrue(pj.isSpringDamperEnabled());
		
		pj.setSpringEnabled(false);
		TestCase.assertTrue(pj.isSpringDamperEnabled());
	}
	
	/**
	 * Tests valid distance values.
	 */
	@Test
	public void setSpringRestOffset() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setSpringRestOffset(0.0);
		TestCase.assertEquals(0.0, pj.getSpringRestOffset());
		
		pj.setSpringRestOffset(1.0);
		TestCase.assertEquals(1.0, pj.getSpringRestOffset());
		
		pj.setSpringRestOffset(-1.0);
		TestCase.assertEquals(-1.0, pj.getSpringRestOffset());
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setSpringDampingRatio() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringDampingRatio(0.001);
		TestCase.assertEquals(0.001, pj.getSpringDampingRatio());
		
		pj.setSpringDampingRatio(1.0);
		TestCase.assertEquals(1.0, pj.getSpringDampingRatio());
		
		pj.setSpringDampingRatio(0.2);
		TestCase.assertEquals(0.2, pj.getSpringDampingRatio());

		pj.setSpringEnabled(false);
		pj.setSpringDamperEnabled(false);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		pj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		pj.setSpringDamperEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		pj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		pj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		pj.setSpringDampingRatio(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests a zero damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroDampingRatio() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringDampingRatio(0.0);
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringFrequency() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setSpringFrequency(0.001);
		TestCase.assertEquals(0.001, pj.getSpringFrequency());
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, pj.getSpringMode());
		
		pj.setSpringFrequency(1.0);
		TestCase.assertEquals(1.0, pj.getSpringFrequency());
		
		pj.setSpringFrequency(29.0);
		TestCase.assertEquals(29.0, pj.getSpringFrequency());
		
		// at rest testing
		
		pj.setSpringEnabled(false);
		pj.setSpringDamperEnabled(false);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// the spring isn't enabled so it shouldn't wake the bodies
		pj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// enabling the spring should wake the bodies
		pj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		pj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// the frequency is changing, they should wake
		pj.setSpringFrequency(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// this should wake the bodies
		pj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests the spring mode changing.
	 */
	@Test
	public void setSpringMode() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		// test mode swapping
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, pj.getSpringMode());
		pj.setSpringStiffness(0.3);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, pj.getSpringMode());
		pj.setSpringFrequency(0.5);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, pj.getSpringMode());
	}
	
	/**
	 * Tests a negative stiffness value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringStiffnessNegative() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringStiffness(-0.3);
	}

	/**
	 * Tests a zero stiffness value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringStiffnessZero() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringStiffness(0.0);
	}

	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringStiffness() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		pj.setSpringStiffness(0.001);
		TestCase.assertEquals(0.001, pj.getSpringStiffness());
		
		pj.setSpringStiffness(1.0);
		TestCase.assertEquals(1.0, pj.getSpringStiffness());
		
		pj.setSpringStiffness(29.0);
		TestCase.assertEquals(29.0, pj.getSpringStiffness());
		
		// at rest testing
		pj.setSpringEnabled(false);
		pj.setSpringDamperEnabled(false);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// the spring isn't enabled so it shouldn't wake the bodies
		pj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// enabling the spring should wake the bodies
		pj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		pj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// the frequency is changing, they should wake
		pj.setSpringStiffness(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// this should wake the bodies
		pj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringFrequencyNegative() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringFrequency(-0.3);
	}

	/**
	 * Tests a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringFrequencyZero() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringFrequency(0.0);
	}

	/**
	 * Tests setting a zero maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceZero() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setMaximumSpringForce(0.0);
	}

	/**
	 * Tests setting a negative maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceNegative() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setMaximumSpringForce(-1.0);
	}
	
	/**
	 * Tests setting the maximum force.
	 */
	@Test
	public void setSpringMaximumForce() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setMaximumSpringForce(0.001);
		TestCase.assertEquals(0.001, pj.getMaximumSpringForce());
		
		pj.setMaximumSpringForce(1.0);
		TestCase.assertEquals(1.0, pj.getMaximumSpringForce());
		
		pj.setMaximumSpringForce(1000);
		TestCase.assertEquals(1000.0, pj.getMaximumSpringForce());

		pj.setSpringEnabled(false);
		pj.setMaximumSpringForceEnabled(false);
		TestCase.assertFalse(pj.isMaximumSpringForceEnabled());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		pj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		pj.setMaximumSpringForceEnabled(true);
		TestCase.assertTrue(pj.isMaximumSpringForceEnabled());
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		pj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		pj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		pj.setMaximumSpringForce(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this should wake them
		pj.setMaximumSpringForceEnabled(false);
		TestCase.assertFalse(pj.isMaximumSpringForceEnabled());
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}

	/**
	 * Tests spring stiffness/frequency calculations
	 */
	@Test
	public void computeSpringStiffnessFrequency() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		pj.setSpringEnabled(true);
		pj.setSpringDamperEnabled(true);
		pj.setSpringFrequency(8.0);
		pj.setSpringDampingRatio(0.5);
		
		pj.updateSpringCoefficients();
		
		TestCase.assertEquals(8.0, pj.springFrequency);
		TestCase.assertEquals(0.5, pj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, pj.getSpringMode());
		TestCase.assertEquals(3968.803, pj.springStiffness, 1e-3);
		
		pj.setSpringStiffness(1000.0);
		pj.updateSpringCoefficients();
		
		TestCase.assertEquals(4.015, pj.springFrequency, 1e-3);
		TestCase.assertEquals(0.5, pj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, pj.getSpringMode());
		TestCase.assertEquals(1000.0, pj.springStiffness, 1e-3);
	}
	
	/**
	 * Tests the body's sleep state when changing the distance.
	 */
	@Test
	public void setSpringRestOffsetAtRest() {
		PrismaticJoint<Body> pj = new PrismaticJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		double distance = pj.getSpringRestOffset();
		
		pj.setSpringEnabled(true);
		
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(distance, pj.getSpringRestOffset());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the distance to the same value
		pj.setSpringRestOffset(distance);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(distance, pj.getSpringRestOffset());
		
		// set the distance to a different value and make
		// sure the bodies are awakened
		pj.setSpringRestOffset(10);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(10.0, pj.getSpringRestOffset());
	}
	
}
