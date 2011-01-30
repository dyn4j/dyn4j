/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.game2d.collision.Fixture;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Shape;

/**
 * Represents a part of a {@link Body}.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.0.0
 */
public class BodyFixture extends Fixture {
	/** The default coefficient of friction; value = {@value #DEFAULT_FRICTION} */
	public static final double DEFAULT_FRICTION = 0.2;
	
	/** The default coefficient of restitution; value = {@value #DEFAULT_RESTITUTION} */
	public static final double DEFAULT_RESTITUTION = 0.0;

	/** The default density in kg/m<sup>2</sup> */
	public static final double DEFAULT_DENSITY = 1.0;
	
	/** The density in kg/m<sup>2</sup> */
	protected double density;
	
	/** The coefficient of friction */
	protected double friction;
	
	/** The coefficient of restitution */
	protected double restitution;
	
	/**
	 * Minimal constructor.
	 * @param shape the {@link Convex} {@link Shape} for this fixture
	 */
	public BodyFixture(Convex shape) {
		super(shape);
		this.density = BodyFixture.DEFAULT_DENSITY;
		this.friction = BodyFixture.DEFAULT_FRICTION;
		this.restitution = BodyFixture.DEFAULT_RESTITUTION;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BODY_FIXTURE[")
		.append(this.id).append("|")
		.append(this.shape).append("|")
		.append(this.density).append("|")
		.append(this.filter).append("|")
		.append(this.sensor).append("|")
		.append(this.friction).append("|")
		.append(this.restitution).append("|")
		.append(this.userData).append("|")
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Sets the density of this shape in kg/m<sup>2</sup>.
	 * @param density the density in kg/m<sup>2</sup>
	 * @throws IllegalArgumentException if density is less than or equal to zero
	 */
	public void setDensity(double density) {
		if (density <= 0) throw new IllegalArgumentException("The density must be greater than 0.");
		this.density = density;
	}
	
	/**
	 * Returns the density of this shape in kg/m<sup>2</sup>.
	 * @return double the density in kg/m<sup>2</sup>
	 */
	public double getDensity() {
		return this.density;
	}
	
	/**
	 * Returns the coefficient of friction.
	 * @return double
	 */
	public double getFriction() {
		return friction;
	}
	
	/**
	 * Sets the coefficient of friction.
	 * @param friction the coefficient of friction; must be greater than zero
	 * @throws IllegalArgumentException if friction is less than zero
	 */
	public void setFriction(double friction) {
		if (friction < 0) throw new IllegalArgumentException("The coefficient of friction cannot be negative.");
		this.friction = friction;
	}
	
	/**
	 * Returns the coefficient of restitution.
	 * @return double
	 */
	public double getRestitution() {
		return restitution;
	}
	
	/**
	 * Sets the coefficient of restitution.
	 * @param restitution the coefficient of restitution; must be greater than zero
	 * @throws IllegalArgumentException if restitution is less than zero
	 */
	public void setRestitution(double restitution) {
		if (restitution < 0) throw new IllegalArgumentException("The coefficient of restitution cannot be negative.");
		this.restitution = restitution;
	}
	
	/**
	 * Creates a new {@link Mass} object using the set density.
	 * @return {@link Mass}
	 */
	public Mass createMass() {
		return this.shape.createMass(this.density);
	}
}
