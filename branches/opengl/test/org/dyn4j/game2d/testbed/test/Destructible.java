/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.BodyFixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.contact.ContactListener;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.joint.WeldJoint;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.geometry.Wound;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the destruction of a joint and a body.
 * @author William Bittle
 * @version 2.2.2
 * @since 1.0.3
 */
public class Destructible extends Test {
	/** The floor body */
	private Entity floor;
	
	/** The top body of the joined bodies */
	private Entity top;
	
	/** The bottom body of the joined bodies */
	private Entity bot;
	
	/** The test body */
	private Entity test;
	
	/** The joint joining the two bodies */
	private WeldJoint joint;
	
	/** The destrution class */
	private Destructor destructor;
	
	/**
	 * Extends the contact counter to implement the destruction
	 * of a joint when a contact is encountered.  Normally you would just
	 * extend the {@link ContactListener} interface.
	 * @author William Bittle
	 */
	public class Destructor extends ContactCounter {
		/** Used to flag that the joint has been removed */
		private boolean removed = false;
		
		/** Used to flag that the test body has been destroyed */
		private boolean broken = false;
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.testbed.ContactCounter#preSolve(org.dyn4j.game2d.dynamics.contact.ContactPoint)
		 */
		@Override
		public boolean preSolve(ContactPoint point) {
			// call the super method
			super.preSolve(point);
			
			if (!removed) {
				Body b1 = point.getBody1();
				Body b2 = point.getBody2();
				
				// check the bodies involved
				if ((b1 == floor || b1 == top || b1 == bot)
				 && (b2 == floor || b2 == top || b2 == bot)) {
					// remove the joint
					world.remove(joint);
					removed = true;
				}
			}
			
			if (!broken) {
				Body b1 = point.getBody1();
				Body b2 = point.getBody2();
				
				// check the bodies involved
				if ((b1 == floor || b1 == test)
				 && (b2 == floor || b2 == test)) {
					// remove the body from the world
					world.remove(test);
					
					// make the test body into triangles
					
					// make the new bodies the same color
					float[] color = test.getColor();
					
					// get the velocity
					Vector2 v = test.getVelocity().copy();
					// half the velocity to give the effect of a broken body
					v.multiply(0.5);
					
					Convex convex = test.getFixture(0).getShape();
					Transform tx = test.getTransform();
					Vector2 center = convex.getCenter();
					// we assume its a unit circle polygon to make
					// tessellation easy
					Vector2[] vertices = ((Wound) convex).getVertices();
					int size = vertices.length;
					for (int i = 0; i < size; i++) {
						// get the first and second vertices
						Vector2 p1 = vertices[i];
						Vector2 p2 = vertices[i + 1 == size ? 0 : i + 1];
						// create a body for the triangle
						Entity b = new Entity(color[0], color[1], color[2], color[3]);
						b.addFixture(Geometry.createTriangle(p1, p2, center));
						b.setMass();
						// copy over the transform
						b.translate(tx.getTranslation());
						b.rotate(tx.getRotation());
						// copy over the velocity
						b.setVelocity(v.copy());						
						// add the new body to the world
						world.add(b);
					}
					
					broken = true;
				}
			}
			
			// allow the contact
			return true;
		}
		
		/**
		 * Resets the booleans.
		 */
		public void reset() {
			this.removed = false;
			this.broken = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Destructible";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the destruction of a joint and a body.";
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
		
		// create the world
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(16.0, 15.0));
		this.world = new World(bounds);
		
		// setup the contact counter
		this.destructor = new Destructor();
		this.world.setContactListener(this.destructor);
		this.world.setStepListener(this.destructor);
		
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
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.add(floor);
		
		// create a reusable rectangle
		Rectangle r = new Rectangle(0.5, 1.0);
		
		Entity top = new Entity();
		top.addFixture(new BodyFixture(r));
		top.setMass();
		top.translate(0.0, -1.5);
		top.getVelocity().set(2.0, 0.0);
		
		Entity bot = new Entity();
		bot.addFixture(new BodyFixture(r));
		bot.setMass();
		bot.translate(0.0, -0.5);
		
		this.world.add(top);
		this.world.add(bot);
		
		WeldJoint joint = new WeldJoint(top, bot, new Vector2(0.0, -1.0));
		
		this.world.add(joint);
		
		Entity test = new Entity();
		test.addFixture(Geometry.createUnitCirclePolygon(20, 1.0));
		test.setMass();
		test.translate(-2.5, 0.0);
		this.world.add(test);
		
		// set the class variables to help
		// with referencing
		this.floor = floor;
		this.top = top;
		this.bot = bot;
		this.joint = joint;
		this.test = test;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#reset()
	 */
	@Override
	public void reset() {
		// call the super method
		super.reset();
		// reset the destructor flags
		this.destructor.reset();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, 2.0);
	}
}
