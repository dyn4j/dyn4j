/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the methods of the {@link Body} class.
 * <p>
 * Was FixtureTest.
 * @author William Bittle
 * @version 6.0.0
 * @since 6.0.0
 */
public class BodyTest {
	/** The {@link Body} object to test */
	private Body body;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		// create a body and set everything we can
		Body body = new Body();
		body.addFixture(Geometry.createCircle(Math.toRadians(30)));
		body.addFixture(Geometry.createSquare(1.0));
		body.setAngularDamping(1.0);
		body.setAngularVelocity(Math.toRadians(20));
		body.setAtRest(true);
		body.setAtRestDetectionEnabled(false);
		body.setBullet(true);
		body.setEnabled(true);
		body.setGravityScale(1.1);
		body.setLinearDamping(3.0);
		body.setLinearVelocity(-1, -1);
		body.setMass(MassType.NORMAL);
		body.setOwner(new Object());
		body.setUserData(new Object());
		body.applyForce(new Force(2.0, 2.0));
		body.applyTorque(2);
		body.rotate(Math.toRadians(15), new Vector2(0, 0));
		body.translate(1, 1);
		body.getPreviousTransform().set(body.getTransform());
		body.atRestTime = 1;
		body.force.set(5, 5);
		body.torque = 4;
		
		this.body = body;
	}

	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		Body b = new Body();
		
		// these field should be defaulted
		TestCase.assertEquals(PhysicsBody.DEFAULT_ANGULAR_DAMPING, b.angularDamping);
		TestCase.assertEquals(0.0, b.angularVelocity);
		TestCase.assertFalse(b.atRest);
		TestCase.assertTrue(b.atRestDetectionEnabled);
		TestCase.assertEquals(0.0, b.atRestTime);
		TestCase.assertFalse(b.bullet);
		TestCase.assertNotNull(b.force);
		TestCase.assertNotNull(b.forces);
		TestCase.assertEquals(1.0, b.gravityScale);
		TestCase.assertEquals(PhysicsBody.DEFAULT_LINEAR_DAMPING, b.linearDamping);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertNotNull(b.torques);
		TestCase.assertNotNull(b.linearVelocity);
		
		b = new Body(3);
		b = new Body(-3);
		b = new Body(0);
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Body copy = this.body.copy();
		
		TestCase.assertNotSame(this.body, copy);
		TestCase.assertNotSame(this.body.force, copy.force);
		TestCase.assertNotSame(this.body.forces, copy.forces);
		TestCase.assertNotSame(this.body.linearVelocity, copy.linearVelocity);
		TestCase.assertNotSame(this.body.mass, copy.mass);
		TestCase.assertNotSame(this.body.torques, copy.torques);
		TestCase.assertNull(copy.getOwner());
		TestCase.assertNull(copy.getFixtureModificationHandler());
		TestCase.assertNull(copy.getUserData());

		TestCase.assertEquals(this.body.angularDamping, copy.angularDamping);
		TestCase.assertEquals(this.body.angularVelocity, copy.angularVelocity);
		TestCase.assertEquals(this.body.atRest, copy.atRest);
		TestCase.assertEquals(this.body.atRestDetectionEnabled, copy.atRestDetectionEnabled);
		TestCase.assertEquals(this.body.atRestTime, copy.atRestTime);
		TestCase.assertEquals(this.body.bullet, copy.bullet);
		TestCase.assertEquals(this.body.gravityScale, copy.gravityScale);
		TestCase.assertEquals(this.body.linearDamping, copy.linearDamping);
		TestCase.assertEquals(this.body.torque, copy.torque);
		TestCase.assertEquals(this.body.force.x, copy.force.x);
		TestCase.assertEquals(this.body.force.y, copy.force.y);
		TestCase.assertEquals(this.body.linearVelocity.x, copy.linearVelocity.x);
		TestCase.assertEquals(this.body.linearVelocity.y, copy.linearVelocity.y);
		
		TestCase.assertEquals(this.body.mass.getInertia(), copy.mass.getInertia());
		TestCase.assertEquals(this.body.mass.getInverseInertia(), copy.mass.getInverseInertia());
		TestCase.assertEquals(this.body.mass.getInverseMass(), copy.mass.getInverseMass());
		TestCase.assertEquals(this.body.mass.getMass(), copy.mass.getMass());
		TestCase.assertNotSame(this.body.mass.getCenter(), copy.mass.getCenter());
		TestCase.assertEquals(this.body.mass.getCenter().x, copy.mass.getCenter().x);
		TestCase.assertEquals(this.body.mass.getCenter().y, copy.mass.getCenter().y);
		TestCase.assertEquals(this.body.mass.getType(), copy.mass.getType());
		
		TestCase.assertEquals(this.body.forces.size(), copy.forces.size());
		for (int i = 0; i < this.body.forces.size(); i++) {
			TestCase.assertNotSame(this.body.forces.get(i), copy.forces.get(i));
			TestCase.assertNotSame(this.body.forces.get(i).force, copy.forces.get(i).force);
			TestCase.assertEquals(this.body.forces.get(i).force.x, copy.forces.get(i).force.x);
			TestCase.assertEquals(this.body.forces.get(i).force.y, copy.forces.get(i).force.y);
		}
		
		TestCase.assertEquals(this.body.torques.size(), copy.torques.size());
		for (int i = 0; i < this.body.torques.size(); i++) {
			TestCase.assertNotSame(this.body.torques.get(i), copy.torques.get(i));
			TestCase.assertEquals(this.body.torques.get(i).torque, copy.torques.get(i).torque);
		}
	}
}
