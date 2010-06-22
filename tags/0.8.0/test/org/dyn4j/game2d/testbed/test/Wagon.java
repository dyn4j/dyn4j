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
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the distance joint in a two wheel configuration.
 * @author William Bittle
 */
public class Wagon extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the distance joint in a two wheel configuration.";
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
		this.bounds = new Rectangle(30.0, 15.0);
		
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
		Rectangle floorRect = new Rectangle(20.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new Fixture(floorRect));
		floor.setMassFromShapes(Mass.Type.INFINITE);
		
		// create some slopes
		Rectangle slope1Rect = new Rectangle(9.0, 0.5);
		Entity slope1 = new Entity();
		slope1.addFixture(new Fixture(slope1Rect));
		slope1.setMassFromShapes(Mass.Type.INFINITE);
		slope1.translate(-3.0, 7.0);
		slope1.rotate(Math.toRadians(-20), slope1.getWorldCenter());
		this.world.add(slope1);
		
		Rectangle slope2Rect = new Rectangle(7.0, 0.5);
		Entity slope2 = new Entity();
		slope2.addFixture(new Fixture(slope2Rect));
		slope2.setMassFromShapes(Mass.Type.INFINITE);
		slope2.translate(3.0, 4.0);
		slope2.rotate(Math.toRadians(20), slope2.getWorldCenter());
		this.world.add(slope2);
		
		Rectangle slope3Rect = new Rectangle(3.0, 0.2);
		Entity slope3 = new Entity();
		slope3.addFixture(new Fixture(slope3Rect));
		slope3.setMassFromShapes(Mass.Type.INFINITE);
		slope3.translate(5.0, 0.8);
		slope3.rotate(Math.toRadians(30), slope3.getWorldCenter());
		this.world.add(slope3);
		
		Rectangle slope4Rect = new Rectangle(3.0, 0.2);
		Entity slope4 = new Entity();
		slope4.addFixture(new Fixture(slope4Rect));
		slope4.setMassFromShapes(Mass.Type.INFINITE);
		slope4.translate(-5.0, 0.8);
		slope4.rotate(Math.toRadians(-30), slope4.getWorldCenter());
		this.world.add(slope4);
		
		// render the floor after the slope3 and slope4
		this.world.add(floor);
		
		// create a circle
		Circle circle = new Circle(0.5);
		
		Entity wheel1 = new Entity();
		wheel1.addFixture(new Fixture(circle));
		wheel1.setMassFromShapes();
		wheel1.translate(-1.5, 7.5);
		this.world.add(wheel1);
		
		Entity wheel2 = new Entity();
		wheel2.addFixture(new Fixture(circle));
		wheel2.setMassFromShapes();
		wheel2.translate(-2.9, 8.0);
		this.world.add(wheel2);
		
		// create a distance joint between them
		DistanceJoint j1 = new DistanceJoint(wheel1,
											 wheel2,
				                             true,
				                             wheel1.getWorldCenter().copy(),
				                             wheel2.getWorldCenter().copy());
		
		this.world.add(j1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the camera offset
		this.offset.set(0.0, -3.0);
	}
}
