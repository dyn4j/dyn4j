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
package org.dyn4j.game2d.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dyn4j.game2d.Epsilon;

/**
 * Contains static methods to perform standard geometric operations.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Geometry {
	/** The value of 1/3 */
	private static final double INV_3 = 1.0 / 3.0;
	
	/** The value of the inverse of the square root of 3; 1/sqrt(3) */
	private static final double INV_SQRT_3 = 1.0 / Math.sqrt(3.0);
	
	/**
	 * Returns the winding, Clockwise or Counter-Clockwise, for the given
	 * list of points of a polygon.
	 * <p>
	 * This method determines the winding by computing a signed "area".
	 * @param points the points of a polygon
	 * @return double negative for Clockwise winding; positive for Counter-Clockwise winding
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points contains less than 2 elements
	 * @since 2.2.0
	 */
	public static final double getWinding(List<Vector2> points) {
		// check for a null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// get the size
		int size = points.size();
		// the size must be larger than 1
		if (size < 2) throw new IllegalArgumentException("The points list must contain at least 2 non-null points.");
		// determine the winding by computing a signed "area"
		double area = 0.0;
		for (int i = 0; i < size; i++) {
			// get the current point and the next point
			Vector2 p1 = points.get(i);
			Vector2 p2 = points.get(i + 1 == size ? 0 : i + 1);
			// check for null
			if (p1 == null || p2 == null) throw new NullPointerException("The points list cannot contain null points.");
			// add the signed area
			area += p1.cross(p2);
		}
		// return the area
		return area;
	}
	
	/**
	 * Returns the winding, Clockwise or Counter-Clockwise, for the given
	 * array of points of a polygon.
	 * @param points the points of a polygon
	 * @return double negative for Clockwise winding; positive for Counter-Clockwise winding
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points contains less than 2 elements
	 * @since 2.2.0
	 */
	public static final double getWinding(Vector2... points) {
		// check for a null list
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// get the size
		int size = points.length;
		// the size must be larger than 1
		if (size < 2) throw new IllegalArgumentException("The points array must contain at least 2 non-null points.");
		// determine the winding by computing a signed "area"
		double area = 0.0;
		for (int i = 0; i < size; i++) {
			// get the current point and the next point
			Vector2 p1 = points[i];
			Vector2 p2 = points[i + 1 == size ? 0 : i + 1];
			// check for null
			if (p1 == null || p2 == null) throw new NullPointerException("The points array cannot contain null points.");
			// add the signed area
			area += p1.cross(p2);
		}
		// return the area
		return area;
	}
	
	/**
	 * Reverses the order of the polygon points within the given array.
	 * <p>
	 * This method performs a simple array reverse.
	 * @param points the polygon points
	 * @throws NullPointerException if points is null
	 * @since 2.2.0
	 */
	public static final void reverseWinding(Vector2... points) {
		// check for a null list
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// get the length
		int size = points.length;
		// check for a length of 1
		if (size == 1 || size == 0) return;
		// otherwise perform the swapping loop
		int i = 0;
		int j = size - 1;
		Vector2 temp = null;
		while (j > i) {
			// swap
			temp = points[j];
			points[j] = points[i];
			points[i] = temp;
			// increment
			j--;
			i++;
		}
	}
	
	/**
	 * Reverses the order of the polygon points within the given list.
	 * <p>
	 * This method performs a simple list reverse.
	 * @param points the polygon points
	 * @throws NullPointerException if points is null
	 * @since 2.2.0
	 */
	public static final void reverseWinding(List<Vector2> points) {
		// check for a null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// get the length
		int size = points.size();
		// check for a length of 1
		if (size == 1 || size == 0) return;
		// otherwise reverse the list
		Collections.reverse(points);
	}
	
	/**
	 * Returns the centroid of the given points by performing an average.
	 * @param points the list of points
	 * @return {@link Vector2} the centroid
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points is an empty list
	 */
	public static final Vector2 getAverageCenter(List<Vector2> points) {
		// check for null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// check for empty list
		if (points.isEmpty()) throw new IllegalArgumentException("The points list must have at least one point.");
		// get the size
		int size = points.size();
		// check for a list of one point
		if (size == 1) {
			Vector2 p = points.get(0);
			// make sure its not null
			if (p == null) throw new NullPointerException("The points list cannot contain null points.");
			// return a copy
			return p.copy();
		}
		// otherwise perform the average
		double x = 0;
		double y = 0;
		for (int i = 0; i < size; i++) {
			Vector2 point = points.get(i);
			// check for null
			if (point == null) throw new NullPointerException("The points list cannot contain null points.");
			x += point.x;
			y += point.y;
		}
		return new Vector2(x / (double) size, y / (double) size);
	}
	
	/**
	 * Returns the centroid of the given points by performing an average.
	 * @see #getAverageCenter(List)
	 * @param points the array of points
	 * @return {@link Vector2} the centroid
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points is an empty array
	 */
	public static final Vector2 getAverageCenter(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// get the length
		int size = points.length;
		// check for empty
		if (size == 0) throw new IllegalArgumentException("The points array must have at least one point.");
		// check for a list of one point
		if (size == 1) {
			Vector2 p = points[0];
			// check for null
			if (p == null) throw new NullPointerException("The points array cannot contain null points.");
			return p.copy();
		}
		double x = 0;
		double y = 0;
		for (int i = 0; i < size; i++) {
			Vector2 point = points[i];
			// check for null
			if (point == null) throw new NullPointerException("The points array cannot contain null points.");
			x += point.x;
			y += point.y;
		}
		return new Vector2(x / (double) size, y / (double) size);
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
	 * @return {@link Vector2} the area weighted centroid
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points is empty
	 */
	public static final Vector2 getAreaWeightedCenter(List<Vector2> points) {
		// check for null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// check for empty list
		if (points.isEmpty()) throw new IllegalArgumentException("The points list must have at least one point.");
		// check for list of one point
		int size = points.size();
		if (size == 1) {
			Vector2 p = points.get(0);
			// check for null
			if (p == null) throw new NullPointerException("The points list cannot contain null points.");
			return p.copy();
		}
		// otherwise perform the computation
		Vector2 center = new Vector2();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector2 p1 = points.get(i);
			Vector2 p2 = i + 1 < size ? points.get(i + 1) : points.get(0);
			// check for null
			if (p1 == null || p2 == null) throw new NullPointerException("The points list cannot contain null points.");
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
		// check for zero area
		if (Math.abs(area) < Epsilon.E) {
			// zero area can only happen if all the points are the same point
			// in which case just return a copy of the first
			return points.get(0).copy();
		}
		// finish the centroid calculation by dividing by the total area
		center.multiply(1.0 / area);
		// return the center
		return center;
	}

	/**
	 * Returns the area weighted centroid for the given points.
	 * @see #getAreaWeightedCenter(List)
	 * @param points the {@link Polygon} points
	 * @return {@link Vector2} the area weighted centroid
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points is empty
	 */
	public static final Vector2 getAreaWeightedCenter(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// get the size
		int size = points.length;
		// check for empty array
		if (size == 0) throw new IllegalArgumentException("The points array must have at least one point.");
		// check for array of one point
		if (size == 1) {
			Vector2 p = points[0];
			// check for null
			if (p == null) throw new NullPointerException("The points array cannot contain null points.");
			return p.copy();
		}
		// otherwise perform the computation
		Vector2 center = new Vector2();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector2 p1 = points[i];
			Vector2 p2 = i + 1 < size ? points[i + 1] : points[0];
			// check for null
			if (p1 == null || p2 == null) throw new NullPointerException("The points array cannot contain null points.");
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
		// check for zero area
		if (Math.abs(area) < Epsilon.E) {
			// zero area can only happen if all the points are the same point
			// in which case just return a copy of the first
			return points[0].copy();
		}
		// finish the centroid calculation by dividing by the total area
		center.multiply(1.0 / area);
		// return the center
		return center;
	}
	
	/**
	 * Returns a new {@link Circle} with the given radius.
	 * @param radius the radius in meters
	 * @return {@link Circle}
	 * @throws IllegalArgumentException if radius is less than or equal to zero
	 */
	public static final Circle createCircle(double radius) {
		return new Circle(radius);
	}
	
	/**
	 * Returns a new {@link Polygon} with the given vertices.
	 * <p>
	 * This method makes a copy of both the array and the vertices within the array to 
	 * create the new {@link Polygon}.
	 * @param vertices the array of vertices
	 * @return {@link Polygon}
	 * @throws NullPointerException if vertices is null or an element of vertices is null
	 * @throws IllegalArgumentException if vertices contains less than 3 non-null vertices
	 */
	public static final Polygon createPolygon(Vector2... vertices) {
		// check the vertices array
		if (vertices == null) throw new NullPointerException("The vertices array cannot be null.");
		// loop over the points an copy them
		int size = vertices.length;
		// check the size
		Vector2[] verts = new Vector2[size];
		for (int i = 0; i < size; i++) {
			Vector2 vertex = vertices[i];
			// check for null points
			if (vertex != null) {
				verts[i] = vertex.copy();
			} else {
				throw new NullPointerException("A polygon cannot contain null points.");
			}
		}
		return new Polygon(verts);
	}
	
	/**
	 * Returns a new {@link Polygon} with the given vertices centered at the origin.
	 * <p>
	 * This method makes a copy of both the array and the vertices within the array to 
	 * create the new {@link Polygon}.
	 * <p>
	 * This method translates the {@link Polygon} vertices so that the center is at the origin.
	 * @param vertices the array of vertices
	 * @return {@link Polygon}
	 * @throws NullPointerException if vertices is null or an element of vertices is null
	 * @throws IllegalArgumentException if vertices contains less than 3 non-null vertices
	 */
	public static final Polygon createPolygonAtOrigin(Vector2... vertices) {
		Polygon polygon = Geometry.createPolygon(vertices);
		Vector2 center = polygon.getCenter();
		polygon.translate(-center.x, -center.y);
		return polygon;
	}
	
	/**
	 * Returns a new {@link Polygon} object with count number of points, where the
	 * points are evenly distributed around the unit circle.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon 
	 * (the origin) to each vertex.
	 * @see #createUnitCirclePolygon(int, double, double)
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex in meters
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException if count is less than 3 or radius is less than or equal to zero
	 */
	public static final Polygon createUnitCirclePolygon(int count, double radius) {
		return Geometry.createUnitCirclePolygon(count, radius, 0.0);
	}
	
	/**
	 * Returns a new {@link Polygon} object with count number of points, where the
	 * points are evenly distributed around the unit circle.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon 
	 * (the origin) to each vertex.
	 * <p>
	 * The theta parameter is a vertex angle offset used to rotate all the vertices
	 * by the given amount.
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex in meters
	 * @param theta the vertex angle offset in radians
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException if count is less than 3 or radius is less than or equal to zero
	 */
	public static final Polygon createUnitCirclePolygon(int count, double radius, double theta) {
		// check the count
		if (count < 3) throw new IllegalArgumentException("The number of vertices must be greater than 2.");
		// check the radius
		if (radius <= 0.0) throw new IllegalArgumentException("The radius must be greater than zero.");
		Vector2[] verts = new Vector2[count];
		double angle = 2.0 * Math.PI / count;
		for (int i = count - 1; i >= 0; i--) {
			verts[i] = new Vector2(Math.cos(angle * i + theta) * radius, Math.sin(angle * i + theta) * radius);
		}
		return new Polygon(verts);
	}
	
	/**
	 * Creates a new {@link Rectangle} with the given size centered at the origin.
	 * @param size the size in meters
	 * @return {@link Rectangle}
	 * @throws IllegalArgumentException if size is less than or equal to zero
	 */
	public static final Rectangle createSquare(double size) {
		// check the size
		if (size <= 0.0) throw new IllegalArgumentException("The size must be greater than zero.");
		return new Rectangle(size, size);
	}
	
	/**
	 * Creates a new {@link Rectangle} with the given width and height centered at the origin.
	 * @param width the width in meters
	 * @param height the height in meters
	 * @return {@link Rectangle}
	 * @throws IllegalArgumentException if width or height is less than or equal to zero
	 */
	public static final Rectangle createRectangle(double width, double height) {
		return new Rectangle(width, height);
	}
	
	/**
	 * Creates a new {@link Triangle} with the given points.
	 * <p>
	 * This method makes a copy of the given points to create the {@link Triangle}.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param p3 the third point
	 * @return {@link Triangle}
	 * @throws NullPointerException if p1, p2, or p3 is null
	 */
	public static final Triangle createTriangle(Vector2 p1, Vector2 p2, Vector2 p3) {
		if (p1 == null || p2 == null || p3 == null) throw new NullPointerException("A triangle cannot contain a null point.");
		return new Triangle(p1.copy(), p2.copy(), p3.copy());
	}
	
	/**
	 * Creates a new {@link Triangle} with the given points centered at the origin.
	 * <p>
	 * This method makes a copy of the given points to create the {@link Triangle}.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param p3 the third point
	 * @return {@link Triangle}
	 * @throws NullPointerException if p1, p2, or p3 is null
	 */
	public static final Triangle createTriangleAtOrigin(Vector2 p1, Vector2 p2, Vector2 p3) {
		Triangle triangle = Geometry.createTriangle(p1, p2, p3);
		Vector2 center = triangle.getCenter();
		triangle.translate(-center.x, -center.y);
		return triangle;
	}
	
	/**
	 * Creates a right {@link Triangle} with the center at the origin.
	 * @param width the width of the base in meters
	 * @param height the height in meters
	 * @return {@link Triangle}
	 * @throws IllegalArgumentException if width or height is less than or equal to zero
	 */
	public static final Triangle createRightTriangle(double width, double height) {
		// check the width
		if (width <= 0.0) throw new IllegalArgumentException("The width must be greater than zero.");
		// check the height
		if (height <= 0.0) throw new IllegalArgumentException("The width must be greater than zero.");
		Vector2 top = new Vector2(0.0, height);
		Vector2 left = new Vector2(0.0, 0.0);
		Vector2 right = new Vector2(width, 0.0);
		Triangle triangle = new Triangle(top, left, right);
		Vector2 center = triangle.getCenter();
		triangle.translate(-center.x, -center.y);
		return triangle;
	}
	
	/**
	 * Creates an equilateral {@link Triangle} with the center at the origin.
	 * @param height the height of the triangle in meters
	 * @return {@link Triangle}
	 * @throws IllegalArgumentException if height is less than or equal to zero
	 */
	public static final Triangle createEquilateralTriangle(double height) {
		// check the size
		if (height <= 0.0) throw new IllegalArgumentException("The size must be greater than zero.");
		// compute a where height = a * sqrt(3) / 2.0 (a is the width of the base
		double a = 2.0 * height * INV_SQRT_3;
		// create the triangle
		return Geometry.createIsoscelesTriangle(a, height);
	}
	
	/**
	 * Creates an isosceles {@link Triangle} with the center at the origin.
	 * @param width the width of the base in meters
	 * @param height the height in meters
	 * @return {@link Triangle}
	 * @throws IllegalArgumentException if width or height is less than or equal to zero
	 */
	public static final Triangle createIsoscelesTriangle(double width, double height) {
		// check the width
		if (width <= 0.0) throw new IllegalArgumentException("The width must be greater than zero.");
		// check the height
		if (height <= 0.0) throw new IllegalArgumentException("The width must be greater than zero.");
		Vector2 top = new Vector2(0.0, height);
		Vector2 left = new Vector2(-width * 0.5, 0.0);
		Vector2 right = new Vector2(width * 0.5, 0.0);
		// create the triangle
		Triangle triangle = new Triangle(top, left, right);
		Vector2 center = triangle.getCenter();
		triangle.translate(-center.x, -center.y);
		return triangle;
	}
	
	/**
	 * Creates a new {@link Segment} with the given points.
	 * <p>
	 * This method makes a copy of the given points to create the {@link Segment}.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return {@link Segment}
	 * @throws NullPointerException if p1 or p2 is null
	 */
	public static final Segment createSegment(Vector2 p1, Vector2 p2) {
		if (p1 == null || p2 == null) throw new NullPointerException("A segment cannot contain a null point.");
		return new Segment(p1.copy(), p2.copy());
	}
	
	/**
	 * Creates a new {@link Segment} with the given points.
	 * <p>
	 * This method makes a copy of the given points to create the {@link Segment}.
	 * <p>
	 * This method translates the {@link Segment} vertices so that the center is at the origin.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return {@link Segment}
	 * @throws NullPointerException if p1 or p2 is null
	 */
	public static final Segment createSegmentAtOrigin(Vector2 p1, Vector2 p2) {
		Segment segment = Geometry.createSegment(p1, p2);
		Vector2 center = segment.getCenter();
		segment.translate(-center.x, -center.y);
		return segment;
	}
	
	/**
	 * Creates a new {@link Segment} from the origin to the given end point
	 * <p>
	 * This method makes a copy of the given point to create the {@link Segment}.
	 * @param end the end point
	 * @return {@link Segment}
	 * @throws NullPointerException if end is null
	 */
	public static final Segment createSegment(Vector2 end) {
		return Geometry.createSegment(new Vector2(), end);
	}
	
	/**
	 * Creates a new {@link Segment} with the given length with the center
	 * at the origin.
	 * <p>
	 * Renamed from createSegment(double).
	 * @param length the length of the segment in meters
	 * @return {@link Segment}
	 * @throws IllegalArgumentException if length is less than or equal to zero
	 * @since 2.2.3
	 */
	public static final Segment createHorizontalSegment(double length) {
		// check the length
		if (length <= 0.0) throw new IllegalArgumentException("The length must be greater than zero.");
		Vector2 start = new Vector2(-length * 0.5, 0.0);
		Vector2 end = new Vector2(length * 0.5, 0.0);
		return new Segment(start, end);
	}
	
	/**
	 * Creates a new {@link Segment} with the given length with the center
	 * at the origin.
	 * @param length the length of the segment in meters
	 * @return {@link Segment}
	 * @throws IllegalArgumentException if length is less than or equal to zero
	 * @since 2.2.3
	 */
	public static final Segment createVerticalSegment(double length) {
		// check the length
		if (length <= 0.0) throw new IllegalArgumentException("The length must be greater than zero.");
		Vector2 start = new Vector2(0.0, -length * 0.5);
		Vector2 end = new Vector2(0.0, length * 0.5);
		return new Segment(start, end);
	}
	
	/**
	 * Returns a new list containing the 'cleansed' version of the given listing of polygon points.
	 * <p>
	 * This method ensures the polygon has CCW winding, removes colinear vertices, and removes coincident vertices.
	 * <p>
	 * If the given list is empty, the list is returned.
	 * @param points the list polygon points
	 * @return List&lt;{@link Vector2}&gt;
	 * @throws NullPointerException if points is null or if points contains null elements
	 */
	public static final List<Vector2> cleanse(List<Vector2> points) {
		// check for null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// get the size of the points list
		int size = points.size();
		// check the size
		if (size == 0) return points;
		// create a result list
		List<Vector2> result = new ArrayList<Vector2>(size);
		
		double winding = 0.0;
		
		// loop over the points
		for (int i = 0; i < size; i++) {
			// get the current point
			Vector2 point = points.get(i);
			
			// get the adjacent points
			Vector2 prev = points.get(i - 1 < 0 ? size - 1 : i - 1);
			Vector2 next = points.get(i + 1 == size ? 0 : i + 1);
			
			// check for null
			if (point == null || prev == null || next == null)
				throw new NullPointerException("The points list cannot contain null elements.");
			
			// is this point equal to the next?
			Vector2 diff = point.difference(next);
			if (diff.isZero()) {
				// then skip this point
				continue;
			}
			
			// create the edge vectors
			Vector2 prevToPoint = prev.to(point);
			Vector2 pointToNext = point.to(next);
			
			// check if the previous point is equal to this point
			
			// since the next point is not equal to this point
			// if this is true we still need to add the point because
			// it is the last of a string of coincident vertices
			if (!prevToPoint.isZero()) {
				// compute the cross product
				double cross = prevToPoint.cross(pointToNext);
				
				// if the cross product is near zero then point is a colinear point
				if (Math.abs(cross) <= Epsilon.E) {
					continue;
				}
			}
			
			// sum the current signed area
			winding += point.cross(next);
			
			// otherwise the point is valid
			result.add(point);
		}
		
		// check the winding
		if (winding < 0.0) {
			Geometry.reverseWinding(result);
		}
		
		return result;
	}
	
	/**
	 * Returns a new array containing the 'cleansed' version of the given array of polygon points.
	 * <p>
	 * This method ensures the polygon has CCW winding, removes colinear vertices, and removes coincident vertices.
	 * @param points the list polygon points
	 * @return {@link Vector2}[]
	 * @throws NullPointerException if points is null or points contains null elements
	 */
	public static Vector2[] cleanse(Vector2... points) {
		// check for null
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// create a list from the array
		List<Vector2> pointList = Arrays.asList(points);
		// cleanse the list
		List<Vector2> resultList = Geometry.cleanse(pointList);
		// convert it back to an array
		Vector2[] result = new Vector2[resultList.size()];
		resultList.toArray(result);
		// return the result
		return result;
	}
}
