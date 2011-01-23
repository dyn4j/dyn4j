/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldPoint;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.collision.narrowphase.Separation;
import org.dyn4j.game2d.dynamics.BodyFixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.GLHelper;
import org.dyn4j.game2d.testbed.Test;
import org.dyn4j.game2d.testbed.input.Input;
import org.dyn4j.game2d.testbed.input.Keyboard;
import org.dyn4j.game2d.testbed.input.Mouse;

/**
 * Test used to plot shapes, points, vectors, etc for debugging.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Plotter extends Test {
	/** The first entity */
	private Entity e1 = null;
	
	/** The second entity */
	private Entity e2 = null;
	
	/** The list of entities for the first object */
	private Entity[] e1List = new Entity[13];
	
	/** The list of entities for the second object */
	private Entity[] e2List = new Entity[13];
	
	/** The first entity's current shape */
	private int e1Shape = 0;
	
	/** The second entity's current shape */
	private int e2Shape = 0;
	
	/** Whether to flip the shape order or not */
	private boolean flip = false;
	
	/** The render radius of the points */
	private static final double r = 0.01;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Plotter";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Used to plot shapes, points, vectors, etc for debugging.";
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
		// create all the bodies
		for (int i = 0; i < 13; i++) {
			this.e1List[i] = this.getEntity(i);
			this.e2List[i] = this.getEntity(i);
		}
		// default to the first set
		this.e1 = this.e1List[4];
		this.e2 = this.e2List[5];
		// add them to the world
		this.world.add(this.e1);
		this.world.add(this.e2);
	}
	
	/**
	 * Helper method to create the various shapes from the collision tests.
	 * @param index the shape index
	 * @return {@link Entity}
	 */
	public Entity getEntity(int index) {
		Entity e = new Entity(128);
		// which shape to make?
		if (index == 0) {
			Circle c = new Circle(1.0);
			e.addFixture(new BodyFixture(c));
		} else if (index == 1) {
			Circle c = new Circle(0.5);
			e.addFixture(new BodyFixture(c));
		} else if (index == 2) {
			Polygon p = Geometry.createUnitCirclePolygon(6, 0.5);
			e.addFixture(new BodyFixture(p));
		} else if (index == 3) {
			Polygon p = Geometry.createUnitCirclePolygon(5, 1.0);
			e.addFixture(new BodyFixture(p));
		} else if (index == 4) {
			Rectangle r = new Rectangle(1.0, 1.0);
			e.addFixture(new BodyFixture(r));
		} else if (index == 5) {
			Rectangle r = new Rectangle(0.5, 0.5);
			e.addFixture(new BodyFixture(r));
		} else if (index == 6) {
			Triangle t = new Triangle(new Vector2(0.45, -0.12), new Vector2(-0.45, 0.38), new Vector2(-0.15, -0.22));
			e.addFixture(new BodyFixture(t));
		} else if (index == 7) {
			Triangle t = new Triangle(new Vector2(1.29, 0.25), new Vector2(-0.71, 0.65), new Vector2(-0.59, -0.85));
			e.addFixture(new BodyFixture(t));
		} else if (index == 8) {
			Triangle t = new Triangle(new Vector2(0.5, 0.5), new Vector2(-0.3, -0.5), new Vector2(1.0, -0.3));
			e.addFixture(new BodyFixture(t));
		} else if (index == 9) {	
			Segment s = new Segment(new Vector2(-0.5, 0.0), new Vector2(0.5, 0.0));
			e.addFixture(new BodyFixture(s));
		} else if (index == 10) {
			Segment s = new Segment(new Vector2(0.1, -0.3), new Vector2(-0.8, 0.2));
			e.addFixture(new BodyFixture(s));
		} else if (index == 11) {
			Segment s = new Segment(new Vector2(-0.3, -0.3), new Vector2(0.2, 0.3));
			e.addFixture(new BodyFixture(s));
		} else {
			Segment s = new Segment(new Vector2(-0.3, 0.2), new Vector2(0.0, -0.1));
			e.addFixture(new BodyFixture(s));
		}
		// set the mass to infinite
		e.setMass(Mass.Type.INFINITE);
		// return the entity
		return e;
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
		// set the color
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		
		Convex c1, c2;
		Transform t1, t2;
		if (this.flip) {
			c1 = e2.getFixture(0).getShape();
			t1 = e2.getTransform();
			c2 = e1.getFixture(0).getShape();
			t2 = e1.getTransform();
		} else {
			c1 = e1.getFixture(0).getShape();
			t1 = e1.getTransform();
			c2 = e2.getFixture(0).getShape();
			t2 = e2.getTransform();
		}
		
		Separation s = new Separation();
		Penetration p = new Penetration();
		
		// use whatever npd was set using the control panel
		NarrowphaseDetector npd = this.world.getNarrowphaseDetector();
		ClippingManifoldSolver cmf = new ClippingManifoldSolver();
		
		if (npd.detect(c1, t1, c2, t2, p)) {
			Manifold m = new Manifold();
			if (cmf.getManifold(p, c1, t1, c2, t2, m)) {
				// get the points
				List<ManifoldPoint> points = m.getPoints();
				Vector2 n = m.getNormal();
				// if we got a manifold lets show it
				// there are only two cases for 2D (2 points or 1 point)
				if (points.size() == 2) {
					ManifoldPoint mp1 = points.get(0);
					ManifoldPoint mp2 = points.get(1);
					Vector2 p1 = mp1.getPoint();
					Vector2 p2 = mp2.getPoint();
					
					GLHelper.fillRectangle(gl, p1.x, p1.y, r, r);
					GLHelper.fillRectangle(gl, p2.x, p2.y, r, r);
					
					Vector2 mid = p1.copy().add(p2).multiply(0.5);
					gl.glBegin(GL.GL_LINES);
						gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
						gl.glVertex2d(mid.x, mid.y);
						gl.glVertex2d(mid.x + n.x * p.getDepth(), mid.y + n.y * p.getDepth());
						
						gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
						gl.glVertex2d(p1.x, p1.y);
						gl.glVertex2d(p1.x + n.x * mp1.getDepth(), p1.y + n.y * mp1.getDepth());
						
						gl.glVertex2d(p2.x, p2.y);
						gl.glVertex2d(p2.x + n.x * mp2.getDepth(), p2.y + n.y * mp2.getDepth());
					gl.glEnd();
				} else if (points.size() == 1) {
					ManifoldPoint mp1 = points.get(0);
					Vector2 p1 = mp1.getPoint();
					
					GLHelper.fillRectangle(gl, p1.x, p1.y, r, r);
					
					gl.glBegin(GL.GL_LINES);
						gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
						gl.glVertex2d(p1.x, p1.y);
						gl.glVertex2d(p1.x + n.x * p.getDepth(), p1.y + n.y * p.getDepth());
						
						gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
						gl.glVertex2d(p1.x, p1.y);
						gl.glVertex2d(p1.x + n.x * mp1.getDepth(), p1.y + n.y * mp1.getDepth());
					gl.glEnd();
				}
			}
		} else {
			Gjk gjk = new Gjk();
			if (gjk.distance(c1, t1, c2, t2, s)) {
				Vector2 p1 = s.getPoint1();
				Vector2 p2 = s.getPoint2();
				Vector2 n = s.getNormal();
				
				GLHelper.fillRectangle(gl, p1.x, p1.y, r, r);
				GLHelper.fillRectangle(gl, p2.x, p2.y, r, r);
				
				gl.glBegin(GL.GL_LINES);
					gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
					gl.glVertex2d(p1.x, p1.y);
					gl.glVertex2d(p1.x + n.x * s.getDistance(), p1.y + n.y * s.getDistance());
				gl.glEnd();
			}
		}
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
				{"<html>Cycle 1<sup>st</sup> Shape</html>", "<html>Cycle through the list of test shapes for the first body.</html>", "<html><span style='color: blue;'>1</span></html>"},
				{"<html>Cycle 2<sup>nd</sup> Shape</html>", "<html>Cycle through the list of test shapes for the second body.</html>", "<html><span style='color: blue;'>2</span></html>"},
				{"Reverse Order", "<html>Reverses the order in manifold generation.</html>", "<html><span style='color: blue;'>f</span></html>"}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#initializeInput(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		
		// setup the 1 and 2 keys
		keyboard.add(new Input(KeyEvent.VK_1, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_2, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_F, Input.Hold.NO_HOLD));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#poll(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void poll(Keyboard keyboard, Mouse mouse) {
		super.poll(keyboard, mouse);
		
		// look for the 1 key
		if (keyboard.isPressed(KeyEvent.VK_1)) {
			// save the current entity
			Entity te = this.e1;
			// increment the current shape
			this.e1Shape = this.e1Shape + 1 == this.e1List.length ? 0 : this.e1Shape + 1;
			// remove the current body from the world
			this.world.remove(this.e1);
			// set the new shape
			this.e1 = this.e1List[this.e1Shape];
			// find the difference in the centers
			Vector2 tx = te.getWorldCenter().difference(this.e1.getWorldCenter());
			// translate the shape to that position
			this.e1.translate(tx);
			// add it to the world
			this.world.add(this.e1);
		}
		
		// look for the 2 key
		if (keyboard.isPressed(KeyEvent.VK_2)) {
			// save the current entity
			Entity te = this.e2;
			// increment the current shape
			this.e2Shape = this.e2Shape + 1 == this.e2List.length ? 0 : this.e2Shape + 1;
			// remove the current body from the world
			this.world.remove(this.e2);
			// set the new shape
			this.e2 = this.e2List[this.e2Shape];
			// find the difference in the centers
			Vector2 tx = te.getWorldCenter().difference(this.e2.getWorldCenter());
			// translate the shape to that position
			this.e2.translate(tx);
			// add it to the world
			this.world.add(this.e2);
		}
		
		// look for the f key
		if (keyboard.isPressed(KeyEvent.VK_F)) {
			// then switch the body order
			this.flip = !this.flip;
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
