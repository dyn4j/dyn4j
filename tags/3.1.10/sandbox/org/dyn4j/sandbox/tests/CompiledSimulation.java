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

import javax.media.opengl.GL2;

import org.dyn4j.sandbox.Simulation;

/**
 * Represents a simulation that is pre-compiled and therefore should not be edited.
 * <p>
 * Tests of this nature are typically tests that require custom code to illustrate a
 * particular feature.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.2
 */
public abstract class CompiledSimulation extends Simulation {
	/** Set this to true if an update is required to show new bodies/joints */
	protected boolean changed;
	
	/**
	 * Default constructor.
	 * <p>
	 * This method automatically calls the {@link #initialize()} method after
	 * the super class is created.
	 * <p>
	 * The constructor is the best place to do camera setup since it will not
	 * be reset via the reset method.
	 */
	public CompiledSimulation() {
		super();
		// call the initialize method
		this.initialize();
	}
	
	/**
	 * This method should be implemented to initialize the simulation.
	 * <p>
	 * Creation of bodies/joints should be done here, setup of the world, custom listeners
	 * should be added, etc.
	 */
	public abstract void initialize();
	
	/**
	 * This method should be implemented to update any custom simulation code.
	 * <p>
	 * At the time this method is called the world has already been updated.  This is called
	 * whether the world performed a step or not.  The stepped parameter will be set to
	 * true if the world performed a simulation step.
	 * @param elapsedTime the elapsed time in seconds
	 * @param stepped true if the world performed a step
	 */
	public abstract void update(double elapsedTime, boolean stepped);
	
	/**
	 * This method should be implemented to render anything additional.  The body/fixture labels,
	 * origin and origin label, and the scale helper are rendered on top of anything rendered in this
	 * method.
	 * @param gl the OpenGL context
	 */
	public abstract void render(GL2 gl);
	
	/**
	 * This method should be implemented to reset the test.
	 * <p>
	 * This method usually takes the form of:
	 * <code>
	 * this.world.removeAll();
	 * this.initialize();
	 * // then any other custom reset code
	 * </code>
	 */
	public abstract void reset();
	
	/**
	 * Returns true if this simulation has been changed.
	 * <p>
	 * When this method is called, the flag will be reset.
	 * @return boolean
	 */
	public boolean isChanged() {
		boolean changed = this.changed;
		this.changed = false;
		return changed;
	}
}
