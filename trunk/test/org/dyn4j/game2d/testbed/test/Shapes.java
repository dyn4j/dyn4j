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
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests circle and polygon shapes in collision deteciton and resolution.
 * @author William Bittle
 */
public class Shapes extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the various shapes supported.  This test ensures that all " +
			   "shapes supported are caught by collision detection and resolved accordingly.";
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
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new Fixture(floorRect));
		floor.setMassFromShapes(Mass.Type.INFINITE);
		this.world.add(floor);
		
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector(0.0, 0.5), 
				new Vector(-0.5, -0.5), 
				new Vector(0.5, -0.5));
		Entity triangle = new Entity();
		triangle.addFixture(new Fixture(triShape));
		triangle.setMassFromShapes();
		triangle.translate(-1.0, 2.0);
		// test having a velocity
		triangle.getVelocity().set(5.0, 0.0);
		this.world.add(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		Entity circle = new Entity();
		circle.addFixture(new Fixture(cirShape));
		circle.setMassFromShapes();
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.apply(new Vector(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.add(circle);
		
		// create a line segment
		Segment segShape = new Segment(new Vector(0.5, 0.5), new Vector(-0.5, -0.5));
		Entity segment1 = new Entity();
		segment1.addFixture(new Fixture(segShape));
		segment1.setMassFromShapes();
		segment1.translate(1.0, 6.0);
		this.world.add(segment1);
		
		// try a segment parallel to the floor
		Entity segment2 = new Entity();
		segment2.addFixture(new Fixture(segShape));
		segment2.setMassFromShapes();
		segment2.rotateAboutCenter(Math.toRadians(-45.0));
		segment2.translate(-4.5, 1.0);
		this.world.add(segment2);
		
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		Entity rectangle = new Entity();
		rectangle.addFixture(new Fixture(rectShape));
		rectangle.setMassFromShapes();
		rectangle.translate(0.0, 2.0);
		rectangle.getVelocity().set(-5.0, 0.0);
		this.world.add(rectangle);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		Entity polygon = new Entity();
		polygon.addFixture(new Fixture(polyShape));
		polygon.setMassFromShapes();
		polygon.translate(-2.5, 2.0);
		// set the angular velocity
		polygon.setAngularVelocity(Math.toRadians(-20.0));
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
		capsule.setMassFromShapes();
		capsule.translate(0.0, 4.0);
		this.world.add(capsule);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, -2.0);
	}
}
