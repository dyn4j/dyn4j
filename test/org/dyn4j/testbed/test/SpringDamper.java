/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

/**
 * Tests the distance joint's spring damper capabilities.
 * @author William Bittle
 * @version 2.2.2
 * @since 1.0.0
 */
public class SpringDamper extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Spring-Damper";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests a spring/damper joint.";
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
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(16.0, 15.0));
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.add(floor);
		
		/*
		 * Make this configuration
		 * +------------------------+
		 * |                        |
		 * | .                    . |
		 * +-|--------------------|-+
		 *   0                    0
		 */
		
		// create a reusable rectangle
		Rectangle r = new Rectangle(3.0, 0.5);
		BodyFixture fr = new BodyFixture(r);
		fr.setDensity(0.2);
		// create a reusable circle
		Circle c = new Circle(0.25);
		BodyFixture fc = new BodyFixture(c);
		fc.setDensity(0.5);
		fc.setFriction(0.5);
		
		Entity body = new Entity();
		body.addFixture(fr);
		body.setMass();
		body.translate(0, 4.25);
		
		Entity wheel1 = new Entity();
		wheel1.addFixture(fc);
		wheel1.setMass();
		wheel1.translate(-1.0, 3.6);
		
		Entity wheel2 = new Entity();
		wheel2.addFixture(fc);
		wheel2.setMass();
		wheel2.translate(1.0, 3.6);
		
		this.world.add(body);
		this.world.add(wheel1);
		this.world.add(wheel2);
		
		// create a  fixed distance joint between the wheels
		Vector2 p1 = wheel1.getWorldCenter().copy();
		Vector2 p2 = wheel2.getWorldCenter().copy();
		Vector2 p3 = body.getWorldCenter().copy();
		
		// join them
		DistanceJoint j1 = new DistanceJoint(body, wheel1, p3, p1);
		j1.setCollisionAllowed(true);
		this.world.add(j1);
		DistanceJoint j2 = new DistanceJoint(body, wheel2, p3, p2);
		j2.setCollisionAllowed(true);
		this.world.add(j2);
		
		// create a spring joint for the rear wheel
		DistanceJoint j3 = new DistanceJoint(body, wheel1, new Vector2(-1.0, 4.0), p1);
		j3.setCollisionAllowed(true);
		j3.setFrequency(8.0);
		j3.setDampingRatio(0.4);
		this.world.add(j3);
		
		// create a spring joint for the front wheel
		DistanceJoint j4 = new DistanceJoint(body, wheel2, new Vector2(1.0, 4.0), p2);
		j4.setCollisionAllowed(true);
		j4.setFrequency(8.0);
		j4.setDampingRatio(0.4);
		this.world.add(j4);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, 2.0);
	}
}
