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
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a angle joint.
 * <p>
 * A angle joint constrains the relative rotation.
 * <p>
 * NOTE: The {@link #getAnchor1()} and {@link #getAnchor2()} methods return
 * null references since this joint does not require any anchor points at creation.
 * @author William Bittle
 * @version 2.2.2
 * @since 2.2.2
 */
public class AngleJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Angle");
	
	/** The initial angle between the two bodies */
	protected double referenceAngle;
	
	/** The inverse effective mass */
	protected double invK;
	
	/** The impulse applied to reduce angular motion */
	protected double impulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 */
	public AngleJoint(Body body1, Body body2) {
		// default no collision allowed
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException("Cannot create a angle joint between the same body instance.");
		// initialize
		this.impulse = 0.0;
		// compute the reference angle
		this.referenceAngle = body1.getTransform().getRotation() - body2.getTransform().getRotation();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ANGLE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.referenceAngle).append("|")
		.append(this.impulse).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the angular mass
		this.invK = invI1 + invI2;
		if (this.invK >= Epsilon.E) {
			this.invK = 1.0 / this.invK;
		}
		
		// account for variable time step
		this.impulse *= step.getDeltaTimeRatio();
		
		// warm start
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * this.impulse);
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * this.impulse);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// solve the angular constraint
		// get the relative velocity - the target motor speed
		double C = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
		// get the impulse required to obtain the speed
		double impulse = this.invK * -C;
		
		// apply the impulse
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * impulse);
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * impulse);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		Settings settings = Settings.getInstance();
		double angularTolerance = settings.getAngularTolerance();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the current rotation given the reference angle
		double  rotation = this.body1.getTransform().getRotation() - this.body2.getTransform().getRotation() - this.referenceAngle;
		
		double angularError = Math.abs(rotation);
		
		// compute the impulse to fix the drift
		double impulse = this.invK * -rotation;
		
		this.body1.rotateAboutCenter(invI1 * impulse);
		this.body2.rotateAboutCenter(-invI2 * impulse);
		
		return angularError <= angularTolerance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getType()
	 */
	@Override
	public Type getType() {
		return AngleJoint.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return new Vector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.impulse * invdt;
	}
}
