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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * A simple scene with a few shapes and a raycast being performed.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.0.0
 */
public class Raycast extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 1462952703366297615L;

	/**
	 * Default constructor.
	 */
	public Raycast() {
		super("Raycast", 45.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 * <p>
	 * Basically the same shapes from the Shapes test in
	 * the TestBed.
	 */
	protected void initializeWorld() {
	    this.world.setGravity(World.ZERO_GRAVITY);

	    // Triangle
	    Body triangle = new SimulationBody();
	    triangle.addFixture(Geometry.createTriangle(new Vector2(0.0, 0.5), new Vector2(-0.5, -0.5), new Vector2(0.5, -0.5)));
	    triangle.translate(new Vector2(2.5, 3));
	    triangle.setMass(MassType.INFINITE);
	    this.world.addBody(triangle);

	    // Circle
	    Body circle = new SimulationBody();
	    circle.addFixture(Geometry.createCircle(0.5));
	    circle.translate(new Vector2(3.2, 3.5));
	    circle.setMass(MassType.INFINITE);
	    this.world.addBody(circle);

	    // Segment
	    Body segment = new SimulationBody();
	    segment.addFixture(Geometry.createSegment(new Vector2(0.5, 0.5), new Vector2(-0.5, 0)));
	    segment.translate(new Vector2(4.2, 4));
	    segment.setMass(MassType.INFINITE);
	    this.world.addBody(segment);

	    // Square
	    Body square = new SimulationBody();
	    square.addFixture(Geometry.createSquare(1.0));
	    square.translate(new Vector2(1.5, 2.0));
	    square.setMass(MassType.INFINITE);
	    this.world.addBody(square);

	    // Polygon
	    Body polygon = new SimulationBody();
	    polygon.addFixture(Geometry.createUnitCirclePolygon(5, 0.5));
	    polygon.translate(new Vector2(0.5, 0));
	    polygon.setMass(MassType.INFINITE);
	    this.world.addBody(polygon);

	    // Capsule
	    Body capsule = new SimulationBody();
	    capsule.addFixture(Geometry.createCapsule(2, 1));
	    capsule.translate(new Vector2(4.5, 5.0));
	    capsule.setMass(MassType.INFINITE);
	    this.world.addBody(capsule);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	protected void render(Graphics2D g, double elapsedTime) {
		super.render(g, elapsedTime);
		
		final double r = 4.0;
		final double scale = this.scale;
		final double length = 100;
		
		Ray ray = new Ray(Math.toRadians(45.0));
		g.setColor(Color.RED);
		g.draw(new Line2D.Double(
				ray.getStart().x * scale, 
				ray.getStart().y * scale, 
				ray.getDirectionVector().x * length * scale, 
				ray.getDirectionVector().y * length * scale));
		
		List<RaycastResult> results = new ArrayList<RaycastResult>();
		if (this.world.raycast(ray, length, true, true, results)) {
			for (RaycastResult result : results) {
				// draw the intersection
				Vector2 point = result.getRaycast().getPoint();
				g.setColor(Color.GREEN);
				g.fill(new Ellipse2D.Double(
						point.x * scale - r * 0.5, 
						point.y * scale - r * 0.5, 
						r, 
						r));
			}
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Raycast simulation = new Raycast();
		simulation.run();
	}
}
