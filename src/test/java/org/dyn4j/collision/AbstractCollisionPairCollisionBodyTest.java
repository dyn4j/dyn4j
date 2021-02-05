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

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test cases for pair hashcode/equals.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class AbstractCollisionPairCollisionBodyTest {
	private TestCollisionBody cb1;
	private TestCollisionBody cb2;
	private TestCollisionBody cb3;
	
	private CollisionPair<TestCollisionBody> pair_1_to_2;
	private CollisionPair<TestCollisionBody> pair_1_to_3;
	private CollisionPair<TestCollisionBody> pair_2_to_1;
	private CollisionPair<TestCollisionBody> pair_2_to_3;
	private CollisionPair<TestCollisionBody> pair_3_to_1;
	private CollisionPair<TestCollisionBody> pair_3_to_2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.cb1 = new TestCollisionBody();
		this.cb2 = new TestCollisionBody();
		this.cb3 = new TestCollisionBody();
		
		this.pair_1_to_2 = new BasicCollisionPair<TestCollisionBody>(this.cb1, this.cb2);
		this.pair_1_to_3 = new BasicCollisionPair<TestCollisionBody>(this.cb1, this.cb3);
		this.pair_2_to_1 = new BasicCollisionPair<TestCollisionBody>(this.cb2, this.cb1);
		this.pair_2_to_3 = new BasicCollisionPair<TestCollisionBody>(this.cb2, this.cb3);
		this.pair_3_to_1 = new BasicCollisionPair<TestCollisionBody>(this.cb3, this.cb1);
		this.pair_3_to_2 = new BasicCollisionPair<TestCollisionBody>(this.cb3, this.cb2);
		
	}
	
	/**
	 * Tests the hashcode method.
	 */
	@Test
	public void hashCodeTest() {
		// HashCode must be the same for the same input, but could be the same for different input
		// in this particular case, the hashcode for re-ordered body/fixtures should be the same
		int h1 = AbstractCollisionPair.getHashCode(this.cb1, this.cb2);
		int h2 = AbstractCollisionPair.getHashCode(this.cb2, this.cb1);
		
		TestCase.assertEquals(h1, h2);
		
		h2 = this.pair_1_to_2.hashCode();
		
		TestCase.assertEquals(h1, h2);
		
		h2 = this.pair_2_to_1.hashCode();
		
		TestCase.assertEquals(h1, h2);
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void equalsTest() {
		// 1 vs 2
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_1_to_2, this.pair_1_to_2));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_1_to_2, this.pair_2_to_1));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_2_to_1, this.pair_1_to_2));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_2_to_1, this.pair_2_to_1));
		
		// 2 vs 3
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_2_to_3, this.pair_2_to_3));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_2_to_3, this.pair_3_to_2));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_3_to_2, this.pair_2_to_3));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_3_to_2, this.pair_3_to_2));
		
		// 1 vs 3
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_1_to_3, this.pair_1_to_3));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_1_to_3, this.pair_3_to_1));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_3_to_1, this.pair_1_to_3));
		TestCase.assertTrue(AbstractCollisionPair.equals(this.pair_3_to_1, this.pair_3_to_1));
		
		// null
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_1_to_2, null));
		TestCase.assertFalse(AbstractCollisionPair.equals(null, this.pair_1_to_2));
		TestCase.assertTrue(AbstractCollisionPair.equals(null, null));
		
		// object
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_1_to_2, new Object()));
		TestCase.assertFalse(AbstractCollisionPair.equals(null, new Object()));
		
		// falsey
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_1_to_2, this.pair_2_to_3));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_1_to_2, this.pair_3_to_2));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_1_to_2, this.pair_1_to_3));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_1_to_2, this.pair_3_to_1));
		
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_2_to_3, this.pair_1_to_2));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_2_to_3, this.pair_2_to_1));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_2_to_3, this.pair_1_to_3));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_2_to_3, this.pair_3_to_1));
		
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_3_to_1, this.pair_2_to_3));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_3_to_1, this.pair_3_to_2));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_3_to_1, this.pair_1_to_2));
		TestCase.assertFalse(AbstractCollisionPair.equals(this.pair_3_to_1, this.pair_2_to_1));
	}
	
	/**
	 * Tests the hashcode/equals method in a standard Map implementation.
	 */
	@Test
	public void mapTest() {
		HashMap<CollisionPair<TestCollisionBody>, Object> map = new HashMap<CollisionPair<TestCollisionBody>, Object>();
		
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		
		map.put(this.pair_1_to_2, o1);
		map.put(this.pair_2_to_3, o2);
		map.put(this.pair_1_to_3, o3);
		
		Object test = map.get(this.pair_1_to_2);
		TestCase.assertEquals(o1, test);
		
		test = map.get(this.pair_2_to_3);
		TestCase.assertEquals(o2, test);
		
		test = map.get(this.pair_2_to_1);
		TestCase.assertEquals(o1, test);
		
		test = map.get(this.pair_3_to_2);
		TestCase.assertEquals(o2, test);
		
		test = map.get(this.pair_3_to_1);
		TestCase.assertEquals(o3, test);
		
		test = map.get(this.pair_1_to_3);
		TestCase.assertEquals(o3, test);
	}
}
