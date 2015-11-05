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

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

/**
 * A simple scene of two billiard balls colliding with one another
 * and a wall.
 * <p>
 * Primarily used to illustrate the computation of the mass and size
 * of the balls.  See the {@link Billiards#initializeWorld()} method.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public final class Billiards extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -8518496343422955267L;

	/**
	 * Default constructor.
	 */
	public Billiards() {
		super("Billiards", 300.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#initializeWorld()
	 */
	@Override
	protected void initializeWorld() {
		// no gravity on a top-down view of a billiards game
		this.world.setGravity(World.ZERO_GRAVITY);
		
		// create all your bodies/joints
		
		SimulationBody wallr = new SimulationBody();
		wallr.addFixture(Geometry.createRectangle(0.2, 10));
		wallr.translate(2, 0);
		wallr.setMass(MassType.INFINITE);
		world.addBody(wallr);
		
		SimulationBody ball1 = new SimulationBody();
		ball1.addFixture(Geometry.createCircle(0.028575), //  2.25 in diameter = 0.028575 m radius
				217.97925, 								  //  0.126 oz/in^3 = 217.97925 kg/m^3
				0.08,
				0.9);
		ball1.translate(-1.0, 0.0);
		//ball1.setLinearVelocity(5.36448, 0.0); 		  // 12 mph = 5.36448 m/s
		ball1.setLinearVelocity(2, 0);					  //  so we can see the bouncing
		ball1.setMass(MassType.NORMAL);
		this.world.addBody(ball1);
		
		SimulationBody ball2 = new SimulationBody();
		ball2.addFixture(Geometry.createCircle(0.028575), 217.97925, 0.08, 0.9);
		ball2.translate(1.0, 0.0);
		ball2.setMass(MassType.NORMAL);
		this.world.addBody(ball2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.samples.SimulationFrame#render(java.awt.Graphics2D, double)
	 */
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		// move the view a bit
		g.translate(-200, 0);
		
		super.render(g, elapsedTime);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Billiards simulation = new Billiards();
		simulation.run();
	}
}
