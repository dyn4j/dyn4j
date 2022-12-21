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
public class WheelJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 p = new Vector2(1.0, 1.0);
		Vector2 a = new Vector2(0.0, 1.0);
		
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, p, a);
		
		TestCase.assertEquals(p, wj.getAnchor1());
		TestCase.assertEquals(p, wj.getAnchor2());
		TestCase.assertNotSame(p, wj.getAnchor1());
		TestCase.assertNotSame(p, wj.getAnchor2());
		TestCase.assertEquals(a, wj.getAxis());
		
		TestCase.assertEquals(0.0, wj.getAngularSpeed());
		TestCase.assertEquals(0.0, wj.getAngularTranslation());
		TestCase.assertEquals(0.0, wj.getLinearSpeed());
		TestCase.assertEquals(0.0, wj.getLinearTranslation());
		
		TestCase.assertEquals(0.3, wj.getSpringDampingRatio());
		TestCase.assertEquals(8.0, wj.getSpringFrequency());
		TestCase.assertEquals(0.0, wj.getSpringStiffness());
		TestCase.assertEquals(1000.0, wj.getMaximumSpringForce());
		TestCase.assertEquals(0.0, wj.getSpringRestOffset());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		TestCase.assertEquals(1000.0, wj.getMaximumMotorTorque());
		TestCase.assertEquals(0.0, wj.getMotorSpeed());
		
		TestCase.assertEquals(b1, wj.getBody1());
		TestCase.assertEquals(b2, wj.getBody2());
		
		TestCase.assertEquals(null, wj.getOwner());
		TestCase.assertEquals(null, wj.getUserData());
		TestCase.assertEquals(b2, wj.getOtherBody(b1));
		
		TestCase.assertEquals(false, wj.isCollisionAllowed());
		TestCase.assertEquals(false, wj.isLowerLimitEnabled());
		TestCase.assertEquals(false, wj.isUpperLimitEnabled());
		TestCase.assertEquals(false, wj.isMotorEnabled());
		TestCase.assertEquals(false, wj.isMaximumMotorTorqueEnabled());
		TestCase.assertEquals(true, wj.isSpringDamperEnabled());
		TestCase.assertEquals(true, wj.isSpringEnabled());
		TestCase.assertEquals(false, wj.isMaximumSpringForceEnabled());
		
		TestCase.assertNotNull(wj.toString());
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
	 * Tests the isSpringEnabled method.
	 */
	@Test
	public void isSpringEnabled() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setSpringFrequency(1.0);
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setSpringFrequency(100.0);
		TestCase.assertTrue(wj.isSpringEnabled());
		
		wj.setSpringEnabled(false);
		TestCase.assertFalse(wj.isSpringEnabled());

		wj.setSpringFrequency(50.0);
		TestCase.assertFalse(wj.isSpringEnabled());
		
		wj.setSpringEnabled(true);
		TestCase.assertTrue(wj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringFrequency(1.0);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringFrequency(100.0);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringDampingRatio(0.4);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringDampingRatio(1.0);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringEnabled(false);
		wj.setSpringDamperEnabled(true);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		TestCase.assertTrue(wj.isSpringDamperEnabled());
		
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(false);
		TestCase.assertFalse(wj.isSpringDamperEnabled());
	}
	
	/**
	 * Tests valid distance values.
	 */
	@Test
	public void setSpringRestOffset() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setSpringRestOffset(0.0);
		TestCase.assertEquals(0.0, wj.getSpringRestOffset());
		
		wj.setSpringRestOffset(1.0);
		TestCase.assertEquals(1.0, wj.getSpringRestOffset());
		
		wj.setSpringRestOffset(-1.0);
		TestCase.assertEquals(-1.0, wj.getSpringRestOffset());
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setSpringDampingRatio() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
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
	 * Tests a zero damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroDampingRatio() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringDampingRatio(0.0);
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringFrequency() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
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
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
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
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringStiffness(-0.3);
	}

	/**
	 * Tests a zero stiffness value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringStiffnessZero() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringStiffness(0.0);
	}

	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringStiffness() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
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
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringFrequency(-0.3);
	}

	/**
	 * Tests a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringFrequencyZero() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringFrequency(0.0);
	}

	/**
	 * Tests setting a zero maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceZero() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setMaximumSpringForce(0.0);
	}

	/**
	 * Tests setting a negative maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceNegative() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setMaximumSpringForce(-1.0);
	}
	
	/**
	 * Tests setting the maximum force.
	 */
	@Test
	public void setSpringMaximumForce() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setMaximumSpringForce(0.001);
		TestCase.assertEquals(0.001, wj.getMaximumSpringForce());
		
		wj.setMaximumSpringForce(1.0);
		TestCase.assertEquals(1.0, wj.getMaximumSpringForce());
		
		wj.setMaximumSpringForce(1000);
		TestCase.assertEquals(1000.0, wj.getMaximumSpringForce());

		wj.setSpringEnabled(false);
		wj.setMaximumSpringForceEnabled(false);
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		wj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		wj.setMaximumSpringForceEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		wj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		wj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		wj.setMaximumSpringForce(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this should wake them
		wj.setMaximumSpringForceEnabled(false);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}

	/**
	 * Tests spring stiffness/frequency calculations
	 */
	@Test
	public void computeSpringStiffnessFrequency() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		wj.setSpringFrequency(8.0);
		wj.setSpringDampingRatio(0.5);
		
		wj.updateSpringCoefficients();
		
		TestCase.assertEquals(8.0, wj.springFrequency);
		TestCase.assertEquals(0.5, wj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, wj.getSpringMode());
		TestCase.assertEquals(3968.803, wj.springStiffness, 1e-3);
		
		wj.setSpringStiffness(1000.0);
		wj.updateSpringCoefficients();
		
		TestCase.assertEquals(4.015, wj.springFrequency, 1e-3);
		TestCase.assertEquals(0.5, wj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, wj.getSpringMode());
		TestCase.assertEquals(1000.0, wj.springStiffness, 1e-3);
	}
	
	/**
	 * Tests the body's sleep state when changing the distance.
	 */
	@Test
	public void setSpringRestOffsetAtRest() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		double distance = wj.getSpringRestOffset();
		
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(distance, wj.getSpringRestOffset());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the distance to the same value
		wj.setSpringRestOffset(distance);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(distance, wj.getSpringRestOffset());
		
		// set the distance to a different value and make
		// sure the bodies are awakened
		wj.setSpringRestOffset(10);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(10.0, wj.getSpringRestOffset());
	}
	
	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMotorMaximumTorque() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setMaximumMotorTorque(10.0);
		TestCase.assertEquals(10.0, wj.getMaximumMotorTorque());
		
		wj.setMaximumMotorTorque(2548.0);
		TestCase.assertEquals(2548.0, wj.getMaximumMotorTorque());
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumMotorTorqueZero() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setMaximumMotorTorque(0.0);
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
		
		// don't change the max torque
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
	 * Tests the setting the maximum motor torque wrt. sleeping.
	 */
	@Test
	public void setMaximumMotorTorqueEnabledSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertFalse(wj.isMotorEnabled());
		TestCase.assertEquals(1000.0, wj.getMaximumMotorTorque());
		TestCase.assertFalse(wj.isMaximumMotorTorqueEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		wj.setMotorEnabled(true);
		wj.setMaximumMotorTorqueEnabled(true);

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// don't change the flag
		wj.setMaximumMotorTorqueEnabled(true);
		TestCase.assertTrue(wj.isMaximumMotorTorqueEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// disable it
		wj.setMaximumMotorTorqueEnabled(false);
		TestCase.assertFalse(wj.isMaximumMotorTorqueEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// disable the motor and change the value
		// the bodies shouldn't wake up
		wj.setMotorEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setMaximumMotorTorqueEnabled(true);
		TestCase.assertTrue(wj.isMaximumMotorTorqueEnabled());
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
	 * Tests the successful setting of the upper limit.
	 */
	@Test
	public void setUpperLimitSuccess() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setLowerLimit(-5.0);
		
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		wj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, wj.getUpperLimit(), 1e-6);
		
		wj.setUpperLimit(-2.0);
		TestCase.assertEquals(-2.0, wj.getUpperLimit(), 1e-6);
		
		wj.setUpperLimit(0.0);
		TestCase.assertEquals(0.0, wj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the upper limit because it would be less than the lower.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		wj.setUpperLimit(-0.5);
	}
	
	/**
	 * Tests the successful setting of the lower limit.
	 */
	@Test
	public void setLowerLimit() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		wj.setUpperLimit(5.0);
		
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		
		wj.setLowerLimit(-1.0);
		TestCase.assertEquals(-1.0, wj.getLowerLimit(), 1e-6);
		
		wj.setLowerLimit(0.0);
		TestCase.assertEquals(0.0, wj.getLowerLimit(), 1e-6);
		
		wj.setLowerLimit(2.0);
		TestCase.assertEquals(2.0, wj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the lower limit because it would be higher than the upper.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		
		wj.setLowerLimit(1.5);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		wj.setLimits(0.0, 1.0);		
		TestCase.assertEquals(0.0, wj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(1.0, wj.getUpperLimit(), 1e-6);
		
		wj.setLimits(-1.0, 2.0);		
		TestCase.assertEquals(-1.0, wj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(2.0, wj.getUpperLimit(), 1e-6);
		
		wj.setLimits(-1.0, -1.0);
		TestCase.assertEquals(-1.0, wj.getLowerLimit());
		TestCase.assertEquals(-1.0, wj.getUpperLimit());
		
		wj.setLimits(-4.0, -1.0);
		TestCase.assertEquals(-4.0, wj.getLowerLimit());
		TestCase.assertEquals(-1.0, wj.getUpperLimit());
		
		wj.setLimits(3.0, 5.0);
		TestCase.assertEquals(3.0, wj.getLowerLimit());
		TestCase.assertEquals(5.0, wj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid1() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		wj.setLimits(1.0, 0.0);
	}

	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid2() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		wj.setLimits(0.0, -1.0);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setSameLimitValid() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(0.0, wj.getUpperLimit());
		
		wj.setLimits(2.0);
		TestCase.assertEquals(2.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		wj.setLimits(-2.0);
		TestCase.assertEquals(-2.0, wj.getLowerLimit());
		TestCase.assertEquals(-2.0, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitsEnabledSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		
		// lets disable it first and ensure that the bodies are awake
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isUpperLimitEnabled());
		TestCase.assertFalse(wj.isLowerLimitEnabled());

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isUpperLimitEnabled());
		TestCase.assertFalse(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitsEnabled(true);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		wj.setLimitsEnabled(false);
		TestCase.assertFalse(wj.isUpperLimitEnabled());
		TestCase.assertFalse(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimits(2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(0.0);
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(1.0);
		TestCase.assertFalse(wj.isUpperLimitEnabled());
		TestCase.assertFalse(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimits(2.0, 3.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, wj.getLowerLimit());
		TestCase.assertEquals(3.0, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(1.0);
		TestCase.assertEquals(1.0, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(0.0, 3.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(3.0, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(0.0, 1.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(1.0, wj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimits(1.0, 2.0);
		TestCase.assertFalse(wj.isUpperLimitEnabled());
		TestCase.assertFalse(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		wj.setLowerLimit(-0.5);
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(-0.5, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLowerLimit(-0.2);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(-0.2, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		
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
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		wj.setUpperLimit(2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setUpperLimit(3.0);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(3.0, wj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		WheelJoint<Body> wj = new WheelJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		wj.setLimitsEnabled(2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		wj.setLimitsEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		wj.setLimitsEnabled(1.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
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
		
		wj.setLowerLimitEnabled(true);
		wj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		
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
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, wj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, wj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		wj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		wj.setLowerLimit(0.5);
		TestCase.assertEquals(0.5, wj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		wj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, wj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, wj.getLowerLimit());
		TestCase.assertEquals(2.0, wj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		wj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		wj.setLimitsEnabled(0.5, 4.0);
		TestCase.assertTrue(wj.isUpperLimitEnabled());
		TestCase.assertTrue(wj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.5, wj.getLowerLimit());
		TestCase.assertEquals(4.0, wj.getUpperLimit());
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
