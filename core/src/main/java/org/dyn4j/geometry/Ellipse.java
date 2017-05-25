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
 * Implementation of an Ellipse {@link Convex} {@link Shape}.
 * <p>
 * An ellipse must have a width and height greater than zero.
 * <p>
 * <b>This shape is only supported by the GJK collision detection algorithm</b>
 * <p>
 * An <code>UnsupportedOperationException</code> is thrown when this shape is used with SAT.  If you 
 * are using or are planning on using the SAT collision detection algorithm, you can use the 
 * {@link Geometry#createPolygonalEllipse(int, double, double)} method to create a half ellipse
 * {@link Polygon} approximation. Another option is to use the GJK or your own collision detection
 * algorithm for this shape only and use SAT on others.
 * @author William Bittle
 * @version 3.2.3
 * @since 3.1.7
 */
public class Ellipse extends AbstractShape implements Convex, Shape, Transformable, DataContainer {
	/** The inverse of the golden ratio */
	private static final double INV_GOLDEN_RATIO = 1.0 / ((Math.sqrt(5.0) + 1.0) * 0.5);
	
	/** The maximum number of iterations to perform when finding the farthest point */
	private static final int FARTHEST_POINT_MAX_ITERATIONS = 50;
	
	/** The desired accuracy for the farthest point */
	private static final double FARTHEST_POINT_EPSILON = 1.0e-8;
	
	/** The ellipse width */
	final double width;
	
	/** The ellipse height */
	final double height;
	
	/** The half-width */
	final double halfWidth;
	
	/** The half-height */
	final double halfHeight;
	
	/** A local vector to  */
	final Vector2 localXAxis;
	
	/**
	 * Validated constructor.
	 * <p>
	 * This creates an axis-aligned ellipse fitting inside a rectangle of the given width and 
	 * height centered at the origin.
	 * @param valid always true or this constructor would not be called
	 * @param width the width
	 * @param height the height
	 */
	private Ellipse(boolean valid, double width, double height) {
		super(Math.max(width, height) * 0.5);
		
		this.width = width;
		this.height = height;
		
		// compute the major and minor axis lengths
		// (the x,y radii)
		this.halfWidth = width * 0.5;
		this.halfHeight = height * 0.5;
		
		// since we create ellipses as axis aligned we set the local x axis
		// to the world space x axis
		this.localXAxis = new Vector2(1.0, 0.0);
	}
	
	/**
	 * Minimal constructor.
	 * <p>
	 * This creates an axis-aligned ellipse fitting inside a rectangle of the given width and 
	 * height centered at the origin.
	 * @param width the width
	 * @param height the height
	 * @throws IllegalArgumentException if either the width or height is less than or equal to zero
	 */
	public Ellipse(double width, double height) {
		this(validate(width, height), width, height);
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
		if (width <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.ellipse.invalidWidth"));
		if (height <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.ellipse.invalidHeight"));
				
		return true;
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
		throw new UnsupportedOperationException(Messages.getString("geometry.ellipse.satNotSupported"));
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
		throw new UnsupportedOperationException(Messages.getString("geometry.ellipse.satNotSupported"));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 vector, Transform transform) {
		// convert the world space vector(n) to local space
		Vector2 localAxis = transform.getInverseTransformedR(vector);
		// include local rotation
		double r = this.getRotation();
		// invert the local rotation
		localAxis.rotate(-r);
		// an ellipse is a circle with a non-uniform scaling transformation applied
		// so we can achieve that by scaling the input axis by the major and minor
		// axis lengths
		localAxis.x *= this.halfWidth;
		localAxis.y *= this.halfHeight;
		// then normalize it
		localAxis.normalize();
		// add the radius along the vector to the center to get the farthest point
		Vector2 p = new Vector2(localAxis.x * this.halfWidth, localAxis.y  * this.halfHeight);
		// include local rotation
		// invert the local rotation
		p.rotate(r);
		p.add(this.center);
		// then finally convert back into world space coordinates
		transform.transform(p);
		return p;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Feature getFarthestFeature(Vector2 vector, Transform transform) {
		// obtain the farthest point along the given vector
		Vector2 farthest = this.getFarthestPoint(vector, transform);
		// for an ellipse the farthest feature along a vector will always be a vertex
		return new PointFeature(farthest);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#project(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 vector, Transform transform) {
		// get the world space farthest point
		Vector2 p1 = this.getFarthestPoint(vector, transform);
		// get the center in world space
		Vector2 center = transform.getTransformed(this.center);
		// project the center onto the axis
		double c = center.dot(vector);
		// project the point onto the axis
		double d = p1.dot(vector);
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
		double area = Math.PI * this.halfWidth * this.halfHeight;
		double m = area * density;
		// inertia about the z see http://math.stackexchange.com/questions/152277/moment-of-inertia-of-an-ellipse-in-2d
		double I = m * (this.halfWidth * this.halfWidth + this.halfHeight * this.halfHeight) / 4.0;
		return new Mass(this.center, m, I);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		// annoyingly, finding the radius of a rotated/translated ellipse
		// about another point is the same as finding the farthest point
		// from an arbitrary point. The solution to this is a quartic function
		// that has no analytic solution, so we are stuck with a maximization problem.
		// Thankfully, this method shouldn't be called that often, in fact
		// it should only be called when the user modifies the shapes on a body.
		
		// we need to translate/rotate the point so that this ellipse is
		// considered centered at the origin with it's semi-major axis aligned
		// with the x-axis and its semi-minor axis aligned with the y-axis
		Vector2 p = center.difference(this.center).rotate(-this.getRotation());
		
		// get the farthest point.
		Vector2 fp = Ellipse.getFarthestPoint(this.halfWidth, this.halfHeight, p);
		
		// get the distance between the two points. The distance will be the
		// same if we translate/rotate the points back to the real position
		// and rotation, so don't bother
		return p.distance(fp);
	}
	
	/**
	 * Returns the point on this ellipse farthest from the given point.
	 * <p>
	 * This method assumes that this ellipse is centered on the origin and 
	 * has it's semi-major axis aligned with the x-axis and its semi-minor 
	 * axis aligned with the y-axis.
	 * <p>
	 * This method performs a Golden Section Search to find the point of
	 * maximum distance from the given point.
	 * @param point the query point
	 * @param a the half width of the ellipse
	 * @param b the half height of the ellipse
	 * @return {@link Vector2}
	 * @since 3.2.3
	 */
	static final Vector2 getFarthestPoint(double a, double b, Vector2 point) 
	{
		double px = point.x;
		double py = point.y;
		
		// check the semi-major/minor axes
		boolean flipped = false;
		if (a < b) {
			// swap the semi-major/minor axes
			double temp = a;
			a = b;
			b = temp;

			// if we swap the axes, then we needt
			// also rotate our point
			temp = px;
			px = -py;
			py = temp;
			
			flipped = true;
		}
		
		// solve as if point is in 3rd quadrant
		// due to the symmetry of the ellipse we only have
		// to solve this problem in one quadrant and then
		// just flip signs to get the anwser in the original
		// quadrant
		int quadrant = 3;
		if (px >= 0 && py >= 0) {
			quadrant = 1;
			px = -px;
			py = -py;
		} else if (px >= 0 && py <= 0) {
			quadrant = 4;
			px = -px;
		} else if (px <= 0 && py >= 0) {
			quadrant = 2;
			py = -py;
		}
		
		// our bracketing bounds will be [x0, x1]
		double x0 = 0;
		double x1 = a;

		final Vector2 q = new Vector2(px, py);
		final Vector2 p = new Vector2();
		
		final double aa = a * a;
		final double ba = b / a;
		
		if (py == 0.0) {
			// then its on the x-axis and the farthest point is easy to calculate
			p.x = px < 0 ? a : -a;
			p.y = 0;
		} else {
			// compute the golden ratio test points
			double x2 = x1 - (x1 - x0) * INV_GOLDEN_RATIO;
			double x3 = x0 + (x1 - x0) * INV_GOLDEN_RATIO;
			double fx2 = getDistance(aa, ba, x2, q, p);
			double fx3 = getDistance(aa, ba, x3, q, p);

			// our bracket is now: [x0, x2, x3, x1]
			// iteratively reduce the bracket
			for (int i = 0; i < FARTHEST_POINT_MAX_ITERATIONS; i++) {
				if (fx2 < fx3) {
					if (Math.abs(x1 - x2) <= FARTHEST_POINT_EPSILON) {
						break;
					}
					x0 = x2;
					x2 = x3;
					fx2 = fx3;
					x3 = x0 + (x1 - x0) * INV_GOLDEN_RATIO;
					fx3 = getDistance(aa, ba, x3, q, p);
				} else {
					if (Math.abs(x3 - x0) <= FARTHEST_POINT_EPSILON) {
						break;
					}
					x1 = x3;
					x3 = x2;
					fx3 = fx2;
					x2 = x1 - (x1 - x0) * INV_GOLDEN_RATIO;
					fx2 = getDistance(aa, ba, x2, q, p);
				}
			}
		}
		
		// translate the point to the correct quadrant
		if (quadrant == 1) {
			p.x *= -1;
			p.y *= -1;
		} else if (quadrant == 2) {
			p.y *= -1;
		} else if (quadrant == 4) {
			p.x *= -1;
		}
		
		// flip the point's coorindates if the
		// semi-major/minor axes were flipped
		if (flipped) {
			double temp = p.x;
			p.x = p.y;
			p.y = -temp;
		}
		
		return p;
	}
	
	/**
	 * Returns the distance from the ellipse at the given x to the given point q.
	 * @param a2 the ellipse semi-major axis squared (a * a)
	 * @param ba the ellipse semi-minor axis divided by the semi-major axis (b / a)
	 * @param x the x of the point on the ellipse
	 * @param q the query point
	 * @param p output; the point on the ellipse
	 * @return double
	 */
	private static double getDistance(double a2, double ba, double x, Vector2 q, Vector2 p) {
		// compute the y value for the given x on the ellipse:
		// (x^2/a^2) + (y^2/b^2) = 1
		// y^2 = (1 - (x / a)^2) * b^2
		// y^2 = b^2/a^2(a^2 - x^2)
		// y = (b / a) * sqrt(a^2 - x^2)
		double a2x2 = a2 - (x * x);
		if (a2x2 < 0) {
			// this should never happen, but just in case of numeric instability
			// we'll just set it to zero
			a2x2 = 0;
			// x^2/a^2 can never be greater than 1 since a must always be
			// greater than or equal to the largest x value on the ellipse
		}
		double sa2x2 = Math.sqrt(a2x2);
		double y = ba * sa2x2;
		
		// compute the distance from the ellipse point to the query point
		double xx = (q.x - x);
		double yy = (q.y - y);
		double d2 = xx * xx + yy * yy;
		p.x = x;
		p.y = y;
		
		// return the distance
		return d2;
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
		localPoint.rotate(-r, this.center.x, this.center.y);
		
		double x = (localPoint.x - this.center.x);
		double y = (localPoint.y - this.center.y);
		double x2 = x * x;
		double y2 = y * y;
		double a2 = this.halfWidth * this.halfWidth;
		double b2 = this.halfHeight * this.halfHeight;
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
		return this.halfWidth;
	}

	/**
	 * Returns the half height.
	 * @return double
	 */
	public double getHalfHeight() {
		return this.halfHeight;
	}
}
