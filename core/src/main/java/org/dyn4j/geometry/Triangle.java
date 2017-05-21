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

import org.dyn4j.DataContainer;

/**
 * Implementation of a Triangle {@link Convex} {@link Shape}.
 * <p>
 * A {@link Triangle} must have one vertex which is not colinear with the other two.
 * <p>
 * This class is provided to enhance performance of some of the methods contained in
 * the {@link Convex} and {@link Shape} interfaces.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class Triangle extends Polygon implements Convex, Wound, Shape, Transformable, DataContainer {
	/**
	 * Full constructor.
	 * <p>
	 * Creates a new triangle using the given points.  The center will be the area
	 * weighted center of the points.
	 * <p>
	 * A triangle must have 3 non-null points of which one is not colinear with the
	 * other two.
	 * @param point1 the first point
	 * @param point2 the second point
	 * @param point3 the third point
	 * @throws NullPointerException if point1, point2, or point3 is null
	 * @throws IllegalArgumentException if point1, point2, and point3 contain coincident points or has clockwise winding
	 */
	public Triangle(Vector2 point1, Vector2 point2, Vector2 point3) {
		super(point1, point2, point3);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Triangle[").append(super.toString()).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns true if the point is inside the {@link Triangle}.
	 * <p>
	 * The equation of a plane is:
	 * <p style="white-space: pre;"> N &middot; (P - A) = 0</p>
	 * Where A is any point on the plane. <br>
	 * Create two axes ({@link Vector2}s), we will choose V<sub>ab</sub> and V<sub>ac</sub>.
	 * <p style="white-space: pre;"> V<sub>ac</sub> = C - A
	 * V<sub>ab</sub> = B - A</p>
	 * Where A, B, and C are the vertices of the {@link Triangle}.<br>
	 * From this we can say that you can get to any point on the
	 * plane by going some u distance on V<sub>ac</sub> and some v distance on V<sub>ab</sub>
	 * where A is the origin.
	 * <p style="white-space: pre;"> P = A + u * V<sub>ac</sub> + v * V<sub>ab</sub></p>
	 * Simplifing P - A
	 * <p style="white-space: pre;"> V<sub>pa</sub> = u * V<sub>ac</sub> + v * V<sub>ab</sub></p>
	 * We still need another equation to solve for u and v:<br>
	 * Dot the equation by V<sub>ac</sub> to get
	 * <p style="white-space: pre;"> V<sub>pa</sub> &middot; V<sub>ac</sub> = (u * V<sub>ac</sub> + v * V<sub>ab</sub>) &middot; V<sub>ac</sub></p>
	 * Dot the equation by V<sub>ab</sub> to get the other
	 * <p style="white-space: pre;"> V<sub>pa</sub> &middot; V<sub>ab</sub> = (u * V<sub>ac</sub> + v * V<sub>ab</sub>) &middot; V<sub>ab</sub></p>
	 * Distribute out both equations
	 * <p style="white-space: pre;"> V<sub>pa</sub> &middot; V<sub>ac</sub> = u * V<sub>ac</sub> &middot; V<sub>ac</sub> + v * V<sub>ab</sub> &middot; V<sub>ac</sub>
	 * V<sub>pa</sub> &middot; V<sub>ab</sub> = u * V<sub>ac</sub> &middot; V<sub>ab</sub> + v * V<sub>ab</sub> &middot; V<sub>ab</sub></p>
	 * Solving the first equation for u:
	 * <p style="white-space: pre;"> u = (V<sub>pa</sub> &middot; V<sub>ac</sub> - v * V<sub>ab</sub> &middot; V<sub>ac</sub>) / (V<sub>ac</sub> &middot; V<sub>ac</sub>)</p>
	 * Substitute one into the other:
	 * <p style="white-space: pre;"> V<sub>pa</sub> &middot; V<sub>ab</sub> = (V<sub>pa</sub> &middot; V<sub>ac</sub> - v * V<sub>ab</sub> &middot; V<sub>ac</sub>) / (V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub> + v * V<sub>ab</sub> &middot; V<sub>ab</sub>
	 * V<sub>pa</sub> &middot; V<sub>ab</sub> = (V<sub>pa</sub> &middot; V<sub>ac</sub> / V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub> - v * (V<sub>ab</sub> &middot; V<sub>ac</sub> / V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub> + v * V<sub>ab</sub> &middot; V<sub>ab</sub>
	 * V<sub>pa</sub> &middot; V<sub>ab</sub> = (V<sub>pa</sub> &middot; V<sub>ac</sub> / V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub> + v * (V<sub>ab</sub> &middot; V<sub>ab</sub> - (V<sub>ab</sub> &middot; V<sub>ac</sub> / V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub>)
	 * v = (V<sub>pa</sub> &middot; V<sub>ab</sub> - (V<sub>pa</sub> &middot; V<sub>ac</sub> / V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub>) / (V<sub>ab</sub> &middot; V<sub>ab</sub> - (V<sub>ab</sub> &middot; V<sub>ac</sub> / V<sub>ac</sub> &middot; V<sub>ac</sub>) * V<sub>ac</sub> &middot; V<sub>ab</sub>)</p>
	 * Which reduces to:
	 * <p style="white-space: pre;"> v = ((V<sub>pa</sub> &middot; V<sub>ab</sub>) * (V<sub>ac</sub> &middot; V<sub>ac</sub>) - (V<sub>pa</sub> &middot; V<sub>ac</sub>) * (V<sub>ac</sub> &middot; V<sub>ab</sub>)) / ((V<sub>ab</sub> &middot; V<sub>ab</sub>) * (V<sub>ac</sub> &middot; V<sub>ac</sub>) - (V<sub>ab</sub> &middot; V<sub>ac</sub>) * (V<sub>ac</sub> &middot; V<sub>ab</sub>))</p>
	 * Once v is obtained use either equation to obtain u:
	 * <p style="white-space: pre;"> u = (v * V<sub>ab</sub> &middot; V<sub>ab</sub> - V<sub>pa</sub> &middot; V<sub>ab</sub>) / V<sub>ac</sub> &middot; V<sub>ab</sub></p>
	 * We know that the point is inside the {@link Triangle} if u and v are greater than
	 * zero and u + v is less than one.
	 * @param point world space point
	 * @param transform {@link Transform} the {@link Shape}'s transform
	 * @return boolean
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		double u, v;
		// put the point in local coordinates
		Vector2 p = transform.getInverseTransformed(point);
		// get the vertices
		Vector2 p1 = this.vertices[0];
		Vector2 p2 = this.vertices[1];
		Vector2 p3 = this.vertices[2];
		// create a vector representing edge ab
		Vector2 ab = p1.to(p2);
		// create a vector representing edge ac
		Vector2 ac = p1.to(p3);
		// create a vector from a to the point
		Vector2 pa = p1.to(p);
		
		double dot00 = ac.dot(ac);
		double dot01 = ac.dot(ab);
		double dot02 = ac.dot(pa);
		double dot11 = ab.dot(ab);
		double dot12 = ab.dot(pa);

		double denominator = dot00 * dot11 - dot01 * dot01;
		double invD = 1.0 / denominator;
		u = (dot11 * dot02 - dot01 * dot12) * invD;
		
		// don't bother going any farther if u is less than zero
		if (u <= 0) return false;
		
		v = (dot00 * dot12 - dot01 * dot02) * invD;
		
		return /*u > 0 && */v > 0 && (u + v <= 1);
	}
}
