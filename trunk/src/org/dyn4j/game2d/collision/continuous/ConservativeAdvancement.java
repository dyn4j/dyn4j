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
package org.dyn4j.game2d.collision.continuous;

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
 * This method is based of the one found in <a href="http://www.box2d.org">Box2d</a>.
 * <p>
 * Uses a combination of the secant method and bisection for root finding.
 * @author William Bittle
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @version 2.0.0
 * @since 1.2.0
 */
public class ConservativeAdvancement implements TimeOfImpactDetector {
	/** The default tolerance */
	// TODO change this tolerance to Epsilon.E something
	public static final double DEFAULT_TOLERANCE = 1.0e-3;
	
	/** The distance detector */
	protected DistanceDetector distanceDetector;
	
	/** The tolerance */
	protected double tolerance = ConservativeAdvancement.DEFAULT_TOLERANCE;
	
	/** The tolerance squared */
	protected double toleranceSquared = ConservativeAdvancement.DEFAULT_TOLERANCE * ConservativeAdvancement.DEFAULT_TOLERANCE;
	
	/** The maximum number of iterations of the root finder */
	protected int maxIterations = 30;
	
	/**
	 * Default constructor.
	 * <p>
	 * Uses the {@link Gjk} as the {@link DistanceDetector}.
	 */
	public ConservativeAdvancement() {
		this.distanceDetector = new Gjk();
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
		// get the distance of the initial configuration
		Separation s1 = this.getSeparation(swept1, swept2, t1);
		
		// check for overlap
		if (s1 == null) {
			// if the bodies overlap in the beginning configuration
			// then they were already handled by the static collision
			// detector
			return false;
		}
		
		// check the distance
		if (s1.getDistance() < this.tolerance) {
			// if the distance between the bodies is less
			// than the target given some tolerance we can
			// stop and pass this along to the position solver
			
			// we are done return the toi info
			toi.toi = 0.0;
			toi.separation = s1;
			return true;
		}
		
		// get the distance of the final configuration
		Separation s2 = this.getSeparation(swept1, swept2, t2);
		
//		// check for overlap
//		if (s2 == null) {
//			// if the bodies overlap in the end configuration
//			// then we can immediately return because this collision
//			// will be detected normally by the static collision detector
//			return false;
//		}
		
//		// check the distance
//		if (s2 != null && s2.getDistance() < this.tolerance) {
//			// if the distance between the bodies is less
//			// than the target given some tolerance we can
//			// stop and pass this along to the position solver
//			
//			// we are done return the toi info
//			toi.toi = 1.0;
//			toi.separation = s2;
//			return true;
//		}
		
		// get the first separation normal
		Vector2 n1 = s1.getNormal();
		// were the bodies separated in the end configuration?
		if (s2 != null) {
			// if so then get the end configuration normal
			Vector2 n2 = s2.getNormal();
			// the normals should be opposite one another if a collision was missed
			if (n1.dot(n2) > 0.0) {
				// TODO i think we need an extra condition here to check for rotation collision
				return false;
			}
		}
		// if they are in collision at the end of the time step then
		// we must iteratively find the time of impact
		
		// find the root
		double dist = Double.MAX_VALUE;
		// set the time bounds
		double a1 = t1;
		double a2 = t2;
		// set the distance bounds
		double sep1 = s1.getDistance();
		double sep2 = s2 != null ? -s2.getDistance() : 0.0;
		// count the number of iterations
		int iterations = 0;
		for (;;) {
			// Use a mix of the secant rule and bisection.
			double t;
			if ((iterations & 1) == 1) {
				// Secant rule to improve convergence.
				// TODO check for zero denominator
				t = a1 + (0.0 - sep1) * (a2 - a1) / (sep2 - sep1);
			} else {
				// Bisection to guarantee progress.
				t = 0.5f * (a1 + a2);
			}
			
			// have we reached the maximum number of iterations?
			if (iterations == this.maxIterations) {
				return false;
			}
			
			// increment the number of iterations before
			// we reach any termination condition
			iterations++;
			
			// get the separation at the new time
			Separation ns = this.getSeparation(swept1, swept2, t);
			
			// if the separation is null then we know that the bodies are
			// overlapping here.  This doesn't mean we are done however.  We
			// need to continue to find when they first begin to overlap
			if (ns == null) {
				// if they are overlapping, make this time the new
				// upper bound
				a2 = t;
				continue;
			}
			
			// otherwise get the distance and normal
			double s = ns.getDistance();
			Vector2 n = ns.getNormal();
			
			// are we close enough to the solution given the tolerance?
			if (s < this.tolerance) {
				// if so then the current time is the time of impact
				// and the current separation is the best fit separation
				toi.toi = t;
				toi.separation = ns;
				// break from the loop, we are done
				break;
			}
			
			// check for convergence
			if (Math.abs(dist - s) < this.toleranceSquared) {
				// this condition is hit when we are converging but not to zero
				// the previous check should catch convergence to zero
				return false;
			} else {
				dist = s;
			}
			
			// where relative to the collision point is the separation?
			double sig = Math.signum(n.dot(n1));
			// if they are perp then just use 1.0
			if (sig == 0.0) sig = 1.0;
			
			s *= sig;
			
			// make sure we continue to bracket the root/refine the time interval
//			if (s * sig > 0.0) {
			if (s > 0.0) {
				a1 = t;
				sep1 = s;
			} else {
				a2 = t;
				sep2 = s;
			}
		}
		
		// once we get here we have a toi calculated within a given tolerance
		// where the bodies are in collision
		return true;
	}
	
	/**
	 * Returns the separation object containing the smallest distance between the two
	 * swept colliables.
	 * @param swept1 the first swept colliable
	 * @param swept2 the second swept collidable
	 * @param t the time in the range [0, 1] that should be tested
	 * @return {@link Separation} the minimum separation
	 */
	protected Separation getSeparation(Swept swept1, Swept swept2, double t) {
		// get the initial transforms
		Transform it1 = swept1.getInitialTransform();
		Transform it2 = swept2.getInitialTransform();
		// get the final transforms
		Transform ft1 = swept1.getFinalTransform();
		Transform ft2 = swept2.getFinalTransform();
		
		// linearly interpolate t amount from the beginning
		Transform txa = it1.lerped(ft1, t);
		Transform txb = it2.lerped(ft2, t);
		
		int f1size = swept1.getShapeCount();
		int f2size = swept2.getShapeCount();
		boolean separated = true;
		
		Separation separation = null;
		double minDistance = Double.MAX_VALUE;
		
		// loop over each shape of the first object
		for (int k = 0; k < f1size; k++) {
			// get the shape
			Convex s1 = swept1.getShape(k);
			// compare the distance to every shape of
			// the second object
			for (int l = 0; l < f2size; l++) {
				// get the shape
				Convex s2 = swept2.getShape(l);
				
				// get the distance using the initial transforms
				Separation temp = new Separation();
				separated = this.distanceDetector.distance(s1, txa, s2, txb, temp);
				
				// are they separated?
				if (!separated) {
					// if there exists a pair of shapes that
					// are not separated, then we can conclude
					// that the bodies are not separated
					return null;
				}
				
				// get the distance
				double dist = temp.getDistance();
				
				// compare the distance
				if (dist < minDistance) {
					// keep only the minimum
					separation = temp;
					minDistance = dist;
				}
			}
		}
		
		// return the minimum separation
		return separation;
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
	 */
	public void setDistanceDetector(DistanceDetector distanceDetector) {
		if (distanceDetector == null) throw new NullPointerException("The distance detector cannot be null.");
		this.distanceDetector = distanceDetector;
	}
	
	/**
	 * Returns the tolerance used to determine when a sufficient solution
	 * has been found.
	 * @return double the tolerance
	 */
	public double getTolerance() {
		return this.tolerance;
	}
	
	/**
	 * Returns the tolerance squared.
	 * @see #getTolerance()
	 * @return double the tolerance squared
	 */
	public double getToleranceSquared() {
		return this.toleranceSquared;
	}
	
	/**
	 * Sets the tolerance used to determine when a sufficient solution
	 * has been found.
	 * @param tolerance the tolerance; in the range (0, 1]
	 */
	public void setTolerance(double tolerance) {
		if (tolerance <= 0.0 || tolerance > 1.0) throw new IllegalArgumentException("The tolerance must be in the range (0, 1].");
		this.tolerance = tolerance;
		this.toleranceSquared = tolerance * tolerance;
	}
	
	/**
	 * Returns the maximum number of iterations that will be
	 * performed by the root finder.
	 * @return the maximum number of interations the root finder will perform
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
	 * @param maxIterations the maximum number of iterations in the range [10, &infin;]
	 */
	public void setMaxIterations(int maxIterations) {
		if (maxIterations < 10) throw new IllegalArgumentException("The root finder must have a minimum of 10 iterations.");
		this.maxIterations = maxIterations;
	}
}
