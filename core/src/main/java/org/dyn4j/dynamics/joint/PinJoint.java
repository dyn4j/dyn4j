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
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a pin joint.
 * <p>
 * A pin joint is a joint that pins a body to a specified world space point
 * using a spring-damper system.  This joint will attempt to place the given
 * anchor point at the target position.
 * <p>
 * NOTE: The anchor point does not have to be within the bounds of the body.
 * <p>
 * By default the target position will be the given world space anchor. Use 
 * the {@link #setTarget(Vector2)} method to set a different target.
 * <p>
 * The pin joint requires the spring-damper system to function properly and
 * as such the frequency value must be greater than zero.  Use a 
 * {@link RevoluteJoint} instead if a spring-damper system is not desired.
 * A good starting point is a frequency of 8.0 and damping ratio of 0.3
 * then adjust as necessary.
 * <p>
 * The {@link #getAnchor1()} method returns the target and the 
 * {@link #getAnchor2()} method returns the world space anchor point.
 * <p>
 * Both the {@link #getBody1()} and {@link #getBody2()} methods return the same
 * body.
 * <p>
 * Renamed from MouseJoint in 3.2.0.
 * @author William Bittle
 * @version 3.2.1
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Pin_Joint" target="_blank">Documentation</a>
 */
public class PinJoint extends Joint implements Shiftable, DataContainer {
	/** The world space target point */
	protected Vector2 target;
	
	/** The local anchor point for the body */
	protected Vector2 anchor;
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;
	
	/** The maximum force this constraint can apply */
	protected double maximumForce;
	
	// current state
	
	/** The constraint mass; K = J * Minv * Jtrans */
	private Matrix22 K;
	
	/** The bias for adding work to the constraint (simulating a spring) */
	private Vector2 bias;
	
	/** The damping portion of the constraint */
	private double gamma;

	// output
	
	/** The impulse applied to the body to satisfy the constraint */
	private Vector2 impulse;
	
	/**
	 * Full constructor.
	 * @param body the body to attach the joint to
	 * @param anchor the anchor point on the body
	 * @param frequency the oscillation frequency in hz
	 * @param dampingRatio the damping ratio
	 * @param maximumForce the maximum force this constraint can apply in newtons
	 * @throws NullPointerException if body or anchor is null
	 * @throws IllegalArgumentException if frequency is less than or equal to zero, or if dampingRatio is less than zero or greater than one, or if maxForce is less than zero
	 */
	public PinJoint(Body body, Vector2 anchor, double frequency, double dampingRatio, double maximumForce) {
		super(body, body, false);
		// check for a null anchor
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.pin.nullAnchor"));
		// verify the frequency
		if (frequency <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequencyZero"));
		// verify the damping ratio
		if (dampingRatio < 0 || dampingRatio > 1) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidDampingRatio"));
		// verity the max force
		if (maximumForce < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.pin.invalidMaximumForce"));
		
		this.target = anchor;
		this.anchor = body.getLocalPoint(anchor);
		this.frequency = frequency;
		this.dampingRatio = dampingRatio;
		this.maximumForce = maximumForce;
		
		// initialize
		this.K = new Matrix22();
		this.impulse = new Vector2();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PinJoint[").append(super.toString())
		  .append("|Target=").append(this.target)
		  .append("|Anchor=").append(this.anchor)
		  .append("|Frequency=").append(this.frequency)
		  .append("|DampingRatio=").append(this.dampingRatio)
		  .append("|MaximumForce=").append(this.maximumForce)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
		Body body = this.body2;
		Transform transform = body.getTransform();
		
		Mass mass = this.body2.getMass();
		
		double m = mass.getMass();
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// check if the mass is zero
		if (m <= Epsilon.E) {
			// if the mass is zero, use the inertia
			// this will allow the pin joint to work with
			// all mass types other than INFINITE
			m = mass.getInertia();
		}
		
		// compute the natural frequency; f = w / (2 * pi) -> w = 2 * pi * f
		double w = Geometry.TWO_PI * this.frequency;
		// compute the damping coefficient; dRatio = d / (2 * m * w) -> d = 2 * m * w * dRatio
		double d = 2.0 * m * this.dampingRatio * w;
		// compute the spring constant; w = sqrt(k / m) -> k = m * w * w
		double k = m * w * w;
		
		// get the delta time
		double dt = step.getDeltaTime();
		// compute gamma = CMF = 1 / (hk + d)
		this.gamma = dt * (d + dt * k);
		// check for zero before inverting
		if (this.gamma > Epsilon.E) {
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
		body.getLinearVelocity().add(this.impulse.product(invM));
		body.setAngularVelocity(body.getAngularVelocity() + invI * r.cross(this.impulse));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
		Body body = this.body2;
		Transform transform = body.getTransform();
		
		Mass mass = this.body2.getMass();
		
		double invM = mass.getInverseMass();
		double invI = mass.getInverseInertia();
		
		// compute r
		Vector2 r = transform.getTransformedR(body.getLocalCenter().to(this.anchor));

		// Cdot = v + cross(w, r)
		Vector2 C = r.cross(body.getAngularVelocity()).add(body.getLinearVelocity());
		// compute Jv + b
		Vector2 jvb = C;
		jvb.add(this.bias);
		jvb.add(this.impulse.product(this.gamma));
		jvb.negate();
		Vector2 J = this.K.solve(jvb);
		
		// clamp using the maximum force
		Vector2 oldImpulse = this.impulse.copy();
		this.impulse.add(J);
		double maxImpulse = step.getDeltaTime() * this.maximumForce;
		if (this.impulse.getMagnitudeSquared() > maxImpulse * maxImpulse) {
			this.impulse.normalize();
			this.impulse.multiply(maxImpulse);
		}
		J = this.impulse.difference(oldImpulse);
		
		body.getLinearVelocity().add(J.product(invM));
		body.setAngularVelocity(body.getAngularVelocity() + invI * r.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		// nothing to do here for this joint
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the target point in world space.
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.target;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the anchor point on the body in world space.
	 */
	@Override
	public Vector2 getAnchor2() {
		return this.body2.getWorldPoint(this.anchor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.impulse.product(invdt);
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
	 * @see org.dyn4j.dynamics.joint.Joint#isCollisionAllowed()
	 */
	@Override
	public boolean isCollisionAllowed() {
		// never allow collisions since there is only one body attached
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// the target point must be moved
		this.target.add(shift);
	}
	
	/**
	 * Returns the target point in world coordinates.
	 * @param target the target point
	 * @throws NullPointerException if target is null
	 */
	public void setTarget(Vector2 target) {
		// make sure the target is non null
		if (target == null) throw new NullPointerException(Messages.getString("dynamics.joint.pin.nullTarget"));
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
	public double getMaximumForce() {
		return this.maximumForce;
	}
	
	/**
	 * Sets the maximum force this constraint will apply in newtons.
	 * @param maximumForce the maximum force in newtons; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxForce less than zero
	 */
	public void setMaximumForce(double maximumForce) {
		// make sure the max force is non negative
		if (maximumForce < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.pin.invalidMaximumForce"));
		// set the new max force
		this.maximumForce = maximumForce;
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
		if (dampingRatio < 0 || dampingRatio > 1) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidDampingRatio"));
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
		if (frequency <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequencyZero"));
		// set the new value
		this.frequency = frequency;
	}
}
