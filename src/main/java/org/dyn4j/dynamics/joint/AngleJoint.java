/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
 * default the limits are enabled.
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
 * The angle joint also allows a ratio value that allow the bodies to rotate at
 * a specified value relative to the other.  This can be used to simulate 
 * gears.
 * <p>
 * Since the AngleJoint class defaults the upper and lower limits to the same 
 * value and by default the limits are enabled, you will need to disable the 
 * limit to see the effect of the ratio.
 * @author William Bittle
 * @version 5.0.0
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
	
	/** The lower limit */
	protected double lowerLimit;
	
	/** The upper limit */
	protected double upperLimit;
	
	/** Whether the limits are enabled */
	protected boolean limitsEnabled;
	
	/** The initial angle between the two bodies */
	protected double referenceAngle;
	
	// current state
	
	/** The current angle between the bodies */
	private double angle;
		
	/** The angular mass about the pivot point */
	private double axialMass;
	
	/** True if the axial mass was close or equal to zero */
	private boolean fixedRotation;
	
	// output
	
	/** The impulse applied to reduce angular motion */
	private double impulse;
	
	/** The impulse applied by the lower limit */
	private double lowerImpulse;
	
	/** The impulse applied by the upper limit */
	private double upperImpulse;

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
		this.impulse = 0.0;
		// compute the reference angle
		this.referenceAngle = body1.getTransform().getRotationAngle() - body2.getTransform().getRotationAngle();
		// set both limits
		this.upperLimit = this.referenceAngle;
		this.lowerLimit = this.referenceAngle;
		// set enabled
		this.limitsEnabled = true;
		
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
			this.fixedRotation = true;
		}
		
		// compute the current angle
		this.angle = this.getRelativeRotation();
		
		// handle no limits (or if the two bodies have fixed rotation)
		if (!this.limitsEnabled || this.fixedRotation) {
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
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
			this.lowerImpulse *= dtr;
			this.upperImpulse *= dtr;
			
			double axialImpulse = this.impulse * Math.signum(this.ratio) + this.lowerImpulse - this.upperImpulse;
			
			// warm start
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * axialImpulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * axialImpulse);
		} else {
			this.impulse = 0.0;
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
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// check if the limit constraint is enabled
		if (this.limitsEnabled && !this.fixedRotation) {
			// lower limit
			{
				double C = this.angle - this.lowerLimit;
				double Cdot = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
				double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				
				double currentAccumulatedImpulse = this.lowerImpulse;
				this.lowerImpulse = Math.max(this.lowerImpulse + stepImpulse, 0.0);
				stepImpulse = this.lowerImpulse - currentAccumulatedImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * stepImpulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * stepImpulse);
			}
			
			// upper limit
			{
				double C = this.upperLimit - this.angle;
				double Cdot = this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
				double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				
				double currentAccumulatedImpulse = this.upperImpulse;
				this.upperImpulse = Math.max(this.upperImpulse + stepImpulse, 0.0);
				stepImpulse = this.upperImpulse - currentAccumulatedImpulse;
				
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * stepImpulse);
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * stepImpulse);
			}
		}

		// apply the ratio
		if (!this.limitsEnabled) {
			// the limit is inactive and the ratio is not one
			// get the relative velocity
			double C = this.body1.getAngularVelocity() - this.ratio * this.body2.getAngularVelocity();
			
			// get the impulse required to obtain the speed
			double stepImpulse = this.axialMass * -C;
			this.impulse += stepImpulse;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * stepImpulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * stepImpulse * Math.signum(this.ratio));
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
			
			// get the current angle between the bodies
			double impulse = 0.0;
			double angularError = 0.0;
			
			double angle = this.getRelativeRotation();
			double C = 0.0;
			
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * angularTolerance) {
				C = Interval.clamp(angle - this.lowerLimit, -maxAngularCorrection, maxAngularCorrection);
			} else if (angle <= this.lowerLimit) {
				C = Interval.clamp(angle - this.lowerLimit + angularTolerance, -maxAngularCorrection, 0.0);
			} else if (angle >= this.upperLimit) {
				C = Interval.clamp(angle - this.upperLimit - angularTolerance, 0.0, maxAngularCorrection);
			}
			
			impulse = -this.axialMass * C;
			this.body1.rotateAboutCenter(invI1 * impulse);
			this.body2.rotateAboutCenter(-invI2 * impulse);
			angularError = Math.abs(C);
			
			return angularError <= angularTolerance;
		}
		
		return true;
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
		return (this.impulse + this.lowerImpulse - this.upperImpulse) * invdt;
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
		if (ratio == 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.angle.invalidRaio"));
		this.ratio = ratio;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitEnabled(boolean)
	 */
	public void setLimitsEnabled(boolean flag) {
		// only wake the bodies if the flag changes
		if (this.limitsEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the flag
			this.limitsEnabled = flag;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#isLimitEnabled()
	 */
	public boolean isLimitsEnabled() {
		return this.limitsEnabled;
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
		// make sure the minimum is less than or equal to the maximum
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		
		if (this.upperLimit != upperLimit) {
			if (this.limitsEnabled) {
				// wake up both bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new target angle
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.upperImpulse = 0.0;
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
		// make sure the minimum is less than or equal to the maximum
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		if (this.lowerLimit != lowerLimit) {
			if (this.limitsEnabled) {
				// wake up both bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new target angle
			this.lowerLimit = lowerLimit;
			// clear accumulated impulse
			this.lowerImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double, double)
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		// make sure the min < max
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = upperLimit;
			this.lowerLimit = lowerLimit;
			// clear accumulated impulse
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
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
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double)
	 */
	public void setLimitsEnabled(double limit) {
		this.setLimitsEnabled(limit, limit);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getLimitsReferenceAngle()
	 */
	public double getLimitsReferenceAngle() {
		return this.referenceAngle;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsReferenceAngle(double)
	 */
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
