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
package org.dyn4j.game2d.geometry;

import java.util.List;

import org.dyn4j.game2d.dynamics.Body;

/**
 * Represents {@link Mass} data for a {@link Body}.
 * <p>
 * Stores the center of mass, area, mass, and inertia tensor.
 * @author William Bittle
 */
public class Mass {
	/**
	 * Enumeration for special mass types.
	 * @author William Bittle
	 */
	public static enum Type {
		/** Indicates a normal mass */
		NORMAL,
		/** Indicates that the mass is infinite */
		INFINITE,
		/** Indicates that the mass's rate of rotation should not change */
		FIXED_ANGULAR_VELOCITY,
		/** Indicates that the mass's rate of translation should not change */
		FIXED_LINEAR_VELOCITY,
	}
	
	/** The mass type */
	protected Mass.Type type;
	
	/** The center of mass */
	protected Vector2 center;
	
	/** The mass in kg */
	protected double mass;
	
	/** The inertia tensor in kg &middot; m<sup>2</sup> */
	protected double inertia;
	
	/** The inverse mass */
	protected double invMass;
		
	/** The inverse inertia tensor */
	protected double invInertia;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates an infinite mass centered at the origin.
	 */
	public Mass() {
		this.type = Mass.Type.INFINITE;
		this.center = new Vector2();
		this.mass = 0.0;
		this.inertia = 0.0;
		this.invMass = 0.0;
		this.invInertia = 0.0;
	}
	
	/**
	 * Full Constructor.
	 * @param center center of {@link Mass} in local coordinates
	 * @param mass mass in kg
	 * @param inertia inertia tensor in kg &middot; m<sup>2</sup>
	 */
	public Mass(Vector2 center, double mass, double inertia) {
		this.type = Mass.Type.NORMAL;
		this.center = center.copy();
		this.mass = mass;
		this.inertia = inertia;
		if (mass > 0) {
			this.invMass = 1.0 / mass;
		} else {
			this.invMass = 0.0;
		}
		if (inertia > 0) {
			this.invInertia = 1.0 / inertia;
		} else {
			this.invInertia = 0.0;
		}
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * Performs a deep copy.
	 * @param mass the {@link Mass} to copy
	 */
	public Mass(Mass mass) {
		super();
		this.type = mass.type;
		this.center = mass.center.copy();
		this.mass = mass.mass;
		this.inertia = mass.inertia;
		this.invMass = mass.invMass;
		this.invInertia = mass.invInertia;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MASS[")
		.append(this.type).append("|")
		.append(this.center).append("|")
		.append(this.mass).append("|")
		.append(this.inertia).append("]");
		return sb.toString();
	}
	
	/**
	 * Creates a deep copy of the given {@link Mass}.
	 * @param mass the {@link Mass} to copy
	 * @return {@link Mass} the copy
	 */
	public static Mass create(Mass mass) {
		if (mass == null) throw new NullPointerException("Cannot create a copy of a null mass.");
		return new Mass(mass);
	}
	
	/**
	 * Creates a {@link Mass} object for the given center
	 * of mass, mass, and inertia tensor.
	 * @param center the center of mass in local coordinates
	 * @param mass the mass in kg; must be zero or greater
	 * @param inertia the inertia tensor kg &middot; m<sup>2</sup>; must be zero or greater
	 * @return {@link Mass} the mass object
	 */
	public static Mass create(Vector2 center, double mass, double inertia) {
		// verify the passed in values
		if (center == null) throw new NullPointerException("The center point cannot be null.");
		if (mass <= 0.0) throw new IllegalArgumentException("The mass must be greater than zero.");
		if (inertia <= 0.0) throw new IllegalArgumentException("The inertia tensor must be greater than zero.");
		// create the mass if validation passed
		return new Mass(center, mass, inertia);
	}
	
	/**
	 * Creates a {@link Mass} object from the given array of masses.
	 * <p>
	 * Uses the Parallel Axis Theorem to obtain the inertia tensor about
	 * the center of all the given masses:
	 * <pre>
	 * I<sub>dis</sub> = I<sub>cm</sub> + mr<sup>2</sup>
	 * I<sub>total</sub> = &sum; I<sub>dis</sub>
	 * </pre>
	 * The center for the resulting mass will be a mass weighted center.
	 * <p>
	 * This method will produce unexpected results if any mass contained in the
	 * list is infinite.
	 * @param masses the list of {@link Mass} objects to combine
	 * @return {@link Mass} the combined {@link Mass}
	 */
	public static Mass create(List<Mass> masses) {
		// check the list for null or empty
		if (masses == null || masses.size() == 0) {
			throw new IllegalArgumentException("The masses list must not be null and contain at least one element.");
		}
		Vector2 c = new Vector2();
		double m = 0.0;
		double I = 0.0;
		// get the length of the masses array
		int size = masses.size();
		// loop over the masses
		for (int i = 0; i < size; i++) {
			Mass mass = masses.get(i);
			// add the center's up (weighting them by their respective mass)
			c.add(mass.center.product(mass.mass));
			// sum the masses
			m += mass.mass;
		}
		// compute the center by dividing by the total mass
		c.multiply(1.0 / m);
		// after obtaining the new center of mass we need
		// to compute the interia tensor about the center
		// using the parallel axis theorem:
		// Idis = Icm + mr^2 where r is the perpendicular distance
		// between the two parallel axes
		for (int i = 0; i < size; i++) {
			// get the mass 
			Mass mass = masses.get(i);
			// compute the distance from the new center to
			// the current mass's center
			double d2 = mass.center.distanceSquared(c);
			// compute Idis
			double Idis = mass.inertia + mass.mass * d2;
			// add it to the sum
			I += Idis;
		}
		// finally create the mass
		return new Mass(c, m, I);
	}
	
	/**
	 * Returns true if this {@link Mass} object has infinite mass.
	 * @return boolean
	 */
	public boolean isInfinite() {
		return this.type == Mass.Type.INFINITE;
	}
	
	/**
	 * Sets the mass type.
	 * @param type the mass type
	 */
	public void setType(Mass.Type type) {
		if (type == null) throw new NullPointerException("The mass type cannot be null.");
		this.type = type;
	}
	
	/**
	 * Returns the mass type.
	 * @return {@link Mass.Type}
	 */
	public Mass.Type getType() {
		return this.type;
	}
	
	/**
	 * Returns the center of mass.
	 * @return {@link Vector2}
	 */
	public Vector2 getCenter() {
		return center;
	}
	
	/**
	 * Returns the mass.
	 * @return double
	 */
	public double getMass() {
		if (this.type == Mass.Type.INFINITE || this.type == Mass.Type.FIXED_LINEAR_VELOCITY) {
			return 0.0;
		} else {
			return mass;
		}
	}
	
	/**
	 * Returns the inertia tensor.
	 * @return double
	 */
	public double getInertia() {
		if (this.type == Mass.Type.INFINITE || this.type == Mass.Type.FIXED_ANGULAR_VELOCITY) {
			return 0.0;
		} else {
			return inertia;
		}
	}
	
	/**
	 * Returns the inverse mass.
	 * @return double
	 */
	public double getInverseMass() {
		if (this.type == Mass.Type.INFINITE || this.type == Mass.Type.FIXED_LINEAR_VELOCITY) {
			return 0.0;
		} else {
			return invMass;
		}
	}
	
	/**
	 * Returns the inverse inertia tensor.
	 * @return double
	 */
	public double getInverseInertia() {
		if (this.type == Mass.Type.INFINITE || this.type == Mass.Type.FIXED_ANGULAR_VELOCITY) {
			return 0.0;
		} else {
			return invInertia;
		}
	}
}

