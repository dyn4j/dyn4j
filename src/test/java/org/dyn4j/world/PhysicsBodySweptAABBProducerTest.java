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
package org.dyn4j.world;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link PhysicsBodySweptAABBProducer} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class PhysicsBodySweptAABBProducerTest {
	/**
	 * Tests sweep into new AABB
	 */
	@Test
	public void sweepIntoNewAABB() {
		PhysicsBodySweptAABBProducer<Body> aabbProducer = new PhysicsBodySweptAABBProducer<Body>();
		
		Body b1 = new Body();
		b1.addFixture(Geometry.createCircle(0.5));
		b1.setMass(MassType.NORMAL);
		b1.translate(1.0, 0.0);
		
		AABB aabb = aabbProducer.compute(b1);
		
		TestCase.assertNotNull(aabb);
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		TestCase.assertEquals(1.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
		
		b1.translate(1.0, 1.0);
		aabb = aabbProducer.compute(b1);
		
		TestCase.assertNotNull(aabb);
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		TestCase.assertEquals(2.5, aabb.getMaxX());
		TestCase.assertEquals(1.5, aabb.getMaxY());
		
		b1.getPreviousTransform().set(b1.getTransform());
		aabb = aabbProducer.compute(b1);
		
		TestCase.assertNotNull(aabb);
		TestCase.assertEquals(1.5, aabb.getMinX());
		TestCase.assertEquals(0.5, aabb.getMinY());
		TestCase.assertEquals(2.5, aabb.getMaxX());
		TestCase.assertEquals(1.5, aabb.getMaxY());
	}
	
	/**
	 * Tests sweep into existing AABB
	 */
	@Test
	public void sweepIntoExistingAABB() {
		PhysicsBodySweptAABBProducer<Body> aabbProducer = new PhysicsBodySweptAABBProducer<Body>();
		
		Body b1 = new Body();
		b1.addFixture(Geometry.createCircle(0.5));
		b1.setMass(MassType.NORMAL);
		b1.translate(1.0, 0.0);
		
		AABB aabb = new AABB(0,0,0,0);
		
		aabbProducer.compute(b1, aabb);
		
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		TestCase.assertEquals(1.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
		
		b1.translate(1.0, 1.0);
		aabbProducer.compute(b1, aabb);
		
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		TestCase.assertEquals(2.5, aabb.getMaxX());
		TestCase.assertEquals(1.5, aabb.getMaxY());
		
		b1.getPreviousTransform().set(b1.getTransform());
		aabbProducer.compute(b1, aabb);
		
		TestCase.assertEquals(1.5, aabb.getMinX());
		TestCase.assertEquals(0.5, aabb.getMinY());
		TestCase.assertEquals(2.5, aabb.getMaxX());
		TestCase.assertEquals(1.5, aabb.getMaxY());
	}
	
	/**
	 * Tests no sweep
	 */
	@Test
	public void noSweep() {
		PhysicsBodySweptAABBProducer<Body> aabbProducer = new PhysicsBodySweptAABBProducer<Body>();
		
		Body b1 = new Body();
		b1.addFixture(Geometry.createCircle(0.5));
		b1.setMass(MassType.NORMAL);
		
		AABB aabb = aabbProducer.compute(b1);
		
		TestCase.assertNotNull(aabb);
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		TestCase.assertEquals(0.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
	}
	
	/**
	 * Tests no fixtures.
	 */
	@Test
	public void zero() {
		PhysicsBodySweptAABBProducer<Body> aabbProducer = new PhysicsBodySweptAABBProducer<Body>();
		
		Body b1 = new Body();
		
		AABB aabb = aabbProducer.compute(b1);
		
		TestCase.assertNotNull(aabb);
		TestCase.assertEquals(0.0, aabb.getMinX());
		TestCase.assertEquals(0.0, aabb.getMinY());
		TestCase.assertEquals(0.0, aabb.getMaxX());
		TestCase.assertEquals(0.0, aabb.getMaxY());
	}
}
