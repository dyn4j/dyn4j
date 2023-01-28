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
package org.dyn4j.simulation;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link PulleyJoint} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class PulleyJointSimulationTest {
	/**
	 * Tests the PulleyJoint without limits or ratio.
	 */
	@Test
	public void simpleLinkage() {
		World<Body> w = new World<Body>();
		
		// take friction and damping out of the picture
		
		Body b1 = new Body();
		BodyFixture b1f = b1.addFixture(Geometry.createCircle(0.5));
		b1f.setFriction(0.0);
		b1.setMass(MassType.NORMAL);
		b1.setLinearDamping(0.0);
		b1.setAngularDamping(0.0);
		b1.translate(-1.0, 0.0);
		w.addBody(b1);
		
		Body b2 = new Body();
		BodyFixture b2f = b2.addFixture(Geometry.createCircle(0.5));
		b2f.setFriction(0.0);
		b2.setMass(MassType.NORMAL);
		b2.setLinearDamping(0.0);
		b2.setAngularDamping(0.0);
		b2.translate(1.0, 0.0);
		w.addBody(b2);
		
		Vector2 pa1 = new Vector2(-1.0, 1.0);
		Vector2 pa2 = new Vector2(1.0, 1.0);
		Vector2 a1 = new Vector2(-1.0, 0.0);
		Vector2 a2 = new Vector2(1.0, 0.0);
		
		PulleyJoint<Body> aj = new PulleyJoint<Body>(b1, b2, pa1, pa2, a1, a2);
		w.addJoint(aj);
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		w.step(1);
		
		// nothing should happen since both are equally weighted
		TestCase.assertEquals(0.0, b1.getLinearVelocity().getMagnitude());
		TestCase.assertEquals(0.0, b2.getLinearVelocity().getMagnitude());
		TestCase.assertEquals(7.696, aj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, aj.getReactionTorque(invdt));
		
		aj.setRatio(0.5);
		w.step(1);
		
		// with a ratio enabled, one should fall faster than the other
		TestCase.assertTrue(b1.getLinearVelocity().y > 0.0);
		TestCase.assertTrue(b2.getLinearVelocity().y < 0.0);
		TestCase.assertTrue(aj.getCurrentLength2() > aj.getCurrentLength1());
		TestCase.assertEquals(2.0, aj.getLength());
		TestCase.assertEquals(9.236, aj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, aj.getReactionTorque(invdt));
		
		aj.setRatio(2.0);
		w.step(10);
		
		// with a ratio enabled, one should fall faster than the other
		TestCase.assertTrue(b1.getLinearVelocity().y < 0.0);
		TestCase.assertTrue(b2.getLinearVelocity().y > 0.0);
		TestCase.assertTrue(aj.getCurrentLength2() < aj.getCurrentLength1());
		TestCase.assertEquals(2.0, aj.getLength());
		TestCase.assertEquals(4.618, aj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, aj.getReactionTorque(invdt));
	}
	
	/**
	 * Tests the PulleyJoint without limits or ratio.
	 */
	@Test
	public void withAndWithoutSlack() {
		World<Body> w = new World<Body>();
		
		// take friction and damping out of the picture

		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 1.0));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		g.translate(0.0, -1.0);
		w.addBody(g);
		
		Body b1 = new Body();
		BodyFixture b1f = b1.addFixture(Geometry.createCircle(0.5));
		b1f.setFriction(0.0);
		b1.setMass(MassType.NORMAL);
		b1.setLinearDamping(0.0);
		b1.setAngularDamping(0.0);
		b1.translate(-1.0, 0.0);
		w.addBody(b1);
		
		Body b2 = new Body();
		BodyFixture b2f = b2.addFixture(Geometry.createCircle(0.5));
		b2f.setFriction(0.0);
		b2.setMass(MassType.NORMAL);
		b2.setLinearDamping(0.0);
		b2.setAngularDamping(0.0);
		b2.translate(1.0, 0.5);
		w.addBody(b2);
		
		Vector2 pa1 = new Vector2(-1.0, 1.0);
		Vector2 pa2 = new Vector2(1.0, 1.0);
		Vector2 a1 = new Vector2(-1.0, 0.0);
		Vector2 a2 = new Vector2(1.0, 0.5);
		
		PulleyJoint<Body> aj = new PulleyJoint<Body>(b1, b2, pa1, pa2, a1, a2);
		w.addJoint(aj);
		
		w.step(1);
		
		// nothing should happen since both are equally weighted
		TestCase.assertEquals(0.0, b1.getLinearVelocity().getMagnitude());
		TestCase.assertEquals(0.0, b2.getLinearVelocity().getMagnitude());
		
		b2.setLinearVelocity(0.0, 10.0);
		w.step(2);
		
		TestCase.assertEquals(1.041, b1.getWorldCenter().distance(pa1), 1e-3);
		TestCase.assertEquals(0.458, b2.getWorldCenter().distance(pa2), 1e-3);
		TestCase.assertEquals(-0.041, b1.getTransform().getTranslationY(), 1e-3);
		TestCase.assertEquals(0.541, b2.getTransform().getTranslationY(), 1e-3);
		TestCase.assertEquals(0.156, b2.getLinearVelocity().y, 1e-3);
		TestCase.assertEquals(1.500, aj.getCurrentLength(), 1e-3);
		
		// with the slack enabled, the total length of the rope can be smaller
		// than the target length
		
		b2.setLinearVelocity(0.0, 10.0);
		b2.translate(0.0, 0.1);
		aj.setSlackEnabled(true);
		
		w.step(1);
		TestCase.assertEquals(9.836, b2.getLinearVelocity().y, 1e-3);
		TestCase.assertEquals(0.805, b2.getTransform().getTranslationY(), 1e-3);
		TestCase.assertEquals(0.194, b2.getWorldCenter().distance(pa2), 1e-3);
		TestCase.assertEquals(1.222, aj.getCurrentLength(), 1e-3);
	}
	
}
