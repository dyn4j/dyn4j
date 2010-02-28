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

/**
 * Represents a {@link Convex} {@link Polygon}.
 * <p>
 * A {@link Polygon} must have at least 3 vertices where one of which is not colinear with the other two
 * simultaneously.  A {@link Polygon} must also be {@link Convex} and have CCW winding of points.
 * <p>
 * A polygon must also not have coincident vertices.
 * @author William Bittle
 */
public class Polygon extends Wound implements Convex, Shape, Transformable {
	/**
	 * Default constructor for sub classes.
	 */
	protected Polygon() {
		super();
	}
	
	/**
	 * Full constructor.
	 * @param vertices the array of vertices
	 */
	public Polygon(Vector[] vertices) {
		super();
		// check the vertex array
		if (vertices == null || vertices.length < 3) throw new IllegalArgumentException("A polygon must have 3 or more vertices.");
		// check for convex
		int size = vertices.length;
		double area = 0.0;
		for (int i = 0; i < size; i++) {
			Vector p0 = (i - 1 < 0) ? vertices[size - 1] : vertices[i - 1];
			Vector p1 = vertices[i];
			Vector p2 = (i + 1 == size) ? vertices[0] : vertices[i + 1];
			// check the cross product for CCW winding
			area += p1.cross(p2);
			// check for coincident vertices
			if (p1.equals(p2)) {
				throw new IllegalArgumentException("A polygon must not have any coincident vertices.");
			}
			// check for convexity
			if (p0.to(p1).cross(p1.to(p2)) < 0) {
				throw new IllegalArgumentException("A polygon must be convex.");
			}
		}
		// check for CCW
		if (area < 0) {
			throw new IllegalArgumentException("A polygon must have Counter-Clockwise vertex winding.");
		}
		this.vertices = vertices;
		this.center = Geometry.getAreaWeightedCenter(this.vertices);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("POLYGON[").append(super.toString()).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getAxes(java.util.List, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector[] getAxes(Vector[] foci, Transform transform) {
		// get the size of the foci list
		int fociSize = foci != null ? foci.length : 0;
		// get the number of vertices this polygon has
		int size = this.vertices.length;
		// the axes of a polygon are created from the normal of the edges
		// plus the closest point to each focus
		Vector[] axes = new Vector[size + fociSize];
		int n = 0;
		// loop over the vertices to get the edge normals
		for (int i = 0; i < size; i++) {
			// create references to the current points
			Vector p1 = this.vertices[i];
			Vector p2 = this.vertices[(i + 1) == size ? 0 : i + 1];
			// transform the points into world space
			p1 = transform.getTransformed(p1);
			p2 = transform.getTransformed(p2);
			// get the edge
			Vector v = p1.to(p2);
			// get the edge normal
			v.left();
			// add it to the list
			axes[n++] = v;
		}
		// loop over the focal points and find the closest
		// points on the polygon to the focal points
		for (int i = 0; i < fociSize; i++) {
			// get the current focus
			Vector f = foci[i];
			// create a place for the closest point
			Vector closest = null;
			double d = Double.MAX_VALUE;
			// find the minimum distance vertex
			for (int j = 0; j < size; j++) {
				// get the vertex
				Vector p = this.vertices[j];
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
			axes[n++] = f.to(closest);
		}
		// return all the axes
		return axes;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFoci(org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector[] getFoci(Transform transform) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#contains(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector point, Transform transform) {
		// if the polygon is convex then do a simple inside test
		// if the the sign of the location of the point on the side of an edge (or line)
		// is always the same and the polygon is convex then we know that the
		// point lies inside the polygon
		// This method doesn't care about vertex winding
		// inverse transform the point to put it in local coordinates
		Vector p = transform.getInverseTransformed(point);
		Vector p1 = this.vertices[0];
		Vector p2 = this.vertices[1];
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
	 * @see org.dyn4j.game2d.geometry.AbstractShape#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		super.rotate(theta, x, y);
		int size = this.vertices.length;
		for (int i = 0; i < size; i++) {
			this.vertices[i].rotate(theta, x, y);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.AbstractShape#translate(double, double)
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
	 * @see org.dyn4j.game2d.geometry.Shape#project(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Interval project(Vector n, Transform transform) {
		double v = 0.0;
    	// get the first point
		Vector p = transform.getTransformed(this.vertices[0]);
		// project the point onto the vector
    	double min = n.dot(p);
    	double max = min;
    	// loop over the rest of the vertices
    	int size = this.vertices.length;
        for(int i = 1; i < size; i++) {
    		// get the next point
    		p = transform.getTransformed(this.vertices[i]);
    		// project it onto the vector
            v = n.dot(p);
            if (v < min) { 
                min = v;
            } else if (v > max) { 
                max = v;
            }
        }
        return new Interval(min, max);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFarthestFeature(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Feature getFarthestFeature(Vector n, Transform transform) {
		double max = -Double.MAX_VALUE;
		int index = 0;
		Feature feature = new Feature();
		feature.type = Feature.Type.EDGE;
		feature.edge = new Vector[2];
		feature.max = new Vector();
		// create a reference to the center
		Vector c = transform.getTransformed(this.center);
		// find the vertex on the polygon that is further along on the penetration axis
		int count = this.vertices.length;
		Vector temp = new Vector();
		for (int i = 0; i < count; i++) {
			// get the vertex
			transform.getTransformed(this.vertices[i], temp);
			// create a vector from the center to the point
			Vector v = c.to(temp);
			// get the scalar projection of v onto axis
			double projection = n.dot(v);
			// keep the maximum projection point
			if (projection > max) {
				// set the max point
				feature.max.set(temp);
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
		Vector left = transform.getTransformed(this.vertices[l]);
		Vector right = transform.getTransformed(this.vertices[r]);
		// is the left or right edge more perpendicular?
		if (left.to(feature.max).dot(n) < right.to(feature.max).dot(n)) {
			feature.edge[0] = feature.max;
			feature.edge[1] = left;
			feature.index = index;
		} else {
			feature.edge[0] = right;
			feature.edge[1] = feature.max;
			feature.index = index - 1;
		}
		// return the feature
		return feature;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFarthestPoint(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector getFarthestPoint(Vector n, Transform transform) {
		Vector temp = new Vector();
		Vector point = new Vector();
		// get the transformed center
		Vector c = transform.getTransformed(this.center);
		// set the farthest point to the first one
		transform.getTransformed(this.vertices[0], temp);
		point.set(temp);
		// set the projection amount
		double max = n.dot(c.to(point));
		// loop through the rest of the vertices to find a further point along the axis
		int size = this.vertices.length;
		for (int i = 1; i < size; i++) {
			// get the next vertex
			transform.getTransformed(this.vertices[i], temp);
			// create a vector from the center to the vertex
			Vector v = c.to(temp);
			// project the vector onto the axis
			double projection = n.dot(v);
			// check to see if the projection is greater than the last
			if (projection > max) {
				// otherwise this point is the farthest so far so clear the array and add it
				point.set(temp);
				// set the new maximum
				max = projection;
			}
		}
		return point;
	}
}
