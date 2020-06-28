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
package org.dyn4j.dynamics;

import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link RopeJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 2.2.2
 */
public class RopeJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation of an rope joint.
	 */
	@Test
	public void createSuccess() {
		new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new RopeJoint<Body>(null, b2, new Vector2(), new Vector2());
	}

	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new RopeJoint<Body>(b1, null, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor1() {
		new RopeJoint<Body>(b1, b2, null, new Vector2());
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor2() {
		new RopeJoint<Body>(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new RopeJoint<Body>(b1, b1, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the successful setting of the upper limit.
	 */
	@Test
	public void setUpperLimitSuccess() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getUpperLimit());
		
		rj.setUpperLimit(2.0);
		
		TestCase.assertEquals(2.0, rj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the upper limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitNegative() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getUpperLimit());
		
		rj.setUpperLimit(-1.0);
	}
	
	/**
	 * Tests the successful setting of the lower limit.
	 */
	@Test
	public void setLowerLimit() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		
		rj.setLowerLimit(0.0);
		
		TestCase.assertEquals(0.0, rj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the lower limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitNegative() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		
		rj.setLowerLimit(-1.0);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
		
		rj.setLimits(0.0, 1.0);		
		TestCase.assertEquals(0.0, rj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(1.0, rj.getUpperLimit(), 1e-6);
		
		rj.setLimits(1.0, 2.0);		
		TestCase.assertEquals(1.0, rj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(2.0, rj.getUpperLimit(), 1e-6);
		
		rj.setLimits(1.0, 1.0);
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
		
		rj.setLimits(1.0, 0.0);
	}

	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsNegative() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
		
		rj.setLimits(-1.0, 0.0);
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitsEnabledSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// lets disable it first and ensure that the bodies are awake
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isUpperLimitEnabled());
		TestCase.assertFalse(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isUpperLimitEnabled());
		TestCase.assertFalse(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setLimitsEnabled(true);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		rj.setLimitsEnabled(false);
		TestCase.assertFalse(rj.isUpperLimitEnabled());
		TestCase.assertFalse(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimits(2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(0.0);
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(1.0);
		TestCase.assertFalse(rj.isUpperLimitEnabled());
		TestCase.assertFalse(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimits(2.0, 3.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, rj.getLowerLimit());
		TestCase.assertEquals(3.0, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(1.0);
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(0.0, 3.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		TestCase.assertEquals(3.0, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(0.0, 1.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimits(1.0, 2.0);
		TestCase.assertFalse(rj.isUpperLimitEnabled());
		TestCase.assertFalse(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		rj.setLowerLimit(0.5);
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.5, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLowerLimit(0.2);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(0.2, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		
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
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		rj.setUpperLimit(2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setUpperLimit(3.0);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(3.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		
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
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		rj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		rj.setLimitsEnabled(2.0, 2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		rj.setLimitsEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		rj.setLimitsEnabled(1.0, 1.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(1.0, rj.getLowerLimit());
		TestCase.assertEquals(1.0, rj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		RopeJoint<Body> rj = new RopeJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		// by default the limit is enabled
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		
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
		// and the limit is already enabled the bodies should remain asleep
		rj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, rj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, rj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		rj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		rj.setLowerLimit(0.5);
		TestCase.assertEquals(0.5, rj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		rj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, rj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, rj.getLowerLimit());
		TestCase.assertEquals(2.0, rj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		rj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		rj.setLimitsEnabled(0.5, 4.0);
		TestCase.assertTrue(rj.isUpperLimitEnabled());
		TestCase.assertTrue(rj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.5, rj.getLowerLimit());
		TestCase.assertEquals(4.0, rj.getUpperLimit());
	}
}
