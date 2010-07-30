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
 * @version 1.0.3
 * @since 1.0.0
 */
public class Geometry {
	/** The value of 1/3 */
	private static final double INV_3 = 1.0 / 3.0;
	
	/**
	 * Returns the centroid of the given points by performing an average.
	 * @param points the list of points
	 * @return {@link Vector2} the centroid
	 */
	public static final Vector2 getAverageCenter(List<Vector2> points) {
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
			Vector2 point = points.get(i);
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
	 */
	public static final Vector2 getAverageCenter(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// check for a list of one point
		int size = points.length;
		if (size == 1) return points[0].copy();
		double x = 0;
		double y = 0;
		for (int i = 0; i < size; i++) {
			Vector2 point = points[i];
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
	 */
	public static final Vector2 getAreaWeightedCenter(List<Vector2> points) {
		// check for null list
		if (points == null) throw new NullPointerException("The points list cannot be null.");
		// check for empty list
		if (points.isEmpty()) throw new IllegalArgumentException("The points list must have at least one point.");
		// check for list of one point
		int size = points.size();
		if (size == 1) return points.get(0).copy();
		// otherwise perform the computation
		Vector2 center = new Vector2();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector2 p1 = points.get(i);
			Vector2 p2 = i + 1 < size ? points.get(i + 1) : points.get(0);
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
		center.multiply(1.0 / area);
		// return the center
		return center;
	}

	/**
	 * Returns the area weighted centroid for the given points.
	 * @see #getAreaWeightedCenter(List)
	 * @param points the {@link Polygon} points
	 * @return {@link Vector2} the area weighted centroid
	 */
	public static final Vector2 getAreaWeightedCenter(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException("The points array cannot be null.");
		// check for array of one point
		int size = points.length;
		if (size == 1) return points[0].copy();
		// otherwise perform the computation
		Vector2 center = new Vector2();
		double area = 0.0;
		// loop through the vertices
		for (int i = 0; i < size; i++) {
			// get two verticies
			Vector2 p1 = points[i];
			Vector2 p2 = i + 1 < size ? points[i + 1] : points[0];
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
		center.multiply(1.0 / area);
		// return the center
		return center;
	}
	
	/**
	 * Returns a new {@link Circle} with the given radius.
	 * @param radius the radius in meters
	 * @return {@link Circle}
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
	 */
	public static final Polygon createPolygon(Vector2[] vertices) {
		// check the vertices array
		if (vertices == null) throw new NullPointerException("The vertices array cannot be null.");
		// loop over the points an copy them
		int size = vertices.length;
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
	 */
	public static final Polygon createPolygonAtOrigin(Vector2[] vertices) {
		Polygon polygon = Geometry.createPolygon(vertices);
		Vector2 center = polygon.getCenter();
		polygon.translate(-center.x, -center.y);
		return polygon;
	}
	
	/**
	 * Returns a new {@link Polygon} object with vertexCount points, where the
	 * points are evenly distributed around the unit circle.
	 * <p>
	 * The radius parameter is the distance from the center of the polygon 
	 * (the origin) to each vertex.
	 * @see #createUnitCirclePolygon(int, double, double)
	 * @param count the number of vertices
	 * @param radius the radius from the center to each vertex in meters
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
	 * @param radius the radius from the center to each vertex in meters
	 * @param theta the vertex angle offset in radians
	 * @return {@link Polygon}
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
	 * Creates a new {@link Rectangle} with the given size.
	 * @param size the size in meters
	 * @return {@link Rectangle}
	 */
	public static final Rectangle createSquare(double size) {
		// check the size
		if (size <= 0.0) throw new IllegalArgumentException("The size must be greater than zero.");
		return new Rectangle(size, size);
	}
	
	/**
	 * Creates a new {@link Rectangle} with the given width and height.
	 * @param width the width in meters
	 * @param height the height in meters
	 * @return {@link Rectangle}
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
	 */
	public static final Triangle createTriangle(Vector2 p1, Vector2 p2, Vector2 p3) {
		if (p1 == null || p2 == null || p3 == null) throw new NullPointerException("A triangle cannot contain a null point.");
		return new Triangle(p1.copy(), p2.copy(), p3.copy());
	}
	
	/**
	 * Creates a new {@link Triangle} with the given points.
	 * <p>
	 * This method makes a copy of the given points to create the {@link Triangle}.
	 * <p>
	 * This method translates the {@link Triangle} points so that the center is at the origin.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param p3 the third point
	 * @return {@link Triangle}
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
	 */
	public static final Triangle createEquilateralTriangle(double height) {
		// check the size
		if (height <= 0.0) throw new IllegalArgumentException("The size must be greater than zero.");
		// compute a where height = a * sqrt(3) / 2.0 (a is the width of the base
		double a = 2.0 * height / Math.sqrt(3.0);
		// create the triangle
		return Geometry.createIsoscelesTriangle(a, height);
	}
	
	/**
	 * Creates an isosceles {@link Triangle} with the center at the origin.
	 * @param width the width of the base in meters
	 * @param height the height in meters
	 * @return {@link Triangle}
	 */
	public static final Triangle createIsoscelesTriangle(double width, double height) {
		// check the width
		if (width <= 0.0) throw new IllegalArgumentException("The width must be greater than zero.");
		// check the height
		if (height <= 0.0) throw new IllegalArgumentException("The width must be greater than zero.");
		Vector2 top = new Vector2(0.0, height);
		Vector2 left = new Vector2(-width / 2.0, 0.0);
		Vector2 right = new Vector2(width / 2.0, 0.0);
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
	 */
	public static final Segment createSegment(Vector2 end) {
		return Geometry.createSegment(new Vector2(), end);
	}
	
	/**
	 * Creates a new {@link Segment} with the given length with the center
	 * at the origin.
	 * <p>
	 * The segment created is a horizontal segment.
	 * @param length the length of the segment in meters
	 * @return {@link Segment}
	 */
	public static final Segment createSegment(double length) {
		// check the length
		if (length <= 0.0) throw new IllegalArgumentException("The length must be greater than zero.");
		Vector2 start = new Vector2(-length / 2.0, 0.0);
		Vector2 end = new Vector2(length / 2.0, 0.0);
		return new Segment(start, end);
	}
}
