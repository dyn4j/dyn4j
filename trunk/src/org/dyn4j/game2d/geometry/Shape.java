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

/**
 * Represents a geometric {@link Shape}.
 * <p>
 * {@link Shape}s are {@link Transformable}, however, in general a {@link Transform} object should
 * be used instead of directly transforming the {@link Shape}.  Doing so will allow reuse of
 * the same {@link Shape} object in multiple places, where only the {@link Transform} differs.
 * @author William Bittle
 */
public interface Shape extends Transformable {
	/**
	 * Returns a unique identifier for this shape instance.
	 * @return String
	 */
	public abstract String getId();
	
	/**
	 * Returns the center/centroid of the {@link Shape} in local coordinates.
	 * @return {@link Vector}
	 */
	public abstract Vector getCenter();
	
	/**
	 * Rotates the {@link Shape} about it's center.
	 * @param theta the rotation angle in radians
	 */
	public abstract void rotate(double theta);
	
	/**
	 * Returns the {@link Interval} of this {@link Shape} projected onto the given {@link Vector} 
	 * given the {@link Transform}.
	 * @param n {@link Vector} to project onto
	 * @param transform {@link Transform} for this {@link Shape}
	 * @return {@link Interval}
	 */
	public abstract Interval project(Vector n, Transform transform);

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
	public abstract boolean contains(Vector point, Transform transform);
}
