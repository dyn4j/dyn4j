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

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

/**
 * A simple scene of a bowling ball bouncing on the floor.
 * <p>
 * Primarily used to illustrate the computation of the mass and size
 * of the ball.  See the {@link BowlingBall#initializeWorld()} method.
 * @author William Bittle
 * @since 3.2.1
 * @version 3.2.0
 */
public class BowlingBall extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = 6102930425312889302L;

	/**
	 * Default constructor.
	 */
	public BowlingBall() {
		super("Bowling Ball", 128.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		SimulationBody floor = new SimulationBody();
		floor.addFixture(Geometry.createRectangle(15.0, 0.2));
		floor.setMass(MassType.INFINITE);
		this.world.addBody(floor);
		
		SimulationBody bowlingBall = new SimulationBody();
		BodyFixture fixture = new BodyFixture(Geometry.createCircle(0.109));
		fixture.setDensity(194.82);
		fixture.setRestitution(0.5);
		bowlingBall.addFixture(fixture);
		bowlingBall.setMass(MassType.NORMAL);
		bowlingBall.setLinearVelocity(new Vector2(2.0, 3.0));
		bowlingBall.translate(-3.0, 3.0);
		this.world.addBody(bowlingBall);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		// move the view a bit
		g.translate(0, -150);
		
		super.render(g, elapsedTime);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		BowlingBall simulation = new BowlingBall();
		simulation.run();
	}
}
