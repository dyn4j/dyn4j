/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link PulleyJoint} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 2.1.0
 */
public class PulleyJointTest {
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
		new PulleyJoint(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor1() {
		new PulleyJoint(b1, b2, null, new Vector2(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor2() {
		new PulleyJoint(b1, b2, new Vector2(), null, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor3() {
		new PulleyJoint(b1, b2, new Vector2(), new Vector2(), null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor4() {
		new PulleyJoint(b1, b2, new Vector2(), new Vector2(), new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSameBody() {
		new PulleyJoint(b1, b1, new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the setRatio method.
	 */
	@Test
	public void setRatio() {
		PulleyJoint pj = new PulleyJoint(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		pj.setRatio(2.0);
		TestCase.assertEquals(2.0, pj.getRatio());
	}
	
	/**
	 * Tests the setRatio method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setRatioNegative() {
		PulleyJoint pj = new PulleyJoint(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		pj.setRatio(-1.0);
		TestCase.assertEquals(2.0, pj.getRatio());
	}
	
	/**
	 * Tests the setRatio method passing a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setRatioZero() {
		PulleyJoint pj = new PulleyJoint(b1, b2, new Vector2(), new Vector2(), new Vector2(), new Vector2());
		pj.setRatio(0.0);
	}
	
	/**
	 * Tests the shiftCoordinates method.
	 * @since 3.1.0
	 */
	@Test
	public void shiftCoordinates() {
		World w = new World();
		
		PulleyJoint pj = new PulleyJoint(b1, b2, new Vector2(1.0, 0.0), new Vector2(-1.0, 1.0), new Vector2(), new Vector2());
		
		w.addJoint(pj);
		w.shift(new Vector2(-1.0, 2.0));
		
		TestCase.assertEquals( 0.0, pj.getPulleyAnchor1().x, 1.0e-3);
		TestCase.assertEquals( 2.0, pj.getPulleyAnchor1().y, 1.0e-3);
		TestCase.assertEquals(-2.0, pj.getPulleyAnchor2().x, 1.0e-3);
		TestCase.assertEquals( 3.0, pj.getPulleyAnchor2().y, 1.0e-3);
	}
}
