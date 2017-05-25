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

import org.junit.Test;

/**
 * Test case for the {@link Capacity} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class CapacityTest {
	/**
	 * Tests the successful creation of a capacity.
	 */
	@Test
	public void createSuccess() {
		new Capacity(0, 0, 0);
		new Capacity(10, 6, 2);
	}
	
	/**
	 * Tests the defaults.
	 */
	public void defualtTest() {
		Capacity c = new Capacity();
		TestCase.assertEquals(Capacity.DEFAULT_BODY_COUNT, c.getBodyCount());
		TestCase.assertEquals(Capacity.DEFAULT_JOINT_COUNT, c.getJointCount());
		TestCase.assertEquals(Capacity.DEFAULT_LISTENER_COUNT, c.getListenerCount());
	}
	
	/**
	 * Tests the failed creation of a capacity by passing a negative
	 * body count.
	 */
	public void createFailureBodyCount() {
		new Capacity(-3, 0, 0);
	}
	
	/**
	 * Tests the failed creation of a capacity by passing a negative
	 * joint count.
	 */
	public void createFailureJointCount() {
		new Capacity(0, -3, 0);
	}
	
	/**
	 * Tests the failed creation of a capacity by passing a negative
	 * listener count.
	 */
	public void createFailureListenerCount() {
		new Capacity(0, 0, -3);
	}
}
