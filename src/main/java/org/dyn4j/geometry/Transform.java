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

import org.dyn4j.Copyable;

/**
 * Represents a transformation matrix.
 * <p>
 * Supported operations are rotation and translation.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class Transform implements Transformable, Copyable<Transform> {
	/**
	 * NOTE: as of being deprecated this instance is no longer immutable.
	 * @deprecated create your own instances of {@link Transform} instead; since 3.4.0
	 */
	@Deprecated
	public static final Transform IDENTITY = new Transform();
	
	/** the cosine of the rotation angle */
	protected double cost = 1.0;
	
	/** the sine of the rotation angle */
	protected double sint = 0.0;
	
	/** The x translation */
	protected double x = 0.0;
	
	/** The y translation */
	protected double y = 0.0;

	/**
	 * Default public constructor
	 */
	public Transform() {
		
	}
	
	/**
	 * Public copy constructor constructor
	 * @param transform the transform to copy
	 * @since 3.4.0
	 */
	public Transform(Transform transform) {
		this.cost = transform.cost;
		this.sint = transform.sint;
		this.x = transform.x;
		this.y = transform.y;
	}
	
	/**
	 * Private constructor for some copy and internal operations
	 * @param cost the cosine
	 * @param sint the negative sine
	 * @param x the x translation
	 * @param y the y translation
	 * @since 3.4.0
	 */
	private Transform(double cost, double sint, double x, double y) {
		this.cost = cost;
		this.sint = sint;
		this.x = x;
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.cost).append(" ").append(-this.sint).append(" | ").append(this.x).append("]")
		  .append("[").append(this.sint).append(" ").append(this.cost).append(" | ").append(this.y).append("]");
		return sb.toString();
	}
	
	/**
	 * Internal helper method to rotate this {@link Transform} by an angle &thetasym;
	 * @param c cos(&thetasym;)
	 * @param s sin(&thetasym;)
	 * @since 3.4.0
	 */
	void rotate(double c, double s) {
		// perform an optimized version of matrix multiplication
		double cost = Interval.clamp(c * this.cost - s * this.sint, -1.0, 1.0);
		double sint = Interval.clamp(s * this.cost + c * this.sint, -1.0, 1.0);
		double x   = c * this.x - s * this.y;
		double y   = s * this.x + c * this.y;
		
		// set the new values
		this.cost = cost;
		this.sint = sint;
		this.x   = x;
		this.y   = y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.rotate(Math.cos(theta), Math.sin(theta));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(org.dyn4j.geometry.Rotation)
	 */
	@Override
	public void rotate(Rotation rotation) {
		this.rotate(rotation.cost, rotation.sint);
	}
	
	/**
	 * Internal helper method to rotate this {@link Transform} by an angle &thetasym; around a point
	 * @param c cos(&thetasym;)
	 * @param s sin(&thetasym;)
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @since 3.4.0
	 */
	void rotate(double c, double s, double x, double y) {
		// perform an optimized version of the matrix multiplication:
		// M(new) = inverse(T) * R * T * M(old)
		double cost = Interval.clamp(c * this.cost - s * this.sint, -1.0, 1.0);
		double sint = Interval.clamp(s * this.cost + c * this.sint, -1.0, 1.0);
		this.cost = cost;
		this.sint = sint;
		
		double cx = this.x - x;
		double cy = this.y - y;
		this.x = c * cx - s * cy + x;
		this.y = s * cx + c * cy + y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.rotate(Math.cos(theta), Math.sin(theta), x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	public void rotate(Rotation rotation, double x, double y) {
		this.rotate(rotation.cost, rotation.sint, x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.rotate(theta, point.x, point.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(org.dyn4j.geometry.Rotation, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(Rotation rotation, Vector2 point) {
		this.rotate(rotation, point.x, point.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(org.dyn4j.geometry.Vector)
	 */
	@Override
	public void translate(Vector2 vector) {
		this.x += vector.x;
		this.y += vector.y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	public Transform copy() {
		return new Transform(this);
	}
	
	/**
	 * Sets this transform to the given transform.
	 * @param transform the transform to copy
	 * @since 1.1.0
	 */
	public void set(Transform transform) {
		this.cost = transform.cost;
		this.sint = transform.sint;
		this.x = transform.x;
		this.y = transform.y;
	}
	
	/**
	 * Sets this {@link Transform} to the identity.
	 */
	public void identity() {
		this.cost = 1;
		this.sint = 0;
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Transforms only the x coordinate of the given {@link Vector2} and returns the result.
	 * @param vector the {@link Vector2} to transform
	 * @return the transformed x coordinate
	 * @since 3.4.0
	 */
	public double getTransformedX(Vector2 vector) {
		return this.cost * vector.x - this.sint * vector.y + this.x;
	}
	
	/**
	 * Transforms only the y coordinate of the given {@link Vector2} and returns the result.
	 * @param vector the {@link Vector2} to transform
	 * @return the transformed y coordinate
	 * @since 3.4.0
	 */
	public double getTransformedY(Vector2 vector) {
		return this.sint * vector.x + this.cost * vector.y + this.y;
	}
	
	/**
	 * Transforms only the x coordinate of the given {@link Vector2} and places the result in the x field of the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @since 3.4.0
	 */
	public void transformX(Vector2 vector) {
		vector.x = this.cost * vector.x - this.sint * vector.y + this.x;
	}
	
	/**
	 * Transforms only the y coordinate of the given {@link Vector2} and places the result in the y field of the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @since 3.4.0
	 */
	public void transformY(Vector2 vector) {
		vector.y = this.sint * vector.x + this.cost * vector.y + this.y;
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
		
		tv.x = this.cost * x - this.sint * y + this.x;
		tv.y = this.sint * x + this.cost * y + this.y;
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
		destination.x = this.cost * x - this.sint * y + this.x;
		destination.y = this.sint * x + this.cost * y + this.y;
	}
	
	/**
	 * Transforms the given {@link Vector2} and places the result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void transform(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.cost * x - this.sint * y + this.x;
		vector.y = this.sint * x + this.cost * y + this.y;
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
		tv.x = this.cost * tx + this.sint * ty;
		tv.y = -this.sint * tx + this.cost * ty;
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
		destination.x = this.cost * tx + this.sint * ty;
		destination.y = -this.sint * tx + this.cost * ty;
	}
	
	/**
	 * Inverse transforms the given {@link Vector2} and places the result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void inverseTransform(Vector2 vector) {
		double x = vector.x - this.x;
		double y = vector.y - this.y;
		vector.x = this.cost * x + this.sint * y;
		vector.y = -this.sint * x + this.cost * y;
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
		v.x = this.cost * x - this.sint * y;
		v.y = this.sint * x + this.cost * y;
		return v;
	}
	
	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the result in the
	 * destination {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @param destination the {@link Vector2} containing the result
	 * @since 3.1.5
	 */
	public void getTransformedR(Vector2 vector, Vector2 destination) {
		double x = vector.x;
		double y = vector.y;
		destination.x = this.cost * x - this.sint * y;
		destination.y = this.sint * x + this.cost * y;
	}

	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the
	 * result in the given {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 */
	public void transformR(Vector2 vector) {
		double x = vector.x;
		double y = vector.y;
		vector.x = this.cost * x - this.sint * y;
		vector.y = this.sint * x + this.cost * y;
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
		v.x = this.cost * x + this.sint * y;
		v.y = -this.sint * x + this.cost * y;
		return v;
	}
	
	/**
	 * Transforms the given {@link Vector2} only by the rotation and returns the result in the
	 * destination {@link Vector2}.
	 * @param vector the {@link Vector2} to transform
	 * @param destination the {@link Vector2} containing the result
	 * @since 3.1.5
	 */
	public void getInverseTransformedR(Vector2 vector, Vector2 destination) {
		double x = vector.x;
		double y = vector.y;
		// since the transpose of a rotation matrix is the inverse
		destination.x = this.cost * x + this.sint * y;
		destination.y = -this.sint * x + this.cost * y;
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
		vector.x = this.cost * x + this.sint * y;
		vector.y = -this.sint * x + this.cost * y;
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
		Transform t = new Transform(1.0, 0.0, this.x, this.y);
		return t;
	}
	
	/**
	 * Returns the rotation.
	 * @return double angle in the range [-&pi;, &pi;]
	 */
	public double getRotationAngle() {
		// Copied from Rotation class; See there for more info
		double acos = Math.acos(this.cost);
		double angle = (this.sint >= 0)? acos: -acos;
		return angle;
	}
	
	/**
	 * @return the {@link Rotation} object representing the rotation of this {@link Transform}
	 * @since 3.4.0
	 */
	public Rotation getRotation() {
		return Rotation.of(this);
	}
	
	/**
	 * Sets the rotation and returns the previous
	 * rotation.
	 * @param theta the angle in radians
	 * @return double the old rotation in radians in the range [-&pi;, &pi;] 
	 * @since 3.1.0
	 */
	public double setRotation(double theta) {
		// get the current rotation
		double r = this.getRotationAngle();
		
		// get rid of the current rotation and rotate by the new theta
		this.cost = Math.cos(theta);
		this.sint = Math.sin(theta);
		
		// return the previous amount
		return r;
	}
	
	/**
	 * Sets the rotation and returns the previous
	 * rotation.
	 * @param rotation the {@link Rotation}
	 * @return A new {@link Rotation} object representing the old rotation of this {@link Transform}
	 * @since 3.4.0
	 */
	public Rotation setRotation(Rotation rotation) {
		// get the current rotation
		Rotation r = getRotation();
		
		// get rid of the current rotation and rotate by the new rotation
		this.cost = rotation.cost;
		this.sint = rotation.sint;
		
		// return the previous rotation object
		return r;
	}
	
	/**
	 * Returns a new {@link Transform} including only the
	 * rotation of this {@link Transform}.
	 * @return {@link Transform}
	 */
	public Transform getRotationTransform() {
		Transform t = new Transform(this.cost, this.sint, 0, 0);
		return t;
	}
	
	/**
	 * Returns the values stored in this transform.
	 * <p>
	 * The values are in the order of 00, 01, x, 10, 11, y.
	 * @return double[]
	 * @since 3.0.1
	 */
	public double[] getValues() {
		return new double[] {this.cost, -this.sint, this.x,
				             this.sint, this.cost, this.y};
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
		double x = this.x + alpha * (end.x - this.x);
		double y = this.y + alpha * (end.y - this.y);
		
		// compute the angle
		// get the start and end rotations
		// its key that these methods use atan2 because
		// it ensures that the angles are always within
		// the range -pi < theta < pi therefore no
		// normalization has to be done
		double rs = this.getRotationAngle();
		double re = end.getRotationAngle();
		// make sure we use the smallest rotation
		// as described in the comments above, there
		// are two possible rotations depending on the
		// direction, we always choose the smaller
		double diff = re - rs;
		if (diff < -Math.PI) diff += Geometry.TWO_PI;
		if (diff > Math.PI) diff -= Geometry.TWO_PI;
		// interpolate
		// its ok if this method produces an angle
		// outside the range of -pi < theta < pi
		// since the rotate method uses sin and cos
		// which are not bounded
		double a = diff * alpha + rs;
		
		// set this transform to the interpolated transform
		// the following performs the following calculations:
		// this.identity();
		// this.rotate(a);
		// this.translate(x, y);
		
		this.cost = Math.cos(a);
		this.sint = Math.sin(a);
		this.x   = x;
		this.y   = y;
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
		double x = this.x + alpha * (end.x - this.x);
		double y = this.y + alpha * (end.y - this.y);
		
		// compute the angle
		// get the start and end rotations
		// its key that these methods use atan2 because
		// it ensures that the angles are always within
		// the range -pi < theta < pi therefore no
		// normalization has to be done
		double rs = this.getRotationAngle();
		double re = end.getRotationAngle();
		// make sure we use the smallest rotation
		// as described in the comments above, there
		// are two possible rotations depending on the
		// direction, we always choose the smaller
		double diff = re - rs;
		if (diff < -Math.PI) diff += Geometry.TWO_PI;
		if (diff > Math.PI) diff -= Geometry.TWO_PI;
		// interpolate
		// its ok if this method produces an angle
		// outside the range of -pi < theta < pi
		// since the rotate method uses sin and cos
		// which are not bounded
		double a = diff * alpha + rs;
		
		// set the result transform to the interpolated transform
		// the following performs the following calculations:
		// result.identity();
		// result.rotate(a);
		// result.translate(x, y);
		
		result.cost = Math.cos(a);
		result.sint = Math.sin(a);
		result.x   = x;
		result.y   = y;
	}
	
	/**
	 * Helper method for the lerp methods below.
	 * Performs rotation but leaves translation intact.
	 * @param theta the angle of rotation in radians
	 * @since 3.4.0
	 */
	private void rotateOnly(double theta) {
		//perform rotation by theta but leave x and y intact
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		double cost = Interval.clamp(cos * this.cost - sin * this.sint, -1.0, 1.0);
		double sint = Interval.clamp(sin * this.cost + cos * this.sint, -1.0, 1.0);
		this.cost = cost;
		this.sint = sint;
	}
	
	/**
	 * Interpolates this transform linearly, by alpha, given the change in 
	 * position (&Delta;p) and the change in angle (&Delta;a) and places it into result.
	 * @param dp the change in position
	 * @param da the change in angle
	 * @param alpha the amount to interpolate
	 * @param result the transform to place the result
	 * @since 3.1.5
	 */
	public void lerp(Vector2 dp, double da, double alpha, Transform result) {
		result.set(this);
		result.rotateOnly(da * alpha);
		result.translate(dp.x * alpha, dp.y * alpha);
	}
	
	/**
	 * Interpolates this transform linearly, by alpha, given the change in 
	 * position (&Delta;p) and the change in angle (&Delta;a).
	 * @param dp the change in position
	 * @param da the change in angle
	 * @param alpha the amount to interpolate
	 * @since 3.1.5
	 */
	public void lerp(Vector2 dp, double da, double alpha) {
		this.rotateOnly(da * alpha);
		this.translate(dp.x * alpha, dp.y * alpha);
	}
	
	/**
	 * Interpolates this transform linearly, by alpha, given the change in 
	 * position (&Delta;p) and the change in angle (&Delta;a) and returns the result.
	 * @param dp the change in position
	 * @param da the change in angle
	 * @param alpha the amount to interpolate
	 * @return {@link Transform}
	 * @since 3.1.5
	 */
	public Transform lerped(Vector2 dp, double da, double alpha) {
		Transform result = new Transform(this);
		result.rotateOnly(da * alpha);
		result.translate(dp.x * alpha, dp.y * alpha);
		return result;
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
		double x = this.x + alpha * (end.x - this.x);
		double y = this.y + alpha * (end.y - this.y);
		
		// compute the angle
		// get the start and end rotations
		// its key that these methods use atan2 because
		// it ensures that the angles are always within
		// the range -pi < theta < pi therefore no
		// normalization has to be done
		double rs = this.getRotationAngle();
		double re = end.getRotationAngle();
		// make sure we use the smallest rotation
		// as described in the comments above, there
		// are two possible rotations depending on the
		// direction, we always choose the smaller
		double diff = re - rs;
		if (diff < -Math.PI) diff += Geometry.TWO_PI;
		if (diff > Math.PI) diff -= Geometry.TWO_PI;
		// interpolate
		// its ok if this method produces an angle
		// outside the range of -pi < theta < pi
		// since the rotate method uses sin and cos
		// which are not bounded
		double a = diff * alpha + rs;
		
		// create the interpolated transform
		// the following performs the following calculations:
		// tx.rotate(a);
		// tx.translate(x, y);
		Transform tx = new Transform(Math.cos(a), Math.sin(a), x, y);
		return tx;
	}
}
