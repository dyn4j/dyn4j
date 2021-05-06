/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
 * Used to test the {@link WheelJoint} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 3.0.0
 */
public class WheelJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 p = new Vector2(1.0, 1.0);
		Vector2 a = new Vector2(0.0, 1.0);
		
		WheelJoint<Body> dj = new WheelJoint<Body>(b1, b2, p, a);
		
		TestCase.assertEquals(p, dj.getAnchor1());
		TestCase.assertEquals(p, dj.getAnchor2());
		TestCase.assertNotSame(p, dj.getAnchor1());
		TestCase.assertNotSame(p, dj.getAnchor2());
		TestCase.assertEquals(a, dj.getAxis());
		
		TestCase.assertEquals(0.0, dj.getAngularSpeed());
		TestCase.assertEquals(0.0, dj.getAngularTranslation());
		TestCase.assertEquals(0.0, dj.getLinearSpeed());
		TestCase.assertEquals(0.0, dj.getLinearTranslation());
		
		TestCase.assertEquals(0.3, dj.getDampingRatio());
		TestCase.assertEquals(8.0, dj.getFrequency());
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		TestCase.assertEquals(0.0, dj.getUpperLimit());
		
		TestCase.assertEquals(1000.0, dj.getMaximumMotorTorque());
		TestCase.assertEquals(0.0, dj.getMotorSpeed());
		
		TestCase.assertEquals(b1, dj.getBody1());
		TestCase.assertEquals(b2, dj.getBody2());
		
		TestCase.assertEquals(null, dj.getOwner());
		TestCase.assertEquals(null, dj.getUserData());
		TestCase.assertEquals(b2, dj.getOtherBody(b1));
		
		TestCase.assertEquals(false, dj.isCollisionAllowed());
		TestCase.assertEquals(false, dj.isLimitEnabled());
		TestCase.assertEquals(false, dj.isMotorEnabled());
		TestCase.assertEquals(true, dj.isSpringDamperEnabled());
		TestCase.assertEquals(true, dj.isSpringEnabled());
		
		TestCase.assertNotNull(dj.toString());
	}

	/**
	 * Tests the create method passing a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new WheelJoint<Body>(null, b2, new Vector2(), new Vector2(0.0, 1.0));
	}

	/**
	 * Tests the create method passing a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new WheelJoint<Body>(b1, null, new Vector2(), new Vector2(0.0, 1.0));
	}

	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new WheelJoint<Body>(b1, b2, null, new Vector2(0.0, 1.0));
	}

	/**
	 * Tests the create method passing a null axis.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAxis() {
		new WheelJoint<Body>(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new WheelJoint<Body>(b1, b1, new Vector2(), new Vector2(0.0, 1.0));
	}

	/**
	 * Tests the isSpring method.
	 */
	@Test
	public void isSpringEnabled() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default is a spring
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setFrequency(1.0);
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setFrequency(15.24);
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setFrequency(0.0);
		TestCase.assertFalse(wj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setDampingRatio(0.0);
		wj.setFrequency(0.1);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setFrequency(15.24);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setDampingRatio(0.2);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setDampingRatio(0.0);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setDampingRatio(0.4);
		wj.setFrequency(0.0);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
	}

	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setDampingRatio() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setDampingRatio(0.0);
		TestCase.assertEquals(0.0, wj.getDampingRatio());
		
		wj.setDampingRatio(1.0);
		TestCase.assertEquals(1.0, wj.getDampingRatio());
		
		wj.setDampingRatio(0.2);
		TestCase.assertEquals(0.2, wj.getDampingRatio());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioNegative() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setFrequency() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));

		wj.setFrequency(0.0);
		TestCase.assertEquals(0.0, wj.getFrequency());
		
		wj.setFrequency(1.0);
		TestCase.assertEquals(1.0, wj.getFrequency());
		
		wj.setFrequency(29.0);
		TestCase.assertEquals(29.0, wj.getFrequency());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setFrequencyNegative() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setFrequency(-0.3);
	}

	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaximumMotorTorque() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setMaximumMotorTorque(0.0);
		TestCase.assertEquals(0.0, wj.getMaximumMotorTorque());
		
		wj.setMaximumMotorTorque(10.0);
		TestCase.assertEquals(10.0, wj.getMaximumMotorTorque());
		
		wj.setMaximumMotorTorque(2548.0);
		TestCase.assertEquals(2548.0, wj.getMaximumMotorTorque());
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumMotorTorqueNegative() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setMaximumMotorTorque(-2.0);
	}

	/**
	 * Tests the setting the maximum motor torque wrt. sleeping.
	 */
	@Test
	public void setMaximumMotorTorqueSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertFalse(wj.isMotorEnabled());
		TestCase.assertEquals(1000.0, wj.getMaximumMotorTorque());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		wj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the max force
		wj.setMaximumMotorTorque(1000.0);
		TestCase.assertEquals(1000.0, wj.getMaximumMotorTorque());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the max force
		wj.setMaximumMotorTorque(2.0);
		TestCase.assertEquals(2.0, wj.getMaximumMotorTorque());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		wj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setMaximumMotorTorque(1.0);
		TestCase.assertEquals(1.0, wj.getMaximumMotorTorque());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}

	/**
	 * Tests the enabling of the motor.
	 */
	@Test
	public void setMotorEnabled() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertFalse(wj.isMotorEnabled());
		
		wj.setMotorEnabled(true);
		TestCase.assertTrue(wj.isMotorEnabled());
		
		wj.setMotorEnabled(false);
		TestCase.assertFalse(wj.isMotorEnabled());
	}
	
	/**
	 * Tests the enabling of the motor wrt sleeping.
	 */
	@Test
	public void setMotorEnabledSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertFalse(wj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// disable the motor
		wj.setMotorEnabled(false);
		TestCase.assertFalse(wj.isMotorEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// enable the motor
		wj.setMotorEnabled(true);
		TestCase.assertTrue(wj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// set the motor to enabled again
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setMotorEnabled(true);
		TestCase.assertTrue(wj.isMotorEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());		
		
		wj.setMotorEnabled(false);
		TestCase.assertFalse(wj.isMotorEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}

	/**
	 * Tests the setting the motor speed.
	 */
	@Test
	public void setMotorSpeed() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getMotorSpeed());
		
		wj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, wj.getMotorSpeed());
		
		wj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, wj.getMotorSpeed());
		
		wj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, wj.getMotorSpeed());
	}
	
	/**
	 * Tests the setting the motor speed wrt. sleeping.
	 */
	@Test
	public void setMotorSpeedSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertFalse(wj.isMotorEnabled());
		TestCase.assertEquals(0.0, wj.getMotorSpeed());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		wj.setMotorEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the speed
		wj.setMotorSpeed(0.0);
		TestCase.assertEquals(0.0, wj.getMotorSpeed());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// change the speed
		wj.setMotorSpeed(2.0);
		TestCase.assertEquals(2.0, wj.getMotorSpeed());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		wj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setMotorSpeed(-1.0);
		TestCase.assertEquals(-1.0, wj.getMotorSpeed());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
	}

	/**
	 * Tests the successful setting of the maximum angle.
	 */
	@Test
	public void setUpperLimitSuccess() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setUpperLimit(10);
		
		TestCase.assertEquals(10, wj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setUpperLimit(Math.toRadians(-10));
	}
	
	/**
	 * Tests the successful setting of the minimum angle.
	 */
	@Test
	public void setLowerLimit() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLowerLimit(Math.toRadians(-10));
		
		TestCase.assertEquals(Math.toRadians(-10), wj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLowerLimit(Math.toRadians(10));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLimits(Math.toRadians(-30), Math.toRadians(20));
		
		TestCase.assertEquals(Math.toRadians(-30), wj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(20), wj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLimits(Math.toRadians(30), Math.toRadians(20));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimitsToSameValue() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLimits(Math.toRadians(30), Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), wj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(30), wj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitEnabledSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// lets disable it first and ensure that the bodies are awake
		wj.setLimitEnabled(false);
		TestCase.assertFalse(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		wj.setLimitEnabled(false);
		TestCase.assertFalse(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		wj.setLimitEnabled(true);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitEnabled(true);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		wj.setLimitEnabled(false);
		TestCase.assertFalse(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimits(Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(-Math.PI);
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
//		wj.setLimits(Math.PI);
//		TestCase.assertTrue(wj.isLimitEnabled());
//		TestCase.assertFalse(b1.isAtRest());
//		TestCase.assertFalse(b2.isAtRest());
//		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
//		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
//		wj.setLimitsEnabled(Math.PI);
//		TestCase.assertTrue(wj.isLimitEnabled());
//		TestCase.assertFalse(b1.isAtRest());
//		TestCase.assertFalse(b2.isAtRest());
//		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
//		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		wj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(-Math.PI, -Math.PI);
		TestCase.assertFalse(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(-Math.PI, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(-2*Math.PI);
		TestCase.assertEquals(-2*Math.PI, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		wj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(Math.PI, 2*Math.PI);
		TestCase.assertFalse(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		wj.setLowerLimit(-Math.PI);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		wj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLowerLimit(-2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-2*Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		wj.setUpperLimit(Math.PI);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		wj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setUpperLimit(2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, wj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		wj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		wj.setLimitsEnabled(1.0, 1.0);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		wj.setLimitsEnabled(1.0, 1.0);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitEnabled());
		
		wj.setLimitEnabled(true);
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		wj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(-2.0);
		TestCase.assertEquals(-2.0, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		wj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(-1.0, 1.0);
		TestCase.assertTrue(wj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
	}
	
	/**
	 * Tests invalid limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void invalidLimits() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLimitsEnabled(5, 0);
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(1.0, wj.getAnchor1().x);
		TestCase.assertEquals(2.0, wj.getAnchor1().y);
		TestCase.assertEquals(1.0, wj.getAnchor2().x);
		TestCase.assertEquals(2.0, wj.getAnchor2().y);
		
		wj.shift(new Vector2(1.0, 3.0));
		
		// nothing should have changed
		TestCase.assertEquals(1.0, wj.getAnchor1().x);
		TestCase.assertEquals(2.0, wj.getAnchor1().y);
		TestCase.assertEquals(1.0, wj.getAnchor2().x);
		TestCase.assertEquals(2.0, wj.getAnchor2().y);
	}
	
}
