/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.EmptyCollectionException;
import org.dyn4j.exception.NullElementException;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shiftable;

/**
 * Represents an abstract implementation of constrained motion between 
 * {@link PhysicsBody}s.
 * @author William Bittle
 * @version 6.0.0
 * @since 5.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public abstract class AbstractJoint<T extends PhysicsBody> implements Joint<T>, Shiftable, DataContainer, Ownable {
	/** An unmodifiable list of the bodies */
	protected final List<T> bodies;

	/** Whether the pair of bodies joined together can collide with each other */
	protected boolean collisionAllowed;

	/** The user data */
	protected Object userData;
	
	/** The joint owner */
	protected Object owner;
	
	/**
	 * Default constructor.
	 * @param bodies the list of bodies
	 * @throws NullPointerException when bodies is null or any element of bodies is null
	 * @throws IllegalArgumentException when bodies is empty
	 */
	public AbstractJoint(List<T> bodies) {
		if (bodies == null) 
			throw new ArgumentNullException("bodies");
		
		if (bodies.size() == 0) 
			throw new EmptyCollectionException("bodies");
		
		int size = bodies.size();
		for (int i = 0; i < size; i++) {
			if (bodies.get(i) == null) {
				throw new NullElementException("bodies", i);
			}
		}
		
		this.bodies = Collections.unmodifiableList(bodies);
		this.collisionAllowed = false;
	}
	
	/**
	 * Copy constructor.
	 * @param joint the joint to copy
	 * @param bodies the bodies for the copy
	 * @since 6.0.0
	 */
	protected AbstractJoint(AbstractJoint<T> joint, List<T> bodies) {
		this.bodies = Collections.unmodifiableList(bodies);
		this.collisionAllowed = joint.collisionAllowed;
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
	 * Returns the frequency given the natural frequency.
	 * <p style="white-space: pre;">
	 * Harmonic oscillator:
	 * f = (1 / 2&pi;) * sqrt(k / m)
	 * 
	 * Natural frequency:
	 * w = sqrt(k / m)
	 * </p>
	 * Substituting w into the first equation and solving for w:
	 * <p style="white-space: pre;">
	 * f = (1 / 2&pi;) * w
	 * f = w / 2&pi;
	 * </p>
	 * @param naturalFrequency the natural frequency
	 * @return double
	 */
	protected static final double getFrequency(double naturalFrequency) {
		return naturalFrequency / Geometry.TWO_PI;
	}
	
	/**
	 * Returns the natural frequency of the given frequency.
	 * <p>
	 * The natural frequency can be determined by combining the following equations:
	 * <p style="white-space: pre;">
	 * Harmonic oscillator:
	 * f = 1 / (2&pi;) * sqrt(k / m)
	 * 
	 * Natural frequency:
	 * w = sqrt(k / m)
	 * </p>
	 * Substituting w into the first equation and solving for w:
	 * <p style="white-space: pre;">
	 * f = 1 / (2&pi;) * w
	 * w = f * 2&pi;
	 * </p>
	 * @param frequency the frequency
	 * @return double
	 * @see <a href="https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator">https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator</a>
	 */
	protected static final double getNaturalFrequency(double frequency) {
		return Geometry.TWO_PI * frequency;
	}
	
	/**
	 * Returns the natural frequency given the spring stiffness and mass.
	 * <p style="white-space: pre;">
	 * Natural frequency:
	 * w = sqrt(k / m)
	 * </p>
	 * @param stiffness the spring stiffness
	 * @param mass the mass
	 * @return double
	 */
	protected static final double getNaturalFrequency(double stiffness, double mass) {
		if (mass <= Epsilon.E)
			return 0.0;
		
		return Math.sqrt(stiffness / mass);
	}

	
	/**
	 * Returns the spring damping coefficient.
	 * <p>
	 * The damping coefficient can be determined by the following equations:
	 * <p style="white-space: pre;">
	 * Damping Ratio:
	 * dr = actual damping (ad) / critical damping (cd)
	 * 
	 * Critical Damping:
	 * cd = 2mw
	 * </p>
	 * Where m is the mass and w is the natural frequency. Substituting cd into the first equation and solving for ad:
	 * <p style="white-space: pre;">
	 * dr = ad / 2mw
	 * ad = dr * 2mw
	 * </p>
	 * @param mass the mass attached to the spring
	 * @param naturalFrequency the natural frequency
	 * @param dampingRatio the damping ratio
	 * @return double
	 * @see <a href="https://en.wikipedia.org/wiki/Damping_ratio">https://en.wikipedia.org/wiki/Damping_ratio</a>
	 */
	protected static final double getSpringDampingCoefficient(double mass, double naturalFrequency, double dampingRatio) {
		return dampingRatio * 2.0 * mass * naturalFrequency;
	}
	
	/**
	 * Returns the spring stiffness, k, from Hooke's Law.
	 * <p>
	 * The stiffness can be determined by the following equation and solving for k:
	 * <p style="white-space: pre;">
	 * Harmonic oscillator:
	 * f = 1 / (2&pi;) * sqrt(k / m)
	 * 
	 * f * 2&pi; = sqrt(k / m)
	 * k / m = (f * 2&pi;)<sup>2</sup>
	 * k = (f * 2&pi;)<sup>2</sup> * m
	 * k = w<sup>2</sup> * m
	 * </p>
	 * Where w is the natural frequency and m is the mass.
	 * @param mass the mass attached to the spring
	 * @param naturalFrequency the natural frequency
	 * @return double
	 * @see <a href="https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator">https://en.wikipedia.org/wiki/Hookes_law#Harmonic_oscillator</a>
	 */
	protected static final double getSpringStiffness(double mass, double naturalFrequency) {
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
	protected static final double getConstraintImpulseMixing(double deltaTime, double stiffness, double damping) {
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
	protected static final double getErrorReductionParameter(double deltaTime, double stiffness, double damping) {
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBodies()
	 */
	@Override
	public final List<T> getBodies() {
		return this.bodies;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBodyCount()
	 */
	@Override
	public int getBodyCount() {
		return this.bodies.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBody(int)
	 */
	@Override
	public T getBody(int index) {
		return this.bodies.get(index);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBodyIterator()
	 */
	@Override
	public final Iterator<T> getBodyIterator() {
		return this.bodies.iterator();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		int size = this.bodies.size();
		for (int i = 0; i < size; i++) {
			T body = this.bodies.get(i);
			if (!body.isEnabled()) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isMember(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean isMember(CollisionBody<?> body) {
		int size = this.bodies.size();
		for (int i = 0; i < size; i++) {
			T test = this.bodies.get(i);
			if (body == test) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isCollisionAllowed()
	 */
	public boolean isCollisionAllowed() {
		return this.collisionAllowed;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#setCollisionAllowed(boolean)
	 */
	public void setCollisionAllowed(boolean flag) {
		this.collisionAllowed = flag;
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
