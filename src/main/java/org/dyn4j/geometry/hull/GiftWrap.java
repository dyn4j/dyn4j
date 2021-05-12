/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.geometry.hull;

import java.util.LinkedHashSet;
import java.util.Set;

import org.dyn4j.geometry.RobustGeometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of the Gift Wrapping convex hull algorithm.
 * <p>
 * This algorithm handles coincident and colinear points by ignoring them during processing. This ensures
 * the produced hull will not have coincident or colinear vertices.
 * <p>
 * This algorithm is O(nh) worst case where n is the number of points and h is the
 * number of sides in the resulting convex hull.
 * @author William Bittle
 * @version 4.2.0
 * @since 2.2.0
 */
public class GiftWrap extends AbstractHullGenerator implements HullGenerator {
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.hull.HullGenerator#generate(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public Vector2[] generate(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException(Messages.getString("geometry.hull.nullArray"));
		
		// get the size
		int size = points.length;
		// check the size
		if (size <= 2) return points;
		
		// find the left most point
		double x = Double.MAX_VALUE;
		double y = Double.MAX_VALUE;
		Vector2 leftMost = null;
		for (int i = 0; i < size; i++) {
			Vector2 p = points[i];
			// check for null points
			if (p == null) throw new NullPointerException(Messages.getString("geometry.hull.nullPoints"));
			// check the x cooridate
			if (p.x < x) {
				x = p.x;
				leftMost = p;
				y = p.y;
			} else if (p.x == x && p.y < y) {
				x = p.x;
				leftMost = p;
				y = p.y;
			}
		}
		
		Vector2 current = leftMost;
		
		// use a linked hash set to maintain insertion order
		// but also to have the set property of no duplicates
		Set<Vector2> hull = new LinkedHashSet<Vector2>();
		do {
			hull.add(current);
			// check all the points to see if anything is more left than the next point
			Vector2 next = points[0];
			if (current == next) next = points[1];
			// loop over the points to find a more left point than the current
			for (int j = 1; j < size; j++) {
				Vector2 test = points[j];
				if (test == current) continue;
				if (test == next) continue;
				// check the point relative to the current line
				// Use the robust side of line test because otherwise this algorithm
				// can fall in an endless loop
				double location = RobustGeometry.getLocation(test, current, next);
				if (location < 0.0) {
					next = test;
				} else if (location == 0.0) {
					// in the case of colinear or coincident verticies
					// only select this vertex if it's farther away
					// than the current vertex
					double d1 = test.distanceSquared(current);
					double d2 = next.distanceSquared(current);
					// we also need to confirm that it's farther away in
					// the direction of the current->next vector
					double dot = current.to(next).dot(current.to(test));
					if (d1 > d2 && dot >= 0) {
						next = test;
					} else {
						// if it's not farther, compute the winding
						Vector2 l1 = current.to(next);
						Vector2 l2 = next.to(test);
						double cross = l1.cross(l2);
						
						// if the winding is anti-clockwise but the location test
						// yielded they were colinear, then we encountered 
						// sufficient numeric error - trust the winding
						// in these cases
						if (cross < 0.0) {
							next = test;
						}
					}
				}
			}
			
			current = next;
			// loop until we repeat the first leftMost point
		} while (leftMost != current);
		
		// copy the list into an array
		Vector2[] hullPoints = new Vector2[hull.size()];
		hull.toArray(hullPoints);

		// return the array
		return hullPoints;
	}
	
}