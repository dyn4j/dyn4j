/*
 * Copyright (c) 2010-2026 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of an angle joint.
 * <p>
 * A angle joint constrains the relative rotation of two bodies.  The bodies 
 * will continue to translate freely.
 * <p>
 * By default the lower and upper limits are set to the current angle between 
 * the bodies. The limits are enabled by default. When the limits are set to 
 * the same value, the bodies are locked together and cannot rotate relative ,
 * to one another. However, the {@link WeldJoint} will be a more stable option 
 * for locked rotation between bodies.
 * <p>
 * Both the lower and upper limits can be set to any value (in radians). Angle 
 * zero is the vector from the anchor point to body 1. There are a few ways 
 * to set the limits. The easiest way is to make sure the lower limit is less 
 * than or equal to the upper limit. For example:
 * <pre>
 * [ -20,   20]
 * [-130,    0]
 * [   0,   40]
 * [  50,  160]
 * </pre>
 * If you don't like where the zero-angle is, you can use the 
 * {@link #setLimitsReferenceAngle(double)} method to set the zero-angle 
 * offset. For example:
 * <pre>
 * [ -20,   20] with reference angle =  50 yields [  30,  70] 
 * [-130,    0] with reference angle = -50 yields [-180, -50]
 * [   0,   40] with reference angle =  50 yields [  50,  90]
 * [  50,  160] with reference angle = -50 yields [   0, 110]
 * </pre>
 * Another way is to reverse the lower and upper limits. This has the effect
 * of setting the reference angle = &pi; and switching the lower and upper
 * limits. The signs of the lower and upper limits must be different and the 
 * lower limit must be larger than the upper limit. For example:
 * <pre>
 * [  20,  -20] translates to [-160,  160] 
 * [ 130,    0] translates to [ 180,  -50]
 * [   0,  -40] translates to [ 140, -180]
 * [  50, -160] translates to [  20, -130]
 * </pre>
 * Another way is to have the lower and upper limits cross over each other.
 * This means the blocked zone is where the two sweeps intersect. The lower 
 * and upper limits must be the same sign and the lower limit must be larger 
 * than the upper limit. For example:
 * <pre>
 * [  -20, -160] where the blocked zone is from [-160, -20]
 * [  130,   50] where the blocked zone is from [  50, 130]
 * [  -20,  -40] where the blocked zone is from [ -40, -20]
 * [   50,   10] where the blocked zone is from [  10,  50]
 * </pre>
 * Finally, you can also provide the lower and upper limits larger than and 
 * smaller than &pi;. They will be normalized into the [-&pi;, &pi;] range 
 * and then matched to one of the patterns above.
 * <pre>
 * [  20,  225] translates to [  20, -135] and matches the reversed pattern 
 * [ 225,  340] translates to [-135,  -20] and matches the crossed pattern
 * [-225,   30] translates to [ 135,   30] and matches the crossed pattern
 * [-225, -160] translates to [ 135, -160] and matches the reversed pattern
 * </pre>
 * When the limits are close together, where the larger arc represents the 
 * free zone, the bodies can over step the limits and jump to the other side. 
 * This is due to the solver running at a fixed rate and allowing rotation 
 * beyond the limit and correcting it on the next solve. To avoid this 
 * situation, make sure the limits are at least 15 degrees apart.
 * <p>
 * The angle joint also allows a ratio value that allow the bodies to rotate at
 * a specified value relative to the other.  This can be used to simulate 
 * gears.
 * <p>
 * Since the AngleJoint class defaults the upper and lower limits to the same 
 * value and by default the limits are enabled, you will need to disable the 
 * limit to see the effect of the ratio.
 * @author William Bittle
 * @version 6.0.0
 * @since 2.2.2
 * @see <a href="https://www.dyn4j.org/pages/joints#Angle_Joint" target="_blank">Documentation</a>
 * @see <a href="https://www.dyn4j.org/2010/12/angle-constraint/" target="_blank">Angle Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class AngleJoint<T extends PhysicsBody> extends AbstractPairedBodyJoint<T> implements AngularLimitsJoint, PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	// gear constraint
	
	/** The angular velocity ratio */
	protected double ratio;
	
	// limits
	
	/** Whether the {@link Joint} limits are enabled or not */
	protected boolean limitsEnabled;
	
	/** The lower limit of the {@link Joint} in radians */
	protected double lowerLimit;
	
	/** The upper limit of the {@link Joint} in radians */
	protected double upperLimit;

	/** The initial rotation between the two {@link PhysicsBody}s in radians */
	protected double referenceAngle;
	
	/** The limit adjusted rotation offset */
	double limitOffset;
	
	/** The adjusted lower limit */
	double adjustedLowerLimit;
	
	/** The adjusted upper limit */
	double adjustedUpperLimit;
	
	// current state
	
	/** The current angle between the bodies */
	double angle;
	
	/** The angular mass about the pivot point */
	double axialMass;
	
	/** True if the axial mass was close or equal to zero */
	boolean fixedRotation;
	
	// output
	
	/** The impulse applied to reduce angular motion */
	double impulse;
	
	/** The impulse applied by the lower limit */
	double lowerLimitImpulse;
	
	/** The impulse applied by the upper limit */
	double upperLimitImpulse;

	/**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @throws NullPointerException if body1 or body2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public AngleJoint(T body1, T body2) {
		// default no collision allowed
		super(body1, body2);
		// initialize
		this.ratio = 1.0;
		// get the initial reference angle for the joint limits
		this.referenceAngle = body2.getTransform().getRotationAngle() - body1.getTransform().getRotationAngle();
		
		// default limits
		this.lowerLimit = this.upperLimit = Rotation.getNormalizedAngle(this.referenceAngle);
		this.limitsEnabled = true;
		this.calculateLimitOffsetAndAdjustedLimits();
		
		this.angle = 0.0;
		this.axialMass = 0.0;
		this.fixedRotation = false;
		
		this.impulse = 0.0;
		this.lowerLimitImpulse = 0.0;
		this.upperLimitImpulse = 0.0;
	}
	
	/**
	 * Copy constructor.
	 * @param joint the joint to copy
	 * @since 6.0.0
	 */
	protected AngleJoint(AngleJoint<T> joint) {
		this(joint, null, null);
	}
	
	/**
	 * Copy constructor.
	 * @param joint the joint to copy
	 * @param body1 the first body
	 * @param body2 the second body
	 * @since 6.0.0
	 */
	protected AngleJoint(AngleJoint<T> joint, T body1, T body2) {
		super(joint, body1, body2);

		this.ratio = joint.ratio;
		this.referenceAngle = joint.referenceAngle;
		
		// limits
		this.limitsEnabled = joint.limitsEnabled;
		this.lowerLimit = joint.lowerLimit;
		this.upperLimit = joint.upperLimit;
		this.referenceAngle = joint.referenceAngle;
		this.limitOffset = joint.limitOffset;
		this.adjustedLowerLimit = joint.adjustedLowerLimit;
		this.adjustedUpperLimit = joint.adjustedUpperLimit;
		
		this.angle = joint.angle;
		this.axialMass = joint.axialMass;
		this.fixedRotation = joint.fixedRotation;
		
		this.impulse = joint.impulse;
		this.lowerLimitImpulse = joint.lowerLimitImpulse;
		this.upperLimitImpulse = joint.upperLimitImpulse;
	}
	
	/**
	 * {@inheritDoc}
	 * @return {@link AngleJoint}
	 * @see #copy(PhysicsBody, PhysicsBody)
	 * @since 6.0.0
	 */
	@Override
	public AngleJoint<T> copy() {
		return new AngleJoint<T>(this);
	}
	
	/**
	 * {@inheritDoc}
	 * @return {@link AngleJoint}
	 * @since 6.0.0
	 */
	@Override
	public AngleJoint<T> copy(T body1, T body2) {
		return new AngleJoint<T>(this, body1, body2);
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
		  .append("|IsLimitEnabled=").append(this.limitsEnabled)
		  .append("|ReferenceAngle=").append(this.referenceAngle)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		this.axialMass = invI1 + Math.abs(this.ratio) * invI2;
		if (this.axialMass > Epsilon.E) {
			this.axialMass = 1.0 / this.axialMass;
		} else {
			this.axialMass = 0.0;
			this.fixedRotation = true;
		}
		
		// compute the current angle
		this.angle = this.getRelativeRotation();
		
		// handle no limits (or if the two bodies have fixed rotation)
		if (!this.limitsEnabled || this.fixedRotation) {
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
		
		// handle the ratio changing or limits be activated
		if (this.limitsEnabled || this.ratio == 1.0) {
			this.impulse = 0.0;
		}
		
		if (settings.isWarmStartingEnabled()) {
			// account for variable time step
			double dtr = step.getDeltaTimeRatio();

			// account for variable time step
			this.impulse *= dtr;
			this.lowerLimitImpulse *= dtr;
			this.upperLimitImpulse *= dtr;
			
			double axialImpulse = this.impulse * Math.signum(this.ratio) + this.lowerLimitImpulse - this.upperLimitImpulse;
			
			// warm start
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * axialImpulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * axialImpulse);
		} else {
			this.impulse = 0.0;
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
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// check if the limit constraint is enabled
		if (this.limitsEnabled && !this.fixedRotation) {
			double lowerLimit = this.adjustedLowerLimit;
			double upperLimit = this.adjustedUpperLimit;

			// lower limit
			{
				double C = this.angle - lowerLimit;
				double Cdot = this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
				double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				
				// clamp
				double currentAccumulatedImpulse = this.lowerLimitImpulse;
				this.lowerLimitImpulse = Math.max(this.lowerLimitImpulse + stepImpulse, 0.0);
				stepImpulse = this.lowerLimitImpulse - currentAccumulatedImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * stepImpulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * stepImpulse);
			}
			
			// upper limit
			{
				double C = upperLimit - this.angle;
				double Cdot = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
				double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				
				// clamp
				double currentAccumulatedImpulse = this.upperLimitImpulse;
				this.upperLimitImpulse = Math.max(this.upperLimitImpulse + stepImpulse, 0.0);
				stepImpulse = this.upperLimitImpulse - currentAccumulatedImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * stepImpulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * stepImpulse);
			}
		}

		// apply the ratio
		if (!this.limitsEnabled) {
			// the limit is inactive and the ratio is not one
			// get the relative velocity
			double C = this.body2.getAngularVelocity() - this.ratio * this.body1.getAngularVelocity();
			
			// get the impulse required to obtain the speed
			double stepImpulse = this.axialMass * -C;
			this.impulse += stepImpulse;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * stepImpulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * stepImpulse * Math.signum(this.ratio));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		// solve position constraint for limits
		if (this.limitsEnabled && !this.fixedRotation) {
			double angularTolerance = settings.getAngularTolerance();
			double maxAngularCorrection = settings.getMaximumAngularCorrection();
			
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			double angularError = 0.0;
			
			double angle = this.getRelativeRotation();
			double lowerLimit = this.adjustedLowerLimit;
			double upperLimit = this.adjustedUpperLimit;
			double C = 0.0;
			
			if (Math.abs(upperLimit - lowerLimit) < 2.0 * angularTolerance) {
				C = Interval.clamp(angle - lowerLimit, -maxAngularCorrection, maxAngularCorrection);
			} else if (angle <= lowerLimit) {
				C = Interval.clamp(angle - lowerLimit + angularTolerance, -maxAngularCorrection, 0.0);
			} else if (angle >= upperLimit) {
				C = Interval.clamp(angle - upperLimit - angularTolerance, 0.0, maxAngularCorrection);
			}
			
			double impulse = -this.axialMass * C;
			this.body1.rotateAboutCenter(-invI1 * impulse);
			this.body2.rotateAboutCenter(invI2 * impulse);
			angularError = Math.abs(C);
			
			return angularError <= angularTolerance;
		}
		
		return true;
	}
	
	/**
	 * Computes the limit offset and adjusted limits based on the limits 
	 * provided by the user.
	 */
	private final void calculateLimitOffsetAndAdjustedLimits() {
		// make sure the limits are in the range [-pi, pi]
		double lowerLimit = Rotation.getNormalizedAngle(this.lowerLimit);
		double upperLimit = Rotation.getNormalizedAngle(this.upperLimit);

		// determine the type of limits we have
		// - NORMAL
		// - REVERSED
		// - CROSSED
		boolean sameSign = (lowerLimit > 0.0) == (upperLimit > 0.0);
		boolean lowerGreaterThanUpper = lowerLimit > upperLimit;
		
		// pre-compute some offset values
		double limitAverage = (lowerLimit + upperLimit) * 0.5;
		double limitDifference = (lowerLimit - upperLimit);

		// calculate adjustments for special cases
		// this is something like [-20, 50] or [20, 50] or [-50, -20]
		if (sameSign && lowerGreaterThanUpper) {
			// the angles are CROSSED
			// this is something like [50, 20] or [-20, -50]
			if (lowerLimit > 0.0) {
				limitDifference *= -1.0;
			}
			// the limit average gets the limits center at zero
			// then we need to further offset by the difference in
			// the angles so that the start angle is correct
			this.limitOffset = limitAverage + limitDifference;
			this.adjustedLowerLimit = lowerLimit - limitAverage - Math.PI;
			this.adjustedUpperLimit = upperLimit - limitAverage + Math.PI;
		} else if (lowerGreaterThanUpper) {
			// the angles are REVERSED
			// this is something like [20, -20]
			this.limitOffset = Math.PI;
			this.adjustedLowerLimit = upperLimit;
			this.adjustedUpperLimit = lowerLimit;
		} else {
			// the angles are NORMAL
			// just leave everything as-is
			this.limitOffset = 0.0;
			this.adjustedLowerLimit = lowerLimit;
			this.adjustedUpperLimit = upperLimit;
		}
	}
	
	/**
	 * Returns the relative angle between the two bodies given the reference angle.
	 * @return double
	 */
	private final double getRelativeRotation() {
		double a1 = this.body1.getTransform().getRotationAngle();
		double a2 = this.body2.getTransform().getRotationAngle();
		return Rotation.getNormalizedAngle(a2 - a1 - this.referenceAngle - this.limitOffset);
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
		return (this.impulse + this.lowerLimitImpulse - this.upperLimit) * invdt;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// nothing to do here since there are no anchor points
	}

	/**
	 * Returns the relative angle between the two {@link PhysicsBody}s in radians in the range [-&pi;, &pi;].
	 * @return double
	 * @since 3.1.0
	 * @deprecated Deprecated in 6.0.0. Use {@link #getAngularTranslation()} instead.
	 */
	@Deprecated
	public double getJointAngle() {
		return this.getAngularTranslation();
	}
	
	/**
	 * Returns the relative angle between the two {@link PhysicsBody}s in radians in the range [-&pi;, &pi;].
	 * @return double
	 * @since 6.0.0
	 */
	public double getAngularTranslation() {
		double a1 = this.body1.getTransform().getRotationAngle();
		double a2 = this.body2.getTransform().getRotationAngle();
		return Rotation.getNormalizedAngle(a2 - a1 - this.referenceAngle);
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
	 * A value of 0.5 means that body1 will rotate 2 times while body2 rotates once.  A value of
	 * 2 means that body2 will rotate 2 times while body1 rotates once.
	 * <p>
	 * A value of 1.0 means that the body rotate at the same rate.
	 * <p>
	 * A negative ratio indicates that the anglular velocities of the joined bodies should be in
	 * opposite directions.
	 * <p>
	 * A ratio of zero is not supported.
	 * @param ratio the ratio; anything, just not zero
	 * @since 3.1.0
	 * @throws IllegalArgumentException if ratio is equal to zero
	 */
	public void setRatio(double ratio) {
		if (ratio == 0.0) 
			throw new ValueOutOfRangeException("ratio", 0.0);
		
		this.ratio = ratio;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#isLimitsEnabled()
	 */
	@Override
	public boolean isLimitsEnabled() {
		return this.limitsEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(boolean)
	 */
	@Override
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
	@Override
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setUpperLimit(double)
	 */
	@Override
	public void setUpperLimit(double upperLimit) {
		if (this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.upperLimit = upperLimit;
			this.calculateLimitOffsetAndAdjustedLimits();
			// clear accumulated impulse
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getLowerLimit()
	 */
	@Override
	public double getLowerLimit() {
		return this.lowerLimit;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLowerLimit(double)
	 */
	@Override
	public void setLowerLimit(double lowerLimit) {
		if (this.lowerLimit != lowerLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.lowerLimit = lowerLimit;
			this.calculateLimitOffsetAndAdjustedLimits();
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double, double)
	 */
	@Override
	public void setLimits(double lowerLimit, double upperLimit) {
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
			this.calculateLimitOffsetAndAdjustedLimits();
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double, double)
	 */
	@Override
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double)
	 */
	@Override
	public void setLimitsEnabled(double limit) {
		this.setLimitsEnabled(limit, limit);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double)
	 */
	@Override
	public void setLimits(double limit) {
		if (this.lowerLimit != limit || this.upperLimit != limit) {
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.lowerLimit = limit;
			this.upperLimit = limit;
			this.calculateLimitOffsetAndAdjustedLimits();
			
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
