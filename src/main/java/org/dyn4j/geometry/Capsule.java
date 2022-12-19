/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.exception.ValueOutOfRangeException;

/**
 * Implementation of a Capsule {@link Convex} {@link Shape}.
 * <p>
 * A capsule can be described as a rectangle with two half circle caps on both ends. A capsule is created
 * by specifying the bounding rectangle of the entire {@link Shape}.
 * <p>
 * If the height is larger than the width the caps will be on the top and bottom of the shape. Otherwise
 * the caps are on the left and right ends of the shape.
 * <p>
 * A capsule's width and height must be larger than zero and cannot be equal.  A {@link Circle} should be used
 * instead of an equal width/height capsule for both performance and stability.
 * @author William Bittle
 * @version 5.0.0
 * @since 3.1.5
 */
public class Capsule extends AbstractShape implements Convex, Shape, Transformable, DataContainer {
	/** 
	 * The Capsule shape has two edge features which could be returned from the {@link #getFarthestFeature(Vector2, Transform)}
	 * method. Under normal floating point conditions the edges will never be selected as the farthest features. Due to this,
	 * stacking of capsule shapes is very unstable (or any resting contact that involves the edge). We introduce this factor
	 * (% of projected normal) to help select the edge in cases where the collision normal is nearly parallel to the edge normal.
	 */
	protected static final double EDGE_FEATURE_SELECTION_CRITERIA = 0.98;
	
	/**
	 * Because we are selecting an edge even when the farthest feature should be a vertex, when the edges are clipped
	 * against each other (in the ClippingManifoldSolver) they will not overlap. Due to this, we introduce an expansion
	 * value (% of the width) that expands the edge feature so that in these cases a collision manifold is still generated.
	 */
	protected static final double EDGE_FEATURE_EXPANSION_FACTOR = 0.1;
	
	/** The bounding rectangle width */
	final double length;
	
	/** The end cap radius */
	final double capRadius;
	
	/** The focal points for the caps */
	final Vector2[] foci;
	
	/** The local x-axis */
	final Vector2 localXAxis;
	
	/**
	 * Validated constructor.
	 * <p>
	 * Creates an axis-aligned capsule centered on the origin with the caps on
	 * ends of the larger dimension.
	 * @param valid always true or this constructor would not be called
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 */
	private Capsule(boolean valid, double width, double height) {
		super(Math.max(width, height) * 0.5);

		// determine the major and minor axis
		double major = width;
		double minor = height;
		boolean vertical = false;
		if (width < height) {
			major = height;
			minor = width;
			vertical = true;
		}
		
		// set the width
		this.length = major;
		// the cap radius is half the height
		this.capRadius = minor * 0.5;
		
		// generate the cap focal points on the
		// major axis
		double f = (major - minor) * 0.5;
		this.foci = new Vector2[2];
		if (vertical) {
			this.foci[0] = new Vector2(0, -f);
			this.foci[1] = new Vector2(0,  f);
			
			// set the local x-axis (to the y-axis)
			this.localXAxis = new Vector2(0.0, 1.0);
		} else {
			this.foci[0] = new Vector2(-f, 0);
			this.foci[1] = new Vector2( f, 0);
			
			// set the local x-axis
			this.localXAxis = new Vector2(1.0, 0.0);
		}
	}
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates an axis-aligned capsule centered on the origin with the caps on
	 * ends of the larger dimension.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @throws IllegalArgumentException thrown if width or height are less than or equal to zero or if the width and height are near equal
	 */
	public Capsule(double width, double height) {
		this(validate(width, height), width, height);
	}
	
	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param width the bounding rectangle width
	 * @param height the bounding rectangle height
	 * @return boolean true
	 * @throws IllegalArgumentException thrown if width or height are less than or equal to zero or if the width and height are near equal
	 */
	private static final boolean validate(double width, double height) {
		// validate the width and height
		if (width <= 0) 
			throw new ValueOutOfRangeException("width", width, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
			
		if (height <= 0)
			throw new ValueOutOfRangeException("height", height, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check for basically a circle
		if (Math.abs(width - height) < Epsilon.E) throw new IllegalArgumentException("The width and height cannot be near equal - use a Circle instead.");
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Capsule[").append(super.toString())
		.append("|Width=").append(this.length)
		.append("|CapRadius=").append(this.capRadius)
		.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getAxes(org.dyn4j.geometry.Vector2[], org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// check for given foci
		if (foci != null) {
			// we need to include the shortest vector from foci to foci
			Vector2[] axes = new Vector2[2 + foci.length];
			
			axes[0] = transform.getTransformedR(this.localXAxis);
			axes[1] = transform.getTransformedR(this.localXAxis.getRightHandOrthogonalVector());
			
			Vector2 f1 = transform.getTransformed(this.foci[0]);
			Vector2 f2 = transform.getTransformed(this.foci[1]);
			for (int i = 0; i < foci.length; i++) {
				// get the one closest to the given focus
				double d1 = f1.distanceSquared(foci[i]);
				double d2 = f2.distanceSquared(foci[i]);
				
				Vector2 v = null;
				if (d1 < d2) {
					v = f1.to(foci[i]);
				} else {
					v = f2.to(foci[i]);
				}
				
				v.normalize();
				axes[2 + i] = v;
			}
			return axes;
		}
		// if there were no foci given then just return the normal axes for the
		// rectangular region
		return new Vector2[] {
			transform.getTransformedR(this.localXAxis),
			transform.getTransformedR(this.localXAxis.getRightHandOrthogonalVector())
		};
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFoci(org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		// return the cap foci
		return new Vector2[] {
			transform.getTransformed(this.foci[0]),
			transform.getTransformed(this.foci[1])
		};
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 vector, Transform transform) {
		// make sure the given direction is normalized
		vector.normalize();
		// a capsule is just a radially expanded line segment
		Vector2 p = Segment.getFarthestPoint(this.foci[0], this.foci[1], vector, transform);
		// apply the radial expansion
		return p.add(vector.product(this.capRadius));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Feature getFarthestFeature(Vector2 vector, Transform transform) {
		// test whether the given direction is within a certain angle of the
		// local x axis. if so, use the edge feature rather than the point
		Vector2 localAxis = transform.getInverseTransformedR(vector);
		Vector2 n1 = this.localXAxis.getLeftHandOrthogonalVector();
		
		// get the squared length of the localaxis and add the fudge factor
		// should always 1.0 * factor since localaxis is normalized
		double d = localAxis.dot(localAxis) * Capsule.EDGE_FEATURE_SELECTION_CRITERIA;
		// project the normal onto the localaxis normal
		double d1 = localAxis.dot(n1);
		
		// we only need to test one normal since we only care about its projection length
		// we can later determine which direction by the sign of the projection
		if (Math.abs(d1) < d) {
			// then its the farthest point
			Vector2 point = this.getFarthestPoint(vector, transform);
			return new PointFeature(point);
		} else {
			// compute the vector to add/sub from the foci
			Vector2 v = n1.multiply(this.capRadius);
			// compute an expansion amount based on the width of the shape
			Vector2 e = this.localXAxis.product(this.length * 0.5 * EDGE_FEATURE_EXPANSION_FACTOR);
			if (d1 > 0) {
				Vector2 p1 = this.foci[0].sum(v).subtract(e);
				Vector2 p2 = this.foci[1].sum(v).add(e);
				// return the full bottom side
				return Segment.getFarthestFeature(p1, p2, vector, transform);
			} else {
				Vector2 p1 = this.foci[0].difference(v).subtract(e);
				Vector2 p2 = this.foci[1].difference(v).add(e);
				return Segment.getFarthestFeature(p1, p2, vector, transform);
			}
		}
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
	 * @see org.dyn4j.geometry.Shape#computeAABB(org.dyn4j.geometry.Transform, org.dyn4j.geometry.AABB)
	 */
	@Override
	public void computeAABB(Transform transform, AABB aabb) {
		// Inlined projection of x axis
		// Interval x = this.project(Vector2.X_AXIS, transform);
		Vector2 p1 = this.getFarthestPoint(Vector2.X_AXIS, transform);
		double c = transform.getTransformedX(this.center);
		double minX = 2 * c - p1.x;
		double maxX = p1.x;
		
		// Inlined projection of y axis
		// Interval y = this.project(Vector2.Y_AXIS, transform);
		p1 = this.getFarthestPoint(Vector2.Y_AXIS, transform);
		c = transform.getTransformedY(this.center);
		double minY = 2 * c - p1.y;
		double maxY = p1.y;
		
		aabb.maxX = maxX;
		aabb.maxY = maxY;
		aabb.minX = minX;
		aabb.minY = minY;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createMass(double)
	 */
	@Override
	public Mass createMass(double density) {
		// the mass of a capsule is the mass of the rectangular section plus the mass
		// of two half circles (really just one circle)
		
		double h = this.capRadius * 2.0;
		double w = this.length - h;
		double r2 = this.capRadius * this.capRadius;
		
		// compute the rectangular area
		double ra = w * h;
		// compuate the circle area
		double ca = r2 * Math.PI;
		double rm = density * ra;
		double cm = density * ca;
		double m = rm + cm;
		
		// the inertia is slightly different. Its the inertia of the rectangular
		// region plus the inertia of half a circle moved from the center
		double d = w * 0.5;
		// parallel axis theorem I2 = Ic + m * d^2
		double cI = 0.5 * cm * r2 + cm * d * d;
		double rI = rm * (h * h + w * w) / 12.0;
		// add the rectangular inertia and cicle inertia
		double I = rI + cI;
		
		return new Mass(this.center, m, I);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getArea()
	 */
	@Override
	public double getArea() {
		double h = this.capRadius * 2.0;
		double w = this.length - h;
		double r2 = this.capRadius * this.capRadius;
		
		// compute the rectangular area
		double ra = w * h;
		// compuate the circle area
		double ca = r2 * Math.PI;
		
		// return the total area
		return ra + ca;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		return Geometry.getRotationRadius(center, this.foci) + this.capRadius;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#contains(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform, boolean)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform, boolean inclusive) {
		// a capsule is just a radially expanded line segment
		Vector2 p = Segment.getPointOnSegmentClosestToPoint(point, transform.getTransformed(this.foci[0]), transform.getTransformed(this.foci[1]));
		double r2 = this.capRadius * this.capRadius;
		double d2 = p.distanceSquared(point);
		return inclusive ? d2 <= r2 : d2 < r2;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	@Override
	public void rotate(Rotation rotation, double x, double y) {
		super.rotate(rotation, x, y);
		
		// rotate the foci
		this.foci[0].rotate(rotation, x, y);
		this.foci[1].rotate(rotation, x, y);
		// rotate the local x-axis
		this.localXAxis.rotate(rotation);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		super.translate(x, y);
		// translate the foci
		this.foci[0].add(x, y);
		this.foci[1].add(x, y);
	}

	/**
	 * Returns the rotation about the local center in radians in the range [-&pi;, &pi;].
	 * @return double the rotation in radians
	 */
	public double getRotationAngle() {
		return Math.atan2(this.localXAxis.y, this.localXAxis.x);
	}
	
	/**
	 * @return the {@link Rotation} object that represents the local rotation
	 */
	public Rotation getRotation() {
		// localXAxis is already a unit vector so we can just return it as a {@link Rotation}
		return new Rotation(this.localXAxis.x, this.localXAxis.y);
	}
	
	/**
	 * Returns the length of the capsule.
	 * <p>
	 * The length is the largest dimension of the capsule's
	 * bounding rectangle.
	 * @return double
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Returns the end cap radius.
	 * @return double
	 */
	public double getCapRadius() {
		return this.capRadius;
	}
}
