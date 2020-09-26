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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a pivot joint.
 * <p>
 * A pivot joint allows two bodies to rotate freely about a common point, but 
 * does not allow them to translate relative to one another.  The system as a
 * whole can translate and rotate freely.
 * <p>
 * By default the lower and upper limit angles are set to the current angle 
 * between the bodies.  When the lower and upper limits are equal, the bodies 
 * rotate together and are not allowed rotate relative to one another.  By
 * default the limits are disabled.
 * <p>
 * If the lower and upper limits are set explicitly, the values must follow 
 * these restrictions:
 * <ul>
 * <li>lower limit &le; upper limit</li>
 * <li>lower limit &gt; -180</li>
 * <li>upper limit &lt; 180</li>
 * </ul> 
 * To create a joint with limits outside of this range use the 
 * {@link #setReferenceAngle(double)} method.  This method sets the baseline 
 * angle for the joint, which represents 0 radians in the context of the 
 * limits.  For example:
 * <pre>
 * // we would like the joint limits to be [30, 260]
 * // this is the same as the limits [-60, 170] if the reference angle is 90
 * joint.setLimits(Math.toRadians(-60), Math.toRadians(170));
 * joint.setReferenceAngle(Math.toRadians(90));
 * </pre>
 * This joint also supports a motor.  The motor is an angular motor about the
 * anchor point.  The motor speed can be positive or negative to indicate a
 * clockwise or counter-clockwise rotation.  The maximum motor torque must be 
 * greater than zero for the motor to apply any motion.
 * @author William Bittle
 * @version 4.0.1
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Revolute_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/07/point-to-point-constraint/" target="_blank">Point-to-Point Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class RevoluteJoint<T extends PhysicsBody> extends Joint<T> implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;

	/** The initial angle between the two {@link PhysicsBody}s */
	protected double referenceAngle;
	
	// limits
	
	/** Whether the {@link Joint} limits are enabled or not */
	protected boolean limitEnabled;
	
	/** The upper limit of the {@link Joint} */
	protected double upperLimit;
	
	/** The lower limit of the {@link Joint} */
	protected double lowerLimit;
	
	// motor
	
	/** Whether the motor for this {@link Joint} is enabled or not */
	protected boolean motorEnabled;
	
	/** The target motor speed; in radians / second */
	protected double motorSpeed;
	
	/** The maximum torque the motor can apply */
	protected double maximumMotorTorque;

	// current state

	/** The current angle between the bodies */
	private double angle;
		
	/** The angular mass about the pivot point */
	private double axialMass;
	
	/** True if the axial mass was close or equal to zero */
	private boolean fixedRotation;
	
	/** The world space vector from b1's COM to the pivot point */
	private Vector2 r1;
	
	/** The world space vector from b2's COM to the pivot point */
	private Vector2 r2;

	/** The pivot mass; K = J * Minv * Jtrans */
	private final Matrix22 K;
	
	// output

	/** The linear impulse applied by the point-to-point constraint */
	private Vector2 impulse;
		
	/** The impulse applied by the motor */
	private double motorImpulse;
	
	/** The impulse applied by the lower limit */
	private double lowerImpulse;
	
	/** The impulse applied by the upper limit */
	private double upperImpulse;

	/**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @throws NullPointerException if body1, body2 or anchor is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public RevoluteJoint(T body1, T body2, Vector2 anchor) {
		// default to no collision allowed between the bodies
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// make sure the anchor point is not null
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
		
		// get the local space points
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		
		// get the initial reference angle for the joint limits
		this.referenceAngle = body1.getTransform().getRotationAngle() - body2.getTransform().getRotationAngle();
		
		// default limits
		this.lowerLimit = this.referenceAngle;
		this.upperLimit = this.referenceAngle;
		this.limitEnabled = false;
		
		// motor defaults
		this.motorSpeed = 0.0;
		this.maximumMotorTorque = 1000.0;
		this.motorEnabled = false;
		
		this.axialMass = 0.0;
		this.fixedRotation = false;
		this.r1 = null;
		this.r2 = null;
		this.angle = 0.0;
		
		this.impulse = new Vector2();
		this.lowerImpulse = 0.0;
		this.upperImpulse = 0.0;
		this.motorImpulse = 0.0;
		
		this.K = new Matrix22();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RevoluteJoint[").append(super.toString())
		  .append("|Anchor=").append(this.getAnchor1())
		  .append("|IsMotorEnabled=").append(this.motorEnabled)
		  .append("|MotorSpeed=").append(this.motorSpeed)
		  .append("|MaximumMotorTorque=").append(this.maximumMotorTorque)
		  .append("|IsLimitEnabled=").append(this.limitEnabled)
		  .append("|LowerLimit=").append(this.lowerLimit)
		  .append("|UpperLimit=").append(this.upperLimit)
		  .append("|ReferenceAngle=").append(this.referenceAngle)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
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
				throw new IllegalStateException(Messages.getString("dynamics.joint.revolute.twoAngularFixedBodies"));
			}
		}
		
		this.r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		this.r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the K matrix for the point-to-point constraint
		this.K.m00 = invM1 + invM2 + this.r1.y * this.r1.y * invI1 + this.r2.y * this.r2.y * invI2;
		this.K.m01 = -this.r1.y * this.r1.x * invI1 - this.r2.y * this.r2.x * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + this.r1.x * this.r1.x * invI1 + this.r2.x * this.r2.x * invI2;
		
		// compute the axial mass
		this.axialMass = invI1 + invI2;
		if (this.axialMass > Epsilon.E) {
			this.axialMass = 1.0 / this.axialMass;
			this.fixedRotation = false;
		} else {
			this.fixedRotation = true;
		}
		
		// check if the motor is disabled
		if (!this.motorEnabled) {
			// if not then make the current motor impulse zero
			this.motorImpulse = 0.0;
		}
		
		// compute the current angle
		this.angle = this.getRelativeRotation();
		
		// handle no limits (or if the two bodies have fixed rotation)
		if (!this.limitEnabled || this.fixedRotation) {
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
		
		// handle no motor (or if the two bodies have fixed rotation)
		if (!this.motorEnabled || this.fixedRotation) {
			this.motorImpulse = 0.0;
		}
		
		if (settings.isWarmStartingEnabled()) {
			// account for variable time step
			double dtr = step.getDeltaTimeRatio();
			
			this.impulse.multiply(dtr);
			this.motorImpulse *= dtr;
			this.lowerImpulse *= dtr;
			this.upperImpulse *= dtr;
			
			double axialImpulse = this.motorImpulse + this.lowerImpulse - this.upperImpulse;
			
			// warm start
			Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
			this.body1.getLinearVelocity().add(impulse.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (this.r1.cross(impulse) + axialImpulse));
			this.body2.getLinearVelocity().subtract(impulse.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (this.r2.cross(impulse) + axialImpulse));
		} else {
			this.impulse.zero();
			this.motorImpulse = 0.0;
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(TimeStep step, Settings settings) {		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// solve the motor constraint
		if (this.motorEnabled && !this.fixedRotation) {
			// get the relative velocity - the target motor speed
			double C = this.body1.getAngularVelocity() - this.body2.getAngularVelocity() - this.motorSpeed;
			// get the impulse required to obtain the speed
			double impulse = this.axialMass * -C;
			// clamp the impulse between the maximum torque
			double oldImpulse = this.motorImpulse;
			double maxImpulse = this.maximumMotorTorque * step.getDeltaTime();
			this.motorImpulse = Interval.clamp(this.motorImpulse + impulse, -maxImpulse, maxImpulse);
			// get the impulse we need to apply to the bodies
			impulse = this.motorImpulse - oldImpulse;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
        }
		
		// solve the limit constraints
		// check if the limit constraint is enabled
		if (this.limitEnabled && !this.fixedRotation) {
			// lower limit
			{
				double C = this.angle - this.lowerLimit;
				double Cdot = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
				double impulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				double oldImpulse = this.lowerImpulse;
				this.lowerImpulse = Math.max(this.lowerImpulse + impulse, 0.0);
				impulse = this.lowerImpulse - oldImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
			}
			
			// upper limit
			{
				double C = this.upperLimit - this.angle;
				double Cdot = this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
				double impulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				double oldImpulse = this.upperImpulse;
				this.upperImpulse = Math.max(this.upperImpulse + impulse, 0.0);
				impulse = this.upperImpulse - oldImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * impulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * impulse);
			}
		}

		// finally solve the point-to-point constraint
		Vector2 v1 = this.body1.getLinearVelocity().sum(this.r1.cross(this.body1.getAngularVelocity()));
		Vector2 v2 = this.body2.getLinearVelocity().sum(this.r2.cross(this.body2.getAngularVelocity()));
		// the 2x2 version of Jv + b
		Vector2 Jvb2 = v1.subtract(v2);
		
		// solve the point-to-point constraint
		Vector2 impulse = this.K.solve(Jvb2.negate());
		this.impulse.x += impulse.x;
		this.impulse.y += impulse.y;
		
		this.body1.getLinearVelocity().add(impulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * this.r1.cross(impulse));
		this.body2.getLinearVelocity().subtract(impulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * this.r2.cross(impulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		double angularTolerance = settings.getAngularTolerance();
		double maxAngularCorrection = settings.getMaximumAngularCorrection();
		
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

		// solve position constraint for limits
		if (this.limitEnabled && !this.fixedRotation) {
			double angle = this.getRelativeRotation();
			double C = 0.0;
			
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * angularTolerance) {
				C = Interval.clamp(angle - this.lowerLimit, -maxAngularCorrection, maxAngularCorrection);
			} else if (angle <= this.lowerLimit) {
				C = Interval.clamp(angle - this.lowerLimit + angularTolerance, -maxAngularCorrection, 0.0);
			} else if (angle >= this.upperLimit) {
				C = Interval.clamp(angle - this.upperLimit - angularTolerance, 0.0, maxAngularCorrection);
			}
			
			double impulse = -this.axialMass * C;
			this.body1.rotateAboutCenter(invI1 * impulse);
			this.body2.rotateAboutCenter(-invI2 * impulse);
			angularError = Math.abs(C);
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

	/**
	 * Returns the relative angle between the two bodies given the reference angle.
	 * @return double
	 */
	private double getRelativeRotation() {
		double rr = this.body1.getTransform().getRotationAngle() - this.body2.getTransform().getRotationAngle() - this.referenceAngle;
		if (rr < -Math.PI) rr += Geometry.TWO_PI;
		if (rr > Math.PI) rr -= Geometry.TWO_PI;
		return rr;
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
		return new Vector2(this.impulse.x * invdt, this.impulse.y * invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return (this.lowerImpulse - this.upperImpulse) * invdt;
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
	 * Returns the relative speed at which the {@link PhysicsBody}s
	 * are rotating in radians/second.
	 * @return double
	 */
	public double getJointSpeed() {
		return this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
	}
	
	/**
	 * Returns the relative angle between the two {@link PhysicsBody}s in radians in the range [-&pi;, &pi;].
	 * @return double
	 */
	public double getJointAngle() {
		return this.getRelativeRotation();
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
		if (this.motorEnabled != flag) {
			// wake up the associated bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the flag
			this.motorEnabled = flag;
		}
	}
	
	/**
	 * Returns the maximum torque this motor will apply in newton-meters.
	 * @return double
	 */
	public double getMaximumMotorTorque() {
		return this.maximumMotorTorque;
	}
	
	/**
	 * Sets the maximum torque this motor will apply in newton-meters.
	 * @param maximumMotorTorque the maximum motor torque in newton-meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if maxMotorTorque is less than zero
	 * @see #setMotorSpeed(double)
	 */
	public void setMaximumMotorTorque(double maximumMotorTorque) {
		// make sure its positive
		if (maximumMotorTorque < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidMaximumMotorTorque"));
		if (this.maximumMotorTorque != maximumMotorTorque) {
			if (this.motorEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			this.maximumMotorTorque = maximumMotorTorque;
		}
	}
	
	/**
	 * Returns the desired motor speed in radians/second.
	 * @return double
	 */
	public double getMotorSpeed() {
		return this.motorSpeed;
	}
	
	/**
	 * Sets the target motor speed in radians/second.
	 * @param motorSpeed the motor speed desired in radians/second
	 * @see #setMaximumMotorTorque(double)
	 */
	public void setMotorSpeed(double motorSpeed) {
		if (this.motorSpeed != motorSpeed) {
			// only wake the bodies if the motor is enabled
			if (this.motorEnabled) {
				// if so, then wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the motor speed
			this.motorSpeed = motorSpeed;
		}
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
		return this.limitEnabled;
	}
	
	/**
	 * Enables or disables the rotational limit.
	 * @param flag true if the limit should be enabled
	 */
	public void setLimitEnabled(boolean flag) {
		// check if its changing
		if (this.limitEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.limitEnabled = flag;
			// clear the accumulated limit impulse
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
	}
	
	/**
	 * Returns the upper rotational limit in radians.
	 * @return double
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/**
	 * Sets the upper rotational limit.
	 * <p>
	 * Must be greater than or equal to the lower rotational limit.
	 * <p>
	 * See the class documentation for more details on the limit ranges.
	 * @param upperLimit the upper rotational limit in radians
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		if (this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.upperImpulse = 0.0;
		}
	}
	
	/**
	 * Returns the lower rotational limit in radians.
	 * @return double
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/**
	 * Sets the lower rotational limit.
	 * <p>
	 * Must be less than or equal to the upper rotational limit.
	 * <p>
	 * See the class documentation for more details on the limit ranges.
	 * @param lowerLimit the lower rotational limit in radians
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		if (this.lowerLimit != lowerLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.lowerLimit = lowerLimit;
			// clear accumulated impulse
			this.lowerImpulse = 0.0;
		}
	}
	
	/**
	 * Sets the upper and lower rotational limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * <p>
	 * See the class documentation for more details on the limit ranges.
	 * @param lowerLimit the lower limit in radians
	 * @param upperLimit the upper limit in radians
	 * @throws IllegalArgumentException if the lowerLimit is greater than upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and one of the limits has changed
			if (this.limitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the values
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
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
	 * @param angle the reference angle in radians
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
	 * @deprecated Deprecated in 4.0.0.
	 */
	@Deprecated
	public LimitState getLimitState() {
		return LimitState.INACTIVE;
	}
}
