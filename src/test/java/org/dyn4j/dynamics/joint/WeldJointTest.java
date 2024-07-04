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
 * Used to test the {@link WeldJoint} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.2
 */
public class WeldJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 p = new Vector2(2.0, 4.0);
		
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, p);
		
		TestCase.assertEquals(p, wj.getAnchor1());
		TestCase.assertEquals(p, wj.getAnchor2());
		TestCase.assertNotSame(p, wj.getAnchor1());
		TestCase.assertNotSame(p, wj.getAnchor2());
		
		TestCase.assertEquals(0.3, wj.getSpringDampingRatio());
		TestCase.assertEquals(8.0, wj.getSpringFrequency());
		TestCase.assertEquals(0.0, wj.getSpringStiffness());
		TestCase.assertEquals(1000.0, wj.getMaximumSpringTorque());
		TestCase.assertEquals(0.0, wj.getLimitsReferenceAngle());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		TestCase.assertEquals(b1, wj.getBody1());
		TestCase.assertEquals(b2, wj.getBody2());
		
		TestCase.assertEquals(null, wj.getOwner());
		TestCase.assertEquals(null, wj.getUserData());
		TestCase.assertEquals(b2, wj.getOtherBody(b1));
		
		TestCase.assertEquals(false, wj.isCollisionAllowed());
		TestCase.assertEquals(false, wj.isSpringDamperEnabled());
		TestCase.assertEquals(false, wj.isSpringEnabled());
		TestCase.assertEquals(false, wj.isMaximumSpringTorqueEnabled());
		TestCase.assertEquals(false, wj.isLimitsEnabled());
		
		TestCase.assertNotNull(wj.toString());
	}

	/**
	 * Tests the create method passing a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new WeldJoint<Body>(null, b2, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new WeldJoint<Body>(b1, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new WeldJoint<Body>(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new WeldJoint<Body>(b1, b1, new Vector2());
	}
	

	/**
	 * Tests the isSpringEnabled method.
	 */
	@Test
	public void isSpringEnabled() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		TestCase.assertFalse(wj.isSpringEnabled());
		
		wj.setSpringFrequency(1.0);
		TestCase.assertFalse(wj.isSpringEnabled());
		
		wj.setSpringEnabled(true);
		TestCase.assertTrue(wj.isSpringEnabled());

		wj.setSpringFrequency(50.0);
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setSpringEnabled(false);
		TestCase.assertFalse(wj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setSpringFrequency(1.0);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setSpringDampingRatio(0.4);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(false);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringEnabled(false);
		wj.setSpringDamperEnabled(true);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setSpringDampingRatio() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		wj.setSpringDampingRatio(0.0);
		TestCase.assertEquals(0.0, wj.getSpringDampingRatio());
		
		wj.setSpringDampingRatio(0.001);
		TestCase.assertEquals(0.001, wj.getSpringDampingRatio());
		
		wj.setSpringDampingRatio(1.0);
		TestCase.assertEquals(1.0, wj.getSpringDampingRatio());
		
		wj.setSpringDampingRatio(0.2);
		TestCase.assertEquals(0.2, wj.getSpringDampingRatio());

		wj.setSpringEnabled(false);
		wj.setSpringDamperEnabled(false);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		wj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		wj.setSpringDamperEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		wj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		wj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		wj.setSpringDampingRatio(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setSpringDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setSpringDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringFrequency() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		wj.setSpringFrequency(0.0);
		TestCase.assertEquals(0.0, wj.getSpringFrequency());
		
		wj.setSpringFrequency(0.001);
		TestCase.assertEquals(0.001, wj.getSpringFrequency());
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, wj.getSpringMode());
		
		wj.setSpringFrequency(1.0);
		TestCase.assertEquals(1.0, wj.getSpringFrequency());
		
		wj.setSpringFrequency(29.0);
		TestCase.assertEquals(29.0, wj.getSpringFrequency());
		
		// at rest testing
		
		wj.setSpringEnabled(false);
		wj.setSpringDamperEnabled(false);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// the spring isn't enabled so it shouldn't wake the bodies
		wj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// enabling the spring should wake the bodies
		wj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		wj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// the frequency is changing, they should wake
		wj.setSpringFrequency(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// this should wake the bodies
		wj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests the spring mode changing.
	 */
	@Test
	public void setSpringMode() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		// test mode swapping
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, wj.getSpringMode());
		wj.setSpringStiffness(0.3);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, wj.getSpringMode());
		wj.setSpringFrequency(0.5);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, wj.getSpringMode());
	}
	
	/**
	 * Tests a negative stiffness value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringStiffnessNegative() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setSpringStiffness(-0.3);
	}

	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringStiffness() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		wj.setSpringStiffness(0.0);
		TestCase.assertEquals(0.0, wj.getSpringStiffness());
		
		wj.setSpringStiffness(0.001);
		TestCase.assertEquals(0.001, wj.getSpringStiffness());
		
		wj.setSpringStiffness(1.0);
		TestCase.assertEquals(1.0, wj.getSpringStiffness());
		
		wj.setSpringStiffness(29.0);
		TestCase.assertEquals(29.0, wj.getSpringStiffness());
		
		// at rest testing
		wj.setSpringEnabled(false);
		wj.setSpringDamperEnabled(false);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// the spring isn't enabled so it shouldn't wake the bodies
		wj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// enabling the spring should wake the bodies
		wj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		wj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// the frequency is changing, they should wake
		wj.setSpringStiffness(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// this should wake the bodies
		wj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringFrequencyNegative() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setSpringFrequency(-0.3);
	}

	/**
	 * Tests setting a negative maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceNegative() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setMaximumSpringTorque(-1.0);
	}
	
	/**
	 * Tests setting the maximum force.
	 */
	@Test
	public void setSpringMaximumForce() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		wj.setMaximumSpringTorque(0.0);
		TestCase.assertEquals(0.0, wj.getMaximumSpringTorque());
		
		wj.setMaximumSpringTorque(0.001);
		TestCase.assertEquals(0.001, wj.getMaximumSpringTorque());
		
		wj.setMaximumSpringTorque(1.0);
		TestCase.assertEquals(1.0, wj.getMaximumSpringTorque());
		
		wj.setMaximumSpringTorque(1000);
		TestCase.assertEquals(1000.0, wj.getMaximumSpringTorque());

		wj.setSpringEnabled(false);
		wj.setMaximumSpringTorqueEnabled(false);
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		wj.setMaximumSpringTorque(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		wj.setMaximumSpringTorqueEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		wj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		wj.setMaximumSpringTorque(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		wj.setMaximumSpringTorque(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this should wake them
		wj.setMaximumSpringTorqueEnabled(false);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}

	/**
	 * Tests spring stiffness/frequency calculations
	 */
	@Test
	public void computeSpringStiffnessFrequency() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		wj.setSpringFrequency(8.0);
		wj.setSpringDampingRatio(0.5);
		
		wj.updateSpringCoefficients();
		
		TestCase.assertEquals(8.0, wj.springFrequency);
		TestCase.assertEquals(0.5, wj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, wj.getSpringMode());
		TestCase.assertEquals(1984.401, wj.springStiffness, 1e-3);
		
		wj.setSpringStiffness(1000.0);
		wj.updateSpringCoefficients();
		
		TestCase.assertEquals(5.679, wj.springFrequency, 1e-3);
		TestCase.assertEquals(0.5, wj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, wj.getSpringMode());
		TestCase.assertEquals(1000.0, wj.springStiffness, 1e-3);
	}
	
	/**
	 * Tests the successful setting of the maximum angle.
	 */
	@Test
	public void setUpperLimitSuccess() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setUpperLimit(Math.toRadians(10));
		
		TestCase.assertEquals(Math.toRadians(10), wj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setUpperLimit(Math.toRadians(-10));
	}
	
	/**
	 * Tests the successful setting of the minimum angle.
	 */
	@Test
	public void setLowerLimit() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setLowerLimit(Math.toRadians(-10));
		
		TestCase.assertEquals(Math.toRadians(-10), wj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setLowerLimit(Math.toRadians(10));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setLimits(Math.toRadians(-30), Math.toRadians(20));
		
		TestCase.assertEquals(Math.toRadians(-30), wj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(20), wj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setLimits(Math.toRadians(30), Math.toRadians(20));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimitsToSameValue() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		wj.setLimits(Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), wj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(30), wj.getUpperLimit(), 1e-6);
	}

	/**
	 * Tests the get/set for the reference angle.
	 */
	@Test
	public void setLimitsReferenceAngle() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		TestCase.assertEquals(0.0, wj.getLimitsReferenceAngle());
	
		wj.setLimitsReferenceAngle(Math.toRadians(30.0));
		
		TestCase.assertEquals(Math.toRadians(30.0), wj.getLimitsReferenceAngle());
	}

	/**
	 * Tests setting the reference angle.
	 */
	@Test
	public void setLimitsReferenceAngleSleep() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we set it to the same value, the bodies should not wake
		wj.setLimitsReferenceAngle(0.0);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		wj.setLimitsReferenceAngle(1.0);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitEnabledSleep() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		wj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setLimits(defaultLowerLimit);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimits(Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(-Math.PI);
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(-Math.PI);
		TestCase.assertFalse(wj.isLimitsEnabled());
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
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		wj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
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
		TestCase.assertTrue(wj.isLimitsEnabled());
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
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(Math.PI, 2*Math.PI);
		TestCase.assertFalse(wj.isLimitsEnabled());
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
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		wj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		wj.setLowerLimit(-Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
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
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		wj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		wj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		wj.setUpperLimit(Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
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
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		wj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		wj.setLimitsEnabled(defaultLowerLimit);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		wj.setLimitsEnabled(Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitsEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		wj.setLimitsEnabled(Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is enabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = wj.getLowerLimit();
		double defaultUpperLimit = wj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		wj.setLimitsEnabled(true);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		wj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(-2*Math.PI);
		TestCase.assertEquals(-2*Math.PI, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, wj.getLowerLimit());
		TestCase.assertEquals(Math.PI, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitsEnabledSleep() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2());
		
		// by default the limit is disabled
		TestCase.assertFalse(wj.isLimitsEnabled());
		
		wj.setLimitsEnabled(true);
		
		// lets disable it first and ensure that the bodies are awake
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isLimitsEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isLimitsEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}

	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(-3.0, wj.getAnchor1().x);
		TestCase.assertEquals(0.5, wj.getAnchor1().y);
		TestCase.assertEquals(-3.0, wj.getAnchor2().x);
		TestCase.assertEquals(0.5, wj.getAnchor2().y);
		
		wj.shift(new Vector2(1.0, 3.0));
		
		// nothing should have changed
		TestCase.assertEquals(-3.0, wj.getAnchor1().x);
		TestCase.assertEquals(0.5, wj.getAnchor1().y);
		TestCase.assertEquals(-3.0, wj.getAnchor2().x);
		TestCase.assertEquals(0.5, wj.getAnchor2().y);
	}

	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2(-3.0, 0.5));
		wj.setCollisionAllowed(true);
		wj.setLimits(2, 5);
		wj.setLimitsEnabled(true);
		wj.setOwner(new Object());
		wj.setUserData(new Object());
		wj.setLimitsReferenceAngle(2);
		wj.setMaximumSpringTorque(4);
		wj.setMaximumSpringTorqueEnabled(true);
		wj.setSpringDamperEnabled(true);
		wj.setSpringDampingRatio(0.3);
		wj.setSpringEnabled(true);
		wj.setSpringFrequency(4.0);
		wj.setSpringStiffness(0.4);
		wj.angle = 2;
		wj.axialMass = 3;
		wj.impulse.set(3, 4, 5);
		wj.K.m00 = 2;
		wj.K.m01 = 3;
		wj.K.m02 = 4;
		wj.K.m10 = 4;
		wj.K.m11 = 5;
		wj.K.m12 = 6;
		wj.K.m20 = 7;
		wj.K.m21 = 2;
		wj.K.m22 = 1;
		wj.lowerLimitImpulse = 5;
		wj.r1.set(4, 5);
		wj.r2.set(9, 5);
		wj.referenceAngle = 3;
		wj.upperLimitImpulse = 7;
		wj.bias = 4;
		wj.damping = 3;
		wj.gamma = 6;
		wj.springImpulse = 5;
		wj.springMass = 6;
		
		WeldJoint<Body> wjc = wj.copy();
		
		TestCase.assertNotSame(wj, wjc);
		TestCase.assertNotSame(wj.bodies, wjc.bodies);
		TestCase.assertNotSame(wj.body1, wjc.body1);
		TestCase.assertNotSame(wj.body2, wjc.body2);
		TestCase.assertNotSame(wj.impulse, wjc.impulse);
		TestCase.assertNotSame(wj.K, wjc.K);
		TestCase.assertNotSame(wj.localAnchor1, wjc.localAnchor1);
		TestCase.assertNotSame(wj.localAnchor2, wjc.localAnchor2);
		TestCase.assertNotSame(wj.r1, wjc.r1);
		TestCase.assertNotSame(wj.r2, wjc.r2);
		TestCase.assertSame(wjc.body1, wjc.bodies.get(0));
		TestCase.assertSame(wjc.body2, wjc.bodies.get(1));
		TestCase.assertEquals(wj.bodies.size(), wjc.bodies.size());
		TestCase.assertEquals(wj.impulse.x, wjc.impulse.x);
		TestCase.assertEquals(wj.impulse.y, wjc.impulse.y);
		TestCase.assertEquals(wj.K.m00, wjc.K.m00);
		TestCase.assertEquals(wj.K.m01, wjc.K.m01);
		TestCase.assertEquals(wj.K.m10, wjc.K.m10);
		TestCase.assertEquals(wj.K.m11, wjc.K.m11);
		TestCase.assertEquals(wj.localAnchor1.x, wjc.localAnchor1.x);
		TestCase.assertEquals(wj.localAnchor1.y, wjc.localAnchor1.y);
		TestCase.assertEquals(wj.localAnchor2.x, wjc.localAnchor2.x);
		TestCase.assertEquals(wj.localAnchor2.y, wjc.localAnchor2.y);
		TestCase.assertEquals(wj.r1.x, wjc.r1.x);
		TestCase.assertEquals(wj.r1.y, wjc.r1.y);
		TestCase.assertEquals(wj.r2.x, wjc.r2.x);
		TestCase.assertEquals(wj.r2.y, wjc.r2.y);
		
		TestCase.assertNull(wjc.owner);
		TestCase.assertNull(wjc.userData);
		
		TestCase.assertEquals(wj.angle, wjc.angle);
		TestCase.assertEquals(wj.axialMass, wjc.axialMass);
		TestCase.assertEquals(wj.bias, wjc.bias);
		TestCase.assertEquals(wj.collisionAllowed, wjc.collisionAllowed);
		TestCase.assertEquals(wj.damping, wjc.damping);
		TestCase.assertEquals(wj.gamma, wjc.gamma);
		TestCase.assertEquals(wj.limitsEnabled, wjc.limitsEnabled);
		TestCase.assertEquals(wj.lowerLimit, wjc.lowerLimit);
		TestCase.assertEquals(wj.lowerLimitImpulse, wjc.lowerLimitImpulse);
		TestCase.assertEquals(wj.springDamperEnabled, wjc.springDamperEnabled);
		TestCase.assertEquals(wj.springDampingRatio, wjc.springDampingRatio);
		TestCase.assertEquals(wj.springEnabled, wjc.springEnabled);
		TestCase.assertEquals(wj.springFrequency, wjc.springFrequency);
		TestCase.assertEquals(wj.springImpulse, wjc.springImpulse);
		TestCase.assertEquals(wj.springMass, wjc.springMass);
		TestCase.assertEquals(wj.springMaximumTorque, wjc.springMaximumTorque);
		TestCase.assertEquals(wj.springMaximumTorqueEnabled, wjc.springMaximumTorqueEnabled);
		TestCase.assertEquals(wj.springMode, wjc.springMode);
		TestCase.assertEquals(wj.springStiffness, wjc.springStiffness);
		TestCase.assertEquals(wj.referenceAngle, wjc.referenceAngle);
		TestCase.assertEquals(wj.upperLimit, wjc.upperLimit);
		TestCase.assertEquals(wj.upperLimitImpulse, wjc.upperLimitImpulse);
		
		// test overriding the bodies
		wjc = wj.copy(b1, b2);
		
		TestCase.assertNotSame(wj, wjc);
		TestCase.assertNotSame(wj.bodies, wjc.bodies);
		TestCase.assertSame(wj.body1, wjc.body1);
		TestCase.assertSame(wj.body2, wjc.body2);
		TestCase.assertSame(wjc.body1, wjc.bodies.get(0));
		TestCase.assertSame(wjc.body2, wjc.bodies.get(1));
		TestCase.assertEquals(wj.bodies.size(), wjc.bodies.size());
		
		// test overriding body1
		wjc = wj.copy(b1, null);
		
		TestCase.assertNotSame(wj, wjc);
		TestCase.assertNotSame(wj.bodies, wjc.bodies);
		TestCase.assertSame(wj.body1, wjc.body1);
		TestCase.assertNotSame(wj.body2, wjc.body2);
		TestCase.assertSame(wjc.body1, wjc.bodies.get(0));
		TestCase.assertSame(wjc.body2, wjc.bodies.get(1));
		TestCase.assertEquals(wj.bodies.size(), wjc.bodies.size());

		// test overriding body2
		wjc = wj.copy(null, b2);
		
		TestCase.assertNotSame(wj, wjc);
		TestCase.assertNotSame(wj.bodies, wjc.bodies);
		TestCase.assertNotSame(wj.body1, wjc.body1);
		TestCase.assertSame(wj.body2, wjc.body2);
		TestCase.assertSame(wjc.body1, wjc.bodies.get(0));
		TestCase.assertSame(wjc.body2, wjc.bodies.get(1));
		TestCase.assertEquals(wj.bodies.size(), wjc.bodies.size());
	}
	
	/**
	 * Test the copy fail fast.
	 */
	@Test(expected = ClassCastException.class)
	public void copyFailed() {
		TestBody b1 = new TestBody();
		TestBody b2 = new TestBody();
		
		WeldJoint<Body> wj = new WeldJoint<Body>(b1, b2, new Vector2(-3.0, 0.5));
		
		wj.copy();
	}
}
