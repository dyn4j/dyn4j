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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents constrained motion between two {@link PhysicsBody}s.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public abstract class Joint<T extends PhysicsBody> implements Shiftable, DataContainer, Ownable {
	/** The first linked body */
	protected final T body1;
	
	/** The second linked body */
	protected final T body2;
	
	/** Whether the pair of bodies joined together can collide with each other */
	protected boolean collisionAllowed;

	/** The user data */
	protected Object userData;
	
	/** The joint owner */
	protected Object owner;
	
	/**
	 * True if this joint is on an island.
	 * @deprecated Deprecated in 4.0.0. No replacement needed.
	 */
	@Deprecated
	boolean onIsland;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Assumes that the joined bodies do not participate 
	 * in collision detection and resolution.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @throws NullPointerException if body1 or body2 is null
	 */
	public Joint(T body1, T body2) {
		this(body1, body2, false);
	}
	
	/**
	 * Full constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param collisionAllowed true if the joined {@link PhysicsBody}s can take part in collision detection
	 * @throws NullPointerException if body1 or body2 is null
	 */
	public Joint(T body1, T body2, boolean collisionAllowed) {
		this.body1 = body1;
		this.body2 = body2;
		this.collisionAllowed = collisionAllowed;
		this.onIsland = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HashCode=").append(this.hashCode());
		// body1, body2, island
		sb.append("|").append(super.toString())
		.append("|IsCollisionAllowed=").append(this.collisionAllowed);
		return sb.toString();
	}
	
	/**
	 * Returns the reduced mass of this pair of bodies.
	 * <p>
	 * The reduced mass is used to solve spring/damper problems as a single body rather
	 * than as a system of two bodies.
	 * <p>
	 * <a href="https://en.wikipedia.org/wiki/Reduced_mass">https://en.wikipedia.org/wiki/Reduced_mass</a>
	 * @return double
	 */
	protected final double getReducedMass() {
		// https://en.wikipedia.org/wiki/Reduced_mass
		double m1 = this.body1.getMass().getMass();
		double m2 = this.body2.getMass().getMass();
		
		// compute the mass
		if (m1 > 0.0 && m2 > 0.0) {
			return m1 * m2 / (m1 + m2);
		} else if (m1 > 0.0) {
			return m1;
		} else {
			return m2;
		}
	}
	
	/**
	 * Returns the reduced inertia of this pair of bodies.
	 * @return double
	 * @see #getReducedMass()
	 */
	protected final double getReducedInertia() {
		double i1 = this.body1.getMass().getInertia();
		double i2 = this.body2.getMass().getInertia();
		
		// compute the mass
		if (i1 > 0.0 && i2 > 0.0) {
			return i1 * i2 / (i1 + i2);
		} else if (i1 > 0.0) {
			return i1;
		} else {
			return i2;
		}
	}
	
	/**
	 * Returns the natural frequency of the given frequency.
	 * <p>
	 * The natural frequency can be determined by combining the following equations:
	 * <pre>
	 * Harmonic oscillator:
	 * f = 1 / (2&pi;) * sqrt(k / m)
	 * 
	 * Natural frequency:
	 * w = sqrt(k / m)
	 * </pre>
	 * Substituting w into the first equation and solving for w:
	 * <pre>
	 * f = 1 / (2&pi;) * w
	 * w = f * 2&pi;
	 * </pre>
	 * @param frequency the frequency
	 * @return double
	 * @see <a href="https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator">https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator</a>
	 */
	protected final double getNaturalFrequency(double frequency) {
		return Geometry.TWO_PI * frequency;
	}
	
	/**
	 * Returns the spring damping coefficient.
	 * <p>
	 * The damping coefficient can be determined by the following equations:
	 * <pre>
	 * Damping Ratio:
	 * dr = actual damping (ad) / critical damping (cd)
	 * 
	 * Critical Damping:
	 * cd = 2mw
	 * </pre>
	 * Where m is the mass and w is the natural frequency. Substituting cd into the first equation and solving for ad:
	 * <pre>
	 * dr = ad / 2mw
	 * ad = dr * 2mw
	 * </pre>
	 * @param mass the mass attached to the spring
	 * @param naturalFrequency the natural frequency
	 * @param dampingRatio the damping ratio
	 * @return double
	 * @see <a href="https://en.wikipedia.org/wiki/Damping_ratio">https://en.wikipedia.org/wiki/Damping_ratio</a>
	 */
	protected final double getSpringDampingCoefficient(double mass, double naturalFrequency, double dampingRatio) {
		return dampingRatio * 2.0 * mass * naturalFrequency;
	}
	
	/**
	 * Returns the spring stiffness, k, from Hooke's Law.
	 * <p>
	 * The stiffness can be determined by the following equation and solving for k:
	 * <pre>
	 * Harmonic oscillator:
	 * f = 1 / (2&pi;) * sqrt(k / m)
	 * 
	 * f * 2&pi; = sqrt(k / m)
	 * k / m = (f * 2&pi;)<sup>2</sup>
	 * k = (f * 2&pi;)<sup>2</sup> * m
	 * k = w<sup>2</sup> * m
	 * </pre>
	 * Where w is the natural frequency and m is the mass.
	 * @param mass the mass attached to the spring
	 * @param naturalFrequency the natural frequency
	 * @return double
	 * @see <a href="https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator">https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator</a>
	 */
	protected final double getSpringStiffness(double mass, double naturalFrequency) {
		return mass * naturalFrequency * naturalFrequency;
	}
	
	/**
	 * Returns the constraint impulse mixing parameter.
	 * @param deltaTime the time step
	 * @param stiffness the stiffness of the spring
	 * @param damping the damping coefficient of the spring
	 * @return double
	 * @see <a href="http://www.ode.org/ode-latest-userguide.html#sec_3_8_2">http://www.ode.org/ode-latest-userguide.html#sec_3_8_2</a>
	 */
	protected final double getConstraintImpulseMixing(double deltaTime, double stiffness, double damping) {
		// CFM = constraint force mixing (from ODE)
		// CFM = 1 / (hk + d)
		
		// since we're solving velocity constraints
		// these factors need an extra h
		
		// CIM = constraint impulse mixing
		// CIM = 1 / (h * (hk + d))

		// compute CIM using [8]
		double cim = deltaTime * (deltaTime * stiffness + damping);
		// check for zero before inverting
		return cim <= Epsilon.E ? 0.0 : 1.0 / cim;
	}
	
	/**
	 * Returns the error reduction parameter.
	 * @param deltaTime the time step
	 * @param stiffness the stiffness of the spring
	 * @param damping the damping coefficient of the spring
	 * @return double
	 * @see <a href="http://www.ode.org/ode-latest-userguide.html#sec_3_8_2">http://www.ode.org/ode-latest-userguide.html#sec_3_8_2</a>
	 */
	protected final double getErrorReductionParameter(double deltaTime, double stiffness, double damping) {
		// ERP = error reduction parameter (from ODE)
		// ERP = hk / (hk + d)
		
		// since we're solving velocity constraints
		// these factors need an extra h
		
		// ERP = error reduction parameter
		// ERP = hk / (h * (hk + d))
		// ERP = k / (hk + d)
		double erp = deltaTime * stiffness + damping;
		// check for zero before inverting
		return erp <= Epsilon.E ? 0.0 : stiffness / erp;
	}
	
	/**
	 * Performs any initialization of the velocity and position constraints.
	 * @param step the time step information
	 * @param settings the current world settings
	 */
	public abstract void initializeConstraints(TimeStep step, Settings settings);
	
	/**
	 * Solves the velocity constraints.
	 * @param step the time step information
	 * @param settings the current world settings
	 */
	public abstract void solveVelocityConstraints(TimeStep step, Settings settings);
	
	/**
	 * Solves the position constraints.
	 * @param step the time step information
	 * @param settings the current world settings
	 * @return boolean true if the position constraints were solved
	 */
	public abstract boolean solvePositionConstraints(TimeStep step, Settings settings);
	
	/**
	 * Returns the first body.
	 * @return T
	 */
	public T getBody1() {
		return this.body1;
	}
	
	/**
	 * Returns the second body.
	 * @return T
	 */
	public T getBody2() {
		return this.body2;
	}

	/**
	 * Returns the body that does not match the given body.
	 * <p>
	 * If the given body is neither body1 or body2, null is returned.
	 * @param body the body
	 * @return T
	 */
	public T getOtherBody(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.body2;
		} else if (this.body2 == body) {
			return this.body1;
		}
		return null;
	}
	
	/**
	 * Returns the anchor point on the first {@link PhysicsBody} in
	 * world coordinates.
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getAnchor1();
	
	/**
	 * Returns the anchor point on the second {@link PhysicsBody} in
	 * world coordinates.
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getAnchor2();
	
	/**
	 * Returns the force applied to the {@link PhysicsBody}s in order
	 * to satisfy the constraint in newtons.
	 * @param invdt the inverse delta time
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getReactionForce(double invdt);
	
	/**
	 * Returns the torque applied to the {@link PhysicsBody}s in order
	 * to satisfy the constraint in newton-meters.
	 * @param invdt the inverse delta time
	 * @return double
	 */
	public abstract double getReactionTorque(double invdt);
	
	/**
	 * Returns true if this {@link Joint} is active.
	 * <p>
	 * A joint is only active if both joined {@link PhysicsBody}s are active.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. Use the isEnabled method instead
	 */
	@Deprecated
	public boolean isActive() {
		return this.body1.isActive() && this.body2.isActive();
	}
	
	/**
	 * Returns true if this {@link Joint} is enabled.
	 * <p>
	 * A joint is only enabled if both joined {@link PhysicsBody}s are enabled.
	 * @return boolean
	 */
	public boolean isEnabled() {
		return this.body1.isEnabled() && this.body2.isEnabled();
	}
	
	/**
	 * Returns true if collision between the joined {@link PhysicsBody}s is allowed.
	 * @return boolean
	 */
	public boolean isCollisionAllowed() {
		return this.collisionAllowed;
	}
	
	/**
	 * Sets whether collision is allowed between the joined {@link PhysicsBody}s.
	 * @param flag true if collisions are allowed
	 */
	public void setCollisionAllowed(boolean flag) {
		// is it different than the current value
		if (this.collisionAllowed != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.collisionAllowed = flag;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#getUserData()
	 */
	public Object getUserData() {
		return this.userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#setUserData(java.lang.Object)
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/**
	 * Returns true if this joint is on an island.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. No replacement needed.
	 */
	@Deprecated
	public boolean isOnIsland() {
		return this.onIsland;
	}
	
	/**
	 * Flags this joint as being on an island.
	 * @param flag true if this joint is on an island
	 * @deprecated Deprecated in 4.0.0. No replacement needed.
	 */
	@Deprecated
	public void setOnIsland(boolean flag) {
		this.onIsland = flag;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.Ownable#getOwner()
	 */
	public Object getOwner() {
		return this.owner;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Ownable#setOwner(java.lang.Object)
	 */
	public void setOwner(Object owner) {
		this.owner = owner;
	}
}
