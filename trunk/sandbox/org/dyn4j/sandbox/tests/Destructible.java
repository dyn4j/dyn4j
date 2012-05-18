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
package org.dyn4j.sandbox.tests;

import java.util.Arrays;

import javax.media.opengl.GL2;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.contact.ContactAdapter;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;
import org.dyn4j.sandbox.SandboxBody;

/**
 * Compiled test for testing destruction of bodies.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.2
 */
public class Destructible extends CompiledSimulation {
	/**
	 * Extends the contact counter to implement the destruction
	 * of a joint when a contact is encountered.  Normally you would just
	 * extend the {@link ContactListener} interface.
	 * @author William Bittle
	 * @version 1.0.2
	 * @since 1.0.2
	 */
	public class Destructor extends ContactAdapter {
		/** Used to flag that the joint has been removed */
		private boolean removed = false;
		
		/** Used to flag that the test body has been destroyed */
		private boolean broken = false;
		
		/* (non-Javadoc)
		 * @see org.dyn4j.testbed.ContactCounter#begin(org.dyn4j.dynamics.contact.ContactPoint)
		 */
		@Override
		public boolean begin(ContactPoint point) {
			// call the super method
			super.begin(point);
			// when a contact is added
			if (!this.removed) {
				Body jb1 = joint.getBody1();
				Body jb2 = joint.getBody2();
				Body b1 = point.getBody1();
				Body b2 = point.getBody2();
				
				// check the bodies involved
				if (b2 == jb1 || b2 == jb2 || b1 == jb1 || b1 == jb2) {
					// remove the joint
					world.remove(joint);
					
					this.removed = true;

					// make sure the sanbox updates the jtree
					changed = true;
				}
			}
			
			if (!this.broken) {
				Body b1 = point.getBody1();
				Body b2 = point.getBody2();
				
				// check the bodies involved
				if (b2 == icosigon || b1 == icosigon) {
					// remove the body from the world
					world.remove(icosigon);
					
					// make the test body into triangles
					
					// make the new bodies the same color
					float[] fcolor = icosigon.getFillColor();
					float[] ocolor = icosigon.getOutlineColor();
					
					// get the velocity
					Vector2 v = icosigon.getVelocity().copy();
					// half the velocity to give the effect of a broken body
					v.multiply(0.5);
					
					Convex convex = icosigon.getFixture(0).getShape();
					Transform tx = icosigon.getTransform();
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
						SandboxBody b = new SandboxBody();
						b.setFillColor(Arrays.copyOf(fcolor, fcolor.length));
						b.setOutlineColor(Arrays.copyOf(ocolor, ocolor.length));
						b.addFixture(Geometry.createTriangle(p1, p2, center));
						b.setMass();
						// copy over the transform
						b.setTransform(tx.copy());
						// copy over the velocity
						b.setVelocity(v.copy());
						b.setUserData("Piece" + (i + 1));
						// add the new body to the world
						world.add(b);
					}
					
					this.broken = true;
					
					// set the requires update flag
					world.setUpdateRequired(true);
					
					// make sure the sanbox updates the jtree
					changed = true;
					
					// don't allow the contact
					return false;
				}
			}
			
			// allow the contact
			return true;
		}
	}
	
	/** The floor body */
	private SandboxBody floor;
	
	/** The 20 sided body */
	private SandboxBody icosigon;
	
	/** The weld joint */
	private WeldJoint joint;

	/** The destrution class */
	private Destructor destructor;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#initialize()
	 */
	@Override
	public void initialize() {
		this.world.setUserData("Destructible");
		
		// create the floor
		this.floor = new SandboxBody();
		this.floor.addFixture(Geometry.createRectangle(15.0, 1.0));
		this.floor.setMass(Mass.Type.INFINITE);
		this.floor.setUserData("Floor");
		
		// create the weld joint bodies
		SandboxBody top = new SandboxBody();
		top.addFixture(Geometry.createRectangle(0.5, 1.0));
		top.setMass();
		top.translate(0.0, 3.0);
		top.getVelocity().set(2.0, 0.0);
		top.setUserData("Top");
		
		SandboxBody bot = new SandboxBody();
		bot.addFixture(Geometry.createRectangle(0.5, 1.0));
		bot.setMass();
		bot.translate(0.0, 2.0);
		bot.setUserData("Bottom");
		
		this.joint = new WeldJoint(top, bot, new Vector2(0.0, 2.5));
		this.joint.setUserData("WeldJoint1");
		
		this.icosigon = new SandboxBody();
		this.icosigon.addFixture(Geometry.createUnitCirclePolygon(20, 1.0));
		this.icosigon.setMass();
		this.icosigon.translate(-2.5, 2.0);
		this.icosigon.setUserData("Icosigon");
		
		this.destructor = new Destructor();
		
		this.world.add(this.floor);
		this.world.add(this.icosigon);
		this.world.add(top);
		this.world.add(bot);
		this.world.add(this.joint);
		this.world.addListener(this.destructor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#update(double, boolean)
	 */
	@Override
	public void update(double elapsedTime, boolean stepped) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#render(javax.media.opengl.GL2)
	 */
	@Override
	public void render(GL2 gl) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#reset()
	 */
	@Override
	public void reset() {
		// remove everything from the world
		this.world.removeAll();
		// remove the listener
		this.world.removeListener(this.destructor);
		// add it all back
		this.initialize();
	}
}
