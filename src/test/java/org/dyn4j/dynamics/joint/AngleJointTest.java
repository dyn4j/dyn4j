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
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link AngleJoint} class.
 * @author William Bittle
 * @version 4.0.1
 * @since 2.2.2
 */
public class AngleJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation of an angle joint.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		new AngleJoint<Body>(b1, b2);
	}
	
	/**
	 * Tests the failed creation of an angle joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new AngleJoint<Body>(null, b2);
	}

	/**
	 * Tests the failed creation of an angle joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new AngleJoint<Body>(b1, null);
	}
	
	/**
	 * Tests the failed creation of an angle joint.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new AngleJoint<Body>(b1, b1);
	}
	
	/**
	 * Tests the successful setting of the maximum angle.
	 */
	@Test
	public void setUpperLimitSuccess() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setUpperLimit(Math.toRadians(10));
		
		TestCase.assertEquals(Math.toRadians(10), aj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setUpperLimit(Math.toRadians(-10));
	}
	
	/**
	 * Tests the successful setting of the minimum angle.
	 */
	@Test
	public void setLowerLimit() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setLowerLimit(Math.toRadians(-10));
		
		TestCase.assertEquals(Math.toRadians(-10), aj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setLowerLimit(Math.toRadians(10));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setLimits(Math.toRadians(-30), Math.toRadians(20));
		
		TestCase.assertEquals(Math.toRadians(-30), aj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(20), aj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setLimits(Math.toRadians(30), Math.toRadians(20));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setUpperAndLowerLimitsToSameValue() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		aj.setLimits(Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), aj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(30), aj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitEnabledSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		aj.setLimitEnabled(false);
		TestCase.assertFalse(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		aj.setLimitEnabled(false);
		TestCase.assertFalse(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		aj.setLimitEnabled(true);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		aj.setLimitEnabled(true);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		aj.setLimitEnabled(false);
		TestCase.assertFalse(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = aj.getLowerLimit();
		double defaultUpperLimit = aj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		aj.setLimits(defaultLowerLimit);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		aj.setLimits(Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		aj.setLowerLimit(-Math.PI);
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimits(Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		aj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, aj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimits(Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		aj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimits(-Math.PI);
		TestCase.assertFalse(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(-Math.PI, aj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = aj.getLowerLimit();
		double defaultUpperLimit = aj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		aj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		aj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		aj.setLowerLimit(-2*Math.PI);
		TestCase.assertEquals(-2*Math.PI, aj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		aj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, aj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimits(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		aj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimits(Math.PI, 2*Math.PI);
		TestCase.assertFalse(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, aj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = aj.getLowerLimit();
		double defaultUpperLimit = aj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		aj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		aj.setLowerLimit(-Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		aj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLowerLimit(-2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-2*Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = aj.getLowerLimit();
		double defaultUpperLimit = aj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		aj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		aj.setUpperLimit(Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		aj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setUpperLimit(2*Math.PI);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(2*Math.PI, aj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = aj.getLowerLimit();
		double defaultUpperLimit = aj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		aj.setLimitsEnabled(defaultLowerLimit);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		aj.setLimitsEnabled(Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		aj.setLimitEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		aj.setLimitsEnabled(Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		// by default the limit is enabled
		TestCase.assertTrue(aj.isLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = aj.getLowerLimit();
		double defaultUpperLimit = aj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		aj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, aj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, aj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		aj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		aj.setLowerLimit(-2*Math.PI);
		TestCase.assertEquals(-2*Math.PI, aj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		aj.setUpperLimit(2*Math.PI);
		TestCase.assertEquals(2*Math.PI, aj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		aj.setLimitEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		aj.setLimitsEnabled(-Math.PI, Math.PI);
		TestCase.assertTrue(aj.isLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-Math.PI, aj.getLowerLimit());
		TestCase.assertEquals(Math.PI, aj.getUpperLimit());
	}
}
