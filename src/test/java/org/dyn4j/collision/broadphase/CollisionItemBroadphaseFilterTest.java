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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link CollisionItemBroadphaseFilter} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class CollisionItemBroadphaseFilterTest {
	/**
	 * Tests valid input to the filter.
	 */
	@Test
	public void isAllowed() {
		CollisionItemBroadphaseFilter<TestCollisionBody, Fixture> filter = new CollisionItemBroadphaseFilter<TestCollisionBody, Fixture>();
		
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct2.addFixture(Geometry.createCircle(0.5));
		
		CollisionItem<TestCollisionBody, Fixture> item1 = new BasicCollisionItem<TestCollisionBody, Fixture>(ct1, ct1.getFixture(0));
		CollisionItem<TestCollisionBody, Fixture> item2 = new BasicCollisionItem<TestCollisionBody, Fixture>(ct2, ct2.getFixture(0));
		CollisionItem<TestCollisionBody, Fixture> item3 = new BasicCollisionItem<TestCollisionBody, Fixture>(ct2, ct2.getFixture(1));
		
		TestCase.assertFalse(filter.isAllowed(null, null));
		TestCase.assertTrue(filter.isAllowed(item1, item2));
		
		TestCase.assertTrue(filter.isAllowed(item1, item2));
		TestCase.assertTrue(filter.isAllowed(item2, item1));
		TestCase.assertTrue(filter.isAllowed(item1, item3));
		
		TestCase.assertFalse(filter.isAllowed(item1, item1));
		TestCase.assertFalse(filter.isAllowed(item2, item2));
		TestCase.assertFalse(filter.isAllowed(item2, item3));
	}
}
