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
		/** Indicates that the mass's rotation should not change */
		FIXED_ROTATION,
		/** Indicates that the mass's translation should not change */
		FIXED_TRANSLATION
	}
	
	/** The default density in kg/m<sup>2</sup> */
	public static final double DEFAULT_DENSITY = 1.0;
	
	/** The center of mass */
	protected Vector c;
	
	/** The mass in kg */
	protected double m;
	
	/** The inertia tensor in kg &middot; m<sup>2</sup> */
	protected double I;
	
	/** The inverse mass */
	protected double invM;
		
	/** The inverse inertia tensor */
	protected double invI;
	
	/**
	 * Full Constructor.
	 * @param c center of {@link Mass} in local coordinates
	 * @param m mass in kg
	 * @param I inertia tensor in kg &middot; m<sup>2</sup>
	 */
	protected Mass(Vector c, double m, double I) {
		this.c = c;
		this.m = m;
		this.I = I;
		if (m > 0) {
			this.invM = 1.0 / m;
		} else {
			this.invM = 0.0;
		}
		if (I > 0) {
			this.invI = 1.0 / I;
		} else {
			this.invI = 0.0;
		}
	}
	
	/**
	 * Infinite mass constructor.
	 * @param c center of {@link Mass} in local coordinates
	 */
	protected Mass(Vector c) {
		this.c = c;
		this.m = 0.0;
		this.I = 0.0;
		this.invM = 0.0;
		this.invI = 0.0;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * Performs a deep copy.
	 * @param mass the {@link Mass} to copy
	 */
	protected Mass(Mass mass) {
		super();
		this.c = mass.c.copy();
		this.m = mass.m;
		this.I = mass.I;
		this.invM = mass.invM;
		this.invI = mass.invI;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MASS[")
		.append(c).append("|")
		.append(m).append("|")
		.append(I).append("]");
		return sb.toString();
	}
	
	/**
	 * Creates an infinite {@link Mass} object with the center at
	 * the given point.
	 * @param c the center
	 * @return {@link Mass} an infinite {@link Mass}
	 */
	public static Mass create(Vector c) {
		if (c == null) throw new NullPointerException("The center point cannot be null.");
		return new Mass(c.copy());
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
	 * Creates a specialized {@link Mass} from the given {@link Mass}.
	 * <p>
	 * This method is intended to be used with {@link Mass} objects
	 * who are of type NORMAL.  Using any specialized mass will have
	 * unexpected results.
	 * @param mass the {@link Mass} to specialize
	 * @param type the type of {@link Mass} to create
	 * @return {@link Mass} the specialized {@link Mass}
	 */
	public static Mass create(Mass mass, Mass.Type type) {
		if (mass == null) throw new NullPointerException("Cannot copy a null mass.");
		if (type == null) throw new NullPointerException("The type parameter cannot be null.");
		if (type == Mass.Type.NORMAL) {
			// perform a simple copy
			return new Mass(mass);
		} else if (type == Mass.Type.INFINITE) {
			// create an infinite mass from the given mass
			return new Mass(mass.c.copy());
		} else if (type == Mass.Type.FIXED_ROTATION) {
			// create a fixed rotation mass
			return new Mass(mass.c.copy(), mass.m, 0.0);
		} else {
			// create a fixed rotation mass
			return new Mass(mass.c.copy(), 0.0, mass.I);
		}
	}
	
	/**
	 * Creates a {@link Mass} object for the given center
	 * of mass, mass, and inertia tensor.
	 * @param c the center of mass
	 * @param m the mass in kg; must be zero or greater
	 * @param I the inertia tensor kg &middot; m<sup>2</sup>; must be zero or greater
	 * @return {@link Mass} the mass object
	 */
	public static Mass create(Vector c, double m, double I) {
		// verify the passed in values
		if (c == null) throw new NullPointerException("The center point cannot be null.");
		if (m < 0.0) throw new IllegalArgumentException("The mass must be greater than zero.");
		if (I < 0.0) throw new IllegalArgumentException("The inertia tensor must be greater than zero.");
		// create the mass if validation passed
		return new Mass(c, m, I);
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
		Vector c = new Vector();
		double m = 0.0;
		double I = 0.0;
		// get the length of the masses array
		int size = masses.size();
		// loop over the masses
		for (int i = 0; i < size; i++) {
			Mass mass = masses.get(i);
			// add the center's up (weighting them by their respective mass)
			c.add(mass.c.product(mass.m));
			// sum the masses
			m += mass.m;
		}
		// compute the center by dividing by the total mass
		c.divide(m);
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
			double d2 = mass.c.distanceSquared(c);
			// compute Idis
			double Idis = mass.I + mass.m * d2;
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
		return this.m == 0.0 && this.I == 0.0;
	}
	
	/**
	 * Returns the center of mass.
	 * @return {@link Vector}
	 */
	public Vector getCenter() {
		return c;
	}
	
	/**
	 * Returns the mass.
	 * @return double
	 */
	public double getMass() {
		return m;
	}
	
	/**
	 * Returns the inertia tensor.
	 * @return double
	 */
	public double getInertia() {
		return I;
	}
	
	/**
	 * Returns the inverse mass.
	 * @return double
	 */
	public double getInverseMass() {
		return invM;
	}
	
	/**
	 * Returns the inverse inertia tensor.
	 * @return double
	 */
	public double getInverseInertia() {
		return invI;
	}
}

