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
package org.dyn4j.samples;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

/**
 * A simple scene of two bodies that when collided will stop immediately.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public class StopBodyAfterCollision extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 7229897723400288930L;

	/**
	 * Custom contact listener to stop the bodies.
	 * @author William Bittle
	 * @version 3.2.0
	 * @since 3.2.0
	 */
	private static class StopContactListener extends CollisionAdapter {
		private Body b1, b2;
		
		public StopContactListener(Body b1, Body b2) {
			this.b1 = b1;
			this.b2 = b2;
		}
		@Override
		public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
			// the bodies can appear in either order
			if ((body1 == b1 && body2 == b2) ||
			    (body1 == b2 && body2 == b1)) {
				// its the collision we were looking for
				// do whatever you need to do here
				
				// stopping them like this isn't really recommended
				// there are probably better ways to do what you want
				
				body1.getLinearVelocity().zero();
				body1.setAngularVelocity(0.0);
				body2.getLinearVelocity().zero();
				body2.setAngularVelocity(0.0);
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Default constructor.
	 */
	public StopBodyAfterCollision() {
		super("Stop Body After Collision", 45.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// create the world
		this.world.setGravity(World.ZERO_GRAVITY);
		
		// create all your bodies/joints
		
		// create a circle
		SimulationBody circle = new SimulationBody();
		circle.addFixture(Geometry.createCircle(0.5));
		circle.setMass(MassType.NORMAL);
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.applyForce(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.addBody(circle);
		
		// try a rectangle
		SimulationBody rectangle = new SimulationBody();
		rectangle.addFixture(Geometry.createRectangle(1, 1));
		rectangle.setMass(MassType.NORMAL);
		rectangle.translate(0.0, 2.0);
		this.world.addBody(rectangle);
		
		this.world.addListener(new StopContactListener(circle, rectangle));
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		StopBodyAfterCollision simulation = new StopBodyAfterCollision();
		simulation.run();
	}
}
