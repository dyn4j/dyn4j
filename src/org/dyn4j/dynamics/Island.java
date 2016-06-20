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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.collision.Collisions;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Used to solve the contact constraints and joints for a group of interconnected bodies.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
final class Island {
	/** The list of {@link Body}s on this {@link Island} */
	final List<Body> bodies;

	/** The list of {@link Joint}s on this {@link Island} */
	final List<Joint> joints;
	
	/** The list of {@link ContactConstraint}s on this {@link Island} */
	final List<ContactConstraint> contactConstraints;
	
	/**
	 * Default constructor.
	 * <p>
	 * Uses a default {@link Capacity} for the initial capacity.
	 * @since 3.2.0
	 */
	public Island() {
		this(Capacity.DEFAULT_CAPACITY);
	}
	
	/**
	 * Full constructor.
	 * @param initialCapacity the initial capacity of the island
	 * @throws NullPointerException if initialCapacity is null
	 * @since 3.2.0
	 */
	public Island(Capacity initialCapacity) {
		// check for null capacity
		if (initialCapacity == null) throw new NullPointerException(Messages.getString("dynamics.nullCapacity"));
		this.bodies = new ArrayList<Body>(initialCapacity.getBodyCount());
		this.joints = new ArrayList<Joint>(initialCapacity.getJointCount());
		// estimated the number of contacts
		int eSize = Collisions.getEstimatedCollisionPairs(initialCapacity.getBodyCount());
		this.contactConstraints = new ArrayList<ContactConstraint>(eSize);
	}

	/**
	 * Clears the island.
	 */
	public void clear() {
		this.bodies.clear();
		this.joints.clear();
		this.contactConstraints.clear();
	}
	
	/**
	 * Adds the given {@link Body} to the {@link Body} list.
	 * @param body the {@link Body}
	 */
	public void add(Body body) {
		this.bodies.add(body);
	}
	
	/**
	 * Adds the given {@link ContactConstraint} to the {@link ContactConstraint} list.
	 * @param contactConstraint the {@link ContactConstraint}
	 */
	public void add(ContactConstraint contactConstraint) {
		this.contactConstraints.add(contactConstraint);
	}
	
	/**
	 * Adds the given {@link Joint} to the {@link Joint} list.
	 * @param joint the {@link Joint}
	 */
	public void add(Joint joint) {
		this.joints.add(joint);
	}
	
	/**
	 * Integrates the {@link Body}s, solves all {@link ContactConstraint}s and
	 * {@link Joint}s, and attempts to sleep motionless {@link Body}s.
	 * @param solver the contact constraint solver
	 * @param gravity the gravity vector
	 * @param step the time step information
	 * @param settings the current world settings
	 */
	public void solve(ContactConstraintSolver solver, Vector2 gravity, Step step, Settings settings) {
		// the number of solver iterations
		int velocitySolverIterations = settings.getVelocityConstraintSolverIterations();
		int positionSolverIterations = settings.getPositionConstraintSolverIterations();
		// the sleep settings
		double sleepAngularVelocitySquared = settings.getSleepAngularVelocitySquared();
		double sleepLinearVelocitySquared = settings.getSleepLinearVelocitySquared();
		double sleepTime = settings.getSleepTime();

		int size = this.bodies.size();
		int jSize = this.joints.size();
		
		double dt = step.dt;
		double invM, invI;
		
		// integrate the velocities
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			// check if the body has infinite mass and infinite inertia
			if (!body.isDynamic()) continue;
			// accumulate the forces and torques
			body.accumulate(dt);
			// get the mass properties
			invM = body.mass.getInverseMass();
			invI = body.mass.getInverseInertia();
			// integrate force and torque to modify the velocity and
			// angular velocity (sympletic euler)
			// v1 = v0 + (f / m) + g) * dt
			if (invM > Epsilon.E) {
				// only perform this step if the body does not have
				// a fixed linear velocity
				body.velocity.x += (body.force.x * invM + gravity.x * body.gravityScale) * dt;
				body.velocity.y += (body.force.y * invM + gravity.y * body.gravityScale) * dt;
			}
			// av1 = av0 + (t / I) * dt
			if (invI > Epsilon.E) {
				// only perform this step if the body does not have
				// a fixed angular velocity
				body.angularVelocity += dt * invI * body.torque;
			}
			// apply damping
			double linear = 1.0 - dt * body.linearDamping;
			double angular = 1.0 - dt * body.angularDamping;
			linear = Interval.clamp(linear, 0.0, 1.0);
			angular = Interval.clamp(angular, 0.0, 1.0);
			// inline body.velocity.multiply(linear);
			body.velocity.x *= linear;
			body.velocity.y *= linear;
			body.angularVelocity *= angular;
		}
		
		// initialize the solver
		solver.initialize(this.contactConstraints, step, settings);
		
		// initialize joint constraints
		for (int i = 0; i < jSize; i++) {
			Joint joint = this.joints.get(i);
			joint.initializeConstraints(step, settings);
		}

		// solve the velocity constraints
		for (int i = 0; i < velocitySolverIterations; i++) {
			// solve the joint velocity constraints
			for (int j = 0; j < jSize; j++) {
				Joint joint = this.joints.get(j);
				joint.solveVelocityConstraints(step, settings);
			}
			
			solver.solveVelocityContraints(this.contactConstraints, step, settings);
		}
		
		// the max settings
		double maxTranslation = settings.getMaximumTranslation();
		double maxRotation = settings.getMaximumRotation();
		double maxTranslationSqrd = settings.getMaximumTranslationSquared();
		double maxRotationSqrd = settings.getMaximumRotationSquared();
		
		// integrate the positions
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			
			if (body.isStatic()) continue;
			
			// compute the translation and rotation for this time step
			Vector2 translation = body.velocity.product(dt);
			double rotation = body.angularVelocity * dt;
			
			// make sure the translation is not over the maximum
			if (translation.getMagnitudeSquared() > maxTranslationSqrd) {
				double ratio = maxTranslation / translation.getMagnitude();
				body.velocity.multiply(ratio);
			}
			
			// make sure the rotation is not over the maximum
			if (rotation * rotation > maxRotationSqrd) {
				double ratio = maxRotation / Math.abs(rotation);
				body.angularVelocity *= ratio;
			}
			
			// recompute the translation/rotation in case we hit the maximums
			// inline body.translate(body.velocity.product(dt));
			body.translate(body.velocity.x * dt, body.velocity.y * dt);
			body.rotateAboutCenter(body.angularVelocity * dt);
		}
		
		// solve the position constraints
		boolean positionConstraintsSolved = false;
		for (int i = 0; i < positionSolverIterations; i++) {
			boolean contactsSolved = solver.solvePositionContraints(this.contactConstraints, step, settings);
			
			// solve the joint position constraints
			boolean jointsSolved = true;
			for (int j = 0; j < jSize; j++) {
				Joint joint = this.joints.get(j);
				boolean jointSolved = joint.solvePositionConstraints(step, settings);
				jointsSolved = jointsSolved && jointSolved;
			}
			
			if (contactsSolved && jointsSolved) {
				positionConstraintsSolved = true;
				break;
			}
		}
		
		// see if sleep is enabled
		if (settings.isAutoSleepingEnabled()) {
			double minSleepTime = Double.MAX_VALUE;
			// check for sleep-able bodies
			for (int i = 0; i < size; i++) {
				Body body = this.bodies.get(i);
				// just skip static bodies
				if (body.isStatic()) continue;
				// see if the body is allowed to sleep
				if (body.isAutoSleepingEnabled()) {
					// check the linear and angular velocity
					if (body.velocity.dot(body.velocity) > sleepLinearVelocitySquared || body.angularVelocity * body.angularVelocity > sleepAngularVelocitySquared) {
						// if either the linear or angular velocity is above the 
						// threshold then reset the sleep time
						body.sleepTime = 0.0;
						minSleepTime = 0.0;
					} else {
						// then increment the sleep time
						body.sleepTime += step.dt;
						minSleepTime = Math.min(minSleepTime, body.sleepTime);
					}
				} else {
					body.sleepTime = 0.0;
					minSleepTime = 0.0;
				}
			}
			
			// check the min sleep time
			if (minSleepTime >= sleepTime && positionConstraintsSolved) {
				for (int i = 0; i < size; i++) {
					Body body = this.bodies.get(i);
					body.setAsleep(true);
				}
			}
		}
	}
}
