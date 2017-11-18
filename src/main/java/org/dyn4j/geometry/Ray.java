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

import org.dyn4j.resources.Messages;

/**
 * Implementation of a ray.
 * <p>
 * A ray is a vector with a start point.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 */
public class Ray {
	/** The start point */
	protected Vector2 start;
	
	/** The direction */
	protected Vector2 direction;
	
	/**
	 * Creates a ray from the origin in the given direction.
	 * @param direction the direction in radians
	 * @since 3.0.2
	 */
	public Ray(double direction) {
		this(new Vector2(direction));
	}
	
	/**
	 * Creates a ray from the origin in the given direction.
	 * @param direction the direction; must be normalized
	 */
	public Ray(Vector2 direction) {
		this(new Vector2(), direction);
	}
	
	/**
	 * Creates a ray from the given start point in the given direction.
	 * @param start the start point
	 * @param direction the direction in radians
	 * @since 3.0.2
	 */
	public Ray(Vector2 start, double direction) {
		this(start, new Vector2(direction));
	}
	
	/**
	 * Creates a ray from the given start point in the given direction.
	 * @param start the start point
	 * @param direction the direction; must be normalized
	 * @throws NullPointerException if start or direction is null
	 * @throws IllegalArgumentException if direction is the zero vector
	 */
	public Ray(Vector2 start, Vector2 direction) {
		if (start == null) throw new NullPointerException(Messages.getString("geometry.ray.nullStart"));
		if (direction == null) throw new NullPointerException(Messages.getString("geometry.ray.nullDirection"));
		if (direction.isZero()) throw new IllegalArgumentException(Messages.getString("geometry.ray.zeroDirection"));
		this.start = start;
		this.direction = direction;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ray[Start=").append(this.start)
		.append("|Direction=").append(this.getDirection())
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the start point.
	 * @return {@link Vector2}
	 */
	public Vector2 getStart() {
		return this.start;
	}

	/**
	 * Sets the start point.
	 * @param start the start point
	 * @throws NullPointerException if start is null
	 */
	public void setStart(Vector2 start) {
		if (start == null) throw new NullPointerException(Messages.getString("geometry.ray.nullStart"));
		this.start = start;
	}
	
	/**
	 * Sets the direction of this ray in radians.
	 * @param direction the direction in radians
	 * @since 3.0.2
	 */
	public void setDirection(double direction) {
		this.direction = new Vector2(direction);
	}
	
	/**
	 * Returns the direction of this ray in radians.
	 * @return double the direction in radians between [-&pi;, &pi;]
	 * @since 3.0.2
	 */
	public double getDirection() {
		return this.direction.getDirection();
	}

	/**
	 * Sets the direction.
	 * @param direction the direction; should be normalized
	 * @throws NullPointerException if direction is null
	 * @throws IllegalArgumentException if direction is the zero vector
	 */
	public void setDirection(Vector2 direction) {
		if (direction == null) throw new NullPointerException(Messages.getString("geometry.ray.nullDirection"));
		if (direction.isZero()) throw new IllegalArgumentException(Messages.getString("geometry.ray.zeroDirection"));
		this.direction = direction;
	}

	/**
	 * Returns the direction.
	 * @return {@link Vector2}
	 * @since 3.0.2
	 */
	public Vector2 getDirectionVector() {
		return this.direction;
	}
}
