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

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests a floor/terrain created by a set of line segments.
 * @author William Bittle
 */
public class Terrain extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests terrain created by segments.  This test shows a terrain " +
			   "created by line segments.  Notice Segment vs. Segment doesn't " +
			   "work.  This is a result of infinitely thin shapes.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
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
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the terrain
		Segment s1 = new Segment(new Vector(6.0, 4.0), new Vector(3.0, 2.0));
		Segment s2 = new Segment(new Vector(3.0, 2.0), new Vector(0.0, -1.0));
		Segment s3 = new Segment(new Vector(0.0, -1.0), new Vector(-2.0, 3.0));
		Segment s4 = new Segment(new Vector(-2.0, 3.0), new Vector(-4.0, 0.0));
		Segment s5 = new Segment(new Vector(-4.0, 0.0), new Vector(-6.0, 1.0));
		Entity terrain = new Entity();
		terrain.addShape(s1, Mass.create(s1));
		terrain.addShape(s2, Mass.create(s2));
		terrain.addShape(s3, Mass.create(s3));
		terrain.addShape(s4, Mass.create(s4));
		terrain.addShape(s5, Mass.create(s5));
		terrain.setMassFromShapes(Mass.Type.INFINITE);
		terrain.translate(0.0, -2.0);
		this.world.add(terrain);
		
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector(0.0, 0.5), 
				new Vector(-0.5, -0.5), 
				new Vector(0.5, -0.5));
		Entity triangle = new Entity();
		triangle.addShape(triShape, Mass.create(triShape));
		triangle.setMassFromShapes();
		triangle.translate(0.0, 2.0);
		// test having a velocity
		triangle.getV().add(5.0, 0.0);
		this.world.add(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		Entity circle = new Entity();
		circle.addShape(cirShape, Mass.create(cirShape));
		circle.setMassFromShapes();
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.apply(new Vector(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.add(circle);

		// try a thin rectangle
		Rectangle rectShape = new Rectangle(2.0, 0.1);
		Entity rectangle = new Entity();
		rectangle.addShape(rectShape, Mass.create(rectShape));
		rectangle.setMassFromShapes();
		rectangle.translate(0.0, 3.0);
		rectangle.rotate(Math.toRadians(10.0));
		this.world.add(rectangle);
		
		// try a segment (shouldn't work)
		Segment segShape = new Segment(new Vector(0.5, 0.0), new Vector(-0.5, 0.0));
		Entity segment = new Entity();
		segment.addShape(segShape, Mass.create(segShape));
		segment.setMassFromShapes();
		segment.translate(-5.0, 4.0);
		this.world.add(segment);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.getUnitCirclePolygon(10, 1.0);
		Entity polygon = new Entity();
		polygon.addShape(polyShape, Mass.create(polyShape));
		polygon.setMassFromShapes();
		polygon.translate(-2.0, 5.0);
		// set the angular velocity
		polygon.setAv(Math.toRadians(20.0));
		this.world.add(polygon);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the camera offset
		this.offset.zero();
	}
}
