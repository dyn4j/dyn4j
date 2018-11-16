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
 * Implementation of a regular polygon {@link Convex} {@link Shape}.
 * Regular polygons can already be constructed with the Geometry class but we can exploit the 
 * properties of regular polygons to create a more efficient implementation.
 * <p>
 * The big difference with the existing polygon class is that for a regular polygon with N vertices
 * the methods getFarthestFeature, getFarthestPoint and createAABB can be implemented (as shown below)
 * in O(1) time instead of the existing O(N) of the polygon class.
 * We also have a much easier and faster way of calculating the mass.
 * The benefit is apparent with regular polygons with many vertices.
 * Also there is no need to store the normals array, saving half the memory.
 * (At the moment this comes at the cost of not having getAxes implemented)
 * <p>
 * It should be noted that for small polygons this will probably perform slightly worse.
 * <p>
 * A {@link RegularPolygon} must have at least 3 vertices.
 * @author Manolis Tsamis
 * @version 3.3.0
 * @since 3.3.0
 */
public class RegularPolygon extends Polygon implements Convex, Wound, Shape, Transformable, DataContainer {
	
	/** The number of vertices this regular has */
	protected final int count;

	/** The inverse of the angular increment */
	protected final double invAngle;
	
	/** Rotation needed for computations (in radians)
	 *  Note that this is used in a strange way to cancel out the rotation
	 *  from the rotate(...) methods. Also contains an initial rotation constant (see constructor)*/
	protected double rotation;
	
	/**
	 * Validated constructor.
	 * <p>
	 * Creates a new {@link RegularPolygon} with the given vertice count and angle.
	 * @param count the number of vertices for this regular polygon
	 * @param radius the radius of the circle in which this polygon is insribed
	 */
	private RegularPolygon(boolean valid, int count, double radius) {
		super(new Vector2(), radius, createRegularPolygonVertices(count, radius), null);
		
		// This is the needed inital offset so that this polygon is aligned
		// the same as the existing polygon creation of the Geometry class
		// See getFarthestPoint
		this.rotation = Geometry.TWO_PI + (Geometry.TWO_PI / count) / 2;
		
		this.count = count;
		this.invAngle = count / Geometry.TWO_PI;
	}
	
	private static Vector2[] createRegularPolygonVertices(int count, double radius) {
		// NOTE: taken from Geometry.createPolygonalCircle
		// compute the angular increment
		final double pin = Geometry.TWO_PI / count;
		// make sure the resulting output is an even number of vertices
		final Vector2[] vertices = new Vector2[count];

		final double c = Math.cos(pin);
		final double s = Math.sin(pin);
		
		double x = radius;
		double y = 0;
		
		for(int i = 0; i < count; i++) {
			vertices[i] = new Vector2(x, y);

			//apply the rotation matrix
			double t = x;
			x = c * t - s * y;
			y = s * t + c * y;
		}
		
		return vertices;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a new {@link RegularPolygon} with the given vertice count and angle.
	 * @param count the number of vertices for this regular polygon
	 * @param radius the radius of the circle in which this polygon is insribed
	 */
	public RegularPolygon(int count, double radius) {
		this(validate(count, radius), count, radius);
	}
	
	/**
	 * Validates the constructor input returning true if valid or throwing an exception if invalid.
	 * @param count the number of vertices
	 * @param radius the radius of the regular polygon to be made
	 * @return boolean true
	 * @throws IllegalArgumentException if radius is less than or equal to zero or count is less than 3
	 */
	private static final boolean validate(int count, double radius) {
		if (count < 3) throw new IllegalArgumentException(Messages.getString("geometry.circleInvalidCount"));
		if (radius <= 0.0) throw new IllegalArgumentException(Messages.getString("geometry.circleInvalidRadius"));
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RegularPolygon[").append(super.toString())
			.append("|Count=").append(count)
			.append("|Radius=").append(radius)
			.append("|Vertices={");
		for (int i = 0; i < this.vertices.length; i++) {  
			if (i != 0) sb.append(",");
			sb.append(this.vertices[i]);
		}
		sb.append("}")
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		// NOTE: copied from Polygon.rotate and AbstractShape.rotate
		// only rotate the center if the point about which
		// we are rotating is not the center
		if (!this.center.equals(x, y)) {
			this.center.rotate(theta, x, y);
		}
		
		// omit the normals
		int size = this.vertices.length;
		for (int i = 0; i < size; i++) {
			this.vertices[i].rotate(theta, x, y);
		}
		
		//cancel out the rotation for computations
		rotation -= theta;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public EdgeFeature getFarthestFeature(Vector2 vector, Transform transform) {
		// transform the normal into local space
		Vector2 localn = transform.getInverseTransformedR(vector);
		
		// See explanation in getFarthestPoint
		double rot = Math.atan2(localn.y, localn.x) + rotation;
		double select = rot * invAngle;
		int fmax = (int) (select);
		// this serves to find on which side this point lies
		// if fmax == fother then the edge needed is the left of the max
		// else if fmax < fother the right one is needed (fmax == fother - 1)
		int fother = (int) (select + 0.5);
		int maxIndex = fmax % count;
		
		Vector2 maximum = transform.getTransformed(this.vertices[maxIndex]);
		PointFeature vm = new PointFeature(maximum, maxIndex);
		
		if (fmax < fother) {
			int index1 = (fother) % count;
			Vector2 right = transform.getTransformed(this.vertices[index1]);
			PointFeature vr = new PointFeature(right, index1);
			
			// make sure the edge is the right winding
			return new EdgeFeature(vm, vr, vm, maximum.to(right), maxIndex + 1);
		} else {
			int index1 = (fmax - 1) % count;
			Vector2 left = transform.getTransformed(this.vertices[index1]);
			PointFeature vl = new PointFeature(left, index1);
			
			// make sure the edge is the right winding
			return new EdgeFeature(vl, vm, vm, left.to(maximum), maxIndex);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestPoint(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2 getFarthestPoint(Vector2 vector, Transform transform) {
		// transform the normal into local space
		Vector2 localn = transform.getInverseTransformedR(vector);
		
		// We will be choosing one of the vertices based on the angle of the vector
		// since all the angles are the same
		// The rotation added is 2*pi in order to handle negative rotations plus
		// (Geometry.TWO_PI / count) / 2 to get the closest vertice (rounding)
		// and also cancels out any rotation applied from the rotate methods
		double rot = Math.atan2(localn.y, localn.x) + rotation;
		// inverse of angles
		double select = rot * invAngle;
		// cast to a specific index
		// mod count because we added 2*pi etc
		int maxIndex = ((int) (select)) % count;
		
		//this is the needed vertice
		localn = transform.getTransformed(this.vertices[maxIndex]);
		
		return localn;
	}

	/**	
	 * Creates a {@link Mass} object using the geometric properties of
	 * this {@link RegularPolygon} and the given density.
	 * <p>
	 * Finding the area of a {@link Polygon} can be done by using the following formula:
	 * <p style="white-space: pre;">
	 * r<sup>2</sup> * n * sin(2&pi / n) / 2
	 * </p>
	 * Finding the inertia tensor can by done by using the following equation:
	 * <p style="white-space: pre;">
	 * (1 + 3cot<sup>2</sup>(&pi / n)) * (M * side<sup>2</sup>) / 24
	 * </p>
	 * Where the mass is computed by:
	 * <p style="white-space: pre;"> d * area</p>
	 * @param density the density in kg/m<sup>2</sup>
	 * @return {@link Mass} the {@link Mass} of this {@link RegularPolygon}
	 */
	@Override
	public Mass createMass(double density) {
		System.out.println(super.createMass(density).getCenter());
		int n = this.vertices.length;
		
		// get the average center
		Vector2 ac = new Vector2();
		for (int i = 0; i < n; i++) {
			ac.add(this.vertices[i]);
		}
		
		ac.multiply(1.0 / n);
		
		double area = (radius * radius * count * Math.sin(Geometry.TWO_PI / count)) / 2;
		double m = density * area;
		
		double sin = Math.sin(Math.PI / count);
		double cos = Math.cos(Math.PI / count);
		
		double side = 2 * radius * sin;
		double cot = cos / sin;
		double I = (1 + 3 * cot * cot) * m * side * side / 24.0;
		
		return new Mass(ac, m, I);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#createAABB(org.dyn4j.geometry.Transform)
	 */
	@Override
	public AABB createAABB(Transform transform) {
		Vector2 center = transform.getTransformed(this.center);
		// return a new aabb
		return new AABB(center, this.radius);
	}
	
}
