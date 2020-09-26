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
 * <p>
 * The lower limit constraint requires that the bodies are initially separated
 * for it to be enforced. The lower limit will be enforced as soon as the bodies
 * separate, but it's recommended that they start separated instead. If the lower
 * limit is not being used, then the initial state doesn't matter.
 * @author William Bittle
 * @version 4.0.1
 * @since 2.2.1
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Rope_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/09/distance-constraint/" target="_blank">Distance Constraint</a>
 * @see <a href="http://www.dyn4j.org/2010/12/max-distance-constraint/" target="_blank">Max Distance Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class RopeJoint<T extends PhysicsBody> extends Joint<T> implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;
	
	/** The maximum distance between the two world space anchor points */
	protected double upperLimit;
	
	/** The minimum distance between the two world space anchor points */
	protected double lowerLimit;
	
	/** Whether the maximum distance is enabled */
	protected boolean upperLimitEnabled;
	
	/** Whether the minimum distance is enabled */
	protected boolean lowerLimitEnabled;
	
	// current state
	
	/** The current distance */
	private double length;
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	private double invK;
	
	/** The normal */
	private Vector2 n;
	
	// output
	
	/** The accumulated upper limit impulse */
	private double upperImpulse;
	
	/** The accumulated lower limit impulse */
	private double lowerImpulse;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates a rope joint between the two bodies that acts like a distance joint.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @throws NullPointerException if body1, body2, anchor1, or anchor2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public RopeJoint(T body1, T body2, Vector2 anchor1, Vector2 anchor2) {
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
		
		this.length = 0.0;
		this.invK = 0.0;
		this.n = null;
		
		this.lowerImpulse = 0.0;
		this.upperImpulse = 0.0;
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
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
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
		this.length = this.n.getMagnitude();
		// check for the tolerance
		if (this.length < linearTolerance) {
			this.n.zero();
			this.upperImpulse = 0.0;
			this.lowerImpulse = 0.0;
			return;
		} else {
			// normalize it
			this.n.multiply(1.0 / this.length);
		}
		
		if (!this.upperLimitEnabled) {
			this.upperImpulse = 0.0;
		}
		if (!this.lowerLimitEnabled) {
			this.lowerImpulse = 0.0;
		}

		// compute K inverse
		double cr1n = r1.cross(this.n);
		double cr2n = r2.cross(this.n);
		double invMass = 
				invM1 + invI1 * cr1n * cr1n + 
				invM2 + invI2 * cr2n * cr2n;
		
		// check for zero before inverting
		this.invK = invMass <= Epsilon.E ? 0.0 : 1.0 / invMass;
		
		if (settings.isWarmStartingEnabled()) {
			// warm start
			this.upperImpulse *= step.getDeltaTimeRatio();
			this.lowerImpulse *= step.getDeltaTimeRatio();
			
			Vector2 J = this.n.product(this.lowerImpulse - this.upperImpulse);
			this.body1.getLinearVelocity().add(J.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
			this.body2.getLinearVelocity().subtract(J.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
		} else {
			this.upperImpulse = 0.0;
			this.lowerImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(TimeStep step, Settings settings) {
		if (this.lowerLimitEnabled || this.upperLimitEnabled) {
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
			
			double invdt = step.getInverseDeltaTime();
			// upper limit (max length)
			if (this.upperLimitEnabled) {
				double d = this.length - this.lowerLimit;
				double Jv = this.n.dot(v1.difference(v2));
				
				// compute lambda (the magnitude of the impulse)
				double impulse = -this.invK * (Jv + Math.max(d, 0.0) * invdt);
				double oldImpulse = this.lowerImpulse;
				this.lowerImpulse = Math.max(0.0, this.lowerImpulse + impulse);
				impulse = this.lowerImpulse - oldImpulse;
				
				// apply the impulse
				Vector2 J = this.n.product(impulse);
				this.body1.getLinearVelocity().add(J.product(invM1));
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
				this.body2.getLinearVelocity().subtract(J.product(invM2));
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
			}
			
			// lower limit (min length)
			if (this.lowerLimitEnabled) {
				double d = this.upperLimit - this.length;
				double Jv = this.n.dot(v2.difference(v1));
				
				// compute lambda (the magnitude of the impulse)
				double impulse = -this.invK * (Jv + Math.max(d, 0.0) * invdt);
				double oldImpulse = this.upperImpulse;
				this.upperImpulse = Math.max(0.0, this.upperImpulse + impulse);
				impulse = this.upperImpulse - oldImpulse;
				
				// apply the impulse
				Vector2 J = this.n.product(impulse);
				this.body1.getLinearVelocity().subtract(J.product(invM1));
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * r1.cross(J));
				this.body2.getLinearVelocity().add(J.product(invM2));
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * r2.cross(J));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		if (this.lowerLimitEnabled || this.upperLimitEnabled) {	
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
			double C = 0.0;
			
			if (this.upperLimitEnabled && this.lowerLimitEnabled && Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				C = Interval.clamp(l - this.lowerLimit, -maxLinearCorrection, maxLinearCorrection);
			} else if (this.lowerLimitEnabled && l <= this.lowerLimit) {
				C = Interval.clamp(l - this.lowerLimit, -maxLinearCorrection, 0.0);
			} else if (this.upperLimitEnabled && l >= this.upperLimit) {
				C = Interval.clamp(l - this.upperLimit, 0.0, maxLinearCorrection);
			}
			
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
		return this.n.product((this.lowerImpulse - this.upperImpulse) * invdt);
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
		
		if (this.upperLimit != upperLimit) {
			// make sure its changed and enabled before waking the bodies
			if (this.upperLimitEnabled) {
				// wake up both bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new target distance
			this.upperLimit = upperLimit;
			// clear the accumulated impulse
			this.upperImpulse = 0.0;
		}
	}
	
	/**
	 * Sets whether the upper limit is enabled.
	 * @param flag true if the upper limit should be enabled
	 */
	public void setUpperLimitEnabled(boolean flag) {
		if (this.upperLimitEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the flag
			this.upperLimitEnabled = flag;
			// clear the accumulated impulse
			this.upperImpulse = 0.0;
		}
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
		
		if (this.lowerLimit != lowerLimit) {
			// make sure its changed and enabled before waking the bodies
			if (this.lowerLimitEnabled) {
				// wake up both bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new target distance
			this.lowerLimit = lowerLimit;
			// clear the accumulated impulse
			this.lowerImpulse = 0.0;
		}
	}

	/**
	 * Sets whether the lower limit is enabled.
	 * @param flag true if the lower limit should be enabled
	 */
	public void setLowerLimitEnabled(boolean flag) {
		if (this.lowerLimitEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the flag
			this.lowerLimitEnabled = flag;
			// clear the accumulated impulse
			this.lowerImpulse = 0.0;
		}
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
		
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			// make sure one of the limits is enabled and has changed before waking the bodies
			if (this.lowerLimitEnabled || this.upperLimitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = upperLimit;
			this.lowerLimit = lowerLimit;
			// clear the accumulated impulse
			this.upperImpulse = 0.0;
			this.lowerImpulse = 0.0;
		}
	}

	/**
	 * Sets both the lower and upper limits and enables both.
	 * @param lowerLimit the lower limit in meters; must be greater than or equal to zero
	 * @param upperLimit the upper limit in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if lowerLimit is less than zero, upperLimit is less than zero, or lowerLimit is greater than upperLimit
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}
	
	/**
	 * Enables or disables both the lower and upper limits.
	 * @param flag true if both limits should be enabled
	 * @since 2.2.2
	 */
	public void setLimitsEnabled(boolean flag) {
		if (this.upperLimitEnabled != flag || this.lowerLimitEnabled != flag) {
			this.upperLimitEnabled = flag;
			this.lowerLimitEnabled = flag;
			// wake up the bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// clear the accumulated impulse
			this.upperImpulse = 0.0;
			this.lowerImpulse = 0.0;
		}
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
		
		if (this.lowerLimit != limit || this.upperLimit != limit) {
			// make sure one of the limits is enabled and has changed before waking the bodies
			if (this.lowerLimitEnabled || this.upperLimitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = limit;
			this.lowerLimit = limit;
			// clear the accumulated impulse
			this.upperImpulse = 0.0;
			this.lowerImpulse = 0.0;
		}
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
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(limit);
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
