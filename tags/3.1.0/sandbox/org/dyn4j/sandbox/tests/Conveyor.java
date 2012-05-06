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
package org.dyn4j.sandbox.tests;

import javax.media.opengl.GL2;

import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.sandbox.SandboxBody;

/**
 * Compiled test for the contact tangent speed feature.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.2
 */
public class Conveyor extends CompiledSimulation {
	/** The collision listener */
	private CustomCollisionListener listener;
	
	/**
	 * A custom collision listener to set the tangent speed of contacts with the floor body.
	 * @author William Bittle
	 * @version 1.0.2
	 * @since 1.0.2
	 */
	private class CustomCollisionListener extends CollisionAdapter {
		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.CollisionAdapter#collision(org.dyn4j.dynamics.contact.ContactConstraint)
		 */
		@Override
		public boolean collision(ContactConstraint contactConstraint) {
			if (contactConstraint.getBody1() == floor) {
				contactConstraint.setTangentSpeed(-5.0);
			} else if (contactConstraint.getBody2() == floor) {
				contactConstraint.setTangentSpeed(5.0);
			}
			
			// allow all collisions
			return true;
		}
	}
	
	/** The floor body */
	private SandboxBody floor;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#initialize()
	 */
	@Override
	public void initialize() {
		this.world.setUserData("Conveyor");
		
		this.floor = new SandboxBody();
		this.floor.addFixture(Geometry.createRectangle(15.0, 1.0));
		this.floor.setMass(Mass.Type.INFINITE);
		this.floor.setUserData("Floor");
		
		SandboxBody box = new SandboxBody();
		box.addFixture(Geometry.createSquare(1.0));
		box.setMass();
		box.translate(0.0, 2.0);
		box.setUserData("Box");
		
		this.listener = new CustomCollisionListener();
		
		this.world.add(this.floor);
		this.world.add(box);
		this.world.addListener(this.listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#update(double, boolean)
	 */
	@Override
	public void update(double elapsedTime, boolean stepped) {}
	
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
		// remove everything from the world
		this.world.removeAll();
		// remove all the listeners
		this.world.removeListener(this.listener);
		// add it all back
		this.initialize();
	}
}
