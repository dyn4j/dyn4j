/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.geometry.hull;

import java.util.Arrays;
import java.util.Comparator;

import org.dyn4j.game2d.geometry.Vector2;

/**
 * Implementation of the Divide and Conquer convex hull algorithm.
 * <p>
 * This implementation is not sensitive to colinear points and returns only
 * the points of the convex hull.
 * <p>
 * This algorithm is O(n log n) where n is the number of points.
 * <p>
 * If the input point array has a size of 1 or 2 the input point array is returned.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.0
 */
public class DivideAndConquer implements HullGenerator {
	/**
	 * Represents a vertex on a Hull.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	private class Vertex {
		/** The vertex point */
		public Vector2 point;
		
		/** The next vertex */
		public Vertex next;
		
		/** The previous vertex */
		public Vertex prev;
	}
	
	/**
	 * Represents a convex hull with CCW winding.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0;
	 */
	private class Hull {
		/** The root vertex (this can be any vertex on the hull) */
		public Vertex root;
		
		/** The vertex that has the smallest x coordinate */
		public Vertex leftMost;
		
		/** The vertex that has the largest x coordinate */
		public Vertex rightMost;
		
		/** The total number of vertices on the hull */
		public int size;
	}
	
	/**
	 * Represents a comparator that sorts points by their x coordinate
	 * lowest to highest.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	private class PointComparator implements Comparator<Vector2> {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Vector2 p1, Vector2 p2) {
			return (int) Math.signum(p1.x - p2.x);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.hull.HullGenerator#generate(org.dyn4j.game2d.geometry.Vector2[])
	 */
	@Override
	public Vector2[] generate(Vector2... points) {
		// check for a null array of points
		if (points == null) throw new NullPointerException("Cannot generate a convex hull from a null point array.");
		
		// get the size
		int size = points.length;
		
		// check the size
		if (size == 1 || size == 2) return points;
		
		try {
			// sort the points by the x coordinate
			Arrays.sort(points, new PointComparator());
		} catch (NullPointerException e) {
			// this will be hit if any of the points are null
			throw new NullPointerException("The point array cannot contain null points.");
		}
		
		// perform the divide and conquer algorithm on the point cloud
		Hull hull = divide(points, 0, size - 1);
		
		// add all the items in the doubly linked list to the output array
		Vector2[] hullPoints = new Vector2[hull.size];
		Vertex v = hull.root;
		for (int i = 0; i < hull.size; i++) {
			hullPoints[i] = v.point;
			v = v.next;
		}
		
		// return the array
		return hullPoints;
	}
	
	/**
	 * Recursive method to subdivide and merge the points.
	 * @param points the array of points
	 * @param first the first index
	 * @param last the last index
	 * @return {@link Hull} the convex hull created
	 */
	private Hull divide(Vector2[] points, int first, int last) {
		// compute the size of the hull we need to create
		int size = last - first;
		// check if its zero
		if (size == 0) {
			// if its zero then we only have one point
			Vertex vertex = new Vertex();
			vertex.point = points[first];
			vertex.next = null;
			vertex.prev = null;
			// create a hull containing the one point
			Hull hull = new Hull();
			hull.root = vertex;
			hull.leftMost = vertex;
			hull.rightMost = vertex;
			hull.size = 1;
			// return the hull
			return hull;
		} else {
			// otherwise find the middle index
			int mid = (first + last) / 2;
			// create the left convex hull
			Hull left = divide(points, first, mid);
			// create the right convex hull
			Hull right = divide(points, mid + 1, last);
			// merge the two convex hulls
			return merge(left, right);
		}
	}
	
	/**
	 * Merges the two given convex {@link Hull}s into one convex {@link Hull}.
	 * <p>
	 * The left {@link Hull} should contain only points whose x coordinates are
	 * less than all the points in the right {@link Hull}.
	 * @param left the left convex {@link Hull}
	 * @param right the right convex {@link Hull}
	 * @return {@link Hull} the merged convex hull
	 */
	private Hull merge(Hull left, Hull right) {
		// check the size of the hulls
		if (left.size == 1 && right.size == 1) {
			// the 1,1 case
			Vertex leftRoot = left.root;
			Vertex rightRoot = right.root;
			// wire up the hulls
			leftRoot.next = rightRoot;
			leftRoot.prev = rightRoot;
			rightRoot.next = leftRoot;
			rightRoot.prev = leftRoot;
			// create a hull
			Hull hull = new Hull();
			hull.root = leftRoot;
			hull.leftMost = leftRoot;
			hull.rightMost = rightRoot;
			hull.size = 2;
			// return the hull
			return hull;
		} else if (left.size == 1 && right.size == 2) {
			// the 1,2 case
			Hull hull = new Hull();
			hull.leftMost = left.root;
			hull.rightMost = right.rightMost;
			hull.size = 3;
			// merge the hulls into a triangle
			this.mergeTriangle(left, right, hull);
			return hull;
		} else if (left.size == 2 && right.size == 1) {
			// the 2,1 case
			Hull hull = new Hull();
			hull.leftMost = left.leftMost;
			hull.rightMost = right.root;
			hull.size = 3;
			// merge the hulls into a triangle
			this.mergeTriangle(right, left, hull);
			return hull;
		} else {
			// otherwise we need to run the algorithm to find the upper and lower edges
			// that connect the two hulls such that the resulting hull remains convex
			Hull hull = new Hull();
			hull.leftMost = left.leftMost;
			hull.rightMost = right.rightMost;
			
			// find the upper edge connection
			// start with leftmost to right most
			Vertex lu = left.rightMost;
			Vertex ru = right.leftMost;
			Vector2 upper = lu.point.to(ru.point);
			// allow the loop to go through every point on the left and right side
			// before stopping, this condition is really a catch for degenerate
			// cases.  Non-degenerate cases should hit the break; statement
			for (int i = 0; i < left.size * right.size; i++) {
				// go counter-clockwise
				Vector2 lv = lu.point.to(lu.next.point);
				// go clockwise
				Vector2 rv = ru.point.to(ru.prev.point);
				
				// what's the winding on both hulls given the upper edge connection?
				double crossR = rv.cross(upper);
				double crossL = upper.getNegative().cross(lv);
				
				// check for both convex
				if (crossR > 0.0 && crossL > 0.0) {
					// l and r contain the vertices for the upper bridge
					break;
				}
				
				// check not convex or colinear
				if (crossR <= 0.0) {
					// then we need to move clockwise on the right side by one
					ru = ru.prev;
				}
				
				// check not convex or colinear
				if (crossL <= 0.0) {
					// then we need to move counter-clockwise on the left side by one
					lu = lu.next;
				}
				
				// compute the new upper edge connection
				upper = lu.point.to(ru.point);
			}
			
			// find the lower edge connection
			Vertex ll = left.rightMost;
			Vertex rl = right.leftMost;
			Vector2 lower = ll.point.to(rl.point);
			// allow the loop to go through every point on the left and right side
			// before stopping, this condition is really a catch for degenerate
			// cases.  Non-degenerate cases should hit the break; statement
			for (int i = 0; i < left.size * right.size; i++) {
				// go clockwise
				Vector2 lv = ll.point.to(ll.prev.point);
				// go counter-clockwise
				Vector2 rv = rl.point.to(rl.next.point);
				
				// what's the winding on both hulls given the lower edge connection?
				double crossR = lower.cross(rv);
				double crossL = lv.cross(lower.getNegative());
				
				// check for both convex
				if (crossR > 0.0 && crossL > 0.0) {
					// l and r contain the vertices for the upper bridge
					break;
				}
				
				// check not convex or colinear
				if (crossR <= 0.0) {
					// then we need to move counter-clockwise on the right side by one
					rl = rl.next;
				}
				
				// check not convex or colinear
				if (crossL <= 0.0) {
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
			Vertex v0 = hull.root;
			Vertex v = v0;
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
	}
	
	/**
	 * Performs a merge of two convex hulls where one is a line segment
	 * and the other is a single point into a triangluar hull.
	 * @param one the {@link Hull} containing one point
	 * @param two the {@link Hull} containing two points
	 * @param hull the hull set the triangle to
	 */
	private void mergeTriangle(Hull one, Hull two, Hull hull) {
		// get the line segment points
		Vector2 p1 = two.root.point;
		Vector2 p2 = two.root.next.point;
		// get the point
		Vector2 p = one.root.point;
		
		// compute the winding
		Vector2 v1 = p.to(p1);
		Vector2 v2 = p1.to(p2);
		double area = v1.cross(v2);
		
		// check the winding to find where to place the point
		if (area < 0.0) {
			// then we to use p3 as the second point
			hull.root = insert(one.root, two.root, two.root.next);
		} else {
			// make sure they are in counter clockwise order
			hull.root = prepend(one.root, two.root, two.root.next);
		}
	}
	
	/**
	 * Adds the given vertex v to the doubly linked list containing
	 * vertices v1 and v2 at the beginning of the list.
	 * <p>
	 * This method changes the linked list from: v1-v2-v1 to: 
	 * v-v1-v2-v.
	 * <p>
	 * The returned vertex is the new root vertex for the hull.  The
	 * actual vertex returned is arbitrary since the root vertex can
	 * be any vertex on the hull.
	 * @param v the vertex to add
	 * @param v1 the first vertex in the doubly linked list
	 * @param v2 the second (and last) vertex in the doubly linked list
	 * @return {@link Vertex} the root vertex
	 */
	private Vertex prepend(Vertex v, Vertex v1, Vertex v2) {
		// prepend v to v1->v2->v1 to create v->v1->v2->v
		v.next = v1;
		v.prev = v2;
		v1.prev = v;
		v2.next = v;
		return v;
	}
	
	/**
	 * Adds the given vertex v to the doubly linked list containing
	 * vertices v1 and v2 in the middle of the list.
	 * <p>
	 * This method changes the linked list from: v1-v2-v1 to: 
	 * v1-v-v2-v1.
	 * <p>
	 * The returned vertex is the new root vertex for the hull.  The
	 * actual vertex returned is arbitrary since the root vertex can
	 * be any vertex on the hull.
	 * @param v the vertex to add
	 * @param v1 the first vertex in the doubly linked list
	 * @param v2 the second (and last) vertex in the doubly linked list
	 * @return {@link Vertex} the root vertex
	 */
	private Vertex insert(Vertex v, Vertex v1, Vertex v2) {
		// insert v in between v1->v2->v1 to create v1->v->v2->v1
		v.prev = v1;
		v.next = v2;
		v1.next = v;
		v2.prev = v;
		return v;
	}
}
