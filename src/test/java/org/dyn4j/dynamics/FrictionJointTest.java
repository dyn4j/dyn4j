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

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link FrictionJoint} class.
 * @author William Bittle
 * @version 3.4.1
 * @since 1.0.2
 */
public class FrictionJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		new FrictionJoint(b1, b2, new Vector2());
	}
	
	/**
	 * Tests the failed creation with a null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new FrictionJoint(null, b2, new Vector2());
	}

	/**
	 * Tests the failed creation with a null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new FrictionJoint(b1, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor() {
		new FrictionJoint(b1, b2, null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new FrictionJoint(b1, b1, new Vector2());
	}
	
	/**
	 * Tests valid maximum torque values.
	 */
	@Test
	public void setMaximumTorque() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		
		fj.setMaximumTorque(0.0);
		TestCase.assertEquals(0.0, fj.getMaximumTorque());
		
		fj.setMaximumTorque(10.0);
		TestCase.assertEquals(10.0, fj.getMaximumTorque());
		
		fj.setMaximumTorque(2548.0);
		TestCase.assertEquals(2548.0, fj.getMaximumTorque());
	}
	
	/**
	 * Tests a negative maximum torque value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumTorque() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		fj.setMaximumTorque(-2.0);
	}
	
	/**
	 * Tests valid maximum force values.
	 */
	@Test
	public void setMaximumForce() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		
		fj.setMaximumForce(0.0);
		TestCase.assertEquals(0.0, fj.getMaximumForce());
		
		fj.setMaximumForce(10.0);
		TestCase.assertEquals(10.0, fj.getMaximumForce());
		
		fj.setMaximumForce(2548.0);
		TestCase.assertEquals(2548.0, fj.getMaximumForce());
	}
	
	/**
	 * Tests a negative maximum force value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumForce() {
		FrictionJoint fj = new FrictionJoint(b1, b2, new Vector2());
		fj.setMaximumForce(-2.0);
	}
	
	/**
	 * Tests the friction joint to ensure that the attached body is slowed.
	 */
	@Test
	public void simulationWithLargeForceTorqueMaximums() {
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
		
		// perform another step to ensure it doesn't increase velocities
		// with the maximums as high as above, the first iteration should
		// complete stop the body
		w.step(1);
		TestCase.assertTrue(b.getLinearVelocity().getMagnitude() < 5.0);
		TestCase.assertTrue(b.getAngularVelocity() < Math.toRadians(30));
	}
	
	/**
	 * Tests the friction joint to ensure that the attached body is slowed.
	 */
	@Test
	public void simulationWithLowForceTorqueMaximums() {
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
		fj.setMaximumForce(10.0);
		fj.setMaximumTorque(0.25);
		w.addJoint(fj);
		
		// with a maximum force of 10, a timestep of 1/60, an initial
		// velocity of 5.0 m/s, and a mass of 0.7853981633974483 compute
		// the number of iterations it will take for the body to come to rest
		
		// compute the acceleration applied each timestep:
		// f * 1/m = m/s^2 (da)
		// da = 12.7323954474
		
		// compute the loss of velocity each time step:
		// da * 1/s = m/s (dv)
		// dv = 0.21220659079
		
		// compute the iterations until v = 0:
		// 0 = vi - n * dv
		// n = vi / dv
		// n = 23.5619449018
		// so about 24 iterations
		
		// with a maximum torque of 0.25, a timestep of 1/60, an initial
		// angular velocity of 30 degrees/second, and an inertia of 0.09817477042468103 compute
		// the number of iterations it will take for the body to come to rest
		
		// compute the acceleration applied each timestep:
		// t * 1/i * dt = m^2/s^2 (dav)
		// dav = 0.04244131815 rads
		//
		
		// compute the iterations until av = 0:
		// 0 = avi - n * dav
		// n = avi / dav
		// n = 12.3370055036
		// so about 13 iterations

		w.step(1);
		
		// make sure that the body has been slowed linearly and angularly
		TestCase.assertTrue(b.getLinearVelocity().getMagnitude() < 5.0);
		TestCase.assertTrue(b.getAngularVelocity() < Math.toRadians(30));
		
		for (int i = 0 ; i < 12; i++) {
			w.step(1);
		}
		
		// should be close enough to zero
		TestCase.assertTrue(b.getAngularVelocity() < 1e-10);
		
		// 11 more iterations for the linear velocity
		for (int i = 0 ; i < 11; i++) {
			w.step(1);
		}
		
		// should be close enough to zero
		TestCase.assertTrue(b.getLinearVelocity().getMagnitude() < 1e-10);
	}
}
