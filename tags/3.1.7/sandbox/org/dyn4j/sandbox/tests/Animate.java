/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.tests;

import javax.media.opengl.GL2;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;

/**
 * Compiled test for the MotorJoint class.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.2
 */
public class Animate extends CompiledSimulation {
	/** The motor joint */
	private MotorJoint mj;
	
	/** The time since the last update */
	private double time;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#initialize()
	 */
	@Override
	public void initialize() {
		this.world.setUserData("Animate");
		
		SandboxBody floor = new SandboxBody();
		floor.addFixture(Geometry.createRectangle(40.0, 0.5));
		floor.setMass(Mass.Type.INFINITE);
		floor.setUserData("Floor");
		
		SandboxBody character = new SandboxBody();
		BodyFixture bf = character.addFixture(Geometry.createRectangle(2.0, 0.5));
		bf.setDensity(2.0);
		bf.setFriction(0.6);
		character.setMass();
		character.translate(0.0, 0.5);
		character.setUserData("Character");
		
		this.mj = new MotorJoint(floor, character);
		this.mj.setMaximumForce(1000);
		this.mj.setMaximumTorque(1000);
		this.mj.setLinearTarget(new Vector2(1.0, 4.0));
		this.mj.setAngularTarget(Math.toRadians(30));
		this.mj.setCorrectionFactor(1.0);
		this.mj.setUserData("MotorJoint1");
		
		this.world.addBody(floor);
		this.world.addBody(character);
		this.world.addJoint(this.mj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#update(double, boolean)
	 */
	@Override
	public void update(double elapsedTime, boolean stepped) {
		this.time += elapsedTime;
		// after one second reverse the target
		if (this.time > 0.75) {
			Vector2 nlt = this.mj.getLinearTarget().copy();
			nlt.x *= -1.0;
			this.mj.setLinearTarget(nlt);
			
			double nat = this.mj.getAngularTarget() * -1.0;
			this.mj.setAngularTarget(nat);
			
			this.time = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#render(javax.media.opengl.GL2)
	 */
	@Override
	public void render(GL2 gl) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#reset()
	 */
	@Override
	public void reset() {
		this.time = 0;
		// remove everything from the world
		this.world.removeAllBodiesAndJoints();
		// add it all back
		this.initialize();
	}
}
