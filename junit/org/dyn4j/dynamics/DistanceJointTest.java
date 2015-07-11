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

import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link DistanceJoint} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.2
 */
public class DistanceJointTest {
	/** The first body used for testing */
	private Body b1;
	
	/** The second body used for testing */
	private Body b2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.b1 = new Body();
		this.b2 = new Body();
	}
	
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new DistanceJoint(b1, b2, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor1.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor1() {
		new DistanceJoint(b1, b2, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor2.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor2() {
		new DistanceJoint(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSameBody() {
		new DistanceJoint(b1, b1, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the isSpring method.
	 */
	@Test
	public void isSpring() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpring());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpring());
		
		dj.setFrequency(1.0);
		TestCase.assertTrue(dj.isSpring());
		
		dj.setFrequency(15.24);
		TestCase.assertTrue(dj.isSpring());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpring());
	}

	/**
	 * Tests the isSpringDamper method.
	 */
	@Test
	public void isSpringDamper() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setFrequency(1.0);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setFrequency(15.24);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setDampingRatio(0.4);
		TestCase.assertTrue(dj.isSpringDamper());
		
		dj.setDampingRatio(0.0);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setDampingRatio(0.61);
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringDamper());
	}
	
	/**
	 * Tests valid distance values.
	 */
	@Test
	public void setDistance() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDistance(0.0);
		dj.setDistance(1.0);
	}
	
	/**
	 * Tests a negative distance value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDistance() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDistance(-2.0);
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setDampingRatio() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(0.0);
		dj.setDampingRatio(1.0);
		dj.setDampingRatio(0.2);
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setGreaterThan1DampingRatio() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setFrequency() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setFrequency(0.0);
		dj.setFrequency(1.0);
		dj.setFrequency(29.0);
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setFrequency(-0.3);
	}
}
