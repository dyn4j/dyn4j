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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

/**
 * A simple scene showing how to capture mouse input and create
 * bodies dynamically.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public class MouseInteraction extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -1366264828445805140L;

	/** A point for tracking the mouse click */
	private Point point;
	
	/**
	 * A custom mouse adapter for listening for mouse clicks.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class CustomMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			// store the mouse click postion for use later
			point = new Point(e.getX(), e.getY());
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			point = null;
		}
	}

	/**
	 * Default constructor.
	 */
	public MouseInteraction() {
		super("Mouse Interaction", 32.0);
		
		MouseAdapter ml = new CustomMouseAdapter();
		this.canvas.addMouseMotionListener(ml);
		this.canvas.addMouseWheelListener(ml);
		this.canvas.addMouseListener(ml);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		SimulationBody floor = new SimulationBody();
	    floor.addFixture(Geometry.createRectangle(20, 1));
	    floor.setMass(MassType.INFINITE);
	    this.world.addBody(floor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
	 */
	@Override
	protected void update(Graphics2D g, double elapsedTime) {
		
		// see if the user clicked
		if (this.point != null) {
			// convert from screen space to world space coordinates
			double x =  (this.point.getX() - this.canvas.getWidth() / 2.0) / this.scale;
			double y = -(this.point.getY() - this.canvas.getHeight() / 2.0) / this.scale;
			
			// create a new body
			SimulationBody no = new SimulationBody();
			no.addFixture(Geometry.createSquare(0.5));
			no.translate(x, y);
			no.setMass(MassType.NORMAL);
			this.world.addBody(no);
			
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
		MouseInteraction simulation = new MouseInteraction();
		simulation.run();
	}
}
