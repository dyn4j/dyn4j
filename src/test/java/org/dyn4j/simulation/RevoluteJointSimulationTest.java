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
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link RevoluteJoint} class in simulation.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class RevoluteJointSimulationTest {
	/**
	 * Tests the body separation as enforced by the distance joint.
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
		
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(g, b, b.getWorldCenter());
		w.addJoint(rj);

		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		w.step(4);
		
		// because the allowed axis is the x-axis and the position of the axis is at body2's world center
		// gravity is applied but has no effect
		Vector2 v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(7.6969, rj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		// apply some velocity along the y-axis
		b.setLinearVelocity(0.0, 1.0);
		w.step(4);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(7.6969, rj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		// now apply some velocity along the x-axis
		b.setLinearVelocity(1.0, 1.0);
		w.step(4);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(7.6969, rj.getReactionForce(invdt).getMagnitude(), 1e-5);
		
		b.applyTorque(Math.toRadians(30));
		w.step(1);
		
		// still nothing should happen
		v2 = b.getWorldCenter();
		TestCase.assertEquals(0.0, v2.x);
		TestCase.assertEquals(2.0, v2.y);
		TestCase.assertEquals(0.00148, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.0, rj.getReactionTorque(invdt), 1e-5);
		TestCase.assertEquals(7.6969, rj.getReactionForce(invdt).getMagnitude(), 1e-5);
	}
	
	/**
	 * Tests the bodies not exceeding the limits
	 */
	@Test
	public void limits() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		w.getSettings().setAngularTolerance(0.0);
		
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
		
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(g, b, b.getWorldCenter());
		
		// NOTE: that I've set the rest distance to more than the limits
		rj.setLimitsEnabled(-Math.toRadians(30), Math.toRadians(30));
		w.addJoint(rj);
		
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		TestCase.assertEquals(0.0, rj.getJointAngle());
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle());
		TestCase.assertEquals(0.0, rj.getLimitsReferenceAngle());
		
		b.setAngularVelocity(Math.toRadians(10));
		w.step(1);
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		// since the bodies within the limits, it should continue to rotate freely
		TestCase.assertEquals(0.00290, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.17453, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(0.17453, rj.getJointSpeed(), 1e-3);
		TestCase.assertEquals(-0.00290, rj.getJointAngle(), 1e-3);
		TestCase.assertEquals(0.0, rj.getReactionTorque(invdt), 1e-3);
		
		rj.setLimitsEnabled(Math.toRadians(10), Math.toRadians(30));
		w.step(1);
		
		// the bodies should be placed at the lower limit
		TestCase.assertEquals(-0.17453, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.00000, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(0.00000, rj.getJointSpeed(), 1e-3);
		TestCase.assertEquals(0.17453, rj.getJointAngle(), 1e-3);
		TestCase.assertEquals(1.028, rj.getReactionTorque(invdt), 1e-3);
		
		b.setAngularVelocity(-Math.toRadians(10));
		rj.setLimitsEnabled(-Math.toRadians(30), Math.toRadians(5));
		w.step(1);
		
		// the bodies should be placed at the upper limit
		TestCase.assertEquals(-0.08726, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.00000, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(0.00000, rj.getJointSpeed(), 1e-3);
		TestCase.assertEquals(0.08726, rj.getJointAngle(), 1e-3);
		TestCase.assertEquals(-1.028, rj.getReactionTorque(invdt), 1e-3);
	}

	/**
	 * Tests the bodies with a motor
	 */
	@Test
	public void motorWithAndWithoutLimits() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		w.getSettings().setAngularTolerance(0.0);
		
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
		
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(g, b, b.getWorldCenter());
		
		// NOTE: that I've set the rest distance to more than the limits
		rj.setMaximumMotorTorque(1000);
		rj.setMaximumMotorTorqueEnabled(true);
		rj.setMotorSpeed(Math.toRadians(20));
		rj.setMotorEnabled(true);
		w.addJoint(rj);
		
		double invdt = w.getTimeStep().getInverseDeltaTime();
		
		TestCase.assertEquals(0.0, rj.getJointAngle());
		TestCase.assertEquals(0.0000, rj.getJointSpeed(), 1e-5);
		TestCase.assertEquals(0.0, rj.getMotorTorque(invdt));
		
		w.step(1);
		
		// since the bodies are already 2.0 units apparent, nothing should happen
		TestCase.assertEquals(-0.00581, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(-0.34906, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(Math.toRadians(20) / 60.0, rj.getJointAngle(), 1e-5);
		TestCase.assertEquals(-Math.toRadians(20), rj.getJointSpeed(), 1e-5);
		TestCase.assertEquals(2.05616, rj.getReactionTorque(invdt), 1e-3);
		
		rj.setLimitsEnabled(-Math.toRadians(5), Math.toRadians(5));
		w.step(30);
		
		// the bodies should be placed at the upper limit
		TestCase.assertEquals(-0.08726, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.00000, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(0.00000, rj.getJointSpeed(), 1e-3);
		TestCase.assertEquals(0.08726, rj.getJointAngle(), 1e-3);
		TestCase.assertEquals(0.000, rj.getReactionTorque(invdt), 1e-3);
		
		rj.setMotorSpeed(-Math.toRadians(20));
		w.step(1);
		
		// the bodies should be placed at the upper limit
		TestCase.assertEquals(-0.08144, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.34906, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(0.34906, rj.getJointSpeed(), 1e-3);
		TestCase.assertEquals(0.08144, rj.getJointAngle(), 1e-3);
		TestCase.assertEquals(-2.05616, rj.getReactionTorque(invdt), 1e-3);
		
		w.step(60);
		
		// the bodies should be placed at the lower limit
		TestCase.assertEquals(0.08726, b.getTransform().getRotationAngle(), 1e-5);
		TestCase.assertEquals(0.00000, b.getAngularVelocity(), 1e-5);
		TestCase.assertEquals(0.00000, rj.getJointSpeed(), 1e-3);
		TestCase.assertEquals(-0.08726, rj.getJointAngle(), 1e-3);
		TestCase.assertEquals(0.000, rj.getReactionTorque(invdt), 1e-3);
	}
	
}
