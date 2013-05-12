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

import java.text.DecimalFormat;

import javax.media.opengl.GL2;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Tests the angle joint.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.0
 */
public class AngleRatio extends Test {
	/** The first body */
	private Entity body1;
	
	/** The second body */
	private Entity body2;
	
	/** The angle joint */
	private AngleJoint aj;
	
	/** The number format */
	private DecimalFormat df = new DecimalFormat("0.00");
	
	/** The GLUT library used for text rendering */
	private GLUT glut = new GLUT();
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Angle Ratio";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests an angle joint with a ratio.";
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
		this.world = new World(new AxisAlignedBounds(16.0, 15.0));
		
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
		
		this.body1 = new Entity();
		this.body1.addFixture(Geometry.createCircle(1.0));
		this.body1.setMass(Mass.Type.FIXED_LINEAR_VELOCITY);
		this.body1.translate(-2.0, 0.0);
		
		this.body2 = new Entity();
		this.body2.addFixture(Geometry.createCircle(2.0));
		this.body2.setMass(Mass.Type.FIXED_LINEAR_VELOCITY);
		this.body2.translate(2.0, 0.0);
		this.body2.setAngularVelocity(Math.toRadians(10.0));
		
		this.world.addBody(this.body1);
		this.world.addBody(this.body2);
		
		this.aj = new AngleJoint(this.body1, this.body2);
		this.aj.setRatio(-2.0);
		//this.aj.setLimitsEnabled(Math.toRadians(-30.0), Math.toRadians(30.0));
		this.aj.setLimitEnabled(false);
		
		this.world.addJoint(this.aj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, 2.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#renderAfter(javax.media.opengl.GL2)
	 */
	@Override
	protected void renderAfter(GL2 gl) {
		super.renderAfter(gl);
		
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glPushMatrix();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glLoadIdentity();
		gl.glRasterPos2d(-this.size.width / 2.0 + 5.0, this.size.height / 2.0 - 15.0);
		this.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Ratio = " + df.format(this.aj.getRatio()));
		gl.glRasterPos2d(-this.size.width / 2.0 + 5.0, this.size.height / 2.0 - 30.0);
		this.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Body1.av = " + df.format(this.body1.getAngularVelocity()));
		gl.glRasterPos2d(-this.size.width / 2.0 + 5.0, this.size.height / 2.0 - 45.0);
		this.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Body2.av = " + df.format(this.body2.getAngularVelocity()));
		gl.glRasterPos2d(-this.size.width / 2.0 + 5.0, this.size.height / 2.0 - 60.0);
		this.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Angle = " + df.format(Math.toDegrees(this.aj.getJointAngle())));
		gl.glPopMatrix();
	}
}
