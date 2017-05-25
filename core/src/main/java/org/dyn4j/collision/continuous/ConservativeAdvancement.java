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
package org.dyn4j.collision.continuous;

import org.dyn4j.Epsilon;
import org.dyn4j.collision.narrowphase.DistanceDetector;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implements the Conservative Advancement technique to solve for the time of impact.
 * <p>
 * This method assumes that translation and rotation are linear and computes the
 * time of impact within a given tolerance.
 * <p>
 * This method is described in "Continuous Collision Detection and Physics" by Erwin Coumans (Draft).
 * @author William Bittle
 * @version 3.1.5
 * @since 1.2.0
 */
public class ConservativeAdvancement implements TimeOfImpactDetector {
	/** The default distance epsilon */
	public static final double DEFAULT_DISTANCE_EPSILON = Math.cbrt(Epsilon.E);
	
	/** The default maximum number of iterations */
	public static final int DEFAULT_MAX_ITERATIONS = 30;
	
	/** The distance detector */
	protected DistanceDetector distanceDetector = new Gjk();
	
	/** The tolerance */
	protected double distanceEpsilon = ConservativeAdvancement.DEFAULT_DISTANCE_EPSILON;
	
	/** The maximum number of iterations of the root finder */
	protected int maxIterations = ConservativeAdvancement.DEFAULT_MAX_ITERATIONS;
	
	/**
	 * Default constructor.
	 * <p>
	 * Uses {@link Gjk} as the {@link DistanceDetector}.
	 */
	public ConservativeAdvancement() {}
	
	/**
	 * Optional constructor.
	 * @param distanceDetector the distance detector
	 * @throws NullPointerException if distanceDetector is null
	 */
	public ConservativeAdvancement(DistanceDetector distanceDetector) {
		if (distanceDetector == null) throw new NullPointerException(Messages.getString("collision.continuous.conservativeAdvancement.nullDistanceDetector"));
		this.distanceDetector = distanceDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.continuous.TimeOfImpactDetector#getTimeOfImpact(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.collision.continuous.TimeOfImpact)
	 */
	@Override
	public boolean getTimeOfImpact(Convex convex1, Transform transform1, Vector2 dp1, double da1, Convex convex2, Transform transform2, Vector2 dp2, double da2, TimeOfImpact toi) {
		return this.getTimeOfImpact(convex1, transform1, dp1, da1, convex2, transform2, dp2, da2, 0.0, 1.0, toi);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.continuous.TimeOfImpactDetector#getTimeOfImpact(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, double, double, org.dyn4j.collision.continuous.TimeOfImpact)
	 */
	@Override
	public boolean getTimeOfImpact(Convex convex1, Transform transform1, Vector2 dp1, double da1, Convex convex2, Transform transform2, Vector2 dp2, double da2, double t1, double t2, TimeOfImpact toi) {
		// count the number of iterations
		int iterations = 0;
		
		// create some reusable transforms for interpolation
		Transform lerpTx1 = new Transform();
		Transform lerpTx2 = new Transform();
		
		// check for separation at the beginning of the interval
		Separation separation = new Separation();
		boolean separated = this.distanceDetector.distance(convex1, transform1, convex2, transform2, separation);
		// if they are not separated then there is nothing to do
		if (!separated) {
			return false;
		}
		// get the distance
		double d = separation.getDistance();
		// check if the distance is less than the tolerance
		if (d < this.distanceEpsilon) {
			// fill up the toi
			toi.time = 0.0;
			toi.separation = separation;
			return true;
		}
		// get the separation normal
		Vector2 n = separation.getNormal();
		
		// get the rotation disc radius for the swept object
		double rmax1 = convex1.getRadius();
		double rmax2 = convex2.getRadius();
		
		// compute the relative linear velocity
		Vector2 rv = dp1.difference(dp2);
		// compute the relative linear velocity magnitude
		double rvl = rv.getMagnitude();		
		// compute the maximum rotational velocity
		double amax = rmax1 * Math.abs(da1) + rmax2 * Math.abs(da2);
		
		// check if the bodies are moving relative to one another
		if (rvl + amax == 0.0) {
			return false;
		}
		
		// set the initial time
		double l = t1;
		// set the previous time
		double l0 = l;
		
		// loop until the distance is less than the tolerance
		while (d > this.distanceEpsilon && iterations < this.maxIterations) {
			// project the relative max velocity along the separation normal
			double rvDotN = rv.dot(n);
			// compute the max relative velocity
			double drel = rvDotN + amax;
			// is the relative velocity along the normal and the maximum
			// rotation velocity less than epsilon
			if (drel <= Epsilon.E) {
				return false;
			} else {
				// compute the time to advance
				double dt = d / drel;
				// advance the time
				l += dt;
				// if l drops below the minimum time
				if (l < t1) {
					return false;
				}
				// if l goes above the maximum time
				if (l > t2) {
					return false;
				}
				// if l doesn't change significantly
				if (l <= l0) {
					// l hasn't changed so just return with
					// what we have now
					break;
				}
				// set the last time
				l0 = l;
			}
			
			// increment the number of iterations
			iterations++;
			
			// interpolate to time
			transform1.lerp(dp1, da1, l, lerpTx1);
			transform2.lerp(dp2, da2, l, lerpTx2);
			
			// find closest points
			separated = this.distanceDetector.distance(convex1, lerpTx1, convex2, lerpTx2, separation);
			d = separation.getDistance();
			// check for intersection
			if (!separated) {
				// the shapes are intersecting.  This should
				// not happen because of the conservative nature
				// of the algorithm, however because of numeric
				// error it will.
				
				// back up to half the distance epsilon
				l -= 0.5 * this.distanceEpsilon / drel;
				// interpolate
				transform1.lerp(dp1, da1, l, lerpTx1);
				transform2.lerp(dp2, da2, l, lerpTx2);
				// compute a new separation
				this.distanceDetector.distance(convex1, lerpTx1, convex2, lerpTx2, separation);
				// get the distance
				d = separation.getDistance();
				// the separation here could still be close to zero if the
				// objects are rotating very fast, in which case just assume
				// this is as close as we can get
				
				// break from the loop since we have detected the
				// time of impact but had to fix the distance
				break;
			}
			
			// set the new normal and distance
			n = separation.getNormal();
			d = separation.getDistance();
		}
		
		// fill up the separation object
		toi.time = l;
		toi.separation = separation;
		
		return true;
	}
	
	/**
	 * Returns the {@link DistanceDetector} that is used.
	 * @return {@link DistanceDetector}
	 */
	public DistanceDetector getDistanceDetector() {
		return this.distanceDetector;
	}
	
	/**
	 * Sets the {@link DistanceDetector} to be used.
	 * @param distanceDetector the distance detector
	 * @throws NullPointerException if distanceDetector is null
	 */
	public void setDistanceDetector(DistanceDetector distanceDetector) {
		if (distanceDetector == null) throw new NullPointerException(Messages.getString("collision.continuous.conservativeAdvancement.nullDistanceDetector"));
		this.distanceDetector = distanceDetector;
	}
	
	/**
	 * Returns the distance epsilon used to determine when a sufficient solution
	 * has been found.
	 * @return double the distance epsilon
	 */
	public double getDistanceEpsilon() {
		return this.distanceEpsilon;
	}
	
	/**
	 * Sets the distance epsilon used to determine when a sufficient solution
	 * has been found.
	 * @param distanceEpsilon the distance epsilon; must be greater than zero
	 * @throws IllegalArgumentException if distanceEpsilon is less than or equal to zero
	 */
	public void setDistanceEpsilon(double distanceEpsilon) {
		if (distanceEpsilon <= 0.0) throw new IllegalArgumentException(Messages.getString("collision.continuous.conservativeAdvancement.invalidDistanceEpsilon"));
		this.distanceEpsilon = distanceEpsilon;
	}
	
	/**
	 * Returns the maximum number of iterations that will be
	 * performed by the root finder.
	 * @return the maximum number of iterations the root finder will perform
	 */
	public int getMaxIterations() {
		return this.maxIterations;
	}
	
	/**
	 * Sets the maximum number of iterations that will be
	 * performed by the root finder.
	 * <p>
	 * Lower values increase performance yet decrease accuracy whereas
	 * higher values decrease performance and increase accuracy.
	 * @param maxIterations the maximum number of iterations in the range [5, &infin;]
	 * @throws IllegalArgumentException if maxIterations is less than 5
	 */
	public void setMaxIterations(int maxIterations) {
		if (maxIterations < 5) throw new IllegalArgumentException(Messages.getString("collision.continuous.conservativeAdvancement.invalidMaximumIterations"));
		this.maxIterations = maxIterations;
	}
}
