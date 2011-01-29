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
 * Represents a 3x3 Matrix.
 * <p>
 * Used to solve 3x3 systems of equations.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Matrix33 {
	/** The element at 0,0 */
	public double m00;
	
	/** The element at 0,1 */
	public double m01;
	
	/** The element at 0,2 */
	public double m02;
	
	/** The element at 1,0 */
	public double m10;
	
	/** The element at 1,1 */
	public double m11;
	
	/** The element at 1,2 */
	public double m12;
	
	/** The element at 2,0 */
	public double m20;
	
	/** The element at 2,1 */
	public double m21;
	
	/** The element at 2,2 */
	public double m22;
	
	/**
	 * Default constructor.
	 */
	public Matrix33() {}
	
	/**
	 * Full constructor.
	 * @param m00 the element at 0,0
	 * @param m01 the element at 0,1
	 * @param m02 the element at 0,2
	 * @param m10 the element at 1,0
	 * @param m11 the element at 1,1
	 * @param m12 the element at 1,2
	 * @param m20 the element at 2,0
	 * @param m21 the element at 2,1
	 * @param m22 the element at 2,2
	 */
	public Matrix33(double m00, double m01, double m02,
			        double m10, double m11, double m12,
			        double m20, double m21, double m22) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The given array should be in the same order as the 
	 * {@link #Matrix33(double, double, double, double, double, double, double, double, double)} constructor.
	 * @param values the values array
	 * @throws NullPointerException if values is null
	 * @throws IllegalArgumentException if values is not length 9
	 */
	public Matrix33(double[] values) {
		if (values == null) throw new NullPointerException("The values array cannot be null.");
		if (values.length != 9) throw new IndexOutOfBoundsException("The values array must be of length 9.");
		this.m00 = values[0];
		this.m01 = values[1];
		this.m02 = values[2];
		this.m10 = values[3];
		this.m11 = values[4];
		this.m12 = values[5];
		this.m20 = values[6];
		this.m21 = values[7];
		this.m22 = values[8];
	}
	
	/**
	 * Copy constructor.
	 * @param matrix the {@link Matrix33} to copy
	 */
	public Matrix33(Matrix33 matrix) {
		this.m00 = matrix.m00; this.m01 = matrix.m01; this.m02 = matrix.m02;
		this.m10 = matrix.m10; this.m11 = matrix.m11; this.m12 = matrix.m12;
		this.m20 = matrix.m20; this.m21 = matrix.m21; this.m22 = matrix.m22;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Matrix33) {
			Matrix33 other = (Matrix33) obj;
			if (other.m00 == this.m00
			 && other.m01 == this.m01
			 && other.m02 == this.m02
			 && other.m10 == this.m10
			 && other.m11 == this.m11
			 && other.m12 == this.m12
			 && other.m20 == this.m20
			 && other.m21 == this.m21
			 && other.m22 == this.m22) {
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
		sb.append("[").append(this.m00).append(" ").append(this.m01).append(" ").append(this.m02).append("][")
		.append(this.m10).append(" ").append(this.m11).append(" ").append(this.m12).append("][")
		.append(this.m20).append(" ").append(this.m21).append(" ").append(this.m22).append("]");
		return sb.toString();
	}
	
	/**
	 * Adds the given {@link Matrix33} to this {@link Matrix33}
	 * returning this {@link Matrix33}.
	 * <pre>
	 * this = this + m
	 * </pre>
	 * @param matrix the {@link Matrix33} to add
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 add(Matrix33 matrix) {
		this.m00 += matrix.m00; this.m01 += matrix.m01; this.m02 += matrix.m02;
		this.m10 += matrix.m10; this.m11 += matrix.m11; this.m12 += matrix.m12;
		this.m20 += matrix.m20; this.m21 += matrix.m21; this.m22 += matrix.m22;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix33} that is the sum of this {@link Matrix33}
	 * and the given {@link Matrix33}.
	 * <pre>
	 * r = this + m
	 * </pre>
	 * @param matrix the {@link Matrix33} to add
	 * @return {@link Matrix33} a new matrix containing the result
	 */
	public Matrix33 sum(Matrix33 matrix) {
		// make a copy of this matrix
		Matrix33 rm = new Matrix33(this);
		// perform the addition
		rm.m00 += matrix.m00; rm.m01 += matrix.m01; rm.m02 += matrix.m02;
		rm.m10 += matrix.m10; rm.m11 += matrix.m11; rm.m12 += matrix.m12;
		rm.m20 += matrix.m20; rm.m21 += matrix.m21; rm.m22 += matrix.m22;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Subtracts the given {@link Matrix33} from this {@link Matrix33}
	 * returning this {@link Matrix33}.
	 * <pre>
	 * this = this - m
	 * </pre>
	 * @param matrix the {@link Matrix33} to subtract
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 subtract(Matrix33 matrix) {
		this.m00 -= matrix.m00; this.m01 -= matrix.m01; this.m02 -= matrix.m02;
		this.m10 -= matrix.m10; this.m11 -= matrix.m11; this.m12 -= matrix.m12;
		this.m20 -= matrix.m20; this.m21 -= matrix.m21; this.m22 -= matrix.m22;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix33} that is the difference of this {@link Matrix33}
	 * and the given {@link Matrix33}.
	 * <pre>
	 * r = this - m
	 * </pre>
	 * @param matrix the {@link Matrix33} to subtract
	 * @return {@link Matrix33} a new matrix containing the result
	 */
	public Matrix33 difference(Matrix33 matrix) {
		// make a copy of this matrix
		Matrix33 rm = new Matrix33(this);
		// perform the subtraction
		rm.m00 -= matrix.m00; rm.m01 -= matrix.m01; rm.m02 -= matrix.m02;
		rm.m10 -= matrix.m10; rm.m11 -= matrix.m11; rm.m12 -= matrix.m12;
		rm.m20 -= matrix.m20; rm.m21 -= matrix.m21; rm.m22 -= matrix.m22;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Multiplies this {@link Matrix33} by the given matrix {@link Matrix33}
	 * returning this {@link Matrix33}.
	 * <pre>
	 * this = this * m
	 * </pre>
	 * @param matrix the {@link Matrix33} to subtract
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 multiply(Matrix33 matrix) {
		double m00 = this.m00;
		double m01 = this.m01;
		double m02 = this.m02;
		double m10 = this.m10;
		double m11 = this.m11;
		double m12 = this.m12;
		double m20 = this.m20;
		double m21 = this.m21;
		double m22 = this.m22;
		// row 1
		this.m00 = m00 * matrix.m00 + m01 * matrix.m10 + m02 * matrix.m20;
		this.m01 = m00 * matrix.m01 + m01 * matrix.m11 + m02 * matrix.m21;
		this.m02 = m00 * matrix.m02 + m01 * matrix.m12 + m02 * matrix.m22;
		// row 2
		this.m10 = m10 * matrix.m00 + m11 * matrix.m10 + m12 * matrix.m20;
		this.m11 = m10 * matrix.m01 + m11 * matrix.m11 + m12 * matrix.m21;
		this.m12 = m10 * matrix.m02 + m11 * matrix.m12 + m12 * matrix.m22;
		// row 3
		this.m20 = m20 * matrix.m00 + m21 * matrix.m10 + m22 * matrix.m20;
		this.m21 = m20 * matrix.m01 + m21 * matrix.m11 + m22 * matrix.m21;
		this.m22 = m20 * matrix.m02 + m21 * matrix.m12 + m22 * matrix.m22;
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix33} that is the product of this {@link Matrix33}
	 * and the given {@link Matrix33}.
	 * <pre>
	 * r = this * m
	 * </pre>
	 * @param matrix the {@link Matrix33} to multiply
	 * @return {@link Matrix33} a new matrix containing the result
	 */
	public Matrix33 product(Matrix33 matrix) {
		Matrix33 rm = new Matrix33();
		// row 1
		rm.m00 = this.m00 * matrix.m00 + this.m01 * matrix.m10 + this.m02 * matrix.m20;
		rm.m01 = this.m00 * matrix.m01 + this.m01 * matrix.m11 + this.m02 * matrix.m21;
		rm.m02 = this.m00 * matrix.m02 + this.m01 * matrix.m12 + this.m02 * matrix.m22;
		// row 2
		rm.m10 = this.m10 * matrix.m00 + this.m11 * matrix.m10 + this.m12 * matrix.m20;
		rm.m11 = this.m10 * matrix.m01 + this.m11 * matrix.m11 + this.m12 * matrix.m21;
		rm.m12 = this.m10 * matrix.m02 + this.m11 * matrix.m12 + this.m12 * matrix.m22;
		// row 3
		rm.m20 = this.m20 * matrix.m00 + this.m21 * matrix.m10 + this.m22 * matrix.m20;
		rm.m21 = this.m20 * matrix.m01 + this.m21 * matrix.m11 + this.m22 * matrix.m21;
		rm.m22 = this.m20 * matrix.m02 + this.m21 * matrix.m12 + this.m22 * matrix.m22;
		return rm;
	}
	
	/**
	 * Multiplies this {@link Matrix33} by the given {@link Vector3} and
	 * places the result in the given {@link Vector3}.
	 * <pre>
	 * v = this * v
	 * </pre>
	 * @param vector the {@link Vector3} to multiply
	 * @return {@link Vector3} the vector result
	 */
	public Vector3 multiply(Vector3 vector) {
		double x = vector.x;
		double y = vector.y;
		double z = vector.z;
		vector.x = this.m00 * x + this.m01 * y + this.m02 * z;
		vector.y = this.m10 * x + this.m11 * y + this.m12 * z;
		vector.z = this.m20 * x + this.m21 * y + this.m22 * z;
		return vector;
	}
	
	/**
	 * Multiplies this {@link Matrix33} by the given {@link Vector3} returning
	 * the result in a new {@link Vector3}.
	 * <pre>
	 * r = this * v
	 * </pre>
	 * @param vector the {@link Vector3} to multiply
	 * @return {@link Vector3} the vector result
	 */
	public Vector3 product(Vector3 vector) {
		Vector3 r = new Vector3();
		r.x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z;
		r.y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z;
		r.z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z;
		return r;
	}
	
	/**
	 * Multiplies the given {@link Vector3} by this {@link Matrix33} and
	 * places the result in the given {@link Vector3}.
	 * <pre>
	 * v = v<sup>T</sup> * this
	 * </pre>
	 * @param vector the {@link Vector3} to multiply
	 * @return {@link Vector3} the vector result
	 */
	public Vector3 multiplyT(Vector3 vector) {
		double x = vector.x;
		double y = vector.y;
		double z = vector.z;
		vector.x = this.m00 * x + this.m10 * y + this.m20 * z;
		vector.y = this.m01 * x + this.m11 * y + this.m21 * z;
		vector.z = this.m02 * x + this.m12 * y + this.m22 * z;
		return vector;
	}
	
	/**
	 * Multiplies the given {@link Vector3} by this {@link Matrix33} returning
	 * the result in a new {@link Vector3}.
	 * <pre>
	 * r = v<sup>T</sup> * this
	 * </pre>
	 * @param vector the {@link Vector3} to multiply
	 * @return {@link Vector3} the vector result
	 */
	public Vector3 productT(Vector3 vector) {
		Vector3 r = new Vector3();
		r.x = this.m00 * vector.x + this.m10 * vector.y + this.m20 * vector.z;
		r.y = this.m01 * vector.x + this.m11 * vector.y + this.m21 * vector.z;
		r.z = this.m02 * vector.x + this.m12 * vector.y + this.m22 * vector.z;
		return r;
	}
	
	/**
	 * Multiplies this {@link Matrix33} by the given scalar and places
	 * the result in this {@link Matrix33}.
	 * <pre>
	 * this = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 multiply(double scalar) {
		this.m00 *= scalar;
		this.m01 *= scalar;
		this.m02 *= scalar;
		this.m10 *= scalar;
		this.m11 *= scalar;
		this.m12 *= scalar;
		this.m20 *= scalar;
		this.m21 *= scalar;
		this.m22 *= scalar;
		return this;
	}
	
	/**
	 * Multiplies this {@link Matrix33} by the given scalar returning a
	 * new {@link Matrix33} containing the result.
	 * <pre>
	 * r = this * scalar
	 * </pre>
	 * @param scalar the scalar to multiply by
	 * @return {@link Matrix33} a new matrix containing the result
	 */
	public Matrix33 product(double scalar) {
		// make a copy of this matrix
		Matrix33 rm = new Matrix33(this);
		// multiply by the scalar
		rm.m00 *= scalar;
		rm.m01 *= scalar;
		rm.m02 *= scalar;
		rm.m10 *= scalar;
		rm.m11 *= scalar;
		rm.m12 *= scalar;
		rm.m20 *= scalar;
		rm.m21 *= scalar;
		rm.m22 *= scalar;
		// return the new matrix
		return rm;
	}
	
	/**
	 * Sets this {@link Matrix33} to an identity {@link Matrix33}.
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 identity() {
		this.m00 = 1; this.m01 = 0; this.m02 = 0;
		this.m10 = 0; this.m11 = 1; this.m12 = 0;
		this.m20 = 0; this.m21 = 0; this.m22 = 1;
		return this;
	}
	
	/**
	 * Sets this {@link Matrix33} to the transpose of this {@link Matrix33}.
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 transpose() {
		double s;
		// switch 01 and 10
		s = this.m01; this.m01 = this.m10; this.m10 = s;
		// switch 02 and 20
		s = this.m02; this.m02 = this.m20; this.m20 = s;
		// switch 12 and 21
		s = this.m12; this.m12 = this.m21; this.m21 = s;
		return this;
	}
	
	/**
	 * Returns the the transpose of this {@link Matrix33} in a new {@link Matrix33}.
	 * @return {@link Matrix33} a new matrix contianing the transpose
	 */
	public Matrix33 getTranspose() {
		Matrix33 rm = new Matrix33();
		rm.m00 = this.m00; rm.m01 = this.m10; rm.m02 = this.m20;
		rm.m10 = this.m01; rm.m11 = this.m11; rm.m12 = this.m21;
		rm.m20 = this.m02; rm.m21 = this.m12; rm.m22 = this.m22;
		return rm;
	}
	
	/**
	 * Returns the determinant of this {@link Matrix33}.
	 * @return double
	 */
	public double determinant() {
		return this.m00 * this.m11 * this.m22 +
		       this.m01 * this.m12 * this.m20 +
		       this.m02 * this.m10 * this.m21 -
		       this.m20 * this.m11 * this.m02 -
		       this.m21 * this.m12 * this.m00 -
		       this.m22 * this.m10 * this.m01;
	}
	
	/**
	 * Performs the inverse of this {@link Matrix33} and places the
	 * result in this {@link Matrix33}.
	 * @return {@link Matrix33} this matrix
	 */
	public Matrix33 invert() {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) >= Epsilon.E) {
			det = 1.0 / det;
		}
		
		// compute the cofactor determinants and apply the signs
		// and transpose the matrix and multiply by the inverse 
		// of the determinant
		double m00 =  det * (this.m11 * this.m22 - this.m12 * this.m21);
		double m01 = -det * (this.m01 * this.m22 - this.m21 * this.m02); // actually m10 in the cofactor matrix
		double m02 =  det * (this.m01 * this.m12 - this.m11 * this.m02); // actually m20 in the cofactor matrix
		
		double m10 = -det * (this.m10 * this.m22 - this.m20 * this.m12); // actually m01 in the cofactor matrix
		double m11 =  det * (this.m00 * this.m22 - this.m20 * this.m02);
		double m12 = -det * (this.m00 * this.m12 - this.m10 * this.m02); // actually m21 in the cofactor matrix
		
		double m20 =  det * (this.m10 * this.m21 - this.m20 * this.m11); // actually m02 in the cofactor matrix
		double m21 = -det * (this.m00 * this.m21 - this.m20 * this.m01); // actually m12 in the cofactor matrix
		double m22 =  det * (this.m00 * this.m11 - this.m10 * this.m01);
		
		this.m00 = m00; this.m01 = m01; this.m02 = m02;
		this.m10 = m10; this.m11 = m11; this.m12 = m12;
		this.m20 = m20; this.m21 = m21; this.m22 = m22;
		
		return this;
	}
	
	/**
	 * Returns a new {@link Matrix33} containing the inverse of this {@link Matrix33}.
	 * @return {@link Matrix33} a new matrix containing the result
	 */
	public Matrix33 getInverse() {
		Matrix33 rm = new Matrix33();
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) >= Epsilon.E) {
			det = 1.0 / det;
		}
		
		// compute the cofactor determinants and apply the signs
		// and transpose the matrix and multiply by the inverse 
		// of the determinant
		rm.m00 =  det * (this.m11 * this.m22 - this.m12 * this.m21);
		rm.m01 = -det * (this.m01 * this.m22 - this.m21 * this.m02); // actually m10 in the cofactor matrix
		rm.m02 =  det * (this.m01 * this.m12 - this.m11 * this.m02); // actually m20 in the cofactor matrix
		
		rm.m10 = -det * (this.m10 * this.m22 - this.m20 * this.m12); // actually m01 in the cofactor matrix
		rm.m11 =  det * (this.m00 * this.m22 - this.m20 * this.m02);
		rm.m12 = -det * (this.m00 * this.m12 - this.m10 * this.m02); // actually m21 in the cofactor matrix
		
		rm.m20 =  det * (this.m10 * this.m21 - this.m20 * this.m11); // actually m02 in the cofactor matrix
		rm.m21 = -det * (this.m00 * this.m21 - this.m20 * this.m01); // actually m12 in the cofactor matrix
		rm.m22 =  det * (this.m00 * this.m11 - this.m10 * this.m01);
		
		return rm;
	}

	/**
	 * Solves the system of linear equations:
	 * <pre>
	 * Ax = b
	 * Multiply by A<sup>-1</sup> on both sides
	 * x = A<sup>-1</sup>b
	 * </pre>
	 * @param b the b {@link Vector3}
	 * @return {@link Vector3} the x vector
	 */
	public Vector3 solve33(Vector3 b) {
		// get the determinant
		double det = this.determinant();
		// check for zero determinant
		if (Math.abs(det) >= Epsilon.E) {
			det = 1.0 / det;
		}
		Vector3 r = new Vector3();
		
		double m00 =  this.m11 * this.m22 - this.m12 * this.m21;
		double m01 = -this.m01 * this.m22 + this.m21 * this.m02;
		double m02 =  this.m01 * this.m12 - this.m11 * this.m02;
		
		double m10 = -this.m10 * this.m22 + this.m20 * this.m12;
		double m11 =  this.m00 * this.m22 - this.m20 * this.m02;
		double m12 = -this.m00 * this.m12 + this.m10 * this.m02;
		
		double m20 =  this.m10 * this.m21 - this.m20 * this.m11;
		double m21 = -this.m00 * this.m21 + this.m20 * this.m01;
		double m22 =  this.m00 * this.m11 - this.m10 * this.m01;
		
		r.x = det * (m00 * b.x + m01 * b.y + m02 * b.z);
		r.y = det * (m10 * b.x + m11 * b.y + m12 * b.z);
		r.z = det * (m20 * b.x + m21 * b.y + m22 * b.z);
		
		return r;
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
	public Vector2 solve22(Vector2 b) {
		// get the 2D determinant
		double det = this.m00 * this.m11 - this.m01 * this.m10;
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
