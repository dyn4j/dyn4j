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
import java.util.Stack;

import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Implementation of Graham's Scan convex hull algorithm.
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
public class GrahamScan implements HullGenerator {
	/**
	 * Comparator class to compare points by their angle from the positive
	 * x-axis with reference from a given point.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	private class PointComparator implements Comparator<Vector2> {
		/** The positive x-axis */
		private final Vector2 x = new Vector2(1.0, 0.0);
		
		/** The reference point for testing polar angles */
		private Vector2 reference;
		
		/**
		 * Full constructor.
		 * @param reference the reference point for finding angles
		 */
		public PointComparator(Vector2 reference) {
			this.reference = reference;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Vector2 o1, Vector2 o2) {
			// get the vectors from p to the points
			Vector2 v1 = reference.to(o1);
			Vector2 v2 = reference.to(o2);
			// compare the vector's angles with the x-axis
			return (int) Math.signum(v2.getAngleBetween(x) - v1.getAngleBetween(x));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.hull.HullGenerator#generate(org.dyn4j.game2d.geometry.Vector2[])
	 */
	@Override
	public Vector2[] generate(Vector2... points) {
		// check for null points array
		if (points == null) throw new NullPointerException("Cannot generate a convex hull from a null point array.");
		// get the size
		int size = points.length;
		
		// check the size
		if (size == 1 || size == 2) return points;
		
		// find the point of minimum y (choose the point of minimum x if there is a tie)
		Vector2 minY = points[0];
		for (int i = 1; i < size; i++) {
			Vector2 p = points[i];
			// make sure the point is not null
			if (p == null) throw new NullPointerException("The point array cannot contain null points.");
			if (p.y < minY.y) {
				minY = p;
			} else if (p.y == minY.y) {
				if (p.x < minY.x) {
					minY = p;
				}
			}
		}
		
		// create the comparator for the array
		PointComparator pc = new PointComparator(minY);
		// sort the array by angle
		Arrays.sort(points, pc);
		
		// build the hull
		Stack<Vector2> stack = new Stack<Vector2>();
		stack.push(points[0]);
		stack.push(points[1]);
		int i = 2;
		while (i < size) {
			// if the stack size is one then just
			// push the current point onto the stack
			// thereby making a line segment
			if (stack.size() == 1) {
				stack.push(points[i]);
				i++;
				continue;
			}
			// otherwise get the top two items off the stack
			Vector2 p1 = stack.get(stack.size() - 2);
			Vector2 p2 = stack.peek();
			// get the current point
			Vector2 p3 = points[i];
			// test if the current point is to the left of the line
			// created by the top two items in the stack (the last edge
			// on the current convex hull)
			double location = Segment.getLocation(p3, p1, p2);
			if (location > 0.0) {
				// if its to the left, then push the new point on
				// the stack since it maintains convexity
				stack.push(p3);
				i++;
			} else {
				// otherwise the pop the previous point off the stack
				// since this indicates that if we added the current
				// point to the stack we would make a concave section
				stack.pop();
			}
		}
		
		// finally copy all the stack items into the array to return
		Vector2[] hull = new Vector2[stack.size()];
		stack.toArray(hull);
		
		// return the array
		return hull;
	}
}
