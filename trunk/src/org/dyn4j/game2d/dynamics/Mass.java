/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.dynamics;

import java.util.List;

import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents {@link Mass} data for a {@link Body}.
 * <p>
 * Stores the center of mass, area, mass, and inertia tensor.
 * @author William Bittle
 */
public class Mass {
	/**
	 * Enumeration for special mass types.
	 * @author William Bittle
	 */
	public static enum Type {
		/** Indicates a normal mass */
		NORMAL,
		/** Indicates that the mass is infinite */
		INFINITE,
		/** Indicates that the mass's rotation should not change */
		FIXED_ROTATION,
		/** Indicates that the mass's translation should not change */
		FIXED_TRANSLATION
	}
	
	/** The default density in kg/m<sup>2</sup> */
	public static final double DEFAULT_DENSITY = 1.0;
	
	/** The center of mass */
	protected Vector c;
	
	/** The mass in kg */
	protected double m;
	
	/** The inertia tensor in kg &middot; m<sup>2</sup> */
	protected double I;
	
	/** The inverse mass */
	protected double invM;
		
	/** The inverse inertia tensor */
	protected double invI;
	
	/**
	 * Full Constructor.
	 * @param c center of {@link Mass} in local coordinates
	 * @param m mass in kg
	 * @param I inertia tensor in kg &middot; m<sup>2</sup>
	 */
	protected Mass(Vector c, double m, double I) {
		this.c = c;
		this.m = m;
		this.invM = 1.0 / m;
		this.I = I;
		this.invI = 1.0 / I;
	}
	
	/**
	 * Infinite mass constructor.
	 * @param c center of {@link Mass} in local coordinates
	 */
	protected Mass(Vector c) {
		this.c = c;
		this.m = 0.0;
		this.I = 0.0;
		this.invM = 0.0;
		this.invI = 0.0;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * Performs a deep copy.
	 * @param mass the {@link Mass} to copy
	 */
	protected Mass(Mass mass) {
		super();
		this.c = mass.c.copy();
		this.m = mass.m;
		this.I = mass.I;
		this.invM = mass.invM;
		this.invI = mass.invI;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MASS[")
		.append(c).append("|")
		.append(m).append("|")
		.append(I).append("]");
		return sb.toString();
	}
	
	/**
	 * Creates a deep copy of the given {@link Mass}.
	 * @param mass the {@link Mass} to copy
	 * @return {@link Mass} the copy
	 */
	public static Mass create(Mass mass) {
		return new Mass(mass);
	}
	
	/**
	 * Creates a {@link Mass} object for the given center
	 * of mass, mass, and inertia tensor.
	 * @param c the center of mass
	 * @param m the mass in kg; must be greater than zero
	 * @param I the inertia tensor kg &middot; m<sup>2</sup>; must be greater than zero
	 * @return {@link Mass} the mass object
	 */
	public static Mass create(Vector c, double m, double I) {
		// verify the passed in values
		if (c == null) throw new IllegalArgumentException("The center point cannot be null.");
		if (m <= 0.0) throw new IllegalArgumentException("The mass must be greater than zero.");
		if (I <= 0.0) throw new IllegalArgumentException("The inertia tensor must be greater than zero.");
		// create the mass if validation passed
		return new Mass(c, m, I);
	}
	
	/**
	 * Creates a {@link Mass} object for the given {@link Circle}
	 * using the default density.
	 * @param c the {@link Circle}
	 * @return {@link Mass}
	 * @see #create(Circle, double)
	 */
	public static Mass create(Circle c) {
		return Mass.create(c, Mass.DEFAULT_DENSITY);
	}
	
	/**
	 * Creates a {@link Mass} object for the given {@link Circle}.
	 * <p>
	 * Supplying a zero density creates an infinite {@link Mass}.
	 * <pre>
	 * m = d * &pi; * r<sup>2</sup>
	 * I = m * r<sup>2</sup> / 2
	 * </pre>
	 * @param c the {@link Circle}
	 * @param d the density in kg/m<sup>2</sup>; must be greater than zero
	 * @return {@link Mass}
	 */
	public static Mass create(Circle c, double d) {
		// check the density input
		if (d <= 0.0) throw new IllegalArgumentException("The density cannot be negative.");
		// get the radius
		double r = c.getRadius();
		// compute the mass
		double m = d * Math.PI * r * r;
		// compute the inertia tensor
		double I = m * r * r / 2.0;
		// use the center supplied to the circle
		return new Mass(c.getCenter().copy(), m, I);
	}
	
	/**
	 * Creates a {@link Mass} object for the given {@link Polygon}
	 * using the default density.
	 * @param p the {@link Polygon}
	 * @return {@link Mass}
	 */
	public static Mass create(Polygon p) {
		return Mass.create(p, Mass.DEFAULT_DENSITY);
	}
	
	/**
	 * Creates a {@link Mass} object for the given {@link Polygon}.
	 * <p>
	 * Supplying a zero density creates an infinite {@link Mass}.
	 * <p>
	 * A {@link Polygon}'s centroid must be computed by the area weighted method since the
	 * average method can be bias to one side if there are more points on that one
	 * side than another.
	 * <p>
	 * Finding the area of a {@link Polygon} can be done by using the following
	 * summation:
	 * <pre>
	 * 0.5 * &sum;(x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)
	 * </pre>
	 * Finding the area weighted centroid can be done by using the following
	 * summation:
	 * <pre>
	 * 1 / (6 * A) * &sum;(p<sub>i</sub> + p<sub>i + 1</sub>) * (x<sub>i</sub> * y<sub>i + 1</sub> - x<sub>i + 1</sub> * y<sub>i</sub>)
	 * </pre>
	 * Finding the inertia tensor can by done by using the following equation:
	 * <pre>
	 *          &sum;(p<sub>i + 1</sub> x p<sub>i</sub>) * (p<sub>i</sub><sup>2</sup> + p<sub>i</sub> &middot; p<sub>i + 1</sub> + p<sub>i + 1</sub><sup>2</sup>)
	 * m / 6 * -------------------------------------------
	 *                        &sum;(p<sub>i + 1</sub> x p<sub>i</sub>)
	 * </pre>
	 * Where the mass is computed by:
	 * <pre>
	 * d * area
	 * </pre>
	 * @param p the {@link Polygon}
	 * @param d the density in kg/m<sup>2</sup>; must be greater than zero
	 * @return {@link Mass}
	 */
	public static Mass create(Polygon p, double d) {
		// check the density input
		if (d <= 0.0) throw new IllegalArgumentException("The density cannot be negative.");
		// can't use normal centroid calculation since it will be weighted towards sides
		// that have larger distribution of points.
		Vector center = new Vector();
		double area = 0.0;
		double I = 0.0;
		// get the vertices
		Vector[] verts = p.getVertices();
		int n = verts.length;
		// calculate inverse three once
		double inv3 = 1.0 / 3.0;
		// loop through the vertices
		for (int i = 0; i < n; i++) {
			// get two verticies
			Vector p1 = verts[i];
			Vector p2 = i + 1 < n ? verts[i + 1] : verts[0];
			// perform the cross product (yi * x(i+1) - y(i+1) * xi)
			double D = p1.cross(p2);
			// multiply by half
			double triangleArea = 0.5 * D;
			// add it to the total area
			area += triangleArea;

			// area weighted centroid
			// (p1 + p2) * (D / 6)
			// = (x1 + x2) * (yi * x(i+1) - y(i+1) * xi) / 6
			// we will divide by the total area later
			center.add(p1.sum(p2).multiply(inv3).multiply(triangleArea));

			// (yi * x(i+1) - y(i+1) * xi) * (p2^2 + p2 . p1 + p1^2)
			I += triangleArea * (p2.dot(p2) + p2.dot(p1) + p1.dot(p1));
			// we will do the m / 6A = (d / 6) when we have the area summed up
		}
		// compute the mass
		double m = d * area;
		// finish the centroid calculation by dividing by the total area
		center.divide(area);
		// finish the inertia tensor by dividing by the total area and multiplying by d / 6
		I *= (d / 6);
		return new Mass(center, m, I);
	}
	
	/**
	 * Creates a {@link Mass} object for the given {@link Rectangle}
	 * using the default density.
	 * @param r the {@link Rectangle}
	 * @return {@link Mass}
	 */
	public static Mass create(Rectangle r) {
		return Mass.create(r, Mass.DEFAULT_DENSITY);
	}
	
	/**
	 * Creates a {@link Mass} object for the given {@link Rectangle}.
	 * <p>
	 * Supplying a zero density creates an infinite {@link Mass}.
	 * <pre>
	 * m = d * h * w
	 * I = m * (h<sup>2</sup> + w<sup>2</sup>) / 12
	 * </pre>
	 * @param r the {@link Rectangle}
	 * @param d the density in kg/m<sup>2</sup>; must be greater than zero
	 * @return {@link Mass}
	 */
	public static Mass create(Rectangle r, double d) {
		// check the density input
		if (d <= 0.0) throw new IllegalArgumentException("The density cannot be negative.");
		double h = r.getHeight();
		double w = r.getWidth();
		// compute the mass
		double m = d * h * w;
		// compute the inertia tensor
		double I = m * (h * h + w * w) / 12.0;
		// since we know that a rectangle has only four points that are
		// evenly distributed we can feel safe using the averaging method 
		// for the centroid
		return new Mass(r.getCenter().copy(), m, I);
	}
	
	/**
	 * Creates a {@link Mass} object for the given line {@link Segment}
	 * using the default density.
	 * @param s the line {@link Segment}
	 * @return {@link Mass}
	 */
	public static Mass create(Segment s) {
		return Mass.create(s, Mass.DEFAULT_DENSITY);
	}
	
	/**
	 * Creates a {@link Mass} object for the given line {@link Segment}.
	 * <p>
	 * Supplying a zero density creates an infinite {@link Mass}.
	 * <pre>
	 * m = d * length
	 * I = l<sup>2</sup> * m / 12
	 * </pre>
	 * @param s the line {@link Segment}
	 * @param d the density in kg/m<sup>2</sup>; must be greater than zero
	 * @return {@link Mass}
	 */
	public static Mass create(Segment s, double d) {
		// check the density input
		if (d <= 0.0) throw new IllegalArgumentException("The density cannot be negative.");
		double length = s.getLength();
		// compute the mass
		double m = d * length;
		// compute the inertia tensor
		double I = 1.0 / 12.0 * length * length * m;
		// since we know that a line segment has only two points we can
		// feel safe using the averaging method for the centroid
		return new Mass(s.getCenter().copy(), m, I);
	}
	
	/**
	 * Creates a {@link Mass} object from the given array of masses.
	 * <p>
	 * Uses the Parallel Axis Theorem to obtain the inertia tensor about
	 * the center of all the given masses:
	 * <pre>
	 * I<sub>dis</sub> = I<sub>cm</sub> + mr<sup>2</sup>
	 * I<sub>total</sub> = &sum; I<sub>dis</sub>
	 * </pre>
	 * The center for the resulting mass will be a mass weighted center.
	 * @param masses the list of {@link Mass} objects to combine
	 * @return {@link Mass} the combined {@link Mass}
	 */
	public static Mass create(List<Mass> masses) {
		Vector c = new Vector();
		double m = 0.0;
		double I = 0.0;
		// get the length of the masses array
		int size = masses.size();
		// loop over the masses
		for (int i = 0; i < size; i++) {
			Mass mass = masses.get(i);
			// add the center's up (weighting them by their respective mass)
			c.add(mass.c.product(mass.m));
			// sum the masses
			m += mass.m;
		}
		// compute the center by dividing by the total mass
		c.divide(m);
		// after obtaining the new center of mass we need
		// to compute the interia tensor about the center
		// using the parallel axis theorem:
		// Idis = Icm + mr^2 where r is the perpendicular distance
		// between the two parallel axes
		for (int i = 0; i < size; i++) {
			// get the mass 
			Mass mass = masses.get(i);
			// compute the distance from the new center to
			// the current mass's center
			double d2 = mass.c.distanceSquared(c);
			// compute Idis
			double Idis = mass.I + mass.m * d2;
			// add it to the sum
			I += Idis;
		}
		// finally create the mass
		return new Mass(c, m, I);
	}
	
	/**
	 * Returns true if this {@link Mass} object has infinite mass.
	 * @return boolean
	 */
	public boolean isInfinite() {
		return this.m == 0.0 && this.I == 0.0;
	}
	
	/**
	 * Returns the center of mass.
	 * @return {@link Vector}
	 */
	public Vector getCenter() {
		return c;
	}
	
	/**
	 * Returns the mass.
	 * @return double
	 */
	public double getMass() {
		return m;
	}
	
	/**
	 * Returns the inertia tensor.
	 * @return double
	 */
	public double getInertia() {
		return I;
	}
	
	/**
	 * Returns the inverse mass.
	 * @return double
	 */
	public double getInverseMass() {
		return invM;
	}
	
	/**
	 * Returns the inverse inertia tensor.
	 * @return double
	 */
	public double getInverseInertia() {
		return invI;
	}
}

