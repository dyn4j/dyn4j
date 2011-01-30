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

import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a force.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Force {
	/** The force to apply */
	protected Vector2 force;
	
	/**
	 * Default constructor.
	 */
	public Force() {
		this.force = new Vector2();
	}
	
	/**
	 * Creates a new {@link Force} using the x and y components.
	 * @param x the x component
	 * @param y the y component
	 */
	public Force(double x, double y) {
		this.force = new Vector2(x, y);
	}
	
	/**
	 * Creates a new {@link Force} using the given {@link Vector2}.
	 * @param force the force {@link Vector2}
	 * @throws NullPointerException if force is null
	 */
	public Force(Vector2 force) {
		if (force == null) throw new NullPointerException("The force vector cannot be null.");
		this.force = force;
	}
	
	/**
	 * Copy constructor.
	 * @param force the {@link Force} to copy
	 * @throws NullPointerException if force is null
	 */
	public Force(Force force) {
		if (force == null) throw new NullPointerException("Cannot copy a null force.");
		this.force = force.force.copy();
	}
	
	/**
	 * Sets this {@link Force} to the given components.
	 * @param x the x component
	 * @param y the y component
	 */
	public void set(double x, double y) {
		this.force.set(x, y);
	}
	
	/**
	 * Sets this {@link Force} to the given force {@link Vector2}.
	 * @param force the force {@link Vector2}
	 * @throws NullPointerException if force is null
	 */
	public void set(Vector2 force) {
		if (force == null) throw new NullPointerException("Cannot set this force vector to a null vector.");
		this.force.set(force);
	}
	
	/**
	 * Sets this {@link Force} to the given {@link Force}.
	 * @param force the {@link Force} to copy
	 * @throws NullPointerException if force is null
	 */
	public void set(Force force) {
		if (force == null) throw new NullPointerException("Cannot set this force to a null force.");
		this.force.set(force.force);
	}
	
	/**
	 * Applies this {@link Force} to the {@link Body}.
	 * @param body the {@link Body} to apply the {@link Force} to
	 */
	public void apply(Body body) {
		body.force.add(this.force);
	}
	
	/**
	 * Returns true if this force should be removed.
	 * <p>
	 * Implement this method to create {@link Force} objects
	 * that are not cleared each iteration by the {@link World}.
	 * <p>
	 * The default implementation always returns true.
	 * @return boolean true if this force should be removed
	 * @since 2.0.0
	 */
	public boolean isComplete() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FORCE[").append(this.force).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the force vector.
	 * @return {@link Vector2}
	 */
	public Vector2 getForce() {
		return this.force;
	}
}
