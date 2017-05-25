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
package org.dyn4j.dynamics.contact;

import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.Step;

/**
 * Represents an impulse/velocity based rigid {@link Body} physics collision resolver.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
public interface ContactConstraintSolver {
	/**
	 * Performs initialization of the contact constraints.
	 * @param contactConstraints the contact constraints to solve
	 * @param step the time step information
	 * @param settings the current settings
	 */
	public void initialize(List<ContactConstraint> contactConstraints, Step step, Settings settings);
	
	/**
	 * Solves the {@link ContactConstraint}s on the velocity level.
	 * @param contactConstraints the contact constraints to solve
	 * @param step the time step information
	 * @param settings the current settings
	 */
	public void solveVelocityContraints(List<ContactConstraint> contactConstraints, Step step, Settings settings);
	
	/**
	 * Solves the {@link ContactConstraint}s on the position level.
	 * @param contactConstraints the contact constraints to solve
	 * @param step the time step information
	 * @param settings the current settings
	 * @return boolean true if the constraints have been solved
	 */
	public boolean solvePositionContraints(List<ContactConstraint> contactConstraints, Step step, Settings settings);
}
