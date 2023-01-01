/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.manifold.IndexedManifoldPointId;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ValueMixer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the methods of the {@link ContactConstraint} class.
 * @author William Bittle
 * @version 5.0.1
 * @since 4.0.0
 */
public class ContactConstraintTest {
	private Body b1;
	private Body b2;
	private BodyFixture f1;
	private BodyFixture f2;
	private CollisionPair<CollisionItem<Body, BodyFixture>> cp;
	private CountingContactUpdateHandler cuh;
	
	private class CountingContactUpdateHandler implements ContactUpdateHandler {
		private final ValueMixer cm = ValueMixer.DEFAULT_MIXER;
		private int persist;
		private int end;
		private int begin;
		
		@Override
		public double getRestitution(BodyFixture fixture1, BodyFixture fixture2) { return cm.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution()); }
		@Override
		public double getFriction(BodyFixture fixture1, BodyFixture fixture2) { return cm.mixFriction(fixture1.getFriction(), fixture2.getFriction()); }
		@Override
		public double getRestitutionVelocity(BodyFixture fixture1, BodyFixture fixture2) { return cm.mixRestitutionVelocity(fixture1.getRestitutionVelocity(), fixture2.getRestitutionVelocity()); }
		@Override
		public void begin(Contact contact) { this.begin++; }
		@Override
		public void persist(Contact oldContact, Contact newContact) { this.persist++; }
		@Override
		public void end(Contact contact) { this.end++; }
		public void clear() { this.begin = 0; this.persist = 0; this.end = 0; }
	};
	
	/**
	 * Sets up the test data.
	 */
	@Before
	public void setup() {
		this.b1 = new Body();
		this.b2 = new Body();
		this.f1 = b1.addFixture(Geometry.createCircle(0.5));
		this.f2 = b2.addFixture(Geometry.createCircle(0.5));
		this.cp = new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(this.b1, this.f1), 
				new BasicCollisionItem<Body, BodyFixture>(this.b2, this.f2));

		this.cuh = new CountingContactUpdateHandler();
	}
	
	/**
	 * Tests the creation.
	 */
	@Test
	public void create() {
		ContactConstraint<Body> cc = new ContactConstraint<Body>(this.cp);
		
		// test initialization state
		TestCase.assertNotNull(cc.contacts);
		TestCase.assertNotNull(cc.getContacts());
		TestCase.assertEquals(0, cc.contacts.size());
		TestCase.assertEquals(0, cc.getContacts().size());
		TestCase.assertNotNull(cc.contactsUnmodifiable);
		TestCase.assertEquals(0, cc.contactsUnmodifiable.size());
		TestCase.assertTrue(cc.enabled);
		TestCase.assertTrue(cc.isEnabled());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.friction);
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.getFriction());
		TestCase.assertNull(cc.invK);
		TestCase.assertNull(cc.K);
		TestCase.assertNotNull(cc.normal);
		TestCase.assertNotNull(cc.getNormal());
		TestCase.assertEquals(0.0, cc.normal.x);
		TestCase.assertEquals(0.0, cc.normal.y);
		TestCase.assertNotNull(cc.pair);
		TestCase.assertNotNull(cc.getCollisionPair());
		TestCase.assertEquals(this.cp, cc.pair);
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.b1, cc.getBody1());
		TestCase.assertEquals(this.b2, cc.getBody2());
		TestCase.assertEquals(this.f1, cc.getFixture1());
		TestCase.assertEquals(this.f2, cc.getFixture2());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.restitution);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.getRestitution());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.restitutionVelocity);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.getRestitutionVelocity());
		TestCase.assertFalse(cc.sensor);
		TestCase.assertFalse(cc.isSensor());
		TestCase.assertNotNull(cc.tangent);
		TestCase.assertNotNull(cc.getTangent());
		TestCase.assertEquals(0.0, cc.tangent.x);
		TestCase.assertEquals(0.0, cc.tangent.y);
		TestCase.assertEquals(0.0, cc.tangentSpeed);
		TestCase.assertEquals(0.0, cc.getTangentSpeed());
	
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getBody(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getBody(this.b2));
		TestCase.assertEquals(null, cc.getBody(new Body()));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getFixture(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getFixture(this.b2));
		TestCase.assertEquals(null, cc.getFixture(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getOtherBody(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getOtherBody(this.b2));
		TestCase.assertEquals(null, cc.getOtherBody(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getOtherFixture(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getOtherFixture(this.b2));
		TestCase.assertEquals(null, cc.getOtherFixture(new Body()));
	}

	/**
	 * Tests the toString method.
	 */
	@Test
	public void update() {
		ContactConstraint<Body> cc = new ContactConstraint<Body>(this.cp);
		Settings s = new Settings();
		
		Manifold m = new Manifold();
		m.setNormal(new Vector2(1.0, 1.0));
		ManifoldPoint mp0 = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp0.setDepth(1.0);
		mp0.setPoint(new Vector2(2.0, 2.0));
		m.getPoints().add(mp0);
		
		// test begin
		
		ManifoldPoint mp1 = new ManifoldPoint(new IndexedManifoldPointId(0, 0, 0));
		mp1.setDepth(2.0);
		mp1.setPoint(new Vector2(2.0, 3.0));
		m.getPoints().add(mp1);
		
		cc.update(m, s, this.cuh);
		
		// test initialization state
		TestCase.assertNotNull(cc.contacts);
		TestCase.assertNotNull(cc.getContacts());
		TestCase.assertEquals(2, cc.contacts.size());
		TestCase.assertEquals(2, cc.getContacts().size());
		TestCase.assertNotNull(cc.contactsUnmodifiable);
		TestCase.assertEquals(2, cc.contactsUnmodifiable.size());
		TestCase.assertTrue(cc.enabled);
		TestCase.assertTrue(cc.isEnabled());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.friction);
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.getFriction());
		TestCase.assertNull(cc.invK);
		TestCase.assertNull(cc.K);
		TestCase.assertNotNull(cc.normal);
		TestCase.assertNotNull(cc.getNormal());
		TestCase.assertEquals(m.getNormal().x, cc.normal.x);
		TestCase.assertEquals(m.getNormal().y, cc.normal.y);
		TestCase.assertNotNull(cc.pair);
		TestCase.assertNotNull(cc.getCollisionPair());
		TestCase.assertEquals(this.cp, cc.pair);
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.b1, cc.getBody1());
		TestCase.assertEquals(this.b2, cc.getBody2());
		TestCase.assertEquals(this.f1, cc.getFixture1());
		TestCase.assertEquals(this.f2, cc.getFixture2());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.restitution);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.getRestitution());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.restitutionVelocity);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.getRestitutionVelocity());
		TestCase.assertFalse(cc.sensor);
		TestCase.assertFalse(cc.isSensor());
		TestCase.assertNotNull(cc.tangent);
		TestCase.assertNotNull(cc.getTangent());
		TestCase.assertEquals(m.getNormal().getLeftHandOrthogonalVector().x, cc.tangent.x);
		TestCase.assertEquals(m.getNormal().getLeftHandOrthogonalVector().y, cc.tangent.y);
		TestCase.assertEquals(0.0, cc.tangentSpeed);
		TestCase.assertEquals(0.0, cc.getTangentSpeed());
		
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getBody(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getBody(this.b2));
		TestCase.assertEquals(null, cc.getBody(new Body()));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getFixture(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getFixture(this.b2));
		TestCase.assertEquals(null, cc.getFixture(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getOtherBody(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getOtherBody(this.b2));
		TestCase.assertEquals(null, cc.getOtherBody(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getOtherFixture(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getOtherFixture(this.b2));
		TestCase.assertEquals(null, cc.getOtherFixture(new Body()));
		
		TestCase.assertEquals(2, this.cuh.begin);
		
		// test persist
		
		// don't do this :), but for the test this works
		m.getPoints().get(0).getPoint().x = 2.0001;
		m.getPoints().get(1).getPoint().x = 2.0001;
		
		ManifoldPoint mp2 = new ManifoldPoint(new IndexedManifoldPointId(0, 0, 1));
		mp2.setDepth(2.0);
		mp2.setPoint(new Vector2(2.0, 3.0));
		m.getPoints().add(0, mp2);
		
		this.f1.setSensor(true);
		
		this.cuh.clear();
		cc.update(m, s, this.cuh);
		
		// test initialization state
		TestCase.assertNotNull(cc.contacts);
		TestCase.assertNotNull(cc.getContacts());
		TestCase.assertEquals(3, cc.contacts.size());
		TestCase.assertEquals(3, cc.getContacts().size());
		TestCase.assertNotNull(cc.contactsUnmodifiable);
		TestCase.assertEquals(3, cc.contactsUnmodifiable.size());
		TestCase.assertTrue(cc.enabled);
		TestCase.assertTrue(cc.isEnabled());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.friction);
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.getFriction());
		TestCase.assertNull(cc.invK);
		TestCase.assertNull(cc.K);
		TestCase.assertNotNull(cc.normal);
		TestCase.assertNotNull(cc.getNormal());
		TestCase.assertEquals(m.getNormal().x, cc.normal.x);
		TestCase.assertEquals(m.getNormal().y, cc.normal.y);
		TestCase.assertNotNull(cc.pair);
		TestCase.assertNotNull(cc.getCollisionPair());
		TestCase.assertEquals(this.cp, cc.pair);
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.b1, cc.getBody1());
		TestCase.assertEquals(this.b2, cc.getBody2());
		TestCase.assertEquals(this.f1, cc.getFixture1());
		TestCase.assertEquals(this.f2, cc.getFixture2());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.restitution);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.getRestitution());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.restitutionVelocity);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.getRestitutionVelocity());
		TestCase.assertTrue(cc.sensor);
		TestCase.assertTrue(cc.isSensor());
		TestCase.assertNotNull(cc.tangent);
		TestCase.assertNotNull(cc.getTangent());
		TestCase.assertEquals(m.getNormal().getLeftHandOrthogonalVector().x, cc.tangent.x);
		TestCase.assertEquals(m.getNormal().getLeftHandOrthogonalVector().y, cc.tangent.y);
		TestCase.assertEquals(0.0, cc.tangentSpeed);
		TestCase.assertEquals(0.0, cc.getTangentSpeed());
		
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getBody(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getBody(this.b2));
		TestCase.assertEquals(null, cc.getBody(new Body()));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getFixture(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getFixture(this.b2));
		TestCase.assertEquals(null, cc.getFixture(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getOtherBody(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getOtherBody(this.b2));
		TestCase.assertEquals(null, cc.getOtherBody(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getOtherFixture(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getOtherFixture(this.b2));
		TestCase.assertEquals(null, cc.getOtherFixture(new Body()));
		
		TestCase.assertEquals(1, this.cuh.begin);
		TestCase.assertEquals(2, this.cuh.persist);
		
		// test end
		
		m.getPoints().remove(0);
		this.f1.setSensor(false);
		this.cuh.clear();
		cc.update(m, s, this.cuh);
		
		// test initialization state
		TestCase.assertNotNull(cc.contacts);
		TestCase.assertNotNull(cc.getContacts());
		TestCase.assertEquals(2, cc.contacts.size());
		TestCase.assertEquals(2, cc.getContacts().size());
		TestCase.assertNotNull(cc.contactsUnmodifiable);
		TestCase.assertEquals(2, cc.contactsUnmodifiable.size());
		TestCase.assertTrue(cc.enabled);
		TestCase.assertTrue(cc.isEnabled());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.friction);
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, cc.getFriction());
		TestCase.assertNull(cc.invK);
		TestCase.assertNull(cc.K);
		TestCase.assertNotNull(cc.normal);
		TestCase.assertNotNull(cc.getNormal());
		TestCase.assertEquals(m.getNormal().x, cc.normal.x);
		TestCase.assertEquals(m.getNormal().y, cc.normal.y);
		TestCase.assertNotNull(cc.pair);
		TestCase.assertNotNull(cc.getCollisionPair());
		TestCase.assertEquals(this.cp, cc.pair);
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.b1, cc.getBody1());
		TestCase.assertEquals(this.b2, cc.getBody2());
		TestCase.assertEquals(this.f1, cc.getFixture1());
		TestCase.assertEquals(this.f2, cc.getFixture2());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.restitution);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, cc.getRestitution());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.restitutionVelocity);
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, cc.getRestitutionVelocity());
		TestCase.assertFalse(cc.sensor);
		TestCase.assertFalse(cc.isSensor());
		TestCase.assertNotNull(cc.tangent);
		TestCase.assertNotNull(cc.getTangent());
		TestCase.assertEquals(m.getNormal().getLeftHandOrthogonalVector().x, cc.tangent.x);
		TestCase.assertEquals(m.getNormal().getLeftHandOrthogonalVector().y, cc.tangent.y);
		TestCase.assertEquals(0.0, cc.tangentSpeed);
		TestCase.assertEquals(0.0, cc.getTangentSpeed());
		
		TestCase.assertEquals(this.cp, cc.getCollisionPair());
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getBody(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getBody(this.b2));
		TestCase.assertEquals(null, cc.getBody(new Body()));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getFixture(this.b1));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getFixture(this.b2));
		TestCase.assertEquals(null, cc.getFixture(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getBody(), cc.getOtherBody(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getBody(), cc.getOtherBody(this.b2));
		TestCase.assertEquals(null, cc.getOtherBody(new Body()));
		TestCase.assertEquals(this.cp.getSecond().getFixture(), cc.getOtherFixture(this.b1));
		TestCase.assertEquals(this.cp.getFirst().getFixture(), cc.getOtherFixture(this.b2));
		TestCase.assertEquals(null, cc.getOtherFixture(new Body()));
		
		TestCase.assertEquals(0, this.cuh.begin);
		TestCase.assertEquals(2, this.cuh.persist);
		TestCase.assertEquals(1, this.cuh.end);
	}
	
	/**
	 * Tests the get/set sensor methods.
	 */
	@Test
	public void getSetEnabled() {
		ContactConstraint<Body> cc = new ContactConstraint<Body>(this.cp);
		
		cc.setEnabled(true);
		TestCase.assertTrue(cc.isEnabled());
		
		cc.setEnabled(false);
		TestCase.assertFalse(cc.isEnabled());
		
		cc.setEnabled(true);
		TestCase.assertTrue(cc.isEnabled());
	}
	
	/**
	 * Tests the get/set tanget speed methods.
	 */
	@Test
	public void getSetTangentSpeed() {
		ContactConstraint<Body> cc = new ContactConstraint<Body>(this.cp);
		
		cc.setTangentSpeed(0.5);
		TestCase.assertEquals(0.5, cc.getTangentSpeed());
		
		cc.setTangentSpeed(0.0);
		TestCase.assertEquals(0.0, cc.getTangentSpeed());
		
		cc.setTangentSpeed(50.0);
		TestCase.assertEquals(50.0, cc.getTangentSpeed());
		
		cc.setTangentSpeed(-0.5);
		TestCase.assertEquals(-0.5, cc.getTangentSpeed());
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		ContactConstraint<Body> cc = new ContactConstraint<Body>(this.cp);
		Settings s = new Settings();
		
		Manifold m = new Manifold();
		m.setNormal(new Vector2(1.0, 1.0));
		ManifoldPoint mp0 = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp0.setDepth(1.0);
		mp0.setPoint(new Vector2(2.0, 2.0));
		m.getPoints().add(mp0);
		
		TestCase.assertNotNull(cc.toString());
		
		ManifoldPoint mp1 = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp1.setDepth(2.0);
		mp1.setPoint(new Vector2(3.0, 2.0));
		m.getPoints().add(mp1);
		cc.update(m, s, this.cuh);
		
		TestCase.assertNotNull(cc.toString());
	}

	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		ContactConstraint<Body> cc = new ContactConstraint<Body>(this.cp);
		Settings s = new Settings();
		
		Manifold m = new Manifold();
		m.setNormal(new Vector2(1.0, 1.0));
		
		ManifoldPoint mp0 = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp0.setDepth(1.0);
		mp0.setPoint(new Vector2(2.0, 2.0));
		m.getPoints().add(mp0);

		ManifoldPoint mp1 = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp1.setDepth(2.0);
		mp1.setPoint(new Vector2(3.0, 2.0));
		m.getPoints().add(mp1);
		cc.update(m, s, this.cuh);
		
		TestCase.assertEquals(1.0, cc.getNormal().x);
		TestCase.assertEquals(1.0, cc.getNormal().y);
		TestCase.assertEquals(1.0, cc.getTangent().x);
		TestCase.assertEquals(-1.0, cc.getTangent().y);
		TestCase.assertEquals(1.0, cc.contacts.get(0).getDepth());
		TestCase.assertEquals(2.0, cc.getContacts().get(0).getPoint().x);
		TestCase.assertEquals(2.0, cc.getContacts().get(0).getPoint().y);
		TestCase.assertEquals(2.0, cc.contacts.get(1).getDepth());
		TestCase.assertEquals(3.0, cc.getContacts().get(1).getPoint().x);
		TestCase.assertEquals(2.0, cc.getContacts().get(1).getPoint().y);
		
		cc.shift(new Vector2(2.0, -1.0));
		
		TestCase.assertEquals(1.0, cc.getNormal().x);
		TestCase.assertEquals(1.0, cc.getNormal().y);
		TestCase.assertEquals(1.0, cc.getTangent().x);
		TestCase.assertEquals(-1.0, cc.getTangent().y);
		TestCase.assertEquals(1.0, cc.contacts.get(0).getDepth());
		TestCase.assertEquals(4.0, cc.getContacts().get(0).getPoint().x);
		TestCase.assertEquals(1.0, cc.getContacts().get(0).getPoint().y);
		TestCase.assertEquals(2.0, cc.contacts.get(1).getDepth());
		TestCase.assertEquals(5.0, cc.getContacts().get(1).getPoint().x);
		TestCase.assertEquals(1.0, cc.getContacts().get(1).getPoint().y);
	}
}
