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
 * Tests multi-shape bodies with concavity.
 * @author William Bittle
 */
public class Concave extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests multi-shape bodies with concavity."
		+ "  A concave body is dropped on a box that should fit"
		+ " within the concave body's cavity.";
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
		this.bounds = new Rectangle(16.0, 15.0);
		
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
	@Override
	protected void setup() {
		//////////////////////////////////////
		// create the bodies
		//////////////////////////////////////
		
		// create the floor
		Rectangle rect2 = new Rectangle(15.0, 1.0);
		List<Convex> geometry2 = new ArrayList<Convex>();
		geometry2.add(rect2);
		Mass mass2 = Mass.create(rect2.getCenter());
		
		Entity obj0 = new Entity(geometry2, mass2);
		this.world.add(obj0);
		
		// create the concave object
		/* +-----------------+
		 * |                 |
		 * +-----------------+
		 * +---+         +---+
		 * |   |         |   |
		 * |   |         |   |
		 * +---+         +---+
		 */
		Rectangle top = new Rectangle(3.0, 1.0);
		Rectangle left = new Rectangle(1.0, 1.0);
		Rectangle right = new Rectangle(1.0, 1.0);
		
		// setup the relative coordinates
		top.translate(0.0, 0.5);
		left.translate(-1.0, -0.5);
		right.translate(1.0, -0.5);
		
		// add all the shapes
		List<Convex> geometry = new ArrayList<Convex>(3);
		geometry.add(top);
		geometry.add(left);
		geometry.add(right);
		
		// create the mass
		Mass massTop = Mass.create(top, 1.0);
		Mass massLeft = Mass.create(left, 1.0);
		Mass massRight = Mass.create(right, 1.0);
		
		Mass mass = Mass.create(massTop, massLeft, massRight);
		
		// create the object
		Entity obj1 = new Entity(geometry, mass);
		obj1.translate(0.0, 4.0);
		this.world.add(obj1);
		
		// setup a small object to go in between the n
		Rectangle small = new Rectangle(0.5, 0.5);
		List<Convex> smallGeometry = new ArrayList<Convex>();
		smallGeometry.add(small);
		Mass smallMass = Mass.create(small, 1.0);
		Entity obj2 = new Entity(smallGeometry, smallMass);
		obj2.translate(0.0, 1.0);
		this.world.add(obj2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the offset
		this.offset.zero();
	}
}
