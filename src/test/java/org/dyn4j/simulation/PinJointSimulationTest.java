/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link PinJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class PinJointSimulationTest {
	/**
	 * Tests the pin joint with a body who has FIXED_LINEAR_VELOCITY as its
	 * mass type.  The pin joint applied at a point on the body should rotate
	 * the body (before it wasn't doing anything).
	 */
	@Test
	public void fixedLinearVelocity() {
		World w = new World();
		
		Body body = new Body();
		body.addFixture(Geometry.createCircle(1.0));
		body.setMass(MassType.FIXED_LINEAR_VELOCITY);
		w.addBody(body);
		
		PinJoint<Body> pj = new PinJoint<Body>(body, new Vector2(0.5, 0.0), 8.0, 0.3, 1000.0);
		w.addJoint(pj);
		
		pj.setTarget(new Vector2(0.7, -0.5));
		
		w.step(1);
		
		TestCase.assertTrue(pj.getReactionForce(w.getTimeStep().getInverseDeltaTime()).getMagnitude() > 0);
		TestCase.assertTrue(pj.getReactionForce(w.getTimeStep().getInverseDeltaTime()).getMagnitude() <= 1000.0);
		TestCase.assertTrue(body.getTransform().getRotationAngle() < 0);
	}
}
