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

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

/**
 * Class to test the {@link Constraint} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class ConstraintTest {
	/**
	 * Class for testing the {@link Constraint} classes methods.
	 * @author William Bittle
	 * @version 3.1.1
	 * @since 3.1.1
	 */
	private class TestConstraint extends Constraint {
		/**
		 * Full constructor.
		 * @param b1 the first body
		 * @param b2 the second body
		 */
		public TestConstraint(Body b1, Body b2) {
			super(b1, b2);
		}
		@Override
		protected void shiftCoordinates(Vector2 shift) {}
	}
	
	/**
	 * Tests the get/set user data.
	 */
	@Test
	public void getUserData() {
		String obj = "hello";
		Body b1 = new Body();
		Body b2 = new Body();
		TestConstraint tc = new TestConstraint(b1, b2);
		
		TestCase.assertNull(tc.getUserData());
		
		tc.setUserData(obj);
		TestCase.assertNotNull(tc.getUserData());
		TestCase.assertSame(obj, tc.getUserData());
	}
}
