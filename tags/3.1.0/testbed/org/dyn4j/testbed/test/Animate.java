/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.testbed.test;

import javax.media.opengl.GL2;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.GLHelper;
import org.dyn4j.testbed.Test;

/**
 * Tests the angle joint.
 * @author William Bittle
 * @version 3.1.0
 * @since 3.1.0
 */
public class Animate extends Test {
	/** The motor joint */
	private MotorJoint mj;
	
	/** The time since the last update */
	private double time;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Animate";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the motor joint.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// create the world
		//Bounds bounds = new RectangularBounds(Geometry.createRectangle(16.0, 15.0));
		this.world = new World();
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.addListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#setup()
	 */
	@Override
	protected void setup() {
		Entity floor = new Entity();
		floor.addFixture(Geometry.createRectangle(40.0, 0.5));
		floor.setMass(Mass.Type.INFINITE);
		
		Entity character = new Entity();
		BodyFixture bf = character.addFixture(Geometry.createRectangle(2.0, 0.5));
		bf.setDensity(2.0);
		bf.setFriction(0.6);
		character.setMass();
		character.translate(0.0, 0.5);
		
		mj = new MotorJoint(floor, character);
		mj.setMaximumForce(1000);
		mj.setMaximumTorque(1000);
		mj.setLinearTarget(new Vector2(1.0, 4.0));
		mj.setAngularTarget(Math.toRadians(30));
		mj.setCorrectionFactor(1.0);
		
		this.world.add(floor);
		this.world.add(character);
		this.world.add(mj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the camera offset
		this.offset.set(0.0, -2.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#update(double)
	 */
	@Override
	public void update(double dt) {
		super.update(dt);
		
		time += dt;
		// after one second reverse the target
		if (time > 0.5) {
			Vector2 nlt = mj.getLinearTarget().copy();
			nlt.x *= -1.0;
			mj.setLinearTarget(nlt);
			
			double nat = mj.getAngularTarget() * -1.0;
			mj.setAngularTarget(nat);
			
			time = 0.0;
		}
	}
	
	@Override
	protected void renderAfter(GL2 gl) {
		super.renderAfter(gl);
		
		Vector2 lt = mj.getLinearTarget();
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		GLHelper.fillCircle(gl, lt.x, lt.y, 0.1, 10);
	}
}
