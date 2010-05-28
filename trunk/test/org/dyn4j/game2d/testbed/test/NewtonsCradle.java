/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.testbed.test;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the distance joint in a Newton's Cradle configuration.
 * @author William Bittle
 */
public class NewtonsCradle extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the distance joint in a Newton's Cradle configuration.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// set the bounds
		this.bounds = new Rectangle(16.0, 15.0);
		
		// create the world
		Bounds bounds = new RectangularBounds(this.bounds);
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addShape(floorRect, Mass.create(floorRect));
		floor.setMassFromShapes(Mass.Type.INFINITE);
		// move the floor up a bit
		floor.translate(0.0, 4.0);
		this.world.add(floor);
		
		double x = -2.0;
		double y =  0.0;
		Entity e = null;
		// loop to create the balls and joints
		for (int i = 0; i < 5; i++) {
			// create a circle
			Circle circle = new Circle(0.5);
			Entity ball = new Entity();
			ball.addShape(circle, Mass.create(circle));
			ball.setMassFromShapes();
			ball.setE(0.8);
			ball.setLinearDamping(0.1);
			ball.translate(x, y);
			e = ball;
			this.world.add(ball);
			
			// create the joint
			DistanceJoint dj = new DistanceJoint(floor,
					                             ball,
					                             true,
					                             new Vector(x, 4.0), 
					                             ball.getWorldCenter());
			this.world.add(dj);
			
			x += 1.0;
		}
		
		// move the last body
		e.rotate(Math.toRadians(60), 2.0, 4.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, -1.0);
	}
}
