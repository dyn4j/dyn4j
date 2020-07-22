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
 * Test case for the {@link BroadphasePair} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class BroadphasePairTest {
	/**
	 * Tests the create method.
	 */
	@Test
	public void create() {
		TestCollisionBody body1 = new TestCollisionBody();
		Fixture fixture1 = new Fixture(Geometry.createCircle(0.5));
		TestCollisionBody body2 = new TestCollisionBody();
		Fixture fixture2 = new Fixture(Geometry.createCircle(0.5));
		
		BroadphasePair<TestCollisionBody, Fixture> pair = new BroadphasePair<TestCollisionBody, Fixture>(body1, fixture1, body2, fixture2);
		
		TestCase.assertEquals(body1, pair.getBody1());
		TestCase.assertEquals(fixture1, pair.getFixture1());
		TestCase.assertEquals(body2, pair.getBody2());
		TestCase.assertEquals(fixture2, pair.getFixture2());
		
		TestCase.assertEquals(body1, pair.getBody(body1));
		TestCase.assertEquals(body2, pair.getBody(body2));
		TestCase.assertEquals(fixture1, pair.getFixture(body1));
		TestCase.assertEquals(fixture2, pair.getFixture(body2));
		
		TestCase.assertEquals(body2, pair.getOtherBody(body1));
		TestCase.assertEquals(body1, pair.getOtherBody(body2));
		TestCase.assertEquals(fixture2, pair.getOtherFixture(body1));
		TestCase.assertEquals(fixture1, pair.getOtherFixture(body2));
		
		TestCollisionBody body3 = new TestCollisionBody();
		
		TestCase.assertEquals(null, pair.getBody(body3));
		TestCase.assertEquals(null, pair.getFixture(body3));
		TestCase.assertEquals(null, pair.getOtherBody(body3));
		TestCase.assertEquals(null, pair.getOtherFixture(body3));
		
		TestCase.assertEquals(null, pair.getBody(null));
		TestCase.assertEquals(null, pair.getFixture(null));
		TestCase.assertEquals(null, pair.getOtherBody(null));
		TestCase.assertEquals(null, pair.getOtherFixture(null));
		
		TestCase.assertFalse(0 == pair.hashCode());
	}
	
	/**
	 * Tests the hashCode and equals methods.
	 */
	@Test
	public void hashcodeEquals() {
		TestCollisionBody body1 = new TestCollisionBody();
		Fixture fixture1 = new Fixture(Geometry.createCircle(0.5));
		TestCollisionBody body2 = new TestCollisionBody();
		Fixture fixture2 = new Fixture(Geometry.createCircle(0.5));
		
		BroadphasePair<TestCollisionBody, Fixture> pair1 = new BroadphasePair<TestCollisionBody, Fixture>(body1, fixture1, body2, fixture2);
		BroadphasePair<TestCollisionBody, Fixture> pair2 = new BroadphasePair<TestCollisionBody, Fixture>(body2, fixture2, body1, fixture1);
		BroadphasePair<TestCollisionBody, Fixture> pair3 = new BroadphasePair<TestCollisionBody, Fixture>(body1, fixture1, body1, fixture1);
		
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
		Fixture fixture1 = new Fixture(Geometry.createCircle(0.5));
		TestCollisionBody body2 = new TestCollisionBody();
		Fixture fixture2 = new Fixture(Geometry.createCircle(0.5));
		
		BroadphasePair<TestCollisionBody, Fixture> pair = new BroadphasePair<TestCollisionBody, Fixture>(body1, fixture1, body2, fixture2);
		BroadphasePair<TestCollisionBody, Fixture> copy = pair.copy();
		
		TestCase.assertEquals(pair.getBody1(), copy.getBody1());
		TestCase.assertEquals(pair.getFixture1(), copy.getFixture1());
		TestCase.assertEquals(pair.getBody2(), copy.getBody2());
		TestCase.assertEquals(pair.getFixture2(), copy.getFixture2());
		TestCase.assertEquals(pair.hashCode(), copy.hashCode());
		TestCase.assertFalse(pair == copy);
		TestCase.assertTrue(pair.equals(copy));
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		TestCollisionBody body1 = new TestCollisionBody();
		Fixture fixture1 = new Fixture(Geometry.createCircle(0.5));
		TestCollisionBody body2 = new TestCollisionBody();
		Fixture fixture2 = new Fixture(Geometry.createCircle(0.5));
		
		BroadphasePair<TestCollisionBody, Fixture> pair = new BroadphasePair<TestCollisionBody, Fixture>(body1, fixture1, body2, fixture2);
		
		TestCase.assertNotNull(pair.toString());
	}
}
