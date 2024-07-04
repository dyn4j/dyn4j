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
 * Used to test the {@link PinJoint} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.2
 */
public class PinJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		Vector2 p = new Vector2(1.0, -1.0);
		PinJoint<Body> pj = new PinJoint<Body>(b1, p);
		
		TestCase.assertEquals(p, pj.getAnchor());
		TestCase.assertNotSame(p, pj.getAnchor());
		
		TestCase.assertEquals(0.3, pj.getSpringDampingRatio());
		TestCase.assertEquals(8.0, pj.getSpringFrequency());
		TestCase.assertEquals(1000.0, pj.getMaximumSpringForce());
		TestCase.assertTrue(pj.isSpringDamperEnabled());
		TestCase.assertTrue(pj.isSpringEnabled());
		TestCase.assertTrue(pj.isMaximumSpringForceEnabled());
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, pj.getSpringMode());
		
		TestCase.assertEquals(b1, pj.getBody());
		
		TestCase.assertEquals(null, pj.getOwner());
		TestCase.assertEquals(null, pj.getUserData());
		
		TestCase.assertEquals(false, pj.isCollisionAllowed());
		TestCase.assertEquals(p, pj.getTarget());
		
		TestCase.assertNotNull(pj.toString());
	}
	
	/**
	 * Tests the create method passing a null body.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody() {
		new PinJoint<Body>(null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null target point.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullTarget() {
		new PinJoint<Body>(b1, null);
	}
	
	/**
	 * Tests setting a valid target.
	 */
	@Test
	public void setTarget() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		
		Vector2 v1 = new Vector2();
		pj.setTarget(v1);
		TestCase.assertTrue(v1.equals(pj.getTarget()));
		
		Vector2 v2 = new Vector2(2.0, 1.032);
		pj.setTarget(v2);
		TestCase.assertTrue(v2.equals(pj.getTarget()));
	}
	
	/**
	 * Tests setting a null target.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTarget() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setTarget(null);
	}
	

	/**
	 * Tests the isSpringEnabled method.
	 */
	@Test
	public void isSpringEnabled() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		TestCase.assertTrue(pj.isSpringEnabled());
		
		pj.setSpringEnabled(false);
		
		TestCase.assertFalse(pj.isSpringEnabled());
		
		pj.setSpringFrequency(1.0);
		TestCase.assertFalse(pj.isSpringEnabled());
		
		pj.setSpringFrequency(100.0);
		TestCase.assertFalse(pj.isSpringEnabled());
		
		pj.setSpringEnabled(true);
		TestCase.assertTrue(pj.isSpringEnabled());
		
		pj.setSpringEnabled(false);
		TestCase.assertFalse(pj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		TestCase.assertTrue(pj.isSpringEnabled());
		TestCase.assertTrue(pj.isSpringDamperEnabled());
		
		pj.setSpringEnabled(false);
		pj.setSpringDamperEnabled(false);
		
		TestCase.assertFalse(pj.isSpringEnabled());
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringFrequency(1.0);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringFrequency(100.0);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringDampingRatio(0.4);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringDampingRatio(1.0);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
		
		pj.setSpringEnabled(false);
		pj.setSpringDamperEnabled(true);
		TestCase.assertTrue(pj.isSpringDamperEnabled());
		
		pj.setSpringEnabled(true);
		pj.setSpringDamperEnabled(true);
		TestCase.assertTrue(pj.isSpringDamperEnabled());
		
		pj.setSpringEnabled(true);
		pj.setSpringDamperEnabled(false);
		TestCase.assertFalse(pj.isSpringDamperEnabled());
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setSpringDampingRatio() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		
		pj.setSpringDampingRatio(0.0);
		TestCase.assertEquals(0.0, pj.getSpringDampingRatio());
		
		pj.setSpringDampingRatio(0.001);
		TestCase.assertEquals(0.001, pj.getSpringDampingRatio());
		
		pj.setSpringDampingRatio(1.0);
		TestCase.assertEquals(1.0, pj.getSpringDampingRatio());
		
		pj.setSpringDampingRatio(0.2);
		TestCase.assertEquals(0.2, pj.getSpringDampingRatio());

		pj.setSpringEnabled(false);
		
		this.b1.setAtRest(true);
		
		// this won't wake them because its not enabled
		pj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		pj.setSpringDamperEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());

		// enable the spring
		pj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		pj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		
		// this should wake them
		pj.setSpringDampingRatio(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setSpringDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setSpringDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringFrequency() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		
		pj.setSpringFrequency(0.0);
		TestCase.assertEquals(0.0, pj.getSpringFrequency());
		
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
		
		// the spring isn't enabled so it shouldn't wake the bodies
		pj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		
		// enabling the spring should wake the bodies
		pj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());

		this.b1.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		pj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		
		// the frequency is changing, they should wake
		pj.setSpringFrequency(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		
		this.b1.setAtRest(true);

		// this should wake the bodies
		pj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
	}
	
	/**
	 * Tests the spring mode changing.
	 */
	@Test
	public void setSpringMode() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
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
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setSpringStiffness(-0.3);
	}

	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringStiffness() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		
		pj.setSpringStiffness(0.0);
		TestCase.assertEquals(0.0, pj.getSpringStiffness());
		
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
		
		// the spring isn't enabled so it shouldn't wake the bodies
		pj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		
		// enabling the spring should wake the bodies
		pj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());

		this.b1.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		pj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		
		// the frequency is changing, they should wake
		pj.setSpringStiffness(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		
		this.b1.setAtRest(true);
		
		// this should wake the bodies
		pj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringFrequencyNegative() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setSpringFrequency(-0.3);
	}

	/**
	 * Tests setting a negative maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceNegative() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setMaximumSpringForce(-1.0);
	}
	
	/**
	 * Tests setting the maximum force.
	 */
	@Test
	public void setSpringMaximumForce() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		
		pj.setMaximumSpringForce(0.0);
		TestCase.assertEquals(0.0, pj.getMaximumSpringForce());
		
		pj.setMaximumSpringForce(0.001);
		TestCase.assertEquals(0.001, pj.getMaximumSpringForce());
		
		pj.setMaximumSpringForce(1.0);
		TestCase.assertEquals(1.0, pj.getMaximumSpringForce());
		
		pj.setMaximumSpringForce(1000);
		TestCase.assertEquals(1000.0, pj.getMaximumSpringForce());

		pj.setSpringEnabled(false);
		
		this.b1.setAtRest(true);
		
		// this won't wake them because its not enabled
		pj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		pj.setMaximumSpringForceEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());

		// enable the spring
		pj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		pj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		
		// this should wake them
		pj.setMaximumSpringForce(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		
		this.b1.setAtRest(true);
		
		// this should wake them
		pj.setMaximumSpringForceEnabled(false);
		TestCase.assertFalse(this.b1.isAtRest());
	}

	/**
	 * Tests spring stiffness/frequency calculations
	 */
	@Test
	public void computeSpringStiffnessFrequency() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setSpringEnabled(true);
		pj.setSpringDamperEnabled(true);
		pj.setSpringFrequency(8.0);
		pj.setSpringDampingRatio(0.5);
		
		pj.updateSpringCoefficients();
		
		TestCase.assertEquals(8.0, pj.springFrequency);
		TestCase.assertEquals(0.5, pj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, pj.getSpringMode());
		TestCase.assertEquals(7937.606, pj.springStiffness, 1e-3);
		
		pj.setSpringStiffness(1000.0);
		pj.updateSpringCoefficients();
		
		TestCase.assertEquals(2.839, pj.springFrequency, 1e-3);
		TestCase.assertEquals(0.5, pj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, pj.getSpringMode());
		TestCase.assertEquals(1000.0, pj.springStiffness, 1e-3);
	}
	
	/**
	 * Tests the shift method.
	 * @since 3.1.0
	 */
	@Test
	public void shiftCoordinates() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setTarget(new Vector2(1.0, -1.0));
		
		pj.shift(new Vector2(-1.0, 2.0));
		
		TestCase.assertEquals(0.0, pj.getTarget().x, 1.0e-3);
		TestCase.assertEquals(1.0, pj.getTarget().y, 1.0e-3);
	}
	
	/**
	 * Tests the body's sleep state when changing the target.
	 */
	@Test
	public void setTargetSleep() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		
		Vector2 defaultTarget = pj.getTarget();
		
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertTrue(defaultTarget.equals(pj.getTarget()));
		
		b1.setAtRest(true);
		
		// set the target to the same value
		pj.setTarget(defaultTarget);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(defaultTarget.equals(pj.getTarget()));
		
		// set the target to a different value and make
		// sure the bodies are awakened
		Vector2 target = new Vector2(1.0, 1.0);
		pj.setTarget(target);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertTrue(target.equals(pj.getTarget()));
		
		// set the target to the same value
		b1.setAtRest(true);
		pj.setTarget(target);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(target.equals(pj.getTarget()));
	}
	
	/**
	 * Tests get/set of the correction factor.
	 */
	@Test
	public void getSetCorrectionFactor() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		TestCase.assertEquals(0.3, pj.getCorrectionFactor());
		
		pj.setCorrectionFactor(0.0);
		TestCase.assertEquals(0.0, pj.getCorrectionFactor());
		
		pj.setCorrectionFactor(0.3);
		TestCase.assertEquals(0.3, pj.getCorrectionFactor());
		
		pj.setCorrectionFactor(1.0);
		TestCase.assertEquals(1.0, pj.getCorrectionFactor());
	}

	/**
	 * Tests a negative correction factor.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeCorrectionFactor() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setCorrectionFactor(-1.0);
	}
	
	/**
	 * Tests a correction factor greater than 1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setGreaterThan1CorrectionFactor() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setCorrectionFactor(5.0);
	}

	/**
	 * Tests a correction factor greater than 1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLessThanZeroCorrectionMaximumForce() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		pj.setMaximumCorrectionForce(-1.0);
	}

	/**
	 * Tests get/set of the correction maximum force.
	 */
	@Test
	public void getSetCorrectionMaximumForce() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2());
		TestCase.assertEquals(1000.0, pj.getMaximumCorrectionForce());
		
		pj.setMaximumCorrectionForce(0.0);
		TestCase.assertEquals(0.0, pj.getMaximumCorrectionForce());
		
		pj.setMaximumCorrectionForce(0.3);
		TestCase.assertEquals(0.3, pj.getMaximumCorrectionForce());
		
		pj.setMaximumCorrectionForce(100.0);
		TestCase.assertEquals(100.0, pj.getMaximumCorrectionForce());
	}

	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(5, 7));
		pj.setCollisionAllowed(true);
		pj.setOwner(new Object());
		pj.setUserData(new Object());
		pj.setMaximumSpringForce(3);
		pj.setMaximumSpringForceEnabled(true);
		pj.setSpringDamperEnabled(true);
		pj.setSpringDampingRatio(0.5);
		pj.setSpringEnabled(true);
		pj.setSpringFrequency(1.0);
		pj.setCorrectionFactor(0.5);
		pj.setMaximumCorrectionForce(4);
		pj.setTarget(2, 3);
		pj.bias.set(2, 5);
		pj.damping = 1;
		pj.gamma = 4;
		pj.impulse.set(3, 5);
		pj.K.m00 = 1;
		pj.K.m01 = 2;
		pj.K.m10 = 3;
		pj.K.m11 = 4;
		pj.linearError.set(2, 6);
		pj.r.set(3, 2);
		
		PinJoint<Body> pjc = pj.copy();
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertNotSame(pj.body, pjc.body);
		TestCase.assertNotSame(pj.localAnchor, pjc.localAnchor);
		TestCase.assertNotSame(pj.bias, pjc.bias);
		TestCase.assertNotSame(pj.impulse, pjc.impulse);
		TestCase.assertNotSame(pj.K, pjc.K);
		TestCase.assertNotSame(pj.linearError, pjc.linearError);
		TestCase.assertNotSame(pj.r, pjc.r);
		TestCase.assertNotSame(pj.target, pjc.target);
		TestCase.assertSame(pjc.body, pjc.bodies.get(0));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());
		TestCase.assertEquals(pj.localAnchor.x, pjc.localAnchor.x);
		TestCase.assertEquals(pj.localAnchor.y, pjc.localAnchor.y);
		TestCase.assertEquals(pj.target.x, pjc.target.x);
		TestCase.assertEquals(pj.target.y, pjc.target.y);
		TestCase.assertEquals(pj.K.m00, pjc.K.m00);
		TestCase.assertEquals(pj.K.m01, pjc.K.m01);
		TestCase.assertEquals(pj.K.m10, pjc.K.m10);
		TestCase.assertEquals(pj.K.m11, pjc.K.m11);
		TestCase.assertEquals(pj.bias.x, pjc.bias.x);
		TestCase.assertEquals(pj.bias.y, pjc.bias.y);
		TestCase.assertEquals(pj.impulse.x, pjc.impulse.x);
		TestCase.assertEquals(pj.impulse.y, pjc.impulse.y);
		TestCase.assertEquals(pj.linearError.x, pjc.linearError.x);
		TestCase.assertEquals(pj.linearError.y, pjc.linearError.y);
		TestCase.assertEquals(pj.r.x, pjc.r.x);
		TestCase.assertEquals(pj.r.y, pjc.r.y);
		
		TestCase.assertNull(pjc.owner);
		TestCase.assertNull(pjc.userData);
		
		TestCase.assertEquals(pj.collisionAllowed, pjc.collisionAllowed);
		TestCase.assertEquals(pj.correctionFactor, pjc.correctionFactor);
		TestCase.assertEquals(pj.correctionMaximumForce, pjc.correctionMaximumForce);
		TestCase.assertEquals(pj.damping, pjc.damping);
		TestCase.assertEquals(pj.gamma, pjc.gamma);
		TestCase.assertEquals(pj.springDamperEnabled, pjc.springDamperEnabled);
		TestCase.assertEquals(pj.springDampingRatio, pjc.springDampingRatio);
		TestCase.assertEquals(pj.springEnabled, pjc.springEnabled);
		TestCase.assertEquals(pj.springFrequency, pjc.springFrequency);
		TestCase.assertEquals(pj.springMaximumForce, pjc.springMaximumForce);
		TestCase.assertEquals(pj.springMaximumForceEnabled, pjc.springMaximumForceEnabled);
		TestCase.assertEquals(pj.springMode, pjc.springMode);
		TestCase.assertEquals(pj.springStiffness, pjc.springStiffness);
		
		// test overriding the body
		pjc = pj.copy(b1);
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertSame(pj.body, pjc.body);
		TestCase.assertSame(pjc.body, pjc.bodies.get(0));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());
		
		// test not overriding the body
		pjc = pj.copy(null);
		
		TestCase.assertNotSame(pj, pjc);
		TestCase.assertNotSame(pj.bodies, pjc.bodies);
		TestCase.assertNotSame(pj.body, pjc.body);
		TestCase.assertSame(pjc.body, pjc.bodies.get(0));
		TestCase.assertEquals(pj.bodies.size(), pjc.bodies.size());
	}
	
	/**
	 * Test the copy fail fast.
	 */
	@Test(expected = ClassCastException.class)
	public void copyFailed() {
		TestBody b1 = new TestBody();
		TestBody b2 = new TestBody();
		
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.copy();
	}
}
