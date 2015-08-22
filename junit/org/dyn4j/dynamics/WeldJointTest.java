/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link WeldJoint} class.
 * @author William Bittle
 * @version 3.0.2
 * @since 1.0.2
 */
public class WeldJointTest {
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
		new WeldJoint(b1, b2, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor() {
		new WeldJoint(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSameBody() {
		new WeldJoint(b1, b1, new Vector2());
	}
	
	/**
	 * Tests the isSpring method.
	 * @since 3.0.2
	 */
	@Test
	public void isSpring() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		TestCase.assertFalse(wj.isSpring());
		
		wj.setFrequency(0.0);
		TestCase.assertFalse(wj.isSpring());
		
		wj.setFrequency(1.0);
		TestCase.assertTrue(wj.isSpring());
		
		wj.setFrequency(15.24);
		TestCase.assertTrue(wj.isSpring());
		
		wj.setFrequency(0.0);
		TestCase.assertFalse(wj.isSpring());
	}

	/**
	 * Tests the isSpringDamper method.
	 * @since 3.0.2
	 */
	@Test
	public void isSpringDamper() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		TestCase.assertFalse(wj.isSpringDamper());
		
		wj.setFrequency(0.0);
		TestCase.assertFalse(wj.isSpringDamper());
		
		wj.setFrequency(1.0);
		TestCase.assertFalse(wj.isSpringDamper());
		
		wj.setFrequency(15.24);
		TestCase.assertFalse(wj.isSpringDamper());
		
		wj.setDampingRatio(0.4);
		TestCase.assertTrue(wj.isSpringDamper());
		
		wj.setDampingRatio(0.0);
		TestCase.assertFalse(wj.isSpringDamper());
		
		wj.setDampingRatio(0.61);
		wj.setFrequency(0.0);
		TestCase.assertFalse(wj.isSpringDamper());
	}
	
	/**
	 * Tests valid damping ratio values.
	 * @since 3.0.2
	 */
	@Test
	public void setDampingRatio() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		wj.setDampingRatio(0.0);
		wj.setDampingRatio(1.0);
		wj.setDampingRatio(0.2);
	}
	
	/**
	 * Tests a negative damping ratio value.
	 * @since 3.0.2
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		wj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 * @since 3.0.2
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setGreaterThan1DampingRatio() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		wj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 * @since 3.0.2
	 */
	@Test
	public void setFrequency() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		wj.setFrequency(0.0);
		wj.setFrequency(1.0);
		wj.setFrequency(29.0);
	}
	
	/**
	 * Tests a negative frequency value.
	 * @since 3.0.2
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		WeldJoint wj = new WeldJoint(b1, b2, new Vector2());
		wj.setFrequency(-0.3);
	}
}
