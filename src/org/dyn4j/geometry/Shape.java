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

import java.util.UUID;

import org.dyn4j.DataContainer;

/**
 * Represents a geometric {@link Shape}.
 * <p>
 * The {@link Shape} class implements the {@link Transformable} interface and modifies the 
 * internal state of the {@link Shape} directly (translating the vertices for example).
 * <p>
 * The various implementing classes may allow mutation of the shape indirectly by returning
 * mutable objects.  It's recommended that a {@link Shape}, after creation and use, remain
 * unchanged and instead be replaced with a new {@link Shape} if modification is necessary.
 * @author William Bittle
 * @version 3.1.4
 * @since 1.0.0
 */
public interface Shape extends Transformable, DataContainer {	
	/**
	 * Returns the unique identifier for this shape instance.
	 * @return UUID
	 */
	public abstract UUID getId();
	
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
	 * @throws NullPointerException if the given point is null
	 * @since 3.0.2
	 */
	public abstract double getRadius(Vector2 center);
	
	/**
	 * Rotates the {@link Shape} about it's center.
	 * <p>
	 * This method replaced the overriding functionality of the
	 * rotate method from the {@link Transformable} interface.
	 * @param theta the rotation angle in radians
	 * @since 3.1.1
	 */
	public abstract void rotateAboutCenter(double theta);
	
	/**
	 * Returns the {@link Interval} of this {@link Shape} projected onto the given {@link Vector2} 
	 * given the {@link Transform}.
	 * <p>
	 * This is the same as calling {@link #project(Vector2, Transform)} and passing {@link Transform#IDENTITY}.
	 * @param vector {@link Vector2} to project onto
	 * @return {@link Interval}
	 * @throws NullPointerException if the given vector is null
	 * @since 3.1.5
	 */
	public abstract Interval project(Vector2 vector);
	
	/**
	 * Returns the {@link Interval} of this {@link Shape} projected onto the given {@link Vector2} 
	 * given the {@link Transform}.
	 * @param vector {@link Vector2} to project onto
	 * @param transform {@link Transform} for this {@link Shape}
	 * @return {@link Interval}
	 * @throws NullPointerException if the given vector or transform is null
	 */
	public abstract Interval project(Vector2 vector, Transform transform);

	/**
	 * Returns true if the given point is inside this {@link Shape}.
	 * <p>
	 * If the given point lies on an edge the point is considered
	 * to be inside the {@link Shape}.
	 * <p>
	 * The given point is assumed to be in world space.
	 * <p>
	 * This is the same as calling {@link #contains(Vector2, Transform)} and passing {@link Transform#IDENTITY}.
	 * @param point world space point
	 * @return boolean
	 * @throws NullPointerException if the given point is null
	 * @since 3.1.5
	 */
	public abstract boolean contains(Vector2 point);
	
	/**
	 * Returns true if the given point is inside this {@link Shape}.
	 * <p>
	 * If the given point lies on an edge the point is considered
	 * to be inside the {@link Shape}.
	 * <p>
	 * The given point is assumed to be in world space.
	 * @param point world space point
	 * @param transform {@link Transform} for this {@link Shape}
	 * @throws NullPointerException if the given point or transform is null
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
	 * <p>
	 * This is the same as calling {@link #createAABB(Transform)} and passing {@link Transform#IDENTITY}.
	 * @return {@link AABB} the {@link AABB} enclosing this {@link Shape}
	 * @since 3.1.4
	 */
	public abstract AABB createAABB();
	
	/**
	 * Creates an {@link AABB} from this {@link Shape} after applying the given
	 * transformation to the shape.
	 * @param transform the {@link Transform} for this {@link Shape}
	 * @return {@link AABB} the {@link AABB} enclosing this {@link Shape}
	 * @throws NullPointerException if the given transform is null
	 * @since 3.0.0
	 */
	public abstract AABB createAABB(Transform transform);
}
