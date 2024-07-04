/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.EmptyCollectionException;
import org.dyn4j.exception.NullElementException;
import org.dyn4j.exception.ValueOutOfRangeException;

/**
 * Contains static methods to perform standard geometric operations.
 * <p>
 * This class can be used to create {@link Shape}s of varying types via the <code>create</code>* methods.
 * While {@link Shape}s can be created using their constructors as well, the methods here can place their
 * centers on the origin and also make copies of the given input to avoid reuse issues.
 * <p>
 * This class also contains various helper methods for cleaning vector arrays and lists and performing
 * various operations on {@link Shape}s.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public final class Geometry {
	/** 2 * PI constant */
	public static final double TWO_PI = 2.0 * Math.PI;
	
	/** The value of 1/3 */
	public static final double INV_3 = 1.0 / 3.0;
	
	/** The value of the inverse of the square root of 3; 1/sqrt(3) */
	public static final double INV_SQRT_3 = 1.0 / Math.sqrt(3.0);
	
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
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// get the size
		int size = points.size();
		// the size must be larger than 1
		if (size < 2) 
			throw new ValueOutOfRangeException("points.size", size, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 2);
		
		// determine the winding by computing a signed "area"
		double area = 0.0;
		for (int i = 0; i < size; i++) {
			int j = i + 1 == size ? 0 : i + 1;
			// get the current point and the next point
			Vector2 p1 = points.get(i);
			Vector2 p2 = points.get(j);
			
			// check for null
			if (p1 == null)
				throw new NullElementException("points", i);
			
			if (p2 == null)
				throw new NullElementException("points", j);

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
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// get the size
		int size = points.length;
		// the size must be larger than 1
		if (size < 2) 
			throw new ValueOutOfRangeException("points.length", size, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 2);
		
		// determine the winding by computing a signed "area"
		double area = 0.0;
		for (int i = 0; i < size; i++) {
			int j = i + 1 == size ? 0 : i + 1;
			
			// get the current point and the next point
			Vector2 p1 = points[i];
			Vector2 p2 = points[j];
			
			// check for null
			if (p1 == null)
				throw new NullElementException("points", i);
			
			if (p2 == null)
				throw new NullElementException("points", j);
			
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
		if (points == null) 
			throw new ArgumentNullException("points");
		
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
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// check for a length of 0 or 1
		if (points.size() <= 1) return;
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
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// check for empty list
		if (points.isEmpty()) 
			throw new EmptyCollectionException("points");
		
		// get the size
		int size = points.size();
		// check for a list of one point
		if (size == 1) {
			Vector2 p = points.get(0);
			
			// make sure its not null
			if (p == null) 
				throw new NullElementException("points", 0);
			
			// return a copy
			return p.copy();
		}
		
		// otherwise perform the average
		Vector2 ac = new Vector2();
		for (int i = 0; i < size; i++) {
			Vector2 point = points.get(i);
			
			// check for null
			if (point == null) 
				throw new NullElementException("points", i);
			
			ac.add(point);
		}
		
		return ac.divide(size);
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
		if (points == null) 
			throw new ArgumentNullException("points");
		
		// get the length
		int size = points.length;
		// check for empty
		if (size == 0) 
			throw new EmptyCollectionException("points");
		
		// check for a list of one point
		if (size == 1) {
			Vector2 p = points[0];
			
			// check for null
			if (p == null) 
				throw new NullElementException("points", 0);
			
			return p.copy();
		}
		
		// otherwise perform the average
		Vector2 ac = new Vector2();
		for (int i = 0; i < size; i++) {
			Vector2 point = points[i];
			
			// check for null
			if (point == null) 
				throw new NullElementException("points", i);
			
			ac.add(point);
		}
		
		return ac.divide(size);
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
	 * <p style="white-space: pre;"> 0.5 * &sum;(x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)</p>
	 * Finding the area weighted centroid can be done by using the following
	 * summation:
	 * <p style="white-space: pre;"> 1 / (6 * A) * &sum;(p<sub>i</sub> + p<sub>i + 1</sub>) * (x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)</p>
	 * @param points the {@link Polygon} points
	 * @return {@link Vector2} the area weighted centroid
	 * @throws NullPointerException if points is null or an element of points is null
	 * @throws IllegalArgumentException if points is empty
	 */
	public static final Vector2 getAreaWeightedCenter(List<Vector2> points) {
		// calculate the average center
		// note that this also performs the necessary checks and throws any exceptions needed
		Vector2 ac = Geometry.getAverageCenter(points);
		int size = points.size();
		
		// otherwise perform the computation
		Vector2 center = new Vector2();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector2 p1 = points.get(i);
			Vector2 p2 = i + 1 < size ? points.get(i + 1) : points.get(0);
			p1 = p1.difference(ac);
			p2 = p2.difference(ac);
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
			center.add(p1.add(p2).multiply(INV_3).multiply(triangleArea));
		}
		// check for zero area
		if (Math.abs(area) <= Epsilon.E) {
			// zero area can only happen if all the points are the same point
			// in which case just return a copy of the first
			return points.get(0).copy();
		}
		// finish the centroid calculation by dividing by the total area
		center.divide(area).add(ac);
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
		// calculate the average center
		// note that this also performs the necessary checks and throws any exceptions needed
		Vector2 ac = Geometry.getAverageCenter(points);
		int size = points.length;

		Vector2 center = new Vector2();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector2 p1 = points[i];
			Vector2 p2 = i + 1 < size ? points[i + 1] : points[0];
			p1 = p1.difference(ac);
			p2 = p2.difference(ac);
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
			center.add(p1.add(p2).multiply(INV_3).multiply(triangleArea));
		}
		// check for zero area
		if (Math.abs(area) <= Epsilon.E) {
			// zero area can only happen if all the points are the same point
			// in which case just return a copy of the first
			return points[0].copy();
		}
		// finish the centroid calculation by dividing by the total area
		center.divide(area).add(ac);
		// return the center
		return center;
	}

	/**
	 * Returns the maximum radius of the given vertices rotated about the origin.
	 * <p>
	 * If the vertices array is null or empty, zero is returned.
	 * @param vertices the polygon vertices
	 * @return double
	 * @see #getRotationRadius(Vector2, Vector2...)
	 * @since 3.2.0
	 */
	public static final double getRotationRadius(Vector2... vertices) {
		return Geometry.getRotationRadius(new Vector2(), vertices);
	}
	
	/**
	 * Returns the maximum radius of the given vertices rotated about the given center.
	 * <p>
	 * If the vertices array is null or empty, zero is returned.  If center is null
	 * the origin will be used instead.
	 * @param center the center point
	 * @param vertices the polygon vertices
	 * @return double
	 * @since 3.2.0
	 */
	public static final double getRotationRadius(Vector2 center, Vector2... vertices) {
		// validate the vertices
		if (vertices == null) return 0.0;
		// validate the center
		if (center == null) center = new Vector2();
		// validate the length
		int size = vertices.length;
		if (size == 0) return 0.0;
		// find the maximum radius from the center
		double r2 = 0.0;
		for (int i = 0; i < size; i++) {
			Vector2 v = vertices[i];
			// validate each vertex
			if (v != null) {
				double r2t = center.distanceSquared(v);
				// keep the largest
				r2 = Math.max(r2, r2t);
			}
		}
		// set the radius
		return Math.sqrt(r2);
	}
	
	/**
	 * Returns an array of normalized vectors representing the normals of all the
	 * edges given the vertices.
	 * <p>
	 * This method assumes counter-clockwise ordering.
	 * <p>
	 * Returns null if the given vertices array is null or empty.
	 * @param vertices the vertices
	 * @return {@link Vector2}[]
	 * @throws NullPointerException if vertices contains a null element
	 * @since 3.2.0
	 */
	public static final Vector2[] getCounterClockwiseEdgeNormals(Vector2... vertices) {
		if (vertices == null) return null;
		
		int size = vertices.length;
		if (size == 0) return null;
		
		Vector2[] normals = new Vector2[size];
		for (int i = 0; i < size; i++) {
			// get the edge points
			Vector2 p1 = vertices[i];
			Vector2 p2 = (i + 1 == size) ? vertices[0] : vertices[i + 1];
			// create the edge and get its left perpedicular vector
			Vector2 n = p1.to(p2).left();
			// normalize it
			n.normalize();
			normals[i] = n;
		}
		
		return normals;
	}
	
	/**
	 * Returns a new {@link Circle} with the given radius centered on the origin.
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
	 * <p>
	 * The center of the {@link Polygon} will be computed using the area weighted method.
	 * @param vertices the array of vertices
	 * @return {@link Polygon}
	 * @throws NullPointerException if vertices is null or an element of vertices is null
	 * @throws IllegalArgumentException if vertices contains less than 3 non-null vertices
	 * @see #createPolygonAtOrigin(Vector2...) to create a new {@link Polygon} that is centered on the origin
	 */
	public static final Polygon createPolygon(Vector2... vertices) {
		// check the vertices array
		if (vertices == null) 
			throw new ArgumentNullException("vertices");
		
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
				throw new NullElementException("vertices", i);
			}
		}
		return new Polygon(verts);
	}
	
	/**
	 * Returns a new {@link Polygon}, using the given vertices, centered at the origin.
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
	 * Returns a new {@link Polygon} with count number of points, where the
	 * points are evenly distributed around the unit circle.  The resulting {@link Polygon}
	 * will be centered on the origin.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon to each vertex.
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex in meters
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException if count is less than 3 or radius is less than or equal to zero
	 * @see #createUnitCirclePolygon(int, double, double)
	 * @see #createPolygonalCircle(int, double)
	 * @see #createPolygonalCircle(int, double, double)
	 */
	public static final Polygon createUnitCirclePolygon(int count, double radius) {
		return Geometry.createUnitCirclePolygon(count, radius, 0.0);
	}
	
	/**
	 * Returns a new {@link Polygon} with count number of points, where the
	 * points are evenly distributed around the unit circle.  The resulting {@link Polygon}
	 * will be centered on the origin.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon to each vertex.
	 * <p>
	 * The theta parameter is a vertex angle offset used to rotate all the vertices
	 * by the given amount.
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex in meters
	 * @param theta the vertex angle offset in radians
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException if count is less than 3 or radius is less than or equal to zero
	 * @see #createPolygonalCircle(int, double, double)
	 */
	public static final Polygon createUnitCirclePolygon(int count, double radius, double theta) {
		return Geometry.createPolygonalCircle(count, radius, theta);
	}
	
	/**
	 * Creates a square (equal height and width {@link Rectangle}) with the given size 
	 * centered at the origin.
	 * @param size the size in meters
	 * @return {@link Rectangle}
	 * @throws IllegalArgumentException if size is less than or equal to zero
	 */
	public static final Rectangle createSquare(double size) {
		// check the size
		if (size <= 0.0) 
			throw new ValueOutOfRangeException("size", size, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0);
		
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
	 * Creates a new {@link Triangle}, using the given points.
	 * <p>
	 * This method makes a copy of the given points to create the {@link Triangle}.
	 * <p>
	 * The center of the {@link Triangle} will be computed using the area weighted method.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param p3 the third point
	 * @return {@link Triangle}
	 * @throws NullPointerException if p1, p2, or p3 is null
	 * @see #createTriangleAtOrigin(Vector2, Vector2, Vector2) to create a new {@link Triangle} that is centered on the origin
	 */
	public static final Triangle createTriangle(Vector2 p1, Vector2 p2, Vector2 p3) {
		if (p1 == null)
			throw new ArgumentNullException("p1");
		
		if (p2 == null)
			throw new ArgumentNullException("p2");
		
		if (p3 == null)
			throw new ArgumentNullException("p3");
		
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
	 * Creates a right angle {@link Triangle} with the center at the origin.
	 * @param width the width of the base in meters
	 * @param height the height in meters
	 * @return {@link Triangle}
	 * @throws IllegalArgumentException if width or height is less than or equal to zero
	 */
	public static final Triangle createRightTriangle(double width, double height) {
		return Geometry.createRightTriangle(width, height, false);
	}
	
	/**
	 * Creates a right angle {@link Triangle} with the center at the origin.
	 * @param width the width of the base in meters
	 * @param height the height in meters
	 * @param mirror true if the triangle should be mirrored along the y-axis
	 * @return {@link Triangle}
	 * @throws IllegalArgumentException if width or height is less than or equal to zero
	 */
	public static final Triangle createRightTriangle(double width, double height, boolean mirror) {
		// check the width
		if (width <= 0.0) 
			throw new ValueOutOfRangeException("width", width, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check the height
		if (height <= 0.0)
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		Vector2 top = new Vector2(0.0, height);
		Vector2 left = new Vector2(0.0, 0.0);
		Vector2 right = new Vector2(mirror ? -width : width, 0.0);
		Triangle triangle;
		if (mirror) {
			// make sure it has anti-clockwise winding
			triangle = new Triangle(top, right, left);
		} else {
			triangle = new Triangle(top, left, right);
		}
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
		if (height <= 0.0) 
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
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
		if (width <= 0.0) 
			throw new ValueOutOfRangeException("width", width, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check the height
		if (height <= 0.0)
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
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
	 * <p>
	 * The center of the {@link Segment} will be the average of the two points.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return {@link Segment}
	 * @throws NullPointerException if p1 or p2 is null
	 * @see #createSegmentAtOrigin(Vector2, Vector2) to create a {@link Segment} centered on the origin
	 */
	public static final Segment createSegment(Vector2 p1, Vector2 p2) {
		if (p1 == null)
			throw new ArgumentNullException("p1");
		
		if (p2 == null)
			throw new ArgumentNullException("p2");

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
		if (length <= 0.0)
			throw new ValueOutOfRangeException("length", length, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
			
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
		if (length <= 0.0)
			throw new ValueOutOfRangeException("length", length, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		Vector2 start = new Vector2(0.0, -length * 0.5);
		Vector2 end = new Vector2(0.0, length * 0.5);
		return new Segment(start, end);
	}
	
	/**
	 * Creates a new {@link Capsule} bounded by the given rectangle width and height.
	 * <p>
	 * The capsule will be axis-aligned and centered on the origin with the caps on the
	 * ends of the largest dimension.
	 * <p>
	 * If width and height are equal use a {@link Circle} shape instead.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return {@link Capsule}
	 * @throws IllegalArgumentException if width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Capsule createCapsule(double width, double height) {
		return new Capsule(width, height);
	}
	
	/**
	 * Creates a new {@link Slice} with the given circle radius and arc length theta.
	 * <p>
	 * A {@link Slice} is an arbitrary slice of a circle. The specified radius is the radius
	 * of the circle. The slice will be positioned with the <i>circle center</i> on the origin.
	 * <p>
	 * Theta is the total arc length of the slice specified in radians. Theta is halved, putting
	 * half the arc length below the x-axis and half above.
	 * <p>
	 * Theta cannot be greater than &pi;.
	 * @param radius the circle radius
	 * @param theta the total arc length in radians
	 * @return {@link Slice}
	 * @throws IllegalArgumentException if radius is less than or equal to zero; if theta is less than or equal to zero or is greater than &pi;
	 * @since 3.1.5
	 */
	public static final Slice createSlice(double radius, double theta) {
		return new Slice(radius, theta);
	}
	
	/**
	 * Creates a new {@link Slice} with the given circle radius and arc length theta.
	 * <p>
	 * A {@link Slice} is an arbitrary slice of a circle. The specified radius is the radius
	 * of the circle. The slice will be positioned with the <i>centroid</i> at the origin.
	 * <p>
	 * Theta is the total arc length of the slice specified in radians. Theta is halved, putting
	 * half the arc length below the x-axis and half above.
	 * <p>
	 * Theta cannot be greater than &pi;.
	 * @param radius the circle radius
	 * @param theta the total arc length in radians
	 * @return {@link Slice}
	 * @throws IllegalArgumentException if radius is less than or equal to zero; if theta is less than or equal to zero or is greater than &pi;
	 * @since 3.1.5
	 */
	public static final Slice createSliceAtOrigin(double radius, double theta) {
		Slice slice = new Slice(radius, theta);
		slice.translate(-slice.center.x, -slice.center.y);
		return slice;
	}
	
	/**
	 * Creates a new {@link Ellipse} bounded by the given rectangle width and height.
	 * <p>
	 * The ellipse will be axis-aligned and centered on the origin.
	 * <p>
	 * If width and height are equal use a {@link Circle} shape instead.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return {@link Ellipse}
	 * @throws IllegalArgumentException if width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Ellipse createEllipse(double width, double height) {
		return new Ellipse(width, height);
	}
	
	/**
	 * Creates a new {@link HalfEllipse} bounded by the given rectangle width and height.
	 * <p>
	 * The ellipse will be axis-aligned with the base of the half ellipse on the x-axis. The given height
	 * is the height of the half, not the height of the full ellipse.
	 * <p>
	 * If width and height are equal use a {@link Slice} shape with <code>theta = Math.PI</code> instead.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return {@link HalfEllipse}
	 * @throws IllegalArgumentException if width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final HalfEllipse createHalfEllipse(double width, double height) {
		return new HalfEllipse(width, height);
	}
	
	/**
	 * Creates a new {@link HalfEllipse} bounded by the given rectangle width and height.
	 * <p>
	 * The ellipse will be axis-aligned with the base of the half ellipse on the x-axis. The given height
	 * is the height of the half, not the height of the full ellipse.
	 * <p>
	 * If width and height are equal use a {@link Slice} shape with <code>theta = Math.PI</code> instead.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return {@link HalfEllipse}
	 * @throws IllegalArgumentException if width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final HalfEllipse createHalfEllipseAtOrigin(double width, double height) {
		HalfEllipse half = new HalfEllipse(width, height);
		Vector2 c = half.getCenter();
		half.translate(-c.x, -c.y);
		return half;
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of a circle with count number of vertices centered
	 * on the origin.
	 * @param count the number of vertices to use; must be greater than 2
	 * @param radius the radius of the circle; must be greater than zero
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 3 or the radius is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalCircle(int count, double radius) {
		return Geometry.createPolygonalCircle(count, radius, 0);
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of a circle with count number of vertices centered
	 * on the origin.
	 * @param count the number of vertices to use; must be greater than or equal to 3
	 * @param radius the radius of the circle; must be greater than zero
	 * @param theta the radial offset for the points in radians
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 3 or the radius is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalCircle(int count, double radius, double theta) {
		// validate the input
		if (count < 3) 
			throw new ValueOutOfRangeException("count", count, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 3);
		
		if (radius <= 0.0) 
			throw new ValueOutOfRangeException("radius", radius, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// compute the angular increment
		final double pin = Geometry.TWO_PI / count;
		// make sure the resulting output is an even number of vertices
		final Vector2[] vertices = new Vector2[count];
		
		final double c = Math.cos(pin);
		final double s = Math.sin(pin);
		double t = 0;

		double x = radius;
		double y = 0;
		// initialize at theta if necessary
		if (theta != 0) {
			x = radius * Math.cos(theta);
			y = radius * Math.sin(theta);
		}
		
		for(int i = 0; i < count; i++) {
			vertices[i] = new Vector2(x, y);

			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		} 
		
		return new Polygon(vertices);
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of a {@link Slice} with count number of vertices with the
	 * circle center centered on the origin.
	 * <p>
	 * This method returns a polygon with count + 3 vertices.
	 * @param count the number of vertices to use; must be greater than or equal to 1
	 * @param radius the radius of the circle; must be greater than zero
	 * @param theta the arc length of the slice in radians; must be greater than zero
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 1 or the radius is less than or equal to zero or theta is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalSlice(int count, double radius, double theta) {
		// validate the input
		if (count < 1) 
			throw new ValueOutOfRangeException("count", count, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 1);
		
		if (radius <= 0.0) 
			throw new ValueOutOfRangeException("radius", radius, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		if (theta <= 0.0) 
			throw new ValueOutOfRangeException("theta", theta, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// compute the angular increment
		final double pin = theta / (count + 1);
		// make sure the resulting output is an even number of vertices
		final Vector2[] vertices = new Vector2[count + 3];
		
		final double c = Math.cos(pin);
		final double s = Math.sin(pin);
		double t = 0;

		// initialize at minus theta
		double x = radius * Math.cos(-theta * 0.5);
		double y = radius * Math.sin(-theta * 0.5);
		
		// set the first and last points of the arc
		vertices[0] = new Vector2(x, y);
		vertices[count + 1] = new Vector2(x, -y);
		
		for(int i = 1; i < count + 1; i++) {
			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
			// add a point
			vertices[i] = new Vector2(x, y);
		}
		
		// finish off by adding the origin
		vertices[count + 2] = new Vector2();
		
		return new Polygon(vertices);
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of a {@link Slice} with count number of vertices centered on the origin.
	 * <p>
	 * This method returns a polygon with count + 3 vertices.
	 * @param count the number of vertices to use; must be greater than or equal to 1
	 * @param radius the radius of the circle; must be greater than zero
	 * @param theta the arc length of the slice in radians; must be greater than zero
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 1 or the radius is less than or equal to zero or theta is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalSliceAtOrigin(int count, double radius, double theta) {
		Polygon polygon = Geometry.createPolygonalSlice(count, radius, theta);
		Vector2 center = polygon.getCenter();
		polygon.translate(-center.x, -center.y);
		return polygon;
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of an ellipse with count number of vertices centered
	 * on the origin.
	 * <p>
	 * The count should be greater than or equal to 4 and a multiple of 2.  If not, the returned polygon will have count - 1
	 * vertices.
	 * @param count the number of vertices to use; must be greater than or equal to 4; should be even, if not, count - 1 vertices will be generated
	 * @param width the width of the ellipse
	 * @param height the height of the ellipse
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 4 or the width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalEllipse(int count, double width, double height) {
		// validate the input
		if (count < 4) 
			throw new ValueOutOfRangeException("count", count, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 4);

		// check the width
		if (width <= 0.0) 
			throw new ValueOutOfRangeException("width", width, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check the height
		if (height <= 0.0)
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		final double a = width * 0.5;
		final double b = height * 0.5;
		
		final int n2 = count / 2;
		// compute the angular increment
		final double pin2 = Math.PI / n2;
		// make sure the resulting output is an even number of vertices
		final Vector2[] vertices = new Vector2[n2 * 2];
		
		// use the parametric equations:
		// x = a * cos(t)
		// y = b * sin(t)
		
		int j = 0;
		for (int i = 0; i < n2 + 1; i++) {
			final double t = pin2 * i;
			// since the under side of the ellipse is the same
			// as the top side, only with a negated y, lets save
			// some time by creating the under side at the same time
			final double x = a * Math.cos(t);
			final double y = b * Math.sin(t);
			if (i > 0) {
				vertices[vertices.length - j] = new Vector2(x, -y);
			}
			vertices[j++] = new Vector2(x, y);
		}
		
		return new Polygon(vertices);
	}

	/**
	 * Creates a new {@link Polygon} in the shape of a half ellipse with count number of vertices with the
	 * base at the origin.
	 * <p>
	 * Returns a polygon with count + 2 vertices.
	 * <p>
	 * The height is the total height of the half not the half height.
	 * @param count the number of vertices to use; must be greater than or equal to 1
	 * @param width the width of the half ellipse
	 * @param height the height of the half ellipse; should be the total height
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 1 or the width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalHalfEllipse(int count, double width, double height) {
		// validate the input
		if (count < 4) 
			throw new ValueOutOfRangeException("count", count, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 4);

		// check the width
		if (width <= 0.0) 
			throw new ValueOutOfRangeException("width", width, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check the height
		if (height <= 0.0)
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		final double a = width * 0.5;
		final double b = height * 0.5;
		
		// compute the angular increment
		final double inc = Math.PI / (count + 1);
		// make sure the resulting output is an even number of vertices
		final Vector2[] vertices = new Vector2[count + 2];
		
		// set the start and end vertices
		vertices[0] = new Vector2(a, 0);
		vertices[count + 1] = new Vector2(-a, 0);
		
		// use the parametric equations:
		// x = a * cos(t)
		// y = b * sin(t)
		
		for (int i = 1; i < count + 1; i++) {
			final double t = inc * i;
			// since the under side of the ellipse is the same
			// as the top side, only with a negated y, lets save
			// some time by creating the under side at the same time
			final double x = a * Math.cos(t);
			final double y = b * Math.sin(t);
			vertices[i] = new Vector2(x, y);
		}
		
		return new Polygon(vertices);
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of a half ellipse with count number of vertices centered
	 * on the origin.
	 * <p>
	 * Returns a polygon with count + 2 vertices.
	 * <p>
	 * The height is the total height of the half not the half height.
	 * @param count the number of vertices to use; should be even, if not, count - 1 vertices will be generated
	 * @param width the width of the half ellipse
	 * @param height the height of the half ellipse; should be the total height
	 * @return {@link Polygon}
	 * @throws IllegalArgumentException thrown if count is less than 1 or the width or height are less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalHalfEllipseAtOrigin(int count, double width, double height) {
		Polygon polygon = Geometry.createPolygonalHalfEllipse(count, width, height);
		Vector2 center = polygon.getCenter();
		polygon.translate(-center.x, -center.y);
		return polygon;
	}
	
	/**
	 * Creates a new {@link Polygon} in the shape of a capsule using count number of vertices on each
	 * cap, centered on the origin.  The caps will be on the ends of the largest dimension.
	 * <p>
	 * The returned polygon will have 4 + 2 * count number of vertices.
	 * @param count the number of vertices to use for one cap; must be greater than or equal to 1
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return {@link Polygon}
	 * @since 3.1.5
	 */
	public static final Polygon createPolygonalCapsule(int count, double width, double height) {
		// validate the input
		if (count < 1) 
			throw new ValueOutOfRangeException("count", count, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 1);

		// check the width
		if (width <= 0.0) 
			throw new ValueOutOfRangeException("width", width, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check the height
		if (height <= 0.0)
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// if the width and height are close enough to being equal, just return a circle
		if (Math.abs(width - height) < Epsilon.E) {
			return Geometry.createPolygonalCircle(count, width);
		}
		
		// compute the angular increment
		final double pin = Math.PI / (count + 1);
		// 4 rect verts plus 2 * circle half verts
		final Vector2[] vertices = new Vector2[4 + 2 * count];
		
		final double c = Math.cos(pin);
		final double s = Math.sin(pin);
		double t = 0;

		// get the major and minor axes
		double major = width;
		double minor = height;
		boolean vertical = false;
		if (width < height) {
			major = height;
			minor = width;
			vertical = true;
		}
		
		// get the radius from the minor axis
		double radius = minor * 0.5;
		
		// compute the x/y offsets
		double offset = major * 0.5 - radius;
		double ox = 0;
		double oy = 0;
		if (vertical) {
			// aligned to the y
			oy = offset;
		} else {
			// aligned to the x
			ox = offset;
		}
		
		int n = 0;
		
		// right cap
		double ao = vertical ? 0 : Math.PI * 0.5;
		double x = radius * Math.cos(pin - ao);
		double y = radius * Math.sin(pin - ao);
		for(int i = 0; i < count; i++) {
			vertices[n++] = new Vector2(x + ox, y + oy);

			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}
		
		// add in top/left vertices
		if (vertical) {
			vertices[n++] = new Vector2(-radius,  oy);
			vertices[n++] = new Vector2(-radius, -oy);
		} else {
			vertices[n++] = new Vector2( ox, radius);
			vertices[n++] = new Vector2(-ox, radius);
		}
		
		// left cap
		ao = vertical ? Math.PI : Math.PI * 0.5;
		x = radius * Math.cos(pin + ao);
		y = radius * Math.sin(pin + ao);
		for(int i = 0; i < count; i++) {
			vertices[n++] = new Vector2(x - ox, y - oy);

			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}
		
		// add in bottom/right vertices
		if (vertical) {
			vertices[n++] = new Vector2(radius, -oy);
			vertices[n++] = new Vector2(radius,  oy);
		} else {
			vertices[n++] = new Vector2(-ox, -radius);
			vertices[n++] = new Vector2( ox, -radius);
		}
		
		return new Polygon(vertices);
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
		if (points == null) 
			throw new ArgumentNullException("points");
		
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
			int n = i - 1 < 0 ? size - 1 : i - 1;
			int m = i + 1 == size ? 0 : i + 1;
			Vector2 prev = points.get(n);
			Vector2 next = points.get(m);
			
			// check for null
			if (point == null) 
				throw new NullElementException("points", i);
			
			if (prev == null)
				throw new NullElementException("points", n);
			
			if (next == null)
				throw new NullElementException("points", m);
			
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
	public static final Vector2[] cleanse(Vector2... points) {
		// check for null
		if (points == null) 
			throw new ArgumentNullException("points");
		
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
	
	/**
	 * Flips the given polygon about its center along the x-axis and
	 * returns the result as a new polygon.
	 * <p>
	 * This method assumes that the line is through the origin.
	 * @param polygon the polygon to flip
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon is null
	 * @see #flip(Polygon, Vector2)
	 * @see #flip(Polygon, Vector2, Vector2)
	 * @since 3.1.4
	 */
	public static final Polygon flipAlongTheXAxis(Polygon polygon) {
		return Geometry.flip(polygon, Vector2.X_AXIS, null);
	}
	
	/**
	 * Flips the given polygon about its center along the y-axis and
	 * returns the result as a new polygon.
	 * <p>
	 * This method assumes that the line is through the origin.
	 * @param polygon the polygon to flip
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon is null
	 * @see #flip(Polygon, Vector2)
	 * @see #flip(Polygon, Vector2, Vector2)
	 * @since 3.1.4
	 */
	public static final Polygon flipAlongTheYAxis(Polygon polygon) {
		return Geometry.flip(polygon, Vector2.Y_AXIS, null);
	}
	
	/**
	 * Flips the given polygon about the given point along the x-axis and
	 * returns the result as a new polygon.
	 * @param polygon the polygon to flip
	 * @param point the point to flip about
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon is null
	 * @see #flip(Polygon, Vector2)
	 * @see #flip(Polygon, Vector2, Vector2)
	 * @since 3.1.4
	 */
	public static final Polygon flipAlongTheXAxis(Polygon polygon, Vector2 point) {
		return Geometry.flip(polygon, Vector2.X_AXIS, point);
	}
	
	/**
	 * Flips the given polygon about the given point along the y-axis and
	 * returns the result as a new polygon.
	 * @param polygon the polygon to flip
	 * @param point the point to flip about
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon is null
	 * @see #flip(Polygon, Vector2)
	 * @see #flip(Polygon, Vector2, Vector2)
	 * @since 3.1.4
	 */
	public static final Polygon flipAlongTheYAxis(Polygon polygon, Vector2 point) {
		return Geometry.flip(polygon, Vector2.Y_AXIS, point);
	}
	
	/**
	 * Flips the given polygon about the given line and returns the result
	 * as a new polygon.
	 * <p>
	 * This method assumes that the line is through the origin.
	 * @param polygon the polygon to flip
	 * @param axis the axis to flip about
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon or axis is null
	 * @throws IllegalArgumentException if the given axis is the zero vector
	 * @see #flip(Polygon, Vector2, Vector2)
	 * @since 3.1.4
	 */
	public static final Polygon flip(Polygon polygon, Vector2 axis) {
		return Geometry.flip(polygon, axis, null);
	}
	
	/**
	 * Flips the given polygon about the given line and returns the result
	 * as a new polygon.
	 * @param polygon the polygon to flip
	 * @param axis the axis to flip about
	 * @param point the point to flip about; if null, the polygon center is used
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon or axis is null
	 * @throws IllegalArgumentException if the given axis is the zero vector
	 * @since 3.1.4
	 */
	public static final Polygon flip(Polygon polygon, Vector2 axis, Vector2 point) {
		// check for valid input
		if (polygon == null) 
			throw new ArgumentNullException("polygon");
		
		if (axis == null) 
			throw new ArgumentNullException("axis");
		
		if (axis.isZero()) 
			throw new IllegalArgumentException("The axis cannot be a zero vector");
		
		// just use the center of the polygon if the given point is null
		if (point == null) point = polygon.getCenter();
		// flip about the axis and point
		// make sure the axis is normalized
		axis.normalize();
		Vector2[] pv = polygon.getVertices();
		Vector2[] nv = new Vector2[pv.length];
		for (int i = 0; i < pv.length; i++) {
			Vector2 v0 = pv[i];
			// center on the origin
			Vector2 v1 = v0.difference(point);
			// get the projection of the point onto the axis
			double proj = v1.dot(axis);
			// get the point on the axis
			Vector2 vp = axis.product(proj);
			// get the point past the projection
			Vector2 rv = vp.add(vp.x - v1.x, vp.y - v1.y);
			nv[i] = rv.add(point);
		}
		// check the winding
		if (Geometry.getWinding(nv) < 0.0) {
			Geometry.reverseWinding(nv);
		}
		return new Polygon(nv);
	}
	
	/**
	 * Returns the Minkowski Sum of the given convex shapes.
	 * <p>
	 * This method computes the Minkowski Sum in O(n + m) time where n and m are the number
	 * of vertices of the first and second convex respectively.
	 * <p>
	 * This method accepts any {@link Convex} {@link Wound} shape which basically means
	 * {@link Polygon}s or {@link Segment}s.
	 * <p>
	 * This method will compute the minkowski sum based on the current position of the input
	 * convex. This means that the result polygon may not be positioned at a location that's expected.
	 * There are two ways to solve this. The preferred approach is that both input {@link Convex}
	 * are centered at the origin. This ensures the result will be positioned at the origin AND
	 * helps with the numeric accuracy of the computation. The alternative is to leave the input
	 * {@link Convex} as is and translate the resulting {@link Polygon} by the negative of it's
	 * current position.
	 * <p>
	 * This method throws an IllegalArgumentException if two {@link Segment}s are supplied
	 * that are colinear (in this case the resulting Minkowski Sum would be another segment
	 * rather than a polygon).
	 * @param convex1 the first convex
	 * @param convex2 the second convex
	 * @param <E> either a {@link Wound} or {@link Convex} type
	 * @return {@link Polygon}
	 * @throws NullPointerException if convex1 or convex2 are null
	 * @throws IllegalArgumentException if both convex1 and convex2 are {@link Segment}s and are colinear
	 * @since 3.1.5
	 */
	public static final <E extends Wound & Convex> Polygon minkowskiSum(E convex1, E convex2) {
		if (convex1 == null) 
			throw new ArgumentNullException("convex1");
		
		if (convex2 == null) 
			throw new ArgumentNullException("convex2");
		
		Vector2[] p1v = convex1.getVertices();
		Vector2[] p2v = convex2.getVertices();
		
		// check for two segments
		if (convex1 instanceof Segment && convex2 instanceof Segment) {
			// check if they are colinear
			Vector2 s1 = p1v[0].to(p1v[1]);
			Vector2 s2 = p2v[0].to(p2v[1]);
			if (s1.cross(s2) <= Epsilon.E) {
				throw new IllegalArgumentException("Two segments were given and they are colinear");
			}
		}
		
		int c1 = p1v.length;
		int c2 = p2v.length;
		
		// find the minimum y-coordinate vertex in the first polygon
		// (in the case of a tie, use the minimum x-coordinate vertex)
		int i = 0, j = 0;
		Vector2 min = new Vector2(Double.MAX_VALUE, Double.MAX_VALUE);
		for (int k = 0; k < c1; k++) {
			Vector2 v = p1v[k];
			if (v.y < min.y) {
				min.set(v);
				i = k;
			} else if (v.y == min.y) {
				if (v.x < min.x) {
					min.set(v);
					i = k;
				}
			}
		}
		// find the minimum y-coordinate vertex in the second polygon
		// (in the case of a tie, use the minimum x-coordinate vertex)
		min.set(Double.MAX_VALUE, Double.MAX_VALUE);
		for (int k = 0; k < c2; k++) {
			Vector2 v = p2v[k];
			if (v.y < min.y) {
				min.set(v);
				j = k;
			} else if (v.y == min.y) {
				if (v.x < min.x) {
					min.set(v);
					j = k;
				}
			}
		}
		
		// iterate through the vertices
		int n1 = c1 + i;
		int n2 = c2 + j;
		// the maximum number of vertices for the output shape is m + n
		List<Vector2> sum = new ArrayList<Vector2>(c1 + c2);
		for (; i < n1 || j < n2;) {
			// get the current edges
			Vector2 v1s = p1v[i % c1];
			Vector2 v1e = p1v[(i + 1) % c1];
			
			Vector2 v2s = p2v[j % c2];
			Vector2 v2e = p2v[(j + 1) % c2];
			
			// add the vertex to the final output
			
			// on the first iteration we can assume this is a correct
			// one since we started at the minimum y-coordinate vertices
			
			// on subsequent interations we can assume this is a correct
			// one since the angle condition was used to increment the
			// vertex index
			Vector2 v = v1s.sum(v2s);
			sum.add(v);
			
			// compute the edge vectors
			Vector2 e1 = v1s.to(v1e);
			Vector2 e2 = v2s.to(v2e);
			
			// compare the polar angles between the edges
			double a3 = e1.cross(e2);
			
			// check for near parallel edges
			if (Math.abs(a3) <= Epsilon.E) {
				a3 = 0.0;
			}
			
			// determine which vertex to use next
			if (a3 > 0) {
				i++;
			} else if (a3 < 0) {
				j++;
			} else {
				i++;
				j++;
			}
		}
		
		return new Polygon(sum.toArray(new Vector2[0]));
	}
	
	/**
	 * Performs the Minkowski Sum of the given {@link Polygon} and {@link Circle}.
	 * <p>
	 * Use the count parameter to specify the number of vertices to use per round corner.
	 * <p>
	 * If the given polygon has <i>n</i> number of vertices, the returned polygon will have 
	 * <i>n * 2 + n * count</i> number of vertices.
	 * <p>
	 * This method is O(n) where n is the number of vertices in the given polygon.
	 * @param polygon the polygon
	 * @param circle the circle to add to the polygon
	 * @param count the number of vertices to add for each rounded corner; must be greater than zero
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon or circle is null
	 * @throws IllegalArgumentException if the given radius or count is less than or equal to zero
	 * @since 3.1.5
	 * @see #minkowskiSum(Polygon, double, int)
	 */
	public static final Polygon minkowskiSum(Circle circle, Polygon polygon, int count) {
		return Geometry.minkowskiSum(polygon, circle, count);
	}
	
	/**
	 * Performs the Minkowski Sum of the given {@link Polygon} and {@link Circle}.
	 * <p>
	 * Use the count parameter to specify the number of vertices to use per round corner.
	 * <p>
	 * If the given polygon has <i>n</i> number of vertices, the returned polygon will have 
	 * <i>n * 2 + n * count</i> number of vertices.
	 * <p>
	 * This method is O(n) where n is the number of vertices in the given polygon.
	 * @param polygon the polygon
	 * @param circle the circle to add to the polygon
	 * @param count the number of vertices to add for each rounded corner; must be greater than zero
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon or circle is null
	 * @throws IllegalArgumentException if the given radius or count is less than or equal to zero
	 * @since 3.1.5
	 * @see #minkowskiSum(Polygon, double, int)
	 */
	public static final Polygon minkowskiSum(Polygon polygon, Circle circle, int count) {
		if (circle == null) 
			throw new ArgumentNullException("circle");
		
		return Geometry.minkowskiSum(polygon, circle.radius, count);
	}
	
	/**
	 * Returns a new polygon that has been radially expanded.  This is equivalent to the Minkowski sum of
	 * a circle, of the given radius, and the given polygon.
	 * <p>
	 * Use the count parameter to specify the number of vertices to use per round corner.
	 * <p>
	 * If the given polygon has <i>n</i> number of vertices, the returned polygon will have 
	 * <i>n * 2 + n * count</i> number of vertices.
	 * <p>
	 * This method is O(n) where n is the number of vertices in the given polygon.
	 * @param polygon the polygon to expand radially
	 * @param radius the radial expansion; must be greater than zero
	 * @param count the number of vertices to add for each rounded corner; must be greater than zero
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon is null
	 * @throws IllegalArgumentException if the given radius or count is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon minkowskiSum(Polygon polygon, double radius, int count) {
		// check for valid input
		if (polygon == null)
			throw new ArgumentNullException("polygon");
		
		if (radius <= 0.0) 
			throw new ValueOutOfRangeException("radius", radius, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);

		if (count <= 0) 
			throw new ValueOutOfRangeException("count", count, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		Vector2[] vertices = polygon.vertices;
		Vector2[] normals = polygon.normals;
		int size = vertices.length;
		
		Vector2[] nVerts = new Vector2[size * 2 + size * count];
		// perform the expansion
		int j = 0;
		for (int i = 0; i < size; i++) {
			Vector2 v1 = vertices[i];
			Vector2 v2 = vertices[i + 1 == size ? 0 : i + 1];
			Vector2 normal = normals[i];
			Vector2 nv1 = normal.product(radius).add(v1); 
			Vector2 nv2 = normal.product(radius).add(v2);
			
			// generate the previous polygonal arc with count vertices
			// compute (circular) angle between the edges
			Vector2 cv1 = null;
			if (i == 0) {
				// if its the first iteration, then we need to compute the
				// last vertex's new position
				Vector2 tn = normals[size - 1];
				cv1 = v1.to(tn.product(radius).add(v1));
			} else {
				cv1 = v1.to(nVerts[j - 1]);
			}
			Vector2 cv2 = v1.to(nv1);
			final double theta = cv1.getAngleBetween(cv2);
			// compute the angular increment
			final double pin = theta / (count + 1);
			
			final double c = Math.cos(pin);
			final double s = Math.sin(pin);
			double t = 0;

			// compute the start theta
			double sTheta = Vector2.X_AXIS.getAngleBetween(normals[i - 1 < 0 ? size - 1 : i - 1]);
			if (sTheta < 0) {
				sTheta += Geometry.TWO_PI;
			}
			
			// initialize at minus theta
			double x = radius * Math.cos(sTheta);
			double y = radius * Math.sin(sTheta);
			
			for(int k = 0; k < count; k++) {
				//apply the rotation matrix
				t = x;
				x = c * x - s * y;
				y = s * t + c * y;
				// add a point
				nVerts[j++] = new Vector2(x, y).add(v1);
			}
			
			nVerts[j++] = nv1;
			nVerts[j++] = nv2;
		}
		
		return new Polygon(nVerts);
	}
	
	/**
	 * Returns a scaled version of the given circle.
	 * @param circle the circle
	 * @param scale the scale; must be greater than zero
	 * @return {@link Circle}
	 * @throws NullPointerException if the given circle is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Circle scale(Circle circle, double scale) {
		if (circle == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		return new Circle(circle.radius * scale);
	}
	
	/**
	 * Returns a scaled version of the given capsule.
	 * @param capsule the capsule
	 * @param scale the scale; must be greater than zero
	 * @return {@link Capsule}
	 * @throws NullPointerException if the given capsule is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Capsule scale(Capsule capsule, double scale) {
		if (capsule == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		return new Capsule(capsule.getLength() * scale, capsule.getCapRadius() * 2.0 * scale);
	}
	
	/**
	 * Returns a scaled version of the given ellipse.
	 * @param ellipse the ellipse
	 * @param scale the scale; must be greater than zero
	 * @return {@link Ellipse}
	 * @throws NullPointerException if the given ellipse is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Ellipse scale(Ellipse ellipse, double scale) {
		if (ellipse == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		return new Ellipse(ellipse.getWidth() * scale, ellipse.getHeight() * scale);
	}

	/**
	 * Returns a scaled version of the given half-ellipse.
	 * @param halfEllipse the half-ellipse
	 * @param scale the scale; must be greater than zero
	 * @return {@link HalfEllipse}
	 * @throws NullPointerException if the given half-ellipse is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final HalfEllipse scale(HalfEllipse halfEllipse, double scale) {
		if (halfEllipse == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		return new HalfEllipse(halfEllipse.getWidth() * scale, halfEllipse.getHeight() * scale);
	}
	
	/**
	 * Returns a scaled version of the given slice.
	 * @param slice the slice
	 * @param scale the scale; must be greater than zero
	 * @return {@link Slice}
	 * @throws NullPointerException if the given slice is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Slice scale(Slice slice, double scale) {
		if (slice == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		return new Slice(slice.getSliceRadius() * scale, slice.getTheta());
	}
	
	/**
	 * Returns a scaled version of the given polygon.
	 * @param polygon the polygon
	 * @param scale the scale; must be greater than zero
	 * @return {@link Polygon}
	 * @throws NullPointerException if the given polygon is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Polygon scale(Polygon polygon, double scale) {
		if (polygon == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		Vector2[] oVertices = polygon.vertices;
		int size = oVertices.length;
		
		Vector2[] vertices = new Vector2[size];
		Vector2 center = polygon.center;
		for (int i = 0; i < size; i++) {
			vertices[i] = center.to(oVertices[i]).multiply(scale).add(center);
		}
		
		return new Polygon(vertices);
	}
	
	/**
	 * Returns a scaled version of the given segment.
	 * @param segment the segment
	 * @param scale the scale; must be greater than zero
	 * @return {@link Segment}
	 * @throws NullPointerException if the given segment is null
	 * @throws IllegalArgumentException if the given scale is less than or equal to zero
	 * @since 3.1.5
	 */
	public static final Segment scale(Segment segment, double scale) {
		if (segment == null) 
			throw new ArgumentNullException("circle");
		
		if (scale <= 0) 
			throw new ValueOutOfRangeException("scale", scale, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		final double length = segment.getLength() * scale * 0.5;
		Vector2 n = segment.vertices[0].to(segment.vertices[1]);
		n.normalize();
		n.multiply(length);
		return new Segment(segment.center.sum(n.x, n.y), segment.center.difference(n.x, n.y));
	}
	
	/**
	 * Creates a list of {@link Link}s for the given vertices.
	 * <p>
	 * If the closed parameter is true, an extra link is created joining the last and first
	 * vertices in the list.
	 * @param vertices the poly-line vertices
	 * @param closed true if the shape should be enclosed
	 * @return List&lt;{@link Link}&gt;
	 * @throws NullPointerException if the list of vertices is null or an element of the vertex list is null
	 * @throws IllegalArgumentException if the list of vertices doesn't contain 2 or more elements
	 * @since 3.2.2
	 */
	public static final List<Link> createLinks(List<Vector2> vertices, boolean closed) {
		return Geometry.createLinks(vertices.toArray(new Vector2[0]), closed);
	}
	
	/**
	 * Creates a {@link Link} chain for the given vertices.
	 * <p>
	 * If the closed parameter is true, an extra link is created joining the last and first
	 * vertices in the array.
	 * @param vertices the poly-line vertices
	 * @param closed true if the shape should be enclosed
	 * @return List&lt;{@link Link}&gt;
	 * @throws NullPointerException if the array of vertices is null or an element of the vertex array is null
	 * @throws IllegalArgumentException if the array of vertices doesn't contain 2 or more elements
	 * @since 3.2.2
	 */
	public static final List<Link> createLinks(Vector2[] vertices, boolean closed) {
		// check the vertex array
		if (vertices == null) 
			throw new ArgumentNullException("vertices");
		
		// get the vertex length
		int size = vertices.length;
		// the size must be larger than 1 (2 or more)
		if (size < 2)
			throw new ValueOutOfRangeException("vertices.length", size, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 2);
		
		// generate the links
		List<Link> links = new ArrayList<Link>();
		for (int i = 0; i < size - 1; i++) {
			Vector2 p0 = null;
			if (i == 0) {
				p0 = closed ? vertices[size - 1] : null;
			} else {
				p0 = vertices[i - 1];
			}
			
			Vector2 p1 = vertices[i];
			Vector2 p2 = vertices[i + 1];
			
			Vector2 p3 = null;//vertices[i + 2];
			if (i + 2 >= size) {
				p3 = closed ? vertices[i + 2 - size] : null;
			} else {
				p3 = vertices[i + 2]; 
			}
			
			// check for null segment vertices
			if (p1 == null)
				throw new NullElementException("points", i);
			
			if (p2 == null)
				throw new NullElementException("points", i + 1);
			
			Link link = new Link(
					p0 != null ? p0.copy() : null, 
					p1.copy(), 
					p2.copy(),
					p3 != null ? p3.copy() : null);
			
			// add link to the list of links
			links.add(link);
		}
		
		if (closed) {
			// create a link to span the first and last vertex
			Vector2 p0 = vertices[size - 2].copy();
			Vector2 p1 = vertices[size - 1].copy();
			Vector2 p2 = vertices[0].copy();
			Vector2 p3 = vertices[1].copy();
			
			Link link = new Link(p0, p1, p2, p3);
			links.add(link);
		}
		
		return links;
	}
	
	/**
	 * Returns the intersection {@link Polygon} for the given {@link Polygon}s or returns null if there's no intersection.
	 * <p>
	 * Since all {@link Polygon}s in dyn4j are convex, their intersection will always be convex.
	 * <p>
	 * The basic premise of the algorithm is to track which polygon's edge is on the outside and when they intersect. This
	 * allows the algorithm to be O(n+m) complexity (linear time) by iterating the edges of each polygon until all intersections
	 * have been found. See the linked paper for more details.
	 * <p>
	 * NOTE: This algorithm returns null for all scenarios where the two {@link Polygon}s have touching edges or touching vertices.
	 * The primary reason for this is to improve the robustness of the algorithm, but also to ensure the output is always
	 * non-degenerate.
	 * @param p1 the first {@link Polygon}
	 * @param tx1 the first {@link Polygon}'s {@link Transform}
	 * @param p2 the second {@link Polygon}
	 * @param tx2 the second {@link Polygon}'s {@link Transform}
	 * @see <a href="https://www.cs.jhu.edu/~misha/Spring20/ORourke82.pdf">A New Linear Algorithm for Intersecting Convex Polygons</a>
	 * @return {@link Polygon}
	 * @since 4.2.1
	 */
	public static final Polygon getIntersection(Polygon p1, Transform tx1, Polygon p2, Transform tx2) {
		int firstIntersectionI = -1;
		int firstIntersectionP = -1;
		int firstIntersectionQ = -1;
		
		List<Vector2> result = new ArrayList<Vector2>();
		
		final int pn = p1.vertices.length;
		final int qn = p2.vertices.length;
		
		int pi = 0;
		int qi = 0;
		
		Vector2 p = tx1.getTransformed(p1.vertices[0]);
		Vector2 q = tx2.getTransformed(p2.vertices[0]);
		
		// get the previous point
		Vector2 p0 = tx1.getTransformed(p1.vertices[p1.vertices.length - 1]);
		Vector2 q0 = tx2.getTransformed(p2.vertices[p2.vertices.length - 1]);
		
		Vector2 pv = p0.to(p);
		Vector2 qv = q0.to(q);
		
		boolean insideP = false;
		boolean insideQ = false;
		
		final int n = 2 * (pn + qn);
		for (int i = 0; i < n; i++) {
			// step 1: check for intersection of the two current edges
			
			Vector2 intersection = Segment.getSegmentIntersection(p0, p, q0, q, false);
			if (intersection != null) {
				
				// check if this intersection is the same intersection as the first
				// this would indicate we've made it all the way around both shapes
				
				// the extra condition about the first intersection index is specifically
				// for the case where a vertex from one polygon lies on the edge of another
				// polygon. This ensures we don't exit early in these scenarios
				if (pi == firstIntersectionP && 
					qi == firstIntersectionQ && 
					(i - 1) != firstIntersectionI) {
					// then we're done, result should contain the intersection polygon
					return new Polygon(result.toArray(new Vector2[0]));
				}
				
				// record the first intersection
				if (firstIntersectionP == -1) {
					firstIntersectionI = i;
					firstIntersectionP = pi;
					firstIntersectionQ = qi;
				}
				
				// add the intersection point to the result
				result.add(intersection);
				
				// test whether p is on the inside or outside of qv
				insideP = Segment.getLocation(p, q0, q) >= 0;
				insideQ = !insideP;
			}
			
			// step 2: Advance p or q
			
			// determine the direction of p and q relative to one another
			if (qv.cross(pv) >= 0) {
				// q is pointing outside p1
				
				// is p inside p2?
				double ploc = Segment.getLocation(p, q0, q);
				if (ploc >= 0) {
					// should we add the current q vertex?
					if (insideQ) {
						result.add(q);
					}
					
					// advance q
					qi = qi + 1 == qn ? 0 : qi + 1;
					q0.set(q);
					q = tx2.getTransformed(p2.vertices[qi]);
					qv.set(q).subtract(q0);
				} else {
					// should we add the current p vertex?
					if (insideP) {
						result.add(p);
					}
					
					// advance p
					pi = pi + 1 == pn ? 0 : pi + 1;
					p0.set(p);
					p = tx1.getTransformed(p1.vertices[pi]);
					pv.set(p).subtract(p0);
				}
			} else {
				// q is pointing inside p1
				
				// is q inside p1?
				double ploc = Segment.getLocation(q, p0, p);
				if (ploc >= 0) {
					// should we add the current p vertex?
					if (insideP) {
						result.add(p);
					}
					
					// advance p
					pi = pi + 1 == pn ? 0 : pi + 1;
					p0.set(p);
					p = tx1.getTransformed(p1.vertices[pi]);
					pv.set(p).subtract(p0);
				} else {
					// should we add the current q vertex?
					if (insideQ) {
						result.add(q);
					}
					
					// advance q
					qi = qi + 1 == qn ? 0 : qi + 1;
					q0.set(q);
					q = tx2.getTransformed(p2.vertices[qi]);
					qv.set(q).subtract(q0);
				}
			}
		}
		
		// if we make it here, 3 possibilities exist
		// 1: P1 & P2 do not overlap at all
		// 2: P1 is fully contained in P2
		// 3: P2 is fully contained in P1
		tx1.getTransformed(p1.vertices[0], p);
		tx2.getTransformed(p2.vertices[0], q);
		
		// we know if P1 is fully contained in P2 if ANY of p1's vertices are contained in P2's bounds
		// because we already determined that there were no intersections - there can only be zero intersections
		// when they don't overlap at all or when one is contained in the other.
		if (p2.contains(p, tx2, false)) {
			return p1;
		} else if (p1.contains(q, tx1, false)) {
			return p2;
		} else {
			return null;
		}
	}
}
