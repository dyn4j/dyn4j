/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
import java.util.Iterator;
import java.util.List;

import org.dyn4j.Listener;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.BoundsAdapter;
import org.dyn4j.collision.BoundsListener;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.dynamics.contact.ContactAdapter;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Contains the test cases for the {@link World} class.
 * @author William Bittle
 * @version 3.3.0
 * @since 1.0.2
 */
public class WorldTest {
	/**
	 * Step listener for testing.
	 * @author William Bittle
	 * @version 1.0.2
	 * @since 1.0.2
	 */
	public class WTStepListener implements StepListener {
		/** The number of steps performed */
		public int steps;
		@Override
		public void begin(Step step, World world) {}
		@Override
		public void end(Step step, World world) { steps++; }
		@Override
		public void updatePerformed(Step step, World world) {}
		@Override
		public void postSolve(Step step, World world) {}
	}
	
	/**
	 * Destruction listener for testing.
	 * @author William Bittle
	 * @version 1.0.2
	 * @since 1.0.2
	 */
	public class WTDestructionListener implements DestructionListener {
		/** The number of times called */
		public int called;
		@Override
		public void destroyed(ContactPoint contactPoint) { called++; }
		@Override
		public void destroyed(Joint joint) { called++; }
		@Override
		public void destroyed(Body body) { called++; }
	}
	
	/**
	 * Tests the successful creation of a world object.
	 */
	@Test
	public void createSuccess() {
		// test the two creation methods
		World w = new World();
		w = new World(new AxisAlignedBounds(1.0, 1.0));
		// make sure all the other junk is not null
		TestCase.assertNotNull(w.settings);
		TestCase.assertEquals(0, w.getBodyCount());
		TestCase.assertNotNull(w.bounds);
		TestCase.assertNotNull(w.broadphaseDetector);
		TestCase.assertNotNull(w.coefficientMixer);
		TestCase.assertNotNull(w.contactManager);
		TestCase.assertNotNull(w.gravity);
		TestCase.assertEquals(0, w.getJointCount());
		TestCase.assertNotNull(w.manifoldSolver);
		TestCase.assertNotNull(w.narrowphaseDetector);
		TestCase.assertNotNull(w.step);
		TestCase.assertNotNull(w.raycastDetector);
		TestCase.assertNotNull(w.timeOfImpactDetector);
		TestCase.assertEquals(0, w.getListenerCount());
	}
	
	/**
	 * Tests passing a null capacity.
	 * @since 3.1.1
	 */
	public void createFailureNullCapacity1() {
		new World((Capacity)null);
	}
	
	/**
	 * Tests passing a null capacity.
	 * @since 3.1.1
	 */
	public void createFailureNullCapacity2() {
		new World(null, new AxisAlignedBounds(1, 1));
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
		w.addListener(sl);
		
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
		w.addListener(sl);
		
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
		w.addListener(sl);
		
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
		b.addFixture(Geometry.createCapsule(1.0, 0.5));
		w.addBody(b);
		TestCase.assertFalse(0 == w.getBodyCount());
		// make sure the body's world reference is there
//		TestCase.assertNotNull(b.world);
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
		Body b1 = new Body();
		Body b2 = new Body();
		
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		TestCase.assertTrue(w.getJointCount() > 0);
		TestCase.assertFalse(b1.joints.isEmpty());
		TestCase.assertFalse(b2.joints.isEmpty());
	}

	/**
	 * Tests the add joint method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullJoint() {
		World w = new World();
		w.addJoint((Joint) null);
	}
	
	/**
	 * Tests the remove body method.
	 */
	@Test
	public void removeBody() {
		World w = new World();
		
		// setup the destruction listener
		WTDestructionListener dl = new WTDestructionListener();
		w.addListener(dl);
		
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
		TestCase.assertTrue(w.getBodyCount() > 0);
//		TestCase.assertNull(b1.world);
		
		// make sure it was added to the broadphase
		TestCase.assertFalse(w.broadphaseDetector.contains(b1));
		
		// add that one back
		w.addBody(b1);
//		TestCase.assertNotNull(b1.world);
		// create a joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// remove a body and make sure destruction events are called
		w.removeBody(b2, true);
		// make sure it was added to the broadphase
		TestCase.assertFalse(w.broadphaseDetector.contains(b2));
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.getJointCount());
		// make sure the world still has the first body
		TestCase.assertTrue(w.getBodyCount() > 0);
		TestCase.assertEquals(1, w.getBodyCount());
		// make sure it really is the first body
		TestCase.assertSame(b1, w.getBody(0));
		// make sure that the remaining body has no joints attached
		TestCase.assertTrue(b1.joints.isEmpty());
		// make sure that the remaining body has no contacts attached
		TestCase.assertTrue(b1.contacts.isEmpty());
		// make sure the removed body has no joints or contacts
		TestCase.assertTrue(b2.joints.isEmpty());
		TestCase.assertTrue(b2.contacts.isEmpty());
//		TestCase.assertNull(b2.world);
		// make sure the destruction listener was called for the one
		// joint and one contact
		TestCase.assertEquals(2, dl.called);
		// the contact manager should not have anything in the cache
		TestCase.assertTrue(w.contactManager.getContactCount() == 0);
	}
	
	/**
	 * Tests the remove joint method.
	 */
	@Test
	public void removeJoint() {
		World w = new World();
		
		// test removing a null body
		boolean success = w.removeJoint((Joint) null);
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
		success = w.removeBody(b1);
		TestCase.assertTrue(success);
		TestCase.assertTrue(w.getBodyCount() > 0);
		
		// add that one back
		w.addBody(b1);
		// create a joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.addJoint(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// remove a body and make sure destruction events are called
		w.removeJoint(j);
		// make sure the world has zero joints
		TestCase.assertEquals(0, w.getJointCount());
		// make sure the world still has both bodies
		TestCase.assertTrue(w.getBodyCount() > 0);
		TestCase.assertEquals(2, w.getBodyCount());
		// make sure that the remaining body has no joints attached
		TestCase.assertTrue(b1.joints.isEmpty());
		// make sure that no contacts were removed
		TestCase.assertFalse(b1.contacts.isEmpty());
		// make sure the removed body has no joints
		TestCase.assertTrue(b2.joints.isEmpty());
		// make sure that no contacts were removed
		TestCase.assertFalse(b2.contacts.isEmpty());
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
		BoundsListener bl = new BoundsAdapter();
		w.addListener(bl);
		
		TestCase.assertSame(bl, w.getListeners(BoundsListener.class).get(0));
	}
	
	/**
	 * Tests the add listener method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullBoundsListener() {
		World w = new World();
		w.addListener(null);
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
		w.addListener(dl);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// setup the joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to generate contacts
		w.step(1);
		
		// call the clear method
		w.removeAllBodiesAndJoints(true);
		
		// verify that it cleared everything and made all the callbacks
		TestCase.assertTrue(b1.contacts.isEmpty());
		TestCase.assertTrue(b1.joints.isEmpty());
		TestCase.assertTrue(b2.contacts.isEmpty());
		TestCase.assertTrue(b2.joints.isEmpty());
		TestCase.assertEquals(0, w.getJointCount());
		TestCase.assertEquals(0, w.getBodyCount());
//		TestCase.assertNull(b1.world);
//		TestCase.assertNull(b2.world);
		// one contact, one joint, and two bodies
		TestCase.assertEquals(4, dl.called);
		// the contact manager should not have anything in the cache
		TestCase.assertTrue(w.contactManager.getContactCount() == 0);
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
		w.addListener(dl);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.NORMAL);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.NORMAL);
		
		// setup the joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
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
		TestCase.assertTrue(b1.contacts.isEmpty());
		TestCase.assertTrue(b1.joints.isEmpty());
		TestCase.assertTrue(b2.contacts.isEmpty());
		TestCase.assertTrue(b2.joints.isEmpty());
		TestCase.assertEquals(0, w.getJointCount());
		TestCase.assertEquals(0, w.getBodyCount());
//		TestCase.assertNull(b1.world);
//		TestCase.assertNull(b2.world);
		// one contact, one joint, and two bodies
		TestCase.assertEquals(4, dl.called);
		// the contact manager should not have anything in the cache
		TestCase.assertTrue(w.contactManager.getContactCount() == 0);
	}
	
	/**
	 * Tests the removeAllJoints method.
	 * @since 3.0.2
	 */
	@Test
	public void removeAllJoints() {
		World w = new World();
		
		// setup the listener
		WTDestructionListener dl = new WTDestructionListener();
		w.addListener(dl);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass(MassType.INFINITE);
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass(MassType.INFINITE);
		
		// setup the joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		
		w.addBody(b1);
		w.addBody(b2);
		w.addJoint(j);
		
		// perform a world step to generate contacts
		w.step(1);
		
		// call the clear method
		w.removeAllJoints(true);
		
		// verify that it cleared all the joints from the bodies and world
		// and made all the callbacks
		TestCase.assertTrue(b1.joints.isEmpty());
		TestCase.assertTrue(b2.joints.isEmpty());
		TestCase.assertEquals(0, w.getJointCount());
		// one contact, one joint, and two bodies
		TestCase.assertEquals(1, dl.called);
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
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		
		w.addBody(b1);
		TestCase.assertFalse(w.isEmpty());
		
		w.removeAllBodiesAndJoints();
		TestCase.assertTrue(w.isEmpty());
		
		w.addJoint(j);
		TestCase.assertFalse(w.isEmpty());
		
		w.addBody(b1);
		w.addBody(b2);
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
		
		// the world should begin with no listeners
		List<Listener> listeners = w.getListeners(Listener.class);
		TestCase.assertEquals(0, listeners.size());
		
		// add some listeners
		BoundsAdapter ba = new BoundsAdapter();
		CollisionAdapter ca = new CollisionAdapter();
		DestructionAdapter da = new DestructionAdapter();
		RaycastAdapter ra = new RaycastAdapter();
		StepAdapter sa = new StepAdapter();
		TimeOfImpactAdapter ta = new TimeOfImpactAdapter();
		ContactAdapter na = new ContactAdapter();
		
		w.addListener(ba);
		w.addListener(ca);
		w.addListener(da);
		w.addListener(ra);
		w.addListener(sa);
		w.addListener(ta);
		w.addListener(na);
		
		// test the counting methods
		int n = w.getListenerCount();
		TestCase.assertEquals(7, n);
		
		n = w.getListenerCount(BoundsListener.class);
		TestCase.assertEquals(1, n);
		
		// we should have 7 listeners now
		listeners = w.getListeners(Listener.class);
		TestCase.assertEquals(7, listeners.size());
		
		// remove a listener
		TestCase.assertTrue(w.removeListener(ta));
		listeners = w.getListeners(Listener.class);
		TestCase.assertEquals(6, listeners.size());
		
		// attempt to get a specific type
		List<BoundsListener> bls = w.getListeners(BoundsListener.class);
		TestCase.assertEquals(1, bls.size());
		
		// test the multi-listener functionality
		w.addListener(new WTStepListener());
		w.addListener(new WTStepListener());
		w.step();
		List<WTStepListener> sls = w.getListeners(WTStepListener.class);
		TestCase.assertEquals(2, sls.size());
		// verify both were called
		TestCase.assertEquals(1, sls.get(0).steps);
		TestCase.assertEquals(1, sls.get(1).steps);
		
		// test the remove all listener's of a type
		n = w.removeAllListeners(WTStepListener.class);
		listeners = w.getListeners(Listener.class);
		TestCase.assertEquals(6, listeners.size());
		TestCase.assertEquals(2, n);
		List<WTStepListener> wtsls = w.getListeners(WTStepListener.class);
		TestCase.assertEquals(0, wtsls.size());
		
		// test removing all
		n = w.removeAllListeners();
		List<Listener> ls = w.getListeners(Listener.class);
		TestCase.assertEquals(0, ls.size());
		TestCase.assertEquals(6, n);
		
		// test get listeners passing a list
		w.addListener(ba);
		w.addListener(ca);
		w.addListener(da);
		w.addListener(ra);
		w.addListener(sa);
		w.addListener(ta);
		w.addListener(na);
		listeners.clear();
		w.getListeners(Listener.class, listeners);
		TestCase.assertEquals(7, listeners.size());
		// make sure we test that it doesn't clear the passed in list
		w.getListeners(Listener.class, listeners);
		TestCase.assertEquals(14, listeners.size());
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
	   World world = new World() ;
	   Circle c = new Circle(20.0) ;
	   ArrayList<DetectResult> result = new ArrayList<DetectResult>() ;
	   Body b = new Body();
	   b.addFixture(c) ;
	   world.addBody(b);
	   if (!world.detect(c, new CategoryFilter(2,15), true, false, result)) {
		   TestCase.fail();
	   }
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
		w.getJoints().add(new AngleJoint(new Body(), new Body()));
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
		
		w.addJoint(new AngleJoint(new Body(), new Body()));
		w.addJoint(new AngleJoint(new Body(), new Body()));
		w.addJoint(new AngleJoint(new Body(), new Body()));
		w.addJoint(new AngleJoint(new Body(), new Body()));
		
		Iterator<Joint> it = w.getJointIterator();
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
		b1.translate(-1.0, 0.25);
		b1.setMass(MassType.NORMAL);
		world.addBody(b1);
		
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		f2.setSensor(true);
		b2.translate(-0.25, 0.25);
		b2.setMass(MassType.NORMAL);
		world.addBody(b2);
		
		Body b3 = new Body();
		b3.addFixture(c3);
		b3.translate(0.0, -0.75);
		b3.setMass(MassType.NORMAL);
		world.addBody(b3);
		
		Body b4 = new Body();
		b4.addFixture(c4);
		b4.translate(0.0, 1.0);
		b4.setMass(MassType.NORMAL);
		world.addBody(b4);
		
		// override the contact adapter to simulate a disabled contact
		world.addListener(new ContactAdapter() {
			@Override
			public boolean begin(ContactPoint point) {
				if ((point.getBody1() == b1 && point.getBody2() == b2) ||
					(point.getBody1() == b2 && point.getBody2() == b1)) {
					return false;
				}
				return super.begin(point);
			}
		});
		
		// override the contact constraint solver
		// to check for any disabled or sensor contacts
		world.setContactConstraintSolver(new SequentialImpulses<Body>() {
			@Override
			public void initialize(List<ContactConstraint<Body>> contactConstraints, TimeStep step, Settings settings) {
				for (ContactConstraint cc : contactConstraints) {
					if (!cc.isEnabled() || cc.isSensor()) {
						TestCase.fail();
					}
				}
				super.initialize(contactConstraints, step, settings);
			}
		});
		
		world.step(1);
	}
}
