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

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link FrictionJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class FrictionJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		new FrictionJoint<Body>(b1, b2, new Vector2());
	}
	
	/**
	 * Tests the failed creation with a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new FrictionJoint<Body>(null, b2, new Vector2());
	}

	/**
	 * Tests the failed creation with a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new FrictionJoint<Body>(b1, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new FrictionJoint<Body>(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new FrictionJoint<Body>(b1, b1, new Vector2());
	}
	
	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaximumTorque() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		
		fj.setMaximumTorque(0.0);
		TestCase.assertEquals(0.0, fj.getMaximumTorque());
		
		fj.setMaximumTorque(10.0);
		TestCase.assertEquals(10.0, fj.getMaximumTorque());
		
		fj.setMaximumTorque(2548.0);
		TestCase.assertEquals(2548.0, fj.getMaximumTorque());
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumTorque() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		fj.setMaximumTorque(-2.0);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaximumForce() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		
		fj.setMaximumForce(0.0);
		TestCase.assertEquals(0.0, fj.getMaximumForce());
		
		fj.setMaximumForce(10.0);
		TestCase.assertEquals(10.0, fj.getMaximumForce());
		
		fj.setMaximumForce(2548.0);
		TestCase.assertEquals(2548.0, fj.getMaximumForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumForce() {
		FrictionJoint<Body> fj = new FrictionJoint<Body>(b1, b2, new Vector2());
		fj.setMaximumForce(-2.0);
	}
}
