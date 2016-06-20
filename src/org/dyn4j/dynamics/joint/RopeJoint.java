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
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation a maximum and/or minimum length distance joint.
 * <p>
 * A rope joint contains the distance between two bodies.  The bodies can 
 * rotate freely about the anchor points.  The system as a whole can rotate and
 * translate freely as well.
 * <p>
 * This joint is like the {@link DistanceJoint}, but includes an upper and 
 * lower limit and does not include a spring-damper system.
 * <p>
 * By default the lower and upper limits are set to the current distance
 * between the given anchor points and will function identically like a
 * {@link DistanceJoint}.  The upper and lower limits can be enabled
 * separately.
 * @author William Bittle
 * @version 3.2.1
 * @since 2.2.1
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Rope_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/09/distance-constraint/" target="_blank">Distance Constraint</a>
 * @see <a href="http://www.dyn4j.org/2010/12/max-distance-constraint/" target="_blank">Max Distance Constraint</a>
 */
public class RopeJoint extends Joint implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** The maximum distance between the two world space anchor points */
	protected double upperLimit;
	
	/** The minimum distance between the two world space anchor points */
	protected double lowerLimit;
	
	/** Whether the maximum distance is enabled */
	protected boolean upperLimitEnabled;
	
	/** Whether the minimum distance is enabled */
	protected boolean lowerLimitEnabled;
	
	// current state
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	private double invK;
	
	/** The normal */
	private Vector2 n;
	
	/** The current state of the joint limits */
	private LimitState limitState;
	
	// output
	
	/** The accumulated impulse from the previous time step */
	private double impulse;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates a rope joint between the two bodies that acts like a distance joint.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @throws NullPointerException if body1, body2, anchor1, or anchor2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public RopeJoint(Body body1, Body body2, Vector2 anchor1, Vector2 anchor2) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// verify the anchor points are not null
		if (anchor1 == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor1"));
		if (anchor2 == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor2"));
		// get the local anchor points
		this.localAnchor1 = body1.getLocalPoint(anchor1);
		this.localAnchor2 = body2.getLocalPoint(anchor2);
		// default to act like a fixed length distance joint
		this.upperLimitEnabled = true;
		this.lowerLimitEnabled = true;
		// default the limits
		double distance = anchor1.distance(anchor2);
		this.upperLimit = distance;
		this.lowerLimit = distance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RopeJoint[").append(super.toString())
		  .append("|Anchor1=").append(this.getAnchor1())
		  .append("|Anchor2=").append(this.getAnchor2())
	 	  .append("|IsLowerLimitEnabled=").append(this.lowerLimitEnabled)
		  .append("|LowerLimit").append(this.lowerLimit)
		  .append("|IsUpperLimitEnabled=").append(this.upperLimitEnabled)
		  .append("|UpperLimit=").append(this.upperLimit)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the normal
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		this.n = r1.sum(this.body1.getWorldCenter()).subtract(r2.sum(this.body2.getWorldCenter()));
		
		// get the current length
		double length = this.n.getMagnitude();
		// check for the tolerance
		if (length < linearTolerance) {
			this.n.zero();
		} else {
			// normalize it
			this.n.multiply(1.0 / length);
		}
		
		// check if both limits are enabled
		// and get the current state of the limits
		if (this.upperLimitEnabled && this.lowerLimitEnabled) {
			// if both are enabled check if they are equal
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				// if so then set the state to equal
				this.limitState = LimitState.EQUAL;
			} else {
				// make sure we have valid settings
				if (this.upperLimit > this.lowerLimit) {
					// check against the max and min distances
					if (length > this.upperLimit) {
						// set the state to at upper
						this.limitState = LimitState.AT_UPPER;
					} else if (length < this.lowerLimit) {
						// set the state to at lower
						this.limitState = LimitState.AT_LOWER;
					} else {
						// set the state to inactive
						this.limitState = LimitState.INACTIVE;
					}
				}
			}
		} else if (this.upperLimitEnabled) {
			// check the maximum against the current length
			if (length > this.upperLimit) {
				// set the state to at upper
				this.limitState = LimitState.AT_UPPER;
			} else {
				// no constraint needed at this time
				this.limitState = LimitState.INACTIVE;
			}
		} else if (this.lowerLimitEnabled) {
			// check the minimum against the current length
			if (length < this.lowerLimit) {
				// set the state to at lower
				this.limitState = LimitState.AT_LOWER;
			} else {
				// no constraint needed at this time
				this.limitState = LimitState.INACTIVE;
			}
		} else {
			// neither is enabled so no constraint needed at this time
			this.limitState = LimitState.INACTIVE;
		}
		
		// check the length to see if we need to apply the constraint
		if (this.limitState != LimitState.INACTIVE) {
			// compute K inverse
			double cr1n = r1.cross(this.n);
			double cr2n = r2.cross(this.n);
			double invMass = invM1 + invI1 * cr1n * cr1n;
			invMass += invM2 + invI2 * cr2n * cr2n;
			
			// check for zero before inverting
			this.invK = invMass <= Epsilon.E ? 0.0 : 1.0 / invMass;
			
			// warm start
			this.impulse *= step.getDeltaTimeRatio();
			
			Vector2 J = this.n.product(this.impulse);
			this.body1.getLinearVelocity().add(J.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
			this.body2.getLinearVelocity().subtract(J.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
		} else {
			// clear the impulse
			this.impulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
		// check if the constraint need to be applied
		if (this.limitState != LimitState.INACTIVE) {
			Transform t1 = this.body1.getTransform();
			Transform t2 = this.body2.getTransform();
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// compute r1 and r2
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			
			// compute the relative velocity
			Vector2 v1 = this.body1.getLinearVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
			Vector2 v2 = this.body2.getLinearVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
			
			// compute Jv
			double Jv = this.n.dot(v1.difference(v2));
			
			// compute lambda (the magnitude of the impulse)
			double j = -this.invK * (Jv);
			this.impulse += j;
			
			// apply the impulse
			Vector2 J = this.n.product(j);
			this.body1.getLinearVelocity().add(J.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
			this.body2.getLinearVelocity().subtract(J.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		// check if the constraint need to be applied
		if (this.limitState != LimitState.INACTIVE) {
			// if the limits are equal it doesn't matter if we
			// use the maximum or minimum setting
			double targetDistance = this.upperLimit;
			// determine the target distance
			if (this.limitState == LimitState.AT_LOWER) {
				// use the minimum distance as the target
				targetDistance = this.lowerLimit;
			}
			
			double linearTolerance = settings.getLinearTolerance();
			double maxLinearCorrection = settings.getMaximumLinearCorrection();
			
			Transform t1 = this.body1.getTransform();
			Transform t2 = this.body2.getTransform();
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			Vector2 c1 = this.body1.getWorldCenter();
			Vector2 c2 = this.body2.getWorldCenter();
			
			// recompute n since it may have changed after integration
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			this.n = r1.sum(this.body1.getWorldCenter()).subtract(r2.sum(this.body2.getWorldCenter()));
			
			// solve the position constraint
			double l = this.n.normalize();
			double C = l - targetDistance;
			C = Interval.clamp(C, -maxLinearCorrection, maxLinearCorrection);
			
			double impulse = -this.invK * C;
			
			Vector2 J = this.n.product(impulse);
			
			// translate and rotate the objects
			this.body1.translate(J.product(invM1));
			this.body1.rotate(invI1 * r1.cross(J), c1);
			
			this.body2.translate(J.product(-invM2));
			this.body2.rotate(-invI2 * r2.cross(J), c2);
			
			return Math.abs(C) < linearTolerance;
		} else {
			// if not then just return true that the position constraint is satisfied
			return true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor1()
	 */
	public Vector2 getAnchor1() {
		return body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor2()
	 */
	public Vector2 getAnchor2() {
		return body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.n.product(this.impulse * invdt);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint.
	 * Always returns zero.
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0.0;
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
	 * Returns the upper limit in meters.
	 * @return double
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/**
	 * Sets the upper limit in meters.
	 * @param upperLimit the upper limit in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if upperLimit is less than zero or less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		// make sure the distance is greater than zero
		if (upperLimit < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.rope.lessThanZeroUpperLimit"));
		// make sure the minimum is less than or equal to the maximum
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		// make sure its changed and enabled before waking the bodies
		if (this.upperLimitEnabled && upperLimit != this.upperLimit) {
			// wake up both bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
		}
		// set the new target distance
		this.upperLimit = upperLimit;
	}
	
	/**
	 * Sets whether the upper limit is enabled.
	 * @param flag true if the upper limit should be enabled
	 */
	public void setUpperLimitEnabled(boolean flag) {
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the flag
		this.upperLimitEnabled = flag;
	}
	
	/**
	 * Returns true if the upper limit is enabled.
	 * @return boolean true if the upper limit is enabled
	 */
	public boolean isUpperLimitEnabled() {
		return this.upperLimitEnabled;
	}
	
	/**
	 * Returns the lower limit in meters.
	 * @return double
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/**
	 * Sets the lower limit in meters.
	 * @param lowerLimit the lower limit in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if lowerLimit is less than zero or greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		// make sure the distance is greater than zero
		if (lowerLimit < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.rope.lessThanZeroLowerLimit"));
		// make sure the minimum is less than or equal to the maximum
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		// make sure its changed and enabled before waking the bodies
		if (this.lowerLimitEnabled && lowerLimit != this.lowerLimit) {
			// wake up both bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
		}
		// set the new target distance
		this.lowerLimit = lowerLimit;
	}

	/**
	 * Sets whether the lower limit is enabled.
	 * @param flag true if the lower limit should be enabled
	 */
	public void setLowerLimitEnabled(boolean flag) {
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the flag
		this.lowerLimitEnabled = flag;
	}

	/**
	 * Returns true if the lower limit is enabled.
	 * @return boolean true if the lower limit is enabled
	 */
	public boolean isLowerLimitEnabled() {
		return this.lowerLimitEnabled;
	}
	
	/**
	 * Sets both the lower and upper limits.
	 * @param lowerLimit the lower limit in meters; must be greater than or equal to zero
	 * @param upperLimit the upper limit in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if lowerLimit is less than zero, upperLimit is less than zero, or lowerLimit is greater than upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		// make sure the minimum distance is greater than zero
		if (lowerLimit < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.rope.lessThanZeroLowerLimit"));
		// make sure the maximum distance is greater than zero
		if (upperLimit < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.rope.lessThanZeroUpperLimit"));
		// make sure the min < max
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		// make sure one of the limits is enabled and has changed before waking the bodies
		if ((this.lowerLimitEnabled && lowerLimit != this.lowerLimit) || (this.upperLimitEnabled && upperLimit != this.upperLimit)) {
			// wake up the bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
		}
		// set the limits
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
	}

	/**
	 * Sets both the lower and upper limits and enables both.
	 * @param lowerLimit the lower limit in meters; must be greater than or equal to zero
	 * @param upperLimit the upper limit in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if lowerLimit is less than zero, upperLimit is less than zero, or lowerLimit is greater than upperLimit
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.upperLimitEnabled = true;
		this.lowerLimitEnabled = true;
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}
	
	/**
	 * Enables or disables both the lower and upper limits.
	 * @param flag true if both limits should be enabled
	 * @since 2.2.2
	 */
	public void setLimitsEnabled(boolean flag) {
		this.upperLimitEnabled = flag;
		this.lowerLimitEnabled = flag;
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
	}
	
	/**
	 * Sets both the lower and upper limits to the given limit.
	 * <p>
	 * This makes the joint a fixed length joint.
	 * @param limit the desired limit
	 * @throws IllegalArgumentException if limit is less than zero
	 * @since 2.2.2
	 */
	public void setLimits(double limit) {
		// make sure the distance is greater than zero
		if (limit < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.rope.invalidLimit"));
		// make sure one of the limits is enabled and has changed before waking the bodies
		if ((this.lowerLimitEnabled && limit != this.lowerLimit) || (this.upperLimitEnabled && limit != this.upperLimit)) {
			// wake up the bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
		}
		// set the limits
		this.upperLimit = limit;
		this.lowerLimit = limit;
	}
	
	/**
	 * Sets both the lower and upper limits to the given limit and
	 * enables both.
	 * <p>
	 * This makes the joint a fixed length joint.
	 * @param limit the desired limit
	 * @throws IllegalArgumentException if limit is less than zero
	 * @since 2.2.2
	 */
	public void setLimitsEnabled(double limit) {
		// enable the limits
		this.upperLimitEnabled = true;
		this.lowerLimitEnabled = true;
		// set the values
		this.setLimits(limit);
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
