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
		// that has no analytic solution, so we are stuck with root finding.
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
	
	public Vector2 getFarthestPointFromPoint(Vector2 p, Transform transform) {
		// local coordinates
		Vector2 pLocal = transform.getInverseTransformed(p);
		
		// unrotated/translated coordinates
		Vector2 po = pLocal.difference(this.center).rotate(-this.getRotation());
		
		// get the farthest point
		Vector2 fp = Ellipse.getFarthestPoint(this.halfWidth, this.halfHeight, po);
		
		// rotate/translate back
		Vector2 pn = fp.rotate(this.getRotation()).add(this.center);
		
		return transform.getTransformed(pn);
	}
	
	/**
	 * Returns the point on this ellipse farthest from the given point.
	 * <p>
	 * This method assumes that this ellipse is centered on the origin and 
	 * has it's semi-major axis aligned with the x-axis and its semi-minor 
	 * axis aligned with the y-axis.
	 * @param point the query point
	 * @param a the half width of the ellipse
	 * @param b the half height of the ellipse
	 * @return {@link Vector2}
	 * @since 3.2.3
	 */
	static final Vector2 getFarthestPoint(double a, double b, Vector2 point) 
	{
		final int maxIterations = 50;
		final double epsilon = 1e-8;
		
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
		
		Vector2 r = root(a, b, px, py);
		
		// our root finding bounds will be [x0, x1]
		double x0 = 0;
		double x1 = a;
		double y0 = b;
		double y1 = 0;

		// compute the initial maximum distance
		double xx = (px - x0);
		double yy = (py - y0);
//		double max = xx * xx + yy * yy;
//		double m1 = max;
//		double m2 = (px - x1) * (px - x1) + (py * py);
		
		// this will store our output
		double x = 0;
		double y = 0;
		
		// begin the root finding
		final double gr = 1 / ((Math.sqrt(5) + 1) * 0.5);
		final double a2 = a * a;
		final double ba = b / a;
		
		double x2 = x1 - (x1 - x0) * gr;
		double x3 = x0 + (x1 - x0) * gr;
		
		final Vector2 q = new Vector2(px, py);
		final Vector2 p2 = new Vector2();
		final Vector2 p3 = new Vector2();
		double fx0 = b;
		double fx1 = 0;
		double fx2 = eval(a, b, x2, q, p2);
		double fx3 = eval(a, b, x3, q, p3);
		
		Vector2 p = new Vector2();
		
//		final int n = 50;
//		for (int i = 0; i <= n; i++) {
//			// get the mid point (bisection) of our [x0, x1] interval
//			x = x0 + (a / n) * i;
//			
//			// compute the y value for the mid point
//			// x^2/a^2 + y^2/b^2 = 1
//			// y^2/b^2 = 1 - x^2/a^2
//			// y^2 = (1 - x^2/a^2)b^2
//			// y = sqrt((1 - x^2/a^2) / b^2)
//			// y = b * sqrt(1 - x^2/a^2)
//			// y = b/a * sqrt(a^2 - x^2)
//			double a2x2 = a2 - x * x;
//			if (a2x2 < 0) {
//				// this should never happen, but just in case of numeric instability
//				// we'll just set it to zero
//				a2x2 = 0;
//				// x^2/a^2 can never be greater than 1 since a must always be
//				// greater than or equal to the largest x value on the ellipse
//			}
//			double sa2x2 = Math.sqrt(a2x2);
//			y = ba * sa2x2;
//			
//			xx = (px - x);
//			yy = (py - y);
//			double d2 = xx * xx + yy * yy;
//			
//			System.out.println("(" + x + ", " + y + ") = " + Math.sqrt(d2));
//		}
//		
		
		double xp = (x0 + x1) * 0.5;
		int i = 0;
		for (; i < maxIterations; i++) {
//			// Newton
//			double fx = eval(a, b, xp, q, p);
//			
////			double dx = x0 - px - (a * a) / (b * b) * x0 + (x0 * (b / a) * py) / (a * Math.sqrt(a * a + x0 * x0));
//			double dx = (-b * xp) / (a * Math.sqrt(a * a - xp * xp));
//			Vector2 v1 = q.to(xp, fx).multiply(2);
//			Vector2 v2 = new Vector2(b * xp, a * Math.sqrt(a * a - xp * xp));
//			double fdx = v1.dot(v2);
//			
//			if (Math.abs(fdx) < 1e-16) {
//				// denominator too small
//				break;
//			}
//			
//			double xn = x0 + fx / fdx;
//			if (Math.abs(xn - x0) <= epsilon * Math.abs(xn)) {
//				// found
//				x = x0;
//				y = fx;
//				break;
//			}
//			
//			x0 = xn;
			
			double bafbfc = (x1 - x0) * (fx1 - fx0);
			double bcfbfa = (x1 - x2) * (fx1 - fx2);
			double bcqbap = (x1 - x2) * bcfbfa - (x1 - x0) * bafbfc;
			double pq = 2.0 * (bafbfc - bcfbfa);
			
			if (Math.abs(pq) > 1e-10) {
				double xn = x1 + bcqbap / pq;
				if (x0 < xn && xn < x1) {
				System.out.println(x0 + ", " + xn + ", " + x1);
				}
			} else {
				
			}
			
			
			
			// Golden Section search
			// ====================================
			if (fx2 < fx3) {
				if (Math.abs(x1 - x2) <= epsilon) {
					x = p3.x;
					y = p3.y;
					break;
				}
				x0 = x2;
				fx0 = fx2;
				x2 = x3;
				fx2 = fx3;
				x3 = x0 + (x1 - x0) * gr;
				fx3 = eval(a, b, x3, q, p3);
			} else {
				if (Math.abs(x3 - x0) <= epsilon) {
					x = p2.x;
					y = p2.y;
					break;
				}
				x1 = x3;
				fx1 = fx3;
				x3 = x2;
				fx3 = fx2;
				x2 = x1 - (x1 - x0) * gr;
				fx2 = eval(a, b, x2, q, p2);
			}
			// ====================================
			
//			if (Math.abs(x2 - x3) <= epsilon) {
//				x = x2;
//				y = y2;
//				break;
//			}
//			
//			double a2x2 = a2 - x2 * x2;
//			if (a2x2 < 0) {
//				// this should never happen, but just in case of numeric instability
//				// we'll just set it to zero
//				a2x2 = 0;
//				// x^2/a^2 can never be greater than 1 since a must always be
//				// greater than or equal to the largest x value on the ellipse
//			}
//			double sa2x2 = Math.sqrt(a2x2);
//			y2 = ba * sa2x2;
//			xx = (px - x2);
//			yy = (py - y2);
//			double fx2 = xx * xx + yy * yy;
//			
//			a2x2 = a2 - x3 * x3;
//			if (a2x2 < 0) {
//				// this should never happen, but just in case of numeric instability
//				// we'll just set it to zero
//				a2x2 = 0;
//				// x^2/a^2 can never be greater than 1 since a must always be
//				// greater than or equal to the largest x value on the ellipse
//			}
//			sa2x2 = Math.sqrt(a2x2);
//			y3 = ba * sa2x2;
//			xx = (px - x3);
//			yy = (py - y3);
//			double fx3 = xx * xx + yy * yy;
//			
//			if (fx2 < fx3) {
//				x0 = x2;
//			} else {
//				x1 = x3;
//			}
//			
//			x2 = x1 - (x1 - x0) * gr;
//			x3 = x0 + (x1 - x0) * gr;
			
			
			// get the mid point (bisection) of our [x0, x1] interval
//			x = (x0 + x1) * 0.5;
			
//			// compute the y value for the mid point
//			// x^2/a^2 + y^2/b^2 = 1
//			// y^2/b^2 = 1 - x^2/a^2
//			// y^2 = (1 - x^2/a^2)b^2
//			// y = sqrt((1 - x^2/a^2) / b^2)
//			// y = b * sqrt(1 - x^2/a^2)
//			// y = b/a * sqrt(a^2 - x^2)
//			double a2x2 = a2 - x * x;
//			if (a2x2 < 0) {
//				// this should never happen, but just in case of numeric instability
//				// we'll just set it to zero
//				a2x2 = 0;
//				// x^2/a^2 can never be greater than 1 since a must always be
//				// greater than or equal to the largest x value on the ellipse
//			}
//			double sa2x2 = Math.sqrt(a2x2);
//			y = ba * sa2x2;
			
//			Vector2 v = new Vector2(x - px, y - py);
//			Vector2 t = new Vector2(a * sa2x2, x * b);
//			double perp = v.cross(t);
			
			// get the squared distance from the point
//			xx = (px - x);
//			yy = (py - y);
//			double d = xx * xx + yy * yy;

			// are we close enough?
//			if (Math.abs(x0-x1) <= epsilon) {
//				break;
//			}
//						
//			// how do we need to update the bounds
//			if (d > m2 && d > m1) {
//				m1 = m2;
//				m2 = d;
//				x0 = x1;
//				x1 = x;
//			} else if (d > m1) {
//				m1 = d;
//				x0 = x;
//			} 
//			else if (d > m2) {
//				m2 = d;
//				x1 = x;
//			}
//			if (d > m2) {
//				x1 = x;
//			} else {
//				x0 = x;
//			}

			// set the new maximum
//			if (max < d) {
//				max = d;
//			}
		}
		
		System.out.println(i);
		
		// translate the point to the correct quadrant
		if (quadrant == 1) {
			x *= -1;
			y *= -1;
		} else if (quadrant == 2) {
			y *= -1;
		} else if (quadrant == 4) {
			x *= -1;
		}
		
		// flip the point's coorindates if the
		// semi-major/minor axes were flipped
		if (flipped) {
			double temp = x;
			x = y;
			y = -temp;
		}
		
		return new Vector2(x, y);
	}
	
	private static Vector2 root(double a, double b, double px, double py) {
		final double ab = a/b;
		final double abab = ab * ab; // r0
		final double pxa = px / a;  // z0
		final double pyb = py / b;  // z1
		double fx = pxa * pxa + pyb * pyb - 1;  // g = (px / a)^2 + (py / b)^2 - 1 = 0
		final double n0 = abab * pxa;
		double s0 = pyb - 1;
		double s1 = fx < 0 ? 0 : Math.sqrt(n0 * n0 + pyb * pyb); // sqrt( ((a/b)^2 * (px/a))^2 + (py/b)^2 )
		double s = 0;
		for (int i = 0; i < 50; i++) {
			s = (s0 + s1) * 0.5;
			double x0 = n0 / (s + abab);
			double y0 = pyb / (s + 1);
			
			fx = x0 * x0 + y0 * y0 - 1;
			if (fx > 0) {
				s0 = s;
			} else if (fx < 0) {
				s1 = s;
			} else {
				break;
			}
		}

		double x = (abab * px) / (s + abab);
		double y = (py) / (s + 1);

		return new Vector2(x, y);
	}
	
	private static double eval(double a, double b, double x, Vector2 q, Vector2 p) {
		double a2x2 = (a * a) - (x * x);
		if (a2x2 < 0) {
			// this should never happen, but just in case of numeric instability
			// we'll just set it to zero
			a2x2 = 0;
			// x^2/a^2 can never be greater than 1 since a must always be
			// greater than or equal to the largest x value on the ellipse
		}
		double sa2x2 = Math.sqrt(a2x2);
		double y = (b / a) * sa2x2;
		double xx = (q.x - x);
		double yy = (q.y - y);
		double d2 = xx * xx + yy * yy;
		p.x = x;
		p.y = y;
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
