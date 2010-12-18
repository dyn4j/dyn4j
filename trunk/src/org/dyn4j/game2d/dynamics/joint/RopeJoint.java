/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
 * Represents a max length distance joint.
 * <p>
 * Given the two world space anchor points and a max distance, the {@link Body}s are
 * not allowed to separate past the max distance.
 * <p>
 * Nearly identical to <a href="http://www.box2d.org">Box2d</a>'s equivalent class.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.1
 * @since 2.2.1
 */
public class RopeJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Rope");
	
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** The computed distance between the two world space anchor points */
	protected double maxDistance;
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	protected double invK;
	
	/** The normal */
	protected Vector2 n;
	
	/** The current state of the joint */
	protected Joint.LimitState state;
	
	/** The accumulated impulse from the previous time step */
	protected double impulse;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates a max distance {@link Joint} where the joined 
	 * {@link Body}s do not participate in collision detection and
	 * resolution.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @param maxDistance the maximum distance the {@link Body}s can be separated
	 */
	public RopeJoint(Body body1, Body body2, Vector2 anchor1, Vector2 anchor2, double maxDistance) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException("Cannot create a distance joint between the same body instance.");
		// verify the anchor points are not null
		if (anchor1 == null || anchor2 == null) throw new NullPointerException("Neither anchor point can be null.");
		// get the local anchor points
		this.localAnchor1 = body1.getLocalPoint(anchor1);
		this.localAnchor2 = body2.getLocalPoint(anchor2);
		// set the max distance
		this.maxDistance = maxDistance;
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
		.append(this.maxDistance).append("|")
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
		
		// check the length to see if we need to apply the constraint
		if (length > this.maxDistance) {
			this.state = Joint.LimitState.AT_UPPER;
			
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
			this.state = Joint.LimitState.INACTIVE;
			this.impulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		if (this.state == Joint.LimitState.AT_UPPER) {
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
		if (this.state == Joint.LimitState.AT_UPPER) {
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
			double C = l - this.maxDistance;
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
	 * Returns the max distance between the two constrained {@link Body}s in meters.
	 * @return double
	 */
	public double getMaxDistance() {
		return this.maxDistance;
	}
	
	/**
	 * Sets the max distance between the two constrained {@link Body}s in meters.
	 * @param distance the distance in meters
	 */
	public void setMaxDistance(double maxDistance) {
		// make sure the distance is greater than zero
		if (maxDistance < 0.0) throw new IllegalArgumentException("The max distance must be greater than or equal to zero.");
		// check if the value changed
		if (this.maxDistance != maxDistance) {
			// wake up both bodies
			this.body1.setAsleep(false);
			this.body2.setAsleep(false);
			// set the new target distance
			this.maxDistance = maxDistance;
		}
	}
}
