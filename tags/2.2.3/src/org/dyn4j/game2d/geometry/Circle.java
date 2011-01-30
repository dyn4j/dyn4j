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
 * Represents a {@link Circle}.
 * <p>
 * A {@link Circle}'s radius must be larger than zero.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Circle extends AbstractShape implements Convex, Shape, Transformable {
	/** The circle {@link Shape.Type} */
	public static final Shape.Type TYPE = new Shape.Type("Circle");
	
	/**
	 * Full constructor.
	 * @param radius the radius
	 * @throws IllegalArgumentException if the given radius is less than or equal to zero
	 */
	public Circle(double radius) {
		if (radius <= 0.0) throw new IllegalArgumentException("The radius must be greater than zero.");
		this.center = new Vector2();
		this.radius = radius;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#getType()
	 */
	@Override
	public Type getType() {
		return Circle.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CIRCLE[").append(super.toString()).append("|").append(radius).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#contains(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean contains(Vector2 point, Transform transform) {
		// transform the center
		Vector2 v = transform.getTransformed(this.center);
		// get the transformed radius squared
		double radiusSquared = this.radius * this.radius;
		// create a vector from the center to the given point
		v.subtract(point);
		if (v.getMagnitudeSquared() <= radiusSquared) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#project(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Interval project(Vector2 axis, Transform transform) {
		// if the transform is not null then transform the center
		Vector2 center = transform.getTransformed(this.center);
		// project the center onto the given axis
		double c = center.dot(axis);
		// the interval is defined by the radius
		return new Interval(c - this.radius, c + this.radius);
	}
	
	/**
	 * Returns the feature farthest in the given direction.
	 * <p>
	 * For a {@link Circle} this is always a point.
	 * @param n the direction
	 * @param transform the local to world space {@link Transform} of this {@link Convex} {@link Shape}
	 * @return {@link Vertex}
	 */
	@Override
	public Vertex getFarthestFeature(Vector2 n, Transform transform) {
		// obtain the farthest point along the given vector
		Vector2 farthest = this.getFarthestPoint(n, transform);
		// for a circle the farthest feature along a vector will always be a vertex
		return new Vertex(farthest);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFarthestPoint(org.dyn4j.game2d.geometry.Vector, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 n, Transform transform) {
		// make sure the axis is normalized
		Vector2 nAxis = n.getNormalized();
		// get the transformed center
		Vector2 center = transform.getTransformed(this.center);
		// add the radius along the vector to the center to get the farthest point
		center.add(this.radius * nAxis.x, this.radius * nAxis.y);
		// return the new point
		return center;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getAxes(java.util.List, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		// a circle has infinite separating axes and zero voronoi regions
		// therefore we return null
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Convex#getFoci(org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		Vector2[] foci = new Vector2[1];
		// a circle only has one focus
		foci[0] = transform.getTransformed(this.center);
		return foci;
	}
	
	/**
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link Circle} and the given density.
	 * <pre>
	 * m = d * &pi; * r<sup>2</sup>
	 * I = m * r<sup>2</sup> / 2
	 * </pre>
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link Circle}
	 */
	@Override
	public Mass createMass(double density) {
		// get the radius and density
		double radius = this.radius;
		// compute the mass
		double mass = density * Math.PI * radius * radius;
		// compute the inertia tensor
		double inertia = mass * radius * radius * 0.5;
		// use the center supplied to the circle
		return new Mass(this.center, mass, inertia);
	}
}
