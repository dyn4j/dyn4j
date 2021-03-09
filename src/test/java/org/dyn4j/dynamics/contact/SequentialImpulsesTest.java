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
package org.dyn4j.dynamics.contact;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.ValueMixer;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the methods of the {@link SequentialImpulses} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class SequentialImpulsesTest {
	private class CustomContactUpdateHandler implements ContactUpdateHandler {
		private final ValueMixer cm = ValueMixer.DEFAULT_MIXER;
		
		@Override
		public double getRestitution(BodyFixture fixture1, BodyFixture fixture2) { return cm.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution()); }
		@Override
		public double getFriction(BodyFixture fixture1, BodyFixture fixture2) { return cm.mixFriction(fixture1.getFriction(), fixture2.getFriction()); }
		@Override
		public double getRestitutionVelocity(BodyFixture fixture1, BodyFixture fixture2) { return cm.mixRestitutionVelocity(fixture1.getRestitutionVelocity(), fixture2.getRestitutionVelocity()); }
		@Override
		public void begin(Contact contact) {}
		@Override
		public void persist(Contact oldContact, Contact newContact) {}
		@Override
		public void end(Contact contact) {}
	};
	
	/**
	 * Tests the restitution velocity using/not using the restitution.
	 */
	@Test
	public void restitutionVelocity() {
		Body b1 = new Body();
		Body b2 = new Body();
		BodyFixture bf1 = b1.addFixture(Geometry.createSquare(0.5));
		BodyFixture bf2 = b2.addFixture(Geometry.createSquare(0.5));
		CollisionPair<CollisionItem<Body, BodyFixture>> cp = new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, bf1), 
				new BasicCollisionItem<Body, BodyFixture>(b2, bf2));
		
		final double vy = -BodyFixture.DEFAULT_RESTITUTION_VELOCITY - 0.5;
		
		bf1.setRestitution(0.1);
		bf2.setRestitution(0.1);
		b1.translate(0.0, 0.45);
		b1.setLinearVelocity(0.0, vy);
		
		Settings settings = new Settings();
		TimeStep step = new TimeStep(settings.getStepFrequency());
		ContactUpdateHandler handler = new CustomContactUpdateHandler();
		ContactConstraint<Body> cc = new ContactConstraint<Body>(cp);
		
		NarrowphaseDetector np = new Gjk();
		Penetration penetration = new Penetration();
		np.detect(bf1.getShape(), b1.getTransform(), bf2.getShape(), b2.getTransform(), penetration);
		
		ManifoldSolver ms = new ClippingManifoldSolver();
		Manifold manifold = new Manifold();
		ms.getManifold(penetration, bf1.getShape(), b1.getTransform(), bf2.getShape(), b2.getTransform(), manifold);
		
		SequentialImpulses<Body> si = new SequentialImpulses<Body>();
		List<ContactConstraint<Body>> ccs = new ArrayList<ContactConstraint<Body>>();
		ccs.add(cc);
		
		// the velocity is towards each other and higher than the default velocity threshold
		
		cc.update(manifold, settings, handler);
		si.initialize(ccs, step, settings);
		
		TestCase.assertEquals(-cc.restitution * vy, cc.contacts.get(0).vb);
		TestCase.assertEquals(-cc.restitution * vy, cc.contacts.get(1).vb);
		
		// the velocity is towards each other but lower than the threshold
		
		b1.setLinearVelocity(0.0, -0.5);
		cc.update(manifold, settings, handler);
		si.initialize(ccs, step, settings);
		
		TestCase.assertEquals(0.0, cc.contacts.get(0).vb);
		TestCase.assertEquals(0.0, cc.contacts.get(1).vb);
	}
	
	/**
	 * Tests when the contact points are ill-conditioned.
	 */
	@Test
	public void illConditioned() {
		Body b1 = new Body();
		Body b2 = new Body();
		BodyFixture bf1 = b1.addFixture(Geometry.createSquare(0.5));
		BodyFixture bf2 = b2.addFixture(Geometry.createSquare(0.5));
		CollisionPair<CollisionItem<Body, BodyFixture>> cp = new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, bf1), 
				new BasicCollisionItem<Body, BodyFixture>(b2, bf2));
		
		b1.setMass(MassType.NORMAL);
		b2.setMass(MassType.NORMAL);
		bf1.setRestitution(0.1);
		bf2.setRestitution(0.1);
		b1.translate(0.49995, 0.49995);
		b1.setLinearVelocity(0.0, -1.5);
		
		Settings settings = new Settings();
		TimeStep step = new TimeStep(settings.getStepFrequency());
		ContactUpdateHandler handler = new CustomContactUpdateHandler();
		ContactConstraint<Body> cc = new ContactConstraint<Body>(cp);
		
		NarrowphaseDetector np = new Gjk();
		Penetration penetration = new Penetration();
		np.detect(bf1.getShape(), b1.getTransform(), bf2.getShape(), b2.getTransform(), penetration);
		
		ManifoldSolver ms = new ClippingManifoldSolver();
		Manifold manifold = new Manifold();
		ms.getManifold(penetration, bf1.getShape(), b1.getTransform(), bf2.getShape(), b2.getTransform(), manifold);
		
		SequentialImpulses<Body> si = new SequentialImpulses<Body>();
		List<ContactConstraint<Body>> ccs = new ArrayList<ContactConstraint<Body>>();
		ccs.add(cc);
		
		// contact points are so close that the solving is ill-conditioned so we ignore one of them
		
		cc.update(manifold, settings, handler);
		si.initialize(ccs, step, settings);
		
		TestCase.assertEquals(cc.size, 1);
		TestCase.assertTrue(cc.contacts.get(1).ignored);
		
		// ignore one, but swap to use the second that has the larger depth
		manifold.getPoints().get(1).setDepth(penetration.getDepth() + 0.1);

		cc.update(manifold, settings, handler);

		SolvableContact c1 = cc.contacts.get(0);
		SolvableContact c2 = cc.contacts.get(1);
		
		si.initialize(ccs, step, settings);
		
		TestCase.assertEquals(cc.size, 1);
		TestCase.assertEquals(cc.contacts.get(0), c2);
		TestCase.assertEquals(cc.contacts.get(1), c1);
		TestCase.assertTrue(cc.contacts.get(1).ignored);
		TestCase.assertTrue(c1.ignored);
	}
}
