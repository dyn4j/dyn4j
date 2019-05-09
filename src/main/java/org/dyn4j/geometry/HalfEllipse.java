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

import org.dyn4j.DataContainer;
import org.dyn4j.resources.Messages;

/**
 * Implementation of an Half-Ellipse {@link Convex} {@link Shape}.
 * <p>
 * A half ellipse must have a width and height greater than zero and the height parameter is the height of the half.
 * <p>
 * <b>This shape is only supported by the GJK collision detection algorithm</b>.
 * <p>
 * An <code>UnsupportedOperationException</code> is thrown when this shape is used with SAT.  If you are using
 * or are planning on using the SAT collision detection algorithm, you can use the 
 * {@link Geometry#createPolygonalHalfEllipse(int, double, double)} method to create a half ellipse
 * {@link Polygon} approximation. Another option is to use the GJK or your own collision detection
 * algorithm for this shape only and use SAT on others.
 * @author William Bittle
 * @version 3.3.1
 * @since 3.1.7
 */
public class HalfEllipse extends AbstractShape implements Convex, Shape, Transformable, DataContainer {
	/** 
	 * The half ellipse inertia constant. 
	 * @see <a href="http://www.efunda.com/math/areas/ellipticalhalf.cfm" target="_blank">Elliptical Half</a>
	 */
	private static final double INERTIA_CONSTANT = Math.PI / 8.0 - 8.0 / (9.0 * Math.PI);
	
	/** The ellipse height */
	final double height;
	
	/** The half-width */
	final double halfWidth;
	
	/** The local rotation */
	final Rotation rotation;
	
	/** The ellipse center */
	final Vector2 ellipseCenter;
	
	/** The first vertex of the bottom */
	final Vector2 vertexLeft;
	
	/** The second vertex of the bottom */
	final Vector2 vertexRight;
	
	/**
	 * Validated constructor.
	 * <p>
	 * This creates an axis-aligned half ellipse fitting inside a rectangle
	 * of the given width and height.
	 * @param valid always true or this constructor would not be called
	 * @param width the width
	 * @param center the center
	 * @param vertexLeft the first vertex
	 * @param vertexRight the second vertex
	 */
	private HalfEllipse(boolean valid, double width, double height, Vector2 center, Vector2 vertexLeft, Vector2 vertexRight) {
		super(center, center.distance(vertexRight));
		
		// set height. width can be computed as halfWidth * 2 when needed
		this.height = height;
		
		// compute the major and minor axis lengths
		// (the x,y radii)
		this.halfWidth = width * 0.5;

		// set the ellipse center
		this.ellipseCenter = new Vector2();
		
		// Initially the half ellipse is aligned to the world space x axis
		this.rotation = new Rotation();
		
		// setup the vertices
		this.vertexLeft = vertexLeft;
		this.vertexRight = vertexRight;
	}
	
	/**
	 * Minimal constructor.
	 * <p>
	 * This creates an axis-aligned half ellipse fitting inside a rectangle
	 * of the given width and height.
	 * @param width the width
	 * @param height the height of the half
	 * @throws IllegalArgumentException if either the width or height is less than or equal to zero
	 */
	public HalfEllipse(double width, double height) {
		this(
			validate(width, height), 
			width, 
			height, 
			new Vector2(0, (4.0 * height) / (3.0 * Math.PI)), 
			// the left point
			new Vector2(-width * 0.5, 0),
			// the right point
			new Vector2( width * 0.5, 0)
			);
	}
	
	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return boolean true
	 * @throws IllegalArgumentException if either the width or height is less than or equal to zero
	 */
	private static final boolean validate(double width, double height) {
		// validate the width and height
		if (width <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.halfEllipse.invalidWidth"));
		if (height <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.halfEllipse.invalidHeight"));
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HalfEllipse[").append(super.toString())
		.append("|Width=").append(this.getWidth())
		.append("|Height=").append(this.getHeight())
		.append("]");
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method is not supported by this shape.
	 * @throws UnsupportedOperationException when called
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// this shape is not supported by SAT
		throw new UnsupportedOperationException(Messages.getString("geometry.halfEllipse.satNotSupported"));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method is not supported by this shape.
	 * @throws UnsupportedOperationException when called
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		// this shape is not supported by SAT
		throw new UnsupportedOperationException(Messages.getString("geometry.halfEllipse.satNotSupported"));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 vector, Transform transform) {
		// convert the world space vector(n) to local space
		Vector2 localAxis = transform.getInverseTransformedR(vector);
		
		// private implementation
		localAxis = this.getFarthestPoint(localAxis);
		
		// then convert back into world space coordinates
		transform.transform(localAxis);
		
		return localAxis;
	}
	
	/**
	 * Modifies the given local space axis into the farthest point along that axis and
	 * additionally returns it.
	 * @param localAxis the direction vector in local space
	 * @return {@link Vector2}
	 */
	private Vector2 getFarthestPoint(Vector2 localAxis) {
		// localAxis is already in local coordinates
		
		// invert the local rotation
		localAxis.rotateInv(this.rotation);
		
		if (localAxis.y <= 0) {
			if (localAxis.x >= 0) {
				localAxis.set(this.vertexRight);
			} else {
				localAxis.set(this.vertexLeft);
			}
			
			return localAxis;
		}
		
		getFarthestPointOnAlignedEllipse(localAxis);
		
		// include local rotation (inverse again to restore the original rotation)
		localAxis.rotate(this.rotation);
		
		// add the radius along the vector to the center to get the farthest point
		localAxis.add(this.ellipseCenter);
		
		return localAxis;
	}
	
	/**
	 * Returns the farthest point along the given local space axis assuming the
	 * ellipse and the given axis are aligned.
	 * <p>
	 * Typically this means that the ellipse is axis-aligned, but it could also
	 * mean that the ellipse is not axis-aligned, but the given local space axis
	 * has been rotated to match the alignment of the ellipse.
	 */
	private void getFarthestPointOnAlignedEllipse(Vector2 localAxis) {
		// an ellipse is a circle with a non-uniform scaling transformation applied
		// so we can achieve that by scaling the input axis by the major and minor
		// axis lengths
		localAxis.x *= this.halfWidth;
		localAxis.y *= this.height;
		
		// then normalize it
		localAxis.normalize();
		
		// then scale again to get a point in the ellipse
		localAxis.x *= this.halfWidth;
		localAxis.y *= this.height;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Feature getFarthestFeature(Vector2 vector, Transform transform) {
		Vector2 localAxis = transform.getInverseTransformedR(vector);
		
		// invert the local rotation
		localAxis.rotateInv(this.rotation);
		
		if (localAxis.y > 0) {
			// then its the farthest point
			getFarthestPointOnAlignedEllipse(localAxis);
			
			// include local rotation (inverse again to restore the original rotation)
			localAxis.rotate(this.rotation);
			// add the radius along the vector to the center to get the farthest point
			localAxis.add(this.ellipseCenter);
			
			transform.transform(localAxis);
			
			return new PointFeature(localAxis);
		} else {
			// Below code is equivalent to
			// return Segment.getFarthestFeature(this.vertexLeft, this.vertexRight, vector, transform);
			
			// Transform the vertices to world space
			Vector2 p1 = transform.getTransformed(this.vertexLeft);
			Vector2 p2 = transform.getTransformed(this.vertexRight);
			
			// The vector p1->p2 is always CCW winding
			PointFeature vp1 = new PointFeature(p1, 0);
			PointFeature vp2 = new PointFeature(p2, 1);
			
			// Choose the vertex that maximizes v.dot(vector)
			// localAxis is vector in local space and we can choose the correct vertex by
			// checking if localAxis points to the left or right
			PointFeature vmax = (localAxis.x <= 0)? vp1 : vp2;
			
			return new EdgeFeature(vp1, vp2, vmax, p1.to(p2), 0);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#project(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 vector, Transform transform) {
		// get the world space farthest point
		Vector2 p1 = this.getFarthestPoint(vector, transform);
		Vector2 p2 = this.getFarthestPoint(vector.getNegative(), transform);
		// project the point onto the axis
		double d1 = p1.dot(vector);
		double d2 = p2.dot(vector);
		// get the interval along the axis
		return new Interval(d2, d1);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createAABB(org.dyn4j.geometry.Transform)
	 */
	@Override
	public AABB createAABB(Transform transform) {
		// Inlined projection of x axis
		
		// Equivalent of temp = transform.getInverseTransformedR(Vector2.X_AXIS)
		Vector2 temp = new Vector2(transform.cost, -transform.sint);
		// Equivalent of maxX = this.project(Vector2.X_AXIS, transform).getMax();
		double maxX = transform.getTransformedX(this.getFarthestPoint(temp));
		
		// Equivalent of temp = transform.getInverseTransformedR(Vector2.INV_X_AXIS)
		temp.set(-transform.cost, transform.sint);
		// Equivalent of minX = this.project(Vector2.X_AXIS, transform).getMin();
		double minX = transform.getTransformedX(this.getFarthestPoint(temp));
		
		// Inlined projection of y axis
		
		// Equivalent of temp = transform.getInverseTransformedR(Vector2.Y_AXIS)
		temp.set(transform.sint, transform.cost);
		// Equivalent of maxY = this.project(Vector2.Y_AXIS, transform).getMax();
		double maxY = transform.getTransformedY(this.getFarthestPoint(temp));
		
		// Equivalent of temp = transform.getInverseTransformedR(Vector2.INV_Y_AXIS)
		temp.set(-transform.sint, -transform.cost);
		// Equivalent of minY = this.project(Vector2.Y_AXIS, transform).getMin();
		double minY = transform.getTransformedY(this.getFarthestPoint(temp));
		
		return new AABB(minX, minY, maxX, maxY);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createMass(double)
	 */
	@Override
	public Mass createMass(double density) {
		double area = Math.PI * this.halfWidth * this.height;
		double m = area * density * 0.5;
		// moment of inertia given by: http://www.efunda.com/math/areas/ellipticalhalf.cfm
		double I = m * (this.halfWidth * this.halfWidth + this.height * this.height) * INERTIA_CONSTANT;
		return new Mass(this.center, m, I);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		// it turns out that a half ellipse is even more annoying than an ellipse
		
		// if the half ellipse is wider than it is tall
		if (this.halfWidth >= this.height) {
			// then we have two solutions based on the point location
			// if the point is below the half ellipse, then we need to perform
			// a golden section search like the ellipse code
			if (Segment.getLocation(center, this.vertexLeft, this.vertexRight) <= 0) {
				return this.getMaxDistanceEllipse(center);
			} else {
				// otherwise we can just take the greater distance of the vertices
				return this.getMaxDistanceToVertices(center);
			}
		} else {
			// otherwise we have even more conditions
			return this.getMaxDistanceHalfEllipse(center);
		}
	}
	
	/**
	 * Returns the maximum distance between the two vertices of the ellipse and the given point.
	 * @param point the point
	 * @return double
	 * @since 3.3.1
	 */
	private double getMaxDistanceToVertices(Vector2 point) {
		// find the maximum radius from the center
		double leftR = point.distanceSquared(this.vertexLeft);
		double rightR = point.distanceSquared(this.vertexRight);
		// keep the largest
		double r2 = Math.max(leftR, rightR);
		return Math.sqrt(r2);
	}
	
	/**
	 * Returns the maximum distance from the given point to the ellipse.
	 * @param point the point
	 * @return double
	 * @since 3.3.1
	 */
	private double getMaxDistanceEllipse(Vector2 point) {
		// we need to translate/rotate the point so that this ellipse is
		// considered centered at the origin with it's semi-major axis aligned
		// with the x-axis and its semi-minor axis aligned with the y-axis
		Vector2 p = point.difference(this.ellipseCenter).rotate(-this.getRotation());
		
		// get the farthest point
		Vector2 fp = Ellipse.getFarthestPointOnEllipse(this.halfWidth, this.height, p);
		
		// get the distance between the two points. The distance will be the
		// same if we translate/rotate the points back to the real position
		// and rotation, so don't bother
		return p.distance(fp);
	}
	
	/**
	 * Returns the maximum distance between the given point and the half ellipse.
	 * @param point the point
	 * @return double
	 * @since 3.3.1
	 */
	private double getMaxDistanceHalfEllipse(Vector2 point) {
		final double a = this.halfWidth;
		final double b = this.height;
		
		// we need to translate/rotate the point so that this ellipse is
		// considered centered at the origin with it's semi-major axis aligned
		// with the x-axis and its semi-minor axis aligned with the y-axis
		Vector2 p = point.difference(this.ellipseCenter).rotate(-this.getRotation());
		
		// if the point is below the x axis, then we only need to perform the ellipse code
		if (p.y < 0) {
			return this.getMaxDistanceEllipse(point);
		}
		
		// move the point to the 1st quadrant to conform my formulation
		if (p.x < 0) {
			p.x = -p.x;
		}
		
		// if the point is above the evolute, then we only need to evaluate
		// the max distance of the two vertices
		// evolute: (ax)^2/3 + (by)^2/3 = (a^2 - b^2)^2/3
		
		// compute the y coordinate of the point on the evolute at p.x
		// ey = ((b^2 - a^2)^2/3 - (ax)^2/3)^3/2 / b
		final double ab = (b * b - a * a);
		final double ab2r3 = Math.cbrt(ab * ab);
		final double ax = a * p.x;
		final double ax2r3 = Math.cbrt(ax * ax);
		double top = ab2r3 - ax2r3;
		
		if (top < 0) {
			// the evolute isn't defined at p.x
			return this.getMaxDistanceToVertices(point);
		}
		
		top = Math.sqrt(top);
		final double ey = (top * top * top) / b;
		
		if (p.y > ey) {
			// the point is above the evolute
			return this.getMaxDistanceToVertices(point);
		}
		
		// check if p.x is close to zero (if it is, then m will be inifinity)
		if (Math.abs(p.x) < 1e-16) {
			// compare the distance to the points and the height
			double d1 = this.height - p.y;
			double d2 = this.getMaxDistanceToVertices(point);
			return d1 > d2 ? d1 : d2;
		}
		
		// else compute the bounds for the unimodal region for golden section to work
		
		// compute the slope of the evolute at x
		// m = -a^2/3 * sqrt((b^2 - a^2)^2/3 - (ax)^2/3) / (bx^1/3)
		final double xr3 = Math.cbrt(p.x);
		final double a2r3 = Math.cbrt(a * a);
		final double m = (-a2r3 * top) / (b * xr3);
		
		// then compute the ellipse intersect of m, ex, and ey
		// y - ey = m(x - ex)
		// (x / a)^2 + (y / b)^2 = 1
		// solve for y then substitute
		// then examine terms to get quadratic equation parameters
		// qa = a^2m^2 + b^2
		// qb = 2a^2mey - 2a^2m^2ex
		// qc = a^2m^2ex^2 - 2a^2mexey + a^2ey^2 - b^2a^2
		final double a2 = a * a;
		final double b2 = b * b;
		final double m2 = m * m;
		final double x2 = p.x * p.x;
		final double y2 = ey * ey;
		
		// compute quadratic equation parameters
		final double qa = a2 * m2 + b2;
		final double qb = 2 * a2 * m * ey - 2 * a2 * m2 * p.x;
		final double qc = a2 * m2 * x2 - 2 * a2 * m * p.x * ey + a2 * y2 - b2 * a2;
		
		// use the quadratic equation to limit the search space
		final double b24ac = qb * qb - 4 * qa * qc;
		if (b24ac < 0) {
			// this would mean that the line from the evolute at p.x doesn't
			// intersect with the ellipse, which shouldn't be possible
			return this.getMaxDistanceToVertices(point);
		}
		
		final double xmin = (-qb - Math.sqrt(b24ac)) / (2 * qa);
		final double xmax = 0; 
		
		// get the farthest point on the ellipse
		Vector2 s = Ellipse.getFarthestPointOnBoundedEllipse(xmin, xmax, a, b, p);
		
		// then compare that with the farthest point of the two vertices
		double d1 = s.distance(p);
		double d2 = this.getMaxDistanceToVertices(point);
		
		return d1 > d2 ? d1 : d2;
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
		localPoint.rotate(-r, this.ellipseCenter.x, this.ellipseCenter.y);
		
		// translate into local coordinates
		double x = (localPoint.x - this.ellipseCenter.x);
		double y = (localPoint.y - this.ellipseCenter.y);
		
		// for half ellipse we have an early out
		if (y < 0) return false;
		
		double x2 = x * x;
		double y2 = y * y;
		double a2 = this.halfWidth * this.halfWidth;
		double b2 = this.height * this.height;
		double value = x2 / a2 + y2 / b2;
		
		if (value <= 1.0) {
			return true;
		}
		return false;
	}
	

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	@Override
	public void rotate(Rotation rotation, double x, double y) {
		super.rotate(rotation, x, y);
		
		// rotate the local axis as well
		this.rotation.rotate(rotation);
		
		this.vertexLeft.rotate(rotation, x, y);
		this.vertexRight.rotate(rotation, x, y);
		this.ellipseCenter.rotate(rotation, x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		// translate the centroid
		super.translate(x, y);
		// translate the pie vertices
		this.vertexLeft.add(x, y);
		this.vertexRight.add(x, y);
		// translate the ellipse center
		this.ellipseCenter.add(x, y);
	}

	/**
	 * Returns the rotation about the local center in radians.
	 * @return double the rotation in radians
	 */
	public double getRotation() {
		return this.rotation.toRadians();
	}
	
	/**
	 * @return the {@link Rotation} object that represents the local rotation
	 */
	public Rotation getRotationObject() {
		return this.rotation.copy();
	}
	
	/**
	 * Returns the width.
	 * @return double
	 */
	public double getWidth() {
		return this.halfWidth * 2;
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
		return this.halfWidth;
	}
	
	/**
	 * Returns the center of the ellipse.
	 * @return {@link Vector2}
	 */
	public Vector2 getEllipseCenter() {
		return this.ellipseCenter;
	}
}
