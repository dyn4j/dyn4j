/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.dynamics;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Used to solve the contact constraints and joints for a group of interconnected bodies.
 * <p>
 * Nearly identitcal to <a href="http://www.box2d.org">Box2d</a>'s equivalent class.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.4
 * @since 1.0.0
 */
public class Island {
	/** The {@link ContactConstraintSolver} */
	protected ContactConstraintSolver contactConstraintSolver;
	
	/** The list of {@link Body}s on this {@link Island} */
	protected List<Body> bodies;
	
	/** The list of {@link ContactConstraint}s on this {@link Island} */
	protected List<ContactConstraint> contactConstraints;
	
	/** The list of {@link Joint}s on this {@link Island} */
	protected List<Joint> joints;
	
	/**
	 * Full constructor.
	 */
	public Island() {
		super();
		this.contactConstraintSolver = new ContactConstraintSolver();
		this.bodies = new ArrayList<Body>();
		this.contactConstraints = new ArrayList<ContactConstraint>();
		this.joints = new ArrayList<Joint>();
	}

	/**
	 * Clears the {@link Body} and {@link ContactConstraint} lists.
	 */
	public void clear() {
		this.bodies.clear();
		this.contactConstraints.clear();
		this.joints.clear();
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
		// add static constraints to the beginning of the list and normal constraints
		// at the end of the list
		Body b1 = contactConstraint.getBody1();
		Body b2 = contactConstraint.getBody2();
		if (b1.isStatic() || b2.isStatic()) {
			this.contactConstraints.add(0, contactConstraint);
		} else {
			this.contactConstraints.add(contactConstraint);
		}
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
	 * @param gravity the gravity vector
	 * @param step the {@link Step}
	 */
	public void solve(Vector2 gravity, Step step) {
		// get the settings
		Settings settings = Settings.getInstance();
		// the number of solver iterations
		int velocitySolverIterations = settings.getVelocityConstraintSolverIterations();
		int positionSolverIterations = settings.getPositionConstraintSolverIterations();
		// the sleep settings
		double sleepAngularVelocitySquared = settings.getSleepAngularVelocitySquared();
		double sleepVelocitySquared = settings.getSleepVelocitySquared();
		double sleepTime = settings.getSleepTime();

		int size = this.bodies.size();
		int jSize = this.joints.size();
		
		double invM, invI;
		
		// integrate the velocities
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			// check if the body has infinite mass and infinite inertia
			if (!body.isDynamic()) continue;
			// accumulate the forces and torques
			body.accumulate();
			// get the mass properties
			invM = body.mass.getInverseMass();
			invI = body.mass.getInverseInertia();
			// integrate force and torque to modify the velocity and
			// angular velocity (sympletic euler)
			// v1 = v0 + (f / m) + g) * dt
			if (invM > Epsilon.E) {
				// only perform this step if the body does not have
				// a fixed linear velocity
				body.velocity.x += (body.force.x * invM + gravity.x * body.gravityScale) * step.dt;
				body.velocity.y += (body.force.y * invM + gravity.y * body.gravityScale) * step.dt;
			}
			// av1 = av0 + (t / I) * dt
			if (invI > Epsilon.E) {
				// only perform this step if the body does not have
				// a fixed angular velocity
				body.angularVelocity += step.dt * invI * body.torque;
			}
			// apply damping
			double linear = 1.0 - step.dt * body.linearDamping;
			double angular = 1.0 - step.dt * body.angularDamping;
			linear = Interval.clamp(linear, 0.0, 1.0);
			angular = Interval.clamp(angular, 0.0, 1.0);
			body.velocity.multiply(linear);
			body.angularVelocity *= angular;
		}
		
		// set the contact constraints
		this.contactConstraintSolver.setup(this.contactConstraints);
		
		// initialize the constraints
		this.contactConstraintSolver.initializeConstraints(step);
		
		// initialize joint constraints
		for (int i = 0; i < jSize; i++) {
			Joint joint = this.joints.get(i);
			joint.initializeConstraints(step);
		}

		// solve the velocity constraints
		for (int i = 0; i < velocitySolverIterations; i++) {
			// solve the joint velocity constraints
			for (int j = 0; j < jSize; j++) {
				Joint joint = this.joints.get(j);
				joint.solveVelocityConstraints(step);
			}
			
			this.contactConstraintSolver.solveVelocityContraints();
		}
		
		// the max settings
		double maxTranslation = settings.getMaxTranslation();
		double maxRotation = settings.getMaxRotation();
		double maxTranslationSqrd = settings.getMaxTranslationSquared();
		double maxRotationSqrd = settings.getMaxRotationSquared();
		
		// integrate the positions
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			
			if (body.isStatic()) continue;
			
			// compute the translation and rotation for this time step
			Vector2 translation = body.velocity.product(step.dt);
			double rotation = body.angularVelocity * step.dt;
			
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
			body.translate(body.velocity.product(step.dt));
			body.rotateAboutCenter(body.angularVelocity * step.dt);
		}
		
		// solve the position constraints
		for (int i = 0; i < positionSolverIterations; i++) {
			boolean contactsSolved = this.contactConstraintSolver.solvePositionContraints();
			
			// solve the joint position constraints
			boolean jointsSolved = true;
			for (int j = 0; j < jSize; j++) {
				Joint joint = this.joints.get(j);
				boolean jointSolved = joint.solvePositionConstraints();
				jointsSolved = jointsSolved && jointSolved;
			}
			
			if (contactsSolved && jointsSolved) {
				break;
			}
		}
		
		// see if sleep is enabled
		if (settings.isAutoSleepingEnabled()) {
			double minSleepTime = Double.MAX_VALUE;
			// check for sleep-able bodies
			for (int i = 0; i < size; i++) {
				Body body = this.bodies.get(i);
				// see if the body is allowed to sleep
				if (body.isAutoSleepingEnabled()) {
					// just skip static bodies
					if (body.isStatic()) continue;
					// check the linear and angular velocity
					if (body.velocity.dot(body.velocity) > sleepVelocitySquared || body.angularVelocity * body.angularVelocity > sleepAngularVelocitySquared) {
						// if either the linear or angular velocity is above the 
						// threshold then reset the sleep time
						body.setAsleep(false);
						minSleepTime = 0.0;
					} else {
						// then increment the sleep time
						body.incrementSleepTime(step.dt);
						minSleepTime = Math.min(minSleepTime, body.getSleepTime());
					}
				} else {
					minSleepTime = 0.0;
				}
			}
			
			// check the min sleep time
			if (minSleepTime >= sleepTime) {
				for (int i = 0; i < size; i++) {
					Body body = this.bodies.get(i);
					body.setAsleep(true);
				}
			}
		}
	}
}
