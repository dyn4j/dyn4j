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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
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
		Rectangle rect2 = new Rectangle(15.0, 1.0);
		List<Convex> geometry2 = new ArrayList<Convex>();
		geometry2.add(rect2);
		Mass mass2 = Mass.create(rect2.getCenter());
		
		Entity obj0 = new Entity(geometry2, mass2);
		this.world.add(obj0);
		
		// temp variables
		List<Convex> shapes = null;
		Mass mass = null;
		
		// create a triangle object
		Triangle t = new Triangle(new Vector(0.0, 0.5), new Vector(-0.5, -0.5), new Vector(0.5, -0.5));
		shapes = new ArrayList<Convex>(1);
		shapes.add(t);
		mass = Mass.create(t, 1.0);
		Entity obj1 = new Entity(shapes, mass);
		obj1.translate(-1.0, 2.0);
		// test having a velocity
		obj1.getV().add(5.0, 0.0);
		this.world.add(obj1);
		
		// create a circle
		Circle c = new Circle(0.5);
		shapes = new ArrayList<Convex>(1);
		shapes.add(c);
		mass = Mass.create(c, 1.0);
		Entity obj2 = new Entity(shapes, mass);
		obj2.translate(2.0, 2.0);
		// test adding some force
		obj2.apply(new Vector(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		obj2.setLinearDamping(0.05);
		this.world.add(obj2);
		
		// create a line segment
		Segment s = new Segment(new Vector(0.5, 0.5), new Vector(-0.5, -0.5));
		shapes = new ArrayList<Convex>(1);
		shapes.add(s);
		mass = Mass.create(s, 1.0);
		Entity obj3 = new Entity(shapes, mass);
		obj3.translate(1.0, 6.0);
		this.world.add(obj3);
		
		// try a segment parallel to the floor
		Segment s2 = new Segment(new Vector(-0.5, 0.0), new Vector(0.5, 0.0));
		shapes = new ArrayList<Convex>(1);
		shapes.add(s2);
		mass = Mass.create(s2, 1.0);
		Entity obj4 = new Entity(shapes, mass);
		obj4.translate(-4.5, 1.0);
		this.world.add(obj4);
		
		// try a rectangle
		Rectangle r = new Rectangle(1.0, 1.0);
		shapes = new ArrayList<Convex>(1);
		shapes.add(r);
		mass = Mass.create(r, 1.0);
		Entity obj6 = new Entity(shapes, mass);
		obj6.translate(0.0, 2.0);
		obj6.getV().set(-5.0, 0.0);
		this.world.add(obj6);
		
		// try a polygon with lots of vertices
		Polygon p = Geometry.getUnitCirclePolygon(10, 1.0);
		shapes = new ArrayList<Convex>(1);
		shapes.add(p);
		mass = Mass.create(p, 1.0);
		Entity obj5 = new Entity(shapes, mass);
		obj5.translate(-2.5, 2.0);
		// set the angular velocity
		obj5.setAv(Math.toRadians(-20.0));
		this.world.add(obj5);
		
		// try a compound object (Capsule)
		Circle c1 = new Circle(0.5);
		Circle c2 = new Circle(0.5);
		Rectangle rm = new Rectangle(2.0, 1.0);
		c1.translate(-1.0, 0.0);
		c2.translate(1.0, 0.0);
		shapes = new ArrayList<Convex>(3);
		shapes.add(c1);
		shapes.add(c2);
		shapes.add(rm);
		Mass cm1 = Mass.create(c1, 0.5);
		Mass cm2 = Mass.create(c2, 0.5);
		Mass rmm = Mass.create(rm, 1.0);
		Mass total = Mass.create(cm1, cm2, rmm);
		Entity obj10 = new Entity(shapes, total);
		obj10.translate(0.0, 4.0);
		this.world.add(obj10);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.zero();
	}
}
