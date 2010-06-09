/*
 * Copyright (c) 2010, William Bittle
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

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Settings;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a fixed length distance joint.
 * <p>
 * Given the two world space anchor points a distance is computed and used
 * to constraint the attached {@link Body}s.
 * <p>
 * This joint doubles as a spring/damping distance joint where the length can
 * change but is constantly approaching the target distance.
 * @author William Bittle
 */
public class DistanceJoint extends Joint {
	/** The local anchor point on the first {@link Body} */
	protected Vector localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector localAnchor2;
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;
	
	/** The computed distance between the two world space anchor points */
	protected double distance;
	
	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	protected double invK;
	
	/** The normal */
	protected Vector n;
	
	/** The bias for adding work to the constraint (simulating a spring) */
	protected double bias;
	
	/** The damping portion of the constraint */
	protected double gamma;
	
	/** The accumulated impulse from the previous time step */
	protected double j;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a fixed distance {@link Joint} where the joined 
	 * {@link Body}s do not participate in collision detection and
	 * resolution.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 */
	public DistanceJoint(Body b1, Body b2, Vector anchor1, Vector anchor2) {
		this(b1, b2, false, anchor1, anchor2);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a spring/damper distance {@link Joint} where the joined 
	 * {@link Body}s do not participate in collision detection and
	 * resolution.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @param frequency the spring frequency in hz; must be greater than zero
	 * @param dampingRatio the damping ratio; in the range [0, 1]
	 */
	public DistanceJoint(Body b1, Body b2, Vector anchor1, Vector anchor2, double frequency, double dampingRatio) {
		this(b1, b2, false, anchor1, anchor2, frequency, dampingRatio);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a fixed distance joint.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param collisionAllowed true if collision between the two {@link Body}s is allowed
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 */
	public DistanceJoint(Body b1, Body b2, boolean collisionAllowed, Vector anchor1, Vector anchor2) {
		super(b1, b2, collisionAllowed);
		if (anchor1 == null || anchor2 == null) throw new NullPointerException("Both anchor points cannot be null.");
		this.localAnchor1 = b1.getLocalPoint(anchor1);
		this.localAnchor2 = b2.getLocalPoint(anchor2);
		this.distance = anchor1.distance(anchor2);
		this.frequency = 0.0;
		this.dampingRatio = 0.0;
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a spring/damper distance joint.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param collisionAllowed true if collision between the two {@link Body}s is allowed
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @param frequency the spring frequency in hz; must be greater than zero
	 * @param dampingRatio the damping ratio; in the range [0, 1]
	 */
	public DistanceJoint(Body b1, Body b2, boolean collisionAllowed, Vector anchor1, Vector anchor2, double frequency, double dampingRatio) {
		super(b1, b2, collisionAllowed);
		// verify the anchor points
		if (anchor1 == null || anchor2 == null) throw new NullPointerException("Both anchor points cannot be null.");
		// verify the frequency
		if (frequency <= 0) throw new IllegalArgumentException("The frequency must be greater than zero.");
		// verify the damping ratio
		if (dampingRatio <= 0 || dampingRatio >= 1) throw new IllegalArgumentException("The damping ratio must be between 0 and 1.");
		this.localAnchor1 = b1.getLocalPoint(anchor1);
		this.localAnchor2 = b2.getLocalPoint(anchor2);
		this.distance = anchor1.distance(anchor2);
		this.frequency = frequency;
		this.dampingRatio = dampingRatio;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DISTANCE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.localAnchor1).append("|")
		.append(this.localAnchor2).append("|")
		.append(this.frequency).append("|")
		.append(this.dampingRatio).append("|")
		.append(this.distance).append("|")
		.append(this.j).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	public void initializeConstraints(Step step) {
		// get the current settings
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = b1.getTransform();
		Transform t2 = b2.getTransform();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the normal
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		this.n = r1.sum(this.b1.getWorldCenter()).subtract(r2.sum(this.b2.getWorldCenter()));
		
		// get the current length
		double l = this.n.getMagnitude();
		// check for the tolerance
		if (l < linearTolerance) {
			this.n.zero();
		} else {
			// normalize it
			this.n.divide(l);
		}
		
		// compute K inverse
		double cr1n = r1.cross(this.n);
		double cr2n = r2.cross(this.n);
		double invMass = invM1 + invI1 * cr1n * cr1n;
		invMass += invM2 + invI2 * cr2n * cr2n;
		this.invK = 1.0 / invMass;
		
		// see if we need to compute spring damping
		if (this.frequency > 0.0) {
			double dt = step.getDeltaTime();
			// get the current compression/extension of the spring
			double x = l - this.distance;
			// compute the natural frequency; f = w / (2 * pi) -> w = 2 * pi * f
			double w = 2.0 * Math.PI * this.frequency;
			// compute the damping coefficient; dRatio = d / (2 * m * w) -> d = 2 * m * w * dRatio
			double d = 2.0 * this.invK * this.dampingRatio * w;
			// compute the spring constant; w = sqrt(k / m) -> k = m * w * w
			double k = this.invK * w * w;
			
			// compute gamma = CMF = 1 / (hk + d)
			this.gamma = 1.0 / (dt * (d + dt * k));
			// compute the bias = x * ERP where ERP = hk / (hk + d)
			this.bias = x * dt * k * this.gamma;
			// compute the effective mass
			this.invK = 1.0 / (invMass + this.gamma); 
		}
		
		// warm start
		j *= step.getDeltaTimeRatio();
		Vector J = n.product(j);
		b1.getV().add(J.product(invM1));
		b1.setAv(b1.getAv() + invI1 * r1.cross(J));
		b2.getV().subtract(J.product(invM2));
		b2.setAv(b2.getAv() - invI2 * r2.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	public void solveVelocityConstraints(Step step) {
		Transform t1 = b1.getTransform();
		Transform t2 = b2.getTransform();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute r1 and r2
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		
		// compute the relative velocity
		Vector v1 = b1.getV().sum(r1.cross(b1.getAv()));
		Vector v2 = b2.getV().sum(r2.cross(b2.getAv()));
		
		// compute Jv
		double Jv = n.dot(v1.difference(v2));
		
		// compute lambda (the magnitude of the impulse)
		double j = -this.invK * (Jv + this.bias + this.gamma * this.j);
		this.j += j;
		
		// apply the impulse
		Vector J = n.product(j);
		b1.getV().add(J.product(invM1));
		b1.setAv(b1.getAv() + invI1 * r1.cross(J));
		b2.getV().subtract(J.product(invM2));
		b2.setAv(b2.getAv() - invI2 * r2.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		// get the current settings
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		double maxLinearCorrection = settings.getMaxLinearCorrection();
		
		Transform t1 = b1.getTransform();
		Transform t2 = b2.getTransform();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector c1 = b1.getWorldCenter();
		Vector c2 = b2.getWorldCenter();
		
		// recompute n since it may have changed after integration
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		n = r1.sum(b1.getWorldCenter()).subtract(r2.sum(b2.getWorldCenter()));
		
		// solve the position constraint
		double l = n.normalize();
		double C = l - this.distance;
		C = Interval.clamp(C, -maxLinearCorrection, maxLinearCorrection);
		
		double impulse = -this.invK * C;
		
		Vector J = n.product(impulse);
		
		// translate and rotate the objects
		b1.translate(J.product(invM1));
		b1.rotate(invI1 * r1.cross(J), c1);
		
		b2.translate(J.product(-invM2));
		b2.rotate(-invI2 * r2.cross(J), c2);
		
		return Math.abs(C) < linearTolerance;
	}
	
	/**
	 * Returns the local anchor point for the first {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getLocalAnchor1() {
		return this.localAnchor1;
	}
	
	/**
	 * Returns the local anchor point for the second {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getLocalAnchor2() {
		return this.localAnchor2;
	}
	
	/**
	 * Returns the world space anchor point for the first {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getWorldAnchor1() {
		return b1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * Returns the world space anchor point for the second {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getWorldAnchor2() {
		return b2.getWorldPoint(this.localAnchor2);
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
	public boolean hasDamper() {
		return this.dampingRatio > 0.0;
	}
	
	/**
	 * Returns the rest distance between the two constrained {@link Body}s.
	 * @return double
	 */
	public double getDistance() {
		return this.distance;
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
	 */
	public void setDampingRatio(double dampingRatio) {
		if (dampingRatio <= 0 || dampingRatio >= 1) throw new IllegalArgumentException("The damping ratio must be between 0 and 1.");
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
	 */
	public void setFrequency(double frequency) {
		if (frequency <= 0) throw new IllegalArgumentException("The frequency must be greater than zero.");
		this.frequency = frequency;
	}
	
	/**
	 * Returns the last frame's accumulated impulse.
	 * @return double
	 */
	public double getImpulse() {
		return this.j;
	}
}
