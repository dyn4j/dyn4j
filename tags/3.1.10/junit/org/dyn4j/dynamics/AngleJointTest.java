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

import org.dyn4j.dynamics.joint.AngleJoint;
import org.junit.Test;

/**
 * Test case for the {@link AngleJoint} class.
 * @author William Bittle
 * @version 3.0.1
 * @since 2.2.2
 */
public class AngleJointTest {
	/**
	 * Tests the successful creation of an angle joint.
	 */
	@Test
	public void createSuccess() {
		new AngleJoint(new Body(), new Body());
	}
	
	/**
	 * Tests the failed creation of an angle joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createFail1() {
		new AngleJoint(null, new Body());
	}

	/**
	 * Tests the failed creation of an angle joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createFail2() {
		new AngleJoint(new Body(), null);
	}
	
	/**
	 * Tests the failed creation of an angle joint.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createFail3() {
		Body b = new Body();
		new AngleJoint(b, b);
	}
	
	/**
	 * Tests the successful setting of the maximum angle.
	 */
	@Test
	public void setMaximum() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setUpperLimit(Math.toRadians(10));
		
		TestCase.assertEquals(Math.toRadians(10), aj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumFail() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setUpperLimit(Math.toRadians(-10));
	}
	
	/**
	 * Tests the successful setting of the minimum angle.
	 */
	@Test
	public void setMinimum() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setLowerLimit(Math.toRadians(-10));
		
		TestCase.assertEquals(Math.toRadians(-10), aj.getLowerLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinimumFail() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setLowerLimit(Math.toRadians(10));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setMinAndMax() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setLimits(Math.toRadians(-30), Math.toRadians(20));
		
		TestCase.assertEquals(Math.toRadians(-30), aj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(20), aj.getUpperLimit(), 1e-6);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum angle.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinAndMaxFail() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setLimits(Math.toRadians(30), Math.toRadians(20));
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum angle.
	 */
	@Test
	public void setMinMax() {
		AngleJoint aj = new AngleJoint(new Body(), new Body());
		aj.setLimits(Math.toRadians(30));
		
		TestCase.assertEquals(Math.toRadians(30), aj.getLowerLimit(), 1e-6);
		TestCase.assertEquals(Math.toRadians(30), aj.getUpperLimit(), 1e-6);
	}
}
