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

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of the Separating Axis Theorem (SAT) for collision detection.
 * <p>
 * {@link Sat} states that &quot;if two {@link Convex} objects are not penetrating, there exists an axis (vector) 
 * for which the projection of the objects does not overlap.&quot;
 * <p>
 * The axes that must be tested are <strong>all</strong> the edge normals of both {@link Convex} {@link Shape}s.  For each 
 * edge normal we project the {@link Convex} {@link Shape}s onto it yielding a 1 dimensional {@link Interval}.  If any 
 * {@link Interval} doesn't overlap, then we can conclude the {@link Convex} {@link Shape}s do not intersect.  If all the
 * {@link Interval}s overlap, then we can conclude that the {@link Convex} {@link Shape}s intersect.
 * <p>
 * If the {@link Convex} {@link Shape}s are penetrating, a {@link Penetration} object can be built from the {@link Interval}s
 * with the least overlap.  The normal will be the edge normal of the {@link Interval} and the depth will be the {@link Interval}
 * overlap.
 * @author William Bittle
 * @version 3.0.2
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/2010/01/sat/" target="_blank">SAT (Separating Axis Theorem)</a>
 */
public class Sat implements NarrowphaseDetector {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Penetration)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2, Penetration penetration) {
		// check for circles
		if (convex1 instanceof Circle && convex2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return CircleDetector.detect((Circle) convex1, transform1, (Circle) convex2, transform2, penetration);
		}
		
		penetration.clear();
		Vector2 n = null;
		double overlap = Double.MAX_VALUE;
		
		// get the foci from both shapes, the foci are used to test any
		// voronoi regions of the other shape
		Vector2[] foci1 = convex1.getFoci(transform1);
		Vector2[] foci2 = convex2.getFoci(transform2);
		
		// get the vector arrays for the separating axes tests
		Vector2[] axes1 = convex1.getAxes(foci2, transform1);
		Vector2[] axes2 = convex2.getAxes(foci1, transform2);
		
		// loop through shape1 axes
		if (axes1 != null) {
			int size = axes1.length;
			for (int i = 0; i < size; i++) {
				Vector2 axis = axes1[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// project both shapes onto the axis
	        		Interval intervalA = convex1.project(axis, transform1);
		            Interval intervalB = convex2.project(axis, transform2);
		            // if the intervals do not overlap then the two shapes
		            // cannot be intersecting
		            if (!intervalA.overlaps(intervalB)) {
		            	// the shapes cannot be intersecting so immediately return null
		            	return false;
		            } else {
		            	// get the overlap
		            	double o = intervalA.getOverlap(intervalB);
		            	// check for containment
		            	if (intervalA.contains(intervalB) || intervalB.contains(intervalA)) {
		            		// if containment exists then get the overlap plus the distance
		            		// to between the two end points that are the closest
		            		double max = Math.abs(intervalA.getMax() - intervalB.getMax());
		            		double min = Math.abs(intervalA.getMin() - intervalB.getMin());
		            		if (max > min) {
		            			// if the min differences is less than the max then we need
		            			// to flip the penetration axis
		            			axis.negate();
		            			o += min;
		            		} else {
		            			o += max;
		            		}
		            	}
		            	// if the intervals do overlap then get save the depth and axis
		            	// get the magnitude of the overlap
		            	// get the minimum penetration depth and axis
		            	if (o < overlap) {
		            		overlap = o;
		            		n = axis;
		            	}
		            }
				}
			}
		}
		
		// loop through shape2 axes
		if (axes2 != null) {
			int size = axes2.length;
			for (int i = 0; i < size; i++) {
				Vector2 axis = axes2[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// project both shapes onto the axis
	        		Interval intervalA = convex1.project(axis, transform1);
		            Interval intervalB = convex2.project(axis, transform2);
		            // if the intervals do not overlap then the two shapes
		            // cannot be intersecting
		            if (!intervalA.overlaps(intervalB)) {
		            	// the shapes cannot be intersecting so immediately return null
		            	return false;
		            } else {
		            	// if the intervals do overlap then get save the depth and axis
		            	// get the magnitude of the overlap
		            	double o = intervalA.getOverlap(intervalB);
		            	// check for containment
		            	if (intervalA.contains(intervalB) || intervalB.contains(intervalA)) {
		            		// if containment exists then get the overlap plus the distance
		            		// to between the two end points that are the closest
		            		double max = Math.abs(intervalA.getMax() - intervalB.getMax());
		            		double min = Math.abs(intervalA.getMin() - intervalB.getMin());
		            		if (max > min) {
		            			// if the min differences is less than the max then we need
		            			// to flip the penetration axis
		            			axis.negate();
		            			o += min;
		            		} else {
		            			o += max;
		            		}
		            	}
		            	// get the minimum penetration depth and axis
		            	if (o < overlap) {
		            		overlap = o;
		            		n = axis;
		            	}
		            }
				}
			}
		}
		
		// make sure the vector is pointing from shape1 to shape2
		Vector2 c1 = transform1.getTransformed(convex1.getCenter());
		Vector2 c2 = transform2.getTransformed(convex2.getCenter());
		Vector2 cToc = c1.to(c2);
		if (cToc.dot(n) < 0) {
			// negate the normal if its not
			n.negate();
		}
		
		// fill the penetration object
		penetration.normal = n;
		penetration.depth = overlap;
		// return true
        return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.NarrowphaseDetector#test(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		// check for circles
		if (convex1 instanceof Circle && convex2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return CircleDetector.detect((Circle) convex1, transform1, (Circle) convex2, transform2);
		}

		// get the foci from both shapes, the foci are used to test any
		// voronoi regions of the other shape
		Vector2[] foci1 = convex1.getFoci(transform1);
		Vector2[] foci2 = convex2.getFoci(transform2);
		
		// get the vector arrays for the separating axes tests
		Vector2[] axes1 = convex1.getAxes(foci2, transform1);
		Vector2[] axes2 = convex2.getAxes(foci1, transform2);

		// loop through shape1 axes
		if (axes1 != null) {
			int size = axes1.length;
			for (int i = 0; i < size; i++) {
				Vector2 axis = axes1[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// project both shapes onto the axis
	        		Interval intervalA = convex1.project(axis, transform1);
		            Interval intervalB = convex2.project(axis, transform2);
		            // if the intervals do not overlap then the two shapes
		            // cannot be intersecting
		            if (!intervalA.overlaps(intervalB)) {
		            	// the shapes cannot be intersecting so immediately return
		            	return false;
		            }
				}
			}
		}
		
		// loop through shape2 axes
		if (axes2 != null) {
			int size = axes2.length;
			for (int i = 0; i < size; i++) {
				Vector2 axis = axes2[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// project both shapes onto the axis
	        		Interval intervalA = convex1.project(axis, transform1);
		            Interval intervalB = convex2.project(axis, transform2);
		            // if the intervals do not overlap then the two shapes
		            // cannot be intersecting
		            if (!intervalA.overlaps(intervalB)) {
		            	// the shapes cannot be intersecting so immediately return
		            	return false;
		            }
				}
			}
		}
		
		// if we get here, then we have intersection
		return true;
	}
}
