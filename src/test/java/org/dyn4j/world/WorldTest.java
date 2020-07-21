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

import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.BoundsListenerAdapter;
import org.dyn4j.world.listener.CollisionListenerAdapter;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.listener.DestructionListenerAdapter;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.StepListenerAdapter;
import org.dyn4j.world.listener.TimeOfImpactListenerAdapter;
import org.dyn4j.world.result.ConvexDetectResult;
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
		
		World w = new World();
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
	
	
	// FIXME OLD CODE - please remove once AbstractPhysicsWorld is tested
	
	/**
	 * Step listener for testing.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	public class WTStepListener implements StepListener<Body> {
		/** The number of steps performed */
		public int steps;
		@Override
		public void begin(TimeStep step, PhysicsWorld<Body, ?> world) {}
		@Override
		public void updatePerformed(TimeStep step, PhysicsWorld<Body, ?> world) {}
		@Override
		public void postSolve(TimeStep step, PhysicsWorld<Body, ?> world) {}
		@Override
		public void end(TimeStep step, PhysicsWorld<Body, ?> world) {
			this.steps++;
		}
	}
	
	/**
	 * Destruction listener for testing.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	public class WTDestructionListener implements DestructionListener<Body> {
		/** The number of times called */
		public int called;
		@Override
		public void destroyed(Joint<Body> joint) { this.called++; }
		@Override
		public void destroyed(ContactConstraint<Body> contactConstraint) { this.called++; }
		@Override
		public void destroyed(Body body) { this.called++; }
	}
	
	/**
	 * Tests the successful creation of a world object.
	 */
	@Test
	public void createSuccess() {
		World w = new World();
		TestCase.assertNotNull(w.bodies);
		TestCase.assertNotNull(w.bodiesUnmodifiable);
		TestCase.assertNull(w.bounds);
		TestCase.assertNotNull(w.boundsListeners);
		TestCase.assertNotNull(w.broadphaseDetector);
		TestCase.assertNotNull(w.coefficientMixer);
		TestCase.assertNotNull(w.collisionData);
		TestCase.assertNotNull(w.collisionListeners);
		TestCase.assertNotNull(w.contactCollisions);
		TestCase.assertNotNull(w.contactConstraintSolver);
		TestCase.assertNotNull(w.contactListeners);
		TestCase.assertNotNull(w.destructionListeners);
		TestCase.assertNotNull(w.broadphaseFilter);
		TestCase.assertNotNull(w.gravity);
		TestCase.assertNotNull(w.constraintGraph);
		TestCase.assertNotNull(w.joints);
		TestCase.assertNotNull(w.manifoldSolver);
		TestCase.assertNotNull(w.narrowphaseDetector);
		TestCase.assertNotNull(w.narrowphasePostProcessor);
		TestCase.assertNotNull(w.raycastDetector);
		TestCase.assertNotNull(w.settings);
		TestCase.assertNotNull(w.step);
		TestCase.assertNotNull(w.stepListeners);
		TestCase.assertEquals(0.0, w.time);
		TestCase.assertNotNull(w.timeOfImpactDetector);
		TestCase.assertNotNull(w.timeOfImpactListeners);
		TestCase.assertNotNull(w.timeOfImpactSolver);
		TestCase.assertNotNull(w.bodiesUnmodifiable);
		TestCase.assertNotNull(w.jointsUnmodifiable);
		TestCase.assertTrue(w.updateRequired);
	}
	
	/**
	 * Tests the update method.
	 */
	@Test
	public void update() {
		Settings s = new Settings();
		World w = new World();
		
		// update using enough elapsed time to make a step happen
		boolean stepped = w.update(s.getStepFrequency());
		TestCase.assertTrue(stepped);
		
		stepped = w.update(s.getStepFrequency() * 2.5, 10);
		TestCase.assertTrue(stepped);
		// verify left over time
		TestCase.assertEquals(s.getStepFrequency() * 0.5, w.getAccumulatedTime(), 1.0e-8);
		
		// make sure negative or zero does nothing
		stepped = w.update(-2.0);
		TestCase.assertFalse(stepped);
		stepped = w.update(0.0);
		TestCase.assertFalse(stepped);
	}
	
	/**
	 * Tests the updatev method.
	 */
	@Test
	public void updatev() {
		World w = new World();
		WTStepListener sl = new WTStepListener();
		w.addStepListener(sl);
		
		// a step is always taken unless the update time
		// is zero or less
		w.updatev(3.0);
		TestCase.assertEquals(1, sl.steps);
		
		// shouldnt step at all
		w.updatev(0.0);
		TestCase.assertEquals(1, sl.steps);
		// shouldnt step at all
		w.updatev(-1.0);
		TestCase.assertEquals(1, sl.steps);
	}
	
	/**
	 * Tests the step a discrete number of times method.
	 */
	@Test
	public void stepInt() {
		World w = new World();
		WTStepListener sl = new WTStepListener();
		w.addStepListener(sl);
		
		// make sure the specified number of steps are taken
		w.step(3);
		TestCase.assertEquals(3, sl.steps);
		
		// test zero and negative
		w.step(0);
		TestCase.assertEquals(3, sl.steps);
		w.step(-2);
		TestCase.assertEquals(3, sl.steps);
	}
	
	/**
	 * Tests the step a discrete number of times method using
	 * a specified elapsed time.
	 */
	@Test
	public void stepIntElapsed() {
		World w = new World();
		WTStepListener sl = new WTStepListener();
		w.addStepListener(sl);
		
		// make sure the specified number of steps are taken
		w.step(3, 1.0 / 50.0);
		TestCase.assertEquals(3, sl.steps);
		
		// test zero and negative
		w.step(0, 1.0 / 50.0);
		TestCase.assertEquals(3, sl.steps);
		w.step(-2, 1.0 / 50.0);
		TestCase.assertEquals(3, sl.steps);
	}
	
	/**
	 * Tests the add body method.
	 */
	@Test
	public void addBody() {
		World w = new World();
		Body b = new Body();
		
		TestCase.assertNull(b.getOwner());
		
		b.addFixture(Geometry.createCapsule(1.0, 0.5));
		w.addBody(b);
		TestCase.assertFalse(0 == w.getBodyCount());
		// make sure the body's world reference is there
		TestCase.assertNotNull(b.getOwner());
		TestCase.assertEquals(w, b.getOwner());
		// make sure it was added to the broadphase
		TestCase.assertTrue(w.broadphaseDetector.contains(b));
		TestCase.assertTrue(w.broadphaseDetector.contains(b, b.getFixture(0)));
	}
	
	/**
	 * Tests the add body method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullBody() {
		World w = new World();
		w.addBody((Body) null);
	}
	
	/**
	 * Tests the add body method attempting to add the
	 * same body more than once.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addSameBody() {
		World w = new World();
		Body b1 = new Body();
		w.addBody(b1);
		w.addBody(b1);
	}
	
	/**
	 * Tests the add body method.
	 */
	@Test
	public void addJoint() {
		World w = new World();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		TestCase.assertTrue(w.getJointCount() == 1);
		TestCase.assertTrue(w.isJoined(b1, b2));
		TestCase.assertTrue(!w.isJointCollisionAllowed(b1, b2));
		
		TestCase.assertNotNull(b1.getOwner());
		TestCase.assertEquals(w, b1.getOwner());
		TestCase.assertNotNull(b1.getFixtureModificationHandler());
		TestCase.assertTrue(w.broadphaseDetector.contains(b1));
		
		TestCase.assertNotNull(b2.getOwner());
		TestCase.assertEquals(w, b2.getOwner());
		TestCase.assertNotNull(b2.getFixtureModificationHandler());
		TestCase.assertTrue(w.broadphaseDetector.contains(b2));
		
		TestCase.assertNotNull(j.getOwner());
		TestCase.assertEquals(w, j.getOwner());
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(1, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(1, n2.joints.size());
	}

	/**
	 * Tests the add joint method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullJoint() {
		World w = new World();
		w.addJoint((Joint<Body>) null);
	}
	
	/**
	 * Tests the remove body method.
	 */
	@Test
	public void removeBody() {
		World w = new World();
		
		// setup the destruction listener
		WTDestructionListener dl = new WTDestructionListener();
		w.addDestructionListener(dl);
		
		// test removing a null body
		boolean success = w.removeBody((Body) null);
		TestCase.assertFalse(success);
		
		// test removing a body not in the list
		success = w.removeBody(new Body());
		TestCase.assertFalse(success);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// remove one of them
		success = w.removeBody(b1, true);
		TestCase.assertTrue(success);
		TestCase.assertEquals(1, w.bodies.size());
		TestCase.assertNull(b1.getOwner());
		TestCase.assertNull(b1.getFixtureModificationHandler());
		TestCase.assertFalse(w.broadphaseDetector.contains(b1));
		
		// add that one back
		w.addBody(b1);
		TestCase.assertNotNull(b1.getOwner());
		TestCase.assertNotNull(b1.getFixtureModificationHandler());
		TestCase.assertTrue(w.broadphaseDetector.contains(b1));
		
		// create a joint
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		TestCase.assertEquals(1, w.collisionData.size());
		
		// remove a body and make sure destruction events are called
		success = w.removeBody(b2, true);
		TestCase.assertTrue(success);
		// make sure it was added to the broadphase
		TestCase.assertNull(b2.getOwner());
		TestCase.assertNull(b2.getFixtureModificationHandler());
		TestCase.assertFalse(w.broadphaseDetector.contains(b2));
		// make sure the world has zero joints now
		TestCase.assertEquals(0, w.joints.size());
		// make sure the world still has the first body
		TestCase.assertEquals(1, w.bodies.size());
		// make sure it really is the first body
		TestCase.assertEquals(b1, w.getBody(0));
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.contactConstraints.size());
		TestCase.assertEquals(0, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNull(n2);

		// make sure the destruction listener was called for the one
		// joint and one contact
		TestCase.assertEquals(2, dl.called);
		
		// make sure the collision data was cleaned up
		TestCase.assertEquals(0, w.collisionData.size());
	}
	
	/**
	 * Tests the remove body method ensuring that leaked collisions are cleaned up.
	 */
	@Test
	public void removeBodyMemoryLeak() {
		World w = new World();
		
		// setup the destruction listener
		WTDestructionListener dl = new WTDestructionListener();
		w.addDestructionListener(dl);
		
		// test removing a null body
		boolean success = w.removeBody((Body) null);
		TestCase.assertFalse(success);
		
		// test removing a body not in the list
		success = w.removeBody(new Body());
		TestCase.assertFalse(success);
		
		// setup the bodies
		Convex c1 = Geometry.createSquare(1.0);
		Convex c2 = Geometry.createSquare(1.0);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		b1.translate(0.5, 1.1);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// remove one of them
		success = w.removeBody(b1, true);
		TestCase.assertTrue(success);
		TestCase.assertEquals(1, w.bodies.size());
		TestCase.assertNull(b1.getOwner());
		TestCase.assertNull(b1.getFixtureModificationHandler());
		TestCase.assertFalse(w.broadphaseDetector.contains(b1));
		
		// add that one back
		w.addBody(b1);
		TestCase.assertNotNull(b1.getOwner());
		TestCase.assertNotNull(b1.getFixtureModificationHandler());
		TestCase.assertTrue(w.broadphaseDetector.contains(b1));
		
		// create a joint
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		TestCase.assertEquals(1, w.collisionData.size());
		
		// remove a body and make sure destruction events are called
		success = w.removeBody(b2, true);
		TestCase.assertTrue(success);
		// make sure it was added to the broadphase
		TestCase.assertNull(b2.getOwner());
		TestCase.assertNull(b2.getFixtureModificationHandler());
		TestCase.assertFalse(w.broadphaseDetector.contains(b2));
		// make sure the world has zero joints now
		TestCase.assertEquals(0, w.joints.size());
		// make sure the world still has the first body
		TestCase.assertEquals(1, w.bodies.size());
		// make sure it really is the first body
		TestCase.assertEquals(b1, w.getBody(0));
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.contactConstraints.size());
		TestCase.assertEquals(0, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNull(n2);

		// make sure the destruction listener was called for the joint
		TestCase.assertEquals(1, dl.called);
		
		// NOTE: the collision will remain because it didn't make it to
		// the contact constraint stage, but it should be cleaned up
		// in the next iteration of the engine
		TestCase.assertEquals(1, w.collisionData.size());
		
		// step the engine
		w.step(1);
		
		// make sure the leaked collision was cleaned up
		TestCase.assertEquals(0, w.collisionData.size());
	}
	
	/**
	 * Tests the remove joint method.
	 */
	@Test
	public void removeJoint() {
		World w = new World();
		
		// test removing a null body
		boolean success = w.removeJoint((Joint<Body>) null);
		TestCase.assertFalse(success);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// create a joint
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// remove a joint and make sure destruction events are called
		w.removeJoint(j);
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.getJointCount());
		// make sure the world still has both bodies
		TestCase.assertTrue(w.getBodyCount() > 0);
		TestCase.assertEquals(2, w.getBodyCount());
		TestCase.assertNull(j.getOwner());
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(0, n2.joints.size());
	}
	
	/**
	 * Tests the set gravity method.
	 */
	@Test
	public void setGravity() {
		World w = new World();
		Vector2 g = new Vector2(0.0, -10.0);
		w.setGravity(g);
		TestCase.assertSame(g, w.gravity);
	}
	
	/**
	 * Tests the set gravity method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullGravity() {
		World w = new World();
		w.setGravity(null);
	}
	
	/**
	 * Tests the set bounds method.
	 */
	@Test
	public void setBounds() {
		World w = new World();
		AxisAlignedBounds b = new AxisAlignedBounds(1.0, 1.0);
		w.setBounds(b);
		
		TestCase.assertNotNull(w.getBounds());
		TestCase.assertEquals(1.0, ((AxisAlignedBounds)w.getBounds()).getWidth());
		TestCase.assertEquals(1.0, ((AxisAlignedBounds)w.getBounds()).getHeight());
	}
	
	/**
	 * Tests the add listener method.
	 */
	@Test
	public void addListener() {
		World w = new World();
		BoundsListener<Body, BodyFixture> bl = new BoundsListenerAdapter<Body, BodyFixture>();
		w.addBoundsListener(bl);
		
		TestCase.assertSame(bl, w.getBoundsListeners().get(0));
	}
	
	/**
	 * Tests the set broadphase detector method.
	 */
	@Test
	public void setBroadphaseDetector() {
		World w = new World();
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		w.addBody(b);
		BroadphaseDetector<Body, BodyFixture> bd = new Sap<Body, BodyFixture>();
		w.setBroadphaseDetector(bd);
		TestCase.assertSame(bd, w.getBroadphaseDetector());
		// test bodies are re-added
		TestCase.assertTrue(w.broadphaseDetector.contains(b));
		TestCase.assertTrue(w.broadphaseDetector.contains(b, b.getFixture(0)));
	}
	
	/**
	 * Tests the set broadphase detector method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullBroadphaseDetector() {
		World w = new World();
		w.setBroadphaseDetector(null);
	}
	
	/**
	 * Tests the set narrowphase detector method.
	 */
	@Test
	public void setNarrowphaseDetector() {
		World w = new World();
		NarrowphaseDetector nd = new Gjk();
		w.setNarrowphaseDetector(nd);
		
		TestCase.assertSame(nd, w.getNarrowphaseDetector());
	}
	
	/**
	 * Tests the set narrowphase detector method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullNarrowphaseDetector() {
		World w = new World();
		w.setNarrowphaseDetector(null);
	}
	
	/**
	 * Tests the set manifold solver method.
	 */
	@Test
	public void setManifoldSolver() {
		World w = new World();
		ManifoldSolver ms = new ClippingManifoldSolver();
		w.setManifoldSolver(ms);
		
		TestCase.assertSame(ms, w.getManifoldSolver());
	}
	
	/**
	 * Tests the set manifold solver method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullManifoldSolver() {
		World w = new World();
		w.setManifoldSolver(null);
	}
	
	/**
	 * Tests the set time of impact detector method.
	 * @since 1.2.0
	 */
	@Test
	public void setTimeOfImpactDetector() {
		World w = new World();
		TimeOfImpactDetector toid = new ConservativeAdvancement();
		w.setTimeOfImpactDetector(toid);
		
		TestCase.assertSame(toid, w.getTimeOfImpactDetector());
	}
	
	/**
	 * Tests the set time of impact detector method passing a null value.
	 * @since 1.2.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTimeOfImpactDetector() {
		World w = new World();
		w.setTimeOfImpactDetector(null);
	}
	
	/**
	 * Tests the set raycast detector method.
	 * @since 2.0.0
	 */
	@Test
	public void setRaycastDetector() {
		World w = new World();
		w.setRaycastDetector(new Gjk());
	}
	
	/**
	 * Tests the set raycast detector method passing a null value.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullRaycastDetector() {
		World w = new World();
		w.setRaycastDetector(null);
	}
	
	/**
	 * Tests the set coefficient mixer method.
	 */
	@Test
	public void setCoefficientMixer() {
		World w = new World();
		CoefficientMixer cm = new CoefficientMixer() {
			@Override
			public double mixRestitution(double restitution1, double restitution2) { return (restitution1 + restitution2) * 0.5; }
			@Override
			public double mixFriction(double friction1, double friction2) { return (friction1 + friction2) * 0.5; }
		};
		w.setCoefficientMixer(cm);
		
		TestCase.assertSame(cm, w.getCoefficientMixer());
	}
	
	/**
	 * Tests the set coefficient mixer method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullCoefficientMixer() {
		World w = new World();
		w.setCoefficientMixer(null);
	}
	
	/**
	 * Tests the removeAll method.
	 */
	@Test
	public void removeAll() {
		World w = new World();
		
		// setup the listener
		WTDestructionListener dl = new WTDestructionListener();
		w.addDestructionListener(dl);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// setup the joint
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to generate contacts
		w.step(1);
		
		// call the clear method
		w.removeAllBodiesAndJoints(true);
		
		// verify that it cleared everything and made all the callbacks
		TestCase.assertEquals(0, w.constraintGraph.size());
		TestCase.assertEquals(0, w.collisionData.size());
		TestCase.assertEquals(0, w.joints.size());
		TestCase.assertEquals(0, w.bodies.size());

		// one contact, one joint, and two bodies
		TestCase.assertEquals(4, dl.called);
	}
	
	/**
	 * Tests the removeAllBodies method.
	 * @since 3.0.2
	 */
	@Test
	public void removeAllBodies() {
		World w = new World();
		
		// setup the listener
		WTDestructionListener dl = new WTDestructionListener();
		w.addDestructionListener(dl);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// setup the joint
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to generate contacts
		w.step(1);
		
		// call the remove all bodies method
		w.removeAllBodies(true);
		
		// this method should remove everything because joints cannot exist
		// without the bodies
		
		// verify that it cleared everything and made all the callbacks
		TestCase.assertEquals(0, w.constraintGraph.size());
		TestCase.assertEquals(0, w.collisionData.size());
		TestCase.assertEquals(0, w.joints.size());
		TestCase.assertEquals(0, w.bodies.size());
		
		// one contact, one joint, and two bodies
		TestCase.assertEquals(4, dl.called);
	}
	
	/**
	 * Tests the removeAllJoints method.
	 * @since 3.0.2
	 */
	@Test
	public void removeAllJoints() {
		World w = new World();
		
		// test removing a null body
		boolean success = w.removeJoint((Joint<Body>) null);
		TestCase.assertFalse(success);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// create a joint
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// remove a joint and make sure destruction events are called
		w.removeAllJoints();
		
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.joints.size());
		// make sure the world still has both bodies
		TestCase.assertEquals(2, w.bodies.size());
		TestCase.assertNotNull(b1.getOwner());
		TestCase.assertNotNull(b1.getFixtureModificationHandler());
		TestCase.assertNotNull(b2.getOwner());
		TestCase.assertNotNull(b2.getFixtureModificationHandler());
		TestCase.assertNull(j.getOwner());
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(0, n2.joints.size());
	}
	
	/**
	 * Tests the isEmpty method.
	 * @since 3.0.2
	 */
	@Test
	public void isEmpty() {
		World w = new World();
		
		TestCase.assertTrue(w.isEmpty());
		
		Body b1 = new Body();
		Body b2 = new Body();
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		w.addBody(b1);
		TestCase.assertFalse(w.isEmpty());
		
		w.removeAllBodiesAndJoints();
		TestCase.assertTrue(w.isEmpty());
		
		w.addBody(b1);
		w.addBody(b2);
		TestCase.assertFalse(w.isEmpty());
		
		w.addJoint(j);
		TestCase.assertFalse(w.isEmpty());
	}
	
	/**
	 * Tests the setSettings method.
	 * @since 3.0.3
	 */
	@Test
	public void setSettings() {
		World w = new World();
		Settings s = new Settings();
		s.setLinearTolerance(10000.0);
		w.setSettings(s);
		TestCase.assertEquals(s, w.getSettings());
		TestCase.assertEquals(10000.0, w.getSettings().getLinearTolerance());
	}
	
	/**
	 * Tests setting the settings to null.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullSettings() {
		World w = new World();
		w.setSettings(null);
	}
	
	/**
	 * Tests adding a body to a world that has already been added to a different world.
	 * @since 3.1.0
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addBodyFromAnotherWorld() {
		World w1 = new World();
		World w2 = new World();
		
		Body b = new Body();
		
		w1.addBody(b);
		
		w2.addBody(b);
	}
	
	/**
	 * Tests the get/add/remove listeners methods.
	 * @since 3.1.1
	 */
	@Test
	public void listeners() {
		World w = new World();
		
		// add some listeners
		BoundsListenerAdapter<Body, BodyFixture> ba = new BoundsListenerAdapter<Body, BodyFixture>();
		CollisionListenerAdapter<Body, BodyFixture> ca = new CollisionListenerAdapter<Body, BodyFixture>();
		DestructionListenerAdapter<Body> da = new DestructionListenerAdapter<Body>();
		StepListenerAdapter<Body> sa = new StepListenerAdapter<Body>();
		TimeOfImpactListenerAdapter<Body> ta = new TimeOfImpactListenerAdapter<Body>();
		ContactListenerAdapter<Body> na = new ContactListenerAdapter<Body>();
		
		w.addBoundsListener(ba);
		TestCase.assertEquals(1, w.boundsListeners.size());
		
		w.addCollisionListener(ca);
		TestCase.assertEquals(1, w.collisionListeners.size());
		
		w.addDestructionListener(da);
		TestCase.assertEquals(1, w.destructionListeners.size());
		
		w.addStepListener(sa);
		TestCase.assertEquals(1, w.stepListeners.size());
		
		w.addTimeOfImpactListener(ta);
		TestCase.assertEquals(1, w.timeOfImpactListeners.size());
		
		w.addContactListener(na);
		TestCase.assertEquals(1, w.contactListeners.size());
		
		// test the multi-listener functionality
		w.addStepListener(new WTStepListener());
		w.addStepListener(new WTStepListener());
		w.step();
		TestCase.assertEquals(3, w.stepListeners.size());
		// verify both were called
		TestCase.assertEquals(1, ((WTStepListener)w.stepListeners.get(1)).steps);
		TestCase.assertEquals(1, ((WTStepListener)w.stepListeners.get(2)).steps);
		
		// test removing
		w.removeAllBoundsListeners();
		TestCase.assertEquals(0, w.boundsListeners.size());
		
		w.removeAllCollisionListeners();
		TestCase.assertEquals(0, w.collisionListeners.size());
		
		w.removeAllDestructionListeners();
		TestCase.assertEquals(0, w.destructionListeners.size());
		
		w.removeAllStepListeners();
		TestCase.assertEquals(0, w.stepListeners.size());
		
		w.removeAllTimeOfImpactListeners();
		TestCase.assertEquals(0, w.timeOfImpactListeners.size());
		
		w.removeAllContactListeners();
		TestCase.assertEquals(0, w.contactListeners.size());
	}
	
	/**
	 * Tests the get/set of the user data.
	 */
	@Test
	public void getUserData() {
		String obj = "hello";
		World w = new World();
		
		TestCase.assertNull(w.getUserData());
		
		w.setUserData(obj);
		TestCase.assertNotNull(w.getUserData());
		TestCase.assertSame(obj, w.getUserData());
	}
	
	/**
	 * Tests the new detect methods.
	 * @since 3.1.10
	 */
	@Test
	public void testNewDetectMethod() {
	   World world = new World();
	   Circle c = new Circle(20.0);
	   
	   DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(true, false, new CategoryFilter(2,15));
	   
	   Body b = new Body();
	   b.addFixture(c) ;
	   world.addBody(b);
	   
	   List<ConvexDetectResult<Body, BodyFixture>> results = world.detect(c, new Transform(), filter);
	   TestCase.assertTrue(results.size() > 0);
	}

	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodies() {
		World w = new World();
		w.getBodies().add(new Body());
	}

	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getJoints() {
		World w = new World();
		w.getJoints().add(new AngleJoint<Body>(new Body(), new Body()));
	}

	/**
	 * Tests the body iterator.
	 */
	@Test
	public void bodyIterator() {
		World w = new World();
		
		w.addBody(new Body());
		w.addBody(new Body());
		w.addBody(new Body());
		w.addBody(new Body());
		
		Iterator<Body> it = w.getBodyIterator();
		while (it.hasNext()) {
			it.next();
		}
		
		it = w.getBodyIterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
		
		TestCase.assertEquals(0, w.getBodyCount());
	}
	
	/**
	 * Tests the joint iterator.
	 */
	@Test
	public void jointIterator() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		
		w.addJoint(new AngleJoint<Body>(b1, b2));
		w.addJoint(new AngleJoint<Body>(b1, b2));
		w.addJoint(new AngleJoint<Body>(b1, b2));
		w.addJoint(new AngleJoint<Body>(b1, b2));
		
		Iterator<Joint<Body>> it = w.getJointIterator();
		while (it.hasNext()) {
			it.next();
		}
		
		it = w.getJointIterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
		
		TestCase.assertEquals(0, w.getJointCount());
	}
	
	/**
	 * Make sure disabled or sensor contacts don't make it into
	 * the solve stage.
	 */
	@Test
	public void disabledAndSensor() {
		World world = new World();
		world.setGravity(World.ZERO_GRAVITY);
		
		Convex c1 = Geometry.createUnitCirclePolygon(6, 0.7);
		Convex c2 = Geometry.createEquilateralTriangle(1.0);
		Convex c3 = Geometry.createCircle(1.0);
		Convex c4 = Geometry.createSquare(1.2);
		
		Body b1 = new Body();
		b1.addFixture(c1);
		b1.setMass(MassType.NORMAL);
		world.addBody(b1);
		
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		f2.setSensor(true);
		b2.setMass(MassType.NORMAL);
		world.addBody(b2);
		
		Body b3 = new Body();
		b3.addFixture(c3);
		b3.setMass(MassType.NORMAL);
		world.addBody(b3);
		
		Body b4 = new Body();
		b4.addFixture(c4);
		b4.setMass(MassType.NORMAL);
		world.addBody(b4);
		
		// override the contact adapter to simulate a disabled contact
		world.addContactListener(new ContactListenerAdapter<Body>() {
			@Override
			public void begin(ContactCollisionData<Body> collision, Contact contact) {
				
				if ((collision.getBody1() == b1 && collision.getBody2() == b2) ||
					(collision.getBody1() == b2 && collision.getBody2() == b1)) {
					collision.getContactConstraint().setEnabled(false);
				}
				
				super.begin(collision, contact);
			}
		});
		
		// override the contact constraint solver
		// to check for any disabled or sensor contacts
		world.setContactConstraintSolver(new SequentialImpulses<Body>() {
			@Override
			public void initialize(List<ContactConstraint<Body>> contactConstraints, TimeStep step, Settings settings) {
				for (ContactConstraint<Body> cc : contactConstraints) {
					if (!cc.isEnabled() || cc.isSensor()) {
						TestCase.fail();
					}
				}
				super.initialize(contactConstraints, step, settings);
			}
		});
		
		world.step(1);
	}
	
	/**
	 * Tests the isJoined method.
	 */
	@Test
	public void isJoined() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		Body b3 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		w.addBody(b3);
		
		// test null test
		TestCase.assertFalse(w.isJoined(b1, null));
		
		// test no connections
		TestCase.assertFalse(w.isJoined(b1, b2));
				
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		w.addJoint(j);
		
		// test no connection to b3
		TestCase.assertFalse(w.isJoined(b1, b3));
		
		// test that they are connected
		TestCase.assertTrue(w.isJoined(b1, b2));
	}
	
	/**
	 * Tests a case where two or more joints are connecting
	 * two bodies one of which allows collision and another
	 * does not.
	 * @since 2.2.2
	 */
	@Test
	public void isJointCollisionAllowed() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		w.addJoint(rj);
		w.addJoint(aj);
		
		// test both with no collision
		TestCase.assertFalse(w.isJointCollisionAllowed(b1, b2));
		
		// set one joint to allow collision
		aj.setCollisionAllowed(true);
		
		TestCase.assertTrue(w.isJointCollisionAllowed(b1, b2));
	}

	/**
	 * Tests the getJoinedBodies method.
	 */
	@Test
	public void getJoinedBodies() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		
		List<Body> bodies = w.getJoinedBodies(b1);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		w.addJoint(j);
		
		bodies = w.getJoinedBodies(b1);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
	}
	
	/**
	 * Tests the getJoints method.
	 */
	@Test
	public void getJointsForBody() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		
		List<Joint<Body>> joints = w.getJoints(b1);
		TestCase.assertNotNull(joints);
		TestCase.assertTrue(joints.isEmpty());
		
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		w.addJoint(j);
		
		joints = w.getJoints(b1);
		TestCase.assertNotNull(joints);
		TestCase.assertFalse(joints.isEmpty());
		TestCase.assertSame(j, joints.get(0));
	}
	
	/**
	 * Test the getInContactBodies method.
	 */
	@Test
	public void getInContactBodies() {
		World w = new World();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		b2.addFixture(c2);
		
		b1.setMass(MassType.NORMAL);
		b2.setMass(MassType.NORMAL);
		
		w.addBody(b1);
		w.addBody(b2);
		
		List<Body> bodies = w.getInContactBodies(b1, false);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		bodies = w.getInContactBodies(b1, true);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		w.step(1);
		
		bodies = w.getInContactBodies(b1, false);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
		
		f1.setSensor(true);
		w.step(1);
		
		bodies = w.getInContactBodies(b1, false);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		bodies = w.getInContactBodies(b1, true);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
	}
	
	/**
	 * Test the getContacts method.
	 */
	@Test
	public void getContacts() {
		World w = new World();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		b1.addFixture(c1);
		Body b2 = new Body();
		b2.addFixture(c2);
		
		b1.setMass(MassType.NORMAL);
		b2.setMass(MassType.NORMAL);
		
		w.addBody(b1);
		w.addBody(b2);
		
		List<ContactConstraint<Body>> contacts = w.getContacts(b1);
		TestCase.assertNotNull(contacts);
		TestCase.assertTrue(contacts.isEmpty());
		
		w.step(1);
		
		contacts = w.getContacts(b1);
		TestCase.assertNotNull(contacts);
		TestCase.assertFalse(contacts.isEmpty());
	}
	
	/**
	 * Tests bodies joined by multiple joints ensuring that the
	 * getJoinedBodies method only returns one instance of the joined
	 * body.
	 */
	@Test
	public void getJoinedBodiesMulti() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b2);
		
		w.addJoint(j1);
		w.addJoint(j2);
		
		List<Body> jbs = w.getJoinedBodies(b1);
		TestCase.assertEquals(1, jbs.size());
	}
	
	/**
	 * Tests bodies in contact with multiple fixtures ensuring that the
	 * getInContactBodies method only returns one instance of the in contact
	 * body.
	 */
	@Test
	public void getInContactBodiesMulti() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		b1.addFixture(Geometry.createRectangle(15.0, 1.0));
		b1.setMass(MassType.NORMAL);
		
		b2.addFixture(Geometry.createSquare(1.0));
		Convex c = Geometry.createSquare(1.0);
		c.translate(-0.5, 0.0);
		b2.addFixture(c);
		b2.setMass(MassType.NORMAL);
		b2.translate(0.0, 0.75);
		
		w.addBody(b1);
		w.addBody(b2);
		
		w.step(1);
		
		List<Body> cbs = w.getInContactBodies(b1, false);
		TestCase.assertEquals(1, cbs.size());
	}
}
