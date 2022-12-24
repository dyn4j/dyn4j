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
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link WheelJoint} class in simulation.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class WheelJointSimulationTest {
	/**
	 * Tests with no motor/limits.
	 */
	@Test
	public void noLimitsNoMotor() {
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
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		WheelJoint<Body> dj = new WheelJoint<Body>(g, b, b.getWorldCenter(), new Vector2(1.0, 0.0));
		w.addJoint(dj);

		w.step(4);
		
		// because the allowed axis is the x-axis and the position of the axis is at body2's world center
		// gravity is applied but has no effect
		Vector2 v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x, 1e-3);
		TestCase.assertEquals(2.0, v2.y, 1e-3);
		
		// apply some velocity along the y-axis
		b.setLinearVelocity(0.0, 1.0);
		
		w.step(4);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x, 1e-3);
		TestCase.assertEquals(2.0, v2.y, 1e-3);
		
		// now apply some velocity along the x-axis
		b.setLinearVelocity(1.0, 1.0);

		w.step(4);
		
		// things should happen
		v2 = b.getWorldCenter();
		TestCase.assertTrue(v2.x > 0.0);
		TestCase.assertEquals(2.0, v2.y, 1e-3);
		
		b.applyTorque(Math.toRadians(30));
		w.step(1);
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		// The torque should rotate the body
		v2 = b.getWorldCenter();
		TestCase.assertTrue(v2.x > 0.0);
		TestCase.assertEquals(2.0, v2.y, 1e-3);
		TestCase.assertEquals(0.001, b.getTransform().getRotationAngle(), 1e-3);
		TestCase.assertEquals(7.89277, dj.getReactionForce(invdt).getMagnitude(), 1e-5);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-5);
	}
	
	/**
	 * Tests the bodies not exceeding the limits
	 */
	@Test
	public void limitsOnly() {
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
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		Vector2 p = b.getWorldCenter();
		WheelJoint<Body> dj = new WheelJoint<Body>(g, b, p, new Vector2(0.0, 1.0));
		
		// NOTE: that I've set the rest distance to more than the limits
		dj.setLimitsEnabled(-1.0, 5.0);
		dj.setSpringEnabled(false);
		w.addJoint(dj);
		
		Vector2 v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, p.distance(v2));
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		w.step(30);
		
		// gravity pulls down the circle to the lower limit
		v2 = b.getWorldCenter();
		TestCase.assertEquals(1.000, p.distance(v2), 1e-3);
		TestCase.assertEquals(7.696, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
		
		w.setGravity(0.0, 98.0);
		
		w.step(30);

		// gravity pulls up the circle to the upper limit
		v2 = b.getWorldCenter();
		TestCase.assertEquals(5.0, p.distance(v2), 1e-5);
		TestCase.assertEquals(76.969, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
		
		w.setGravity(0.0, -9.8);
		dj.setLimitsEnabled(-1.0, 4.0);
		w.step(1);
		
		// the bodies should be placed at the upper limit
		v2 = b.getWorldCenter();
		TestCase.assertEquals(4.0, p.distance(v2), 1e-5);
		TestCase.assertEquals(0.0, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
		
		w.step(50);
		
		dj.setLimitsEnabled(1.0, 3.0);
		w.step(1);
		
		// the bodies should be placed at the lower limit
		v2 = b.getWorldCenter();
		TestCase.assertEquals(1.0, p.distance(v2), 1e-5);
		TestCase.assertEquals(400.238, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
	}

	/**
	 * Tests the bodies not exceeding the limits
	 */
	@Test
	public void springDamperWithLimits() {
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
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		Vector2 p = b.getWorldCenter();
		WheelJoint<Body> dj = new WheelJoint<Body>(g, b, p, new Vector2(0.0, 1.0));
		
		// NOTE: that I've set the rest distance to more than the limits
		dj.setLimitsEnabled(-1.0, 5.0);
		w.addJoint(dj);
		
		Vector2 v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, p.distance(v2));
		
		w.step(20);
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		// gravity pulls down the circle, but spring fights back
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.00387, p.distance(v2), 1e-5);
		TestCase.assertEquals(7.695, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
		
		// swap the lower limit
		dj.setLimitsEnabled(1.0, 5.0);
		w.step(2);
		
		// gravity pulls down the circle, but spring fights back
		v2 = b.getWorldCenter();
		TestCase.assertEquals(1.0, p.distance(v2), 1e-5);
		TestCase.assertEquals(7.696, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);

		// swap the upper limit
		dj.setLimitsEnabled(-1.0, -0.5);
		w.step(2);
		
		// gravity pulls down the circle, but spring fights back
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.5, p.distance(v2), 1e-5);
		TestCase.assertEquals(911.350, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
		
		dj.setLimitsEnabled(-1.5, 1.0);
		dj.setMaximumSpringForceEnabled(true);
		dj.setMaximumSpringForce(200);
		w.step(2);
		
		// gravity pulls down the circle, but spring fights back
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.29596, p.distance(v2), 1e-5);
		TestCase.assertEquals(200.0, dj.getSpringForce(invdt), 1e-3);
		TestCase.assertEquals(200.0, dj.getReactionForce(invdt).getMagnitude(), 1e-3);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt), 1e-3);
	}

	/**
	 * Tests the bodies with a motor
	 */
	@Test
	public void motorOnly() {
		World<Body> w = new World<Body>();
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
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		Vector2 p = b.getWorldCenter();
		WheelJoint<Body> dj = new WheelJoint<Body>(g, b, p, new Vector2(-1.0, 0.0));
		
		// NOTE: that I've set the rest distance to more than the limits
		dj.setMaximumMotorTorqueEnabled(true);
		dj.setMaximumMotorTorque(10000);
		dj.setMotorSpeed(Math.toRadians(90));
		dj.setMotorEnabled(true);
		w.addJoint(dj);
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		Vector2 v2 = b.getWorldCenter();
		Vector2 v = b.getLinearVelocity();
		TestCase.assertEquals(0.0, p.distance(v2));
		TestCase.assertEquals(0.0, dj.getAngularTranslation());
		TestCase.assertEquals(0.0, v.x);
		TestCase.assertEquals(0.0000, dj.getAngularSpeed(), 1e-5);
		TestCase.assertEquals(0.0, dj.getReactionTorque(invdt));
		
		w.step(1);
		
		// since the bodies are already 2.0 units apparent, nothing should happen
		v2 = b.getWorldCenter();
		v = b.getLinearVelocity();
		TestCase.assertEquals(0.0, p.distance(v2), 1e-3);
		TestCase.assertEquals(0.026, dj.getAngularTranslation(), 1e-3);
		TestCase.assertEquals(0.0, v.x, 1e-3);
		TestCase.assertEquals(1.5707, dj.getAngularSpeed(), 1e-3);
		TestCase.assertEquals(9.252, dj.getReactionTorque(invdt), 1e-3);
		TestCase.assertEquals(9.252, dj.getMotorTorque(invdt), 1e-3);
	}
}
