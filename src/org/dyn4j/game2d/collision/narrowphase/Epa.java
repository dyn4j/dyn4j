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
package org.dyn4j.game2d.collision.narrowphase;

import java.util.List;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * {@link Epa} stands for Expanding Polytope Algorithm and is used to find the 
 * penetration depth and vector given a resulting {@link Gjk} simplex.
 * <p>
 * {@link Epa} expands the given simplex in the direction of the origin until it cannot
 * be expanded any further.  {@link Gjk} guarantees that the simplex points are on the
 * edge of the Minkowski sum which creates a convex polytope.
 * <p> 
 * Expansion is achieved by breaking edges of the simplex.  Find the edge on the simplex
 * closest to the origin.  Then use that edge's normal to find another support point (using 
 * the same support method that {@link Gjk} uses).  Add the new support point to 
 * the simplex between the points that made the closest edge.  Repeat this process until
 * the polytope cannot be expanded any more.
 * <p>
 * This implementation has three termination cases:
 * <ul>
 * <li>If the new support point is not past the edge along the edge normal given some epsilon.</li>
 * <li>If the distance between the last support point and the new support point 
 * is below a given epsilon.</li>
 * <li>Maximum iteration count.</li>
 * </ul>
 * Once {@link Epa} terminates the penetration vector is the current closest edge normal
 * and the penetration depth is the distance from the origin to the edge along the normal.
 * <p>
 * {@link Epa} will terminate in a finite number of iterations if the two shapes are {@link Polygon}s.
 * If either shape has curved surfaces the algorithm requires an expected accuracy epsilon.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
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
	
	/**
	 * Represents an {@link Edge} of the simplex.
	 * @author William Bittle
	 * @version 1.0.3
	 * @since 1.0.0
	 */
	protected class Edge {
		/** The distance from the origin to the edge along n */
		public double distance;
		
		/** The edge normal */
		public Vector2 n;
		
		/** The edge index in the simplex */
		public int index;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("EDGE[")
			.append(n).append("|")
			.append(distance).append("|")
			.append(index).append("]");
			return sb.toString();
		}
	}
	
	/**
	 * Returns the penetration in the given penetration object given the simplex
	 * created by {@link Gjk} and the {@link MinkowskiSum}.
	 * @param simplex the simplex
	 * @param minkowskiSum the {@link MinkowskiSum}
	 * @param penetration the {@link Penetration} object to fill
	 */
	@Override
	public void getPenetration(List<Vector2> simplex, MinkowskiSum minkowskiSum, Penetration penetration) {
		// this method is called from the GJK detect method and therefore we can assume
		// that the simplex has 3 points
		
		// get the winding of the simplex points
		// the winding may be different depending on the points added by GJK
		// however EPA will preserve the winding so we only need to compute this once
		int winding = this.getWinding(simplex);
		// store the last point added to the simplex
		Vector2 point = null;
		// the current closest edge
		Edge edge = null;
		// start the loop
		for (int i = 0; i < this.maxIterations; i++) {
			// get the closest edge to the origin
			edge = this.findClosestEdge(simplex, winding);
			// get a new support point in the direction of the edge normal
			point = minkowskiSum.support(edge.n);
			
			// see if the new point is significantly past the edge
			double projection = point.dot(edge.n);
			if ((projection - edge.distance) < this.distanceEpsilon) {
				// then the new point we just made is not far enough
				// in the direction of n so we can stop now and
				// return n as the direction and the projection
				// as the depth since this is the closest found
				// edge and it cannot increase any more
				penetration.normal = edge.n;
				penetration.depth = projection;
				return;
			}
			
			// lastly add the point to the simplex
			// this breaks the edge we just found to be closest into two edges
			// from a -> b to a -> newPoint -> b
			simplex.add(edge.index, point);
		}
		// if we made it here then we know that we hit the maximum number of iterations
		// this is really a catch all termination case
		// set the normal and depth equal to the last edge we created
		penetration.normal = edge.n;
		penetration.depth = point.dot(edge.n);
	}
	
	/**
	 * Returns the edge on the simplex that is closest to the origin.
	 * @param simplex the simplex
	 * @param winding the simplex winding
	 * @return {@link Edge} the closest edge to the origin
	 */
	protected Edge findClosestEdge(List<Vector2> simplex, int winding) {
		// get the current size of the simplex
		int size = simplex.size();
		// create an edge
		Edge edge = new Edge();
		// set edge's distance to the max double value
		edge.distance = Double.MAX_VALUE;
		// find the edge on the simplex closest to the origin
		for (int i = 0; i < size; i++) {
			// compute j
			int j = i + 1 == size ? 0 : i + 1;
			// get the points that make up the current edge
			Vector2 a = simplex.get(i);
			Vector2 b = simplex.get(j);
			// create the edge
			Vector2 normal = a.to(b);
			// depending on the winding get the edge normal
			// it would be better to use Vector.tripleProduct(ab, ao, ab);
			// where ab is the edge and ao is a.to(ORIGIN) but this will
			// return an incorrect normal if the origin lies on the ab segment
			// therefore we use the winding of the simplex to determine the 
			// normal direction
			if (winding < 0) {
				normal.right();
			} else {
				normal.left();
			}
			// normalize the vector
			normal.normalize();
			// project the first point onto the normal (it doesnt matter which
			// you project since the normal is perpendicular to the edge)
			double d = Math.abs(a.dot(normal));
			// record the closest edge
			if (d < edge.distance) {
				edge.distance = d;
				edge.n = normal;
				edge.index = j;
			}
		}
		// return the closest edge
		return edge;
	}
	
	/**
	 * Returns the winding of the given simplex.
	 * <p>
	 * Returns -1 if the winding is Clockwise.<br />
	 * Returns 1 if the winding is Counter-Clockwise.
	 * <p>
	 * This method will continue checking all edges until
	 * an edge is found whose cross product is less than 
	 * or greater than zero.
	 * <p>
	 * This is used to get the correct edge normal of
	 * the simplex.
	 * @param simplex the simplex
	 * @return int the winding
	 */
	protected int getWinding(List<Vector2> simplex) {
		int size = simplex.size();
		for (int i = 0; i < size; i++) {
			int j = i + 1 == size ? 0 : i + 1;
			Vector2 a = simplex.get(i);
			Vector2 b = simplex.get(j);
			if (a.cross(b) > 0) {
				return 1;
			} else if (a.cross(b) < 0) {
				return -1;
			}
		}
		return 0;
	}
	
	/**
	 * Returns the maximum number of EPA iterations.
	 * @return int the maximum number of EPA iterations
	 * @see #setMaxIterations(int)
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * Sets the maximum number of EPA iterations.
	 * <p>
	 * Valid values are in the range [5, &infin;].
	 * @param maxIterations the maximum number of EPA iterations
	 * @throws IllegalArgumentException if maxIterations is less than 5
	 */
	public void setMaxIterations(int maxIterations) {
		if (maxIterations < 5) throw new IllegalArgumentException("The EPA penetration depth algorithm requires 5 or more iterations.");
		this.maxIterations = maxIterations;
	}

	/**
	 * Returns the EPA distance epsilon.
	 * @return double the EPA distance epsilon
	 * @see #setDistanceEpsilon(double)
	 */
	public double getDistanceEpsilon() {
		return distanceEpsilon;
	}

	/**
	 * The minimum distance between two iterations of the EPA algorithm.
	 * <p>
	 * Valid values are in the range (0, &infin;].
	 * @param distanceEpsilon the EPA distance epsilon
	 * @throws IllegalArgumentException if distanceEpsilon is less than or equal to zero
	 */
	public void setDistanceEpsilon(double distanceEpsilon) {
		if (distanceEpsilon <= 0) throw new IllegalArgumentException("The EPA distance epsilon must be larger than zero.");
		this.distanceEpsilon = distanceEpsilon;
	}
}
