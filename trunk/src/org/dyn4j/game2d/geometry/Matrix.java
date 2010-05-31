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
 * Represents a 2x2 Matrix.
 * @author William Bittle
 */
public class Matrix {
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
	public Matrix() {}
	
	/**
	 * Full constructor.
	 * @param m00 the element at 0,0
	 * @param m01 the element at 0,1
	 * @param m10 the element at 1,0
	 * @param m11 the element at 1,1
	 */
	public Matrix(double m00, double m01, double m10, double m11) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The given array should be in the same order as the 
	 * {@link #Matrix(double, double, double, double)} constructor
	 * (m00, m01, m10, m11).
	 * @param values the values array
	 */
	public Matrix(double[] values) {
		if (values == null) throw new NullPointerException("The values array cannot be null.");
		if (values.length != 4) throw new IndexOutOfBoundsException("The values array must be of length 4.");
		this.m00 = values[0];
		this.m01 = values[1];
		this.m10 = values[2];
		this.m11 = values[3];
	}
	
	/**
	 * Copy constructor.
	 * @param m the {@link Matrix} to copy
	 */
	public Matrix(Matrix m) {
		this.m00 = m.m00; this.m01 = m.m01;
		this.m10 = m.m10; this.m11 = m.m11;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Matrix) {
			Matrix o = (Matrix) obj;
			if (o.m00 == this.m00
			 && o.m01 == this.m01
			 && o.m10 == this.m10
			 && o.m11 == this.m11) {
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
		sb.append("[").append(this.m00)
		.append(" ").append(this.m01)
		.append(" ").append(this.m10)
		.append(" ").append(this.m11).append("]");
		return sb.toString();
	}
	
	/**
	 * Adds the given {@link Matrix} to this {@link Matrix}
	 * returning this {@link Matrix}.
	 * <pre>
	 * this = this + m
	 * </pre>
	 * @param m the {@link Matrix} to add
	 * @return {@link Matrix} this matrix
	 */
	public Matrix add(Matrix m) {
		this.m00 += m.m00;
		this.m01 += m.m01;
		this.m10 += m.m10;
		this.m11 += m.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix} that is the sum of this {@link Matrix}
	 * and the given {@link Matrix}.
	 * <pre>
	 * r = this + m
	 * </pre>
	 * @param m the {@link Matrix} to add
	 * @return {@link Matrix} a new matrix containing the result
	 */
	public Matrix sum(Matrix m) {
		// make a copy of this matrix
		Matrix rm = new Matrix(this);
		// perform the addition
		rm.m00 += m.m00;
		rm.m01 += m.m01;
		rm.m10 += m.m10;
		rm.m11 += m.m11;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Subtracts the given {@link Matrix} from this {@link Matrix}
	 * returning this {@link Matrix}.
	 * <pre>
	 * this = this - m
	 * </pre>
	 * @param m the {@link Matrix} to subtract
	 * @return {@link Matrix} this matrix
	 */
	public Matrix subtract(Matrix m) {
		this.m00 -= m.m00;
		this.m01 -= m.m01;
		this.m10 -= m.m10;
		this.m11 -= m.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix} that is the difference of this {@link Matrix}
	 * and the given {@link Matrix}.
	 * <pre>
	 * r = this - m
	 * </pre>
	 * @param m the {@link Matrix} to subtract
	 * @return {@link Matrix} a new matrix containing the result
	 */
	public Matrix difference(Matrix m) {
		// make a copy of this matrix
		Matrix rm = new Matrix(this);
		// perform the addition
		rm.m00 -= m.m00;
		rm.m01 -= m.m01;
		rm.m10 -= m.m10;
		rm.m11 -= m.m11;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Multiplies this {@link Matrix} by the given matrix {@link Matrix}
	 * returning this {@link Matrix}.
	 * <pre>
	 * this = this * m
	 * </pre>
	 * @param m the {@link Matrix} to subtract
	 * @return {@link Matrix} this matrix
	 */
	public Matrix multiply(Matrix m) {
		double m00 = this.m00;
		double m01 = this.m01;
		double m10 = this.m10;
		double m11 = this.m11;
		this.m00 = m00 * m.m00 + m01 * m.m10;
		this.m01 = m00 * m.m01 + m01 * m.m11;
		this.m10 = m10 * m.m00 + m11 * m.m10;
		this.m11 = m10 * m.m01 + m11 * m.m11;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix} that is the product of this {@link Matrix}
	 * and the given {@link Matrix}.
	 * <pre>
	 * r = this * m
	 * </pre>
	 * @param m the {@link Matrix} to multiply
	 * @return {@link Matrix} a new matrix containing the result
	 */
	public Matrix product(Matrix m) {
		Matrix rm = new Matrix();
		rm.m00 = this.m00 * m.m00 + this.m01 * m.m10;
		rm.m01 = this.m00 * m.m01 + this.m01 * m.m11;
		rm.m10 = this.m10 * m.m00 + this.m11 * m.m10;
		rm.m11 = this.m10 * m.m01 + this.m11 * m.m11;
		return rm;
	}
	
	/**
	 * Multiplies this {@link Matrix} by the given {@link Vector} and
	 * places the result in the given {@link Vector}.
	 * <pre>
	 * v = this * v
	 * </pre>
	 * @param v the {@link Vector} to multiply
	 * @return {@link Vector} the vector result
	 */
	public Vector multiply(Vector v) {
		double x = v.x;
		double y = v.y;
		v.x = this.m00 * x + this.m01 * y;
		v.y = this.m10 * x + this.m11 * y;
		return v;
	}
	
	/**
	 * Multiplies this {@link Matrix} by the given {@link Vector} returning
	 * the result in a new {@link Vector}.
	 * <pre>
	 * r = this * v
	 * </pre>
	 * @param v the {@link Vector} to multiply
	 * @return {@link Vector} the vector result
	 */
	public Vector product(Vector v) {
		Vector r = new Vector();
		r.x = this.m00 * v.x + this.m01 * v.y;
		r.y = this.m10 * v.x + this.m11 * v.y;
		return r;
	}
	
	/**
	 * Multiplies the given {@link Vector} by this {@link Matrix} and
	 * places the result in the given {@link Vector}.
	 * <pre>
	 * v = v<sup>T</sup> * this
	 * </pre>
	 * @param v the {@link Vector} to multiply
	 * @return {@link Vector} the vector result
	 */
	public Vector multiplyT(Vector v) {
		double x = v.x;
		double y = v.y;
		v.x = this.m00 * x + this.m10 * y;
		v.y = this.m01 * x + this.m11 * y;
		return v;
	}
	
	/**
	 * Multiplies the given {@link Vector} by this {@link Matrix} returning
	 * the result in a new {@link Vector}.
	 * <pre>
	 * r = v<sup>T</sup> * this
	 * </pre>
	 * @param v the {@link Vector} to multiply
	 * @return {@link Vector} the vector result
	 */
	public Vector productT(Vector v) {
		Vector r = new Vector();
		r.x = this.m00 * v.x + this.m10 * v.y;
		r.y = this.m01 * v.x + this.m11 * v.y;
		return r;
	}
	
	/**
	 * Multiplies this {@link Matrix} by the given scalar and places
	 * the result in this {@link Matrix}.
	 * <pre>
	 * this = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix} this matrix
	 */
	public Matrix multiply(double scalar) {
		this.m00 *= scalar;
		this.m01 *= scalar;
		this.m10 *= scalar;
		this.m11 *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link Matrix} by the given scalar returning a
	 * new {@link Matrix} containing the result.
	 * <pre>
	 * r = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix} a new matrix containing the result
	 */
	public Matrix product(double scalar) {
		// make a copy of this matrix
		Matrix rm = new Matrix(this);
		// multiply by the scalar
		rm.m00 *= scalar;
		rm.m01 *= scalar;
		rm.m10 *= scalar;
		rm.m11 *= scalar;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Divides this {@link Matrix} by the given scalar and places
	 * the result in this {@link Matrix}.
	 * <pre>
	 * this = this / scalar
	 * </pre>
	 * @param scalar the scalar to divide by
	 * @return {@link Matrix} this matrix
	 */
	public Matrix divide(double scalar) {
		// compute the inverse of the scalar once
		double inv = 1.0 / scalar;
		this.m00 *= inv;
		this.m01 *= inv;
		this.m10 *= inv;
		this.m11 *= inv;
		return this;
	}
	
	/**
	 * Divides this {@link Matrix} by the given scalar returning a
	 * new {@link Matrix} containing the result.
	 * <pre>
	 * r = this / scalar
	 * </pre>
	 * @param scalar the scalar to divide by
	 * @return {@link Matrix} a new matrix containing the result
	 */
	public Matrix quotient(double scalar) {
		// make a copy of this matrix
		Matrix rm = new Matrix(this);
		// compute the inverse of the scalar once
		double inv = 1.0 / scalar;
		// multiply by the scalar
		rm.m00 *= inv;
		rm.m01 *= inv;
		rm.m10 *= inv;
		rm.m11 *= inv;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Sets this {@link Matrix} to an identity {@link Matrix}.
	 * @return {@link Matrix} this matrix
	 */
	public Matrix identity() {
		this.m00 = 1; this.m01 = 0;
		this.m10 = 0; this.m11 = 1;
		return this;
	}
	
	/**
	 * Sets this {@link Matrix} to the transpose of this {@link Matrix}.
	 * @return {@link Matrix} this matrix
	 */
	public Matrix transpose() {
		double m = this.m01;
		this.m01 = this.m10;
		this.m10 = m;
		return this;
	}
	
	/**
	 * Returns the the transpose of this {@link Matrix} in a new {@link Matrix}.
	 * @return {@link Matrix} a new matrix contianing the transpose
	 */
	public Matrix getTranspose() {
		Matrix rm = new Matrix(this);
		rm.transpose();
		return rm;
	}
	
	/**
	 * Returns the determinant of this {@link Matrix}.
	 * @return double
	 */
	public double determinant() {
		return this.m00 * this.m11 - this.m01 * this.m10;
	}
	
	/**
	 * Performs the inverse of this {@link Matrix} and places the
	 * result in this {@link Matrix}.
	 * @return {@link Matrix} this matrix
	 */
	public Matrix invert() {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (det == 0.0) throw new ArithmeticException();
		double a = this.m00;
		double b = this.m01;
		double c = this.m10;
		double d = this.m11;
		det = 1.0 / det;
		this.m00 =  det * d;
		this.m01 = -det * b;
		this.m10 = -det * c;
		this.m11 =  det * a;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix} containing the inverse of this {@link Matrix}.
	 * @return {@link Matrix} a new matrix containing the result
	 */
	public Matrix getInverse() {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (det == 0.0) throw new ArithmeticException();
		Matrix rm = new Matrix();
		det = 1.0 / det;
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
	 * @param b the b {@link Vector}
	 * @return {@link Vector} the x vector
	 */
	public Vector solve(Vector b) {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (det == 0.0) throw new ArithmeticException();
		det = 1.0 / det;
		Vector r = new Vector();
		r.x = det * (this.m11 * b.x - this.m01 * b.y);
		r.y = det * (this.m00 * b.y - this.m10 * b.x);
		return r;
	}
}
