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
package org.dyn4j.geometry;

/**
 * Represents a geometric {@link Shape}.
 * <p>
 * {@link Shape}s are {@link Transformable}, however, in general a {@link Transform} object should
 * be used instead of directly transforming the {@link Shape}.  Doing so will allow reuse of
 * the same {@link Shape} object in multiple places, where only the {@link Transform} differs.
 * @author William Bittle
 * @version 3.0.2
 * @since 1.0.0
 */
public interface Shape extends Transformable {	
	/**
	 * Returns the unique identifier for this shape instance.
	 * @return String
	 */
	public abstract String getId();
	
	/**
	 * Returns the center/centroid of the {@link Shape} in local coordinates.
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getCenter();
	
	/**
	 * Returns the maximum radius of the shape from the center.
	 * @return double
	 * @since 2.0.0
	 */
	public abstract double getRadius();
	
	/**
	 * Returns the radius of the shape if the given point was the
	 * center for this shape.
	 * @param center the center point
	 * @return double
	 * @since 3.0.2
	 */
	public abstract double getRadius(Vector2 center);
	
	/**
	 * Returns the user data.
	 * @return Object
	 */
	public abstract Object getUserData();
	
	/**
	 * Sets the user data.
	 * @param userData the user data
	 */
	public abstract void setUserData(Object userData);
	
	/**
	 * Rotates the {@link Shape} about it's center.
	 * @param theta the rotation angle in radians
	 */
	public abstract void rotate(double theta);
	
	/**
	 * Returns the {@link Interval} of this {@link Shape} projected onto the given {@link Vector2} 
	 * given the {@link Transform}.
	 * @param n {@link Vector2} to project onto
	 * @param transform {@link Transform} for this {@link Shape}
	 * @return {@link Interval}
	 */
	public abstract Interval project(Vector2 n, Transform transform);

	/**
	 * Returns true if the given point is inside this {@link Shape}.
	 * <p>
	 * If the given point lies on an edge the point is considered
	 * to be inside the {@link Shape}.
	 * <p>
	 * The given point is assumed to be in world space.
	 * @param point world space point
	 * @param transform {@link Transform} for this {@link Shape}
	 * @return boolean
	 */
	public abstract boolean contains(Vector2 point, Transform transform);
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Shape} and the given density.
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Shape}
	 */
	public abstract Mass createMass(double density);
	
	/**
	 * Creates an {@link AABB} from this {@link Shape}.
	 * @param transform the {@link Transform} for this {@link Shape}
	 * @return {@link AABB} the {@link AABB} enclosing this {@link Shape}
	 * @since 3.0.0
	 */
	public abstract AABB createAABB(Transform transform);
}
