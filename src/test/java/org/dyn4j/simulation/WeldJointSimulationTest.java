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
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link WeldJoint} class in simulation.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class WeldJointSimulationTest {
	/**
	 * Tests the body separation as enforced by the distance joint.
	 */
	@Test
	public void standard() {
		World<Body> w = new World<Body>();
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createRectangle(1.0, 0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		WeldJoint<Body> wj = new WeldJoint<Body>(g, b, b.getWorldCenter());
		w.addJoint(wj);

		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		w.step(4);
		
		// because the allowed axis is the x-axis and the position of the axis is at body2's world center
		// gravity is applied but has no effect
		Vector2 v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(4.9000, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		// apply some velocity along the y-axis
		b.setLinearVelocity(0.0, 1.0);
		w.step(4);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(4.9000, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		// now apply some velocity along the x-axis
		b.setLinearVelocity(1.0, 1.0);
		w.step(4);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(4.9000, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		b.applyTorque(Math.toRadians(30));
		w.step(1);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.52359, wj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals(4.9000, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
	}
	
	/**
	 * Tests the bodies with an angular spring
	 */
	@Test
	public void softConstraint() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createRectangle(1.0, 0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		Vector2 p = b.getWorldCenter().sum(-0.5, 0.0);
		WeldJoint<Body> wj = new WeldJoint<Body>(g, b, p);
		
		// NOTE: that I've set the rest distance to more than the limits
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		wj.setSpringDampingRatio(0.3);
		wj.setSpringFrequency(8.0);
		w.addJoint(wj);
		
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle(), 1e-5);

		// do one simulation step, this should cause body2 to drop due to the soft constraint
		// and have a negative torque
		w.step(1);
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		TestCase.assertEquals(-0.17027, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(-0.00283, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(-0.64077, wj.getSpringTorque(invdt), 1e-5);
		TestCase.assertEquals(-0.64077, wj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals( 2.34580, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		// after a few more simulations it should stabilize with rotation below the x-axis
		w.step(20);
		
		TestCase.assertEquals(-0.01916, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(-0.01883, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(-2.50894, wj.getSpringTorque(invdt), 1e-5);
		TestCase.assertEquals(-2.50894, wj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals( 4.98379, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		// test the maximum torque setting
		wj.setMaximumSpringTorque(5);
		wj.setMaximumSpringTorqueEnabled(true);
		b.applyForce(new Vector2(0.0, -10.0), new Vector2(0.5, 2.0));
		w.step(1);
		
		TestCase.assertEquals(-0.72029, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(-0.03084, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(-5.00000, wj.getSpringTorque(invdt), 1e-5);
		TestCase.assertEquals(-5.00000, wj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals( 4.38931, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
	}

	/**
	 * Tests the bodies not exceeding the limits
	 */
	@Test
	public void softConstraintWithLimitsLower() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createRectangle(1.0, 0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		Vector2 p = b.getWorldCenter().sum(-0.5, 0.0);
		WeldJoint<Body> wj = new WeldJoint<Body>(g, b, p);
		
		// NOTE: that I've set the rest distance to more than the limits
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		wj.setSpringDampingRatio(0.3);
		wj.setSpringFrequency(8.0);
		
		double lim = Math.PI * 0.2;
		wj.setLimitsEnabled(-lim, lim);
		
		w.addJoint(wj);
		
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle(), 1e-5);

		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		// too weak to hold up the object, so it should rest on the limit
		wj.setMaximumSpringTorque(1);
		wj.setMaximumSpringTorqueEnabled(true);
		b.applyForce(new Vector2(0.0, -10.0), new Vector2(0.5, 2.0));
		w.step(50);
		
		TestCase.assertEquals( 0.00000, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(-lim, b.getTransform().getRotationAngle(), 1e-4);
		TestCase.assertEquals(-1.00000, wj.getSpringTorque(invdt), 1e-5);
		TestCase.assertEquals(-1.98201, wj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals( 4.89999, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
	}
	
	/**
	 * Tests the bodies not exceeding the limits
	 */
	@Test
	public void softConstraintWithLimitsUpper() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createRectangle(1.0, 0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		Vector2 p = b.getWorldCenter().sum(0.5, 0.0);
		WeldJoint<Body> wj = new WeldJoint<Body>(g, b, p);
		
		// NOTE: that I've set the rest distance to more than the limits
		wj.setSpringEnabled(true);
		wj.setSpringDamperEnabled(true);
		wj.setSpringDampingRatio(0.3);
		wj.setSpringFrequency(8.0);
		
		double lim = Math.PI * 0.2;
		wj.setLimitsEnabled(-lim, lim);
		
		w.addJoint(wj);
		
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle(), 1e-5);

		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		// too weak to hold up the object, so it should rest on the limit
		wj.setMaximumSpringTorque(1);
		wj.setMaximumSpringTorqueEnabled(true);
		b.applyForce(new Vector2(0.0, -10.0), new Vector2(-0.5, 2.0));
		w.step(50);
		
		TestCase.assertEquals( 0.00000, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals( lim, b.getTransform().getRotationAngle(), 1e-4);
		TestCase.assertEquals( 1.00000, wj.getSpringTorque(invdt), 1e-5);
		TestCase.assertEquals( 1.98201, wj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals( 4.89999, wj.getReactionForce(invdt).getMagnitude(), 1e-5);
	}
}
