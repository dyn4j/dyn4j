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
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link AngleJoint} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class AngleJointSimulationTest {
	/**
	 * Tests the AngleJoint without limits or ratio.
	 */
	@Test
	public void simpleLinkage() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createCircle(0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.NORMAL);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		// 30 degrees/second
		b.setAngularVelocity(Math.toRadians(30.0));
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		AngleJoint<Body> aj = new AngleJoint<Body>(g, b);
		w.addJoint(aj);
		
		w.step(1);
		
		// since g was NOT moving and b was, and they are equal in mass/inertia then
		// the angular velocity is split between them
		TestCase.assertEquals(Math.toRadians(15), g.getAngularVelocity());
		TestCase.assertEquals(Math.toRadians(15), b.getAngularVelocity());
		
		aj.setRatio(0.5);
		w.step(1);
		
		// since limits are enabled, they will continue to move at the same rate
		TestCase.assertEquals(Math.toRadians(15), g.getAngularVelocity());
		TestCase.assertEquals(Math.toRadians(15), b.getAngularVelocity());
		
		aj.setLimitEnabled(false);
		w.step(1);
		
		// with limits disabled, the ratio should take effect causing body1 to
		// move at half the rate of body2
		TestCase.assertEquals(Math.toRadians(10), g.getAngularVelocity(), 1e-8);
		TestCase.assertEquals(Math.toRadians(20), b.getAngularVelocity(), 1e-8);
	}
	
	/**
	 * Tests the AngleJoint with limits hitting the upper limit.
	 */
	@Test
	public void withLimitsHitUpper() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createCircle(0.5));
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
		// 30 degrees/second
		b.setAngularVelocity(Math.toRadians(30.0));
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		AngleJoint<Body> aj = new AngleJoint<Body>(g, b);
		aj.setLimitsEnabled(Math.toRadians(-30.0), Math.toRadians(30.0));
		w.addJoint(aj);
		
		w.step(40);
		
		// normal motion for both
		TestCase.assertEquals(0.0, g.getAngularVelocity());
		TestCase.assertEquals(Math.toRadians(30), b.getAngularVelocity());
		
		w.step(40);
		
		// we hit the upper limit so velocity for b should be zero
		TestCase.assertEquals(0.0, g.getAngularVelocity());
		TestCase.assertEquals(0.0, b.getAngularVelocity());
	}
	

	/**
	 * Tests the AngleJoint with limits hitting the lower limit.
	 */
	@Test
	public void withLimitsHitLower() {
		World<Body> w = new World<Body>();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createCircle(0.5));
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
		// 30 degrees/second
		b.setAngularVelocity(Math.toRadians(-30.0));
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		AngleJoint<Body> aj = new AngleJoint<Body>(g, b);
		aj.setLimitsEnabled(Math.toRadians(-30.0), Math.toRadians(30.0));
		w.addJoint(aj);
		
		w.step(40);
		
		// normal motion for both
		TestCase.assertEquals(0.0, g.getAngularVelocity());
		TestCase.assertEquals(Math.toRadians(-30), b.getAngularVelocity());
		
		w.step(40);
		
		// we hit the upper limit so velocity for b should be zero
		TestCase.assertEquals(0.0, g.getAngularVelocity());
		TestCase.assertEquals(0.0, b.getAngularVelocity());
	}
}
