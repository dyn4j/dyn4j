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

import org.dyn4j.geometry.Vector2;

/**
 * Represents an edge of an {@link ExpandingSimplex}.
 * <p>
 * An {@link ExpandingSimplexEdge} tracks its vertices, the edge normal, and the
 * distance to the origin.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
final class ExpandingSimplexEdge implements Comparable<ExpandingSimplexEdge> {
	/** The first point of the edge */
	final Vector2 point1;
	
	/** The second point of the edge */
	final Vector2 point2;
	
	/** The normal of the edge */
	final Vector2 normal;
	
	/** The perpendicular distance from the edge to the origin */
	final double distance;

	/**
	 * Minimal constructor.
	 * @param point1 the first point
	 * @param point2 the second point
	 * @param winding the winding
	 */
	public ExpandingSimplexEdge(Vector2 point1, Vector2 point2, int winding) {
		// create the edge
		// inline b - a
		this.normal = new Vector2(point2.x - point1.x, point2.y - point1.y);
		// depending on the winding get the edge normal
		// it would be better to use Vector.tripleProduct(ab, ao, ab);
		// where ab is the edge and ao is a.to(ORIGIN) but this will
		// return an incorrect normal if the origin lies on the ab segment
		// therefore we use the winding of the simplex to determine the 
		// normal direction
		if (winding < 0) {
			this.normal.right();
		} else {
			this.normal.left();
		}
		// normalize the vector
		this.normal.normalize();
		// project the first point onto the normal (it doesnt matter which
		// you project since the normal is perpendicular to the edge)
		//double d = Math.abs(a.dot(normal));
		this.distance = Math.abs(point1.x * this.normal.x + point1.y * this.normal.y);
		this.point1 = point1;
		this.point2 = point2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ExpandingSimplexEdge o) {
		if (this.distance < o.distance) return -1;
		if (this.distance > o.distance) return 1;
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ExpandingSimplexEdge[Point1=").append(this.point1)
		  .append("|Point2=").append(this.point2)
		  .append("|Normal=").append(this.normal)
		  .append("|Distance=").append(this.distance)
		  .append("]");
		return sb.toString();
	}
}
