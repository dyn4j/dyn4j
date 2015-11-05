/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;

/**
 * A simple scene showing how to drag an object around the scene
 * with the mouse using a MotorJoint.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 * @see PlayerControl
 */
public class MouseDrag extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -4132057742762298086L;

	/** The controller body */
	private SimulationBody controller;
	
	/** The current mouse drag point */
	private Point point;
	
	/**
	 * A custom mouse adapter to track mouse drag events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			// just create a new point and store it locally
			// later, on the next update we'll check for it
			point = new Point(e.getX(), e.getY());
			super.mouseDragged(e);
		}
	}

	/**
	 * Default constructor for the window
	 */
	public MouseDrag() {
		super("Mouse Drag", 32.0);
		
		// setup the mouse listening
		MouseAdapter ml = new CustomMouseAdapter();
		this.canvas.addMouseMotionListener(ml);
		this.canvas.addMouseWheelListener(ml);
		this.canvas.addMouseListener(ml);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// no gravity please
		this.world.setGravity(World.ZERO_GRAVITY);
		
		// player control setup
		
		this.controller = new SimulationBody(Color.CYAN);
	    this.controller.addFixture(Geometry.createCircle(0.5));
	    this.controller.setMass(MassType.INFINITE);
	    this.controller.setAutoSleepingEnabled(false);
	    this.world.addBody(this.controller);
	    
	    SimulationBody player = new SimulationBody(Color.GREEN);
	    player.addFixture(Geometry.createCircle(0.5));
	    player.setMass(MassType.NORMAL);
	    player.setAutoSleepingEnabled(false);
	    this.world.addBody(player);
	    
	    MotorJoint control = new MotorJoint(player, this.controller);
	    control.setCollisionAllowed(false);
	    control.setMaximumForce(1000.0);
	    control.setMaximumTorque(1000.0);
	    this.world.addJoint(control);
	    
	    // obstacles
	    
	    SimulationBody wall = new SimulationBody();
	    wall.addFixture(Geometry.createRectangle(1, 10));
	    wall.setMass(MassType.INFINITE);
	    wall.translate(2, 0);
	    this.world.addBody(wall);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
	 */
	@Override
	protected void update(Graphics2D g, double elapsedTime) {
		
		// check if the mouse has moved/dragged
		if (this.point != null) {
			// convert from screen space to world space
			double x =  (this.point.getX() - this.canvas.getWidth() / 2.0) / this.scale;
			double y = -(this.point.getY() - this.canvas.getHeight() / 2.0) / this.scale;
			
			// reset the transform of the controller body
			Transform tx = new Transform();
			tx.translate(x, y);
			this.controller.setTransform(tx);
			
			// clear the point
			this.point = null;
		}
		
		super.update(g, elapsedTime);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		MouseDrag simulation = new MouseDrag();
		simulation.run();
	}
}
