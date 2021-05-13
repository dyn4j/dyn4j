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

import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.broadphase.AABBExpansionMethod;
import org.dyn4j.collision.broadphase.AABBProducer;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.CollisionBodyAABBProducer;
import org.dyn4j.collision.broadphase.CollisionBodyBroadphaseFilter;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.broadphase.StaticValueAABBExpansionMethod;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.ForceCollisionTimeOfImpactSolver;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.ContactListenerAdapter;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.listener.DestructionListenerAdapter;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.StepListenerAdapter;
import org.dyn4j.world.listener.TimeOfImpactListener;
import org.dyn4j.world.listener.TimeOfImpactListenerAdapter;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link AbstractPhysicsWorld} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.0.0
 */
public class AbstractPhysicsWorldTest {
	private class TestWorld extends AbstractPhysicsWorld<Body, WorldCollisionData<Body>> {
		public TestWorld() {
			super();
		}
		
		public TestWorld(int initialBodyCapacity, int initialJointCapacity) {
			super(initialBodyCapacity, initialJointCapacity);
		}

		@Override
		protected WorldCollisionData<Body> createCollisionData(CollisionPair<CollisionItem<Body, BodyFixture>> pair) {
			return new WorldCollisionData<Body>(pair);
		}
	}
	
	/**
	 * Step listener for testing.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private class StepListenerCounter implements StepListener<Body> {
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
	private class DestructionListenerCounter implements DestructionListener<Body> {
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
	 * Contact listener for testing.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private class ContactListenerCounter implements ContactListener<Body> {
		/** The number of times called */
		public int end;
		public int destroyed;
		@Override
		public void preSolve(ContactCollisionData<Body> collision, Contact contact) { }
		@Override
		public void begin(ContactCollisionData<Body> collision, Contact contact) { }
		@Override
		public void end(ContactCollisionData<Body> collision, Contact contact) { this.end++; }
		@Override
		public void destroyed(ContactCollisionData<Body> collision, Contact contact) { this.destroyed++; }
		@Override
		public void persist(ContactCollisionData<Body> collision, Contact oldContact, Contact newContact) { }
		@Override
		public void postSolve(ContactCollisionData<Body> collision, SolvedContact contact) { }
		@Override
		public void collision(ContactCollisionData<Body> collision) { }
	}
	
	/**
	 * Tests the successful creation of a world object.
	 */
	@Test
	public void createSuccess() {
		TestWorld w = new TestWorld();
		
		TestCase.assertNotNull(w.bodies);
		TestCase.assertNotNull(w.bodiesUnmodifiable);
		TestCase.assertNull(w.bounds);
		TestCase.assertNotNull(w.boundsListeners);
		TestCase.assertNotNull(w.broadphaseDetector);
		TestCase.assertNotNull(w.collisionData);
		TestCase.assertNotNull(w.collisionListeners);
		TestCase.assertNotNull(w.broadphaseFilter);
		TestCase.assertNotNull(w.manifoldSolver);
		TestCase.assertNotNull(w.narrowphaseDetector);
		TestCase.assertNotNull(w.narrowphasePostProcessor);
		TestCase.assertNotNull(w.raycastDetector);
		TestCase.assertNotNull(w.timeOfImpactDetector);
		TestCase.assertNull(w.userData);
		
		TestCase.assertNotNull(w.valueMixer);
		TestCase.assertNotNull(w.contactCollisions);
		TestCase.assertNotNull(w.contactConstraintSolver);
		TestCase.assertNotNull(w.contactListeners);
		TestCase.assertNotNull(w.contactListenersUnmodifiable);
		TestCase.assertNotNull(w.destructionListeners);
		TestCase.assertNotNull(w.destructionListenersUnmodifiable);
		TestCase.assertNotNull(w.gravity);
		TestCase.assertNotNull(w.constraintGraph);
		TestCase.assertNotNull(w.joints);
		TestCase.assertNotNull(w.jointsUnmodifiable);
		TestCase.assertNotNull(w.settings);
		TestCase.assertNotNull(w.timeStep);
		TestCase.assertNotNull(w.stepListeners);
		TestCase.assertNotNull(w.stepListenersUnmodifiable);
		TestCase.assertEquals(0.0, w.time);
		TestCase.assertNotNull(w.timeOfImpactDetector);
		TestCase.assertNotNull(w.timeOfImpactListeners);
		TestCase.assertNotNull(w.timeOfImpactListenersUnmodifiable);
		TestCase.assertNotNull(w.timeOfImpactSolver);
		TestCase.assertTrue(w.updateRequired);
		
		// test create with initial size
		w = new TestWorld(16, 2);
		w = new TestWorld(-16, 2);
		w = new TestWorld(0, 2);
		w = new TestWorld(5, -2);
		w = new TestWorld(5, 0);
		w = new TestWorld(-5, -2);
		w = new TestWorld(0, 0);
	}
	
	/**
	 * Tests the addBody method,
	 */
	@Test
	public void addBody() {
		TestWorld w = new TestWorld();
		Body b = new Body();
		b.addFixture(Geometry.createCapsule(1.0, 0.5));
		
		TestCase.assertFalse(w.constraintGraph.containsBody(b));
		
		w.addBody(b);
		
		TestCase.assertTrue(w.constraintGraph.containsBody(b));
	}
	
	/**
	 * Tests adding a joint.
	 */
	@Test
	public void addJoint() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body(); b1.addFixture(Geometry.createCapsule(1.0, 0.5));
		Body b2 = new Body(); b2.addFixture(Geometry.createCapsule(1.0, 0.5));
		
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		
		TestCase.assertNull(j.getOwner());
		TestCase.assertEquals(0, w.joints.size());
		TestCase.assertFalse(w.constraintGraph.containsBody(b1));
		TestCase.assertFalse(w.constraintGraph.containsBody(b2));
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		TestCase.assertEquals(1, w.joints.size());
		
		// make sure the joint has the right owner
		TestCase.assertNotNull(j.getOwner());
		TestCase.assertEquals(w, j.getOwner());
		TestCase.assertTrue(w.constraintGraph.containsBody(b1));
		TestCase.assertTrue(w.constraintGraph.containsBody(b2));
		TestCase.assertTrue(w.constraintGraph.containsJoint(j));
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(1, n1.joints.size());
		
		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(1, n2.joints.size());

		TestCase.assertTrue(w.getJointCount() == 1);
		TestCase.assertTrue(w.isJoined(b1, b2));
		TestCase.assertTrue(!w.isJointCollisionAllowed(b1, b2));
	}

	/**
	 * Tests the add joint method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullJoint() {
		TestWorld w = new TestWorld();
		w.addJoint((Joint<Body>) null);
	}

	/**
	 * Tests adding a joint that's on another world.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addJointFromOtherWorld() {
		TestWorld w1 = new TestWorld();
		TestWorld w2 = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		
		w1.addBody(b1);
		w1.addBody(b2);
		w1.addJoint(j);
		
		w2.addJoint(j);
	}
	
	/**
	 * Tests adding a joint that's already on this world.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addSameJointToSameWorld() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		w.addJoint(j);
	}

	/**
	 * Tests adding a joint that's already on this world.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addJointWithoutBodies() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		
		// you must add the bodies first
		w.addJoint(j);
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getJointsAndAdd() {
		TestWorld w = new TestWorld();
		w.getJoints().add(new AngleJoint<Body>(new Body(), new Body()));
	}

	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getJointsAndRemove() {
		TestWorld w = new TestWorld();
		w.getJoints().remove(0);
	}

	/**
	 * Tests the successful getJoint(int) method.
	 */
	@Test
	public void getJointAtIndex() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		TestCase.assertEquals(j, w.getJoint(0));
	}
	
	/**
	 * Tests the getJoint(int) method w/ a negative value.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void getJointAtNegativeIndex() {
		TestWorld w = new TestWorld();
		w.getJoint(-1);
	}

	/**
	 * Tests the getJoint(int) method w/ a value out of range.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void getJointAtBadIndex() {
		TestWorld w = new TestWorld();
		w.getJoint(1);
	}
	
	/**
	 * Tests the getJoint(int) method w/ a value out of range.
	 */
	@Test
	public void getJointCount() {
		TestWorld w = new TestWorld();
		
		TestCase.assertEquals(0, w.getJointCount());
		
		Body b1 = new Body();
		Body b2 = new Body();
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		TestCase.assertEquals(1, w.getJointCount());
	}

	/**
	 * Tests the getJointIterator method.
	 */
	@Test
	public void getJointIterator() {
		TestWorld w = new TestWorld();
		
		TestCase.assertNotNull(w.getJointIterator());
		
		// setup the bodies
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b2);
		
		w.addJoint(j1);
		w.addJoint(j2);
		
		TestCase.assertNotNull(w.getJointIterator());
		
		int i = 0;
		Iterator<Joint<Body>> it = w.getJointIterator();
		while (it.hasNext()) {
			TestCase.assertNotNull(it.next());
			i++;
		}
		
		TestCase.assertEquals(w.joints.size(), i);
		
		i = 0;
		it = w.getJointIterator();
		while (it.hasNext()) {
			i++;
			it.next();
			it.remove();
			TestCase.assertEquals(2 - i, w.joints.size());
		}
	}
	
	/**
	 * Tests the getJointIterator method.
	 */
	@Test
	public void getJointIteratorFailure() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b2);
		
		w.addJoint(j1);
		w.addJoint(j2);
		
		// test overflow
		Iterator<Joint<Body>> it = w.getJointIterator();
		try {
			it.next();
			it.next();
			it.next();
			TestCase.fail();
		} catch (IndexOutOfBoundsException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test remove
		it = w.getJointIterator();
		try {
			it.remove();
			TestCase.fail();
		} catch (IllegalStateException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test second remove
		it = w.getJointIterator();
		try {
			it.next();
			it.remove();
			it.remove();
			TestCase.fail();
		} catch (IllegalStateException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		// test source modification before remove
		it = w.getJointIterator();
		try {
			it.next();
			w.removeAllJoints();
			it.remove();
			TestCase.fail();
		} catch (IndexOutOfBoundsException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}

	/**
	 * Tests the containsJoint method.
	 */
	@Test
	public void containsJoint() {
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		
		w.addJoint(j1);

		TestCase.assertTrue(w.containsJoint(j1));
		TestCase.assertFalse(w.containsJoint(null));
		TestCase.assertFalse(w.containsJoint(new AngleJoint<Body>(b1, b2)));
	}

	/**
	 * Tests the removeJoint method w/ a negative index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeJointNegativeIndex() {
		TestWorld w = new TestWorld();
		w.removeJoint(-1);
	}
	
	/**
	 * Tests the removeJoint method w/ a non-existent index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeJointBadIndex() {
		TestWorld w = new TestWorld();
		w.removeJoint(2);
	}
	
	/**
	 * Tests the removeJoint method.
	 */
	@Test
	public void removeJoint() {
		TestWorld w = new TestWorld();
		
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
		
		TestCase.assertEquals(1, w.joints.size());
		// remove a joint and make sure destruction events are called
		TestCase.assertTrue(w.removeJoint(j));
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.joints.size());
		// make sure the world still has both bodies
		TestCase.assertEquals(2, w.bodies.size());
		TestCase.assertNull(j.getOwner());
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(0, n2.joints.size());
	}

	/**
	 * Tests the removeAllBodies method.
	 */
	@Test
	public void removeAllJoints() {
		TestWorld w = new TestWorld();
		DestructionListenerCounter dlc = new DestructionListenerCounter();
		w.addDestructionListener(dlc);
		
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
		
		// remove a joint and make sure destruction events are called
		w.removeAllJoints();
		
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.joints.size());
		// make sure the world still has both bodies
		TestCase.assertEquals(2, w.bodies.size());
		TestCase.assertNull(j.getOwner());
		TestCase.assertEquals(0, dlc.called);
		
		ConstraintGraphNode<Body> n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.joints.size());

		ConstraintGraphNode<Body> n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(0, n2.joints.size());
		
		// try without notification
		w.addJoint(j);
		dlc.called = 0;
		
		// remove a joint and make sure destruction events are called
		w.removeAllJoints(true);
		
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.joints.size());
		// make sure the world still has both bodies
		TestCase.assertEquals(2, w.bodies.size());
		TestCase.assertNull(j.getOwner());
		TestCase.assertEquals(1, dlc.called);
		
		n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.joints.size());

		n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(0, n2.joints.size());
	}

	/**
	 * Test the fixture modification handler.
	 */
	@Test
	public void fixtureModification() {
		TestWorld w = new TestWorld();
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);

		Body b1 = new Body(); BodyFixture f1 = b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); BodyFixture f2 = b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		w.detect();
		
		// sanity checks
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertEquals(1, w.collisionData.size());
		WorldCollisionData<Body> data = w.getCollisionData(b1, f1, b2, f2);
		TestCase.assertNotNull(data);
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertTrue(data.isContactConstraintCollision());
		TestCase.assertEquals(1, w.constraintGraph.getNode(b1).getContactConstraints().size());
		TestCase.assertEquals(1, w.constraintGraph.getNode(b2).getContactConstraints().size());
		
		// test adding a new fixture
		BodyFixture f3 = b1.addFixture(c2);
		w.detect();
		
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f3));
		TestCase.assertEquals(2, w.collisionData.size());
		data = w.getCollisionData(b1, f3, b2, f2);
		TestCase.assertNotNull(data);
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertTrue(data.isContactConstraintCollision());
		TestCase.assertEquals(2, w.constraintGraph.getNode(b1).getContactConstraints().size());
		TestCase.assertEquals(2, w.constraintGraph.getNode(b2).getContactConstraints().size());
		
		// test removing a fixture
		b1.removeFixture(f1);
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f3));
		TestCase.assertEquals(1, w.collisionData.size());
		data = w.getCollisionData(b1, f1, b2, f2);
		TestCase.assertNull(data);
		data = w.getCollisionData(b1, f3, b2, f2);
		TestCase.assertNotNull(data);
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertTrue(data.isContactConstraintCollision());
		TestCase.assertEquals(1, w.constraintGraph.getNode(b1).getContactConstraints().size());
		TestCase.assertEquals(1, w.constraintGraph.getNode(b2).getContactConstraints().size());
		
		// add f1 back
		b1.addFixture(f1);
		w.detect();
		
		TestCase.assertTrue(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertEquals(2, w.collisionData.size());
		data = w.getCollisionData(b1, f1, b2, f2);
		TestCase.assertNotNull(data);
		TestCase.assertTrue(data.isBroadphaseCollision());
		TestCase.assertTrue(data.isNarrowphaseCollision());
		TestCase.assertTrue(data.isManifoldCollision());
		TestCase.assertTrue(data.isContactConstraintCollision());
		TestCase.assertEquals(2, w.constraintGraph.getNode(b1).getContactConstraints().size());
		TestCase.assertEquals(2, w.constraintGraph.getNode(b2).getContactConstraints().size());
		
		// test removing all fixtures
		b1.removeAllFixtures();
		
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, f1));
		TestCase.assertFalse(w.broadphaseDetector.contains(b1, f3));
		TestCase.assertEquals(0, w.collisionData.size());
		data = w.getCollisionData(b1, f1, b2, f2);
		TestCase.assertNull(data);
		data = w.getCollisionData(b1, f3, b2, f2);
		TestCase.assertNull(data);
		TestCase.assertEquals(0, w.constraintGraph.getNode(b1).getContactConstraints().size());
		TestCase.assertEquals(0, w.constraintGraph.getNode(b2).getContactConstraints().size());
	}
	
	/**
	 * Test the various listener methods (add, remove, remove all, get).
	 */
	@Test
	public void listeners() {
		TestWorld w = new TestWorld();
		
		ContactListener<Body> cl = new ContactListenerAdapter<Body>();
		StepListener<Body> sl = new StepListenerAdapter<Body>();
		DestructionListener<Body> dl = new DestructionListenerAdapter<Body>();
		TimeOfImpactListener<Body> tl = new TimeOfImpactListenerAdapter<Body>();
		
		w.addContactListener(cl);
		w.addStepListener(sl);
		w.addDestructionListener(dl);
		w.addTimeOfImpactListener(tl);
		
		TestCase.assertEquals(0, w.collisionListeners.size());
		TestCase.assertEquals(0, w.boundsListeners.size());
		TestCase.assertEquals(1, w.contactListeners.size());
		TestCase.assertEquals(1, w.stepListeners.size());
		TestCase.assertEquals(1, w.destructionListeners.size());
		TestCase.assertEquals(1, w.timeOfImpactListeners.size());
		
		List<ContactListener<Body>> cls = w.getContactListeners();
		List<StepListener<Body>> sls = w.getStepListeners();
		List<DestructionListener<Body>> dls = w.getDestructionListeners();
		List<TimeOfImpactListener<Body>> tls = w.getTimeOfImpactListeners();
		
		TestCase.assertEquals(1, cls.size());
		TestCase.assertEquals(1, sls.size());
		TestCase.assertEquals(1, dls.size());
		TestCase.assertEquals(1, tls.size());
		
		TestCase.assertEquals(cl, cls.get(0));
		TestCase.assertEquals(sl, sls.get(0));
		TestCase.assertEquals(dl, dls.get(0));
		TestCase.assertEquals(tl, tls.get(0));
		
		// confirm duplicates are allowed
		w.addContactListener(cl);
		w.addStepListener(sl);
		w.addDestructionListener(dl);
		w.addTimeOfImpactListener(tl);
		
		TestCase.assertEquals(2, cls.size());
		TestCase.assertEquals(2, sls.size());
		TestCase.assertEquals(2, dls.size());
		TestCase.assertEquals(2, tls.size());
		
		// test removing a single one
		
		w.removeContactListener(cl);
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(1, cls.size());
		TestCase.assertEquals(2, sls.size());
		TestCase.assertEquals(2, dls.size());
		TestCase.assertEquals(2, tls.size());
		
		w.removeStepListener(sl);
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(1, cls.size());
		TestCase.assertEquals(1, sls.size());
		TestCase.assertEquals(2, dls.size());
		TestCase.assertEquals(2, tls.size());
		
		w.removeDestructionListener(dl);
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(1, cls.size());
		TestCase.assertEquals(1, sls.size());
		TestCase.assertEquals(1, dls.size());
		TestCase.assertEquals(2, tls.size());
		
		w.removeTimeOfImpactListener(tl);
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(1, cls.size());
		TestCase.assertEquals(1, sls.size());
		TestCase.assertEquals(1, dls.size());
		TestCase.assertEquals(1, tls.size());
		
		// test removing all of a specific type
		
		w.removeAllContactListeners();
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(0, cls.size());
		TestCase.assertEquals(1, sls.size());
		TestCase.assertEquals(1, dls.size());
		TestCase.assertEquals(1, tls.size());
		
		w.removeAllStepListeners();
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(0, cls.size());
		TestCase.assertEquals(0, sls.size());
		TestCase.assertEquals(1, dls.size());
		TestCase.assertEquals(1, tls.size());
		
		w.removeAllDestructionListeners();
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(0, cls.size());
		TestCase.assertEquals(0, sls.size());
		TestCase.assertEquals(0, dls.size());
		TestCase.assertEquals(1, tls.size());
		
		w.removeAllTimeOfImpactListeners();
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(0, cls.size());
		TestCase.assertEquals(0, sls.size());
		TestCase.assertEquals(0, dls.size());
		TestCase.assertEquals(0, tls.size());
		
		// test removing all
		
		w.addContactListener(cl);
		w.addStepListener(sl);
		w.addDestructionListener(dl);
		w.addTimeOfImpactListener(tl);
		w.removeAllListeners();
		cls = w.getContactListeners();
		sls = w.getStepListeners();
		dls = w.getDestructionListeners();
		tls = w.getTimeOfImpactListeners();
		TestCase.assertEquals(0, cls.size());
		TestCase.assertEquals(0, sls.size());
		TestCase.assertEquals(0, dls.size());
		TestCase.assertEquals(0, tls.size());
	}
	
	/**
	 * Tests the get/set AccumulatedTime methods.
	 */
	@Test
	public void getSetAccumulatedTime() {
		TestWorld w = new TestWorld();
		
		TestCase.assertEquals(0.0, w.getAccumulatedTime());
		
		double halfStep = Settings.DEFAULT_STEP_FREQUENCY * 0.5;
		w.update(halfStep);
		TestCase.assertEquals(halfStep, w.getAccumulatedTime());
		
		w.update(halfStep);
		TestCase.assertEquals(0.0, w.getAccumulatedTime());
		
		w.setAccumulatedTime(halfStep);
		TestCase.assertEquals(halfStep, w.getAccumulatedTime());
		
		// try setting to 0 or less
		
		w.setAccumulatedTime(0);
		TestCase.assertEquals(0.0, w.getAccumulatedTime());
		
		w.setAccumulatedTime(halfStep);
		w.setAccumulatedTime(-10);
		TestCase.assertEquals(halfStep, w.getAccumulatedTime());
	}
	
	/**
	 * Tests the get/set CoefficientMixer method.
	 */
	@Test
	@Deprecated
	public void getSetCoefficientMixer() {
		TestWorld w = new TestWorld();
		
		TestCase.assertFalse(CoefficientMixer.class == w.getValueMixer().getClass());
		
		CoefficientMixer cm = new CoefficientMixer() {
			@Override
			public double mixRestitution(double restitution1, double restitution2) { return (restitution1 + restitution2) * 0.5; }
			@Override
			public double mixFriction(double friction1, double friction2) { return (friction1 + friction2) * 0.5; }
			@Override
			public double mixRestitutionVelocity(double restitutionVelocity1, double restitutionVelocity2) { return 0; }
		};
		w.setCoefficientMixer(cm);
		
		TestCase.assertEquals(cm, w.getCoefficientMixer());
	}
	
	/**
	 * Tests the get/set ValueMixer method.
	 */
	@Test
	public void getSetValueMixer() {
		TestWorld w = new TestWorld();
		
		TestCase.assertEquals(ValueMixer.DEFAULT_MIXER, w.getValueMixer());
		
		ValueMixer cm = new ValueMixer() {
			@Override
			public double mixRestitution(double restitution1, double restitution2) { return (restitution1 + restitution2) * 0.5; }
			@Override
			public double mixFriction(double friction1, double friction2) { return (friction1 + friction2) * 0.5; }
			@Override
			public double mixRestitutionVelocity(double restitutionVelocity1, double restitutionVelocity2) { return 0; }
		};
		w.setValueMixer(cm);
		
		TestCase.assertEquals(cm, w.getValueMixer());
	}
	
	/**
	 * Tests the set coefficient mixer method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	@Deprecated
	public void setNullCoefficientMixer() {
		TestWorld w = new TestWorld();
		w.setCoefficientMixer(null);
	}
	
	/**
	 * Tests the set value mixer method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullVaueMixer() {
		TestWorld w = new TestWorld();
		w.setValueMixer(null);
	}
	
	/**
	 * Tests the get/set methods for the ContactConstraintSolver.
	 */
	@Test
	public void getSetContactConstraintSolver() {
		TestWorld w = new TestWorld();
		
		ContactConstraintSolver<Body> ccs = new ContactConstraintSolver<Body>() {
			@Override
			public void solveVelocityContraints(List<ContactConstraint<Body>> contactConstraints, TimeStep step, Settings settings) {}
			@Override
			public boolean solvePositionContraints(List<ContactConstraint<Body>> contactConstraints, TimeStep step, Settings settings) { return false; }
			@Override
			public void initialize(List<ContactConstraint<Body>> contactConstraints, TimeStep step, Settings settings) {}
		};
		w.setContactConstraintSolver(ccs);
		
		TestCase.assertEquals(ccs, w.getContactConstraintSolver());
	}
	
	/**
	 * Tests the set contact constraint solver method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullContactConstraintSolver() {
		TestWorld w = new TestWorld();
		w.setContactConstraintSolver(null);
	}
	
	/**
	 * Tests the get/set gravity methods.
	 */
	@Test
	public void getSetGravity() {
		TestWorld w = new TestWorld();
		
		TestCase.assertEquals(PhysicsWorld.EARTH_GRAVITY, w.getGravity());
		
		Vector2 g = new Vector2(0.0, -10.0);
		w.setGravity(g);
		TestCase.assertEquals(g, w.getGravity());
	}
	
	/**
	 * Tests the set gravity method passing a null value.
	 */
	@Test
	public void setNullGravity() {
		TestWorld w = new TestWorld();
		w.setGravity(0.0, -4.0);
		
		w.setGravity(null);
		
		TestCase.assertNotNull(w.getGravity());
		TestCase.assertEquals(0.0, w.getGravity().x);
		TestCase.assertEquals(-4.0, w.getGravity().y);
	}
	
	/**
	 * Tests the set CCD broadphase detector method.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullCCDBroadphaseDetector() {
		TestWorld w = new TestWorld();
		w.setContinuousCollisionDetectionBroadphaseDetector(null);
	}
	
	/**
	 * Tests the set CCD broadphase detector method.
	 */
	@Test
	public void getAndSetCCDBroadphaseDetector() {
		TestWorld w = new TestWorld();
		
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		w.addBody(b);
		
		BroadphaseDetector<Body> original = w.getContinuousCollisionDetectionBroadphaseDetector();
		TestCase.assertNotNull(original);
		
		BroadphaseFilter<Body> broadphaseFilter = new CollisionBodyBroadphaseFilter<Body>();
		AABBProducer<Body> aabbProducer = new CollisionBodyAABBProducer<Body>();
    	AABBExpansionMethod<Body> aabbExpansionMethod = new StaticValueAABBExpansionMethod<Body>(0.2);
    	BroadphaseDetector<Body> bd = new Sap<Body>(broadphaseFilter, aabbProducer, aabbExpansionMethod); 
		
		w.setContinuousCollisionDetectionBroadphaseDetector(bd);
		TestCase.assertSame(bd, w.getContinuousCollisionDetectionBroadphaseDetector());
		TestCase.assertNotSame(original, w.getContinuousCollisionDetectionBroadphaseDetector());
		
		// test bodies are re-added
		TestCase.assertTrue(w.ccdBroadphase.contains(b));
	}
	
	/**
	 * Tests the isJoined method.
	 */
	@Test
	public void isJoined() {
		TestWorld w = new TestWorld();
		
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
		TestWorld w = new TestWorld();
		
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
		TestWorld w = new TestWorld();
		
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
		TestWorld w = new TestWorld();
		
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
		TestWorld w = new TestWorld();
		
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
		TestWorld w = new TestWorld();
		
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
	 * Test the isInContact method.
	 */
	@Test
	public void isInContact() {
		TestWorld w = new TestWorld();
		
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
		
		TestCase.assertFalse(w.isInContact(b1, b2));
		
		w.step(1);
		
		TestCase.assertTrue(w.isInContact(b1, b2));
	}
	
	/**
	 * Tests bodies joined by multiple joints ensuring that the
	 * getJoinedBodies method only returns one instance of the joined
	 * body.
	 */
	@Test
	public void getJoinedBodiesMulti() {
		TestWorld w = new TestWorld();
		
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
		TestWorld w = new TestWorld();
		
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

	/**
	 * Tests the setSettings method.
	 * @since 3.0.3
	 */
	@Test
	public void setSettings() {
		TestWorld w = new TestWorld();
		Settings s = new Settings();
		s.setLinearTolerance(10000.0);
		w.setSettings(s);
		TestCase.assertNotSame(s, w.getSettings());
		TestCase.assertEquals(10000.0, w.getSettings().getLinearTolerance());
	}
	
	/**
	 * Tests setting the settings to null.
	 */
	@Test
	public void setNullSettings() {
		TestWorld w = new TestWorld();
		w.getSettings().setAngularTolerance(4.0);
		
		w.setSettings(null);
		
		TestCase.assertNotNull(w.getSettings());
		TestCase.assertEquals(4.0, w.getSettings().getAngularTolerance());
	}
	
	/**
	 * Tests the get/set methods for the TimeOfImpactSolver.
	 */
	@Test
	public void getSetTimeOfImpactSolver() {
		TestWorld w = new TestWorld();
		
		ForceCollisionTimeOfImpactSolver<Body> tois = new ForceCollisionTimeOfImpactSolver<Body>();
		w.setTimeOfImpactSolver(tois);
		
		TestCase.assertEquals(tois, w.getTimeOfImpactSolver());
	}
	
	/**
	 * Tests the set contact constraint solver method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTimeOfImpactSolver() {
		TestWorld w = new TestWorld();
		w.setTimeOfImpactSolver(null);
	}
	
	/**
	 * Tests the getTimeStep method.
	 */
	@Test
	public void getTimeStep() {
		TestWorld w = new TestWorld();
		
		TestCase.assertNotNull(w.getTimeStep());
		
		TimeStep step = w.getTimeStep();
		TestCase.assertEquals(Settings.DEFAULT_STEP_FREQUENCY, step.getDeltaTime());
		
		w.step(1);
		
		TestCase.assertEquals(Settings.DEFAULT_STEP_FREQUENCY, step.getDeltaTime());
	}

	/**
	 * Tests the isEmpty method.
	 * @since 3.0.2
	 */
	@Test
	public void isEmpty() {
		TestWorld w = new TestWorld();
		
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
	 * Tests the get/set UpdateRequired methods.
	 */
	@Test
	public void getSetUpdateRequired() {
		TestWorld w = new TestWorld();
		
		TestCase.assertTrue(w.isUpdateRequired());
		
		w.step(1);
		
		TestCase.assertFalse(w.isUpdateRequired());
		
		w.setUpdateRequired(true);
		
		TestCase.assertTrue(w.isUpdateRequired());
	}
	
	/**
	 * Test the shift method.
	 */
	@Test
	public void shift() {
		TestWorld w = new TestWorld();
		AxisAlignedBounds bounds = new AxisAlignedBounds(20, 20);
		w.setBounds(bounds);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		Joint<Body> joint = new PinJoint<Body>(b1, new Vector2(), 0.8, 0.2, 1000);
		
		w.addJoint(joint);
		
		w.shift(new Vector2(5.0, 6.0));
		
		TestCase.assertEquals(5.0, joint.getAnchor1().x);
		TestCase.assertEquals(6.0, joint.getAnchor1().y);
	}
	
	/**
	 * Tests the update method.
	 */
	@Test
	public void update() {
		Settings s = new Settings();
		TestWorld w = new TestWorld();
		
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
		
		w.setAccumulatedTime(0.0);
		TestCase.assertTrue(w.update(0.02, 0.01));
		TestCase.assertEquals(0.02 - w.getSettings().getStepFrequency(), w.getAccumulatedTime());
		
		w.setAccumulatedTime(0.0);
		TestCase.assertTrue(w.update(w.getSettings().getStepFrequency()*4, 0.01, 5));
		TestCase.assertEquals(0.0, w.getAccumulatedTime(), 1e-8);
	}
	
	/**
	 * Tests the updatev method.
	 */
	@Test
	public void updatev() {
		TestWorld w = new TestWorld();
		StepListenerCounter sl = new StepListenerCounter();
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
		TestWorld w = new TestWorld();
		StepListenerCounter sl = new StepListenerCounter();
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
		TestWorld w = new TestWorld();
		StepListenerCounter sl = new StepListenerCounter();
		w.addStepListener(sl);
		
		// make sure the specified number of steps are taken
		w.step(3, 1.0 / 50.0);
		TestCase.assertEquals(3, sl.steps);
		
		// test zero and negative
		w.step(0, 1.0 / 50.0);
		TestCase.assertEquals(3, sl.steps);
		w.step(-2, 1.0 / 50.0);
		TestCase.assertEquals(3, sl.steps);
		
		// test zero elapsed time (no steps should occur)
		w.step(1, 0);
		TestCase.assertEquals(3, sl.steps);
		
		// test negative elapsed time (no steps should occur)
		w.step(1, -1);
		TestCase.assertEquals(3, sl.steps);
	}
	
	/**
	 * Tests the remove body method.
	 */
	@Test
	public void removeBody() {
		TestWorld w = new TestWorld();
		
		// setup the destruction listener
		ContactListenerCounter cl = new ContactListenerCounter();
		DestructionListenerCounter dl = new DestructionListenerCounter();
		w.addContactListener(cl);
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
		TestCase.assertEquals(1, cl.destroyed);
		
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
		
		// test no notification
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// reset the destruction listener count
		dl.called = 0;
		
		TestCase.assertEquals(1, w.collisionData.size());
		
		// remove a body and make sure destruction events are called
		success = w.removeBody(b2, false);
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
		
		n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.contactConstraints.size());
		TestCase.assertEquals(0, n1.joints.size());

		n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNull(n2);

		// make sure the destruction listener was NOT called
		TestCase.assertEquals(0, dl.called);
		
		// make sure the collision data was cleaned up
		TestCase.assertEquals(0, w.collisionData.size());
	}
	
	/**
	 * Tests the remove body method.
	 */
	@Test
	public void removeBodyAtIndex() {
		TestWorld w = new TestWorld();
		
		// setup the destruction listener
		DestructionListenerCounter dl = new DestructionListenerCounter();
		w.addDestructionListener(dl);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// add them to the world
		w.addBody(b1);
		w.addBody(b2);
		
		// remove one of them
		boolean success = w.removeBody(0);
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
		success = w.removeBody(0, true);
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
		
		// test no notification
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// reset the destruction listener count
		dl.called = 0;
		
		TestCase.assertEquals(1, w.collisionData.size());
		
		// remove a body and make sure destruction events are called
		success = w.removeBody(1, false);
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
		
		n1 = w.constraintGraph.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(0, n1.contactConstraints.size());
		TestCase.assertEquals(0, n1.joints.size());

		n2 = w.constraintGraph.getNode(b2);
		TestCase.assertNull(n2);

		// make sure the destruction listener was NOT called
		TestCase.assertEquals(0, dl.called);
		
		// make sure the collision data was cleaned up
		TestCase.assertEquals(0, w.collisionData.size());
	}
	
	/**
	 * Tests the remove body method ensuring that leaked collisions are cleaned up.
	 */
	@Test
	public void removeBodyMemoryLeak() {
		TestWorld w = new TestWorld();
		
		// setup the destruction listener
		DestructionListenerCounter dl = new DestructionListenerCounter();
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
	 * Tests the removeAll method.
	 */
	@Test
	public void removeAll() {
		TestWorld w = new TestWorld();
		
		// setup the listener
		DestructionListenerCounter dl = new DestructionListenerCounter();
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
		TestWorld w = new TestWorld();
		
		// setup the listener
		ContactListenerCounter cl = new ContactListenerCounter();
		DestructionListenerCounter dl = new DestructionListenerCounter();
		w.addContactListener(cl);
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
		TestCase.assertEquals(1, cl.destroyed);
		TestCase.assertEquals(0, cl.end);
		
		// now try without notification
		dl.called = 0;
		cl.destroyed = 0;
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to generate contacts
		w.step(1);
		
		// call the remove all bodies method
		w.removeAllBodies();
		
		// this method should remove everything because joints cannot exist
		// without the bodies
		
		// verify that it cleared everything and made all the callbacks
		TestCase.assertEquals(0, w.constraintGraph.size());
		TestCase.assertEquals(0, w.collisionData.size());
		TestCase.assertEquals(0, w.joints.size());
		TestCase.assertEquals(0, w.bodies.size());
		
		// one contact, one joint, and two bodies
		TestCase.assertEquals(0, dl.called);
		TestCase.assertEquals(0, cl.destroyed);
		TestCase.assertEquals(0, cl.end);
	}

	/**
	 * Tests a bug introduced in 4.1.0 where the world would try to do a remove twice
	 * on an iterator and throw an IllegalStateException.
	 * @since 4.1.1
	 */
	@Test
	public void removeBodyIllegalStateException() {
		// NOTE: this method will throw an IllegalStateException in the following condition:
		// 1. Two bodies are overlapping wrt. CCD AABBs
		// 2. One of those bodies is removed
		// 3. Either of the removed bodies is moved to where they're AABBs are no longer overlapping
		// This causes the iterator.remove() method to be called twice, thus producing the
		// IllegalStateException.
		
		// This method sets up the above scenario and is successful if it DOES NOT throw
		// an IllegalStateException
		
		TestWorld w = new TestWorld();
		
		Body b1 = new Body();
		b1.addFixture(Geometry.createCircle(1.0));
		b1.setMass(MassType.NORMAL);
		w.addBody(b1);
		
		Body b2 = new Body();
		b2.addFixture(Geometry.createCircle(1.0));
		b2.setMass(MassType.NORMAL);
		w.addBody(b2);
		
		w.step(1);
		
		w.removeBody(b2);
		
		// force the problem case
		w.getContinuousCollisionDetectionBroadphaseDetector().setUpdated(b1);
		Transform tx = new Transform();
		tx.translate(10, 10);
		b2.setTransform(tx);
		b2.getPreviousTransform().set(b2.getTransform());
		
		w.step(1);
	}
	
	/**
	 * Make sure disabled or sensor contacts don't make it into
	 * the solve stage.
	 */
	@Test
	public void disabledAndSensor() {
		TestWorld world = new TestWorld();
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
	 * Tests the scenario where a sensor fixture is removed and as such
	 * triggers any sensor contact bodies to have their atRest flag reset.
	 * @since 4.1.4
	 */
	@Test
	public void sensorRemovalShouldNotClearAtRestState() {
		TestWorld w = new TestWorld();
		w.setGravity(0.0, 0.0);
		
		Body b1 = new Body();
		BodyFixture bf = b1.addFixture(Geometry.createCircle(1.0));
		bf.setSensor(true);
		b1.translate(-0.5, 0.0);
		b1.setMass(MassType.INFINITE);
		w.addBody(b1);
		
		Body b2 = new Body();
		b2.addFixture(Geometry.createCircle(1.0));
		b2.translate(0.4, 0.0);
		b2.setMass(MassType.NORMAL);
		w.addBody(b2);
		
		w.step(10);
		
		b2.setAtRest(true);
		TestCase.assertTrue(b2.isAtRest());
		
		b1.removeFixture(bf);
		TestCase.assertTrue(b2.isAtRest());
		
		// now try the remove all method
		
		b1.addFixture(bf);
		w.step(1);
		b2.setAtRest(true);
		TestCase.assertTrue(b2.isAtRest());
		
		b1.removeAllFixtures();
		TestCase.assertTrue(b2.isAtRest());
		
		// try removal when the state of the contact is disabled
		
		bf.setSensor(false);
		b1.addFixture(bf);
		
		w.addContactListener(new ContactListenerAdapter<Body>() {
			@Override
			public void collision(ContactCollisionData<Body> collision) {
				collision.getContactConstraint().setEnabled(false);
			}
		});
		
		w.step(1);
		b2.setAtRest(true);
		TestCase.assertTrue(b2.isAtRest());
		
		b1.removeFixture(bf);
		TestCase.assertTrue(b2.isAtRest());
	}
}