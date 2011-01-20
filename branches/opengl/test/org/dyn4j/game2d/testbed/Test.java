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
package org.dyn4j.game2d.testbed;

import java.awt.Dimension;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.contact.SolvedContactPoint;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.LineJoint;
import org.dyn4j.game2d.dynamics.joint.MouseJoint;
import org.dyn4j.game2d.dynamics.joint.PrismaticJoint;
import org.dyn4j.game2d.dynamics.joint.PulleyJoint;
import org.dyn4j.game2d.dynamics.joint.RevoluteJoint;
import org.dyn4j.game2d.dynamics.joint.RopeJoint;
import org.dyn4j.game2d.dynamics.joint.WeldJoint;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a test.
 * <p>
 * Using the {@link TestBed} class one can switch test without stopping
 * and starting the driver again.
 * @author William Bittle
 * @version 2.2.2
 * @since 1.0.0
 */
public abstract class Test implements Comparable<Test> {
	/** The test key */
	protected String key;
	
	/** A scaling factor from world space to device/screen space */
	protected double scale;
	
	/** The view port offset (x, y) */
	protected Vector2 offset;
	
	/** The physics world */
	protected World world;
	
	/** The current display area */
	protected Dimension size;

	/**
	 * Returns the test name.
	 * @return String the test name
	 */
	public abstract String getName();
	
	/**
	 * Returns the description of the test
	 * @return String the description of the test
	 */
	public abstract String getDescription();
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Test o) {
		// sort by name
		return this.getName().compareTo(o.getName());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getName();
	}
	
	/**
	 * Initializes the {@link Test}.
	 */
	public void initialize() {
		// initialize the scale
		this.scale = 0.0;
		// initialize the offset
		this.offset = new Vector2();
		// initialize the display size
		this.size = new Dimension();
	}
	
	/**
	 * Sets up the test.
	 * <p>
	 * This method is called by {@link #initialize()} and {@link #reset()}.
	 */
	protected abstract void setup();
	
	/**
	 * Resets the test.
	 */
	public void reset() {
		// clear all the bodies
		this.world.clear(false);
		// setup the test
		this.setup();
	}
	
	/**
	 * Resets the camera to focus on the center of the world bounds
	 * at the original zoom.
	 */
	public abstract void home();
	
	/**
	 * Releases the resources used by this test.
	 */
	public void release() {
		this.world = null;
	}
	
	/**
	 * Initializes any inputs specific to the test.
	 * <p>
	 * This method is called each time a test is selected and run
	 * from the TestBed.
	 * @param keyboard the keyboard to add inputs to listen for
	 * @param mouse the mouse to add inputs to listen for
	 */
	public void initializeInput(Keyboard keyboard, Mouse mouse) {}
	
	/**
	 * Returns a string representation of any test specific controls.
	 * @return String the test specific controls
	 */
	public String[][] getControls() { return new String[][] {}; }

	/**
	 * Returns true if this test has specific controls.
	 * @return boolean
	 */
	public boolean hasSpecificControls() {
		return this.getControls().length > 0;
	}
	
	/**
	 * Performs any input polling required.
	 * @param keyboard the keyboard input
	 * @param mouse the mouse input
	 */
	public void poll(Keyboard keyboard, Mouse mouse) {}
	
	/**
	 * Performs the rendering for the {@link Test}.
	 * @param gl the OpenGL graphics context
	 * @param width the width of the rendering area
	 * @param height the height of the rendering area
	 */
	public void render(GL2 gl, double width, double height) {
		// immediately update the display size
		this.size.setSize(width, height);
		// get the draw flags singleton instance
		Draw draw = Draw.getInstance();
		
		// go from screen to world coordinates
		gl.glPushMatrix();
		gl.glScaled(this.scale, this.scale, this.scale);
		gl.glTranslated(this.offset.x, this.offset.y, 0.0);
		
		// draw any test specific stuff
		this.renderBefore(gl);
		
		// render all the bodies
		int size = this.world.getBodyCount();
		for (int i = 0; i < size; i++) {
			Entity obj = (Entity) this.world.getBody(i);
			obj.render(gl);
		}
		
		// see if the user wanted any contact information drawn
		if (draw.drawContacts() || draw.drawContactImpulses() 
		 || draw.drawFrictionImpulses() || draw.drawContactPairs()) {
			// get the contact counter
			ContactCounter cc = (ContactCounter) this.world.getContactListener();
			// get the contacts from the counter
			List<ContactPoint> contacts = cc.getContacts();
			
			// loop over the contacts
			int cSize = contacts.size();
			for (int i = 0; i < cSize; i++) {
				// draw the contacts
				ContactPoint cp = contacts.get(i);
				// get the world space contact point
				Vector2 c = cp.getPoint();
				
				// draw the contact pairs
				if (draw.drawContactPairs()) {
					// set the color
					float[] color = draw.getContactPairsColor();
					gl.glColor4fv(color, 0);
					// get the world space points
					Vector2 p1 = cp.getBody1().getTransform().getTransformed(cp.getFixture1().getShape().getCenter());
					Vector2 p2 = cp.getBody2().getTransform().getTransformed(cp.getFixture2().getShape().getCenter());
					// draw line between the shapes
					gl.glBegin(GL.GL_LINES);
						gl.glVertex2d(p1.x, p1.y);
						gl.glVertex2d(p2.x, p2.y);
					gl.glEnd();
				}
				
				// draw the contact
				if (draw.drawContacts()) {
					// set the color
					float[] color = draw.getContactColor();
					gl.glColor4fv(color, 0);
					// draw the contact points
					GLHelper.fillRectangle(gl, c.x, c.y, 0.02, 0.02);
				}
				
				// check if the contact is a solved contact
				if (cp instanceof SolvedContactPoint) {
					// get the solved contact point to show the impulses applied
					SolvedContactPoint scp = (SolvedContactPoint) cp;
					Vector2 n = scp.getNormal();
					Vector2 t = n.cross(1.0);
					double j = scp.getNormalImpulse();
					double jt = scp.getTangentialImpulse();
					
					// draw the contact forces
					if (draw.drawContactImpulses()) {
						// set the color
						float[] color = draw.getContactImpulsesColor(); 
						gl.glColor4fv(color, 0);
						gl.glBegin(GL.GL_LINES);
							gl.glVertex2d(c.x, c.y);
							gl.glVertex2d(c.x + n.x * j, c.y + n.y * j);
						gl.glEnd();
					}
					
					// draw the friction forces
					if (draw.drawFrictionImpulses()) {
						// set the color
						float[] color = draw.getFrictionImpulsesColor();
						gl.glColor4fv(color, 0);
						gl.glBegin(GL.GL_LINES);
							gl.glVertex2d(c.x, c.y);
							gl.glVertex2d(c.x + t.x * jt, c.y + t.y * jt);
						gl.glEnd();
					}
				}
			}
		}
		
		// see if we should draw joints or not
		if (draw.drawJoints()) {
			size = this.world.getJointCount();
			// draw the joints
			for (int j = 0; j < size; j++) {
				Joint joint = this.world.getJoint(j);
				// check the joint type
				if (joint instanceof DistanceJoint) {
					this.render(gl, (DistanceJoint) joint);
				} else if (joint instanceof RevoluteJoint) {
					this.render(gl, (RevoluteJoint) joint);
				} else if (joint instanceof MouseJoint) {
					this.render(gl, (MouseJoint) joint);
				} else if (joint instanceof WeldJoint) {
					this.render(gl, (WeldJoint) joint);
				} else if (joint instanceof LineJoint) {
					this.render(gl, (LineJoint) joint);
				} else if (joint instanceof PrismaticJoint) {
					this.render(gl, (PrismaticJoint) joint);
				} else if (joint instanceof PulleyJoint) {
					this.render(gl, (PulleyJoint) joint);
				} else if (joint instanceof RopeJoint) {
					this.render(gl, (RopeJoint) joint);
				}
			}
		}
		
		// draw the bounds
		if (draw.drawBounds()) {
			// set the color
			float[] color = draw.getBoundsColor();
			gl.glColor4fv(color, 0);
			// get the bounds object
			Bounds bounds = this.world.getBounds();
			// check the type
			if (bounds instanceof RectangularBounds) {
				// cast to get access to the fields
				RectangularBounds rb = (RectangularBounds) bounds;
				
				// get the bounding rectangle
				Rectangle r = rb.getBounds();
				// get the transform
				Transform t = rb.getTransform();
				// get the rectangle's vertices
				Vector2[] vertices = r.getVertices();
				
				// save the current model-view matrix
				gl.glPushMatrix();
				// transform the model-view matrix
				gl.glTranslated(t.getTranslationX(), t.getTranslationY(), 0.0);
				gl.glRotated(Math.toDegrees(t.getRotation()), 0.0, 0.0, 1.0);
				
				// draw the box
				gl.glBegin(GL.GL_LINE_LOOP);
				// declare a vector for use
				Vector2 v;
				for (int i = 0; i < 4; i++) {
					// get the point
					v = vertices[i];
					// add the vertex
					gl.glVertex2d(v.x, v.y);
				}
				gl.glEnd();
				
				// throw away the current model-view matrix
				gl.glPopMatrix();
			}
		}
		
		// finally draw any test specific stuff
		this.renderAfter(gl);
		
		// restore the old model-view matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders a {@link DistanceJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link DistanceJoint} to render
	 */
	private void render(GL2 gl, DistanceJoint joint) {
		// get the anchor points
		Vector2 v1 = joint.getAnchor1();
		Vector2 v2 = joint.getAnchor2();
		// set the color to be mostly transparent
		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.25f);
		// check for spring distance joint
		if (joint.isSpring()) {
			// draw a spring
			final double h = 0.03;
			final double w = 0.25;
			// compute the offset from the first joint point to the start
			// of the spring loops
			double offset = h * 0.5;
			// compute the number of spring loops
			// we have to use the joint's desired distance here so that the
			// number of loops in the spring doesnt change as the simulation
			// progresses
			int loops = (int) Math.ceil((joint.getDistance() - offset * 2.0) / h);
			// get the vector between the two points
			Vector2 n = v1.to(v2);
			// normalize it to get the current distance
			double x = n.normalize();
			// get the tangent to the normal
			Vector2 t = n.getRightHandOrthogonalVector();
			// compute the distance between each loop along the normal
			double d = (x - offset * 2.0) / (loops - 1);
			// draw a line straight down using the offset
			Vector2 d1 = n.product(offset).add(v1);
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2d(v1.x, v1.y);
				gl.glVertex2d(d1.x, d1.y);
				// draw the first loop (half loop)
				Vector2 ct = t.product(w * 0.5);
				Vector2 cn = n.product(d * 0.5);
				Vector2 first = ct.sum(cn).add(d1);
				gl.glVertex2d(d1.x, d1.y);
				gl.glVertex2d(first.x, first.y);
				// draw the middle loops
				Vector2 prev = first;
				for (int i = 1; i < loops - 1; i++) {
					ct = t.product(w * 0.5 * ((i + 1) % 2 == 1 ? 1.0 : -1.0));
					cn = n.product(d * (i + 0.5) + offset);
					Vector2 p2 = ct.sum(cn).add(v1);
					// draw the line
					gl.glVertex2d(prev.x, prev.y);
					gl.glVertex2d(p2.x, p2.y);
					prev = p2;
				}
				// draw the final loop (half loop)
				Vector2 d2 = n.product(-offset).add(v2);
				gl.glVertex2d(prev.x, prev.y);
				gl.glVertex2d(d2.x, d2.y);
				// draw a line straight down using the offset
				gl.glVertex2d(d2.x, d2.y);
				gl.glVertex2d(v2.x, v2.y);
			gl.glEnd();
		} else {
			// emulate a line stroke of arbitrary width without cap/join
			
			// get the tangent vector
			Vector2 t = v1.to(v2);
			t.normalize();
			t.left();
			t.multiply(0.05);
			
			gl.glBegin(GL2.GL_QUADS);
				gl.glVertex2d(v1.x - t.x, v1.y - t.y);
				gl.glVertex2d(v1.x + t.x, v1.y + t.y);
				gl.glVertex2d(v2.x + t.x, v2.y + t.y);
				gl.glVertex2d(v2.x - t.x, v2.y - t.y);
			gl.glEnd();
		}
	}
	
	/**
	 * Renders a {@link RevoluteJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link RevoluteJoint} to render
	 */
	private void render(GL2 gl, RevoluteJoint joint) {
		Vector2 anchor = joint.getAnchor1();
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
		GLHelper.fillCircle(gl, anchor.x, anchor.y, 0.025, 10);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		GLHelper.renderCircle(gl, anchor.x, anchor.y, 0.025, 10);
	}
	
	/**
	 * Renders a {@link MouseJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link MouseJoint} to render
	 */
	private void render(GL2 gl, MouseJoint joint) {
		// set the color
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
		// draw the anchor point
		Vector2 anchor = joint.getAnchor2();
		GLHelper.fillRectangle(gl, anchor.x, anchor.y, 0.05, 0.05);
		// draw the target point
		Vector2 target = joint.getTarget();
		GLHelper.fillRectangle(gl, target.x, target.y, 0.05, 0.05);
		// draw a line connecting them
		// make the line color a function of stress (black to red)
		Step step = this.world.getStep();
		double invdt = step.getInverseDeltaTime();
		double maxForce = joint.getMaxForce();
		double force = joint.getReactionForce(invdt).getMagnitude();
		double red = force / maxForce;
		red *= 1.10;
		red = Interval.clamp(red, 0.0, 1.0);
		// set the color
		gl.glColor4f((float)red, 0.0f, 0.0f, 0.5f);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(anchor.x, anchor.y);
			gl.glVertex2d(target.x, target.y);
		gl.glEnd();
	}
	
	/**
	 * Renders a {@link WeldJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link WeldJoint} to render
	 */
	private void render(GL2 gl, WeldJoint joint) {
		// set the color
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		// draw an x at the anchor point
		Vector2 anchor = joint.getAnchor1();
		final double d = 0.025;
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(anchor.x - d, anchor.y - d);
			gl.glVertex2d(anchor.x + d, anchor.y + d);
			gl.glVertex2d(anchor.x - d, anchor.y + d);
			gl.glVertex2d(anchor.x + d, anchor.y - d);
		gl.glEnd();
	}
	
	/**
	 * Renders a {@link LineJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link LineJoint} to render
	 */
	private void render(GL2 gl, LineJoint joint) {
		// draw an x at the anchor point
		Vector2 anchor = joint.getAnchor1();
		// draw a circle at the rotation anchor point
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
		GLHelper.fillCircle(gl, anchor.x, anchor.y, 0.025, 10);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		GLHelper.renderCircle(gl, anchor.x, anchor.y, 0.025, 10);
		// draw a line to each center
		Body b1 = joint.getBody1();
		Body b2 = joint.getBody2();
		Vector2 c1 = b1.getWorldCenter();
		Vector2 c2 = b2.getWorldCenter();
		// draw a line from the anchor to each center
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(anchor.x, anchor.y);
			gl.glVertex2d(c1.x, c1.y);
			gl.glVertex2d(anchor.x, anchor.y);
			gl.glVertex2d(c2.x, c2.y);
		gl.glEnd();
	}

	/**
	 * Renders a {@link PrismaticJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link PrismaticJoint} to render
	 */
	private void render(GL2 gl, PrismaticJoint joint) {
		// the length scale factor
		final double lf = 0.75;
		// the "piston" width
		final double w = 0.10;
		
		double hw = w * 0.5;
		Body b1 = joint.getBody1();
		Body b2 = joint.getBody2();
		Vector2 c1 = b1.getWorldCenter();
		Vector2 c2 = b2.getWorldCenter();
		Vector2 n = c1.to(c2);
		double l = n.normalize();
		
		// emulate a line stroke of arbitrary width without cap/join
		// get the tangent vector
		Vector2 t = n.product(w * 0.25).left();
		
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.2f);
		// draw the inner piston
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(c1.x - t.x, c1.y - t.y);
			gl.glVertex2d(c1.x + t.x, c1.y + t.y);
			gl.glVertex2d(c2.x + t.x, c2.y + t.y);
			gl.glVertex2d(c2.x - t.x, c2.y - t.y);
		gl.glEnd();
		
		// draw a line from body1's center to the anchor
		gl.glBegin(GL.GL_LINES);
			// draw two lines slightly offset from the center line
			t = n.cross(1.0);
			gl.glVertex2d(c2.x + t.x * hw, c2.y + t.y * hw);
			gl.glVertex2d(c2.x - n.x * l * lf + t.x * hw, c2.y - n.y * l * lf + t.y * hw);
			gl.glVertex2d(c2.x - t.x * hw, c2.y - t.y * hw);
			gl.glVertex2d(c2.x - n.x * l * lf - t.x * hw, c2.y - n.y * l * lf - t.y * hw);
		gl.glEnd();
	}
	
	/**
	 * Renders a {@link PulleyJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link PulleyJoint} to render
	 * @since 2.2.0
	 */
	private void render(GL2 gl, PulleyJoint joint) {
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.2f);
		
		Vector2 p1 = joint.getAnchor1();
		Vector2 p2 = joint.getPulleyAnchor1();
		Vector2 p3 = joint.getPulleyAnchor2();
		Vector2 p4 = joint.getAnchor2();
		
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2d(p1.x, p1.y);
			gl.glVertex2d(p2.x, p2.y);
			gl.glVertex2d(p3.x, p3.y);
			gl.glVertex2d(p4.x, p4.y);
		gl.glEnd();
	}
	
	/**
	 * Renders a {@link RopeJoint} to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the {@link RopeJoint} to render
	 */
	private void render(GL2 gl, RopeJoint joint) {
		Vector2 v1 = joint.getAnchor1();
		Vector2 v2 = joint.getAnchor2();
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.2f);
		
		// emulate a line stroke of arbitrary width without cap/join
		// get the tangent vector
		Vector2 t = v1.to(v2);
		t.normalize();
		t.left();
		t.multiply(0.05);
		
		// save the original stroke
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(v1.x - t.x, v1.y - t.y);
			gl.glVertex2d(v1.x + t.x, v1.y + t.y);
			gl.glVertex2d(v2.x + t.x, v2.y + t.y);
			gl.glVertex2d(v2.x - t.x, v2.y - t.y);
		gl.glEnd();
	}
	
	/**
	 * Performs rendering for a subclass of {@link Test} before the world,
	 * contacts, and bounds are drawn.
	 * <p>
	 * Any graphics rendered in this method does not need to apply
	 * a transformation for screen to world coordinates.
	 * @param gl the OpenGL graphics context
	 */
	protected void renderBefore(GL2 gl) {};
	
	/**
	 * Performs rendering for a subclass of {@link Test} after the world,
	 * contacts, and bounds are drawn.
	 * <p>
	 * Any graphics rendered in this method does not need to apply
	 * a transformation for screen to world coordinates.
	 * @param gl the OpenGL graphics context
	 */
	protected void renderAfter(GL2 gl) {};
	
	/**
	 * Updates the {@link Test} given the delta time
	 * in seconds.
	 * <p>
	 * This method assumes the step mode is continuous and the TestBed is not paused.
	 * @param dt the delta time in seconds
	 */
	public void update(double dt) {}
	
	/**
	 * Updates the {@link Test} given the number of steps to perform.
	 * @param steps the number of steps to perform
	 */
	public void update(int steps) {}
	
	/**
	 * Converts the screen coordinate to world space.
	 * @param x screen x
	 * @param y screen y
	 * @return {@link Vector2}
	 */
	public Vector2 screenToWorld(double x, double y) {
		Vector2 v = new Vector2();
		v.x = (x - this.size.width / 2.0) / this.scale - this.offset.x;
		v.y = -((y - this.size.height / 2.0) / this.scale + this.offset.y);
		return v;
	}
	
	/**
	 * Zooms the camera in by the given zoom factor.
	 * @param zoom the zoom factor
	 */
	public void zoom(double zoom) {
		this.scale *= zoom;
	}
	
	/**
	 * Translates the camera by the given values.
	 * @param dx the delta x translation
	 * @param dy the delta y translation
	 */
	public void translate(double dx, double dy) {
		this.offset.add(dx, dy);
	}

	/**
	 * Returns the current zoom amount.
	 * @return double the current zoom amount
	 */
	public double getZoom() {
		return this.scale;
	}
	
	/**
	 * Returns the world object.
	 * @return {@link World} the world object
	 */
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * Returns the current display size.
	 * @return Dimension
	 */
	public Dimension getSize() {
		return this.size;
	}
}
