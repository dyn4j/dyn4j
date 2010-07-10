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
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents the Minkowski sum of the given {@link Convex} {@link Shape}s.
 * <p>
 * This class is used by the {@link Gjk} and {@link Epa} classes.
 * @author William Bittle
 */
public class MinkowskiSum {
	/** The first {@link Convex} */
	protected Convex convex1;
	
	/** The second {@link Convex} */
	protected Convex convex2;
	
	/** The first {@link Convex}'s {@link Transform} */
	protected Transform transform1;
	
	/** The second {@link Convex}'s {@link Transform} */
	protected Transform transform2;
	
	/**
	 * Represents a point in the {@link MinkowskiSum}.
	 * @author William Bittle
	 * @version $Revision: 489 $
	 */
	public static class Point {
		/** The support point in the first {@link Convex} {@link Shape} */
		protected Vector2 p1;
		
		/** The support point in the second {@link Convex} {@link Shape} */
		protected Vector2 p2;
		
		/** The Minkowski sum point */
		protected Vector2 p;
		
		/** Default constructor */
		protected Point() {}
		
		/**
		 * Full constructor.
		 * @param p1 the support point in the first {@link Convex} {@link Shape}
		 * @param p2 the support point in the second {@link Convex} {@link Shape}
		 */
		public Point(Vector2 p1, Vector2 p2) {
			this.set(p1, p2);
		}
		
		/**
		 * Sets the values of this {@link MinkowskiSum.Point} to the given values.
		 * @param p1 the support point in the first {@link Convex} {@link Shape}
		 * @param p2 the support point in the second {@link Convex} {@link Shape}
		 */
		public void set(Vector2 p1, Vector2 p2) {
			this.p1 = p1;
			this.p2 = p2;
			this.p = p1.difference(p2);
		}
		
		/**
		 * Copies the values of the given point to this point.
		 * @param p the point to copy
		 */
		public void set(MinkowskiSum.Point p) {
			this.p1 = p.p1;
			this.p2 = p.p2;
			this.p = p.p;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("MINKOWSKI_POINT[")
			.append(p1).append("|")
			.append(p2).append("|")
			.append(p).append("]");
			return sb.toString();
		}
	}
		
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
		sb.append("MINKOWSKI_SUM[")
		.append(convex1).append("|").append(transform1).append("|")
		.append(convex2).append("|").append(transform2).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the farthest point in the Minkowski sum given the direction.
	 * @param direction the search direction
	 * @return {@link Vector2} the point farthest in the given direction
	 */
	public Vector2 support(Vector2 direction) {
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
	 * Returns the farthest point in the Minkowski sum given the direction
	 * in the given {@link MinkowskiSum.Point} object.
	 * @param direction the search direction
	 * @param p the {@link MinkowskiSum.Point} object to fill
	 */
	public void support(Vector2 direction, MinkowskiSum.Point p) {
		// get the farthest point in the given direction in convex1
		Vector2 point1 = this.convex1.getFarthestPoint(direction, this.transform1);
		direction.negate();
		// get the farthest point in the opposite direction in convex2
		Vector2 point2 = this.convex2.getFarthestPoint(direction, this.transform2);
		direction.negate();
		// set the Minkowski sum point given the support points
		p.set(point1, point2);
	}
	
	/**
	 * Returns the first {@link Convex}.
	 * @return {@link Convex}
	 */
	public Convex getConvex1() {
		return convex1;
	}
	
	/**
	 * Sets the first {@link Convex}
	 * @param convex1 the first {@link Convex}
	 */
	public void setConvex1(Convex convex1) {
		this.convex1 = convex1;
	}
	
	/**
	 * Returns the second {@link Convex}.
	 * @return {@link Convex}
	 */
	public Convex getConvex2() {
		return convex2;
	}
	
	/**
	 * Sets the second {@link Convex}
	 * @param convex2 the second {@link Convex}
	 */
	public void setConvex2(Convex convex2) {
		this.convex2 = convex2;
	}
	
	/**
	 * Returns the first {@link Convex}'s {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getTransform1() {
		return transform1;
	}
	
	/**
	 * Sets the first {@link Convex}'s {@link Transform}.
	 * @param transform1 the first {@link Convex} {@link Transform}
	 */
	public void setTransform1(Transform transform1) {
		this.transform1 = transform1;
	}
	
	/**
	 * Returns the second {@link Convex}'s {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getTransform2() {
		return transform2;
	}
	
	/**
	 * Sets the second {@link Convex}'s {@link Transform}.
	 * @param transform2 the second {@link Convex} {@link Transform}
	 */
	public void setTransform2(Transform transform2) {
		this.transform2 = transform2;
	}
}
