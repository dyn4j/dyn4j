/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.Copyable;
import org.dyn4j.Epsilon;
import org.dyn4j.resources.Messages;

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
 * @version 4.0.0
 * @since 3.4.0
 */
public class Rotation implements Copyable<Rotation> {
	/** 1/sqrt(2) */
	private static final double SQRT_2_INV = 1.0 / Math.sqrt(2);
	
	/** The cosine of the angle described by this Rotation */
	protected double cost;
	
	/** The sine of the angle described by this Rotation */
	protected double sint;
	
	/**
	 * Alternative way to create a new {@link Rotation} from a given angle.
	 * @param angle in radians
	 * @return A {@link Rotation} for that angle
	 */
	public static Rotation of(double angle) {
		return new Rotation(angle);
	}
	
	/**
	 * Alternative way to create a new {@link Rotation} from a given angle, in degrees.
	 * @param angle in degrees
	 * @return A {@link Rotation} for that angle
	 */
	public static Rotation ofDegrees(double angle) {
		return new Rotation(Math.toRadians(angle));
	}
	
	/**
	 * Static method to create a {@link Rotation} object from the direction
	 * of a given {@link Vector2}.
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
	 * Static method to create a {@link Rotation} from a pair of values that lie on the unit circle;
	 * That is a pair of values (x, y) such that x = cos(&theta;), y = sin(&theta;) for some value &theta;
	 * This method is provided for the case where the cos and sin values are already computed and
	 * the overhead can be avoided.
	 * This method will check whether those values are indeed on the unit circle and otherwise throw an {@link IllegalArgumentException}.
	 * @param cost The x value = cos(&theta;)
	 * @param sint The y value = sin(&theta;)
	 * @throws IllegalArgumentException if (cost, sint) is not on the unit circle
	 * @return A {@link Rotation} defined by (cost, sint)
	 */
	public static Rotation of(double cost, double sint) {
		double magnitude = cost * cost + sint * sint;
		
		if (Math.abs(magnitude - 1) > Epsilon.E) {
			throw new IllegalArgumentException(Messages.getString("geometry.rotation.invalidPoint"));
		}
		
		return new Rotation(cost, sint);
	}
	
	/**
	 * Creates a new {@link Rotation} representing the same rotation
	 * of a {@link Transform} object.
	 * @param transform The {@link Transform}
	 * @return A {@link Rotation} representing the same rotation
	 */
	public static Rotation of(Transform transform) {
		// The cos and sin values are already computed internally in Transform
		return new Rotation(transform.cost, transform.sint);
	}
	
	/**
	 * Creates a new {@link Rotation} of 0 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation0() {
		return new Rotation();
	}
	
	/**
	 * Creates a new {@link Rotation} of 90 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation90() {
		return new Rotation(0.0, 1.0);
	}
	
	/**
	 * Creates a new {@link Rotation} of 180 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation180() {
		return new Rotation(-1.0, 0.0);
	}
	
	/**
	 * Creates a new {@link Rotation} of 270 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation270() {
		return new Rotation(0.0, -1.0);
	}
	
	/**
	 * Creates a new {@link Rotation} of 45 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation45() {
		return new Rotation(SQRT_2_INV, SQRT_2_INV);
	}
	
	/**
	 * Creates a new {@link Rotation} of 135 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation135() {
		return new Rotation(-SQRT_2_INV, SQRT_2_INV);
	}
	
	/**
	 * Creates a new {@link Rotation} of 225 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation225() {
		return new Rotation(-SQRT_2_INV, -SQRT_2_INV);
	}
	
	/**
	 * Creates a new {@link Rotation} of 315 degrees.
	 * @return {@link Rotation}
	 */
	public static Rotation rotation315() {
		return new Rotation(SQRT_2_INV, -SQRT_2_INV);
	}
	
	/**
	 * Internal constructor that directly sets the cost and sint fields
	 * of the {@link Rotation} without additional validation.
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
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
		temp = Double.doubleToLongBits(this.cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.sint);
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
	 * @param rotation the {@link Rotation} to compare to
	 * @param error the error
	 * @return boolean
	 */
	public boolean equals(Rotation rotation, double error) {
		if (rotation == null) return false;
		return Math.abs(this.cost - rotation.cost) < error && Math.abs(this.sint - rotation.sint) < error;
	}
	
	/**
	 * Returns true if the cos and sin components of this {@link Rotation}
	 * are the same as the given angle
	 * @param angle the angle in radians
	 * @return boolean
	 */
	public boolean equals(double angle) {
		return this.cost == Math.cos(angle) && this.sint == Math.sin(angle);
	}
	
	/**
	 * Returns true if the cos and sin components of this {@link Rotation}
	 * are the same as the given angle given the specified error.
	 * @param angle the angle in radians
	 * @param error the error
	 * @return boolean
	 */
	public boolean equals(double angle, double error) {
		return Math.abs(this.cost - Math.cos(angle)) < error && Math.abs(this.sint - Math.sin(angle)) < error;
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
	 * @param angle the angle in radians
	 * @return {@link Rotation} this rotation
	 */
	public Rotation set(double angle) {
		this.cost = Math.cos(angle);
		this.sint = Math.sin(angle);
		return this;
	}
	
	/**
	 * Returns the value of cos(&theta;) for this {@link Rotation}.
	 * @return double
	 */
	public double getCost() {
		return this.cost;
	}
	
	/**
	 * Returns the value of sin(&theta;) for this {@link Rotation}.
	 * @return double
	 */
	public double getSint() {
		return this.sint;
	}
	
	/**
	 * Returns the angle in radians for this {@link Rotation}.
	 * @return double
	 */
	public double toRadians() {
		// Since we have the cos and sin values computed we can use
		// the Math.acos function which is much faster than Math.atan2
		
		// We can find the angle in the range [0, &pi;] with Math.acos
		// and then we'll use the sign of the sin value to find in which
		// semicircle we are and extend the result to [-&pi;, &pi;]
		
		// Apart from being quite faster this is also more precise
		// (see the documentation of Math.acos and Math.atan2)
		
		double acos = Math.acos(this.cost);
		double angle = (this.sint >= 0)? acos: -acos;
		return angle;
	}
	
	/**
	 * Returns the angle in degrees for this {@link Rotation}.
	 * @return double
	 */
	public double toDegrees() {
		return Math.toDegrees(toRadians());
	}
	
	/**
	 * Returns this {@link Rotation} as a unit length direction vector.
	 * @return {@link Vector2}
	 */
	public Vector2 toVector() {
		return new Vector2(this.cost, this.sint);
	}
	
	/**
	 * Returns this {@link Rotation} as a direction vector with the given magnitude.
	 * @param magnitude the magnitude
	 * @return {@link Vector2}
	 */
	public Vector2 toVector(double magnitude) {
		return new Vector2(this.cost * magnitude, this.sint * magnitude);
	}
	
	/**
	 * Internal helper method to perform rotations consisting of a 45 degree.
	 * @param cost the cos of the angle
	 * @param sint the sin of the angle
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
	 * @param cost the cos of the angle
	 * @param sint the sin of the angle
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
	 * Rotates this rotation 45 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate45() {
		return this.rotate45Helper(this.cost, this.sint);
	}
	
	/**
	 * Rotates this rotation 45 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated45() {
		return this.getRotated45Helper(this.cost, this.sint);
	}
	
	/**
	 * Rotates this rotation 90 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate90() {
		double temp = this.cost;
		this.cost = -this.sint;
		this.sint = temp;
		return this;
	}
	
	/**
	 * Rotates this rotation 90 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated90() {
		return new Rotation(-this.sint, this.cost);
	}
	
	/**
	 * Rotates this rotation 135 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate135() {
		// Rotate by 90 and another 45
		return this.rotate45Helper(-this.sint, this.cost);
	}
	
	/**
	 * Rotates this rotation 135 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated135() {
		// Rotate by 90 and another 45
		return this.getRotated45Helper(-this.sint, this.cost);
	}
	
	/**
	 * Rotates this rotation 180 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate180() {
		this.cost = -this.cost;
		this.sint = -this.sint;
		return this;
	}
	
	/**
	 * Rotates this rotation 180 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated180() {
		return new Rotation(-this.cost, -this.sint);
	}
	
	/**
	 * Rotates this rotation 225 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate225() {
		// Rotate by 180 and another 45
		return this.rotate45Helper(-this.cost, -this.sint);
	}
	
	/**
	 * Rotates this rotation 225 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated225() {
		// Rotate by 180 and another 45
		return this.getRotated45Helper(-this.cost, -this.sint);
	}
	
	/**
	 * Rotates this rotation 270 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate270() {
		double temp = this.cost;
		this.cost = this.sint;
		this.sint = -temp;
		return this;
	}
	
	/**
	 * Rotates this rotation 270 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated270() {
		return new Rotation(this.sint, -this.cost);
	}
	
	/**
	 * Rotates this rotation 315 degrees and returns this rotation.
	 * @return {@link Rotation}
	 */
	public Rotation rotate315() {
		// Rotate by 270 and another 45
		return this.rotate45Helper(this.sint, -this.cost);
	}
	
	/**
	 * Rotates this rotation 315 degrees and returns a new rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getRotated315() {
		// Rotate by 270 and another 45
		return this.getRotated45Helper(this.sint, -this.cost);
	}
	
	/**
	 * Negates this rotation and returns this rotation.
	 * <p>
	 * Let &theta; be the rotation, then -&theta; is the inverse rotation.
	 * @return {@link Rotation}
	 */
	public Rotation inverse() {
		this.sint = -this.sint;
		return this;
	}
	
	/**
	 * Negates this rotation and returns a new rotation.
	 * <p>
	 * Let &theta; be the rotation, then -&theta; is the inverse rotation.
	 * @return {@link Rotation}
	 */
	public Rotation getInversed() {
		return new Rotation(this.cost, -this.sint);
	}
	
	/**
	 * Internal method that rotates this {@link Rotation} by an angle &theta; and
	 * returns this rotation.
	 * @param c cos(&theta;)
	 * @param s sin(&theta;)
	 * @return {@link Rotation}
	 */
	Rotation rotate(double c, double s) {
		double cost = this.cost;
		double sint = this.sint;
		
		this.cost = Interval.clamp(cost * c - sint * s, -1.0, 1.0);
		this.sint = Interval.clamp(cost * s + sint * c, -1.0, 1.0);
		
		return this;
	}
	
	/**
	 * Internal method that return a new {@link Rotation} representing
	 * this {@link Rotation} after being rotated by an angle &theta;.
	 * @param c cos(&theta;)
	 * @param s sin(&theta;)
	 * @return {@link Rotation}
	 */
	public Rotation getRotated(double c, double s) {
		return new Rotation(
				Interval.clamp(this.cost * c - this.sint * s, -1.0, 1.0),
				Interval.clamp(this.cost * s + this.sint * c, -1.0, 1.0));
	}
	
	/**
	 * Rotates this rotation by the given rotation and returns this rotation.
	 * @param rotation the {@link Rotation}
	 * @return {@link Rotation}
	 */
	public Rotation rotate(Rotation rotation) {
		return this.rotate(rotation.cost, rotation.sint);
	}
	
	/**
	 * Rotates this rotation by the given rotation and returns a new rotation.
	 * @param rotation the {@link Rotation}
	 * @return {@link Rotation}
	 */
	public Rotation getRotated(Rotation rotation) {
		return this.getRotated(rotation.cost, rotation.sint);
	}
	
	/**
	 * Rotates this rotation by the given angle and returns this rotation.
	 * @param angle the rotation in radians
	 * @return {@link Rotation}
	 */
	public Rotation rotate(double angle) {
		return this.rotate(Math.cos(angle), Math.sin(angle));
	}
	
	/**
	 * Rotates this rotation by the given angle and returns a new rotation.
	 * @param angle the rotation in radians
	 * @return {@link Rotation}
	 */
	public Rotation getRotated(double angle) {
		return this.getRotated(Math.cos(angle), Math.sin(angle));
	}
	
	/**
	 * Returns true if this rotation is an identity rotation.
	 * @return boolean
	 */
	public boolean isIdentity() {
		return this.cost == 1;
	}
	
	/**
	 * Returns true if this rotation is an identity rotation within the given error.
	 * @param error the error
	 * @return boolean
	 */
	public boolean isIdentity(double error) {
		return Math.abs(this.cost - 1) < error;
	}
	
	/**
	 * Returns the dot product of the this {@link Rotation} and the given {@link Rotation}
	 * which is essentially the sine of the angle between those rotations.
	 * @param rotation the {@link Rotation}
	 * @return double
	 */
	public double dot(Rotation rotation) {
		return this.cost * rotation.cost + this.sint * rotation.sint;
	}
	
	/**
	 * Returns the cross product of the this {@link Rotation} and the given {@link Rotation}
	 * which is essentially the sine of the angle between those rotations.
	 * @param rotation the {@link Rotation}
	 * @return double
	 */
	public double cross(Rotation rotation) {
		return this.cost * rotation.sint - this.sint * rotation.cost;
	}
	
	/**
	 * Returns the dot product of the this {@link Rotation} and the given {@link Vector2}.
	 * For internal use.
	 * @param vector the {@link Vector2}
	 * @return double
	 */
	double dot(Vector2 vector) {
		return this.cost * vector.x + this.sint * vector.y;
	}
	
	/**
	 * Returns the cross product of the this {@link Rotation} and the given {@link Vector2}.
	 * For internal use.
	 * @param vector the {@link Vector2}
	 * @return double
	 */
	double cross(Vector2 vector) {
		return this.cost * vector.y - this.sint * vector.x;
	}

	/**
	 * Compares this {@link Rotation} with another one, based on the angle between them (The one with -&pi; &le; &theta; &le; &pi;)
	 * Returns 1 if &theta; &gt; 0, -1 if &theta; &lt; 0 and 0 otherwise
	 * @param other the {@link Rotation} to compare to
	 * @return int 
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
	 * Compares this {@link Rotation} with a {@link Vector2}, based on the angle between them (The one with -&pi; &le; &theta; &le; &pi;)
	 * Returns 1 if &theta; &gt; 0, -1 if &theta; &lt; 0 and 0 otherwise
	 * @param other the {@link Vector2} to compare to
	 * @return int 
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
	 * represented as a new {@link Rotation}.
	 * @param rotation the {@link Rotation}
	 * @return {@link Rotation}
	 */
	public Rotation getRotationBetween(Rotation rotation) {
		return new Rotation(this.dot(rotation), this.cross(rotation));
	}
	
	/**
	 * Returns the angle between this {@link Rotation} and the
	 * given {@link Vector2} represented as a new {@link Rotation}.
	 * @param vector the {@link Vector2}
	 * @return {@link Rotation}
	 */
	public Rotation getRotationBetween(Vector2 vector) {
		return this.getRotationBetween(Rotation.of(vector));
	}
	
}
