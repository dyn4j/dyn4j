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
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldPoint;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.collision.narrowphase.Separation;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests shape collisions by plotting shapes, points, vectors, etc.
 * @author William Bittle
 */
public class Plotter extends Test {
	/** The first entity */
	private Entity e1 = null;
	
	/** The second entity */
	private Entity e2 = null;
	
	/** The radius of the points */
	private static final double r = 0.01;
	
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
		
		// set the bounds
		this.bounds = new Rectangle(16.0, 15.0);
		
		// create the world
		Bounds bounds = new RectangularBounds(this.bounds);
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// turn off gravity
		this.world.setGravity(new Vector());
		
		// setup the bodies in the world
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		//////////////////////////////////////
		// create the bodies
		//////////////////////////////////////
		
		List<Convex> shapes = null;
		Mass mass = null;
		
//		Circle c = new Circle(1.0);
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(c);
//		mass = Mass.create(c.getCenter());
//		e2 = new Entity(shapes, mass);
//		e2.translate(-1.0, 0.0);
//		this.world.add(e2);
		
		Triangle t = new Triangle(
				new Vector(0.45, -0.12),
				new Vector(-0.45, 0.38),
				new Vector(-0.15, -0.22));
		shapes = new ArrayList<Convex>(1);
		shapes.add(t);
		mass = Mass.create(t.getCenter());
		e2 = new Entity(shapes, mass, 128);
		e2.translate(0.0, 0.5);
		this.world.add(e2);
		
		Triangle t2 = new Triangle(
				new Vector(1.29, 0.25),
				new Vector(-0.71, 0.65),
				new Vector(-0.59, -0.85));
		shapes = new ArrayList<Convex>(1);
		shapes.add(t2);
		mass = Mass.create(t2.getCenter());
		e1 = new Entity(shapes, mass, 128);
//		e1.translate(0.15, 0.2);
		this.world.add(e1);
		
//		Polygon p1 = Geometry.getUnitCirclePolygon(5, 1.0);
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(p1);
//		mass = Mass.create(p1.getCenter());
//		e1 = new Entity(shapes, mass);
//		e1.translate(-0.3, 0.0);
//		this.world.add(e1);
		
//		Polygon p2 = Geometry.getUnitCirclePolygon(6, 0.5);
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(p2);
//		mass = Mass.create(p2.getCenter());
//		e2 = new Entity(shapes, mass);
//		e2.translate(0.0, 0.0);
//		this.world.add(e2);
		
//		Rectangle r1 = new Rectangle(0.5, 0.5);
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(r1);
//		mass = Mass.create(r1.getCenter());
//		e1 = new Entity(shapes, mass);
//		this.world.add(e1);
		
//		Rectangle r = new Rectangle(1.0, 1.0);
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(r);
//		mass = Mass.create(r.getCenter());
//		e2 = new Entity(shapes, mass);
//		this.world.add(e2);
		
//		Segment s = new Segment(new Vector(-0.3, 0.2), new Vector(0.0, -0.1));
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(s);
//		mass = Mass.create(s.getCenter());
//		e1 = new Entity(shapes, mass);
//		e1.translate(0.0, 0.0);
//		this.world.add(e1);
		
//		Segment s2 = new Segment(new Vector(-0.3, -0.3), new Vector(0.2, 0.3));
//		shapes = new ArrayList<Convex>(1);
//		shapes.add(s2);
//		mass = Mass.create(s2.getCenter());
//		e2 = new Entity(shapes, mass);
//		e2.translate(0.0, 0.0);
//		this.world.add(e2);
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
		g.setColor(Color.GREEN);
		
		Convex c1 = e1.getShapes().get(0);
		Transform t1 = e1.getTransform();
		Convex c2 = e2.getShapes().get(0);
		Transform t2 = e2.getTransform();
		
		Separation s = new Separation();
		Penetration p = new Penetration();
		
		Sat sat = new Sat();
		Gjk gjk = new Gjk();
		ClippingManifoldSolver cmf = new ClippingManifoldSolver();
		
		if (sat.detect(c1, t1, c2, t2, p)) {
			Manifold m = new Manifold();
			if (cmf.getManifold(p, c1, t1, c2, t2, m)) {
				// get the points
				List<ManifoldPoint> points = m.getPoints();
				Vector n = m.getNormal();
				// if we got a manifold lets show it
				// there are only two cases for 2D (2 points or 1 point)
				if (points.size() == 2) {
					ManifoldPoint mp1 = points.get(0);
					ManifoldPoint mp2 = points.get(1);
					Vector p1 = mp1.getPoint();
					Vector p2 = mp2.getPoint();
					this.renderPoint(g, p1.x, p1.y, r);
					this.renderPoint(g, p2.x, p2.y, r);
					g.setColor(Color.RED);
					this.renderNormal(g, p1.x, p1.y, p2.x, p2.y, n.x, n.y, p.getDepth());
					g.setColor(Color.BLUE);
					this.renderVector(g, p1.x, p1.y, n.x, n.y, mp1.getDepth());
					this.renderVector(g, p2.x, p2.y, n.x, n.y, mp2.getDepth());
				} else if (points.size() == 1) {
					ManifoldPoint mp1 = points.get(0);
					Vector p1 = mp1.getPoint();
					this.renderPoint(g, p1.x, p1.y, r);
					g.setColor(Color.RED);
					this.renderVector(g, p1.x, p1.y, n.x, n.y, p.getDepth());
					g.setColor(Color.BLUE);
					this.renderVector(g, p1.x, p1.y, n.x, n.y, mp1.getDepth());
				}
			}
		} else {
			if (gjk.distance(c1, t1, c2, t2, s)) {
				Vector p1 = s.getPoint1();
				Vector p2 = s.getPoint2();
				Vector n = s.getNormal();
				this.renderPoint(g, p1.x, p1.y, r);
				this.renderPoint(g, p2.x, p2.y, r);
				this.renderVector(g, p1.x, p1.y, n.x, n.y, s.getDistance());
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
