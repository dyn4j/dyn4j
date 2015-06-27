/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.tests;

import com.jogamp.opengl.GL2;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;

/**
 * Compiled test for the shiftCoordinates method.
 * @author William Bittle
 * @version 1.0.6
 * @since 1.0.2
 */
public class CoordinateShift extends CompiledSimulation {
	/** The current origin */
	private Vector2 origin = new Vector2(1.0, 1.0);
	
	/** The time since the last setMass */
	private double time;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#initialize()
	 */
	@Override
	public void initialize() {
		this.world.setUserData("Coordinate Shift");
		
		// create the floor
		SandboxBody floor = new SandboxBody();
		floor.addFixture(Geometry.createRectangle(30.0, 1.0));
		floor.setMass(Mass.Type.INFINITE);
		floor.setUserData("Floor");
		this.world.addBody(floor);
		
		// create a triangle object
		SandboxBody triangle = new SandboxBody();
		triangle.addFixture(Geometry.createTriangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5)));
		triangle.setMass(Mass.Type.NORMAL);
		triangle.translate(-1.0, 2.0);
		triangle.setUserData("Triangle");
		this.world.addBody(triangle);
		
		// create a circle
		SandboxBody circle = new SandboxBody();
		circle.addFixture(Geometry.createCircle(1.0));
		circle.setMass(Mass.Type.NORMAL);
		circle.translate(2.0, 2.0);
		circle.setUserData("Circle");
		this.world.addBody(circle);
		
		// create a line segment
		SandboxBody segment = new SandboxBody();
		segment.addFixture(Geometry.createSegment(new Vector2(0.5, 0.5), new Vector2(-0.5, -0.5)));
		segment.setMass(Mass.Type.NORMAL);
		segment.translate(1.0, 6.0);
		segment.setUserData("Segment");
		this.world.addBody(segment);
		
		// try a rectangle
		SandboxBody rectangle = new SandboxBody();
		rectangle.addFixture(Geometry.createRectangle(1.0, 1.0));
		rectangle.setMass(Mass.Type.NORMAL);
		rectangle.translate(0.0, 2.0);
		rectangle.setUserData("Rectangle");
		this.world.addBody(rectangle);
		
		// try a polygon with lots of vertices
		SandboxBody polygon = new SandboxBody();
		polygon.addFixture(Geometry.createUnitCirclePolygon(10, 1.0));
		polygon.setMass(Mass.Type.NORMAL);
		polygon.translate(-2.5, 2.0);
		polygon.setUserData("Polygon");
		this.world.addBody(polygon);
		
		// try a compound object (Capsule)
		BodyFixture c1Fixture = new BodyFixture(Geometry.createCircle(0.5));
		c1Fixture.setDensity(0.5);
		BodyFixture c2Fixture = new BodyFixture(Geometry.createCircle(0.5));
		c2Fixture.setDensity(0.5);
		// translate the circles in local coordinates
		c1Fixture.getShape().translate(-1.0, 0.0);
		c2Fixture.getShape().translate(1.0, 0.0);
		SandboxBody capsule = new SandboxBody();
		capsule.addFixture(c1Fixture);
		capsule.addFixture(c2Fixture);
		capsule.addFixture(Geometry.createRectangle(2.0, 1.0));
		capsule.setMass(Mass.Type.NORMAL);
		capsule.translate(0.0, 2.0);
		capsule.setUserData("Capsule");
		this.world.addBody(capsule);
		
		// create mouse joint
		{
			SandboxBody box = new SandboxBody();
			box.addFixture(Geometry.createRectangle(1.0, 1.0));
			box.setMass(Mass.Type.NORMAL);
			box.translate(-4.0, 2.5);
			box.setUserData("MouseJoint Box");
			
			this.world.addBody(box);
			
			MouseJoint mj = new MouseJoint(box, new Vector2(-4.0, 2.75), 5.0, 0.3, 100);
			// pin it to a arbitrary point
			mj.setTarget(new Vector2(-4.0, 4.0));
			mj.setUserData("MouseJoint");
			
			this.world.addJoint(mj);
		}
		
		// create pulley joint
		{
			double x = 4.0;
			double y = 4.0;
			double w = 0.5;
			double h = 0.5;
			double l = 3.0;
			
			// create a reusable rectangle
			Rectangle r = new Rectangle(w, h);
			
			SandboxBody obj1 = new SandboxBody();
			obj1.addFixture(r);
			obj1.setMass(Mass.Type.NORMAL);
			obj1.translate(-x, y);
			obj1.setUserData("PulleyJoint Box1");
			
			SandboxBody obj2 = new SandboxBody();
			obj2.addFixture(r);
			obj2.setMass(Mass.Type.NORMAL);
			obj2.translate(x, y);
			obj2.setUserData("PulleyJoint Box2");
			
			this.world.addBody(obj1);
			this.world.addBody(obj2);
			
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
			pulleyJoint.setUserData("PulleyJoint");
			
			// defaults to collision not allowed
			this.world.addJoint(pulleyJoint);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#setMass(double, boolean)
	 */
	@Override
	public void update(double elapsedTime, boolean stepped) {
		this.time += elapsedTime;
		// after one second reverse the target
		if (this.time > 0.75) {
			Vector2 c = this.origin.copy();
			c.negate();
			this.origin = c;
			
			this.world.shift(c);
			
			this.time = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#render(com.jogamp.opengl.GL2)
	 */
	@Override
	public void render(GL2 gl) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#reset()
	 */
	@Override
	public void reset() {
		this.time = 0;
		// remove everything from the world
		this.world.removeAllBodiesAndJoints();
		// add it all back
		this.initialize();
	}
}
