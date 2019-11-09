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
package org.dyn4j.geometry.hull;

import org.dyn4j.geometry.Vector2;

/**
 * Represents a convex hull of {@link LinkedVertex}es.
 * <p>
 * The root vertex can be any point on the hull.
 * @author William Bittle
 * @version 3.4.0
 * @since 3.2.0
 */
final class LinkedVertexHull {
	/** The hull's root vertex */
	LinkedVertex root;

	/** The vertex that has the smallest x coordinate */
	LinkedVertex leftMost;
	
	/** The vertex that has the largest x coordinate */
	LinkedVertex rightMost;
	
	/** The total number of vertices on the hull */
	int size;
	
	/** Default constructor */
	public LinkedVertexHull() {}
	
	/**
	 * Create a convex {@link LinkedVertexHull} of one point.
	 * @param point the point
	 */
	public LinkedVertexHull(Vector2 point) {
		LinkedVertex root = new LinkedVertex(point);
		this.root = root;
		this.leftMost = root;
		this.rightMost = root;
		this.size = 1;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LinkedVertexHull[Size=").append(this.size)
		  .append("|Root=").append(this.root.point);
		return sb.toString();
	}
	
	/**
	 * Returns a new array representing this convex hull.
	 * @return {@link Vector2}[]
	 */
	public final Vector2[] toArray() {
		Vector2[] points = new Vector2[this.size];
		LinkedVertex vertex = this.root;
		for (int i = 0; i < this.size; i++) {
			points[i] = vertex.point;
			vertex = vertex.next;
		}
		return points;
	}
	
	/**
	 * Merges the two given convex {@link LinkedVertexHull}s into one convex {@link LinkedVertexHull}.
	 * <p>
	 * The left {@link LinkedVertexHull} should contain only points whose x coordinates are
	 * less than all the points in the right {@link LinkedVertexHull}.
	 * @param left the left convex {@link LinkedVertexHull}
	 * @param right the right convex {@link LinkedVertexHull}
	 * @return {@link LinkedVertexHull} the merged convex hull
	 */
	public static final LinkedVertexHull merge(LinkedVertexHull left, LinkedVertexHull right) {
		// check the size of the hulls
		if (left.size == 1 && right.size == 1) {
			// the 1,1 case
			return LinkedVertexHull.mergePointPoint(left, right);
		} else if (left.size + right.size == 3) {
			// the 1,2 or 2,1 cases
			return LinkedVertexHull.mergePointSegment(left, right);
		} else {
			// all other cases
			return LinkedVertexHull.mergeHulls(left, right);
		}
	}

	/**
	 * Merges the given left and right point hulls.
	 * @param left the left hull
	 * @param right the right hull
	 * @return {@link LinkedVertexHull} a line segment hull
	 */
	static final LinkedVertexHull mergePointPoint(LinkedVertexHull left, LinkedVertexHull right) {
		LinkedVertex leftRoot = left.root;
		LinkedVertex rightRoot = right.root;
		
		// check for coincident vertices and ignore them
		// shouldn't happen since DivideAndConquer filters them
		// before starting the divide/merge process
		double d = leftRoot.point.distanceSquared(rightRoot.point);
		if (d == 0.0) 
			return left;
		
		// wire up the hulls
		leftRoot.next = rightRoot;
		leftRoot.prev = rightRoot;
		rightRoot.next = leftRoot;
		rightRoot.prev = leftRoot;
		// create a hull
		LinkedVertexHull hull = new LinkedVertexHull();
		hull.root = leftRoot;
		hull.leftMost = leftRoot;
		hull.rightMost = rightRoot;
		hull.size = 2;
		// return the hull
		return hull;
	}
	
	/**
	 * Performs a merge of a point hull and segment hull returning a triangular hull.
	 * @param left the left hull
	 * @param right the right hull
	 * @return {@link LinkedVertexHull} a triangular hull
	 */
	static final LinkedVertexHull mergePointSegment(LinkedVertexHull left, LinkedVertexHull right) {
		// the 1,2 or 2,1 case
		LinkedVertexHull hull = new LinkedVertexHull();
		hull.size = 3;
		
		LinkedVertexHull point = left;
		LinkedVertexHull segment = right;
		if (left.size == 1) {
			// the 1,2 case
			hull.leftMost = left.root;
			hull.rightMost = right.rightMost;
		} else {
			// the 2,1 case
			hull.leftMost = left.leftMost;
			hull.rightMost = right.root;
			point = right;
			segment = left;
		}
		hull.root = point.root;
		
		// get the line segment points
		Vector2 p1 = segment.root.point;
		Vector2 p2 = segment.root.next.point;
		// get the point
		Vector2 p = point.root.point;
		
		// compute the winding
		Vector2 v1 = p.to(p1);
		Vector2 v2 = p1.to(p2);
		double area = v1.cross(v2);
		
		// check the winding to find where to place the point
		if (area < 0.0) {
			// then we use the point hull as the second point
			// insert v in between v1->v2 to create v1->v->v2
			point.root.next = segment.root.next;	// v->v2
			segment.root.next.prev = point.root;	// v2<-v
			point.root.prev = segment.root;			// v1<-v
			segment.root.next = point.root;			// v1->v
		} else if (area > 0.0) {
			// then we use the point hull as the first point
			// prepend v to v1->v2 to create v->v1->v2
			point.root.next = segment.root;			// v->v1
			segment.root.prev = point.root;			// v1<-v
			point.root.prev = segment.root.next;	// v2<-v
			segment.root.next.next = point.root;	// v2->v
		} else {
			// if the points are all colinear, then return
			// a new hull that's a segment of the extreme
			// vertices (i.e. removing one of the points)
			
			// replace v2 with v
			segment.root.next = point.root;
			point.root.next = segment.root;
			segment.root.prev = point.root;
			point.root.prev = segment.root;
			hull.size = 2;
		}
		
		return hull;
	}
	
	/**
	 * Performs a merge of the left and right hulls into one hull.
	 * @param left the left hull
	 * @param right the right hull
	 * @return {@link LinkedVertexHull} a convex hull
	 */
	static final LinkedVertexHull mergeHulls(LinkedVertexHull left, LinkedVertexHull right) {
		// otherwise we need to run the algorithm to find the upper and lower edges
		// that connect the two hulls such that the resulting hull remains convex
		LinkedVertexHull hull = new LinkedVertexHull();
		hull.leftMost = left.leftMost;
		hull.rightMost = right.rightMost;
		
		// find the upper edge connection
		// start with leftmost to right most
		LinkedVertex lu = left.rightMost;
		LinkedVertex ru = right.leftMost;
		Vector2 upper = lu.point.to(ru.point);
		upper.normalize();
		// allow the loop to go through every point on the left and right side
		// before stopping, this condition is really a catch for degenerate
		// cases.  Non-degenerate cases should hit the break; statement
		for (int i = 0; i < left.size * right.size; i++) {
			// go counter-clockwise
			Vector2 lv = LinkedVertexHull.getEdge(lu, false);
			// go clockwise
			Vector2 rv = LinkedVertexHull.getEdge(ru, true);
			
			// what's the winding on both hulls given the upper edge connection?
			double crossR = rv.cross(upper);
			double crossL = upper.getNegative().cross(lv);
			
			// this can happen when two line segments that are colinear are being merged
			if (crossR == 0.0 && crossL == 0.0 && left.size == 2 && right.size == 2) {
				// both segments are colinear so take only
				// the max and min of the two segments
				hull.root = hull.leftMost;
				hull.leftMost.next = hull.rightMost;
				hull.rightMost.next = hull.leftMost;
				hull.leftMost.prev = hull.rightMost;
				hull.rightMost.prev = hull.leftMost;
				hull.size = 2;
				return hull;
			}

			// check for both convex
			if (crossR >= 0.0 && crossL >= 0.0) {
				// l and r contain the vertices for the upper bridge
				break;
			}

			// check not convex or colinear
			if (crossR < 0.0) {
				// then we need to move clockwise on the right side by one
				ru = ru.prev;
			}
			
			// check not convex or colinear
			if (crossL < 0.0) {
				// then we need to move counter-clockwise on the left side by one
				lu = lu.next;
			}
			
			// compute the new upper edge connection
			upper = lu.point.to(ru.point);
		}
		
		// find the lower edge connection
		LinkedVertex ll = left.rightMost;
		LinkedVertex rl = right.leftMost;
		Vector2 lower = ll.point.to(rl.point);
		lower.normalize();
		// allow the loop to go through every point on the left and right side
		// before stopping, this condition is really a catch for degenerate
		// cases.  Non-degenerate cases should hit the break; statement
		for (int i = 0; i < left.size * right.size; i++) {
			// go clockwise
			Vector2 lv = LinkedVertexHull.getEdge(ll, true);
			// go counter-clockwise
			Vector2 rv = LinkedVertexHull.getEdge(rl, false);

			// what's the winding on both hulls given the lower edge connection?
			double crossR = lower.cross(rv);
			double crossL = lv.cross(lower.getNegative());
			
			// this can happen when two line segments that are colinear are being merged
			if (crossR == 0.0 && crossL == 0.0 && left.size == 2 && right.size == 2) {
				// both segments are colinear
				// NOTE: it shouldn't be possible to get here
				hull.root = hull.leftMost;
				hull.leftMost.next = hull.rightMost;
				hull.rightMost.next = hull.leftMost;
				hull.leftMost.prev = hull.rightMost;
				hull.rightMost.prev = hull.leftMost;
				hull.size = 2;
				return hull;
			}

			// check for both convex
			if (crossR >= 0.0 && crossL >= 0.0) {
				// l and r contain the vertices for the upper bridge
				break;
			}
			
			// check not convex or colinear
			if (crossR < 0.0) {
				// then we need to move counter-clockwise on the right side by one
				rl = rl.next;
			}
			
			// check not convex or colinear
			if (crossL < 0.0) {
				// then we need to move clockwise on the left side by one
				ll = ll.prev;
			}
			
			// compute the new lower edge connection
			lower = ll.point.to(rl.point);
		}
		
		// wire up the two hulls using the lower and upper edge
		// connections found above (the other vertices will be
		// garbage collected)
		lu.prev = ru;
		ru.next = lu;
		ll.next = rl;
		rl.prev = ll;
		
		// set the root of the hull to any of the bridge points
		// or any point on the convex hull
		hull.root = lu;
		
		// count the number of points
		LinkedVertex v0 = hull.root;
		LinkedVertex v = v0;
		int size = 0;
		do {
			size ++;
			v = v.next;
		} while (v != v0);
		// set the size
		hull.size = size;
		
		// return the merged hull
		return hull;
	}
	
	/**
	 * Returns the clockwise or anti-clockwise edge from the given vertex and
	 * checks for numeric precision loss.
	 * @param vertex the vertex
	 * @param clockwise the desired winding
	 * @return {@link Vector2}
	 */
	private static final Vector2 getEdge(LinkedVertex vertex, boolean clockwise) {
		Vector2 a = vertex.point;
		Vector2 b = clockwise ? vertex.prev.point : vertex.next.point;
		Vector2 n = a.to(b);
		
		// check for lost precision due to large points and small points being subtracted
		// NOTE: we could normalize all the time, but since we aren't comparing the dot/cross
		//		 products, we only need to normalize those that lose precision
		double d1 = a.getMagnitudeSquared();
		double d2 = b.getMagnitudeSquared();
		double dx = Math.abs(d1 - d2);
		if (dx == d1 || dx == d2) {
			// if we get precision loss, we need to normalize the edge
			n.normalize();
		}
		return n;
	}
}
