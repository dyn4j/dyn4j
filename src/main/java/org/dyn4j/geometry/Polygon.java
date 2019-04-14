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
package org.dyn4j.geometry;

import java.util.Iterator;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.resources.Messages;

/**
 * Implementation of an arbitrary polygon {@link Convex} {@link Shape}.
 * <p>
 * A {@link Polygon} must have at least 3 vertices where one of which is not colinear with the other two.
 * A {@link Polygon} must also be {@link Convex} and have counter-clockwise winding of points.
 * <p>
 * A polygon cannot have coincident vertices.
 * @author William Bittle
 * @version 3.3.1
 * @since 1.0.0
 */
public class Polygon extends AbstractShape implements Convex, Wound, Shape, Transformable, DataContainer {
	private static final int FAST_FARTHEST_POINT_THRESHOLD = 10;
	private static final int FAST_AABB_THRESHOLD = 4 * FAST_FARTHEST_POINT_THRESHOLD;
	
	/** The polygon vertices */
	final Vector2[] vertices;
	
	/** The polygon normals */
	final Vector2[] normals;
	
	/** Precomputed step for optimized search
	 * Because searchStep = sqrt(n) we want to avoid computing the square root every time. */
	final int searchStep;
	
	/**
	 * Full constructor. Assumes everything is valid.
	 * Allows access to sub classes.
	 * @param center the center
	 * @param radius the rotation radius
	 * @param vertices the vertices
	 * @param normals the normals
	 */
	Polygon(Vector2 center, double radius, Vector2[] vertices, Vector2[] normals) {
		super(center, radius);
		this.vertices = vertices;
		this.normals = normals;
		
		int size = this.vertices.length;
		if (size >= FAST_FARTHEST_POINT_THRESHOLD) {
			// We can find the farthest point along a vector in O(n/step + step) steps
			// so the optimal selection for step is sqrt(n) to get O(sqrt(n))
			// Also note that this algorithm is more complex so we enable it only for sufficient vertex count
			this.searchStep = (int) (Math.sqrt(size) + 0.5);
		} else {
			// If the polygon has few vertices switch to the linear search
			this.searchStep = 1;
		}
	}
	
	/**
	 * Validated constructor.
	 * <p>
	 * Creates a new {@link Polygon} using the given vertices.  The center of the polygon
	 * is calculated using an area weighted method.
	 * @param valid always true or this constructor would not be called
	 * @param vertices the polygon vertices
	 * @param center the center of the polygon
	 */
	private Polygon(boolean valid, Vector2[] vertices, Vector2 center) {
		this(center, Geometry.getRotationRadius(center, vertices), vertices, Geometry.getCounterClockwiseEdgeNormals(vertices));
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a new {@link Polygon} using the given vertices.  The center of the polygon
	 * is calculated using an area weighted method.
	 * <p>
	 * A polygon must have 3 or more vertices, of which one is not colinear with the other two.
	 * <p>
	 * A polygon must also be convex and have counter-clockwise winding.
	 * @param vertices the array of vertices
	 * @throws NullPointerException if vertices is null or contains a null element
	 * @throws IllegalArgumentException if vertices contains less than 3 points, contains coincident points, is not convex, or has clockwise winding
	 */
	public Polygon(Vector2... vertices) {
		this(validate(vertices), vertices, Geometry.getAreaWeightedCenter(vertices));
	}
	
	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param vertices the array of vertices
	 * @return boolean true
	 * @throws NullPointerException if vertices is null or contains a null element
	 * @throws IllegalArgumentException if vertices contains less than 3 points, contains coincident points, is not convex, or has clockwise winding
	 */
	private static final boolean validate(Vector2... vertices) {
		// check the vertex array
		if (vertices == null) throw new NullPointerException(Messages.getString("geometry.polygon.nullArray"));
		// get the size
		int size = vertices.length;
		// check the size
		if (size < 3) throw new IllegalArgumentException(Messages.getString("geometry.polygon.lessThan3Vertices"));
		// check for null vertices
		for (int i = 0; i < size; i++) {
			if (vertices[i] == null) throw new NullPointerException(Messages.getString("geometry.polygon.nullVertices"));
		}
		// check for convex
		double area = 0.0;
		double sign = 0.0;
		for (int i = 0; i < size; i++) {
			Vector2 p0 = (i - 1 < 0) ? vertices[size - 1] : vertices[i - 1];
			Vector2 p1 = vertices[i];
			Vector2 p2 = (i + 1 == size) ? vertices[0] : vertices[i + 1];
			// check for coincident vertices
			if (p1.equals(p2)) {
				throw new IllegalArgumentException(Messages.getString("geometry.polygon.coincidentVertices"));
			}
			// check the cross product for CCW winding
			double cross = p0.to(p1).cross(p1.to(p2));
			double tsign = Math.signum(cross);
			area += cross;
			// check for colinear points (for now its allowed)
			if (Math.abs(cross) > Epsilon.E) {
				// check for convexity
				if (sign != 0.0 && tsign != sign) {
					throw new IllegalArgumentException(Messages.getString("geometry.polygon.nonConvex"));
				}
			}
			sign = tsign;
		}
		// check for CCW
		if (area < 0.0) {
			throw new IllegalArgumentException(Messages.getString("geometry.polygon.invalidWinding"));
		}
		// if we've made it this far then continue;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Polygon[").append(super.toString())
		  .append("|Vertices={");
		for (int i = 0; i < this.vertices.length; i++) {  
			if (i != 0) sb.append(",");
			sb.append(this.vertices[i]);
		}
		sb.append("}")
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getVertices()
	 */
	@Override
	public Vector2[] getVertices() {
		return this.vertices;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getNormals()
	 */
	@Override
	public Vector2[] getNormals() {
		return this.normals;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getVertexIterator()
	 */
	@Override
	public Iterator<Vector2> getVertexIterator() {
		return new WoundIterator(this.vertices);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getNormalIterator()
	 */
	@Override
	public Iterator<Vector2> getNormalIterator() {
		return new WoundIterator(this.normals);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		return Geometry.getRotationRadius(center, this.vertices);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getAxes(java.util.List, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// get the size of the foci list
		int fociSize = foci != null ? foci.length : 0;
		// get the number of vertices this polygon has
		int size = this.vertices.length;
		// the axes of a polygon are created from the normal of the edges
		// plus the closest point to each focus
		Vector2[] axes = new Vector2[size + fociSize];
		int n = 0;
		// loop over the edge normals and put them into world space
		for (int i = 0; i < size; i++) {
			// create references to the current points
			Vector2 v = this.normals[i];
			// transform it into world space and add it to the list
			axes[n++] = transform.getTransformedR(v);
		}
		// loop over the focal points and find the closest
		// points on the polygon to the focal points
		for (int i = 0; i < fociSize; i++) {
			// get the current focus
			Vector2 f = foci[i];
			// create a place for the closest point
			Vector2 closest = transform.getTransformed(this.vertices[0]);
			double d = f.distanceSquared(closest);
			// find the minimum distance vertex
			for (int j = 1; j < size; j++) {
				// get the vertex
				Vector2 p = this.vertices[j];
				// transform it into world space
				p = transform.getTransformed(p);
				// get the squared distance to the focus
				double dt = f.distanceSquared(p);
				// compare with the last distance
				if (dt < d) {
					// if its closer then save it
					closest = p;
					d = dt;
				}
			}
			// once we have found the closest point create 
			// a vector from the focal point to the point
			Vector2 axis = f.to(closest);
			// normalize it
			axis.normalize();
			// add it to the array
			axes[n++] = axis;
		}
		// return all the axes
		return axes;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this shape. Always returns null.
	 * @return null
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#contains(org.dyn4j.geometry.Vector, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		// if the polygon is convex then do a simple inside test
		// if the the sign of the location of the point on the side of an edge (or line)
		// is always the same and the polygon is convex then we know that the
		// point lies inside the polygon
		// This method doesn't care about vertex winding
		// inverse transform the point to put it in local coordinates
		Vector2 p = transform.getInverseTransformed(point);
		Vector2 p1 = this.vertices[0];
		Vector2 p2 = this.vertices[1];
		// get the location of the point relative to the first two vertices
		double last = Segment.getLocation(p, p1, p2);
		int size = this.vertices.length;
		// loop through the rest of the vertices
		for (int i = 1; i < size; i++) {
			// p1 is now p2
			p1 = p2;
			// p2 is the next point
			p2 = this.vertices[(i + 1) == size ? 0 : i + 1];
			// check if they are equal (one of the vertices)
			if (p.equals(p1)) {
				return true;
			}
			// do side of line test
			// multiply the last location with this location
			// if they are the same sign then the opertation will yield a positive result
			// -x * -y = +xy, x * y = +xy, -x * y = -xy, x * -y = -xy
			if (last * Segment.getLocation(p, p1, p2) < 0) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double, double, double, double, double)
	 */
	@Override
	protected void rotate(double theta, double cos, double sin, double x, double y) {
		super.rotate(theta, cos, sin, x, y);
		
		int size = this.vertices.length;
		
		for (int i = 0; i < size; i++) {
			this.vertices[i].rotate(cos, sin, x, y);
			this.normals[i].rotate(cos, sin);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		super.translate(x, y);
		int size = this.vertices.length;
		for (int i = 0; i < size; i++) {
			this.vertices[i].add(x, y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#project(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 vector, Transform transform) {
		//System.out.println(1);
		double v = 0.0;
    	// get the first point
		Vector2 p = transform.getTransformed(this.vertices[0]);
		// project the point onto the vector
    	double min = vector.dot(p);
    	double max = min;
    	// loop over the rest of the vertices
    	int size = this.vertices.length;
        for(int i = 1; i < size; i++) {
    		// get the next point
    		p = transform.getTransformed(this.vertices[i]);
    		// project it onto the vector
            v = vector.dot(p);
            if (v < min) { 
                min = v;
            } else if (v > max) { 
                max = v;
            }
        }
        return new Interval(min, max);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public EdgeFeature getFarthestFeature(Vector2 vector, Transform transform) {
		// transform the normal into local space
		Vector2 localn = transform.getInverseTransformedR(vector);
		
		int index = maxIndex(localn);
		int count = this.vertices.length;
		
		Vector2 maximum = new Vector2(this.vertices[index]);
		
		// once we have the point of maximum
		// see which edge is most perpendicular
		Vector2 leftN = this.normals[index == 0 ? count - 1 : index - 1];
		Vector2 rightN = this.normals[index];
		
		// create the maximum point for the feature (transform the maximum into world space)
		transform.transform(maximum);
		PointFeature vm = new PointFeature(maximum, index);
		// is the left or right edge more perpendicular?
		
		if (leftN.dot(localn) < rightN.dot(localn)) {
			int l = index + 1 == count ? 0 : index + 1;
			
			Vector2 left = transform.getTransformed(this.vertices[l]);
			PointFeature vl = new PointFeature(left, l);
			// make sure the edge is the right winding
			return new EdgeFeature(vm, vl, vm, maximum.to(left), index + 1);
		} else {
			int r = index - 1 < 0 ? count - 1 : index - 1;
			
			Vector2 right = transform.getTransformed(this.vertices[r]);
			PointFeature vr = new PointFeature(right, r);
			// make sure the edge is the right winding
			return new EdgeFeature(vr, vm, vm, right.to(maximum), index);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 vector, Transform transform) {
		// transform the normal into local space
		Vector2 localn = transform.getInverseTransformedR(vector);
		
		int index = maxIndex(localn);
		
		// transform the point into world space and return
		return transform.getTransformed(this.vertices[index]);
	}
	
	/**
	 * One of the implementations for the maxIndex functionality, runs in O(n) time.
	 * 
	 * @param vector the direction
	 * @return the index of the farthest vertex in that direction
	 */
	private int maxIndexLinear(Vector2 vector) {
		int index = 0;
		// prime the projection amount
		double max = vector.dot(this.vertices[0]);
		// loop through the rest of the vertices to find a further point along the axis
		int size = this.vertices.length;
		for (int i = 1; i < size; i++) {
			// get the current vertex
			Vector2 v = this.vertices[i];
			// project the vertex onto the axis
			double projection = vector.dot(v);
			// check to see if the projection is greater than the last
			if (projection > max) {
				// otherwise this point is the farthest so far so clear the array and add it
				index = i;
				// set the new maximum
				max = projection;
			}
		}
		
		return index;
	}
	
	/** Private variable that holds the current maxProjection while performing the maxIndexFast function calls.
	 *  Unfortunately we can't return two variables (both index and max) so this is the only viable way. */
	private double maxProjection;
	
	/**
	 * Helper method to implement findMaxTwoWay. Performs half the search (only to the right)
	 * and return the index of the vertex maximizing vector.dot(v) or -1 if no vertex with projection
	 * greater than maxProjection found.
	 * 
	 * Checks only point from startRight and incrementing by step
	 * @see findMaxTwoWay
	 * 
	 * @param startRight The starting point for the search
	 * @param step The step that defines how many point will be skipped in each iteration
	 * @param vector The direction
	 * @return the index 
	 */
	private int findMaxToRight(int startRight, int step, Vector2 vector) {
		double projection;
		
		// Check if there's at least one (the first) vertex with greater projection
		// Also checks if startRight < this.vertices.length but assumes startRight >= 0
		if (startRight < this.vertices.length && (projection = vector.dot(this.vertices[startRight])) > maxProjection) {
			maxProjection = projection;
			int maxIndex = startRight;
			
			// Keep searching to the right while we find more and more vertices that increase the projection.
			// First increment the search index and check if it is a valid index (first in order to short circuit the condition if not valid).
			while ((maxIndex = maxIndex + step) < this.vertices.length && (projection = vector.dot(this.vertices[maxIndex])) > maxProjection) {
				maxProjection = projection;
			}
			
			// Because we increment the index before checking and calculating we always end with one extra addition, so we cancel this here
			return maxIndex - step;
		}
		
		return -1;
	}
	
	/**
	 * Symmetric to findMaxToRight.
	 * Helper method to implement findMaxTwoWay. Performs half the search (only to the left)
	 * and return the index of the vertex maximizing vector.dot(v) or -1 if no vertex with projection
	 * greater than maxProjection found.
	 * 
	 * Checks only point from startLeft and decrementing by step
	 * @see findMaxTwoWay
	 * 
	 * @param startLeft The starting point for the search
	 * @param step The step that defines how many point will be skipped in each iteration
	 * @param vector The direction
	 * @return the index 
	 */
	private int findMaxToLeft(int startLeft, int step, Vector2 vector) {
		double projection;
		
		// Check if there's at least one (the first) vertex with greater projection
		// Also checks if startLeft >= 0 but assumes startRight < this.vertices.length
		if (startLeft >= 0 && (projection = vector.dot(this.vertices[startLeft])) > maxProjection) {
			maxProjection = projection;
			int maxIndex = startLeft;
			
			// Keep searching to the left while we find more and more vertices that increase the projection.
			// First decrement the search index and check if it is a valid index (first in order to short circuit the condition if not valid).
			while ((maxIndex = maxIndex - step) >= 0 && (projection = vector.dot(this.vertices[maxIndex])) > maxProjection) {
				maxProjection = projection;
			}
			
			// Because we decrement the index before checking and calculating we always end with one extra substraction, so we cancel this here
			return maxIndex + step;
		}
		
		return -1;
	}
	
	/**
	 * Helper method for maxIndexFast. Finds the vertex that maximize vector.dot(v) (affected by the local variable 'maxProjection')
	 * but only checks the points indicated by startRight, startLeft and step.
	 * Specifically this method will search either from startRight to the right, in the indices (startRight + k * step)
	 * or if the maximum lies in the other side it will search from startLeft to the left, in the indices (startLeft - k * step)
	 * 
	 * Works because vertices are sorted by angle @see maxIndexFast
	 * 
	 * @param startRight The starting point for the search to the right
	 * @param startLeft The starting point for the search to the left
	 * @param step The step that defines how many point will be skipped in each iteration
	 * @param initialMaxIndex The index to be returned if there where no vertices with product greater than 'maxProjection' (only when the max was in (startLeft, startRight))
	 * @param vector The direction
	 * @return The index of the resulting vertex 
	 */
	private int findMaxTwoWay(int startRight, int startLeft, int step, int initialMaxIndex, Vector2 vector) {
		int searchIndex;
		
		if ((searchIndex = findMaxToRight(startRight, step, vector)) != -1) {
			// If we found at least one vertex with projection > maxProjection
			// then we don't need to search on the left; return the index found
			return searchIndex;
		} else if ((searchIndex = findMaxToLeft(startLeft, step, vector)) != -1) {
			// This means than the first point had projection < maxProjection and the search to the right aborted
			// so go to the left
			return searchIndex;
		} else {
			// Left side search aborted as well, return initialMaxIndex
			return initialMaxIndex;
		}
	}
	
	/**
	 * One of the implementations for the maxIndex functionality, runs in O(sqrt(n)) time.
	 * Since we have the vertices sorted by angle we can do better than linear search.
	 * If we consider the function f(n) = vector.dot(vertices[n]) we can see that it will have one maximum and one minimum value
	 * and also it will be monotonically increasing/decreasing between those points.
	 * So we can perform the search in two phases: Choose a step 's' and find the max (let's say it's in position j) only in vertices with index = k * s for some natural number k.
	 * After this point the real max will be near j, at most with distance s from it.
	 * This means that we can find the real max in n/s + s steps, which is asymptotically better for s = sqrt(n).
	 * Complete proof of correctness omitted from here. See additional comments below for more details.
	 * 
	 * @param vector the direction
	 * @return the index of the farthest vertex in that direction
	 */
	private int maxIndexFast(Vector2 vector) {
		// set the farthest point to the first one
		int n = this.vertices.length;
		
		// Initialize maxProjection to the projection of the first vertex
		maxProjection = vector.dot(this.vertices[0]);
		
		// As described, we first find a vertex that is 'close enough' to the max.
		// Close enough here means that the real maximum won't be farthest away than (searchStep - 1) places to the left or right
		int maxIndex = findMaxTwoWay(searchStep, n - searchStep, searchStep, 0, vector);
		
		// Now we need to find the exact place of the maximum, so we continue with the search near the last index found.
		// We must be careful with the edge case where maxIndex is 0, because then we can't go to the left by substracting 1.
		if (maxIndex == 0) {
			// In this edge case the left index to start is (n - 1) and not (maxIndex - 1)
			return findMaxTwoWay(1, n - 1, 1, maxIndex, vector);
		} else {
			// Correctness note: one may wonder what will happen when 0 < maxIndex < searchStep or maxIndex > n - searchStep because in that case the
			// search will end prematurely due to index bounds [0, this.vertices.length).
			// The only way for this to happen is if we find only an increasing (or decreasing) sequence of projections with this step and the last happens to be closer to n - step (or step for when going to the left).
			// But in this case the indices that won't be checked will never contain an even greater projection value, because we made the whole loop and that wouldn't happen if a bigger projection existed near index 0.
			
			// The other case, safely search to left and right with step 1, until we hit the real max.
			return findMaxTwoWay(maxIndex + 1, maxIndex - 1, 1, maxIndex, vector);
		}
	}
	
	/**
	 * Internal helper method that returns the index of the point that is
	 * farthest in direction of a vector. Chooses what algorithm to use based on vertex count.
	 * 
	 * @param vector the direction
	 * @return the index of the farthest vertex in that direction
	 */
	int maxIndex(Vector2 vector) {
		if (this.searchStep == 1) {
			return maxIndexLinear(vector);
		} else {
			return maxIndexFast(vector);
		}
	}
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Polygon} and the given density.
	 * <p>
	 * A {@link Polygon}'s centroid must be computed by the area weighted method since the
	 * average method can be bias to one side if there are more points on that one
	 * side than another.
	 * <p>
	 * Finding the area of a {@link Polygon} can be done by using the following
	 * summation:
	 * <p style="white-space: pre;"> 0.5 * &sum;(x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)</p>
	 * Finding the area weighted centroid can be done by using the following
	 * summation:
	 * <p style="white-space: pre;"> 1 / (6 * A) * &sum;(p<sub>i</sub> + p<sub>i + 1</sub>) * (x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)</p>
	 * Finding the inertia tensor can by done by using the following equation:
	 * <p style="white-space: pre;">
	 *          &sum;(p<sub>i + 1</sub> x p<sub>i</sub>) * (p<sub>i</sub><sup>2</sup> + p<sub>i</sub> &middot; p<sub>i + 1</sub> + p<sub>i + 1</sub><sup>2</sup>)
	 * m / 6 * -------------------------------------------
	 *                        &sum;(p<sub>i + 1</sub> x p<sub>i</sub>)
	 * </p>
	 * Where the mass is computed by:
	 * <p style="white-space: pre;"> d * area</p>
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Polygon}
	 */
	@Override
	public Mass createMass(double density) {
		// can't use normal centroid calculation since it will be weighted towards sides
		// that have larger distribution of points.
		Vector2 center = new Vector2();
		double area = 0.0;
		double I = 0.0;
		int n = this.vertices.length;
		// get the average center
		Vector2 ac = new Vector2();
		for (int i = 0; i < n; i++) {
			ac.add(this.vertices[i]);
		}
		ac.multiply(1.0 / n);
		// loop through the vertices
		for (int i = 0; i < n; i++) {
			// get two vertices
			Vector2 p1 = this.vertices[i];
			Vector2 p2 = i + 1 < n ? this.vertices[i + 1] : this.vertices[0];
			// get the vector from the center to the point
			p1 = p1.difference(ac);
			p2 = p2.difference(ac);
			// perform the cross product (yi * x(i+1) - y(i+1) * xi)
			double D = p1.cross(p2);
			// multiply by half
			double triangleArea = 0.5 * D;
			// add it to the total area
			area += triangleArea;

			// area weighted centroid
			// (p1 + p2) * (D / 6)
			// = (x1 + x2) * (yi * x(i+1) - y(i+1) * xi) / 6
			// we will divide by the total area later
			center.x += (p1.x + p2.x) * Geometry.INV_3 * triangleArea;
			center.y += (p1.y + p2.y) * Geometry.INV_3 * triangleArea;

			// (yi * x(i+1) - y(i+1) * xi) * (p2^2 + p2 . p1 + p1^2)
			I += triangleArea * (p2.dot(p2) + p2.dot(p1) + p1.dot(p1));
			// we will do the m / 6A = (d / 6) when we have the area summed up
		}
		// compute the mass
		double m = density * area;
		// finish the centroid calculation by dividing by the total area
		// and adding in the average center
		center.multiply(1.0 / area);
		Vector2 c = center.sum(ac);
		// finish the inertia tensor by dividing by the total area and multiplying by d / 6
		I *= (density / 6.0);
		// shift the axis of rotation to the area weighted center
		// (center is the vector from the average center to the area weighted center since
		// the average center is used as the origin)
		I -= m * center.getMagnitudeSquared();
		
		return new Mass(c, m, I);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createAABB(org.dyn4j.geometry.Transform)
	 */
	public AABB createAABB(Transform transform) {
		if (this.vertices.length >= FAST_AABB_THRESHOLD) {
			return createAABBFast(transform);
		} else {
			return createAABBLinear(transform);
		}
	}
	
	/**
	 * Internal implementation of createAABB that runs in O(sqrt(n)) time
	 * Only suitable for sufficiently large polygons. Used when vertexCount >= FAST_AABB_THRESHOLD
	 * 
	 * @see org.dyn4j.geometry.Shape#createAABB(org.dyn4j.geometry.Transform)
	 */
	AABB createAABBFast(Transform transform) {
        // This is an alternative way of computing the AABB
		// that can be used instead of the straightforward linear time algorithm
		// Works by finding the farthest point in each of the four semi-axis using the fast maxIndexFast method
		// Asymptotically faster time O(sqrt(n)) but involves larger constants so should be used only for polygons with really high vertex count
		// Vertex count > 40 is a safe point from which this version performs always faster
		// We also apply the matrix transformation after finding the vertices which is an extra benefit
		
		// Inline Vector2 xAxis = transform.getTransformedR(Vector2.X_AXIS)
		Vector2 xAxis = new Vector2(transform.cost, -transform.sint);
		// Inline Vector2 yAxis = transform.getTransformedR(Vector2.Y_AXIS)
		Vector2 yAxis = new Vector2(transform.sint, transform.cost);
        
		// find maxX with X_AXIS
        double maxX = transform.getTransformedX(this.vertices[maxIndexFast(xAxis)]);
        
		// and then minX with INV_X_AXIS
        xAxis.negate();
        double minX = transform.getTransformedX(this.vertices[maxIndexFast(xAxis)]);
        
        // find maxY with Y_AXIS
        double maxY = transform.getTransformedY(this.vertices[maxIndexFast(yAxis)]);
        
        // and then minY with INV_Y_AXIS
        yAxis.negate();
        double minY = transform.getTransformedY(this.vertices[maxIndexFast(yAxis)]);

        return new AABB(minX, minY, maxX, maxY);
	}
	
	/**
	 * Internal implementation of createAABB that runs in O(n) time, suitable for most uses unless vertex count is large.
	 * 
	 * @see org.dyn4j.geometry.Shape#createAABB(org.dyn4j.geometry.Transform)
	 */
	AABB createAABBLinear(Transform transform) {
		// get the first point
		// project the point onto the vector
    	double minX, maxX, minY, maxY;
    	
    	Vector2 v = this.vertices[0];
    	minX = maxX = transform.getTransformedX(v);
    	minY = maxY = transform.getTransformedY(v);
    	
    	// loop over the rest of the vertices
    	int size = this.vertices.length;
        for(int i = 1; i < size; i++) {
    		// get the next point
    		// project it onto the vector
        	v = this.vertices[i];
        	
        	// v = transform.getTransformed(v) allocates a new Vector2 for each loop
        	// but we can avoid this by transforming x and y separately
            double vx = transform.getTransformedX(v);
            double vy = transform.getTransformedY(v);
            
            // compare the x values
            if (vx < minX) {
            	minX = vx;
            } else if (vx > maxX) {
            	maxX = vx;
            }
            // compare the y values
            if (vy < minY) {
            	minY = vy;
            } else if (vy > maxY) {
            	maxY = vy;
            }
        }
        
		// create the aabb
        return new AABB(minX, minY, maxX, maxY);
	}
	
}