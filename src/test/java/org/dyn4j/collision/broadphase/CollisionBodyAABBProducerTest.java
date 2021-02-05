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

import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link CollisionBodyAABBProducer} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class CollisionBodyAABBProducerTest {
	/**
	 * Tests null input to the producer.
	 */
	@Test(expected = NullPointerException.class)
	public void computeNull() {
		CollisionBodyAABBProducer<TestCollisionBody> producer = new CollisionBodyAABBProducer<TestCollisionBody>();
		producer.compute(null);
	}
	
	/**
	 * Tests null input to the producer.
	 */
	@Test(expected = NullPointerException.class)
	public void computeNullBody() {
		CollisionBodyAABBProducer<TestCollisionBody> producer = new CollisionBodyAABBProducer<TestCollisionBody>();
		producer.compute(null, new AABB(0,0,0,0));
	}
	
	/**
	 * Tests null input to the producer.
	 */
	@Test(expected = NullPointerException.class)
	public void computeNullResult() {
		CollisionBodyAABBProducer<TestCollisionBody> producer = new CollisionBodyAABBProducer<TestCollisionBody>();
		producer.compute(new TestCollisionBody(), null);
	}
	
	/**
	 * Tests null input to the producer.
	 */
	@Test(expected = NullPointerException.class)
	public void computeNullBoth() {
		CollisionBodyAABBProducer<TestCollisionBody> producer = new CollisionBodyAABBProducer<TestCollisionBody>();
		producer.compute(null, null);
	}
	
	/**
	 * Tests valid input to the producer.
	 */
	@Test
	public void computeSuccess() {
		CollisionBodyAABBProducer<TestCollisionBody> producer = new CollisionBodyAABBProducer<TestCollisionBody>();
		
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		AABB aabb1 = ct1.createAABB();
		AABB aabb2 = ct2.createAABB();
		
		AABB aabb = producer.compute(ct1);
		
		TestCase.assertEquals(aabb1.getMinX(), aabb.getMinX());
		TestCase.assertEquals(aabb1.getMinY(), aabb.getMinY());
		TestCase.assertEquals(aabb1.getMaxX(), aabb.getMaxX());
		TestCase.assertEquals(aabb1.getMaxY(), aabb.getMaxY());
		
		aabb = producer.compute(ct2);
		
		TestCase.assertEquals(aabb2.getMinX(), aabb.getMinX());
		TestCase.assertEquals(aabb2.getMinY(), aabb.getMinY());
		TestCase.assertEquals(aabb2.getMaxX(), aabb.getMaxX());
		TestCase.assertEquals(aabb2.getMaxY(), aabb.getMaxY());
		
		// test overwrite
		ct1.computeAABB(aabb1);
		producer.compute(ct1, aabb);
		
		TestCase.assertEquals(aabb1.getMinX(), aabb.getMinX());
		TestCase.assertEquals(aabb1.getMinY(), aabb.getMinY());
		TestCase.assertEquals(aabb1.getMaxX(), aabb.getMaxX());
		TestCase.assertEquals(aabb1.getMaxY(), aabb.getMaxY());
		
		// test overwrite empty
		producer.compute(new TestCollisionBody(), aabb);
		
		TestCase.assertEquals(0.0, aabb.getMinX());
		TestCase.assertEquals(0.0, aabb.getMinY());
		TestCase.assertEquals(0.0, aabb.getMaxX());
		TestCase.assertEquals(0.0, aabb.getMaxY());
	}
}
