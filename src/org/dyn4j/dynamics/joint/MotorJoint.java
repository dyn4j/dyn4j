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
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation a motor joint.
 * <p>
 * A motor joint uses a motor to apply forces and torques to move the joined 
 * bodies together.
 * <p>
 * The motor is limited by a maximum force and torque.  By default these are
 * zero and will need to be set before the joint will function properly.
 * Larger values will allow the motor to apply more force and torque to the
 * bodies.  This can have two effects.  The first is that the bodies will
 * move to their correct positions faster.  The second is that the bodies
 * will be moving faster and may overshoot more causing more oscillation.
 * Use the {@link #setCorrectionFactor(double)} method to help reduce the
 * oscillation. 
 * <p>
 * The linear and angular targets are the target distance and angle that the 
 * bodies should achieve relative to each other's position and rotation.  By 
 * default, the linear target will be the distance between the two body centers
 * and the angular target will be the relative rotation of the bodies.  Use the
 * {@link #setLinearTarget(Vector2)} and {@link #setAngularTarget(double)} 
 * methods to set the desired relative translation and rotate between the 
 * bodies.
 * <p>
 * This joint is ideal for character movement as it allows direct control of 
 * the motion using targets, but yet still allows interaction with the 
 * environment.  The best way to achieve this effect is to have the second body
 * be an infinite mass body that doesn't collide with anything.  Then, simply 
 * set the current position and rotation of the infinite mass body.  The 
 * character body will move and rotate smoothly, participating in any collision
 * or with other joints to match the infinite mass body.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.1.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Motor_Joint" target="_blank">Documentation</a>
 */
public class MotorJoint extends Joint implements Shiftable, DataContainer {
	/** The linear target distance from body1's world space center */
	protected Vector2 linearTarget;
	
	/** The target angle between the two body's angles */
	protected double angularTarget;
	
	/** The correction factor in the range [0, 1] */
	protected double correctionFactor;
	
	/** The maximum force the constraint can apply */
	protected double maximumForce;
	
	/** The maximum torque the constraint can apply */
	protected double maximumTorque;
	
	// current state
	
	/** The pivot mass; K = J * Minv * Jtrans */
	private Matrix22 K;
	
	/** The mass for the angular constraint */
	private double angularMass;
	
	/** The calculated linear error in the target distance */
	private Vector2 linearError;
	
	/** The calculated angular error in the target angle */
	private double angularError;

	// output
	
	/** The impulse applied to reduce linear motion */
	private Vector2 linearImpulse;
	
	/** The impulse applied to reduce angular motion */
	private double angularImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @throws NullPointerException if body1 or body2
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public MotorJoint(Body body1, Body body2) {
		// default no collision allowed
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// default the linear target to body2's position in body1's frame
		this.linearTarget = body1.getLocalPoint(body2.getWorldCenter());
		// get the angular target for the joint
		this.angularTarget = body2.getTransform().getRotation() - body1.getTransform().getRotation();
		// initialize
		this.correctionFactor = 0.3;
		this.K = new Matrix22();
		this.linearImpulse = new Vector2();
		this.angularImpulse = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MotorJoint[").append(super.toString())
		  .append("|LinearTarget=").append(this.linearTarget)
		  .append("|AngularTarget=").append(this.angularTarget)
		  .append("|CorrectionFactor=").append(this.correctionFactor)
		  .append("|MaximumForce=").append(this.maximumForce)
		  .append("|MaximumTorque=").append(this.maximumTorque)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().getNegative());
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().getNegative());
		
		// compute the K inverse matrix
		this.K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		this.K.m01 = -invI1 * r1.x * r1.y - invI2 * r2.x * r2.y; 
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		
		this.K.invert();
		
		// compute the angular mass
		this.angularMass = invI1 + invI2;
		if (this.angularMass > Epsilon.E) {
			this.angularMass = 1.0 / this.angularMass;
		}
		
		// compute the error in the linear and angular targets
		Vector2 d1 = r1.sum(this.body1.getWorldCenter());
		Vector2 d2 = r2.sum(this.body2.getWorldCenter());
		Vector2 d0 = t1.getTransformedR(this.linearTarget);
		this.linearError = d2.subtract(d1).subtract(d0);
		this.angularError = this.getAngularError();
		
		// account for variable time step
		this.linearImpulse.multiply(step.getDeltaTimeRatio());
		this.angularImpulse *= step.getDeltaTimeRatio();
		
		// warm start
		this.body1.getLinearVelocity().subtract(this.linearImpulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * (r1.cross(this.linearImpulse) + this.angularImpulse));
		this.body2.getLinearVelocity().add(this.linearImpulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * (r2.cross(this.linearImpulse) + this.angularImpulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
		double dt = step.getDeltaTime();
		double invdt = step.getInverseDeltaTime();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// solve the angular constraint
		{
			// get the relative velocity - the target motor speed
			double C = this.body2.getAngularVelocity() - this.body1.getAngularVelocity() + invdt * this.correctionFactor * this.angularError;
			// get the impulse required to obtain the speed
			double impulse = this.angularMass * -C;
			// clamp the impulse between the maximum torque
			double oldImpulse = this.angularImpulse;
			double maxImpulse = this.maximumTorque * dt;
			this.angularImpulse = Interval.clamp(this.angularImpulse + impulse, -maxImpulse, maxImpulse);
			// get the impulse we need to apply to the bodies
			impulse = this.angularImpulse - oldImpulse;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * impulse);
		}
		
		// solve the point-to-point constraint
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().getNegative());
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().getNegative());
		
		Vector2 v1 = this.body1.getLinearVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
		Vector2 v2 = this.body2.getLinearVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
		Vector2 pivotV = v2.subtract(v1);
		
		pivotV.add(this.linearError.product(this.correctionFactor * invdt));
		
		Vector2 impulse = this.K.multiply(pivotV);
		impulse.negate();
		
		// clamp by the maxforce
		Vector2 oldImpulse = this.linearImpulse.copy();
		this.linearImpulse.add(impulse);
		double maxImpulse = this.maximumForce * dt;
		if (this.linearImpulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
			this.linearImpulse.normalize();
			this.linearImpulse.multiply(maxImpulse);
		}
		impulse = this.linearImpulse.difference(oldImpulse);
		
		this.body1.getLinearVelocity().subtract(impulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * r1.cross(impulse));
		this.body2.getLinearVelocity().add(impulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * r2.cross(impulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		// nothing to do here for this joint since there is no "hard" constraint
		return true;
	}
	
	/**
	 * Returns error in the angle between the joined bodies given the target 
	 * angle.
	 * @return double
	 */
	private double getAngularError() {
		double rr = this.body2.getTransform().getRotation() - this.body1.getTransform().getRotation() - this.angularTarget;
		if (rr < -Math.PI) rr += Geometry.TWO_PI;
		if (rr > Math.PI) rr -= Geometry.TWO_PI;
		return rr;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint.
	 * Returns the first body's world center.
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.body1.getWorldCenter();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint.
	 * Returns the second body's world center.
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldCenter();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.linearImpulse.product(invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.angularImpulse * invdt;
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
	 * Returns the desired linear distance along the x and y coordinates from 
	 * body1's world center.
	 * <p>
	 * To get the world linear target:
	 * <pre>
	 * joint.getBody1().getWorldVector(joint.getLinearTarget());
	 * </pre>
	 * @return {@link Vector2}
	 */
	public Vector2 getLinearTarget() {
		return this.linearTarget;
	}
	
	/**
	 * Sets the desired linear distance along the x and y coordinates from 
	 * body1's world center.
	 * @param target the desired distance along the x and y coordinates
	 */
	public void setLinearTarget(Vector2 target) {
		if (!target.equals(this.linearTarget)) {
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			this.linearTarget = target;
		}
	}
	
	/**
	 * Returns the desired angle between the bodies.
	 * @return double
	 */
	public double getAngularTarget() {
		return this.angularTarget;
	}
	
	/**
	 * Sets the desired angle between the bodies.
	 * @param target the desired angle between the bodies
	 */
	public void setAngularTarget(double target) {
		if (target != this.angularTarget) {
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			this.angularTarget = target;
		}
	}
	
	/**
	 * Returns the correction factor.
	 * @return double
	 */
	public double getCorrectionFactor() {
		return this.correctionFactor;
	}
	
	/**
	 * Sets the correction factor.
	 * <p>
	 * The correction factor controls the rate at which the bodies perform the
	 * desired actions.  The default is 0.3.
	 * <p>
	 * A value of zero means that the bodies do not perform any action.
	 * @param correctionFactor the correction factor in the range [0, 1]
	 */
	public void setCorrectionFactor(double correctionFactor) {
		if (correctionFactor < 0.0 || correctionFactor > 1.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.motor.invalidCorrectionFactor"));
		this.correctionFactor = correctionFactor;
	}
	
	/**
	 * Returns the maximum torque this constraint will apply in newton-meters.
	 * @return double
	 */
	public double getMaximumTorque() {
		return this.maximumTorque;
	}
		
	/**
	 * Sets the maximum torque this constraint will apply in newton-meters.
	 * @param maximumTorque the maximum torque in newton-meters; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxTorque is less than zero
	 */
	public void setMaximumTorque(double maximumTorque) {
		// make sure its greater than or equal to zero
		if (maximumTorque < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.friction.invalidMaximumTorque"));
		// set the max
		this.maximumTorque = maximumTorque;
	}

	/**
	 * Returns the maximum force this constraint will apply in newtons.
	 * @return double
	 */
	public double getMaximumForce() {
		return this.maximumForce;
	}
	
	/**
	 * Sets the maximum force this constraint will apply in newtons.
	 * @param maximumForce the maximum force in newtons; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxForce is less than zero
	 */
	public void setMaximumForce(double maximumForce) {
		// make sure its greater than or equal to zero
		if (maximumForce < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.friction.invalidMaximumForce"));
		// set the max
		this.maximumForce = maximumForce;
	}
}
