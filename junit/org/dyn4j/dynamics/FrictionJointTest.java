/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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

import junit.framework.TestCase;

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test the {@link FrictionJoint} class.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.0.2
 */
public class FrictionJointTest {
	/** The first body used for testing */
	private Body b1;
	
	/** The second body used for testing */
	private Body b2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.b1 = new Body();
		this.b2 = new Body();
	}
	
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createSuccess() {
		new FrictionJoint(b1, b2, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAnchor() {
		new FrictionJoint(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSameBody() {
		new FrictionJoint(b1, b1, new Vector2());
	}
	
	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaxTorque() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		fj.setMaximumTorque(0.0);
		fj.setMaximumTorque(10.0);
		fj.setMaximumTorque(2548.0);
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxTorque() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		fj.setMaximumTorque(-2.0);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaxForce() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		fj.setMaximumForce(0.0);
		fj.setMaximumForce(10.0);
		fj.setMaximumForce(2548.0);
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxForce() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		fj.setMaximumForce(-2.0);
	}
	
	/**
	 * Tests the friction joint to ensure that the attached body is slowed.
	 */
	@Test
	public void friction1() {
		World w = new World();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		// 5 meters/second
		b.setLinearVelocity(new Vector2(4.0, 3.0));
		// 30 degrees/second
		b.setAngularVelocity(Math.toRadians(30.0));
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		FrictionJoint fj = new FrictionJoint(g, b, b.getWorldCenter());
		fj.setMaximumForce(1000.0);
		fj.setMaximumTorque(1000.0);
		w.addJoint(fj);
		
		w.step(1);
		
		// make sure that the body has been slowed linearly and angularly
		TestCase.assertTrue(b.getLinearVelocity().getMagnitude() < 5.0);
		TestCase.assertTrue(b.getAngularVelocity() < Math.toRadians(30));
	}
}
