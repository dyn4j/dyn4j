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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

/**
 * Represents the result of a {@link Ray} cast against (intersecting with) a 
 * {@link Convex} {@link Shape}.
 * <p>
 * The point is the point on the {@link Convex} {@link Shape} where the ray
 * intersects. The normal is the normal of the edge the {@link Ray} intersects.
 * @author William Bittle
 * @version 3.0.2
 * @since 2.0.0
 */
public class Raycast {
	/** The hit point */
	protected Vector2 point;
	
	/** The normal at the hit point */
	protected Vector2 normal;
	
	/** The distance from the start of the {@link Ray} to the hit point */
	protected double distance;
	
	/**
	 * Default constructor.
	 */
	public Raycast() {}
	
	/**
	 * Full constructor.
	 * @param point the hit point
	 * @param normal the normal at the hit point
	 * @param distance the distance from the start of the {@link Ray} to the hit point
	 */
	public Raycast(Vector2 point, Vector2 normal, double distance) {
		this.point = point;
		this.normal = normal;
		this.distance = distance;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Raycast[Point=").append(this.point)
		.append("|Normal=").append(this.normal)
		.append("|Distance=").append(this.distance)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Clears this object setting all values to 
	 * their default values.
	 */
	public void clear() {
		this.point = null;
		this.normal = null;
		this.distance = 0.0;
	}
	
	/**
	 * Returns the hit point.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint() {
		return this.point;
	}
	
	/**
	 * Sets the hit point.
	 * @param point the hit point
	 */
	public void setPoint(Vector2 point) {
		this.point = point;
	}
	
	/**
	 * Returns the normal at the hit point.
	 * @return {@link Vector2}
	 */
	public Vector2 getNormal() {
		return this.normal;
	}
	
	/**
	 * Sets the normal at the hit point.
	 * @param normal the normal at the hit point
	 */
	public void setNormal(Vector2 normal) {
		this.normal = normal;
	}
	
	/**
	 * Returns the distance from the start of the
	 * {@link Ray} to the hit point.
	 * @return double
	 */
	public double getDistance() {
		return this.distance;
	}
	
	/**
	 * Sets the distance from the start of the
	 * {@link Ray} to the hit point.
	 * @param distance the distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
