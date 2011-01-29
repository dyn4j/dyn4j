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
import org.dyn4j.game2d.geometry.Matrix33;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.geometry.Vector3;

/**
 * Represents a pivot joint.
 * <p>
 * The limits that a revolute joint can place on the bodies are world space limits, not 
 * relative angle limits (although the limits are relative to the initial angle of the 
 * bodies given at joint creation time).  Therefore its recommended to only use the limits 
 * when one body is fixed.
 * <p>
 * Nearly identical to <a href="http://www.box2d.org">Box2d</a>'s equivalent class.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class RevoluteJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Revolute");
	
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** Whether the motor for this {@link Joint} is enabled or not */
	protected boolean motorEnabled;
	
	/** The target motor speed; in radians / second */
	protected double motorSpeed;
	
	/** The maximum torque the motor can apply */
	protected double maxMotorTorque;
	
	/** Whether the {@link Joint} limits are enabled or not */
	protected boolean limitEnabled;
	
	/** The upper limit of the {@link Joint} */
	protected double upperLimit;
	
	/** The lower limit of the {@link Joint} */
	protected double lowerLimit;
	
	/** The initial angle between the two {@link Body}s */
	protected double referenceAngle;
	
	/** The current state of the {@link Joint} limit */
	protected Joint.LimitState limitState;
	
	/** The pivot mass; K = J * Minv * Jtrans */
	protected Matrix33 K;
	
	/** The motor mass that resists motion */
	protected double motorMass;
	
	/** The accumulated impulse for warm starting */
	protected Vector3 impulse;
		
	/** The impulse applied by the motor */
	protected double motorImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 * @throws NullPointerException if body1, body2 or anchor is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public RevoluteJoint(Body body1, Body body2, Vector2 anchor) {
		// default to no collision allowed between the bodies
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException("Cannot create a revolute joint between the same body instance.");
		// make sure the anchor point is not null
		if (anchor == null) throw new NullPointerException("The anchor point cannot be null.");
		// get the local space points
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// get the initial reference angle for the joint limits
		this.referenceAngle = body1.getTransform().getRotation() - body2.getTransform().getRotation();
		// initialize
		this.limitState = Joint.LimitState.INACTIVE;
		this.impulse = new Vector3();
		this.K = new Matrix33();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("REVOLUTE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.localAnchor1).append("|")
		.append(this.localAnchor2).append("|")
		.append(this.motorEnabled).append("|")
		.append(this.motorSpeed).append("|")
		.append(this.maxMotorTorque).append("|")
		.append(this.limitEnabled).append("|")
		.append(this.lowerLimit).append("|")
		.append(this.upperLimit).append("|")
		.append(this.referenceAngle).append("|")
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
		double angularTolerance = Settings.getInstance().getAngularTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// is the motor enabled?
		if (this.motorEnabled) {
			// compute the motor mass
			if (invI1 <= 0.0 && invI2 <= 0.0) {
				// cannot have a motor with two bodies
				// who have fixed angular velocities
				throw new IllegalStateException("A RevoluteJoint must have at least one body without fixed angular velocity.");
			}
		}
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the K matrix
		this.K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		this.K.m01 = -r1.y * r1.x * invI1 - r2.y * r2.x * invI2;
		this.K.m02 = -r1.y * invI1 - r2.y * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		this.K.m12 = r1.x * invI1 + r2.x * invI2;
		this.K.m20 = this.K.m02;
		this.K.m21 = this.K.m12;
		this.K.m22 = invI1 + invI2;
		
		// compute the motor mass
		this.motorMass = invI1 + invI2;
		if (this.motorMass >= Epsilon.E) {
			this.motorMass = 1.0 / this.motorMass;
		}
		
		// check if the motor is still enabled
		if (!this.motorEnabled) {
			// if not then make the current motor impulse zero
			this.motorImpulse = 0.0;
		}
		
		// check if the joint limit is enabled
		if (this.limitEnabled) {
			// set the current state of the joint limit
			double angle = t1.getRotation() - t2.getRotation() - this.referenceAngle;
			// see if the limits are close enough to be equal
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * angularTolerance) {
				// if they are close enough then they are equal
				this.limitState = Joint.LimitState.EQUAL;
			} else if (angle <= this.lowerLimit) {
				// is it currently at the lower limit?
				if (this.limitState != Joint.LimitState.AT_LOWER) {
					// if not then make the limit impulse zero
					this.impulse.z = 0.0;
				}
				this.limitState = Joint.LimitState.AT_LOWER;
			} else if (angle >= this.upperLimit) {
				// is it currently at the upper limit?
				if (this.limitState == Joint.LimitState.AT_UPPER) {
					// if not then make the limit impulse zero
					this.impulse.z = 0.0;
				}
				this.limitState = Joint.LimitState.AT_UPPER;
			} else {
				// otherwise the limit constraint is inactive
				this.impulse.z = 0.0;
				this.limitState = Joint.LimitState.INACTIVE;
			}
		} else {
			this.limitState = Joint.LimitState.INACTIVE;
		}
		
		// account for variable time step
		this.impulse.multiply(step.getDeltaTimeRatio());
		this.motorImpulse *= step.getDeltaTimeRatio();
		
		// warm start
		Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
		this.body1.getVelocity().add(impulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (r1.cross(impulse) + this.motorImpulse + this.impulse.z));
		this.body2.getVelocity().subtract(impulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (r2.cross(impulse) + this.motorImpulse + this.impulse.z));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// solve the motor constraint
		if (this.motorEnabled && this.limitState != Joint.LimitState.EQUAL) {
			// get the relative velocity - the target motor speed
			double C = this.body1.getAngularVelocity() - this.body2.getAngularVelocity() - this.motorSpeed;
			// get the impulse required to obtain the speed
			double impulse = this.motorMass * -C;
			// clamp the impulse between the maximum torque
			double oldImpulse = this.motorImpulse;
			double maxImpulse = this.maxMotorTorque * step.getDeltaTime();
			this.motorImpulse = Interval.clamp(this.motorImpulse + impulse, -maxImpulse, maxImpulse);
			// get the impulse we need to apply to the bodies
			impulse = this.motorImpulse - oldImpulse;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
        }
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 v1 = this.body1.getVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
		Vector2 v2 = this.body2.getVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
		// the 2x2 version of Jv + b
		Vector2 Jvb2 = v1.subtract(v2);
		
		// check if the limit constraint is enabled
		if (this.limitEnabled && this.limitState != Joint.LimitState.INACTIVE) {
			// solve the point to point constraint including the limit constraint
			double pivotW = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
			// the 3x3 version of Jv + b
			Vector3 Jvb3 = new Vector3(Jvb2.x, Jvb2.y, pivotW);
			
			Vector3 impulse3 = this.K.solve33(Jvb3.negate());
			// check the state to determine how to apply the impulse
			if (this.limitState == Joint.LimitState.EQUAL) {
				// if its equal limits then this is basically a weld joint
				// so add all the impulse to satisfy the point-to-point and
				// angle constraints
				this.impulse.add(impulse3);
			} else if (this.limitState == Joint.LimitState.AT_LOWER) {
				// if its at the lower limit then clamp the rotational impulse
				// and solve the point-to-point constraint alone
				double newImpulse = this.impulse.z + impulse3.z;
				if (newImpulse < 0.0) {
					Vector2 reduced = this.K.solve22(Jvb2.negate());
					impulse3.x = reduced.x;
					impulse3.y = reduced.y;
					impulse3.z = -this.impulse.z;
					this.impulse.x += reduced.x;
					this.impulse.y += reduced.y;
					this.impulse.z = 0.0;
				}
			} else if (this.limitState == Joint.LimitState.AT_UPPER) {
				// if its at the upper limit then clamp the rotational impulse
				// and solve the point-to-point constraint alone
				double newImpulse = this.impulse.z + impulse3.z;
				if (newImpulse > 0.0) {
					Vector2 reduced = this.K.solve22(Jvb2.negate());
					impulse3.x = reduced.x;
					impulse3.y = reduced.y;
					impulse3.z = -this.impulse.z;
					this.impulse.x += reduced.x;
					this.impulse.y += reduced.y;
					this.impulse.z = 0.0;
				}
			}
			
			// apply the impulses
			Vector2 impulse = new Vector2(impulse3.x, impulse3.y);
			this.body1.getVelocity().add(impulse.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (r1.cross(impulse) + impulse3.z));
			this.body2.getVelocity().subtract(impulse.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (r2.cross(impulse) + impulse3.z));
		} else {
			// solve the point-to-point constraint
			Vector2 impulse = this.K.solve22(Jvb2.negate());
			this.impulse.x += impulse.x;
			this.impulse.y += impulse.y;
			
			this.body1.getVelocity().add(impulse.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(impulse));
			this.body2.getVelocity().subtract(impulse.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(impulse));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		double angularTolerance = settings.getAngularTolerance();
		double maxAngularCorrection = settings.getMaxAngularCorrection();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		double linearError = 0.0;
		double angularError = 0.0;

		// solve the angular constraint if the limits are active
		if (this.limitEnabled && this.limitState != Joint.LimitState.INACTIVE) {
			// get the current angle between the bodies
			double angle = t1.getRotation() - t2.getRotation() - this.referenceAngle;
			double impulse = 0.0;
			// check the limit state
			if (this.limitState == Joint.LimitState.EQUAL) {
				// if the limits are equal then clamp the impulse to maintain
				// the constraint between the maximum
				double j = Interval.clamp(angle - this.lowerLimit, -maxAngularCorrection, maxAngularCorrection);
				impulse = -j * this.motorMass;
				angularError = Math.abs(j);
			} else if (this.limitState == Joint.LimitState.AT_LOWER) {
				// if the joint is at the lower limit then clamp only the lower value
				double j = angle - this.lowerLimit;
				angularError = -j;
				j = Interval.clamp(j + angularTolerance, -maxAngularCorrection, 0.0);
				impulse = -j * this.motorMass;
			} else if (this.limitState == Joint.LimitState.AT_UPPER) {
				// if the joint is at the upper limit then clamp only the upper value
				double j = angle - this.upperLimit;
				angularError = j;
				j = Interval.clamp(j - angularTolerance, 0.0, maxAngularCorrection);
				impulse = -j * this.motorMass;
			}
			
			// apply the impulse
			this.body1.rotateAboutCenter(invI1 * impulse);
			this.body2.rotateAboutCenter(-invI2 * impulse);
		}
		
		// always solve the point-to-point constraint
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 p1 = this.body1.getWorldCenter().add(r1);
		Vector2 p2 = this.body2.getWorldCenter().add(r2);
		Vector2 p = p1.difference(p2);
		
		linearError = p.getMagnitude();

		// compute the K matrix
		Matrix22 K = new Matrix22();
		K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		K.m01 = -invI1 * r1.x * r1.y - invI2 * r2.x * r2.y; 
		K.m10 = this.K.m01;
		K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		
		// solve for the impulse
		Vector2 J = K.solve(p.negate());

		// translate and rotate the objects
		this.body1.translate(J.product(invM1));
		this.body1.rotateAboutCenter(invI1 * r1.cross(J));
		
		this.body2.translate(J.product(-invM2));
		this.body2.rotateAboutCenter(-invI2 * r2.cross(J));
		
		return linearError <= linearTolerance && angularError <= angularTolerance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getType()
	 */
	@Override
	public Type getType() {
		return RevoluteJoint.TYPE;
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
		return new Vector2(this.impulse.x * invdt, this.impulse.y * invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.impulse.z * invdt;
	}
	
	/**
	 * Returns the relative speed at which the {@link Body}s
	 * are rotating in radians/second.
	 * @return double
	 */
	public double getJointSpeed() {
		return this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
	}
	
	/**
	 * Returns the relative angle between the two {@link Body}s in radians.
	 * @return double
	 */
	public double getJointAngle() {
		return this.body2.getTransform().getRotation() - this.body1.getTransform().getRotation();
	}
	
	/**
	 * Returns true if this motor is enabled.
	 * @return boolean
	 */
	public boolean isMotorEnabled() {
		return this.motorEnabled;
	}
	
	/**
	 * Sets whether the motor for this joint is enabled or not.
	 * @param flag true if the motor should be enabled
	 */
	public void setMotorEnabled(boolean flag) {
		// wake up the associated bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the flag
		this.motorEnabled = flag;
	}
	
	/**
	 * Returns the maximum torque this motor will apply in newton-meters.
	 * @return double
	 */
	public double getMaxMotorTorque() {
		return this.maxMotorTorque;
	}
	
	/**
	 * Sets the maximum torque this motor will apply in newton-meters.
	 * @param maxMotorTorque the maximum motor torque in newton-meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if maxMotorTorque is less than zero
	 */
	public void setMaxMotorTorque(double maxMotorTorque) {
		// make sure its positive
		if (maxMotorTorque < 0.0) throw new IllegalArgumentException("The maximum motor torque must be greater than or equal to zero.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the max
		this.maxMotorTorque = maxMotorTorque;
	}
	
	/**
	 * Returns the desired motor speed in radians/second.
	 * @return double
	 */
	public double getMotorSpeed() {
		return motorSpeed;
	}
	
	/**
	 * Sets the target motor speed in radians/second
	 * @param motorSpeed the motor speed desired in radians/second
	 */
	public void setMotorSpeed(double motorSpeed) {
		// if so, then wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the motor speed
		this.motorSpeed = motorSpeed;
	}
	
	/**
	 * Returns the motor torque in newton-meters.
	 * @return double
	 */
	public double getMotorTorque() {
		return this.motorImpulse;
	}
	
	/**
	 * Returns true if the rotational limit is enabled.
	 * @return boolean
	 */
	public boolean isLimitEnabled() {
		return limitEnabled;
	}
	
	/**
	 * Enables or disables the rotational limit.
	 * @param flag true if the limit should be enabled
	 */
	public void setLimitEnabled(boolean flag) {
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.limitEnabled = flag;
	}
	
	/**
	 * Returns the upper rotational limit in radians.
	 * @return double
	 */
	public double getUpperLimit() {
		return upperLimit;
	}
	
	/**
	 * Sets the upper rotational limit.
	 * <p>
	 * Must be greater than or equal to the lower rotational limit.
	 * @param upperLimit the upper rotational limit in radians
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException("The upper limit cannot be less than the lower limit.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.upperLimit = upperLimit;
	}
	
	/**
	 * Returns the lower rotational limit in radians.
	 * @return double
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}
	
	/**
	 * Sets the lower rotational limit.
	 * <p>
	 * Must be less than or equal to the upper rotational limit.
	 * @param lowerLimit the lower rotational limit in radians
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException("The lower limit cannot be greater than the upper limit.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.lowerLimit = lowerLimit;
	}
	
	/**
	 * Sets the upper and lower rotational limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * @param lowerLimit the lower limit in radians
	 * @param upperLimit the upper limit in radians
	 * @throws IllegalArgumentException if the lowerLimit is greater than upperLimit
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
