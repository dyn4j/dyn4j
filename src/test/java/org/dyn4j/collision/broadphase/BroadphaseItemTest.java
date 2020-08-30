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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link BroadphaseItem} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class BroadphaseItemTest {
	/**
	 * Tests the create method.
	 */
	@Test
	public void create() {
		TestCollisionBody body = new TestCollisionBody();
		Fixture fixture = new Fixture(Geometry.createCircle(0.5));
		BroadphaseItem<TestCollisionBody, Fixture> item = new BroadphaseItem<TestCollisionBody, Fixture>(body, fixture);
		
		TestCase.assertEquals(body, item.getBody());
		TestCase.assertEquals(fixture, item.getFixture());
		
		TestCase.assertFalse(0 == item.hashCode());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		TestCollisionBody body = new TestCollisionBody();
		Fixture fixture = new Fixture(Geometry.createCircle(0.5));
		BroadphaseItem<TestCollisionBody, Fixture> item = new BroadphaseItem<TestCollisionBody, Fixture>(body, fixture);
		
		BroadphaseItem<TestCollisionBody, Fixture> copy = item.copy();
		
		TestCase.assertEquals(item.getBody(), copy.getBody());
		TestCase.assertEquals(item.getFixture(), copy.getFixture());
		TestCase.assertEquals(item.hashCode(), copy.hashCode());
		TestCase.assertFalse(item == copy);
		TestCase.assertTrue(item.equals(copy));
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		TestCollisionBody body = new TestCollisionBody();
		Fixture fixture = new Fixture(Geometry.createCircle(0.5));
		BroadphaseItem<TestCollisionBody, Fixture> item = new BroadphaseItem<TestCollisionBody, Fixture>(body, fixture);
		
		TestCase.assertNotNull(item.toString());
	}
}
