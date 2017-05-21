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

import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a vertex on a polygon that stores information
 * about the left and right edges and left and right vertices.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
final class SweepLineVertex implements Comparable<SweepLineVertex> {
	/** The vertex point */
	final Vector2 point;
	
	/** The index in the original simple polygon */
	final int index;
	
	/** The vertex type */
	SweepLineVertexType type;
	
	/** The next vertex in Counter-Clockwise order */
	SweepLineVertex next;
	
	/** The previous vertex in Counter-Clockwise order */
	SweepLineVertex prev;
	
	/** The next edge in Counter-Clockwise order */
	SweepLineEdge left;
	
	/** The previous edge in Counter-Clockwise order */
	SweepLineEdge right;
	
	/**
	 * Minimal constructor.
	 * @param point the vertex point
	 * @param index the index in the original simple polygon
	 */
	public SweepLineVertex(Vector2 point, int index) {
		this.point = point;
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SweepLineVertex other) {
		// sort by the y first then by x if the y's are equal
		Vector2 p = this.point;
		Vector2 q = other.point;
		double diff = q.y - p.y;
		if (diff == 0.0) {
			// if the difference is near equal then compare the x values
			return (int) Math.signum(p.x - q.x);
		} else {
			return (int) Math.signum(diff);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.point.toString();
	}
	
	/**
	 * Returns true if this {@link SweepLineVertex} is left of the given {@link SweepLineEdge}.
	 * @param edge the {@link SweepLineEdge}
	 * @return boolean true if this {@link SweepLineVertex} is to the left of the given {@link SweepLineEdge}
	 */
	final boolean isLeft(SweepLineEdge edge) {
		// its in between the min and max x so we need to 
		// do a side of line test
		double location = Segment.getLocation(this.point, edge.v0.point, edge.v1.point);
		if (location < 0.0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if the interior is to the right of this vertex.
	 * <p>
	 * The left edge of this vertex is used to determine where the
	 * interior of the polygon is.
	 * @return boolean
	 */
	public boolean isInteriorRight() {
		return this.left.isInteriorRight();
	}
}