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
package org.dyn4j.geometry.decompose;

import org.dyn4j.Reference;
import org.dyn4j.geometry.Vector2;

/**
 * Represents an edge of a polygon storing the next and previous edges
 * and the vertices that make up this edge.
 * <p>
 * The edge also stores a helper vertex which is used during y-monotone
 * decomposition.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
final class SweepLineEdge implements Comparable<SweepLineEdge> {
	/** The current state of the sweep; a reference value shared between all edges (for context when sorting) */
	final Reference<Double> referenceY;
	
	/** The first vertex of the edge in Counter-Clockwise order */
	SweepLineVertex v0;
	
	/** The second vertex of the edge in Counter-Clockwise order */
	SweepLineVertex v1;
	
	/** The helper vertex of this edge */
	SweepLineVertex helper;
	
	/** 
	 * The inverted slope of the edge (run/rise); This will be 
	 * Double.POSITIVE_INFINITY if its a horizontal edge
	 */
	double slope;
	
	/**
	 * Minimal constructor.
	 * @param referenceY the current sweep position
	 */
	public SweepLineEdge(Reference<Double> referenceY) {
		this.referenceY = referenceY;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.v0)
		  .append(" to ")
		  .append(this.v1);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SweepLineEdge o) {
		// check for equality
		if (this == o) return 0;
		
		// compare the intersection of the sweep line and the edges
		// to see which is to the left or right
		double y = this.referenceY.value;
		
		double x1 = this.getSortValue(y);
		double x2 = o.getSortValue(y);
		
		if (x1 < x2) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * Returns the intersection point of the given y value (horizontal
	 * sweep line) with this edge.
	 * <p>
	 * Returns the x value of the corresponding intersection point.
	 * @param y the horizontal line y value
	 * @return double
	 */
	public double getSortValue(double y) {
		// get the minimum x vertex
		// (if we use the min x vertex rather than an 
		// arbitrary one, we can save a step to check
		// if the edge is vertical)
		Vector2 min = this.v0.point;
		if (this.v1.point.x < this.v0.point.x) {
			min = this.v1.point;
		}
		// check for a horizontal line
		if (this.slope == Double.POSITIVE_INFINITY) {
			// for horizontal lines, use the min x
			return min.x;
		} else {
			// otherwise compute the intersection point
			return min.x + (y - min.y) * this.slope;
		}
	}
	
	/**
	 * Returns true if the interior of the polygon is
	 * to the right of this edge.
	 * <p>
	 * Given that the polygon's vertex winding is Counter-
	 * Clockwise, if the vertices that make this edge
	 * decrease along the y axis then the interior of the
	 * polygon is to the right, otherwise its to the
	 * left.
	 * @return boolean
	 */
	public boolean isInteriorRight() {
		double diff = v0.point.y - v1.point.y;
		// check if the points have the same y value
		if (diff == 0.0) {
			// if they do, is the vector of the
			// two points to the right or to the left
			if (v0.point.x < v1.point.x) {
				return true;
			} else {
				return false;
			}
		// otherwise just compare the y values
		} else if (diff > 0.0) {
			return true;
		} else {
			return false;
		}
	}
}

