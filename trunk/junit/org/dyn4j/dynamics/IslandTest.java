/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

/**
 * Test case for the {@link Island} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class IslandTest {
	/**
	 * Tests the successful creation of an island.
	 */
	@Test
	public void createSuccess() {
		World w = new World();
		Island island = new Island(w);
		island = new Island(w, Capacity.DEFAULT_CAPACITY);
		
		TestCase.assertNotNull(island.bodies);
		TestCase.assertNotNull(island.world);
		TestCase.assertNotNull(island.contactConstraints);
		TestCase.assertNotNull(island.contactConstraintSolver);
		TestCase.assertNotNull(island.joints);
		TestCase.assertEquals(w, island.world);
	}
	
	/**
	 * Tests the failed creation of an island.
	 */
	@Test(expected = NullPointerException.class)
	public void createFailNullWorld() {
		new Island(new World(), null);
	}

	/**
	 * Tests the failed creation of an island.
	 */
	@Test(expected = NullPointerException.class)
	public void createFailNullCapacity() {
		new Island(null, Capacity.DEFAULT_CAPACITY);
	}
	
	/**
	 * Tests the add methods.
	 */
	@Test
	public void add() {
		World w = new World();
		Island i = new Island(w);
		
		i.add(new Body());
		i.add(new AngleJoint(new Body(), new Body()));
		i.add(new ContactConstraint(new Body(), new BodyFixture(Geometry.createCircle(1.0)), new Body(), new BodyFixture(Geometry.createCircle(1.0)), new Manifold(new ArrayList<ManifoldPoint>(), new Vector2()), w));
		
		TestCase.assertEquals(1, i.bodies.size());
		TestCase.assertEquals(1, i.joints.size());
		TestCase.assertEquals(1, i.contactConstraints.size());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		World w = new World();
		Island i = new Island(w);
		
		i.add(new Body());
		i.add(new AngleJoint(new Body(), new Body()));
		i.add(new ContactConstraint(new Body(), new BodyFixture(Geometry.createCircle(1.0)), new Body(), new BodyFixture(Geometry.createCircle(1.0)), new Manifold(new ArrayList<ManifoldPoint>(), new Vector2()), w));
		
		TestCase.assertEquals(1, i.bodies.size());
		TestCase.assertEquals(1, i.joints.size());
		TestCase.assertEquals(1, i.contactConstraints.size());
		
		i.clear();
		
		TestCase.assertEquals(0, i.bodies.size());
		TestCase.assertEquals(0, i.joints.size());
		TestCase.assertEquals(0, i.contactConstraints.size());
	}
}
