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
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a point in the {@link MinkowskiSum}.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class MinkowskiSumPoint {
	/** The support point in the first {@link Convex} {@link Shape} */
	final Vector2 supportPoint1;
	
	/** The support point in the second {@link Convex} {@link Shape} */
	final Vector2 supportPoint2;
	
	/** The Minkowski sum point */
	final Vector2 point;
	
	/**
	 * Full constructor.
	 * @param supportPoint1 the support point in the first {@link Convex} {@link Shape}
	 * @param supportPoint2 the support point in the second {@link Convex} {@link Shape}
	 */
	public MinkowskiSumPoint(Vector2 supportPoint1, Vector2 supportPoint2) {
		this.supportPoint1 = supportPoint1;
		this.supportPoint2 = supportPoint2;
		this.point = supportPoint1.difference(supportPoint2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MinkowskiSum.Point[Point=").append(this.point)
		.append("|SupportPoint1=").append(this.supportPoint1)
		.append("|SupportPoint2=").append(this.supportPoint2)
		.append("]");
		return sb.toString();
	}

	/**
	 * Returns the support point for the first {@link Convex} {@link Shape}.
	 * @return {@link Vector2}
	 */
	public Vector2 getSupportPoint1() {
		return this.supportPoint1;
	}

	/**
	 * Returns the support point for the second {@link Convex} {@link Shape}.
	 * @return {@link Vector2}
	 */
	public Vector2 getSupportPoint2() {
		return this.supportPoint2;
	}

	/**
	 * Returns the Minkowski sum point given the two support points.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint() {
		return this.point;
	}
}
	
