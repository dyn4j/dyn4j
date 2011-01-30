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
package org.dyn4j.game2d.collision.continuous;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.Filter;
import org.dyn4j.game2d.collision.Fixture;
import org.dyn4j.game2d.collision.narrowphase.DistanceDetector;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Separation;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Implements the Conservative Advancement technique to solve for the time of impact.
 * <p>
 * This method assumes that translation and rotation are linear and computes the
 * time of impact within a given tolerance.
 * <p>
 * This method is based on the one found in <a href="http://bulletphysics.org">Bullet</a>.
 * @author William Bittle
 * @see <a href="http://bulletphysics.org">Bullet</a>
 * @version 2.2.3
 * @since 1.2.0
 */
public class ConservativeAdvancement implements TimeOfImpactDetector {
	/**
	 * Class used to store the minimum separation and the closest
	 * fixtures for the separation.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	protected static class MinimumSeparation {
		/** The minimum {@link Separation} */
		public Separation separation;
		
		/** The closest {@link Fixture} on the first {@link Swept} {@link Collidable} */
		public Fixture fixture1;
		
		/** The closest {@link Fixture} on the second {@link Swept} {@link Collidable} */
		public Fixture fixture2;
	}
	
	/** The default distance epsilon; around 1e-6 */
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
		if (distanceDetector == null) throw new NullPointerException("The distance detector cannot null.");
		this.distanceDetector = distanceDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.continuous.TimeOfImpactDetector#getTimeOfImpact(org.dyn4j.game2d.collision.continuous.Swept, org.dyn4j.game2d.collision.continuous.Swept, org.dyn4j.game2d.collision.continuous.TimeOfImpact)
	 */
	@Override
	public boolean getTimeOfImpact(Swept swept1, Swept swept2, TimeOfImpact toi) {
		return this.getTimeOfImpact(swept1, swept2, 0.0, 1.0, toi);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.continuous.TimeOfImpactDetector#getTimeOfImpact(org.dyn4j.game2d.collision.continuous.Swept, org.dyn4j.game2d.collision.continuous.Swept, double, double, org.dyn4j.game2d.collision.continuous.TimeOfImpact)
	 */
	public boolean getTimeOfImpact(Swept swept1, Swept swept2, double t1, double t2, TimeOfImpact toi) {
		// count the number of iterations
		int iterations = 0;
		
		// check for separation at the beginning of the interval
		MinimumSeparation distance = new MinimumSeparation();
		boolean separated = this.getSeparation(swept1, swept2, t1, distance);
		// if they are not separated then there is nothing to do
		if (!separated) {
			return false;
		}
		// get the separation object
		Separation separation = distance.separation;
		// get the distance
		double d = separation.getDistance();
		// check if the distance is less than the tolerance
		if (d < this.distanceEpsilon) {
			// fill up the toi
			toi.toi = 0.0;
			toi.separation = separation;
			toi.fixture1 = distance.fixture1;
			toi.fixture2 = distance.fixture2;
			return true;
		}
		// get the separation normal
		Vector2 n = separation.getNormal();
		
		// get the transforms
		Transform ti1 = swept1.getInitialTransform();
		Transform tf1 = swept1.getFinalTransform();
		Transform ti2 = swept2.getInitialTransform();
		Transform tf2 = swept2.getFinalTransform();
		
		// get this timestep's velocities
		Vector2 v1 = this.getLinearVelocity(ti1, tf1);
		Vector2 v2 = this.getLinearVelocity(ti2, tf2);
		double  a1 = this.getAngularVelocity(ti1, tf1);
		double  a2 = this.getAngularVelocity(ti2, tf2);
		
		// get the rotation disc radius for the swept object
		double rmax1 = swept1.getRotationDiscRadius();
		double rmax2 = swept2.getRotationDiscRadius();
		
		// compute the relative linear velocity
		Vector2 rv = v1.difference(v2);
		// compute the relative linear velocity magnitude
		double rvl = rv.getMagnitude();		
		// compute the maximum rotational velocity
		double amax = rmax1 * Math.abs(a1) + rmax2 * Math.abs(a2);
		
		// check if the bodies are moving relative to one another
		if (rvl + amax == 0.0) {
			return false;
		}
		
		// set the initial time
		double l = t1;
		// set the previous time
		double l0 = l;
		
		// loop until the distance is less than the tolerance
		while (d > this.distanceEpsilon) {
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
					return false;
				}
				// set the last time
				l0 = l;
			}
			
			// increment the number of iterations
			iterations++;
			// check if we have reach the maximum number of iterations
			if (iterations == this.maxIterations) {
				return false;
			}
			
			// find closest points
			separated = this.getSeparation(swept1, swept2, l, distance);
			// check for intersection
			if (!separated) {
				// the shapes are intersecting.  This should
				// not happen because of the conservative nature
				// of the algorithm, however because of numerical
				// error it will. Subtract epsilon from the toi
				// to back-up a bit
				l -= Epsilon.E;
				// compute a new separation
				separated = this.getSeparation(swept1, swept2, l, distance);
				// are they still penetrating
				if (!separated) {
					// if so then just quit
					return false;
				}
			}
			
			// set the new normal and distance
			separation = distance.separation;
			n = separation.getNormal();
			d = separation.getDistance();
		}
		
		// fill up the separation object
		toi.toi = l;
		toi.separation = distance.separation;
		toi.fixture1 = distance.fixture1;
		toi.fixture2 = distance.fixture2;
		
		return true;
	}
	
	/**
	 * Returns the linear velocity given the start and end {@link Transform}s.
	 * @param txi the initial {@link Transform}
	 * @param txf the final {@link Transform}
	 * @return {@link Vector2} the linear velocity
	 */
	protected Vector2 getLinearVelocity(Transform txi, Transform txf) {
		return txf.getTranslation().subtract(txi.getTranslation());
	}
	
	/**
	 * Returns the angular velocity given the start and end {@link Transform}s.
	 * @param txi the initial {@link Transform}
	 * @param txf the final {@link Transform}
	 * @return double the angular velocity
	 */
	protected double getAngularVelocity(Transform txi, Transform txf) {
		return txf.getRotation() - txi.getRotation(); 
	}
	
	/**
	 * Returns the separation object containing the smallest distance between the two
	 * {@link Swept} {@link Collidable}s.
	 * @param swept1 the first {@link Swept} {@link Collidable}
	 * @param swept2 the second {@link Swept} {@link Collidable}
	 * @param t the time in the range [0, 1] that should be tested
	 * @param distance the distance output in separation cases
	 * @return boolean true if the {@link Swept} {@link Collidable}s are separated
	 */
	protected boolean getSeparation(Swept swept1, Swept swept2, double t, MinimumSeparation distance) {
		// get the initial transforms
		Transform it1 = swept1.getInitialTransform();
		Transform it2 = swept2.getInitialTransform();
		// get the final transforms
		Transform ft1 = swept1.getFinalTransform();
		Transform ft2 = swept2.getFinalTransform();
		
		// linearly interpolate t amount from the beginning
		Transform txa = it1.lerped(ft1, t);
		Transform txb = it2.lerped(ft2, t);
		
		int f1size = swept1.getFixtureCount();
		int f2size = swept2.getFixtureCount();
		boolean separated = true;
		
		// keep track of the minimum distance fixtures
		// and separation objects
		Separation minSeparation = null;
		Fixture minFixture1 = null;
		Fixture minFixture2 = null;
		double minDistance = Double.MAX_VALUE;
		boolean found = false;
		
		// loop over each shape of the first object
		for (int k = 0; k < f1size; k++) {
			// get the shape
			Fixture fixture1 = swept1.getFixture(k);
			// ignore sensor fixtures
			if (fixture1.isSensor()) continue;
			Filter filter1 = fixture1.getFilter();
			Convex convex1 = fixture1.getShape();
			// compare the distance to every shape of
			// the second object
			for (int l = 0; l < f2size; l++) {
				// get the shape
				Fixture fixture2 = swept2.getFixture(l);
				// ignore sensor fixtures
				if (fixture2.isSensor()) continue;
				Filter filter2 = fixture2.getFilter();
				Convex convex2 = fixture2.getShape();
				
				// test the filter
				if (!filter1.isAllowed(filter2)) {
					// if the collision is not allowed then continue
					continue;
				}
				
				// get the distance using the initial transforms
				Separation temp = new Separation();
				separated = this.distanceDetector.distance(convex1, txa, convex2, txb, temp);
				
				// are they separated?
				if (!separated) {
					// if there exists a pair of shapes that
					// are not separated, then we can conclude
					// that the bodies are not separated
					return false;
				}
				
				// get the distance
				double dist = temp.getDistance();
				
				// compare the distance
				if (dist < minDistance) {
					// keep only the minimum
					minSeparation = temp;
					minDistance = dist;
					minFixture1 = fixture1;
					minFixture2 = fixture2;
					found = true;
				}
			}
		}
		
		// check if a separation was found
		if (!found) return false;
		
		// fill up the distance object
		distance.separation = minSeparation;
		distance.fixture1 = minFixture1;
		distance.fixture2 = minFixture2;
		
		// return the minimum separation
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
		if (distanceDetector == null) throw new NullPointerException("The distance detector cannot null.");
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
		if (distanceEpsilon <= 0.0) throw new IllegalArgumentException("The tolerance must be greater than zero.");
		this.distanceEpsilon = distanceEpsilon;
	}
	
	/**
	 * Returns the maximum number of iterations that will be
	 * performed by the root finder.
	 * @return the maximum number of iterations the root finder will perform
	 */
	public int getMaxIterations() {
		return maxIterations;
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
		if (maxIterations < 5) throw new IllegalArgumentException("The root finder must have a minimum of 5 iterations.");
		this.maxIterations = maxIterations;
	}
}
