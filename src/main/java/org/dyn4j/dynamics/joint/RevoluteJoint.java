/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.Ownable;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

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
 * default the limits are disabled.  If you want the lower and upper limit to
 * be the same, use a {@link WeldJoint} instead.
 * <p>
 * If the lower and upper limits are set explicitly, the values must follow 
 * these restrictions:
 * <ul>
 * <li>lower limit &le; upper limit</li>
 * <li>lower limit &gt; -180</li>
 * <li>upper limit &lt; 180</li>
 * </ul> 
 * To create a joint with limits outside of this range use the 
 * {@link #setLimitsReferenceAngle(double)} method.  This method sets the 
 * baseline angle for the joint, which represents 0 radians in the context of
 * the limits.  For example:
 * <pre>
 * // we would like the joint limits to be [30, 260]
 * // this is the same as the limits [-60, 170] if the reference angle is 90
 * joint.setLimits(Math.toRadians(-60), Math.toRadians(170));
 * joint.setReferenceAngle(Math.toRadians(90));
 * </pre>
 * This joint also supports a motor.  The motor is an angular motor about the
 * anchor point.  The motor speed can be positive or negative to indicate a
 * clockwise or counter-clockwise rotation.  The maximum motor torque must be 
 * greater than zero for the motor to apply any motion, but can be disabled 
 * or enabled using {@link #setMaximumMotorTorqueEnabled(boolean)}.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Revolute_Joint" target="_blank">Documentation</a>
 * @see <a href="https://www.dyn4j.org/2010/07/point-to-point-constraint/" target="_blank">Point-to-Point Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class RevoluteJoint<T extends PhysicsBody> extends AbstractPairedBodyJoint<T> implements AngularLimitsJoint, AngularMotorJoint, PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;

	// limits
	
	/** Whether the {@link Joint} limits are enabled or not */
	protected boolean limitsEnabled;
	
	/** The upper limit of the {@link Joint} */
	protected double upperLimit;
	
	/** The lower limit of the {@link Joint} */
	protected double lowerLimit;

	/** The initial angle between the two {@link PhysicsBody}s */
	protected double referenceAngle;
	
	// motor
	
	/** Whether the motor for this {@link Joint} is enabled or not */
	protected boolean motorEnabled;
	
	/** The target motor speed; in radians / second */
	protected double motorSpeed;
	
	/** True if the motor maximum torque has been enabled */
	protected boolean motorMaximumTorqueEnabled;
	
	/** The maximum torque the motor can apply */
	protected double motorMaximumTorque;

	// current state

	/** The current angle between the bodies */
	double angle;
		
	/** The angular mass about the pivot point */
	double axialMass;
	
	/** True if the axial mass was close or equal to zero */
	boolean fixedRotation;
	
	/** The world space vector from b1's COM to the pivot point */
	final Vector2 r1;
	
	/** The world space vector from b2's COM to the pivot point */
	final Vector2 r2;

	/** The pivot mass; K = J * Minv * Jtrans */
	final Matrix22 K;
	
	// output

	/** The linear impulse applied by the point-to-point constraint */
	final Vector2 impulse;
		
	/** The impulse applied by the motor */
	double motorImpulse;
	
	/** The impulse applied by the lower limit */
	double lowerLimitImpulse;
	
	/** The impulse applied by the upper limit */
	double upperLimitImpulse;

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
		super(body1, body2);
		
		// make sure the anchor point is not null
		if (anchor == null) 
			throw new ArgumentNullException("anchor");
		
		// get the local space points
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		
		// get the initial reference angle for the joint limits
		this.referenceAngle = body1.getTransform().getRotationAngle() - body2.getTransform().getRotationAngle();
		
		// default limits
		this.lowerLimit = this.referenceAngle;
		this.upperLimit = this.referenceAngle;
		this.limitsEnabled = false;
		
		// motor defaults
		this.motorSpeed = 0.0;
		this.motorMaximumTorque = 1000.0;
		this.motorEnabled = false;
		this.motorMaximumTorqueEnabled = false;
		
		this.axialMass = 0.0;
		this.fixedRotation = false;
		this.r1 = new Vector2();
		this.r2 = new Vector2();
		this.angle = 0.0;
		
		this.impulse = new Vector2();
		this.lowerLimitImpulse = 0.0;
		this.upperLimitImpulse = 0.0;
		this.motorImpulse = 0.0;
		
		this.K = new Matrix22();
	}

	/**
	 * Copy constructor.
	 * @param joint the joint to copy
	 * @since 6.0.0
	 */
	protected RevoluteJoint(RevoluteJoint<T> joint) {
		this(joint, null, null);
	}
	
	/**
	 * Copy constructor.
	 * @param joint the joint to copy
	 * @param body1 the first body
	 * @param body2 the second body
	 * @since 6.0.0
	 */
	protected RevoluteJoint(RevoluteJoint<T> joint, T body1, T body2) {
		super(joint, body1, body2);
		
		this.localAnchor1 = joint.localAnchor1.copy();
		this.localAnchor2 = joint.localAnchor2.copy();
		
		// limits
		this.limitsEnabled = joint.limitsEnabled;
		this.lowerLimit = joint.lowerLimit;
		this.upperLimit = joint.upperLimit;
		this.referenceAngle = joint.referenceAngle;
		
		// motor
		this.motorEnabled = joint.motorEnabled;
		this.motorMaximumTorque = joint.motorMaximumTorque;
		this.motorMaximumTorqueEnabled = joint.motorMaximumTorqueEnabled;
		this.motorSpeed = joint.motorSpeed;
		
		// state
		this.angle = joint.angle;
		this.axialMass = joint.axialMass;
		this.fixedRotation = joint.fixedRotation;
		this.K = joint.K.copy();
		this.r1 = joint.r1.copy();
		this.r2 = joint.r2.copy();
		
		// output
		this.impulse = joint.impulse.copy();
		this.motorImpulse = joint.motorImpulse;
		this.lowerLimitImpulse = joint.lowerLimitImpulse;
		this.upperLimitImpulse = joint.upperLimitImpulse;
	}

	/**
	 * {@inheritDoc}
	 * @return {@link RevoluteJoint}
	 * @see #copy(PhysicsBody, PhysicsBody)
	 * @since 6.0.0
	 */
	@Override
	public RevoluteJoint<T> copy() {
		return new RevoluteJoint<T>(this);
	}
	
	/**
	 * {@inheritDoc}
	 * @return {@link RevoluteJoint}
	 * @since 6.0.0
	 */
	@Override
	public RevoluteJoint<T> copy(T body1, T body2) {
		return new RevoluteJoint<T>(this, body1, body2);
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
		  .append("|MaximumMotorTorque=").append(this.motorMaximumTorque)
		  .append("|IsLimitEnabled=").append(this.limitsEnabled)
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
		
		// update r1 and r2
		t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1), this.r1);
		t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2), this.r2);
		
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
			this.axialMass = 0.0;
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
		if (!this.limitsEnabled || this.fixedRotation) {
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
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
			this.lowerLimitImpulse *= dtr;
			this.upperLimitImpulse *= dtr;
			
			double axialImpulse = this.motorImpulse + this.lowerLimitImpulse - this.upperLimitImpulse;
			
			// warm start
			Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
			this.body1.getLinearVelocity().add(impulse.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (this.r1.cross(impulse) + axialImpulse));
			this.body2.getLinearVelocity().subtract(impulse.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (this.r2.cross(impulse) + axialImpulse));
		} else {
			this.impulse.zero();
			this.motorImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
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
			double stepImpulse = this.axialMass * -C;
			
			if (this.motorMaximumTorqueEnabled) {
				// clamp the impulse between the maximum torque
				double currentAccumulatedImpulse = this.motorImpulse;
				double maxImpulse = this.motorMaximumTorque * step.getDeltaTime();
				this.motorImpulse = Interval.clamp(this.motorImpulse + stepImpulse, -maxImpulse, maxImpulse);
				// get the impulse we need to apply to the bodies
				stepImpulse = this.motorImpulse - currentAccumulatedImpulse;
			} else {
				this.motorImpulse += stepImpulse;
			}
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * stepImpulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * stepImpulse);
        }
		
		// solve the limit constraints
		// check if the limit constraint is enabled
		if (this.limitsEnabled && !this.fixedRotation) {
			// lower limit
			{
				double C = this.angle - this.lowerLimit;
				double Cdot = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
				double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				
				// clamp
				double currentAccumulatedImpulse = this.lowerLimitImpulse;
				this.lowerLimitImpulse = Math.max(this.lowerLimitImpulse + stepImpulse, 0.0);
				stepImpulse = this.lowerLimitImpulse - currentAccumulatedImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * stepImpulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * stepImpulse);
			}
			
			// upper limit
			{
				double C = this.upperLimit - this.angle;
				double Cdot = this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
				double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				
				// clamp
				double currentAccumulatedImpulse = this.upperLimitImpulse;
				this.upperLimitImpulse = Math.max(this.upperLimitImpulse + stepImpulse, 0.0);
				stepImpulse = this.upperLimitImpulse - currentAccumulatedImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * stepImpulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * stepImpulse);
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
		if (this.limitsEnabled && !this.fixedRotation) {
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
	
	/**
	 * The anchor point in world space on the first body.
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * The anchor point in world space on the second body.
	 * @return {@link Vector2}
	 */
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
		return (this.motorImpulse + this.lowerLimitImpulse - this.upperLimitImpulse) * invdt;
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
	public double getAngularSpeed() {
		return this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
	}
	
	/**
	 * Returns the relative angle between the two {@link PhysicsBody}s in radians in the range [-&pi;, &pi;].
	 * @return double
	 */
	public double getAngularTranslation() {
		return this.getRelativeRotation();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#isMotorEnabled()
	 */
	public boolean isMotorEnabled() {
		return this.motorEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorEnabled(boolean)
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#getMotorMaximumTorque()
	 */
	public double getMaximumMotorTorque() {
		return this.motorMaximumTorque;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorMaximumTorque(double)
	 */
	public void setMaximumMotorTorque(double maximumMotorTorque) {
		// make sure its positive
		if (maximumMotorTorque < 0.0) 
			throw new ValueOutOfRangeException("maximumMotorTorque", maximumMotorTorque, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (this.motorMaximumTorque != maximumMotorTorque) {
			if (this.motorEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			this.motorMaximumTorque = maximumMotorTorque;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorMaximumTorqueEnabled(boolean)
	 */
	@Override
	public void setMaximumMotorTorqueEnabled(boolean enabled) {
		if (this.motorMaximumTorqueEnabled != enabled) {
			this.motorMaximumTorqueEnabled = enabled;
			
			if (this.motorEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#isMotorMaximumTorqueEnabled()
	 */
	@Override
	public boolean isMaximumMotorTorqueEnabled() {
		return this.motorMaximumTorqueEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#getMotorSpeed()
	 */
	public double getMotorSpeed() {
		return this.motorSpeed;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorSpeed(double)
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
			this.motorImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#getMotorTorque(double)
	 */
	public double getMotorTorque(double invdt) {
		return this.motorImpulse * invdt;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#isLimitsEnabled()
	 */
	public boolean isLimitsEnabled() {
		return this.limitsEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(boolean)
	 */
	public void setLimitsEnabled(boolean flag) {
		// check if its changing
		if (this.limitsEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.limitsEnabled = flag;
			// clear the accumulated limit impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getUpperLimit()
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setUpperLimit(double)
	 */
	public void setUpperLimit(double upperLimit) {
		if (upperLimit < this.lowerLimit) 
			throw new ValueOutOfRangeException("upperLimit", upperLimit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, "lowerLimit", this.lowerLimit);
		
		if (this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getLowerLimit()
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLowerLimit(double)
	 */
	public void setLowerLimit(double lowerLimit) {
		if (lowerLimit > this.upperLimit) 
			throw new ValueOutOfRangeException("lowerLimit", lowerLimit, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, "upperLimit", this.upperLimit);
		
		if (this.lowerLimit != lowerLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.lowerLimit = lowerLimit;
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double, double)
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		if (lowerLimit > upperLimit) 
			throw new ValueOutOfRangeException("lowerLimit", lowerLimit, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, "upperLimit", upperLimit);
		
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and one of the limits has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the values
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double, double)
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double)
	 */
	public void setLimitsEnabled(double limit) {
		this.setLimitsEnabled(limit, limit);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double)
	 */
	public void setLimits(double limit) {
		if (this.lowerLimit != limit || this.upperLimit != limit) {
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = limit;
			this.lowerLimit = limit;
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getLimitsReferenceAngle()
	 */
	@Override
	public double getLimitsReferenceAngle() {
		return this.referenceAngle;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsReferenceAngle(double)
	 */
	@Override
	public void setLimitsReferenceAngle(double angle) {
		if (this.referenceAngle != angle) {
			this.referenceAngle = angle;
			
			if (this.limitsEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
}
