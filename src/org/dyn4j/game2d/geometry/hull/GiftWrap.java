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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Implementation of the Gift Wrapping convex hull algorithm.
 * <p>
 * This implementation is not sensitive to colinear points and returns only
 * the points of the convex hull.
 * <p>
 * This algorithm is O(nh) worst case where n is the number of points and h is the
 * number of sides in the convex hull.
 * <p>
 * If the input point array has a size of 1 or 2 the input point array is returned.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.0
 */
public class GiftWrap implements HullGenerator {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.hull.HullGenerator#generate(org.dyn4j.game2d.geometry.Vector2[])
	 */
	@Override
	public Vector2[] generate(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException("Cannot generate a convex hull from a null point array.");
		// get the size
		int size = points.length;
		// check the size
		if (size == 1 || size == 2) return points;
		
		// find the left most point
		double x = Double.MAX_VALUE;
		Vector2 leftMost = null;
		for (int i = 0; i < size; i++) {
			Vector2 p = points[i];
			// check for null points
			if (p == null) throw new NullPointerException("The array of points cannot contain null points.");
			// check the x cooridate
			if (p.x < x) {
				x = p.x;
				leftMost = p;
			}
		}
		
		// initialize the hull size to the worst case size
		List<Vector2> hull = new ArrayList<Vector2>(size);
		do {
			// add the left most point
			hull.add(leftMost);
			// check all the points to see if anything is more left than the next point
			Vector2 maxLeft = points[0];
			// check if the first point in the array is the leftMost point
			// if so, then we need to choose another point so that the location
			// check performs correctly
			if (maxLeft == leftMost) maxLeft = points[1];
			// loop over the points to find a more left point than the current
			for (int j = 0; j < size; j++) {
				Vector2 t = points[j];
				// don't worry about the points that create the line we are inspecting
				// since we know that they are already the left most
				if (t == maxLeft) continue;
				if (t == leftMost) continue;
				// check the point relative to the current line
				if (Segment.getLocation(t, leftMost, maxLeft) < 0.0) {
					// this point is further left than the current point
					maxLeft = t;
				}
			}
			// set the new leftMost point
			leftMost = maxLeft;
			// loop until we repeat the first leftMost point
		} while (leftMost != hull.get(0));
		
		// copy the list into an array
		Vector2[] hullPoints = new Vector2[hull.size()];
		hull.toArray(hullPoints);
		
		// return the array
		return hullPoints;
	}
}
