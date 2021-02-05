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
 * Used to test the {@link PinJoint} class.
 * @author William Bittle
 * @version 4.0.1
 * @since 1.0.2
 */
public class PinJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a null body.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody() {
		new PinJoint<Body>(null, new Vector2(), 4.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a null target point.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullTarget() {
		new PinJoint<Body>(b1, null, 4.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a zero frequency.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithZeroFrequency() {
		new PinJoint<Body>(b1, new Vector2(), 0.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a negative frequency.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithNegativeFrequency() {
		new PinJoint<Body>(b1, new Vector2(), -2.0, 0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a damping ratio greater than 1.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithGreaterThan1DampingRatio() {
		new PinJoint<Body>(b1, new Vector2(), 4.0, 1.0001, 10.0);
	}
	
	/**
	 * Tests the create method passing a negative damping ratio.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithNegativeDampingRatio() {
		new PinJoint<Body>(b1, new Vector2(), 4.0, -0.4, 10.0);
	}
	
	/**
	 * Tests the create method passing a negative damping ratio.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithNegativeMaximumForce() {
		new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, -10.0);
	}
	
	/**
	 * Tests setting a valid target.
	 */
	@Test
	public void setTarget() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		
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
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setTarget(null);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaximumForce() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		
		pj.setMaximumForce(0.0);
		TestCase.assertEquals(0.0, pj.getMaximumForce());
		
		pj.setMaximumForce(10.0);
		TestCase.assertEquals(10.0, pj.getMaximumForce());
		
		pj.setMaximumForce(2548.0);
		TestCase.assertEquals(2548.0, pj.getMaximumForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumForce() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setMaximumForce(-2.0);
	}

	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setDampingRatio() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setDampingRatio(0.0);
		TestCase.assertEquals(0.0, pj.getDampingRatio());
		
		pj.setDampingRatio(1.0);
		TestCase.assertEquals(1.0, pj.getDampingRatio());
		
		pj.setDampingRatio(0.2);
		TestCase.assertEquals(0.2, pj.getDampingRatio());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setFrequency() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		
		pj.setFrequency(0.1);
		TestCase.assertEquals(0.1, pj.getFrequency());
		
		pj.setFrequency(1.0);
		TestCase.assertEquals(1.0, pj.getFrequency());
		
		pj.setFrequency(29.0);
		TestCase.assertEquals(29.0, pj.getFrequency());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setFrequency(-0.3);
	}

	/**
	 * Tests a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroFrequency() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		pj.setFrequency(0.0);
	}

	/**
	 * Tests the shift method.
	 * @since 3.1.0
	 */
	@Test
	public void shiftCoordinates() {
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
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
		PinJoint<Body> pj = new PinJoint<Body>(b1, new Vector2(), 4.0, 0.4, 10.0);
		
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
}
