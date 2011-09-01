/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link RevoluteJoint} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.2
 */
public class RevoluteJointTest {
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
		new RevoluteJoint(b1, b2, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor() {
		new RevoluteJoint(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSameBody() {
		new RevoluteJoint(b1, b1, new Vector2());
	}
	
	/**
	 * Tests valid maximum motor torque values.
	 */
	@Test
	public void setMaxMotorTorque() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setMaxMotorTorque(0.0);
		rj.setMaxMotorTorque(4.0);
		rj.setMaxMotorTorque(1000.0);
	}
	
	/**
	 * Tests a negative maximum motor torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxMotorTorque() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setMaxMotorTorque(-2.0);
	}
	
	/**
	 * Tests valid lower limit values.
	 */
	@Test
	public void setLowerLimit() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setUpperLimit(2.0);
		rj.setLowerLimit(0.0);
		rj.setLowerLimit(1.0);
		rj.setLowerLimit(-2.3);
	}
	
	/**
	 * Tests an invalid lower limit value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidLowerLimit() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setUpperLimit(2.0);
		rj.setLowerLimit(3.0);
	}
	
	/**
	 * Tests valid upper limit values.
	 */
	@Test
	public void setUpperLimit() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setLowerLimit(-3.0);
		rj.setUpperLimit(-2.0);
		rj.setUpperLimit(0.0);
		rj.setUpperLimit(3.0);
	}
	
	/**
	 * Tests an invalid upper limit value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidUpperLimit() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setLowerLimit(-3.0);
		rj.setUpperLimit(-4.0);
	}
	
	/**
	 * Tests valid limit values.
	 */
	@Test
	public void setLimits() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setLowerLimit(-3.0);
		rj.setUpperLimit(-2.0);
		rj.setUpperLimit(0.0);
		rj.setUpperLimit(3.0);
	}
	
	/**
	 * Tests invalid limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidLimits1() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setLimits(-2.0, -3.0);
	}
	
	/**
	 * Tests invalid limits.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidLimits2() {
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		rj.setLimits(3.0, 2.0);
	}
}
