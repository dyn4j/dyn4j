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
 * Used to test the {@link DistanceJoint} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 1.0.2
 */
public class DistanceJointTest extends BaseJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		Vector2 p1 = new Vector2(1.0, 2.0);
		Vector2 p2 = new Vector2(-2.0, 5.0);
		double d = p1.distance(p2);
		
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, p1, p2);
		
		TestCase.assertEquals(p1, dj.getAnchor1());
		TestCase.assertEquals(p2, dj.getAnchor2());
		TestCase.assertNotSame(p1, dj.getAnchor1());
		TestCase.assertNotSame(p2, dj.getAnchor2());
		
		TestCase.assertEquals(d, dj.getRestDistance());
		TestCase.assertEquals(d, dj.getCurrentDistance());
		
		TestCase.assertEquals(0.3, dj.getSpringDampingRatio());
		TestCase.assertEquals(8.0, dj.getSpringFrequency());
		TestCase.assertEquals(1000.0, dj.getMaximumSpringForce());
		TestCase.assertEquals(0.0, dj.getSpringStiffness());
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, dj.getSpringMode());
		
		TestCase.assertEquals(d, dj.getLowerLimit());
		TestCase.assertEquals(d, dj.getUpperLimit());
		
		TestCase.assertEquals(b1, dj.getBody1());
		TestCase.assertEquals(b2, dj.getBody2());
		
		TestCase.assertEquals(null, dj.getOwner());
		TestCase.assertEquals(null, dj.getUserData());
		TestCase.assertEquals(b2, dj.getOtherBody(b1));
		
		TestCase.assertEquals(false, dj.isCollisionAllowed());
		TestCase.assertEquals(false, dj.isLowerLimitEnabled());
		TestCase.assertEquals(false, dj.isUpperLimitEnabled());
		TestCase.assertEquals(false, dj.isSpringDamperEnabled());
		TestCase.assertEquals(false, dj.isSpringEnabled());
		TestCase.assertEquals(false, dj.isMaximumSpringForceEnabled());
		
		TestCase.assertNotNull(dj.toString());
	}

	/**
	 * Tests the failed creation passing null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new DistanceJoint<Body>(null, b2, new Vector2(), new Vector2());
	}

	/**
	 * Tests the failed creation passing null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new DistanceJoint<Body>(b1, null, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor1Point() {
		new DistanceJoint<Body>(b1, b2, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor2Point() {
		new DistanceJoint<Body>(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new DistanceJoint<Body>(b1, b1, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		TestCase.assertEquals(1.0, dj.getAnchor1().x);
		TestCase.assertEquals(2.0, dj.getAnchor1().y);
		TestCase.assertEquals(-3.0, dj.getAnchor2().x);
		TestCase.assertEquals(0.5, dj.getAnchor2().y);
		
		dj.shift(new Vector2(1.0, 3.0));
		
		// nothing should have changed
		TestCase.assertEquals(1.0, dj.getAnchor1().x);
		TestCase.assertEquals(2.0, dj.getAnchor1().y);
		TestCase.assertEquals(-3.0, dj.getAnchor2().x);
		TestCase.assertEquals(0.5, dj.getAnchor2().y);
	}
	
	/**
	 * Tests the isSpringEnabled method.
	 */
	@Test
	public void isSpringEnabled() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpringEnabled());
		
		dj.setSpringFrequency(1.0);
		TestCase.assertFalse(dj.isSpringEnabled());
		
		dj.setSpringFrequency(100.0);
		TestCase.assertFalse(dj.isSpringEnabled());
		
		dj.setSpringEnabled(true);
		TestCase.assertTrue(dj.isSpringEnabled());
		
		dj.setSpringEnabled(false);
		TestCase.assertFalse(dj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamperEnabled method.
	 */
	@Test
	public void isSpringDamperEnabled() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setSpringFrequency(1.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setSpringFrequency(100.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setSpringDampingRatio(0.4);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setSpringDampingRatio(1.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setSpringEnabled(false);
		dj.setSpringDamperEnabled(true);
		TestCase.assertTrue(dj.isSpringDamperEnabled());
		
		dj.setSpringEnabled(true);
		dj.setSpringDamperEnabled(true);
		TestCase.assertTrue(dj.isSpringDamperEnabled());
		
		dj.setSpringEnabled(true);
		dj.setSpringDamperEnabled(false);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
	}
	
	/**
	 * Tests valid distance values.
	 */
	@Test
	public void setPositiveRestDistance() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setRestDistance(0.0);
		TestCase.assertEquals(0.0, dj.getRestDistance());
		
		dj.setRestDistance(1.0);
		TestCase.assertEquals(1.0, dj.getRestDistance());
	}
	
	/**
	 * Tests a negative distance value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeRestDistance() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setRestDistance(-2.0);
	}

	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setSpringDampingRatio() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setSpringDampingRatio(0.0);
		TestCase.assertEquals(0.0, dj.getSpringDampingRatio());
		
		dj.setSpringDampingRatio(0.001);
		TestCase.assertEquals(0.001, dj.getSpringDampingRatio());
		
		dj.setSpringDampingRatio(1.0);
		TestCase.assertEquals(1.0, dj.getSpringDampingRatio());
		
		dj.setSpringDampingRatio(0.2);
		TestCase.assertEquals(0.2, dj.getSpringDampingRatio());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		dj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		dj.setSpringDamperEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		dj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		dj.setSpringDampingRatio(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		dj.setSpringDampingRatio(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setSpringDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setSpringDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringFrequency() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setSpringFrequency(0.0);
		TestCase.assertEquals(0.0, dj.getSpringFrequency());
		
		dj.setSpringFrequency(0.001);
		TestCase.assertEquals(0.001, dj.getSpringFrequency());
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, dj.getSpringMode());
		
		dj.setSpringFrequency(1.0);
		TestCase.assertEquals(1.0, dj.getSpringFrequency());
		
		dj.setSpringFrequency(29.0);
		TestCase.assertEquals(29.0, dj.getSpringFrequency());
		
		// at rest testing
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// the spring isn't enabled so it shouldn't wake the bodies
		dj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// enabling the spring should wake the bodies
		dj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		dj.setSpringFrequency(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// the frequency is changing, they should wake
		dj.setSpringFrequency(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// this should wake the bodies
		dj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests the spring mode changing.
	 */
	@Test
	public void setSpringMode() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		// test mode swapping
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, dj.getSpringMode());
		dj.setSpringStiffness(0.3);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, dj.getSpringMode());
		dj.setSpringFrequency(0.5);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, dj.getSpringMode());
	}
	
	/**
	 * Tests a negative stiffness value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringStiffnessNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setSpringStiffness(-0.3);
	}

	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setSpringStiffness() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setSpringStiffness(0.0);
		TestCase.assertEquals(0.0, dj.getSpringStiffness());
		
		dj.setSpringStiffness(0.001);
		TestCase.assertEquals(0.001, dj.getSpringStiffness());
		
		dj.setSpringStiffness(1.0);
		TestCase.assertEquals(1.0, dj.getSpringStiffness());
		
		dj.setSpringStiffness(29.0);
		TestCase.assertEquals(29.0, dj.getSpringStiffness());
		
		// at rest testing
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// the spring isn't enabled so it shouldn't wake the bodies
		dj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// enabling the spring should wake the bodies
		dj.setSpringEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// if the spring frequency doesn't change, then the bodies should
		// state at rest
		dj.setSpringStiffness(3.0);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// the frequency is changing, they should wake
		dj.setSpringStiffness(5.0);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);

		// this should wake the bodies
		dj.setSpringDamperEnabled(true);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringFrequencyNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setSpringFrequency(-0.3);
	}

	/**
	 * Tests setting a negative maximum force.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSpringMaximumForceNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setMaximumSpringForce(-1.0);
	}
	
	/**
	 * Tests setting the maximum force.
	 */
	@Test
	public void setSpringMaximumForce() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setMaximumSpringForce(0.0);
		TestCase.assertEquals(0.0, dj.getMaximumSpringForce());
		
		dj.setMaximumSpringForce(0.001);
		TestCase.assertEquals(0.001, dj.getMaximumSpringForce());
		
		dj.setMaximumSpringForce(1.0);
		TestCase.assertEquals(1.0, dj.getMaximumSpringForce());
		
		dj.setMaximumSpringForce(1000);
		TestCase.assertEquals(1000.0, dj.getMaximumSpringForce());

		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake them because its not enabled
		dj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// this won't wake the bodies because the spring isn't enabled
		dj.setMaximumSpringForceEnabled(true);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());

		// enable the spring
		dj.setSpringEnabled(true);
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this won't wake the bodies because it's the same value
		dj.setMaximumSpringForce(0.5);
		TestCase.assertTrue(this.b1.isAtRest());
		TestCase.assertTrue(this.b2.isAtRest());
		
		// this should wake them
		dj.setMaximumSpringForce(0.6);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
		
		this.b1.setAtRest(true);
		this.b2.setAtRest(true);
		
		// this should wake them
		dj.setMaximumSpringForceEnabled(false);
		TestCase.assertFalse(this.b1.isAtRest());
		TestCase.assertFalse(this.b2.isAtRest());
	}
	
	/**
	 * Tests spring stiffness/frequency calculations
	 */
	@Test
	public void computeSpringStiffnessFrequency() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setSpringEnabled(true);
		dj.setSpringDamperEnabled(true);
		dj.setSpringFrequency(8.0);
		dj.setSpringDampingRatio(0.5);
		
		dj.updateSpringCoefficients();
		
		TestCase.assertEquals(8.0, dj.springFrequency);
		TestCase.assertEquals(0.5, dj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_FREQUENCY, dj.getSpringMode());
		TestCase.assertEquals(3968.803, dj.springStiffness, 1e-3);
		
		dj.setSpringStiffness(1000.0);
		dj.updateSpringCoefficients();
		
		TestCase.assertEquals(4.015, dj.springFrequency, 1e-3);
		TestCase.assertEquals(0.5, dj.springDampingRatio);
		TestCase.assertEquals(AbstractJoint.SPRING_MODE_STIFFNESS, dj.getSpringMode());
		TestCase.assertEquals(1000.0, dj.springStiffness, 1e-3);
	}
	
	/**
	 * Tests the body's sleep state when changing the distance.
	 */
	@Test
	public void setDistanceAtRest() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		double distance = dj.getRestDistance();
		
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(distance, dj.getRestDistance());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the distance to the same value
		dj.setRestDistance(distance);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(distance, dj.getRestDistance());
		
		// set the distance to a different value and make
		// sure the bodies are awakened
		dj.setRestDistance(10);
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(10.0, dj.getRestDistance());
	}

	/**
	 * Tests the successful setting of the upper limit.
	 */
	@Test
	public void setUpperLimitSuccess() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setUpperLimit(2.0);
		
		TestCase.assertEquals(2.0, dj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the upper limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setUpperLimit(-1.0);
	}
	
	/**
	 * Tests the failed setting of the upper limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperLimitInvalid() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setUpperLimit(0.5);
	}
	
	/**
	 * Tests the successful setting of the lower limit.
	 */
	@Test
	public void setLowerLimit() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		
		dj.setLowerLimit(0.0);
		
		TestCase.assertEquals(0.0, dj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the lower limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		
		dj.setLowerLimit(-1.0);
	}

	/**
	 * Tests the failed setting of the lower limit.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLowerLimitInvalid() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		
		dj.setLowerLimit(1.5);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setUpperAndLowerLimits() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setLimits(0.0, 1.0);		
		TestCase.assertEquals(0.0, dj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(1.0, dj.getUpperLimit(), 1e-6);
		
		dj.setLimits(1.0, 2.0);		
		TestCase.assertEquals(1.0, dj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(2.0, dj.getUpperLimit(), 1e-6);
		
		dj.setLimits(1.0, 1.0);
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsInvalid() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setLimits(1.0, 0.0);
	}

	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setLimits(-1.0, 0.0);
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setUpperAndLowerLimitsNegative2() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setLimits(0.0, -1.0);
	}
	
	/**
	 * Tests the failed setting of the lower and upper limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSameLimitNegative() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setLimits(-1.0);
	}
	
	/**
	 * Tests the successful setting of the lower and upper limits.
	 */
	@Test
	public void setSameLimitValid() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		dj.setLimits(2.0);
		
		TestCase.assertEquals(2.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when enabling/disabling the limits.
	 */
	@Test
	public void setLimitsEnabledSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// lets disable it first and ensure that the bodies are awake
		dj.setLimitsEnabled(false);
		TestCase.assertFalse(dj.isUpperLimitEnabled());
		TestCase.assertFalse(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// if we disable it again, the bodies should not wake
		dj.setLimitsEnabled(false);
		TestCase.assertFalse(dj.isUpperLimitEnabled());
		TestCase.assertFalse(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// when we enable it, we should awake the bodies
		dj.setLimitsEnabled(true);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		
		// if we enable it when it's already enabled and the bodies are asleep
		// it should not wake the bodies
		b1.setAtRest(true);
		b2.setAtRest(true);
		dj.setLimitsEnabled(true);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		
		// if we disable the limit, then the bodies should be reawakened
		dj.setLimitsEnabled(false);
		TestCase.assertFalse(dj.isUpperLimitEnabled());
		TestCase.assertFalse(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to the same value.
	 */
	@Test
	public void setLimitsSameSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = dj.getLowerLimit();
		double defaultUpperLimit = dj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		dj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		dj.setLimits(2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		dj.setLowerLimit(0.0);
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimits(2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		dj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, dj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimits(2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		dj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimits(1.0);
		TestCase.assertFalse(dj.isUpperLimitEnabled());
		TestCase.assertFalse(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits to different values.
	 */
	@Test
	public void setLimitsDifferentSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = dj.getLowerLimit();
		double defaultUpperLimit = dj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// the bodies should remain asleep
		dj.setLimits(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		dj.setLimits(2.0, 3.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, dj.getLowerLimit());
		TestCase.assertEquals(3.0, dj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		dj.setLowerLimit(1.0);
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimits(0.0, 3.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		TestCase.assertEquals(3.0, dj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		dj.setUpperLimit(2.0);
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimits(0.0, 1.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
		
		// now disable the limit, and the limits should change
		// but the bodies should not wake
		dj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimits(1.0, 2.0);
		TestCase.assertFalse(dj.isUpperLimitEnabled());
		TestCase.assertFalse(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the lower limit.
	 */
	@Test
	public void setLowerLimitSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = dj.getLowerLimit();
		double defaultUpperLimit = dj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the lower limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		dj.setLowerLimit(defaultLowerLimit);
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		dj.setLowerLimit(0.5);
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.5, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// now disable the limit, and the lower limit should change
		// but the bodies should not wake
		dj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLowerLimit(0.2);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(0.2, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the upper limit.
	 */
	@Test
	public void setUpperLimitSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = dj.getLowerLimit();
		double defaultUpperLimit = dj.getUpperLimit();

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the upper limit to the current value - since the value hasn't changed
		// the bodies should remain asleep
		dj.setUpperLimit(defaultUpperLimit);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// set the limit to a different value - the bodies should wake up
		dj.setUpperLimit(2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// now disable the limit, and the upper limit should change
		// but the bodies should not wake
		dj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setUpperLimit(3.0);
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(3.0, dj.getUpperLimit());
	}
	
	/**
	 * Tests the sleep interaction when changing the limits and enabling them.
	 */
	@Test
	public void setLimitsEnabledSameSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = dj.getLowerLimit();
		double defaultUpperLimit = dj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// the limit should already be enabled and the value isn't changing
		// so the bodies should not wake
		dj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// the limit should already be enabled and the value is changing
		// so the bodies should wake
		dj.setLimitsEnabled(2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(2.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		b1.setAtRest(true);
		b2.setAtRest(true);
		dj.setLimitsEnabled(false);
		
		// the limit is not enabled but the value isn't changing
		// so the bodies should still wake
		dj.setLimitsEnabled(1.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(1.0, dj.getLowerLimit());
		TestCase.assertEquals(1.0, dj.getUpperLimit());
	}

	/**
	 * Tests the sleep interaction when changing the limits to different values and enabling them.
	 */
	@Test
	public void setLimitsEnabledDifferentSleep() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2(0.0, 1.0));
		
		dj.setLowerLimitEnabled(true);
		dj.setUpperLimitEnabled(true);
		
		// by default the limit is enabled
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		
		// the default upper and lower limits should be equal
		double defaultLowerLimit = dj.getLowerLimit();
		double defaultUpperLimit = dj.getUpperLimit();
		TestCase.assertEquals(defaultLowerLimit, defaultUpperLimit);

		// the bodies should be initially awake
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());

		// then put the bodies to sleep
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		// set the limits to the current value - since the value hasn't changed
		// and the limit is already enabled the bodies should remain asleep
		dj.setLimitsEnabled(defaultLowerLimit, defaultUpperLimit);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertTrue(b1.isAtRest());
		TestCase.assertTrue(b2.isAtRest());
		TestCase.assertEquals(defaultLowerLimit, dj.getLowerLimit());
		TestCase.assertEquals(defaultUpperLimit, dj.getUpperLimit());
		
		// set the limits to a different value - the bodies should wake up
		dj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// test the scenario where only the lower limit value changes
		dj.setLowerLimit(0.5);
		TestCase.assertEquals(0.5, dj.getLowerLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// test the scenario where only the upper limit value changes
		dj.setUpperLimit(3.0);
		TestCase.assertEquals(3.0, dj.getUpperLimit());
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimitsEnabled(0.0, 2.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.0, dj.getLowerLimit());
		TestCase.assertEquals(2.0, dj.getUpperLimit());
		
		// now disable the limit and make sure they wake
		// even though the limits don't change
		dj.setLimitsEnabled(false);
		b1.setAtRest(true);
		b2.setAtRest(true);
		
		dj.setLimitsEnabled(0.5, 4.0);
		TestCase.assertTrue(dj.isUpperLimitEnabled());
		TestCase.assertTrue(dj.isLowerLimitEnabled());
		TestCase.assertFalse(b1.isAtRest());
		TestCase.assertFalse(b2.isAtRest());
		TestCase.assertEquals(0.5, dj.getLowerLimit());
		TestCase.assertEquals(4.0, dj.getUpperLimit());
	}
}
