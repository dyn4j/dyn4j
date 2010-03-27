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
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the distance joint in a Newton's Cradle configuration.
 * @author William Bittle
 */
public class JointCollision extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests a distance joint with the no collide flag enabled/disabled.";
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
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.add(floor);
		
		/*
		 * Make this configuration
		 * +-----+
		 * |     |
		 * |     |
		 * |  .  |
		 * +--|--+
		 *    |
		 * +--|--+
		 * |  .  |
		 * |     |
		 * |     |
		 * +-----+
		 */
		
		// create a reusable rectangle
		Rectangle r = new Rectangle(0.5, 1.0);
		
		Entity obj1 = new Entity();
		obj1.addShape(r, Mass.create(r));
		obj1.setMassFromShapes();
		obj1.translate(2.0, 3.6);
		
		Entity obj2 = new Entity();
		obj2.addShape(r, Mass.create(r));
		obj2.setMassFromShapes();
		obj2.translate(2.0, 2.4);
		
		this.world.add(obj1);
		this.world.add(obj2);
		
		// compute the joint points
		Vector p1 = obj1.getWorldCenter().copy();
		Vector p2 = obj2.getWorldCenter().copy();
		p1.add(0.0, -0.4);
		p2.add(0.0, 0.4);
		
		// join them
		Joint j1 = new DistanceJoint(obj1, obj2, true, p1, p2);
		this.world.add(j1);
		
		Entity obj3 = new Entity();
		obj3.addShape(r, Mass.create(r));
		obj3.setMassFromShapes();
		obj3.translate(-2.0, 3.6);
		
		Entity obj4 = new Entity();
		obj4.addShape(r, Mass.create(r));
		obj4.setMassFromShapes();
		obj4.translate(-2.0, 2.4);
		
		this.world.add(obj3);
		this.world.add(obj4);
		
		// compute the joint points
		Vector p3 = obj3.getWorldCenter().copy();
		Vector p4 = obj4.getWorldCenter().copy();
		p3.add(0.0, -0.4);
		p4.add(0.0, 0.4);
		
		// join them
		Joint j2 = new DistanceJoint(obj3, obj4, false, p3, p4);
		this.world.add(j2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.zero();
	}
}
