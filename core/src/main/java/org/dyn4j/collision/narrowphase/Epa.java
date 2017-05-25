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

import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * {@link Epa}, or Expanding Polytope Algorithm, is used to find the 
 * penetration depth and vector given the final simplex of {@link Gjk}.
 * <p>
 * {@link Epa} expands the given simplex in the direction of the origin until it cannot
 * be expanded any further.  {@link Gjk} guarantees that the simplex points are on the
 * edge of the Minkowski sum which creates a convex polytope from which to start the
 * {@link Epa} algorithm.
 * <p> 
 * Expansion is achieved by breaking edges of the simplex.  Find the edge on the simplex
 * closest to the origin, then use that edge's normal to find another support point (using 
 * the same support method that {@link Gjk} uses).  Add the new support point to 
 * the simplex between the points that made the closest edge.  Repeat this process until
 * the polytope cannot be expanded further.
 * <p>
 * This implementation has three termination cases:
 * <ul>
 * <li>If the new support point is not past the edge along the edge normal given some epsilon.</li>
 * <li>If the distance between the last support point and the new support point is below a given epsilon.</li>
 * <li>Maximum iteration count.</li>
 * </ul>
 * Once {@link Epa} terminates, the penetration vector is the current closest edge normal
 * and the penetration depth is the distance from the origin to the edge along the normal.
 * <p>
 * {@link Epa} will terminate in a finite number of iterations if the two shapes are {@link Polygon}s.
 * If either shape has curved surfaces the algorithm requires an expected accuracy epsilon: {@link #distanceEpsilon}.
 * In the case that the {@link #distanceEpsilon} is too small, the {@link #maxIterations} will prevent the
 * algorithm from running forever.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @see Gjk
 * @see <a href="http://www.dyn4j.org/2010/05/epa-expanding-polytope-algorithm/" target="_blank">EPA (Expanding Polytope Algorithm)</a>
 */
public class Epa implements MinkowskiPenetrationSolver {
	/** The default {@link Epa} maximum iterations */
	public static final int DEFAULT_MAX_ITERATIONS = 100;

	/** The default {@link Epa} distance epsilon in meters; near 1E-8 */
	public static final double DEFAULT_DISTANCE_EPSILON = Math.sqrt(Epsilon.E);
	
	/** The maximum number of {@link Epa} iterations */
	protected int maxIterations = Epa.DEFAULT_MAX_ITERATIONS;

	/** The {@link Epa} distance epsilon in meters */
	protected double distanceEpsilon = Epa.DEFAULT_DISTANCE_EPSILON;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.MinkowskiPenetrationSolver#getPenetration(java.util.List, org.dyn4j.collision.narrowphase.MinkowskiSum, org.dyn4j.collision.narrowphase.Penetration)
	 */
	public void getPenetration(List<Vector2> simplex, MinkowskiSum minkowskiSum, Penetration penetration) {
		// create an expandable simplex
		ExpandingSimplex smplx = new ExpandingSimplex(simplex);
		ExpandingSimplexEdge edge = null;
		Vector2 point = null;
		for (int i = 0; i < this.maxIterations; i++) {
			// get the closest edge to the origin
			edge = smplx.getClosestEdge();
			// get a new support point in the direction of the edge normal
			point = minkowskiSum.getSupportPoint(edge.normal);
			
			// see if the new point is significantly past the edge
			double projection = point.dot(edge.normal);
			if ((projection - edge.distance) < this.distanceEpsilon) {
				// then the new point we just made is not far enough
				// in the direction of n so we can stop now and
				// return n as the direction and the projection
				// as the depth since this is the closest found
				// edge and it cannot increase any more
				penetration.normal = edge.normal;
				penetration.depth = projection;
				return;
			}
			
			// lastly add the point to the simplex
			// this breaks the edge we just found to be closest into two edges
			// from a -> b to a -> newPoint -> b
			smplx.expand(point);
		}
		// if we made it here then we know that we hit the maximum number of iterations
		// this is really a catch all termination case
		// set the normal and depth equal to the last edge we created
		penetration.normal = edge.normal;
		penetration.depth = point.dot(edge.normal);
	}
	
	/**
	 * Returns the maximum number of iterations the algorithm will perform before exiting.
	 * @return int
	 * @see #setMaxIterations(int)
	 */
	public int getMaxIterations() {
		return this.maxIterations;
	}

	/**
	 * Sets the maximum number of iterations the algorithm will perform before exiting.
	 * @param maxIterations the maximum number of iterations in the range [5, &infin;]
	 * @throws IllegalArgumentException if maxIterations is less than 5
	 */
	public void setMaxIterations(int maxIterations) {
		if (maxIterations < 5) throw new IllegalArgumentException(Messages.getString("collision.narrowphase.epa.invalidMaximumIterations"));
		this.maxIterations = maxIterations;
	}

	/**
	 * Returns the distance epsilon.
	 * @return double
	 * @see #setDistanceEpsilon(double)
	 */
	public double getDistanceEpsilon() {
		return this.distanceEpsilon;
	}

	/**
	 * The minimum distance between two iterations of the algorithm.
	 * <p>
	 * The distance epsilon is used to determine when the algorithm is close enough to the
	 * edge of the minkowski sum to conclude that it can no longer expand.  This is primarily
	 * used when one of the {@link Convex} {@link Shape}s in question has a curved shape.
	 * @param distanceEpsilon the distance epsilon in the range (0, &infin;]
	 * @throws IllegalArgumentException if distanceEpsilon is less than or equal to zero
	 */
	public void setDistanceEpsilon(double distanceEpsilon) {
		if (distanceEpsilon <= 0) throw new IllegalArgumentException(Messages.getString("collision.narrowphase.epa.invalidDistanceEpsilon"));
		this.distanceEpsilon = distanceEpsilon;
	}
}
