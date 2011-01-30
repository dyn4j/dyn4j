/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.game2d.Epsilon;

/**
 * This class represents a vector in 2D space.
 * <p>
 * The operations {@link Vector2#setMagnitude(double)}, {@link Vector2#getNormalized()},
 * {@link Vector2#project(Vector2)}, and {@link Vector2#normalize()} require the {@link Vector2}
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
 * @version 2.2.3
 * @since 1.0.0
 */
public class Vector2 {
	/** The magnitude of the x component of this {@link Vector2} */
	public double x;
	
	/** The magnitude of the y component of this {@link Vector2} */
	public double y;
	
	/** Default constructor. */
	public Vector2() {}

	/**
	 * Copy constructor.
	 * @param vector the {@link Vector2} to copy from
	 */
	public Vector2(Vector2 vector) {
		this.x = vector.x;
		this.y = vector.y;
	}
	
	/**
	 * Optional constructor.
	 * @param x the x component
	 * @param y the y component
	 */
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a {@link Vector2} from the first point to the second point.
	 * @param x1 the x coordinate of the first point
	 * @param y1 the y coordinate of the first point
	 * @param x2 the x coordinate of the second point
	 * @param y2 the y coordinate of the second point
	 */
	public Vector2(double x1, double y1, double x2, double y2) {
		this.x = x2 - x1;
		this.y = y2 - y1;
	}
	
	/**
	 * Creates a {@link Vector2} from the first point to the second point.
	 * @param p1 the first point
	 * @param p2 the second point
	 */
	public Vector2(Vector2 p1, Vector2 p2) {
		this.x = p2.x - p1.x;
		this.y = p2.y - p1.y;
	}

	/**
	 * Returns a new {@link Vector2} given the magnitude and direction.
	 * @param magnitude the magnitude of the {@link Vector2}
	 * @param direction the direction of the {@link Vector2} in radians
	 * @return {@link Vector2}
	 */
	public static Vector2 create(double magnitude, double direction) {
		double x = magnitude * Math.cos(direction);
		double y = magnitude * Math.sin(direction);
		return new Vector2(x, y);
	}
	
	/**
	 * Returns a copy of this {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 copy() {
		return new Vector2(this.x, this.y);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return double
	 */
	public double distance(double x, double y) {
		return Math.hypot(x, y);
	}
	
	/**
	 * Returns the distance from this point to the given point.
	 * @param point the point
	 * @return double
	 */
	public double distance(Vector2 point) {
		return Math.hypot(this.x - point.x, this.y - point.y);
	}
	
	/**
	 * Returns the distance from this point to the given point squared.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
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
	public double distanceSquared(Vector2 point) {
		return (this.x - point.x) * (this.x - point.x) + (this.y - point.y) * (this.y - point.y);
	}
	
	/**
	 * The triple product of {@link Vector2}s is defined as:
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
	 * @param a the a {@link Vector2} in the above equation
	 * @param b the b {@link Vector2} in the above equation
	 * @param c the c {@link Vector2} in the above equation
	 * @return {@link Vector2}
	 */
	public static Vector2 tripleProduct(Vector2 a, Vector2 b, Vector2 c) {
		// expanded version of above formula
		Vector2 r = new Vector2();
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
	 * Returns true if the x and y components of this {@link Vector2}
	 * are the same as the given {@link Vector2}.
	 * @param vector the {@link Vector2} to compare to
	 * @return boolean
	 */
	public boolean equals(Vector2 vector) {
		if (vector == null) return false;
		if (this == vector) {
			return true;
		} else {
			return this.x == vector.x && this.y == vector.y;
		}
	}
	
	/**
	 * Returns true if the x and y components of this {@link Vector2}
	 * are the same as the given x and y components.
	 * @param x the x coordinate of the {@link Vector2} to compare to
	 * @param y the y coordinate of the {@link Vector2} to compare to
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
	 * Sets this {@link Vector2} to the given {@link Vector2}.
	 * @param vector the {@link Vector2} to set this {@link Vector2} to
	 * @return {@link Vector2} this vector
	 */
	public Vector2 set(Vector2 vector) {
		this.x = vector.x;
		this.y = vector.y;
		return this;
	}
	
	/**
	 * Sets this {@link Vector2} to the given {@link Vector2}.
	 * @param x the x component of the {@link Vector2} to set this {@link Vector2} to
	 * @param y the y component of the {@link Vector2} to set this {@link Vector2} to
	 * @return {@link Vector2} this vector
	 */
	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * Returns the x component of this {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 getXComponent() {
		return new Vector2(this.x, 0.0);
	}
	
	/**
	 * Returns the y component of this {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 getYComponent() {
		return new Vector2(0.0, this.y);
	}
	
	/**
	 * Returns the magnitude of this {@link Vector2}.
	 * @return double
	 */
	public double getMagnitude() {
		// the magnitude is just the pathagorean theorem
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * Returns the magnitude of this {@link Vector2} squared.
	 * @return double
	 */
	public double getMagnitudeSquared() {
		return this.x * this.x + this.y * this.y;
	}
	
	/**
	 * Sets the magnitude of the {@link Vector2}.
	 * @param magnitude the magnitude
	 * @return {@link Vector2} this vector
	 */
	public Vector2 setMagnitude(double magnitude) {
		// check the given magnitude
		if (Math.abs(magnitude) < Epsilon.E) {
			this.x = 0.0;
			this.y = 0.0;
			return this;
		}
		// is this vector a zero vector?
		if (this.isZero()) {
			return this;
		}
		// get the magnitude
		double mag = Math.sqrt(this.x * this.x + this.y * this.y);
		// normalize and multiply by the new magnitude
		mag = magnitude / mag;
		this.x *= mag;
		this.y *= mag;
		return this;
	}
	
	/**
	 * Returns the direction of this {@link Vector2}
	 * as an angle in radians.
	 * @return double angle in radians [-pi, pi]
	 */
	public double getDirection() {
		return Math.atan2(this.y, this.x);
	}
	
	/**
	 * Sets the direction of this {@link Vector2}.
	 * @param angle angle in radians
	 * @return {@link Vector2} this vector
	 */
	public Vector2 setDirection(double angle) {
		double magnitude = Math.sqrt(this.x * this.x + this.y * this.y);
        this.x = magnitude * Math.cos(angle);
        this.y = magnitude * Math.sin(angle);
        return this;
	}
	
	/**
	 * Adds the given {@link Vector2} to this {@link Vector2}.
	 * @param vector the {@link Vector2}
	 * @return {@link Vector2} this vector
	 */
	public Vector2 add(Vector2 vector) {
		this.x += vector.x;
		this.y += vector.y;
		return this;
	}
	
	/**
	 * Adds the given {@link Vector2} to this {@link Vector2}.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return {@link Vector2} this vector
	 */
	public Vector2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Adds this {@link Vector2} and the given {@link Vector2} returning
	 * a new {@link Vector2} containing the result.
	 * @param vector the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 sum(Vector2 vector) {
		return new Vector2(this.x + vector.x, this.y + vector.y);
	}
	
	/**
	 * Adds this {@link Vector2} and the given {@link Vector2} returning
	 * a new {@link Vector2} containing the result.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 sum(double x, double y) {
		return new Vector2(this.x + x, this.y + y);
	}
	
	/**
	 * Subtracts the given {@link Vector2} from this {@link Vector2}.
	 * @param vector the {@link Vector2}
	 * @return {@link Vector2} this vector
	 */
	public Vector2 subtract(Vector2 vector) {
		this.x -= vector.x;
		this.y -= vector.y;
		return this;
	}
	
	/**
	 * Subtracts the given {@link Vector2} from this {@link Vector2}.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return {@link Vector2} this vector
	 */
	public Vector2 subtract(double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	/**
	 * Subtracts the given {@link Vector2} from this {@link Vector2} returning
	 * a new {@link Vector2} containing the result.
	 * @param vector the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 difference(Vector2 vector) {
		return new Vector2(this.x - vector.x, this.y - vector.y);
	}
	
	/**
	 * Subtracts the given {@link Vector2} from this {@link Vector2} returning
	 * a new {@link Vector2} containing the result.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 difference(double x, double y) {
		return new Vector2(this.x - x, this.y - y);
	}
	
	/**
	 * Creates a {@link Vector2} from this {@link Vector2} to the given {@link Vector2}.
	 * @param vector the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 to(Vector2 vector) {
		return new Vector2(vector.x - this.x, vector.y - this.y);
	}
	
	/**
	 * Creates a {@link Vector2} from this {@link Vector2} to the given {@link Vector2}.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 to(double x, double y) {
		return new Vector2(x - this.x, y - this.y);
	}
		
	/**
	 * Multiplies this {@link Vector2} by the given scalar.
	 * @param scalar the scalar
	 * @return {@link Vector2} this vector
	 */
	public Vector2 multiply(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link Vector2} by the given scalar returning
	 * a new {@link Vector2} containing the result.
	 * @param scalar the scalar
	 * @return {@link Vector2}
	 */
	public Vector2 product(double scalar) {
		return new Vector2(this.x * scalar, this.y * scalar);
	}
	
	/**
	 * Returns the dot product of the given {@link Vector2}
	 * and this {@link Vector2}.
	 * @param vector the {@link Vector2}
	 * @return double
	 */
	public double dot(Vector2 vector) {
		return this.x * vector.x + this.y * vector.y;
	}
	
	/**
	 * Returns the dot product of the given {@link Vector2}
	 * and this {@link Vector2}.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return double
	 */
	public double dot(double x, double y) {
		return this.x * x + this.y * y;
	}
	
	/**
	 * Returns the cross product of the this {@link Vector2} and the given {@link Vector2}.
	 * @param vector the {@link Vector2}
	 * @return double
	 */
	public double cross(Vector2 vector) {
		return this.x * vector.y - this.y * vector.x;
	}
	
	/**
	 * Returns the cross product of the this {@link Vector2} and the given {@link Vector2}.
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return double
	 */
	public double cross(double x, double y) {
		return this.x * y - this.y * x;
	}
	
	/**
	 * Returns the cross product of this {@link Vector2} and the z value of the right {@link Vector2}.
	 * @param z the z component of the {@link Vector2}
	 * @return {@link Vector2}
	 */
	public Vector2 cross(double z) {
		return new Vector2(-1.0 * this.y * z, this.x * z);
	}
	
	/**
	 * Returns true if the given {@link Vector2} is orthogonal (perpendicular)
	 * to this {@link Vector2}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param vector the {@link Vector2}
	 * @return boolean
	 */
	public boolean isOrthogonal(Vector2 vector) {
		return Math.abs(this.x * vector.x + this.y * vector.y) < Epsilon.E ? true : false;
	}
	
	/**
	 * Returns true if the given {@link Vector2} is orthogonal (perpendicular)
	 * to this {@link Vector2}.
	 * <p>
	 * If the dot product of this vector and the given vector is
	 * zero then we know that they are perpendicular
	 * @param x the x component of the {@link Vector2}
	 * @param y the y component of the {@link Vector2}
	 * @return boolean
	 */
	public boolean isOrthogonal(double x, double y) {
		return Math.abs(this.x * x + this.y * y) < Epsilon.E ? true : false;
	}
	
	/**
	 * Returns true if this {@link Vector2} is the zero {@link Vector2}.
	 * @return boolean
	 */
	public boolean isZero() {
		return (this.x * this.x + this.y * this.y) < Epsilon.E;
	}
	
	/** 
	 * Negates this {@link Vector2}.
	 * @return {@link Vector2} this vector
	 */
	public Vector2 negate() {
		this.x *= -1.0;
		this.y *= -1.0;
		return this;
	}
	
	/**
	 * Returns a {@link Vector2} which is the negative of this {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 getNegative() {
		return new Vector2(-this.x, -this.y);
	}
	
	/** 
	 * Sets the {@link Vector2} to the zero {@link Vector2}
	 * @return {@link Vector2} this vector
	 */
	public Vector2 zero() {
		this.x = 0.0;
		this.y = 0.0;
		return this;
	}
	
	/**
	 * Rotates about the origin.
	 * @param theta the rotation angle in radians
	 * @return {@link Vector2} this vector
	 */
	public Vector2 rotate(double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		double x = this.x;
		double y = this.y;
		this.x = x * cos - y * sin;
		this.y = x * sin + y * cos;
		return this;
	}
	
	/**
	 * Rotates the {@link Vector2} about the given coordinates.
	 * @param theta the rotation angle in radians
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 * @return {@link Vector2} this vector
	 */
	public Vector2 rotate(double theta, double x, double y) {
		this.x -= x;
		this.y -= y;
		this.rotate(theta);
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Rotates the {@link Vector2} about the given point.
	 * @param theta the rotation angle in radians
	 * @param point the point to rotate about
	 * @return {@link Vector2} this vector
	 */
	public Vector2 rotate(double theta, Vector2 point) {
		return this.rotate(theta, point.x, point.y);
	}
	
	/**
	 * Projects this {@link Vector2} onto the given {@link Vector2}.
	 * @param vector the {@link Vector2}
	 * @return {@link Vector2} the projected {@link Vector2}
	 */
	public Vector2 project(Vector2 vector) {
		double dotProd = this.dot(vector);
		double denominator = vector.dot(vector);
		if (denominator < Epsilon.E) return new Vector2();
		denominator = dotProd / denominator;
		return new Vector2(denominator * vector.x, denominator * vector.y);		
	}

	/**
	 * Returns the perproduct.
	 * <p>
	 * The perproduct of a {@link Vector2} results in two orthogonal {@link Vector2}s relative
	 * to the original {@link Vector2}.
	 * <p>
	 * This method returns the right hand one.
	 * @return {@link Vector2} the right hand orthogonal {@link Vector2}
	 */
	public Vector2 getRightHandOrthogonalVector() {
		return new Vector2(-this.y, this.x);
	}
	
	/**
	 * Performs the perproduct and places the result in this {@link Vector2}.
	 * @return {@link Vector2} this vector
	 * @see #getRightHandOrthogonalVector()
	 */
	public Vector2 right() {
		double temp = this.x;
		this.x = -this.y;
		this.y = temp;
		return this;
	}
	
	/**
	 * Returns the perproduct.
	 * <p>
	 * The perproduct of a {@link Vector2} results in two orthogonal {@link Vector2}s relative
	 * to the original {@link Vector2}.
	 * <p>
	 * This method returns the left hand one.
	 * @return {@link Vector2} the left hand orthogonal {@link Vector2}
	 */
	public Vector2 getLeftHandOrthogonalVector() {
		return new Vector2(this.y, -this.x);
	}
	
	/**
	 * Performs the perproduct and places the result in this {@link Vector2}.
	 * @return {@link Vector2} this vector
	 * @see #getLeftHandOrthogonalVector()
	 */
	public Vector2 left() {
		double temp = this.x;
		this.x = this.y;
		this.y = -temp;
		return this;
	}

	/**
	 * Returns a unit {@link Vector2} of this {@link Vector2}.
	 * <p>
	 * This method requires the length of this {@link Vector2} is not zero.
	 * @return {@link Vector2}
	 */
	public Vector2 getNormalized() {
		double magnitude = this.getMagnitude();
		if (magnitude < Epsilon.E) return new Vector2();
		magnitude = 1.0 / magnitude;
		return new Vector2(this.x * magnitude, this.y * magnitude);
	}
	
	/**
	 * Converts this {@link Vector2} into a unit {@link Vector2} and returns
	 * the magnitude before normalization.
	 * <p>
	 * This method requires the length of this {@link Vector2} is not zero.
	 * @return double
	 */
	public double normalize() {
		double magnitude = this.getMagnitude();
		if (magnitude < Epsilon.E) return 0;
		double m = 1.0 / magnitude;
		this.x *= m;
		this.y *= m;
		return magnitude;
	}
	
	/**
	 * Returns the smallest angle between the given {@link Vector2}s.
	 * <p>
	 * Returns the angle in radians in the range -pi to pi.
	 * @param vector the {@link Vector2}
	 * @return angle in radians [-pi, pi]
	 */
	public double getAngleBetween(Vector2 vector) {
		return Math.atan2(vector.y, vector.x) - Math.atan2(this.y, this.x);
	}
}
