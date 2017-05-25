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
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a fixed length distance joint.
 * <p>
 * Given the two world space anchor points a distance is computed and used
 * to constrain the attached {@link Body}s at that distance.  The bodies can rotate
 * freely about the anchor points and the whole system can move and rotate freely, but
 * the distance between the two anchor points is fixed.
 * <p>
 * This joint doubles as a spring/damper distance joint where the length can
 * change but is constantly approaching the target distance.  Enable the
 * spring/damper by setting the frequency and damping ratio to values greater than
 * zero.  A good starting point is a frequency of 8.0 and damping ratio of 0.3
 * then adjust as necessary.
 * @author William Bittle
 * @version 3.2.1
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Distance_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/09/distance-constraint/" target="_blank">Distance Constraint</a>
 */
public class DistanceJoint extends Joint implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;
	
	/** The computed distance between the two world space anchor points */
	protected double distance;
	
	// current state
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	private double invK;
	
	/** The normal */
	private Vector2 n;
	
	/** The bias for adding work to the constraint (simulating a spring) */
	private double bias;
	
	/** The damping portion of the constraint */
	private double gamma;

	// output
	
	/** The accumulated impulse from the previous time step */
	private double impulse;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates a fixed distance {@link Joint} where the joined 
	 * {@link Body}s do not participate in collision detection and
	 * resolution.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @throws NullPointerException if body1, body2, anchor1, or anchor2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public DistanceJoint(Body body1, Body body2, Vector2 anchor1, Vector2 anchor2) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// verify the anchor points are not null
		if (anchor1 == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor1"));
		if (anchor2 == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor2"));
		// get the local anchor points
		this.localAnchor1 = body1.getLocalPoint(anchor1);
		this.localAnchor2 = body2.getLocalPoint(anchor2);
		// compute the initial distance
		this.distance = anchor1.distance(anchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DistanceJoint[").append(super.toString())
		  .append("|Anchor1=").append(this.getAnchor1())
		  .append("|Anchor2=").append(this.getAnchor2())
		  .append("|Frequency=").append(this.frequency)
		  .append("|DampingRatio=").append(this.dampingRatio)
		  .append("|Distance=").append(this.distance)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
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
		
		// compute K inverse
		double cr1n = r1.cross(this.n);
		double cr2n = r2.cross(this.n);
		double invMass = invM1 + invI1 * cr1n * cr1n;
		invMass += invM2 + invI2 * cr2n * cr2n;
		
		// check for zero before inverting
		this.invK = invMass <= Epsilon.E ? 0.0 : 1.0 / invMass;
		
		// see if we need to compute spring damping
		if (this.frequency > 0.0) {
			double dt = step.getDeltaTime();
			// get the current compression/extension of the spring
			double x = length - this.distance;
			// compute the natural frequency; f = w / (2 * pi) -> w = 2 * pi * f
			double w = Geometry.TWO_PI * this.frequency;
			// compute the damping coefficient; dRatio = d / (2 * m * w) -> d = 2 * m * w * dRatio
			double d = 2.0 * this.invK * this.dampingRatio * w;
			// compute the spring constant; w = sqrt(k / m) -> k = m * w * w
			double k = this.invK * w * w;
			
			// compute gamma = CMF = 1 / (hk + d)
			this.gamma = dt * (d + dt * k);
			// check for zero before inverting
			this.gamma = this.gamma <= Epsilon.E ? 0.0 : 1.0 / this.gamma;			
			// compute the bias = x * ERP where ERP = hk / (hk + d)
			this.bias = x * dt * k * this.gamma;
			
			// compute the effective mass			
			invMass += this.gamma;
			// check for zero before inverting
			this.invK = invMass <= Epsilon.E ? 0.0 : 1.0 / invMass;
		} else {
			this.gamma = 0.0;
			this.bias = 0.0;
		}
		
		// warm start
		impulse *= step.getDeltaTimeRatio();
		
		Vector2 J = n.product(impulse);
		body1.getLinearVelocity().add(J.product(invM1));
		body1.setAngularVelocity(body1.getAngularVelocity() + invI1 * r1.cross(J));
		body2.getLinearVelocity().subtract(J.product(invM2));
		body2.setAngularVelocity(body2.getAngularVelocity() - invI2 * r2.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
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
		Vector2 v1 = body1.getLinearVelocity().sum(r1.cross(body1.getAngularVelocity()));
		Vector2 v2 = body2.getLinearVelocity().sum(r2.cross(body2.getAngularVelocity()));
		
		// compute Jv
		double Jv = n.dot(v1.difference(v2));
		
		// compute lambda (the magnitude of the impulse)
		double j = -this.invK * (Jv + this.bias + this.gamma * this.impulse);
		this.impulse += j;
		
		// apply the impulse
		Vector2 J = n.product(j);
		body1.getLinearVelocity().add(J.product(invM1));
		body1.setAngularVelocity(body1.getAngularVelocity() + invI1 * r1.cross(J));
		body2.getLinearVelocity().subtract(J.product(invM2));
		body2.setAngularVelocity(body2.getAngularVelocity() - invI2 * r2.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
		// check if this is a spring damper
		if (this.frequency > 0.0) {
			// don't solve position constraints for spring damper
			return true;
		}
		
		double linearTolerance = settings.getLinearTolerance();
		double maxLinearCorrection = settings.getMaximumLinearCorrection();
		
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
		double C = l - this.distance;
		C = Interval.clamp(C, -maxLinearCorrection, maxLinearCorrection);
		
		double impulse = -this.invK * C;
		
		Vector2 J = n.product(impulse);
		
		// translate and rotate the objects
		body1.translate(J.product(invM1));
		body1.rotate(invI1 * r1.cross(J), c1);
		
		body2.translate(J.product(-invM2));
		body2.rotate(-invI2 * r2.cross(J), c2);
		
		return Math.abs(C) < linearTolerance;
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
	 * Not applicable to this joint. Always returns zero.
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
	 * Returns true if this distance joint is a spring distance joint.
	 * @return boolean
	 */
	public boolean isSpring() {
		return this.frequency > 0.0;
	}
	
	/**
	 * Returns true if this distance joint is a spring distance joint
	 * with damping.
	 * @return boolean
	 */
	public boolean isSpringDamper() {
		return this.frequency > 0.0 && this.dampingRatio > 0.0;
	}
	
	/**
	 * Returns the rest distance between the two constrained {@link Body}s in meters.
	 * @return double
	 */
	public double getDistance() {
		return this.distance;
	}
	
	/**
	 * Sets the rest distance between the two constrained {@link Body}s in meters.
	 * @param distance the distance in meters
	 * @throws IllegalArgumentException if distance is less than zero
	 */
	public void setDistance(double distance) {
		// make sure the distance is greater than zero
		if (distance < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.distance.invalidDistance"));
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new target distance
		this.distance = distance;
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
	 * <p>
	 * Larger values reduce the oscillation of the spring.
	 * @param dampingRatio the damping ratio; in the range [0, 1]
	 * @throws IllegalArgumentException if damping ration is less than zero or greater than 1
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
	 * <p>
	 * Larger values increase the stiffness of the spring.
	 * @param frequency the spring frequency in hz; must be greater than or equal to zero
	 * @throws IllegalArgumentException if frequency is less than zero
	 */
	public void setFrequency(double frequency) {
		// check for valid value
		if (frequency < 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequency"));
		// set the new value
		this.frequency = frequency;
	}
}
