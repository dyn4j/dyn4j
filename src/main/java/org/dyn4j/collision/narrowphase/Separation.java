/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.Copyable;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a {@link Separation} of one {@link Convex} {@link Shape} between another.
 * <p>
 * The separation normal should always be normalized.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public class Separation implements Shiftable, Copyable<Separation> {
	/** The normalized axis of separation */
	protected final Vector2 normal;
	
	/** The separating distance along the axis */
	protected double distance;
	
	/** The closest point on the first {@link Convex} {@link Shape} to the second */
	protected final Vector2 point1;
	
	/** The closest point on the second {@link Convex} {@link Shape} to the first */
	protected final Vector2 point2;
	
	/**
	 * Default constructor.
	 */
	public Separation() {
		this.normal = new Vector2();
		this.distance = 0;
		this.point1 = new Vector2();
		this.point2 = new Vector2();
	}
	
	/**
	 * Full constructor.
	 * @param normal the penetration normal
	 * @param distance the separation distance
	 * @param point1 the closest point on the first {@link Convex} {@link Shape} to the second
	 * @param point2 the closest point on the second {@link Convex} {@link Shape} to the first
	 */
	protected Separation(Vector2 normal, double distance, Vector2 point1, Vector2 point2) {
		this.normal = normal.copy();
		this.distance = distance;
		this.point1 = point1.copy();
		this.point2 = point2.copy();
	}
	
	/**
	 * Copy constructor.
	 * @param separation the separation to copy
	 * @since 6.0.0
	 */
	protected Separation(Separation separation) {
		this.normal = separation.normal.copy();
		this.distance = separation.distance;
		this.point1 = separation.point1.copy();
		this.point2 = separation.point2.copy();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Separation[Point1=").append(this.point1)
		.append("|Point2=").append(this.point2)
		.append("|Normal=").append(this.normal)
		.append("|Distance=").append(this.distance)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Clears the separation information.
	 */
	public void clear() {
		this.normal.zero();
		this.distance = 0;
		this.point1.zero();
		this.point2.zero();
	}
	
	/**
	 * Returns the separation normal.
	 * @return {@link Vector2}
	 */
	public Vector2 getNormal() {
		return this.normal;
	}
	
	/**
	 * Returns the separation distance.
	 * @return double
	 */
	public double getDistance() {
		return this.distance;
	}
	
	/**
	 * Returns the closest point on the first {@link Convex} {@link Shape}.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint1() {
		return this.point1;
	}
	
	/**
	 * Returns the closest point on the second {@link Convex} {@link Shape}.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint2() {
		return this.point2;
	}
	
	/**
	 * Sets the separation normal.
	 * <p>
	 * Must be normalized.
	 * @param normal the separation normal
	 */
	public void setNormal(Vector2 normal) {
		this.normal.x = normal.x;
		this.normal.y = normal.y;
	}
	
	/**
	 * Sets the separation distance.
	 * @param distance the separation distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	/**
	 * Sets the closest point on the first {@link Convex} {@link Shape}.
	 * @param point1 the closest point on the first {@link Convex} {@link Shape}
	 */
	public void setPoint1(Vector2 point1) {
		this.point1.x = point1.x;
		this.point1.y = point1.y;
	}
	
	/**
	 * Sets the closest point on the second {@link Convex} {@link Shape}.
	 * @param point2 the closest point on the second {@link Convex} {@link Shape}
	 */
	public void setPoint2(Vector2 point2) {
		this.point2.x = point2.x;
		this.point2.y = point2.y;
	}
	
	/**
	 * Copies (deep) the given {@link Separation} information to this {@link Separation}.
	 * @param separation the separation to copy
	 * @since 4.0.0
	 * @deprecated Deprecated in 6.0.0.  Use {@link #set(Separation)} instead.
	 */
	@Deprecated
	public void copy(Separation separation) {
		this.set(separation);
	}
	
	/**
	 * Sets this separation to the given separation.
	 * @param separation the separation to use
	 * @since 6.0.0
	 */
	public void set(Separation separation) {
		this.distance = separation.distance;
		this.normal.x = separation.normal.x;
		this.normal.y = separation.normal.y;
		this.point1.x = separation.point1.x;
		this.point1.y = separation.point1.y;
		this.point2.x = separation.point2.x;
		this.point2.y = separation.point2.y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		this.point1.x += shift.x;
		this.point1.y += shift.y;
		this.point2.x += shift.x;
		this.point2.y += shift.y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	@Override
	public Separation copy() {
		return new Separation(this);
	}
}
