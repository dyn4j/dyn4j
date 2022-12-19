/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import org.dyn4j.DataContainer;
import org.dyn4j.collision.Fixture;
import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shape;

/**
 * Represents a piece of a {@link PhysicsBody}.
 * <p>
 * {@link BodyFixture} extends the {@link Fixture} class, adding physical features
 * like density and friction.
 * @author William Bittle
 * @version 5.0.0
 * @since 2.0.0
 * @see Fixture
 */
public class BodyFixture extends Fixture implements DataContainer {
	/** The default coefficient of friction; value = {@link #DEFAULT_FRICTION} */
	public static final double DEFAULT_FRICTION = 0.2;
	
	/** The default coefficient of restitution; value = {@link #DEFAULT_RESTITUTION} */
	public static final double DEFAULT_RESTITUTION = 0.0;

	/** The default density in kg/m<sup>2</sup>; value = {@link #DEFAULT_DENSITY} */
	public static final double DEFAULT_DENSITY = 1.0;
	
	/** 
	 * The default restitution velocity; in meters/second 
	 * @since 4.2.0
	 */
	public static final double DEFAULT_RESTITUTION_VELOCITY = 1.0;
	
	/** The density in kg/m<sup>2</sup> */
	protected double density;
	
	/** The coefficient of friction */
	protected double friction;
	
	/** The coefficient of restitution */
	protected double restitution;
	
	/** 
	 * The minimum velocity to apply restitution
	 * @since 4.2.0
	 */
	protected double restitutionVelocity;
	
	/**
	 * Minimal constructor.
	 * @param shape the {@link Convex} {@link Shape} for this fixture
	 */
	public BodyFixture(Convex shape) {
		super(shape);
		this.density = BodyFixture.DEFAULT_DENSITY;
		this.friction = BodyFixture.DEFAULT_FRICTION;
		this.restitution = BodyFixture.DEFAULT_RESTITUTION;
		this.restitutionVelocity = DEFAULT_RESTITUTION_VELOCITY;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BodyFixture[HashCode=").append(this.hashCode())
		.append("|Shape=").append(this.shape)
		.append("|Filter=").append(this.filter)
		.append("|IsSensor=").append(this.sensor)
		.append("|Density=").append(this.density)
		.append("|Friction=").append(this.friction)
		.append("|Restitution=").append(this.restitution)
		.append("|RestitutionVelocity=").append(this.restitutionVelocity)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Sets the density of this shape in kg/m<sup>2</sup>.
	 * <p>
	 * The density of an object is a number that represent how much matter is contained
	 * in a given space.  Larger density values indicate a more massive object.  Larger
	 * density objects resist changes in motion more than smaller objects.
	 * <p>
	 * A density equal to zero will make this shape participate in collision detection
	 * and resolution, but will it will not contribute to the total mass of the body.
	 * @param density the density in kg/m<sup>2</sup>
	 * @throws IllegalArgumentException if density is less than zero
	 */
	public void setDensity(double density) {
		if (density < 0) 
			throw new ValueOutOfRangeException("density", density, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		this.density = density;
	}
	
	/**
	 * Returns the density of this shape in kg/m<sup>2</sup>.
	 * @return double the density in kg/m<sup>2</sup>
	 * @see #setDensity(double)
	 */
	public double getDensity() {
		return this.density;
	}
	
	/**
	 * Returns the coefficient of friction.
	 * @return double
	 * @see #setFriction(double)
	 */
	public double getFriction() {
		return this.friction;
	}
	
	/**
	 * Sets the coefficient of friction.
	 * <p>
	 * The coefficient of friction is a number that represents how rough a material is.
	 * Friction between surfaces converts the kinetic (motion) energy into heat, thereby
	 * slowing the objects in contact down.
	 * <p>
	 * A higher value of friction will slow the object down faster.  A friction value of
	 * zero represents no friction.
	 * @param friction the coefficient of friction; must be greater than zero
	 * @throws IllegalArgumentException if friction is less than zero
	 */
	public void setFriction(double friction) {
		if (friction < 0) 
			throw new ValueOutOfRangeException("friction", friction, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		this.friction = friction;
	}
	
	/**
	 * Returns the coefficient of restitution.
	 * @return double
	 * @see #setRestitution(double)
	 */
	public double getRestitution() {
		return this.restitution;
	}
	
	/**
	 * Sets the coefficient of restitution.
	 * <p>
	 * The coefficient of restitution is a number that represents the bounciness of a material.
	 * Larger values produce more bounce and smaller values produce less bounce.  A value of 1.0
	 * indicates that an object would retain all of its velocity after bouncing.  The value can
	 * be higher than 1 to increase the velocity after bouncing.
	 * <p>
	 * Due to floating point precision and accuracy, a value of 1.0 may not produce a fully
	 * ellastic bounce (all the velocity is retained).
	 * @param restitution the coefficient of restitution; must be greater than zero
	 * @throws IllegalArgumentException if restitution is less than zero
	 */
	public void setRestitution(double restitution) {
		if (restitution < 0) 
			throw new ValueOutOfRangeException("restitution", restitution, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		this.restitution = restitution;
	}
	
	/**
	 * Returns the minimum velocity required to apply restitution.
	 * @return double
	 * @since 4.2.0
	 */
	public double getRestitutionVelocity() {
		return this.restitutionVelocity;
	}
	
	/**
	 * Sets the minimum velocity required to apply restitution.
	 * @param restitutionVelocity the velocity
	 * @since 4.2.0
	 */
	public void setRestitutionVelocity(double restitutionVelocity) {
		this.restitutionVelocity = restitutionVelocity;
	}
	
	/**
	 * Creates a new {@link Mass} object using the set density and shape.
	 * @return {@link Mass}
	 */
	public Mass createMass() {
		return this.shape.createMass(this.density);
	}
}
