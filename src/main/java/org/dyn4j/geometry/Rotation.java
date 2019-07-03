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

import org.dyn4j.Epsilon;

/**
 * This class represents a rotation (in 2D space).
 * The aim of this class is to reduce as much as possible the use of
 * trigonometric function calls (like Math.sin/cos or Math.atan2) because
 * the majority of those are very slow to compute.
 * This can be achieved by pre-computing the sin and cos of the angle
 * of the rotation.
 * 
 * A Rotation object is essentially a vector with norm 1.
 * 
 * This class encapsulates the above so the user need not directly use
 * and compute those trigonometric values.
 * This also provides implicit validation as the user cannot create a
 * Rotation with invalid values (values not derived from cos/sin of some angle).
 * The receiver of a Rotation object can be sure it always represents a valid rotation.
 * 
 * @author Manolis Tsamis
 * @version 3.3.1
 * @since 3.3.1
 */
public class Rotation {
	
	/** The cosine of the angle described by this Rotation */
	protected double cost;
	
	/** The sine of the angle described by this Rotation */
	protected double sint;
	
	/**
	 * Alternative way to create a new {@link Rotation} from a given angle.
	 * 
	 * @param angle in radians
	 * @return A {@link Rotation} for that angle
	 */
	public static Rotation of(double angle) {
		return new Rotation(angle);
	}
	
	/**
	 * Alternative way to create a new {@link Rotation} from a given angle, in degrees.
	 * 
	 * @param angle in degrees
	 * @return A {@link Rotation} for that angle
	 */
	public static Rotation ofDegrees(double angle) {
		return new Rotation(Math.toDegrees(angle));
	}
	
	/**
	 * Static method to create a {@link Rotation} object from the direction
	 * of a given {@link Vector2}.
	 * 
	 * @param direction The {@link Vector2} describing a direction
	 * @return A {@link Rotation} with the same direction
	 */
	public static Rotation of(Vector2 direction) {
		// Normalize the vector
		double magnitude = Math.sqrt(direction.x * direction.x + direction.y * direction.y);
		
		if (magnitude <= Epsilon.E) {
			// The zero vector has no direction, return the Identity rotation
			return new Rotation();
		}
		
		// Avoid multipying by the inverse in order to achieve better numerical accuracy
		// double m = 1.0 / magnitude;
		
		// The rotation is the normalized vector
		return new Rotation(direction.x / magnitude, direction.y / magnitude);
	}
	
	/**
	 * Creates a new {@link Rotation} representing the same rotation
	 * of a {@link Transform} object.
	 * 
	 * @param transform The {@link Transform}
	 * @return A {@link Rotation} representing the same rotation
	 */
	public static Rotation of(Transform transform) {
		// The cos and sin values are already computed internally in Transform
		return new Rotation(transform.cost, transform.sint);
	}
	
	/* ********************************************************************
	 * Helper static methods to create Rotation objects for common angles *
	 ******************************************************************** */
	
	/**
	 * @return A new identity {@link Rotation}
	 */
	public static Rotation rotation0() {
		return new Rotation();
	}
	
	/**
	 * @return A new {@link Rotation} representing rotation by 90 degrees
	 */
	public static Rotation rotation90() {
		return new Rotation(0.0, 1.0);
	}
	
	/**
	 * @return A new {@link Rotation} representing rotation by 180 degrees
	 */
	public static Rotation rotation180() {
		return new Rotation(-1.0, 0.0);
	}
	
	/**
	 * @return A new {@link Rotation} representing rotation by 270 degrees
	 */
	public static Rotation rotation270() {
		return new Rotation(0.0, -1.0);
	}
	
	private static final double SQRT_2_INV = 1.0 / Math.sqrt(2);
	
	/**
	 * @return A new {@link Rotation} representing rotation by 45 degrees
	 */
	public static Rotation rotation45() {
		return new Rotation(SQRT_2_INV, SQRT_2_INV);
	}
	
	/**
	 * @return A new {@link Rotation} representing rotation by 135 degrees
	 */
	public static Rotation rotation135() {
		return new Rotation(-SQRT_2_INV, SQRT_2_INV);
	}
	
	/**
	 * @return A new {@link Rotation} representing rotation by 225 degrees
	 */
	public static Rotation rotation225() {
		return new Rotation(-SQRT_2_INV, -SQRT_2_INV);
	}
	
	/**
	 * @return A new {@link Rotation} representing rotation by 315 degrees
	 */
	public static Rotation rotation315() {
		return new Rotation(SQRT_2_INV, -SQRT_2_INV);
	}
	
	/**
	 * Internal constructor that directly sets the cost and sint fields
	 * of the {@link Rotation} without additional validation.
	 * 
	 * @param cost The cosine of some angle
	 * @param sint The sine of the same angle
	 */
	protected Rotation(double cost, double sint) {
		this.cost = cost;
		this.sint = sint;
	}
	
	/**
	 * Default constructor. Creates an identity {@link Rotation}.
	 */
	public Rotation() {
		this.cost = 1.0; // cos(0)
		this.sint = 0.0; // sin(0)
	}
	
	/**
	 * Copy constructor.
	 * @param rotation the {@link Rotation} to copy from
	 */
	public Rotation(Rotation rotation) {
		this.cost = rotation.cost;
		this.sint = rotation.sint;
	}
	
	/**
	 * Creates a {@link Rotation} from the given angle.
	 * @param angle the angle in radians
	 */
	public Rotation(double angle) {
		this.cost = Math.cos(angle);
		this.sint = Math.sin(angle);
	}
	
	/**
	 * @return a copy of this {@link Rotation}
	 */
	public Rotation copy() {
		return new Rotation(this.cost, this.sint);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 3;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(sint);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Rotation) {
			Rotation rotation = (Rotation) obj;
			return this.cost == rotation.cost && this.sint == rotation.sint;
		}
		return false;
	}

	/**
	 * Returns true if the cos and sin components of this {@link Rotation}
	 * are the same as the given {@link Rotation}.
	 * @param rotation the {@link Rotation} to compare to
	 * @return boolean
	 */
	public boolean equals(Rotation rotation) {
		if (rotation == null) return false;
		return this.cost == rotation.cost && this.sint == rotation.sint;
	}
	
	/**
	 * Returns true if the cos and sin components of this {@link Rotation}
	 * are the same as the given {@link Rotation} given the specified error.
	 * @param error The error
	 * @param rotation the {@link Rotation} to compare to
	 * @return boolean
	 */
	public boolean equals(Rotation rotation, double error) {
		if (rotation == null) return false;
		return Math.abs(this.cost - rotation.cost) < error && Math.abs(this.sint - rotation.sint) < error;
	}
	
	/**
	 * @param angle An angle in radians
	 * @return true if the angle represents the same rotation as this {@link Rotation}, false otherwise
	 */
	public boolean equals(double angle) {
		return this.cost == Math.cos(angle) && this.sint == Math.sin(angle);
	}
	
	/**
	 * @param angle An angle in radians
	 * @param error The error
	 * @return true if the angle represents the same rotation as this {@link Rotation} given the specified error, false otherwise
	 */
	public boolean equals(double radians, double error) {
		return Math.abs(this.cost - Math.cos(radians)) < error && Math.abs(this.sint - Math.sin(radians)) < error;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Rotation(")
		  .append(this.cost)
		  .append(", ")
		  .append(this.sint)
		  .append(")");
		return sb.toString();
	}
	
	/**
	 * Sets this {@link Rotation} to the given {@link Rotation}.
	 * @param rotation the {@link Rotation} to set this {@link Rotation} to
	 * @return {@link Rotation} this rotation
	 */
	public Rotation set(Rotation rotation) {
		this.cost = rotation.cost;
		this.sint = rotation.sint;
		return this;
	}
	
	/**
	 * Sets this {@link Rotation} to be the identity.
	 * @return {@link Rotation} this rotation
	 */
	public Rotation setIdentity() {
		this.cost = 1.0;
		this.sint = 0.0;
		return this;
	}
	
	/**
	 * Sets this {@link Rotation} to the given angle.
	 * @param angle The angle in radians
	 * @return {@link Rotation} this rotation
	 */
	public Rotation set(double angle) {
		this.cost = Math.cos(angle);
		this.sint = Math.sin(angle);
		return this;
	}
	
	/**
	 * @return cos(theta) for the angle represented in this {@link Rotation}
	 */
	public double getCost() {
		return this.cost;
	}
	
	/**
	 * @return sin(theta) for the angle represented in this {@link Rotation}
	 */
	public double getSint() {
		return this.sint;
	}
	
	/**
	 * @return the angle represented in this {@link Rotation} in radians
	 */
	public double toRadians() {
		return Math.atan2(this.sint, this.cost);
	}
	
	/**
	 * @return the angle represented in this {@link Rotation} in degrees
	 */
	public double toDegrees() {
		return Math.toDegrees(toRadians());
	}
	
	/**
	 * @return A new {@link Vector2} representing the same unit vector as this {@link Rotation}
	 */
	public Vector2 toVector() {
		return new Vector2(cost, sint);
	}
	
	/**
	 * @param magnitude The magnitude for the resulting {@link Vector2}
	 * @return A new {@link Vector2} representing the unit vector of this {@link Rotation} but with the given magnitude
	 */
	public Vector2 toVector(double magnitude) {
		return new Vector2(cost * magnitude, sint * magnitude);
	}
	
	/**
	 * Internal helper method to perform rotations consisting of a 45 degree.
	 * 
	 * @param cost The cos of the angle
	 * @param sint The sin of the angle
	 * @return This {@link Rotation} after being set to (cost, sint) and rotated 45 degrees
	 */
	Rotation rotate45Helper(double cost, double sint) {
		this.cost = SQRT_2_INV * (cost - sint);
		this.sint = SQRT_2_INV * (cost + sint);
		
		return this;
	}
	
	/**
	 * Internal helper method to perform rotations consisting of a 45 degree.
	 * Returns a new {@link Rotation} object.
	 * 
	 * @param cost The cos of the angle
	 * @param sint The sin of the angle
	 * @return A new {@link Rotation} with initial values (cost, sint) and then rotated 45 degrees
	 */
	Rotation getRotated45Helper(double cost, double sint) {
		return new Rotation(
				SQRT_2_INV * (cost - sint),
				SQRT_2_INV * (cost + sint));
	}
	
	/* ************************************
	 * Methods to rotate by common angles *
	 ************************************ */
	
	/**
	 * @return this {@link Rotation} after being rotated 45 degrees
	 */
	public Rotation rotate45() {
		return this.rotate45Helper(this.cost, this.sint);
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 45 degrees
	 */
	public Rotation getRotated45() {
		return this.getRotated45Helper(this.cost, this.sint);
	}
	
	/**
	 * @return this {@link Rotation} after being rotated 90 degrees
	 */
	public Rotation rotate90() {
		double temp = this.cost;
		this.cost = -this.sint;
		this.sint = temp;
		return this;
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 90 degrees
	 */
	public Rotation getRotated90() {
		return new Rotation(-this.sint, this.cost);
	}
	
	/**
	 * @return this {@link Rotation} after being rotated 135 degrees
	 */
	public Rotation rotate135() {
		// Rotate by 90 and another 45
		return this.rotate45Helper(-this.sint, this.cost);
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 135 degrees
	 */
	public Rotation getRotated135() {
		// Rotate by 90 and another 45
		return this.getRotated45Helper(-this.sint, this.cost);
	}
	
	/**
	 * @return this {@link Rotation} after being rotated 180 degrees
	 */
	public Rotation rotate180() {
		this.cost = -this.cost;
		this.sint = -this.sint;
		return this;
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 180 degrees
	 */
	public Rotation getRotated180() {
		return new Rotation(-this.cost, -this.sint);
	}
	
	/**
	 * @return this {@link Rotation} after being rotated 225 degrees
	 */
	public Rotation rotate225() {
		// Rotate by 180 and another 45
		return this.rotate45Helper(-this.cost, -this.sint);
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 225 degrees
	 */
	public Rotation getRotated225() {
		// Rotate by 180 and another 45
		return this.getRotated45Helper(-this.cost, -this.sint);
	}
	
	/**
	 * @return this {@link Rotation} after being rotated 270 degrees
	 */
	public Rotation rotate270() {
		double temp = this.cost;
		this.cost = this.sint;
		this.sint = -temp;
		return this;
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 270 degrees
	 */
	public Rotation getRotated270() {
		return new Rotation(this.sint, -this.cost);
	}
	
	/**
	 * @return this {@link Rotation} after being rotated 315 degrees
	 */
	public Rotation rotate315() {
		// Rotate by 270 and another 45
		return this.rotate45Helper(this.sint, -this.cost);
	}
	
	/**
	 * @return a new {@link Rotation} equal to this {@link Rotation} after being rotated 315 degrees
	 */
	public Rotation getRotated315() {
		// Rotate by 270 and another 45
		return this.getRotated45Helper(this.sint, -this.cost);
	}
	
	/**
	 * If this {@link Rotation} represents the angle &thetasym;, after calling
	 * this method the {@link Rotation} represents the angle -&thetasym;.
	 * 
	 * @return The inverse of this {@link Rotation}
	 */
	public Rotation inverse() {
		this.sint = -this.sint;
		return this;
	}
	
	/**
	 * If this {@link Rotation} represents the angle &thetasym;, returns
	 * an new {@link Rotation} representing the angle -&thetasym;.
	 * 
	 * @return A new {@link Rotation} representing inverse angle
	 */
	public Rotation getInversed() {
		return new Rotation(this.cost, -this.sint);
	}
	
	/**
	 * Internal method that rotates this {@link Rotation} by an angle &thetasym;.
	 * 
	 * @param c cos(&thetasym;)
	 * @param s sin(&thetasym;)
	 * @return This {@link Rotation} after being rotated by &thetasym;
	 */
	Rotation rotate(double c, double s) {
		double cost = this.cost;
		double sint = this.sint;
		
		this.cost = cost * c - sint * s;
		this.sint = cost * s + sint * c;
		
		return this;
	}
	
	/**
	 * Internal method that return a new {@link Rotation} representing
	 * this {@link Rotation} after being rotated by an angle &thetasym;.
	 * 
	 * @param c cos(&thetasym;)
	 * @param s sin(&thetasym;)
	 * @return A new {@link Rotation} after being rotated by &thetasym;
	 */
	public Rotation getRotated(double c, double s) {
		return new Rotation(
				this.cost * c - this.sint * s,
				this.cost * s + this.sint * c);
	}
	
	/**
	 * @param rotation The {@link Rotation} object
	 * @return This {@link Rotation} after being rotated by the argument
	 */
	public Rotation rotate(Rotation rotation) {
		return this.rotate(rotation.cost, rotation.sint);
	}
	
	/**
	 * @param rotation The {@link Rotation} object
	 * @return A new {@link Rotation} which is this {@link Rotation} after being rotated by the argument
	 */
	public Rotation getRotated(Rotation rotation) {
		return this.getRotated(rotation.cost, rotation.sint);
	}
	
	/**
	 * @param angle The rotation in radians
	 * @return This {@link Rotation} after being rotated by the argument
	 */
	public Rotation rotate(double angle) {
		return this.rotate(Math.cos(angle), Math.sin(angle));
	}
	
	/**
	 * @param angle The rotation in radians
	 * @return A new {@link Rotation} which is this {@link Rotation} after being rotated by the argument
	 */
	public Rotation getRotated(double radians) {
		return this.getRotated(Math.cos(radians), Math.sin(radians));
	}
	
	/**
	 * @return true if this {@link Rotation} is an identity rotation, false otherwise
	 */
	public boolean isIdentity() {
		return this.cost == 1;
	}
	
	/**
	 * @param error The error
	 * @return true if this {@link Rotation} is an identity rotation given the specified error, false otherwise
	 */
	public boolean isIdentity(double error) {
		return Math.abs(this.cost - 1) < error;
	}
	
	/**
	 * Returns the dot product of the this {@link Rotation} and the given {@link Rotation}
	 * which is essentially the sine of the angle between those rotations.
	 * 
	 * @param rotation the {@link Rotation}
	 * @return double cos(&thetasym;)
	 */
	public double dot(Rotation rotation) {
		return this.cost * rotation.cost + this.sint * rotation.sint;
	}
	
	/**
	 * Returns the cross product of the this {@link Rotation} and the given {@link Rotation}
	 * which is essentially the sine of the angle between those rotations.
	 * 
	 * @param rotation the {@link Rotation}
	 * @return double sin(&thetasym;)
	 */
	public double cross(Rotation rotation) {
		return this.cost * rotation.sint - this.sint * rotation.cost;
	}
	
	/**
	 * Returns the dot product of the this {@link Rotation} and the given {@link Vector2}.
	 * For internal use.
	 * 
	 * @param rotation the {@link Rotation}
	 * @return double cos(&thetasym;) * |v|
	 */
	double dot(Vector2 vector) {
		return this.cost * vector.x + this.sint * vector.y;
	}
	
	/**
	 * Returns the cross product of the this {@link Rotation} and the given {@link Vector2}.
	 * For internal use.
	 * 
	 * @param rotation the {@link Rotation}
	 * @return double sin(&thetasym;) * |v|
	 */
	double cross(Vector2 vector) {
		return this.cost * vector.y - this.sint * vector.x;
	}

	/**
	 * Compares this {@link Rotation} with another one, based on the angle between them (The one with -&pi; &le; &thetasym; &le; &pi;)
	 * Returns 1 if &thetasym; > 0, -1 if &thetasym; < 0 and 0 otherwise
	 * 
	 * @param other The {@link Rotation} to compare to
	 * @return The comparison result; 1, -1 or 0 respectively 
	 */
	public int compare(Rotation other) {
		// cmp = sin(&thetasym;) where &thetasym; is the angle between this rotation and the other
		// So we can decide what to return based on the sign of cmp
		double cmp = this.cross(other);
		
		if (cmp > 0.0) {
			return 1;
		} else if (cmp < 0.0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Compares this {@link Rotation} with a {@link Vector2}, based on the angle between them (The one with -&pi; &le; &thetasym; &le; &pi;)
	 * Returns 1 if &thetasym; > 0, -1 if &thetasym; < 0 and 0 otherwise
	 * 
	 * @param other The {@link Vector2} to compare to
	 * @return The comparison result; 1, -1 or 0 respectively 
	 */
	public int compare(Vector2 other) {
		// cmp = |v| * sin(&thetasym;) where &thetasym; is the angle between this rotation and the other
		// |v| is always positive and does not affect the result so we can decide what to return based just on the sign of cmp
		double cmp = this.cross(other);
		
		if (cmp > 0.0) {
			return 1;
		} else if (cmp < 0.0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns the angle between this and the given {@link Rotation}
	 * represented again as a new {@link Rotation}.
	 * 
	 * @param rotation the {@link Rotation}
	 * @return A new {@link Rotation} representing the angle between them
	 */
	public Rotation getRotationBetween(Rotation rotation) {
		return new Rotation(this.dot(rotation), this.cross(rotation));
	}
	
	/**
	 * Returns the angle between this {@link Rotation} and the
	 * given {@link Vector2} represented as a new {@link Rotation}.
	 * 
	 * @param vector the {@link Vector2}
	 * @return A new {@link Rotation} representing the angle between them
	 */
	public Rotation getRotationBetween(Vector2 vector) {
		return this.getRotationBetween(Rotation.of(vector));
	}
	
}
