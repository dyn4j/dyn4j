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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.Step;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix33;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a prismatic joint.
 * <p>
 * A prismatic joint constrains the linear motion of two bodies along an axis
 * and prevents relative rotation.  The whole system can rotate and translate 
 * freely.
 * <p>
 * The initial relative rotation of the bodies will remain unchanged unless 
 * updated by calling {@link #setReferenceAngle(double)} method.  The bodies
 * are not required to be aligned in any particular way.
 * <p>
 * The world space anchor point can be any point but is typically a point on
 * the axis of allowed motion, usually the world center of either of the joined
 * bodies.
 * <p>
 * The limits are linear limits along the axis.  The limits are checked against
 * the separation of the local anchor points, rather than the separation of the
 * bodies.  This can have the effect of offsetting the limit values.  The best
 * way to describe the effect is to examine the "0 to 0" limit case.  This case
 * specifies that the bodies should not move along the axis, forcing them to 
 * stay at their <em>initial location</em> along the axis.  So if the bodies 
 * were initially separated when they were joined, they will stay separated at
 * that initial distance.
 * <p>
 * This joint also supports a motor.  The motor is a linear motor along the
 * axis.  The motor speed can be positive or negative to indicate motion along
 * or opposite the axis direction.  The maximum motor force must be greater 
 * than zero for the motor to apply any motion.
 * @author William Bittle
 * @version 3.2.1
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Prismatic_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2011/03/prismatic-constraint/" target="_blank">Prismatic Constraint</a>
 */
public class PrismaticJoint extends Joint implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** Whether the motor is enabled or not */
	protected boolean motorEnabled;
	
	/** The target velocity in meters / second */
	protected double motorSpeed;
	
	/** The maximum force the motor can apply in newtons */
	protected double maximumMotorForce;
	
	/** Whether the limit is enabled or not */
	protected boolean limitEnabled;
	
	/** The upper limit in meters */
	protected double upperLimit;
	
	/** The lower limit in meters */
	protected double lowerLimit;
	
	/** The initial angle between the two {@link Body}s */
	protected double referenceAngle;

	// internal
	
	/** The axis representing the allowed line of motion */
	private final Vector2 xAxis;
	
	/** The perpendicular axis of the line of motion */
	private final Vector2 yAxis;
	
	// current state
	
	/** The current state of the limit */
	private LimitState limitState;

	/** The constraint mass; K = J * Minv * Jtrans */
	private Matrix33 K;
	
	/** The mass of the motor */
	private double motorMass;
	
	// pre-computed values for J, recalculated each time step
	
	/** The world space yAxis  */
	private Vector2 perp;
	
	/** The world space xAxis */
	private Vector2 axis;
	
	/** s1 = (r1 + d).cross(perp) */
	private double s1;
	
	/** s2 = r2.cross(perp) */
	private double s2;
	
	/** a1 = (r1 + d).cross(axis) */
	private double a1;

	/** a2 = r2.cross(axis) */
	private double a2;

	// output
	
	/** The accumulated impulse for warm starting */
	private Vector3 impulse;
	
	/** The impulse applied by the motor */
	private double motorImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public PrismaticJoint(Body body1, Body body2, Vector2 anchor, Vector2 axis) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// check for a null anchor
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
		// check for a null axis
		if (axis == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAxis"));
		// set the anchor point
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// make sure the axis is normalized
		Vector2 n = axis.getNormalized();
		// get the axis in local coordinates
		this.xAxis = body2.getLocalVector(n);
		// get the perpendicular axis
		this.yAxis = this.xAxis.cross(1.0);
		// get the initial rotation
		this.referenceAngle = body1.getTransform().getRotation() - body2.getTransform().getRotation();
		// initialize
		this.K = new Matrix33();
		this.impulse = new Vector3();
		this.limitEnabled = false;
		this.motorEnabled = false;
		this.limitState = LimitState.INACTIVE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PrismaticJoint[").append(super.toString())
		  .append("|Anchor=").append(this.getAnchor1())
		  .append("|Axis=").append(this.getAxis())
		  .append("|IsMotorEnabled=").append(this.motorEnabled)
		  .append("|MotorSpeed=").append(this.motorSpeed)
		  .append("|MaximumMotorForce=").append(this.maximumMotorForce)
		  .append("|ReferenceAngle=").append(this.referenceAngle)
		  .append("|IsLimitEnabled=").append(this.limitEnabled)
		  .append("|LowerLimit=").append(this.lowerLimit)
		  .append("|UpperLimit=").append(this.upperLimit)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 d = this.body1.getWorldCenter().sum(r1).subtract(this.body2.getWorldCenter().sum(r2));
		this.axis = this.body2.getWorldVector(this.xAxis);
		this.perp = this.body2.getWorldVector(this.yAxis);
		
		// compute the K matrix
		// s1 = r1.cross(perp)
		// s2 = (r2 + d).cross(perp)
		// a1 = r1.cross(axis)
		// a2 = (r2 + d).cross(axis)
		this.s1 = r1.cross(this.perp);
		this.s2 = r2.sum(d).cross(this.perp);
		this.a1 = r1.cross(this.axis);
		this.a2 = r2.sum(d).cross(this.axis);
		
		this.K.m00 = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
		this.K.m01 = this.s1 * invI1 + this.s2 * invI2;
		this.K.m02 = this.s1 * this.a1 * invI1 + this.s2 * this.a2 * invI2;
		
		this.K.m10 = this.K.m01;
		this.K.m11 = invI1 + invI2;
		// handle prismatic constraint between two fixed rotation bodies
		if (this.K.m11 <= Epsilon.E) this.K.m11 = 1.0;
		this.K.m12 = this.a1 * invI1 + this.a2 * invI2;
		
		this.K.m20 = this.K.m02;
		this.K.m21 = this.K.m12;
		this.K.m22 = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
		
		// compute the motor mass
		this.motorMass = this.K.m22;
		if (Math.abs(this.motorMass) > Epsilon.E) {
			this.motorMass = 1.0 / this.motorMass;
		}
		
		// check if the motor is still enabled
		if (!this.motorEnabled) {
			// if not then make the current motor impulse zero
			this.motorImpulse = 0.0;
		}
		
		// is the limit enabled
		if (this.limitEnabled) {
			// determine the current state of the limit
			double dist = this.axis.dot(d);
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				// if the limits are close enough then they are basically equal
				this.limitState = LimitState.EQUAL;
			} else if (dist <= this.lowerLimit) {
				// if the current distance along the axis is less than the limit
				// then the joint is at the lower limit
				// check if its already at the lower limit
				if (this.limitState != LimitState.AT_LOWER) {
					// if its not already at the lower limit then
					// set the state and clear the impulse for the
					// joint limit
					this.limitState = LimitState.AT_LOWER;
					this.impulse.z = 0.0;
				}
			} else if (dist >= this.upperLimit) {
				// if the current distance along the axis is greater than the limit
				// then the joint is at the upper limit
				// check if its already at the upper limit
				if (this.limitState != LimitState.AT_UPPER) {
					// if its not already at the upper limit then
					// set the state and clear the impulse for the
					// joint limit
					this.limitState = LimitState.AT_UPPER;
					this.impulse.z = 0.0;
				}
			} else {
				// otherwise the joint is currently within the limits
				// so set the limit to inactive
				this.limitState = LimitState.INACTIVE;
				this.impulse.z = 0.0;
			}
		} else {
			this.limitState = LimitState.INACTIVE;
			this.impulse.z = 0.0;
		}
		
		// warm start
		// account for variable time step
		this.impulse.multiply(step.getDeltaTimeRatio());
		this.motorImpulse *= step.getDeltaTimeRatio();
		
		// compute the applied impulses
		// Pc = Jtrans * lambda
		
		// where Jtrans = |  perp   axis | excluding rotational elements
		//                | -perp  -axis |
		// we only compute the impulse for body1 since body2's impulse is
		// just the negative of body1's impulse
		Vector2 P = new Vector2();
		// perp.product(impulse.x) + axis.product(motorImpulse + impulse.z)
		P.x = this.perp.x * this.impulse.x + (this.motorImpulse + this.impulse.z) * this.axis.x;
		P.y = this.perp.y * this.impulse.x + (this.motorImpulse + this.impulse.z) * this.axis.y;
		
		// where Jtrans = |  s1   a1 | excluding linear elements
		//                |   1    1 |
		//                | -s2  -a2 |
		double l1 = this.impulse.x * this.s1 + this.impulse.y + (this.motorImpulse + this.impulse.z) * this.a1;
		double l2 = this.impulse.x * this.s2 + this.impulse.y + (this.motorImpulse + this.impulse.z) * this.a2;
		
		// apply the impulses
		this.body1.getLinearVelocity().add(P.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * l1);
		this.body2.getLinearVelocity().subtract(P.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * l2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 v1 = this.body1.getLinearVelocity();
		Vector2 v2 = this.body2.getLinearVelocity();
		double w1 = this.body1.getAngularVelocity();
		double w2 = this.body2.getAngularVelocity();
		
		// solve the motor constraint
		if (this.motorEnabled && this.limitState != LimitState.EQUAL) {
			// compute Jv + b
			double Cdt = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
			// compute lambda = Kinv * (Jv + b)
			double impulse = this.motorMass * (this.motorSpeed - Cdt);
			// clamp the impulse between the max force
			double oldImpulse = this.motorImpulse;
			double maxImpulse = this.maximumMotorForce * step.getDeltaTime();
			this.motorImpulse = Interval.clamp(this.motorImpulse + impulse, -maxImpulse, maxImpulse);
			impulse = this.motorImpulse - oldImpulse;
			
			// apply the impulse
			Vector2 P = this.axis.product(impulse);
			double l1 = impulse * this.a1;
			double l2 = impulse * this.a2;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		}
		
		// solve the linear and angular constraint (excluding the limit)
		Vector2 Cdt = new Vector2();
		Cdt.x = perp.dot(v1.difference(v2)) + this.s1 * w1 - this.s2 * w2;
		Cdt.y = w1 - w2;
		
		// is the limit enabled?
		if (this.limitEnabled && this.limitState != LimitState.INACTIVE) {
			// solve the linear and angular constraints with the limit constraint
			double Cdtl = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
			Vector3 b = new Vector3(Cdt.x, Cdt.y, Cdtl);
			// solve for the impulse
			Vector3 impulse = this.K.solve33(b.negate());
			// save the previous impulse
			Vector3 f1 = this.impulse.copy();
			// add the impulse to the accumulated impulse
			this.impulse.add(impulse);
			
			// check the limit state
			if (this.limitState == LimitState.AT_LOWER) {
				// if the joint is at the lower limit then clamp
				// the accumulated impulse applied by the limit constraint
				this.impulse.z = Math.max(this.impulse.z, 0.0);
			} else if (this.limitState == LimitState.AT_UPPER) {
				// if the joint is at the upper limit then clamp
				// the accumulated impulse applied by the limit constraint
				this.impulse.z = Math.min(this.impulse.z, 0.0);
			}
			
			// solve for the corrected impulse
			Vector2 f2_1 = Cdt.negate().difference(new Vector2(this.K.m02, this.K.m12).multiply(this.impulse.z - f1.z));
			Vector2 f2r = this.K.solve22(f2_1).add(f1.x, f1.y);
			
			this.impulse.x = f2r.x;
			this.impulse.y = f2r.y;
			
			// only apply the impulse found in this iteration (given clamping)
			impulse = this.impulse.difference(f1);
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			
			// where Jtrans = |  perp   axis | excluding rotational elements
			//                | -perp  -axis |
			// we only compute the impulse for body1 since body2's impulse is
			// just the negative of body1's impulse
			Vector2 P = new Vector2();
			// perp.product(impulse.x) + axis.product(impulse.y)
			P.x = this.perp.x * impulse.x + impulse.z * this.axis.x;
			P.y = this.perp.y * impulse.x + impulse.z * this.axis.y;
			
			// where Jtrans = |  s1   a1 | excluding linear elements
			//                |   1    1 |
			//                | -s2  -a2 |
			double l1 = impulse.x * this.s1 + impulse.y + impulse.z * this.a1;
			double l2 = impulse.x * this.s2 + impulse.y + impulse.z * this.a2;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		} else {
			// otherwise just solve the linear and angular constraints
			Vector2 f2r = this.K.solve22(Cdt.negate());
			this.impulse.x += f2r.x;
			this.impulse.y += f2r.y;
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.perp.product(f2r.x);
			double l1 = f2r.x * this.s1 + f2r.y;
			double l2 = f2r.x * this.s2 + f2r.y;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		}
		
		// finally set the velocities
		// NOTE we dont have to update v1 or v2 because they are references
		this.body1.setAngularVelocity(w1);
		this.body2.setAngularVelocity(w2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		double maxLinearCorrection = settings.getMaximumLinearCorrection();
		double linearTolerance = settings.getLinearTolerance();
		double angularTolerance = settings.getAngularTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 c1 = this.body1.getWorldCenter();
		Vector2 c2 = this.body2.getWorldCenter();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
		this.axis = this.body2.getWorldVector(this.xAxis);
		this.perp = this.body2.getWorldVector(this.yAxis);
		
		Vector2 C = new Vector2();
		C.x = this.perp.dot(d);
		C.y = t1.getRotation() - t2.getRotation() - this.referenceAngle;
		double Cz = 0.0;
		double linearError = 0.0;
		double angularError = 0.0;
		boolean limitActive = false;
		
		// check if the limit is enabled
		if (this.limitEnabled) {
			// compute a1 and a2
			this.a1 = r1.cross(axis);
			this.a2 = r2.sum(d).cross(axis);
			
			// what's the current distance
			double dist = axis.dot(d);
			// check for equal limits
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				// then apply the limit and clamp it
				Cz = Interval.clamp(dist, -maxLinearCorrection, maxLinearCorrection);
				linearError = Math.abs(dist);
				limitActive = true;
			} else if (dist <= this.lowerLimit) {
				// if its less than the lower limit then attempt to correct it
				Cz = Interval.clamp(dist - this.lowerLimit + linearTolerance, -maxLinearCorrection, 0.0);
				linearError = this.lowerLimit - dist;
				limitActive = true;
			} else if (dist >= this.upperLimit) {
				// if its less than the lower limit then attempt to correct it
				Cz = Interval.clamp(dist - this.upperLimit - linearTolerance, 0.0, maxLinearCorrection);
				linearError = dist - this.upperLimit;
				limitActive = true;
			}
		}
		
		// compute the linear constraint
		this.s1 = r1.cross(this.perp);
		this.s2 = r2.sum(d).cross(this.perp);
		
		// compute the overall linear error
		linearError = Math.max(linearError, Math.abs(C.x));
		angularError = Math.abs(C.y);
		
		Vector3 impulse;
		// check if the limit is active
		if (limitActive) {
			// then solve the linear and angular constraints along with the limit constraint
			this.K.m00 = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
			this.K.m01 = this.s1 * invI1 + this.s2 * invI2;
			this.K.m02 = this.s1 * this.a1 * invI1 + this.s2 * this.a2 * invI2;
			
			this.K.m10 = this.K.m01;
			this.K.m11 = invI1 + invI2;
			// handle prismatic constraint between two fixed rotation bodies
			if (this.K.m11 <= Epsilon.E) this.K.m11 = 1.0;
			this.K.m12 = this.a1 * invI1 + this.a2 * invI2;
			
			this.K.m20 = this.K.m02;
			this.K.m21 = this.K.m12;
			this.K.m22 = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
			
			Vector3 Clim = new Vector3(C.x, C.y, Cz);
			impulse = this.K.solve33(Clim.negate());
		} else {
			// then solve just the linear and angular constraints
			this.K.m00 = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
			this.K.m01 = this.s1 * invI1 + this.s2 * invI2;
			this.K.m02 = 0.0;
			
			this.K.m10 = this.K.m01;
			this.K.m11 = invI1 + invI2;
			// handle prismatic constraint between two fixed rotation bodies
			if (this.K.m11 <= Epsilon.E) this.K.m11 = 1.0;
			this.K.m12 = 0.0;
			
			this.K.m20 = 0.0;
			this.K.m21 = 0.0;
			this.K.m22 = 0.0;
			
			Vector2 impulsec = this.K.solve22(C.negate());
			impulse = new Vector3(impulsec.x, impulsec.y, 0.0);
		}
		
		// compute the applied impulses
		// Pc = Jtrans * lambda
		
		// where Jtrans = |  perp   axis | excluding rotational elements
		//                | -perp  -axis |
		// we only compute the impulse for body1 since body2's impulse is
		// just the negative of body1's impulse
		Vector2 P = new Vector2();
		// perp.product(impulse.x) + axis.product(impulse.y)
		P.x = this.perp.x * impulse.x + impulse.z * this.axis.x;
		P.y = this.perp.y * impulse.x + impulse.z * this.axis.y;
		
		// where Jtrans = |  s1   a1 | excluding linear elements
		//                |   1    1 |
		//                | -s2  -a2 |
		double l1 = impulse.x * this.s1 + impulse.y + impulse.z * this.a1;
		double l2 = impulse.x * this.s2 + impulse.y + impulse.z * this.a2;
		
		// apply the impulse
		this.body1.translate(P.product(invM1));
		this.body1.rotateAboutCenter(l1 * invI1);
		
		this.body2.translate(P.product(-invM2));
		this.body2.rotateAboutCenter(-l2 * invI2);
		
		// return if we corrected the error enough
		return linearError <= linearTolerance && angularError <= angularTolerance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		Vector2 force = new Vector2();
		// compute the impulse
		force.x = this.impulse.x * this.perp.x + (this.motorImpulse + this.impulse.z) * this.axis.x;
		force.y = this.impulse.x * this.perp.y + (this.motorImpulse + this.impulse.z) * this.axis.y;
		// multiply by invdt to obtain the force
		force.multiply(invdt);
		return force;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return invdt * this.impulse.y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// nothing to translate here since the anchor points are in local coordinates
		// they will move with the bodies
	}
	
	/**
	 * Returns the current joint speed.
	 * @return double
	 */
	public double getJointSpeed() {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Vector2 c1 = this.body1.getWorldCenter();
		Vector2 c2 = this.body2.getWorldCenter();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
		Vector2 axis = this.body2.getWorldVector(this.xAxis);
		
		Vector2 v1 = this.body1.getLinearVelocity();
		Vector2 v2 = this.body2.getLinearVelocity();
		double w1 = this.body1.getAngularVelocity();
		double w2 = this.body2.getAngularVelocity();
		
		double speed = d.dot(axis.cross(w2)) + axis.dot(v1.sum(r1.cross(w1)).subtract(v2.sum(r2.cross(w2))));
		return speed;
	}
	
	/**
	 * Returns the current joint translation.
	 * @return double
	 */
	public double getJointTranslation() {
		Vector2 p1 = this.body1.getWorldPoint(this.localAnchor1);
		Vector2 p2 = this.body2.getWorldPoint(this.localAnchor2);
		Vector2 d = p2.difference(p1);
		Vector2 axis = this.body2.getWorldVector(this.xAxis);
		return d.dot(axis);
	}
	
	/**
	 * Returns true if the motor is enabled.
	 * @return boolean
	 */
	public boolean isMotorEnabled() {
		return motorEnabled;
	}
	
	/**
	 * Enables or disables the motor.
	 * @param motorEnabled true if the motor should be enabled
	 */
	public void setMotorEnabled(boolean motorEnabled) {
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.motorEnabled = motorEnabled;
	}
	
	/**
	 * Returns the target motor speed in meters / second.
	 * @return double
	 */
	public double getMotorSpeed() {
		return motorSpeed;
	}
	
	/**
	 * Sets the target motor speed.
	 * @param motorSpeed the target motor speed in meters / second
	 * @see #setMaximumMotorForce(double)
	 */
	public void setMotorSpeed(double motorSpeed) {
		// only wake up the bodies if the motor is currently enabled
		if (this.motorEnabled) {
			// wake up the joined bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
		}
		// set the new value
		this.motorSpeed = motorSpeed;
	}
	
	/**
	 * Returns the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @return double
	 */
	public double getMaximumMotorForce() {
		return maximumMotorForce;
	}
	
	/**
	 * Sets the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @param maximumMotorForce the maximum force in newtons; must be greater than zero
	 * @throws IllegalArgumentException if maxMotorForce is less than zero
	 * @see #setMotorSpeed(double)
	 */
	public void setMaximumMotorForce(double maximumMotorForce) {
		// make sure its greater than or equal to zero
		if (maximumMotorForce < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidMaximumMotorForce"));
		// set the new value
		this.maximumMotorForce = maximumMotorForce;
	}
	
	/**
	 * Returns the applied motor force.
	 * @param invdt the inverse delta time
	 * @return double
	 */
	public double getMotorForce(double invdt) {
		return this.motorImpulse * invdt;
	}
	
	/**
	 * Returns true if the limit is enabled.
	 * @return boolean
	 */
	public boolean isLimitEnabled() {
		return this.limitEnabled;
	}
	
	/**
	 * Enables or disables the limits.
	 * @param limitEnabled true if the limit should be enabled.
	 */
	public void setLimitEnabled(boolean limitEnabled) {
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.limitEnabled = limitEnabled;
	}
	
	/**
	 * Returns the lower limit in meters.
	 * @return double
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}
	
	/**
	 * Sets the lower limit.
	 * @param lowerLimit the lower limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		// check for valid value
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		// make sure the limits are enabled and that the limit has changed
		if (this.limitEnabled && lowerLimit != this.lowerLimit) {
			// wake up the joined bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// reset the limit impulse
			this.impulse.z = 0.0;
		}
		// set the new value
		this.lowerLimit = lowerLimit;
	}
	
	/**
	 * Returns the upper limit in meters.
	 * @return double
	 */
	public double getUpperLimit() {
		return upperLimit;
	}

	/**
	 * Sets the upper limit.
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		// check for valid value
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		// make sure the limits are enabled and that the limit has changed
		if (this.limitEnabled && upperLimit != this.upperLimit) {
			// wake up the joined bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// reset the limit impulse
			this.impulse.z = 0.0;
		}
		// set the new value
		this.upperLimit = upperLimit;
	}
	
	/**
	 * Sets the upper and lower limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		// make sure the limits are enabled and that the limit has changed
		if (this.limitEnabled) {
			// wake up the bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// reset the limit impulse
			this.impulse.z = 0.0;
		}
		// set the values
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}
	
	/**
	 * Sets the upper and lower limits and enables the limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 * @since 2.2.2
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the values
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		// enable the limits
		this.limitEnabled = true;
	}
	
	/**
	 * Returns the axis in which the joint is allowed move along in world coordinates.
	 * @return {@link Vector2}
	 * @since 3.0.0
	 */
	public Vector2 getAxis() {
		return this.body2.getWorldVector(this.xAxis);
	}
	
	/**
	 * Returns the reference angle.
	 * <p>
	 * The reference angle is the angle calculated when the joint was created from the
	 * two joined bodies.  The reference angle is the angular difference between the
	 * bodies.
	 * @return double
	 * @since 3.0.1
	 */
	public double getReferenceAngle() {
		return this.referenceAngle;
	}
	
	/**
	 * Sets the reference angle.
	 * <p>
	 * This method can be used to set the reference angle to override the computed
	 * reference angle from the constructor.  This is useful in recreating the joint
	 * from a current state.
	 * <p>
	 * This can also be used to override the initial angle between the bodies.
	 * @param angle the reference angle
	 * @see #getReferenceAngle()
	 * @since 3.0.1
	 */
	public void setReferenceAngle(double angle) {
		this.referenceAngle = angle;
	}

	/**
	 * Returns the current state of the limit.
	 * @return {@link LimitState}
	 * @since 3.2.0
	 */
	public LimitState getLimitState() {
		return this.limitState;
	}
}
