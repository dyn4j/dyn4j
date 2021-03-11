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
public class DistanceJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
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
	 * Tests the isSpring method.
	 */
	@Test
	public void isSpring() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpringEnabled());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringEnabled());
		
		dj.setFrequency(1.0);
		TestCase.assertTrue(dj.isSpringEnabled());
		
		dj.setFrequency(15.24);
		TestCase.assertTrue(dj.isSpringEnabled());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringEnabled());
	}

	/**
	 * Tests the isSpringDamper method.
	 */
	@Test
	public void isSpringDamper() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setFrequency(1.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setFrequency(15.24);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setDampingRatio(0.4);
		TestCase.assertTrue(dj.isSpringDamperEnabled());
		
		dj.setDampingRatio(0.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
		
		dj.setDampingRatio(0.61);
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringDamperEnabled());
	}
	
	/**
	 * Tests valid distance values.
	 */
	@Test
	public void setPositiveDistance() {
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
	public void setNegativeDistance() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setRestDistance(-2.0);
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setDampingRatio() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(0.0);
		TestCase.assertEquals(0.0, dj.getDampingRatio());
		
		dj.setDampingRatio(1.0);
		TestCase.assertEquals(1.0, dj.getDampingRatio());
		
		dj.setDampingRatio(0.2);
		TestCase.assertEquals(0.2, dj.getDampingRatio());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setFrequency() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setFrequency(0.0);
		TestCase.assertEquals(0.0, dj.getFrequency());
		
		dj.setFrequency(1.0);
		TestCase.assertEquals(1.0, dj.getFrequency());
		
		dj.setFrequency(29.0);
		TestCase.assertEquals(29.0, dj.getFrequency());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		DistanceJoint<Body> dj = new DistanceJoint<Body>(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setFrequency(-0.3);
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
}
