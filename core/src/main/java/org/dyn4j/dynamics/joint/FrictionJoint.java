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
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a friction joint.
 * <p>
 * A friction joint is a constraint that drives both linear and angular 
 * velocities to zero.
 * <p>
 * This joint is typically used with one dynamic and one static body.  In this
 * context, the joint will apply linear and angular friction to stop the body's motion.
 * <p>
 * Setting the maximum force and torque values will determine the rate at which the motion
 * is stopped.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Friction_Joint" target="_blank">Documentation</a>
 */
public class FrictionJoint extends Joint implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** The maximum force the constraint can apply */
	protected double maximumForce;
	
	/** The maximum torque the constraint can apply */
	protected double maximumTorque;
	
	// current state
	
	/** The pivot mass; K = J * Minv * Jtrans */
	private Matrix22 K;
	
	/** The mass for the angular constraint */
	private double angularMass;

	// output
	
	/** The impulse applied to reduce linear motion */
	private Vector2 linearImpulse;
	
	/** The impulse applied to reduce angular motion */
	private double angularImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 * @throws NullPointerException if body1, body2, or anchor is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public FrictionJoint(Body body1, Body body2, Vector2 anchor) {
		// default no collision allowed
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// verify the anchor point is non null
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
		// put the anchor in local space
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// initialize
		this.K = new Matrix22();
		this.linearImpulse = new Vector2();
		this.angularImpulse = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FrictionJoint[").append(super.toString())
		  .append("|Anchor=").append(this.getAnchor1())
		  .append("|MaximumForce=").append(this.maximumForce)
		  .append("|MaximumTorque=").append(this.maximumTorque)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the K inverse matrix
		this.K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		this.K.m01 = -invI1 * r1.x * r1.y - invI2 * r2.x * r2.y; 
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		
		// compute the angular mass
		this.angularMass = invI1 + invI2;
		if (this.angularMass > Epsilon.E) {
			this.angularMass = 1.0 / this.angularMass;
		}
		
		// account for variable time step
		this.linearImpulse.multiply(step.getDeltaTimeRatio());
		this.angularImpulse *= step.getDeltaTimeRatio();
		
		// warm start
		this.body1.getLinearVelocity().add(this.linearImpulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (r1.cross(this.linearImpulse) + this.angularImpulse));
		this.body2.getLinearVelocity().subtract(this.linearImpulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (r2.cross(this.linearImpulse) + this.angularImpulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// solve the angular constraint
		{
			// get the relative velocity - the target motor speed
			double C = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
			// get the impulse required to obtain the speed
			double impulse = this.angularMass * -C;
			// clamp the impulse between the maximum torque
			double oldImpulse = this.angularImpulse;
			double maxImpulse = this.maximumTorque * step.getDeltaTime();
			this.angularImpulse = Interval.clamp(this.angularImpulse + impulse, -maxImpulse, maxImpulse);
			// get the impulse we need to apply to the bodies
			impulse = this.angularImpulse - oldImpulse;
			
			// apply the impulse
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
		}
		
		// solve the point-to-point constraint
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 v1 = this.body1.getLinearVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
		Vector2 v2 = this.body2.getLinearVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
		Vector2 pivotV = v1.subtract(v2);
		
		Vector2 impulse = this.K.solve(pivotV.negate());
		
		// clamp by the maxforce
		Vector2 oldImpulse = this.linearImpulse.copy();
		this.linearImpulse.add(impulse);
		double maxImpulse = this.maximumForce * step.getDeltaTime();
		if (this.linearImpulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
			this.linearImpulse.normalize();
			this.linearImpulse.multiply(maxImpulse);
		}
		impulse = this.linearImpulse.difference(oldImpulse);
		
		this.body1.getLinearVelocity().add(impulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(impulse));
		this.body2.getLinearVelocity().subtract(impulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(impulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		// nothing to do here for this joint
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.linearImpulse.product(invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.angularImpulse * invdt;
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
	 * Returns the maximum torque this constraint will apply in newton-meters.
	 * @return double
	 */
	public double getMaximumTorque() {
		return this.maximumTorque;
	}
	
	/**
	 * Sets the maximum torque this constraint will apply in newton-meters.
	 * @param maximumTorque the maximum torque in newton-meters; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxTorque is less than zero
	 */
	public void setMaximumTorque(double maximumTorque) {
		// make sure its greater than or equal to zero
		if (maximumTorque < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.friction.invalidMaximumTorque"));
		// set the max
		this.maximumTorque = maximumTorque;
	}

	/**
	 * Returns the maximum force this constraint will apply in newtons.
	 * @return double
	 */
	public double getMaximumForce() {
		return this.maximumForce;
	}
	
	/**
	 * Sets the maximum force this constraint will apply in newtons.
	 * @param maximumForce the maximum force in newtons; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxForce is less than zero
	 */
	public void setMaximumForce(double maximumForce) {
		// make sure its greater than or equal to zero
		if (maximumForce < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.friction.invalidMaximumForce"));
		// set the max
		this.maximumForce = maximumForce;
	}
}
