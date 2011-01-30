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
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Matrix22;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a joint attached to a body and the mouse.
 * <p>
 * Nearly identical to <a href="http://www.box2d.org">Box2d</a>'s equivalent class.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class MouseJoint extends Joint {
	/** The joint type */
	public static final Joint.Type TYPE = new Joint.Type("Mouse");
	
	/** The world space target point */
	protected Vector2 target;
	
	/** The local anchor point for the body */
	protected Vector2 anchor;
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;
	
	/** The maximum force this constraint can apply */
	protected double maxForce;
	
	/** The constraint mass; K = J * Minv * Jtrans */
	protected Matrix22 K;
	
	/** The bias for adding work to the constraint (simulating a spring) */
	protected Vector2 bias;
	
	/** The damping portion of the constraint */
	protected double gamma;
	
	/** The impulse applied to the body to satisfy the constraint */
	protected Vector2 impulse;
	
	/**
	 * Full constructor.
	 * @param body the body to attach the joint to
	 * @param target the target point; where the mouse clicked on the body
	 * @param frequency the oscillation frequency in hz
	 * @param dampingRatio the damping ratio
	 * @param maxForce the maximum force this constraint can apply in newtons
	 * @throws NullPointerException if body or target is null
	 * @throws IllegalArgumentException if frequency is less than or equal to zero, or if dampingRatio is less than zero or greater than one, or if maxForce is less than zero
	 */
	public MouseJoint(Body body, Vector2 target, double frequency, double dampingRatio, double maxForce) {
		super(body, body, false);
		// check for a null target
		if (target == null) throw new NullPointerException("The target point cannot be null.");
		// verify the frequency
		if (frequency <= 0) throw new IllegalArgumentException("The frequency must be greater than zero.");
		// verify the damping ratio
		if (dampingRatio < 0 || dampingRatio > 1) throw new IllegalArgumentException("The damping ratio must be between 0 and 1 inclusive.");
		// verity the max force
		if (maxForce < 0.0) throw new IllegalArgumentException("The max force must be zero or greater.");
		this.target = target;
		this.anchor = body.getLocalPoint(target);
		this.frequency = frequency;
		this.dampingRatio = dampingRatio;
		this.maxForce = maxForce;
		// initialize
		this.K = new Matrix22();
		this.impulse = new Vector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MOUSE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.target).append("|")
		.append(this.anchor).append("|")
		.append(this.frequency).append("|")
		.append(this.dampingRatio).append("|")
		.append(this.maxForce).append("|")
		.append(this.impulse).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
		Body body = this.body2;
		Transform transform = body.getTransform();
		
		Mass mass = this.body2.getMass();
		
		double m = mass.getMass();
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// compute the natural frequency; f = w / (2 * pi) -> w = 2 * pi * f
		double w = 2.0 * Math.PI * this.frequency;
		// compute the damping coefficient; dRatio = d / (2 * m * w) -> d = 2 * m * w * dRatio
		double d = 2.0 * m * this.dampingRatio * w;
		// compute the spring constant; w = sqrt(k / m) -> k = m * w * w
		double k = m * w * w;
		
		// get the delta time
		double dt = step.getDeltaTime();
		// compute gamma = CMF = 1 / (hk + d)
		this.gamma = dt * (d + dt * k);
		// check for zero before inverting
		if (this.gamma >= Epsilon.E) {
			this.gamma = 1.0 / this.gamma;
		}
		
		// compute the r vector
		Vector2 r = transform.getTransformedR(body.getLocalCenter().to(this.anchor));
		
		// compute the bias = ERP where ERP = hk / (hk + d)
		this.bias = body.getWorldCenter().add(r).difference(this.target);
		this.bias.multiply(dt * k * this.gamma);
		
		// compute the K inverse matrix
		this.K.m00 = invM + r.y * r.y * invI;
		this.K.m01 = -invI * r.x * r.y; 
		this.K.m10 = this.K.m01;
		this.K.m11 = invM + r.x * r.x * invI;
		
		// apply the spring
		this.K.m00 += this.gamma;
		this.K.m11 += this.gamma;
		
		// warm start
		this.impulse.multiply(step.getDeltaTimeRatio());
		body.getVelocity().add(this.impulse.product(invM));
		body.setAngularVelocity(body.getAngularVelocity() + invI * r.cross(this.impulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		Body body = this.body2;
		Transform transform = body.getTransform();
		
		Mass mass = this.body2.getMass();
		
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// compute r
		Vector2 r = transform.getTransformedR(body.getLocalCenter().to(this.anchor));

		// Cdot = v + cross(w, r)
		Vector2 C = r.cross(body.getAngularVelocity()).add(body.getVelocity());
		// compute Jv + b
		Vector2 jvb = C;
		jvb.add(this.bias);
		jvb.add(this.impulse.product(this.gamma));
		jvb.negate();
		Vector2 J = this.K.solve(jvb);
		
		// clamp using the maximum force
		Vector2 oldImpulse = this.impulse.copy();
		this.impulse.add(J);
		double maxImpulse = step.getDeltaTime() * this.maxForce;
		if (this.impulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
			this.impulse.normalize();
			this.impulse.multiply(maxImpulse);
		}
		J = this.impulse.difference(oldImpulse);
		
		body.getVelocity().add(J.product(invM));
		body.setAngularVelocity(body.getAngularVelocity() + invI * r.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getType()
	 */
	@Override
	public Type getType() {
		return MouseJoint.TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.target;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldPoint(this.anchor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.impulse.product(invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0.0;
	}
	
	/**
	 * Returns the target point in world coordinates.
	 * @param target the target point
	 * @throws NullPointerException if target is null
	 */
	public void setTarget(Vector2 target) {
		// make sure the target is non null
		if (target == null) throw new NullPointerException("The target point cannot be null.");
		// wake up the body
		this.body2.setAsleep(false);
		// set the new target
		this.target = target;
	}
	
	/**
	 * Returns the target point in world coordinates
	 * @return {@link Vector2}
	 */
	public Vector2 getTarget() {
		return this.target;
	}
	
	/**
	 * Returns the maximum force this constraint will apply in newtons.
	 * @return double
	 */
	public double getMaxForce() {
		return this.maxForce;
	}
	
	/**
	 * Sets the maximum force this constraint will apply in newtons.
	 * @param maxForce the maximum force in newtons; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxForce less than zero
	 */
	public void setMaxForce(double maxForce) {
		// make sure the max force is non negative
		if (maxForce < 0.0) throw new IllegalArgumentException("The maximum force must be zero or greater.");
		// wake up the body
		this.body2.setAsleep(false);
		// set the new max force
		this.maxForce = maxForce;
	}

	/**
	 * Returns the damping ratio.
	 * @return double
	 */
	public double getDampingRatio() {
		return this.dampingRatio;
	}
	
	/**
	 * Sets the damping ratio.
	 * @param dampingRatio the damping ratio; in the range [0, 1]
	 * @throws IllegalArgumentException if dampingRation is less than zero or greater than one
	 */
	public void setDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio < 0 || dampingRatio > 1) throw new IllegalArgumentException("The damping ratio must be between 0 and 1.");
		// wake up the body
		this.body2.setAsleep(false);
		// set the new value
		this.dampingRatio = dampingRatio;
	}
	
	/**
	 * Returns the spring frequency.
	 * @return double
	 */
	public double getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Sets the spring frequency.
	 * @param frequency the spring frequency in hz; must be greater than zero
	 * @throws IllegalArgumentException if frequency is less than or equal to zero
	 */
	public void setFrequency(double frequency) {
		// check for valid value
		if (frequency <= 0) throw new IllegalArgumentException("The frequency must be greater than zero.");
		// wake the body
		this.body2.setAsleep(false);
		// set the new value
		this.frequency = frequency;
	}
}
