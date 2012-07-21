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
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

/**
 * Tests the pulley joint.
 * @author William Bittle
 * @version 3.1.1
 * @since 2.1.0
 */
public class Pulley extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Pulley";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests pulley joint.";
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
		this.world = new World(new AxisAlignedBounds(16.0, 40.0));
		
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
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.addBody(floor);

		double x = 1.0;
		double y = 1.0;
		double w = 0.5;
		double h = 0.5;
		double l = 3.0;
		
		// create a reusable rectangle
		Rectangle r = new Rectangle(w, h);
		
		Entity obj1 = new Entity();
		BodyFixture f1 = obj1.addFixture(r);
		f1.setDensity(5.0);
		obj1.setMass();
		obj1.translate(-x, y);
		
		Entity obj2 = new Entity();
		BodyFixture f2 = obj2.addFixture(r);
		f2.setDensity(5.0);
		obj2.setMass();
		obj2.translate(x, y);
		
		this.world.addBody(obj1);
		this.world.addBody(obj2);
		
		// compute the joint points
		Vector2 bodyAnchor1 = new Vector2(-x, y + h);
		Vector2 bodyAnchor2 = new Vector2(x, y + h);
		Vector2 pulleyAnchor1 = new Vector2(-x, y + h + l);
		Vector2 pulleyAnchor2 = new Vector2(x, y + h + l);
		
		// create the joint
		PulleyJoint pulleyJoint = new PulleyJoint(obj1, obj2, pulleyAnchor1, pulleyAnchor2, bodyAnchor1, bodyAnchor2);
		// emulate a block-and-tackle
		pulleyJoint.setRatio(2.0);
		// allow them to collide
		pulleyJoint.setCollisionAllowed(true);
		
		// defaults to collision not allowed
		this.world.addJoint(pulleyJoint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the camera offset
		this.offset.set(0.0, 2.0);
	}
}
