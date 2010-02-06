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
		List<Convex> ss = new ArrayList<Convex>(5);
		ss.add(s1);
		ss.add(s2);
		ss.add(s3);
		ss.add(s4);
		ss.add(s5);
		Vector center = Geometry.getAverageCenter(s1.getCenter(), s2.getCenter(), s3.getCenter(), s4.getCenter(), s5.getCenter());
		Mass m = Mass.create(center);
		Entity e1 = new Entity(ss, m);
		e1.translate(0.0, -2.0);
		this.world.add(e1);
		
		// temp variables
		List<Convex> shapes = null;
		Mass mass = null;

		// create a triangle object
		Triangle t = new Triangle(new Vector(0.0, 0.5), new Vector(-0.5, -0.5), new Vector(0.5, -0.5));
		shapes = new ArrayList<Convex>(1);
		shapes.add(t);
		mass = Mass.create(t, 1.0);
		Entity obj1 = new Entity(shapes, mass);
		obj1.translate(0.0, 2.0);
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

		// try a thin rectangle
		Rectangle r = new Rectangle(2.0, 0.1);
		shapes = new ArrayList<Convex>(1);
		shapes.add(r);
		mass = Mass.create(r, 2.0);
		Entity obj4 = new Entity(shapes, mass);
		obj4.translate(0.0, 3.0);
		obj4.rotate(Math.toRadians(10.0));
		this.world.add(obj4);
		
		// try a segment (shouldn't work)
		Segment s = new Segment(new Vector(0.5, 0.0), new Vector(-0.5, 0.0));
		shapes = new ArrayList<Convex>(1);
		shapes.add(s);
		mass = Mass.create(s, 1.0);
		Entity obj6 = new Entity(shapes, mass);
		obj6.translate(-5.0, 4.0);
		this.world.add(obj6);
		
		// try a polygon with lots of vertices
		Vector[] verts = new Vector[10];
		double angle = 2.0 * Math.PI / 10.0;
		for (int i = 9; i >= 0; i--) {
			verts[i] = new Vector(Math.cos(angle * i), Math.sin(angle * i));
		}
		Polygon p = new Polygon(verts);
		shapes = new ArrayList<Convex>(1);
		shapes.add(p);
		mass = Mass.create(p, 1.0);
		Entity obj5 = new Entity(shapes, mass);
		obj5.translate(-2.0, 5.0);
		// set the angular velocity
		obj5.setAv(Math.toRadians(20.0));
		this.world.add(obj5);
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
