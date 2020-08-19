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
package org.dyn4j.world;

import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Contains the test cases for the {@link World} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class WorldTest {
	/**
	 * Tests the creation of world collision data when given a valid pair.
	 */
	@Test
	public void createCollisionData() {
		Body b1 = new Body();
		Body b2 = new Body();
		BodyFixture f1 = b1.addFixture(Geometry.createCircle(0.5));
		BodyFixture f2 = b2.addFixture(Geometry.createCircle(0.5));
		
		World<Body> w = new World<Body>();
		WorldCollisionData<Body> data = w.createCollisionData(new BasicCollisionPair<Body, BodyFixture>(b1, f1, b2, f2));
		
		TestCase.assertEquals(b1, data.getBody1());
		TestCase.assertEquals(b2, data.getBody2());
		TestCase.assertEquals(f1, data.getFixture1());
		TestCase.assertEquals(f2, data.getFixture2());
		TestCase.assertNotNull(data.getPair());
		TestCase.assertNotNull(data.getPenetration());
		TestCase.assertNotNull(data.getManifold());
		TestCase.assertNotNull(data.getContactConstraint());
		TestCase.assertFalse(data.isBroadphaseCollision());
		TestCase.assertFalse(data.isManifoldCollision());
		TestCase.assertFalse(data.isNarrowphaseCollision());
		TestCase.assertFalse(data.isContactConstraintCollision());
	}
}
