/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.dynamics;

import junit.framework.TestCase;

import org.dyn4j.game2d.collision.BoundsAdapter;
import org.dyn4j.game2d.collision.BoundsListener;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.collision.broadphase.BroadphaseDetector;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.continuous.ConservativeAdvancement;
import org.dyn4j.game2d.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.ManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.dynamics.contact.ContactAdapter;
import org.dyn4j.game2d.dynamics.contact.ContactListener;
import org.dyn4j.game2d.dynamics.contact.ContactManager;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Vector2;
import org.junit.Test;

/**
 * Contains the test cases for the {@link World} class.
 * @author William Bittle
 * @version 2.2.3
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
	 * Contact manager for testing.
	 * @author William Bittle
	 * @version 1.0.2
	 * @since 1.0.2
	 */
	public class WTContactManager extends ContactManager {
		/**
		 * Returns true if the contact manager has no cached contacts.
		 * @return boolean
		 */
		public boolean isEmpty() {
			return this.map.isEmpty();
		}
	}
	
	/**
	 * Tests the successful creation of a world object.
	 */
	@Test
	public void createSuccess() {
		// test the two creation methods
		World w = new World();
		w = new World(new RectangularBounds(Geometry.createRectangle(1.0, 1.0)));
		// make sure all the other junk is not null
		TestCase.assertNotNull(w.bodies);
		TestCase.assertNotNull(w.bounds);
		TestCase.assertNotNull(w.boundsListener);
		TestCase.assertNotNull(w.broadphaseDetector);
		TestCase.assertNotNull(w.coefficientMixer);
		TestCase.assertNotNull(w.collisionListener);
		TestCase.assertNotNull(w.contactManager);
		TestCase.assertNotNull(w.destructionListener);
		TestCase.assertNotNull(w.gravity);
		TestCase.assertNotNull(w.island);
		TestCase.assertNotNull(w.joints);
		TestCase.assertNotNull(w.manifoldSolver);
		TestCase.assertNotNull(w.narrowphaseDetector);
		TestCase.assertNotNull(w.step);
		TestCase.assertNotNull(w.stepListener);
		TestCase.assertNotNull(w.raycastDetector);
		TestCase.assertNotNull(w.raycastListener);
		TestCase.assertNotNull(w.timeOfImpactDetector);
		TestCase.assertNotNull(w.timeOfImpactListener);
	}
	
	/**
	 * Tests the update method.
	 */
	@Test
	public void update() {
		Settings s = Settings.getInstance();
		World w = new World();
		
		// update using enough elapsed time to make a step happen
		boolean stepped = w.update(s.getStepFrequency());
		TestCase.assertTrue(stepped);
		
		stepped = w.update(s.getStepFrequency() * 2.0);
		TestCase.assertTrue(stepped);
		
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
		w.setStepListener(sl);
		
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
		w.setStepListener(sl);
		
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
		w.setStepListener(sl);
		
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
		w.add(b);
		TestCase.assertFalse(w.bodies.isEmpty());
	}
	
	/**
	 * Tests the add body method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullBody() {
		World w = new World();
		w.add((Body) null);
	}
	
	/**
	 * Tests the add body method attempting to add the
	 * same body more than once.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addSameBody() {
		World w = new World();
		Body b1 = new Body();
		w.add(b1);
		w.add(b1);
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
		
		w.add(b1);
		w.add(b2);
		w.add(j);
		
		TestCase.assertFalse(w.joints.isEmpty());
		TestCase.assertFalse(b1.joints.isEmpty());
		TestCase.assertFalse(b2.joints.isEmpty());
	}

	/**
	 * Tests the add joint method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullJoint() {
		World w = new World();
		w.add((Joint) null);
	}
	
	/**
	 * Tests the add body method attempting to add the
	 * same body more than once.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addSameJoint() {
		World w = new World();
		Body b1 = new Body();
		Body b2 = new Body();
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		w.add(j);
		w.add(j);
	}
	
	/**
	 * Tests the remove body method.
	 */
	@Test
	public void removeBody() {
		World w = new World();
		
		// setup the destruction listener
		WTDestructionListener dl = new WTDestructionListener();
		w.setDestructionListener(dl);
		
		// setup the contact manager
		WTContactManager cm = new WTContactManager();
		w.setContactManager(cm);
		
		// test removing a null body
		boolean success = w.remove((Body) null);
		TestCase.assertFalse(success);
		
		// test removing a body not in the list
		success = w.remove(new Body());
		TestCase.assertFalse(success);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass();
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass();
		
		// add them to the world
		w.add(b1);
		w.add(b2);
		// remove one of them
		success = w.remove(b1);
		TestCase.assertTrue(success);
		TestCase.assertFalse(w.bodies.isEmpty());
		
		// add that one back
		w.add(b1);
		// create a joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.add(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// remove a body and make sure destruction events are called
		w.remove(b2);
		// make sure the world has zero joints
		TestCase.assertTrue(w.joints.isEmpty());
		// make sure the world still has the first body
		TestCase.assertFalse(w.bodies.isEmpty());
		TestCase.assertEquals(1, w.bodies.size());
		// make sure it really is the first body
		TestCase.assertSame(b1, w.bodies.get(0));
		// make sure that the remaining body has no joints attached
		TestCase.assertTrue(b1.joints.isEmpty());
		// make sure that the remaining body has no contacts attached
		TestCase.assertTrue(b1.contacts.isEmpty());
		// make sure the removed body has no joints or contacts
		TestCase.assertTrue(b2.joints.isEmpty());
		TestCase.assertTrue(b2.contacts.isEmpty());
		// make sure the destruction listener was called for the one
		// joint and one contact
		TestCase.assertEquals(2, dl.called);
		// the contact manager should not have anything in the cache
		TestCase.assertTrue(cm.isEmpty());
	}
	
	/**
	 * Tests the remove joint method.
	 */
	@Test
	public void removeJoint() {
		World w = new World();
		
		// test removing a null body
		boolean success = w.remove((Joint) null);
		TestCase.assertFalse(success);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass();
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass();
		
		// add them to the world
		w.add(b1);
		w.add(b2);
		// remove one of them
		success = w.remove(b1);
		TestCase.assertTrue(success);
		TestCase.assertFalse(w.bodies.isEmpty());
		
		// add that one back
		w.add(b1);
		// create a joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		w.add(j);
		
		// perform a world step to get contacts
		w.step(1);
		
		// remove a body and make sure destruction events are called
		w.remove(j);
		// make sure the world has zero joints
		TestCase.assertTrue(w.joints.isEmpty());
		// make sure the world still has both bodies
		TestCase.assertFalse(w.bodies.isEmpty());
		TestCase.assertEquals(2, w.bodies.size());
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
		RectangularBounds rb = new RectangularBounds(Geometry.createRectangle(1.0, 1.0));
		w.setBounds(rb);
		
		TestCase.assertSame(rb, w.getBounds());
	}
	
	/**
	 * Tests the set bounds listener method.
	 */
	@Test
	public void setBoundsListener() {
		World w = new World();
		BoundsListener bl = new BoundsAdapter();
		w.setBoundsListener(bl);
		
		TestCase.assertSame(bl, w.getBoundsListener());
	}
	
	/**
	 * Tests the set bounds listener method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullBoundsListener() {
		World w = new World();
		w.setBoundsListener(null);
	}
	
	/**
	 * Tests the set contact listener method.
	 */
	@Test
	public void setContactListener() {
		World w = new World();
		ContactListener cl = new ContactAdapter();
		w.setContactListener(cl);
		
		TestCase.assertSame(cl, w.getContactListener());
	}
	
	/**
	 * Tests the set contact listener method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullContactListener() {
		World w = new World();
		w.setContactListener(null);
	}
	
	/**
	 * Tests the set time of impact listener method.
	 * @since 1.2.0
	 */
	@Test
	public void setTimeOfImpactListener() {
		World w = new World();
		TimeOfImpactListener toil = new TimeOfImpactAdapter();
		w.setTimeOfImpactListener(toil);
		
		TestCase.assertSame(toil, w.getTimeOfImpactListener());
	}
	
	/**
	 * Tests the set time of impact listener method passing a null value.
	 * @since 1.2.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTimeOfImpactListener() {
		World w = new World();
		w.setTimeOfImpactListener(null);
	}
	
	/**
	 * Tests the set raycast listener method.
	 * @since 2.0.0
	 */
	@Test
	public void setRaycastListener() {
		World w = new World();
		w.setRaycastListener(new RaycastAdapter());
	}
	
	/**
	 * Tests the set raycast listener method passing a null value.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullRaycastListener() {
		World w = new World();
		w.setRaycastListener(null);
	}
	
	/**
	 * Tests the set destruction listener method.
	 */
	@Test
	public void setDestructionListener() {
		World w = new World();
		DestructionListener dl = new DestructionAdapter();
		w.setDestructionListener(dl);
		
		TestCase.assertSame(dl, w.getDestructionListener());
	}
	
	/**
	 * Tests the set destruction listener method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullDestructionListener() {
		World w = new World();
		w.setDestructionListener(null);
	}
	
	/**
	 * Tests the set step listener method.
	 */
	@Test
	public void setStepListener() {
		World w = new World();
		StepListener sl = new StepAdapter();
		w.setStepListener(sl);
		
		TestCase.assertSame(sl, w.getStepListener());
	}
	
	/**
	 * Tests the set step listener method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullStepListener() {
		World w = new World();
		w.setStepListener(null);
	}
	
	/**
	 * Tests the set collision listener method.
	 */
	@Test
	public void setCollisionListener() {
		World w = new World();
		CollisionListener cl = new CollisionAdapter();
		w.setCollisionListener(cl);
		
		TestCase.assertSame(cl, w.getCollisionListener());
	}
	
	/**
	 * Tests the set collision listener method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullCollisionListener() {
		World w = new World();
		w.setCollisionListener(null);
	}
	
	/**
	 * Tests the set contact manager method.
	 */
	@Test
	public void setContactManager() {
		World w = new World();
		ContactManager cm = new ContactManager();
		w.setContactManager(cm);
		
		TestCase.assertSame(cm, w.getContactManager());
	}
	
	/**
	 * Tests the set contact manager method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullContactManager() {
		World w = new World();
		w.setContactManager(null);
	}
	
	/**
	 * Tests the set broadphase detector method.
	 */
	@Test
	public void setBroadphaseDetector() {
		World w = new World();
		BroadphaseDetector bd = new Sap();
		w.setBroadphaseDetector(bd);
		
		TestCase.assertSame(bd, w.getBroadphaseDetector());
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
	 * Tests the set time of impact solver method.
	 */
	@Test
	public void setTimeOfImpactSolver() {
		World w = new World();
		TimeOfImpactSolver tois = new TimeOfImpactSolver();
		w.setTimeOfImpactSolver(tois);
		
		TestCase.assertSame(tois, w.getTimeOfImpactSolver());
	}
	
	/**
	 * Tests the set time of impact solver method.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTimeOfImpactSolver() {
		World w = new World();
		w.setTimeOfImpactSolver(null);
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		World w = new World();
		
		// setup the listener
		WTDestructionListener dl = new WTDestructionListener();
		w.setDestructionListener(dl);
		
		// setup the contact manager
		WTContactManager cm = new WTContactManager();
		w.setContactManager(cm);
		
		// setup the bodies
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Body b1 = new Body(); b1.addFixture(c1); b1.setMass();
		Body b2 = new Body(); b2.addFixture(c2); b2.setMass();
		
		// setup the joint
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		j.setCollisionAllowed(true);
		
		w.add(b1);
		w.add(b2);
		w.add(j);
		
		// perform a world step to generate contacts
		w.step(1);
		
		// call the clear method
		w.clear(true);
		
		// verify that it cleared everything and made all the callbacks
		TestCase.assertTrue(b1.contacts.isEmpty());
		TestCase.assertTrue(b1.joints.isEmpty());
		TestCase.assertTrue(b2.contacts.isEmpty());
		TestCase.assertTrue(b2.joints.isEmpty());
		TestCase.assertTrue(w.joints.isEmpty());
		TestCase.assertTrue(w.bodies.isEmpty());
		// one contact, one joint, and two bodies
		TestCase.assertEquals(4, dl.called);
		// the contact manager should not have anything in the cache
		TestCase.assertTrue(cm.isEmpty());
	}
}
