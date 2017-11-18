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
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of an angle joint.
 * <p>
 * A angle joint constrains the relative rotation of two bodies.  The bodies 
 * will continue to translate freely.
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
 * The angle joint also allows a ratio value that allow the bodies to rotate at
 * a specified value relative to the other.  This can be used to simulate gears.
 * <p>
 * Since the AngleJoint class defaults the upper and lower limits to the same 
 * value and by default the limits are enabled, you will need to modify the 
 * limits, or disable the limit to see the effect of the ratio.
 * <p>
 * When the angle between the bodies reaches a limit, and limits are enabled, 
 * the ratio will be turned off.
 * <p>
 * NOTE: The {@link #getAnchor1()} and {@link #getAnchor2()} methods return
 * the world space center points for the joined bodies.  This constraint 
 * doesn't need anchor points.
 * @author William Bittle
 * @version 3.2.1
 * @since 2.2.2
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Angle_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/12/angle-constraint/" target="_blank">Angle Constraint</a>
 */
public class AngleJoint extends Joint implements Shiftable, DataContainer {
	/** The angular velocity ratio */
	protected double ratio;
	
	/** The lower limit */
	protected double lowerLimit;
	
	/** The upper limit */
	protected double upperLimit;
	
	/** Whether the limits are enabled */
	protected boolean limitEnabled;
	
	/** The initial angle between the two bodies */
	protected double referenceAngle;
	
	// current state
	
	/** The current state of the joint limits */
	private LimitState limitState;
	
	/** The inverse effective mass */
	private double invK;

	// output
	
	/** The impulse applied to reduce angular motion */
	private double impulse;

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
		this.ratio = 1.0;
		this.impulse = 0.0;
		// compute the reference angle
		this.referenceAngle = body1.getTransform().getRotation() - body2.getTransform().getRotation();
		// set both limits
		this.upperLimit = this.referenceAngle;
		this.lowerLimit = this.referenceAngle;
		// set enabled
		this.limitEnabled = true;
		// default the limit state
		this.limitState = LimitState.EQUAL;
		
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AngleJoint[").append(super.toString())
		  .append("|Ratio=").append(this.ratio)
		  .append("|LowerLimit=").append(this.lowerLimit)
		  .append("|UpperLimit=").append(this.upperLimit)
		  .append("|IsLimitEnabled=").append(this.limitEnabled)
		  .append("|ReferenceAngle=").append(this.referenceAngle)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
		double angularTolerance = settings.getAngularTolerance();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// check if the limits are enabled
		if (this.limitEnabled) {
			// compute the current angle
			double angle = this.getRelativeRotation();
			
			// if they are enabled check if they are equal
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * angularTolerance) {
				// if so then set the state to equal
				this.limitState = LimitState.EQUAL;
			} else {
				// make sure we have valid settings
				if (this.upperLimit > this.lowerLimit) {
					// check against the max and min distances
					if (angle >= this.upperLimit) {
						// is the limit already at the upper limit
						if (this.limitState != LimitState.AT_UPPER) {
							this.impulse = 0;
						}
						// set the state to at upper
						this.limitState = LimitState.AT_UPPER;
					} else if (angle <= this.lowerLimit) {
						// is the limit already at the lower limit
						if (this.limitState != LimitState.AT_LOWER) {
							this.impulse = 0;
						}
						// set the state to at lower
						this.limitState = LimitState.AT_LOWER;
					} else {
						// set the state to inactive
						this.limitState = LimitState.INACTIVE;
						this.impulse = 0;
					}
				}
			}
		} else {
			// neither is enabled so no constraint needed at this time
			this.limitState = LimitState.INACTIVE;
			this.impulse = 0;
		}
		
		// compute the mass
		if (this.limitState == LimitState.INACTIVE) {
			// compute the angular mass including the ratio
			this.invK = invI1 + this.ratio * this.ratio * invI2;
		} else {
			// compute the angular mass normally
			this.invK = invI1 + invI2;
		}
		
		if (this.invK > Epsilon.E) {
			this.invK = 1.0 / this.invK;
		}
		
		// account for variable time step
		this.impulse *= step.getDeltaTimeRatio();
		
		// warm start
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * this.impulse);
		// we only want to apply the ratio to the impulse if the limits are not active.  When the
		// limits are active we effectively disable the ratio
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * this.impulse * (this.limitState == LimitState.INACTIVE ? this.ratio : 1.0));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// check if the limit needs to be applied (if we are at one of the limits
		// then we ignore the ratio)
		if (this.limitState != LimitState.INACTIVE) {
			// solve the angular constraint
			// get the relative velocity
			double C = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
			// get the impulse required to obtain the speed
			double impulse = this.invK * -C;
			
			if (this.limitState == LimitState.EQUAL) {
				this.impulse += impulse;
			}else if (this.limitState == LimitState.AT_LOWER) {
				double newImpulse = this.impulse + impulse;
				if (newImpulse < 0.0) {
					impulse = -this.impulse;
					this.impulse = 0.0;
				}
			} else if (this.limitState == LimitState.AT_UPPER) {
				double newImpulse = this.impulse + impulse;
				if (newImpulse > 0.0) {
					impulse = -this.impulse;
					this.impulse = 0.0;
				}
			}
		
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
		} else if (this.ratio != 1.0) {
			// the limit is inactive and the ratio is not one
			// get the relative velocity
			double C = this.body1.getAngularVelocity() - this.ratio * this.body2.getAngularVelocity();
			// get the impulse required to obtain the speed
			double impulse = this.invK * -C;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse * this.ratio);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		// check if the constraint needs to be applied
		if (this.limitState != LimitState.INACTIVE) {
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
			if (this.limitState == LimitState.EQUAL) {
				// if the limits are equal then clamp the impulse to maintain
				// the constraint between the maximum
				double j = Interval.clamp(angle - this.lowerLimit, -maxAngularCorrection, maxAngularCorrection);
				impulse = -j * this.invK;
				angularError = Math.abs(j);
			} else if (this.limitState == LimitState.AT_LOWER) {
				// if the joint is at the lower limit then clamp only the lower value
				double j = angle - this.lowerLimit;
				angularError = -j;
				j = Interval.clamp(j + angularTolerance, -maxAngularCorrection, 0.0);
				impulse = -j * this.invK;
			} else if (this.limitState == LimitState.AT_UPPER) {
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
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint. 
	 * This method returns the first body's world center.
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.body1.getWorldCenter();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint.
	 * This method returns the second body's world center.
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldCenter();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint. Returns a new zero {@link Vector2}.
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// nothing to do here since there are no anchor points
	}

	/**
	 * Returns the relative angle between the two {@link Body}s in radians in the range [-&pi;, &pi;].
	 * @return double
	 * @since 3.1.0
	 */
	public double getJointAngle() {
		return this.getRelativeRotation();
	}
	
	/**
	 * Returns the angular velocity ratio between the two bodies.
	 * @return double
	 * @since 3.1.0
	 */
	public double getRatio() {
		return this.ratio;
	}
	
	/**
	 * Sets the angular velocity ratio between the two bodies.
	 * <p>
	 * To disable the ratio and fix their velocities set the ratio to 1.0.
	 * <p>
	 * The ratio can be negative to reverse the direction of the velocity
	 * of the other body.
	 * @param ratio the ratio
	 * @since 3.1.0
	 */
	public void setRatio(double ratio) {
		this.ratio = ratio;
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
	 * <p>
	 * See the class documentation for more details on the limit ranges.
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
	 * <p>
	 * See the class documentation for more details on the limit ranges.
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
	 * <p>
	 * See the class documentation for more details on the limit ranges.
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
	 * <p>
	 * See the class documentation for more details on the limit ranges.
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
	 * <p>
	 * See the class documentation for more details on the limit ranges.
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
	 * <p>
	 * See the class documentation for more details on the limit ranges.
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
	 * <p>
	 * See the class documentation for more details.
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
	 */
	public LimitState getLimitState() {
		return this.limitState;
	}
}
