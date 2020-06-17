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
package org.dyn4j.dynamics;

import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Island;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Island} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.1.1
 */
@Deprecated
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IslandTest {
	/**
	 * Tests the successful creation of an island.
	 */
	@Test
	public void createSuccess() {
		Island island = new Island();
		island = new Island(Capacity.DEFAULT_CAPACITY);
		
		TestCase.assertNotNull(island.bodies);
		TestCase.assertNotNull(island.contactConstraints);
		TestCase.assertNotNull(island.joints);
	}
	
	/**
	 * Tests the failed creation of an island.
	 */
	public void createSuccessNullCapacity() {
		new Island(null);
	}

	/**
	 * Tests the add methods.
	 */
	@Test
	public void add() {
		Island i = new Island();
		
		i.add(new Body());
		i.add(new AngleJoint(new Body(), new Body()));
		i.add(new ContactConstraint(new BroadphasePair(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0))), 
				new Manifold(), 0, 0));
		
		TestCase.assertEquals(1, i.bodies.size());
		TestCase.assertEquals(1, i.joints.size());
		TestCase.assertEquals(1, i.contactConstraints.size());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Island i = new Island();
		
		i.add(new Body());
		i.add(new AngleJoint(new Body(), new Body()));
		i.add(new ContactConstraint(new BroadphasePair(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0))), 
				new Manifold(), 0, 0));
		
		TestCase.assertEquals(1, i.bodies.size());
		TestCase.assertEquals(1, i.joints.size());
		TestCase.assertEquals(1, i.contactConstraints.size());
		
		i.clear();
		
		TestCase.assertEquals(0, i.bodies.size());
		TestCase.assertEquals(0, i.joints.size());
		TestCase.assertEquals(0, i.contactConstraints.size());
	}
}
