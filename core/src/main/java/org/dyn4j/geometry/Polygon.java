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
 * @version 3.2.0
 * @since 1.0.0
 */
public class Polygon extends AbstractShape implements Convex, Wound, Shape, Transformable, DataContainer {
	/** The polygon vertices */
	final Vector2[] vertices;
	
	/** The polygon normals */
	final Vector2[] normals;
	
	/**
	 * Full constructor for sub classes.
	 * @param center the center
	 * @param radius the rotation radius
	 * @param vertices the vertices
	 * @param normals the normals
	 */
	Polygon(Vector2 center, double radius, Vector2[] vertices, Vector2[] normals) {
		super(center, radius);
		this.vertices = vertices;
		this.normals = normals;
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
		super(center, Geometry.getRotationRadius(center, vertices));
			
		// set the vertices
		this.vertices = vertices;
		// create the normals
		this.normals = Geometry.getCounterClockwiseEdgeNormals(vertices);
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
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		super.rotate(theta, x, y);
		int size = this.vertices.length;
		for (int i = 0; i < size; i++) {
			this.vertices[i].rotate(theta, x, y);
			this.normals[i].rotate(theta);
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
		Vector2 maximum = new Vector2();
		double max = -Double.MAX_VALUE;
		int index = 0;
		// find the vertex on the polygon that is further along on the penetration axis
		int count = this.vertices.length;
		for (int i = 0; i < count; i++) {
			// get the current vertex
			Vector2 v = this.vertices[i];
			// get the scalar projection of v onto axis
			double projection = localn.dot(v);
			// keep the maximum projection point
			if (projection > max) {
				// set the max point
				maximum.set(v);
				// set the new maximum
				max = projection;
				// save the index
				index = i;
			}
		}
		
		// once we have the point of maximum
		// see which edge is most perpendicular
		int l = index + 1 == count ? 0 : index + 1;
		int r = index - 1 < 0 ? count - 1 : index - 1;
		Vector2 leftN = this.normals[index == 0 ? count - 1 : index - 1];
		Vector2 rightN = this.normals[index];
		// create the maximum point for the feature (transform the maximum into world space)
		transform.transform(maximum);
		PointFeature vm = new PointFeature(maximum, index);
		// is the left or right edge more perpendicular?
		if (leftN.dot(localn) < rightN.dot(localn)) {
			Vector2 left = transform.getTransformed(this.vertices[l]);
			PointFeature vl = new PointFeature(left, l);
			// make sure the edge is the right winding
			return new EdgeFeature(vm, vl, vm, maximum.to(left), index + 1);
		} else {
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
		Vector2 point = new Vector2();
		// set the farthest point to the first one
		point.set(this.vertices[0]);
		// prime the projection amount
		double max = localn.dot(this.vertices[0]);
		// loop through the rest of the vertices to find a further point along the axis
		int size = this.vertices.length;
		for (int i = 1; i < size; i++) {
			// get the current vertex
			Vector2 v = this.vertices[i];
			// project the vertex onto the axis
			double projection = localn.dot(v);
			// check to see if the projection is greater than the last
			if (projection > max) {
				// otherwise this point is the farthest so far so clear the array and add it
				point.set(v);
				// set the new maximum
				max = projection;
			}
		}
		// transform the point into world space
		transform.transform(point);
		return point;
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
	@Override
	public AABB createAABB(Transform transform) {
		double vx = 0.0;
		double vy = 0.0;
    	// get the first point
		Vector2 p = transform.getTransformed(this.vertices[0]);
		// project the point onto the vector
    	double minX = Vector2.X_AXIS.dot(p);
    	double maxX = minX;
    	double minY = Vector2.Y_AXIS.dot(p);
    	double maxY = minY;
    	// loop over the rest of the vertices
    	int size = this.vertices.length;
        for(int i = 1; i < size; i++) {
    		// get the next point
    		p = transform.getTransformed(this.vertices[i]);
    		// project it onto the vector
            vx = Vector2.X_AXIS.dot(p);
            vy = Vector2.Y_AXIS.dot(p);
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
