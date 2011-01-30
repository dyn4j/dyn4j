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

import org.dyn4j.game2d.Epsilon;

/**
 * Represents a line {@link Segment}.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Segment extends Wound implements Convex, Shape, Transformable {
	/** The segment {@link Shape.Type} */
	public static final Shape.Type TYPE = new Shape.Type("Segment");
	
	/** The segment length */
	protected double length;
	
	/**
	 * Full constructor.
	 * @param point1 the first point
	 * @param point2 the second point
	 * @throws NullPointerException if point1 or point2 is null
	 * @throws IllegalArgumentException if point1 == point2
	 */
	public Segment(Vector2 point1, Vector2 point2) {
		super();
		// make sure either point is not null
		if (point1 == null || point2 == null) throw new NullPointerException("Both point1 and point2 cannot be null.");
		// make sure the two points are not coincident
		if (point1.equals(point2)) {
			throw new IllegalArgumentException("A line segment must have two different vertices.");
		}
		// assign the verices
		this.vertices = new Vector2[2];
		this.vertices[0] = point1;
		this.vertices[1] = point2;
		// create the normals
		this.normals = new Vector2[2];
		this.normals[0] = point1.to(point2).right();
		this.normals[0].normalize();
		this.normals[1] = point1.to(point2).left();
		this.normals[1].normalize();
		// get the center
		this.center = Geometry.getAverageCenter(this.vertices);
		// compute the length
		this.length = point1.distance(point2);
		// compute the radius
		this.radius = this.length * 0.5;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#getType()
	 */
	@Override
	public Type getType() {
		return Segment.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SEGMENT[").append(super.toString()).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns point1 in local coordinates.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint1() {
		return this.vertices[0];
	}
	
	/**
	 * Returns point2 in local coordinates.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint2() {
		return this.vertices[1];
	}
	
	/**
	 * Returns the length of the line {@link Segment}.
	 * @return double
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Determines where the point is relative to the given line.
	 * <pre>
	 * Set L = linePoint2 - linePoint1
	 * Set P = point - linePoint1
	 * location = L.cross(P)
	 * </pre>
	 * Returns 0 if the point lies on the line created from the line segment.<br />
	 * Assuming a right handed coordinate system:<br />
	 * Returns < 0 if the point lies on the right side of the line<br />
	 * Returns > 0 if the point lies on the left side of the line
	 * <p>
	 * Assumes all points are in world space.
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return double
	 */
	public static double getLocation(Vector2 point, Vector2 linePoint1, Vector2 linePoint2) {
		return (linePoint2.x - linePoint1.x) * (point.y - linePoint1.y) -
			  (point.x - linePoint1.x) * (linePoint2.y - linePoint1.y);
	}

	/**
	 * Returns the point on the given line closest to the given point.
	 * <p>
	 * Project the point onto the line:
	 * <pre>
	 * V<sub>line</sub> = P<sub>1</sub> - P<sub>0</sub>
	 * V<sub>point</sub> = P<sub>0</sub> - P
	 * P<sub>closest</sub> = V<sub>point</sub>.project(V<sub>line</sub>)
	 * </pre>
	 * Assumes all points are in world space.
	 * @see Vector2#project(Vector2)
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return {@link Vector2}
	 */
	public static Vector2 getPointOnLineClosestToPoint(Vector2 point, Vector2 linePoint1, Vector2 linePoint2) {
		// create a vector from the point to the first line point
		Vector2 p1ToP = point.difference(linePoint1);
		// create a vector representing the line
	    Vector2 line = linePoint2.difference(linePoint1);
	    // get the length squared of the line
	    double ab2 = line.dot(line);
	    // check ab2 for zero (linePoint1 == linePoint2)
	    if (ab2 < Epsilon.E) return linePoint1.copy();
	    // get the projection of AP on AB
	    double ap_ab = p1ToP.dot(line);
	    // get the position from the first line point to the projection
	    double t = ap_ab / ab2;
	    // create the point on the line
	    return line.multiply(t).add(linePoint1);
	}
	
	/**
	 * Returns the point on this line segment closest to the given point.
	 * <p>
	 * If the point closest to the given point on the line created by this
	 * line segment is not on the line segment then either of the segments
	 * end points will be returned.
	 * <p>
	 * Assumes all points are in world space.
	 * @see Segment#getPointOnLineClosestToPoint(Vector2, Vector2, Vector2)
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return {@link Vector2}
	 */
	public static Vector2 getPointOnSegmentClosestToPoint(Vector2 point, Vector2 linePoint1, Vector2 linePoint2) {
		// create a vector from the point to the first line point
		Vector2 p1ToP = point.difference(linePoint1);
		// create a vector representing the line
	    Vector2 line = linePoint2.difference(linePoint1);
	    // get the length squared of the line
	    double ab2 = line.dot(line);
	    // get the projection of AP on AB
	    double ap_ab = p1ToP.dot(line);
	    // check ab2 for zero (linePoint1 == linePoint2)
	    if (ab2 < Epsilon.E) return linePoint1.copy();
	    // get the position from the first line point to the projection
	    double t = ap_ab / ab2;
	    // make sure t is in between 0.0 and 1.0
	    t = Interval.clamp(t, 0.0, 1.0);
	    // create the point on the line
	    return line.multiply(t).add(linePoint1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getAxes(java.util.List, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// get the number of foci
		int size = foci != null ? foci.length : 0;
		// create an array to hold the axes
		Vector2[] axes = new Vector2[2 + size];
		int n = 0;
		// get the vertices
		Vector2 p1 = transform.getTransformed(this.vertices[0]);
		Vector2 p2 = transform.getTransformed(this.vertices[1]);
		// use both the edge and its normal
		axes[n++] = transform.getTransformedR(this.normals[1]);
		axes[n++] = transform.getTransformedR(this.normals[0].getLeftHandOrthogonalVector());
		Vector2 axis;
		// add the voronoi region axes if point is supplied
		for (int i = 0; i < size; i++) {
			// get the focal point
			Vector2 f = foci[i];
			// find the closest point
			if (p1.distanceSquared(f) < p2.distanceSquared(f)) {
				axis = p1.to(f);
			} else {
				axis = p2.to(f);
			}
			// normalize the axis
			axis.normalize();
			// add the axis to the array
			axes[n++] = axis;
		}
		// return all the axes
		return axes;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFoci(org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#contains(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		// put the point in local coordinates
		Vector2 p = transform.getInverseTransformed(point);
		// create a reference to the end points
		Vector2 p1 = this.vertices[0];
		Vector2 p2 = this.vertices[1];
		// get the location of the given point relative to this segment
		double value = Segment.getLocation(p, p1, p2);
		// see if the point is on the line created by this line segment
		if (Math.abs(value) < Epsilon.E) {
			double distSqrd = p1.distanceSquared(p2);
			if (p.distanceSquared(p1) <= distSqrd
			 && p.distanceSquared(p2) <= distSqrd) {
				// if the distance to the point from both points is less than or equal
				// to the length of the segment squared then we know its on the line segment
				return true;
			}
			// if the point is further away from either point than the length of the
			// segment then its not on the segment
			return false;
		}
		return false;
	}
	
	/**
	 * Returns true if the given point is inside this {@link Shape}.
	 * <p>
	 * If the given point lies on an edge the point is considered
	 * to be inside the {@link Shape}.
	 * <p>
	 * The given point is assumed to be in world space.
	 * <p>
	 * If the radius is greater than zero then the point is tested to be
	 * within the shape expanded radially by the radius.
	 * @param point world space point
	 * @param transform {@link Transform} for this {@link Shape}
	 * @param radius the expansion radius; in the range [0, &infin;]
	 * @return boolean
	 */
	public boolean contains(Vector2 point, Transform transform, double radius) {
		// if the radius is zero or less then perform the normal procedure
		if (radius <= 0) {
			return contains(point, transform);
		} else {
			// put the point in local coordinates
			Vector2 p = transform.getInverseTransformed(point);
			// otherwise act like the segment is two circles and a rectangle
			if (this.vertices[0].distanceSquared(p) <= radius * radius) {
				return true;
			} else if (this.vertices[1].distanceSquared(p) <= radius * radius) {
				return true;
			} else {
				// see if the point is in the rectangle portion
				Vector2 l = this.vertices[0].to(this.vertices[1]);
				Vector2 p1 = this.vertices[0].to(p);
				Vector2 p2 = this.vertices[1].to(p);
				if (l.dot(p1) > 0 && -l.dot(p2) > 0) {
					double dist = p1.project(l.getRightHandOrthogonalVector()).getMagnitudeSquared();
					if (dist <= radius * radius) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#project(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 n, Transform transform) {
		double v = 0.0;
		// get the vertices
		Vector2 p1 = transform.getTransformed(this.vertices[0]);
		Vector2 p2 = transform.getTransformed(this.vertices[1]);
		// project the first
    	double min = n.dot(p1);
    	double max = min;
    	// project the second
        v = n.dot(p2);
        if (v < min) { 
            min = v;
        } else if (v > max) { 
            max = v;
        }

        return new Interval(min, max);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFurthestPoint(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 n, Transform transform) {
		// get the vertices and the center
		Vector2 p1 = transform.getTransformed(this.vertices[0]);
		Vector2 p2 = transform.getTransformed(this.vertices[1]);
		Vector2 c = transform.getTransformed(this.center);
		// create vectors from the center to each vertex
		Vector2 v1 = c.to(p1);
		Vector2 v2 = c.to(p2);
		// project them onto the vector
		double dot1 = n.dot(v1);
		double dot2 = n.dot(v2);
		// find the greatest projection
		if (dot1 >= dot2) {
			return p1;
		} else {
			return p2;
		}
	}
	
	/**
	 * Returns the feature farthest in the direction of n.
	 * <p>
	 * For a {@link Segment} it's always the {@link Segment} itself.
	 * @param n the direction
	 * @param transform the local to world space {@link Transform} of this {@link Convex} {@link Shape}
	 * @return {@link Edge}
	 */
	@Override
	public Edge getFarthestFeature(Vector2 n, Transform transform) {
		// the farthest feature for a line is always the line itself
		Vector2 max = null;
		// get the vertices and the center
		Vector2 p1 = transform.getTransformed(this.vertices[0]);
		Vector2 p2 = transform.getTransformed(this.vertices[1]);
		Vector2 c = transform.getTransformed(this.center);
		// create vectors from the center to each vertex
		Vector2 v1 = c.to(p1);
		Vector2 v2 = c.to(p2);
		// project them onto the vector
		double dot1 = n.dot(v1);
		double dot2 = n.dot(v2);
		// find the greatest projection
		int index = 0;
		if (dot1 >= dot2) {
			max = p1;
			index = 0;
		} else {
			max = p2;
			index = 1;
		}
		// return the points of the segment in the
		// opposite direction as the other shape
		Vertex vp1 = new Vertex(p1, 0);
		Vertex vp2 = new Vertex(p2, 1);
		Vertex vm = new Vertex(max, index);
		// make sure the edge is the right winding
		if (p1.to(p2).right().dot(n) > 0) {
			return new Edge(vp2, vp1, vm, p2.to(p1), 0);
		} else {
			return new Edge(vp1, vp2, vm, p1.to(p2), 0);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.AbstractShape#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		super.rotate(theta, x, y);
		this.vertices[0].rotate(theta, x, y);
		this.vertices[1].rotate(theta, x, y);
		this.normals[0].rotate(theta, x, y);
		this.normals[1].rotate(theta, x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.AbstractShape#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		super.translate(x, y);
		this.vertices[0].add(x, y);
		this.vertices[1].add(x, y);
	}
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Segment} and the given density.
	 * <pre>
	 * m = d * length
	 * I = l<sup>2</sup> * m / 12
	 * </pre>
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Segment}
	 */
	@Override
	public Mass createMass(double density) {
		double length = this.length;
		// compute the mass
		double mass = density * length;
		// compute the inertia tensor
		double inertia = 1.0 / 12.0 * length * length * mass;
		// since we know that a line segment has only two points we can
		// feel safe using the averaging method for the centroid
		return new Mass(this.center, mass, inertia);
	}
}
