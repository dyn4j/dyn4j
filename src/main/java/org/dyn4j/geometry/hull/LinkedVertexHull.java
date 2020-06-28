/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.geometry.RobustGeometry;
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
		.append("|LeftMostPoint=").append(this.leftMost.point)
		.append("|RightMostPoint=").append(this.rightMost.point);
		return sb.toString();
	}
	
	/**
	 * Returns a new array representing this convex hull.
	 * @return {@link Vector2}[]
	 */
	public final Vector2[] toArray() {
		Vector2[] points = new Vector2[this.size];
		LinkedVertex vertex = this.leftMost;
		
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
		// This merge algorithm handles all cases, including point-point and point-segment without special cases.
		// It finds the upper and lower edges that connect the two hulls such that the resulting hull remains convex
		
		LinkedVertexHull hull = new LinkedVertexHull();
		hull.leftMost = left.leftMost;
		hull.rightMost = right.rightMost;
		
		LinkedVertex lu = left.rightMost;
		LinkedVertex ru = right.leftMost;
		
		// We don't use strict inequalities when checking the result of getLocation
		// so we can remove coincident points in the hull.
		// As a result we must limit the number of loops that go to the left or right
		// because else ru = ru.prev can loop over and never terminate
		// We can walk at most side.size - 1 before looping over
		int limitRightU = right.size - 1;
		int limitLeftU = left.size - 1;
		
		while (true) {
			LinkedVertex prevLu = lu;
			LinkedVertex prevRu = ru;
			
			while (limitRightU > 0 && RobustGeometry.getLocation(ru.next.point, lu.point, ru.point) <= 0) {
				ru = ru.next;
				limitRightU--;
			}
			
			while (limitLeftU > 0 && RobustGeometry.getLocation(lu.prev.point, lu.point, ru.point) <= 0) {
				lu = lu.prev;
				limitLeftU--;
			}
			
			// If no progress is made there's nothing else to do
			if (lu == prevLu && ru == prevRu) {
				break;
			}
		}
		
		// Same as before, for the other side
		
		LinkedVertex ll = left.rightMost;
		LinkedVertex rl = right.leftMost;
		
		int limitRightL = right.size - 1;
		int limitLeftL = left.size - 1;
		
		while (true) {
			LinkedVertex prevLl = ll;
			LinkedVertex prevRl = rl;
			
			while (limitRightL > 0 && RobustGeometry.getLocation(rl.prev.point, ll.point, rl.point) >= 0) {
				rl = rl.prev;
				limitRightL--;
			}
			
			while (limitLeftL > 0 && RobustGeometry.getLocation(ll.next.point, ll.point, rl.point) >= 0) {
				ll = ll.next;
				limitLeftL--;
			}
			
			// If no progress is made there's nothing else to do
			if (ll == prevLl && rl == prevRl) {
				break;
			}
		}
		
		// link the hull
		lu.next = ru;
		ru.prev = lu;
		
		ll.prev = rl;
		rl.next = ll;
		
		// We could compute size with a closed-form type based on the four values
		// of limitLeft/Right/L/U but it is not straightforward and there is no observable
		// speed gain. So use a simple loop instead
		int size = 0;
		LinkedVertex v = lu;
		
		do {
			size ++;
			v = v.next;
		} while (v != lu);
		
		// set the size
		hull.size = size;
		
		// return the merged hull
		return hull;
	}
}
