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

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

/**
 * Tests a number of stacks of boxes.
 * @author William Bittle
 * @version 3.1.1
 * @since 2.1.0
 */
public class Parallel extends Test {
	/** The number of bodies in the stack */
	private static final int HEIGHT = 10;
	
	/** The number of stacks to create */
	private static final int STACKS = 30;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Parallel";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests " + STACKS + " stacks of boxes.";
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
		this.world = new World(new AxisAlignedBounds(STACKS + 10.0, 15.0));
		
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
		// create the floor
		Rectangle floorRect = new Rectangle(STACKS + 10, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		this.world.addBody(floor);
		
		// create the stack
		
		// set the width and height
		double height = 0.5;
		double width = 0.5;
		
		// create a reusable rect for the boxes
		Rectangle rect = new Rectangle(width, height);
		
		// initialize the x value
		double x = -width * STACKS;
		for (int i = 0; i < STACKS; i++) {
			// initialize the y translation
			double y = 0.75;
			
			// loop to create the stack
			for (int j = 0; j < HEIGHT; j++) {
				Entity e = new Entity();
				e.addFixture(new BodyFixture(rect));
				e.setMass();
				e.translate(x, y);
				this.world.addBody(e);
				// increment y
				y += height;
			}
			
			// increment x
			x += width * 2.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 16.0;
		// set the camera offset
		this.offset.set(0.0, -2.0);
	}
}