/*
 * Copyright (c) 2010, William Bittle
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

import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Used to solve the contact constraints and joints for a group of interconnected bodies.
 * @author William Bittle
 */
public class Island {
	/** The {@link ContactConstraintSolver} */
	protected ContactConstraintSolver solver;
	
	/** The list of {@link Body}s on this {@link Island} */
	protected List<Body> bodies;
	
	/** The list of {@link ContactConstraint}s on this {@link Island} */
	protected List<ContactConstraint> ccs;
	
	/** The list of {@link Joint}s on this {@link Island} */
	protected List<Joint> joints;
	
	/**
	 * Full constructor.
	 * @param sleepListener the sleep listener
	 */
	public Island() {
		super();
		this.solver = new ContactConstraintSolver();
		this.bodies = new ArrayList<Body>();
		this.ccs = new ArrayList<ContactConstraint>();
		this.joints = new ArrayList<Joint>();
	}

	/**
	 * Clears the {@link Body} and {@link ContactConstraint} lists.
	 */
	public void clear() {
		this.bodies.clear();
		this.ccs.clear();
		this.joints.clear();
	}
	
	/**
	 * Adds the given {@link Body} to the {@link Body} list.
	 * @param b the {@link Body}
	 */
	public void add(Body b) {
		this.bodies.add(b);
	}
	
	/**
	 * Adds the given {@link ContactConstraint} to the {@link ContactConstraint} list.
	 * @param cc the {@link ContactConstraint}
	 */
	public void add(ContactConstraint cc) {
		this.ccs.add(cc);
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
	public void solve(Vector gravity, Step step) {
		// get the settings
		Settings settings = Settings.getInstance();
		double hz = settings.getStepFrequency();
		double maxVelocity = settings.getMaxVelocity();
		double maxAngularVelocity = settings.getMaxAngularVelocity();
		int siSolverIterations = settings.getSiSolverIterations();
		double sleepAngularVelocity = settings.getSleepAngularVelocity();
		double sleepVelocity = settings.getSleepVelocity();
		double sleepTime = settings.getSleepTime();

		int size = this.bodies.size();
		int jSize = this.joints.size();
		
		// integrate the velocities
		for (int i = 0; i < size; i++) {
			Body b = this.bodies.get(i);
			// check if the body has infinite mass and infinite inertia
			if (b.isStatic()) continue;
			// accumulate the forces and torques
			b.accumulate();
			// integrate force and torque to modify the velocity and
			// angular velocity (sympletic euler)
			// v1 = v0 + (f / m) + g) * dt
			b.v.add(b.force.product(b.mass.invM).add(gravity).multiply(step.dt));
			// av1 = av0 + (t / I) * dt
			b.av += step.dt * b.mass.invI * b.torque;
			// apply damping
			double linear = 1.0 - step.dt * b.linearDamping;
			double angular = 1.0 - step.dt * b.angularDamping;
			linear = Interval.clamp(linear, 0.0, 1.0);
			angular = Interval.clamp(angular, 0.0, 1.0);
			b.v.multiply(linear);
			b.av *= angular;
			// make sure the bodies aren't going too fast
			if (b.v.dot(b.v) > maxVelocity * maxVelocity) {
				b.v.normalize();
				b.v.multiply(maxVelocity);
			}
			if (b.av * b.av > maxAngularVelocity * maxAngularVelocity) {
				b.av = maxAngularVelocity;
			}
		}
		
		// set the contact constraints
		this.solver.setup(ccs);
		
		// initialize the constraints
		this.solver.initializeConstraints(hz, step);
		
		// initialize joint constraints
		for (int i = 0; i < jSize; i++) {
			Joint joint = this.joints.get(i);
			joint.initializeConstraints(step);
		}

		// solve the velocity constraints
		for (int i = 0; i < siSolverIterations; i++) {
			this.solver.solveVelocityContraints();
			
			// solve the joint velocity constraints
			for (int j = 0; j < jSize; j++) {
				Joint joint = this.joints.get(j);
				joint.solveVelocityConstraints(step);
			}
		}

		// integrate the positions
		for (int i = 0; i < size; i++) {
			Body b = this.bodies.get(i);
			
			if (b.isStatic()) continue;
			
			b.translate(b.v.product(step.dt));

			Vector c = b.transform.getTransformed(b.mass.c);
			b.rotate(b.av * step.dt, c);
		}
		
		// solve the position constraints
		for (int i = 0; i < siSolverIterations; i++) {
			boolean solved = this.solver.solvePositionContraints();
			
			// solve the joint position constraints
			for (int j = 0; j < jSize; j++) {
				Joint joint = this.joints.get(j);
				solved = solved && joint.solvePositionConstraints();
			}
			
			if (solved) {
				break;
			}
		}
		
		// see if sleep is enabled
		if (settings.canSleep()) {
			double minSleepTime = Double.MAX_VALUE;
			// check for sleep-able bodies
			for (int i = 0; i < size; i++) {
				Body b = this.bodies.get(i);
				// see if the body is allowed to sleep
				if (b.canSleep()) {
					// just skip static bodies
					if (b.isStatic()) continue;
					// check the linear and angular velocity
					if (b.v.dot(b.v) > sleepVelocity * sleepVelocity
					 || Math.abs(b.av) > sleepAngularVelocity) {
						// if either the linear or angular velocity is above the 
						// threshold then reset the sleep time
						b.awaken();
						minSleepTime = 0.0;
					} else {
						// then increment the sleep time
						b.incrementSleepTime(step.dt);
						minSleepTime = Math.min(minSleepTime, b.getSleepTime());
					}
				} else {
					minSleepTime = 0.0;
				}
			}
			
			// check the min sleep time
			if (minSleepTime >= sleepTime) {
				for (int i = 0; i < size; i++) {
					Body b = this.bodies.get(i);
					b.sleep();
				}
			}
		}
	}
}
