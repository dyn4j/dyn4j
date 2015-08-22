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

import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

/**
 * Test case for the {@link RopeJoint} class.
 * @author William Bittle
 * @version 3.0.1
 * @since 2.2.2
 */
public class RopeJointTest {
	/**
	 * Tests the successful creation of an rope joint.
	 */
	@Test
	public void createSuccess() {
		new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createFail1() {
		new RopeJoint(null, new Body(), new Vector2(), new Vector2());
	}

	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createFail2() {
		new RopeJoint(new Body(), null, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createFail3() {
		new RopeJoint(new Body(), new Body(), null, new Vector2());
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = NullPointerException.class)
	public void createFail4() {
		new RopeJoint(new Body(), new Body(), new Vector2(), null);
	}
	
	/**
	 * Tests the failed creation of an rope joint.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createFail5() {
		Body b = new Body();
		new RopeJoint(b, b, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the successful setting of the maximum distance.
	 */
	@Test
	public void setMaximum() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setUpperLimit(10);
		
		TestCase.assertEquals(10.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumFail1() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setUpperLimit(-10);
	}

	/**
	 * Tests the failed setting of the maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMaximumFail2() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLowerLimit(2);
		rj.setUpperLimit(1);
	}
	
	/**
	 * Tests the successful setting of the minimum distance.
	 */
	@Test
	public void setMinimum() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setUpperLimit(10);
		rj.setLowerLimit(2);
		
		TestCase.assertEquals(2.0, rj.getLowerLimit());
	}
	
	/**
	 * Tests the failed setting of the maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinimumFail1() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setUpperLimit(-3);
	}
	
	/**
	 * Tests the failed setting of the maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinimumFail2() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setUpperLimit(1);
		rj.setLowerLimit(2);
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum distance.
	 */
	@Test
	public void setMinAndMax() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(7, 10);
		
		TestCase.assertEquals(7.0, rj.getLowerLimit());
		TestCase.assertEquals(10.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinAndMaxFail1() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(7, -10);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinAndMaxFail2() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(-7, 10);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinAndMaxFail3() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(-7, -10);
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinAndMaxFail4() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(10, 7);
	}
	
	/**
	 * Tests the successful setting of the minimum and maximum distance.
	 */
	@Test
	public void setMinMax() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(10);
		
		TestCase.assertEquals(10.0, rj.getLowerLimit());
		TestCase.assertEquals(10.0, rj.getUpperLimit());
	}
	
	/**
	 * Tests the failed setting of the minimum and maximum distance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setMinMaxFail1() {
		RopeJoint rj = new RopeJoint(new Body(), new Body(), new Vector2(), new Vector2());
		rj.setLimits(-10);
	}
}
