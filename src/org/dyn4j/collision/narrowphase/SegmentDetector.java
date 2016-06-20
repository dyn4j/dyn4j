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

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Class devoted to improving performance of {@link Segment} detection queries.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 */
public final class SegmentDetector {
	/**
	 * Hidden constructor.
	 */
	private SegmentDetector() {}
	
	/**
	 * Performs a ray cast against the given segment.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum ray length
	 * @param segment the {@link Segment}
	 * @param transform the {@link Segment}'s {@link Transform}
	 * @param raycast the {@link Raycast} result
	 * @return boolean true if the ray intersects the segment
	 * @since 2.0.0
	 */
	public static boolean raycast(Ray ray, double maxLength, Segment segment, Transform transform, Raycast raycast) {
		// solve the problem algebraically
		Vector2 p0 = ray.getStart();
		Vector2 d0 = ray.getDirectionVector();
		Vector2 p1 = transform.getTransformed(segment.getPoint1());
		Vector2 p2 = transform.getTransformed(segment.getPoint2());
		Vector2 d1 = p1.to(p2);
		
		// make sure the start of the ray is not on the segment
		if (segment.contains(p0, transform)) return false;
		
		// any point on a ray can be found by the parametric equation:
		// P = tD0 + P0
		// any point on a segment can be found by:
		// P = sD1 + P1
		// substituting the first equation into the second yields:
		// tD0 + P0 = sD1 + P1
		// solve for s and t:
		// tD0.x + P0.x = sD1.x + P1.x
		// tD0.y + P0.y = sD1.y + P1.y
		// solve the first equation for s
		// s = (tD0.x + P0.x - P1.x) / D1.x
		// substitute into the second equation
		// tD0.y + P0.y = ((tD0.x + P0.x - P1.x) / D1.x) * D1.y + P1.y
		// solve for t
		// tD0.yD1.x + P0.yD1.x = tD0.xD1.y + P0.xD1.y - P1.xD1.y + P1.yD1.x
		// t(D0.yD1.x - D0.xD1.y) = P0.xD1.y - P0.yD1.x + D1.xP1.y - D1.yP1.x
		// t(D0.yD1.x - D0.xD1.y) = P0.cross(D1) + D1.cross(P1)
		// since the cross product is anti-cummulative
		// t(D0.yD1.x - D0.xD1.y) = -D1.cross(P0) + D1.cross(P1)
		// t(D0.yD1.x - D0.xD1.y) = D1.cross(P1) - D1.cross(P0)
		// t(D0.yD1.x - D0.xD1.y) = D1.cross(P1 - P0)
		// tD1.cross(D0) = D1.cross(P1 - P0)
		// t = D1.cross(P1 - P0) / D1.cross(D0)
		Vector2 p0ToP1 = p1.difference(p0);
		double num = d1.cross(p0ToP1);
		double den = d1.cross(d0);
		
		// check for zero denominator
		if (Math.abs(den) <= Epsilon.E) {
			// they are parallel but could be overlapping
			
			// since they are parallel d0 is the direction for both the
			// segment and the ray; ie d0 = d1
			
			// get the common direction's normal
			Vector2 n = d0.cross(1.0);
			// project a point from each onto the normal
			double nDotP0 = n.dot(p0);
			double nDotP1 = n.dot(p1);
			// project the segment and ray onto the common direction's normal
			if (Math.abs(nDotP0 - nDotP1) < Epsilon.E) {
				// if their projections are close enough then they are
				// on the same line
				
				// project the points onto the common direction
				double d0DotP0 = d0.dot(p0);
				
				// these projections must be positive
				double d0DotP1 = d0.dot(p1);
				double d0DotP2 = d0.dot(p2);
				
				if (d0DotP1 < 0.0 || d0DotP2 < 0.0) {
					// if either point is behind the ray
					return false;
				}
				
				// if both are along the ray then compute the
				// distance and point of intersection
				double d = 0.0;
				Vector2 p = null;
				if (d0DotP1 < d0DotP2) {
					d = d0DotP1 - d0DotP0;
					p = p1.copy();
				} else {
					d = d0DotP2 - d0DotP0;
					p = p2.copy();
				}
				
				// make sure the distance is not greater than the maximum
				if (maxLength > 0.0 && d > maxLength) {
					return false;
				}
				
				// set the raycast fields
				raycast.distance = d;
				raycast.point = p;
				raycast.normal = d0.getNegative();
				return true;
			} else {
				// parallel but not overlapping
				return false;
			}
		}
		
		// compute t
		double t = num / den;
		
		// t should be in the range t >= 0.0
		if (t < 0.0) {
			return false;
		}
		
		// t should be less than the maximum length
		if (maxLength > 0.0 && t > maxLength) {
			return false;
		}
		
		// compute s
		double s = (t * d0.x + p0.x - p1.x) / d1.x;
		
		// s should be in the range 0.0 <= s <= 1.0
		if (s < 0.0 || s > 1.0) {
			return false;
		}
		
		// compute the hit point
		Vector2 p = d0.product(t).add(p0);
		// compute the normal
		Vector2 l = p1.to(p2); l.normalize(); l.right();
		double lDotD = l.dot(d0);
		if (lDotD > 0.0) {
			l.negate();
		}
		
		// populate the raycast result
		raycast.point = p;
		raycast.normal = l;
		raycast.distance = t;
		
		// return success
		return true;
	}
}
