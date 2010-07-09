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
import org.dyn4j.game2d.dynamics.joint.RevoluteJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the a motorized revolute joint.
 * @author William Bittle
 */
public class Motor extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Motor";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests a motorized revolute joint.";
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
		this.bounds = new Rectangle(150.0, 15.0);
		
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
		// create some shapes
		// the floor rect
		Rectangle floorRect = new Rectangle(50.0, 1.0);
		// the wheel base frame rect
		Rectangle frameRect = new Rectangle(5.0, 0.25);
		// the body rect
		Rectangle bodyRect  = new Rectangle(5.2, 0.5);
		// tail gate rectangles
		Rectangle tgRect1   = new Rectangle(0.25, 0.5);
		Rectangle tgRect2   = new Rectangle(0.25, 0.5);
		// payload rectangle
		Rectangle pRect     = new Rectangle(0.25, 0.25);
		// the slope rect
		Rectangle slopeRect = new Rectangle(10.0, 0.2);
		// the wheel circle
		Circle wheelCircle  = new Circle(0.5);
		
		// create the fixtures for the bodies
		Fixture floorFixture  = new Fixture(floorRect); floorFixture.setFriction(0.5);
		Fixture wheelFixture1 = new Fixture(wheelCircle); wheelFixture1.setFriction(0.5);
		Fixture wheelFixture2 = new Fixture(wheelCircle); wheelFixture2.setFriction(0.5);
		
		// create the floor
		Entity floor = new Entity();
		floor.addFixture(floorFixture);
		floor.setMass(Mass.Type.INFINITE);
		floor.translate(-2.0, -4.0);
		
		// create the car frame and body
		Entity body = new Entity();
		body.addFixture(new Fixture(frameRect));
		// locally transform the body fixture
		bodyRect.translate(0.0, 0.5);
		body.addFixture(new Fixture(bodyRect));
		// locally transform the first tail gate rect1 fixture
		tgRect1.translate(2.4, 1.0);
		body.addFixture(new Fixture(tgRect1));
		// locally transform the first tail gate rect2 fixture
		tgRect2.translate(-2.4, 1.0);
		body.addFixture(new Fixture(tgRect2));
		body.setMass();
		body.translate(-23.0, -3.0);
		
		// add some payload bodies
		double x, y;
		for (int i = 0; i < 8; i++) {
			x = -24.0 + 0.25 * i;
			for (int j = 0; j < 3; j++) {
				y = -2.0 + 0.25 * j;
				Entity payload1 = new Entity();
				payload1.addFixture(new Fixture(pRect));
				payload1.setMass();
				payload1.translate(x, y);
				this.world.add(payload1);
			}
		}
		
		// create the slope to go up
		Entity slope = new Entity();
		slope.addFixture(new Fixture(slopeRect));
		slope.setMass(Mass.Type.INFINITE);
		slope.translate(0.0, -3.0);
		slope.rotate(Math.toRadians(10), slope.getWorldCenter());
		
		// create the first wheel
		Entity wheel1 = new Entity();
		wheel1.addFixture(wheelFixture1);
		wheel1.setMass();
		wheel1.translate(-25.0, -3.0);
		
		// create the second wheel
		Entity wheel2 = new Entity();
		wheel2.addFixture(wheelFixture2);
		wheel2.setMass();
		wheel2.translate(-21.0, -3.0);
		
		// add the bodies to the world
		this.world.add(floor);
		this.world.add(slope);
		this.world.add(body);
		this.world.add(wheel1);
		this.world.add(wheel2);
		
		// create a revolute joint between the wheels and the frame
		Vector2 p1 = wheel1.getWorldCenter().copy();
		Vector2 p2 = wheel2.getWorldCenter().copy();
		
		// the rear wheel is just a normal revolute joint
		RevoluteJoint j1 = new RevoluteJoint(wheel1, body, p1);
		
		// the front wheel is a motorized revolute joint
		RevoluteJoint j2 = new RevoluteJoint(wheel2, body, p2);
		j2.setMotorSpeed(-1.0 * Math.PI);
		j2.setMaxMotorTorque(1000.0);
		j2.setMotorEnabled(true);
		
		// add the joints to the world
		this.world.add(j1);
		this.world.add(j2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 16.0;
		// set the camera offset
		this.offset.set(0.0, 2.0);
	}
}
