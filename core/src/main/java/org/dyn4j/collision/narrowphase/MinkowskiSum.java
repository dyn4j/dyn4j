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
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Represents the Minkowski sum of the given {@link Convex} {@link Shape}s.
 * <p>
 * This class is used by the {@link Gjk} and {@link Epa} classes to compute support points.
 * <p>
 * This class doesn't actually compute the Minkowski sum.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class MinkowskiSum {
	/** The first {@link Convex} */
	final Convex convex1;
	
	/** The second {@link Convex} */
	final Convex convex2;
	
	/** The first {@link Convex}'s {@link Transform} */
	final Transform transform1;
	
	/** The second {@link Convex}'s {@link Transform} */
	final Transform transform2;
		
	/**
	 * Full constructor.
	 * @param convex1 the first {@link Convex}
	 * @param transform1 the first {@link Convex}'s {@link Transform}
	 * @param convex2 the second {@link Convex}
	 * @param transform2 the second {@link Convex}'s {@link Transform}
	 */
	public MinkowskiSum(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		this.convex1 = convex1;
		this.convex2 = convex2;
		this.transform1 = transform1;
		this.transform2 = transform2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MinkowskiSum[Convex1=").append(this.convex1.getId())
		.append("|Transform1=").append(this.transform1)
		.append("|Convex2=").append(this.convex2.getId())
		.append("|Transform2=").append(this.transform2)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the farthest point in the Minkowski sum given the direction.
	 * @param direction the search direction
	 * @return {@link Vector2} the point farthest in the Minkowski sum in the given direction 
	 */
	public final Vector2 getSupportPoint(Vector2 direction) {
		// get the farthest point in the given direction in convex1
		Vector2 point1 = this.convex1.getFarthestPoint(direction, this.transform1);
		direction.negate();
		// get the farthest point in the opposite direction in convex2
		Vector2 point2 = this.convex2.getFarthestPoint(direction, this.transform2);
		direction.negate();
		// return the Minkowski sum point
		return point1.subtract(point2);
	}
	
	/**
	 * Returns the farthest point, and the support points in the shapes, in the Minkowski sum given the direction.
	 * @param direction the search direction
	 * @return {@link MinkowskiSumPoint} the point farthest in the Minkowski sum in the given direction 
	 */
	public final MinkowskiSumPoint getSupportPoints(Vector2 direction) {
		// get the farthest point in the given direction in convex1
		Vector2 point1 = this.convex1.getFarthestPoint(direction, this.transform1);
		direction.negate();
		// get the farthest point in the opposite direction in convex2
		Vector2 point2 = this.convex2.getFarthestPoint(direction, this.transform2);
		direction.negate();
		// set the Minkowski sum point given the support points
		return new MinkowskiSumPoint(point1, point2);
	}

	/**
	 * Returns the first {@link Convex} {@link Shape}.
	 * @return {@link Convex}
	 */
	public Convex getConvex1() {
		return this.convex1;
	}

	/**
	 * Returns the second {@link Convex} {@link Shape}.
	 * @return {@link Convex}
	 */
	public Convex getConvex2() {
		return this.convex2;
	}

	/**
	 * Returns the first {@link Convex} {@link Shape}'s {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getTransform1() {
		return this.transform1;
	}

	/**
	 * Returns the second {@link Convex} {@link Shape}'s {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getTransform2() {
		return this.transform2;
	}
}
