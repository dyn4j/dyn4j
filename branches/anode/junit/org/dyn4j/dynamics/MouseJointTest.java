/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import junit.framework.TestCase;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link MouseJoint} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.0.2
 */
public class MouseJointTest {
	/** The body used for testing */
	private Body b;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.b = new Body();
	}
	
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a null target point.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullTarget() {
		new MouseJoint(b, null, 4.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a zero frequency.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroFrequency() {
		new MouseJoint(b, new Vector2(), 0.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a negative frequency.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeFrequency() {
		new MouseJoint(b, new Vector2(), -2.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a damping ratio greater than 1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createGreaterThan1DampingRatio() {
		new MouseJoint(b, new Vector2(), 4.0, 1.0001, 10.0);
	}
	
	/**
	 * Tests the create method passing a negative damping ratio.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeDampingRatio() {
		new MouseJoint(b, new Vector2(), 4.0, -0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a negative damping ratio.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeMaxForce() {
		new MouseJoint(b, new Vector2(), 4.0, 0.4, -10.0);
	}
	
	/**
	 * Tests setting a valid target.
	 */
	@Test
	public void setTarget() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setTarget(new Vector2());
		mj.setTarget(new Vector2(2.0, 1.032));
	}
	
	/**
	 * Tests setting a null target.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTarget() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setTarget(null);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaxForce() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setMaximumForce(0.0);
		mj.setMaximumForce(10.0);
		mj.setMaximumForce(2548.0);
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxForce() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setMaximumForce(-2.0);
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setDampingRatio() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setDampingRatio(0.0);
		mj.setDampingRatio(1.0);
		mj.setDampingRatio(0.2);
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setGreaterThan1DampingRatio() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setFrequency() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setFrequency(1.0);
		mj.setFrequency(29.0);
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setFrequency(-0.3);
	}
	
	/**
	 * Tests a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroFrequency() {
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setFrequency(0.0);
	}

	/**
	 * Tests the shiftCoordinates method.
	 * @since 3.1.0
	 */
	@Test
	public void shiftCoordinates() {
		World w = new World();
		MouseJoint mj = new MouseJoint(b, new Vector2(), 4.0, 0.4, 10.0);
		mj.setTarget(new Vector2(1.0, -1.0));
		
		w.addJoint(mj);
		w.shiftCoordinates(new Vector2(-1.0, 2.0));
		
		TestCase.assertEquals(0.0, mj.getTarget().x, 1.0e-3);
		TestCase.assertEquals(1.0, mj.getTarget().y, 1.0e-3);
	}
	
	/**
	 * Tests the MouseJoint with a body who has FIXED_LINEAR_VELOCITY as its
	 * mass type.  The mouse joint applied at a point on the body should rotate
	 * the body (before it wasn't doing anything).
	 */
	@Test
	public void fixedLinearVelocity() {
		World w = new World();
		
		Body body = new Body();
		body.addFixture(Geometry.createCircle(1.0));
		body.setMass(Mass.Type.FIXED_LINEAR_VELOCITY);
		w.addBody(body);
		
		MouseJoint mj = new MouseJoint(body, new Vector2(0.5, 0.0), 8.0, 0.3, 1000.0);
		w.addJoint(mj);
		
		mj.setTarget(new Vector2(0.7, -0.5));
		
		w.step(1);
		
		TestCase.assertTrue(mj.getReactionForce(w.step.invdt).getMagnitude() > 0);
		TestCase.assertTrue(mj.getReactionForce(w.step.invdt).getMagnitude() <= 1000.0);
		TestCase.assertTrue(body.getTransform().getRotation() < 0);
	}
}
