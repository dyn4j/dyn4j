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
package org.dyn4j.world.listener;

import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.BroadphaseCollisionData;
import org.dyn4j.world.ManifoldCollisionData;
import org.dyn4j.world.NarrowphaseCollisionData;
import org.dyn4j.world.World;
import org.dyn4j.world.WorldCollisionData;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link WorldEventListener} adapter classes.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class ListenerAdaptersTest {
	/**
	 * Tests the {@link BoundsListenerAdapter} class.
	 */
	@Test
	public void boundsListenerAdapter() {
		// test that nothing happens, even with null
		// there's nothing to assert except for successful operation
		BoundsListenerAdapter<Body, BodyFixture> bla = new BoundsListenerAdapter<Body, BodyFixture>();
		bla.outside(null);
		bla.outside(new Body());
	}
	
	/**
	 * Tests the {@link CollisionListenerAdapter} class.
	 */
	@Test
	public void collisionListenerAdapter() {
		// test that nothing happens, even with null
		CollisionListenerAdapter<Body, BodyFixture> cla = new CollisionListenerAdapter<Body, BodyFixture>();
		WorldCollisionData<Body> data = new WorldCollisionData<Body>(new BasicCollisionPair<Body, BodyFixture>(null, null, null, null));
		
		TestCase.assertTrue(cla.collision((BroadphaseCollisionData<Body, BodyFixture>)null));
		TestCase.assertTrue(cla.collision((BroadphaseCollisionData<Body, BodyFixture>)data));
		
		TestCase.assertTrue(cla.collision((NarrowphaseCollisionData<Body, BodyFixture>)null));
		TestCase.assertTrue(cla.collision((NarrowphaseCollisionData<Body, BodyFixture>)data));
		
		TestCase.assertTrue(cla.collision((ManifoldCollisionData<Body, BodyFixture>)null));
		TestCase.assertTrue(cla.collision((ManifoldCollisionData<Body, BodyFixture>)data));
	}
	
	/**
	 * Tests the {@link ContactListenerAdapter} class.
	 */
	@Test
	public void contactListenerAdapter() {
		// test that nothing happens, even with null
		ContactListenerAdapter<Body> cla = new ContactListenerAdapter<Body>();
		WorldCollisionData<Body> data = new WorldCollisionData<Body>(new BasicCollisionPair<Body, BodyFixture>(null, null, null, null));
		SolvedContact c = new SolvedContact() {
			@Override
			public Vector2 getPoint() { return null; }
			@Override
			public ManifoldPointId getId() { return null; }
			@Override
			public double getDepth() { return 0; }
			@Override
			public double getNormalImpulse() { return 0; }
			@Override
			public double getTangentialImpulse() { return 0; }
			@Override
			public boolean isSolved() { return true; }
		};
		
		// nothing to assert since they are no-ops with no effect any where
		cla.begin(data, null);
		cla.begin(data, c);
		
		cla.end(data, null);
		cla.end(data, c);
		
		cla.persist(data, null, c);
		cla.persist(data, c, null);
		cla.persist(data, null, null);
		cla.persist(data, c, c);
		
		cla.postSolve(data, null);
		cla.postSolve(data, c);
		
		cla.preSolve(data, null);
		cla.preSolve(data, c);
	}

	/**
	 * Tests the {@link DestructionListenerAdapter} class.
	 */
	@Test
	public void destructionListenerAdapter() {
		// test that nothing happens, even with null
		DestructionListenerAdapter<Body> dla = new DestructionListenerAdapter<Body>();
		ContactConstraint<Body> cc = new ContactConstraint<Body>(new BasicCollisionPair<Body, BodyFixture>(null, null, null, null));
		Joint<Body> j = new AngleJoint<Body>(new Body(), new Body());
		
		// nothing to assert since they are no-ops with no effect any where
		dla.destroyed((Body)null);
		dla.destroyed(new Body());
		
		dla.destroyed((ContactConstraint<Body>)null);
		dla.destroyed(cc);
		
		dla.destroyed((Joint<Body>)null);
		dla.destroyed(j);
	}

	/**
	 * Tests the {@link StepListenerAdapter} class.
	 */
	@Test
	public void stepListenerAdapter() {
		// test that nothing happens, even with null
		StepListenerAdapter<Body> sla = new StepListenerAdapter<Body>();
		TimeStep ts = new TimeStep(0.3);
		World<Body> w = new World<Body>();
		
		// nothing to assert since they are no-ops with no effect any where
		sla.begin(null, w);
		sla.begin(ts, null);
		sla.begin(null, null);
		sla.begin(ts, w);
		
		sla.end(ts, w);
		sla.end(ts, null);
		sla.end(null, w);
		sla.end(null, null);
		
		sla.postSolve(ts, w);
		sla.postSolve(null, w);
		sla.postSolve(ts, null);
		sla.postSolve(null, null);
		
		sla.updatePerformed(ts, w);
		sla.updatePerformed(null, w);
		sla.updatePerformed(ts, null);
		sla.updatePerformed(null, null);
	}

	/**
	 * Tests the {@link TimeOfImpactListenerAdapter} class.
	 */
	@Test
	public void timeOfImpactListenerAdapter() {
		// test that nothing happens, even with null
		TimeOfImpactListenerAdapter<Body> sla = new TimeOfImpactListenerAdapter<Body>();
		Body b = new Body();
		BodyFixture f = new BodyFixture(Geometry.createCircle(0.5));
		
		TestCase.assertTrue(sla.collision(null, null, null, null, null));
		TestCase.assertTrue(sla.collision(b, f, b, f, new TimeOfImpact()));
	}
}
