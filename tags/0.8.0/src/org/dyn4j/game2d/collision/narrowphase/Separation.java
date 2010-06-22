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
package org.dyn4j.game2d.collision.narrowphase;

import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a {@link Separation} of one {@link Convex} {@link Shape} between another.
 * <p>
 * The separation axis should always be normalized.
 * @author William Bittle
 */
public class Separation {
	/** The normalized axis of separation */
	protected Vector normal;
	
	/** The separating distance along the axis */
	protected double distance;
	
	/** The closest point on the first {@link Convex} {@link Shape} to the second */
	protected Vector point1;
	
	/** The closest point on the second {@link Convex} {@link Shape} to the first */
	protected Vector point2;
	
	/**
	 * Default constructor.
	 */
	public Separation() {}
	
	/**
	 * Full constructor.
	 * @param normal the penetration normal
	 * @param distance the separation distance
	 * @param point1 the closest point on the first {@link Convex} {@link Shape} to the second
	 * @param point2 the closest point on the second {@link Convex} {@link Shape} to the first
	 */
	public Separation(Vector normal, double distance, Vector point1, Vector point2) {
		this.normal = normal;
		this.distance = distance;
		this.point1 = point1;
		this.point2 = point2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SEPARATION[")
		.append(this.normal).append("|")
		.append(this.distance).append("|")
		.append(this.point1).append("|")
		.append(this.point2).append("]");
		return sb.toString();
	}
	
	/**
	 * Clears the separation information.
	 */
	public void clear() {
		this.normal = null;
		this.distance = 0;
		this.point1 = null;
		this.point2 = null;
	}
	
	/**
	 * Returns the separation normal.
	 * @return {@link Vector}
	 */
	public Vector getNormal() {
		return normal;
	}
	
	/**
	 * Returns the separation distance.
	 * @return double
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Returns the closest point on the first {@link Convex} {@link Shape}.
	 * @return {@link Vector}
	 */
	public Vector getPoint1() {
		return point1;
	}
	
	/**
	 * Returns the closest point on the second {@link Convex} {@link Shape}.
	 * @return {@link Vector}
	 */
	public Vector getPoint2() {
		return point2;
	}
	
	/**
	 * Sets the separation normal.
	 * <p>
	 * Must be normalized.
	 * @param normal the separation normal
	 */
	public void setNormal(Vector normal) {
		this.normal = normal;
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
	public void setPoint1(Vector point1) {
		this.point1 = point1;
	}
	
	/**
	 * Sets the closest point on the second {@link Convex} {@link Shape}.
	 * @param point2 the closest point on the second {@link Convex} {@link Shape}
	 */
	public void setPoint2(Vector point2) {
		this.point2 = point2;
	}
}
