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
package org.dyn4j.game2d.testbed;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.contact.SolvedContactPoint;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.MouseJoint;
import org.dyn4j.game2d.dynamics.joint.RevoluteJoint;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.geometry.Wound;

/**
 * Represents a test.
 * <p>
 * Using the {@link TestBed} class one can switch test without stopping
 * and starting the driver again.
 * @author William Bittle
 */
public abstract class Test {
	/** The test name */
	protected String name;
	
	/** A scaling factor from world space to device/screen space */
	protected double scale;
	
	/** The view port offset (x, y) */
	protected Vector offset;
	
	/** The bounds object */
	protected Rectangle bounds;
	
	/** The physics world */
	protected World world;
	
	/** The current display area */
	protected Dimension size;

	/**
	 * Returns the test name.
	 * @return String the test name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the description of the test
	 * @return String the description of the test
	 */
	public abstract String getDescription();
	
	/**
	 * Initializes the {@link Test}.
	 */
	public void initialize() {
		// initialize the scale
		this.scale = 0.0;
		// initialize the offset
		this.offset = new Vector();
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
		this.world.clear();
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
		this.bounds = null;
		this.world = null;
	}
	
	/**
	 * Initializes any inputs specific to the test.
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
	 * Performs any input polling required.
	 * @param keyboard the keyboard input
	 * @param mouse the mouse input
	 */
	public void poll(Keyboard keyboard, Mouse mouse) {}
	
	/**
	 * Performs the rendering for the {@link Test}.
	 * @param g the graphics object to render to
	 * @param width the width of the rendering area
	 * @param height the height of the rendering area
	 */
	public void render(Graphics2D g, double width, double height) {
		// immediately update the display size
		this.size.setSize(width, height);
		// get the draw flags singleton instance
		Draw draw = Draw.getInstance();
		// create the world to screen transform
		// flip the y-axis
		AffineTransform tx = AffineTransform.getScaleInstance(1.0, -1.0);
		// set 0, 0 in the middle of the screen
		tx.translate( this.size.width / 2.0 + this.offset.x * this.scale,
                     -this.size.height / 2.0 + this.offset.y * this.scale);
		// store the old transform
		AffineTransform af = g.getTransform();
		// transform the subsequent graphics
		g.transform(tx);
		
		// finally draw any test specific stuff
		this.renderBefore(g);
		
		// render all the bodies
		int size = this.world.getBodyCount();
		for (int i = 0; i < size; i++) {
			Entity obj = (Entity) this.world.getBody(i);
			obj.render(g, this.scale);
		}

		// check if we should render normals
		if (draw.drawNormals()) {
			// set the color
			g.setColor(Color.RED);
			// draw all the normals for every convex on each shape
			for (int i = 0; i < size; i++) {
				Body b = this.world.getBody(i);
				Transform t = b.getTransform();
				int cSize = b.getShapeCount();
				// loop over the convex shapes again
				for (int j = 0; j < cSize; j++) {
					Convex c = b.getShape(j);
					// render the normals
					this.renderNormals(g, c, t, this.scale);
				}
			}
		}

		if (draw.drawVelocityVectors()) {
			// set the color
			g.setColor(Color.MAGENTA);
			// draw all the normals for every convex on each shape
			for (int i = 0; i < size; i++) {
				Body b = this.world.getBody(i);
				Vector center = b.getWorldCenter();
				Vector v = b.getVelocity();
				// draw the velocity vector
				this.renderVector(g, v, center, scale);
			}
		}
		
		// see if the user wanted any contact information drawn
		if (draw.drawContacts() || draw.drawContactForces() 
		 || draw.drawFrictionForces() || draw.drawContactPairs()) {
			// get the contact counter
			ContactCounter cc = (ContactCounter) this.world.getContactListener();
			// get the contacts from the counter
			List<ContactPoint> contacts = cc.getContacts();
			
			// loop over the contacts
			int cSize = contacts.size();
			for (int i = 0; i < cSize; i++) {
				ContactPoint cp = contacts.get(i);
				Vector c = cp.getPoint();
				
				// draw the contact pairs
				if (draw.drawContactPairs()) {
					// get the centers of the convex shapes
					Vector c1 = cp.getBody1().getTransform().getTransformed(cp.getFixture1().getShape().getCenter());
					Vector c2 = cp.getBody2().getTransform().getTransformed(cp.getFixture2().getShape().getCenter());
					// draw a line between them
					g.setColor(Color.YELLOW);
					g.drawLine((int) Math.ceil(c1.x * scale),
							   (int) Math.ceil(c1.y * scale), 
							   (int) Math.ceil(c2.x * scale),
							   (int) Math.ceil(c2.y * scale));
				}
				
				// draw the contact
				if (draw.drawContacts()) {
					g.setColor(Color.ORANGE);
					// draw the contact as a square
					g.fillRect((int) Math.ceil((c.x - 0.025) * scale),
							   (int) Math.ceil((c.y - 0.025) * scale), 
							   (int) Math.ceil(0.05 * scale),
							   (int) Math.ceil(0.05 * scale));
				}
				
				// check if the contact is a solved contact
				if (cp instanceof SolvedContactPoint) {
					g.setColor(Color.BLUE);
					SolvedContactPoint scp = (SolvedContactPoint) cp;
					Vector n = scp.getNormal();
					Vector t = n.cross(1.0);
					double j = scp.getNormalImpulse();
					double jt = scp.getTangentialImpulse();
					
					// draw the contact forces
					if (draw.drawContactForces()) {
						g.drawLine((int) Math.ceil(c.x * scale),
								   (int) Math.ceil(c.y * scale), 
								   (int) Math.ceil((c.x + n.x * j) * scale),
								   (int) Math.ceil((c.y + n.y * j) * scale));
					}
					
					// draw the friction forces
					if (draw.drawFrictionForces()) {
						g.drawLine((int) Math.ceil(c.x * scale),
								   (int) Math.ceil(c.y * scale), 
								   (int) Math.ceil((c.x + t.x * jt) * scale),
								   (int) Math.ceil((c.y + t.y * jt) * scale));
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
					this.render(g, (DistanceJoint) joint);
				} else if (joint instanceof RevoluteJoint) {
					this.render(g, (RevoluteJoint) joint);
				} else if (joint instanceof MouseJoint) {
					this.render(g, (MouseJoint) joint);
				}
			}
		}
		
		// draw the bounds
		if (draw.drawBounds()) {
			// draw the bounds
			g.setColor(Color.CYAN);
			double x = this.bounds.getVertices()[0].x;
			double y = this.bounds.getVertices()[0].y;
			g.drawRect((int) Math.ceil(x * this.scale),
					   (int) Math.ceil(y * this.scale),
					   (int) Math.ceil(this.bounds.getWidth() * this.scale),
					   (int) Math.ceil(this.bounds.getHeight() * this.scale));
		}
		
		// finally draw any test specific stuff
		this.renderAfter(g);
		
		g.setTransform(af);
	}
	
	/**
	 * Renders a {@link DistanceJoint} to the given graphics object.
	 * @param g the graphics object to render to
	 * @param joint the {@link DistanceJoint} to render
	 */
	private void render(Graphics2D g, DistanceJoint joint) {
		Vector v1 = joint.getAnchor1();
		Vector v2 = joint.getAnchor2();
		// set the color to be mostly transparent
		g.setColor(new Color(0, 0, 0, 64));
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
			Vector n = v1.to(v2);
			// normalize it to get the current distance
			double x = n.normalize();
			// get the tangent to the normal
			Vector t = n.getRightHandOrthogonalVector();
			// compute the distance between each loop along the normal
			double d = (x - offset * 2.0) / (loops - 1);
			// draw a line straight down using the offset
			Vector d1 = n.product(offset).add(v1);
			g.drawLine((int) Math.ceil(v1.x * scale),
					   (int) Math.ceil(v1.y * scale), 
					   (int) Math.ceil(d1.x * scale),
					   (int) Math.ceil(d1.y * scale));
			// draw the first loop (half loop)
			Vector ct = t.product(w * 0.5);
			Vector cn = n.product(d * 0.5);
			Vector first = ct.sum(cn).add(d1);
			g.drawLine((int) Math.ceil(d1.x * scale),
					   (int) Math.ceil(d1.y * scale), 
					   (int) Math.ceil(first.x * scale),
					   (int) Math.ceil(first.y * scale));
			// draw the middle loops
			Vector prev = first;
			for (int i = 1; i < loops - 1; i++) {
				ct = t.product(w * 0.5 * ((i + 1) % 2 == 1 ? 1.0 : -1.0));
				cn = n.product(d * (i + 0.5) + offset);
				Vector p2 = ct.sum(cn).add(v1);
				// draw the line
				g.drawLine((int) Math.ceil(prev.x * scale),
						   (int) Math.ceil(prev.y * scale), 
						   (int) Math.ceil(p2.x * scale),
						   (int) Math.ceil(p2.y * scale));
				prev = p2;
			}
			// draw the final loop (half loop)
			Vector d2 = n.product(-offset).add(v2);
			g.drawLine((int) Math.ceil(prev.x * scale),
					   (int) Math.ceil(prev.y * scale), 
					   (int) Math.ceil(d2.x * scale),
					   (int) Math.ceil(d2.y * scale));
			// draw a line straight down using the offset
			g.drawLine((int) Math.ceil(d2.x * scale),
					   (int) Math.ceil(d2.y * scale), 
					   (int) Math.ceil(v2.x * scale),
					   (int) Math.ceil(v2.y * scale));
		} else {
			// save the original stroke
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke((float)(0.1 * scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.drawLine((int) Math.ceil(v1.x * scale),
					   (int) Math.ceil(v1.y * scale), 
					   (int) Math.ceil(v2.x * scale),
					   (int) Math.ceil(v2.y * scale));
			// set back the original stroke
			g.setStroke(stroke);
		}
	}
	
	/**
	 * Renders a {@link RevoluteJoint} to the given graphics object.
	 * @param g the graphics object to render to
	 * @param joint the {@link RevoluteJoint} to render
	 */
	private void render(Graphics2D g, RevoluteJoint joint) {
		Vector anchor = joint.getAnchor1();
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval((int) Math.ceil((anchor.x - 0.025) * scale),
				   (int) Math.ceil((anchor.y - 0.025) * scale), 
				   (int) Math.ceil(0.05 * scale),
				   (int) Math.ceil(0.05 * scale));
		g.setColor(Color.DARK_GRAY);
		g.drawOval((int) Math.ceil((anchor.x - 0.025) * scale),
				   (int) Math.ceil((anchor.y - 0.025) * scale), 
				   (int) Math.ceil(0.05 * scale),
				   (int) Math.ceil(0.05 * scale));
	}
	
	/**
	 * Renders a {@link MouseJoint} to the given graphics object.
	 * @param g the graphics object to render to
	 * @param joint the {@link MouseJoint} to render
	 */
	private void render(Graphics2D g, MouseJoint joint) {
		g.setColor(Color.BLACK);
		// draw the anchor point
		Vector anchor = joint.getAnchor2();
		g.fillRect((int) Math.ceil((anchor.x - 0.025) * scale),
				   (int) Math.ceil((anchor.y - 0.025) * scale), 
				   (int) Math.ceil(0.05 * scale),
				   (int) Math.ceil(0.05 * scale));
		// draw the target point
		Vector target = joint.getTarget();
		g.fillRect((int) Math.ceil((target.x - 0.025) * scale),
				   (int) Math.ceil((target.y - 0.025) * scale), 
				   (int) Math.ceil(0.05 * scale),
				   (int) Math.ceil(0.05 * scale));
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
		g.setColor(new Color((float)red, 0.0f, 0.0f));		
		g.drawLine((int) Math.ceil(anchor.x * scale),
				   (int) Math.ceil(anchor.y * scale), 
				   (int) Math.ceil(target.x * scale),
				   (int) Math.ceil(target.y * scale));
	}
	
	/**
	 * Performs rendering for a subclass of {@link Test} before the world,
	 * contacts, and bounds are drawn.
	 * <p>
	 * Any graphics rendered in this method do not need to apply
	 * a transformation for screen to world coordinates.
	 * @param g the graphics to draw to
	 */
	protected void renderBefore(Graphics2D g) {};
	
	/**
	 * Performs rendering for a subclass of {@link Test} after the world,
	 * contacts, and bounds are drawn.
	 * <p>
	 * Any graphics rendered in this method do not need to apply
	 * a transformation for screen to world coordinates.
	 * @param g the graphics to draw to
	 */
	protected void renderAfter(Graphics2D g) {};
	
	/**
	 * Updates the {@link Test} given the delta time
	 * in seconds.
	 * @param paused whether the simulation is paused
	 * @param step whether the simulation is in step mode or continuous mode
	 * @param dt the delta time in seconds
	 */
	public void update(boolean paused, boolean step, double dt) {
		if (!paused && !step) {
			// update the world
			this.world.update(dt);
		}
	}
	
	/**
	 * Converts the screen coordinate to world space.
	 * @param x screen x
	 * @param y screen y
	 * @return {@link Vector}
	 */
	public Vector screenToWorld(double x, double y) {
		Vector v = new Vector();
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

	/**
	 * Renders the edge normals of the given {@link Convex} {@link Shape} if the {@link Shape}
	 * is a {@link Wound} {@link Shape}.
	 * @param g the graphics object to render to
	 * @param c the convex object to render on top of
	 * @param t the transform of the convex object
	 * @param s the world to screen space scale factor
	 */
	private void renderNormals(Graphics2D g, Convex c, Transform t, double s) {
		// set the normal length
		final double l = 0.1;
		// make sure the shape is of type wound
		if (c instanceof Wound) {
			Wound shape = (Wound) c;
			Vector[] vertices = shape.getVertices();
			Vector[] normals = shape.getNormals();
			int size = normals.length;
			// render all the normals
			for (int i = 0; i < size; i++) {
				Vector p1 = t.getTransformed(vertices[i]);
				Vector p2 = t.getTransformed(vertices[(i + 1 == size) ? 0 : i + 1]);
				Vector n = t.getTransformedR(normals[i]);
				// find the mid point between p1 and p2
				Vector mid = p1.to(p2).multiply(0.5).add(p1);
				// draw a line from the mid point along n
				g.drawLine(
						(int) Math.ceil(mid.x * s),
						(int) Math.ceil(mid.y * s),
						(int) Math.ceil((mid.x + n.x * l) * s),
						(int) Math.ceil((mid.y + n.y * l) * s));
			}
		}
	}

	/**
	 * Renders a vector from a start position.
	 * @param g the graphics object to render to
	 * @param v the vector to render
	 * @param s the start position to render the vector from
	 * @param scale the scaling factor from world space to screen space
	 */
	private void renderVector(Graphics2D g, Vector v, Vector s, double scale) {
		g.drawLine(
				(int) Math.ceil(s.x * scale),
				(int) Math.ceil(s.y * scale),
				(int) Math.ceil((s.x + v.x) * scale),
				(int) Math.ceil((s.y + v.y) * scale));
	}
}
