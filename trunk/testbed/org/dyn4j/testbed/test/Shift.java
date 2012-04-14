/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.testbed.test;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.testbed.ContactCounter;
import org.dyn4j.testbed.Entity;
import org.dyn4j.testbed.Test;
import org.dyn4j.testbed.input.Input;
import org.dyn4j.testbed.input.Input.Hold;
import org.dyn4j.testbed.input.Keyboard;
import org.dyn4j.testbed.input.Mouse;

/**
 * Tests the shifting of the entire world by a given amount.
 * @author William Bittle
 * @version 3.1.0
 * @since 3.1.0
 */
public class Shift extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Shift";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests shifting the origin.\n\nClick anywhere in the world to shift the world.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// create the world
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(20, 30));
		bounds.translate(0.0, 3.0);
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the floor
		Rectangle floorRect = new Rectangle(30.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		floor.translate(0.0, -3.0);
		this.world.add(floor);
		
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5));
		Entity triangle = new Entity();
		triangle.addFixture(new BodyFixture(triShape));
		triangle.setMass();
		triangle.translate(-1.0, -1.0);
		// test having a velocity
		triangle.getVelocity().set(5.0, 0.0);
		this.world.add(triangle);
		
		// create a circle
		Circle cirShape = new Circle(0.5);
		Entity circle = new Entity();
		circle.addFixture(new BodyFixture(cirShape));
		circle.setMass();
		circle.translate(2.0, -1.0);
		// test adding some force
		circle.apply(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.add(circle);
		
		// create a line segment
		Segment segShape = new Segment(new Vector2(0.5, 0.5), new Vector2(-0.5, -0.5));
		Entity segment1 = new Entity();
		segment1.addFixture(new BodyFixture(segShape));
		segment1.setMass();
		segment1.translate(1.0, 3.0);
		this.world.add(segment1);
		
		// try a segment parallel to the floor
		Entity segment2 = new Entity();
		segment2.addFixture(new BodyFixture(segShape));
		segment2.setMass();
		segment2.rotateAboutCenter(Math.toRadians(-45.0));
		segment2.translate(-4.5, -2.0);
		this.world.add(segment2);
		
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		Entity rectangle = new Entity();
		rectangle.addFixture(new BodyFixture(rectShape));
		rectangle.setMass();
		rectangle.translate(0.0, -1.0);
		rectangle.getVelocity().set(-5.0, 0.0);
		this.world.add(rectangle);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		Entity polygon = new Entity();
		polygon.addFixture(new BodyFixture(polyShape));
		polygon.setMass();
		polygon.translate(-2.5, -1.0);
		// set the angular velocity
		polygon.setAngularVelocity(Math.toRadians(-20.0));
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
		capsule.setMass();
		capsule.translate(0.0, -1.0);
		this.world.add(capsule);
		
		Entity issTri = new Entity();
		issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0));
		issTri.setMass();
		issTri.translate(2.0, 0.0);
		this.world.add(issTri);
		
		Entity equTri = new Entity();
		equTri.addFixture(Geometry.createEquilateralTriangle(2.0));
		equTri.setMass();
		equTri.translate(3.0, 0.0);
		this.world.add(equTri);
		
		Entity rightTri = new Entity();
		rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0));
		rightTri.setMass();
		rightTri.translate(4.0, 0.0);
		this.world.add(rightTri);
		
		// create mouse joint
		{
			// create a reusable rectangle
			Rectangle r = new Rectangle(1.0, 1.0);
			
			Entity top = new Entity();
			top.addFixture(r);
			top.setMass();
			top.translate(-4.0, -0.5);
			
			this.world.add(top);
			
			MouseJoint mj = new MouseJoint(top, new Vector2(-4.0, 0.25), 5.0, 0.3, 100);
			// pin it to a random point
			mj.setTarget(new Vector2(-4.0, 1.0));
			
			this.world.add(mj);
		}
		
		// create pulley joint
		{
			double x = 4.0;
			double y = 1.0;
			double w = 0.5;
			double h = 0.5;
			double l = 3.0;
			
			// create a reusable rectangle
			Rectangle r = new Rectangle(w, h);
			
			Entity obj1 = new Entity();
			BodyFixture f1 = obj1.addFixture(r);
			f1.setDensity(5.0);
			obj1.setMass();
			obj1.translate(-x, y);
			
			Entity obj2 = new Entity();
			BodyFixture f2 = obj2.addFixture(r);
			f2.setDensity(5.0);
			obj2.setMass();
			obj2.translate(x, y);
			
			this.world.add(obj1);
			this.world.add(obj2);
			
			// compute the joint points
			Vector2 bodyAnchor1 = new Vector2(-x, y + h);
			Vector2 bodyAnchor2 = new Vector2(x, y + h);
			Vector2 pulleyAnchor1 = new Vector2(-x, y + h + l);
			Vector2 pulleyAnchor2 = new Vector2(x, y + h + l);
			
			// create the joint
			PulleyJoint pulleyJoint = new PulleyJoint(obj1, obj2, pulleyAnchor1, pulleyAnchor2, bodyAnchor1, bodyAnchor2);
			// emulate a block-and-tackle
			pulleyJoint.setRatio(2.0);
			// allow them to collide
			pulleyJoint.setCollisionAllowed(true);
			
			// defaults to collision not allowed
			this.world.add(pulleyJoint);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 16.0;
		// set the camera offset
		this.offset.set(0.0, -2.0);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(20, 30));
		bounds.translate(0.0, 3.0);
		this.world.setBounds(bounds);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#getControls()
	 */
	@Override
	public String[][] getControls() {
		return new String[][] {
				{"Shift Origin", "<html>Shifts the coordinates of the world.<br />All bodies and joints are translated by -(mouse position).</html>", "<html><span style='color: blue;'>Right Mouse Button</span></html>"}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#initializeInput(org.dyn4j.testbed.input.Keyboard, org.dyn4j.testbed.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		// we also need to override the TestBed's registration of the right mouse button
		// so that we don't allow holding of the button
		mouse.remove(MouseEvent.BUTTON3);
		mouse.add(new Input(MouseEvent.BUTTON3, Hold.NO_HOLD));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.testbed.Test#poll(org.dyn4j.testbed.input.Keyboard, org.dyn4j.testbed.input.Mouse)
	 */
	@Override
	public void poll(Keyboard keyboard, Mouse mouse) {
		super.poll(keyboard, mouse);
		
		// look for the left mouse button
		if (mouse.isPressed(MouseEvent.BUTTON3)) {
			// add the point to the list of points
			Point p = mouse.getRelativeLocation();
			// convert to world coordinates
			Vector2 v = this.screenToWorld(p.x, p.y);
			this.world.shiftCoordinates(v.getNegative());
		}
	}
}
