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
 * Implementation of a Slice {@link Convex} {@link Shape}.
 * <p>
 * A slice is a piece of a {@link Circle}.
 * <p>
 * This shape can represent any slice of a circle up to 180 degrees (half circle).
 * @author William Bittle
 * @version 3.3.1
 * @since 3.1.5
 */
public class Slice extends AbstractShape implements Convex, Shape, Transformable, DataContainer {
	
	/** Half the total circular section in radians */
	final double alpha, cosAlpha;
	
	/** The radius passed in at creation */
	final double sliceRadius;
	
	/** The vertices of the slice */
	final Vector2[] vertices;
	
	/** The normals of the polygonal sides */
	final Vector2[] normals;
	
	/** The local rotation in radians */
	final Rotation rotation;
	
	/**
	 * Validated constructor.
	 * <p>
	 * This method creates a slice of a circle with the <b>circle center</b> at the origin
	 * and half of theta below the x-axis and half above.
	 * @param valid always true or this constructor would not be called
	 * @param radius the radius of the circular section
	 * @param theta the angular extent in radians; must be greater than zero and less than or equal to &pi;
	 * @param center the center
	 */
	private Slice(boolean valid, double radius, double theta, Vector2 center) {
		super(center, Math.max(center.x, center.distance(new Vector2(radius, 0).rotate(0.5*theta))));
		
		this.sliceRadius = radius;
		this.alpha = theta * 0.5;
		
		// compute the triangular section of the pie (and cache cos(alpha))
		double x = radius * (this.cosAlpha = Math.cos(this.alpha));
		double y = radius * Math.sin(this.alpha);
		this.vertices = new Vector2[] {
			// the origin
			new Vector2(),
			// the top point
			new Vector2(x, y),
			// the bottom point
			new Vector2(x, -y)
		};
		
		Vector2 v1 = this.vertices[1].to(this.vertices[0]);
		Vector2 v2 = this.vertices[0].to(this.vertices[2]);
		v1.left().normalize();
		v2.left().normalize();
		this.normals = new Vector2[] { v1, v2 };
		
		// Initially the slice is aligned to the world space x axis
		this.rotation = new Rotation();
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * This method creates a slice of a circle with the <b>circle center</b> at the origin
	 * and half of theta below the x-axis and half above.
	 * @param radius the radius of the circular section
	 * @param theta the angular extent in radians; must be greater than zero and less than or equal to &pi;
	 * @throws IllegalArgumentException throw if 1) radius is less than or equal to zero or 2) theta is less than or equal to zero or 3) theta is greater than 180 degrees
	 */
	public Slice(double radius, double theta) {
		this(validate(radius, theta), radius, theta, new Vector2(2.0 * radius * Math.sin(theta * 0.5) / (1.5 * theta), 0));
	}

	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param radius the radius of the circular section
	 * @param theta the angular extent in radians; must be greater than zero and less than or equal to &pi;
	 * return true
	 * @throws IllegalArgumentException throw if 1) radius is less than or equal to zero or 2) theta is less than or equal to zero or 3) theta is greater than 180 degrees
	 */
	private static final boolean validate(double radius, double theta) {
		// check the radius
		if (radius <= 0) throw new IllegalArgumentException(Messages.getString("geometry.slice.invalidRadius"));
		// check the theta
		if (theta <= 0 || theta > Math.PI) throw new IllegalArgumentException(Messages.getString("geometry.slice.invalidTheta"));
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Slice[").append(super.toString())
		.append("|Radius=").append(this.sliceRadius)
		.append("|Theta=").append(this.getTheta())
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getAxes(org.dyn4j.geometry.Vector2[], org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// get the size of the foci list
		int fociSize = foci != null ? foci.length : 0;
		// get the number of vertices this polygon has
		int size = this.vertices.length;
		// the axes of a polygon are created from the normal of the edges
		// plus the closest point to each focus
		Vector2[] axes = new Vector2[2 + fociSize];
		int n = 0;
		
		// add the normals of the sides
		axes[n++] = transform.getTransformedR(this.normals[0]);
		axes[n++] = transform.getTransformedR(this.normals[1]);
		
		// loop over the focal points and find the closest
		// points on the polygon to the focal points
		Vector2 focus = transform.getTransformed(this.vertices[0]);
		for (int i = 0; i < fociSize; i++) {
			// get the current focus
			Vector2 f = foci[i];
			// create a place for the closest point
			Vector2 closest = focus;
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
	 * Returns a single point, the circle center.
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		return new Vector2[] {
			transform.getTransformed(this.vertices[0])	
		};
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 vector, Transform transform) {
		Vector2 localn = transform.getInverseTransformedR(vector);
		Vector2 localnRotated;
		
		// We need to normalize localn in order for localnRotated.x < cosAlpha to work
		// and we also use that to compute the farthest point in the circle part of the slice
		localn.normalize();
		
		// Include rotation if needed
		// Note that the vertices are already rotated and we need both the rotated and not rotated localn vector
		if (!this.rotation.isIdentity()) {
			localnRotated = localn.copy().inverseRotate(this.rotation);
		} else {
			localnRotated = localn;
		}
		
		// if (abs(angleBetween(localn, rotation)) < alpha)
		if (localnRotated.x < cosAlpha) {
			double edge = this.vertices[0].dot(localn);
			int maxIndex = 0;
			
			// Based on the sign of localnRotated.y we can rule out one vertex
			if (localnRotated.y < 0) {
				if (this.vertices[2].dot(localn) > edge) {
					maxIndex = 2;
				}
			} else {
				if (this.vertices[1].dot(localn) > edge) {
					maxIndex = 1;
				}
			}
			
			Vector2 point = new Vector2(this.vertices[maxIndex]);
			
			// transform the point into world space
			transform.transform(point);
			
			return point;
		} else {
			// NOTE: taken from Circle.getFarthestPoint with some modifications
			localn.multiply(this.sliceRadius).add(this.vertices[0]);
			transform.transform(localn);
			
			return localn;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Feature getFarthestFeature(Vector2 vector, Transform transform) {
		Vector2 localn = transform.getInverseTransformedR(vector);
		Vector2 localnRotated;
		
		// We need to normalize localn in order for localnRotated.x < cosAlpha to work
		// and we also use that to compute the farthest point in the circle part of the slice
		localn.normalize();
		
		// Include rotation if needed
		// Note that the vertices are already rotated and we need both the rotated and not rotated localn vector
		if (!this.rotation.isIdentity()) {
			localnRotated = localn.copy().inverseRotate(this.rotation);
		} else {
			localnRotated = localn;
		}
		
		// if (abs(angleBetween(localn, rotation)) < alpha)
		if (localnRotated.x < cosAlpha) {
			// check if this section is nearly a half circle
			if (cosAlpha <= 1.0e-6) {
				// if so, we want to return the full back side
				return Segment.getFarthestFeature(this.vertices[1], this.vertices[2], vector, transform);
			}
			
			// otherwise check which side its on
			if (localnRotated.y > 0) {
				// then its the top segment
				return Segment.getFarthestFeature(this.vertices[0], this.vertices[1], vector, transform);
			} else if (localnRotated.y < 0) {
				// then its the bottom segment
				return Segment.getFarthestFeature(this.vertices[0], this.vertices[2], vector, transform);
			} else {
				// then its the tip point
				return new PointFeature(transform.getTransformed(this.vertices[0]));
			}
		} else {
			// taken from Slice::getFarthestPoint
			localn.multiply(this.sliceRadius).add(this.vertices[0]);
			transform.transform(localn);
			
			return new PointFeature(localn);
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
		// Interval x = this.project(Vector2.X_AXIS, transform);
		double minX = this.getFarthestPoint(Vector2.INV_X_AXIS, transform).x;
		double maxX = this.getFarthestPoint(Vector2.X_AXIS, transform).x;
		
		// Inlined projection of y axis
		// Interval y = this.project(Vector2.Y_AXIS, transform);
		double minY = this.getFarthestPoint(Vector2.INV_Y_AXIS, transform).y;
		double maxY = this.getFarthestPoint(Vector2.Y_AXIS, transform).y;
		
		return new AABB(minX, minY, maxX, maxY);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createMass(double)
	 */
	@Override
	public Mass createMass(double density) {
		// area of a circular section is a = r^2 * alpha
		double r2 = this.sliceRadius * this.sliceRadius;
		double m = density * r2 * this.alpha;
		// inertia about z: http://www.efunda.com/math/areas/CircularSection.cfm
		double sina = Math.sin(this.alpha);
		double I = 1.0 / 18.0 * r2 * r2 * (9.0 * this.alpha * this.alpha - 8.0 * sina * sina) / this.alpha;
		return new Mass(this.center, m, I);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		// is the given center in region A?
		// \    /)
		//  \  /  )
		//   \/    )
		//A  /\    )
		//  /  \  )
		// /    \)
		if (Segment.getLocation(center, this.vertices[1], this.vertices[0]) <= 0 &&
			Segment.getLocation(center, this.vertices[2], this.vertices[0]) >= 0) {
			// if so, its the slice radius plus the distance from the
			// center to the tip of the slice
			return this.sliceRadius + center.distance(this.vertices[0]);
		} else {
			// otherwise its the rotation radius of the triangular section
			return Geometry.getRotationRadius(center, this.vertices);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#contains(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		// see if the point is in the circle
		// transform the point into local space
		Vector2 lp = transform.getInverseTransformed(point);
		// get the transformed radius squared
		double radiusSquared = this.sliceRadius * this.sliceRadius;
		// create a vector from the circle center to the given point
		Vector2 v = this.vertices[0].to(lp);
		if (v.getMagnitudeSquared() <= radiusSquared) {
			// if its in the circle then we need to make sure its in the section
			if (Segment.getLocation(lp, this.vertices[0], this.vertices[1]) <= 0 &&
				Segment.getLocation(lp, this.vertices[0], this.vertices[2]) >= 0) { 
				return true;
			}
		}
		// if its not in the circle then no other checks need to be performed
		return false;
	}
	

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	@Override
	public void rotate(Rotation rotation, double x, double y) {
		super.rotate(rotation, x, y);
		
		// rotate the pie vertices
		for (int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].rotate(rotation, x, y);
		}
		
		// rotate the pie normals
		for (int i = 0; i < this.normals.length; i++) {
			this.normals[i].rotate(rotation);
		}
		
		// rotate the local x axis
		this.rotation.rotate(rotation);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		// translate the centroid
		super.translate(x, y);
		// translate the pie vertices
		for (int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].add(x, y);
		}
	}
	
	/**
	 * Returns the rotation about the local center in radians.
	 * @return double the rotation in radians
	 */
	public double getRotationAngle() {
		return this.rotation.toRadians();
	}
	
	/**
	 * @return the {@link Rotation} object that represents the local rotation
	 */
	public Rotation getRotation() {
		return this.rotation.copy();
	}
	
	/**
	 * Returns the angular extent of the slice in radians.
	 * @return double
	 */
	public double getTheta() {
		return this.alpha * 2;
	}

	/**
	 * Returns the slice radius.
	 * <p>
	 * This is the radius passed in at creation.
	 * @return double
	 */
	public double getSliceRadius() {
		return this.sliceRadius;
	}
	
	/**
	 * Returns the tip of the pie shape.
	 * <p>
	 * This is the center of the circle.
	 * @return {@link Vector2}
	 */
	public Vector2 getCircleCenter() {
		return this.vertices[0];
	}
}
