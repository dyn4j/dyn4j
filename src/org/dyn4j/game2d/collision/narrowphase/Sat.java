/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.collision.narrowphase;

import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Implementation of the Separating Axis Theorem (SAT) for penetration detection.
 * <p>
 * {@link Sat} states that &quot;if two {@link Convex} objects are not penetrating, there exists an axis 
 * for which the projection of the objects does not overlap.&quot;
 * <p>
 * Get all the separating axes for the first {@link Shape}, which are given by retrieving the normal (
 * or perpendicular {@link Vector}) of each edge of the {@link Shape}.  Project both {@link Shape}s onto 
 * each axis, if any projection does not overlap, then there is no collision.<br />
 * If none of the above axes fail, then do the same process on the second {@link Shape}.<br />
 * If both of the above do not fail then there is a collision.
 * <p>
 * If there is a collision, one can obtain the penetration {@link Vector} and depth from scaling the axis
 * by the projection overlap.
 * @author William Bittle
 */
public class Sat extends AbstractNarrowphaseDetector implements NarrowphaseDetector {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.collision.narrowphase.Penetration)
	 */
	@Override
	public boolean detect(Convex s1, Transform t1, Convex s2, Transform t2, Penetration p) {
		// check for circles
		if (s1 instanceof Circle && s2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return super.detect((Circle) s1, t1, (Circle) s2, t2, p);
		}
		
		Vector n = null;
		double overlap = Double.MAX_VALUE;
		
		// get the foci from both shapes, the foci are used to test any
		// voronoi regions of the other shape
		Vector[] foci1 = s1.getFoci(t1);
		Vector[] foci2 = s2.getFoci(t2);
		
		// get the vector arrays for the separating axes tests
		Vector[] axes1 = s1.getAxes(foci2, t1);
		Vector[] axes2 = s2.getAxes(foci1, t2);
		
		// loop through shape1 axes
		if (axes1 != null) {
			int size = axes1.length;
			for (int i = 0; i < size; i++) {
				Vector axis = axes1[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// normalize the vector first so we can get an accurate penetration depth
					axis.normalize();
					// project both shapes onto the axis
	        		Interval intervalA = s1.project(axis, t1);
		            Interval intervalB = s2.project(axis, t2);
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
				Vector axis = axes2[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// normalize the vector first so we can get an accurate penetration depth
					axis.normalize();
					// project both shapes onto the axis
	        		Interval intervalA = s1.project(axis, t1);
		            Interval intervalB = s2.project(axis, t2);
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
		Vector c1 = t1.getTransformed(s1.getCenter());
		Vector c2 = t2.getTransformed(s2.getCenter());
		Vector cToc = c1.to(c2);
		if (cToc.dot(n) < 0) {
			// negate the normal if its not
			n.negate();
		}
		
		// fill the penetration object
		p.normal = n;
		p.depth = overlap;
		// return true
        return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector#test(org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex s1, Transform t1, Convex s2, Transform t2) {
		// check for circles
		if (s1 instanceof Circle && s2 instanceof Circle) {
			// if its a circle - circle collision use the faster method
			return super.detect((Circle) s1, t1, (Circle) s2, t2);
		}

		// get the foci from both shapes, the foci are used to test any
		// voronoi regions of the other shape
		Vector[] foci1 = s1.getFoci(t1);
		Vector[] foci2 = s2.getFoci(t2);
		
		// get the vector arrays for the separating axes tests
		Vector[] axes1 = s1.getAxes(foci2, t1);
		Vector[] axes2 = s2.getAxes(foci1, t2);

		// loop through shape1 axes
		if (axes1 != null) {
			int size = axes1.length;
			for (int i = 0; i < size; i++) {
				Vector axis = axes1[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// normalize the vector first so we can get an accurate penetration depth
					axis.normalize();
					// project both shapes onto the axis
	        		Interval intervalA = s1.project(axis, t1);
		            Interval intervalB = s2.project(axis, t2);
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
				Vector axis = axes2[i];
				// check for the zero vector
				if (!axis.isZero()) {
					// normalize the vector first so we can get an accurate penetration depth
					axis.normalize();
					// project both shapes onto the axis
	        		Interval intervalA = s1.project(axis, t1);
		            Interval intervalB = s2.project(axis, t2);
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
