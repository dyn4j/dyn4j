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
package org.dyn4j.collision;

import java.util.HashMap;

import org.dyn4j.geometry.Geometry;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test cases for pair hashcode/equals.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class AbstractCollisionItemTest {
	private TestCollisionBody cb1;
	private TestCollisionBody cb2;
	
	private Fixture f1a;
	private Fixture f1b;
	private Fixture f2;
	
	private CollisionItem<TestCollisionBody, Fixture> item1a;
	private CollisionItem<TestCollisionBody, Fixture> item1b;
	private CollisionItem<TestCollisionBody, Fixture> item2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.f1a = new Fixture(Geometry.createCircle(1.0));
		this.f1b = new Fixture(Geometry.createCircle(1.0));
		this.f2 = new Fixture(Geometry.createCircle(2.0));
		
		this.cb1 = new TestCollisionBody(this.f1a); 
		this.cb1.addFixture(this.f1b);
		this.cb2 = new TestCollisionBody(this.f2);
		
		this.item1a = new BasicCollisionItem<TestCollisionBody, Fixture>(this.cb1, this.f1a);
		this.item1b = new BasicCollisionItem<TestCollisionBody, Fixture>(this.cb1, this.f1b);
		this.item2 = new BasicCollisionItem<TestCollisionBody, Fixture>(this.cb2, this.f2);
	}
	
	/**
	 * Tests the hashcode method.
	 */
	@Test
	public void hashCodeTest() {
		// HashCode must be the same for the same input, but could be the same for different input
		int h1 = AbstractCollisionItem.getHashCode(this.cb1, this.f1a);
		int h2 = AbstractCollisionItem.getHashCode(this.cb1, this.f1a);
		
		TestCase.assertEquals(h1, h2);
		
		h1 = AbstractCollisionItem.getHashCode(this.cb2, this.f1b);
		h2 = AbstractCollisionItem.getHashCode(this.cb2, this.f1b);
		
		TestCase.assertEquals(h1, h2);
		
		h1 = AbstractCollisionItem.getHashCode(this.cb1, this.f1a);
		h2 = AbstractCollisionItem.getHashCode(this.cb1, this.f1b);
		
		TestCase.assertFalse(h1 == h2);
		
		h1 = AbstractCollisionItem.getHashCode(this.cb1, this.f1a);
		h2 = AbstractCollisionItem.getHashCode(this.cb2, this.f2);
		
		TestCase.assertFalse(h1 == h2);
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void equalsTest() {
		TestCase.assertTrue(AbstractCollisionItem.equals(this.item1a, this.item1a));
		TestCase.assertTrue(AbstractCollisionItem.equals(this.item1b, this.item1b));
		TestCase.assertTrue(AbstractCollisionItem.equals(this.item2, this.item2));
		
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item1a, this.item1b));
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item1a, this.item2));
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item1b, this.item2));
		
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item1b, this.item1a));
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item2, this.item1a));
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item2, this.item1b));
		
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item2, null));
		TestCase.assertFalse(AbstractCollisionItem.equals(null, this.item2));
		TestCase.assertTrue(AbstractCollisionItem.equals(null, null));
		
		TestCase.assertFalse(AbstractCollisionItem.equals(this.item2, new Object()));
		TestCase.assertFalse(AbstractCollisionItem.equals(null, new Object()));
	}
	
	/**
	 * Tests the hashcode/equals method in a standard Map implementation.
	 */
	@Test
	public void mapTest() {
		HashMap<CollisionItem<TestCollisionBody, Fixture>, Object> map = new HashMap<CollisionItem<TestCollisionBody, Fixture>, Object>();
		
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		
		map.put(this.item1a, o1);
		map.put(this.item1b, o2);
		map.put(this.item2, o3);
		
		Object test = map.get(this.item1a);
		TestCase.assertEquals(o1, test);
		
		test = map.get(this.item1b);
		TestCase.assertEquals(o2, test);
		
		test = map.get(this.item2);
		TestCase.assertEquals(o3, test);
	}
}
