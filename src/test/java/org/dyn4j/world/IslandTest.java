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
public class IslandTest {
	/**
	 * Tests the successful creation of an island.
	 */
	@Test
	public void createSuccess() {
		Island<Body> island = new Island<Body>();
		TestCase.assertNotNull(island.bodies);
		TestCase.assertNotNull(island.contactConstraints);
		TestCase.assertNotNull(island.joints);
	}
	
	/**
	 * Tests the invalid constructor counts.
	 */
	@Test
	public void createInvalidInitialCapacity() {
		Island<Body> island = new Island<Body>(-1, 0);
		TestCase.assertNotNull(island.bodies);
		TestCase.assertNotNull(island.contactConstraints);
		TestCase.assertNotNull(island.joints);
	}
	
	/**
	 * Tests the add methods.
	 */
	@Test
	public void add() {
		Island<Body> i = new Island<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		AngleJoint<Body> j1 = new AngleJoint<Body>(b1, b2);
		ContactConstraint<Body> c1 = new ContactConstraint<Body>(new BasicCollisionPair<Body, BodyFixture>(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0))));
		
		i.add(b1);
		i.add(b2);
		i.add(j1);
		i.add(c1);
		
		TestCase.assertEquals(2, i.bodies.size());
		TestCase.assertEquals(1, i.joints.size());
		TestCase.assertEquals(1, i.contactConstraints.size());
		
		// the island doesn't do any duplicate checking
		
		i.add(b1);
		i.add(b2);
		i.add(j1);
		i.add(c1);
		
		TestCase.assertEquals(4, i.bodies.size());
		TestCase.assertEquals(2, i.joints.size());
		TestCase.assertEquals(2, i.contactConstraints.size());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Island<Body> i = new Island<Body>();
		
		i.add(new Body());
		i.add(new AngleJoint<Body>(new Body(), new Body()));
		i.add(new ContactConstraint<Body>(new BasicCollisionPair<Body, BodyFixture>(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)))));
		
		TestCase.assertEquals(1, i.bodies.size());
		TestCase.assertEquals(1, i.joints.size());
		TestCase.assertEquals(1, i.contactConstraints.size());
		
		i.clear();
		
		TestCase.assertEquals(0, i.bodies.size());
		TestCase.assertEquals(0, i.joints.size());
		TestCase.assertEquals(0, i.contactConstraints.size());
	}
}
