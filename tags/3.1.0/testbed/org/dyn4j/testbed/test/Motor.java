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

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

/**
 * Tests the a motorized revolute joint.
 * @author William Bittle
 * @version 3.0.1
 * @since 1.0.0
 */
public class Motor extends Test {
	/** The car body */
	private Entity carBody = null;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
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
	 * @see org.dyn4j.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// create the world
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(150.0, 15.0));
		this.world = new World(bounds);
		
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
		BodyFixture floorFixture  = new BodyFixture(floorRect); floorFixture.setFriction(0.5);
		BodyFixture wheelFixture1 = new BodyFixture(wheelCircle); wheelFixture1.setFriction(0.5);
		BodyFixture wheelFixture2 = new BodyFixture(wheelCircle); wheelFixture2.setFriction(0.5);
		
		// create the floor
		Entity floor = new Entity();
		floor.addFixture(floorFixture);
		floor.setMass(Mass.Type.INFINITE);
		floor.translate(-2.0, -4.0);
		
		// create the car frame and body
		Entity body = new Entity();
		body.addFixture(new BodyFixture(frameRect));
		// locally transform the body fixture
		bodyRect.translate(0.0, 0.5);
		body.addFixture(new BodyFixture(bodyRect));
		// locally transform the first tail gate rect1 fixture
		tgRect1.translate(2.4, 1.0);
		body.addFixture(new BodyFixture(tgRect1));
		// locally transform the first tail gate rect2 fixture
		tgRect2.translate(-2.4, 1.0);
		body.addFixture(new BodyFixture(tgRect2));
		body.setMass();
		body.translate(-23.0, -3.0);
		carBody = body;
		
		// add some payload bodies
		double x, y;
		for (int i = 0; i < 8; i++) {
			x = -24.0 + 0.25 * i;
			for (int j = 0; j < 3; j++) {
				y = -2.0 + 0.25 * j;
				Entity payload1 = new Entity();
				payload1.addFixture(new BodyFixture(pRect));
				payload1.setMass();
				payload1.translate(x, y);
				this.world.add(payload1);
			}
		}
		
		// create the slope to go up
		Entity slope = new Entity();
		slope.addFixture(new BodyFixture(slopeRect));
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
		
		// the rear wheel is a motorized revolute joint
		RevoluteJoint j1 = new RevoluteJoint(body, wheel1, p1);
		j1.setMotorSpeed(Math.PI);
		// make sure we also specify the maximum motor torque since it defaults to zero
		j1.setMaximumMotorTorque(1000.0);
		j1.setMotorEnabled(true);
		
		// the front wheel is a motorized revolute joint
		RevoluteJoint j2 = new RevoluteJoint(body, wheel2, p2);
		j2.setMotorSpeed(Math.PI);
		// make sure we also specify the maximum motor torque since it defaults to zero
		j2.setMaximumMotorTorque(1000.0);
		j2.setMotorEnabled(true);
		
		// add the joints to the world
		this.world.add(j1);
		this.world.add(j2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#update(double)
	 */
	@Override
	public void update(double dt) {
		// update the world
		super.update(dt);
		// after the world has been updated
		// update the camera location
		Vector2 pos = this.carBody.getWorldCenter();
		this.offset.set(-pos.x, -pos.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
	}
}
