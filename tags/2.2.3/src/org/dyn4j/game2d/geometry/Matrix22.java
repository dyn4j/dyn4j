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

import org.dyn4j.game2d.Epsilon;

/**
 * Represents a 2x2 Matrix.
 * <p>
 * Used to solve 2x2 systems of equations.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Matrix22 {
	/** The element at 0,0 */
	public double m00;
	
	/** The element at 0,1 */
	public double m01;
	
	/** The element at 1,0 */
	public double m10;
	
	/** The element at 1,1 */
	public double m11;
	
	/**
	 * Default constructor.
	 */
	public Matrix22() {}
	
	/**
	 * Full constructor.
	 * @param m00 the element at 0,0
	 * @param m01 the element at 0,1
	 * @param m10 the element at 1,0
	 * @param m11 the element at 1,1
	 */
	public Matrix22(double m00, double m01, double m10, double m11) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The given array should be in the same order as the 
	 * {@link #Matrix22(double, double, double, double)} constructor.
	 * @param values the values array
	 * @throws NullPointerException if values is null
	 * @throws IllegalArgumentException if values is not of length 4
	 */
	public Matrix22(double[] values) {
		if (values == null) throw new NullPointerException("The values array cannot be null.");
		if (values.length != 4) throw new IndexOutOfBoundsException("The values array must be of length 4.");
		this.m00 = values[0];
		this.m01 = values[1];
		this.m10 = values[2];
		this.m11 = values[3];
	}
	
	/**
	 * Copy constructor.
	 * @param matrix the {@link Matrix22} to copy
	 */
	public Matrix22(Matrix22 matrix) {
		this.m00 = matrix.m00; this.m01 = matrix.m01;
		this.m10 = matrix.m10; this.m11 = matrix.m11;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Matrix22) {
			Matrix22 other = (Matrix22) obj;
			if (other.m00 == this.m00
			 && other.m01 == this.m01
			 && other.m10 == this.m10
			 && other.m11 == this.m11) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.m00).append(" ").append(this.m01).append("][")
		.append(this.m10).append(" ").append(this.m11).append("]");
		return sb.toString();
	}
	
	/**
	 * Adds the given {@link Matrix22} to this {@link Matrix22}
	 * returning this {@link Matrix22}.
	 * <pre>
	 * this = this + m
	 * </pre>
	 * @param matrix the {@link Matrix22} to add
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 add(Matrix22 matrix) {
		this.m00 += matrix.m00;
		this.m01 += matrix.m01;
		this.m10 += matrix.m10;
		this.m11 += matrix.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} that is the sum of this {@link Matrix22}
	 * and the given {@link Matrix22}.
	 * <pre>
	 * r = this + m
	 * </pre>
	 * @param matrix the {@link Matrix22} to add
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 sum(Matrix22 matrix) {
		// make a copy of this matrix
		Matrix22 rm = new Matrix22(this);
		// perform the addition
		rm.m00 += matrix.m00;
		rm.m01 += matrix.m01;
		rm.m10 += matrix.m10;
		rm.m11 += matrix.m11;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Subtracts the given {@link Matrix22} from this {@link Matrix22}
	 * returning this {@link Matrix22}.
	 * <pre>
	 * this = this - m
	 * </pre>
	 * @param matrix the {@link Matrix22} to subtract
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 subtract(Matrix22 matrix) {
		this.m00 -= matrix.m00;
		this.m01 -= matrix.m01;
		this.m10 -= matrix.m10;
		this.m11 -= matrix.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} that is the difference of this {@link Matrix22}
	 * and the given {@link Matrix22}.
	 * <pre>
	 * r = this - m
	 * </pre>
	 * @param matrix the {@link Matrix22} to subtract
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 difference(Matrix22 matrix) {
		// make a copy of this matrix
		Matrix22 rm = new Matrix22(this);
		// perform the subtraction
		rm.m00 -= matrix.m00;
		rm.m01 -= matrix.m01;
		rm.m10 -= matrix.m10;
		rm.m11 -= matrix.m11;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given matrix {@link Matrix22}
	 * returning this {@link Matrix22}.
	 * <pre>
	 * this = this * m
	 * </pre>
	 * @param matrix the {@link Matrix22} to subtract
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 multiply(Matrix22 matrix) {
		double m00 = this.m00;
		double m01 = this.m01;
		double m10 = this.m10;
		double m11 = this.m11;
		this.m00 = m00 * matrix.m00 + m01 * matrix.m10;
		this.m01 = m00 * matrix.m01 + m01 * matrix.m11;
		this.m10 = m10 * matrix.m00 + m11 * matrix.m10;
		this.m11 = m10 * matrix.m01 + m11 * matrix.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} that is the product of this {@link Matrix22}
	 * and the given {@link Matrix22}.
	 * <pre>
	 * r = this * m
	 * </pre>
	 * @param matrix the {@link Matrix22} to multiply
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 product(Matrix22 matrix) {
		Matrix22 rm = new Matrix22();
		rm.m00 = this.m00 * matrix.m00 + this.m01 * matrix.m10;
		rm.m01 = this.m00 * matrix.m01 + this.m01 * matrix.m11;
		rm.m10 = this.m10 * matrix.m00 + this.m11 * matrix.m10;
		rm.m11 = this.m10 * matrix.m01 + this.m11 * matrix.m11;
		return rm;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given {@link Vector2} and
	 * places the result in the given {@link Vector2}.
	 * <pre>
	 * v = this * v
	 * </pre>
	 * @param vector the {@link Vector2} to multiply
	 * @return {@link Vector2} the vector result
	 */
	public Vector2 multiply(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.m00 * x + this.m01 * y;
		vector.y = this.m10 * x + this.m11 * y;
		return vector;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given {@link Vector2} returning
	 * the result in a new {@link Vector2}.
	 * <pre>
	 * r = this * v
	 * </pre>
	 * @param vector the {@link Vector2} to multiply
	 * @return {@link Vector2} the vector result
	 */
	public Vector2 product(Vector2 vector) {
		Vector2 r = new Vector2();
		r.x = this.m00 * vector.x + this.m01 * vector.y;
		r.y = this.m10 * vector.x + this.m11 * vector.y;
		return r;
	}
	
	/**
	 * Multiplies the given {@link Vector2} by this {@link Matrix22} and
	 * places the result in the given {@link Vector2}.
	 * <pre>
	 * v = v<sup>T</sup> * this
	 * </pre>
	 * @param vector the {@link Vector2} to multiply
	 * @return {@link Vector2} the vector result
	 */
	public Vector2 multiplyT(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.m00 * x + this.m10 * y;
		vector.y = this.m01 * x + this.m11 * y;
		return vector;
	}
	
	/**
	 * Multiplies the given {@link Vector2} by this {@link Matrix22} returning
	 * the result in a new {@link Vector2}.
	 * <pre>
	 * r = v<sup>T</sup> * this
	 * </pre>
	 * @param vector the {@link Vector2} to multiply
	 * @return {@link Vector2} the vector result
	 */
	public Vector2 productT(Vector2 vector) {
		Vector2 r = new Vector2();
		r.x = this.m00 * vector.x + this.m10 * vector.y;
		r.y = this.m01 * vector.x + this.m11 * vector.y;
		return r;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given scalar and places
	 * the result in this {@link Matrix22}.
	 * <pre>
	 * this = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 multiply(double scalar) {
		this.m00 *= scalar;
		this.m01 *= scalar;
		this.m10 *= scalar;
		this.m11 *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link Matrix22} by the given scalar returning a
	 * new {@link Matrix22} containing the result.
	 * <pre>
	 * r = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 product(double scalar) {
		// make a copy of this matrix
		Matrix22 rm = new Matrix22(this);
		// multiply by the scalar
		rm.m00 *= scalar;
		rm.m01 *= scalar;
		rm.m10 *= scalar;
		rm.m11 *= scalar;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Sets this {@link Matrix22} to an identity {@link Matrix22}.
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 identity() {
		this.m00 = 1; this.m01 = 0;
		this.m10 = 0; this.m11 = 1;
		return this;
	}
	
	/**
	 * Sets this {@link Matrix22} to the transpose of this {@link Matrix22}.
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 transpose() {
		double m = this.m01;
		this.m01 = this.m10;
		this.m10 = m;
		return this;
	}
	
	/**
	 * Returns the the transpose of this {@link Matrix22} in a new {@link Matrix22}.
	 * @return {@link Matrix22} a new matrix contianing the transpose
	 */
	public Matrix22 getTranspose() {
		Matrix22 rm = new Matrix22(this);
		rm.transpose();
		return rm;
	}
	
	/**
	 * Returns the determinant of this {@link Matrix22}.
	 * @return double
	 */
	public double determinant() {
		return this.m00 * this.m11 - this.m01 * this.m10;
	}
	
	/**
	 * Performs the inverse of this {@link Matrix22} and places the
	 * result in this {@link Matrix22}.
	 * @return {@link Matrix22} this matrix
	 */
	public Matrix22 invert() {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) >= Epsilon.E) {
			det = 1.0 / det;
		}
		double a = this.m00;
		double b = this.m01;
		double c = this.m10;
		double d = this.m11;
		this.m00 =  det * d;
		this.m01 = -det * b;
		this.m10 = -det * c;
		this.m11 =  det * a;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix22} containing the inverse of this {@link Matrix22}.
	 * @return {@link Matrix22} a new matrix containing the result
	 */
	public Matrix22 getInverse() {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) >= Epsilon.E) {
			det = 1.0 / det;
		}
		Matrix22 rm = new Matrix22();
		rm.m00 =  det * this.m11;
		rm.m01 = -det * this.m01;
		rm.m10 = -det * this.m10;
		rm.m11 =  det * this.m00;
		return rm;
	}
	
	/**
	 * Solves the system of linear equations:
	 * <pre>
	 * Ax = b
	 * Multiply by A<sup>-1</sup> on both sides
	 * x = A<sup>-1</sup>b
	 * </pre>
	 * @param b the b {@link Vector2}
	 * @return {@link Vector2} the x vector
	 */
	public Vector2 solve(Vector2 b) {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) >= Epsilon.E) {
			det = 1.0 / det;
		}
		Vector2 r = new Vector2();
		r.x = det * (this.m11 * b.x - this.m01 * b.y);
		r.y = det * (this.m00 * b.y - this.m10 * b.x);
		return r;
	}
}
