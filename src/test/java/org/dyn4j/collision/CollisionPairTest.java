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
package org.dyn4j.collision;

import java.util.HashMap;

import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.geometry.Geometry;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class CollisionPairTest {
	private CollisionBody<Fixture> cb1;
	private CollisionBody<Fixture> cb2;
	private CollisionBody<Fixture> cb3;
	
	private Fixture f1a;
	private Fixture f1b;
	private Fixture f2;
	private Fixture f3;
	
	private CollisionPair<CollisionBody<Fixture>, Fixture> pair_1a_to_2;
	private CollisionPair<CollisionBody<Fixture>, Fixture> pair_2_to_1a;
	private CollisionPair<CollisionBody<Fixture>, Fixture> pair_1b_to_2;
	private CollisionPair<CollisionBody<Fixture>, Fixture> pair_2_to_1b;
	private CollisionPair<CollisionBody<Fixture>, Fixture> pair_2_to_3;
	private CollisionPair<CollisionBody<Fixture>, Fixture> pair_3_to_2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.f1a = new Fixture(Geometry.createCircle(1.0));
		this.f1b = new Fixture(Geometry.createCircle(1.0));
		this.f2 = new Fixture(Geometry.createCircle(2.0));
		this.f3 = new Fixture(Geometry.createCircle(3.0));
		
		this.cb1 = new CollidableTest(this.f1a); cb1.addFixture(this.f1b);
		this.cb2 = new CollidableTest(this.f2);
		this.cb3 = new CollidableTest(this.f3);
		
		this.pair_1a_to_2 = new BroadphasePair<CollisionBody<Fixture>, Fixture>(this.cb1, this.f1a, this.cb2, this.f2);
		this.pair_2_to_1a = new BroadphasePair<CollisionBody<Fixture>, Fixture>(this.cb2, this.f2, this.cb1, this.f1a);
		this.pair_1b_to_2 = new BroadphasePair<CollisionBody<Fixture>, Fixture>(this.cb1, this.f1b, this.cb2, this.f2);
		this.pair_2_to_1b = new BroadphasePair<CollisionBody<Fixture>, Fixture>(this.cb2, this.f2, this.cb1, this.f1b);
		this.pair_2_to_3 = new BroadphasePair<CollisionBody<Fixture>, Fixture>(this.cb2, this.f2, this.cb3, this.f3);
		this.pair_3_to_2 = new BroadphasePair<CollisionBody<Fixture>, Fixture>(this.cb3, this.f3, this.cb2, this.f2);
	}
	
	@Test
	public void hashCodeTest() {
		// HashCode must be the same for the same input, but could be the same for different input
		// in this particular case, the hashcode for re-ordered body/fixtures should be the same
		int h1 = CollisionPair.getHashCode(this.cb1, this.f1a, this.cb2, this.f2);
		int h2 = CollisionPair.getHashCode(this.cb2, this.f2, this.cb1, this.f1a);
		
		TestCase.assertEquals(h1, h2);
		
		h2 = this.pair_1a_to_2.hashCode();
		
		TestCase.assertEquals(h1, h2);
		
		h2 = this.pair_2_to_1a.hashCode();
		
		TestCase.assertEquals(h1, h2);
	}
	
	@Test
	public void equalsTest() {
		TestCase.assertTrue(CollisionPair.equals(this.pair_1a_to_2, this.pair_1a_to_2));
		TestCase.assertTrue(CollisionPair.equals(this.pair_1a_to_2, this.pair_2_to_1a));
		
		TestCase.assertFalse(CollisionPair.equals(this.pair_1a_to_2, this.pair_1b_to_2));
		TestCase.assertFalse(CollisionPair.equals(this.pair_1b_to_2, this.pair_1a_to_2));
		
		TestCase.assertFalse(CollisionPair.equals(this.pair_1a_to_2, this.pair_2_to_1b));
		TestCase.assertFalse(CollisionPair.equals(this.pair_2_to_1b, this.pair_1a_to_2));
		
		TestCase.assertTrue(CollisionPair.equals(this.pair_2_to_3, this.pair_2_to_3));
		TestCase.assertTrue(CollisionPair.equals(this.pair_2_to_3, this.pair_3_to_2));
		TestCase.assertTrue(CollisionPair.equals(this.pair_3_to_2, this.pair_2_to_3));
		TestCase.assertTrue(CollisionPair.equals(this.pair_3_to_2, this.pair_3_to_2));
	}
	
	@Test
	public void mapTest() {
		HashMap<CollisionPair<CollisionBody<Fixture>, Fixture>, Object> map = new HashMap<CollisionPair<CollisionBody<Fixture>,Fixture>, Object>();
		
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		
		map.put(this.pair_1a_to_2, o1);
		map.put(this.pair_2_to_1b, o2);
		map.put(this.pair_2_to_3, o3);
		
		Object test = map.get(this.pair_1a_to_2);
		TestCase.assertEquals(o1, test);
		
		test = map.get(this.pair_1b_to_2);
		TestCase.assertEquals(o2, test);
		
		test = map.get(this.pair_2_to_1a);
		TestCase.assertEquals(o1, test);
		
		test = map.get(this.pair_2_to_1b);
		TestCase.assertEquals(o2, test);
		
		test = map.get(this.pair_2_to_3);
		TestCase.assertEquals(o3, test);
		
		test = map.get(this.pair_3_to_2);
		TestCase.assertEquals(o3, test);
	}
}
