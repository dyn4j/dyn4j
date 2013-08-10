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
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeOfImpactAdapter;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

/**
 * Tests the handling of bodies that rotate very fast.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.2.0
 */
public class FastRotation extends Test {
	/**
	 * Test time of impact listener.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	private class ToiListener extends TimeOfImpactAdapter {
		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.TimeOfImpactAdapter#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.collision.continuous.TimeOfImpact)
		 */
		@Override
		public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, TimeOfImpact toi) {
			body1.getVelocity().zero();
			body1.setAngularVelocity(0);
			
			body2.getVelocity().zero();
			body2.setAngularVelocity(0);
			
			return true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Fast Rotations";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the handling of bodies that rotate very fast.";
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
		this.world = new World(new AxisAlignedBounds(50.0, 50.0));
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.addListener(cc);
		this.world.addListener(new ToiListener());
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#setup()
	 */
	@Override
	protected void setup() {
		Entity e1 = new Entity();
		e1.addFixture(Geometry.createRectangle(2.0, 0.2));
		// don't allow it to translate but rotation is desired
		e1.setMass(Mass.Type.FIXED_LINEAR_VELOCITY);
		e1.translate(0.5, 0.0);
		e1.rotateAboutCenter(Math.toRadians(-40));
		e1.setAngularVelocity(Math.toRadians(60.0 * 80.0));
		e1.setBullet(true);
		
		Entity e2 = new Entity();
		e2.addFixture(Geometry.createRectangle(10.0, 0.5));
		// don't allow it to translate but rotation is desired
		e2.setMass(Mass.Type.FIXED_LINEAR_VELOCITY);
		e2.translate(-5.0, 0.0);
		e2.rotateAboutCenter(Math.toRadians(-50));
		e2.setAngularVelocity(Math.toRadians(60.0 * 60.0));
		
		this.world.addBody(e1);
		this.world.addBody(e2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 16.0;
		// set the camera offset
		this.offset.set(0.0, -5.0);
	}
}
