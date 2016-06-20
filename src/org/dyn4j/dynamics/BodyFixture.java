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

import org.dyn4j.DataContainer;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shape;
import org.dyn4j.resources.Messages;

/**
 * Represents a piece of a {@link Body}.
 * <p>
 * {@link BodyFixture} extends the {@link Fixture} class, adding physical features
 * like density and friction.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 * @see Fixture
 */
public class BodyFixture extends Fixture implements DataContainer {
	/** The default coefficient of friction; value = {@value #DEFAULT_FRICTION} */
	public static final double DEFAULT_FRICTION = 0.2;
	
	/** The default coefficient of restitution; value = {@value #DEFAULT_RESTITUTION} */
	public static final double DEFAULT_RESTITUTION = 0.0;

	/** The default density in kg/m<sup>2</sup>; value = {@value #DEFAULT_DENSITY} */
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
		sb.append("BodyFixture[Id=").append(this.id)
		.append("|Shape=").append(this.shape)
		.append("|Filter=").append(this.filter)
		.append("|IsSensor=").append(this.sensor)
		.append("|Density=").append(this.density)
		.append("|Friction=").append(this.friction)
		.append("|Restitution=").append(this.restitution)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Sets the density of this shape in kg/m<sup>2</sup>.
	 * <p>
	 * The density of an object is a number that represent how much matter is contained
	 * in a given space.  Larger density values indicate a more massive object.  Larger
	 * density objects resist changes in motion more than smaller objects.
	 * @param density the density in kg/m<sup>2</sup>
	 * @throws IllegalArgumentException if density is less than or equal to zero
	 */
	public void setDensity(double density) {
		if (density <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.body.fixture.invalidDensity"));
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
	 * <p>
	 * Since two {@link BodyFixture}s participate in a collision, their coefficients will
	 * be mixed by a {@link CoefficientMixer} to product a single value for the collision.
	 * @param friction the coefficient of friction; must be greater than zero
	 * @throws IllegalArgumentException if friction is less than zero
	 */
	public void setFriction(double friction) {
		if (friction < 0) throw new IllegalArgumentException(Messages.getString("dynamics.body.fixture.invalidFriction"));
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
	 * <p>
	 * Since two {@link BodyFixture}s participate in a collision, their coefficients will
	 * be mixed by a {@link CoefficientMixer} to product a single value for the collision.
	 * @param restitution the coefficient of restitution; must be greater than zero
	 * @throws IllegalArgumentException if restitution is less than zero
	 */
	public void setRestitution(double restitution) {
		if (restitution < 0) throw new IllegalArgumentException(Messages.getString("dynamics.body.fixture.invalidRestitution"));
		this.restitution = restitution;
	}
	
	/**
	 * Creates a new {@link Mass} object using the set density and shape.
	 * @return {@link Mass}
	 */
	public Mass createMass() {
		return this.shape.createMass(this.density);
	}
}
