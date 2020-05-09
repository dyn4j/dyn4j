/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Collisions;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

/**
 * Used to solve the contact constraints and joints for a group of interconnected bodies.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
final class Island<T extends PhysicsBody> {
	/** The list of {@link Body}s on this {@link Island} */
	final List<T> bodies;

	/** The list of {@link Joint}s on this {@link Island} */
	final List<Joint<T>> joints;
	
	/** The list of {@link ContactConstraint}s on this {@link Island} */
	final List<ContactConstraint<T>> contactConstraints;

	/** A global map to see if a body, joint, or contact constraint has been added to the island already */
	final Map<Object, Boolean> added;
	
	/**
	 * Default constructor.
	 * @since 3.2.0
	 */
	public Island() {
		this(64, 16);
	}
	
	/**
	 * Full constructor.
	 * @param initialBodyCount the initial body capacity
	 * @param initialJointCount the initial joint capacity
	 * @since 4.0.0
	 */
	public Island(int initialBodyCount, int initialJointCount) {
		int bodyCount = initialBodyCount <= 0 ? 64 : initialBodyCount;
		int jointCount = initialJointCount <= 0 ? 16 : initialJointCount;
		int contactConstraintCount = Collisions.getEstimatedCollisionPairs(bodyCount);
		
		// estimate sizes while we stand up the island
		this.bodies = new ArrayList<T>(bodyCount);
		this.joints = new ArrayList<Joint<T>>(jointCount);
		this.contactConstraints = new ArrayList<ContactConstraint<T>>(contactConstraintCount);
		this.added = new HashMap<Object, Boolean>(contactConstraintCount + bodyCount + jointCount);
	}

	/**
	 * Clears the island.
	 */
	public void clear() {
		this.bodies.clear();
		this.joints.clear();
		this.contactConstraints.clear();
		this.added.clear();
	}
	
	/**
	 * Adds the given {@link Body} to the {@link Body} list.
	 * @param body the {@link Body}
	 */
	public void add(T body) {
		if (!this.added.containsKey(body)) {
			this.bodies.add(body);
			this.added.put(body, true);
		}
	}
	
	/**
	 * Adds the given {@link ContactConstraint} to the {@link ContactConstraint} list.
	 * @param contactConstraint the {@link ContactConstraint}
	 */
	public void add(ContactConstraint<T> contactConstraint) {
		if (!this.added.containsKey(contactConstraint)) {
			this.contactConstraints.add(contactConstraint);
			this.added.put(contactConstraint, true);
		}
	}
	
	/**
	 * Adds the given {@link Joint} to the {@link Joint} list.
	 * @param joint the {@link Joint}
	 */
	public void add(Joint<T> joint) {
		if (!this.added.containsKey(joint)) {
			this.joints.add(joint);
			this.added.put(joint, true);
		}
	}
	
	/**
	 * Returns true if the given object has already been
	 * added to this island.
	 * @param object the object (body, joint, or contact constraint)
	 * @return boolean
	 */
	public boolean isOnIsland(Object object) {
		return this.added.containsKey(object);
	}
	
	/**
	 * Integrates the {@link Body}s, solves all {@link ContactConstraint}s and
	 * {@link Joint}s, and attempts to sleep motionless {@link Body}s.
	 * @param solver the contact constraint solver
	 * @param gravity the gravity vector
	 * @param step the time step information
	 * @param settings the current world settings
	 */
	public void solve(ContactConstraintSolver<T> solver, Vector2 gravity, TimeStep step, Settings settings) {
		// the number of solver iterations
		final int velocitySolverIterations = settings.getVelocityConstraintSolverIterations();
		final int positionSolverIterations = settings.getPositionConstraintSolverIterations();
		final double sleepTime = settings.getMinimumAtRestTime();

		final int size = this.bodies.size();
		final int jSize = this.joints.size();
		
		// integrate the velocities
		for (int i = 0; i < size; i++) {
			PhysicsBody body = this.bodies.get(i);
			body.integrateVelocity(gravity, step, settings);
		}
		
		// initialize the solver
		solver.initialize(this.contactConstraints, step, settings);
		
		// initialize joint constraints
		for (int i = 0; i < jSize; i++) {
			Joint<T> joint = this.joints.get(i);
			joint.initializeConstraints(step, settings);
		}
		
		if (!this.contactConstraints.isEmpty() || !this.joints.isEmpty()) {
			// solve the velocity constraints if needed
			for (int i = 0; i < velocitySolverIterations; i++) {
				// solve the joint velocity constraints
				for (int j = 0; j < jSize; j++) {
					Joint<T> joint = this.joints.get(j);
					joint.solveVelocityConstraints(step, settings);
				}
				
				solver.solveVelocityContraints(this.contactConstraints, step, settings);
			}
		}
		
		// integrate the positions
		for (int i = 0; i < size; i++) {
			PhysicsBody body = this.bodies.get(i);
			
			// FIXME there's a bug here i think - static bodies are allowed to participate in many islands. This means that if it's a moving static body, it's position will be updated more than once
			body.integratePosition(step, settings);
		}
		
		// solve the position constraints
		boolean positionConstraintsSolved = false;
		for (int i = 0; i < positionSolverIterations; i++) {
			boolean contactsSolved = solver.solvePositionContraints(this.contactConstraints, step, settings);
			
			// solve the joint position constraints
			boolean jointsSolved = true;
			for (int j = 0; j < jSize; j++) {
				Joint<T> joint = this.joints.get(j);
				boolean jointSolved = joint.solvePositionConstraints(step, settings);
				jointsSolved = jointsSolved && jointSolved;
			}
			
			if (contactsSolved && jointsSolved) {
				positionConstraintsSolved = true;
				break;
			}
		}
		
		// see if sleep is enabled
		if (settings.isAtRestDetectionEnabled()) {
			double minSleepTime = Double.MAX_VALUE;
			// check for sleep-able bodies
			for (int i = 0; i < size; i++) {
				PhysicsBody body = this.bodies.get(i);
				
				double bodySleepTime = body.updateAtRestTime(step, settings);
				minSleepTime = Math.min(minSleepTime, bodySleepTime);
			}
			
			// check the min sleep time
			if (minSleepTime >= sleepTime && positionConstraintsSolved) {
				for (int i = 0; i < size; i++) {
					PhysicsBody body = this.bodies.get(i);
					body.setAtRest(true);
				}
			}
		}
	}
}
