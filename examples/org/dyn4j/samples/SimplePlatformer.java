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

import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

/**
 * A simple scene of a circle that is controlled by the left and
 * right arrow keys that is moved by applying torques and forces.
 * @author William Bittle
 * @since 3.2.1
 * @version 3.2.0
 */
public class SimplePlatformer extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -313391186714427055L;

	/**
	 * Default constructor for the window
	 */
	public SimplePlatformer() {
		super("Simple Platformer", 32.0);
		
		KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
	}
	
	private SimulationBody wheel;
	
	private final AtomicBoolean leftPressed = new AtomicBoolean(false);
	private final AtomicBoolean rightPressed = new AtomicBoolean(false);
	
	/**
	 * Custom key adapter to listen for key events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private class CustomKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					leftPressed.set(true);
					break;
				case KeyEvent.VK_RIGHT:
					rightPressed.set(true);
					break;
			}
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					leftPressed.set(false);
					break;
				case KeyEvent.VK_RIGHT:
					rightPressed.set(false);
					break;
			}
		}
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// the floor
		SimulationBody floor = new SimulationBody();
		floor.addFixture(Geometry.createRectangle(50.0, 0.2));
		floor.setMass(MassType.INFINITE);
		floor.translate(0, -3);
		this.world.addBody(floor);
		
		// some obstacles
		final int n = 5;
		for (int i = 0; i < n; i++) {
			SimulationBody sb = new SimulationBody();
			double w = 1.0;
			double h = Math.random() * 0.3 + 0.1;
			sb.addFixture(Geometry.createIsoscelesTriangle(w, h));
			sb.translate((Math.random() > 0.5 ? -1 : 1) * Math.random() * 5.0, h * 0.5 - 2.9);
			sb.setMass(MassType.INFINITE);
			this.world.addBody(sb);
		}
		
		// some bounding shapes
		SimulationBody right = new SimulationBody();
		right.addFixture(Geometry.createRectangle(0.2, 20));
		right.setMass(MassType.INFINITE);
		right.translate(10, 7);
		this.world.addBody(right);
		SimulationBody left = new SimulationBody();
		left.addFixture(Geometry.createRectangle(0.2, 20));
		left.setMass(MassType.INFINITE);
		left.translate(-10, 7);
		this.world.addBody(left);
		
		// the wheel
		wheel = new SimulationBody();
		// NOTE: lots of friction to simulate a sticky tire
		wheel.addFixture(Geometry.createCircle(0.5), 1.0, 20.0, 0.1);
		wheel.setMass(MassType.NORMAL);
		this.world.addBody(wheel);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#update(java.awt.Graphics2D, double)
	 */
	@Override
	protected void update(Graphics2D g, double elapsedTime) {
		// apply a torque based on key input
		if (this.leftPressed.get()) {
			wheel.applyTorque(Math.PI / 2);
		}
		if (this.rightPressed.get()) {
			wheel.applyTorque(-Math.PI / 2);
		}
		
		super.update(g, elapsedTime);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SimplePlatformer simulation = new SimplePlatformer();
		simulation.run();
	}
}
