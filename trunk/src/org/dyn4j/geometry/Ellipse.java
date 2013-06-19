/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.resources.Messages;

/**
 * Represents an ellipse shape.
 * <p>
 * An ellipse must have a width and height greater than zero.
 * <p>
 * This shape is only supported by the GJK collision detection algorithm. It may even be more efficient
 * and stable using {@link Geometry#createPolygonalEllipse(int, double, double)} instead.
 * @author William Bittle
 * @since 3.1.5
 * @version 3.1.5
 */
public class Ellipse extends AbstractShape implements Convex, Shape, Transformable {
	/** The ellipse width */
	protected double width;
	
	/** The ellipse height */
	protected double height;
	
	/** The half-width */
	protected double a;
	
	/** The half-height */
	protected double b;
	
	/** A local vector to  */
	protected Vector2 localXAxis;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * This creates an axis-aligned ellipse fitting inside a rectangle
	 * of the given width and height.
	 * @param width the width
	 * @param height the height
	 * @throws IllegalArgumentException if either the width or height is less than or equal to zero
	 */
	public Ellipse(double width, double height) {
		// validate the width and height
		if (width <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.ellipse.invalidWidth"));
		if (height <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.ellipse.invalidHeight"));
		
		this.center = new Vector2();
		
		this.width = width;
		this.height = height;
		
		// compute the major and minor axis lengths
		// (the x,y radii)
		this.a = width * 0.5;
		this.b = height * 0.5;
		
		// the rotation radius of the entire shape is the maximum
		// of the radii
		this.radius = Math.max(this.a, this.b);
		
		// since we create ellipses as axis aligned we set the local x axis
		// to the world space x axis
		this.localXAxis = new Vector2(1.0, 0.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ellipse[").append(super.toString())
		.append("|Width=").append(this.width)
		.append("|Height=").append(this.height)
		.append("|UserData=").append(this.userData)
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getAxes(org.dyn4j.geometry.Vector2[], org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// this shape is not supported by SAT
		throw new UnsupportedOperationException(Messages.getString("geometry.ellipse.satNotSupported"));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFoci(org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		// this shape is not supported by SAT
		throw new UnsupportedOperationException(Messages.getString("geometry.ellipse.satNotSupported"));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 n, Transform transform) {
		// convert the world space vector(n) to local space
		Vector2 localAxis = transform.getInverseTransformedR(n);
		// include local rotation
		double r = this.getRotation();
		// invert the local rotation
		localAxis.rotate(-r);
		// an ellipse is a circle with a non-uniform scaling transformation applied
		// so we can achieve that by scaling the input axis by the major and minor
		// axis lengths
		localAxis.x *= this.a;
		localAxis.y *= this.b;
		// then normalize it
		localAxis.normalize();
		// add the radius along the vector to the center to get the farthest point
		Vector2 p = new Vector2(localAxis.x * this.a, localAxis.y  * this.b);
		// include local rotation
		// invert the local rotation
		p.rotate(r);
		p.add(this.center);
		// then finally convert back into world space coordinates
		transform.transform(p);
		return p;
	}
	
	/**
	 * Returns the point on the ellipse closest to the given point.
	 * @param point the world space point
	 * @param transform the transform
	 * @return {@link Vector2} in world space
	 */
	public Vector2 getPointClosestToPoint(Vector2 point, Transform transform) {
		// convert the world space vector(n) to local space
		Vector2 localPoint = transform.getInverseTransformed(point);
		// include local rotation
		double r = this.getRotation();
		// invert the local rotation
		localPoint.rotate(-r);
		// an ellipse is a circle with a non-uniform scaling transformation applied
		// so we can achieve that by scaling the vector from this center to the
		// point by the major and minor axis lengths to get the point on the ellipse
		// closest to the given point
		
		Vector2 v = this.center.to(localPoint);
		v.normalize();
		
		v.x *= this.a;
		v.y *= this.b;
		
		// include local rotation
		// invert the local rotation
		v.rotate(r);
		v.add(this.center);
		// then finally convert back into world space coordinates
		transform.transform(v);
		return v;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Feature getFarthestFeature(Vector2 n, Transform transform) {
		// obtain the farthest point along the given vector
		Vector2 farthest = this.getFarthestPoint(n, transform);
		// for an ellipse the farthest feature along a vector will always be a vertex
		return new Vertex(farthest);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#project(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 n, Transform transform) {
		// get the world space farthest point
		Vector2 p1 = this.getFarthestPoint(n, transform);
		// get the center in world space
		Vector2 center = transform.getTransformed(this.center);
		// project the center onto the axis
		double c = center.dot(n);
		// project the point onto the axis
		double d = p1.dot(n);
		// get the interval along the axis
		return new Interval(2 * c - d, d);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createAABB(org.dyn4j.geometry.Transform)
	 */
	@Override
	public AABB createAABB(Transform transform) {
		Interval x = this.project(Vector2.X_AXIS, transform);
		Interval y = this.project(Vector2.Y_AXIS, transform);
		
		return new AABB(x.getMin(), y.getMin(), x.getMax(), y.getMax());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createMass(double)
	 */
	@Override
	public Mass createMass(double density) {
		double area = Math.PI * this.a * this.b;
		double m = area * density;
		// inertia about the z see http://math.stackexchange.com/questions/152277/moment-of-inertia-of-an-ellipse-in-2d
		double I = m * (this.a * this.a + this.b * this.b) / 4.0;
		return new Mass(this.center, m, I);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		return this.radius + center.distance(this.center);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#contains(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		// equation of an ellipse:
		// (x - h)^2/a^2 + (y - k)^2/b^2 = 1
		// for a point to be inside the ellipse, we can plug in
		// the point into this equation and verify that the value
		// is less than or equal to one
		
		// get the world space point into local coordinates
		Vector2 localPoint = transform.getInverseTransformed(point);
		// account for local rotation
		double r = this.getRotation();
		localPoint.rotate(-r);
		
		double x = (localPoint.x - this.center.x);
		double y = (localPoint.y - this.center.y);
		double x2 = x * x;
		double y2 = y * y;
		double a2 = this.a * this.a;
		double b2 = this.b * this.b;
		double value = x2 / a2 + y2 / b2;
		
		if (value <= 1.0) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		super.rotate(theta, x, y);
		// rotate the local axis as well
		this.localXAxis.rotate(theta);
	}
	
	/**
	 * Returns the rotation about the local center in radians.
	 * @return double the rotation in radians
	 */
	public double getRotation() {
		return Vector2.X_AXIS.getAngleBetween(this.localXAxis);
	}
	
	/**
	 * Returns the width.
	 * @return double
	 */
	public double getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height.
	 * @return double
	 */
	public double getHeight() {
		return this.height;
	}
	
	/**
	 * Returns the half width.
	 * @return double
	 */
	public double getHalfWidth() {
		return this.a;
	}

	/**
	 * Returns the half height.
	 * @return double
	 */
	public double getHalfHeight() {
		return this.a;
	}
}
