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
package org.dyn4j.game2d.testbed.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.codezealot.game.input.Input;
import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.dynamics.RaycastResult;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Ray;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the {@link World}'s raycast methods.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class Raycast extends Test {
	/** The render radius of the points */
	private static final double r = 0.01;
	
	/** The ray for the raycast test */
	private Ray ray;
	
	/** The ray length; initially zero for an infinite length */
	private double length = 0.0;
	
	/** Whether to get all results or just the closest */
	private boolean all = false;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Raycast";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the raycast methods of the World class.";
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
		
		// create the ray
		Vector2 s = new Vector2();
		Vector2 d = new Vector2(Math.sqrt(3) * 0.5, 0.5);
		this.ray = new Ray(s, d);
		
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
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5));
		Entity triangle = new Entity();
		triangle.addFixture(new Fixture(triShape));
		triangle.setMass(Mass.Type.INFINITE);
		triangle.translate(-0.90625, 2.40625);
		this.world.add(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		Entity circle = new Entity();
		circle.addFixture(new Fixture(cirShape));
		circle.setMass(Mass.Type.INFINITE);
		circle.translate(2.421875, 3.5);
		this.world.add(circle);
		
		// create a line segment
		Segment segShape = new Segment(new Vector2(0.5, 0.5), new Vector2(-0.5, -0.5));
		Entity segment1 = new Entity();
		segment1.addFixture(new Fixture(segShape));
		segment1.setMass(Mass.Type.INFINITE);
		segment1.translate(4.53125, 3.34375);
		this.world.add(segment1);
		
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		Entity rectangle = new Entity();
		rectangle.addFixture(new Fixture(rectShape));
		rectangle.setMass(Mass.Type.INFINITE);
		rectangle.translate(1.65625, 2.21875);
		this.world.add(rectangle);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		Entity polygon = new Entity();
		polygon.addFixture(new Fixture(polyShape));
		polygon.setMass(Mass.Type.INFINITE);
		polygon.translate(0.28125, 4.765625);
		this.world.add(polygon);
		
		// try a compound object (Capsule)
		Circle c1 = new Circle(0.5);
		Fixture c1Fixture = new Fixture(c1);
		c1Fixture.setDensity(0.5);
		Circle c2 = new Circle(0.5);
		Fixture c2Fixture = new Fixture(c2);
		c2Fixture.setDensity(0.5);
		Rectangle rm = new Rectangle(2.0, 1.0);
		// translate the circles in local coordinates
		c1.translate(-1.0, 0.0);
		c2.translate(1.0, 0.0);
		Entity capsule = new Entity();
		capsule.addFixture(c1Fixture);
		capsule.addFixture(c2Fixture);
		capsule.addFixture(new Fixture(rm));
		capsule.setMass(Mass.Type.INFINITE);
		capsule.translate(4.890625, 5.328125);
		this.world.add(capsule);
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
		// create the list for the results
		List<RaycastResult> results = new ArrayList<RaycastResult>();
		
		// render the ray
		g.setColor(Color.RED);
		this.renderRay(g, this.ray, this.length);
		
		g.setColor(Color.GREEN);
		// perform a raycast
		if (this.world.raycast(this.ray, this.length, false, this.all, results)) {
			int size = results.size();
			// loop over the results
			for (int i = 0; i < size; i++) {
				// should always contain just one result
				RaycastResult result = results.get(i);
				org.dyn4j.game2d.collision.narrowphase.Raycast raycast = result.getRaycast();
				
				// draw the normal and point
				Vector2 point = raycast.getPoint();
				Vector2 normal = raycast.getNormal();
				
				this.renderPoint(g, point.x, point.y, r);
				this.renderVector(g, point.x, point.y, normal.x, normal.y, 1.0);
			}
		}
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
	 * @param length the ray length; 0 for infinite length
	 * @since 2.0.0
	 */
	protected void renderRay(Graphics2D g, Ray ray, double length) {
		// get the ray attributes (world coordinates)
		Vector2 s = ray.getStart();
		Vector2 d = ray.getDirection();
		
		double l = length > 0.0 ? length * scale : 10000.0;
		
		// draw the line from the start to the end, along d, l distance
		g.drawLine((int) Math.ceil(s.x * scale), 
				   (int) Math.ceil(s.y * scale), 
				   (int) Math.ceil(s.x * scale + d.x * l), 
				   (int) Math.ceil(s.y * scale + d.y * l));
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
				{"d", "Decrease the angle from the positive x-axis by 2 degrees."},
				{"D", "Increase the angle from the positive x-axis by 2 degrees."},
				{"l", "Decrease the length of the ray by 0.25m."},
				{"L", "Increase the lenght of the ray by 0.25m."},
				{"i", "Make the ray's length infinite."},
				{"a", "Toggles all or closest results."}};
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#initializeInput(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		
		// shift is already setup by the testbed
		
		// setup the a and l
		
		keyboard.add(new Input(KeyEvent.VK_D, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_L, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_I, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_A, Input.Hold.NO_HOLD));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#poll(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void poll(Keyboard keyboard, Mouse mouse) {
		super.poll(keyboard, mouse);
		
		// look for the a key
		if (keyboard.isPressed(KeyEvent.VK_D)) {
			// look for the shift key
			if (keyboard.isPressed(KeyEvent.VK_SHIFT)) {
				this.ray.getDirection().rotate(Math.toRadians(2.0));
			} else {
				this.ray.getDirection().rotate(Math.toRadians(-2.0));
			}
		}
		
		// look for the l key
		if (keyboard.isPressed(KeyEvent.VK_L)) {
			// look for the shift key
			if (keyboard.isPressed(KeyEvent.VK_SHIFT)) {
				this.length += 0.25;
			} else {
				if (this.length != 0.0) {
					this.length -= 0.25;
				}
			}
		}
		
		// look for the i key
		if (keyboard.isPressed(KeyEvent.VK_I)) {
			this.length = 0.0;
		}
		
		// look for the a key
		if (keyboard.isPressed(KeyEvent.VK_A)) {
			this.all = !this.all;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the offset
		this.offset.set(-3.0, -2.5);
	}
}
