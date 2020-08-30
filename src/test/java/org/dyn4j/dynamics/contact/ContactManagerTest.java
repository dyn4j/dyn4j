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
package org.dyn4j.dynamics.contact;

import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactAdapter;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactManager;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.DefaultContactManager;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link DefaultContactManager} class.
 * @author William Bittle
 * @version 3.3.0
 * @since 1.0.2
 */
@Deprecated
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ContactManagerTest {
	/** The world */
	private World world;
	
	/** The contact manager */
	private ContactManager contactManager;
	
	/** The contact listener */
	private CMTContactListener contactListener;
	
	/**
	 * Contact listener class for testing.
	 * @author William Bittle
	 * @version 1.0.3
	 * @since 1.0.2
	 */
	public class CMTContactListener extends ContactAdapter {
		/** The number of contacts added */
		public int added;
		/** The number of contacts removed */
		public int removed;
		/** The number of contacts persisted */
		public int persisted;
		/** The number of contacts sensed */
		public int sensed;
		/** The number of contacts that will be solved */
		public int preSolve;
		/** The number of contacts that were solved */
		public int postSolve;
		
		@Override
		public boolean begin(ContactPoint point) {
			if (point.isSensor()) this.sensed++;
			this.added++; 
			return true; 
		}
		
		@Override
		public void end(ContactPoint point) {
			if (point.isSensor()) this.sensed++;
			this.removed++; 
		}
		
		@Override
		public boolean persist(PersistedContactPoint point) {
			if (point.isSensor()) this.sensed++;
			this.persisted++; 
			return true; 
		}
		
		// this shouldn't be called for sensors
		@Override
		public void postSolve(SolvedContactPoint point) {
			if (point.isSensor()) {
				TestCase.fail("This should not be called for sensor contacts.");
			}
			this.postSolve++; 
		}
		
		// this shouldn't be called for sensors
		@Override
		public boolean preSolve(ContactPoint point) {
			if (point.isSensor()) {
				TestCase.fail("This should not be called for sensor contacts.");
			}
			this.preSolve++; 
			return true; 
		}
		
		// this shouldn't be called anymore
		@Deprecated
		@Override
		public void sensed(ContactPoint point) { 
			TestCase.fail("This method should not be called.");
		}
		
		/**
		 * Clears the counters.
		 */
		public void clear() {
			this.added = 0;
			this.removed = 0;
			this.persisted = 0;
			this.sensed = 0;
			this.preSolve = 0;
			this.postSolve = 0;
		}
	}
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.world = new World();
		this.contactManager = this.world.getContactManager();
		
		this.contactListener = new CMTContactListener();
		this.world.addListener(this.contactListener);
		
		Gjk gjk = new Gjk();
		ClippingManifoldSolver cms = new ClippingManifoldSolver();
		
		// add some contacts to the manager simulating the first iteration
		Convex c1 = Geometry.createUnitCirclePolygon(6, 0.7);
		Convex c2 = Geometry.createEquilateralTriangle(1.0);
		Convex c3 = Geometry.createCircle(1.0);
		Convex c4 = Geometry.createSquare(1.2);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		b1.translate(-1.0, 0.25);
		
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		f2.setSensor(true);
		b2.translate(-0.25, 0.25);
		
		Body b3 = new Body();
		BodyFixture f3 = b3.addFixture(c3);
		b3.translate(0.75, -0.25);
		
		Body b4 = new Body();
		BodyFixture f4 = b4.addFixture(c4);
		b4.translate(0.0, 1.0);
		
		// get contacts
		Penetration p = new Penetration();
		Manifold m = new Manifold();
		ContactConstraint cc = null;
		
		// b1 - b2 (2 contacts)
		if (gjk.detect(c1, b1.getTransform(), c2, b2.getTransform(), p)) {
			cms.getManifold(p, c1, b1.getTransform(), c2, b2.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b1, f1, b2, f2), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b1 - b3 (2 contacts)
		if (gjk.detect(c1, b1.getTransform(), c3, b3.getTransform(), p)) {
			cms.getManifold(p, c1, b1.getTransform(), c3, b3.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b1, f1, b3, f3), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b1 - b4 (2 contacts)
		if (gjk.detect(c1, b1.getTransform(), c4, b4.getTransform(), p)) {
			cms.getManifold(p, c1, b1.getTransform(), c4, b4.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b1, f1, b4, f4), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b2 - b3 (1 contact)
		p.clear(); m.clear();
		if (gjk.detect(c2, b2.getTransform(), c3, b3.getTransform(), p)) {
			cms.getManifold(p, c2, b2.getTransform(), c3, b3.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b2, f2, b3, f3), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b2 - b4 (1 contact)
		p.clear(); m.clear();
		if (gjk.detect(c2, b2.getTransform(), c4, b4.getTransform(), p)) {
			cms.getManifold(p, c2, b2.getTransform(), c4, b4.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b2, f2, b4, f4), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b3 - b4 (1 contact)
		p.clear(); m.clear();
		if (gjk.detect(c3, b3.getTransform(), c4, b4.getTransform(), p)) {
			cms.getManifold(p, c3, b3.getTransform(), c4, b4.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b3, f3, b4, f4), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// perform one update (since there is nothing yet)
		this.contactManager.updateAndNotify(this.world.getListeners(ContactListener.class), this.world.getSettings());
		this.contactListener.clear();
		
		// now move some objects so that some objects are sensed, some are
		// persisted, some are removed, and some are added
		
		b3.translate(-0.75, -0.50);
		
		// b1 - b2 (2 contacts)
		if (gjk.detect(c1, b1.getTransform(), c2, b2.getTransform(), p)) {
			cms.getManifold(p, c1, b1.getTransform(), c2, b2.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b1, f1, b2, f2), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b1 - b3 (2 contacts)
		if (gjk.detect(c1, b1.getTransform(), c3, b3.getTransform(), p)) {
			cms.getManifold(p, c1, b1.getTransform(), c3, b3.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b1, f1, b3, f3), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b1 - b4 (2 contacts)
		if (gjk.detect(c1, b1.getTransform(), c4, b4.getTransform(), p)) {
			cms.getManifold(p, c1, b1.getTransform(), c4, b4.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b1, f1, b4, f4), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b2 - b3 (1 contact)
		p.clear(); m.clear();
		if (gjk.detect(c2, b2.getTransform(), c3, b3.getTransform(), p)) {
			cms.getManifold(p, c2, b2.getTransform(), c3, b3.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b2, f2, b3, f3), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b2 - b4 (1 contact)
		p.clear(); m.clear();
		if (gjk.detect(c2, b2.getTransform(), c4, b4.getTransform(), p)) {
			cms.getManifold(p, c2, b2.getTransform(), c4, b4.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b2, f2, b4, f4), m, 0, 0);
			this.contactManager.queue(cc);
		}
		
		// b3 - b4 (1 contact)
		p.clear(); m.clear();
		if (gjk.detect(c3, b3.getTransform(), c4, b4.getTransform(), p)) {
			cms.getManifold(p, c3, b3.getTransform(), c4, b4.getTransform(), m);
			cc = new ContactConstraint(new BasicCollisionPair(b3, f3, b4, f4), m, 0, 0);
			this.contactManager.queue(cc);
		}
	}
	
	/**
	 * Tests the queue and end methods.
	 */
	@Test
	public void addRemove() {
		Manifold manifold = new Manifold();
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp.setDepth(1.0);
		manifold.getPoints().add(mp);
		ContactConstraint cc = new ContactConstraint(new BasicCollisionPair(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0))), 
				manifold, 
				0, 0);
		
		ContactManager cm = new DefaultContactManager();
		cm.queue(cc);
		cm.updateAndNotify(null, new Settings());
		
		TestCase.assertFalse(cm.getContactCount() == 0);
		// remove should remove the contact from the cache
		TestCase.assertTrue(cm.end(cc));
		TestCase.assertTrue(cm.getContactCount() == 0);
		
		TestCase.assertFalse(cm.end(cc));
	}
	
	/**
	 * Tests the shift coordinates method.
	 */
	@Test
	public void shiftCoordinates() {
		Manifold manifold = new Manifold();
		manifold.getNormal().x = 1.0;
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp.getPoint().x = 2.0;
		mp.setDepth(1.0);
		manifold.getPoints().add(mp);
		ContactConstraint<Body> cc = new ContactConstraint(new BasicCollisionPair(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0))), 
				manifold, 
				0, 0);
		
		ContactManager cm = new DefaultContactManager();
		cm.queue(cc);
		cm.updateAndNotify(null, new Settings());
		
		cm.shift(new Vector2(2.0, -1.0));
		
		// make sure the point has been moved
		TestCase.assertEquals(4.0, cc.getContacts().get(0).getPoint().x);
		TestCase.assertEquals(-1.0, cc.getContacts().get(0).getPoint().y);
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Manifold manifold = new Manifold();
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp.setDepth(1.0);
		manifold.getPoints().add(mp);
		ContactConstraint cc = new ContactConstraint(new BasicCollisionPair(
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0)), 
				new Body(), 
				new BodyFixture(Geometry.createCircle(1.0))), 
				manifold, 
				0, 0);
		
		ContactManager cm = new DefaultContactManager();
		cm.queue(cc);
		
		TestCase.assertTrue(cm.getQueueCount() == 1);
		TestCase.assertTrue(cm.getContactCount() == 0);
		
		cm.updateAndNotify(null, new Settings());
		
		TestCase.assertTrue(cm.getQueueCount() == 0);
		TestCase.assertTrue(cm.getContactCount() == 1);
		
		cm.queue(cc);
		
		TestCase.assertTrue(cm.getQueueCount() == 1);
		TestCase.assertTrue(cm.getContactCount() == 1);
		
		cm.clear();
		
		TestCase.assertTrue(cm.getQueueCount() == 0);
		TestCase.assertTrue(cm.getContactCount() == 0);
	}
	
	/**
	 * Tests the update contacts method.
	 */
	@Test
	public void updateContacts() {
		// call the update contacts method
		this.contactManager.updateAndNotify(this.world.getListeners(ContactListener.class), this.world.getSettings());
		this.contactManager.preSolveNotify(this.world.getListeners(ContactListener.class));
		this.contactManager.postSolveNotify(this.world.getListeners(ContactListener.class));
		
		// verify that the contact listener received the correct events
		// the one between b1 and b4
		TestCase.assertEquals(5, this.contactListener.persisted);
		// the one between b1 and b3
		TestCase.assertEquals(1, this.contactListener.added);
		// the one between b3 and b4
		TestCase.assertEquals(1, this.contactListener.removed);
		// the ones between b2 and b1, b3, and b4
		// this should be zero now that sensed contacts will come from the other notifications
		TestCase.assertEquals(4, this.contactListener.sensed); 
		// the one persisted and the one added
		TestCase.assertEquals(2, this.contactListener.preSolve);
		TestCase.assertEquals(2, this.contactListener.postSolve);
	}
	
	/**
	 * Tests the creation of the contact manager with a null capacity.
	 * @since 3.1.1
	 */
	public void createSuccessNullCapacity() {
		new DefaultContactManager(null);
	}
}
