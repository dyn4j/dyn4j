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
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a pulley joint.
 * <p>
 * A pulley joint joins two bodies in a pulley system with a fixed length zero
 * mass rope.  The bodies are allowed to rotate freely.  The bodies are allowed
 * to translate freely up to the total length of the "rope."
 * <p>
 * The length of the "rope" connecting the two bodies is computed by distance
 * from the pulley anchors to the body anchors including the ratio (if any)
 * when the joint is created.  The length can be changed dynamically by calling
 * the {@link #setLength(double)} method.
 * <p>
 * The pulley anchor points represent the "hanging" points for the respective 
 * bodies and can be any world space point.
 * <p>
 * This joint can also model a block-and-tackle system by setting the ratio
 * using the {@link #setRatio(double)} method.  A value of 1.0 indicates no
 * ratio.  Values between 0 and 1 exclusive indicate that the first body's
 * rope length will be 1/x times longer than the second body's rope length.
 * Values between 1 and infinity indicate that the second body's rope length
 * will be x times longer than the first body's rope length.
 * <p>
 * By default this joint acts very similar to two {@link DistanceJoint}s in
 * that the bodies are forced to be their respective rope-distance away from 
 * the pulley anchors (i.e. not behaving like a rope).  To have the bodies 
 * behave as if connected by flexible rope pass in <code>true</code> to the 
 * {@link #setSlackEnabled(boolean)} method.
 * @author William Bittle
 * @version 3.2.1
 * @since 2.1.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Pulley_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/12/pulley-constraint/" target="_blank">Pulley Constraint</a>
 */
public class PulleyJoint extends Joint implements Shiftable, DataContainer {
	/** The world space pulley anchor point for the first {@link Body} */
	protected Vector2 pulleyAnchor1;
	
	/** The world space pulley anchor point for the second {@link Body} */
	protected Vector2 pulleyAnchor2;
	
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** The pulley ratio for modeling a block-and-tackle */
	protected double ratio;

	/** True if slack in the rope is enabled */
	protected boolean slackEnabled;

	// current state
	
	/** The state of the limit (only used for slack) */
	private LimitState limitState;
	
	/** The original length of the first side of the pulley */
	private final double length1;
	
	/** The original length of the second side of the pulley */
	private final double length2;
	
	/** The total length of the pulley system */
	private double length;
	
	/** The normal from the first pulley anchor to the first {@link Body} anchor */
	private Vector2 n1;
	
	/** The normal from the second pulley anchor to the second {@link Body} anchor */
	private Vector2 n2;
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	private double invK;
	
	// output
	
	/** The accumulated impulse from the previous time step */
	private double impulse;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates a pulley joint between the two given {@link Body}s using the given anchor points.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param pulleyAnchor1 the first pulley anchor point
	 * @param pulleyAnchor2 the second pulley anchor point
	 * @param bodyAnchor1 the first {@link Body}'s anchor point
	 * @param bodyAnchor2 the second {@link Body}'s anchor point
	 * @throws NullPointerException if body1, body2, pulleyAnchor1, pulleyAnchor2, bodyAnchor1, or bodyAnchor2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public PulleyJoint(Body body1, Body body2, Vector2 pulleyAnchor1, Vector2 pulleyAnchor2, Vector2 bodyAnchor1, Vector2 bodyAnchor2) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// verify the pulley anchor points are not null
		if (pulleyAnchor1 == null) throw new NullPointerException(Messages.getString("dynamics.joint.pulley.nullPulleyAnchor1"));
		if (pulleyAnchor2 == null) throw new NullPointerException(Messages.getString("dynamics.joint.pulley.nullPulleyAnchor2"));
		// verify the body anchor points are not null
		if (bodyAnchor1 == null) throw new NullPointerException(Messages.getString("dynamics.joint.pulley.nullBodyAnchor1"));
		if (bodyAnchor2 == null) throw new NullPointerException(Messages.getString("dynamics.joint.pulley.nullBodyAnchor2"));
		// set the pulley anchor points
		this.pulleyAnchor1 = pulleyAnchor1;
		this.pulleyAnchor2 = pulleyAnchor2;
		// get the local anchor points
		this.localAnchor1 = body1.getLocalPoint(bodyAnchor1);
		this.localAnchor2 = body2.getLocalPoint(bodyAnchor2);
		// default the ratio and minimum length
		this.ratio = 1.0;
		// compute the lengths
		this.length1 = bodyAnchor1.distance(pulleyAnchor1);
		this.length2 = bodyAnchor2.distance(pulleyAnchor2);
		// compute the lengths
		// length = l1 + ratio * l2
		this.length = this.length1 + this.length2;
		// initialize the other fields
		this.impulse = 0.0;
		// initialize the slack parameters
		this.slackEnabled = false;
		this.limitState = LimitState.AT_UPPER;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PulleyJoint[").append(super.toString())
		  .append("|PulleyAnchor1=").append(this.pulleyAnchor1)
		  .append("|PulleyAnchor2=").append(this.pulleyAnchor2)
		  .append("|Anchor1=").append(this.getAnchor1())
		  .append("|Anchor2=").append(this.getAnchor2())
		  .append("|Ratio=").append(this.ratio)
		  .append("|Length=").append(this.length)
		  .append("|SlackEnabled=").append(this.slackEnabled)
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
		
		// put the body anchors in world space
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		Vector2 p1 = r1.sum(this.body1.getWorldCenter());
		Vector2 p2 = r2.sum(this.body2.getWorldCenter());
		
		Vector2 s1 = this.pulleyAnchor1;
		Vector2 s2 = this.pulleyAnchor2;
		
		// compute the axes
		this.n1 = s1.to(p1);
		this.n2 = s2.to(p2);
		
		// get the lengths
		double l1 = this.n1.normalize();
		double l2 = this.n2.normalize();
		
		// get the current total length
		double l = l1 + this.ratio * l2;
		
		// check if we need to solve the constraint
		if (l > this.length || !this.slackEnabled) {
			this.limitState = LimitState.AT_UPPER;
			
			// check for near zero length
			if (l1 <= 10.0 * linearTolerance) {
				// zero out the axis
				this.n1.zero();
			}
			
			// check for near zero length		
			if (l2 <= 10.0 * linearTolerance) {
				// zero out the axis
				this.n2.zero();
			}
			
			// compute the inverse effective masses (K matrix, in this case its a scalar) for the constraints
			double r1CrossN1 = r1.cross(this.n1);
			double r2CrossN2 = r2.cross(this.n2);
			double pm1 = invM1 + invI1 * r1CrossN1 * r1CrossN1;
			double pm2 = invM2 + invI2 * r2CrossN2 * r2CrossN2;
			this.invK = pm1 + this.ratio * this.ratio * pm2;
			// make sure we can invert it
			if (this.invK > Epsilon.E) {
				this.invK = 1.0 / this.invK;
			} else {
				this.invK = 0.0;
			}
			
			// warm start the constraints taking
			// variable time steps into account
			double dtRatio = step.getDeltaTimeRatio();
			this.impulse *= dtRatio;
			
			// compute the impulse along the axes
			Vector2 J1 = this.n1.product(-this.impulse);
			Vector2 J2 = this.n2.product(-this.ratio * this.impulse);
			
			// apply the impulse
			this.body1.getLinearVelocity().add(J1.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J1));
			this.body2.getLinearVelocity().add(J2.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * r2.cross(J2));
		} else {
			// clear the impulse and don't solve anything
			this.impulse = 0;
			this.limitState = LimitState.INACTIVE;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
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
			
			// compute Jv + b
			double C = -this.n1.dot(v1) - this.ratio * this.n2.dot(v2);
			// compute the impulse
			double impulse = this.invK * (-C);
			this.impulse += impulse;
			
			// compute the impulse along each axis
			Vector2 J1 = this.n1.product(-impulse);
			Vector2 J2 = this.n2.product(-impulse * this.ratio);
			
			// apply the impulse
			this.body1.getLinearVelocity().add(J1.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J1));
			this.body2.getLinearVelocity().add(J2.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * r2.cross(J2));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		if (this.limitState != LimitState.INACTIVE) {
			double linearTolerance = settings.getLinearTolerance();
			
			Transform t1 = this.body1.getTransform();
			Transform t2 = this.body2.getTransform();
			Mass m1 = this.body1.getMass();
			Mass m2 = this.body2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// put the body anchors in world space
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			Vector2 p1 = r1.sum(this.body1.getWorldCenter());
			Vector2 p2 = r2.sum(this.body2.getWorldCenter());
			
			Vector2 s1 = this.pulleyAnchor1;
			Vector2 s2 = this.pulleyAnchor2;
			
			// compute the axes
			this.n1 = s1.to(p1);
			this.n2 = s2.to(p2);
			
			// normalize and save the length
			double l1 = this.n1.normalize();
			double l2 = this.n2.normalize();
			
			// make sure the length is not near zero
			if (l1 <= 10.0 * linearTolerance) {
				this.n1.zero();
			}
			// make sure the length is not near zero
			if (l2 <= 10.0 * linearTolerance) {
				this.n2.zero();
			}
			
			double linearError = 0.0;
			
			// recompute K
			double r1CrossN1 = r1.cross(this.n1);
			double r2CrossN2 = r2.cross(this.n2);
			double pm1 = invM1 + invI1 * r1CrossN1 * r1CrossN1;
			double pm2 = invM2 + invI2 * r2CrossN2 * r2CrossN2;
			this.invK = pm1 + this.ratio * this.ratio * pm2;
			// make sure we can invert it
			if (this.invK > Epsilon.E) {
				this.invK = 1.0 / this.invK;
			} else {
				this.invK = 0.0;
			}
			
			// compute the constraint error
			double C = this.length - l1 - this.ratio * l2;
			linearError = Math.abs(C);
			
			// clamping the impulse does not work with the limit state
			// clamp the error
//			C = Interval.clamp(C + linearTolerance, -maxLinearCorrection, maxLinearCorrection);
			double impulse = -this.invK * C;
			
			// compute the impulse along the axes
			Vector2 J1 = this.n1.product(-impulse);
			Vector2 J2 = this.n2.product(-this.ratio * impulse);
			
			// apply the impulse
			this.body1.translate(J1.x * invM1, J1.y * invM1);
			this.body1.rotateAboutCenter(r1.cross(J1) * invI1);
			this.body2.translate(J2.x * invM2, J2.y * invM2);
			this.body2.rotateAboutCenter(r2.cross(J2) * invI2);
			
			return linearError < linearTolerance;
		} else {
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
		return this.n2.product(this.impulse * invdt);
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
		// we must move the world space pulley anchors
		this.pulleyAnchor1.add(shift);
		this.pulleyAnchor2.add(shift);
	}
	
	/**
	 * Returns the pulley anchor point for the first {@link Body}
	 * in world coordinates.
	 * @return {@link Vector2}
	 */
	public Vector2 getPulleyAnchor1() {
		return this.pulleyAnchor1;
	}
	
	/**
	 * Returns the pulley anchor point for the second {@link Body}
	 * in world coordinates.
	 * @return {@link Vector2}
	 */
	public Vector2 getPulleyAnchor2() {
		return this.pulleyAnchor2;
	}
	
	/**
	 * Returns the total length of the pulley "rope."
	 * @since 3.0.1
	 * @return double
	 * @see #setLength(double)
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Sets the total length of the pulley "rope."
	 * <p>
	 * Typically this is computed when the joint is created by adding the distance from the
	 * first body anchor to the first pulley anchor with the distance from the second body anchor
	 * to the second pulley anchor.
	 * @param length the length
	 * @since 3.2.1
	 */
	public void setLength(double length) {
		this.length = length;
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
	}
	
	/**
	 * Returns the current length from the first pulley anchor point to the
	 * anchor point on the first {@link Body}.
	 * <p>
	 * This is used, in conjunction with length2, to compute the total length
	 * when the ratio is changed.
	 * @return double
	 */
	public double getLength1() {
		// get the body anchor point in world space
		Vector2 ba = this.body1.getWorldPoint(this.localAnchor1);
		return this.pulleyAnchor1.distance(ba);
	}

	/**
	 * Returns the current length from the second pulley anchor point to the
	 * anchor point on the second {@link Body}.
	 * <p>
	 * This is used, in conjunction with length1, to compute the total length
	 * when the ratio is changed.
	 * @return double
	 */
	public double getLength2() {
		// get the body anchor point in world space
		Vector2 ba = this.body2.getWorldPoint(this.localAnchor2);
		return this.pulleyAnchor2.distance(ba);
	}
	
	/**
	 * Returns the pulley ratio.
	 * @return double
	 */
	public double getRatio() {
		return this.ratio;
	}
	
	/**
	 * Sets the pulley ratio.
	 * <p>
	 * The ratio value is used to simulate a block-and-tackle.  A ratio of 1.0 is the default
	 * and indicates that the pulley is not a block-and-tackle.
	 * <p>
	 * This method recomputes the total length of the pulley system.
	 * @param ratio the ratio; must be greater than zero
	 * @throws IllegalArgumentException if ratio is less than or equal to zero
	 */
	public void setRatio(double ratio) {
		if (ratio <= 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.pulley.invalidRatio"));
		// make sure the ratio changed
		if (ratio != this.ratio) {
			// set the new ratio
			this.ratio = ratio;
			// compute the new length
			this.length = this.length1 + this.ratio * this.length2;
			// wake up both bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
		}
	}
	
	/**
	 * Returns true if slack in the rope is enabled.
	 * @return boolean
	 * @since 3.1.6
	 */
	public boolean isSlackEnabled() {
		return this.slackEnabled;
	}
	
	/**
	 * Toggles the slack in the rope.
	 * <p>
	 * If slack is not enabled the rope length is fixed to the total length of the rope, acting like the {@link DistanceJoint}.
	 * @param flag true to enable slack
	 * @since 3.1.6
	 */
	public void setSlackEnabled(boolean flag) {
		this.slackEnabled = flag;
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
