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
package org.dyn4j.game2d.dynamics.joint;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Settings;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Matrix22;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a line joint.
 * <p>
 * A line joint constrains motion between two {@link Body}s to a line.
 * <p>
 * This differs from the {@link PrismaticJoint} since it allows the {@link Body}s
 * to rotate freely about the anchor point.
 * <p>
 * Nearly identical to <a href="http://www.box2d.org">Box2d</a>'s equivalent class.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class LineJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Line");
	
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** Whether the motor is enabled or not */
	protected boolean motorEnabled;
	
	/** The target velocity in meters / second */
	protected double motorSpeed;
	
	/** The maximum force the motor can apply in newtons */
	protected double maxMotorForce;
	
	/** Whether the limit is enabled or not */
	protected boolean limitEnabled;
	
	/** The upper limit in meters */
	protected double upperLimit;
	
	/** The lower limit in meters */
	protected double lowerLimit;
	
	/** The constraint mass; K = J * Minv * Jtrans */
	protected Matrix22 K;
	
	/** The mass of the motor */
	protected double motorMass;
	
	/** The current state of the limit */
	protected Joint.LimitState limitState;
	
	/** The accumulated impulse for warm starting */
	protected Vector2 impulse;
	
	/** The impulse applied by the motor */
	protected double motorImpulse;
	
	// pre-computed values for J, recalculated each time step
	
	/** The axis representing the allowed line of motion */
	protected Vector2 xAxis;
	
	/** The perpendicular axis of the line of motion */
	protected Vector2 yAxis;
	
	/** The world space yAxis  */
	protected Vector2 perp;
	
	/** The world space xAxis */
	protected Vector2 axis;
	
	/** s1 = (r1 + d).cross(perp) */
	protected double s1;
	
	/** s2 = r2.cross(perp) */
	protected double s2;
	
	/** a1 = (r1 + d).cross(axis) */
	protected double a1;

	/** a2 = r2.cross(axis) */
	protected double a2;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor, or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public LineJoint(Body body1, Body body2, Vector2 anchor, Vector2 axis) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException("Cannot create a line joint between the same body instance.");
		// check for a null anchor
		if (anchor == null) throw new NullPointerException("The anchor point cannot be null.");
		// check for a null axis
		if (axis == null) throw new NullPointerException("The axis cannot be null.");
		// set the anchor point
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// make sure the axis is normalized
		Vector2 n = axis.getNormalized();
		// get the axis in local coordinates
		this.xAxis = body2.getLocalVector(n);
		// get the perpendicular axis
		this.yAxis = this.xAxis.cross(1.0);
		// initialize
		this.K = new Matrix22();
		this.impulse = new Vector2();
		this.limitEnabled = false;
		this.motorEnabled = false;
		this.limitState = Joint.LimitState.INACTIVE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LINE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.localAnchor1).append("|")
		.append(this.localAnchor2).append("|")
		.append(this.xAxis).append("|")
		.append(this.yAxis).append("|")
		.append(this.motorEnabled).append("|")
		.append(this.motorSpeed).append("|")
		.append(this.maxMotorForce).append("|")
		.append(this.limitEnabled).append("|")
		.append(this.lowerLimit).append("|")
		.append(this.upperLimit).append("|")
		.append(this.limitState).append("|")
		.append(this.impulse).append("|")
		.append(this.motorImpulse).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
		double linearTolerance = Settings.getInstance().getLinearTolerance();
		
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
		
		// compute the 
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
		this.K.m01 = this.s1 * this.a1 * invI1 + this.s2 * this.a2 * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
		
		// compute the motor mass
		this.motorMass = this.K.m11;
		if (Math.abs(this.motorMass) >= Epsilon.E) {
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
				this.limitState = Joint.LimitState.EQUAL;
			} else if (dist <= this.lowerLimit) {
				// if the current distance along the axis is less than the limit
				// then the joint is at the lower limit
				// check if its already at the lower limit
				if (this.limitState != Joint.LimitState.AT_LOWER) {
					// if its not already at the lower limit then
					// set the state and clear the impulse for the
					// joint limit
					this.limitState = Joint.LimitState.AT_LOWER;
					this.impulse.y = 0.0;
				}
			} else if (dist >= this.upperLimit) {
				// if the current distance along the axis is greater than the limit
				// then the joint is at the upper limit
				// check if its already at the upper limit
				if (this.limitState != Joint.LimitState.AT_UPPER) {
					// if its not already at the upper limit then
					// set the state and clear the impulse for the
					// joint limit
					this.limitState = Joint.LimitState.AT_UPPER;
					this.impulse.y = 0.0;
				}
			} else {
				// otherwise the joint is currently within the limits
				// so set the limit to inactive
				this.limitState = Joint.LimitState.INACTIVE;
				this.impulse.y = 0.0;
			}
		} else {
			this.limitState = Joint.LimitState.INACTIVE;
			this.impulse.y = 0.0;
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
		// perp.product(impulse.x) + axis.product(motorImpulse + impulse.y)
		P.x = this.perp.x * this.impulse.x + (this.motorImpulse + this.impulse.y) * this.axis.x;
		P.y = this.perp.y * this.impulse.x + (this.motorImpulse + this.impulse.y) * this.axis.y;
		
		// where Jtrans = |  s1   a1 | excluding linear elements
		//                | -s2  -a2 |
		double l1 = this.impulse.x * this.s1 + (this.motorImpulse + this.impulse.y) * this.a1;
		double l2 = this.impulse.x * this.s2 + (this.motorImpulse + this.impulse.y) * this.a2;
		
		// apply the impulses
		this.body1.getVelocity().add(P.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * l1);
		this.body2.getVelocity().subtract(P.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * l2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 v1 = this.body1.getVelocity();
		Vector2 v2 = this.body2.getVelocity();
		double w1 = this.body1.getAngularVelocity();
		double w2 = this.body2.getAngularVelocity();
		
		// solve the motor constraint
		if (this.motorEnabled && this.limitState != Joint.LimitState.EQUAL) {
			// compute Jv + b
			double Cdt = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
			// compute lambda = Kinv * (Jv + b)
			double impulse = this.motorMass * (this.motorSpeed - Cdt);
			// clamp the impulse between the max force
			double oldImpulse = this.motorImpulse;
			double maxImpulse = this.maxMotorForce * step.getDeltaTime();
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
		
		// solve the linear constraint
		double Cdt = this.perp.dot(v1.difference(v2)) + this.s1 * w1 - this.s2 * w2;
		
		// is the limit enabled?
		if (this.limitEnabled && this.limitState != Joint.LimitState.INACTIVE) {
			// solve the linear and limit constraints together
			double Cdtl = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
			Vector2 b = new Vector2(Cdt, Cdtl);
			// solve for the impulse
			Vector2 impulse = this.K.solve(b.negate());
			// save the prevous impulse
			Vector2 f1 = this.impulse.copy();
			// add the impulse to the accumulated impulse
			this.impulse.add(impulse);
			
			// check the limit state
			if (this.limitState == Joint.LimitState.AT_LOWER) {
				// if the joint is at the lower limit then clamp
				// the accumulated impulse applied by the limit constraint
				this.impulse.y = Math.max(this.impulse.y, 0.0);
			} else if (this.limitState == Joint.LimitState.AT_UPPER) {
				// if the joint is at the upper limit then clamp
				// the accumulated impulse applied by the limit constraint
				this.impulse.y = Math.min(this.impulse.y, 0.0);
			}
			
			// solve for the corrected impulse
			double f2_1 = -Cdt - (this.impulse.y - f1.y) * this.K.m01;
			double f2r;
			if (Math.abs(this.K.m00) >= Epsilon.E) {
				f2r = f2_1 / this.K.m00 + f1.x;
			} else {
				f2r = f1.x;
			}
			
			this.impulse.x = f2r;
			
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
			P.x = this.perp.x * impulse.x + impulse.y * this.axis.x;
			P.y = this.perp.y * impulse.x + impulse.y * this.axis.y;
			
			// where Jtrans = |  s1   a1 | excluding linear elements
			//                | -s2  -a2 |
			double l1 = impulse.x * this.s1 + impulse.y * this.a1;
			double l2 = impulse.x * this.s2 + impulse.y * this.a2;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		} else {
			// otherwise just solve the linear constraint
			double f2r;
			// the linear constraint without the limits has a
			// scalar mass equal to the 0,0 element of the K matrix
			if (Math.abs(this.K.m00) >= Epsilon.E) {
				f2r = -Cdt / this.K.m00;
			} else {
				f2r = 0.0;
			}
			
			this.impulse.x += f2r;
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.perp.product(f2r);
			double l1 = f2r * this.s1;
			double l2 = f2r * this.s2;
			
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
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		Settings settings = Settings.getInstance();
		double maxLinearCorrection = settings.getMaxLinearCorrection();
		double linearTolerance = settings.getLinearTolerance();
		
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
		
		double Cx = this.perp.dot(d);
		double Cy = 0.0;
		double linearError = 0.0;
		boolean limitActive = false;
		
		// check if the limit is enabled
		if (this.limitEnabled) {
			// compute a1 and a2
			this.a1 = r1.cross(axis);
			this.a2 = r2.sum(d).cross(axis);
			
			// whats the current distance
			double dist = axis.dot(d);
			// check for equal limits
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				// then apply the limit and clamp it
				Cy = Interval.clamp(dist, -maxLinearCorrection, maxLinearCorrection);
				linearError = Math.abs(dist);
				limitActive = true;
			} else if (dist <= this.lowerLimit) {
				// if its less than the lower limit then attempt to correct it
				Cy = Interval.clamp(dist - this.lowerLimit + linearTolerance, -maxLinearCorrection, 0.0);
				linearError = this.lowerLimit - dist;
				limitActive = true;
			} else if (dist >= this.upperLimit) {
				// if its less than the lower limit then attempt to correct it
				Cy = Interval.clamp(dist - this.upperLimit - linearTolerance, 0.0, maxLinearCorrection);
				linearError = dist - this.upperLimit;
				limitActive = true;
			}
		}
		
		// compute the linear constraint
		this.s1 = r1.cross(this.perp);
		this.s2 = r2.sum(d).cross(this.perp);
		
		// compute the overall linear error
		linearError = Math.max(linearError, Math.abs(Cx));
		
		Vector2 impulse;
		// check if the limit is active
		if (limitActive) {
			// then solve for both position constraints
			this.K.m00 = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
			this.K.m01 = this.s1 * this.a1 * invI1 + this.s2 * this.a2 * invI2;
			this.K.m10 = this.K.m01;
			this.K.m11 = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
			
			Vector2 C = new Vector2(Cx, Cy);
			impulse = this.K.solve(C.negate());
		} else {
			// otherwise only solve the linear constraint
			double mass = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
			if (Math.abs(mass) >= Epsilon.E) {
				mass = 1.0 / mass;
			}
			
			double impulsex = -mass * Cx;
			impulse = new Vector2(impulsex, 0.0);
		}
		
		// compute the applied impulses
		// Pc = Jtrans * lambda
		
		// where Jtrans = |  perp   axis | excluding rotational elements
		//                | -perp  -axis |
		// we only compute the impulse for body1 since body2's impulse is
		// just the negative of body1's impulse
		Vector2 P = new Vector2();
		// perp.product(impulse.x) + axis.product(impulse.y)
		P.x = this.perp.x * impulse.x + impulse.y * this.axis.x;
		P.y = this.perp.y * impulse.x + impulse.y * this.axis.y;
		
		// where Jtrans = |  s1   a1 | excluding linear elements
		//                | -s2  -a2 |
		double l1 = impulse.x * this.s1 + impulse.y * this.a1;
		double l2 = impulse.x * this.s2 + impulse.y * this.a2;
		
		// apply the impulse
		this.body1.translate(P.product(invM1));
		this.body1.rotateAboutCenter(l1 * invI1);
		
		this.body2.translate(P.product(-invM2));
		this.body2.rotateAboutCenter(-l2 * invI2);
		
		// return if we corrected the error enough
		return linearError <= linearTolerance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getType()
	 */
	@Override
	public Type getType() {
		return LineJoint.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		Vector2 force = new Vector2();
		// compute the impulse
		force.x = this.impulse.x * this.perp.x + (this.motorImpulse + this.impulse.y) * this.axis.x;
		force.y = this.impulse.x * this.perp.y + (this.motorImpulse + this.impulse.y) * this.axis.y;
		// multiply by invdt to obtain the force
		force.multiply(invdt);
		return force;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0;
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
		
		Vector2 v1 = this.body1.getVelocity();
		Vector2 v2 = this.body2.getVelocity();
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
	 */
	public void setMotorSpeed(double motorSpeed) {
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.motorSpeed = motorSpeed;
	}
	
	/**
	 * Returns the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @return double
	 */
	public double getMaxMotorForce() {
		return maxMotorForce;
	}
	
	/**
	 * Sets the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @param maxMotorForce the maximum force in newtons; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxMotorForce is less than zero
	 */
	public void setMaxMotorForce(double maxMotorForce) {
		// make sure its greater than or equal to zero
		if (maxMotorForce < 0.0) throw new IllegalArgumentException("The maximum motor force must be greater than or equal to zero.");
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.maxMotorForce = maxMotorForce;
	}
	
	/**
	 * Returns the applied motor force.
	 * @return double
	 */
	public double getMotorForce() {
		return this.motorImpulse;
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
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException("The lower limit cannot be greater than the upper limit."); 
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
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
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException("The upper limit cannot be less than the lower limit."); 
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
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
		if (lowerLimit > upperLimit) throw new IllegalArgumentException("The lower limit cannot be greater than the upper limit.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the values
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}
}
