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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Vector;

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
		this.world.getBodies().clear();
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
		List<Body> bodies = this.world.getBodies();
		int size = bodies.size();
		for (int i = 0; i < size; i++) {
			Entity obj = (Entity) bodies.get(i);
			obj.render(g, this.scale);
		}

		if (draw.drawContacts()) {
			// get the contact counter
			ContactCounter cc = (ContactCounter) this.world.getContactListener();
			// get the contacts from the counter
			List<Vector> contacts = cc.getContacts();
			g.setColor(Color.ORANGE);
			// render all the contact points
			if (contacts != null && contacts.size() > 0) {
				int cSize = contacts.size();
				for (int i = 0; i < cSize; i++) {
					Vector c = contacts.get(i);
					// draw the contact as a square
					g.drawRect((int) Math.ceil((c.x - 0.05) * scale),
							   (int) Math.ceil((c.y - 0.05) * scale), 
							   (int) Math.ceil(0.10 * scale),
							   (int) Math.ceil(0.10 * scale));
				}
			}
		}
		
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
}
