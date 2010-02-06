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
package org.dyn4j.game2d.geometry;

/**
 * This class represents a {@link Vector} in 2D space.
 * <p>
 * The operations {@link Vector#setMagnitude(double)}, {@link Vector#getNormalized()},
 * {@link Vector#project(Vector)}, and {@link Vector#normalize()} require the {@link Vector}
 * to be non-zero in length.
 * <p>
 * Some methods that modify the vector will also return the vector.  This is to facilitate 
 * operations like:
 * <pre>
 * Vector a = new Vector();
 * a.zero().add(1, 2).multiply(2);
 * </pre>
 * This can decrease the number of temporary vectors.
 * @author William Bittle
 */
public class Vector {
	/** The magnitude of the x component of this {@link Vector} */
	public double x;
	
	/** The magnitude of the y component of this {@link Vector} */
	public double y;
	
	/** Default constructor. */
	public Vector() {}

	/**
	 * Copy constructor.
	 * @param vector the {@link Vector} to copy from
	 */
	public Vector(Vector vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	/**
	 * Optional constructor.
	 * @param x the x component
	 * @param y the y component
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a {@link Vector} from the first point to the second point.
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 */
	public Vector(double x1, double y1, double x2, double y2) {
		this.x = x2 - x1;
		this.y = y2 - y1;
	}
	
	/**
	 * Creates a {@link Vector} from the first point to the second point.
	 * @param p1 the first point
	 * @param p2 the second point
	 */
	public Vector(Vector p1, Vector p2) {
		this.x = p2.x - p1.x;
		this.y = p2.y - p1.y;
	}

	/**
	 * Returns a new {@link Vector} given the magnitude and direction.
	 * @param magnitude the magnitude of the {@link Vector}
	 * @param direction the direction of the {@link Vector} in radians
	 * @return {@link Vector}
	 */
	public static Vector create(double magnitude, double direction) {
		double x = magnitude * Math.cos(direction);
		double y = magnitude * Math.sin(direction);
		return new Vector(x, y);
	}
	
	/**
	 * Returns a copy of this {@link Vector}.
	 * @return {@link Vector}
	 */
	public Vector copy() {
		return new Vector(this.x, this.y);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param x the x coordinate of the point
	 * @param y the y coordiante of the point
	 * @return double
	 */
	public double distance(double x, double y) {
		return Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param point the point
	 * @return double
	 */
	public double distance(Vector point) {
		return Math.sqrt((this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y));
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param x the x coordinate of the point
	 * @param y the y coordiante of the point
	 * @return double
	 */
	public double distanceSquared(double x, double y) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y);
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param point the point
	 * @return double
	 */
	public double distanceSquared(Vector point) {
		return (this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y);
	}
	
	/**
	 * The triple product of {@link Vector}s is defined as:
	 * <pre>
	 * a x (b x c)
	 * </pre>
	 * However, this method performs the following triple product:
	 * <pre>
	 * (a x b) x c
	 * </pre>
	 * this can be simplified to:
	 * <pre>
	 * -a * (b &middot; c) + b * (a &middot; c)
	 * </pre>
	 * or:
	 * <pre>
	 * b * (a &middot; c) - a * (b &middot; c)
	 * </pre>
	 * @param a the a {@link Vector} in the above equation
	 * @param b the b {@link Vector} in the above equation
	 * @param c the c {@link Vector} in the above equation
	 * @return {@link Vector}
	 */
	public static Vector tripleProduct(Vector a, Vector b, Vector c) {
		// expanded version of above formula
		Vector r = new Vector();
		// perform a.dot(c)
		double ac = a.x * c.x + a.y * c.y;
		// perform b.dot(c)
		double bc = b.x * c.x + b.y * c.y;
		// perform b * a.dot(c) - a * b.dot(c)
		r.x = b.x * ac - a.x * bc;
		r.y = b.y * ac - a.y * bc;
		return r;
	}

	/**
	 * Returns true if the x and y components of this {@link Vector}
	 * are the same as the given {@link Vector}.
	 * @param vector the {@link Vector} to compare to
	 * @return boolean
	 */
	public boolean equals(Vector vector) {
		if (this == vector) {
			return true;
		} else {
			return this.x == vector.x && this.y == vector.y;
		}
	}
	
	/**
	 * Returns true if the x and y components of this {@link Vector}
	 * are the same as the given x and y components.
	 * @param x the x coordinate of the {@link Vector} to compare to
	 * @param y the y coordinate of the {@link Vector} to compare to
	 * @return boolean
	 */
	public boolean equals(double x, double y) {
		return this.x == x && this.y == y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(")
		  .append(this.x)
		  .append(", ")
		  .append(this.y)
		  .append(")");
		return sb.toString();
	}
	
	/**
	 * Sets this {@link Vector} to the given {@link Vector}.
	 * @param vector the {@link Vector} to set this {@link Vector} to
	 * @return {@link Vector} this vector
	 */
	public Vector set(Vector vector) {
		this.x = vector.x;
		this.y = vector.y;
		return this;
	}
	
	/**
	 * Sets this {@link Vector} to the given {@link Vector}.
	 * @param x the x component of the {@link Vector} to set this {@link Vector} to
	 * @param y the y component of the {@link Vector} to set this {@link Vector} to
	 * @return {@link Vector} this vector
	 */
	public Vector set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Returns the x component of this {@link Vector}.
	 * @return {@link Vector}
	 */
	public Vector getXComponent() {
		return new Vector(this.x, 0.0);
	}
	
	/**
	 * Returns the y component of this {@link Vector}.
	 * @return {@link Vector}
	 */
	public Vector getYComponent() {
		return new Vector(0.0, this.y);
	}
	
	/**
	 * Returns the magnitude of this {@link Vector}.
	 * @return double
	 */
	public double getMagnitude() {
		// the magnitude is just the pathagorean theorem
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * Returns the magnitude of this {@link Vector} squared.
	 * @return double
	 */
	public double getMagnitudeSquared() {
		return this.x * this.x + this.y * this.y;
	}
	
	/**
	 * Sets the magnitude of the {@link Vector}.
	 * <p>
	 * This method requires this {@link Vector} be of non-zero length.
	 * @param magnitude the magnitude
	 */
	public void setMagnitude(double magnitude) {
		// get the magnitude
		double mag = Math.sqrt(this.x * this.x + this.y * this.y);
		// normalize and multiply by the new magnitude
		mag = 1.0 / mag * magnitude;
		this.x *= mag;
		this.y *= mag;
	}
	
	/**
	 * Returns the direction of this {@link Vector}
	 * as an angle in radians.
	 * @return double angle in radians [-pi, pi]
	 */
	public double getDirection() {
		return Math.atan2(this.y, this.x);
	}
	
	/**
	 * Sets the direction of this {@link Vector}.
	 * @param angle angle in radians
	 */
	public void setDirection(double angle) {
		double magnitude = Math.sqrt(this.x * this.x + this.y * this.y);
        this.x = magnitude * Math.cos(angle);
        this.y = magnitude * Math.sin(angle);
	}
	
	/**
	 * Adds the given {@link Vector} to this {@link Vector}.
	 * @param vector the {@link Vector}
	 * @return {@link Vector} this vector
	 */
	public Vector add(Vector vector) {
		this.x += vector.x;
		this.y += vector.y;
		return this;
	}
	
	/**
	 * Adds the given {@link Vector} to this {@link Vector}.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return {@link Vector} this vector
	 */
	public Vector add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Adds this {@link Vector} and the given {@link Vector} returning
	 * a new {@link Vector} containing the result.
	 * @param vector the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector sum(Vector vector) {
		return new Vector(this.x + vector.x, this.y + vector.y);
	}
	
	/**
	 * Adds this {@link Vector} and the given {@link Vector} returning
	 * a new {@link Vector} containing the result.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector sum(double x, double y) {
		return new Vector(this.x + x, this.y + y);
	}
	
	/**
	 * Subtracts the given {@link Vector} from this {@link Vector}.
	 * @param vector the {@link Vector}
	 * @return {@link Vector} this vector
	 */
	public Vector subtract(Vector vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		return this;
	}
	
	/**
	 * Subtracts the given {@link Vector} from this {@link Vector}.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return {@link Vector} this vector
	 */
	public Vector subtract(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	/**
	 * Subtracts the given {@link Vector} from this {@link Vector} returning
	 * a new {@link Vector} containing the result.
	 * @param vector the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector difference(Vector vector) {
		return new Vector(this.x - vector.x, this.y - vector.y);
	}
	
	/**
	 * Subtracts the given {@link Vector} from this {@link Vector} returning
	 * a new {@link Vector} containing the result.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector difference(double x, double y) {
		return new Vector(this.x - x, this.y - y);
	}
	
	/**
	 * Creates a {@link Vector} from this {@link Vector} to the given {@link Vector}.
	 * @param vector the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector to(Vector vector) {
		return new Vector(vector.x - this.x, vector.y - this.y);
	}
	
	/**
	 * Creates a {@link Vector} from this {@link Vector} to the given {@link Vector}.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector to(double x, double y) {
		return new Vector(x - this.x, y - this.y);
	}
		
	/**
	 * Multiplies this {@link Vector} by the given scalar.
	 * @param scalar the scalar
	 * @return {@link Vector} this vector
	 */
	public Vector multiply(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link Vector} by the given scalar returning
	 * a new {@link Vector} containing the result.
	 * @param scalar the scalar
	 * @return {@link Vector}
	 */
	public Vector product(double scalar) {
		return new Vector(this.x * scalar, this.y * scalar);
	}
	
	/**
	 * Divides this {@link Vector} by the given scalar.
	 * @param scalar the scalar
	 * @return {@link Vector} this vector
	 */
	public Vector divide(double scalar) {
		double s = 1.0 / scalar;
		this.x *= s;
		this.y *= s;
		return this;
	}
	
	/**
	 * Divides this {@link Vector} by the given scalar returning
	 * a new {@link Vector} containing the result.
	 * @param scalar the scalar
	 * @return {@link Vector}
	 */
	public Vector quotient(double scalar) {
		double s = 1.0 / scalar;
		return new Vector(this.x * s, this.y * s);
	}
	
	/**
	 * Returns the dot product of the given {@link Vector}
	 * and this {@link Vector}.
	 * @param vector the {@link Vector}
	 * @return double
	 */
	public double dot(Vector vector) {
		return this.x * vector.x + this.y * vector.y;
	}
	
	/**
	 * Returns the dot product of the given {@link Vector}
	 * and this {@link Vector}.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return double
	 */
	public double dot(double x, double y) {
		return this.x * x + this.y * y;
	}
	
	/**
	 * Returns the cross product of the this {@link Vector} and the given {@link Vector}.
	 * @param vector the {@link Vector}
	 * @return double
	 */
	public double cross(Vector vector) {
		return this.x * vector.y - this.y * vector.x;
	}
	
	/**
	 * Returns the cross product of the this {@link Vector} and the given {@link Vector}.
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return double
	 */
	public double cross(double x, double y) {
		return this.x * y - this.y * x;
	}
	
	/**
	 * Returns the cross product of this {@link Vector} and the z value of the right {@link Vector}.
	 * @param z the z component of the {@link Vector}
	 * @return {@link Vector}
	 */
	public Vector cross(double z) {
		return new Vector(-1.0 * this.y * z, this.x * z);
	}
	
	/**
	 * Returns true if the given {@link Vector} is orthogonal (perpendicular)
	 * to this {@link Vector}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param vector the {@link Vector}
	 * @return boolean
	 */
	public boolean isOrthogonal(Vector vector) {
		return (this.x * vector.x + this.y * vector.y) == 0.0 ? true : false;
	}
	
	/**
	 * Returns true if the given {@link Vector} is orthogonal (perpendicular)
	 * to this {@link Vector}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param x the x component of the {@link Vector}
	 * @param y the y component of the {@link Vector}
	 * @return boolean
	 */
	public boolean isOrthogonal(double x, double y) {
		return (this.x * x + this.y * y) == 0.0 ? true : false;
	}
	
	/**
	 * Returns true if this {@link Vector} is the zero {@link Vector}.
	 * @return boolean
	 */
	public boolean isZero() {
		return this.x == 0.0 && this.y == 0.0;
	}

	/** 
	 * Negates this {@link Vector}.
	 * @return {@link Vector} this vector
	 */
	public Vector negate() {
		this.x *= -1.0;
		this.y *= -1.0;
		return this;
	}
	
	/**
	 * Returns a {@link Vector} which is the negative of this {@link Vector}.
	 * @return {@link Vector}
	 */
	public Vector getNegative() {
		return new Vector(-this.x, -this.y);
	}
	
	/** 
	 * Sets the {@link Vector} to the zero {@link Vector}
	 * @return {@link Vector} this vector
	 */
	public Vector zero() {
		this.x = 0.0;
		this.y = 0.0;
		return this;
	}
	
	/**
	 * Rotates about the origin.
	 * @param theta the rotation angle in radians
	 * @return {@link Vector} this vector
	 */
	public Vector rotate(double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		double x = this.x;
		double y = this.y;
		this.x = x * cos - y * sin;
		this.y = x * sin + y * cos;
		return this;
	}
	
	/**
	 * Rotates the {@link Vector} about the given coordinates.
	 * @param theta the rotation angle in radians
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link Vector} this vector
	 */
	public Vector rotate(double theta, double x, double y) {
		this.x -= x;
		this.y -= y;
		this.rotate(theta);
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Rotates the {@link Vector} about the given point.
	 * @param theta the rotation angle in radians
	 * @param point the point to rotate about
	 * @return {@link Vector} this vector
	 */
	public Vector rotate(double theta, Vector point) {
		return this.rotate(theta, point.x, point.y);
	}
	
	/**
	 * Projects this {@link Vector} onto the given {@link Vector}.
	 * <p>
	 * This method requires the length of the given {@link Vector} is not zero.
	 * @param vector the {@link Vector}
	 * @return {@link Vector} the projected {@link Vector}
	 */
	public Vector project(Vector vector) {
		double dotProd = this.dot(vector);
		double denominator = vector.dot(vector);
		if (denominator == 0.0) throw new ArithmeticException();
		denominator = dotProd / denominator;
		return new Vector(denominator * vector.x, denominator * vector.y);		
	}

	/**
	 * Returns the perproduct.
	 * <p>
	 * The perproduct of a {@link Vector} results in two orthogonal {@link Vector}s relative
	 * to the original {@link Vector}.
	 * <p>
	 * This method returns the right hand one.
	 * @return {@link Vector} the right hand orthogonal {@link Vector}
	 */
	public Vector getRightHandOrthogonalVector() {
		return new Vector(-this.y, this.x);
	}
	
	/**
	 * Performs the perproduct and places the result in this {@link Vector}.
	 * @return {@link Vector} this vector
	 * @see #getRightHandOrthogonalVector()
	 */
	public Vector right() {
		double temp = this.x;
		this.x = -this.y;
		this.y = temp;
		return this;
	}
	
	/**
	 * Returns the perproduct.
	 * <p>
	 * The perproduct of a {@link Vector} results in two orthogonal {@link Vector}s relative
	 * to the original {@link Vector}.
	 * <p>
	 * This method returns the left hand one.
	 * @return {@link Vector} the left hand orthogonal {@link Vector}
	 */
	public Vector getLeftHandOrthogonalVector() {
		return new Vector(this.y, -this.x);
	}
	
	/**
	 * Performs the perproduct and places the result in this {@link Vector}.
	 * @return {@link Vector} this vector
	 * @see #getLeftHandOrthogonalVector()
	 */
	public Vector left() {
		double temp = this.x;
		this.x = this.y;
		this.y = -temp;
		return this;
	}

	/**
	 * Returns a unit {@link Vector} of this {@link Vector}.
	 * <p>
	 * This method requires the length of this {@link Vector} is not zero.
	 * @return {@link Vector}
	 */
	public Vector getNormalized() {
		double magnitude = this.getMagnitude();
		if (magnitude == 0.0) return new Vector();
		magnitude = 1.0 / magnitude;
		return new Vector(this.x * magnitude, this.y * magnitude);
	}
	
	/**
	 * Converts this {@link Vector} into a unit {@link Vector} and returns
	 * the magnitude before normalization.
	 * <p>
	 * This method requires the length of this {@link Vector} is not zero.
	 * @return double
	 */
	public double normalize() {
		double magnitude = this.getMagnitude();
		if (magnitude == 0.0) return 0;
		double m = 1.0 / magnitude;
		this.x *= m;
		this.y *= m;
		return magnitude;
	}
	
	/**
	 * Returns the smallest angle between the given {@link Vector}s.
	 * <p>
	 * Returns the angle in radians in the range -pi to pi.
	 * @param vector the {@link Vector}
	 * @return angle in radians [-pi, pi]
	 */
	public double getAngleBetween(Vector vector) {
		return Math.atan2(vector.y, vector.x) - Math.atan2(this.y, this.x);
	}
}
