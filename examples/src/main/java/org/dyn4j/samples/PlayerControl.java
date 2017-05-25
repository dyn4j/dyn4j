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

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * A moderately complex scene where one body is controlled using a motor joint
 * by another body that is directly rotated.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public class PlayerControl extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -805418642620588619L;
	
	/** Stored point for mouse moves */
	private Point movedPoint;
	
	/** Stored point for mouse drags */
	private Point draggedPoint;
	
	/**
	 * Custom mouse adapter to track mouse moves and drags.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			// get the panel-space point
			draggedPoint = e.getLocationOnScreen();
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			// get the panel-space point
			movedPoint = e.getLocationOnScreen();
		}
	}
	
	/** The controller body */
	private SimulationBody controller;
	
	/**
	 * Default constructor.
	 */
	public PlayerControl() {
		super("Player Control", 45.0);
		
		MouseAdapter ml = new CustomMouseAdapter();
		this.canvas.addMouseMotionListener(ml);
		this.canvas.addMouseWheelListener(ml);
		this.canvas.addMouseListener(ml);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
	    this.world.setGravity(World.ZERO_GRAVITY);
	    
	    // Player
	    SimulationBody body1 = new SimulationBody(Color.CYAN);
	    {// Fixture1
			Convex c = Geometry.createSquare(1.0);
			BodyFixture bf = new BodyFixture(c);
			body1.addFixture(bf);
	    }
	    body1.setLinearVelocity(new Vector2(0.0, 0.0));
	    body1.setAngularVelocity(0.0);
	    body1.setMass(MassType.NORMAL);
	    // if you let the body sleep, which is totally fine, make sure
	    // that when you change the rotation in the controller body to
	    // wake up this body.  If you are constantly changing the controller
	    // body's rotation, you might as well just do this and not worry
	    // about it.
	    body1.setAutoSleepingEnabled(false);
	    world.addBody(body1);

	    // Controller
	    controller = new SimulationBody(new Color(100, 100, 100, 50));
	    {// Fixture1
		    Convex c = Geometry.createSquare(1.0);
		    BodyFixture bf = new BodyFixture(c);
		    // make sure the controller body doesn't participate in
		    // collision.  A better way would go the route of using 
		    // collision filters, but doing this made the sample smaller.
		    bf.setSensor(true);
		    controller.addFixture(bf);
	    }
	    controller.setAngularVelocity(0.0);
	    // make sure the controller body cannot be moved by anything
	    // even though the body is mass type infinite, you can still
	    // move the body around manually by modifying its transform.
	    // This is what we do below
	    controller.setMass(MassType.INFINITE);
	    world.addBody(controller);

	    // the controller joint
	    MotorJoint joint1 = new MotorJoint(controller, body1);
	    joint1.setLinearTarget(new Vector2(0.5, 0.5));
	    joint1.setAngularTarget(Math.PI * 0.25);
	    joint1.setCorrectionFactor(0.3);
	    // allow translational changes (change this depending on how fast you
	    // want the player body to react to changes in the controller body)
	    joint1.setMaximumForce(1000.0);
	    // allow rotational changes (change this depending on how fast you want
	    // the player body to react to changes in the controller body)
	    joint1.setMaximumTorque(7.0);
	    joint1.setCollisionAllowed(false);
	    world.addJoint(joint1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
	 */
	@Override
	protected void update(Graphics2D g, double elapsedTime) {
		// make sure the controller exists
		if (this.controller != null) {
			// check if the mouse has moved
			if (this.movedPoint != null) {
				Vector2 p = this.toWorldCoordinates(this.movedPoint);
				
				// set the desired position
				double angle = this.controller.getTransform().getRotation();
				Transform tx = new Transform();
				tx.translate(p);
				tx.rotate(angle, p);
				this.controller.setTransform(tx);
				
				// clear the point
				this.movedPoint = null;
			}
			
			// check if the mouse has dragged
			if (this.draggedPoint != null) {
				Vector2 v = this.toWorldCoordinates(this.draggedPoint);
				Vector2 c = this.controller.getWorldCenter();
				
				// set the desired rotation
				Vector2 xAxis = new Vector2(1.0, 0.0);
				double angle = xAxis.getAngleBetween(c.to(v));
				// account for negative angles
				if (angle < 0) {
					angle += 2.0 * Math.PI;
				}
				this.controller.getTransform().setRotation(angle);
				
				// clear the point
				this.draggedPoint = null;
			}
		}
		
		super.update(g, elapsedTime);
	}
	
	/**
	 * Converts from screen space to world space.
	 * @param point the screen space point
	 * @return {@link Vector2}
	 */
	private Vector2 toWorldCoordinates(Point point) {
		double x =  (point.getX() - this.canvas.getWidth() / 2.0) / this.scale;
		double y = -(point.getY() - this.canvas.getHeight() / 2.0) / this.scale;
		return new Vector2(x, y);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		PlayerControl simulation = new PlayerControl();
		simulation.run();
	}
}
