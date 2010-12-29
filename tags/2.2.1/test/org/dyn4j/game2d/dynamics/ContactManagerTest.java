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

import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactListener;
import org.dyn4j.game2d.dynamics.contact.ContactManager;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.contact.PersistedContactPoint;
import org.dyn4j.game2d.dynamics.contact.SolvedContactPoint;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link ContactManager} class.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.2
 */
public class ContactManagerTest {
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
	public class CMTContactListener implements ContactListener {
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
		public boolean begin(ContactPoint point) { this.added++; return true; }
		@Override
		public boolean end(ContactPoint point) { this.removed++; return true; }
		@Override
		public boolean persist(PersistedContactPoint point) { this.persisted++; return true; }
		@Override
		public void postSolve(SolvedContactPoint point) { this.postSolve++; }
		@Override
		public boolean preSolve(ContactPoint point) { this.preSolve++; return true; }
		@Override
		public void sensed(ContactPoint point) { this.sensed++; }
		
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
		this.contactManager = new ContactManager();
		this.contactListener = new CMTContactListener();
		
		Gjk gjk = new Gjk();
		ClippingManifoldSolver cms = new ClippingManifoldSolver();
		
		// add some contacts to the manager simulating the first iteration
		Convex c1 = Geometry.createUnitCirclePolygon(6, 0.7);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Convex c3 = Geometry.createCircle(1.0);
		Convex c4 = Geometry.createSquare(1.2);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		Body b3 = new Body();
		BodyFixture f3 = b3.addFixture(c3);
		
		Body b4 = new Body();
		BodyFixture f4 = b4.addFixture(c4);
		f4.setSensor(true);
		
		// get contacts
		Penetration p = new Penetration();
		Manifold m = new Manifold();
		ContactConstraint cc = null;
		
		// b1 - b2 (2 contacts)
		gjk.detect(c1, b1.transform, c2, b2.transform, p);
		cms.getManifold(p, c1, b1.transform, c2, b2.transform, m);
		cc = new ContactConstraint(b1, f1, b2, f2, m, 0.2, 0.3);
		this.contactManager.add(cc);
		
		// b1 - b3 (1 contacts)
		p.clear(); m.clear();
		gjk.detect(c1, b1.transform, c3, b3.transform, p);
		cms.getManifold(p, c1, b1.transform, c3, b3.transform, m);
		cc = new ContactConstraint(b1, f1, b3, f3, m, 0.2, 0.3);
		this.contactManager.add(cc);
		
		// b2 - b3 (1 contacts)
		p.clear(); m.clear();
		gjk.detect(c2, b2.transform, c3, b3.transform, p);
		cms.getManifold(p, c2, b2.transform, c3, b3.transform, m);
		cc = new ContactConstraint(b2, f2, b3, f3, m, 0.2, 0.3);
		this.contactManager.add(cc);
		
		// perform one upadate (since there is nothing yet)
		this.contactManager.updateContacts(this.contactListener);
		this.contactManager.clear();
		this.contactListener.clear();
		
		// now move some objects so that some objects are sensed, some are
		// persisted, some are removed, and some are added
		
		b1.translate(0.3, -0.5);
		b1.rotateAboutCenter(Math.PI / 6.0);
		
		// b1 - b2 (2 contacts)
		p.clear(); m.clear();
		gjk.detect(c1, b1.transform, c2, b2.transform, p);
		cms.getManifold(p, c1, b1.transform, c2, b2.transform, m);
		cc = new ContactConstraint(b1, f1, b2, f2, m, 0.2, 0.3);
		this.contactManager.add(cc);
		
		// b1 - b3 (1 contacts)
		p.clear(); m.clear();
		gjk.detect(c1, b1.transform, c3, b3.transform, p);
		cms.getManifold(p, c1, b1.transform, c3, b3.transform, m);
		cc = new ContactConstraint(b1, f1, b3, f3, m, 0.2, 0.3);
		this.contactManager.add(cc);
		
		// b2 - b3 (1 contacts)
		p.clear(); m.clear();
		gjk.detect(c2, b2.transform, c3, b3.transform, p);
		cms.getManifold(p, c2, b2.transform, c3, b3.transform, m);
		cc = new ContactConstraint(b2, f2, b3, f3, m, 0.2, 0.3);
		this.contactManager.add(cc);
		
		// do one sensor contact
		// b1 - b4  (2 contacts)
		p.clear(); m.clear();
		gjk.detect(c1, b1.transform, c4, b4.transform, p);
		cms.getManifold(p, c1, b1.transform, c4, b4.transform, m);
		cc = new ContactConstraint(b1, f1, b4, f4, m, 0.2, 0.3);
		this.contactManager.add(cc);
	}
	
	/**
	 * Tests the update contacts method.
	 */
	@Test
	public void updateContacts() {
		// call the update contacts method
		this.contactManager.updateContacts(this.contactListener);
		this.contactManager.preSolveNotify(this.contactListener);
		this.contactManager.postSolveNotify(this.contactListener);
		
		// verify that the contact listener received the correct events
		TestCase.assertEquals(2, this.contactListener.persisted);
		TestCase.assertEquals(2, this.contactListener.added);
		TestCase.assertEquals(2, this.contactListener.removed);
		TestCase.assertEquals(2, this.contactListener.sensed);
		TestCase.assertEquals(4, this.contactListener.preSolve);
		TestCase.assertEquals(4, this.contactListener.postSolve);
	}
}
