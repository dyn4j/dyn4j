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
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a angle joint.
 * <p>
 * A angle joint constrains the relative rotation.
 * <p>
 * NOTE: The {@link #getAnchor1()} and {@link #getAnchor2()} methods return
 * null references since this joint does not require any anchor points at creation.
 * <p>
 * Like the {@link RevoluteJoint}, the limits that an angle joint places on the bodies
 * are world space limits not relative angle limits (although the limits are relative to 
 * the initial angle of the bodies given at joint creation time).  Therefore its recommended 
 * to only use the limits when one body is fixed.
 * <p>
 * Defaults the min and max angles to the current angle (allowing no angular movement).
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.2
 */
public class AngleJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Angle");
	
	/** The minimum angle */
	protected double minimumAngle;
	
	/** The maximum angle */
	protected double maximumAngle;
	
	/** Whether the limits are enabled */
	protected boolean limitEnabled;
	
	/** The initial angle between the two bodies */
	protected double referenceAngle;
	
	/** The inverse effective mass */
	protected double invK;
	
	/** The current state of the joint limits */
	protected Joint.LimitState limitState;
	
	/** The impulse applied to reduce angular motion */
	protected double impulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @throws NullPointerException if body1 or body2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public AngleJoint(Body body1, Body body2) {
		// default no collision allowed
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException("Cannot create a angle joint between the same body instance.");
		// initialize
		this.impulse = 0.0;
		// compute the reference angle
		this.referenceAngle = body1.getTransform().getRotation() - body2.getTransform().getRotation();
		// set both limits
		this.maximumAngle = this.referenceAngle;
		this.minimumAngle = this.referenceAngle;
		// set enabled
		this.limitEnabled = true;
		// default the limit state
		this.limitState = Joint.LimitState.EQUAL;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ANGLE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.minimumAngle).append("|")
		.append(this.maximumAngle).append("|")
		.append(this.limitEnabled).append("|")
		.append(this.referenceAngle).append("|")
		.append(this.impulse).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
		double angularTolerance = Settings.getInstance().getAngularTolerance();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the angular mass
		this.invK = invI1 + invI2;
		if (this.invK >= Epsilon.E) {
			this.invK = 1.0 / this.invK;
		}
		
		// compute the current angle
		double angle = this.body1.getTransform().getRotation() - this.body2.getTransform().getRotation() - this.referenceAngle;
		
		// check if the limits are enabled
		if (this.limitEnabled) {
			// if they are enabled check if they are equal
			if (Math.abs(this.maximumAngle - this.minimumAngle) < 2.0 * angularTolerance) {
				// if so then set the state to equal
				this.limitState = Joint.LimitState.EQUAL;
			} else {
				// make sure we have valid settings
				if (this.maximumAngle > this.minimumAngle) {
					// check against the max and min distances
					if (angle >= this.maximumAngle) {
						// is the limit already at the upper limit
						if (this.limitState != Joint.LimitState.AT_UPPER) {
							this.impulse = 0;
						}
						// set the state to at upper
						this.limitState = Joint.LimitState.AT_UPPER;
					} else if (angle <= this.minimumAngle) {
						// is the limit already at the lower limit
						if (this.limitState != Joint.LimitState.AT_LOWER) {
							this.impulse = 0;
						}
						// set the state to at lower
						this.limitState = Joint.LimitState.AT_LOWER;
					} else {
						// set the state to inactive
						this.limitState = Joint.LimitState.INACTIVE;
						this.impulse = 0;
					}
				}
			}
		} else {
			// neither is enabled so no constraint needed at this time
			this.limitState = Joint.LimitState.INACTIVE;
			this.impulse = 0;
		}
		
		// account for variable time step
		this.impulse *= step.getDeltaTimeRatio();
		
		// warm start
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * this.impulse);
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * this.impulse);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		// check if the constraint needs to be applied
		if (this.limitState != Joint.LimitState.INACTIVE) {
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// solve the angular constraint
			// get the relative velocity - the target motor speed
			double C = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
			// get the impulse required to obtain the speed
			double impulse = this.invK * -C;
			
			if (this.limitState == Joint.LimitState.EQUAL) {
				this.impulse += impulse;
			}else if (this.limitState == Joint.LimitState.AT_LOWER) {
				double newImpulse = this.impulse + impulse;
				if (newImpulse < 0.0) {
					impulse = -this.impulse;
					this.impulse = 0.0;
				}
			} else if (this.limitState == Joint.LimitState.AT_UPPER) {
				double newImpulse = this.impulse + impulse;
				if (newImpulse > 0.0) {
					impulse = -this.impulse;
					this.impulse = 0.0;
				}
			}
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		// check if the constraint needs to be applied
		if (this.limitState != Joint.LimitState.INACTIVE) {
			Settings settings = Settings.getInstance();
			double angularTolerance = settings.getAngularTolerance();
			double maxAngularCorrection = settings.getMaxAngularCorrection();
			
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// get the current angle between the bodies
			double angle = this.body1.getTransform().getRotation() - this.body2.getTransform().getRotation() - this.referenceAngle;
			double impulse = 0.0;
			double angularError = 0.0;
			// check the limit state
			if (this.limitState == Joint.LimitState.EQUAL) {
				// if the limits are equal then clamp the impulse to maintain
				// the constraint between the maximum
				double j = Interval.clamp(angle - this.minimumAngle, -maxAngularCorrection, maxAngularCorrection);
				impulse = -j * this.invK;
				angularError = Math.abs(j);
			} else if (this.limitState == Joint.LimitState.AT_LOWER) {
				// if the joint is at the lower limit then clamp only the lower value
				double j = angle - this.minimumAngle;
				angularError = -j;
				j = Interval.clamp(j + angularTolerance, -maxAngularCorrection, 0.0);
				impulse = -j * this.invK;
			} else if (this.limitState == Joint.LimitState.AT_UPPER) {
				// if the joint is at the upper limit then clamp only the upper value
				double j = angle - this.maximumAngle;
				angularError = j;
				j = Interval.clamp(j - angularTolerance, 0.0, maxAngularCorrection);
				impulse = -j * this.invK;
			}
			
			// apply the corrective impulses to the bodies
			this.body1.rotateAboutCenter(invI1 * impulse);
			this.body2.rotateAboutCenter(-invI2 * impulse);
			
			return angularError <= angularTolerance;
		} else {
			return true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getType()
	 */
	@Override
	public Type getType() {
		return AngleJoint.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return new Vector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.impulse * invdt;
	}

	/**
	 * Sets whether the angle limits are enabled.
	 * @param flag true if the angle limits should be enforced
	 */
	public void setLimitEnabled(boolean flag) {
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the flag
		this.limitEnabled = flag;
	}
	
	/**
	 * Returns the maximum angle between the two constrained {@link Body}s in radians.
	 * @return double
	 */
	public double getMaximumAngle() {
		return this.maximumAngle;
	}
	
	/**
	 * Sets the maximum angle between the two constrained {@link Body}s in radians.
	 * @param maximumAngle the maximum angle in radians
	 * @throws IllegalArgumentException if maximumAngle is less than the current minimum
	 */
	public void setMaximumAngle(double maximumAngle) {
		// make sure the minimum is less than or equal to the maximum
		if (maximumAngle < this.minimumAngle) throw new IllegalArgumentException("The maximum angle must be greater than or equal to the current minimum angle.");
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target angle
		this.maximumAngle = maximumAngle;
	}
	
	/**
	 * Returns the minimum angle between the two constrained {@link Body}s in radians.
	 * @return double
	 */
	public double getMinimumAngle() {
		return this.minimumAngle;
	}
	
	/**
	 * Sets the minimum angle between the two constrained {@link Body}s in radians.
	 * @param minimumAngle the minimum angle in radians
	 * @throws IllegalArgumentException if minimumAngle is greater than the current maximum
	 */
	public void setMinimumAngle(double minimumAngle) {
		// make sure the minimum is less than or equal to the maximum
		if (minimumAngle > this.maximumAngle) throw new IllegalArgumentException("The minimum angle must be less than or equal to the current maximum angle.");
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target angle
		this.minimumAngle = minimumAngle;
	}
	
	/**
	 * Sets both the maximum and minimum limit angles.
	 * @param minimumAngle the minimum angle in radians
	 * @param maximumAngle the maximum angle in radians
	 * @throws IllegalArgumentException if minimumAngle is greater than maximumAngle
	 */
	public void setMinimumMaximum(double minimumAngle, double maximumAngle) {
		// make sure the min < max
		if (minimumAngle > maximumAngle) throw new IllegalArgumentException("The minimum angle must be smaller than the maximum angle.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the limits
		this.maximumAngle = maximumAngle;
		this.minimumAngle = minimumAngle;
	}

	/**
	 * Sets both the maximum and minimum limit angles and enables the limits.
	 * @param minimumAngle the minimum angle in radians
	 * @param maximumAngle the maximum angle in radians
	 * @throws IllegalArgumentException if minimumAngle is greater than maximumAngle
	 */
	public void setMinimumMaximumEnabled(double minimumAngle, double maximumAngle) {
		// set the values
		this.setMinimumMaximum(minimumAngle, maximumAngle);
		// enable the limits
		this.limitEnabled = true;
	}
	
	/**
	 * Sets both the maximum and minimum limit angles to the given angle.
	 * @param angle the desired angle between the bodies
	 */
	public void setMinimumMaximum(double angle) {
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the limits
		this.maximumAngle = angle;
		this.minimumAngle = angle;
	}
	
	/**
	 * Sets both the maximum and minimum limit angles to the given angle and
	 * enables the limits.
	 * @param angle the desired angle between the bodies
	 */
	public void setMinimumMaximumEnabled(double angle) {
		// set the values
		this.setMinimumMaximum(angle);
		// enable the limits
		this.limitEnabled = true;
	}
}
