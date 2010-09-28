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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.codezealot.game.input.Input;
import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.codezealot.game.input.Input.Hold;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Ray;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.geometry.decompose.EarClipping;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the decomposition of a simple polygon.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class Decompose extends Test {
	/** The elapsed time since the last update */
	private double elapsedTime;
	
	/** The first simple polygon to decompose */
	private Vector2[] vertices;
	
	/** The current list of points added by the user */
	private List<Vector2> points;
	
	/** The first polygon decomposition result */
	private List<Convex> triangles;
	
	/** The time interval between showing the decomposition */
	private double interval = 0.75;
	
	/** The index of the shape to render to */
	private int toIndex = 0;
	
	/** The mouse to show the current position */
	private Mouse mouse;
	
	/** The radius of points drawn */
	private static final double r = 0.025;
	
	/** True if an error occurred in tesselation */
	private boolean error = false;
	
	/** The error message when an error occurs */
	private String errorMessage = "Invalid Polygon";
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Decompose";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the decomposition methods.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// set the camera position and zoom
		this.home();
		
		// create the world
		this.world = new World();
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// turn off gravity
		this.world.setGravity(new Vector2());
		
		// setup the bodies in the world
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		this.points = new ArrayList<Vector2>();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#renderBefore(java.awt.Graphics2D)
	 */
	@Override
	protected void renderBefore(Graphics2D g) {
		// render the axes
		this.renderAxes(g, Color.DARK_GRAY, 1.0, 10.0, Color.DARK_GRAY, 0.1, 4.0, Color.GRAY);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#renderAfter(java.awt.Graphics2D)
	 */
	@Override
	protected void renderAfter(Graphics2D g) {
		if (this.vertices != null) {
			int size = this.vertices.length;
			
			// render the total polygon
			g.setColor(Color.GREEN);
			for (int i = 0; i < size; i++) {
				Vector2 p1 = this.vertices[i];
				Vector2 p2 = this.vertices[i + 1 == size ? 0 : i + 1];
				this.renderLine(g, p1.x, p1.y, p2.x, p2.y);
			}
			
			// render the convex decomposition depending on the time
			g.setColor(Color.BLUE);
			for (int i = 0; i < this.toIndex; i++) {
				Convex convex = this.triangles.get(i);
				if (convex instanceof Polygon) {
					Polygon poly = (Polygon) convex;
					Vector2[] vertices = poly.getVertices();
					int vSize = vertices.length;
					for (int j = 0; j < vSize; j++) {
						Vector2 p1 = vertices[j];
						Vector2 p2 = vertices[(j + 1) == vSize ? 0 : j + 1];
						this.renderLine(g, p1.x, p1.y, p2.x, p2.y);
					}
				}
			}
			
			// always render the points
			g.setColor(Color.GREEN);
			for (int i = 0; i < size; i++) {
				Vector2 p = this.vertices[i];
				this.renderPoint(g, p.x, p.y, r);
			}
		} else {
			int size = this.points.size();
			if (this.error) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.GREEN);
			}
			
			// get the mouse location
			Point point = this.mouse.getRelativeLocation();
			// convert to world coordinates
			Vector2 v = this.screenToWorld(point.x, point.y);
			
			// only render lines if there is more than one point
			if (size > 1) {
				// draw the current list of points
				for (int i = 0; i < size - 1; i++) {
					Vector2 p1 = this.points.get(i);
					Vector2 p2 = this.points.get(i + 1);
					this.renderLine(g, p1.x, p1.y, p2.x, p2.y);
				}
			}
			
			// always render the points
			for (int i = 0; i < size; i++) {
				Vector2 p = this.points.get(i);
				this.renderPoint(g, p.x, p.y, r);
			}
			
			if (size > 0 && !this.error) {
				// draw a line from the last point to the current mouse point
				Vector2 l = this.points.get(size - 1);
				this.renderLine(g, l.x, l.y, v.x, v.y);
			}
			
			if (!this.error) {
				// draw the mouse point
				this.renderPoint(g, v.x, v.y, r);
			}
			
			// show the error message if needed
			if (this.error) {
				Vector2 p = this.screenToWorld(5.0, 15.0);
				AffineTransform at = g.getTransform();
				g.transform(AffineTransform.getScaleInstance(1, -1));
				g.drawString(this.errorMessage, (int) (p.x * scale), (int) (-p.y * scale));
				g.setTransform(at);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#update(double)
	 */
	@Override
	public void update(double dt) {
		// call the super method
		super.update(dt);
		// only update when the user wants the shape tesselated
		if (this.vertices != null) {
			// compute the number of convex shapes to draw
			this.elapsedTime += dt;
			if (this.elapsedTime >= interval) {
				if ((this.toIndex + 1) > this.triangles.size()) {
					this.toIndex = 0;
				} else {
					this.toIndex++;
				}
				this.elapsedTime = this.elapsedTime - interval;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#update(int)
	 */
	@Override
	public void update(int steps) {
		// update by the interval
		this.update(this.interval);
	}
	
	/**
	 * Renders the given point.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param r the radius of the point
	 */
	protected void renderPoint(Graphics2D g, double x, double y, double r) {
		g.fillOval(
				(int) Math.ceil((x - r) * scale), 
				(int) Math.ceil((y - r) * scale),
				(int) Math.ceil((r + r) * scale), 
				(int) Math.ceil((r + r) * scale));
	}
	
	/**
	 * Renders a line from the given x1,y1 coordinates to x2,y2 coordinates.
	 * @param g the graphics object to render to
	 * @param x1 the first x coordinate
	 * @param y1 the first y coordinate
	 * @param x2 the second x coordinate
	 * @param y2 the second y coordinate
	 */
	protected void renderLine(Graphics2D g, double x1, double y1, double x2, double y2) {
		g.drawLine(
				(int) Math.ceil(x1 * scale),
				(int) Math.ceil(y1 * scale),
				(int) Math.ceil(x2 * scale),
				(int) Math.ceil(y2 * scale));
	}
	
	/**
	 * Renders the given vector from the origin along the x and y components
	 * for the given magnitude.
	 * @param g the graphics object to render to
	 * @param x the x component
	 * @param y the y component
	 * @param magnitude the magnitude
	 */
	protected void renderVector(Graphics2D g, double x, double y, double magnitude) {
		g.drawLine(0, 0, (int) Math.ceil(x * magnitude * scale), (int) Math.ceil(y * magnitude * scale));
	}
	
	/**
	 * Renders the given vector (x, y) from the start point (sx, sy) given the magnitude.
	 * <p>
	 * This method assumes that the vector components are components of a normalized vector.
	 * @param g the graphics object to render to
	 * @param sx the start x coordinate
	 * @param sy the start y coordinate
	 * @param x the x component of the vector
	 * @param y the y component of the vector
	 * @param magnitude the magnitude of the vector
	 */
	protected void renderVector(Graphics2D g, double sx, double sy, double x, double y, double magnitude) {
		this.renderLine(g, sx, sy, sx + x * magnitude, sy + y * magnitude);
	}
	
	/**
	 * Renders the given normal (x, y) from the center of the given line.
	 * @param g the graphics object to render to
	 * @param x1 the x coordinate of the first line point
	 * @param y1 the y coordinate of the first line point
	 * @param x2 the x coordinate of the second line point
	 * @param y2 the y coordinate of the second line point
	 * @param x the x component of the normal
	 * @param y the y component of the normal
	 * @param l the length
	 */
	protected void renderNormal(Graphics2D g, double x1, double y1, double x2, double y2, double x, double y, double l) {
		// compute the start point
		double sx = (x1 + x2) / 2.0;
		double sy = (y1 + y2) / 2.0;
		// render the vector with a magnitude of 1m
		this.renderVector(g, sx, sy, x, y, l);
	}
	
	/**
	 * Renders the given ray to the given graphics object.
	 * @param g the graphics object to render to
	 * @param ray the ray to render
	 * @since 2.0.0
	 */
	protected void renderRay(Graphics2D g, Ray ray) {
		// get the ray attributes (world coordinates)
		Vector2 s = ray.getStart();
		Vector2 d = ray.getDirection();
		
		// draw the line from the start to the end, along d, l distance
		g.drawLine((int) Math.ceil(s.x * scale), 
				   (int) Math.ceil(s.y * scale), 
				   (int) Math.ceil(s.x * scale + d.x * 10000.0), 
				   (int) Math.ceil(s.y * scale + d.y * 10000.0));
	}
	
	/**
	 * Renders the x and y axis with minor and major ticks.
	 * @param g the graphics object to render to
	 * @param lineColor the color of the axes
	 * @param majorTickScale the major tick scale in meters
	 * @param majorTickWidth the major tick width in pixels
	 * @param majorTickColor the major tick color
	 * @param minorTickScale the minor tick scale in meters
	 * @param minorTickWidth the minor tick width in pixels
	 * @param minorTickColor the minor tick color
	 */
	protected void renderAxes(Graphics2D g, Color lineColor,
			double majorTickScale, double majorTickWidth, Color majorTickColor,
			double minorTickScale, double minorTickWidth, Color minorTickColor) {
		// set the line color
		g.setColor(lineColor);
		
		// get the current width and height
		double width = this.size.width;
		double height = this.size.height;
		
		// render the y axis
		g.drawLine(0,  (int) Math.ceil(height / 2.0 - this.offset.y * this.scale),
				   0, -(int) Math.ceil(height / 2.0 + this.offset.y * this.scale));
		// render the x axis
		g.drawLine( (int) Math.ceil(width / 2.0 - this.offset.x * this.scale), 0,
				   -(int) Math.ceil(width / 2.0 + this.offset.x * this.scale), 0);
		
		// compute the major tick offset
		int mao = (int) Math.ceil(majorTickWidth / 2.0);
		// compute the minor tick offset
		int mio = (int) Math.ceil(minorTickWidth / 2.0);
		
		// render the y tick marks
		// compute the number of major ticks on the y axis
		int yMajorTicks= (int) Math.ceil(height / 2.0 / (majorTickScale * this.scale)) + 1;
		// compute the y axis offset
		int yoffset = -(int) Math.floor(this.offset.y / majorTickScale);
		for (int i = (-yMajorTicks + yoffset); i < (yMajorTicks + yoffset); i++) {
			// set the color
			g.setColor(majorTickColor);
			// compute the major tick y
			int yma = (int) Math.ceil(majorTickScale * this.scale * i);
			// skip drawing the major tick at zero
			if (i != 0) {
				// draw the +y ticks
				g.drawLine(-mao, yma, mao, yma);
			}
			
			// render the minor y tick marks
			// set the color
			g.setColor(minorTickColor);
			// compute the number of minor ticks
			int minorTicks = (int) Math.ceil(majorTickScale / minorTickScale);
			for (int j = 1; j < minorTicks; j++) {
				// compute the major tick y
				int ymi = (int) Math.ceil(majorTickScale * this.scale * i - minorTickScale * this.scale * j);
				// draw the +y ticks
				g.drawLine(-mio, ymi, mio, ymi);
			}
		}
		
		// render the x tick marks
		// compute the number of major ticks on the x axis
		int xMajorTicks= (int) Math.ceil(width / 2.0 / (majorTickScale * this.scale)) + 1;
		// compute the x axis offset
		int xoffset = -(int) Math.floor(this.offset.x / majorTickScale);
		for (int i = (-xMajorTicks + xoffset); i < (xMajorTicks + xoffset); i++) {
			// set the color
			g.setColor(majorTickColor);
			// compute the major tick x
			int xma = (int) Math.ceil(majorTickScale * this.scale * i);
			// skip drawing the major tick at zero
			if (i != 0) {
				// draw the major ticks
				g.drawLine(xma, mao, xma, -mao);
			}
			
			// render the minor x tick marks
			// set the color
			g.setColor(minorTickColor);
			// compute the number of minor ticks
			int minorTicks = (int) Math.ceil(majorTickScale / minorTickScale);
			for (int j = 1; j < minorTicks; j++) {
				// compute the major tick x
				int xmi = (int) Math.ceil(majorTickScale * this.scale * i - minorTickScale * this.scale * j);
				// draw the minor ticks
				g.drawLine(xmi, mio, xmi, -mio);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getControls()
	 */
	@Override
	public String[][] getControls() {
		return new String[][] {
				{"Left Mouse Button", "Add a point to the polygon list."},
				{"Right Mouse Button", "Clear the current polygon point list."},
				{"t", "Closes the polygon and tesselates."}};
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#initializeInput(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		// the keys above are already registered by the TestBed
		
		// we need to store the mouse so we know its location
		this.mouse = mouse;
		// we also need to override the TestBed's registration of the left mouse button
		// so that we don't allow holding of the button
		mouse.remove(MouseEvent.BUTTON1);
		mouse.add(new Input(MouseEvent.BUTTON1, Hold.NO_HOLD));
		mouse.remove(MouseEvent.BUTTON3);
		mouse.add(new Input(MouseEvent.BUTTON3, Hold.NO_HOLD));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#poll(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void poll(Keyboard keyboard, Mouse mouse) {
		super.poll(keyboard, mouse);
		
		// look for the t key
		if (keyboard.isPressed(KeyEvent.VK_T)) {
			// only tesselate polys with 4 or more vertices
			if (this.points.size() > 3) {
				// place the points into the array
				this.vertices = new Vector2[this.points.size()];
				this.points.toArray(this.vertices);
				// perform the tesselation
				EarClipping ec = new EarClipping();
				try {
					this.triangles = ec.decompose(this.vertices);
				} catch (Exception e) {
					// if we get an exception color the thing red
					// and flag that its in error so that the next
					// click of the mouse clears the polygon points
					this.error = true;
					this.vertices = null;
					this.triangles = null;
				}
			}
		}
		
		// look for the left mouse button
		if (mouse.isPressed(MouseEvent.BUTTON1)) {
			if (this.vertices != null || this.error) {
				// stop the tesselation animation and start a new poly
				this.vertices = null;
				this.points.clear();
				this.elapsedTime = 0;
				this.toIndex = 0;
				this.triangles = null;
				this.error = false;
			} else {
				// add the point to the list of points
				Point p = mouse.getRelativeLocation();
				// convert to world coordinates
				Vector2 v = this.screenToWorld(p.x, p.y);
				this.points.add(v);
			}
		}
		
		// look for the right mouse button
		if (mouse.isPressed(MouseEvent.BUTTON3)) {
			this.vertices = null;
			this.points.clear();
			this.elapsedTime = 0;
			this.toIndex = 0;
			this.triangles = null;
			this.error = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 128.0;
		// set the offset
		this.offset.zero();
	}
}
