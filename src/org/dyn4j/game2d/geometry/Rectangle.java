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

/**
 * Represents a {@link Rectangle} (either axis aligned or oriented).
 * <p>
 * A {@link Rectangle} cannot have a width or height of zero.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Rectangle extends Polygon implements Shape, Transformable {
	/** The rectangle {@link Shape.Type} */
	public static final Shape.Type TYPE = new Shape.Type(Polygon.TYPE, "Rectangle");
	
	/** The {@link Rectangle}'s width */
	protected double width;
	
	/** The {@link Rectangle}'s height */
	protected double height;

	/**
	 * Full constructor.
	 * <p>
	 * The center of the {@link Rectangle} is (0, 0).
	 * @param width the width
	 * @param height the height
	 * @throws IllegalArgumentException if width or height is less than or equal to zero
	 */
	public Rectangle(double width, double height) {
		if (width <= 0.0) throw new IllegalArgumentException("A rectangle must have a positive non-zero width.");
		if (height <= 0.0) throw new IllegalArgumentException("A rectangle must have a positive non-zero height.");
		// set the vertices
		this.vertices = new Vector2[] {
			new Vector2(-width * 0.5, -height * 0.5),
			new Vector2( width * 0.5, -height * 0.5),
			new Vector2( width * 0.5,  height * 0.5),
			new Vector2(-width * 0.5,  height * 0.5)	
		};
		// set the normals
		this.normals = new Vector2[] {
			new Vector2(0.0, -1.0),
			new Vector2(1.0, 0.0),
			new Vector2(0.0, 1.0),
			new Vector2(-1.0, 0.0)
		};
		// use the average method for the centroid
		this.center = Geometry.getAverageCenter(this.vertices);
		// compute the max radius
		this.radius = this.center.distance(this.vertices[0]);
		// set the width and height
		this.width = width;
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Polygon#getType()
	 */
	@Override
	public Type getType() {
		return Rectangle.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RECTANGLE[").append(super.toString()).append("|")
		.append(width).append("|").append(height).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the height.
	 * @return double
	 */
	public double getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the width.
	 * @return double
	 */
	public double getWidth() {
		return this.width;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Polygon#getAxes(java.util.List, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// get the number of foci
		int fociSize = foci != null ? foci.length : 0;
		// create an array to hold the axes
		Vector2[] axes = new Vector2[2 + fociSize];
		int n = 0;
		// return the normals to the surfaces, since this is a 
		// rectangle we only have two axes to test against
		axes[n++] = transform.getTransformedR(this.normals[1]);
		axes[n++] = transform.getTransformedR(this.normals[2]);
		// get the closest point to each focus
		for (int i = 0; i < fociSize; i++) {
			// get the current focus
			Vector2 focus = foci[i];
			// create a place for the closest point
			Vector2 closest = null;
			double d = Double.MAX_VALUE;
			// find the minimum distance vertex
			for (int j = 0; j < 4; j++) {
				// get the vertex
				Vector2 vertex = this.vertices[j];
				// transform it into world space
				vertex = transform.getTransformed(vertex);
				// get the squared distance to the focus
				double dt = focus.distanceSquared(vertex);
				// compare with the last distance
				if (dt < d) {
					// if its closer then save it
					closest = vertex;
					d = dt;
				}
			}
			// once we have found the closest point create 
			// a vector from the focal point to the point
			Vector2 axis = focus.to(closest);
			// normalize the axis
			axis.normalize();
			// add it to the array
			axes[n++] = axis;
		}
		// return all the axes
		return axes;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Polygon#contains(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		// put the point in local coordinates
		Vector2 p = transform.getInverseTransformed(point);
		// get the center and vertices
		Vector2 c = this.center;
		Vector2 p1 = this.vertices[0];
		Vector2 p2 = this.vertices[1];
		Vector2 p4 = this.vertices[3];
		// get the width and height squared
		double widthSquared = p1.distanceSquared(p2);
		double heightSquared = p1.distanceSquared(p4);
		// i could call the polygon one instead of this method, but im not sure which is faster
		Vector2 projectAxis0 = p1.to(p2);
		Vector2 projectAxis1 = p1.to(p4);
		// create a vector from the centroid to the point
		Vector2 toPoint = c.to(p);
		// find the projection of this vector onto the vector from the
		// centroid to the edge
		if (toPoint.project(projectAxis0).getMagnitudeSquared() <= (widthSquared * 0.25)) {
			// if the projection of the v vector onto the x separating axis is
			// smaller than the half width then we know that the point is within the
			// x bounds of the rectangle
			if (toPoint.project(projectAxis1).getMagnitudeSquared() <= (heightSquared * 0.25)) {
				// if the projection of the v vector onto the y separating axis is 
				// smaller than the half height then we know that the point is within
				// the y bounds of the rectangle
				return true;
			}
		}
		// return null if they do not intersect
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Polygon#project(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 axis, Transform transform) {
		// get the center and vertices
		Vector2 center = transform.getTransformed(this.center);
		// create the project axes
		Vector2 projectAxis0 = transform.getTransformedR(this.normals[1]);
		Vector2 projectAxis1 = transform.getTransformedR(this.normals[2]);
		// project the shape on the axis
		double c = center.dot(axis);
		double e = (this.width * 0.5) * Math.abs(projectAxis0.dot(axis)) + (this.height * 0.5) * Math.abs(projectAxis1.dot(axis));
        return new Interval(c - e, c + e);
	}
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Rectangle} and the given density.
	 * <pre>
	 * m = d * h * w
	 * I = m * (h<sup>2</sup> + w<sup>2</sup>) / 12
	 * </pre>
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Rectangle}
	 */
	@Override
	public Mass createMass(double density) {
		double height = this.height;
		double width = this.width;
		// compute the mass
		double mass = density * height * width;
		// compute the inertia tensor
		double inertia = mass * (height * height + width * width) / 12.0;
		// since we know that a rectangle has only four points that are
		// evenly distributed we can feel safe using the averaging method 
		// for the centroid
		return new Mass(this.center, mass, inertia);
	}
}
