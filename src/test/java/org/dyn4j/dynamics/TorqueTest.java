/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link Torque} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.2
 */
public class TorqueTest {
	/**
	 * Tests successful creation.
	 */
	@Test
	public void createSuccess() {
		Torque t = new Torque();
		t = new Torque(0.2);
		TestCase.assertEquals(0.2, t.torque);
		TestCase.assertEquals(0.2, t.getTorque());
		
		t = new Torque(-3.5);
		TestCase.assertEquals(-3.5, t.torque);
		TestCase.assertEquals(-3.5, t.getTorque());
		
		Torque t2 = new Torque(t);
		TestCase.assertEquals(-3.5, t2.torque);
		TestCase.assertEquals(-3.5, t.getTorque());
	}
	
	/**
	 * Tests the set methods successfully.
	 */
	@Test
	public void setSuccess() {
		Torque t = new Torque();
		t.set(0.32);
		TestCase.assertEquals(0.32, t.torque);
		TestCase.assertEquals(0.32, t.getTorque());
		
		Torque t2 = new Torque(3.42);
		t.set(t2);
		TestCase.assertEquals(3.42, t.torque);
		TestCase.assertEquals(3.42, t.getTorque());
	}
	
	/**
	 * Tests creation using a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullTorque() {
		new Torque(null);
	}
	
	/**
	 * Tests setting the torque using a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTorque() {
		Torque t = new Torque();
		t.set(null);
	}

	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		Torque t = new Torque();
		TestCase.assertNotNull(t.toString());
		
		t.set(0.32);
		TestCase.assertNotNull(t.toString());
	}

	/**
	 * Tests the default isComplete method.
	 */
	@Test
	public void isComplete() {
		Torque t = new Torque();
		
		// by default it should be true
		TestCase.assertTrue(t.isComplete(0.0));
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Torque to = new Torque(5);
		Torque tc = to.copy();
		
		TestCase.assertNotSame(to, tc);
		TestCase.assertEquals(to.torque, tc.torque);
	}
}
