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
package org.dyn4j.game2d.geometry;

import java.util.List;

/**
 * Contains static methods to perform standard geometric operations.
 * @author William Bittle
 */
public class Geometry {
	/** The value of 1/3 */
	private static final double INV_3 = 1.0 / 3.0;
	
	/**
	 * Returns the centroid of the given points by performing an average.
	 * @param points the list of points
	 * @return {@link Vector} the centroid
	 */
	public static final Vector getAverageCenter(List<Vector> points) {
		// check for null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// check for empty list
		if (points.isEmpty()) throw new IllegalArgumentException("The points list must have at least one point.");
		// check for a list of one point
		int size = points.size();
		if (size == 1) return points.get(0).copy();
		// otherwise perform the average
		double x = 0;
		double y = 0;
		for (int i = 0; i < size; i++) {
			Vector point = points.get(i);
			x += point.x;
			y += point.y;
		}
		return new Vector(x / (double) size, y / (double) size);
	}
	
	/**
	 * Returns the centroid of the given points by performing an average.
	 * @see #getAverageCenter(List)
	 * @param points the array of points
	 * @return {@link Vector} the centroid
	 */
	public static final Vector getAverageCenter(Vector... points) {
		// check for null array
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// check for a list of one point
		int size = points.length;
		if (size == 1) return points[0].copy();
		double x = 0;
		double y = 0;
		for (int i = 0; i < size; i++) {
			Vector point = points[i];
			x += point.x;
			y += point.y;
		}
		return new Vector(x / (double) size, y / (double) size);
	}
	
	/**
	 * Returns the area weighted centroid for the given points.
	 * <p>
	 * A {@link Polygon}'s centroid must be computed by the area weighted method since the
	 * average method can be bias to one side if there are more points on that one
	 * side than another.
	 * <p>
	 * Finding the area of a {@link Polygon} can be done by using the following
	 * summation:
	 * <pre>
	 * 0.5 * &sum;(x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)
	 * </pre>
	 * Finding the area weighted centroid can be done by using the following
	 * summation:
	 * <pre>
	 * 1 / (6 * A) * &sum;(p<sub>i</sub> + p<sub>i + 1</sub>) * (x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)
	 * </pre>
	 * @param points the {@link Polygon} points
	 * @return {@link Vector} the area weighted centroid
	 */
	public static final Vector getAreaWeightedCenter(List<Vector> points) {
		// check for null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// check for empty list
		if (points.isEmpty()) throw new IllegalArgumentException("The points list must have at least one point.");
		// check for list of one point
		int size = points.size();
		if (size == 1) return points.get(0).copy();
		// otherwise perform the computation
		Vector center = new Vector();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector p1 = points.get(i);
			Vector p2 = i + 1 < size ? points.get(i + 1) : points.get(0);
			// perform the cross product (yi * x(i+1) - y(i+1) * xi)
			double d = p1.cross(p2);
			// multiply by half
			double triangleArea = 0.5 * d;
			// add it to the total area
			area += triangleArea;

			// area weighted centroid
			// (p1 + p2) * (D / 3)
			// = (x1 + x2) * (yi * x(i+1) - y(i+1) * xi) / 3
			// we will divide by the total area later
			center.add(p1.sum(p2).multiply(INV_3).multiply(triangleArea));
		}
		// finish the centroid calculation by dividing by the total area
		center.divide(area);
		// return the center
		return center;
	}

	/**
	 * Returns the area weighted centroid for the given points.
	 * @see #getAreaWeightedCenter(List)
	 * @param points the {@link Polygon} points
	 * @return {@link Vector} the area weighted centroid
	 */
	public static final Vector getAreaWeightedCenter(Vector... points) {
		// check for null array
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// check for array of one point
		int size = points.length;
		if (size == 1) return points[0].copy();
		// otherwise perform the computation
		Vector center = new Vector();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector p1 = points[i];
			Vector p2 = i + 1 < size ? points[i + 1] : points[0];
			// perform the cross product (yi * x(i+1) - y(i+1) * xi)
			double d = p1.cross(p2);
			// multiply by half
			double triangleArea = 0.5 * d;
			// add it to the total area
			area += triangleArea;

			// area weighted centroid
			// (p1 + p2) * (D / 3)
			// = (x1 + x2) * (yi * x(i+1) - y(i+1) * xi) / 3
			// we will divide by the total area later
			center.add(p1.sum(p2).multiply(INV_3).multiply(triangleArea));
		}
		// finish the centroid calculation by dividing by the total area
		center.divide(area);
		// return the center
		return center;
	}
	
	/**
	 * Returns a new {@link Polygon} object with vertexCount points, where the
	 * points are evenly distributed around the unit circle.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon 
	 * (the origin) to each vertex.
	 * @see #createUnitCirclePolygon(int, double, double)
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex
	 * @return {@link Polygon}
	 */
	public static final Polygon createUnitCirclePolygon(int count, double radius) {
		return Geometry.createUnitCirclePolygon(count, radius, 0.0);
	}
	
	/**
	 * Returns a new {@link Polygon} object with vertexCount points, where the
	 * points are evenly distributed around the unit circle.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon 
	 * (the origin) to each vertex.
	 * <p>
	 * The theta parameter is a vertex angle offset used to rotate all the vertices
	 * by the given amount.
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex
	 * @param theta the vertex angle offset
	 * @return {@link Polygon}
	 */
	public static final Polygon createUnitCirclePolygon(int count, double radius, double theta) {
		Vector[] verts = new Vector[count];
		double angle = 2.0 * Math.PI / count;
		for (int i = count - 1; i >= 0; i--) {
			verts[i] = new Vector(Math.cos(angle * i + theta) * radius, Math.sin(angle * i + theta) * radius);
		}
		return new Polygon(verts);
	}
}
