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
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

/**
 * Tests body filtering.
 * @author William Bittle
 * @version 3.0.0
 * @since 1.0.0
 */
public class Filter extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Filter";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests body collision filtering.  The right most box will pass " +
			   "through the middle box and then hit the left most box.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// set the camera position and zoom
		this.home();
		
		// create the world
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(16.0, 18.0));
		bounds.translate(0.0, 6.0);
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.addListener(cc);
		
		// setup the bodies in the world
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#setup()
	 */
	protected void setup() {
		// create the floor
		// if no filter is supplied or if the filter is not the same type 
		// as the other body then the body will collide with everything
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		this.world.add(floor);
		
		// create some filters (collide with all & in category 1 by default)
		CategoryFilter filter = new CategoryFilter();
		// in category 2 and will collide only with objects in category 2
		CategoryFilter filter2 = new CategoryFilter(2, 2);
		
		// be careful here, for example:
		// CategoryFilter filter3 =  new CategoryFilter(3, 3)
		// says that filter3 is of category 1 and 2 and can collide
		// with 1 and 2.  This is explained by the binary representation of
		// the numbers:
		// 1 = 0...0001
		// 2 = 0...0010
		// 3 = 0...0011
		
		// So categories are defined by 2^n where an integer would allow 32 categories:
		// category 1 =  1 = 2^0
		// category 2 =  2 = 2^1
		// category 3 =  4 = 2^2
		// category 4 =  8 = 2^3
		// category 5 = 16 = 2^4
		// etc.
		
		// Likewise the mask are combinations of the categories OR-ed together
		// for example, to create a filter that allows collision from category
		// 1 and 2 we OR the categories together
		// category 1 = 1 = 2^0 = 0...0001
		// category 2 = 2 = 2^1 = 0...0010
		//--------------------------------
		// mask                 = 0...0011
		
		// see the Javadocs on CategoryFilter for more details
		
		// create a left traveling object
		Rectangle r1 = new Rectangle(1.0, 1.0);
		
		BodyFixture f1 = new BodyFixture(r1);
		f1.setFilter(filter);
		
		Entity left = new Entity();
		left.addFixture(f1);
		left.setMass();
		left.translate(0.0, 2.0);
		left.getVelocity().set(-5.0, 0.0);
		
		this.world.add(left);
		
		// create a right traveling object
		Rectangle r2 = new Rectangle(1.0, 1.0);
		
		BodyFixture f2 = new BodyFixture(r2);
		f2.setFilter(filter2);
		
		Entity right1 = new Entity();
		right1.addFixture(f2);
		right1.setMass();
		right1.translate(-2.0, 2.0);
		
		this.world.add(right1);
		
		// create a second right traveling object
		Rectangle r3 = new Rectangle(1.0, 1.0);
		
		BodyFixture f3 = new BodyFixture(r3);
		f3.setFilter(filter);
		
		Entity right2 = new Entity();
		right2.addFixture(f3);
		right2.setMass();
		right2.translate(-4.0, 2.0);
		
		this.world.add(right2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the offset
		this.offset.set(0.0, -4.0);
	}
}
