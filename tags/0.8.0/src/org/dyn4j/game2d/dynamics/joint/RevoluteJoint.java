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
package org.dyn4j.game2d.dynamics.joint;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Settings;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Matrix;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a pivot joint.
 * @author William Bittle
 */
// TODO implement limits
public class RevoluteJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Revolute");
	
	/** The local anchor point on the first {@link Body} */
	protected Vector localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector localAnchor2;
	
	/** Whether the motor for this {@link Joint} is enabled or not */
	protected boolean motorEnabled;
	
	/** The target motor speed; in radians / second */
	protected double motorSpeed;
	
	/** The maximum torque the motor can apply */
	protected double maxMotorTorque;
	
	/** The pivot mass (Kinv = (J * Minv * Jtrans)inv) */
	protected Matrix invK;
	
	/** The accumulated impulse for warm starting */
	protected Vector impulse;
	
	/** The motor mass that resists motion */
	protected double motorMass;
	
	/** The impulse applied by the motor */
	protected double motorImpulse;
	
	/**
	 * Optional constructor.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 */
	public RevoluteJoint(Body b1, Body b2, Vector anchor) {
		this(b1, b2, false, anchor);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a revolute joint between the given {@link Body}s using the given
	 * world space anchor point.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param collisionAllowed whether collision between the joined {@link Body}s is allowed
	 * @param anchor the anchor point in world coordinates
	 */
	public RevoluteJoint(Body b1, Body b2, boolean collisionAllowed, Vector anchor) {
		this(b1, b2, collisionAllowed, anchor, false, 0.0, 0.0);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a revolute joint between the given {@link Body}s using the given
	 * world space anchor point.
	 * <p>
	 * Allows a motor to be built into the joint by setting the target motor speed.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param collisionAllowed whether collision between the joined {@link Body}s is allowed
	 * @param anchor the anchor point in world coordinates
	 * @param motorEnabled true if the motor should be enabled
	 * @param motorSpeed the target motor speed in radians/second
	 * @param maxMotorTorque the maximum torque the motor can apply in newton-meters
	 */
	public RevoluteJoint(Body b1, Body b2, boolean collisionAllowed, Vector anchor, 
			boolean motorEnabled, double motorSpeed, double maxMotorTorque) {
		super(b1, b2, collisionAllowed);
		if (anchor == null) throw new NullPointerException("The anchor point cannot be null.");
		this.localAnchor1 = b1.getLocalPoint(anchor);
		this.localAnchor2 = b2.getLocalPoint(anchor);
		this.impulse = new Vector();
		this.motorEnabled = motorEnabled;
		this.motorSpeed = motorSpeed;
		this.maxMotorTorque = maxMotorTorque;
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
		.append(this.impulse).append("|")
		.append(this.motorEnabled).append("|")
		.append(this.motorSpeed).append("|")
		.append(this.maxMotorTorque).append("|")
		.append(this.motorImpulse).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
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
		
		Vector r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the K inverse matrix
		Matrix K1 = new Matrix();
		K1.m00 = invM1 + invM2;	K1.m01 = 0.0;
		K1.m10 = 0.0;			K1.m11 = invM1 + invM2;

		Matrix K2 = new Matrix();
		K2.m00 =  invI1 * r1.y * r1.y;	K2.m01 = -invI1 * r1.x * r1.y;
		K2.m10 = -invI1 * r1.x * r1.y;	K2.m11 =  invI1 * r1.x * r1.x;

		Matrix K3 = new Matrix();
		K3.m00 =  invI2 * r2.y * r2.y;	K3.m01 = -invI2 * r2.x * r2.y;
		K3.m10 = -invI2 * r2.x * r2.y;	K3.m11 =  invI2 * r2.x * r2.x;

		Matrix K = new Matrix(K1);
		K.add(K2).add(K3);
		
		this.invK = K.invert();
		
		// compute the motor mass
		this.motorMass = invI1 + invI2;
		if (this.motorMass != 0.0) {
			this.motorMass = 1.0 / this.motorMass;
		}
		
		// check if the motor is still enabled
		if (!this.motorEnabled) {
			this.motorImpulse = 0.0;
		}
		
		// account for variable time step
		this.impulse.multiply(step.getDeltaTimeRatio());
		this.motorImpulse *= step.getDeltaTimeRatio();
		
		// warm start
		this.body1.getVelocity().add(this.impulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(this.impulse) + this.motorImpulse);
		this.body2.getVelocity().subtract(this.impulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(this.impulse) - this.motorImpulse);
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
		if (this.motorEnabled) {
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
		
		// solve the point-to-point constraint		
		Vector r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector v1 = this.body1.getVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
		Vector v2 = this.body2.getVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
		Vector pivotV = v1.subtract(v2);
		
		Vector P = this.invK.multiply(pivotV).negate();
		this.impulse.add(P);
		
		this.body1.getVelocity().add(P.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(P));
		this.body2.getVelocity().subtract(P.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(P));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		double error = 0.0f;

		Vector r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector p1 = this.body1.getWorldCenter().add(r1);
		Vector p2 = this.body2.getWorldCenter().add(r2);
		Vector p = p1.difference(p2);

		error = p.getMagnitude();

		// compute the K inverse matrix
		Matrix K1 = new Matrix();
		K1.m00 = invM1 + invM2;	K1.m01 = 0.0;
		K1.m10 = 0.0;			K1.m11 = invM1 + invM2;

		Matrix K2 = new Matrix();
		K2.m00 =  invI1 * r1.y * r1.y;	K2.m01 = -invI1 * r1.x * r1.y;
		K2.m10 = -invI1 * r1.x * r1.y;	K2.m11 =  invI1 * r1.x * r1.x;

		Matrix K3 = new Matrix();
		K3.m00 =  invI2 * r2.y * r2.y;	K3.m01 = -invI2 * r2.x * r2.y;
		K3.m10 = -invI2 * r2.x * r2.y;	K3.m11 =  invI2 * r2.x * r2.x;

		Matrix K = new Matrix(K1);
		K.add(K2).add(K3);
		
		Vector J = K.solve(p.negate());

		// translate and rotate the objects
		this.body1.translate(J.product(invM1));
		this.body1.rotateAboutCenter(invI1 * r1.cross(J));
		
		this.body2.translate(J.product(-invM2));
		this.body2.rotateAboutCenter(-invI2 * r2.cross(J));
		
		return error <= linearTolerance;
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
	public Vector getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector getAnchor2() {
		return this.body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector getReactionForce(double invdt) {
		return this.impulse.product(invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0;
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
		// did the state change?
		if (flag != this.motorEnabled) {
			// wake up the associated bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// set the flag
			this.motorEnabled = flag;
		}
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
	 * @param maxMotorTorque the maximum motor torque in newton-meters
	 */
	public void setMaxMotorTorque(double maxMotorTorque) {
		// see if the maximum is different than the current
		if (this.maxMotorTorque != maxMotorTorque) {
			// wake up the bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// set the max
			this.maxMotorTorque = maxMotorTorque;
		}
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
		// check if the given motor speed is different than the current
		if (this.motorSpeed != motorSpeed) {
			// if so, then wake up the bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// set the motor speed
			this.motorSpeed = motorSpeed;
		}
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
	 * Returns the motor torque in newton-meters.
	 * @return double
	 */
	public double getMotorTorque() {
		return this.motorImpulse;
	}
}
