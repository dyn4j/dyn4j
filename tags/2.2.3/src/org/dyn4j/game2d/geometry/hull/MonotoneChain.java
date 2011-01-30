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
 * Implementation of the Andrew's Monotone Chain convex hull algorithm.
 * <p>
 * This implementation is not sensitive to colinear points and returns only
 * the points of the convex hull.
 * <p>
 * This algorithm is O(n log n) worst case where n is the number of points.
 * <p>
 * If the input point array has a size of 1 or 2 the input point array is returned.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.0
 */
public class MonotoneChain implements HullGenerator {
	/**
	 * Represents a comparator that sorts points by their x coordinate
	 * lowest to highest then by the y coordinate.
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
			// first sort on the x coordinate
			int value = (int) Math.signum(p1.x - p2.x);
			// check for equal
			if (value == 0) {
				// if they are equal then sort on the y coordinate
				return (int) Math.signum(p1.y - p2.y);
			} else {
				return value;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.hull.HullGenerator#generate(org.dyn4j.game2d.geometry.Vector2[])
	 */
	@Override
	public Vector2[] generate(Vector2... points) {
		// check for a null array
		if (points == null) throw new NullPointerException("Cannot generate a convex hull from a null point array.");
		
		// get the size
		int size = points.length;
		// check the size
		if (size == 1 || size == 2) return points;
		
		try {
			// sort the points
			Arrays.sort(points, new PointComparator());
		} catch (NullPointerException e) {
			// if any comparison generates a null pointer exception
			// throw a null pointer exception with a good message
			throw new NullPointerException("The array of points cannot contain null points.");
		}
		
		// find the points whose x values are the smallest and largest
		int minmin = 0, minmax = 0, maxmin = 0, maxmax = 0;
		// minmin == minmax if there exists only one point who has the smallest
		// x coordinate, likewise, maxmin == maxmax if there exists only one point
		// who has the largest x coordinate
		for (int i = 1; i < size; i++) {
			// get the current values
			Vector2 minxminy = points[minmin];
			Vector2 minxmaxy = points[minmax];
			Vector2 maxxminy = points[maxmin];
			Vector2 maxxmaxy = points[maxmax];
			// get the current point
			Vector2 p = points[i];
			// check against the minimum x
			if (p.x < minxminy.x) {
				// its the new minimum
				minmin = i;
				minmax = i;
			} else if (p.x == minxminy.x) {
				// if they are equal then we need to 
				// check the y coordinate
				if (p.y > minxmaxy.y) {
					minmax = i;
				} else if (p.y < minxminy.y) {
					minmin = i;
				}
			}
			// check against the maximum x
			if (p.x > maxxminy.x) {
				// its the new maximum
				maxmin = i;
				maxmax = i;
			} else if (p.x == maxxminy.x) {
				// if they are equall then we need to 
				// check the y coordinate
				if (p.y > maxxmaxy.y) {
					maxmax = i;
				} else if (p.y < maxxminy.y) {
					maxmin = i;
				}
			} 
		}
		
		// build the lower convex hull
		Stack<Vector2> lower = new Stack<Vector2>();
		Vector2 lp1 = points[maxmin];
		Vector2 lp2 = points[minmin];
		lower.push(points[minmin]);
		// loop over the points between the min and max
		for (int i = minmax + 1; i <= maxmin; i++) {
			// get the current point
			Vector2 p = points[i];
			// where is it relative to the dividing line?
			if (Segment.getLocation(p, lp1, lp2) >= 0.0) {
				// if its on or to the left of the dividing line
				// check if this invalidates any points currently
				// in the convex hull
				while (lower.size() >= 2) {
					Vector2 p1 = lower.peek();
					Vector2 p2 = lower.get(lower.size() - 2);
					// check if the point is to the left of the
					// last edge in the current convex hull
					if (Segment.getLocation(p, p2, p1) > 0.0) {
						// if so, we can safely add the new point and
						// maintain convexity
						break;
					}
					// otherwise we need to remove the top point because
					// it creates a concavity
					lower.pop();
				}
				// when we are done always add the point
				// (it will be removed later if it creates a concavity)
				lower.push(p);
			}
		}
		
		// build the upper convex hull
		Stack<Vector2> upper = new Stack<Vector2>();
		Vector2 up1 = points[minmax];
		Vector2 up2 = points[maxmax];
		upper.push(points[maxmax]);
		// loop over the points between the min and max
		for (int i = maxmax - 1; i >= minmax; i--) {
			// get the current point
			Vector2 p = points[i];
			// where is it relative to the dividing line?
			if (Segment.getLocation(p, up1, up2) >= 0.0) {
				// if its on or to the left of the dividing line
				// check if this invalidates any points currently
				// in the convex hull
				while (upper.size() >= 2) {
					Vector2 p1 = upper.peek();
					Vector2 p2 = upper.get(upper.size() - 2);
					// check if the point is to the left of the
					// last edge in the current convex hull
					if (Segment.getLocation(p, p2, p1) > 0.0) {
						// if so, we can safely add the new point and
						// maintain convexity
						break;
					}
					// otherwise we need to remove the top point because
					// it creates a concavity
					upper.pop();
				}
				// when we are done always add the point
				// (it will be removed later if it creates a concavity)
				upper.push(p);
			}
		}
		
		// check if the first element of the upper hull is the same as
		// the last element of the lower hull
		if (upper.firstElement() == lower.lastElement()) {
			upper.remove(0);
		}
		// likewise check the first element of the lower hull with the
		// last element of the upper hull
		if (lower.firstElement() == upper.lastElement()) {
			lower.remove(0);
		}
		// append all the upper hull points to the lower hull
		// creating the complete convex hull
		lower.addAll(upper);
		
		// create and fill an array with the hull points
		Vector2[] hull = new Vector2[lower.size()];
		lower.toArray(hull);
		
		// return the hull
		return hull;
	}
}
