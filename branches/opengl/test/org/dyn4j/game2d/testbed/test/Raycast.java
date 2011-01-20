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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.codezealot.game.input.Input;
import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.dyn4j.game2d.dynamics.BodyFixture;
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
import org.dyn4j.game2d.testbed.GLHelper;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the {@link World}'s raycast methods.
 * @author William Bittle
 * @version 2.2.2
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
		triangle.addFixture(new BodyFixture(triShape));
		triangle.setMass(Mass.Type.INFINITE);
		triangle.translate(-0.90625, 2.40625);
		this.world.add(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		Entity circle = new Entity();
		circle.addFixture(new BodyFixture(cirShape));
		circle.setMass(Mass.Type.INFINITE);
		circle.translate(2.421875, 3.5);
		this.world.add(circle);
		
		// create a line segment
		Segment segShape = new Segment(new Vector2(0.5, 0.5), new Vector2(-0.5, -0.5));
		Entity segment1 = new Entity();
		segment1.addFixture(new BodyFixture(segShape));
		segment1.setMass(Mass.Type.INFINITE);
		segment1.translate(4.53125, 3.34375);
		this.world.add(segment1);
		
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		Entity rectangle = new Entity();
		rectangle.addFixture(new BodyFixture(rectShape));
		rectangle.setMass(Mass.Type.INFINITE);
		rectangle.translate(1.65625, 2.21875);
		this.world.add(rectangle);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		Entity polygon = new Entity();
		polygon.addFixture(new BodyFixture(polyShape));
		polygon.setMass(Mass.Type.INFINITE);
		polygon.translate(0.28125, 4.765625);
		this.world.add(polygon);
		
		// try a compound object (Capsule)
		Circle c1 = new Circle(0.5);
		BodyFixture c1Fixture = new BodyFixture(c1);
		c1Fixture.setDensity(0.5);
		Circle c2 = new Circle(0.5);
		BodyFixture c2Fixture = new BodyFixture(c2);
		c2Fixture.setDensity(0.5);
		Rectangle rm = new Rectangle(2.0, 1.0);
		// translate the circles in local coordinates
		c1.translate(-1.0, 0.0);
		c2.translate(1.0, 0.0);
		Entity capsule = new Entity();
		capsule.addFixture(c1Fixture);
		capsule.addFixture(c2Fixture);
		capsule.addFixture(new BodyFixture(rm));
		capsule.setMass(Mass.Type.INFINITE);
		capsule.translate(4.890625, 5.328125);
		this.world.add(capsule);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#renderBefore(javax.media.opengl.GL2)
	 */
	@Override
	protected void renderBefore(GL2 gl) {
		// render the axes
		this.renderAxes(gl, new float[] { 0.3f, 0.3f, 0.3f, 1.0f }, 
				1.0, 0.25, new float[] { 0.3f, 0.3f, 0.3f, 1.0f }, 
				0.1, 0.125, new float[] { 0.5f, 0.5f, 0.5f, 1.0f });
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#renderAfter(javax.media.opengl.GL2)
	 */
	@Override
	protected void renderAfter(GL2 gl) {
		// create the list for the results
		List<RaycastResult> results = new ArrayList<RaycastResult>();
		
		// render the ray
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		this.renderRay(gl, this.ray, this.length);
		
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
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
				
				GLHelper.fillRectangle(gl, point.x, point.y, r, r);
				
				gl.glBegin(GL.GL_LINES);
					gl.glVertex2d(point.x, point.y);
					gl.glVertex2d(point.x + normal.x, point.y + normal.y);
				gl.glEnd();
			}
		}
	}
	
	/**
	 * Renders the given ray to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param ray the ray to render
	 * @param length the ray length; 0 for infinite length
	 * @since 2.0.0
	 */
	protected void renderRay(GL2 gl, Ray ray, double length) {
		// get the ray attributes (world coordinates)
		Vector2 s = ray.getStart();
		Vector2 d = ray.getDirection();
		
		double l = length > 0.0 ? length * scale : 10000.0;
		
		// draw the line from the start to the end, along d, l distance
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(s.x, s.y);
			gl.glVertex2d(s.x + d.x * l, s.y + d.y * l);
		gl.glEnd();
	}
	
	/**
	 * Renders the x and y axis with minor and major ticks.
	 * @param gl the OpenGL graphics context
	 * @param lineColor the color of the axes; RGBA
	 * @param majorTickScale the major tick scale in meters
	 * @param majorTickWidth the major tick width in pixels
	 * @param majorTickColor the major tick color; RGBA
	 * @param minorTickScale the minor tick scale in meters
	 * @param minorTickWidth the minor tick width in pixels
	 * @param minorTickColor the minor tick color; RGBA
	 */
	protected void renderAxes(GL2 gl, float[] lineColor,
			double majorTickScale, double majorTickWidth, float[] majorTickColor,
			double minorTickScale, double minorTickWidth, float[] minorTickColor) {
		// set the line color
		gl.glColor4fv(lineColor, 0);
		
		// get the current width and height
		double width = this.size.width;
		double height = this.size.height;
		
		// render the y axis
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(0.0,  height / 2.0 - this.offset.y);
			gl.glVertex2d(0.0, -height / 2.0 + this.offset.y);
			
			gl.glVertex2d( width / 2.0 - this.offset.x, 0.0);
			gl.glVertex2d(-width / 2.0 + this.offset.x, 0.0);
		gl.glEnd();
		
		// compute the major tick offset
		double mao = majorTickWidth / 2.0;
		// compute the minor tick offset
		double mio = minorTickWidth / 2.0;
		
		// render the y tick marks
		// compute the number of major ticks on the y axis
		int yMajorTicks= (int) Math.ceil(height / 2.0 / majorTickScale) + 1;
		// compute the y axis offset
		int yoffset = -(int) Math.floor(this.offset.y / majorTickScale);
		
		gl.glBegin(GL.GL_LINES);
		for (int i = (-yMajorTicks + yoffset); i < (yMajorTicks + yoffset); i++) {
			// set the color
			gl.glColor4fv(majorTickColor, 0);
			// compute the major tick y
			double yma = majorTickScale * i;
			// skip drawing the major tick at zero
			
			if (i != 0) {
				// draw the +y ticks
				gl.glVertex2d(-mao, yma);
				gl.glVertex2d( mao, yma);
			}
			
			// render the minor y tick marks
			// set the color
			gl.glColor4fv(minorTickColor, 0);
			// compute the number of minor ticks
			int minorTicks = (int) Math.ceil(majorTickScale / minorTickScale);
			for (int j = 1; j < minorTicks; j++) {
				// compute the major tick y
				double ymi = majorTickScale * i - minorTickScale * j;
				// draw the +y ticks
				gl.glVertex2d(-mio, ymi);
				gl.glVertex2d( mio, ymi);
			}
		}
		
		// render the x tick marks
		// compute the number of major ticks on the x axis
		int xMajorTicks= (int) Math.ceil(width / 2.0 / majorTickScale) + 1;
		// compute the x axis offset
		int xoffset = -(int) Math.floor(this.offset.x / majorTickScale);
		for (int i = (-xMajorTicks + xoffset); i < (xMajorTicks + xoffset); i++) {
			// set the color
			gl.glColor4fv(majorTickColor, 0);
			// compute the major tick x
			double xma = majorTickScale * i;
			// skip drawing the major tick at zero
			if (i != 0) {
				// draw the major ticks
				gl.glVertex2d(xma,  mao);
				gl.glVertex2d(xma, -mao);
			}
			
			// render the minor x tick marks
			// set the color
			gl.glColor4fv(minorTickColor, 0);
			// compute the number of minor ticks
			int minorTicks = (int) Math.ceil(majorTickScale / minorTickScale);
			for (int j = 1; j < minorTicks; j++) {
				// compute the major tick x
				double xmi = majorTickScale * i - minorTickScale * j;
				// draw the minor ticks
				gl.glVertex2d(xmi,  mio);
				gl.glVertex2d(xmi, -mio);
			}
		}
		gl.glEnd();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getControls()
	 */
	@Override
	public String[][] getControls() {
		return new String[][] {
				{"Change Angle", "Decrease/Increase the angle from the positive x-axis by 2 degrees.", "<html><span style='color: blue;'>d</span> / <span style='color: blue;'>D</span></html>"},
				{"Change Length", "Decrease/Increase the length of the ray by 0.25m.", "<html><span style='color: blue;'>l</span> / <span style='color: blue;'>L</span></html>"},
				{"Toggle Infinite", "Makes the ray's length infinite.", "<html><span style='color: blue;'>i</span></html>"},
				{"Toggle All", "Toggles between all results or the closest result.", "<html><span style='color: blue;'>a</span></html>"}
		};
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
