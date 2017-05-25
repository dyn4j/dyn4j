/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.samples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.DetectResult;
import org.dyn4j.examples.Graphics2DRenderer;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * A simple scene showing how to determine if the mouse touched
 * a body.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public class MousePicking extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 9192144129907042620L;
	
	/** The picking radius */
	private static final double PICKING_RADIUS = 0.1;
	
	/** A point for tracking the mouse click */
	private Point point;
	
	/** The world space mouse point */
	private Vector2 worldPoint = new Vector2();
	
	/** The picking results */
	private List<DetectResult> results = new ArrayList<DetectResult>();
	
	/**
	 * A custom mouse adapter for listening for mouse clicks.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			// store the mouse click postion for use later
			point = new Point(e.getX(), e.getY());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// store the mouse click postion for use later
			point = new Point(e.getX(), e.getY());
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			point = null;
		}
	}

	/**
	 * Default constructor.
	 */
	public MousePicking() {
		super("Picking", 32.0);
		
		MouseAdapter ml = new CustomMouseAdapter();
		this.canvas.addMouseMotionListener(ml);
		this.canvas.addMouseWheelListener(ml);
		this.canvas.addMouseListener(ml);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
	    SimulationBody floor = new SimulationBody();
	    floor.addFixture(Geometry.createRectangle(20, 1));
	    floor.setMass(MassType.INFINITE);
	    this.world.addBody(floor);
	    
	    // Triangle
	    SimulationBody triangle = new SimulationBody();
	    triangle.addFixture(Geometry.createTriangle(new Vector2(0.0, 0.5), new Vector2(-0.5, -0.5), new Vector2(0.5, -0.5)));
	    triangle.translate(new Vector2(-1.0, 2.0));
	    triangle.setLinearVelocity(new Vector2(5.0, 0.0));
	    triangle.setMass(MassType.NORMAL);
	    world.addBody(triangle);

	    // Circle
	    SimulationBody circle = new SimulationBody();
	    circle.addFixture(Geometry.createCircle(0.5));
	    circle.translate(new Vector2(2.0, 2.0));
	    circle.applyForce(new Vector2(-100.0, 0.0));
	    circle.setLinearDamping(0.05);
	    circle.setMass(MassType.NORMAL);
	    world.addBody(circle);

	    // Segment
	    SimulationBody segment = new SimulationBody();
	    segment.addFixture(Geometry.createSegment(new Vector2(0.5, 0.5), new Vector2(-0.5, -0.5)));
	    segment.translate(new Vector2(1.0, 6.0));
	    segment.setMass(MassType.NORMAL);
	    world.addBody(segment);

	    // Square
	    SimulationBody square = new SimulationBody();
	    square.addFixture(Geometry.createSquare(1.0));
	    square.translate(new Vector2(0.0, 2.0));
	    square.setLinearVelocity(new Vector2(-5.0, 0.0));
	    square.setMass(MassType.NORMAL);
	    world.addBody(square);

	    // Decagon
	    SimulationBody decagon = new SimulationBody();
	    decagon.addFixture(Geometry.createUnitCirclePolygon(10, 0.5));
	    decagon.translate(new Vector2(-2.5, 2.0));
	    decagon.setAngularVelocity(Math.toRadians(-20.0));
	    decagon.setMass(MassType.NORMAL);
	    world.addBody(decagon);

	    // Capsule
	    SimulationBody capsule = new SimulationBody();
	    capsule.addFixture(Geometry.createCapsule(2, 1));
	    capsule.translate(new Vector2(0.0, 4.0));
	    capsule.setMass(MassType.NORMAL);
	    world.addBody(capsule);

	    // IsoscelesTriangle
	    SimulationBody isosceles = new SimulationBody();
	    isosceles.addFixture(Geometry.createIsoscelesTriangle(0.5, 0.5));
	    isosceles.translate(new Vector2(2, 3.5));
	    isosceles.setMass(MassType.NORMAL);
	    world.addBody(isosceles);

	    // EquilateralTriangle
	    SimulationBody equilateral = new SimulationBody();
	    equilateral.addFixture(Geometry.createEquilateralTriangle(1));
	    equilateral.translate(new Vector2(3.5, 4.5));
	    equilateral.setMass(MassType.NORMAL);
	    world.addBody(equilateral);

	    // RightTriangle
	    SimulationBody right = new SimulationBody();
	    right.addFixture(Geometry.createRightTriangle(1, 0.5));
	    right.translate(new Vector2(4.0, 3.0));
	    right.setMass(MassType.NORMAL);
	    world.addBody(right);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
	 */
	@Override
	protected void update(Graphics2D g, double elapsedTime) {
		super.update(g, elapsedTime);
		
		final double scale = this.scale;
		this.results.clear();
		
		// we are going to use a circle to do our picking
		Convex convex = Geometry.createCircle(MousePicking.PICKING_RADIUS);
		Transform transform = new Transform();
		double x = 0;
		double y = 0;
		
		// convert the point from panel space to world space
		if (this.point != null) {
			// convert the screen space point to world space
			x =  (this.point.getX() - this.canvas.getWidth() * 0.5) / scale;
			y = -(this.point.getY() - this.canvas.getHeight() * 0.5) / scale;
			this.worldPoint.set(x, y);
			
			// set the transform
			transform.translate(x, y);
			
			// detect bodies under the mouse pointer
			this.world.detect(
					convex, 
					transform,
					null,			// no, don't filter anything using the Filters 
					false,			// include sensor fixtures 
					false,			// include inactive bodies
					false,			// we don't need collision info 
					this.results);
			
			// you could also iterate over the bodies and do a point in body test
//			for (int i = 0; i < this.world.getBodyCount(); i++) {
//				Body b = this.world.getBody(i);
//				if (b.contains(new Vector2(x, y))) {
//					// record this body
//				}
//			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	protected void render(Graphics2D g, double elapsedTime) {
		// render normally
		super.render(g, elapsedTime);

		// render the picking point
		if (this.point != null) {
			AffineTransform tx = g.getTransform();
			g.translate(this.worldPoint.x * this.scale, this.worldPoint.y * this.scale);
			Graphics2DRenderer.render(g, Geometry.createCircle(MousePicking.PICKING_RADIUS), this.scale, Color.GREEN);
			g.setTransform(tx);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double, org.dyn4j.samples.SimulationBody)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime, SimulationBody body) {
		Color color = body.color;
		
		// change the color of the shape if its been picked
		for (DetectResult result : this.results) {
			SimulationBody sbr = (SimulationBody) result.getBody();
			if (sbr == body) {
				color = Color.MAGENTA;
				break;
			}
		}
		
		// draw the object
		body.render(g, this.scale, color);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		MousePicking simulation = new MousePicking();
		simulation.run();
	}
}
