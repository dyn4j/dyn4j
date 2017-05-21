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
package org.dyn4j.dynamics;

import org.dyn4j.Listener;

/**
 * Listener notified before and after a simulation step by the {@link World}.
 * <p>
 * Modification of the {@link World} is permitted in any of these methods.
 * @author William Bittle
 * @version 3.1.0
 * @since 1.0.0
 */
public interface StepListener extends Listener {
	/**
	 * Called before a simulation step is performed.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 */
	public abstract void begin(Step step, World world);
	
	/**
	 * Called after collision detection and contact updates have been performed due
	 * to a call to the {@link World#setUpdateRequired(boolean)} passing true.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 * @since 3.0.1
	 * @see World#setUpdateRequired(boolean)
	 */
	public abstract void updatePerformed(Step step, World world);
	
	/**
	 * Called after the world has been solved but before new contacts are found.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 * @since 3.2.0
	 */
	public abstract void postSolve(Step step, World world);
	
	/**
	 * Called after a simulation step has been performed.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 */
	public abstract void end(Step step, World world);
}
