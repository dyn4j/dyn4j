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

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.BodyFixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests a floor/terrain created by a set of line segments.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class Terrain extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Terrain";
	}
	
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
		
		// create the world
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(16.0, 15.0));
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
		Segment s1 = new Segment(new Vector2(6.0, 4.0), new Vector2(3.0, 2.0));
		Segment s2 = new Segment(new Vector2(3.0, 2.0), new Vector2(0.0, -1.0));
		Segment s3 = new Segment(new Vector2(0.0, -1.0), new Vector2(-2.0, 3.0));
		Segment s4 = new Segment(new Vector2(-2.0, 3.0), new Vector2(-4.0, 0.0));
		Segment s5 = new Segment(new Vector2(-4.0, 0.0), new Vector2(-6.0, 1.0));
		Entity terrain = new Entity();
		terrain.addFixture(new BodyFixture(s1));
		terrain.addFixture(new BodyFixture(s2));
		terrain.addFixture(new BodyFixture(s3));
		terrain.addFixture(new BodyFixture(s4));
		terrain.addFixture(new BodyFixture(s5));
		terrain.setMass(Mass.Type.INFINITE);
		terrain.translate(0.0, -2.0);
		this.world.add(terrain);
		
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5));
		Entity triangle = new Entity();
		triangle.addFixture(new BodyFixture(triShape));
		triangle.setMass();
		triangle.translate(0.0, 2.0);
		// test having a velocity
		triangle.getVelocity().set(5.0, 0.0);
		this.world.add(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		Entity circle = new Entity();
		circle.addFixture(new BodyFixture(cirShape));
		circle.setMass();
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.apply(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.add(circle);

		// try a thin rectangle
		Rectangle rectShape = new Rectangle(2.0, 0.1);
		Entity rectangle = new Entity();
		rectangle.addFixture(new BodyFixture(rectShape));
		rectangle.setMass();
		rectangle.translate(0.0, 3.0);
		rectangle.rotate(Math.toRadians(10.0));
		this.world.add(rectangle);
		
		// try a segment (shouldn't work)
		Segment segShape = new Segment(new Vector2(0.5, 0.0), new Vector2(-0.5, 0.0));
		Entity segment = new Entity();
		segment.addFixture(new BodyFixture(segShape));
		segment.setMass();
		segment.translate(-5.0, 4.0);
		this.world.add(segment);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		Entity polygon = new Entity();
		polygon.addFixture(new BodyFixture(polyShape));
		polygon.setMass();
		polygon.translate(-2.0, 5.0);
		// set the angular velocity
		polygon.setAngularVelocity(Math.toRadians(20.0));
		this.world.add(polygon);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, -0.2);
	}
}
