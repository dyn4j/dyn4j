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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
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
		List<Convex> floorShapes = new ArrayList<Convex>();
		floorShapes.add(floorRect);
		Mass floorMass = Mass.create(floorRect.getCenter());
		Entity floor = new Entity(floorShapes, floorMass);
		
		// create some slopes
		Rectangle slope1Rect = new Rectangle(9.0, 0.5);
		List<Convex> slope1Shapes = new ArrayList<Convex>();
		slope1Shapes.add(slope1Rect);
		Mass slope1Mass = Mass.create(slope1Rect.getCenter());
		Entity slope1 = new Entity(slope1Shapes, slope1Mass);
		slope1.translate(-3.0, 7.0);
		slope1.rotate(Math.toRadians(-20), slope1.getWorldCenter());
		this.world.add(slope1);
		
		Rectangle slope2Rect = new Rectangle(7.0, 0.5);
		List<Convex> slope2Shapes = new ArrayList<Convex>();
		slope2Shapes.add(slope2Rect);
		Mass slope2Mass = Mass.create(slope2Rect.getCenter());
		Entity slope2 = new Entity(slope2Shapes, slope2Mass);
		slope2.translate(3.0, 4.0);
		slope2.rotate(Math.toRadians(20), slope2.getWorldCenter());
		this.world.add(slope2);
		
		Rectangle slope3Rect = new Rectangle(3.0, 0.2);
		List<Convex> slope3Shapes = new ArrayList<Convex>();
		slope3Shapes.add(slope3Rect);
		Mass slope3Mass = Mass.create(slope3Rect.getCenter());
		Entity slope3 = new Entity(slope3Shapes, slope3Mass);
		slope3.translate(5.0, 0.8);
		slope3.rotate(Math.toRadians(30), slope3.getWorldCenter());
		this.world.add(slope3);
		
		Rectangle slope4Rect = new Rectangle(3.0, 0.2);
		List<Convex> slope4Shapes = new ArrayList<Convex>();
		slope4Shapes.add(slope4Rect);
		Mass slope4Mass = Mass.create(slope4Rect.getCenter());
		Entity slope4 = new Entity(slope4Shapes, slope4Mass);
		slope4.translate(-5.0, 0.8);
		slope4.rotate(Math.toRadians(-30), slope4.getWorldCenter());
		this.world.add(slope4);
		
		// render the floor after the slope3 and slope4
		this.world.add(floor);
		
		// temp variables
		List<Convex> shapes = null;
		Mass mass = null;
		
		// create a circle
		Circle c1 = new Circle(0.5);
		shapes = new ArrayList<Convex>(1);
		shapes.add(c1);
		mass = Mass.create(c1, 1.0);
		Entity obj1 = new Entity(shapes, mass);
		obj1.translate(-1.5, 7.5);
		this.world.add(obj1);
		
		Circle c2 = new Circle(0.5);
		shapes = new ArrayList<Convex>(1);
		shapes.add(c2);
		mass = Mass.create(c2, 1.0);
		Entity obj2 = new Entity(shapes, mass);
		obj2.translate(-2.9, 8.0);
		this.world.add(obj2);
		
		// create a distance joint between them
		DistanceJoint j1 = new DistanceJoint(obj1,
				                                obj2, 
				                                obj1.getWorldCenter().copy(),
				                                obj2.getWorldCenter().copy());
		
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
