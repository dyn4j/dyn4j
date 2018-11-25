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

import java.util.Iterator;

import org.dyn4j.DataContainer;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
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
 * 
 * This implementation uses on-demand normal calculation for memory efficiency reasons.
 * Because in various scenarios the normals won't be needed (specifically if SAT is <b>not</not>
 * used as a {@link NarrowphaseDetector} and the user does not ask for them) we will use half the
 * memory without them, which is good for regular polygons with high vertex count.
 * This comes at no performance penalty overall.
 * <p>
 * 
 * It should be noted that for small polygons this will probably perform slightly worse.
 * <p>
 * A {@link RegularPolygon} must have at least 3 vertices.
 * @author Manolis Tsamis
 * @version 3.3.0
 * @since 3.3.0
 */
public class RegularPolygon extends Polygon implements Convex, Wound, Shape, Transformable, DataContainer {
	
	/** Rotation needed for computations (in radians)
	 *  Contains an initial rotation constant (see constructor)*/
	protected final double initialRotationConstant;
	
	/** The local rotation in radians */
	protected double rotation;
	
	/** The inverse regular polygon angle increment in radians. Used for normal calculation */	
	protected final double invPin;
	
	/** The regular polygon normals 
	 *  Note that we're not using the normals array from the Polygon class
	 *  becuase we need those to be mutable
	 */
	protected Vector2[] normalsCache;
	
	/**
	 * Validated constructor.
	 * <p>
	 * Creates a new {@link RegularPolygon} with the given vertice count and angle.
	 * @param count the number of vertices for this regular polygon
	 * @param radius the radius of the circle in which this polygon is insribed
	 */
	private RegularPolygon(boolean valid, int count, double radius) {
		super(new Vector2(), radius, createRegularPolygonVertices(count, radius), null);
		
		this.normalsCache = null;
		this.invPin = count / Geometry.TWO_PI;
		
		// This is the needed inital offset so that this polygon is aligned
		// the same as the existing polygon creation of the Geometry class
		// See getFarthestPoint
		this.initialRotationConstant = Geometry.TWO_PI + (Geometry.TWO_PI / count) / 2;
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
			.append("|Count=").append(vertices.length)
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
	
	/**
	 * Check whether the the nomralsCache array has been filled with the normals.
	 * Used internally and possibly by subclasses.
	 * 
	 * @return true iff the normals are computed
	 */
	protected boolean normalsExist() {
		return this.normalsCache != null;
	}
	
	/**
	 * If the normals have not yet been computed, do it now.
	 * Used internally and possibly by subclasses.
	 */
	protected void ensureNormalsExist() {
		if (!normalsExist()) {
			int size = this.vertices.length;
			
			//calculate the normals at the current rotation
			//this happens only when the normals are needed somewhere
			//probably only when SAT is used as narrowphase
			this.normalsCache = new Vector2[size];
			double pin = Geometry.TWO_PI / size;
			
			double c = Math.cos(pin);
			double s = Math.sin(pin);
			
			double x, y;
			
			//initial angles, size and rotation aware
			if (size % 2 == 0) {
				x = Math.cos(rotation + Math.PI + pin * 0.5);
				y = Math.sin(rotation + Math.PI + pin * 0.5);	
			} else {
				x = Math.cos(rotation + Math.PI);
				y = Math.sin(rotation + Math.PI);
			}
			
			for (int i=0;i<size;i++) {
				normalsCache[i] = new Vector2(x, y);
				
				//apply the rotation matrix
				double t = x;
				x = c * t - s * y;
				y = s * t + c * y;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getNormals()
	 */
	@Override
	public Vector2[] getNormals() {
		ensureNormalsExist();
		
		return this.normalsCache;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getAxes(java.util.List, org.dyn4j.geometry.Transform)
	 */
	@Override
	public Vector2[] getAxes(Vector2[] foci, Transform transform) {
		ensureNormalsExist();
		
		// get the size of the foci list
		int fociSize = foci != null ? foci.length : 0;
		// get the number of vertices this polygon has
		int size = this.vertices.length;
		// the axes of a polygon are created from the normal of the edges
		// plus the closest point to each focus
		Vector2[] axes = new Vector2[size + fociSize];
		int n = 0;
		
		// loop over the edge normals and put them into world space
		for (int i = 0; i < size; i++) {
			// create references to the current points
			Vector2 v = this.normalsCache[i];
			// transform it into world space and add it to the list
			axes[n++] = transform.getTransformedR(v);
		}
		
		// loop over the focal points and find the closest
		// points on the polygon to the focal points
		for (int i = 0; i < fociSize; i++) {
			// get the current focus
			Vector2 f = foci[i];
			// create a place for the closest point
			Vector2 closest = transform.getTransformed(this.vertices[0]);
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
	 * Not applicable to this shape. Always returns null.
	 * @return null
	 */
	@Override
	public Vector2[] getFoci(Transform transform) {
		return null;
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
		
		if (normalsExist()) {
			int size = this.vertices.length;
			for (int i = 0; i < size; i++) {
				this.normalsCache[i].rotate(theta);
				this.vertices[i].rotate(theta, x, y);
			}
		} else {
			// omit the normals
			int size = this.vertices.length;
			for (int i = 0; i < size; i++) {
				this.vertices[i].rotate(theta, x, y);
			}	
		}
		
		//keep track of local rotation
		this.rotation += theta;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Convex#getFarthestFeature(org.dyn4j.geometry.Vector2, org.dyn4j.geometry.Transform)
	 */
	@Override
	public EdgeFeature getFarthestFeature(Vector2 vector, Transform transform) {
		// transform the normal into local space
		Vector2 localn = transform.getInverseTransformedR(vector);
	
		int size = this.vertices.length;
			
		// See explanation in getFarthestPoint
		double rot = Math.atan2(localn.y, localn.x) + (this.initialRotationConstant - this.rotation);
		double select = rot * invPin;
		int fmax = (int) (select);
		// this serves to find on which side this point lies
		// if fmax == fother then the edge needed is the left of the max
		// else if fmax < fother the right one is needed (fmax == fother - 1)
		int fother = (int) (select + 0.5);
		int maxIndex = fmax % size;
		
		Vector2 maximum = transform.getTransformed(this.vertices[maxIndex]);
		PointFeature vm = new PointFeature(maximum, maxIndex);
		
		if (fmax < fother) {
			int index1 = (fother) % size;
			Vector2 right = transform.getTransformed(this.vertices[index1]);
			PointFeature vr = new PointFeature(right, index1);
			
			// make sure the edge is the right winding
			return new EdgeFeature(vm, vr, vm, maximum.to(right), maxIndex + 1);
		} else {
			int index1 = (fmax - 1) % size;
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
		double rot = Math.atan2(localn.y, localn.x) + (this.initialRotationConstant - this.rotation);
		// inverse of angles
		double select = rot * invPin;
		// cast to a specific index
		// mod count because we added 2*pi etc
		int maxIndex = ((int) (select)) % this.vertices.length;
		
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
		int n = this.vertices.length;
		
		// get the average center
		Vector2 ac = new Vector2();
		for (int i = 0; i < n; i++) {
			ac.add(this.vertices[i]);
		}
		
		ac.multiply(1.0 / n);
		
		double area = (radius * radius * n * Math.sin(Geometry.TWO_PI / n)) / 2;
		double m = density * area;
		
		double sin = Math.sin(Math.PI / n);
		double cos = Math.cos(Math.PI / n);
		
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Wound#getNormalIterator()
	 */
	@Override
	public Iterator<Vector2> getNormalIterator() {
		ensureNormalsExist();
		
		return new WoundIterator(this.normalsCache);
	}
	
}
