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

/**
 * Represents a transformation matrix.
 * <p>
 * Supported operations are rotation and translation.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Transform implements Transformable {
	/** Two times PI */
	private static final double TWO_PI = Math.PI * 2.0;
	
	/**
	 * An immutable identity transform.
	 * <p>
	 * Calling any mutator methods on this instance will result in
	 * <code>UnsupportedOperationException</code>s.
	 */
	public static final Transform IDENTITY = new Transform() {
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transform#identity()
		 */
		@Override
		public void identity() {}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transform#rotate(double)
		 */
		@Override
		public void rotate(double theta) {
			throw new UnsupportedOperationException("Cannot modify the Transform.IDENTITY object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transform#rotate(double, double, double)
		 */
		@Override
		public void rotate(double theta, double x, double y) {
			throw new UnsupportedOperationException("Cannot modify the Transform.IDENTITY object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transform#rotate(double, org.dyn4j.game2d.geometry.Vector)
		 */
		@Override
		public void rotate(double theta, Vector2 point) {
			throw new UnsupportedOperationException("Cannot modify the Transform.IDENTITY object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transform#translate(double, double)
		 */
		@Override
		public void translate(double x, double y) {
			throw new UnsupportedOperationException("Cannot modify the Transform.IDENTITY object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transform#translate(org.dyn4j.game2d.geometry.Vector)
		 */
		@Override
		public void translate(Vector2 vector) {
			throw new UnsupportedOperationException("Cannot modify the Transform.IDENTITY object.");
		}
	};
	
	/** The first row, first column entry */
	protected double m00 = 1.0;
	
	/** The first row, second column entry */
	protected double m01 = 0.0;
	
	/** The second row, first column entry */
	protected double m10 = 0.0;
	
	/** The second row, second column entry */
	protected double m11 = 1.0;
	
	/** The x translation */
	protected double x = 0.0;
	
	/** The y translation */
	protected double y = 0.0;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(m00).append(" ").append(m01).append(" | ").append(x).append("]")
		  .append("[").append(m10).append(" ").append(m11).append(" | ").append(y).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		// pre-compute cos/sin of the given angle
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		// perform an optimized version of matrix multiplication
		double m00 = cos * this.m00 - sin * this.m10;
		double m01 = cos * this.m01 - sin * this.m11;
		double m10 = sin * this.m00 + cos * this.m10;
		double m11 = sin * this.m01 + cos * this.m11;
		double x   = cos * this.x - sin * this.y;
		double y   = sin * this.x + cos * this.y;
		// set the new values
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.x   = x;
		this.y   = y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		// save the current transform value
		double cm00 = this.m00;
		double cm01 = this.m01;
		double cx = this.x;
		double cm10 = this.m10;
		double cm11 = this.m11;
		double cy = this.y;
		
		// pre-compute cos/sin of the given angle
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		// pre-compute rx and ry
		double rx = x - cos * x + sin * y;
		double ry = y - sin * x - cos * y;
		
		// perform an optimized version of the matrix multiplication:
		// M(new) = inverse(T) * R * T * M(old)
		this.m00 = cos * cm00 - sin * cm10;
		this.m01 = cos * cm01 - sin * cm11;
		this.x   = cos * cx - sin * cy + rx;
		
		this.m10 = sin * cm00 + cos * cm10;
		this.m11 = sin * cm01 + cos * cm11;
		this.y   = sin * cx + cos * cy + ry;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.rotate(theta, point.x, point.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void translate(Vector2 vector) {
		this.translate(vector.x, vector.y);
	}
	
	/**
	 * Copies this {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform copy() {
		Transform t = new Transform();
		t.m00 = this.m00; t.m01 = this.m01; t.x = this.x;
		t.m10 = this.m10; t.m11 = this.m11; t.y = this.y;
		return t;
	}
	
	/**
	 * Sets this transform to the given transform.
	 * @param transform the transform to copy
	 * @since 1.1.0
	 */
	public void set(Transform transform) {
		this.m00 = transform.m00;
		this.m01 = transform.m01;
		this.m10 = transform.m10;
		this.m11 = transform.m11;
		this.x = transform.x;
		this.y = transform.y;
	}
	
	/**
	 * Sets this {@link Transform} to the identity.
	 */
	public void identity() {
		this.m00 = 1; this.m01 = 0; this.x = 0; 
		this.m10 = 0; this.m11 = 1; this.y = 0;
	}
	
	/**
	 * Transforms the given {@link Vector2} and returns a new {@link Vector2} containing the result.
	 * @param vector the {@link Vector2} to transform
	 * @return {@link Vector2}
	 */
	public Vector2 getTransformed(Vector2 vector) {
		Vector2 tv = new Vector2();
		double x = vector.x;
		double y = vector.y;
		tv.x = this.m00 * x + this.m01 * y + this.x;
		tv.y = this.m10 * x + this.m11 * y + this.y;
		return tv;
	}
	
	/**
	 * Transforms the given {@link Vector2} and returns the result in dest.
	 * @param vector the {@link Vector2} to transform
	 * @param destination the {@link Vector2} containing the result
	 */
	public void getTransformed(Vector2 vector, Vector2 destination) {
		double x = vector.x;
		double y = vector.y;
		destination.x = this.m00 * x + this.m01 * y + this.x;
		destination.y = this.m10 * x + this.m11 * y + this.y;
	}
	
	/**
	 * Transforms the given {@link Vector2} and places the result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void transform(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.m00 * x + this.m01 * y + this.x;
		vector.y = this.m10 * x + this.m11 * y + this.y;
	}

	/**
	 * Inverse transforms the given {@link Vector2} and returns a new {@link Vector2} containing the result.
	 * @param vector the {@link Vector2} to transform
	 * @return {@link Vector2}
	 */
	public Vector2 getInverseTransformed(Vector2 vector) {
		Vector2 tv = new Vector2();
		double tx = vector.x - this.x;
		double ty = vector.y - this.y;
		tv.x = this.m00 * tx + this.m10 * ty;
		tv.y = this.m01 * tx + this.m11 * ty;
		return tv;
	}
	
	/**
	 * Inverse transforms the given {@link Vector2} and returns the result in the destination {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @param destination the {@link Vector2} containing the result
	 */
	public void getInverseTransformed(Vector2 vector, Vector2 destination) {
		double tx = vector.x - this.x;
		double ty = vector.y - this.y;
		destination.x = this.m00 * tx + this.m10 * ty;
		destination.y = this.m01 * tx + this.m11 * ty;
	}
	
	/**
	 * Inverse transforms the given {@link Vector2} and places the result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void inverseTransform(Vector2 vector) {
		double x = vector.x - this.x;
		double y = vector.y - this.y;
		vector.x = this.m00 * x + this.m10 * y;
		vector.y = this.m01 * x + this.m11 * y;
	}

	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns
	 * a new {@link Vector2} containing the result.
	 * @param vector the {@link Vector2} to transform
	 * @return {@link Vector2}
	 */
	public Vector2 getTransformedR(Vector2 vector) {
		Vector2 v = new Vector2();
		double x = vector.x;
		double y = vector.y;
		v.x = this.m00 * x + this.m01 * y;
		v.y = this.m10 * x + this.m11 * y;
		return v;
	}
	
	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the result in the
	 * destination {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @param destination the {@link Vector2} containing the result
	 */
	public void transformR(Vector2 vector, Vector2 destination) {
		double x = vector.x;
		double y = vector.y;
		destination.x = this.m00 * x + this.m01 * y;
		destination.y = this.m10 * x + this.m11 * y;
	}

	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the
	 * result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void transformR(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.m00 * x + this.m01 * y;
		vector.y = this.m10 * x + this.m11 * y;
	}
	
	/**
	 * Inverse transforms the given {@link Vector2} only by the rotation and returns
	 * a new {@link Vector2} containing the result.
	 * @param vector the {@link Vector2} to transform
	 * @return {@link Vector2}
	 */
	public Vector2 getInverseTransformedR(Vector2 vector) {
		Vector2 v = new Vector2();
		double x = vector.x;
		double y = vector.y;
		// since the transpose of a rotation matrix is the inverse
		v.x = this.m00 * x + this.m10 * y;
		v.y = this.m01 * x + this.m11 * y;
		return v;
	}
	
	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the result in the
	 * destination {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @param destination the {@link Vector2} containing the result
	 */
	public void inverseTransformR(Vector2 vector, Vector2 destination) {
		double x = vector.x;
		double y = vector.y;
		// since the transpose of a rotation matrix is the inverse
		destination.x = this.m00 * x + this.m10 * y;
		destination.y = this.m01 * x + this.m11 * y;
	}

	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the
	 * result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void inverseTransformR(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		// since the transpose of a rotation matrix is the inverse
		vector.x = this.m00 * x + this.m10 * y;
		vector.y = this.m01 * x + this.m11 * y;
	}
	
	/**
	 * Returns the x translation.
	 * @return double
	 */
	public double getTranslationX() {
		return this.x;
	}
	
	/**
	 * Sets the translation along the x axis.
	 * @param x the translation along the x axis
	 * @since 1.2.0
	 */
	public void setTranslationX(double x) {
		this.x = x;
	}

	/**
	 * Returns the x translation.
	 * @return double
	 */
	public double getTranslationY() {
		return this.y;
	}
	
	/**
	 * Sets the translation along the y axis.
	 * @param y the translation along the y axis
	 * @since 1.2.0
	 */
	public void setTranslationY(double y) {
		this.y = y;
	}
	
	/**
	 * Returns the translation {@link Vector2}.
	 * @return {@link Vector2}
	 */
	public Vector2 getTranslation() {
		return new Vector2(this.x, this.y);
	}
	
	/**
	 * Sets the translation.
	 * @param x the translation along the x axis
	 * @param y the translation along the y axis
	 * @since 1.2.0
	 */
	public void setTranslation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the translation.
	 * @param translation the translation along both axes
	 * @since 1.2.0
	 */
	public void setTranslation(Vector2 translation) {
		this.setTranslation(translation.x, translation.y);
	}
	
	/**
	 * Returns a new {@link Transform} including only the
	 * translation of this {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getTranslationTransform() {
		Transform t = new Transform();
		t.translate(this.x, this.y);
		return t;
	}
	
	/**
	 * Returns the rotation.
	 * @return double angle in the range [-pi, pi]
	 */
	public double getRotation() {
		return Math.atan2(this.m10, this.m00);
	}
	
	/**
	 * Sets the rotation and returns the previous
	 * rotation.
	 * @param theta the angle in radians
	 * @since 1.2.0
	 */
	public void setRotation(double theta) {
		// get the current rotation
		double r = this.getRotation();
		// get rid of the current rotation
		this.rotate(-r);
		// rotate the given amount
		this.rotate(theta);
	}
	
	/**
	 * Returns a new {@link Transform} including only the
	 * rotation of this {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getRotationTransform() {
		Transform t = new Transform();
		t.rotate(this.getRotation());
		return t;
	}
	
	/**
	 * Interpolates this transform linearly by alpha towards the given end transform.
	 * <p>
	 * Interpolating from one angle to another can have two results depending on the
	 * direction of the rotation.  If a rotation was from 30 to 200 the rotation could
	 * be 170 or -190.  This interpolation method will always choose the smallest
	 * rotation (regardless of sign) as the rotation direction.
	 * @param end the end transform
	 * @param alpha the amount to interpolate
	 * @since 1.2.0
	 */
	public void lerp(Transform end, double alpha) {
		// interpolate the position
		double x = (1.0 - alpha) * this.x + alpha * end.x;
		double y = (1.0 - alpha) * this.y + alpha * end.y;
		
		// compute the angle
		// get the start and end rotations
		// its key that these methods use atan2 because
		// it ensures that the angles are always within
		// the range -pi < theta < pi therefore no
		// normalization has to be done
		double rs = this.getRotation();
		double re = end.getRotation();
		// make sure we use the smallest rotation
		// as described in the comments above, there
		// are two possible rotations depending on the
		// direction, we always choose the smaller
		double diff = re - rs;
		if (diff < -Math.PI) diff += TWO_PI;
		if (diff > Math.PI) diff -= TWO_PI;
		// interpolate
		// its ok if this method produces an angle
		// outside the range of -pi < theta < pi
		// since the rotate method uses sin and cos
		// which are not bounded
		double a = diff * alpha + rs;
		
		// set this transform to the interpolated transform
		this.identity();
		this.rotate(a);
		this.translate(x, y);
	}
	
	/**
	 * Interpolates linearly by alpha towards the given end transform placing
	 * the result in the given transform.
	 * <p>
	 * Interpolating from one angle to another can have two results depending on the
	 * direction of the rotation.  If a rotation was from 30 to 200 the rotation could
	 * be 170 or -190.  This interpolation method will always choose the smallest
	 * rotation (regardless of sign) as the rotation direction.
	 * @param end the end transform
	 * @param alpha the amount to interpolate
	 * @param result the transform to place the result
	 * @since 1.2.0
	 */
	public void lerp(Transform end, double alpha, Transform result) {
		// interpolate the position
		double x = (1.0 - alpha) * this.x + alpha * end.x;
		double y = (1.0 - alpha) * this.y + alpha * end.y;
		
		// compute the angle
		// get the start and end rotations
		// its key that these methods use atan2 because
		// it ensures that the angles are always within
		// the range -pi < theta < pi therefore no
		// normalization has to be done
		double rs = this.getRotation();
		double re = end.getRotation();
		// make sure we use the smallest rotation
		// as described in the comments above, there
		// are two possible rotations depending on the
		// direction, we always choose the smaller
		double diff = re - rs;
		if (diff < -Math.PI) diff += TWO_PI;
		if (diff > Math.PI) diff -= TWO_PI;
		// interpolate
		// its ok if this method produces an angle
		// outside the range of -pi < theta < pi
		// since the rotate method uses sin and cos
		// which are not bounded
		double a = diff * alpha + rs;
		
		// set the result transform to the interpolated transform
		result.identity();
		result.rotate(a);
		result.translate(x, y);
	}
	
	/**
	 * Interpolates linearly by alpha towards the given end transform returning
	 * a new transform containing the result.
	 * <p>
	 * Interpolating from one angle to another can have two results depending on the
	 * direction of the rotation.  If a rotation was from 30 to 200 the rotation could
	 * be 170 or -190.  This interpolation method will always choose the smallest
	 * rotation (regardless of sign) as the rotation direction.
	 * @param end the end transform
	 * @param alpha the amount to interpolate
	 * @return {@link Transform} the resulting transform
	 * @since 1.2.0
	 */
	public Transform lerped(Transform end, double alpha) {
		// interpolate the position
		double x = (1.0 - alpha) * this.x + alpha * end.x;
		double y = (1.0 - alpha) * this.y + alpha * end.y;
		
		// compute the angle
		// get the start and end rotations
		// its key that these methods use atan2 because
		// it ensures that the angles are always within
		// the range -pi < theta < pi therefore no
		// normalization has to be done
		double rs = this.getRotation();
		double re = end.getRotation();
		// make sure we use the smallest rotation
		// as described in the comments above, there
		// are two possible rotations depending on the
		// direction, we always choose the smaller
		double diff = re - rs;
		if (diff < -Math.PI) diff += TWO_PI;
		if (diff > Math.PI) diff -= TWO_PI;
		// interpolate
		// its ok if this method produces an angle
		// outside the range of -pi < theta < pi
		// since the rotate method uses sin and cos
		// which are not bounded
		double a = diff * alpha + rs;
		
		// create the interpolated transform
		Transform tx = new Transform();
		tx.rotate(a);
		tx.translate(x, y);
		return tx;
	}
}
