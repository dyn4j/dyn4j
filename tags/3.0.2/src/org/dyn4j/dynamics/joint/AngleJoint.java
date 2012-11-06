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
package org.dyn4j.dynamics.joint;

import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.Step;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents a angle joint.
 * <p>
 * A angle joint constrains the relative rotation between [-&pi;, &pi;].
 * <p>
 * NOTE: The {@link #getAnchor1()} and {@link #getAnchor2()} methods return
 * null references since this joint does not require any anchor points at creation.
 * <p>
 * Defaults the min and max angles to the current angle (allowing no angular movement).
 * <p>
 * The joint limits must match the following restrictions:
 * <ul>
 * <li>lower limit &le; upper limit</li>
 * <li>lower limit &ge; -180</li>
 * <li>upper limit &le; 180</li>
 * </ul> 
 * To create a joint with limits other than this format use the {@link #setReferenceAngle(double)}
 * method.  For example:
 * <pre>
 * // we would like the joint limits to be [30, 260]
 * // this is the same as the limits [-60, 170] if the reference angle is 90
 * angleJoint.setLimits(Math.toRadians(-60), Math.toRadians(170));
 * angleJoint.setReferenceAngle(Math.toRadians(90));
 * </pre>
 * @author William Bittle
 * @version 3.0.2
 * @since 2.2.2
 */
public class AngleJoint extends Joint {
	/** The lower limit */
	protected double lowerLimit;
	
	/** The upper limit */
	protected double upperLimit;
	
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
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// initialize
		this.impulse = 0.0;
		// compute the reference angle
		this.referenceAngle = body1.getTransform().getRotation() - body2.getTransform().getRotation();
		// set both limits
		this.upperLimit = this.referenceAngle;
		this.lowerLimit = this.referenceAngle;
		// set enabled
		this.limitEnabled = true;
		// default the limit state
		this.limitState = Joint.LimitState.EQUAL;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AngleJoint[").append(super.toString())
		.append("|LowerLimit=").append(this.lowerLimit)
		.append("|UpperLimit=").append(this.upperLimit)
		.append("|IsLimitEnabled=").append(this.limitEnabled)
		.append("|ReferenceAngle=").append(this.referenceAngle)
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step)
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
		if (this.invK > Epsilon.E) {
			this.invK = 1.0 / this.invK;
		}
		
		// check if the limits are enabled
		if (this.limitEnabled) {
			// compute the current angle
			double angle = this.getRelativeRotation();
			
			// if they are enabled check if they are equal
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * angularTolerance) {
				// if so then set the state to equal
				this.limitState = Joint.LimitState.EQUAL;
			} else {
				// make sure we have valid settings
				if (this.upperLimit > this.lowerLimit) {
					// check against the max and min distances
					if (angle >= this.upperLimit) {
						// is the limit already at the upper limit
						if (this.limitState != Joint.LimitState.AT_UPPER) {
							this.impulse = 0;
						}
						// set the state to at upper
						this.limitState = Joint.LimitState.AT_UPPER;
					} else if (angle <= this.lowerLimit) {
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
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step)
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
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		// check if the constraint needs to be applied
		if (this.limitState != Joint.LimitState.INACTIVE) {
			Settings settings = Settings.getInstance();
			double angularTolerance = settings.getAngularTolerance();
			double maxAngularCorrection = settings.getMaximumAngularCorrection();
			
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// get the current angle between the bodies
			double angle = this.getRelativeRotation();
			double impulse = 0.0;
			double angularError = 0.0;
			// check the limit state
			if (this.limitState == Joint.LimitState.EQUAL) {
				// if the limits are equal then clamp the impulse to maintain
				// the constraint between the maximum
				double j = Interval.clamp(angle - this.lowerLimit, -maxAngularCorrection, maxAngularCorrection);
				impulse = -j * this.invK;
				angularError = Math.abs(j);
			} else if (this.limitState == Joint.LimitState.AT_LOWER) {
				// if the joint is at the lower limit then clamp only the lower value
				double j = angle - this.lowerLimit;
				angularError = -j;
				j = Interval.clamp(j + angularTolerance, -maxAngularCorrection, 0.0);
				impulse = -j * this.invK;
			} else if (this.limitState == Joint.LimitState.AT_UPPER) {
				// if the joint is at the upper limit then clamp only the upper value
				double j = angle - this.upperLimit;
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
	
	/**
	 * Returns the relative angle between the two bodies given the reference angle.
	 * @return double
	 */
	private double getRelativeRotation() {
		double rr = this.body1.getTransform().getRotation() - this.body2.getTransform().getRotation() - this.referenceAngle;
		if (rr < -Math.PI) rr += Geometry.TWO_PI;
		if (rr > Math.PI) rr -= Geometry.TWO_PI;
		return rr;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return new Vector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
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
	 * Returns true if the limit is enabled.
	 * @return boolean true if the limit is enabled
	 * @since 3.0.1
	 */
	public boolean isLimitEnabled() {
		return this.limitEnabled;
	}
	
	/**
	 * Returns the upper limit in radians.
	 * @return double
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/**
	 * Sets the upper limit in radians.
	 * @param upperLimit the upper limit in radians
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		// make sure the minimum is less than or equal to the maximum
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target angle
		this.upperLimit = upperLimit;
	}
	
	/**
	 * Returns the lower limit in radians.
	 * @return double
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/**
	 * Sets the lower limit in radians.
	 * @param lowerLimit the lower limit in radians
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		// make sure the minimum is less than or equal to the maximum
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target angle
		this.lowerLimit = lowerLimit;
	}
	
	/**
	 * Sets both the lower and upper limits.
	 * @param lowerLimit the lower limit in radians
	 * @param upperLimit the upper limit in radians
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		// make sure the min < max
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the limits
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
	}

	/**
	 * Sets both the lower and upper limits and enables them.
	 * @param lowerLimit the lower limit in radians
	 * @param upperLimit the upper limit in radians
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// set the values
		this.setLimits(lowerLimit, upperLimit);
		// enable the limits
		this.limitEnabled = true;
	}
	
	/**
	 * Sets both the lower and upper limits to the given limit.
	 * @param limit the desired limit
	 */
	public void setLimits(double limit) {
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the limits
		this.upperLimit = limit;
		this.lowerLimit = limit;
	}
	
	/**
	 * Sets both the lower and upper limits to the given limit and enables them.
	 * @param limit the desired limit
	 */
	public void setLimitsEnabled(double limit) {
		// set the values
		this.setLimits(limit);
		// enable the limits
		this.limitEnabled = true;
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
	 * @param angle the reference angle
	 * @see #getReferenceAngle()
	 * @since 3.0.1
	 */
	public void setReferenceAngle(double angle) {
		this.referenceAngle = angle;
	}
}