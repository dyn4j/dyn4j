/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link BasicCollisionPair} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class BasicCollisionPairCollisionBodyTest {
	/**
	 * Tests the create method.
	 */
	@Test
	public void create() {
		TestCollisionBody body1 = new TestCollisionBody();
		TestCollisionBody body2 = new TestCollisionBody();
		
		BasicCollisionPair<TestCollisionBody> pair = new BasicCollisionPair<TestCollisionBody>(body1, body2);
		
		TestCase.assertEquals(body1, pair.getFirst());
		TestCase.assertEquals(body2, pair.getSecond());
		
		TestCase.assertFalse(0 == pair.hashCode());
	}
	
	/**
	 * Tests the hashCode and equals methods.
	 */
	@Test
	public void hashcodeEquals() {
		TestCollisionBody body1 = new TestCollisionBody();
		TestCollisionBody body2 = new TestCollisionBody();
		
		BasicCollisionPair<TestCollisionBody> pair1 = new BasicCollisionPair<TestCollisionBody>(body1, body2);
		BasicCollisionPair<TestCollisionBody> pair2 = new BasicCollisionPair<TestCollisionBody>(body2, body1);
		BasicCollisionPair<TestCollisionBody> pair3 = new BasicCollisionPair<TestCollisionBody>(body1, body1);
		
		TestCase.assertEquals(pair1.hashCode(), pair2.hashCode());
		TestCase.assertTrue(pair1.equals(pair1));
		TestCase.assertTrue(pair1.equals(pair2));
		TestCase.assertFalse(pair1.equals(pair3));
		TestCase.assertFalse(pair3.hashCode() == pair2.hashCode());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		TestCollisionBody body1 = new TestCollisionBody();
		TestCollisionBody body2 = new TestCollisionBody();
		
		BasicCollisionPair<TestCollisionBody> pair = new BasicCollisionPair<TestCollisionBody>(body1, body2);
		
		BasicCollisionPair<TestCollisionBody> copy = pair.copy();
		
		TestCase.assertEquals(pair.getFirst(), copy.getFirst());
		TestCase.assertEquals(pair.getSecond(), copy.getSecond());
		TestCase.assertEquals(pair.hashCode(), copy.hashCode());
		TestCase.assertFalse(pair == copy);
		TestCase.assertTrue(pair.equals(copy));
	}
}
