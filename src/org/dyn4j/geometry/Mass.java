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
package org.dyn4j.geometry;

import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.resources.Messages;

/**
 * Represents {@link Mass} data for an object about a given point.
 * <p>
 * Stores the center of mass, the mass, and inertia.
 * <p>
 * The center point may be something other than the origin (0, 0).  In this case, the mass and
 * inertia are about this point, not the origin.
 * <p>
 * A {@link Mass} can also take on special {@link MassType}s.  These mass types allow for interesting
 * effects during interaction.
 * <p>
 * When the mass type is changed, the original mass and inertia values are not lost. This allows the
 * swapping of mass types without recomputing the mass.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @see MassType
 */
public class Mass {
	/** The mass type */
	MassType type;
	
	/** The center of mass */
	final Vector2 center;
	
	/** The mass in kg */
	final double mass;
	
	/** The inertia tensor in kg &middot; m<sup>2</sup> */
	final double inertia;
	
	/** The inverse mass */
	final double invMass;
		
	/** The inverse inertia tensor */
	final double invInertia;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates an infinite mass centered at the origin.
	 */
	public Mass() {
		this.type = MassType.INFINITE;
		this.center = new Vector2();
		this.mass = 0.0;
		this.inertia = 0.0;
		this.invMass = 0.0;
		this.invInertia = 0.0;
	}
	
	/**
	 * Full Constructor.
	 * <p>
	 * The <code>center</code> parameter will be copied.
	 * @param center center of {@link Mass} in local coordinates
	 * @param mass mass in kg
	 * @param inertia inertia tensor in kg &middot; m<sup>2</sup>
	 * @throws NullPointerException if center is null
	 * @throws IllegalArgumentException if mass or inertia is less than zero
	 */
	public Mass(Vector2 center, double mass, double inertia) {
		// validate the input
		if (center == null) throw new NullPointerException(Messages.getString("geometry.mass.nullCenter"));
		if (mass < 0.0) throw new IllegalArgumentException(Messages.getString("geometry.mass.invalidMass"));
		if (inertia < 0.0) throw new IllegalArgumentException(Messages.getString("geometry.mass.invalidInertia"));
		// create the mass
		this.type = MassType.NORMAL;
		this.center = center.copy();
		this.mass = mass;
		this.inertia = inertia;
		// set the inverse mass
		if (mass > Epsilon.E) {
			this.invMass = 1.0 / mass;
		} else {
			this.invMass = 0.0;
			this.type = MassType.FIXED_LINEAR_VELOCITY;
		}
		// set the inverse inertia
		if (inertia > Epsilon.E) {
			this.invInertia = 1.0 / inertia;
		} else {
			this.invInertia = 0.0;
			this.type = MassType.FIXED_ANGULAR_VELOCITY;
		}
		// check if both the mass and inertia are zero
		if (mass <= Epsilon.E && inertia <= Epsilon.E) {
			this.type = MassType.INFINITE;
		}
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * Performs a deep copy.
	 * @param mass the {@link Mass} to copy
	 * @throws NullPointerException if mass is null
	 */
	public Mass(Mass mass) {
		// validate the input
		if (mass == null) throw new NullPointerException(Messages.getString("geometry.mass.nullMass"));
		// setup the mass
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
		sb.append("Mass[Type=").append(this.type)
		.append("|Center=").append(this.center)
		.append("|Mass=").append(this.mass)
		.append("|Inertia=").append(this.inertia)
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.center == null) ? 0 : this.center.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.inertia);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.invInertia);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.invMass);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.mass);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof Mass) {
			Mass o = (Mass) other;
			if (this.type == o.type
			 && this.mass == o.mass
			 && this.inertia == o.inertia
			 && this.center.equals(o.center)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a {@link Mass} object from the given array of masses.
	 * <p>
	 * Uses the Parallel Axis Theorem to obtain the inertia tensor about
	 * the center of all the given masses:
	 * <p style="white-space: pre;"> I<sub>dis</sub> = I<sub>cm</sub> + mr<sup>2</sup>
	 * I<sub>total</sub> = &sum; I<sub>dis</sub></p>
	 * The center for the resulting mass will be a mass weighted center.
	 * <p>
	 * This method will produce unexpected results if any mass contained in the
	 * list is infinite.
	 * @param masses the list of {@link Mass} objects to combine
	 * @return {@link Mass} the combined {@link Mass}
	 * @throws NullPointerException if masses is null or contains null elements
	 * @throws IllegalArgumentException if masses is empty
	 */
	public static Mass create(List<Mass> masses) {
		// check the list for null or empty
		if (masses == null) {
			throw new NullPointerException(Messages.getString("geometry.mass.nullMassList"));
		}
		if (masses.size() == 0) {
			throw new IllegalArgumentException(Messages.getString("geometry.mass.invalidMassListSize"));
		}
		// get the length of the masses array
		int size = masses.size();
		
		// check for a list of one
		if (size == 1) {
			// check for null item
			Mass m = masses.get(0);
			if (m != null) {
				return new Mass(masses.get(0));
			} else {
				throw new NullPointerException(Messages.getString("geometry.mass.invalidMassListSize"));
			}
		}
		
		// initialize the new mass values
		Vector2 c = new Vector2();
		double m = 0.0;
		double I = 0.0;
		
		// loop over the masses
		for (int i = 0; i < size; i++) {
			Mass mass = masses.get(i);
			// check for null mass
			if (mass == null) throw new NullPointerException(Messages.getString("geometry.mass.nullMassListElement"));
			// add the center's up (weighting them by their respective mass)
			c.add(mass.center.product(mass.mass));
			// sum the masses
			m += mass.mass;
		}
		// the mass will never be negative but could be zero
		// if all the masses are infinite
		if (m > 0.0) {
			// compute the center by dividing by the total mass
			c.multiply(1.0 / m);
		}
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
	 * Returns true if this {@link Mass} object is of type {@link MassType#INFINITE}.
	 * <p>
	 * A mass will still be treated as an infinite mass in physical modeling if the
	 * mass and inertia are zero. This method simply checks the mass type.
	 * @return boolean
	 */
	public boolean isInfinite() {
		return this.type == MassType.INFINITE;
	}
	
	/**
	 * Sets the mass type.
	 * @param type the mass type
	 * @throws NullPointerException if type is null
	 */
	public void setType(MassType type) {
		if (type == null) throw new NullPointerException(Messages.getString("geometry.mass.nullMassType"));
		this.type = type;
	}
	
	/**
	 * Returns the mass type.
	 * @return {@link MassType}
	 */
	public MassType getType() {
		return this.type;
	}
	
	/**
	 * Returns the center of mass.
	 * @return {@link Vector2}
	 */
	public Vector2 getCenter() {
		return this.center;
	}
	
	/**
	 * Returns the mass.
	 * @return double
	 */
	public double getMass() {
		if (this.type == MassType.INFINITE || this.type == MassType.FIXED_LINEAR_VELOCITY) {
			return 0.0;
		} else {
			return this.mass;
		}
	}
	
	/**
	 * Returns the inertia tensor.
	 * @return double
	 */
	public double getInertia() {
		if (this.type == MassType.INFINITE || this.type == MassType.FIXED_ANGULAR_VELOCITY) {
			return 0.0;
		} else {
			return this.inertia;
		}
	}
	
	/**
	 * Returns the inverse mass.
	 * @return double
	 */
	public double getInverseMass() {
		if (this.type == MassType.INFINITE || this.type == MassType.FIXED_LINEAR_VELOCITY) {
			return 0.0;
		} else {
			return this.invMass;
		}
	}
	
	/**
	 * Returns the inverse inertia tensor.
	 * @return double
	 */
	public double getInverseInertia() {
		if (this.type == MassType.INFINITE || this.type == MassType.FIXED_ANGULAR_VELOCITY) {
			return 0.0;
		} else {
			return this.invInertia;
		}
	}
}

