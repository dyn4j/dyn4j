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
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a maximum/minimum length distance joint.
 * <p>
 * Given the two world space anchor points, the {@link Body}s are not allowed to 
 * separate past the maximum distance and not allowed to approach past the 
 * minimum distance.
 * <p>
 * NOTE: The respective {@link #setMaximumDistance(double)}, {@link #setMaximumEnabled(boolean)},
 * {@link #setMinimumDistance(double)}, {@link #setMinimumEnabled(boolean)}, 
 * {@link #setMinimumMaximum(double, double)}, {@link #setMinimumMaximumEnabled(double, double)},
 * {@link #setMinimumMaximum(double)}, {@link #setMinimumMaximumEnabled(double)}, and
 * {@link #setMinimumMaximumEnabled(boolean)} methods must be called to setup the maximum 
 * and minimum limits, otherwise this joint acts like a {@link DistanceJoint}.
 * <p>
 * Nearly identical to <a href="http://www.box2d.org">Box2d</a>'s equivalent class.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.1
 */
public class RopeJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Rope");
	
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** The maximum distance between the two world space anchor points */
	protected double maximumDistance;
	
	/** The minimum distance between the two world space anchor points */
	protected double minimumDistance;
	
	/** Whether the maximum distance is enabled */
	protected boolean maximumEnabled;
	
	/** Whether the minimum distance is enabled */
	protected boolean minimumEnabled;
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	protected double invK;
	
	/** The normal */
	protected Vector2 n;
	
	/** The current state of the joint limits */
	protected Joint.LimitState limitState;
	
	/** The accumulated impulse from the previous time step */
	protected double impulse;
	
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
		if (body1 == body2) throw new IllegalArgumentException("Cannot create a rope joint between the same body instance.");
		// verify the anchor points are not null
		if (anchor1 == null || anchor2 == null) throw new NullPointerException("Neither anchor point can be null.");
		// get the local anchor points
		this.localAnchor1 = body1.getLocalPoint(anchor1);
		this.localAnchor2 = body2.getLocalPoint(anchor2);
		// default to act like a fixed length distance joint
		this.maximumEnabled = true;
		this.minimumEnabled = true;
		// default the limits
		double distance = anchor1.distance(anchor2);
		this.maximumDistance = distance;
		this.minimumDistance = distance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ROPE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.localAnchor1).append("|")
		.append(this.localAnchor2).append("|")
		.append(this.maximumDistance).append("|")
		.append(this.maximumEnabled).append("|")
		.append(this.minimumDistance).append("|")
		.append(this.minimumEnabled).append("|")
		.append(this.limitState).append("|")
		.append(this.impulse).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
		// get the current settings
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = body1.getTransform();
		Transform t2 = body2.getTransform();
		Mass m1 = body1.getMass();
		Mass m2 = body2.getMass();
		
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
		if (this.maximumEnabled && this.minimumEnabled) {
			// if both are enabled check if they are equal
			if (Math.abs(this.maximumDistance - this.minimumDistance) < 2.0 * linearTolerance) {
				// if so then set the state to equal
				this.limitState = Joint.LimitState.EQUAL;
			} else {
				// make sure we have valid settings
				if (this.maximumDistance > this.minimumDistance) {
					// check against the max and min distances
					if (length > this.maximumDistance) {
						// set the state to at upper
						this.limitState = Joint.LimitState.AT_UPPER;
					} else if (length < this.minimumDistance) {
						// set the state to at lower
						this.limitState = Joint.LimitState.AT_LOWER;
					} else {
						// set the state to inactive
						this.limitState = Joint.LimitState.INACTIVE;
					}
				}
			}
		} else if (this.maximumEnabled) {
			// check the maximum against the current length
			if (length > this.maximumDistance) {
				// set the state to at upper
				this.limitState = Joint.LimitState.AT_UPPER;
			} else {
				// no constraint needed at this time
				this.limitState = Joint.LimitState.INACTIVE;
			}
		} else if (this.minimumEnabled) {
			// check the minimum against the current length
			if (length < this.minimumDistance) {
				// set the state to at lower
				this.limitState = Joint.LimitState.AT_LOWER;
			} else {
				// no constraint needed at this time
				this.limitState = Joint.LimitState.INACTIVE;
			}
		} else {
			// neither is enabled so no constraint needed at this time
			this.limitState = Joint.LimitState.INACTIVE;
		}
		
		// check the length to see if we need to apply the constraint
		if (this.limitState != Joint.LimitState.INACTIVE) {
			// compute K inverse
			double cr1n = r1.cross(this.n);
			double cr2n = r2.cross(this.n);
			double invMass = invM1 + invI1 * cr1n * cr1n;
			invMass += invM2 + invI2 * cr2n * cr2n;
			
			// check for zero before inverting
			this.invK = Math.abs(invMass) < Epsilon.E ? 0.0 : 1.0 / invMass;
			
			// warm start
			impulse *= step.getDeltaTimeRatio();
			Vector2 J = n.product(impulse);
			body1.getVelocity().add(J.product(invM1));
			body1.setAngularVelocity(body1.getAngularVelocity() + invI1 * r1.cross(J));
			body2.getVelocity().subtract(J.product(invM2));
			body2.setAngularVelocity(body2.getAngularVelocity() - invI2 * r2.cross(J));
		} else {
			// clear the impulse
			this.impulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		// check if the constraint need to be applied
		if (this.limitState != Joint.LimitState.INACTIVE) {
			Transform t1 = body1.getTransform();
			Transform t2 = body2.getTransform();
			Mass m1 = body1.getMass();
			Mass m2 = body2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// compute r1 and r2
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			
			// compute the relative velocity
			Vector2 v1 = body1.getVelocity().sum(r1.cross(body1.getAngularVelocity()));
			Vector2 v2 = body2.getVelocity().sum(r2.cross(body2.getAngularVelocity()));
			
			// compute Jv
			double Jv = n.dot(v1.difference(v2));
			
			// compute lambda (the magnitude of the impulse)
			double j = -this.invK * (Jv);
			this.impulse += j;
			
			// apply the impulse
			Vector2 J = n.product(j);
			body1.getVelocity().add(J.product(invM1));
			body1.setAngularVelocity(body1.getAngularVelocity() + invI1 * r1.cross(J));
			body2.getVelocity().subtract(J.product(invM2));
			body2.setAngularVelocity(body2.getAngularVelocity() - invI2 * r2.cross(J));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		// check if the constraint need to be applied
		if (this.limitState != Joint.LimitState.INACTIVE) {
			// if the limits are equal it doesn't matter if we
			// use the maximum or minimum setting
			double targetDistance = this.maximumDistance;
			// determine the target distance
			if (this.limitState == Joint.LimitState.AT_LOWER) {
				// use the minimum distance as the target
				targetDistance = this.minimumDistance;
			}
			// get the current settings
			Settings settings = Settings.getInstance();
			double linearTolerance = settings.getLinearTolerance();
			double maxLinearCorrection = settings.getMaxLinearCorrection();
			
			Transform t1 = body1.getTransform();
			Transform t2 = body2.getTransform();
			Mass m1 = body1.getMass();
			Mass m2 = body2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			Vector2 c1 = body1.getWorldCenter();
			Vector2 c2 = body2.getWorldCenter();
			
			// recompute n since it may have changed after integration
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			n = r1.sum(body1.getWorldCenter()).subtract(r2.sum(body2.getWorldCenter()));
			
			// solve the position constraint
			double l = n.normalize();
			double C = l - targetDistance;
			C = Interval.clamp(C, -maxLinearCorrection, maxLinearCorrection);
			
			double impulse = -this.invK * C;
			
			Vector2 J = n.product(impulse);
			
			// translate and rotate the objects
			body1.translate(J.product(invM1));
			body1.rotate(invI1 * r1.cross(J), c1);
			
			body2.translate(J.product(-invM2));
			body2.rotate(-invI2 * r2.cross(J), c2);
			
			return Math.abs(C) < linearTolerance;
		} else {
			// if not then just return true that the position constraint is satisfied
			return true;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getType()
	 */
	@Override
	public Type getType() {
		return RopeJoint.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor1()
	 */
	public Vector2 getAnchor1() {
		return body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor2()
	 */
	public Vector2 getAnchor2() {
		return body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.n.product(this.impulse * invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0.0;
	}
	
	/**
	 * Returns the maximum distance between the two constrained {@link Body}s in meters.
	 * @return double
	 */
	public double getMaximumDistance() {
		return this.maximumDistance;
	}
	
	/**
	 * Sets the maximum distance between the two constrained {@link Body}s in meters.
	 * @param maximumDistance the maximum distance in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if maximumDistance is less than zero or less than the current minimum
	 */
	public void setMaximumDistance(double maximumDistance) {
		// make sure the distance is greater than zero
		if (maximumDistance < 0.0) throw new IllegalArgumentException("The max distance must be greater than or equal to zero.");
		// make sure the minimum is less than or equal to the maximum
		if (maximumDistance < this.minimumDistance) throw new IllegalArgumentException("The maximum distance must be greater than or equal to the current minimum distance.");
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target distance
		this.maximumDistance = maximumDistance;
	}
	
	/**
	 * Sets whether the maximum distance limit is enabled.
	 * @param flag true if the maximum distance limit should be enforced
	 */
	public void setMaximumEnabled(boolean flag) {
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the flag
		this.maximumEnabled = flag;
	}
	
	/**
	 * Returns the minimum distance between the two constrained {@link Body}s in meters.
	 * @return double
	 */
	public double getMinimumDistance() {
		return this.minimumDistance;
	}
	
	/**
	 * Sets the minimum distance between the two constrained {@link Body}s in meters.
	 * @param minimumDistance the minimum distance in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if minimumDistance is less than zero or greater than the current maximum
	 */
	public void setMinimumDistance(double minimumDistance) {
		// make sure the distance is greater than zero
		if (minimumDistance < 0.0) throw new IllegalArgumentException("The minimum distance must be greater than or equal to zero.");
		// make sure the minimum is less than or equal to the maximum
		if (minimumDistance > this.maximumDistance) throw new IllegalArgumentException("The minimum distance must be less than or equal to the current maximum distance.");
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target distance
		this.minimumDistance = minimumDistance;
	}

	/**
	 * Sets whether the minimum distance limit is enabled.
	 * @param flag true if the minimum distance limit should be enforced
	 */
	public void setMinimumEnabled(boolean flag) {
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the flag
		this.minimumEnabled = flag;
	}
	
	/**
	 * Sets both the maximum and minimum limit distances.
	 * @param minimumDistance the minimum distance in meters; must be greater than or equal to zero
	 * @param maximumDistance the maximum distance in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if minimumDistance is less than zero, maximumDistance is less than zero, or minimumDistance is greater than maximumDistance
	 */
	public void setMinimumMaximum(double minimumDistance, double maximumDistance) {
		// make sure the minimum distance is greater than zero
		if (minimumDistance < 0.0) throw new IllegalArgumentException("The minimum distance must be greater than or equal to zero.");
		// make sure the maximum distance is greater than zero
		if (maximumDistance < 0.0) throw new IllegalArgumentException("The maximum distance must be greater than or equal to zero.");
		// make sure the min < max
		if (minimumDistance > maximumDistance) throw new IllegalArgumentException("The minimum distance must be less than the maximum distance.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the limits
		this.maximumDistance = maximumDistance;
		this.minimumDistance = minimumDistance;
	}

	/**
	 * Sets both the maximum and minimum limit distances and enables both.
	 * @param minimumDistance the minimum distance in meters; must be greater than or equal to zero
	 * @param maximumDistance the maximum distance in meters; must be greater than or equal to zero
	 * @throws IllegalArgumentException if minimumDistance is less than zero, maximumDistance is less than zero, or minimumDistance is greater than maximumDistance
	 */
	public void setMinimumMaximumEnabled(double minimumDistance, double maximumDistance) {
		// set the values
		this.setMinimumMaximum(minimumDistance, maximumDistance);
		// enable the limits
		this.maximumEnabled = true;
		this.minimumEnabled = true;
	}
	
	/**
	 * Enables or disables both the maximum and minimum limits.
	 * @param flag true if both limits should be enabled
	 * @since 2.2.2
	 */
	public void setMinimumMaximumEnabled(boolean flag) {
		this.maximumEnabled = flag;
		this.minimumEnabled = flag;
	}
	
	/**
	 * Sets both the maximum and minimum limit distances to the given distance.
	 * <p>
	 * This makes the joint a fixed length joint.
	 * @param distance the desired distance between the bodies
	 * @throws IllegalArgumentException if distance is less than zero
	 * @since 2.2.2
	 */
	public void setMinimumMaximum(double distance) {
		// make sure the distance is greater than zero
		if (distance < 0.0) throw new IllegalArgumentException("The distance must be greater than or equal to zero.");
		// wake up the bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the limits
		this.maximumDistance = distance;
		this.minimumDistance = distance;
	}
	
	/**
	 * Sets both the maximum and minimum limit distances to the given distance and
	 * enables both.
	 * <p>
	 * This makes the joint a fixed length joint.
	 * @param distance the desired distance between the bodies
	 * @throws IllegalArgumentException if distance is less than zero
	 * @since 2.2.2
	 */
	public void setMinimumMaximumEnabled(double distance) {
		// set the values
		this.setMinimumMaximum(distance);
		// enable the limits
		this.maximumEnabled = true;
		this.minimumEnabled = true;
	}
}
