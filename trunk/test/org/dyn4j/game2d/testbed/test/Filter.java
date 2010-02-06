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
import org.dyn4j.game2d.collision.CategoryFilter;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests body filtering.
 * @author William Bittle
 */
public class Filter extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests body collision filtering.  The right most box will pass " +
			   "through the middle box and then hit the left most box.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// set the camera position and zoom
		this.home();
		
		// set the bounds
		this.bounds = new Rectangle(16.0, 18.0);
		this.bounds.translate(0.0, 6.0);
		
		// create the world
		Bounds bounds = new RectangularBounds(this.bounds);
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// setup the bodies in the world
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	protected void setup() {
		// create the floor
		// if no filter is supplied or if the filter is not the same type 
		// as the other body then the body will collide with everything
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		List<Convex> floorShapes = new ArrayList<Convex>(1);
		floorShapes.add(floorRect);
		Mass floorMass = Mass.create(floorRect.getCenter());
		Entity floor = new Entity(floorShapes, floorMass);
		this.world.add(floor);
		
		// create some filters (collide with all & in category 1 by default)
		CategoryFilter filter = new CategoryFilter();
		// in category 2 and will collide only with objects in category 2
		CategoryFilter filter2 = new CategoryFilter(2, 2);
		
		// reusables
		List<Convex> shapes;
		
		// create a left traveling object
		Rectangle r1 = new Rectangle(1.0, 1.0);
		shapes = new ArrayList<Convex>(1);
		shapes.add(r1);
		
		Entity left = new Entity(shapes, Mass.create(r1, 1.0));
		left.translate(0.0, 2.0);
		left.getV().set(-5.0, 0.0);
		left.setFilter(filter);
		this.world.add(left);
		
		// create a right traveling object
		Rectangle r2 = new Rectangle(1.0, 1.0);
		shapes = new ArrayList<Convex>(1);
		shapes.add(r2);
		
		Entity right1 = new Entity(shapes, Mass.create(r2, 1.0));
		right1.translate(-2.0, 2.0);
		right1.setFilter(filter2);
		this.world.add(right1);
		
		// create a second right traveling object
		Rectangle r3 = new Rectangle(1.0, 1.0);
		shapes = new ArrayList<Convex>(1);
		shapes.add(r3);
		
		Entity right2 = new Entity(shapes, Mass.create(r3, 1.0));
		right2.translate(-4.0, 2.0);
		right2.setFilter(filter);
		this.world.add(right2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the offset
		this.offset.set(0.0, -5.0);
	}
}
