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
 * Used to test the {@link WheelJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.0.0
 */
public class WheelJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new WheelJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
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
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setFrequency(1.0);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setFrequency(15.24);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
		
		wj.setDampingRatio(0.4);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setDampingRatio(0.0);
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
	 * Tests a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setFrequencyZero() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setFrequency(0.0);
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
	
}
