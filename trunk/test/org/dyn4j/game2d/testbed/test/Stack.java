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
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests a stack of boxes.
 * @author William Bittle
 */
public class Stack extends Test {
	/** The size of the stack in number of bodies */
	private static final int SIZE = 10;
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests a stack of boxes.  This test is the standard test for physics " +
			   "engines since it requires that many aspects be working correctly. The " +
			   "test is a single vertical stack of " + SIZE + " boxes.";
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
		List<Convex> floorGeom = new ArrayList<Convex>();
		floorGeom.add(floorRect);
		Mass floorMass = Mass.create(floorRect.getCenter());
		
		Entity floor = new Entity(floorGeom, floorMass);
		this.world.add(floor);
		
		// create the stack
		
		// set the width and height
		double height = 0.5;
		double width = 0.5;
		
		// create a reusable rect for the boxes
		Rectangle rect = new Rectangle(width, height);
		List<Convex> geometry = new ArrayList<Convex>(1);
		geometry.add(rect);
		Mass mass = Mass.create(rect, 1.0);
		
		// initialize the y translation
		double y = 0.75;
		
		// loop to create the stack
		for (int i = 0; i < SIZE; i++) {
			Entity e = new Entity(geometry, mass);
			e.translate(0.0, y);
			this.world.add(e);
			// increment y
			y += height;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the camera offset
		this.offset.zero();
	}
}
